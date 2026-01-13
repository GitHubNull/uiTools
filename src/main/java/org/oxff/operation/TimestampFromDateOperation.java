package org.oxff.operation;

import org.oxff.core.OperationCategory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 日期字符串转时间戳操作实现
 */
public class TimestampFromDateOperation implements Operation {

    @Override
    public String execute(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "请输入日期字符串，格式如：2024-01-13 15:30:00\n\n支持格式：\n- yyyy-MM-dd HH:mm:ss\n- yyyy-MM-dd\n- 自定义格式（在第二行输入格式）";
        }

        try {
            String[] lines = input.trim().split("\n");
            String dateString = lines[0].trim();
            String format = "yyyy-MM-dd HH:mm:ss"; // 默认格式

            // 如果提供了自定义格式
            if (lines.length > 1 && !lines[1].trim().isEmpty()) {
                format = lines[1].trim();
            }

            // 尝试解析日期
            LocalDateTime dateTime;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

            try {
                if (format.contains("H") || format.contains("h")) { // 包含时间部分
                    dateTime = LocalDateTime.parse(dateString, formatter);
                } else { // 只有日期部分，补充时间
                    dateTime = LocalDateTime.parse(dateString + " 00:00:00",
                        DateTimeFormatter.ofPattern(format + " HH:mm:ss"));
                }
            } catch (DateTimeParseException e) {
                // 尝试只解析日期部分
                if (!format.contains("H") && !format.contains("h")) {
                    dateTime = LocalDateTime.parse(dateString, formatter);
                } else {
                    throw e;
                }
            }

            // 转换为时间戳
            ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
            long epochMilli = zonedDateTime.toInstant().toEpochMilli();
            long epochSecond = epochMilli / 1000;

            // 构建结果
            StringBuilder result = new StringBuilder();
            result.append("输入日期: ").append(dateString).append("\n");
            result.append("使用格式: ").append(format).append("\n");
            result.append("本地时区: ").append(ZoneId.systemDefault()).append("\n\n");
            result.append("毫秒级时间戳: ").append(epochMilli).append("\n");
            result.append("秒级时间戳: ").append(epochSecond).append("\n");
            result.append("\n提示：\n");
            result.append("- 如需使用UTC时区，请使用UTC时间戳转换操作\n");
            result.append("- 支持自定义格式，第二行输入格式如：yyyy/MM/dd HH:mm");

            return result.toString();

        } catch (DateTimeParseException e) {
            return "日期解析失败，请检查格式是否正确\n\n输入的日期: " + input.trim().split("\n")[0] + "\n" +
                   "尝试的格式: " + (input.contains("\n") && input.split("\n").length > 1 ?
                   input.split("\n")[1].trim() : "yyyy-MM-dd HH:mm:ss") + "\n\n" +
                   "支持的格式示例：\n" +
                   "- 2024-01-13 15:30:00 (格式: yyyy-MM-dd HH:mm:ss)\n" +
                   "- 2024-01-13 (格式: yyyy-MM-dd)\n" +
                   "- 2024/01/13 15:30:00 (格式: yyyy/MM/dd HH:mm:ss)\n" +
                   "- 13-01-2024 (格式: dd-MM-yyyy)\n\n" +
                   "使用方法：第一行输入日期，第二行输入格式（可选）";
        } catch (Exception e) {
            return "日期转换失败: " + e.getMessage();
        }
    }

    @Override
    public OperationCategory getCategory() {
        return OperationCategory.TIMESTAMP;
    }

    @Override
    public String getDisplayName() {
        return "日期转时间戳";
    }
}