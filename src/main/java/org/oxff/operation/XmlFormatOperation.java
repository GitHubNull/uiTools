package org.oxff.operation;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.oxff.core.OperationCategory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * XML格式化操作实现
 */
public class XmlFormatOperation implements Operation {
    @Override
    public String execute(String input) {
        try {
            Document document = DocumentHelper.parseText(input);
            StringWriter sw = new StringWriter();
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            XMLWriter xw = new XMLWriter(sw, format);
            xw.write(document);
            xw.close();
            return sw.toString();
        } catch (DocumentException | IOException e) {
            return "无效的XML格式: " + e.getMessage();
        }
    }
    
    @Override
    public OperationCategory getCategory() {
        return OperationCategory.FORMATTING;
    }
    
    @Override
    public String getDisplayName() {
        return "XML格式化";
    }

    /**
     * 带XPath表达式的执行方法
     * @param input XML输入
     * @param expressions XPath表达式（每行一个）
     * @return 提取的结果，每行一个值
     */
    public String execute(String input, String expressions) {
        if (expressions == null || expressions.trim().isEmpty()) {
            return execute(input);
        }

        try {
            // 使用dom4j进行XPath处理
            Document document = DocumentHelper.parseText(input);

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

        } catch (DocumentException e) {
            return "无效的XML格式: " + e.getMessage();
        }
    }

    /**
     * 获取dom4j节点的文本值
     */
    private String getDom4jNodeValue(Node node) {
        if (node == null) {
            return null;
        }

        switch (node.getNodeType()) {
            case Node.ELEMENT_NODE:
                return node.getText().trim();
            case Node.ATTRIBUTE_NODE:
            case Node.TEXT_NODE:
            case Node.CDATA_SECTION_NODE:
                return node.getStringValue().trim();
            default:
                return node.asXML().trim();
        }
    }
}