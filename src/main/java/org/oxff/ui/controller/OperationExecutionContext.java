package org.oxff.ui.controller;

/**
 * 操作执行上下文对象
 * 封装操作执行所需的所有上下文信息
 */
public class OperationExecutionContext {
    private final String operationName;
    private String inputText;
    private final String expressions;
    private final AutomationConfig automationConfig;
    private final String timezoneSelection;
    private final String imagePath;
    private final BaseEncodingConfig baseEncodingConfig;
    private final PasswordGeneratorConfig passwordGeneratorConfig;
    private final GetCurrentTimeConfig getCurrentTimeConfig;
    private final TimestampToDatetimeConfig timestampToDatetimeConfig;
    private final DatetimeToTimestampConfig datetimeToTimestampConfig;

    private OperationExecutionContext(Builder builder) {
        this.operationName = builder.operationName;
        this.inputText = builder.inputText;
        this.expressions = builder.expressions;
        this.automationConfig = builder.automationConfig;
        this.timezoneSelection = builder.timezoneSelection;
        this.imagePath = builder.imagePath;
        this.baseEncodingConfig = builder.baseEncodingConfig;
        this.passwordGeneratorConfig = builder.passwordGeneratorConfig;
        this.getCurrentTimeConfig = builder.getCurrentTimeConfig;
        this.timestampToDatetimeConfig = builder.timestampToDatetimeConfig;
        this.datetimeToTimestampConfig = builder.datetimeToTimestampConfig;
    }

    public String getOperationName() {
        return operationName;
    }

