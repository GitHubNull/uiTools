package org.oxff.operation;

import org.apache.commons.codec.binary.Base32;
import org.oxff.core.OperationCategory;

import java.nio.charset.StandardCharsets;

/**
 * Base32解码操作实现
 */
public class Base32DecodeOperation implements Operation {
    @Override
    public String execute(String input) {
        try {
            Base32 base32 = new Base32();
            byte[] decodedBytes = base32.decode(input);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "Base32解码错误: " + e.getMessage();
        }
    }
    
    @Override
    public OperationCategory getCategory() {
        return OperationCategory.ENCODING_DECODING;
    }
    
    @Override
    public String getDisplayName() {
        return "Base32解码";
    }
}