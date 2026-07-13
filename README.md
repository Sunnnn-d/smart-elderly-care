# 智慧养老系统 (Smart Elderly Care System)

## 项目简介

智慧养老系统是一套面向社区养老服务的综合管理平台，包含面向老年人的前端展示网站和后台管理系统。系统采用前后端分离架构，提供老人档案管理、服务预约、健康监测、护理计划、公告资讯等核心功能。

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2.5
- Spring Security + JWT
- MyBatis Plus 3.5.6
- MySQL 8.0+

### 前端（面向老年人）
- Vue 3.4
- Element Plus 2.7
- Vite 5.2
- Pinia 状态管理
- Axios 网络请求

### 后台管理系统
- Vue 3.4
- Element Plus 2.7
- ECharts 5.5
- Vite 5.2

## 项目结构

```
smart-elderly-care/
├── database/                    # 数据库脚本
│   └── smart_elderly_care.sql   # MySQL建表及初始数据
├── backend/                     # Java后端项目
│   ├── pom.xml                  # Maven配置
│   └── src/main/
│       ├── java/com/smart/elderly/
│       │   ├── controller/      # 控制器
│       │   ├── service/         # 服务层
│       │   ├── mapper/          # 数据访问层
│       │   ├── entity/          # 实体类
│       │   ├── dto/             # 数据传输对象
│       │   ├── config/          # 配置类
│       │   └── common/          # 通用工具类
│       └── resources/
│           └── application.yml  # 应用配置
├── frontend/                    # 前端网站（面向老年人）
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── api/                 # 接口请求
│       ├── router/              # 路由配置
│       ├── views/               # 页面组件
│       └── styles/              # 样式文件
└── admin/                       # 后台管理系统
    ├── package.json
    ├── vite.config.js
    └── src/
        ├── api/                 # 接口请求
        ├── router/              # 路由配置
        ├── layout/              # 布局组件
        └── views/               # 页面组件
```

## 快速开始

### 1. 数据库配置

```bash
# 登录MySQL
mysql -u root -p

# 执行建表脚本
source database/smart_elderly_care.sql
```

### 2. 后端启动

```bash
cd backend

# 修改数据库配置（如需）
# 编辑 src/main/resources/application.yml
# 修改 spring.datasource.url/username/password

# Maven打包
mvn clean package -DskipTests

# 运行
java -jar target/elderly-care-1.0.0.jar
```

后端服务默认运行在 http://localhost:8088

### 3. 前端网站启动

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端网站默认运行在 http://localhost:5173

### 4. 后台管理系统启动

```bash
cd admin

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

后台管理系统默认运行在 http://localhost:5174

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 超级管理员 | admin | 123456 |
| 普通管理员 | manager | 123456 |
| 护理员 | nurse_zhang | 123456 |
| 护理员 | nurse_li | 123456 |

## 核心功能

### 前端网站（适老化设计）
- 首页轮播图展示
- 服务项目浏览与在线预约
- 健康资讯查看
- 机构介绍与联系方式

### 后台管理系统
- 首页数据概览（统计图表）
- 老人档案管理（CRUD）
- 服务项目管理（CRUD）
- 服务订单管理（派单、完成、取消）
- 健康监测记录管理
- 护理计划管理
- 公告资讯管理
- 轮播图管理
- 用户管理

## API接口文档

所有接口基础路径：`/api`

### 认证接口
- POST `/auth/login` - 登录
- GET `/auth/info` - 获取用户信息

### 公开接口（无需登录）
- GET `/banner/public/list` - 获取轮播图
- GET `/notice/public/list` - 获取公告列表
- GET `/notice/public/{id}` - 获取公告详情
- GET `/service-item/all` - 获取所有服务项目
- POST `/order/appointment` - 创建预约

### 管理接口（需登录）
- 老人档案：GET/POST/PUT/DELETE `/elderly/*`
- 服务项目：GET/POST/PUT/DELETE `/service-item/*`
- 服务订单：GET/PUT `/order/*`
- 健康监测：GET/POST `/health/*`
- 护理计划：GET/POST/PUT/DELETE `/care-plan/*`
- 公告管理：GET/POST/PUT/DELETE `/notice/*`
- 轮播图：GET/POST/PUT/DELETE `/banner/*`
- 用户管理：GET `/user/*`