    public String getInputText() {
        return inputText;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    public String getExpressions() {
        return expressions;
    }

    public AutomationConfig getAutomationConfig() {
        return automationConfig;
    }

    public String getTimezoneSelection() {
        return timezoneSelection;
    }

    public String getImagePath() {
        return imagePath;
    }

    public BaseEncodingConfig getBaseEncodingConfig() {
        return baseEncodingConfig;
    }

    public PasswordGeneratorConfig getPasswordGeneratorConfig() {
        return passwordGeneratorConfig;
    }

    public GetCurrentTimeConfig getGetCurrentTimeConfig() {
        return getCurrentTimeConfig;
    }

    public TimestampToDatetimeConfig getTimestampToDatetimeConfig() {
        return timestampToDatetimeConfig;
    }

    public DatetimeToTimestampConfig getDatetimeToTimestampConfig() {
        return datetimeToTimestampConfig;
    }

    /**
     * 构建器模式创建上下文对象
     */
    public static class Builder {
        private String operationName;
        private String inputText = "";
        private String expressions = "";
        private AutomationConfig automationConfig;
        private String timezoneSelection;
        private String imagePath;
        private BaseEncodingConfig baseEncodingConfig;
        private PasswordGeneratorConfig passwordGeneratorConfig;
        private GetCurrentTimeConfig getCurrentTimeConfig;
        private TimestampToDatetimeConfig timestampToDatetimeConfig;
        private DatetimeToTimestampConfig datetimeToTimestampConfig;

        public Builder operationName(String operationName) {
            this.operationName = operationName;
            return this;
        }

        public Builder inputText(String inputText) {
            this.inputText = inputText;
            return this;
        }

        public Builder expressions(String expressions) {
            this.expressions = expressions;
            return this;
        }

        public Builder automationConfig(AutomationConfig automationConfig) {
            this.automationConfig = automationConfig;
            return this;
        }

        public Builder timezoneSelection(String timezoneSelection) {
            this.timezoneSelection = timezoneSelection;
            return this;
        }

        public Builder imagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public Builder baseEncodingConfig(BaseEncodingConfig baseEncodingConfig) {
            this.baseEncodingConfig = baseEncodingConfig;
            return this;
        }

        public Builder passwordGeneratorConfig(PasswordGeneratorConfig passwordGeneratorConfig) {
            this.passwordGeneratorConfig = passwordGeneratorConfig;
            return this;
        }

        public Builder getCurrentTimeConfig(GetCurrentTimeConfig getCurrentTimeConfig) {
            this.getCurrentTimeConfig = getCurrentTimeConfig;
            return this;
        }

        public Builder timestampToDatetimeConfig(TimestampToDatetimeConfig timestampToDatetimeConfig) {
            this.timestampToDatetimeConfig = timestampToDatetimeConfig;
            return this;
        }

        public Builder datetimeToTimestampConfig(DatetimeToTimestampConfig datetimeToTimestampConfig) {
            this.datetimeToTimestampConfig = datetimeToTimestampConfig;
            return this;
        }

        public OperationExecutionContext build() {
            return new OperationExecutionContext(this);
        }
    }

    /**
     * 自动化操作配置
     */
    public static class AutomationConfig {
        private final int delaySeconds;
        private final int charIntervalMs;
        private final boolean useClipboard;

        public AutomationConfig(int delaySeconds, int charIntervalMs, boolean useClipboard) {
            this.delaySeconds = delaySeconds;
            this.charIntervalMs = charIntervalMs;
            this.useClipboard = useClipboard;
        }

        public int getDelaySeconds() {
            return delaySeconds;
        }

        public int getCharIntervalMs() {
            return charIntervalMs;
        }

        public boolean isUseClipboard() {
            return useClipboard;
        }
    }

    /**
     * Base编码配置
     */
    public static class BaseEncodingConfig {
        private final String encodingType;  // "Base64" 或 "Base32"

        public BaseEncodingConfig(String encodingType) {
            this.encodingType = encodingType;
        }

        public String getEncodingType() {
            return encodingType;
        }
    }

    /**
     * 密码生成器配置
     */
    public static class PasswordGeneratorConfig {
        private final int passwordLength;
        private final boolean includeDigits;
        private final int digitCount;
        private final boolean includeUppercase;
        private final int uppercaseCount;
        private final boolean includeLowercase;
        private final int lowercaseCount;
        private final boolean includeSpecialChars;
        private final int specialCharCount;
        private final int passwordCount;

        public PasswordGeneratorConfig(int passwordLength,
                                       boolean includeDigits, int digitCount,
                                       boolean includeUppercase, int uppercaseCount,
                                       boolean includeLowercase, int lowercaseCount,
                                       boolean includeSpecialChars, int specialCharCount,
                                       int passwordCount) {
            this.passwordLength = passwordLength;
            this.includeDigits = includeDigits;
            this.digitCount = digitCount;
            this.includeUppercase = includeUppercase;
            this.uppercaseCount = uppercaseCount;
            this.includeLowercase = includeLowercase;
            this.lowercaseCount = lowercaseCount;
            this.includeSpecialChars = includeSpecialChars;
            this.specialCharCount = specialCharCount;
            this.passwordCount = passwordCount;
        }

        public int getPasswordLength() {
            return passwordLength;
        }

        public boolean isIncludeDigits() {
            return includeDigits;
        }

        public int getDigitCount() {
            return digitCount;
        }

        public boolean isIncludeUppercase() {
            return includeUppercase;
        }

        public int getUppercaseCount() {
            return uppercaseCount;
        }

        public boolean isIncludeLowercase() {
            return includeLowercase;
        }

        public int getLowercaseCount() {
            return lowercaseCount;
        }

        public boolean isIncludeSpecialChars() {
            return includeSpecialChars;
        }

        public int getSpecialCharCount() {
            return specialCharCount;
        }

        public int getPasswordCount() {
            return passwordCount;
        }
    }

    /**
     * 获取当前时间配置
     */
    public static class GetCurrentTimeConfig {
        private final String timezoneId;
        private final String outputType;
        private final String dateFormat;
        private final String timestampDigits;
        private final boolean padWithZero;

        public GetCurrentTimeConfig(String timezoneId, String outputType, String dateFormat,
                                   String timestampDigits, boolean padWithZero) {
            this.timezoneId = timezoneId;
            this.outputType = outputType;
            this.dateFormat = dateFormat;
            this.timestampDigits = timestampDigits;
            this.padWithZero = padWithZero;
        }

        public String getTimezoneId() {
            return timezoneId;
        }

        public String getOutputType() {
            return outputType;
        }

        public String getDateFormat() {
            return dateFormat;
        }

        public String getTimestampDigits() {
            return timestampDigits;
        }

        public boolean isPadWithZero() {
            return padWithZero;
        }
    }

    /**
     * 时间戳转日期配置
     */
    public static class TimestampToDatetimeConfig {
        private final String timezoneId;
        private final String dateFormat;

        public TimestampToDatetimeConfig(String timezoneId, String dateFormat) {
            this.timezoneId = timezoneId;
            this.dateFormat = dateFormat;
        }

        public String getTimezoneId() {
            return timezoneId;
        }

        public String getDateFormat() {
            return dateFormat;
        }
    }

    /**
     * 日期转时间戳配置
     */
    public static class DatetimeToTimestampConfig {
        private final String inputFormat;
        private final String outputDigits;
        private final boolean padWithZero;

        public DatetimeToTimestampConfig(String inputFormat, String outputDigits, boolean padWithZero) {
            this.inputFormat = inputFormat;
            this.outputDigits = outputDigits;
            this.padWithZero = padWithZero;
        }

        public String getInputFormat() {
            return inputFormat;
        }

        public String getOutputDigits() {
            return outputDigits;
        }

        public boolean isPadWithZero() {
            return padWithZero;
        }
    }
}
