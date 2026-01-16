package org.oxff.ui.components;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * UI组件注册表，集中管理UI组件引用
 * 提供统一的组件访问接口，避免在多个地方传递组件引用
 */
public class UIComponentRegistry {
    private final Map<String, JComponent> components = new HashMap<>();

    // 常用组件名称常量
    public static final String INPUT_TEXT_AREA = "inputTextArea";
    public static final String OUTPUT_TEXT_AREA = "outputTextArea";
    public static final String EXPRESSION_TEXT_AREA = "expressionTextArea";
    public static final String LOG_TEXT_AREA = "logTextArea";
    public static final String OPERATION_TREE = "operationTree";
    public static final String EXECUTE_BUTTON = "executeButton";
    public static final String COPY_INPUT_BUTTON = "copyInputButton";
    public static final String PASTE_INPUT_BUTTON = "pasteInputButton";
    public static final String COPY_OUTPUT_BUTTON = "copyOutputButton";
    public static final String CLEAR_INPUT_BUTTON = "clearInputButton";
    public static final String CLEAR_EXPRESSION_BUTTON = "clearExpressionButton";
    public static final String SWAP_BUTTON = "swapButton";
    public static final String WRAP_CHECK_BOX = "wrapCheckBox";
    public static final String SELECT_IMAGE_BUTTON = "selectImageButton";
    public static final String PASTE_IMAGE_BUTTON = "pasteImageButton";
    public static final String SELECTED_IMAGE_LABEL = "selectedImageLabel";
    public static final String IMAGE_INPUT_PANEL = "imageInputPanel";
    public static final String TIMEZONE_COMBO_BOX = "timezoneComboBox";
    public static final String TIMEZONE_CONFIG_PANEL = "timezoneConfigPanel";
    public static final String AUTOMATION_CONFIG_PANEL = "automationConfigPanel";
    public static final String EXPRESSION_PANEL = "expressionPanel";
    public static final String OUTPUT_PANEL = "outputPanel";
    public static final String OUTPUT_CARDS_PANEL = "outputCardsPanel";
    public static final String IMAGE_DISPLAY_LABEL = "imageDisplayLabel";
    public static final String OUTPUT_EXPRESSION_SPLIT_PANE = "outputExpressionSplitPane";

    // 输出面板特定组件
    public static final String OUTPUT_WRAP_CHECK_BOX = "outputWrapCheckBox";
    public static final String SAVE_OUTPUT_BUTTON = "saveOutputButton";
    public static final String AUTO_SAVE_CHECK_BOX = "autoSaveCheckBox";
    public static final String DIRECT_FILE_OUTPUT_CHECK_BOX = "directFileOutputCheckBox";

    // 自动保存目录配置相关组件
    public static final String SELECT_AUTO_SAVE_DIR_BUTTON = "selectAutoSaveDirButton";
    public static final String CURRENT_AUTO_SAVE_DIR_LABEL = "currentAutoSaveDirLabel";

    // 自动化配置相关组件
    public static final String DELAY_SECONDS_SPINNER = "delaySecondsSpinner";
    public static final String CHAR_INTERVAL_MS_SPINNER = "charIntervalMsSpinner";
    public static final String INPUT_SOURCE_RADIO = "inputSourceRadio";
    public static final String CLIPBOARD_SOURCE_RADIO = "clipboardSourceRadio";

    // Base编码配置相关组件
    public static final String BASE_ENCODING_COMBO_BOX = "baseEncodingComboBox";
    public static final String SELECT_FILE_BUTTON = "selectFileButton";
    public static final String SELECTED_FILE_LABEL = "selectedFileLabel";

    // 密码生成器配置相关组件
    public static final String PASSWORD_LENGTH_SPINNER = "passwordLengthSpinner";
    public static final String INCLUDE_DIGITS_CHECK_BOX = "includeDigitsCheckBox";
    public static final String DIGIT_COUNT_SPINNER = "digitCountSpinner";
    public static final String INCLUDE_UPPERCASE_CHECK_BOX = "includeUppercaseCheckBox";
    public static final String UPPERCASE_COUNT_SPINNER = "uppercaseCountSpinner";
    public static final String INCLUDE_LOWERCASE_CHECK_BOX = "includeLowercaseCheckBox";
    public static final String LOWERCASE_COUNT_SPINNER = "lowercaseCountSpinner";
    public static final String INCLUDE_SPECIAL_CHARS_CHECK_BOX = "includeSpecialCharsCheckBox";
    public static final String SPECIAL_CHAR_COUNT_SPINNER = "specialCharCountSpinner";
    public static final String PASSWORD_COUNT_SPINNER = "passwordCountSpinner";

    // 输入卡片容器
    public static final String INPUT_CARDS_CONTAINER = "inputCardsContainer";

