# GPU_Renting
该项目用于完成课程设计
# GPU租赁管理系统

## 📋 项目概述
基于JavaSE + MySQL的桌面GPU租赁管理系统，提供管理员和用户双端GUI界面。

## 🔧 技术栈
- JavaSE (Swing)
- MySQL + 原生JDBC
- Git版本控制

## 📊 功能模块

| 模块 | 功能描述 |
|------|----------|
| **用户管理** | 管理员/租户角色区分，不同角色显示不同界面 |
| **资源管理** | GPU服务器编号、显卡型号(A100/H100)、显存、状态(空闲/占用/维护) |
| **租赁申请** | 租户提交申请(显卡型号、数量、时长)，管理员审批 |
| **使用记录** | 租赁开始/结束时间、实际使用时长记录 |
| **计费规则** | 按显卡型号设置单价(时/天/月)，支持折扣 |
| **账单管理** | 自动生成账单、账单状态(待支付/已支付/逾期) |
| **资源监控** | GPU利用率、温度等模拟监控数据 |
| **统计报表** | 按GPU型号/租户/时间段统计收入与利用率 |

## 💾 数据库表设计

### 8张核心表
- **Users** - 用户表(管理员/租户)
- **GPUServers** - GPU服务器表  
- **GPUCards** - 显卡表(关联服务器)
- **RentalApplications** - 租赁申请表(关联用户、显卡)
- **UsageRecords** - 使用记录表(关联申请)
- **PricingRules** - 计费规则表(按显卡型号)
- **Bills** - 账单表(关联申请、用户)
- **MonitoringData** - 监控数据表(关联显卡)

### 表关联关系
```
GPUServers → GPUCards → RentalApplications → UsageRecords
                  ↑              ↓
            MonitoringData       Bills → PricingRules
```

## 🏗️ 项目结构

```
gpu-rental-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com.gpurental/
│   │   │       ├── src.main.App.java                    # 入口类(选择管理员/用户端)
│   │   │       │
│   │   │       ├── gui/                         # 界面层
│   │   │       │   ├── admin/                   # 管理员端
│   │   │       │   │   ├── AdminMainFrame.java
│   │   │       │   │   ├── ResourcePanel.java   # 资源管理
│   │   │       │   │   ├── ApprovePanel.java    # 审批管理
│   │   │       │   │   ├── MonitorPanel.java    # 监控面板
│   │   │       │   │   └── ReportPanel.java     # 统计报表
│   │   │       │   └── src.main.gui.user/                     # 用户端
│   │   │       │       ├── UserMainFrame.java
│   │   │       │       ├── ApplyPanel.java      # 提交申请
│   │   │       │       ├── MyAppsPanel.java     # 我的申请
│   │   │       │       └── MyBillsPanel.java    # 我的账单
│   │   │       │
│   │   │       ├── service/                      # 业务层(直接供GUI调用)
│   │   │       │   ├── ResourceService.java     # 资源管理
│   │   │       │   ├── ApplicationService.java  # 申请审批
│   │   │       │   ├── BillService.java         # 账单(含事务)
│   │   │       │   ├── MonitorService.java      # 监控数据
│   │   │       │   └── ReportService.java       # 统计报表
│   │   │       │
│   │   │       ├── dao/                          # 数据访问层
│   │   │       │   ├── BaseDao.java
│   │   │       │   ├── UserDao.java
│   │   │       │   ├── GPUCardDao.java
│   │   │       │   ├── ApplicationDao.java
│   │   │       │   ├── BillDao.java
│   │   │       │   └── ...
│   │   │       │
│   │   │       ├── model/                        # 实体类
│   │   │       │   ├── User.java
│   │   │       │   ├── GPUCard.java (抽象类)
│   │   │       │   ├── A100Card.java (子类)
│   │   │       │   ├── H100Card.java (子类)
│   │   │       │   ├── Application.java
│   │   │       │   └── Bill.java
│   │   │       │
│   │   │       ├── strategy/                     # 策略模式(计费)
│   │   │       │   ├── BillingStrategy.java
│   │   │       │   ├── HourlyBilling.java
│   │   │       │   ├── DailyBilling.java
│   │   │       │   └── MonthlyBilling.java
│   │   │       │
│   │   │       └── util/                         # 工具类
│   │   │           ├── DBUtil.java               # 数据库连接
│   │   │           └── Constants.java            # 常量定义
│   │   │
│   │   └── resources/
│   │       └── db/
│   │           └── init.sql                       # 建表语句+测试数据
│   │
│   └── test/                                      # 测试代码
│
├── lib/                                            # 依赖JAR
│   └── mysql-connector-java-8.x.jar
│
├── .gitignore
└── README.md
```

## 💡 核心实现要点

### 面向对象设计
```java
// 抽象显卡类
abstract class GPUCard {
    private int cardId;
    private String model;
    private int memory;
    public abstract double getBasePrice();
}

// 具体子类
class A100Card extends GPUCard {
    public double getBasePrice() { return 2.5; }
}
class H100Card extends GPUCard {
    public double getBasePrice() { return 4.0; }
}
```

### 策略模式(计费)
```java
interface BillingStrategy {
    double calculate(double hours, double price);
}

class HourlyBilling implements BillingStrategy {
    public double calculate(double hours, double price) {
        return hours * price;
    }
}

class DailyBilling implements BillingStrategy {
    public double calculate(double hours, double price) {
        return Math.ceil(hours / 8) * price * 8 * 0.9; // 9折
    }
}
```

### 事务处理(账单生成)
```java
public boolean generateBill(int appId) {
    Connection conn = null;
    try {
        conn = DBUtil.getConnection();
        conn.setAutoCommit(false);
        
        // 1. 获取申请信息
        // 2. 计算费用(策略模式)
        // 3. 创建账单
        // 4. 更新使用记录
        
        conn.commit();
        return true;
    } catch (Exception e) {
        conn.rollback();
        return false;
    }
}
```

### 分页查询
```sql
SELECT * FROM GPUCards 
WHERE model LIKE ? AND status = ?
LIMIT ?, ?;
```

## 🚀 快速开始

### 1. 数据库初始化
```bash
mysql -u root -p < src/resources/db/init.sql
```

### 2. 配置连接
修改 `DBUtil.java` 中的数据库连接参数

### 3. 运行程序
运行 `src.main.App.java`，选择进入管理员端或用户端

### 4. 预置测试账号
```
管理员: admin / 123456
租户:   zhangsan / 123456
        lisi / 123456
```

## 📁 Git管理
```bash
git init
echo "*.class" >> .gitignore
echo "lib/" >> .gitignore
git add .
git commit -m "Initial commit"
```

## ✅ 满足的要求
- [x] 8张数据库表，多表关联
- [x] Swing GUI界面
- [x] 原生JDBC操作
- [x] 事务处理(账单生成)
- [x] 策略模式计费
- [x] 分页查询
- [x] 面向对象设计(显卡子类)
- [x] 预置测试数据
- [x] Git版本控制
