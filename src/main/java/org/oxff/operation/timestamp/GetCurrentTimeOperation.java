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
 * 获取当前时间/时间戳操作
 */
public class GetCurrentTimeOperation implements Operation {
    private String timezoneId = "";
    private String outputType = "datetime";
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";
    private String timestampDigits = "13";
    private boolean padWithZero = false;

    public void setConfig(String timezoneId, String outputType, String dateFormat,
                         String timestampDigits, boolean padWithZero) {
        this.timezoneId = timezoneId != null ? timezoneId : "";
        this.outputType = outputType != null ? outputType : "datetime";
        this.dateFormat = dateFormat != null ? dateFormat : "yyyy-MM-dd HH:mm:ss";
        this.timestampDigits = timestampDigits != null ? timestampDigits : "13";
        this.padWithZero = padWithZero;
    }

    @Override
    public String execute(String input) {
        try {
            Instant instant = Instant.now();
            long currentTimeMillis = instant.toEpochMilli();
            long currentTimeSeconds = currentTimeMillis / 1000;

            ZoneId targetZone = parseTimezone(timezoneId);
            ZonedDateTime targetDateTime = instant.atZone(targetZone);

            if ("timestamp".equals(outputType)) {
                return formatTimestamp(currentTimeMillis, currentTimeSeconds);
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
                return targetDateTime.format(formatter);
            }
        } catch (Exception e) {
            return "获取当前时间失败: " + e.getMessage();
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

    private String formatTimestamp(long millis, long seconds) {
        if ("10".equals(timestampDigits)) {
            String result = String.valueOf(seconds);
            if (padWithZero) {
                return String.format("%013d", seconds);
            }
            return result;
        } else {
            String result = String.valueOf(millis);
            if (padWithZero) {
                return String.format("%016d", millis);
            }
            return result;
        }
    }

    @Override
    public OperationCategory getCategory() {
        return OperationCategory.TIMESTAMP;
    }

    @Override
    public String getDisplayName() {
        return "获取当前时间";
    }

    @Override
    public Subcategory getSubcategory() {
        return SubcategoryRegistry.getSubcategory("basic");
    }
}
