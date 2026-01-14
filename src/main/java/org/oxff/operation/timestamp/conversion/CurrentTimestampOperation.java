package org.oxff.operation.timestamp.conversion;

import org.oxff.core.OperationCategory;
import org.oxff.operation.Operation;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 获取当前时间戳操作实现
 */
public class CurrentTimestampOperation implements Operation {

    @Override
    public String execute(String input) {
        try {
            // 获取当前时间戳（毫秒级）
            long currentTimeMillis = System.currentTimeMillis();
            long currentTimeSeconds = currentTimeMillis / 1000;

            // 获取当前时间信息
            Instant instant = Instant.now();

            // 解析输入参数（时区设置）
            String targetZoneId = null;
            if (input != null && !input.trim().isEmpty()) {
                targetZoneId = input.trim();
            }

            // 根据时区设置获取对应的时间
            ZonedDateTime targetDateTime;
            ZoneId targetZone;
            String zoneDisplayName;

            if (targetZoneId == null || targetZoneId.isEmpty()) {
                // 使用系统时区
                targetZone = ZoneId.systemDefault();
                zoneDisplayName = "本地时区";
            } else if ("utc".equalsIgnoreCase(targetZoneId)) {
                // UTC时区
                targetZone = ZoneId.of("UTC");
                zoneDisplayName = "UTC";
            } else {
                // 使用指定的时区ID
                try {
                    targetZone = ZoneId.of(targetZoneId);
                    zoneDisplayName = targetZone.getId();
                } catch (Exception e) {
                    // 时区ID无效，回退到系统时区
                    targetZone = ZoneId.systemDefault();
                    zoneDisplayName = "本地时区";
                }
            }

            targetDateTime = instant.atZone(targetZone);

            // 格式化时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedTime = targetDateTime.format(formatter);

            return currentTimeMillis + "\n" + currentTimeSeconds + "\n" + formattedTime;

        } catch (Exception e) {
            return "获取当前时间戳失败: " + e.getMessage();
        }
    }

    @Override
    public OperationCategory getCategory() {
        return OperationCategory.TIMESTAMP;
    }

    @Override
    public String getDisplayName() {
        return "获取当前时间戳";
    }
}