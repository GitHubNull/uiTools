package org.oxff.ui.controller;

import org.oxff.ui.components.UIComponentRegistry;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import java.awt.CardLayout;

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
     * 更新输入面板可见性
     * 根据操作类型动态切换输入面板
     * @param operationName 操作名称
     */
    public void updateImageInputPanelVisibility(String operationName) {
        String panelType = determineInputPanelType(operationName);
        switchInputCard(panelType);
        updateButtonStates(operationName);
    }

    /**
     * 确定要显示的输入面板类型
     * 注意：检查顺序很重要，具体的配置面板要放在通用面板之前
     */
    private String determineInputPanelType(String operationName) {
        // 首先检查需要特定配置面板的操作
        if (validator.requiresGetCurrentTimeConfig(operationName)) {
            return "GET_CURRENT_TIME";
        } else if (validator.requiresTimestampToDatetimeConfig(operationName)) {
            return "TIMESTAMP_TO_DATETIME";
        } else if (validator.requiresDatetimeToTimestampConfig(operationName)) {
            return "DATETIME_TO_TIMESTAMP";
        } else if (validator.requiresBaseEncodingConfig(operationName)) {
            return "BASE_ENCODING";
        } else if (validator.requiresPasswordGeneratorConfig(operationName)) {
            return "PASSWORD_GENERATOR";
        } else if (validator.requiresIdCardGenerateConfig(operationName)) {
            return "ID_CARD_GENERATE";
        } else if (validator.requiresCreateBlankImageConfig(operationName)) {
            return "CREATE_BLANK_IMAGE";
        } else if (validator.requiresImageResizeConfig(operationName)) {
            return "IMAGE_RESIZE";
        } else if (validator.requiresImageCompressConfig(operationName)) {
            return "IMAGE_COMPRESS";
        } else if (validator.isAutomationOperation(operationName)) {
            return "AUTOMATION";
        } else if (validator.requiresImageInput(operationName)) {
            // 二维码解析等只需要图片输入的操作
            return "IMAGE";
        } else {
            return "TEXT";
        }
    }

    /**
     * 切换输入卡片面板
     */
    private void switchInputCard(String panelType) {
        JPanel inputCardsContainer = registry.getComponent(UIComponentRegistry.INPUT_CARDS_CONTAINER);
        if (inputCardsContainer != null) {
            CardLayout cardLayout = (CardLayout) inputCardsContainer.getLayout();
            cardLayout.show(inputCardsContainer, panelType);
        }
    }

    /**
     * 更新按钮状态
     * 根据操作类型启用或禁用文本输入相关按钮
     */
    private void updateButtonStates(String operationName) {
        boolean needsButtons = validator.requiresTextInputButtons(operationName);

        JButton pasteButton = registry.getComponent(UIComponentRegistry.PASTE_INPUT_BUTTON);
        JButton copyButton = registry.getComponent(UIComponentRegistry.COPY_INPUT_BUTTON);
        JButton clearButton = registry.getComponent(UIComponentRegistry.CLEAR_INPUT_BUTTON);
        JButton swapButton = registry.getComponent(UIComponentRegistry.SWAP_BUTTON);
        JCheckBox wrapCheckBox = registry.getComponent(UIComponentRegistry.WRAP_CHECK_BOX);

        if (pasteButton != null) pasteButton.setEnabled(needsButtons);
        if (copyButton != null) copyButton.setEnabled(needsButtons);
        if (clearButton != null) clearButton.setEnabled(needsButtons);
        if (swapButton != null) swapButton.setEnabled(needsButtons);
        if (wrapCheckBox != null) wrapCheckBox.setEnabled(needsButtons);
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

    /**
     * 切换输出框独立的自动换行状态
     * @param wrap 是否自动换行
     */
    public void toggleOutputLineWrap(boolean wrap) {
        RSyntaxTextArea outputTextArea = registry.getOutputTextArea();
        if (outputTextArea != null) {
            outputTextArea.setLineWrap(wrap);
            outputTextArea.setWrapStyleWord(wrap);
        }
    }

    /**
     * 获取输出框当前换行状态
     * @return 是否启用换行
     */
    public boolean isOutputLineWrapEnabled() {
        RSyntaxTextArea outputTextArea = registry.getOutputTextArea();
        return outputTextArea != null && outputTextArea.getLineWrap();
    }
}
