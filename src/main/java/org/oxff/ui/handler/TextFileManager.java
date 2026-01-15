package org.oxff.ui.handler;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 文本文件管理器，处理文本文件的保存和自动保存
 * 参考 ImageFileManager 的设计模式
 */
public class TextFileManager {

    /**
     * 大结果阈值（默认1MB字符数）
     */
    private static final int LARGE_RESULT_THRESHOLD = 1_048_576;

    /**
     * 自动保存目录名
     */
    private static final String AUTO_SAVE_DIR_NAME = "uiTools_outputs";

    /**
     * 保存文本到文件（手动保存）
     * @param parent 父组件，用于对话框定位
     * @param text 要保存的文本
     * @param defaultName 默认文件名
     * @param logCallback 日志回调，用于记录操作结果
     */
    public void saveTextToFile(Component parent, String text, String defaultName, LogCallback logCallback) {
        if (text == null || text.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "没有可保存的文本内容",
                "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(defaultName));

        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                String fileName = file.getAbsolutePath();
                if (!fileName.toLowerCase().endsWith(".txt")) {
                    fileName += ".txt";
                    file = new File(fileName);
                }

                // 使用UTF-8编码写入文件
                try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
                    writer.write(text);
                }

                logCallback.onLog("文本已保存到: " + fileName);
                JOptionPane.showMessageDialog(parent, "文本保存成功！", "成功", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                logCallback.onError("保存文本失败", e);
                JOptionPane.showMessageDialog(parent, "保存文本失败: " + e.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 自动保存文本到文件（带时间戳命名）
     * @param text 要保存的文本
     * @param baseDir 基础目录（可为null，使用用户主目录）
     * @param operationName 操作名称，用于文件命名
     * @return 保存的文件完整路径
     * @throws IOException 保存失败时抛出
     */
    public String autoSaveText(String text, String baseDir, String operationName) throws IOException {
        if (text == null || text.isEmpty()) {
            return null;
        }

        // 确定保存目录
        File saveDir;
        if (baseDir != null && !baseDir.isEmpty()) {
            saveDir = new File(baseDir);
        } else {
            String userHome = System.getProperty("user.home");
            saveDir = new File(userHome, AUTO_SAVE_DIR_NAME);
        }

        // 创建目录（如果不存在）
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }

        // 生成带时间戳的文件名
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String safeOperationName = sanitizeFileName(operationName);
        String fileName = String.format("%s_%s.txt", safeOperationName, timestamp);
        File file = new File(saveDir, fileName);

        // 保存文件
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(text);
        }

        return file.getAbsolutePath();
    }

    /**
     * 保存文本到指定路径（用于大结果直接输出）
     * @param text 要保存的文本
     * @param filePath 文件路径
     * @return 保存的文件完整路径
     * @throws IOException 保存失败时抛出
     */
    public String saveToPath(String text, String filePath) throws IOException {
        File file = new File(filePath);
        // 确保目录存在
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(text);
        }

        return file.getAbsolutePath();
    }

    /**
     * 判断是否为超大结果
     * @param text 文本内容
     * @return true 如果是大结果
     */
    public boolean isLargeResult(String text) {
        return text != null && text.length() >= LARGE_RESULT_THRESHOLD;
    }

    /**
     * 判断是否为大结果（使用自定义阈值）
     * @param text 文本内容
     * @param threshold 自定义阈值
     * @return true 如果是大结果
     */
    public boolean isLargeResult(String text, int threshold) {
        return text != null && text.length() >= threshold;
    }

    /**
     * 获取建议的文件名
     * @param operationName 操作名称
     * @param extension 文件扩展名
     * @return 建议的文件名
     */
    public String suggestFileName(String operationName, String extension) {
        String safeName = sanitizeFileName(operationName);
        if (!extension.startsWith(".")) {
            extension = "." + extension;
        }
        return safeName + extension;
    }

    /**
     * 获取大结果阈值
     * @return 阈值（字符数）
     */
    public int getLargeResultThreshold() {
        return LARGE_RESULT_THRESHOLD;
    }

    /**
     * 清理文件名，移除不安全的字符
     * @param name 原始名称
     * @return 安全的文件名
     */
    private String sanitizeFileName(String name) {
        if (name == null || name.isEmpty()) {
            return "output";
        }
        // 替换不安全的字符为下划线
        return name.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    /**
     * 日志回调接口
     */
    public interface LogCallback {
        void onLog(String message);
        void onError(String message, Exception e);
    }
}
