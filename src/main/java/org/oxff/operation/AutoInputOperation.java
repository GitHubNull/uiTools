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

    @Override
    public String execute(String input) {
        // 显示配置对话框
        AutoInputConfig config = showConfigDialog(input);
        if (config == null) {
            return "操作已取消";
        }

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
     * 显示配置对话框
     */
    private AutoInputConfig showConfigDialog(String inputText) {
        // 创建对话框
        JDialog dialog = new JDialog((Frame) null, "自动化输入配置", true);
        dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // 延迟时间设置
        JPanel delayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        delayPanel.add(new JLabel("延迟输入时间(秒): "));
        SpinnerModel delayModel = new SpinnerNumberModel(3, 0, 60, 1);
        JSpinner delaySpinner = new JSpinner(delayModel);
        delayPanel.add(delaySpinner);
        dialog.add(delayPanel);

        // 字符间隔设置
        JPanel intervalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        intervalPanel.add(new JLabel("字符间隔(毫秒): "));
        SpinnerModel intervalModel = new SpinnerNumberModel(100, 0, 1000, 10);
        JSpinner intervalSpinner = new JSpinner(intervalModel);
        intervalPanel.add(intervalSpinner);
        dialog.add(intervalPanel);

        // 输入源选择
        JPanel sourcePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sourcePanel.add(new JLabel("输入来源: "));
        ButtonGroup sourceGroup = new ButtonGroup();
        JRadioButton inputSourceRadio = new JRadioButton("输入框内容", true);
        JRadioButton clipboardSourceRadio = new JRadioButton("剪贴板内容");
        sourceGroup.add(inputSourceRadio);
        sourceGroup.add(clipboardSourceRadio);
        sourcePanel.add(inputSourceRadio);
        sourcePanel.add(clipboardSourceRadio);
        dialog.add(sourcePanel);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton okButton = new JButton("确定");
        JButton cancelButton = new JButton("取消");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel);

        // 配置结果
        AutoInputConfig[] result = new AutoInputConfig[1];

        // 确定按钮事件
        okButton.addActionListener(e -> {
            result[0] = new AutoInputConfig();
            result[0].delaySeconds = (Integer) delaySpinner.getValue();
            result[0].charIntervalMs = (Integer) intervalSpinner.getValue();
            result[0].useClipboard = clipboardSourceRadio.isSelected();
            result[0].textToInput = inputText;
            dialog.dispose();
        });

        // 取消按钮事件
        cancelButton.addActionListener(e -> {
            result[0] = null;
            dialog.dispose();
        });

        // 设置对话框属性并显示
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        return result[0];
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