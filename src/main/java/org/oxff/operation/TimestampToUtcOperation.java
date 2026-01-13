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
            return "请输入时间戳\n\n支持的格式：\n- 秒级时间戳（10位）：1712345678\n- 毫秒级时间戳（13位）：1712345678900\n\n将转换为UTC时间和本地时间对比";
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
            String zoneId = ZoneId.systemDefault().getId();

            // 构建结果
            StringBuilder result = new StringBuilder();
            result.append("输入时间戳: ").append(timestampStr).append("\n");
            result.append("时间戳类型: ").append(isMilliseconds ? "毫秒级" : "秒级").append("\n\n");
            result.append("=== UTC时间 ===\n");
            result.append("UTC时间: ").append(utcTime).append("\n");
            result.append("时区: UTC+00:00\n\n");
            result.append("=== 本地时间 ===\n");
            result.append("本地时间: ").append(localTime).append("\n");
            result.append("时区: ").append(zoneId).append("\n\n");
            result.append("=== 时间差 ===\n");

            // 计算时差
            int offsetSeconds = localDateTime.getOffset().getTotalSeconds();
            int hours = offsetSeconds / 3600;
            int minutes = Math.abs(offsetSeconds % 3600) / 60;
            String offset = String.format("%s%02d:%02d", offsetSeconds >= 0 ? "+" : "-", Math.abs(hours), minutes);
            result.append("UTC偏移: ").append(offset).append("\n");
            result.append("本地比UTC").append(offsetSeconds >= 0 ? "快" : "慢").append(" ")
                  .append(Math.abs(hours)).append("小时").append(minutes > 0 ? " " + minutes + "分钟" : "");

            return result.toString();

        } catch (NumberFormatException e) {
            return "时间戳格式错误\n\n输入的时间戳: " + timestampStr + "\n\n" +
                   "支持的格式：\n" +
                   "- 秒级时间戳：10位数字，如1712345678\n" +
                   "- 毫秒级时间戳：13位数字，如1712345678900";
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