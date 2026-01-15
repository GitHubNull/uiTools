package org.oxff.operation.encoding.hex;

import org.oxff.core.OperationCategory;
import org.oxff.core.Subcategory;
import org.oxff.core.SubcategoryRegistry;
import org.oxff.operation.Operation;

import java.nio.charset.StandardCharsets;

/**
 * Hex解码操作实现
 */
public class HexDecodeOperation implements Operation {
    @Override
    public String execute(String input) {
        try {
            // 移除可能存在的空格
            String cleanInput = input.replaceAll("\\s+", "");
            
            // 检查输入长度是否为偶数
            if (cleanInput.length() % 2 != 0) {
                return "Hex解码错误: 输入的Hex字符串长度必须为偶数";
            }
            
            // 检查是否只包含有效的十六进制字符
            if (!cleanInput.matches("^[0-9A-Fa-f]+$")) {
                return "Hex解码错误: 输入包含非十六进制字符";
            }
            
            byte[] bytes = new byte[cleanInput.length() / 2];
            for (int i = 0; i < bytes.length; i++) {
                String hexPair = cleanInput.substring(i * 2, (i * 2) + 2);
                bytes[i] = (byte) Integer.parseInt(hexPair, 16);
            }
            
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "Hex解码错误: " + e.getMessage();
        }
    }
    
    @Override
    public OperationCategory getCategory() {
        return OperationCategory.ENCODING_DECODING;
    }
    
    @Override
    public String getDisplayName() {
        return "Hex解码";
    }

    @Override
    public Subcategory getSubcategory() {
        return SubcategoryRegistry.getSubcategory("hex");
    }
}