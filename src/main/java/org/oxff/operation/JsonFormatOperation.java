package org.oxff.operation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.oxff.core.OperationCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON格式化操作实现
 */
public class JsonFormatOperation implements Operation {
    @Override
    public String execute(String input) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Object obj = gson.fromJson(input, Object.class);
            return gson.toJson(obj);
        } catch (JsonSyntaxException e) {
            return "无效的JSON格式: " + e.getMessage();
        }
    }
    
    @Override
    public OperationCategory getCategory() {
        return OperationCategory.FORMATTING;
    }
    
    @Override
    public String getDisplayName() {
        return "JSON格式化";
    }

    /**
     * 带JSONPath表达式的执行方法
     * @param input JSON输入
     * @param expressions JSONPath表达式（每行一个）
     * @return 提取的结果，每行一个值
     */
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

                    if (result == null) {
                        continue; // 跳过null值
                    }

                    if (result instanceof List) {
                        // 如果结果是列表，逐个处理
                        List<?> resultList = (List<?>) result;
                        for (Object item : resultList) {
                            String value = convertToString(item);
                            if (value != null && !value.isEmpty()) {
                                results.add(value);
                            }
                        }
                    } else {
                        // 单个结果
                        String value = convertToString(result);
                        if (value != null && !value.isEmpty()) {
                            results.add(value);
                        }
                    }
                } catch (PathNotFoundException e) {
                    // 路径未找到，跳过此表达式
                    continue;
                } catch (Exception e) {
                    results.add("JSONPath表达式错误 [" + expression + "]: " + e.getMessage());
                }
            }

            if (results.isEmpty()) {
                return "未找到匹配的节点";
            }

            return String.join("\n", results);

        } catch (JsonSyntaxException e) {
            return "无效的JSON格式: " + e.getMessage();
        }
    }

    /**
     * 将对象转换为字符串
     */
    private String convertToString(Object obj) {
        if (obj == null) {
            return null;
        }

        if (obj instanceof String) {
            return ((String) obj).trim();
        } else if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        } else {
            // 对于复杂对象，使用Gson序列化
            Gson gson = new Gson();
            return gson.toJson(obj).trim();
        }
    }
}