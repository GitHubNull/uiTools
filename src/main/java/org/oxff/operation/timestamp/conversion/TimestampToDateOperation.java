package org.oxff.operation.timestamp.conversion;

import org.oxff.core.OperationCategory;
import org.oxff.core.Subcategory;
import org.oxff.core.SubcategoryRegistry;
import org.oxff.operation.Operation;

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
            return "错误：请输入时间戳（10或13位数字）";
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
            String utcFormatted = instant.atZone(ZoneId.of("UTC")).format(formatter);

            return formattedDate + " (本地)\n" + utcFormatted + " (UTC)";

        } catch (NumberFormatException e) {
            return "时间戳格式错误: " + timestampStr + "\n\n支持的格式：10或13位数字";
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

    @Override
    public Subcategory getSubcategory() {
        return SubcategoryRegistry.getSubcategory("conversion");
    }
}