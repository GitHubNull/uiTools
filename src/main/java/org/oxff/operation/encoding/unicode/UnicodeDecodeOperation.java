package org.oxff.operation.encoding.unicode;

import org.oxff.core.OperationCategory;
import org.oxff.core.Subcategory;
import org.oxff.core.SubcategoryRegistry;
import org.oxff.operation.Operation;

/**
 * Unicode解码操作实现
 */
public class UnicodeDecodeOperation implements Operation {
    @Override
    public String execute(String input) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < input.length()) {
            if (i + 6 <= input.length() && input.substring(i, i + 2).equals("\\u")) {
                try {
                    int codePoint = Integer.parseInt(input.substring(i + 2, i + 6), 16);
                    sb.append((char) codePoint);
                    i += 6;
                } catch (NumberFormatException e) {
                    sb.append(input.charAt(i));
                    i++;
                }
            } else {
                sb.append(input.charAt(i));
                i++;
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
        return "Unicode解码";
    }

    @Override
    public Subcategory getSubcategory() {
        return SubcategoryRegistry.getSubcategory("unicode");
    }
}