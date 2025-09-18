# uiTools - 字符串格式化和编解码工具

一个基于Java Swing的图形界面工具，用于处理各种字符串格式化和编解码操作。

## 功能特性

### 格式化操作
- JSON格式化：将压缩的JSON字符串格式化为易读的格式
- XML格式化：将压缩的XML字符串格式化为易读的格式

### 编解码操作
- URL编码/解码
- Base64编码/解码
- Base32编码/解码
- Unicode编码/解码
- Hex编码/解码

### 哈希操作
- MD5哈希
- SHA1哈希
- SHA256哈希

### 其他特性
- 直观的图形用户界面
- 支持操作分类浏览
- 支持快捷键操作
- 支持粘贴板操作
- 支持输入输出内容交换
- 支持自动换行
- 使用FlatLaf美化界面

## 技术栈

- Java 17
- Maven
- Swing (图形界面)
- Gson (JSON处理)
- dom4j (XML处理)
- Apache Commons Codec (编解码)
- FlatLaf (界面美化)
- RSyntaxTextArea (代码编辑器组件)

## 构建和运行

### 环境要求
- JDK 17 或更高版本
- Maven 3.6 或更高版本

### 构建项目
```bash
mvn clean package
```

### 运行项目
```bash
mvn exec:java -Dexec.mainClass="org.oxff.Main"
```

或者运行打包后的JAR文件：
```bash
java -jar target/uiTools-1.0-SNAPSHOT.jar
```

## 使用说明

1. 启动应用程序后，在左侧选择需要执行的操作分类，然后选择具体操作
2. 在输入框中粘贴或输入需要处理的文本
3. 点击"执行"按钮或使用快捷键Ctrl+E执行操作
4. 处理结果将显示在右侧输出框中
5. 可以使用复制、粘贴、清空等按钮进行相关操作
6. 支持使用快捷键：
   - Ctrl+E: 执行操作
   - Ctrl+C: 复制输入
   - Ctrl+V: 粘贴输入
   - Ctrl+Shift+C: 复制输出
   - Ctrl+A: 全选（在输入或输出框中）

## 开发

### 项目结构
```
src/main/java/org/oxff
├── core/              # 核心类（操作工厂、操作分类）
├── operation/         # 各种操作的具体实现
├── ui/                # 用户界面
└── Main.java          # 程序入口
```

### 添加新操作

1. 创建新的操作类，实现[Operation](file:///E:/devs/java-devs/IdeaProjects/uiTools/src/main/java/org/oxff/operation/Operation.java#L10-L12)接口
2. 在[OperationFactory](file:///E:/devs/java-devs/IdeaProjects/uiTools/src/main/java/org/oxff/core/OperationFactory.java#L15-L64)中注册新操作
3. 新操作会自动出现在界面中

## 许可证

本项目使用MIT许可证，详情请见[LICENSE](file:///E:/devs/java-devs/IdeaProjects/uiTools/LICENSE)文件。