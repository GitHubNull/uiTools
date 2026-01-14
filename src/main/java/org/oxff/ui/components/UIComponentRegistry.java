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
    public static final String OPERATION_COMBO_BOX = "operationComboBox";
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

    // 自动化配置相关组件
    public static final String DELAY_SECONDS_SPINNER = "delaySecondsSpinner";
    public static final String CHAR_INTERVAL_MS_SPINNER = "charIntervalMsSpinner";
    public static final String INPUT_SOURCE_RADIO = "inputSourceRadio";
    public static final String CLIPBOARD_SOURCE_RADIO = "clipboardSourceRadio";

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
     * 获取操作下拉框
     * @return 操作下拉框
     */
    @SuppressWarnings("unchecked")
    public JComboBox<String> getOperationComboBox() {
        return getComponent(OPERATION_COMBO_BOX);
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
