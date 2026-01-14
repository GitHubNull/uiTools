package org.oxff.ui.util;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * 键盘快捷键管理器，负责管理键盘快捷键
 * 从 StringFormatterUI 中提取出来，提供统一的快捷键设置接口
 */
public class KeyboardShortcutManager {

    /**
     * 设置按钮的快捷键
     * @param executeButton 执行按钮
     * @param copyInputButton 复制输入按钮
     * @param pasteInputButton 粘贴输入按钮
     * @param copyOutputButton 复制输出按钮
     * @param clearInputButton 清空输入按钮
     */
    public void setupButtonShortcuts(JButton executeButton,
                                     JButton copyInputButton,
                                     JButton pasteInputButton,
                                     JButton copyOutputButton,
                                     JButton clearInputButton) {
        // Ctrl+E 执行操作
        executeButton.setMnemonic(KeyEvent.VK_E);

        // Ctrl+C 复制输入
        copyInputButton.setMnemonic(KeyEvent.VK_C);

        // Ctrl+V 粘贴输入
        pasteInputButton.setMnemonic(KeyEvent.VK_V);

        // Ctrl+Shift+C 复制输出
        copyOutputButton.setMnemonic(KeyEvent.VK_O);

        // Ctrl+Shift+X 清空输入
        clearInputButton.setMnemonic(KeyEvent.VK_X);
    }

    /**
     * 设置文本区域的全选快捷键 (Ctrl+A)
     * @param textArea 要设置的文本区域
     */
    public void setupSelectAll(RSyntaxTextArea textArea) {
        textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK), "selectAll");
        textArea.getActionMap().put("selectAll", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.selectAll();
            }
        });
    }

    /**
     * 设置所有文本区域的全选快捷键
     * @param inputTextArea 输入文本区域
     * @param outputTextArea 输出文本区域
     */
    public void setupSelectAllForAll(RSyntaxTextArea inputTextArea, RSyntaxTextArea outputTextArea) {
        // Ctrl+A 全选输入框
        setupSelectAll(inputTextArea);

        // Ctrl+A 全选输出框
        setupSelectAll(outputTextArea);
    }
}
