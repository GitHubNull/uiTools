package org.oxff.ui.handler;

import org.oxff.ui.components.UIComponentRegistry;
import org.oxff.ui.controller.*;
import org.oxff.ui.image.ImageDisplayManager;
import org.oxff.ui.image.ImageFileManager;
import org.oxff.ui.util.LogManager;
import org.oxff.core.OperationCategory;
import org.oxff.core.OperationFactory;
import org.oxff.operation.Operation;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * 事件处理器，处理所有UI事件
 * 从 StringFormatterUI 中提取出来，封装事件处理逻辑
 */
public class EventHandler {
    private final UIComponentRegistry registry;
    private final LogManager logManager;
    private final ClipboardManager clipboardManager;
    private final OperationExecutor operationExecutor;
    private final OperationValidator operationValidator;
    private final UIStateManager uiStateManager;
    private final ImageDisplayManager imageDisplayManager;
    private final ImageFileManager imageFileManager;
    private final ExecuteCallback executeCallback;

    private String selectedOperation;
    private String selectedImagePath;

    public EventHandler(UIComponentRegistry registry,
                        LogManager logManager,
                        ClipboardManager clipboardManager,
                        OperationExecutor operationExecutor,
                        OperationValidator operationValidator,
                        UIStateManager uiStateManager,
                        ImageDisplayManager imageDisplayManager,
                        ImageFileManager imageFileManager,
                        ExecuteCallback executeCallback) {
        this.registry = registry;
        this.logManager = logManager;
        this.clipboardManager = clipboardManager;
        this.operationExecutor = operationExecutor;
        this.operationValidator = operationValidator;
        this.uiStateManager = uiStateManager;
        this.imageDisplayManager = imageDisplayManager;
        this.imageFileManager = imageFileManager;
        this.executeCallback = executeCallback;
    }

    /**
     * 处理执行按钮事件
     * @param parent 父组件，用于对话框
     */
    public void handleExecute(Component parent) {
        String inputText = registry.getInputTextArea().getText();
        String expressions = registry.getExpressionTextArea().getText().trim();

        // 对于需要图片输入的操作，先获取图片路径作为输入文本
        if (operationValidator.requiresImageInput(selectedOperation)) {
            if (selectedImagePath != null && !selectedImagePath.isEmpty()) {
                inputText = selectedImagePath;
            } else {
                JOptionPane.showMessageDialog(parent, "请选择二维码图片文件或使用粘贴图片功能",
                    "提示", JOptionPane.WARNING_MESSAGE);
                logManager.log("执行操作失败：未选择图片");
                return;
            }
        }

        // 对于时间戳转日期操作，从单行输入框获取输入
        if (operationValidator.requiresTimestampToDatetimeConfig(selectedOperation)) {
            JTextField inputField = registry.getComponent(UIComponentRegistry.TIMESTAMP_TO_DATETIME_INPUT_FIELD);
            if (inputField != null) {
                inputText = inputField.getText().trim();
            }
            if (inputText.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "请输入时间戳",
                    "提示", JOptionPane.WARNING_MESSAGE);
                logManager.log("执行操作失败：未输入时间戳");
                return;
            }
        }

