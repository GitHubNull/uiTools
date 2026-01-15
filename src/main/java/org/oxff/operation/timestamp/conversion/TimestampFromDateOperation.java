package org.oxff.operation.timestamp.conversion;

import org.oxff.core.OperationCategory;
import org.oxff.core.Subcategory;
import org.oxff.core.SubcategoryRegistry;
import org.oxff.operation.Operation;

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
            return "错误：请输入日期和格式";
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

            return epochMilli + " (毫秒)\n" + epochSecond + " (秒)";

        } catch (DateTimeParseException e) {
            return "日期格式错误";
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

    @Override
    public Subcategory getSubcategory() {
        return SubcategoryRegistry.getSubcategory("conversion");
    }
}