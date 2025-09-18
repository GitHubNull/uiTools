package org.oxff.core;

/**
 * 操作分类枚举
 */
public enum OperationCategory {
    ENCODING_DECODING("编解码"),
    FORMATTING("格式化"),
    HASHING("哈希");

    private final String displayName;

    OperationCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}