package org.oxff.operation.encoding.unicode;

import org.oxff.core.OperationCategory;
import org.oxff.core.Subcategory;
import org.oxff.core.SubcategoryRegistry;
import org.oxff.operation.Operation;

/**
 * Unicode编码操作实现
 */
public class UnicodeEncodeOperation implements Operation {
    @Override
    public String execute(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c < 128) {
                sb.append(c);
            } else {
                sb.append("\\u").append(String.format("%04x", (int) c));
            }
        }
        return sb.toString();
    }
    
    @Override
    public OperationCategory getCategory() {
        return OperationCategory.ENCODING_DECODING;
    }
    
    @Override
    public String getDisplayName() {
        return "Unicode编码";
    }

    @Override
    public Subcategory getSubcategory() {
        return SubcategoryRegistry.getSubcategory("unicode");
    }
}