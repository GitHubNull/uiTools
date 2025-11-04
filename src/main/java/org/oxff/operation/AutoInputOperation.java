package org.oxff.operation;

import org.oxff.core.OperationCategory;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

/**
 * 自动化输入操作实现
 * 支持延迟输入、字符间隔控制、从输入或剪贴板获取文本
 */
public class AutoInputOperation implements Operation {

    // 配置参数(由UI设置)
    private int delaySeconds = 3;
    private int charIntervalMs = 100;
    private boolean useClipboard = false;

    /**
     * 设置延迟时间
     */
    public void setDelaySeconds(int delaySeconds) {
        this.delaySeconds = delaySeconds;
    }

    /**
     * 设置字符间隔
     */
    public void setCharIntervalMs(int charIntervalMs) {
        this.charIntervalMs = charIntervalMs;
    }

    /**
     * 设置是否使用剪贴板
     */
    public void setUseClipboard(boolean useClipboard) {
        this.useClipboard = useClipboard;
    }

    @Override
    public String execute(String input) {
        // 创建配置对象
        AutoInputConfig config = new AutoInputConfig();
        config.delaySeconds = this.delaySeconds;
        config.charIntervalMs = this.charIntervalMs;
        config.useClipboard = this.useClipboard;
        config.textToInput = input;

        // 在新线程中执行自动化输入，避免阻塞UI
        SwingUtilities.invokeLater(() -> {
            try {
                performAutoInput(config);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "自动化输入执行失败: " + e.getMessage(),
                                            "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        return "自动化输入已启动，请切换到目标窗口...\n\n配置信息:\n" +
               "- 延迟时间: " + config.delaySeconds + " 秒\n" +
               "- 字符间隔: " + config.charIntervalMs + " 毫秒\n" +
               "- 输入来源: " + (config.useClipboard ? "剪贴板" : "输入框") + "\n" +
               "- 输入长度: " + config.textToInput.length() + " 字符";
    }

    @Override
    public OperationCategory getCategory() {
        return OperationCategory.AUTOMATION;
    }

    @Override
    public String getDisplayName() {
        return "键盘模拟输入";
    }


    /**
     * 执行自动化输入
     */
    private void performAutoInput(AutoInputConfig config) throws Exception {
        // 获取要输入的文本
        String textToInput = config.textToInput;
        if (config.useClipboard) {
            // 从剪贴板获取文本
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                textToInput = (String) clipboard.getData(DataFlavor.stringFlavor);
            } else {
                throw new Exception("剪贴板中没有文本数据");
            }
        }

        if (textToInput == null || textToInput.isEmpty()) {
            throw new Exception("没有要输入的文本");
        }

        // 延迟指定时间
        TimeUnit.SECONDS.sleep(config.delaySeconds);

        // 获取机器人实例
        Robot robot = new Robot();

        // 逐个字符输入
        for (int i = 0; i < textToInput.length(); i++) {
            char c = textToInput.charAt(i);

            // 处理特殊字符
            if (c == '\n') {
                // 回车键
                robot.keyPress(KeyEvent.VK_ENTER);
                robot.keyRelease(KeyEvent.VK_ENTER);
            } else if (c == '\t') {
                // Tab键
                robot.keyPress(KeyEvent.VK_TAB);
                robot.keyRelease(KeyEvent.VK_TAB);
            } else {
                // 其他字符使用Clipboard方式输入以提高准确性
                String charStr = String.valueOf(c);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection selection = new StringSelection(charStr);
                clipboard.setContents(selection, selection);

                // Ctrl+V 粘贴
                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_CONTROL);
            }

            // 等待字符间隔
            TimeUnit.MILLISECONDS.sleep(config.charIntervalMs);
        }
    }

    /**
     * 自动化输入配置类
     */
    private static class AutoInputConfig {
        int delaySeconds;
        int charIntervalMs;
        boolean useClipboard;
        String textToInput;
    }
}