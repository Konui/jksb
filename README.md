## 健康上报的打卡应用（现已停用，相关框架的Demo学习）
基于酷Q的[Qbot框架](https://github.com/ForteScarlet/simple-robot-core)，使用`HttpClient`爬取信息，`Jsoup`解析。能够定时自动打卡并通过qq通知打卡结果。
## 使用说明

### Windows

* 下载酷q，并登录运行。需要配置上报地址`post_url=http://127.0.0.1:15510/coolq`，具体参照[CQHTTP插件配置](https://cqhttp.cc/docs/4.15/#/Configuration)。
* 下载源码或者jar包并运行即可。

### Linux（推荐）

* 先使用docker下载酷Q。

  `docker pull richardchien/cqhttp:latest `

* 然后使用docker运行酷Q，将命令中`###*###`内容替换。
```
  docker run  --rm --name qbot -d  -p 9000:9000   -p 5700:5700   -e COOLQ_ACCOUNT=###Q号###  -e CQHTTP_POST_URL=http://172.17.0.1:15510/coolq   -e VNC_PASSWD=###设置界面连接密码### richardchien/cqhttp
```

* 访问服务器的9000端口，输入刚才设置的连接密码，登录酷q，确保CQHTTP插件开启。

* 下载jar包，并后台运行，日志在bot.log中。

  `nohup java -jar qbot.jar >bot.log &`

若需要修改QBot请根据[[simple-robot 机器人开发者使用文档](https://www.kancloud.cn/forte-scarlet/simple-coolq-doc/1519393)进行修改，修改后   
在终端使用`mvn clean install -Dmaven.test.skip`生成jar包。
### 命令
* 群聊需要@机器人，在消息前后都可以，私聊则不需要。
* 打卡以更新打卡QQ号为键，打卡消息通知在更新打卡信息的群或个人聊天中。

命令关键词|说明
:- | :-
命令|查看所有命令
更新信息|更新打卡的学号、密码<br>群聊需要用-分割，私聊使用空格分割<br>格式如：更新信息-111111-111111
查询信息|查询打卡的学号、密码和自动打卡状态
打卡|进行单次打卡
【开启】/【关闭】自动打卡|开启或关闭自动打卡<br>自动打卡时间在00:15<br>需要更改请自行修改源码中注解
清空信息|清空打卡信息
## 结构说明
包名|类名|说明
  :-|  :-|  :-
command|Commands|Qbot命令枚举类
config|ExecutorConfig|线程池配置类
| |JobFactory|quartz工厂,将job注入ioc
| |SpringContextUtils|获取上下文
crawler|Jskb|爬虫类
entiry|AccountEntiry|储存用户打卡信息
| |TbData|储存用户打卡过程中的json数据
listen|DefaultListen|默认的消息监听类
| |JksbListen|打卡相关的消息监听类
schedule|AutoDk|定时自动打卡
service|DkService|打卡服务类
| |SendMsgService|全局送信服务类

## 注意问题
* 多次打卡会被要求输入验证码。
* 数据量较小，所以所有数据直接储存在对象内，不作持久化处理。
* 部分打卡命令提示的语句与此页面的命令不符，可能导致不能正确识别，以此页面命令为准，因已停用不做修复。
---
##### 另一分支是早期使用python的requests库写的单次打卡脚本，中间打卡系统升级过一次，地址和提交数据已经变更，现已不能使用，但可以重新抓包修改后使用。
##### 如有问题请在`issue`中反馈。
