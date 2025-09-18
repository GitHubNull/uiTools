package org.oxff.operation;

import org.oxff.core.OperationCategory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * URL解码操作实现
 */
public class UrlDecodeOperation implements Operation {
    @Override
    public String execute(String input) {
        return URLDecoder.decode(input, StandardCharsets.UTF_8);
    }
    
    @Override
    public OperationCategory getCategory() {
        return OperationCategory.ENCODING_DECODING;
    }
    
    @Override
    public String getDisplayName() {
        return "URL解码";
    }
}