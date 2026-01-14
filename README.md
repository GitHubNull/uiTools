# uiTools - 多功能开发工具集

一个功能强大的基于Java Swing的图形界面工具，集成了多种开发者常用功能，包括字符串格式化、编解码、哈希计算、时间戳转换、二维码生成与解析、自动化输入等。

**版本**: v1.6.0

## ✨ 核心特性

### 🔐 JWT工具 (v1.6.0+)
- **JWT编码**: 生成JWT token，支持多种签名算法（HS256/HS384/HS512, RS256/RS384/RS512, ES256/ES384/ES512）
- **JWT解码**: 解析JWT token，显示Header、Payload和签名部分
- **算法支持**: HMAC对称加密、RSA非对称加密、ECDSA非对称加密
- **简洁输出**: 输出区域只显示核心结果，辅助信息显示在日志区域

### 📊 格式化与数据提取
- **JSON格式化**: 将压缩的JSON字符串格式化，支持JSONPath表达式精准提取数据
- **XML格式化**: 将压缩的XML字符串格式化，支持XPath表达式精准提取数据
- **多表达式批量处理**: 支持输入多个表达式，批量提取数据
- **行分隔输出**: 提取结果以行分隔格式输出，便于批量复制使用

### 🔐 编解码操作
- **URL编码/解码**: 处理URL特殊字符编码
- **Base64/Base32编码/解码**: 支持Base64和Base32编解码
- **Unicode编码/解码**: Unicode字符编码转换
- **Hex编码/解码**: 十六进制字节编码转换

### 🔐 哈希操作
- **MD5哈希**: 生成MD5散列值
- **SHA1哈希**: 生成SHA1散列值
- **SHA256哈希**: 生成SHA256散列值

### ⏰ 时间戳工具 (v1.6.0+)
- **获取当前时间戳**: 支持毫秒级和秒级时间戳
- **时区支持**: 支持系统时区、UTC及其他时区选择
- **日期时间互转**: 在日期字符串和时间戳之间转换
- **时间戳格式化**: 自定义时间戳格式化输出
- **UTC时间转换**: 本地时间与UTC时间互转
- **简洁输出**: 输出区域只显示核心转换结果，辅助信息显示在日志区域

### 📱 二维码工具 (v1.3.0+)
- **二维码生成**: 将文本生成为二维码图片
- **二维码解析**: 从二维码图片中解析文本内容
- **图片输入支持**: 支持从文件或剪贴板读取二维码图片

### 🚀 自动化输入 (v1.2.0+)
- **键盘模拟输入**: 模拟键盘输入文本
- **延迟控制**: 支持设置输入延迟时间
- **字符间隔**: 可设置字符输入间隔
- **多输入源**: 支持从输入框文本或剪贴板获取输入内容

### 🎨 用户界面特性
- **模块化UI架构**: refactored代码结构，业务逻辑与UI分离
- **状态感知**: 根据选择的操作自动显示/隐藏对应的输入区域
- **智能表达式输入**: 仅对支持表达式的操作显示表达式输入框
- **图片输入支持**: 对图片相关操作显示图片选择或预览区域
- **自动化配置面板**: 为自动化操作提供配置界面
- **丰富的快捷键**: 支持执行、复制、粘贴、清空等快捷操作
- **完整的剪贴板支持**: 支持从剪贴板读取和写入内容
- **输入输出交换**: 一键交换输入输出内容
- **自动换行**: 支持自动换行显示
- **现代化主题**: FlatLaf界面主题，提供简洁现代的外观
- **语法高亮**: RSyntaxTextArea提供代码编辑增强体验

## 🛠️ 技术栈

