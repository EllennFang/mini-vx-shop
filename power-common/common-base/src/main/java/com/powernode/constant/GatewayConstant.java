package com.powernode.constant;

import java.util.Arrays;
import java.util.List;

/**
 * ClassName:GatewayConstant
 * Package:com.powernode.constant
 * Description:
 * Date:2022/10/25 10:54
 * author:abc
 */

public interface GatewayConstant {

    String TOKEN_PREFIX = "TOKEN:";

    List<String> ALLOW_URLS = Arrays.asList("/oauth/token");

    String AUTHORIZATION = "Authorization";

    String BEARER = "bearer ";
}
