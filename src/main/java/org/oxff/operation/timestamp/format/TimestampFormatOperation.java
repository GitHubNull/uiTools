package org.oxff.operation.timestamp.format;

import org.oxff.core.OperationCategory;
import org.oxff.core.Subcategory;
import org.oxff.core.SubcategoryRegistry;
import org.oxff.operation.Operation;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间戳格式化操作实现
 */
public class TimestampFormatOperation implements Operation {

    @Override
    public String execute(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "错误：请输入时间戳";
        }

        String timestampStr = "";
        try {
            String[] lines = input.trim().split("\n");
            if (lines.length < 2) {
                return "错误：第二行请输入格式";
            }

            timestampStr = lines[0].trim();
            String format = lines[1].trim();

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
                if (timestamp > 9999999999L) {
                    isMilliseconds = true;
                } else {
                    timestamp = timestamp * 1000; // 转换为毫秒
                }
            }

            // 验证格式
            try {
                DateTimeFormatter.ofPattern(format);
            } catch (Exception e) {
                return "格式错误: " + format;
            }

            // 转换时间戳
            Instant instant = Instant.ofEpochMilli(timestamp);
            ZonedDateTime dateTime = instant.atZone(ZoneId.systemDefault());

            // 格式化日期
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            String formattedDate = dateTime.format(formatter);

            return formattedDate;

        } catch (NumberFormatException e) {
            return "时间戳格式: " + timestampStr;
        } catch (Exception e) {
            return "时间戳格式化失败: " + e.getMessage();
        }
    }

    @Override
    public OperationCategory getCategory() {
        return OperationCategory.TIMESTAMP;
    }

    @Override
    public String getDisplayName() {
        return "时间戳格式化";
    }

    @Override
    public Subcategory getSubcategory() {
        return SubcategoryRegistry.getSubcategory("format");
    }
}