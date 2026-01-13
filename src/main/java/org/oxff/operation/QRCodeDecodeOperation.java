package org.oxff.operation;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.oxff.core.OperationCategory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码解析操作实现
 */
public class QRCodeDecodeOperation implements Operation {

    @Override
    public String execute(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "请输入二维码图片数据或图片文件路径";
        }

        try {
            BufferedImage image;
            String trimmedInput = input.trim();

            // 判断输入类型并处理
            if (isBase64ImageData(trimmedInput)) {
                // Base64图片数据
                String base64Data = extractBase64Data(trimmedInput);
                byte[] imageBytes = Base64.getDecoder().decode(base64Data);
                image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            } else if (isImageFile(trimmedInput)) {
                // 图片文件路径
                File imageFile = new File(trimmedInput);
                if (!imageFile.exists()) {
                    return "图片文件不存在: " + trimmedInput;
                }
                image = ImageIO.read(imageFile);
            } else if (trimmedInput.startsWith("file://")) {
                // 文件URL格式
                String filePath = trimmedInput.substring(7);
                File imageFile = new File(filePath);
                if (!imageFile.exists()) {
                    return "图片文件不存在: " + filePath;
                }
                image = ImageIO.read(imageFile);
            } else {
                // 尝试作为Base64纯文本处理
                try {
                    byte[] imageBytes = Base64.getDecoder().decode(trimmedInput);
                    image = ImageIO.read(new ByteArrayInputStream(imageBytes));
                } catch (IllegalArgumentException e) {
                    return "无法识别的输入格式。请输入Base64图片数据、data URL、或图片文件路径";
                }
            }

            if (image == null) {
                return "无法识别图片格式，请确保输入的是有效的PNG/JPG/JPEG/GIF图片";
            }

            // 解析二维码
            String decodedText = decodeQRCode(image);
            return decodedText;

        } catch (IllegalArgumentException e) {
            return "Base64解码失败: " + e.getMessage();
        } catch (IOException e) {
            return "图片读取失败: " + e.getMessage();
        } catch (NotFoundException e) {
            return "未找到二维码或二维码损坏: " + e.getMessage();
        } catch (Exception e) {
            return "解析二维码时发生错误: " + e.getMessage();
        }
    }

    /**
     * 判断输入是否为Base64图片数据（包括data URL）
     */
    private boolean isBase64ImageData(String input) {
        return input.startsWith("data:image/") ||
               (input.length() > 100 && input.matches("^[A-Za-z0-9+/]*={0,2}$"));
    }

    /**
     * 判断输入是否为图片文件路径
     */
    private boolean isImageFile(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        // 检查文件扩展名
        String lowerInput = input.toLowerCase();
        return lowerInput.endsWith(".png") ||
               lowerInput.endsWith(".jpg") ||
               lowerInput.endsWith(".jpeg") ||
               lowerInput.endsWith(".gif") ||
               lowerInput.endsWith(".bmp");
    }

    /**
     * 从data URL或纯Base64字符串中提取Base64数据
     */
    private String extractBase64Data(String input) {
        // 如果是data URL格式，提取逗号后的部分
        if (input.contains(",")) {
            return input.substring(input.indexOf(",") + 1);
        }
        // 否则直接返回（假设是纯Base64字符串）
        return input;
    }

    /**
     * 解析二维码图片
     */
    private String decodeQRCode(BufferedImage image) throws NotFoundException {
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        // 设置解码参数
        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");

        MultiFormatReader reader = new MultiFormatReader();
        Result result = reader.decode(bitmap, hints);

        return result.getText();
    }

    @Override
    public OperationCategory getCategory() {
        return OperationCategory.QRCODE;
    }

    @Override
    public String getDisplayName() {
        return "解析二维码";
    }
}