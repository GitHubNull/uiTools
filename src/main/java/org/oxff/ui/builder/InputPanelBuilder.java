package org.oxff.ui.builder;

import org.oxff.ui.components.UIComponentRegistry;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;

/**
 * 输入面板构建器
 * 负责构建各种操作类型的输入面板
 */
public class InputPanelBuilder {
    private final UIComponentRegistry registry;

    public InputPanelBuilder(UIComponentRegistry registry) {
        this.registry = registry;
    }

    /**
     * 输入面板构建结果
     */
    public static class InputPanelsResult {
        public JPanel inputButtonPanel;
        public JPanel textInputPanel;
        public JPanel imageInputPanel;
        public JPanel timezoneInputPanel;
        public JPanel baseEncodingInputPanel;
        public JPanel passwordGeneratorInputPanel;
        public JPanel automationInputPanel;
    }

    /**
     * 构建所有输入面板
     */
    public InputPanelsResult buildAllInputPanels() {
        InputPanelsResult result = new InputPanelsResult();

        // 创建按钮面板（共享）
        result.inputButtonPanel = createInputButtonPanel();

        // 创建文本输入面板（包含按钮和文本编辑器）
        result.textInputPanel = createTextInputPanel(result.inputButtonPanel);

        // 复用 ConfigPanelBuilder 创建配置面板
        ConfigPanelBuilder configBuilder = new ConfigPanelBuilder(registry);
        ConfigPanelBuilder.ConfigPanelsResult configResult = configBuilder.buildAllConfigPanels();

        result.imageInputPanel = configResult.imageInputPanel;
        result.timezoneInputPanel = configResult.timezoneConfigPanel;
        result.baseEncodingInputPanel = configResult.baseEncodingConfigPanel;
        result.passwordGeneratorInputPanel = configResult.passwordGeneratorConfigPanel;
        result.automationInputPanel = configResult.automationConfigPanel;

        return result;
    }

    /**
     * 创建输入按钮面板
     */
    private JPanel createInputButtonPanel() {
        JPanel inputButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton pasteInputButton = new JButton("粘贴");
        JButton copyInputButton = new JButton("复制");
        JButton clearInputButton = new JButton("清空");
        JButton swapButton = new JButton("交换");
        JCheckBox wrapCheckBox = new JCheckBox("自动换行");

        inputButtonPanel.add(pasteInputButton);
        inputButtonPanel.add(copyInputButton);
        inputButtonPanel.add(clearInputButton);
        inputButtonPanel.add(swapButton);
        inputButtonPanel.add(wrapCheckBox);

        // 注册按钮到注册表
        registry.registerComponent(UIComponentRegistry.PASTE_INPUT_BUTTON, pasteInputButton);
        registry.registerComponent(UIComponentRegistry.COPY_INPUT_BUTTON, copyInputButton);
        registry.registerComponent(UIComponentRegistry.CLEAR_INPUT_BUTTON, clearInputButton);
        registry.registerComponent(UIComponentRegistry.SWAP_BUTTON, swapButton);
        registry.registerComponent(UIComponentRegistry.WRAP_CHECK_BOX, wrapCheckBox);

        return inputButtonPanel;
    }

    /**
     * 创建文本输入面板
     */
    private JPanel createTextInputPanel(JPanel inputButtonPanel) {
        JPanel textInputPanel = new JPanel(new BorderLayout());

        // 创建文本输入区域
        RSyntaxTextArea inputTextArea = new RSyntaxTextArea();
        inputTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inputTextArea.setLineWrap(false);
        inputTextArea.setCodeFoldingEnabled(true);

        // 注册到注册表
        registry.registerComponent(UIComponentRegistry.INPUT_TEXT_AREA, inputTextArea);

        // 使用 RTextScrollPane 提供行号显示
        RTextScrollPane inputScrollPane = new RTextScrollPane(inputTextArea);

        textInputPanel.add(inputButtonPanel, BorderLayout.NORTH);
        textInputPanel.add(inputScrollPane, BorderLayout.CENTER);

        return textInputPanel;
    }
}
