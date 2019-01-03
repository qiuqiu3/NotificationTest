## Android 华为、小米系统级推送（全网通）
说到系统级推送大家对 iOS 的 APNs 一定不会陌生，咱们 Android 实际上也有 GCM 系统级推送，但是众所周知的原因咱们还是没办法使用。
国内提供系统级推送的有华为、小米以及魅族，前两个厂家的手机市场占有率最高，本人所在公司的 app 也都集成了前两家的系统级推送。
所谓的全网通就是在集成华为小米系统级推送 SDK 的基础上，让其他品牌的手机用小米推送 SDK 再加上后台进程存活技术让小米推送 SDK 和小米消息服务器保持长连接，废话不多说，上干货。

**总体概括一下：华为、小米用系统级推送，其它厂家用小米推送 SDK + 后台进程存活。**

### 常见的推送接入方式
- **在 Github 上你搜索华为、小米推送能搜出来不少优秀的项目，但是有些项目已经不维护了，有些考虑的很全面封装了很多类（有些开发者用不到）**
- **通常情况 Android 开发都接过像极光、友盟、个推等等三方推送 SDK，以极光为例，想集成华为系统级推送还要花钱呢**
- **自己查看华为、小米三方文档自己接，这种方式接起来比较费时，官方写的 SDK 实际上有些地方是可以优化的，但是优化不太适合新手操作（需要了解 SDK 代码）**

综合以上三点得出结论：封装的类太多（类多意味着 new 的对象多，费内存啊）、三方平台集成华为系统级推送收费（不经济呗）、厂家平台SDK接入方式太啰嗦（还可以优化）

### 我封装的推送类特点
- **轻量级就几个类（优化了二家 SDK 要求注册的服务和接收器），比较省内存；**
- **app 客户端集成只需要半个小时（前提是华为*__*****和小米的推送服务已经申请并且通过）；**
- **服务端代码还是比较简单的，看一下官司方文档也就半个小时的事（我下面提供 Java 版的后台代码，其它语言触类旁通）；**
- **最重要的一点，它真的实现全网通，效果还不错。**

### 后台进程存活实现原理
我本人之前开发的计步 app 就需要后台进程存活，苹果可以读健康数据，Android 只有三星一家，其它的就需要读硬件计步，后台不存活没办法处理跨天数据。
在这期间也看了网上很多文章，比如一像素、双进程拉起、关联启动、前台进程、JobsSrvice等等，我能找到的都试了，效果都不好，细心的你可能已经发现，
Google 和国内的 ROM 厂家都对后台 app 做了严格的限制，我本人是不提倡无节制的使用后台 app 技术，因为 app 都在后台存活我们在使用手机的时间体验是极差的，手机电池也不耐用，半天一充电用户都换苹果了。

**后台进程存活方法**
- 自启白名单：只有微信等少数几个 app 在国内手机上是默认开启的，其它 app 需要引导用户开启（开启状态不能检测），但是手机厂家同样做了重启限制，
一般在未打开 app 的情况下，后台自启的次数应该在五次左右；
- 锁屏清理应用：每个厂家叫法不同，小米应该叫神隐模式。只有微信等少数几个 app 在国内手机上是默认开启的，其它 app 需要引导用户开启（开启状态不能检测），但是手机厂家同样做了重启限制，
这种方式是真正的后台存活，只要你的 app 不被用户主动理清（可以锁住 app，也可以把 app 从最近任务列表中隐藏），你的 app 就真的像微信一样在后台长时间运行了；
- 关联启动：默认是关闭的，不建议用户开启，三方推送 SDK 大多数都通过这种方式拉活一系列 app，大家下个手机助手看一下就知道了，启动一个 app，拉活一堆 app，手机太卡了；
- 系统白名单：最靠谱的后台进程存活方式就是加入系统白名单，但是加入系统白名单谈何容易啊。以华为为例，首先要测试 app 的耗电量，其次还要去华为总部测试培训。通常情况下耗电量这一关就过不去。

