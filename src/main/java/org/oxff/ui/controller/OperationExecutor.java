package org.oxff.ui.controller;

import org.oxff.core.OperationFactory;
import org.oxff.operation.Operation;

import java.lang.reflect.Method;

/**
 * 操作执行器，负责执行操作的核心逻辑
 * 从 StringFormatterUI 中提取出来，封装操作执行的复杂逻辑
 */
public class OperationExecutor {
    private final OperationValidator validator;

    public OperationExecutor(OperationValidator validator) {
        this.validator = validator;
    }

    /**
     * 执行操作
     * @param context 操作执行上下文
     * @return 执行结果
     * @throws Exception 执行失败时抛出异常
     */
    public ExecutionResult execute(OperationExecutionContext context) throws Exception {
        String operationName = context.getOperationName();

        // 验证操作
        OperationValidator.ValidationResult validation = validator.validateExecution(
            operationName, context.getInputText());
        if (!validation.isValid()) {
            throw new IllegalArgumentException(validation.getErrorMessage());
        }

        // 获取操作实例
        Operation operation = OperationFactory.getOperation(operationName);
        if (operation == null) {
            throw new IllegalArgumentException("未找到操作: " + operationName);
        }

        // 准备输入文本
        String inputText = prepareInputForOperation(context);

        // 配置自动化操作
        if (validator.isAutomationOperation(operationName) && context.getAutomationConfig() != null) {
            handleAutomationConfig(operation, context.getAutomationConfig());
        }

        long startTime = System.currentTimeMillis();
        String result;

        // 检查是否是返回图片的操作
        if (operation.returnsImage()) {
            String imageData = operation.getImageData(inputText);
            long endTime = System.currentTimeMillis();
            return new ExecutionResult(imageData, true, endTime - startTime);
        }

        // 对于XML和JSON格式化操作，如果有表达式输入，使用特殊处理
        if (!context.getExpressions().isEmpty()) {
            result = executeWithExpression(operation, inputText, context.getExpressions());
        } else {
            result = operation.execute(inputText);
        }

        long endTime = System.currentTimeMillis();
        return new ExecutionResult(result, false, endTime - startTime,
            !context.getExpressions().isEmpty());
    }

    /**
     * 准备操作输入
     * @param context 操作执行上下文
     * @return 准备好的输入文本
     */
    public String prepareInputForOperation(OperationExecutionContext context) {
        String operationName = context.getOperationName();
        String inputText = context.getInputText();

        // 对于二维码解析操作，使用图片文件路径
        if (validator.requiresImageInput(operationName)) {
            if (context.getImagePath() != null && !context.getImagePath().isEmpty()) {
                return context.getImagePath();
            }
            // 否则使用输入框中的内容（可能是Base64编码的图片）
            return inputText;
        }

        // 对于"获取当前时间戳"操作，使用时区选择器的值
        if (validator.requiresTimezoneSelection(operationName)) {
            return context.getTimezoneSelection() != null ? context.getTimezoneSelection() : "";
        }

        return inputText;
    }

    /**
     * 处理自动化操作配置
     * @param operation 操作对象
     * @param config 自动化配置
     * @throws Exception 反射调用失败时抛出异常
     */
    public void handleAutomationConfig(Operation operation, OperationExecutionContext.AutomationConfig config)
            throws Exception {
        try {
            Method setDelayMethod = operation.getClass().getMethod("setDelaySeconds", int.class);
            Method setIntervalMethod = operation.getClass().getMethod("setCharIntervalMs", int.class);
            Method setClipboardMethod = operation.getClass().getMethod("setUseClipboard", boolean.class);

            setDelayMethod.invoke(operation, config.getDelaySeconds());
            setIntervalMethod.invoke(operation, config.getCharIntervalMs());
            setClipboardMethod.invoke(operation, config.isUseClipboard());
        } catch (Exception e) {
            throw new Exception("设置自动化配置失败: " + e.getMessage(), e);
        }
    }

    /**
     * 带表达式执行操作
     * @param operation 操作对象
     * @param input 输入文本
     * @param expressions 表达式
     * @return 执行结果
     * @throws Exception 执行失败时抛出异常
     */
    private String executeWithExpression(Operation operation, String input, String expressions)
            throws Exception {
        String className = operation.getClass().getSimpleName();

        // 检查是否支持表达式执行
        if ("XmlFormatOperation".equals(className) || "JsonFormatOperation".equals(className)) {
            try {
                Method method = operation.getClass().getMethod("execute", String.class, String.class);
                return (String) method.invoke(operation, input, expressions);
            } catch (NoSuchMethodException e) {
                // 如果没有带表达式的方法，使用原始方法
                return operation.execute(input);
            }
        }

        // 其他操作直接执行
        return operation.execute(input);
    }

    /**
     * 执行结果
     */
    public static class ExecutionResult {
        private final String result;
        private final boolean isImage;
        private final long executionTimeMs;
        private final boolean usedExpressions;

        public ExecutionResult(String result, boolean isImage, long executionTimeMs) {
            this(result, isImage, executionTimeMs, false);
        }

        public ExecutionResult(String result, boolean isImage, long executionTimeMs, boolean usedExpressions) {
            this.result = result;
            this.isImage = isImage;
            this.executionTimeMs = executionTimeMs;
            this.usedExpressions = usedExpressions;
        }

        public String getResult() {
            return result;
        }

        public boolean isImage() {
            return isImage;
        }

        public long getExecutionTimeMs() {
            return executionTimeMs;
        }

        public boolean usedExpressions() {
            return usedExpressions;
        }
    }
}
