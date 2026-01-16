package org.oxff.operation.imagetools;

import org.oxff.core.OperationCategory;
import org.oxff.core.SubcategoryRegistry;
import org.oxff.operation.Operation;
import org.oxff.util.ImageSizeSpecs;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Base64;

/**
 * 图片尺寸转换操作
 * 可指定像素或寸数规格，支持保持宽高比
 */
public class ImageResizeOperation implements Operation {

    private int width;
    private int height;
    private String sizeSpec;  // 寸数规格名称
    private boolean maintainRatio = true;  // 是否保持宽高比

    // Setter方法（通过反射调用）
    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setSizeSpec(String spec) {
        this.sizeSpec = spec;
    }

    public void setMaintainRatio(boolean ratio) {
        this.maintainRatio = ratio;
    }

    @Override
    public String execute(String input) {
        try {
            // 1. 解析输入（文件路径或Base64）
            BufferedImage sourceImage = loadImage(input);

            if (sourceImage == null) {
                return "无法加载图片，请检查输入是否为有效的图片文件路径或Base64数据";
            }

            // 2. 确定目标尺寸
            int targetWidth = width;
            int targetHeight = height;

            if (sizeSpec != null && !sizeSpec.isEmpty()) {
                ImageSizeSpecs.SizeSpec spec = ImageSizeSpecs.getSpec(sizeSpec);
                if (spec != null) {
                    targetWidth = spec.getWidthPixels();
                    targetHeight = spec.getHeightPixels();
                }
            }

            // 如果没有指定尺寸，使用原图尺寸
            if (targetWidth <= 0 && targetHeight <= 0) {
                targetWidth = sourceImage.getWidth();
                targetHeight = sourceImage.getHeight();
            } else if (targetWidth <= 0) {
                // 只指定了高度
                if (maintainRatio) {
                    double ratio = (double) targetHeight / sourceImage.getHeight();
                    targetWidth = (int) (sourceImage.getWidth() * ratio);
                } else {
                    targetWidth = sourceImage.getWidth();
                }
            } else if (targetHeight <= 0) {
                // 只指定了宽度
                if (maintainRatio) {
                    double ratio = (double) targetWidth / sourceImage.getWidth();
                    targetHeight = (int) (sourceImage.getHeight() * ratio);
                } else {
                    targetHeight = sourceImage.getHeight();
                }
            } else if (maintainRatio) {
                // 两个都指定了，保持宽高比
                double sourceRatio = (double) sourceImage.getWidth() / sourceImage.getHeight();
                double targetRatio = (double) targetWidth / targetHeight;

                if (Math.abs(sourceRatio - targetRatio) > 0.01) {
                    // 宽高比不匹配，按比例调整
                    if (sourceRatio > targetRatio) {
                        // 原图更宽，以宽度为准
                        targetHeight = (int) (targetWidth / sourceRatio);
                    } else {
                        // 原图更高，以高度为准
                        targetWidth = (int) (targetHeight * sourceRatio);
                    }
                }
            }

            // 3. 执行缩放
            Image scaledImage = sourceImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);

            // 4. 转换为BufferedImage
            BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics2D g2d = resizedImage.createGraphics();
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.dispose();

            // 5. 转换为Base64 data URL格式
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "png", outputStream);

            byte[] imageBytes = outputStream.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(imageBytes);

            return "data:image/png;base64," + base64;

        } catch (Exception e) {
            e.printStackTrace();
            return "图片尺寸转换失败: " + e.getMessage();
        }
    }

    /**
     * 从输入加载图片
     * 支持文件路径、Base64数据、data URL格式
     */
    private BufferedImage loadImage(String input) {
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
                    return ImageIO.read(new ByteArrayInputStream(imageBytes));
                }
            }

            // 检查是否是Base64数据
            if (isBase64Image(input)) {
                byte[] imageBytes = Base64.getDecoder().decode(input);
                return ImageIO.read(new ByteArrayInputStream(imageBytes));
            }

            // 尝试作为文件路径处理
            File file = new File(input);
            if (file.exists() && file.isFile()) {
                return ImageIO.read(file);
            }

            // 尝试处理file:// URL格式
            if (input.startsWith("file://")) {
                String filePath = input.substring(7);
                file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    return ImageIO.read(file);
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
        return "图片尺寸转换";
    }
}