        // 对于日期转时间戳操作，从单行输入框获取输入
        if (operationValidator.requiresDatetimeToTimestampConfig(selectedOperation)) {
            JTextField inputField = registry.getComponent(UIComponentRegistry.DATETIME_TO_TIMESTAMP_INPUT_FIELD);
            if (inputField != null) {
                inputText = inputField.getText().trim();
            }
            if (inputText.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "请输入日期时间",
                    "提示", JOptionPane.WARNING_MESSAGE);
                logManager.log("执行操作失败：未输入日期时间");
                return;
            }
        }

        // 对于Base编码操作，先获取图片路径作为输入文本
        if (operationValidator.requiresBaseEncodingConfig(selectedOperation)) {
            if (selectedImagePath != null && !selectedImagePath.isEmpty()) {
                inputText = selectedImagePath;
            } else {
                JOptionPane.showMessageDialog(parent, "请选择图片文件",
                    "提示", JOptionPane.WARNING_MESSAGE);
                logManager.log("执行操作失败：未选择文件");
                return;
            }
        }

        // 验证操作
        OperationValidator.ValidationResult validation =
            operationValidator.validateExecution(selectedOperation, inputText);
        if (!validation.isValid()) {
            JOptionPane.showMessageDialog(parent, validation.getErrorMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            if (validation.getErrorMessage().contains("未找到操作")) {
                logManager.log("未找到操作: " + selectedOperation);
            }
            return;
        }

        Operation operation = OperationFactory.getOperation(selectedOperation);

        // 构建执行上下文
        OperationExecutionContext.Builder builder = new OperationExecutionContext.Builder()
            .operationName(selectedOperation)
            .inputText(inputText)
            .expressions(expressions)
            .imagePath(selectedImagePath);

        // 处理自动化操作配置
        if (operationValidator.isAutomationOperation(selectedOperation)) {
            int delaySeconds = (Integer) registry.getDelaySecondsSpinner().getValue();
            int charIntervalMs = (Integer) registry.getCharIntervalMsSpinner().getValue();
            boolean useClipboard = registry.getClipboardSourceRadio().isSelected();
            builder.automationConfig(new OperationExecutionContext.AutomationConfig(
                delaySeconds, charIntervalMs, useClipboard));
        }

        // 处理Base编码配置
        if (operationValidator.requiresBaseEncodingConfig(selectedOperation)) {
            @SuppressWarnings("unchecked")
            JComboBox<String> encodingTypeComboBox = registry.getComponent(UIComponentRegistry.BASE_ENCODING_COMBO_BOX);
            String encodingType = (String) encodingTypeComboBox.getSelectedItem();
            String filePath = selectedImagePath;
            builder.baseEncodingConfig(new OperationExecutionContext.BaseEncodingConfig(encodingType));
            builder.imagePath(filePath);
        }

        // 处理密码生成器配置
        if (operationValidator.requiresPasswordGeneratorConfig(selectedOperation)) {
            JSpinner passwordLengthSpinner = registry.getComponent(UIComponentRegistry.PASSWORD_LENGTH_SPINNER);
            JCheckBox includeDigitsCheckBox = registry.getComponent(UIComponentRegistry.INCLUDE_DIGITS_CHECK_BOX);
            JSpinner digitCountSpinner = registry.getComponent(UIComponentRegistry.DIGIT_COUNT_SPINNER);
            JCheckBox includeUppercaseCheckBox = registry.getComponent(UIComponentRegistry.INCLUDE_UPPERCASE_CHECK_BOX);
            JSpinner uppercaseCountSpinner = registry.getComponent(UIComponentRegistry.UPPERCASE_COUNT_SPINNER);
            JCheckBox includeLowercaseCheckBox = registry.getComponent(UIComponentRegistry.INCLUDE_LOWERCASE_CHECK_BOX);
            JSpinner lowercaseCountSpinner = registry.getComponent(UIComponentRegistry.LOWERCASE_COUNT_SPINNER);
            JCheckBox includeSpecialCharsCheckBox = registry.getComponent(UIComponentRegistry.INCLUDE_SPECIAL_CHARS_CHECK_BOX);
            JSpinner specialCharCountSpinner = registry.getComponent(UIComponentRegistry.SPECIAL_CHAR_COUNT_SPINNER);
            JSpinner passwordCountSpinner = registry.getComponent(UIComponentRegistry.PASSWORD_COUNT_SPINNER);

            int passwordLength = (Integer) passwordLengthSpinner.getValue();
            boolean includeDigits = includeDigitsCheckBox.isSelected();
            int digitCount = (Integer) digitCountSpinner.getValue();
            boolean includeUppercase = includeUppercaseCheckBox.isSelected();
            int uppercaseCount = (Integer) uppercaseCountSpinner.getValue();
            boolean includeLowercase = includeLowercaseCheckBox.isSelected();
            int lowercaseCount = (Integer) lowercaseCountSpinner.getValue();
            boolean includeSpecialChars = includeSpecialCharsCheckBox.isSelected();
            int specialCharCount = (Integer) specialCharCountSpinner.getValue();
            int passwordCount = (Integer) passwordCountSpinner.getValue();

            builder.passwordGeneratorConfig(new OperationExecutionContext.PasswordGeneratorConfig(
                passwordLength, includeDigits, digitCount, includeUppercase, uppercaseCount,
                includeLowercase, lowercaseCount, includeSpecialChars, specialCharCount, passwordCount));
        }

        // 处理获取当前时间配置
        if (operationValidator.requiresGetCurrentTimeConfig(selectedOperation)) {
            JComboBox<String> timezoneComboBox = registry.getComponent(UIComponentRegistry.GET_CURRENT_TIMEZONE_COMBO_BOX);
            JRadioButton datetimeRadio = registry.getComponent(UIComponentRegistry.GET_CURRENT_DATETIME_RADIO);
            JComboBox<String> dateFormatComboBox = registry.getComponent(UIComponentRegistry.GET_CURRENT_DATE_FORMAT_COMBO_BOX);
            JTextField dateFormatTextField = registry.getComponent(UIComponentRegistry.GET_CURRENT_DATE_FORMAT_TEXT_FIELD);
            JRadioButton digits10Radio = registry.getComponent(UIComponentRegistry.GET_CURRENT_10_DIGITS_RADIO);
            JRadioButton digits13Radio = registry.getComponent(UIComponentRegistry.GET_CURRENT_13_DIGITS_RADIO);
            JCheckBox padWithZeroCheckBox = registry.getComponent(UIComponentRegistry.GET_CURRENT_PAD_WITH_ZERO_CHECK_BOX);

            String timezoneId = (String) timezoneComboBox.getSelectedItem();
            String outputType = datetimeRadio.isSelected() ? "datetime" : "timestamp";
            String dateFormat = "自定义".equals(dateFormatComboBox.getSelectedItem()) ?
                dateFormatTextField.getText() : (String) dateFormatComboBox.getSelectedItem();
            String timestampDigits = digits10Radio.isSelected() ? "10" : "13";
            boolean padWithZero = padWithZeroCheckBox.isSelected();

            builder.getCurrentTimeConfig(new OperationExecutionContext.GetCurrentTimeConfig(
                timezoneId, outputType, dateFormat, timestampDigits, padWithZero));
        }

        // 处理时间戳转日期配置
        if (operationValidator.requiresTimestampToDatetimeConfig(selectedOperation)) {
            JComboBox<String> timezoneComboBox = registry.getComponent(UIComponentRegistry.TO_DATETIME_TIMEZONE_COMBO_BOX);
            JComboBox<String> formatComboBox = registry.getComponent(UIComponentRegistry.TO_DATETIME_FORMAT_COMBO_BOX);
            JTextField formatTextField = registry.getComponent(UIComponentRegistry.TO_DATETIME_FORMAT_TEXT_FIELD);

            String timezoneId = (String) timezoneComboBox.getSelectedItem();
            String dateFormat = "自定义".equals(formatComboBox.getSelectedItem()) ?
                formatTextField.getText() : (String) formatComboBox.getSelectedItem();

            builder.timestampToDatetimeConfig(new OperationExecutionContext.TimestampToDatetimeConfig(timezoneId, dateFormat));
        }

        // 处理日期转时间戳配置
        if (operationValidator.requiresDatetimeToTimestampConfig(selectedOperation)) {
            JComboBox<String> formatComboBox = registry.getComponent(UIComponentRegistry.TO_TIMESTAMP_FORMAT_COMBO_BOX);
            JTextField formatTextField = registry.getComponent(UIComponentRegistry.TO_TIMESTAMP_FORMAT_TEXT_FIELD);
            JRadioButton digits10Radio = registry.getComponent(UIComponentRegistry.TO_TIMESTAMP_10_DIGITS_RADIO);
            JRadioButton digits13Radio = registry.getComponent(UIComponentRegistry.TO_TIMESTAMP_13_DIGITS_RADIO);
            JCheckBox padWithZeroCheckBox = registry.getComponent(UIComponentRegistry.TO_TIMESTAMP_PAD_WITH_ZERO_CHECK_BOX);

            String inputFormat = "自定义".equals(formatComboBox.getSelectedItem()) ?
                formatTextField.getText() : (String) formatComboBox.getSelectedItem();
            String outputDigits = digits10Radio.isSelected() ? "10" : "13";
            boolean padWithZero = padWithZeroCheckBox.isSelected();

            builder.datetimeToTimestampConfig(new OperationExecutionContext.DatetimeToTimestampConfig(
                inputFormat, outputDigits, padWithZero));
        }

        OperationExecutionContext context = builder.build();

        try {
            // 执行前：记录操作提示到日志
            String hints = OperationFactory.getOperationHints(selectedOperation);
            if (hints != null && !hints.isEmpty()) {
                logManager.log(hints);
            }

            OperationExecutor.ExecutionResult result = operationExecutor.execute(context);

            // 显示结果
            if (result.isImage()) {
                imageDisplayManager.displayImage(result.getResult());
                logManager.log("生成二维码图片: " + selectedOperation);
            } else {
                if (operation.getCategory() == OperationCategory.AUTOMATION) {
                    logManager.log(result.getResult());
                } else {
                    displayText(result.getResult());
                }
            }

            logManager.log("执行操作: " + selectedOperation + " (耗时: " + result.getExecutionTimeMs() + "ms)" +
                (result.usedExpressions() ? " [使用表达式过滤]" : ""));

        } catch (Exception ex) {
            logManager.logError("执行操作失败: " + selectedOperation, ex);
            if (operation.getCategory() == OperationCategory.AUTOMATION) {
                logManager.log("执行操作失败: " + selectedOperation + " - " + ex.getMessage());
            } else {
                registry.getOutputTextArea().setText("执行失败: " + ex.getMessage());
            }
        }
    }

    /**
     * 处理复制输入事件
     */
    public void handleCopyInput() {
        String text = registry.getInputTextArea().getText();
        clipboardManager.copyText(text);
        logManager.log("已复制输入内容到剪贴板");
    }

    /**
     * 处理粘贴输入事件
     */
    public void handlePasteInput() {
        String text = clipboardManager.pasteText();
        if (text != null) {
            registry.getInputTextArea().setText(text);
            logManager.log("已从剪贴板粘贴内容到输入框");
        }
    }

    /**
     * 处理复制输出事件
     */
    public void handleCopyOutput() {
        String text = registry.getOutputTextArea().getText();
        clipboardManager.copyText(text);
        logManager.log("已复制输出内容到剪贴板");
    }

    /**
     * 处理清空输入事件
     */
    public void handleClearInput() {
        registry.getInputTextArea().setText("");
        logManager.log("已清空输入内容");
    }

    /**
     * 处理清空表达式事件
     */
    public void handleClearExpression() {
        registry.getExpressionTextArea().setText("");
        logManager.log("已清空表达式内容");
    }

    /**
     * 处理交换输入输出事件
     */
    public void handleSwap() {
        String inputText = registry.getInputTextArea().getText();
        String outputText = registry.getOutputTextArea().getText();
        registry.getInputTextArea().setText(outputText);
        registry.getOutputTextArea().setText(inputText);
        logManager.log("已交换输入和输出内容");
    }

    /**
     * 处理自动换行切换事件
     * @param wrap 是否自动换行
     */
    public void handleWrapToggle(boolean wrap) {
        uiStateManager.toggleLineWrap(wrap);
        logManager.log(wrap ? "已启用自动换行" : "已禁用自动换行");
    }

    /**
     * 处理选择图片事件
     * @param parent 父组件，用于对话框
     */
    public void handleSelectImage(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "图片文件", "png", "jpg", "jpeg", "gif", "bmp"));
        fileChooser.setDialogTitle("选择二维码图片文件");

        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            selectedImagePath = fileChooser.getSelectedFile().getAbsolutePath();
            JLabel selectedImageLabel = registry.getComponent(UIComponentRegistry.SELECTED_IMAGE_LABEL);
            selectedImageLabel.setText("已选择: " + fileChooser.getSelectedFile().getName());
            selectedImageLabel.setForeground(Color.BLACK);
            logManager.log("已选择图片文件: " + selectedImagePath);
        }
    }

    /**
     * 处理选择文件事件（用于Base编码操作）
     * @param parent 父组件，用于对话框
     */
    public void handleSelectFileForBaseEncoding(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "图片文件", "png", "jpg", "jpeg", "gif", "bmp"));
        fileChooser.setDialogTitle("选择图片文件");

        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            selectedImagePath = fileChooser.getSelectedFile().getAbsolutePath();
            JLabel selectedFileLabel = registry.getComponent(UIComponentRegistry.SELECTED_FILE_LABEL);
            selectedFileLabel.setText("已选择: " + fileChooser.getSelectedFile().getName());
            selectedFileLabel.setForeground(Color.BLACK);
            logManager.log("已选择文件: " + selectedImagePath);
        }
    }

    /**
     * 处理粘贴图片事件
     * @param parent 父组件，用于对话框
     */
    public void handlePasteImage(Component parent) {
        Image image = clipboardManager.pasteImage();
        if (image != null) {
            String imageData = clipboardManager.convertImageToBase64(image);
            if (imageData != null) {
                registry.getInputTextArea().setText(imageData);
                selectedImagePath = null;
                JLabel selectedImageLabel = registry.getComponent(UIComponentRegistry.SELECTED_IMAGE_LABEL);
                selectedImageLabel.setText("已粘贴图片到输入框");
                selectedImageLabel.setForeground(Color.BLUE);
                logManager.log("已从剪贴板粘贴图片");
            } else {
                JOptionPane.showMessageDialog(parent, "图片转换失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            String text = clipboardManager.pasteText();
            if (text != null && imageFileManager.isImageFile(text)) {
                File file = new File(text);
                if (file.exists()) {
                    selectedImagePath = text;
                    JLabel selectedImageLabel = registry.getComponent(UIComponentRegistry.SELECTED_IMAGE_LABEL);
                    selectedImageLabel.setText("已选择: " + file.getName());
                    selectedImageLabel.setForeground(Color.BLACK);
                    logManager.log("已从剪贴板选择图片文件: " + selectedImagePath);
                } else {
                    JOptionPane.showMessageDialog(parent, "文件不存在: " + text, "错误", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(parent, "剪贴板中没有图片或有效的图片路径", "提示", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * 处理保存图片事件
     * @param parent 父组件，用于对话框
     */
    public void handleSaveImage(Component parent) {
        Image image = imageDisplayManager.getCurrentImage();
        if (image == null) {
            JOptionPane.showMessageDialog(parent, "没有可保存的图片", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        imageFileManager.saveImageToFile(parent, image, "qrcode.png", new ImageFileManager.LogCallback() {
            @Override
            public void onLog(String message) {
                logManager.log(message);
            }

            @Override
            public void onError(String message, Exception e) {
                logManager.logError(message, e);
            }
        });
    }

    /**
     * 处理复制图片事件
     * @param parent 父组件，用于对话框
     */
    public void handleCopyImage(Component parent) {
        Image image = imageDisplayManager.getCurrentImage();
        if (image == null) {
            JOptionPane.showMessageDialog(parent, "没有可复制的图片", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        clipboardManager.copyImage(image);
        logManager.log("图片已复制到剪贴板");
        JOptionPane.showMessageDialog(parent, "图片已复制到剪贴板！", "成功", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 处理操作选择事件
     * @param operationName 操作名称
     */
    public void handleOperationSelection(String operationName) {
        selectedOperation = operationName;
        uiStateManager.updateInputStateForOperation(operationName);
    }

    /**
     * 获取当前选中的操作
     * @return 操作名称
     */
    public String getSelectedOperation() {
        return selectedOperation;
    }

    /**
     * 设置当前选中的操作
     * @param operationName 操作名称
     */
    public void setSelectedOperation(String operationName) {
        selectedOperation = operationName;
    }

    /**
     * 显示文本结果
     */
    private void displayText(String text) {
        JPanel outputCardsPanel = registry.getOutputCardsPanel();
        CardLayout outputCardLayout = (CardLayout) outputCardsPanel.getLayout();
        outputCardLayout.show(outputCardsPanel, "TEXT");
        registry.getOutputTextArea().setText(text);
    }

    /**
     * 将时区显示名称映射到时区ID
     */
    private String mapTimezoneToId(String timezoneDisplay) {
        switch (timezoneDisplay) {
            case "系统时区":
                return "";
            case "UTC (协调世界时)":
            case "GMT (格林威治)":
                return "utc";
            case "EST (美国东部)":
                return "America/New_York";
            case "PST (美国西部)":
                return "America/Los_Angeles";
            case "CET (欧洲中部)":
                return "Europe/Berlin";
            case "GMT (英国伦敦)":
                return "Europe/London";
            case "JST (日本)":
                return "Asia/Tokyo";
            case "AEST (澳大利亚东部)":
                return "Australia/Sydney";
            case "IST (印度)":
                return "Asia/Kolkata";
            case "CST (中国)":
                return "Asia/Shanghai";
            default:
                return "";
        }
    }

    /**
     * 执行回调接口，用于 EventHandler 与主控制器通信
     */
    public interface ExecuteCallback {
        void onExecute(String operationName);
    }
}
