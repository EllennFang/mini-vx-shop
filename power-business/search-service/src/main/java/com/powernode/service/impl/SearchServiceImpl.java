package com.powernode.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.Prod;
import com.powernode.domain.ProdTagReference;
import com.powernode.feign.SearchProdFeign;
import com.powernode.feign.SearchProdTagReferenceFeign;
import com.powernode.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SearchProdFeign searchProdFeign;

    @Autowired
    private SearchProdTagReferenceFeign searchProdTagReferenceFeign;


    @Override
    public Page<Prod> selectProdListByTagId(Page<Prod> page, Long tagId) {
        long size = page.getSize();
        //远程调用：根据商品分组标签查询关系分页对象
        Page<ProdTagReference> prodTagReferencePage = searchProdTagReferenceFeign.getProdTagReferencePageByTagId(tagId, size);
        if (ObjectUtil.isNull(prodTagReferencePage)) {
            throw new RuntimeException("服务器开小差了");
        }
        //获取商品分组关系集合
        List<ProdTagReference> prodTagReferenceList = prodTagReferencePage.getRecords();
        if (CollectionUtil.isEmpty(prodTagReferenceList) || prodTagReferenceList.size() == 0) {
            return page;
        }
        //从商品分组关系集合中获取商品id集合
        List<Long> prodIdList = prodTagReferenceList.stream().map(ProdTagReference::getProdId).collect(Collectors.toList());
        //远程调用：根据商品id集合查询商品集合
        List<Prod> prodList = searchProdFeign.getProdListByIds(prodIdList);
        if (CollectionUtil.isEmpty(prodList) || prodList.size() == 0) {
            throw new RuntimeException("服务器开小差了");
        }
        page.setRecords(prodList);
        return page;
    }
}
