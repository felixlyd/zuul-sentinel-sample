# 前言
本文代码于[GitHub - felixlyd/zuul-sentinel-sample: 该demo：1. zuul、ribbon、hystrix实现网关路由转发、限流熔断；2. 引入feign实现微服务调用；3. 考虑接口安全设计](https://github.com/felixlyd/zuul-sentinel-sample)

由于项目历史遗留问题，需要使用较老的spring cloud组件集成，并且需要契合老应用做局部改造
因此，有以下考虑和要求：

- [x] zuul、ribbon、~~hystrix~~实现网关路由转发、~~限流熔断~~;
- [x] ~~引入feign实现微服务调用~~;
- [x] 引入Sentinel实现限流和熔断;
- [x] 考虑接口安全设计;
- [x] 能够契合老spring cloud应用做~~局部改造~~;
- [x] 能够对限流进行监控;
- [x] 考虑两台网关的限流;
# ~~整合feign、ribbon？~~
[Ribbon：Spring Cloud负载均衡与服务调用组件（非常详细）](http://c.biancheng.net/springcloud/ribbon.html)
引入feign后，feign默认集成了ribbon做负载均衡. feign的默认负载均衡策略为**轮询**
## ~~如何修改feign的默认负载均衡策略？~~
[OpenFeign修改负载均衡策略_wx62e0b69890c77的技术博客_51CTO博客](https://blog.51cto.com/u_15733182/5802823)
[Spring Cloud Feign 负载均衡策略配置_guoqiusheng的博客-CSDN博客](https://blog.csdn.net/guoqiusheng/article/details/88898426)
以上为两种方式，尚未实践
## zuul和feign的区别和联系
![引入zuul的系统架构示意图](https://cdn.nlark.com/yuque/0/2023/png/22002231/1675392420532-3fbee950-a222-44a1-9b44-f968dbedf771.png#averageHue=%23f6f6f6&clientId=ub6c68bb1-e51c-4&from=drop&height=351&id=u11a66a6e&originHeight=468&originWidth=751&originalType=binary&ratio=1&rotation=0&showTitle=true&size=51840&status=done&style=none&taskId=u5f41a7bd-5ade-4c07-b788-22d788686df&title=%E5%BC%95%E5%85%A5zuul%E7%9A%84%E7%B3%BB%E7%BB%9F%E6%9E%B6%E6%9E%84%E7%A4%BA%E6%84%8F%E5%9B%BE&width=563 "引入zuul的系统架构示意图")

1. zuul作为整个应用的流量入口，接收所有的请求，如app、网页等，并且将不同的请求转发至不同的处理微服务模块，其作用可视为nginx；feign则是将当前微服务的部分服务接口暴露出来，并且主要用于各个微服务之间的服务调用。
2. zuul默认集成hystrix和ribbon，并基于http通讯的，用于代理服务；feign默认集成ribbon，可以通过配置集成hystrix，是在服务互相调用时使用，仿rpc通讯。
# ~~引入hystrix？~~
zuul默认集成hystrix，hystrix只能做微服务应用层级的限流，sentinel可以做到接口级的限流
## hystrix原理
[Spring Cloud - Hystrix 原理解析 - 掘金](https://juejin.cn/post/6904887342302134280#heading-18)
![hystrix原理示意图](https://cdn.nlark.com/yuque/0/2023/png/22002231/1675394936260-667617fd-a5ba-44ac-99f5-54fb8a2e1643.png#averageHue=%23181818&clientId=u71559eb0-2810-4&from=paste&height=947&id=ube42723d&originHeight=1323&originWidth=2000&originalType=url&ratio=1&rotation=0&showTitle=true&status=done&style=none&taskId=ud35e264e-4197-467c-845d-6222e78b6cc&title=hystrix%E5%8E%9F%E7%90%86%E7%A4%BA%E6%84%8F%E5%9B%BE&width=1432 "hystrix原理示意图")
hystrix的限流，是基于设置最大的线程池/信号量，来进行限流，因此，是**有限的限流**
## ~~对hystrix进行监控？~~
需引入**hystrix-dashboard**和**turbine**
# 引入zuul
[spring cloud zuul 原理简介及使用](https://zhuanlan.zhihu.com/p/138943446)
[Zuul 网关转发的五种方式 - qiuxuhui - 博客园](https://www.cnblogs.com/qxhIndex/p/14311976.html)
# 引入Sentinel
[版本说明 · alibaba/spring-cloud-alibaba Wiki](https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E)
[网关限流 · alibaba/spring-cloud-alibaba Wiki](https://github.com/alibaba/Sentinel/wiki/%E7%BD%91%E5%85%B3%E9%99%90%E6%B5%81)
[introduction | Sentinel](https://sentinelguard.io/zh-cn/docs/introduction.html)
[Sentinel/sentinel-demo/sentinel-demo-zuul-gateway at master · alibaba/Sentinel](https://github.com/alibaba/Sentinel/tree/master/sentinel-demo/sentinel-demo-zuul-gateway)
[一篇文章彻底学会使用Spring Cloud Alibaba Sentinel](https://zhuanlan.zhihu.com/p/569512349)
[GitHub - sentinel-group/sentinel-awesome: A curated list of awesome things (resource, sample, extensions) for Sentinel](https://github.com/sentinel-group/sentinel-awesome)
[阿里巴巴开源限流降级神器Sentinel大规模生产级应用实践](https://mp.weixin.qq.com/s/AjHCUmygTr78yo9yMxMEyg)
## Sentinel整合到zuul上
### sentinel对接口级进行流控
sentinel 1.6.0 引入了 Sentinel API Gateway Adapter Common 模块，此模块中包含网关限流的规则和**自定义 API **的实体和管理逻辑：

- GatewayFlowRule：网关限流规则，针对 API Gateway 的场景定制的限流规则，可以针对不同 route 或自定义的 API 分组进行限流，支持针对请求中的参数、Header、来源 IP 等进行定制化的限流。
- ApiDefinition：用户自定义的 API 定义分组，可以看做是一些 URL 匹配的组合。比如我们可以定义一个 API 叫 my_api，请求 path 模式为 /foo/** 和 /baz/** 的都归到 my_api 这个 API 分组下面。限流的时候可以针对这个自定义的 API 分组维度进行限流。

> Q：sentinel有分组api限流的功能，如果我的两个接口都放在同一个api下面，设置10s内最多访问5次。那么我实际的访问中，是两个接口一共每10s内最多访问5次，还是两个接口分别10s内最多访问5次？
> 
> A：如果你的两个接口都放在同一个 API 下面，设置 10s 内最多访问 5 次，那么你实际的访问中，两个接口一共每 10s 内最多访问 5 次。这意味着如果一个接口被访问了 5 次，另一个接口在 10s 内不能再被访问。

### 应用被sentinel识别为网关
方式1：启动参数
```shell
java -Dcsp.sentinel.app.type=1 -jar zuul-gateway.jar
```
方式2：配置文件
在资源目录下新建sentinel.properties文件，使用以下配置
```properties
csp.sentinel.app.type=1
```
方式3：代码
在main主函数中加入以下代码
```java
System.setProperty("csp.sentinel.app.type", "1")
```
## Sentinel-Dashboard对流量的监控
### sentinel-dashboard示例图
![image.png](https://cdn.nlark.com/yuque/0/2023/png/22002231/1675736208009-789682ab-6a7e-4724-8f05-ccd0a64fbe33.png#averageHue=%23fbfbfb&clientId=u899daca7-615a-4&from=paste&height=309&id=ude9f9260&originHeight=1237&originWidth=2133&originalType=binary&ratio=1&rotation=0&showTitle=false&size=105453&status=done&style=none&taskId=udf5974d0-5c52-40a8-8d59-1eae588d091&title=&width=533)![image.png](https://cdn.nlark.com/yuque/0/2023/png/22002231/1675736228787-92987945-80a9-4b29-96e0-e7b9d4ef4c65.png#averageHue=%23fcfcfc&clientId=u899daca7-615a-4&from=paste&height=178&id=u5b6f5049&originHeight=714&originWidth=2165&originalType=binary&ratio=1&rotation=0&showTitle=false&size=71498&status=done&style=none&taskId=u826b4025-8ad4-4816-a96f-c69139b134f&title=&width=541)
### 相比hystrix的dashboard
![image.png](https://cdn.nlark.com/yuque/0/2023/png/22002231/1675736316226-fdff4168-86e2-42ba-a341-3047f93000c2.png#averageHue=%23f5f4f4&clientId=u899daca7-615a-4&from=paste&height=154&id=uf7128e30&originHeight=614&originWidth=2412&originalType=url&ratio=1&rotation=0&showTitle=false&size=415139&status=done&style=none&taskId=u51a20b9f-8662-4cae-9973-b62919f4924&title=&width=603)
![image.png](https://cdn.nlark.com/yuque/0/2023/png/22002231/1675736322493-2d27de4d-a137-4d98-993c-8d1030ac6d02.png#averageHue=%23f9f8f8&clientId=u899daca7-615a-4&from=paste&height=311&id=u3a47db5c&originHeight=414&originWidth=690&originalType=url&ratio=1&rotation=0&showTitle=false&size=34243&status=done&style=none&taskId=uea684a94-3d23-4770-a600-0a7a8ff67d9&title=&width=518)
### ~~sentinel监控数据持久化？~~
dashboard实时监控仅能查看5分钟内的metric数据，持久化到数据库中？需要自行实现相关接口代码。
## Sentinel规则持久化

1. 代码
2. ~~文件~~
3. 关系数据库

默认情况下：sentinel-dashboard能够读取**代码中**的规则配置到**内存中**，并且可以热修改
## Sentinel规则语法
### 流控规则
[流量控制 · alibaba/spring-cloud-alibaba Wiki](https://github.com/alibaba/Sentinel/wiki/%E6%B5%81%E9%87%8F%E6%8E%A7%E5%88%B6)
一条限流规则主要由下面几个因素组成，我们可以组合这些元素来实现不同的限流效果：

- `resource`：资源名，即限流规则的作用对象
- `count`: 限流阈值
- `grade`: 限流阈值类型（QPS 或并发线程数）
- `limitApp`: 流控针对的调用来源，若为 default 则不区分调用来源
- `strategy`: 调用关系限流策略
- `controlBehavior`: 流量控制效果（直接拒绝、Warm Up、匀速排队）
### 熔断降级
[熔断降级 · alibaba/spring-cloud-alibaba Wiki](https://github.com/alibaba/Sentinel/wiki/%E7%86%94%E6%96%AD%E9%99%8D%E7%BA%A7)
熔断降级规则（DegradeRule）包含下面几个重要的属性：

| **Field** | **说明** | **默认值** |
| --- | --- | --- |
| resource | 资源名，即规则的作用对象 |  |
| grade | 熔断策略，支持慢调用比例/异常比例/异常数策略 | 慢调用比例 |
| count | 慢调用比例模式下为慢调用临界 RT（超出该值计为慢调用）；异常比例/异常数模式下为对应的阈值 |  |
| timeWindow | 熔断时长，单位为 s |  |
| minRequestAmount | 熔断触发的最小请求数，请求数小于该值时即使异常比率超出阈值也不会熔断（1.7.0 引入） | 5 |
| statIntervalMs | 统计时长（单位为 ms），如 60*1000 代表分钟级（1.8.0 引入） | 1000 ms |
| slowRatioThreshold | 慢调用比例阈值，仅慢调用比例模式有效（1.8.0 引入） |  |

### 系统规则
[system-adaptive-protection | Sentinel](https://sentinelguard.io/zh-cn/docs/system-adaptive-protection.html?spm=sentinel-github.wiki.0.0.0)
[Sentinel/SystemGuardDemo.java at master · alibaba/Sentinel](https://github.com/alibaba/Sentinel/blob/master/sentinel-demo/sentinel-demo-basic/src/main/java/com/alibaba/csp/sentinel/demo/system/SystemGuardDemo.java)
Sentinel 做系统自适应保护的目的：

- 保证系统不被拖垮
- 在系统稳定的前提下，保持系统的吞吐量

系统保护规则是应用整体维度的，而不是资源维度的，并且**仅对入口流量生效**。入口流量指的是进入应用的流量。
系统自适应限流：**Load**（仅对 Linux/Unix-like 机器生效）：当系统 load1 超过阈值，且系统当前的并发线程数超过系统容量时才会触发系统保护。系统容量由系统的 `maxQps * minRt` 计算得出。设定参考值一般是 `CPU cores * 2.5` .
### 热点参数
[parameter-flow-control | Sentinel](https://sentinelguard.io/zh-cn/docs/parameter-flow-control.html)
何为热点？热点即经常访问的数据。很多时候我们希望统计某个热点数据中访问频次最高的 Top K 数据，并对其访问进行限制。比如：

- 商品 ID 为参数，统计一段时间内最常购买的商品 ID 并进行限制
- 用户 ID 为参数，针对一段时间内频繁访问的用户 ID 进行限制

热点参数限流会统计传入参数中的热点参数，并根据配置的限流阈值与模式，对包含热点参数的资源调用进行限流。热点参数限流可以看做是一种特殊的流量控制，仅对包含热点参数的资源调用生效。
![热点流控示意图](https://cdn.nlark.com/yuque/0/2023/png/22002231/1675754911763-2f9c639e-9fea-4b5d-aa7a-b3c20420bfd7.png#averageHue=%233f3e3e&clientId=uac58541a-a7a7-4&from=drop&height=137&id=uLPMd&originHeight=274&originWidth=902&originalType=binary&ratio=1&rotation=0&showTitle=true&size=66080&status=done&style=none&taskId=u4b0ba9a0-eb04-4a4e-b082-27d7804afb4&title=%E7%83%AD%E7%82%B9%E6%B5%81%E6%8E%A7%E7%A4%BA%E6%84%8F%E5%9B%BE&width=451 "热点流控示意图")
Sentinel 利用 LRU 策略统计最近最常访问的热点参数，结合令牌桶算法来进行参数级别的流控。

在网关限流中，`GatewayFlowRule `下的`paramItem` 会配置该规则是热点规则还是普通流控规则
### ~~黑白名单？~~
**网关限流中没有**
[黑白名单控制 · alibaba/spring-cloud-alibaba Wiki](https://github.com/alibaba/Sentinel/wiki/%E9%BB%91%E7%99%BD%E5%90%8D%E5%8D%95%E6%8E%A7%E5%88%B6)
### 网关api限流规则
其中网关限流规则 `GatewayFlowRule `的字段解释如下：

- `resource`：资源名称，可以是网关中的 `route` 名称或者用户自定义的 API 分组名称。
- `resourceMode`：规则是针对 API Gateway 的` route（RESOURCE_MODE_ROUTE_ID）`还是用户在 Sentinel 中定义的 API 分组`（RESOURCE_MODE_CUSTOM_API_NAME）`，默认是 route。
- `grade`：限流指标维度，同限流规则的 `grade `字段。
- `count`：限流阈值
- `intervalSec`：统计时间窗口，单位是秒，默认是 1 秒。
- `controlBehavior`：流量整形的控制效果，同限流规则的 `controlBehavior `字段，目前支持快速失败和匀速排队两种模式，默认是快速失败。
- `burst`：应对突发请求时额外允许的请求数目。
- `maxQueueingTimeoutMs`：匀速排队模式下的最长排队时间，单位是毫秒，仅在匀速排队模式下生效。
- `paramItem`：**参数限流配置。若不提供，则代表不针对参数进行限流，该网关规则将会被转换成普通流控规则；否则会转换成热点规则。**其中的字段：
   - `parseStrategy`：从请求中提取参数的策略，目前支持提取来源 IP`（PARAM_PARSE_STRATEGY_CLIENT_IP）`、`Host（PARAM_PARSE_STRATEGY_HOST）`、任意 Header`（PARAM_PARSE_STRATEGY_HEADER）`和任意 URL 参数`（PARAM_PARSE_STRATEGY_URL_PARAM）`四种模式。
   - `fieldName`：若提取策略选择 Header 模式或 URL 参数模式，则需要指定对应的 header 名称或 URL 参数名称。
   - `pattern`：参数值的匹配模式，只有匹配该模式的请求属性值会纳入统计和流控；若为空则统计该请求属性的所有值。（1.6.2 版本开始支持）
   - `matchStrategy`：参数值的匹配策略，目前支持精确匹配`（PARAM_MATCH_STRATEGY_EXACT）`、子串匹配`（PARAM_MATCH_STRATEGY_CONTAINS）`和正则匹配`（PARAM_MATCH_STRATEGY_REGEX）`。（1.6.2 版本开始支持）

用户可以通过 `GatewayRuleManager.loadRules(rules) `手动加载网关规则，或通过`GatewayRuleManager.register2Property(property)` 注册动态规则源动态推送（推荐方式）。
### ~~集群流控？~~
**网关限流中没有**
为什么要使用集群流控呢？假设我们希望给某个用户限制调用某个 API 的总 QPS 为 50，但**机器数可能很多**（比如有 100 台）。这时候我们很自然地就想到，找一个 server 来专门来统计总的调用量，其它的实例都与这台 server 通信来判断是否可以调用。这就是最基础的集群流控的方式。
另外**集群流控还可以解决流量不均匀导致总体限流效果不佳的问题**。假设集群中有 10 台机器，我们给每台机器设置单机限流阈值为 10 QPS，理想情况下整个集群的限流阈值就为 100 QPS。**不过实际情况下流量到每台机器可能会不均匀，会导致总量没有到的情况下某些机器就开始限流。**因此仅靠单机维度去限制的话会无法精确地限制总体流量。而集群流控可以精确地控制整个集群的调用总量，结合单机限流兜底，可以更好地发挥流量控制的效果。
## Sentinel多台机器
### 后端
后端机器限流是单机限流，如果有2台机器，希望QPS限制到100，那么单机设为50.
### 网关
例如某个单机app的QPS限制100，启动两台网关，两个网关加起来限制该app的QPS为100.
# 方案讨论
## 网关+限流

1. **zuul+sentinel 所有请求都会经过监听**
2. sentinel 手动加api接口进行监听，就不会影响之前的代码
3. adapter往一个adapter2上转发（双网关思路），adapter2为一个新的用于做数据安全、限流、分发的网关
## 数据安全处理、xml和json转换
数据安全处理内容：加解密、解签名、防重放验证

### zuul流程示意图
#### zuul 1.x流程
![zuul工作的流程示意图](https://cdn.nlark.com/yuque/0/2023/png/22002231/1675823783661-48ee6a45-bbd0-43f3-ba1d-8334bd5ad5ba.png#averageHue=%23f5f5f5&clientId=u2a962d77-9de2-4&from=paste&id=MsN2V&originHeight=377&originWidth=631&originalType=url&ratio=1&rotation=0&showTitle=true&size=64112&status=done&style=none&taskId=u48ed5938-8372-4921-9309-30c161ddefb&title=zuul%E5%B7%A5%E4%BD%9C%E7%9A%84%E6%B5%81%E7%A8%8B%E7%A4%BA%E6%84%8F%E5%9B%BE "zuul工作的流程示意图")
zuul转发：http请求->prefilter->routingfilter->后端应用->postfilter->http响应
#### zuul 2.x流程
![image.png](https://cdn.nlark.com/yuque/0/2023/png/22002231/1675840677838-2d579058-a57b-4abd-8f35-75ad760fc3d8.png#averageHue=%23fcfbf7&clientId=u2a962d77-9de2-4&from=paste&height=679&id=uee367270&originHeight=906&originWidth=1055&originalType=url&ratio=1&rotation=0&showTitle=false&size=136331&status=done&style=none&taskId=udd1b9969-dd25-4527-a6d6-051d36248e6&title=&width=791)
#### spring cloud gateway 流程
![](https://cdn.nlark.com/yuque/0/2023/webp/22002231/1675841047589-90ea13b7-6887-4675-ba9b-0c4fc1511738.webp#averageHue=%23f7f7f7&clientId=u2a962d77-9de2-4&from=paste&id=u9d2a9f45&originHeight=708&originWidth=1080&originalType=url&ratio=1&rotation=0&showTitle=false&status=done&style=none&taskId=u035f21b4-1977-4e26-b349-1c6e771f02f&title=)
### 方案1：http请求和esb请求由zuul网关转发
http请求能通过controller层手动转发，esb请求（netty请求）转发需要发起新的http请求访问后端应用。
#### http请求通过controller层手动转发请求
~~方式1：~~
```java
@PostMapping("/{path1}")
    public String test(@PathVariable String path1){
        log.info("/rule/" + path1);
        return "/rule/" + path1;
    }

@PostMapping("/{path1}/{path2}")
public String test2(@PathVariable String path1, @PathVariable String path2){
    log.info("/rule/" + path1 + "/" + path2);
    return "/rule/" + path1 + "/" + path2;
}

@PostMapping("/{path1}/{path2}/{path3}")
public String test2(@PathVariable String path1, @PathVariable String path2, @PathVariable String path3){
    log.info("/rule/" + path1 + "/" + path2 + "/" + path3);
    return "/rule/" + path1 + "/" + path2 + "/" + path3;
}
```
> spring boot 2.6.0之前对多级路径支持不友好. 

方式2：
```java
@PostMapping("/**")
public String test2(HttpServletRequest request){
    String path = request.getServletPath();
    List<String> paths = Arrays.stream(path.split("/")).filter(i -> !i.contains("rule-controller")).collect(Collectors.toList());
    String zuulPath = "/rule/" + String.join("/", paths);
    log.info(zuulPath);
    return zuulPath;
}
```
#### esb请求发起新的http请求
```java
@PostMapping("/**")
public String test2(HttpServletRequest request, @RequestBody Map<String, String> reqMap){
    log.info("解密--");
    String path = request.getServletPath();
    List<String> paths = Arrays.stream(path.split("/")).filter(i -> !i.contains("rule-controller")).collect(Collectors.toList());
    String zuulPath = "http://localhost:8081/rule" + String.join("/", paths);
    RestTemplate restTemplate = new RestTemplate();
    String responseBody = restTemplate.postForObject(zuulPath, reqMap, String.class);
    log.info("加密--");
    return responseBody;
}
```
esb请求发起http请求如果走feign，则不会过zuul的转发；如果走restTemplate，需要告诉ip:port发送给网关自己（可以走localhost:port，也可以走域名（F5）去负载均衡），然后到zuul，再由zuul转发。
#### PRE-网关转发请求前
前端请求报文需要数据安全设计（解密、防重放、签名），以及esb请求报文协议转换需要xml转换为json

1. 不自定义zuul的prefilter。对于http请求，先进行数据安全处理、报文协议转换，然后通过controller转发到zuul，再由zuul做转发；对于esb请求，先进行数据安全处理、报文协议转换，然后发起新的http请求到zuul，再由zuul做转发。
2. 自定义zuul的prefilter。对于http请求，在zuul的prefilter中实现数据安全处理、报文协议转换；对于esb请求，发起新的http请求到zuul，再由zuul的prefilter进行数据安全处理、报文协议转换。
> zuul 1.x不支持netty转发

#### POST-网关转发请求后
前端请求的响应报文需要加密，以及esb请求的响应报文需要json转xml

1. 不自定义zuul的postfilter。**对于http请求通过controller转发到zuul，不太可能，因为zuul转发会通过postfilter直接返回调用方**。必须在postfilter中处理响应报文，或者通过研究源码写切面从postfilter捞取响应报文；对于esb请求，发起了新的http请求，可以实现。但是，发起http请求如果走feign，则不会过zuul的转发；如果走restTemplate，需要告诉ip:port发送给网关自己（可以走localhost:port，也可以走域名（F5）去负载均衡），然后到zuul，再由zuul转发。
2. 自定义zuul的postfilter。对于http请求，在zuul的postfilter中实现数据安全处理、报文协议转换；对于esb请求，发起新的http请求到zuul，再由zuul的postfilter进行数据安全处理、报文协议转换。

综上所述，如果是http请求，**直接在zuul网关中的prefilter和postfilter中处理请求响应报文最简便**。如果是esb请求，发起新的http请求给网关自己，也直接在zuul网关中prefilter和postfilter处理请求响应报文。

如果均走prefilter和postfilter，就需要区分二者。因为http请求只需要数据安全处理和限流而不需要报文协议转换，esb请求只需要报文协议转换不涉及数据安全处理，可能涉及限流。
### 方案2：http请求走zuul网关转发，esb请求沿用feign客户端转发

1. http请求：分别在zuul网关中的prefilter和postfilter中实现对应数据安全处理。
2. esb请求：沿用之前的流程

因为http请求和esb请求处理请求响应报文规则不同，因此http请求走zuul，改prefilter和postfilter；esb请求在feign客户端前后另加一套逻辑。