- **Java 11+** (向下兼容Java 11，使用Maven编译器release模式)
- **Maven** (项目构建和依赖管理)
- **Swing** (图形用户界面框架)
- **Gson 2.10.1** (JSON处理和解析)
- **dom4j 2.1.4** (XML文档处理)
- **Jayway JsonPath 2.8.0** (JSONPath表达式引擎)
- **Jaxen 1.2.0** (XPath表达式引擎，dom4j依赖)
- **Apache Commons Codec 1.16.0** (Base64/Base32等编解码实现)
- **JJWT 0.12.3** (JWT生成与解析库)
- **FlatLaf 3.2.5** (现代化Swing界面主题库)
- **RSyntaxTextArea 3.3.3** (增强型文本编辑器，支持语法高亮)
- **Google ZXing 3.5.2** (二维码生成与解析库core+javase)
- **Maven Shade Plugin 3.5.0** (创建可执行Uber JAR)
- **GitHub Actions** (自动化构建和发布CI/CD)

## 🚀 快速开始

### 环境要求
- **JDK 11** 或更高版本
- **Maven 3.6** 或更高版本

### 构建项目
```bash
git clone https://github.com/GitHubNull/uiTools.git
cd uiTools
mvn clean package
```

### 运行项目

**方式一：使用Maven运行**
```bash
mvn exec:java -Dexec.mainClass="org.oxff.Main"
```

**方式二：运行打包后的JAR文件**
```bash
java -jar target/uiTools-1.6.0.jar
```

## 📖 使用指南

### 基础操作流程
1. 启动应用程序
2. 在左侧选择操作分类
3. 从下拉列表中选择具体操作
4. 在输入区域输入或粘贴需要处理的内容（文本、日期、时间戳等）
5. 根据操作类型，可能需要：
   - 输入XPath/JSONPath表达式（支持批量，每行一个）
   - 选择时区（时间戳操作）
   - 配置自动化输入参数（延迟、间隔等）
   - 选择或粘贴图片（二维码相关操作）
6. 点击"执行"按钮或使用快捷键Ctrl+E
7. 查看输出结果（文本或图片预览）

### 🎯 XPath/JSONPath表达式使用

#### XPath表达式示例 (XML数据提取)

**示例XML数据**:
```xml
<bookstore>
    <book category="fiction">
        <title lang="en">Great Gatsby</title>
        <author>F. Scott Fitzgerald</author>
        <year>1925</year>
        <price>10.99</price>
    </book>
    <book category="children">
        <title lang="en">Harry Potter</title>
        <author>J.K. Rowling</author>
        <year>2000</year>
        <price>15.99</price>
    </book>
</bookstore>
```

**XPath表达式** (每行一个):
```
//book/title                           # 提取所有书名
//book/author                          # 提取所有作者
//book[@category='fiction']/price      # 提取小说类书籍的价格
//book[@lang='en']/title               # 提取英文书籍的书名
//book[year>2000]/title                # 提取2000年后出版的书籍
```

#### JSONPath表达式示例 (JSON数据提取)

**示例JSON数据**:
```json
{
  "store": {
    "book": [
      {
        "category": "fiction",
        "title": "Great Gatsby",
        "author": "F. Scott Fitzgerald",
        "year": 1925,
        "price": 10.99
      },
      {
        "category": "children",
        "title": "Harry Potter",
        "author": "J.K. Rowling",
        "year": 2000,
        "price": 15.99
      }
    ]
  }
}
```

**JSONPath表达式** (每行一个):
```
$.store.book[*].title                   # 提取所有书名
$.store.book[*].author                  # 提取所有作者
$.store.book[?(@.category=='fiction')].price    # 提取小说类书籍的价格
$.store.book[?(@.year>2000)].title      # 提取2000年后出版的书籍
$.store.book.length()                   # 计算书籍总数
```

### 🔐 JWT工具使用

#### JWT编码（生成Token）
1. 输入JSON格式的配置，包含：
   - `payload`: JWT claims数据（必填）
   - `key`: 签名密钥，Base64编码（必填）
   - `algorithm`: 签名算法，默认HS256（可选）
   - `typ`: token类型，如"JWT"（可选）

