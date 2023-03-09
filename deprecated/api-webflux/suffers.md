# log4j2日志系统

## KeyValuePair导致日志不正常

有这玩意日志就变这样

```json
{
  "logEvent":"No active profile set, falling back to default profiles: default",
  "timestamp":"2020-04-17T19:09:28.774+0800"
}
```

首先在mvc和webflux中，log4j2都是用LogEventWithAdditionalFields来包括日志事件。
区别在于webflux在记录日志的时候使用的是MutableLogEvent，而不是mvc的Log4jLogEvent，导致使用的序列化类不一样。
apache社区上也有人反馈了这个bug([https://issues.apache.org/jira/projects/LOG4J2/issues/LOG4J2-2652?filter=allissues](https://issues.apache.org/jira/projects/LOG4J2/issues/LOG4J2-2652?filter=allissues))

这个bug的修复时间晚于spring boot 2 引入的log4j2的版本2.12.1的发布时间，因此升级为2.13.1后问题消失


