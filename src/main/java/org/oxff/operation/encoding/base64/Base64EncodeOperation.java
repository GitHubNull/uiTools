package org.oxff.operation.encoding.base64;

import org.apache.commons.codec.binary.Base64;
import org.oxff.operation.Operation;
import org.oxff.core.OperationCategory;

import java.nio.charset.StandardCharsets;

/**
 * Base64编码操作实现
 */
public class Base64EncodeOperation implements Operation {
    @Override
    public String execute(String input) {
        return Base64.encodeBase64String(input.getBytes(StandardCharsets.UTF_8));
    }
    
    @Override
    public OperationCategory getCategory() {
        return OperationCategory.ENCODING_DECODING;
    }
    
    @Override
    public String getDisplayName() {
        return "Base64编码";
    }
}