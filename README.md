# 兴茂食材配送管理系统

> 兴茂食材配送管理平台 - Spring Boot + Vue.js 双端系统

## 项目简介

- **技术栈**：Spring Boot 2.7 + Vue.js 3 + MyBatis-Plus + MySQL
- **项目类型**：食材配送企业管理系统（公司管理端 + 客户下单端）
- **数据库**：MySQL 8.0，字符集 utf8mb4

## 功能模块

### 管理端（管理员/公司员工）
| 模块 | 功能 |
|------|------|
| 首页统计 | 食材数、订单数、用户数、收入统计 |
| 食材管理 | CRUD、分类筛选、排序、图片上传 |
| 订单管理 | 列表、详情、搜索、日期筛选、派单 |
| 配送管理 | 配送员管理、手动/自动分配配送员 |
| 用户管理 | 管理员/员工/客户 账号管理 |
| 数据大屏 | 实时订单、收入支出统计图表 |
| 交易流水 | 财务流水、收入/支出/净利润统计 |
| 订单评价 | 查看客户评价、回复评价 |

### 客户端（客户）
| 模块 | 功能 |
|------|------|
| 个人中心 | 订单统计、快捷入口、最近订单 |
| 我要下单 | 食材选择、购物车、地址选择 |
| 我的订单 | 订单列表、详情、确认收货 |
| 收货地址 | 地址管理（增删改、设默认） |
| 个人信息 | 头像、昵称、手机号修改 |
| 订单评价 | 星级评分、发表评价 |

## 三端角色

| 角色 | 说明 | 访问页面 |
|------|------|----------|
| 管理员 (admin) | 系统管理员 | 管理后台全部功能 |
| 公司员工 (employee) | 公司员工 | 首页、食材管理、订单管理、配送管理、数据大屏 |
| 客户 (user) | 注册用户 | 个人中心、我要下单、我的订单、收货地址、个人信息、订单评价 |

## 测试账号

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | 123456 | 管理员 |
| employee1 | 123456 | 公司员工 |
| test | 123456 | 客户 |

## 快速启动

### 环境要求
- JDK 1.8+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### 1. 数据库
在 MySQL 中创建数据库并执行初始化脚本：
```sql
CREATE DATABASE xingmao_food DEFAULT CHARACTER SET utf8mb4;
SOURCE database/xingmao_food.sql;
```

### 2. 后端
```bash
cd backend
# 修改 src/main/resources/application.yml 中的数据库密码

mvn spring-boot:run
# 访问 http://localhost:8080
```

### 3. 前端
```bash
cd frontend
npm install
npm run dev
# 访问 http://localhost:3000
```

## 技术架构

### 后端
- **框架**：Spring Boot 2.7.18
- **ORM**：MyBatis-Plus3.5
- **认证**：JWT (jjwt 0.11.5) + Spring Security
- **文档**：Swagger (Springdoc OpenAPI)
- **工具**：Lombok、Guava

### 前端
- **框架**：Vue.js 3 (Composition API)
- **UI**：Element Plus
- **状态**：Pinia
- **构建**：Vite
- **路由**：Vue Router4
- **请求**：Axios

## 数据库表结构

| 表名 | 说明 |
|------|------|
| sys_user | 用户表（管理员/员工/客户） |
| food | 食材表 |
| orders | 订单表 |
| order_item | 订单明细表 |
| delivery | 配送信息表 |
| delivery_person | 配送员表 |
| transaction | 交易流水表 |
| customer_address | 收货地址表 |
| order_review | 订单评价表 |

## API响应格式

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {...}
}
```

## 项目结构

```
兴茂app/
├── backend/
│   └── src/main/java/com/xingmao/
│       ├── config/          # 配置类
│       ├── controller/      # REST控制器
│       ├── entity/         # 实体类
│       ├── mapper/         # MyBatis Mapper
│       ├── service/        # 业务层
│       └── utils/          # 工具类
├── frontend/
│   └── src/
│       ├── api/            # API请求模块
│       ├── components/     # 公共组件
│       ├── router/         # 路由配置
│       ├── stores/         # Pinia状态管理
│       ├── views/          # 页面组件
│       └── App.vue
└── database/
    └── xingmao_food.sql    # 数据库初始化脚本
```

## License

MIT