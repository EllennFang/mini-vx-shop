package com.powernode.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.Notice;
import com.powernode.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "公告接口管理")
@RequestMapping("shop/notice")
@RestController
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @ApiOperation("多条件分页查询公告列表")
    @GetMapping("page")
    @PreAuthorize("hasAuthority('shop:notice:page')")
    public ResponseEntity<Page<Notice>> loadNoticePage(Page<Notice> page,Notice notice) {
        page = noticeService.selectNoticePage(page,notice);
        return ResponseEntity.ok(page);
    }
}
