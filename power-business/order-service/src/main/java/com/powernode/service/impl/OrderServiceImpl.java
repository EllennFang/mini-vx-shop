package com.powernode.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.domain.*;
import com.powernode.dto.OrderConfirmDto;
import com.powernode.feign.OrderBasketFeign;
import com.powernode.feign.OrderSkuFeign;
import com.powernode.feign.OrderUserAddrFeign;
import com.powernode.mapper.OrderMapper;
import com.powernode.model.ChangeStock;
import com.powernode.model.ProdChange;
import com.powernode.model.ShopOrder;
import com.powernode.model.SkuChange;
import com.powernode.service.OrderService;
import com.powernode.vo.OrderStatus;
import com.powernode.vo.OrderVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService{


    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderUserAddrFeign orderUserAddrFeign;

    @Autowired
    private OrderSkuFeign orderSkuFeign;

    @Autowired
    private OrderBasketFeign orderBasketFeign;


    @Override
    public OrderStatus selectUserOrderStatus(String userId) {


        //查询待支付数量
        Integer unPay = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .eq(Order::getDeleteStatus, 0)
                .eq(Order::getStatus, 1)
        );

        //查询待发货数量
        Integer payed = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .eq(Order::getDeleteStatus, 0)
                .eq(Order::getStatus, 2)
        );

        //查询待收货数量
        Integer consignment = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .eq(Order::getDeleteStatus, 0)
                .eq(Order::getStatus, 3)
        );


        return OrderStatus.builder().unPay(unPay).payed(payed).consignment(consignment).build();
    }

    @Override
    public OrderVo selectOrderConfirmInfo(String userId, OrderConfirmDto orderConfirmDto) {
        OrderVo orderVo = new OrderVo();
        //远程调用：查询用户默认收货地址
        UserAddr userDefaultAddr = orderUserAddrFeign.getUserDefaultAddr(userId);
        orderVo.setUserAddr(userDefaultAddr);

        //判断订单确认来自于哪儿（商品详情页面还是购物车页面）
        List<Long> basketIds = orderConfirmDto.getBasketIds();
        if (CollectionUtil.isEmpty(basketIds) || basketIds.size() == 0) {
            //订单来自于：商品详情页面
            productToConfirm(userId,orderVo,orderConfirmDto.getOrderItem());
        } else {
            //订单来自于：购物车页面
            orderVo.setSource(1);
            cartToConfirm(userId,orderVo,basketIds);
        }

        return orderVo;
    }

    private void cartToConfirm(String userId,OrderVo orderVo, List<Long> basketIds) {

        //远程调用:根据购物车id集合查询购物车对象集合
        List<Basket> basketList = orderBasketFeign.getBasketsByIds(basketIds);
        if (CollectionUtil.isEmpty(basketList) || basketList.size() == 0) {
            throw new RuntimeException("服务器开小差了");
        }

        //从购物车集合对象中获取商品skuId集合
        List<Long> skuIdList = basketList.stream().map(Basket::getSkuId).collect(Collectors.toList());
        //远程调用：根据商品skuId集合查询商品sku对象集合
        List<Sku> skuList = orderSkuFeign.getSkuListBySkuIds(skuIdList);
        if (CollectionUtil.isEmpty(skuList) || skuList.size() == 0) {
            throw new RuntimeException("服务器开小差了");
        }
        List<ShopOrder> shopOrderList = new ArrayList<>();
        //将购物车对象集合使用stream流程进行分组
        Map<Long, List<Basket>> allShopOrderMap = basketList.stream().collect(Collectors.groupingBy(Basket::getShopId));
        List<BigDecimal> allOneSkuTotalAmounts = new ArrayList<>();
        List<Integer> allOneSkuCounts = new ArrayList<>();
        //循环遍历map集合
        allShopOrderMap.forEach((shopId,baskets) -> {
            //创建一个店铺对象
            ShopOrder shopOrder = new ShopOrder();
            List<OrderItem> orderItemList = new ArrayList<>();
            //循环遍历当前店铺中的购物车记录
            for(Basket basket:baskets){
                //创建订单商品条目对象
                OrderItem orderItem = new OrderItem();
                //将购物軖对象中的属性copy到商品条目对象中
                BeanUtils.copyProperties(basket,orderItem);
                //从商品sku集合对象中过滤出与当前购物车记录中商品skuId一致的商品对象
                Sku sku1 = skuList.stream().filter(sku -> sku.getSkuId().equals(basket.getSkuId())).collect(Collectors.toList()).get(0);
                BeanUtils.copyProperties(sku1,orderItem);
                orderItem.setUserId(userId);
                orderItem.setRecTime(new Date());
                orderItem.setCommSts(0);
                //计算 单个商品总金额
                Integer basketCount = basket.getBasketCount();
                orderItem.setProdCount(basketCount);
                allOneSkuCounts.add(basketCount);
                BigDecimal oneSkuTotalAmount = sku1.getPrice().multiply(new BigDecimal(basketCount));
                allOneSkuTotalAmounts.add(oneSkuTotalAmount);
                orderItem.setProductTotalAmount(oneSkuTotalAmount);

                orderItemList.add(orderItem);
            }

            shopOrder.setShopCartItemDiscounts(orderItemList);
            shopOrderList.add(shopOrder);
        });

        //计算购买商品总数量
        Integer allSkuTotalCount = allOneSkuCounts.stream().reduce(Integer::sum).get();
        //计算所有商品总金额
        BigDecimal allSkuTotalAmount = allOneSkuTotalAmounts.stream().reduce(BigDecimal::add).get();
        orderVo.setTotalCount(allSkuTotalCount);
        orderVo.setTotal(allSkuTotalAmount);
        orderVo.setActualTotal(allSkuTotalAmount);
        //计算运费
        if (allSkuTotalAmount.compareTo(new BigDecimal(99)) == -1) {
            orderVo.setTransfee(new BigDecimal(6));
            orderVo.setActualTotal(allSkuTotalAmount.add(new BigDecimal(6)));
        }
        orderVo.setShopCartOrders(shopOrderList);
    }

    private void productToConfirm(String userId,OrderVo orderVo, OrderItem orderItem) {
        Integer prodCount = orderItem.getProdCount();

        //获取商品skuId
        Long skuId = orderItem.getSkuId();
        List<Sku> skuList = orderSkuFeign.getSkuListBySkuIds(Arrays.asList(skuId));
        if (CollectionUtil.isEmpty(skuList) || skuList.size() == 0) {
            throw new RuntimeException("服务器开小差了");
        }
        Sku sku = skuList.get(0);
        BigDecimal price = sku.getPrice();

        orderVo.setTotalCount(prodCount);
        //计算单个商品总金额
        BigDecimal oneSkuTotalAmount = price.multiply(new BigDecimal(prodCount));
        orderVo.setTotal(oneSkuTotalAmount);
        orderVo.setActualTotal(oneSkuTotalAmount);
        //计算运费
        if (oneSkuTotalAmount.compareTo(new BigDecimal(99)) == -1) {
            orderVo.setTransfee(new BigDecimal(6));
            orderVo.setActualTotal(oneSkuTotalAmount.add(new BigDecimal(6)));
        }

        //创建店铺对象
        List<ShopOrder> shopOrderList = new ArrayList<>();
        ShopOrder shopOrder = new ShopOrder();
        List<OrderItem> orderItemList = new ArrayList<>();

        //将商品sku对象中的属性copy到商品条目对象中

        BeanUtils.copyProperties(sku,orderItem);
        orderItem.setUserId(userId);
        orderItem.setProductTotalAmount(oneSkuTotalAmount);
        orderItem.setRecTime(new Date());
        orderItem.setCommSts(0);

        orderItemList.add(orderItem);
        shopOrder.setShopCartItemDiscounts(orderItemList);
        shopOrderList.add(shopOrder);
        orderVo.setShopCartOrders(shopOrderList);
    }

    /**
     * 1.判断订单确认页面的请求是否来自于购物车页面
     *     是：将当前购买的商品从购物车中清除
     *     不是：不需要做任何处理
     * 2.修改商品在数据库中库存数量：商品prod和商品sku的库存数量
     * 3.生成订单：生成全局唯一的订单号，生成订单记录和生成订单详情商品记录
     * 4.如果订单超时，我们需要将商品购买的数量进行回滚
     *     使用消息队列中的延迟队列+死信队列
     * @param userId
     * @param orderVo
     * @return
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public String submitOrder(String userId, OrderVo orderVo) {
        //获取订单确认页面来源标签
        Integer source = orderVo.getSource();
        if (source.equals(1)) {
            //来自于购物车页面-》清除购物车中购买的商品
            clearUserCart(userId,orderVo);
        }

        //修改商品prod和sku库存数量
        ChangeStock changeStock = changeProdAndSkuStock(orderVo);
        return null;
    }

    /**
     * 修改商品prod和sku库存数量
     * @param orderVo
     * @return
     */
    private ChangeStock changeProdAndSkuStock(OrderVo orderVo) {

        List<ProdChange> prodChangeList = new ArrayList<>();
        List<SkuChange> skuChangeList = new ArrayList<>();
        //获取店铺集合对象
        List<ShopOrder> shopOrderList = orderVo.getShopCartOrders();
        //循环店铺集合对象
        shopOrderList.forEach(shopOrder -> {
            //从店铺对象中获取商品条目集合对象
            List<OrderItem> orderItemList = shopOrder.getShopCartItemDiscounts();
            //循环遍历商品条目集合对象
            orderItemList.forEach(orderItem -> {
                //获取商品prodId
                Long prodId = orderItem.getProdId();
                //获取商品skuId
                Long skuId = orderItem.getSkuId();
                //获取商品购买数量
                Integer prodCount = orderItem.getProdCount()*-1;

                //判断当前orderItem商品条目对象中的商品prodId是否与prodChangeList中的商品prodId有一致
                List<ProdChange> prodChanges = prodChangeList.stream()
                        .filter(prodChange -> prodChange.getProdId().equals(prodId))
                        .collect(Collectors.toList());

                //商品prod第1次
                SkuChange skuChange = new SkuChange();
                skuChange.setSkuId(skuId);
                skuChange.setCount(prodCount);
                skuChangeList.add(skuChange);

                ProdChange prodChange = null;
                if (CollectionUtil.isEmpty(prodChanges) || prodChanges.size() == 0) {
                    prodChange = new ProdChange();
                    prodChange.setProdId(prodId);
                    prodChange.setCount(prodCount);
                    prodChangeList.add(prodChange);
                } else {
                    prodChange = prodChanges.get(0);
                    Integer beforeCount = prodChange.getCount();
                    int finalCount = beforeCount + prodCount;
                    prodChange.setCount(finalCount);
                }
            });
        });
        //组装数据
        ChangeStock changeStock = new ChangeStock(prodChangeList,skuChangeList);
        //远程调用:修改商品prod和sku库存数量
        orderSkuFeign.changeStock(changeStock);
        return changeStock;
    }

    /**
     * 清除用户购物车中商品
     * @param userId
     * @param orderVo
     */
    private void clearUserCart(String userId, OrderVo orderVo) {
        //获取订单店铺集合对象
        List<ShopOrder> shopOrderList = orderVo.getShopCartOrders();
        //从订单店铺对象集合中的订单商品条目对象集合中获取商品skuId集合
        List<Long> skuIdList = new ArrayList<>();
        shopOrderList.forEach(shopOrder -> {
            //从店铺对象中获取商品条目集合对象
            List<OrderItem> orderItemList = shopOrder.getShopCartItemDiscounts();
            //循环遍历商品条目集合对象
            orderItemList.forEach(orderItem -> {
                //获取商品skuId
                Long skuId = orderItem.getSkuId();
                skuIdList.add(skuId);
            });
        });
        //远程调用:删除购物车中的商品
        if (!orderBasketFeign.clearBasketSkuList(skuIdList, userId)) {
            throw new RuntimeException("服务器开小差了");
        }
    }
}
