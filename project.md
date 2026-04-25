🍵 TeaProject (茶叶溯源系统核心工程)
├── 📄 pom.xml                             # 📦 采购清单：记录了建设茶馆需要的所有外部工具包（如Spring Boot、MyBatis、WebSocket等）。
├── 📄 tea_db.sql                          # 🗄️ 数据库图纸：包含了茶馆账本、仓库货架的设计图及初始测试数据。
│
├── 📂 src/main/java/com/tea/trace/        # ☕ 核心大脑（后端 Java 逻辑区）
│   ├── 🚀 TeaTraceSystemApplication.java  # 【总电闸】程序的启动入口，运行它，整个茶馆就开始营业。
│   │
│   ├── 📂 config/                         # ⚙️ 基础设施部
│   │   └── WebSocketConfig.java           # 🔌 通讯基站：配置了HTTP Session到WS的状态同步握手拦截器，打通身份壁垒。
│   │
│   ├── 📂 controller/                     # 🌐 前台接待大厅（负责接收网页端的各种请求）
│   │   ├── AuthController.java            # 🛡️ 门卫：处理注册、登录。已装备MD5防脱库安检门，且向下兼容老账本的明文密码。
│   │   ├── TeaController.java             # 💁 导购：负责展示茶叶列表、查询溯源信息。
│   │   └── OrderController.java           # 💰 收银员：处理顾客的下单请求。
│   │
│   ├── 📂 handler/                        # 📡 实时通讯塔
│   │   └── ChatHandler.java               # 🎧 掌柜联络专员：负责处理全双工客服通讯。内置1秒级防刷防火墙与动态管理员寻址系统。
│   │
│   ├── 📂 service/                        # 🧠 业务经理办公室（处理复杂的商业规则）
│   │   └── TeaServiceImpl.java            # 👔 核心经理：专门负责高难度的逻辑，比如“如何在顾客下单时精准扣减库存而不出错”。
│   │
│   ├── 📂 mapper/                         # 🗄️ 仓库保管员（专门负责和地下仓库沟通）
│   │   └── UserMapper/TeaMapper.java      # 🤖 机器人搬运工：负责把数据库里的数据搬上来，或者把新数据存下去。
│   │
│   └── 📂 entity/                         # 🏷️ 实体档案库
│       └── TeaUser/TeaMessage.java        # 📑 标准表格：定义了用户、消息、订单等数据的标准格式。
│
└── 📂 src/main/resources/                 # 🎨 物资与装修室
├── 📄 application.yaml                # 🎛️ 核心配电箱：数据库连接配置，以及Jackson时间戳ISO-8601标准格式化输出配置。
│
└── 📂 static/                         # 🖼️ 前台门面与装修材料（纯前端资源）
├── 📄 index.html                  # 🏠 散客大厅：普通用户的主界面，内置断线自动重连与瞬时聊天气泡。
├── 📄 profile.html                # 🛠️ 掌柜后台：管理员控制台，客服中心模块已完美剥离并独立运作。
├── 📄 login.html                  # 🚪 迎宾大门：用户登录注册页面。
└── 📂 uploads/                    # 📦 储物间：存放用户上传的商品图片、轮播图等静态文件。