package org.oxff.operation;

import org.oxff.core.OperationCategory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * URL编码操作实现
 */
public class UrlEncodeOperation implements Operation {
    @Override
    public String execute(String input) {
        return URLEncoder.encode(input, StandardCharsets.UTF_8);
    }
    
    @Override
    public OperationCategory getCategory() {
        return OperationCategory.ENCODING_DECODING;
    }
    
    @Override
    public String getDisplayName() {
        return "URL编码";
    }
}