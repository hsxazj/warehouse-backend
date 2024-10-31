# 仓库管理系统后端

## 前言

此项目是课程：应用软件开发的课设，基于springboot，因为我们为了实现负载均衡，部署了多个实例，所以采用了服务注册与发现和网关的微服务组件

这是我们的前端地址：[ztmaomao/warehouse -cloud-client](https://gitee.com/ztmaomao/warehouse--cloud-client)

## 模块介绍

```
common					通用模块，包含了一些工具类
gateway					网关，配置路由
log-service				日志模块
main-service			主体模块
security-common			安全校验
version-update			版本升级
```

实际上需要打包并且部署的模块只有`gateway`，`log-service`，`main-service`，`version-update`