    // 时间戳配置相关组件
    // 获取当前时间配置
    public static final String GET_CURRENT_TIMEZONE_COMBO_BOX = "getCurrentTimezoneComboBox";
    public static final String GET_CURRENT_DATETIME_RADIO = "getCurrentDatetimeRadio";
    public static final String GET_CURRENT_TIMESTAMP_RADIO = "getCurrentTimestampRadio";
    public static final String GET_CURRENT_DATE_FORMAT_COMBO_BOX = "getCurrentDateFormatComboBox";
    public static final String GET_CURRENT_DATE_FORMAT_TEXT_FIELD = "getCurrentDateFormatTextField";
    public static final String GET_CURRENT_10_DIGITS_RADIO = "getCurrent10DigitsRadio";
    public static final String GET_CURRENT_13_DIGITS_RADIO = "getCurrent13DigitsRadio";
    public static final String GET_CURRENT_PAD_WITH_ZERO_CHECK_BOX = "getCurrentPadWithZeroCheckBox";

    // 时间戳转日期配置
    public static final String TO_DATETIME_TIMEZONE_COMBO_BOX = "toDatetimeTimezoneComboBox";
    public static final String TO_DATETIME_FORMAT_COMBO_BOX = "toDatetimeFormatComboBox";
    public static final String TO_DATETIME_FORMAT_TEXT_FIELD = "toDatetimeFormatTextField";
    public static final String TIMESTAMP_TO_DATETIME_INPUT_FIELD = "timestampToDatetimeInputField";

    // 日期转时间戳配置
    public static final String TO_TIMESTAMP_FORMAT_COMBO_BOX = "toTimestampFormatComboBox";
    public static final String TO_TIMESTAMP_FORMAT_TEXT_FIELD = "toTimestampFormatTextField";
    public static final String TO_TIMESTAMP_10_DIGITS_RADIO = "toTimestamp10DigitsRadio";
    public static final String TO_TIMESTAMP_13_DIGITS_RADIO = "toTimestamp13DigitsRadio";
    public static final String TO_TIMESTAMP_PAD_WITH_ZERO_CHECK_BOX = "toTimestampPadWithZeroCheckBox";
    public static final String DATETIME_TO_TIMESTAMP_INPUT_FIELD = "datetimeToTimestampInputField";

    // 身份证配置相关组件
    public static final String ID_CARD_PROVINCE_COMBO_BOX = "idCardProvinceComboBox";
    public static final String ID_CARD_CITY_COMBO_BOX = "idCardCityComboBox";
    public static final String ID_CARD_AGE_SPINNER = "idCardAgeSpinner";
    public static final String ID_CARD_BIRTHDATE_FIELD = "idCardBirthDateField";
    public static final String ID_CARD_GENDER_RANDOM_RADIO = "idCardGenderRandomRadio";
    public static final String ID_CARD_GENDER_MALE_RADIO = "idCardGenderMaleRadio";
    public static final String ID_CARD_GENDER_FEMALE_RADIO = "idCardGenderFemaleRadio";
    public static final String ID_CARD_COUNT_SPINNER = "idCardCountSpinner";
    public static final String ID_CARD_AGE_RADIO = "idCardAgeRadio";
    public static final String ID_CARD_BIRTHDATE_RADIO = "idCardBirthDateRadio";

    // 图片工具配置相关组件
    public static final String BLANK_IMAGE_SIZE_SPEC_COMBO = "blankImageSizeSpecCombo";
    public static final String BLANK_IMAGE_WIDTH_SPINNER = "blankImageWidthSpinner";
    public static final String BLANK_IMAGE_HEIGHT_SPINNER = "blankImageHeightSpinner";
    public static final String BLANK_IMAGE_BG_COLOR_BUTTON = "blankImageBgColorButton";
    public static final String BLANK_IMAGE_BG_COLOR_PREVIEW = "blankImageBgColorPreview";
    public static final String BLANK_IMAGE_FORMAT_PNG_RADIO = "blankImageFormatPngRadio";
    public static final String BLANK_IMAGE_FORMAT_JPEG_RADIO = "blankImageFormatJpegRadio";

    public static final String IMAGE_RESIZE_SOURCE_LABEL = "imageResizeSourceLabel";
    public static final String IMAGE_RESIZE_SELECT_BUTTON = "imageResizeSelectButton";
    public static final String IMAGE_RESIZE_PASTE_BUTTON = "imageResizePasteButton";
    public static final String IMAGE_RESIZE_SIZE_SPEC_COMBO = "imageResizeSizeSpecCombo";
    public static final String IMAGE_RESIZE_WIDTH_SPINNER = "imageResizeWidthSpinner";
    public static final String IMAGE_RESIZE_HEIGHT_SPINNER = "imageResizeHeightSpinner";
    public static final String IMAGE_RESIZE_MAINTAIN_RATIO_CHECK = "imageResizeMaintainRatioCheck";

