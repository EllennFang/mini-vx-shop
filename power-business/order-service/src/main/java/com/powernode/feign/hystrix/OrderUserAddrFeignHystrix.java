package com.powernode.feign.hystrix;

import com.powernode.domain.UserAddr;
import com.powernode.feign.OrderUserAddrFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderUserAddrFeignHystrix implements OrderUserAddrFeign {

    @Override
    public UserAddr getUserDefaultAddr(String userId) {
        log.error("远程调用,查询用户默认收货地址失败");
        return null;
    }
}
