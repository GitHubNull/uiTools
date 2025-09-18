package org.oxff.operation;

import org.apache.commons.codec.digest.DigestUtils;
import org.oxff.core.OperationCategory;

/**
 * SHA256哈希操作实现
 */
public class Sha256HashOperation implements Operation {
    @Override
    public String execute(String input) {
        return DigestUtils.sha256Hex(input);
    }
    
    @Override
    public OperationCategory getCategory() {
        return OperationCategory.HASHING;
    }
    
    @Override
    public String getDisplayName() {
        return "SHA256哈希";
    }
}