**示例输入**:
```json
{
  "payload": {
    "sub": "1234567890",
    "name": "John Doe",
    "iat": 1516239022
  },
  "key": "lDzT89LrYQuHlSypnjMj4tstGRHDuT/wrLndm3tIlds=",
  "algorithm": "HS256",
  "typ": "JWT"
}
```

**输出**: 生成的JWT token字符串

**支持的算法**:
- HMAC对称加密: HS256, HS384, HS512
- RSA非对称加密: RS256, RS384, RS512 (需要PKCS#8格式私钥)
- ECDSA非对称加密: ES256, ES384, ES512 (需要PKCS#8格式私钥)

#### JWT解码（解析Token）
1. 输入JWT token字符串
2. （可选）在token后添加空格和验证密钥进行签名验证

**示例输入**:
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

**输出**:
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
---
{
  "sub": "1234567890",
  "name": "John Doe",
  "iat": 1516239022
}
---
SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

**输出说明**: 分别显示Header、Payload和签名三部分，用`---`分隔

### ⏰ 时间戳工具使用

#### 获取当前时间戳
无需输入内容，直接选择时区即可获取：
- 支持的时区：本地时区、UTC、Asia/Shanghai、America/New_York 等
- 输出包含：毫秒级时间戳、秒级时间戳、格式化日期时间

#### 日期与时间戳互转
- **日期转时间戳**：输入格式如 `2024-01-14 15:30:00`，选择时区
- **时间戳转日期**：输入时间戳（10位或13位），自动转换为日期

#### 时间戳格式化
自定义时间戳格式化输出：
- 输入：时间戳值（10位秒级或13位毫秒级）
- 输出：根据选择或输入的格式字符串格式化显示

### 📱 二维码工具使用

#### 生成二维码
1. 输入要生成二维码的文本内容
2. 点击"执行"按钮
3. 输出区域将显示二维码图片预览
4. 右键点击图片可复制或保存

#### 解析二维码
1. 选择"解析二维码"操作
2. 选择图片输入方式：
   - **从文件读取**：点击"浏览"按钮选择二维码图片文件
   - **从剪贴板粘贴**：直接粘贴剪贴板中的图片
3. 点击"执行"按钮
4. 查看解析出的文本内容

**支持的图片格式**：PNG、JPG、JPEG、GIF、BMP

### 🚀 自动化输入使用

#### 键盘模拟输入
1. 输入要自动输入的文本内容（或选择使用剪贴板）
2. 配置输入参数：
   - **延迟时间**：执行后等待多少秒开始输入（默认3秒）
   - **字符间隔**：每个字符之间的输入间隔，单位毫秒（默认100ms）
   - **输入来源**：从输入框文本获取还是从剪贴板获取
3. 切换到目标窗口（如文本编辑器、浏览器等）
4. 点击"执行"按钮
5. 等待延迟时间后，文本将自动输入到目标窗口

**提示**：执行后请立即切换到目标窗口，光标将自动输入到当前焦点位置

### ⌨️ 快捷键支持
- **Ctrl+E**: 执行操作
- **Ctrl+Shift+E**: 执行并自动将输出复制到剪贴板
- **Ctrl+C**: 复制输入内容
- **Ctrl+Shift+C**: 复制输出内容
- **Ctrl+V**: 粘贴到输入框
- **Ctrl+Shift+V**: 从剪贴板粘贴到输出框
- **Ctrl+Shift+X**: 清空输入内容
- **Ctrl+A**: 全选 (在输入或输出框中)
- **Ctrl+Z**: 撤销操作
- **Ctrl+Y**: 重做操作
- **Tab**: 在输入框和表达式框之间切换
- **F1**: 显示关于对话框

## 🔧 开发指南

### 项目结构
```
src/main/java/org/oxff/
├── Main.java              # 程序入口点，负责初始化和启动UI
├── core/                  # 核心功能模块
│   ├── OperationFactory.java      # 操作工厂，管理所有操作实例
│   └── OperationCategory.java     # 操作分类枚举
├── operation/             # 操作实现模块（按功能分组的二级包结构）
│   ├── Operation.java            # 操作接口（根包）
│   ├── encoding/                 # 编解码操作（12个）
│   │   ├── url/                 # URL编解码
│   │   ├── base64/              # Base64编解码
│   │   ├── base32/              # Base32编解码
│   │   ├── unicode/             # Unicode编解码
│   │   ├── hex/                 # Hex编解码
│   │   ├── jwt/                 # JWT编解码
│   │   └── image/               # 图片转Base编码
│   ├── formatting/               # 格式化操作（2个）
│   │   ├── JsonFormatOperation  # JSON格式化
│   │   └── XmlFormatOperation   # XML格式化
│   ├── hashing/                  # 哈希操作（3个）
│   │   ├── Md5HashOperation     # MD5哈希
│   │   ├── Sha1HashOperation    # SHA1哈希
│   │   └── Sha256HashOperation  # SHA256哈希
│   ├── timestamp/                # 时间戳操作（6个）
│   │   ├── conversion/          # 时间戳转换（3个）
│   │   ├── format/              # 时间戳格式化（1个）
│   │   └── utc/                 # UTC时间转换（2个）
│   ├── qrcode/                   # 二维码操作（2个）
│   │   ├── QRCodeGenerateOperation  # 生成二维码
│   │   └── QRCodeDecodeOperation   # 解析二维码
│   ├── automation/               # 自动化操作（1个）
│   │   └── AutoInputOperation   # 键盘模拟输入
│   └── generator/                # 生成工具（1个）
│       └── RandomPasswordOperation  # 生成随机密码
└── ui/                    # 用户界面模块（refactored架构）
    ├── StringFormatterUI.java       # 主UI控制器
    ├── components/          # UI组件注册和构建
    │   ├── ComponentRegistry.java
    │   └── ComponentBuilder.java
    ├── controller/          # 业务逻辑控制器
    │   ├── OperationValidator.java
    │   ├── OperationExecutor.java
    │   └── UIStateManager.java
    ├── handler/             # 事件处理模块
    │   ├── ClipboardHandler.java
    │   └── KeyboardShortcutHandler.java
    ├── image/               # 图片处理管理器
    │   ├── ImageDisplayManager.java
    │   └── QRCodeHandler.java
    └── util/                # UI工具类
        ├── LogManager.java
        └── *Manager.java
```

### 添加新操作

1. **创建操作类**: 实现`Operation`接口
   ```java
   public class YourOperation implements Operation {
       @Override
       public String execute(String input) {
           // 实现你的逻辑
           return processedResult;
       }

       @Override
       public OperationCategory getCategory() {
           return OperationCategory.YOUR_CATEGORY; // 选择已有分类或添加新分类
       }

       @Override
       public String getDisplayName() {
           return "你的操作名称";
       }
   }
   ```

2. **支持表达式功能 (可选)**: 如果你的操作需要表达式参数，添加重载的`execute`方法
   ```java
   public String execute(String input, String expressions) {
       // 处理表达式输入（支持多行批量）
       return extractedResults;
   }
   ```

3. **支持图片操作 (可选)**: 如果你的操作处理图片，需要实现以下方法
   ```java
   @Override
   public boolean returnsImage() {
       return true;  // 返回true表示该操作输出图片
   }

   @Override
   public String getImageData(String input) {
       return execute(input);  // 返回Base64编码的图片数据
   }
   ```

4. **注册操作**: 在`OperationFactory.java`的静态块中添加新操作实例到`allOperations`数组
   ```java
   static {
       Operation[] allOperations = {
           // 现有操作...
           new YourOperation(),  // 添加你的新操作
       };
   }
   ```

5. **UI自动适配**: 重新编译后，操作会自动出现在UI中，无需额外配置：
   - 操作会自动分类显示
   - 表达式支持的操作会自动显示表达式输入框
   - 图片操作会自动显示图片预览区域
   - 自动化操作会自动显示配置面板

### 添加新操作分类（如果需要）

1. 在`OperationCategory.java`中添加新分类：
   ```java
   public enum OperationCategory {
       // 现有分类...
       YOUR_NEW_CATEGORY("你的分类名称"),
   }
   ```

2. 在操作类中使用新分类
3. UI会自动识别并显示新分类

### 构建和打包
```bash
# 开发构建
mvn clean compile

# 完整构建 (包含测试)
mvn clean package

# 跳过测试构建
mvn clean package -DskipTests

# 跳过测试并跳过测试编译
mvn clean package -DskipTests -Dmaven.test.skip=true
```

**构建产物**：
- `target/uiTools-1.5.0.jar` - 可执行JAR文件（包含所有依赖）
- `target/original-uiTools-1.5.0.jar` - 原始JAR文件（不包含依赖）

### 跨平台支持
项目使用GitHub Actions自动化构建，为以下平台生成原生安装包：
- **Linux**: Debian包 (.deb)
- **macOS**: DMG安装包 (.dmg)
- **Windows**: NSIS安装程序 (.exe)

触发方式：推送`v*.*.*`格式的Git tag即可触发自动化构建和发布

## 🐛 故障排除

### 常见问题

**Q: XPath表达式返回空结果**
A: 检查XML格式是否正确，确认XPath语法无误，注意命名空间问题。确保XML有正确的根元素和闭合标签。

**Q: JSONPath表达式报错**
A: 确保JSON格式有效，检查JSONPath语法是否符合Jayway规范。可使用在线JSONPath测试工具验证表达式。

**Q: 程序无法启动**
A: 确认Java版本为11或更高（运行`java -version`检查），检查JAR文件是否完整下载。尝试重新构建：`mvn clean package`

**Q: 表达式输入区域不可见**
A: 只有JSON格式化和XML格式化操作支持表达式。选择这两类操作后，表达式输入区域会自动显示。

**Q: 时间戳转换结果不正确**
A: 检查输入的时间戳格式（10位秒级或13位毫秒级），确认时区设置正确。输入日期时注意格式：`yyyy-MM-dd HH:mm:ss`

**Q: 二维码生成失败**
A: 确保输入内容不为空，ZXing库支持UTF-8编码。生成的二维码尺寸固定为300x300像素。

**Q: 二维码解析失败**
A: 确保图片格式受支持（PNG/JPG/JPEG/GIF/BMP），图片中包含清晰可识别的二维码。从剪贴板粘贴图片时确保剪贴板中有图片数据。

**Q: 自动化输入不工作**
A: 确保有足够的权限控制键盘（某些操作系统可能需要），延迟时间内切换到目标窗口，检查是否有其他程序拦截键盘输入。

**Q: 自动化输入出现乱码**
A: 当前实现使用Ctrl+V粘贴方式输入字符，确保目标应用支持粘贴操作。特殊字符（如回车、Tab）会直接发送按键事件。

**Q: 构建时出现依赖下载失败**
A: 检查网络连接，尝试配置Maven镜像。可以删除`~/.m2/repository`目录后重新构建。

**Q: GitHub Actions构建失败**
A: 检查workflow文件配置，确认secrets设置正确(GITHUB_TOKEN)。查看Actions日志获取详细错误信息。

## 📋 更新日志

### v1.6.0 (2026-01-14)
- ✨ **新增**: JWT工具集（JWT编码/JWT解码）
- ✨ **新增**: 支持多种JWT签名算法（HS256/HS384/HS512, RS256/RS384/RS512, ES256/ES384/ES512）
- ✨ **新增**: 支持自定义JWT header字段（如typ）
- 🎨 **优化**: 简化JWT和时间戳操作的输出格式，输出区域只显示核心结果
- 🎨 **增强**: 辅助信息和提示移至日志区域显示
- 🎨 **修复**: 修复JWT解码输出中换行符显示问题
- 📦 **依赖**: 添加JJWT库（JWT处理）

### v1.5.0 (2026-01-14)
- ✨ **新增**: 时间戳工具集（6个时间戳相关操作）
- ✨ **新增**: 支持多时区选择和UTC时间转换
- 🎨 **UI重构**: 模块化架构，分离业务逻辑与UI层
- 🎨 **状态管理**: 智能UI状态管理，根据操作类型显示/隐藏对应输入区域
- 🔧 **开发体验**: 改进操作注册机制，简化新操作添加流程
- 📦 **依赖**: 优化Maven依赖管理
- 🐛 **修复**: 修复已知bug和稳定性问题

### v1.4.0 (2026-01-13)
- ✨ **新增**: 时间戳工具功能（获取当前时间戳、日期时间转换）
- ✨ **新增**: 支持时区选择和UTC时间转换
- 🎨 **增强**: 改进表达式多行处理和错误提示
- 🔧 **优化**: 代码重构和性能优化

### v1.3.0 (2026-01-13)
- ✨ **新增**: 二维码生成功能
- ✨ **新增**: 二维码解析功能（支持文件和剪贴板）
- 🎨 **增强**: 新增图片输入和预览组件
- 📦 **依赖**: 添加ZXing库（二维码处理）
- 🔧 **改进**: 图片组件与操作集成

### v1.2.0 (2025-11-04)
- ✨ **新增**: 自动化键盘输入功能
- ✨ **新增**: 支持配置延迟时间和字符间隔
- 🎨 **增强**: 新增自动化配置界面
- 🔧 **改进**: 自动化输入的线程管理和错误处理

### v1.1.4 (2025-11-03)
- ✨ **新增**: XPath表达式支持 (XML数据精准提取)
- ✨ **新增**: JSONPath表达式支持 (JSON数据精准提取)
- 🎨 **增强**: 新增表达式输入UI区域
- 📦 **依赖**: 添加json-path 2.8.0和jaxen 1.2.0
- 🔧 **兼容**: 调整Java版本要求至11+
- 🚀 **性能**: 优化表达式处理性能

### v1.1.3
- ✨ **初始版本**: 基础字符串格式化和编解码功能
- 支持JSON格式化
- 支持XML格式化
- 支持URL/Base64/Base32/Unicode/Hex编解码
- 支持MD5/SHA1/SHA256哈希
- 基于Swing的图形界面

## 📄 许可证

本项目使用MIT许可证，详情请见[LICENSE](LICENSE)文件。

## 🤝 贡献

欢迎提交Issue和Pull Request来帮助改进项目！

### 贡献指南
1. Fork本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启Pull Request

### 开发建议
- 保持代码风格一致
- 为新功能添加适当的注释
- 更新README文档（如果需要）
- 确保代码通过编译

## 📞 联系方式

如有问题或建议，请通过以下方式联系我们：

- **GitHub Issues**: [创建Issue](https://github.com/GitHubNull/uiTools/issues)
- **GitHub Discussions**: [参与讨论](https://github.com/GitHubNull/uiTools/discussions)

## ⭐ 项目统计

[![GitHub stars](https://img.shields.io/github/stars/GitHubNull/uiTools?style=social)](https://github.com/GitHubNull/uiTools/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/GitHubNull/uiTools?style=social)](https://github.com/GitHubNull/uiTools/network/members)

## 🙏 致谢

- [FlatLaf](https://www.formdev.com/flatlaf/) - 现代化的Swing界面主题
- [RSyntaxTextArea](https://github.com/bobbylight/RSyntaxTextArea) - 语法高亮文本编辑器
- [ZXing](https://github.com/zxing/zxing) - 二维码处理库
- [Gson](https://github.com/google/gson) - JSON处理库
- [Jayway JsonPath](https://github.com/json-path/JsonPath) - JSONPath表达式库
- [dom4j](https://github.com/dom4j/dom4j) - XML处理库

## 📱 相关项目

如果你对这个项目感兴趣，可能也会喜欢：
- [其他工具项目](https://github.com/GitHubNull?tab=repositories)

---

**最后更新时间**: 2026-01-14
**当前版本**: v1.6.0
**Java版本**: 11+
**许可证**: MIT