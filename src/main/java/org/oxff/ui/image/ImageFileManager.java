package org.oxff.ui.image;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import javax.imageio.ImageIO;

/**
 * 图片文件管理器，处理图片文件的保存和加载
 * 从 StringFormatterUI 中提取出来，提供统一的图片文件操作接口
 */
public class ImageFileManager {

    /**
     * 保存图片到文件
     * @param parent 父组件，用于对话框定位
     * @param image 要保存的图片
     * @param defaultName 默认文件名
     * @param logCallback 日志回调，用于记录操作结果
     */
    public void saveImageToFile(Component parent, Image image, String defaultName, LogCallback logCallback) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(defaultName));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG图片", "png"));

        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            try {
                String fileName = fileChooser.getSelectedFile().getAbsolutePath();
                if (!fileName.toLowerCase().endsWith(".png")) {
                    fileName += ".png";
                }

                // 创建BufferedImage并保存
                BufferedImage bufferedImage = new BufferedImage(
                    image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = bufferedImage.createGraphics();
                g2d.drawImage(image, 0, 0, null);
                g2d.dispose();

                ImageIO.write(bufferedImage, "PNG", new File(fileName));
                logCallback.onLog("图片已保存到: " + fileName);
                JOptionPane.showMessageDialog(parent, "图片保存成功！", "成功", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                logCallback.onError("保存图片失败", e);
                JOptionPane.showMessageDialog(parent, "保存图片失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 从文件加载图片
     * @param path 图片文件路径
     * @return 加载的图片，失败返回 null
     */
    public Image loadImageFromFile(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 转换图片为Base64编码
     * @param image 要转换的图片
     * @return Base64编码的data URL，失败返回 null
     */
    public String convertImageToBase64(Image image) {
        try {
            // 创建BufferedImage
            BufferedImage bufferedImage = new BufferedImage(
                image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();

            // 转换为PNG格式的字节数组
            java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "PNG", outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            // 转换为Base64
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            return "data:image/png;base64," + base64;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 转换Base64为图片
     * @param base64 Base64编码的图片数据（data URL格式）
     * @return 解码后的图片，失败返回 null
     */
    public Image convertBase64ToImage(String base64) {
        try {
            // 解析data URL，提取Base64数据
            String base64Data = base64.substring(base64.indexOf(",") + 1);
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            return ImageIO.read(new java.io.ByteArrayInputStream(imageBytes));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断是否为图片文件
     * @param path 文件路径
     * @return true 如果是图片文件
     */
    public boolean isImageFile(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        String lowerPath = path.toLowerCase();
        return lowerPath.endsWith(".png") || lowerPath.endsWith(".jpg") ||
               lowerPath.endsWith(".jpeg") || lowerPath.endsWith(".gif") ||
               lowerPath.endsWith(".bmp");
    }

    /**
     * 日志回调接口
     */
    public interface LogCallback {
        void onLog(String message);
        void onError(String message, Exception e);
    }
}
