package org.oxff.ui.controller;

import org.oxff.core.OperationCategory;
import org.oxff.core.OperationFactory;
import org.oxff.operation.Operation;

/**
 * 操作验证器，负责验证操作是否可以执行
 * 从 StringFormatterUI 中提取出来，提供无状态的工具方法
 */
public class OperationValidator {

    /**
     * 检查操作是否需要表达式输入 (XPath/JSONPath)
     * @param operationName 操作名称
     * @return true 如果操作需要表达式输入，否则返回 false
     */
    public boolean requiresExpressionInput(String operationName) {
        if (operationName == null || operationName.isEmpty()) {
            return false;
        }

        Operation operation = OperationFactory.getOperation(operationName);
        if (operation == null) {
            return false;
        }

        // 只有 JSON 和 XML 格式化操作需要表达式输入
        String className = operation.getClass().getSimpleName();
        return "JsonFormatOperation".equals(className) || "XmlFormatOperation".equals(className);
    }

    /**
     * 检查操作是否是自动化操作
     * @param operationName 操作名称
     * @return true 如果操作是自动化操作，否则返回 false
     */
    public boolean isAutomationOperation(String operationName) {
        if (operationName == null || operationName.isEmpty()) {
            return false;
        }

        Operation operation = OperationFactory.getOperation(operationName);
        if (operation == null) {
            return false;
        }

        // 检查操作是否属于 AUTOMATION 分类
        return operation.getCategory() == OperationCategory.AUTOMATION;
    }

    /**
     * 检查操作是否需要图片输入 (二维码解析)
     * @param operationName 操作名称
     * @return true 如果操作需要图片输入，否则返回 false
     */
    public boolean requiresImageInput(String operationName) {
        if (operationName == null || operationName.isEmpty()) {
            return false;
        }

        Operation operation = OperationFactory.getOperation(operationName);
        if (operation == null) {
            return false;
        }

        // 检查操作是否是二维码解析操作
        return "QRCodeDecodeOperation".equals(operation.getClass().getSimpleName());
    }

    /**
     * 检查操作是否可以在无输入时执行
     * @param operationName 操作名称
     * @return true 如果操作可以在无输入时执行，否则返回 false
     */
    public boolean canExecuteWithoutInput(String operationName) {
        if (operationName == null || operationName.isEmpty()) {
            return false;
        }

        // "获取当前时间戳" 和 "生成随机密码" 可以在无输入时执行
        return "获取当前时间戳".equals(operationName) || "生成随机密码".equals(operationName);
    }

    /**
     * 检查操作是否需要时区选择
     * @param operationName 操作名称
     * @return true 如果操作需要时区选择，否则返回 false
     */
    public boolean requiresTimezoneSelection(String operationName) {
        if (operationName == null || operationName.isEmpty()) {
            return false;
        }

        // "获取当前时间戳" 需要时区选择
        return "获取当前时间戳".equals(operationName);
    }

    /**
     * 检查操作是否需要Base编码配置面板
     * @param operationName 操作名称
     * @return true 如果操作需要Base编码配置面板，否则返回 false
     */
    public boolean requiresBaseEncodingConfig(String operationName) {
        if (operationName == null || operationName.isEmpty()) {
            return false;
        }

        Operation operation = OperationFactory.getOperation(operationName);
        if (operation == null) {
            return false;
        }

        return "ImageToBaseOperation".equals(operation.getClass().getSimpleName());
    }

    /**
     * 检查操作是否需要密码生成器配置面板
     * @param operationName 操作名称
     * @return true 如果操作需要密码生成器配置面板，否则返回 false
     */
    public boolean requiresPasswordGeneratorConfig(String operationName) {
        if (operationName == null || operationName.isEmpty()) {
            return false;
        }

        Operation operation = OperationFactory.getOperation(operationName);
        if (operation == null) {
            return false;
        }

        return "RandomPasswordOperation".equals(operation.getClass().getSimpleName());
    }

    /**
     * 验证执行条件
     * @param operationName 操作名称
     * @param inputText 输入文本
     * @return 验证结果，包含是否有效和错误消息
     */
    public ValidationResult validateExecution(String operationName, String inputText) {
        if (operationName == null || operationName.isEmpty()) {
            return new ValidationResult(false, "请选择一个操作");
        }

        Operation operation = OperationFactory.getOperation(operationName);
        if (operation == null) {
            return new ValidationResult(false, "未找到操作: " + operationName);
        }

        // 对于自动化操作，不需要输入文本验证
        if (operation.getCategory() == OperationCategory.AUTOMATION) {
            return new ValidationResult(true, null);
        }

        // 检查是否可以在无输入时执行
        if (!canExecuteWithoutInput(operationName) && inputText.isEmpty()) {
            return new ValidationResult(false, "请输入要处理的文本");
        }

        return new ValidationResult(true, null);
    }

    /**
     * 验证结果
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;

        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
