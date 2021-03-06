package com.opencloud.api.gateway.filter;

import com.opencloud.api.gateway.exception.JsonAccessDeniedHandler;
import com.opencloud.api.gateway.util.ReactiveWebUtils;
import com.opencloud.common.constants.ResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * IP访问限制过滤器
 *
 * @author liuyadu
 */
@Slf4j
public class IpCheckFilter implements WebFilter {

    private JsonAccessDeniedHandler accessDeniedHandler;

    private ApiAuthorizationManager apiAccessManager;

    public IpCheckFilter(ApiAuthorizationManager apiAccessManager, JsonAccessDeniedHandler accessDeniedHandler) {
        this.apiAccessManager = apiAccessManager;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String requestPath = request.getURI().getPath();
        String remoteIpAddress = ReactiveWebUtils.getRemoteAddress(exchange);
        // 1.ip黑名单检测
        boolean deny = apiAccessManager.matchIpBlacklist(requestPath, remoteIpAddress);
        if (deny) {
            // 拒绝
            return accessDeniedHandler.handle(exchange, new AccessDeniedException(ResultEnum.ACCESS_DENIED_BLACK_IP_LIMITED.getMessage()));
        }

        // 3.ip白名单检测
        boolean[] matchIpWhiteListResult = apiAccessManager.matchIpWhiteList(requestPath, remoteIpAddress);
        boolean hasWhiteList = matchIpWhiteListResult[0];
        boolean allow = matchIpWhiteListResult[1];
        if (hasWhiteList) {
            // 接口存在白名单限制
            if (!allow) {
                // IP白名单检测通过,拒绝
                return accessDeniedHandler.handle(exchange, new AccessDeniedException(ResultEnum.ACCESS_DENIED_WHITE_IP_LIMITED.getMessage()));
            }
        }
        return chain.filter(exchange);
    }
}
