package org.oxff.operation.generator.idcard;

import org.oxff.core.OperationCategory;
import org.oxff.core.Subcategory;
import org.oxff.core.SubcategoryRegistry;
import org.oxff.operation.Operation;
import org.oxff.util.IdCardUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 身份证号码校验操作
 * 验证身份证号码格式和校验位是否正确
 */
public class IdCardValidateOperation implements Operation {

    @Override
    public String execute(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "请输入要校验的身份证号码";
        }

        // 按行分割，支持批量校验
        String[] lines = input.trim().split("\\r?\\n");
        List<String> results = new ArrayList<>();

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }

            // 提取号码（可能包含其他字符）
            String idCard = extractIdCard(line);

            if (idCard == null) {
                results.add(line + ": " + "未找到有效的身份证号码");
                continue;
            }

            // 校验身份证号码
            String validationResult = IdCardUtils.validateIdCard(idCard);

            if ("校验通过".equals(validationResult)) {
                results.add(idCard + ": " + "校验通过");
            } else {
                results.add(idCard + ": " + "校验失败 - " + validationResult);
            }
        }

        return String.join("\n", results);
    }

    /**
     * 从文本中提取身份证号码
     * 支持纯数字格式和带X的格式
     */
    private String extractIdCard(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }

        // 移除所有空格
        text = text.replaceAll("\\s+", "");

        // 检查长度和格式
        if (text.length() == 18) {
            // 检查是否是有效的身份证格式
            boolean valid = true;
            for (int i = 0; i < 17; i++) {
                char c = text.charAt(i);
                if (c < '0' || c > '9') {
                    valid = false;
                    break;
                }
            }

            if (valid) {
                char lastChar = text.charAt(17);
                if ((lastChar >= '0' && lastChar <= '9') || lastChar == 'X' || lastChar == 'x') {
                    return text.toUpperCase();
                }
            }
        }

        // 尝试使用正则表达式查找18位数字或17位数字+X
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\d{17}[0-9Xx]");
        java.util.regex.Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group().toUpperCase();
        }

        return null;
    }

    @Override
    public OperationCategory getCategory() {
        return OperationCategory.GENERATOR;
    }

    @Override
    public String getDisplayName() {
        return "校验身份证号码";
    }

    @Override
    public Subcategory getSubcategory() {
        return SubcategoryRegistry.getSubcategory("idcard");
    }
}
