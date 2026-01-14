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

    private OperationExecutionContext(Builder builder) {
        this.operationName = builder.operationName;
        this.inputText = builder.inputText;
        this.expressions = builder.expressions;
        this.automationConfig = builder.automationConfig;
        this.timezoneSelection = builder.timezoneSelection;
        this.imagePath = builder.imagePath;
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
}
