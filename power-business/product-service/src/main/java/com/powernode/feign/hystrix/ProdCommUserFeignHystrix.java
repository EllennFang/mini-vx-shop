package com.powernode.feign.hystrix;

import com.powernode.domain.User;
import com.powernode.feign.ProdCommUserFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ProdCommUserFeignHystrix implements ProdCommUserFeign {

    @Override
    public List<User> getUserListByUserIds(List<String> userIds) {
        log.error("远程调用：根据用户id集合查询用户集合失败");
        return null;
    }
}
