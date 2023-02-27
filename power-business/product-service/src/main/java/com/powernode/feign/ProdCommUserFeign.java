package com.powernode.feign;

import com.powernode.domain.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "member-service")
public interface ProdCommUserFeign {

    @GetMapping("p/user/getUserListByUserIds")
    List<User> getUserListByUserIds(@RequestParam List<String> userIds);
}
