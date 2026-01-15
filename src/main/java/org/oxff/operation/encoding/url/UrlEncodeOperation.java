package org.oxff.operation.encoding.url;

import org.oxff.core.OperationCategory;
import org.oxff.core.Subcategory;
import org.oxff.core.SubcategoryRegistry;
import org.oxff.operation.Operation;

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

    @Override
    public Subcategory getSubcategory() {
        return SubcategoryRegistry.getSubcategory("url");
    }
}