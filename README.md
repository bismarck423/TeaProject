 🍃 茶叶溯源管理系统 (Tea Traceability System)

这是一个前后端分离的现代化茶叶业务综合平台。它不仅仅是一个简单的“增删改查”电商骨架，更是我对全栈架构、双向实时通讯以及系统底层网络安全的一次完整实战。

在这个项目里，我从零到一搭建了从商品展示、购物车、订单沙箱结算，到图文并茂的茶文化内容发布的整个业务闭环。

 🛠️ 技术栈 (Tech Stack)

项目坚持了清晰的代码分层和全局健壮性设计，未盲目堆砌臃肿的框架：

 后端: Java 17 + Spring Boot 3 + MyBatis-Plus
 实时通讯: Spring WebSocket (纯手写全双工、防断线架构)
 前端: Vue 3 (SFC 思想, CDN 注入) + Element Plus + ECharts
 数据库: MySQL 8.0

 ✨ 核心特性 (Core Features)

 🛍️ 交易全链路: 实现了从商品浏览 -> 购物车管理 -> 订单结算的全流程，并内置了虚拟余额与模拟沙箱支付。
 💬 瞬时全双工客服: 彻底抛弃了低效的 HTTP 轮询。基于 WebSocket 打造了左右分栏的客服调度中心，支持断线自动重连、红点未读提示和实时消息回执。
 📊 数据可视化面板: 后台管理员专属数据看盘，接入 ECharts 实时洞察平台销量走势与用户活跃度。
 📰 文化沉淀板块: 支持动态发布茶道知识与产地故事，支持视频挂载与图文排版。

 🛡️ 安全与健壮性底座 (Security & Robustness)


1.  WebSocket 内存级防刷: 针对实时通讯接口，手写了极其轻量的高效内存限流（Rate Limiting）。一旦遭遇恶意脚本的密集的并发轰炸，超频请求会被瞬间丢弃，死死护住数据库的连接池。
2.  动态路由与防越权拦截: 彻底剔除了前端硬编码 ID 的陋习，在服务端强制重写消息发送者身份，并动态检索系统管理员池，杜绝了水平越权和 Payload 伪造。
3.  无明文落盘: 账户系统全面接入强散列哈希加密，保障极端的拖库场景下用户数据的绝对安全。
4.  无缝状态同步: 编写了原生的 HTTP Session 握手拦截器，打破了 Web 容器与 WS 协议间的身份壁垒。

 🚀 快速启动 (How to Run)

1. 准备数据库
 在 MySQL 中新建数据库 `tea_db` (字符集推荐使用 `utf8mb4`)。
 导入项目根目录提供的完整备份文件 `tea_db.sql`。

2. 配置后端
 打开 `src/main/resources/application.yaml`。
 修改 `spring.datasource` 下的 `username` 和 `password` 为你的本地数据库环境。

3. 运行项目
 找到入口类 `TeaTraceSystemApplication.java`，直接 Run 启动 Spring Boot 服务。
 打开浏览器访问: `http://localhost:8080/index.html`。

> 测试账号：
> 普通用户: test/test2/USER / 123456
> 管理员后台: admin / 123456


