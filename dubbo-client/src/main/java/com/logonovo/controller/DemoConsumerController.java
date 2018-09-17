package com.logonovo.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.dubbo.config.annotation.Reference;
import com.logonovo.service.DemoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author 小凡
 * Email: logonovo@gmail.com
 * @Date 2018/9/15 8:53
 */
@RestController
public class DemoConsumerController {
    @Reference(version = "1.0.0",
            application = "${dubbo.application.id}",
            url = "dubbo://localhost:12345")
    private DemoService demoService;

    @RequestMapping("/sayHello")
    public String sayHello(@RequestParam String name, @RequestParam String app) {
        initFlowRules();
        String s = "";
        try {
            ContextUtil.enter("HelloWorld", app);
            s = demoService.sayHello(name);
            System.out.println("hello world");
        } finally {
            ContextUtil.exit();
        }
        return s;
    }
    @RequestMapping("/sayHello2")
    public String sayHello2(@RequestParam String name, @RequestParam String app) {
        initFlowRules();
        Entry entry = null;
    // 务必保证finally会被执行
        try {
            // 资源名可使用任意有业务语义的字符串
            entry = SphU.entry(app+":HelloWorld");
            System.out.println("sayHello2");
        } catch (BlockException e1) {
            // 资源访问阻止，被限流或被降级
            // 进行相应的处理操作
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }
        return "13";
    }



    private static void initFlowRules(){
        List<FlowRule> rules = new ArrayList<FlowRule>();
        FlowRule rule = new FlowRule();
        rule.setResource("HelloWorld");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setLimitApp("app1");
        // Set limit QPS to 20.
        rule.setCount(1);
        rules.add(rule);

        rule = new FlowRule();
        rule.setResource("app1:HelloWorld");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(1);
        rules.add(rule);

        FlowRuleManager.loadRules(rules);
    }
}