package org.oxff.operation;

import org.apache.commons.codec.digest.DigestUtils;
import org.oxff.core.OperationCategory;

/**
 * SHA1哈希操作实现
 */
public class Sha1HashOperation implements Operation {
    @Override
    public String execute(String input) {
        return DigestUtils.sha1Hex(input);
    }
    
    @Override
    public OperationCategory getCategory() {
        return OperationCategory.HASHING;
    }
    
    @Override
    public String getDisplayName() {
        return "SHA1哈希";
    }
}