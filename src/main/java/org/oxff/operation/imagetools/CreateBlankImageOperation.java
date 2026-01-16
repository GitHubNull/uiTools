package org.oxff.operation.imagetools;

import org.oxff.core.OperationCategory;
import org.oxff.core.SubcategoryRegistry;
import org.oxff.operation.Operation;
import org.oxff.util.ImageSizeSpecs;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

/**
 * 生成空白图片操作
 * 可指定像素或寸数规格，支持自定义背景颜色
 */
public class CreateBlankImageOperation implements Operation {

    private int width;
    private int height;
    private String sizeSpec;  // 寸数规格名称
    private String backgroundColor = "FFFFFF";  // 默认白色，十六进制RGB
    private String format = "PNG";  // 输出格式

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

    public void setBackgroundColor(String color) {
        this.backgroundColor = color;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String execute(String input) {
        try {
            // 1. 根据sizeSpec或width/height确定尺寸
            int actualWidth = width;
            int actualHeight = height;

            if (sizeSpec != null && !sizeSpec.isEmpty()) {
                ImageSizeSpecs.SizeSpec spec = ImageSizeSpecs.getSpec(sizeSpec);
                if (spec != null) {
                    actualWidth = spec.getWidthPixels();
                    actualHeight = spec.getHeightPixels();
                }
            }

            // 确保尺寸有效
            if (actualWidth <= 0) {
                actualWidth = 295;  // 默认1寸宽度
            }
            if (actualHeight <= 0) {
                actualHeight = 413;  // 默认1寸高度
            }

            // 2. 解析背景颜色
            Color bgColor = parseColor(backgroundColor);

            // 3. 创建BufferedImage并填充指定颜色
            BufferedImage image = new BufferedImage(actualWidth, actualHeight, BufferedImage.TYPE_INT_RGB);

            // 填充背景色
            for (int x = 0; x < actualWidth; x++) {
                for (int y = 0; y < actualHeight; y++) {
                    image.setRGB(x, y, bgColor.getRGB());
                }
            }

            // 4. 转换为Base64 data URL格式
            String imageFormat = format != null && !format.isEmpty() ? format.toLowerCase() : "png";

            // 确保格式有效
            if (!imageFormat.equals("png") && !imageFormat.equals("jpeg") && !imageFormat.equals("jpg")) {
                imageFormat = "png";
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, imageFormat, outputStream);

            byte[] imageBytes = outputStream.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(imageBytes);

            String mimeType = imageFormat.equals("jpeg") || imageFormat.equals("jpg") ? "jpeg" : "png";
            return "data:image/" + mimeType + ";base64," + base64;

        } catch (Exception e) {
            return "生成空白图片失败: " + e.getMessage();
        }
    }

    /**
     * 解析十六进制颜色字符串
     * @param colorHex 十六进制颜色字符串（如"FF0000"）
     * @return Color对象
     */
    private Color parseColor(String colorHex) {
        if (colorHex == null || colorHex.isEmpty()) {
            return Color.WHITE;
        }

        try {
            // 移除可能的前缀#
            colorHex = colorHex.replace("#", "");

            // 如果是3位简写，扩展为6位
            if (colorHex.length() == 3) {
                colorHex = "" + colorHex.charAt(0) + colorHex.charAt(0)
                         + colorHex.charAt(1) + colorHex.charAt(1)
                         + colorHex.charAt(2) + colorHex.charAt(2);
            }

            // 解析RGB
            if (colorHex.length() == 6) {
                int red = Integer.parseInt(colorHex.substring(0, 2), 16);
                int green = Integer.parseInt(colorHex.substring(2, 4), 16);
                int blue = Integer.parseInt(colorHex.substring(4, 6), 16);
                return new Color(red, green, blue);
            }

            // 如果包含Alpha通道（8位）
            if (colorHex.length() == 8) {
                int red = Integer.parseInt(colorHex.substring(0, 2), 16);
                int green = Integer.parseInt(colorHex.substring(2, 4), 16);
                int blue = Integer.parseInt(colorHex.substring(4, 6), 16);
                int alpha = Integer.parseInt(colorHex.substring(6, 8), 16);
                return new Color(red, green, blue, alpha);
            }
        } catch (Exception e) {
            // 解析失败，返回白色
        }

        return Color.WHITE;
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
        return "生成空白图片";
    }
}
