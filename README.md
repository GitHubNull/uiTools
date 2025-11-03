# uiTools - 字符串格式化、编解码和数据提取工具

一个功能强大的基于Java Swing的图形界面工具，用于处理各种字符串格式化、编解码操作，以及XML/JSON数据的XPath和JSONPath表达式提取。

**版本**: v1.1.4

## ✨ 核心特性

### 📊 格式化操作 (增强版)
- **JSON格式化 + JSONPath提取**: 将压缩的JSON字符串格式化，并支持使用JSONPath表达式精准提取数据
- **XML格式化 + XPath提取**: 将压缩的XML字符串格式化，并支持使用XPath表达式精准提取数据

### 🔐 编解码操作
- URL编码/解码
- Base64/Base32编码/解码
- Unicode编码/解码
- Hex编码/解码

### 🔐 哈希操作
- MD5哈希
- SHA1哈希
- SHA256哈希

### 🚀 新增特性 (v1.1.4)
- **XPath表达式支持**: 支持标准XPath 1.0语法，可提取XML文档中的任意数据
- **JSONPath表达式支持**: 支持Jayway JSONPath语法，可提取JSON文档中的任意数据
- **多表达式批量处理**: 支持一次输入多个表达式，每行一个，批量提取数据
- **智能表达式识别**: 自动识别操作类型并应用相应的表达式引擎
- **行分隔输出**: 提取结果以行分隔格式输出，便于批量复制使用

### 🎨 用户界面特性
- 直观的三面板布局设计 (输入 → 表达式 + 输出)
- 支持操作分类浏览
- 丰富的快捷键操作
- 完整的剪贴板操作支持
- 输入输出内容交换功能
- 自动换行支持
- 现代化FlatLaf界面主题
- 语法高亮的代码编辑器

## 🛠️ 技术栈

- **Java 11+** (向下兼容Java 11)
- **Maven** (项目构建和依赖管理)
- **Swing** (图形用户界面)
- **Gson 2.10.1** (JSON处理)
- **dom4j 2.1.4** (XML处理)
- **Jayway JsonPath 2.8.0** (JSONPath表达式处理)
- **Jaxen 1.2.0** (XPath表达式处理)
- **Apache Commons Codec 1.16.0** (编解码操作)
- **FlatLaf 3.2.5** (现代化界面主题)
- **RSyntaxTextArea 3.3.3** (语法高亮编辑器)

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
java -jar target/uiTools-1.1.4.jar
```

## 📖 使用指南

### 基础操作流程
1. 启动应用程序
2. 在左侧选择操作分类和具体操作
3. 在输入框中粘贴或输入需要处理的文本
4. **(可选)** 在表达式输入框中输入XPath/JSONPath表达式
5. 点击"执行"按钮或使用快捷键Ctrl+E
6. 查看输出结果

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

### ⌨️ 快捷键支持
- **Ctrl+E**: 执行操作
- **Ctrl+C**: 复制输入内容
- **Ctrl+V**: 粘贴到输入框
- **Ctrl+Shift+C**: 复制输出内容
- **Ctrl+Shift+X**: 清空输入内容
- **Ctrl+A**: 全选 (在输入或输出框中)

## 🔧 开发指南

### 项目结构
```
src/main/java/org/oxff/
├── core/              # 核心类（操作工厂、操作分类）
├── operation/         # 各种操作的具体实现
├── ui/                # 用户界面组件
└── Main.java          # 程序入口点
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
           return OperationCategory.YOUR_CATEGORY;
       }

       @Override
       public String getDisplayName() {
           return "你的操作名称";
       }
   }
   ```

2. **支持表达式功能 (可选)**: 添加重载的`execute`方法
   ```java
   public String execute(String input, String expressions) {
       // 处理表达式输入
       return extractedResults;
   }
   ```

3. **注册操作**: 在`OperationFactory`中添加新操作到`allOperations`数组

### 构建和打包
```bash
# 开发构建
mvn clean compile

# 完整构建 (包含测试)
mvn clean package

# 跳过测试构建
mvn clean package -DskipTests
```

## 🐛 故障排除

### 常见问题

**Q: XPath表达式返回空结果**
A: 检查XML格式是否正确，确认XPath语法无误，注意命名空间问题

**Q: JSONPath表达式报错**
A: 确保JSON格式有效，检查JSONPath语法是否符合规范

**Q: 程序无法启动**
A: 确认Java版本为11或更高，检查JAR文件是否完整

**Q: 表达式输入区域不可见**
A: 选择JSON或XML格式化操作后，表达式输入区域会自动显示

## 📋 更新日志

### v1.1.4 (2024-12-XX)
- ✨ **新增**: XPath表达式支持 (XML数据精准提取)
- ✨ **新增**: JSONPath表达式支持 (JSON数据精准提取)
- 🎨 **增强**: 新增表达式输入UI区域
- 📦 **依赖**: 添加json-path 2.8.0和jaxen 1.2.0
- 🔧 **兼容**: 调整Java版本要求至11+
- 🚀 **性能**: 优化表达式处理性能
- 🐛 **修复**: 修复已知问题

### 早期版本
- v1.1.3: 基础格式化和编解码功能
- v1.0.x: 初始版本发布

## 📄 许可证

本项目使用MIT许可证，详情请见[LICENSE](LICENSE)文件。

## 🤝 贡献

欢迎提交Issue和Pull Request来帮助改进项目！

## 📞 联系方式

如有问题或建议，请通过GitHub Issues联系我们。