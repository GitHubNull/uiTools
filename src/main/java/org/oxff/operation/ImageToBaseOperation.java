package org.oxff.operation;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;
import org.oxff.core.OperationCategory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * 图片转Base编码操作实现
 * 支持将图片文件转换为Base64或Base32编码
 */
public class ImageToBaseOperation implements Operation {

    /**
     * 编码类型枚举
     */
    public enum EncodingType {
        BASE64("Base64", "data:image/png;base64,"),
        BASE32("Base32", "data:image/png;base32,");

        private final String displayName;
        private final String dataUrlPrefix;

        EncodingType(String displayName, String dataUrlPrefix) {
            this.displayName = displayName;
            this.dataUrlPrefix = dataUrlPrefix;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDataUrlPrefix() {
            return dataUrlPrefix;
        }
    }

    // 当前编码类型（由UI设置）
    private EncodingType encodingType = EncodingType.BASE64;

    /**
     * 设置编码类型
     * @param typeName 编码类型名称 ("Base64" 或 "Base32")
     */
    public void setEncodingType(String typeName) {
        for (EncodingType type : EncodingType.values()) {
            if (type.getDisplayName().equals(typeName)) {
                this.encodingType = type;
                break;
            }
        }
    }

    @Override
    public String execute(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "请输入图片文件路径";
        }

        try {
            File imageFile = new File(input.trim());
            if (!imageFile.exists()) {
                return "图片文件不存在: " + input;
            }

            // 读取图片文件
            BufferedImage image = ImageIO.read(imageFile);
            if (image == null) {
                return "无法识别图片格式，请确保输入的是有效的PNG/JPG/JPEG/GIF/BMP图片";
            }

            // 转换为字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            // 根据编码类型进行编码
            String encoded;
            if (encodingType == EncodingType.BASE32) {
                Base32 base32 = new Base32();
                encoded = base32.encodeAsString(imageBytes);
            } else {
                encoded = Base64.encodeBase64String(imageBytes);
            }

            // 返回data URL格式
            return encodingType.getDataUrlPrefix() + encoded;

        } catch (Exception e) {
            return "处理图片失败: " + e.getMessage();
        }
    }

    @Override
    public OperationCategory getCategory() {
        return OperationCategory.ENCODING_DECODING;
    }

    @Override
    public String getDisplayName() {
        return "图片转Base编码";
    }
}