    public static final String IMAGE_COMPRESS_SOURCE_LABEL = "imageCompressSourceLabel";
    public static final String IMAGE_COMPRESS_SELECT_BUTTON = "imageCompressSelectButton";
    public static final String IMAGE_COMPRESS_PASTE_BUTTON = "imageCompressPasteButton";
    public static final String IMAGE_COMPRESS_FORMAT_JPEG_RADIO = "imageCompressFormatJpegRadio";
    public static final String IMAGE_COMPRESS_FORMAT_PNG_RADIO = "imageCompressFormatPngRadio";
    public static final String IMAGE_COMPRESS_LEVEL_SLIDER = "imageCompressLevelSlider";
    public static final String IMAGE_COMPRESS_LEVEL_LABEL = "imageCompressLevelLabel";
    public static final String IMAGE_COMPRESS_SHOW_COMPARISON_CHECK = "imageCompressShowComparisonCheck";

    /**
     * 注册组件
     * @param name 组件名称
     * @param component 组件对象
     */
    public void registerComponent(String name, JComponent component) {
        components.put(name, component);
    }

    /**
     * 获取组件
     * @param name 组件名称
     * @return 组件对象，如果不存在返回 null
     */
    @SuppressWarnings("unchecked")
    public <T extends JComponent> T getComponent(String name) {
        return (T) components.get(name);
    }

    /**
     * 获取输入文本区域
     * @return 输入文本区域
     */
    public RSyntaxTextArea getInputTextArea() {
        return getComponent(INPUT_TEXT_AREA);
    }

    /**
     * 获取输出文本区域
     * @return 输出文本区域
     */
    public RSyntaxTextArea getOutputTextArea() {
        return getComponent(OUTPUT_TEXT_AREA);
    }

    /**
     * 获取表达式文本区域
     * @return 表达式文本区域
     */
    public RSyntaxTextArea getExpressionTextArea() {
        return getComponent(EXPRESSION_TEXT_AREA);
    }

    /**
     * 获取操作树
     * @return 操作树
     */
    public JTree getOperationTree() {
        return getComponent(OPERATION_TREE);
    }

    /**
     * 获取时区下拉框
     * @return 时区下拉框
     */
    @SuppressWarnings("unchecked")
    public JComboBox<String> getTimezoneComboBox() {
        return getComponent(TIMEZONE_COMBO_BOX);
    }

    /**
     * 获取延迟时间输入框
     * @return 延迟时间输入框
     */
    public JSpinner getDelaySecondsSpinner() {
        return getComponent(DELAY_SECONDS_SPINNER);
    }

    /**
     * 获取字符间隔输入框
     * @return 字符间隔输入框
     */
    public JSpinner getCharIntervalMsSpinner() {
        return getComponent(CHAR_INTERVAL_MS_SPINNER);
    }

    /**
     * 获取输入来源单选按钮
     * @return 输入来源单选按钮
     */
    public JRadioButton getInputSourceRadio() {
        return getComponent(INPUT_SOURCE_RADIO);
    }

    /**
     * 获取剪贴板来源单选按钮
     * @return 剪贴板来源单选按钮
     */
    public JRadioButton getClipboardSourceRadio() {
        return getComponent(CLIPBOARD_SOURCE_RADIO);
    }

    /**
     * 获取输出表达式分割面板
     * @return 输出表达式分割面板
     */
    public JSplitPane getOutputExpressionSplitPane() {
        return getComponent(OUTPUT_EXPRESSION_SPLIT_PANE);
    }

    /**
     * 获取图片显示标签
     * @return 图片显示标签
     */
    public JLabel getImageDisplayLabel() {
        return getComponent(IMAGE_DISPLAY_LABEL);
    }

    /**
     * 获取输出卡片面板
     * @return 输出卡片面板
     */
    public JPanel getOutputCardsPanel() {
        return getComponent(OUTPUT_CARDS_PANEL);
    }

    /**
     * 获取输出换行复选框
     * @return 输出换行复选框
     */
    public JCheckBox getOutputWrapCheckBox() {
        return getComponent(OUTPUT_WRAP_CHECK_BOX);
    }

    /**
     * 获取保存输出按钮
     * @return 保存输出按钮
     */
    public JButton getSaveOutputButton() {
        return getComponent(SAVE_OUTPUT_BUTTON);
    }

    /**
     * 获取自动保存复选框
     * @return 自动保存复选框
     */
    public JCheckBox getAutoSaveCheckBox() {
        return getComponent(AUTO_SAVE_CHECK_BOX);
    }

    /**
     * 获取直接文件输出复选框
     * @return 直接文件输出复选框
     */
    public JCheckBox getDirectFileOutputCheckBox() {
        return getComponent(DIRECT_FILE_OUTPUT_CHECK_BOX);
    }

    /**
     * 检查组件是否已注册
     * @param name 组件名称
     * @return true 如果组件已注册
     */
    public boolean hasComponent(String name) {
        return components.containsKey(name);
    }

    /**
     * 注销组件
     * @param name 组件名称
     */
    public void unregisterComponent(String name) {
        components.remove(name);
    }

    /**
     * 清空所有注册的组件
     */
    public void clear() {
        components.clear();
    }
}
