package org.oxff.operation.imagetools;

import org.oxff.core.OperationCategory;
import org.oxff.core.SubcategoryRegistry;
import org.oxff.operation.Operation;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Base64;
import java.util.Iterator;

/**
 * 图片压缩操作
 * 支持JPEG有损压缩和PNG无损压缩
 */
public class ImageCompressOperation implements Operation {

    private float compressionLevel = 0.75f;  // JPEG压缩级别（0.0-1.0）
    private String format = "JPEG";  // 输出格式（JPEG/PNG）
    private boolean showComparison = false;  // 是否显示压缩前后对比
    private String lastComparisonInfo = null;  // 最后一次的对比信息

    // Setter方法（通过反射调用）
    public void setCompressionLevel(float level) {
        this.compressionLevel = level;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setShowComparison(boolean show) {
        this.showComparison = show;
    }

    /**
     * 获取最后一次的对比信息
     * @return 对比信息文本，如果没有则返回 null
     */
    public String getLastComparisonInfo() {
        return lastComparisonInfo;
    }

    @Override
    public String execute(String input) {
        // 重置对比信息
        lastComparisonInfo = null;

        try {
            // 1. 加载原始图片
            ImageLoadResult loadResult = loadImage(input);
            if (loadResult == null || loadResult.image == null) {
                return "无法加载图片，请检查输入是否为有效的图片文件路径或Base64数据";
            }

            BufferedImage sourceImage = loadResult.image;
            int originalSize = loadResult.originalFileSize;  // 使用原始文件大小

            // 2. 根据格式进行压缩
            ByteArrayOutputStream compressedOutput = new ByteArrayOutputStream();

            if ("JPEG".equalsIgnoreCase(format) || "JPG".equalsIgnoreCase(format)) {
                // JPEG有损压缩
                compressJPEG(sourceImage, compressedOutput, compressionLevel);
            } else if ("PNG".equalsIgnoreCase(format)) {
                // PNG无损压缩
                compressPNG(sourceImage, compressedOutput);
            } else {
                // 默认使用JPEG
                compressJPEG(sourceImage, compressedOutput, compressionLevel);
            }

            int compressedSize = compressedOutput.size();

            // 3. 如果需要显示对比，生成对比信息
            if (showComparison) {
                double reduction = ((originalSize - compressedSize) / (double) originalSize) * 100;
                lastComparisonInfo = String.format("压缩结果：\n" +
                        "原始大小: %,.0f bytes (%.2f KB)\n" +
                        "压缩后大小: %,.0f bytes (%.2f KB)\n" +
                        "压缩率: %.2f%%\n" +
                        "减少: %.2f%%\n" +
                        "格式: %s\n" +
                        "压缩级别: %.2f",
                        (double) originalSize, originalSize / 1024.0,
                        (double) compressedSize, compressedSize / 1024.0,
                        compressionLevel * 100,
                        reduction,
                        format.toUpperCase(),
                        compressionLevel);
            }

            // 4. 始终返回压缩后的图片
            String mimeType = "JPEG".equalsIgnoreCase(format) ? "jpeg" : "png";
            byte[] imageBytes = compressedOutput.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            return "data:image/" + mimeType + ";base64," + base64;

        } catch (Exception e) {
            e.printStackTrace();
            return "图片压缩失败: " + e.getMessage();
        }
    }

    /**
     * 图片加载结果
     */
    private static class ImageLoadResult {
        BufferedImage image;
        int originalFileSize;

        ImageLoadResult(BufferedImage image, int originalFileSize) {
            this.image = image;
            this.originalFileSize = originalFileSize;
        }
    }

    /**
     * JPEG有损压缩
     */
    private void compressJPEG(BufferedImage image, ByteArrayOutputStream output, float quality) throws Exception {
        // 处理透明通道：JPEG不支持透明度，需要转换为RGB
        BufferedImage rgbImage = image;
        if (image.getTransparency() != BufferedImage.OPAQUE) {
            // 创建一个不透明的白色背景图像
            rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics2D g2d = rgbImage.createGraphics();
            g2d.setColor(java.awt.Color.WHITE);
            g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();
        }

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
        if (!writers.hasNext()) {
            throw new Exception("无法获取JPEG写入器");
        }

        ImageWriter writer = writers.next();
        ImageWriteParam param = writer.getDefaultWriteParam();

        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
        }

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(output)) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(rgbImage, null, null), param);
        } finally {
            writer.dispose();
        }
    }

    /**
     * PNG无损压缩
     */
    private void compressPNG(BufferedImage image, ByteArrayOutputStream output) throws Exception {
        // PNG使用默认压缩（已经内置）
        ImageIO.write(image, "png", output);
    }

    /**
     * 从输入加载图片
     * 返回图片和原始文件大小
     */
    private ImageLoadResult loadImage(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        input = input.trim();

        try {
            // 检查是否是data URL格式
            if (input.startsWith("data:image/")) {
                String base64Data = extractBase64FromDataURL(input);
                if (base64Data != null) {
                    byte[] imageBytes = Base64.getDecoder().decode(base64Data);
                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
                    return new ImageLoadResult(image, imageBytes.length);
                }
            }

            // 检查是否是Base64数据
            if (isBase64Image(input)) {
                byte[] imageBytes = Base64.getDecoder().decode(input);
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
                return new ImageLoadResult(image, imageBytes.length);
            }

            // 尝试作为文件路径处理
            File file = new File(input);
            if (file.exists() && file.isFile()) {
                BufferedImage image = ImageIO.read(file);
                return new ImageLoadResult(image, (int) file.length());
            }

            // 尝试处理file:// URL格式
            if (input.startsWith("file://")) {
                String filePath = input.substring(7);
                file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    BufferedImage image = ImageIO.read(file);
                    return new ImageLoadResult(image, (int) file.length());
                }
            }

        } catch (Exception e) {
            return null;
        }

        return null;
    }

    /**
     * 从data URL中提取Base64数据
     */
    private String extractBase64FromDataURL(String dataUrl) {
        try {
            int commaIndex = dataUrl.indexOf(',');
            if (commaIndex > 0 && commaIndex < dataUrl.length() - 1) {
                return dataUrl.substring(commaIndex + 1);
            }
        } catch (Exception e) {
            // 忽略错误
        }
        return null;
    }

    /**
     * 判断是否是Base64图片数据
     */
    private boolean isBase64Image(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        // 检查是否只包含Base64字符
        String base64Pattern = "^[A-Za-z0-9+/]+=*$";
        if (!input.matches(base64Pattern)) {
            return false;
        }

        // Base64图片数据通常比较长
        return input.length() > 100;
    }

    @Override
    public boolean returnsImage() {
        // 始终返回图片，对比信息通过额外的字段获取
        return true;
    }

    @Override
    public String getImageData(String input) {
        return execute(input);
    }

    @Override
    public OperationCategory getCategory() {
        return OperationCategory.IMAGE_TOOLS;
    }

    @Override
    public String getDisplayName() {
        return "图片压缩";
    }
}
