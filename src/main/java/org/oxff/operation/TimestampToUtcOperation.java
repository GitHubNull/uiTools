package org.oxff.operation;

import org.oxff.core.OperationCategory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间戳转UTC时间操作实现
 */
public class TimestampToUtcOperation implements Operation {

    @Override
    public String execute(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "错误：请输入时间戳";
        }

        String timestampStr = "";
        try {
            timestampStr = input.trim();

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

            // 转换时间戳
            Instant instant = Instant.ofEpochMilli(timestamp);

            // 获取UTC时间和本地时间
            ZonedDateTime utcDateTime = instant.atZone(ZoneId.of("UTC"));
            ZonedDateTime localDateTime = instant.atZone(ZoneId.systemDefault());

            // 格式化
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String utcTime = utcDateTime.format(formatter);
            String localTime = localDateTime.format(formatter);

            return utcTime + " (UTC)\n" + localTime + " (本地)";

        } catch (NumberFormatException e) {
            return "时间戳格式: " + timestampStr;
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
        return "时间戳转UTC时间";
    }
}