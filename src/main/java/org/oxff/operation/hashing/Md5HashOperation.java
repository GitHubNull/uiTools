package org.oxff.operation.hashing;

import org.apache.commons.codec.digest.DigestUtils;
import org.oxff.operation.Operation;
import org.oxff.core.OperationCategory;

/**
 * MD5哈希操作实现
 */
public class Md5HashOperation implements Operation {
    @Override
    public String execute(String input) {
        return DigestUtils.md5Hex(input);
    }
    
    @Override
    public OperationCategory getCategory() {
        return OperationCategory.HASHING;
    }
    
    @Override
    public String getDisplayName() {
        return "MD5哈希";
    }
}