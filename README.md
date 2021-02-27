# 游戏防沉迷 AntiAddictionKit

## 说明
AntiAddictionKit 是为了遵循最新防沉迷政策而编写的一个集实名登记、防沉迷时长限制、付费限制三部分功能的组件，方便国内游戏团队快速接入游戏实现防沉迷功能从而符合政策规定。

### <p style='color:red'>SDK注意事项：</p>

- SDK只提供了实名认证模板，<span style='color:red'>实名认证现在需要自行接入中宣部实名系统或者间接(第三方实名SDK)接入中宣部使命认证系统</span>，自行接入时调用`setHost`设置自己业务服务器， 通过服务端请求中宣部接口进行实名认证然后返回给客户端。中宣部接入详细请参考[网络游戏防沉迷认证指引](https://wlc.nppa.gov.cn/fcm_company/index.html#/guide)
- SDK的登录为伪登录，需要游戏自己实现登录或者接入第三方登录，游戏登录时调用login会根据玩家信息触发实名弹窗判断



## SDK业务流程图

![流程图](./flow.jpg)

## 下载

[Release](https://github.com/xindong/anti-addiction-kit/releases/latest)

支持通过 Android 原生、iOS 原生、Unity 三种方式使用。

## 文档

[Server](/Server/README.md)

[iOS](/iOS/README.md)

[Android](/Android/README.md)

[Unity](/Unity/README.md)

## License

[MIT](https://github.com/xindong/anti-addiction-kit//blob/master/LICENSE)
