package org.oxff.operation.encoding.base64;

import org.apache.commons.codec.binary.Base64;
import org.oxff.operation.Operation;
import org.oxff.core.OperationCategory;

import java.nio.charset.StandardCharsets;

/**
 * Base64解码操作实现
 */
public class Base64DecodeOperation implements Operation {
    @Override
    public String execute(String input) {
        try {
            byte[] decodedBytes = Base64.decodeBase64(input);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "Base64解码错误: " + e.getMessage();
        }
    }
    
    @Override
    public OperationCategory getCategory() {
        return OperationCategory.ENCODING_DECODING;
    }
    
    @Override
    public String getDisplayName() {
        return "Base64解码";
    }
}