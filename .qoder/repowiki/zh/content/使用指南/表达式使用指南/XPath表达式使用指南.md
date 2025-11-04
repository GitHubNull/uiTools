# XPath表达式使用指南

<cite>
**Referenced Files in This Document**   
- [README.md](file://README.md)
- [XmlFormatOperation.java](file://src/main/java/org/oxff/operation/XmlFormatOperation.java)
- [StringFormatterUI.java](file://src/main/java/org/oxff/ui/StringFormatterUI.java)
- [JsonFormatOperation.java](file://src/main/java/org/oxff/operation/JsonFormatOperation.java)
- [OperationFactory.java](file://src/main/java/org/oxff/core/OperationFactory.java)
</cite>

## 目录
1. [XPath表达式语法与使用方法](#xpath表达式语法与使用方法)
2. [XML数据提取示例](#xml数据提取示例)
3. [Jaxen库实现XPath解析](#jaxen库实现xpath解析)
4. [反射调用机制](#反射调用机制)
5. [多表达式批量处理](#多表达式批量处理)
6. [表达式输入区域动态显示](#表达式输入区域动态显示)

## XPath表达式语法与使用方法

XPath（XML Path Language）是一种用于在XML文档中查找信息的语言。它通过路径表达式来选取XML文档中的节点或节点集。XPath支持多种表达式语法，包括绝对路径、相对路径、谓词过滤、通配符等。

在uiTools工具中，XPath表达式主要用于从XML文档中提取特定数据。用户可以在表达式输入区域输入XPath表达式，系统将根据表达式从输入的XML数据中提取匹配的节点值。

XPath表达式的基本语法包括：
- `/`：表示从根节点开始的绝对路径
- `//`：表示从任意位置开始的相对路径
- `@`：用于选取属性
- `[]`：用于谓词过滤
- `*`：通配符，匹配任意元素

## XML数据提取示例

根据README中的XML示例数据，以下是具体的XPath表达式编写示例：

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

**XPath表达式示例** (每行一个):
```
//book/title                           # 提取所有书名
//book/author                          # 提取所有作者
//book[@category='fiction']/price      # 提取小说类书籍的价格
//book[@lang='en']/title               # 提取英文书籍的书名
//book[year>2000]/title                # 提取2000年后出版的书籍
```

这些表达式展示了XPath的基本用法，包括节点选取、属性过滤和条件判断。在uiTools中，用户可以输入多个表达式，每行一个，系统将批量处理并返回结果。

**Section sources**
- [README.md](file://README.md#L130-L160)

## Jaxen库实现XPath解析

在uiTools中，XPath表达式的解析是通过Jaxen库实现的。Jaxen是一个开源的XPath引擎，支持在Java应用程序中执行XPath表达式。虽然项目依赖中包含了Jaxen库，但实际的XPath解析是通过dom4j库完成的，因为dom4j内部集成了Jaxen作为其XPath引擎。

在`XmlFormatOperation`类中，XPath解析的实现如下：

```java
// 使用dom4j进行XPath处理
Document document = DocumentHelper.parseText(input);

// 使用dom4j的XPath功能
List<?> nodes = document.selectNodes(expression);
for (Object node : nodes) {
    if (node instanceof Node) {
        String nodeValue = getDom4jNodeValue((Node) node);
        if (nodeValue != null && !nodeValue.isEmpty()) {
            results.add(nodeValue);
        }
    }
}

// 如果没有找到节点，检查单个节点值
if (nodes.isEmpty()) {
    Node node = document.selectSingleNode(expression);
    if (node != null) {
        String nodeValue = getDom4jNodeValue(node);
        if (nodeValue != null && !nodeValue.isEmpty()) {
            results.add(nodeValue);
        }
    }
}
```

当用户输入XML数据和XPath表达式后，系统首先使用dom4j的`DocumentHelper.parseText()`方法解析XML字符串为Document对象。然后，通过Document对象的`selectNodes()`方法执行XPath表达式，返回匹配的节点列表。对于每个匹配的节点，系统调用`getDom4jNodeValue()`方法获取节点的文本值。

`getDom4jNodeValue()`方法根据节点类型返回相应的值：
- 元素节点（ELEMENT_NODE）：返回元素的文本内容
- 属性节点（ATTRIBUTE_NODE）、文本节点（TEXT_NODE）、CDATA节点（CDATA_SECTION_NODE）：返回节点的字符串值
- 其他类型节点：返回节点的XML表示

**Section sources**
- [XmlFormatOperation.java](file://src/main/java/org/oxff/operation/XmlFormatOperation.java#L51-L105)
- [XmlFormatOperation.java](file://src/main/java/org/oxff/operation/XmlFormatOperation.java#L110-L125)

## 反射调用机制

在uiTools中，`executeOperation`方法通过反射机制调用带表达式参数的`execute`方法。这种设计模式允许系统在运行时动态调用特定的方法，而不需要在编译时确定方法签名。

`executeOperation`方法的实现逻辑如下：

```java
// 对于XML和JSON格式化操作，如果有表达式输入，使用特殊处理
if (!expressions.isEmpty() && operation.getClass().getSimpleName().equals("XmlFormatOperation")) {
    // 通过反射调用带有表达式参数的方法
    try {
        java.lang.reflect.Method method = operation.getClass().getMethod("execute", String.class, String.class);
        result = (String) method.invoke(operation, inputText, expressions);
    } catch (NoSuchMethodException e) {
        // 如果没有带表达式的方法，使用原始方法
        result = operation.execute(inputText);
    }
}
```

该机制的工作流程如下：
1. 首先检查用户是否输入了表达式，以及当前操作是否为XML或JSON格式化操作
2. 如果条件满足，则通过反射获取`execute`方法，该方法接受两个参数：输入字符串和表达式字符串
3. 使用`invoke()`方法调用该方法，传入输入文本和表达式
4. 如果反射调用失败（例如，该操作类没有带两个参数的`execute`方法），则回退到原始的`execute`方法

这种设计的优点是：
- 保持了操作接口的统一性，所有操作都实现了`Operation`接口
- 允许特定操作类（如`XmlFormatOperation`和`JsonFormatOperation`）提供额外的功能（如表达式支持）
- 提高了系统的灵活性和可扩展性，新增操作时不需要修改核心执行逻辑

**Section sources**
- [StringFormatterUI.java](file://src/main/java/org/oxff/ui/StringFormatterUI.java#L395-L451)

## 多表达式批量处理

uiTools支持多表达式批量处理功能，允许用户一次输入多个XPath或JSONPath表达式，每行一个，系统将批量处理并返回结果。

该功能的实现主要在`XmlFormatOperation.execute(String input, String expressions)`方法中：

```java
String[] expressionLines = expressions.split("\\r?\\n");
List<String> results = new ArrayList<>();

for (String expression : expressionLines) {
    expression = expression.trim();
    if (expression.isEmpty()) {
        continue;
    }

    try {
        // 使用dom4j的XPath功能
        List<?> nodes = document.selectNodes(expression);
        for (Object node : nodes) {
            if (node instanceof Node) {
                String nodeValue = getDom4jNodeValue((Node) node);
                if (nodeValue != null && !nodeValue.isEmpty()) {
                    results.add(nodeValue);
                }
            }
        }

        // 如果没有找到节点，检查单个节点值
        if (nodes.isEmpty()) {
            Node node = document.selectSingleNode(expression);
            if (node != null) {
                String nodeValue = getDom4jNodeValue(node);
                if (nodeValue != null && !nodeValue.isEmpty()) {
                    results.add(nodeValue);
                }
            }
        }
    } catch (Exception e) {
        results.add("XPath表达式错误 [" + expression + "]: " + e.getMessage());
    }
}

if (results.isEmpty()) {
    return "未找到匹配的节点";
}

return String.join("\n", results);
```

处理流程如下：
1. 将输入的表达式字符串按行分割，支持Windows（\r\n）和Unix（\n）换行符
2. 遍历每个表达式，去除首尾空白字符
3. 对每个有效表达式执行XPath查询
4. 将每个表达式的结果添加到结果列表中
5. 如果表达式执行出错，将错误信息作为结果添加
6. 最后，将所有结果用换行符连接成一个字符串返回

结果以行分隔输出，便于用户批量复制使用。如果没有任何表达式返回结果，系统将返回"未找到匹配的节点"的提示。

**Section sources**
- [XmlFormatOperation.java](file://src/main/java/org/oxff/operation/XmlFormatOperation.java#L51-L105)

## 表达式输入区域动态显示

在uiTools中，`expressionTextArea`组件的显示逻辑是动态的，即仅在选择XML或JSON格式化操作时才显示表达式输入区域。这一功能通过`requiresExpressionInput`和`updateExpressionPanelVisibility`方法实现。

`requiresExpressionInput`方法用于判断当前操作是否需要表达式输入：

```java
private boolean requiresExpressionInput(String operationName) {
    if (operationName == null || operationName.isEmpty()) {
        return false;
    }

    Operation operation = OperationFactory.getOperation(operationName);
    if (operation == null) {
        return false;
    }

    // 只有JSON和XML格式操作需要表达式输入
    String className = operation.getClass().getSimpleName();
    return "JsonFormatOperation".equals(className) || "XmlFormatOperation".equals(className);
}
```

该方法的工作原理：
1. 检查操作名称是否为空
2. 通过`OperationFactory.getOperation()`获取对应的操作实例
3. 获取操作类的简单名称
4. 判断是否为`JsonFormatOperation`或`XmlFormatOperation`

`updateExpressionPanelVisibility`方法根据`requiresExpressionInput`的返回值更新表达式面板的可见性：

```java
private void updateExpressionPanelVisibility() {
    boolean showExpressionPanel = requiresExpressionInput(selectedOperation);
    if (showExpressionPanel) {
        // 显示表达式面板
        outputExpressionSplitPane.setLeftComponent(expressionPanel);
    } else {
        // 隐藏表达式面板
        outputExpressionSplitPane.setLeftComponent(null);
    }
    outputExpressionSplitPane.revalidate();
    outputExpressionSplitPane.repaint();
}
```

该方法在以下情况下被调用：
- 当用户通过下拉框选择操作时
- 当用户通过操作树选择操作时

通过`setLeftComponent()`方法，系统动态地添加或移除表达式面板。调用`revalidate()`和`repaint()`方法确保界面及时更新。

这种设计优化了用户界面，避免在不需要表达式输入的操作中显示不必要的输入区域，提高了用户体验。

**Section sources**
- [StringFormatterUI.java](file://src/main/java/org/oxff/ui/StringFormatterUI.java#L266-L279)
- [StringFormatterUI.java](file://src/main/java/org/oxff/ui/StringFormatterUI.java#L284-L295)