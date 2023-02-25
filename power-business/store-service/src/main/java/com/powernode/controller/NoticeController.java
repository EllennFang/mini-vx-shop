package com.powernode.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.Notice;
import com.powernode.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @ApiOperation("新增公告")
    @PostMapping
    @PreAuthorize("hasAuthority('shop:notice:save')")
    public ResponseEntity<Void> saveNotice(@RequestBody Notice notice) {
        noticeService.save(notice);
        return ResponseEntity.ok().build();
    }

//    shop/notice/info/6
    @ApiOperation("根据标识查询公告详情")
    @GetMapping("info/{noticeId}")
    @PreAuthorize("hasAuthority('shop:notice:info')")
    public ResponseEntity<Notice> loadNoticeInfo(@PathVariable Long noticeId) {
        Notice notice = noticeService.getById(noticeId);
        return ResponseEntity.ok(notice);
    }

    @ApiOperation("修改公告内容")
    @PutMapping
    @PreAuthorize("hasAuthority('shop:notice:update')")
    public ResponseEntity<Void> updateNotice(@RequestBody Notice notice) {
        noticeService.updateById(notice);
        return ResponseEntity.ok().build();
    }

//    shop/notice/5
    @ApiOperation("删除公告")
    @DeleteMapping("{noticeId}")
    @PreAuthorize("hasAuthority('shop:notice:delete')")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long noticeId) {
        noticeService.removeById(noticeId);
        return ResponseEntity.ok().build();
    }

    //////////////////////微信小程序数据接口/////////////////////////
//    shop/notice/topNoticeList
    @ApiOperation("查询小程序公告列表")
    @GetMapping("topNoticeList")
    public ResponseEntity<List<Notice>> loadNoticeList() {
        List<Notice> list = noticeService.list();
        return ResponseEntity.ok(list);
    }
}
