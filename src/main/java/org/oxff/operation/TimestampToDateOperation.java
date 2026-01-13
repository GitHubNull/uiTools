package org.oxff.operation;

import org.oxff.core.OperationCategory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间戳转日期字符串操作实现
 */
public class TimestampToDateOperation implements Operation {

    @Override
    public String execute(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "请输入时间戳\n\n支持的格式：\n- 秒级时间戳（10位）：1712345678\n- 毫秒级时间戳（13位）：1712345678900\n- 支持自定义格式，第二行输入格式";
        }

        String timestampStr = "";
        try {
            String[] lines = input.trim().split("\n");
            timestampStr = lines[0].trim();
            String format = "yyyy-MM-dd HH:mm:ss"; // 默认格式

            // 如果提供了自定义格式
            if (lines.length > 1 && !lines[1].trim().isEmpty()) {
                format = lines[1].trim();
            }

            // 解析时间戳
            long timestamp;
            boolean isMilliseconds = false;

            if (timestampStr.matches("\\d{13}")) {
                // 13位毫秒级时间戳
                timestamp = Long.parseLong(timestampStr);
                isMilliseconds = true;
            } else if (timestampStr.matches("\\d{10}")) {
                // 10位秒级时间戳
                timestamp = Long.parseLong(timestampStr) * 1000;
            } else {
                // 尝试解析为数字
                timestamp = Long.parseLong(timestampStr);
                // 自动判断是否为毫秒级
                if (timestamp > 9999999999L) {
                    isMilliseconds = true;
                } else {
                    timestamp = timestamp * 1000; // 转换为毫秒
                }
            }

            // 转换为日期时间
            Instant instant = Instant.ofEpochMilli(timestamp);
            ZonedDateTime dateTime = instant.atZone(ZoneId.systemDefault());

            // 格式化日期
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            String formattedDate = dateTime.format(formatter);

            // 构建结果
            StringBuilder result = new StringBuilder();
            result.append("输入时间戳: ").append(timestampStr).append("\n");
            result.append("时间戳类型: ").append(isMilliseconds ? "毫秒级" : "秒级").append("\n");
            result.append("使用格式: ").append(format).append("\n");
            result.append("本地时间: ").append(formattedDate).append("\n");
            result.append("UTC时间: ").append(instant.atZone(ZoneId.of("UTC")).format(formatter)).append("\n");
            result.append("\n提示：\n");
            result.append("- 如需使用UTC时区，请使用UTC时间戳转换操作\n");
            result.append("- 支持自定义格式，第二行输入格式如：yyyy/MM/dd HH:mm:ss");

            return result.toString();

        } catch (NumberFormatException e) {
            return "时间戳格式错误\n\n输入的时间戳: " + timestampStr + "\n\n" +
                   "支持的格式：\n" +
                   "- 秒级时间戳：10位数字，如1712345678\n" +
                   "- 毫秒级时间戳：13位数字，如1712345678900\n\n" +
                   "使用方法：第一行输入时间戳，第二行输入格式（可选）";
        } catch (Exception e) {
            return "时间戳转换失败: " + e.getMessage();
        }
    }

    @Override
    public OperationCategory getCategory() {
        return OperationCategory.TIMESTAMP;
    }

    @Override
    public String getDisplayName() {
        return "时间戳转日期";
    }
}