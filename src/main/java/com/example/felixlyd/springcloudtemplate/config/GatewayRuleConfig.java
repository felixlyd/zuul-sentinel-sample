package com.example.felixlyd.springcloudtemplate.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * class classname
 *
 * @author : liuyaodong
 * @date 2023/2/3
 */

@Configuration
public class GatewayRuleConfig {

    @PostConstruct
    public void doInit() {
        // Prepare some gateway rules and API definitions (only for demo).
        // It's recommended to leverage dynamic data source or the Sentinel dashboard to push the rules.
        initCustomizedApis();
        initGatewayRules();
//        initDegradeRules();
//        initSystemRule();
    }

    private void initCustomizedApis() {
        Set<ApiDefinition> definitions = new HashSet<>();
        ApiDefinition api1 = new ApiDefinition("rule_api")
                .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                    add(new ApiPathPredicateItem().setPattern("/rule/api-a/rule"));
                    add(new ApiPathPredicateItem().setPattern("/rule/api-a/refresh"));
                }});
        definitions.add(api1);
        GatewayApiDefinitionManager.loadApiDefinitions(definitions);
    }

    private void initGatewayRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();
        rules.add(new GatewayFlowRule("rule_api")
                .setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_CUSTOM_API_NAME)
                .setCount(10)
                .setIntervalSec(1)
        );
        GatewayRuleManager.loadRules(rules);
    }

    private void initDegradeRules(){
        List<DegradeRule> rules = new ArrayList<>();
        rules.add(new DegradeRule("rule_api")
                .setCount(5)
                .setGrade(CircuitBreakerStrategy.ERROR_COUNT.getType())
                .setTimeWindow(10)
        );
        DegradeRuleManager.loadRules(rules);
    }

    private void initSystemRule() {

        List<SystemRule> rules = new ArrayList<>();

        SystemRule rule1 = new SystemRule();
        // max load is 3
        rule1.setHighestSystemLoad(3.0);
        // max cpu usage is 60%
        rule1.setHighestCpuUsage(0.6);
        // max avg rt of all request is 10 ms
        rule1.setAvgRt(10);
        // max total qps is 20
        rule1.setQps(20);
        // max parallel working thread is 10
        rule1.setMaxThread(10);

        rules.add(rule1);

        SystemRuleManager.loadRules(rules);
    }
}
