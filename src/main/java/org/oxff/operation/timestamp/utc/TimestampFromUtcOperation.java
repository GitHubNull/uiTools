package org.oxff.operation.timestamp.utc;

import org.oxff.core.OperationCategory;
import org.oxff.operation.Operation;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * UTC时间转时间戳操作实现
 */
public class TimestampFromUtcOperation implements Operation {

    @Override
    public String execute(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "错误：请输入UTC时间和格式";
        }

        try {
            String[] lines = input.trim().split("\n");
            String dateString = lines[0].trim();
            String format = "yyyy-MM-dd HH:mm:ss"; // 默认格式

            // 如果提供了自定义格式
            if (lines.length > 1 && !lines[1].trim().isEmpty()) {
                format = lines[1].trim();
            }

            // 解析UTC日期时间
            LocalDateTime utcDateTime;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

            try {
                if (format.contains("H") || format.contains("h")) { // 包含时间部分
                    utcDateTime = LocalDateTime.parse(dateString, formatter);
                } else { // 只有日期部分，补充时间
                    utcDateTime = LocalDateTime.parse(dateString + " 00:00:00",
                        DateTimeFormatter.ofPattern(format + " HH:mm:ss"));
                }
            } catch (DateTimeParseException e) {
                if (!format.contains("H") && !format.contains("h")) {
                    utcDateTime = LocalDateTime.parse(dateString, formatter);
                } else {
                    throw e;
                }
            }

            // 转换为UTC时间戳
            ZonedDateTime utcZonedDateTime = utcDateTime.atZone(ZoneId.of("UTC"));
            long epochMilli = utcZonedDateTime.toInstant().toEpochMilli();
            long epochSecond = epochMilli / 1000;

            // 获取对应的本地时间
            ZonedDateTime localDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.systemDefault());
            DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String localTime = localDateTime.format(displayFormatter);

            return epochMilli + " (毫秒)\n" + epochSecond + " (秒)\n" + localTime + " (本地)";

        } catch (DateTimeParseException e) {
            return "UTC时间格式错误";
        } catch (Exception e) {
            return "UTC时间转换失败: " + e.getMessage();
        }
    }

    @Override
    public OperationCategory getCategory() {
        return OperationCategory.TIMESTAMP;
    }

    @Override
    public String getDisplayName() {
        return "UTC时间转时间戳";
    }
}