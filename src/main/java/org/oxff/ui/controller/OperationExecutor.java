package org.oxff.ui.controller;

import org.oxff.core.OperationFactory;
import org.oxff.operation.Operation;
import org.oxff.ui.components.UIComponentRegistry;

import javax.swing.*;
import java.awt.Color;
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
     * @param registry UI组件注册表
     * @return 执行结果
     * @throws Exception 执行失败时抛出异常
     */
    public ExecutionResult execute(OperationExecutionContext context, UIComponentRegistry registry) throws Exception {
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
        String inputText = prepareInputForOperation(context, registry);

        // 配置自动化操作
        if (validator.isAutomationOperation(operationName) && context.getAutomationConfig() != null) {
            handleAutomationConfig(operation, context.getAutomationConfig());
        }

        // 配置Base编码操作
        if (validator.requiresBaseEncodingConfig(operationName) && context.getBaseEncodingConfig() != null) {
            handleBaseEncodingConfig(operation, context.getBaseEncodingConfig());
        }

        // 配置密码生成器操作
        if (validator.requiresPasswordGeneratorConfig(operationName) && context.getPasswordGeneratorConfig() != null) {
            handlePasswordGeneratorConfig(operation, context.getPasswordGeneratorConfig());
        }

        // 配置获取当前时间操作
        if (validator.requiresGetCurrentTimeConfig(operationName) && context.getGetCurrentTimeConfig() != null) {
            handleGetCurrentTimeConfig(operation, context.getGetCurrentTimeConfig());
        }

        // 配置时间戳转日期操作
        if (validator.requiresTimestampToDatetimeConfig(operationName) && context.getTimestampToDatetimeConfig() != null) {
            handleTimestampToDatetimeConfig(operation, context.getTimestampToDatetimeConfig());
        }

        // 配置日期转时间戳操作
        if (validator.requiresDatetimeToTimestampConfig(operationName) && context.getDatetimeToTimestampConfig() != null) {
            handleDatetimeToTimestampConfig(operation, context.getDatetimeToTimestampConfig());
        }

        // 配置身份证生成操作
        if (validator.requiresIdCardGenerateConfig(operationName) && registry.hasComponent(UIComponentRegistry.ID_CARD_PROVINCE_COMBO_BOX)) {
            handleIdCardGenerateConfig(operation, registry);
        }

        // 配置生成空白图片操作
        if (validator.requiresCreateBlankImageConfig(operationName) && registry.hasComponent(UIComponentRegistry.BLANK_IMAGE_WIDTH_SPINNER)) {
            handleCreateBlankImageConfig(operation, registry);
        }

        // 配置图片尺寸转换操作
        if (validator.requiresImageResizeConfig(operationName) && registry.hasComponent(UIComponentRegistry.IMAGE_RESIZE_WIDTH_SPINNER)) {
            handleImageResizeConfig(operation, registry);
        }

        // 配置图片压缩操作
        if (validator.requiresImageCompressConfig(operationName) && registry.hasComponent(UIComponentRegistry.IMAGE_COMPRESS_LEVEL_SLIDER)) {
            handleImageCompressConfig(operation, registry);
        }

        long startTime = System.currentTimeMillis();
        String result;

        // 检查是否是返回图片的操作
        if (operation.returnsImage()) {
            String imageData = operation.getImageData(inputText);
            long endTime = System.currentTimeMillis();

            // 对于图片压缩操作，尝试获取对比信息
            String additionalInfo = null;
            if ("图片压缩".equals(operationName) && operation instanceof org.oxff.operation.imagetools.ImageCompressOperation) {
                org.oxff.operation.imagetools.ImageCompressOperation compressOp =
                    (org.oxff.operation.imagetools.ImageCompressOperation) operation;
                additionalInfo = compressOp.getLastComparisonInfo();
            }

            return new ExecutionResult(imageData, true, endTime - startTime, false, additionalInfo);
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
     * @param registry UI组件注册表
     * @return 准备好的输入文本
     */
    public String prepareInputForOperation(OperationExecutionContext context, UIComponentRegistry registry) {
        String operationName = context.getOperationName();
        String inputText = context.getInputText();

        // 对于二维码解析操作，使用图片文件路径
        if (validator.requiresImageInput(operationName)) {
            // 对于图片尺寸转换操作
            if ("图片尺寸转换".equals(operationName)) {
                JLabel pathLabel = registry.getComponent("imageResizeSelectedPath");
                if (pathLabel != null) {
                    String path = pathLabel.getText();
                    if (path != null && !path.isEmpty()) {
                        return path;
                    }
                }
            }
            // 对于图片压缩操作
            else if ("图片压缩".equals(operationName)) {
                JLabel pathLabel = registry.getComponent("imageCompressSelectedPath");
                if (pathLabel != null) {
                    String path = pathLabel.getText();
                    if (path != null && !path.isEmpty()) {
                        return path;
                    }
                }
            }
            // 对于二维码解析操作，使用context.getImagePath()
            if (context.getImagePath() != null && !context.getImagePath().isEmpty()) {
                return context.getImagePath();
            }
            // 否则使用输入框中的内容（可能是Base64编码的图片）
            return inputText;
        }

        // 对于Base编码配置操作，使用图片文件路径
        if (validator.requiresBaseEncodingConfig(operationName)) {
            if (context.getImagePath() != null && !context.getImagePath().isEmpty()) {
                return context.getImagePath();
            }
            // 否则使用输入框中的内容
            return inputText;
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
     * 处理Base编码配置
     * @param operation 操作对象
     * @param config Base编码配置
     * @throws Exception 反射调用失败时抛出异常
     */
    public void handleBaseEncodingConfig(Operation operation, OperationExecutionContext.BaseEncodingConfig config)
            throws Exception {
        try {
            Method method = operation.getClass().getMethod("setEncodingType", String.class);
            method.invoke(operation, config.getEncodingType());
        } catch (Exception e) {
            throw new Exception("设置Base编码配置失败: " + e.getMessage(), e);
        }
    }

    /**
     * 处理密码生成器配置
     * @param operation 操作对象
     * @param config 密码生成器配置
     * @throws Exception 反射调用失败时抛出异常
     */
    public void handlePasswordGeneratorConfig(Operation operation,
                                               OperationExecutionContext.PasswordGeneratorConfig config)
            throws Exception {
        try {
            Method setLengthMethod = operation.getClass().getMethod("setPasswordLength", int.class);
            Method setIncludeDigitsMethod = operation.getClass().getMethod("setIncludeDigits", boolean.class);
            Method setDigitCountMethod = operation.getClass().getMethod("setDigitCount", int.class);
            Method setIncludeUppercaseMethod = operation.getClass().getMethod("setIncludeUppercase", boolean.class);
            Method setUppercaseCountMethod = operation.getClass().getMethod("setUppercaseCount", int.class);
            Method setIncludeLowercaseMethod = operation.getClass().getMethod("setIncludeLowercase", boolean.class);
            Method setLowercaseCountMethod = operation.getClass().getMethod("setLowercaseCount", int.class);
            Method setIncludeSpecialMethod = operation.getClass().getMethod("setIncludeSpecialChars", boolean.class);
            Method setSpecialCountMethod = operation.getClass().getMethod("setSpecialCharCount", int.class);
            Method setCountMethod = operation.getClass().getMethod("setPasswordCount", int.class);

            setLengthMethod.invoke(operation, config.getPasswordLength());
            setIncludeDigitsMethod.invoke(operation, config.isIncludeDigits());
            setDigitCountMethod.invoke(operation, config.getDigitCount());
            setIncludeUppercaseMethod.invoke(operation, config.isIncludeUppercase());
            setUppercaseCountMethod.invoke(operation, config.getUppercaseCount());
            setIncludeLowercaseMethod.invoke(operation, config.isIncludeLowercase());
            setLowercaseCountMethod.invoke(operation, config.getLowercaseCount());
            setIncludeSpecialMethod.invoke(operation, config.isIncludeSpecialChars());
            setSpecialCountMethod.invoke(operation, config.getSpecialCharCount());
            setCountMethod.invoke(operation, config.getPasswordCount());
        } catch (Exception e) {
            throw new Exception("设置密码生成器配置失败: " + e.getMessage(), e);
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
     * 处理获取当前时间配置
     * @param operation 操作对象
     * @param config 获取当前时间配置
     * @throws Exception 反射调用失败时抛出异常
     */
    public void handleGetCurrentTimeConfig(Operation operation,
                                           OperationExecutionContext.GetCurrentTimeConfig config)
            throws Exception {
        try {
            Method method = operation.getClass().getMethod("setConfig",
                String.class, String.class, String.class, String.class, boolean.class);
            method.invoke(operation,
                config.getTimezoneId(), config.getOutputType(), config.getDateFormat(),
                config.getTimestampDigits(), config.isPadWithZero());
        } catch (Exception e) {
            throw new Exception("设置获取当前时间配置失败: " + e.getMessage(), e);
        }
    }

    /**
     * 处理时间戳转日期配置
     * @param operation 操作对象
     * @param config 时间戳转日期配置
     * @throws Exception 反射调用失败时抛出异常
     */
    public void handleTimestampToDatetimeConfig(Operation operation,
                                                 OperationExecutionContext.TimestampToDatetimeConfig config)
            throws Exception {
        try {
            Method method = operation.getClass().getMethod("setConfig", String.class, String.class);
            method.invoke(operation, config.getTimezoneId(), config.getDateFormat());
        } catch (Exception e) {
            throw new Exception("设置时间戳转日期配置失败: " + e.getMessage(), e);
        }
    }

    /**
     * 处理日期转时间戳配置
     * @param operation 操作对象
     * @param config 日期转时间戳配置
     * @throws Exception 反射调用失败时抛出异常
     */
    public void handleDatetimeToTimestampConfig(Operation operation,
                                                 OperationExecutionContext.DatetimeToTimestampConfig config)
            throws Exception {
        try {
            Method method = operation.getClass().getMethod("setConfig", String.class, String.class, boolean.class);
            method.invoke(operation, config.getInputFormat(), config.getOutputDigits(), config.isPadWithZero());
        } catch (Exception e) {
            throw new Exception("设置日期转时间戳配置失败: " + e.getMessage(), e);
        }
    }

    /**
     * 处理身份证生成配置
     * @param operation 操作对象
     * @param registry UI组件注册表
     * @throws Exception 反射调用失败时抛出异常
     */
    public void handleIdCardGenerateConfig(Operation operation, UIComponentRegistry registry)
            throws Exception {
        try {
            // 获取省份代码
            JComboBox<?> provinceCombo = registry.getComponent(UIComponentRegistry.ID_CARD_PROVINCE_COMBO_BOX);
            JComboBox<?> cityCombo = registry.getComponent(UIComponentRegistry.ID_CARD_CITY_COMBO_BOX);
            JRadioButton ageRadio = registry.getComponent(UIComponentRegistry.ID_CARD_AGE_RADIO);
            JSpinner ageSpinner = registry.getComponent(UIComponentRegistry.ID_CARD_AGE_SPINNER);
            JRadioButton maleRadio = registry.getComponent(UIComponentRegistry.ID_CARD_GENDER_MALE_RADIO);
            JRadioButton femaleRadio = registry.getComponent(UIComponentRegistry.ID_CARD_GENDER_FEMALE_RADIO);
            JSpinner countSpinner = registry.getComponent(UIComponentRegistry.ID_CARD_COUNT_SPINNER);

            String provinceCode = null;
            String selectedProvince = (String) provinceCombo.getSelectedItem();
            if (!"全部".equals(selectedProvince)) {
                provinceCode = org.oxff.util.IdCardUtils.getProvinceCode(selectedProvince);
            }

            String cityCode = null;
            String selectedCity = (String) cityCombo.getSelectedItem();
            if (!"全部".equals(selectedCity)) {
                cityCode = org.oxff.util.IdCardUtils.getCityCode(provinceCode, selectedCity);
            }

            String birthDate = null;
            int age = 0;
            if (ageRadio.isSelected()) {
                age = (Integer) ageSpinner.getValue();
            } else {
                // 获取出生日期
                JSpinner yearSpinner = registry.getComponent("idCardBirthDateYearSpinner");
                JSpinner monthSpinner = registry.getComponent("idCardBirthDateMonthSpinner");
                JSpinner daySpinner = registry.getComponent("idCardBirthDateDaySpinner");
                birthDate = String.format("%d-%02d-%02d",
                    yearSpinner.getValue(), monthSpinner.getValue(), daySpinner.getValue());
            }

            String gender = null;
            if (maleRadio.isSelected()) {
                gender = "M";
            } else if (femaleRadio.isSelected()) {
                gender = "F";
            }

            int count = (Integer) countSpinner.getValue();

            // 调用setter方法
            if (provinceCode != null) {
                Method method = operation.getClass().getMethod("setProvinceCode", String.class);
                method.invoke(operation, provinceCode);
            }
            if (cityCode != null) {
                Method method = operation.getClass().getMethod("setCityCode", String.class);
                method.invoke(operation, cityCode);
            }
            if (age > 0) {
                Method method = operation.getClass().getMethod("setAge", int.class);
                method.invoke(operation, age);
            }
            if (birthDate != null) {
                Method method = operation.getClass().getMethod("setBirthDate", String.class);
                method.invoke(operation, birthDate);
            }
            if (gender != null) {
                Method method = operation.getClass().getMethod("setGender", String.class);
                method.invoke(operation, gender);
            }
            Method method = operation.getClass().getMethod("setCount", int.class);
            method.invoke(operation, count);

        } catch (Exception e) {
            throw new Exception("设置身份证生成配置失败: " + e.getMessage(), e);
        }
    }

    /**
     * 处理生成空白图片配置
     * @param operation 操作对象
     * @param registry UI组件注册表
     * @throws Exception 反射调用失败时抛出异常
     */
    public void handleCreateBlankImageConfig(Operation operation, UIComponentRegistry registry)
            throws Exception {
        try {
            JRadioButton customSizeRadio = registry.getComponent("blankImageCustomSizeRadio");
            JRadioButton presetSizeRadio = registry.getComponent("blankImagePresetSizeRadio");
            JSpinner widthSpinner = registry.getComponent(UIComponentRegistry.BLANK_IMAGE_WIDTH_SPINNER);
            JSpinner heightSpinner = registry.getComponent(UIComponentRegistry.BLANK_IMAGE_HEIGHT_SPINNER);
            JComboBox<?> sizeSpecCombo = registry.getComponent(UIComponentRegistry.BLANK_IMAGE_SIZE_SPEC_COMBO);
            JLabel colorPreview = registry.getComponent(UIComponentRegistry.BLANK_IMAGE_BG_COLOR_PREVIEW);
            JRadioButton pngRadio = registry.getComponent(UIComponentRegistry.BLANK_IMAGE_FORMAT_PNG_RADIO);
            JRadioButton jpegRadio = registry.getComponent(UIComponentRegistry.BLANK_IMAGE_FORMAT_JPEG_RADIO);

            int width = (Integer) widthSpinner.getValue();
            int height = (Integer) heightSpinner.getValue();
            String sizeSpec = presetSizeRadio.isSelected() ? (String) sizeSpecCombo.getSelectedItem() : null;
            Color bgColor = colorPreview.getBackground();
            String backgroundColor = String.format("%02X%02X%02X", bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue());
            String format = pngRadio.isSelected() ? "PNG" : "JPEG";

            // 调用setter方法
            Method setWidthMethod = operation.getClass().getMethod("setWidth", int.class);
            setWidthMethod.invoke(operation, width);

            Method setHeightMethod = operation.getClass().getMethod("setHeight", int.class);
            setHeightMethod.invoke(operation, height);

            Method setSizeSpecMethod = operation.getClass().getMethod("setSizeSpec", String.class);
            setSizeSpecMethod.invoke(operation, sizeSpec != null ? sizeSpec : "");

            Method setBackgroundColorMethod = operation.getClass().getMethod("setBackgroundColor", String.class);
            setBackgroundColorMethod.invoke(operation, backgroundColor);

            Method setFormatMethod = operation.getClass().getMethod("setFormat", String.class);
            setFormatMethod.invoke(operation, format);

        } catch (Exception e) {
            throw new Exception("设置生成空白图片配置失败: " + e.getMessage(), e);
        }
    }

    /**
     * 处理图片尺寸转换配置
     * @param operation 操作对象
     * @param registry UI组件注册表
     * @throws Exception 反射调用失败时抛出异常
     */
    public void handleImageResizeConfig(Operation operation, UIComponentRegistry registry)
            throws Exception {
        try {
            JRadioButton customSizeRadio = registry.getComponent("imageResizeCustomSizeRadio");
            JRadioButton presetSizeRadio = registry.getComponent("imageResizePresetSizeRadio");
            JSpinner widthSpinner = registry.getComponent(UIComponentRegistry.IMAGE_RESIZE_WIDTH_SPINNER);
            JSpinner heightSpinner = registry.getComponent(UIComponentRegistry.IMAGE_RESIZE_HEIGHT_SPINNER);
            JComboBox<?> sizeSpecCombo = registry.getComponent(UIComponentRegistry.IMAGE_RESIZE_SIZE_SPEC_COMBO);
            JCheckBox maintainRatioCheck = registry.getComponent(UIComponentRegistry.IMAGE_RESIZE_MAINTAIN_RATIO_CHECK);
            JLabel sourceLabel = registry.getComponent(UIComponentRegistry.IMAGE_RESIZE_SOURCE_LABEL);

            int width = (Integer) widthSpinner.getValue();
            int height = (Integer) heightSpinner.getValue();
            String sizeSpec = presetSizeRadio.isSelected() ? (String) sizeSpecCombo.getSelectedItem() : null;
            boolean maintainRatio = maintainRatioCheck.isSelected();

            // 如果没有指定尺寸，使用0值
            if (presetSizeRadio.isSelected()) {
                width = 0;
                height = 0;
            }

            // 调用setter方法
            Method setWidthMethod = operation.getClass().getMethod("setWidth", int.class);
            setWidthMethod.invoke(operation, width);

            Method setHeightMethod = operation.getClass().getMethod("setHeight", int.class);
            setHeightMethod.invoke(operation, height);

            Method setSizeSpecMethod = operation.getClass().getMethod("setSizeSpec", String.class);
            setSizeSpecMethod.invoke(operation, sizeSpec != null ? sizeSpec : "");

            Method setMaintainRatioMethod = operation.getClass().getMethod("setMaintainRatio", boolean.class);
            setMaintainRatioMethod.invoke(operation, maintainRatio);

        } catch (Exception e) {
            throw new Exception("设置图片尺寸转换配置失败: " + e.getMessage(), e);
        }
    }

    /**
     * 处理图片压缩配置
     * @param operation 操作对象
     * @param registry UI组件注册表
     * @throws Exception 反射调用失败时抛出异常
     */
    public void handleImageCompressConfig(Operation operation, UIComponentRegistry registry)
            throws Exception {
        try {
            JRadioButton jpegRadio = registry.getComponent(UIComponentRegistry.IMAGE_COMPRESS_FORMAT_JPEG_RADIO);
            JRadioButton pngRadio = registry.getComponent(UIComponentRegistry.IMAGE_COMPRESS_FORMAT_PNG_RADIO);
            JSlider levelSlider = registry.getComponent(UIComponentRegistry.IMAGE_COMPRESS_LEVEL_SLIDER);
            JCheckBox showComparisonCheck = registry.getComponent(UIComponentRegistry.IMAGE_COMPRESS_SHOW_COMPARISON_CHECK);

            // 调试：检查组件是否为null
            if (jpegRadio == null) throw new Exception("JPEG单选框为null");
            if (pngRadio == null) throw new Exception("PNG单选框为null");
            if (levelSlider == null) throw new Exception("压缩级别滑块为null");
            if (showComparisonCheck == null) throw new Exception("显示对比复选框为null");

            // 压缩强度滑块：0-100，值越大=压缩越厉害=质量越低
            // 转换为JPEG质量参数：0.0-1.0，值越大=质量越高
            int compressionStrength = levelSlider.getValue(); // 0-100，压缩强度
            float quality = 1.0f - (compressionStrength / 100.0f); // 转换为质量参数

            String format = jpegRadio.isSelected() ? "JPEG" : "PNG";
            boolean showComparison = showComparisonCheck.isSelected();

            // 调用setter方法
            Method setCompressionLevelMethod = operation.getClass().getMethod("setCompressionLevel", float.class);
            setCompressionLevelMethod.invoke(operation, quality);

            Method setFormatMethod = operation.getClass().getMethod("setFormat", String.class);
            setFormatMethod.invoke(operation, format);

            Method setShowComparisonMethod = operation.getClass().getMethod("setShowComparison", boolean.class);
            setShowComparisonMethod.invoke(operation, showComparison);

        } catch (Exception e) {
            throw new Exception("设置图片压缩配置失败: " + e.getMessage(), e);
        }
    }

    public static class ExecutionResult {
        private final String result;
        private final boolean isImage;
        private final long executionTimeMs;
        private final boolean usedExpressions;
        private final String additionalInfo;  // 额外信息（如压缩对比信息）

        public ExecutionResult(String result, boolean isImage, long executionTimeMs) {
            this(result, isImage, executionTimeMs, false, null);
        }

        public ExecutionResult(String result, boolean isImage, long executionTimeMs, boolean usedExpressions) {
            this(result, isImage, executionTimeMs, usedExpressions, null);
        }

        public ExecutionResult(String result, boolean isImage, long executionTimeMs, boolean usedExpressions, String additionalInfo) {
            this.result = result;
            this.isImage = isImage;
            this.executionTimeMs = executionTimeMs;
            this.usedExpressions = usedExpressions;
            this.additionalInfo = additionalInfo;
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

        public String getAdditionalInfo() {
            return additionalInfo;
        }

        public boolean hasAdditionalInfo() {
            return additionalInfo != null && !additionalInfo.isEmpty();
        }
    }
}
