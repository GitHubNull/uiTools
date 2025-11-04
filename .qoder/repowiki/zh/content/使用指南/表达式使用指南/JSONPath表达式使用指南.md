# JSONPath表达式使用指南

<cite>
**Referenced Files in This Document**   
- [JsonFormatOperation.java](file://src/main/java/org/oxff/operation/JsonFormatOperation.java)
- [StringFormatterUI.java](file://src/main/java/org/oxff/ui/StringFormatterUI.java)
- [README.md](file://README.md)
</cite>

## 目录
1. [JSONPath表达式语法](#jsonpath表达式语法)
2. [JSONPath使用示例](#jsonpath使用示例)
3. [Jayway JsonPath库实现](#jayway-jsonpath库实现)
4. [多表达式批量处理](#多表达式批量处理)
5. [表达式输入区域动态显示](#表达式输入区域动态显示)

## JSONPath表达式语法

JSONPath是一种用于在JSON文档中定位和提取数据的查询语言，类似于XPath在XML文档中的作用。它提供了一套简洁的语法来导航和筛选JSON数据结构。

### 基本语法元素

- **$**: 表示JSON文档的根元素
- **.**: 用于访问对象的属性
- **[]**: 用于数组索引或条件过滤
- **\***: 通配符，表示所有元素
- **?()**: 条件表达式，用于过滤
- **@**: 在条件表达式中引用当前节点

### 常用操作符

- **点表示法**: `$.store.book` 访问store对象下的book属性
- **括号表示法**: `$['store']['book']` 等同于点表示法
- **数组索引**: `$.store.book[0]` 访问第一个book对象
- **范围操作**: `$.store.book[0:2]` 访问前两个book对象
- **过滤操作**: `$.store.book[?(@.price > 10)]` 过滤价格大于10的书籍

**Section sources**
- [README.md](file://README.md#L121-L172)

## JSONPath使用示例

基于README中的示例JSON数据，以下是一些常用的JSONPath表达式及其结果。

### 示例JSON数据
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

### 常用表达式示例

#### 提取所有书名
```jsonpath
$.store.book[*].title
```
此表达式将返回所有书籍的标题：
```
Great Gatsby
Harry Potter
```

#### 提取特定类别的书籍价格
```jsonpath
$.store.book[?(@.category=='fiction')].price
```
此表达式使用条件过滤器提取小说类书籍的价格：
```
10.99
```

#### 提取特定年份后的书籍
```jsonpath
$.store.book[?(@.year>2000)].title
```
此表达式提取2000年后出版的书籍标题：
```
Harry Potter
```

#### 计算书籍总数
```jsonpath
$.store.book.length()
```
此表达式返回书籍数组的长度：
```
2
```

#### 提取所有作者
```jsonpath
$.store.book[*].author
```
此表达式返回所有书籍的作者：
```
F. Scott Fitzgerald
J.K. Rowling
```

**Section sources**
- [README.md](file://README.md#L121-L172)

## Jayway JsonPath库实现

uiTools工具通过集成Jayway JsonPath库来实现JSONPath表达式的解析和执行功能。该库提供了强大的JSON查询能力，支持复杂的路径表达式和条件过滤。

### JSONPath解析流程

1. **JSON验证**: 首先使用Gson库验证输入的JSON格式是否正确
2. **表达式解析**: 将输入的JSON字符串解析为可操作的对象
3. **路径执行**: 使用JsonPath库的`parse`和`read`方法执行表达式
4. **结果处理**: 将执行结果转换为字符串格式并返回

### 核心实现代码

`JsonFormatOperation`类中的`execute`方法实现了JSONPath表达式的处理逻辑：

```java
public String execute(String input, String expressions) {
    if (expressions == null || expressions.trim().isEmpty()) {
        return execute(input);
    }

    try {
        // 首先验证JSON格式
        Gson gson = new Gson();
        Object jsonObject = gson.fromJson(input, Object.class);

        String[] expressionLines = expressions.split("\\r?\\n");
        List<String> results = new ArrayList<>();

        for (String expression : expressionLines) {
            expression = expression.trim();
            if (expression.isEmpty()) {
                continue;
            }

            try {
                Object result = JsonPath.parse(jsonObject).read(expression);
                // 处理结果...
            } catch (PathNotFoundException e) {
                continue;
            } catch (Exception e) {
                results.add("JSONPath表达式错误 [" + expression + "]: " + e.getMessage());
            }
        }

        return String.join("\n", results);
    } catch (JsonSyntaxException e) {
        return "无效的JSON格式: " + e.getMessage();
    }
}
```

该实现支持处理多个表达式，每行一个，并将结果以行分隔的格式输出。

**Diagram sources**
- [JsonFormatOperation.java](file://src/main/java/org/oxff/operation/JsonFormatOperation.java#L43-L102)

**Section sources**
- [JsonFormatOperation.java](file://src/main/java/org/oxff/operation/JsonFormatOperation.java#L43-L102)

## 多表达式批量处理

uiTools支持多表达式批量处理功能，允许用户一次输入多个JSONPath表达式，系统将按顺序执行并返回所有结果。

### 批量处理机制

1. **表达式分割**: 将表达式输入区域的文本按行分割
2. **逐个执行**: 对每个非空表达式调用JsonPath解析
3. **结果收集**: 将每个表达式的结果添加到结果列表
4. **格式化输出**: 使用换行符连接所有结果

### 错误处理

系统对每个表达式进行独立处理，确保单个表达式的错误不会影响其他表达式的执行：

- **路径未找到**: 跳过该表达式，继续处理下一个
- **语法错误**: 返回错误信息，包含表达式内容和错误详情
- **空结果**: 跳过null值，不添加到结果列表

### 输出格式

结果以纯文本格式输出，每行对应一个表达式的结果。这种格式便于用户复制和进一步处理结果数据。

**Section sources**
- [JsonFormatOperation.java](file://src/main/java/org/oxff/operation/JsonFormatOperation.java#L43-L102)

## 表达式输入区域动态显示

uiTools通过智能的UI逻辑控制表达式输入区域的显示，仅在需要时才显示该区域，提升用户体验。

### 显示逻辑实现

#### requiresExpressionInput方法

该方法判断特定操作是否需要表达式输入：

```java
private boolean requiresExpressionInput(String operationName) {
    if (operationName == null || operationName.isEmpty()) {
        return false;
    }

    Operation operation = OperationFactory.getOperation(operationName);
    if (operation == null) {
        return false;
    }

    // 只有JSON和XML格式化操作需要表达式输入
    String className = operation.getClass().getSimpleName();
    return "JsonFormatOperation".equals(className) || "XmlFormatOperation".equals(className);
}
```

#### updateExpressionPanelVisibility方法

该方法根据当前选择的操作更新表达式面板的可见性：

```java
private void updateExpressionPanelVisibility() {
    boolean showExpressionPanel = requiresExpressionInput(selectedOperation);
    boolean isAutomation = isAutomationOperation(selectedOperation);

    // 更新表达式面板可见性
    if (showExpressionPanel) {
        // 显示表达式面板
        outputExpressionSplitPane.setLeftComponent(expressionPanel);
    } else {
        // 隐藏表达式面板
        outputExpressionSplitPane.setLeftComponent(null);
    }

    // 更新输出面板可见性
    if (isAutomation) {
        // 对于自动化操作，隐藏输出面板
        outputExpressionSplitPane.setRightComponent(null);
    } else {
        // 对于其他操作，显示输出面板
        outputExpressionSplitPane.setRightComponent(outputPanel);
    }

    outputExpressionSplitPane.revalidate();
    outputExpressionSplitPane.repaint();
}
```

### 动态显示流程

1. **操作选择**: 用户在下拉菜单或树形结构中选择操作
2. **事件触发**: 操作选择事件触发`updateExpressionPanelVisibility`方法
3. **条件判断**: 根据操作类型决定是否显示表达式输入区域
4. **UI更新**: 动态添加或移除表达式面板组件
5. **界面重绘**: 重新验证和重绘分割面板

这种动态显示机制确保了界面的简洁性，只在需要时才显示相关控件。

**Diagram sources**
- [StringFormatterUI.java](file://src/main/java/org/oxff/ui/StringFormatterUI.java#L267-L280)
- [StringFormatterUI.java](file://src/main/java/org/oxff/ui/StringFormatterUI.java#L304-L328)

**Section sources**
- [StringFormatterUI.java](file://src/main/java/org/oxff/ui/StringFormatterUI.java#L267-L280)
- [StringFormatterUI.java](file://src/main/java/org/oxff/ui/StringFormatterUI.java#L304-L328)