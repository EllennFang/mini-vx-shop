package com.powernode.feign;

import com.powernode.domain.UserAddr;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "member-service")
public interface OrderUserAddrFeign {

    @GetMapping("p/address/getUserDefaultAddr")
    UserAddr getUserDefaultAddr(@RequestParam String userId);
}
