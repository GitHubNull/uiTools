package org.oxff.ui.image;

import javax.swing.*;
import java.awt.*;
import java.util.Base64;

/**
 * 图片显示管理器，管理图片的显示和转换
 * 从 StringFormatterUI 中提取出来，封装图片显示相关逻辑
 */
public class ImageDisplayManager {
    private final JLabel imageDisplayLabel;
    private final CardLayout outputCardLayout;
    private final JPanel outputCardsPanel;
    private String currentImageFormat; // 当前图片格式 (jpeg/png)
    private String originalImageData;  // 原始图片 data URL
    private Image originalImage;       // 原始图片对象（未缩放）

    public ImageDisplayManager(JLabel imageDisplayLabel, CardLayout outputCardLayout, JPanel outputCardsPanel) {
        this.imageDisplayLabel = imageDisplayLabel;
        this.outputCardLayout = outputCardLayout;
        this.outputCardsPanel = outputCardsPanel;
    }

    /**
     * 显示图片（Base64编码的data URL格式）
     * @param imageData Base64编码的图片数据
     */
    public void displayImage(String imageData) {
        // 保存原始 data URL
        this.originalImageData = imageData;

        // 切换到图片显示
        outputCardLayout.show(outputCardsPanel, "IMAGE");

        try {
            // 从 data URL 提取格式信息
            if (imageData.startsWith("data:image/")) {
                int mimeEnd = imageData.indexOf(';');
                if (mimeEnd > 11) { // "data:image/".length()
                    String mime = imageData.substring(11, mimeEnd); // "jpeg" or "png"
                    currentImageFormat = mime;
                }
            }

            // 解析data URL，提取Base64数据
            String base64Data = imageData.substring(imageData.indexOf(",") + 1);
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);

            // 创建图片图标
            ImageIcon icon = new ImageIcon(imageBytes);

            // 保存原始图片（在缩放之前）
            Image image = icon.getImage();
            this.originalImage = image;

            // 如果图片太大，进行缩放
            int originalWidth = image.getWidth(null);
            int originalHeight = image.getHeight(null);

            // 获取显示区域大小，设置最大尺寸
            int maxWidth = Math.min(originalWidth, 400);
            int maxHeight = Math.min(originalHeight, 400);

            // 如果图片太大，进行缩放
            if (originalWidth > maxWidth || originalHeight > maxHeight) {
                double scaleX = (double) maxWidth / originalWidth;
                double scaleY = (double) maxHeight / originalHeight;
                double scale = Math.min(scaleX, scaleY);

                int scaledWidth = (int) (originalWidth * scale);
                int scaledHeight = (int) (originalHeight * scale);

                Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                icon = new ImageIcon(scaledImage);
            }

            imageDisplayLabel.setIcon(icon);
            imageDisplayLabel.setText("");

        } catch (Exception e) {
            imageDisplayLabel.setIcon(null);
            imageDisplayLabel.setText("图片显示失败: " + e.getMessage());
        }
    }

    /**
     * 清除图片显示
     */
    public void clearImage() {
        imageDisplayLabel.setIcon(null);
        imageDisplayLabel.setText("");
        currentImageFormat = null;
        originalImageData = null;
        originalImage = null;
    }

    /**
     * 获取当前显示的图片（原始尺寸，未缩放）
     * @return 原始图片，如果没有图片则返回 null
     */
    public Image getCurrentImage() {
        return originalImage;
    }

    /**
     * 获取原始图片数据（data URL格式）
     * @return 原始的 Base64 data URL，如果没有图片则返回 null
     */
    public String getCurrentImageData() {
        return originalImageData;
    }

    /**
     * 获取当前图片格式
     * @return 图片格式 ("jpeg", "png" 等)，如果没有图片则返回 null
     */
    public String getCurrentImageFormat() {
        return currentImageFormat;
    }

    /**
     * 检查是否有图片正在显示
     * @return true 如果有图片正在显示
     */
    public boolean hasImage() {
        return imageDisplayLabel.getIcon() != null;
    }

    /**
     * 缩放图片（如果需要）
     * @param image 原始图片
     * @param maxWidth 最大宽度
     * @param maxHeight 最大高度
     * @return 缩放后的图片
     */
    public static Image scaleImageIfNeeded(Image image, int maxWidth, int maxHeight) {
        int originalWidth = image.getWidth(null);
        int originalHeight = image.getHeight(null);

        // 如果图片不需要缩放，直接返回
        if (originalWidth <= maxWidth && originalHeight <= maxHeight) {
            return image;
        }

        // 计算缩放比例
        double scaleX = (double) maxWidth / originalWidth;
        double scaleY = (double) maxHeight / originalHeight;
        double scale = Math.min(scaleX, scaleY);

        int scaledWidth = (int) (originalWidth * scale);
        int scaledHeight = (int) (originalHeight * scale);

        return image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
    }
}
