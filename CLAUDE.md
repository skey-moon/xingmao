# 兴茂食材配送管理系统

## 项目概述
- **项目名称**：兴茂食材配送管理平台
- **技术栈**：Spring Boot + Vue.js
- **项目类型**：食材配送企业双端系统（公司管理端 + 客户下单端）
- **创建日期**：2026/06/02
- **最后更新**：2026/06/03（订单派单、交易流水大屏）

---

## 项目结构

```
兴茂app/
├── backend/                    # Spring Boot 后端 (端口8080)
│   ├── pom.xml                 # Maven配置 (Spring Boot 2.7.18, MyBatis-Plus, JWT, Security, Swagger)
│   └── src/main/java/com/xingmao/
│       ├── XingMaoApplication.java     # 主启动类
│       ├── config/                      # 配置类
│       │   ├── SecurityConfig.java      # Spring Security配置（当前为debug模式，所有请求permitAll）
│       │   ├── JwtAuthenticationFilter.java
│       │   ├── WebConfig.java
│       │   ├── GlobalExceptionHandler.java
│       │   └── MybatisPlusConfig.java
│       ├── entity/                       # 实体类
│       │   ├── SysUser.java, Food.java, Orders.java, OrderItem.java, Delivery.java
│       ├── mapper/                      # MyBatis Mapper
│       ├── service/                     # 业务层
│       │   ├── impl/
│       │   └── UserService.java, FoodService.java, OrderService.java, DeliveryService.java, StatService.java
│       ├── controller/                  # REST控制器
│       │   ├── UserController.java      # /api/user/*
│       │   ├── FoodController.java      # /api/food/*
│       │   ├── OrderController.java      # /api/order/*
│       │   ├── DeliveryController.java    # /api/delivery/*
│       │   ├── StatController.java       # /api/stat/*
│       │   └── TestController.java       # 临时测试接口
│       └── utils/
│           ├── JwtUtils.java            # JWT工具类 (jjwt 0.11.5)
│           ├── Result.java              # 统一响应格式
│           └── CodeGenerator.java       # 订单号生成
├── frontend/                  # Vue 前端 (端口3000, 代理/api到8080)
│   ├── package.json
│   ├── vite.config.js         # 代理配置：/api -> localhost:8080
│   └── src/
│       ├── main.js
│       ├── App.vue
│       ├── router/index.js
│       ├── api/
│       │   ├── request.js      # Axios封装，响应拦截器返回response.data
│       │   ├── user.js, food.js, order.js, delivery.js, stat.js
│       ├── stores/user.js       # Pinia状态管理
│       ├── components/Layout.vue
│       └── views/
│           ├── Home.vue         # 首页统计
│           ├── user/Login.vue   # 登录/注册页面
│           ├── user/List.vue    # 用户管理
│           ├── food/List.vue    # 食材管理（支持排序和图片上传）
│           ├── order/List.vue   # 订单管理（管理员/员工使用）
│           ├── order/Create.vue  # 创建订单页面（客户使用）
│           ├── order/MyOrders.vue # 我的订单页面（客户使用）
│           ├── Dashboard.vue      # 数据大屏（管理员/员工使用）
│           └── delivery/List.vue # 配送管理
└── database/
    └── xingmao_food.sql       # 数据库初始化脚本

## 数据库
- 库名：xingmao_food
- 用户：root / 123456
- 表：sys_user, food, orders, order_item, delivery

---

## 当前状态

### 已完成功能
1. ✅ 用户注册/登录（JWT认证）
2. ✅ 用户管理（CRUD、角色管理）
3. ✅ 食材管理（CRUD、分类筛选、排序功能）
4. ✅ 订单管理（列表、详情、搜索）
5. ✅ 配送管理（状态更新）
6. ✅ 首页统计（食材数、订单数、用户数等）
7. ✅ 前端页面美化（渐变背景、圆角卡片、动效）
8. ✅ 日期范围筛选（订单列表）
9. ✅ 食材图片上传功能
10. ✅ 订单创建页面（用户端）
11. ✅ **三端角色分离** - 管理员/公司员工/客户看到不同菜单和页面
12. ✅ **登录身份选择** - 登录时可选客户、公司员工、管理员身份
13. ✅ **数据大屏** - 展示实时订单、统计数据的可视化大屏
14. ✅ **员工权限调整** - 员工可管理食材、订单、配送，但不能管理用户
15. ✅ **订单派单功能** - 员工/管理员可分配配送员，支持自动分配
16. ✅ **交易流水大屏** - 管理员可查看收入/支出/净利润等财务数据

### 已知问题
1. ⚠️ 后端SecurityConfig当前为debug模式（所有请求permitAll），上线前需调整
2. ⚠️ admin账号密码加密可能有问题，如登录失败需重新注册

### 待完成功能
1. [ ] 完整的登录认证流程（JWT + Security）
2. [x] 日期范围筛选（订单列表）✅
3. [x] 食材图片上传功能 ✅
4. [x] 订单创建页面（用户端）✅
5. [ ] 数据可视化图表
6. [ ] 配送员管理
7. [ ] 统计报表导出

---

## 启动方式

### 后端
```bash
cd 桌面/兴茂app/backend
mvn spring-boot:run
# 访问 http://localhost:8080
```

### 前端
```bash
cd 桌面/兴茂app/frontend
npm install
npm run dev
# 访问 http://localhost:3000
```

### 数据库
在MySQL Workbench/Navicat中执行 `database/xingmao_food.sql`

---

## 测试账号
- 管理员(admin): `admin / 123456` - 可访问完整管理后台
- 公司员工(employee): `employee1 / 123456` - 可访问订单管理、配送管理
- 客户(user): 注册新账号后登录，可访问下单平台

## 登录与权限说明
- 登录时选择身份：客户、公司员工、管理员
- 不同身份登录后跳转到不同页面：
  - **管理员** → 管理后台：首页、食材管理、订单管理、配送管理、用户管理、数据大屏
  - **公司员工** → 员工平台：首页、食材管理、订单管理、配送管理、数据大屏（无用户管理权限）
  - **客户** → 客户平台：我要下单、我的订单

---

## API响应格式
所有接口返回格式：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {...}
}
```

前端request.js响应拦截器返回 `response.data`，所以调用时用 `result.data` 获取实际数据
