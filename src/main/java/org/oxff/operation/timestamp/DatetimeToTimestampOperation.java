package org.oxff.operation.timestamp;

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
 * 日期时间转时间戳操作
 */
public class DatetimeToTimestampOperation implements Operation {
    private String inputFormat = "yyyy-MM-dd HH:mm:ss";
    private String outputDigits = "13";
    private boolean padWithZero = false;

    public void setConfig(String inputFormat, String outputDigits, boolean padWithZero) {
        this.inputFormat = inputFormat != null ? inputFormat : "yyyy-MM-dd HH:mm:ss";
        this.outputDigits = outputDigits != null ? outputDigits : "13";
        this.padWithZero = padWithZero;
    }

    @Override
    public String execute(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "错误：请输入日期时间";
        }

        try {
            String dateString = input.trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(inputFormat);

            LocalDateTime dateTime;
            try {
                if (inputFormat.contains("H") || inputFormat.contains("h")) {
                    dateTime = LocalDateTime.parse(dateString, formatter);
                } else {
                    dateTime = LocalDateTime.parse(dateString + " 00:00:00",
                            DateTimeFormatter.ofPattern(inputFormat + " HH:mm:ss"));
                }
            } catch (DateTimeParseException e) {
                if (!inputFormat.contains("H") && !inputFormat.contains("h")) {
                    dateTime = LocalDateTime.parse(dateString, formatter);
                } else {
                    throw e;
                }
            }

            ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
            long epochMilli = zonedDateTime.toInstant().toEpochMilli();
            long epochSecond = epochMilli / 1000;

            return formatTimestamp(epochMilli, epochSecond);

        } catch (DateTimeParseException e) {
            return "日期格式错误: " + e.getMessage();
        } catch (Exception e) {
            return "日期转换失败: " + e.getMessage();
        }
    }

    private String formatTimestamp(long millis, long seconds) {
        if ("10".equals(outputDigits)) {
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
        return "日期转时间戳";
    }

    @Override
    public Subcategory getSubcategory() {
        return SubcategoryRegistry.getSubcategory("basic");
    }
}
