package org.oxff.operation;

import org.oxff.core.OperationCategory;

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
            return "请输入UTC时间字符串\n\n支持格式：\n- yyyy-MM-dd HH:mm:ss\n- yyyy-MM-dd\n- 自定义格式（在第二行输入格式）\n\n注意：输入的时间将被视为UTC时间";
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
            String zoneId = ZoneId.systemDefault().getId();
            DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String localTime = localDateTime.format(displayFormatter);

            // 计算时差
            int offsetSeconds = localDateTime.getOffset().getTotalSeconds();
            int hours = offsetSeconds / 3600;
            int minutes = Math.abs(offsetSeconds % 3600) / 60;
            String offset = String.format("%s%02d:%02d", offsetSeconds >= 0 ? "+" : "-", Math.abs(hours), minutes);

            // 构建结果
            StringBuilder result = new StringBuilder();
            result.append("输入UTC时间: ").append(dateString).append("\n");
            result.append("使用格式: ").append(format).append("\n");
            result.append("=== 转换结果 ===\n");
            result.append("毫秒级时间戳: ").append(epochMilli).append("\n");
            result.append("秒级时间戳: ").append(epochSecond).append("\n");
            result.append("\n=== 对应本地时间 ===\n");
            result.append("本地时间: ").append(localTime).append("\n");
            result.append("本地时区: ").append(zoneId).append("\n");
            result.append("UTC偏移: ").append(offset).append("\n");
            result.append("\n提示：\n");
            result.append("- 输入的").append(dateString).append("被解析为UTC时间").append("\n");
            result.append("- 如需转换本地时间，请使用日期转时间戳操作");

            return result.toString();

        } catch (DateTimeParseException e) {
            return "UTC时间解析失败，请检查格式是否正确\n\n输入的UTC时间: " + input.trim().split("\n")[0] + "\n" +
                   "尝试的格式: " + (input.contains("\n") && input.split("\n").length > 1 ?
                   input.split("\n")[1].trim() : "yyyy-MM-dd HH:mm:ss") + "\n\n" +
                   "支持的格式示例：\n" +
                   "- 2024-01-13 15:30:00 (格式: yyyy-MM-dd HH:mm:ss)\n" +
                   "- 2024-01-13 (格式: yyyy-MM-dd)\n" +
                   "- 2024/01/13 15:30:00 (格式: yyyy/MM/dd HH:mm:ss)\n" +
                   "- 13-01-2024 (格式: dd-MM-yyyy)\n\n" +
                   "使用方法：第一行输入UTC时间，第二行输入格式（可选）";
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