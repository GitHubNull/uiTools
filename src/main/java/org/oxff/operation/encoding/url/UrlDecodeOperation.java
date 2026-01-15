package org.oxff.operation.encoding.url;

import org.oxff.core.OperationCategory;
import org.oxff.core.Subcategory;
import org.oxff.core.SubcategoryRegistry;
import org.oxff.operation.Operation;

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

    @Override
    public Subcategory getSubcategory() {
        return SubcategoryRegistry.getSubcategory("url");
    }
}