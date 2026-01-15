package org.oxff.operation.encoding.base32;

import org.apache.commons.codec.binary.Base32;
import org.oxff.operation.Operation;
import org.oxff.core.OperationCategory;
import org.oxff.core.Subcategory;
import org.oxff.core.SubcategoryRegistry;

import java.nio.charset.StandardCharsets;

/**
 * Base32编码操作实现
 */
public class Base32EncodeOperation implements Operation {
    @Override
    public String execute(String input) {
        Base32 base32 = new Base32();
        return base32.encodeAsString(input.getBytes(StandardCharsets.UTF_8));
    }
    
    @Override
    public OperationCategory getCategory() {
        return OperationCategory.ENCODING_DECODING;
    }
    
    @Override
    public String getDisplayName() {
        return "Base32编码";
    }

    @Override
    public Subcategory getSubcategory() {
        return SubcategoryRegistry.getSubcategory("base32");
    }
}