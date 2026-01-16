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
     * 检查操作是否需要图片输入 (二维码解析、图片尺寸转换、图片压缩)
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

        // 检查操作是否是需要图片输入的操作
        String className = operation.getClass().getSimpleName();
        return "QRCodeDecodeOperation".equals(className)
            || "ImageResizeOperation".equals(className)
            || "ImageCompressOperation".equals(className);
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

        // "获取当前时间"、"生成随机密码"、"生成身份证号码"、"生成空白图片" 可以在无输入时执行
        // 注意：图片尺寸转换和图片压缩通过 requiresImageInput 处理，不在无输入时执行
        return "获取当前时间".equals(operationName)
            || "生成随机密码".equals(operationName)
            || "生成身份证号码".equals(operationName)
            || "生成空白图片".equals(operationName);
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
     * 检查操作是否需要获取当前时间配置面板
     * @param operationName 操作名称
     * @return true 如果操作需要获取当前时间配置面板，否则返回 false
     */
    public boolean requiresGetCurrentTimeConfig(String operationName) {
        if (operationName == null || operationName.isEmpty()) {
            return false;
        }

        return "获取当前时间".equals(operationName);
    }

    /**
     * 检查操作是否需要时间戳转日期配置面板
     * @param operationName 操作名称
     * @return true 如果操作需要时间戳转日期配置面板，否则返回 false
     */
    public boolean requiresTimestampToDatetimeConfig(String operationName) {
        if (operationName == null || operationName.isEmpty()) {
            return false;
        }

        return "时间戳转日期".equals(operationName);
    }

    /**
     * 检查操作是否需要日期转时间戳配置面板
     * @param operationName 操作名称
     * @return true 如果操作需要日期转时间戳配置面板，否则返回 false
     */
    public boolean requiresDatetimeToTimestampConfig(String operationName) {
        if (operationName == null || operationName.isEmpty()) {
            return false;
        }

        return "日期转时间戳".equals(operationName);
    }

    /**
     * 检查操作是否需要身份证生成配置面板
     * @param operationName 操作名称
     * @return true 如果操作需要身份证生成配置面板，否则返回 false
     */
    public boolean requiresIdCardGenerateConfig(String operationName) {
        if (operationName == null || operationName.isEmpty()) {
            return false;
        }

        return "生成身份证号码".equals(operationName);
    }

    /**
     * 检查操作是否需要生成空白图片配置面板
     * @param operationName 操作名称
     * @return true 如果操作需要生成空白图片配置面板，否则返回 false
     */
    public boolean requiresCreateBlankImageConfig(String operationName) {
        if (operationName == null || operationName.isEmpty()) {
            return false;
        }

        return "生成空白图片".equals(operationName);
    }

    /**
     * 检查操作是否需要图片尺寸转换配置面板
     * @param operationName 操作名称
     * @return true 如果操作需要图片尺寸转换配置面板，否则返回 false
     */
    public boolean requiresImageResizeConfig(String operationName) {
        if (operationName == null || operationName.isEmpty()) {
            return false;
        }

        return "图片尺寸转换".equals(operationName);
    }

    /**
     * 检查操作是否需要图片压缩配置面板
     * @param operationName 操作名称
     * @return true 如果操作需要图片压缩配置面板，否则返回 false
     */
    public boolean requiresImageCompressConfig(String operationName) {
        if (operationName == null || operationName.isEmpty()) {
            return false;
        }

        return "图片压缩".equals(operationName);
    }

    /**
     * 检查操作是否需要文本输入按钮（粘贴、复制、清空等）
     * @param operationName 操作名称
     * @return true 如果操作需要文本输入按钮，否则返回 false
     */
    public boolean requiresTextInputButtons(String operationName) {
        if (operationName == null || operationName.isEmpty()) {
            return false;
        }

        // 不需要按钮的操作类型
        return !requiresImageInput(operationName)
            && !requiresGetCurrentTimeConfig(operationName)
            && !requiresTimestampToDatetimeConfig(operationName)
            && !requiresDatetimeToTimestampConfig(operationName)
            && !requiresBaseEncodingConfig(operationName)
            && !requiresPasswordGeneratorConfig(operationName)
            && !requiresIdCardGenerateConfig(operationName)
            && !requiresCreateBlankImageConfig(operationName)
            && !requiresImageResizeConfig(operationName)
            && !requiresImageCompressConfig(operationName)
            && !isAutomationOperation(operationName);
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
