package org.oxff.operation;

import org.oxff.core.OperationCategory;

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
            return "请输入时间戳和格式\n\n格式示例：\n- yyyy-MM-dd HH:mm:ss\n- yyyy/MM/dd\n- HH:mm:ss\n- E, MMM dd yyyy\n\n使用方法：\n第一行：时间戳（秒级或毫秒级）\n第二行：目标格式";
        }

        String timestampStr = "";
        try {
            String[] lines = input.trim().split("\n");
            if (lines.length < 2) {
                return "请输入格式\n\n使用方法：\n第一行：时间戳\n第二行：目标格式\n\n格式示例：\n- yyyy-MM-dd HH:mm:ss\n- yyyy/MM/dd HH:mm\n- dd-MM-yyyy\n- HH:mm:ss";
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
                return "日期格式错误: " + format + "\n\n" +
                       "支持的格式符号：\n" +
                       "- yyyy: 年份\n" +
                       "- MM: 月份\n" +
                       "- dd: 日期\n" +
                       "- HH: 小时（24小时制）\n" +
                       "- mm: 分钟\n" +
                       "- ss: 秒\n" +
                       "- E: 星期几\n" +
                       "- MMM: 月份缩写\n\n" +
                       "常见格式示例：\n" +
                       "- yyyy-MM-dd HH:mm:ss\n" +
                       "- yyyy/MM/dd\n" +
                       "- dd-MM-yyyy HH:mm\n" +
                       "- E, MMM dd yyyy";
            }

            // 转换时间戳
            Instant instant = Instant.ofEpochMilli(timestamp);
            ZonedDateTime dateTime = instant.atZone(ZoneId.systemDefault());

            // 格式化日期
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            String formattedDate = dateTime.format(formatter);

            // 构建结果
            StringBuilder result = new StringBuilder();
            result.append("输入时间戳: ").append(timestampStr).append("\n");
            result.append("时间戳类型: ").append(isMilliseconds ? "毫秒级" : "秒级").append("\n");
            result.append("使用格式: ").append(format).append("\n");
            result.append("格式化结果: ").append(formattedDate).append("\n");
            result.append("\n格式说明：\n");
            result.append("- 结果使用系统默认时区").append("\n");
            result.append("- 如需UTC时间，请使用UTC时间戳转换操作");

            return result.toString();

        } catch (NumberFormatException e) {
            return "时间戳格式错误: " + timestampStr + "\n\n" +
                   "支持的格式：\n" +
                   "- 秒级时间戳：10位数字\n" +
                   "- 毫秒级时间戳：13位数字\n\n" +
                   "示例：1712345678 或 1712345678900";
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
}