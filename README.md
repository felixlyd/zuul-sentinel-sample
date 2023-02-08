# zuul-sentinel-template

该demo：

1. zuul、ribbon、~~hystrix~~实现网关路由转发、限流熔断;
2. ~~引入feign实现微服务调用~~;
3. 引入Sentinel实现限流和熔断;
4. 考虑接口安全设计;
5. 考虑应用历史遗留原因，能够契合老spring cloud应用做局部改造;
6. 能够对限流进行监控;