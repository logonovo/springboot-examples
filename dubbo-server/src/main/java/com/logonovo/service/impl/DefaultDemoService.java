package com.logonovo.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.logonovo.service.DemoService;

/**
 * @Author 小凡
 * Email: logonovo@gmail.com
 * @Date 2018/9/15 8:37
 */
@Service(
        version = "1.0.0",
        application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}"
)
public class DefaultDemoService implements DemoService {
    @Override
    public String sayHello(String name) {
        return "Hello, " + name + " (from Spring Boot)";
    }
}