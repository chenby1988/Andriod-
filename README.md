# Calculator - Android 全功能计算器

一款基于 Jetpack Compose 开发的 Android 计算器应用，完整实现 Windows 10 计算器的全部功能模块，支持 18 种计算模式。

## 功能特性

| 分类 | 模式 | 说明 |
|------|------|------|
| **基础计算** | 标准 | 四则运算、记忆功能 (MC/MR/M+/M-/MS) |
| | 科学 | sin/cos/tan、log/ln、xʸ、π、括号、DEG/RAD 切换 |
| | 程序员 | HEX/DEC/OCT/BIN 进制、位运算、数据宽度切换 |
| **生活工具** | 日期计算 | 日期间隔、加减天数 |
| | 货币 | 14 种货币实时汇率转换（联网 + 离线 fallback） |
| **单位转换** | 体积 | 20 种体积单位互转 |
| | 长度 | 10 种长度单位互转 |
| | 重量 | 10 种重量单位互转 |
| | 温度 | 摄氏度/华氏度/开尔文（非线性转换） |
| | 能量 | 7 种能量单位互转 |
| | 面积 | 8 种面积单位互转 |
| | 速度 | 5 种速度单位互转 |
| | 时间 | 8 种时间单位互转 |
| | 功率 | 5 种功率单位互转 |
| | 数据 | 7 种数据存储单位互转（位 ~ 拍字节） |
| | 压力 | 6 种压力单位互转 |
| | 角度 | 度/弧度/梯度互转 |
| **其他** | 关于 | 应用信息、版本号、功能列表 |

## 技术栈

- **语言**: Kotlin
- **UI 框架**: Jetpack Compose (Material3)
- **架构**: 单 Activity + Compose 状态管理
- **网络**: Retrofit 2 + OkHttp + Gson（货币实时汇率）
- **构建工具**: Gradle 9.3.1
- **最低 SDK**: API 24 (Android 7.0)
- **目标 SDK**: API 36 (Android 16)

## 项目结构

```
app/src/main/java/com/example/helloworld/
├── MainActivity.kt          # 主界面（2100+ 行，含全部 18 种模式）
├── ui/theme/                # Compose 主题
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
└── data/                    # 汇率网络层
    ├── ExchangeRateApi.kt
    ├── ExchangeRateRepository.kt
    └── ExchangeRateResponse.kt
```

## 构建与运行

### 环境要求
- Android Studio (最新稳定版)
- JDK 17+
- Android SDK (API 24 ~ 36)

### 本地构建
```bash
# 克隆仓库
git clone https://github.com/chenby1988/Andriod-.git
cd Andriod-

# 构建 Debug APK
./gradlew assembleDebug

# APK 输出路径
app/build/outputs/apk/debug/app-debug.apk
```

### 安装到设备
```bash
# 通过 ADB 安装
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## 货币模块说明

货币转换支持**联网实时汇率**和**离线硬编码汇率**双保险：
- 应用启动时自动从 `api.exchangerate-api.com` 获取最新汇率
- 网络失败时自动回退到内置汇率（2026年4月基准）
- 支持 14 种常用货币：USD, CNY, EUR, GBP, JPY, KRW, HKD, AUD, CAD, CHF, SGD, INR, RUB, THB

## 关键设计

- **GenericConverterContent**: 通用单位转换器 Composable，复用于 10 种单位转换模块，大幅减少重复代码
- **温度特殊处理**: 通过自定义 `converter` lambda 实现 Celsius ↔ Fahrenheit ↔ Kelvin 的非线性转换
- **程序员模式**: 支持有符号整数运算、位宽截断（BYTE/WORD/DWORD/QWORD）、括号嵌套

## 许可证

MIT License
