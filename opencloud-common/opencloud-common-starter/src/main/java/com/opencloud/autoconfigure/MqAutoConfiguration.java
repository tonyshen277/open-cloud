package com.opencloud.autoconfigure;

import com.opencloud.common.constants.MqConstants;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuyadu
 */
@Configuration
public  class MqAutoConfiguration {

    /**
     * direct模式，直接根据队列名称投递消息
     *
     * @return
     */
    @Bean
    public Queue apiResourceQueue() {
        return new Queue(MqConstants.QUEUE_SCAN_API_RESOURCE);
    }

    @Bean
    public Queue accessLogsQueue() {
        return new Queue(MqConstants.QUEUE_ACCESS_LOGS);
    }

    @Bean
    public Queue userInfo() {
        return new Queue(MqConstants.QUEUE_USERINFO);
    }

    @Bean
    public Queue loginInit() {
        return new Queue(MqConstants.QUEUE_LOGININIT);
    }

}
