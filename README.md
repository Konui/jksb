## 基于Qbot的zzu打卡
基于酷Q的[Qbot框架](https://github.com/ForteScarlet/simple-robot-core)
包名|类名|说明
  :-|  :-|  :-
command|Commands|Qbot命令枚举类
config|ExecutorConfig|线程池配置类
crawler|Jskb|爬虫类
entiry|AccountEntiry|储存用户打卡信息
| |TbData|储存用户打卡过程中的json数据
listen|DefaultListen|默认的消息监听类
| |JksbListen|打卡相关的消息监听类
schedule|AutoDk|定时自动打卡
service|DkService|打卡服务类
| |SendMsgService|全局送信服务类

## 目前问题
单一ip多次打卡会被要求输入验证码，未实现ip代理池

***
##### 另一分支是早期python写的单次打卡脚本，中间打卡系统升级过一次，地址和提交数据已经变更，现不能使用