实际上以上三种方式都不好，加入系统白名单对于个人开发者还是不太现实，消息推送应该是用户感知的，不到万不得已引导用户设置权限实属下策。

**我的实现方式**  
我的计步 app 就是用的这种方式实现后台进程存活，原理就是启动一个前台服务，这个前台服务播放一段空白音乐文件，前台服务以通知的形式存在，提示用户 app 正在后台运行，如果不需要可以滑掉（比较费电），
这种方式唯一的问题就是太费电（一直播放静音音乐），但是消息推送对用户来说是感知的，根本不需要引导用户设置权限，兼容性还特别好，下面开始进入正题了。

### AndroidManifest.xml
华为和小米用的权限我统一处理，需要注意的是 targetSdkVersion 的设置，我建议 targetSdkVersion 24 是最好设置，可以解决 Android 8 以上出现的屏幕上下留边的问题，
同时也不用兼容 Android 8 新加入的通知栏 API（频道通知）和 应用图标（前景图标和背景颜色）。

同时我还加了 meta-data 自定义标签（MI_PUSH_INFO），如果是给小米用的，这样配置常量比在代码里写要方便很多，具体把例子下来看一下就知道了。

### 华为推送前端
官方给的接入方式太过复杂（需要一个中间层），实现上就两步：连接、getToken，在 PushReceiver 接收 token 上传给自己的后台服务器就行了。
```Java
PendingResult<TokenResult> token = HuaweiPush.HuaweiPushApi.getToken(mClient);
token.await();
```
注册我这里用的是同步的方式，华为系统推送依赖华为移动服务 app，正常用户不会卸载，ROM自带。

### 华为推送后台
```Java
JSONObject param = new JSONObject();
param.put("test", "123");
param.put("intent", "intent://com.example.push/push_detail?json=" + param.toString() + "#Intent;scheme=myscheme;launchFlags=0x10000000;end");
```
核心就几句话，重点看一下 `intent://com.example.push/push_detail` 这个在 Activity 里配置过，当消息到达时点击后就进到配置这个 `intent-filter` 的 Activity，
json 是后台传给 Activity 的值。

### 小米推送前端
也很简单调用 `MiPushClient.registerPush` 之后在 PushMessageReceiver 里接收 regId，我们用 `MiPushClient.setUserAccount` 设置一下帐号，好处是同一个帐号在多个设备上收到推送消息。
小米系统推送依赖 小米服务框架 app，这个 app 是系统级的，同时这个 app 和消息服务器有长连接，所以咱们自己的 app 即使已经退出了，但是小米服务框架在收到消息后就会启动我们 app 的 Activity。

### 小米推送后台
```Java
Constants.useOfficial();
Sender sender = new Sender(appSecret);
Message.Builder builder = new Message.Builder();``
builder.title("通知");``
builder.description("这是测试");
builder.payload(content);
builder.restrictedPackageName("com.example.notificationtest");
builder.notifyType(1);
builder.passThrough(0);
builder.extra(Constants.EXTRA_PARAM_NOTIFY_EFFECT, Constants.NOTIFY_ACTIVITY);
builder.extra(Constants.EXTRA_PARAM_INTENT_URI, "intent:#Intent;launchFlags=0x10000000;component=com.example.notificationtest/.MainActivity;end");
for (String key : extras.keySet()) {
  builder.extra(key, (String) extras.get(key));
}
Message message = builder.build();	    
sender.sendToUserAccount(message, account, 3); 
```
同样很简单，`intent:#Intent;` 部分设置被接收的 Activity，参数用 `builder.extra` 传给 Activity。

### 其它厂家
默认使用小米的推送 SDK 就好，只不过多了一步开启前台服务，原理还是 app 在存活的情况下，小米推送 SDK 和消息服务器有长连接。
