# t-im
     前后端分离的仿微信web im 在线聊天系统

github: https://github.com/caotaoX/t-im <br>
gitee:  https://gitee.com/ct_1466909669/t-im <br>

后端： spring boot , Hutool , netty , disruptor 等 <br>
前端:  vue , axios, element-ui, lemon-imui <br>
- 前端最重要用到了 fanjyy 大佬开源的 lemon-imui 组件
- 地址：https://github.com/fanjyy/lemon-imui <br>

数据库、缓存：mongodb redis <br>

# 说明
    一直想做一个聊天系统，但奈何前端不是特别的好，有幸在码云看到fanjyy开源的仿微信vue组件，就开始搭建起自己的IM系统
    后端代码和表都是按照自己理解设计的，有兴趣的朋友们可以借鉴一下，哈哈哈，因为白天要上班，做的比较快，代码有地方是
    不严谨的，有些权限上的问题。

# 部署
    1、拉取项目，安装mongodb、redis，修改配置文件。
    2、将default文件夹放到配置文件 file.upload.abspath.prefix= 配置路径/default。
    3、前端安装npm,cmd下cd到ui目录,执行 npm install下拉依赖，启动前端。

体验地址：http://49.7.129.239:4100 <br><br>
现有功能；
- 单点登录
- 用户注册
- 用户修改（点击头像）
- 添加加好友，好友管理。
- 创建群，群用户管理，群修改。
- 删除漫游聊天数据，消息撤回。
- 点击查看图片，右键下载图片和附件。
- 系统消息通知。
- 视频通讯(需要https,无法体验)。
- 共享屏幕(需要https,无法体验)。



![img.png](img.png)

![img_1.png](img_1.png)

![img_2.png](img_2.png)

![img_3.png](img_3.png)


