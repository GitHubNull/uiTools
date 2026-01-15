package org.oxff.operation.timestamp;

import org.oxff.core.OperationCategory;
import org.oxff.core.Subcategory;
import org.oxff.core.SubcategoryRegistry;
import org.oxff.operation.Operation;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间戳转日期时间操作
 */
public class TimestampToDatetimeOperation implements Operation {
    private String timezoneId = "";
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";

    public void setConfig(String timezoneId, String dateFormat) {
        this.timezoneId = timezoneId != null ? timezoneId : "";
        this.dateFormat = dateFormat != null ? dateFormat : "yyyy-MM-dd HH:mm:ss";
    }

    @Override
    public String execute(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "错误：请输入时间戳";
        }

        try {
            String timestampStr = input.trim();
            long timestamp;

            // 去除前面的0
            timestampStr = timestampStr.replaceFirst("^0+", "");

            if (timestampStr.matches("\\d{13}")) {
                timestamp = Long.parseLong(timestampStr);
            } else if (timestampStr.matches("\\d{10}")) {
                timestamp = Long.parseLong(timestampStr) * 1000;
            } else if (timestampStr.matches("\\d{1,9}")) {
                timestamp = Long.parseLong(timestampStr) * 1000;
            } else {
                // 尝试解析为数字
                timestamp = Long.parseLong(timestampStr);
                if (timestamp > 9999999999L) {
                    // 已经是毫秒级
                } else {
                    timestamp = timestamp * 1000;
                }
            }

            Instant instant = Instant.ofEpochMilli(timestamp);
            ZoneId targetZone = parseTimezone(timezoneId);
            ZonedDateTime dateTime = instant.atZone(targetZone);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
            return dateTime.format(formatter);

        } catch (NumberFormatException e) {
            return "时间戳格式错误: " + input.trim();
        } catch (Exception e) {
            return "时间戳转换失败: " + e.getMessage();
        }
    }

    private ZoneId parseTimezone(String timezoneId) {
        if (timezoneId == null || timezoneId.isEmpty()) {
            return ZoneId.systemDefault();
        }
        String zoneId = mapTimezoneId(timezoneId);
        try {
            return ZoneId.of(zoneId);
        } catch (Exception e) {
            return ZoneId.systemDefault();
        }
    }

    private String mapTimezoneId(String displayName) {
        if (displayName.contains("系统时区") || displayName.isEmpty()) {
            return ZoneId.systemDefault().getId();
        } else if (displayName.contains("UTC") && displayName.contains("协调")) {
            return "UTC";
        } else if (displayName.contains("中国") || displayName.contains("CST")) {
            return "Asia/Shanghai";
        } else if (displayName.contains("日本") || displayName.contains("JST")) {
            return "Asia/Tokyo";
        } else if (displayName.contains("美国东部") || displayName.contains("EST")) {
            return "America/New_York";
        } else if (displayName.contains("美国西部") || displayName.contains("PST")) {
            return "America/Los_Angeles";
        } else if (displayName.contains("欧洲中部") || displayName.contains("CET")) {
            return "Europe/Paris";
        } else if (displayName.contains("英国") || displayName.contains("伦敦")) {
            return "Europe/London";
        } else if (displayName.contains("澳大利亚") || displayName.contains("AEST")) {
            return "Australia/Sydney";
        } else if (displayName.contains("印度") || displayName.contains("IST")) {
            return "Asia/Kolkata";
        }
        return ZoneId.systemDefault().getId();
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
        return SubcategoryRegistry.getSubcategory("basic");
    }
}
