package com.powernode.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.domain.Prod;
import com.powernode.domain.ProdComm;
import com.powernode.domain.User;
import com.powernode.feign.ProdCommUserFeign;
import com.powernode.mapper.ProdCommMapper;
import com.powernode.mapper.ProdMapper;
import com.powernode.service.ProdCommService;
import com.powernode.vo.ProdCommOverview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProdCommServiceImpl extends ServiceImpl<ProdCommMapper, ProdComm> implements ProdCommService{

    @Autowired
    private ProdCommMapper prodCommMapper;

    @Autowired
    private ProdMapper prodMapper;

    @Autowired
    private ProdCommUserFeign prodCommUserFeign;


    @Override
    public Page<ProdComm> selectProdCommPage(Page<ProdComm> page, ProdComm prodComm) {
        //获取查询条件商品名称
        String prodName = prodComm.getProdName();
        //判断商品名称是否有值
        List<Long> prodIdList = null;
        List<Prod> prodList = null;
        if (StringUtils.hasText(prodName)) {
            //有值：根据商品名称模糊查询商品集合
            prodList = prodMapper.selectList(new LambdaQueryWrapper<Prod>()
                    .like(Prod::getProdName, prodName)
            );
            //判断商品集合是否有值
            if (CollectionUtil.isEmpty(prodList) || prodList.size() == 0) {
                return page;
            }
            //获取商品id集合
            prodIdList = prodList.stream().map(Prod::getProdId).collect(Collectors.toList());
        }

        //分页查询商品评论
        page = prodCommMapper.selectPage(page,new LambdaQueryWrapper<ProdComm>()
                .eq(ObjectUtil.isNotEmpty(prodComm.getStatus()),ProdComm::getStatus,prodComm.getStatus())
                .in(CollectionUtil.isNotEmpty(prodIdList)&&prodIdList.size()!=0,ProdComm::getProdId,prodIdList)
                .orderByDesc(ProdComm::getRecTime)
        );
        //获取评论记录
        List<ProdComm> prodCommList = page.getRecords();
        if (CollectionUtil.isNotEmpty(prodCommList) && prodCommList.size() != 0) {
            //评论记录有值
            //从评论集合中获取商品id集合
            List<Long> prodIds = prodCommList.stream().map(ProdComm::getProdId).collect(Collectors.toList());
            //根据商品id集合查询商品集合
            List<Prod> prods = prodMapper.selectBatchIds(prodIds);
            //循环评论记录
            prodCommList.forEach(prodComm1 -> {
                //从商品集合中过滤出与当前评论中的商品一致的商品对象
                Prod prod1 = prods.stream().filter(prod -> prod.getProdId().equals(prodComm1.getProdId())).collect(Collectors.toList()).get(0);
                prodComm1.setProdName(prod1.getProdName());
            });
        }

        return page;
    }

    @Override
    public boolean updateById(ProdComm prodComm) {
        //获取评论内容
        String content = prodComm.getContent();
        if (StringUtils.hasText(content)) {
            prodComm.setReplyTime(new Date());
            prodComm.setReplySts(1);
        }
        //获取ip地址
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String remoteHost = request.getRemoteHost();
        prodComm.setPostip(remoteHost);
        return prodCommMapper.updateById(prodComm)>0;
    }

    @Override
    public ProdCommOverview selectProdCommOverview(Long prodId) {

        //评论总数量
        Integer number = prodCommMapper.selectCount(new LambdaQueryWrapper<ProdComm>()
                .eq(ProdComm::getStatus, 1)
                .eq(ProdComm::getProdId, prodId)
        );
        //好评数量
        Integer praiseNumber = prodCommMapper.selectCount(new LambdaQueryWrapper<ProdComm>()
                .eq(ProdComm::getStatus, 1)
                .eq(ProdComm::getEvaluate,0)
                .eq(ProdComm::getProdId, prodId)
        );
        //中评数量
        Integer secondaryNumber = prodCommMapper.selectCount(new LambdaQueryWrapper<ProdComm>()
                .eq(ProdComm::getStatus, 1)
                .eq(ProdComm::getEvaluate,1)
                .eq(ProdComm::getProdId, prodId)
        );
        //差评数量
        Integer negativeNumber = prodCommMapper.selectCount(new LambdaQueryWrapper<ProdComm>()
                .eq(ProdComm::getStatus, 1)
                .eq(ProdComm::getEvaluate,2)
                .eq(ProdComm::getProdId, prodId)
        );
        //有图评论数量
        Integer picNumber = prodCommMapper.selectCount(new LambdaQueryWrapper<ProdComm>()
                .eq(ProdComm::getStatus, 1)
                .eq(ProdComm::getProdId, prodId)
                .isNotNull(ProdComm::getPics)
        );
        //好评率 = 好评数 / 评论总数量
        BigDecimal positiveRating = BigDecimal.ZERO;
        if (0 != number) {
            positiveRating = new BigDecimal(praiseNumber)
                    .divide(new BigDecimal(number),2, RoundingMode.HALF_DOWN)
                    .multiply(new BigDecimal(100));
        }

        return ProdCommOverview.builder()
                .number(number)
                .praiseNumber(praiseNumber)
                .secondaryNumber(secondaryNumber)
                .negativeNumber(negativeNumber)
                .picNumber(picNumber)
                .positiveRating(positiveRating).build();
    }

    /**
     * 1.分页查询商品评论
     * 2.获取评论用户的id集合
     * 3.根据用户id集合查询用户信息
     * 4.组装数据
     *
     *
     * @param page
     * @param prodId
     * @param evaluate
     * @return
     */
    @Override
    public Page<ProdComm> selectProdCommPageByProd(Page<ProdComm> page, Long prodId, Long evaluate) {
        //分页查询商品评论
        page = prodCommMapper.selectPage(page,new LambdaQueryWrapper<ProdComm>()
                .eq(ProdComm::getProdId,prodId)
                .eq(ProdComm::getStatus,1)
                .eq(evaluate != null && evaluate != -1 && evaluate != 3,ProdComm::getEvaluate,evaluate)
                .isNotNull(evaluate == 3,ProdComm::getPics)
                .orderByDesc(ProdComm::getRecTime)
        );
        //获取评论记录
        List<ProdComm> prodCommList = page.getRecords();
        //判断是否有值
        if (CollectionUtil.isEmpty(prodCommList) || prodCommList.size() == 0) {
            return page;
        }
        //获取评论的用户id集合
        List<String> userIds = prodCommList.stream().map(ProdComm::getUserId).collect(Collectors.toList());
        //根据用户id集合查询用户对象集合
        List<User> userList = prodCommUserFeign.getUserListByUserIds(userIds);
        if (CollectionUtil.isEmpty(userList) || userList.size() == 0) {
            throw new RuntimeException("服务器开小差了");
        }
        //循环遍历评论记录并组装数据
        prodCommList.forEach(prodComm -> {
            //从用户集合中过滤出与当前评论的用户id一致的用户对象
            User user1 = userList.stream()
                    .filter(user -> user.getUserId().equals(prodComm.getUserId()))
                    .collect(Collectors.toList()).get(0);
            //组装数据
            String nickName = user1.getNickName();
            StringBuffer stringBuffer = new StringBuffer(nickName);
            stringBuffer.replace(1,stringBuffer.length()-1,"***");
            String pic = user1.getPic();
            prodComm.setNickName(stringBuffer.toString());
            prodComm.setPic(pic);
        });

        return page;
    }
}
