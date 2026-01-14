package org.oxff.ui.util;

import javax.swing.JTextArea;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 日志管理器，负责管理日志记录
 * 从 StringFormatterUI 中提取出来，提供统一的日志记录接口
 */
public class LogManager {
    private final JTextArea logTextArea;

    public LogManager(JTextArea logTextArea) {
        this.logTextArea = logTextArea;
    }

    /**
     * 记录日志
     * @param message 日志消息
     */
    public void log(String message) {
        String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        logTextArea.append("[" + timestamp + "] " + message + "\n");
        logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
    }

    /**
     * 记录错误日志
     * @param message 错误消息
     * @param ex 异常对象
     */
    public void logError(String message, Exception ex) {
        log(message + ": " + ex.getMessage());
    }

    /**
     * 清空日志
     */
    public void clearLog() {
        logTextArea.setText("");
    }

    /**
     * 获取日志文本区域
     * @return 日志文本区域
     */
    public JTextArea getLogTextArea() {
        return logTextArea;
    }
}
