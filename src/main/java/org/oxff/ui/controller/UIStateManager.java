package org.oxff.ui.controller;

import org.oxff.ui.components.UIComponentRegistry;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import java.awt.*;

/**
 * UI状态管理器，管理UI状态，控制各面板的显示/隐藏
 * 从 StringFormatterUI 中提取出来，封装UI状态更新逻辑
 */
public class UIStateManager {
    private final UIComponentRegistry registry;
    private final OperationValidator validator;

    public UIStateManager(UIComponentRegistry registry, OperationValidator validator) {
        this.registry = registry;
        this.validator = validator;
    }

    /**
     * 更新表达式面板可见性
     * @param operationName 操作名称
     */
    public void updateExpressionPanelVisibility(String operationName) {
        boolean showExpressionPanel = validator.requiresExpressionInput(operationName);
        boolean isAutomation = validator.isAutomationOperation(operationName);

        JSplitPane outputExpressionSplitPane = registry.getOutputExpressionSplitPane();
        JPanel expressionPanel = registry.getComponent(UIComponentRegistry.EXPRESSION_PANEL);
        JPanel outputPanel = registry.getComponent(UIComponentRegistry.OUTPUT_PANEL);
        JPanel automationConfigPanel = registry.getComponent(UIComponentRegistry.AUTOMATION_CONFIG_PANEL);

        // 更新表达式面板可见性
        if (showExpressionPanel && expressionPanel != null) {
            outputExpressionSplitPane.setLeftComponent(expressionPanel);
        } else {
            outputExpressionSplitPane.setLeftComponent(null);
        }

        // 更新输出面板可见性
        if (isAutomation) {
            // 对于自动化操作，隐藏输出面板
            outputExpressionSplitPane.setRightComponent(null);
            // 显示自动化配置面板
            if (automationConfigPanel != null) {
                automationConfigPanel.setVisible(true);
            }
        } else {
            // 对于其他操作，显示输出面板
            outputExpressionSplitPane.setRightComponent(outputPanel);
            // 隐藏自动化配置面板
            if (automationConfigPanel != null) {
                automationConfigPanel.setVisible(false);
            }
        }

        outputExpressionSplitPane.revalidate();
        outputExpressionSplitPane.repaint();
    }

    /**
     * 更新图片输入面板可见性
     * @param operationName 操作名称
     */
    public void updateImageInputPanelVisibility(String operationName) {
        boolean showImageInputPanel = validator.requiresImageInput(operationName);
        boolean showTimezonePanel = validator.requiresTimezoneSelection(operationName);
        boolean showBaseEncodingConfigPanel = validator.requiresBaseEncodingConfig(operationName);
        boolean showPasswordGeneratorConfigPanel = validator.requiresPasswordGeneratorConfig(operationName);

        RSyntaxTextArea inputTextArea = registry.getInputTextArea();

        if (showImageInputPanel) {
            // 切换到图片输入面板
            switchConfigPanel("IMAGE");
            // 禁用普通的文本输入区域
            inputTextArea.setEnabled(false);
            inputTextArea.setBackground(Color.LIGHT_GRAY);
            inputTextArea.setText("请使用下方的图片选择功能选择二维码图片文件");
        } else if (showTimezonePanel) {
            // 切换到时区选择面板
            switchConfigPanel("TIMEZONE");
            // 禁用文本输入区域
            inputTextArea.setEnabled(false);
            inputTextArea.setBackground(Color.LIGHT_GRAY);
            inputTextArea.setText("请使用下方的时区选择功能选择时区");
        } else if (showBaseEncodingConfigPanel) {
            // 切换到Base编码配置面板
            switchConfigPanel("BASE_ENCODING");
            // 禁用文本输入区域
            inputTextArea.setEnabled(false);
            inputTextArea.setBackground(Color.LIGHT_GRAY);
            inputTextArea.setText("请使用下方的文件选择功能选择图片文件");
        } else if (showPasswordGeneratorConfigPanel) {
            // 切换到密码生成器配置面板
            switchConfigPanel("PASSWORD_GENERATOR");
            // 禁用文本输入区域
            inputTextArea.setEnabled(false);
            inputTextArea.setBackground(Color.LIGHT_GRAY);
            inputTextArea.setText("请使用下方的配置面板设置密码生成规则");
        } else {
            // 切换到空面板
            switchConfigPanel("EMPTY");
            // 显示普通的文本输入区域
            inputTextArea.setEnabled(true);
            inputTextArea.setBackground(Color.WHITE);
            String currentText = inputTextArea.getText();
            if ("请使用下方的图片选择功能选择二维码图片文件".equals(currentText) ||
                "请使用下方的时区选择功能选择时区".equals(currentText) ||
                "请使用下方的文件选择功能选择图片文件".equals(currentText) ||
                "请使用下方的配置面板设置密码生成规则".equals(currentText)) {
                inputTextArea.setText("");
            }
        }
    }

    /**
     * 切换配置面板
     * @param panelName 面板名称 (AUTOMATION, IMAGE, TIMEZONE, EMPTY)
     */
    private void switchConfigPanel(String panelName) {
        // 查找 configContainerPanel（它位于 inputPanel 的 SOUTH 位置）
        // 从 inputTextArea 开始向上查找
        RSyntaxTextArea inputTextArea = registry.getInputTextArea();
        JScrollPane scrollPane = (JScrollPane) inputTextArea.getParent().getParent();
        JPanel inputPanel = (JPanel) scrollPane.getParent();
        JPanel configContainerPanel = (JPanel) inputPanel.getComponent(2);
        CardLayout cardLayout = (CardLayout) configContainerPanel.getLayout();
        cardLayout.show(configContainerPanel, panelName);
    }

    /**
     * 更新自动化配置面板可见性
     * @param visible 是否可见
     */
    public void updateAutomationConfigPanel(boolean visible) {
        if (visible) {
            switchConfigPanel("AUTOMATION");
        }
    }

    /**
     * 更新时区配置面板可见性
     * @param visible 是否可见
     */
    public void updateTimezoneConfigPanel(boolean visible) {
        if (visible) {
            switchConfigPanel("TIMEZONE");
        }
    }

    /**
     * 根据操作类型更新输入状态
     * @param operationName 操作名称
     */
    public void updateInputStateForOperation(String operationName) {
        updateImageInputPanelVisibility(operationName);
        updateExpressionPanelVisibility(operationName);
    }

    /**
     * 切换自动换行状态
     * @param wrap 是否自动换行
     */
    public void toggleLineWrap(boolean wrap) {
        RSyntaxTextArea inputTextArea = registry.getInputTextArea();
        RSyntaxTextArea outputTextArea = registry.getOutputTextArea();
        RSyntaxTextArea expressionTextArea = registry.getExpressionTextArea();

        inputTextArea.setLineWrap(wrap);
        inputTextArea.setWrapStyleWord(wrap);

        outputTextArea.setLineWrap(wrap);
        outputTextArea.setWrapStyleWord(wrap);

        if (expressionTextArea != null) {
            expressionTextArea.setLineWrap(wrap);
            expressionTextArea.setWrapStyleWord(wrap);
        }
    }
}
