package org.oxff.operation.encoding.hex;

import org.oxff.core.OperationCategory;
import org.oxff.operation.Operation;

import java.nio.charset.StandardCharsets;

/**
 * Hex编码操作实现
 */
public class HexEncodeOperation implements Operation {
    @Override
    public String execute(String input) {
        try {
            byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
            StringBuilder hexString = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString().toUpperCase();
        } catch (Exception e) {
            return "Hex编码错误: " + e.getMessage();
        }
    }
    
    @Override
    public OperationCategory getCategory() {
        return OperationCategory.ENCODING_DECODING;
    }
    
    @Override
    public String getDisplayName() {
        return "Hex编码";
    }
}