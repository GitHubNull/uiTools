package org.oxff.core;

/**
 * 操作子分类，表示二级分类
 * 与OperationCategory组合使用形成两级分类体系
 */
public class Subcategory {
    private final String id;
    private final String displayName;

    public Subcategory(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subcategory that = (Subcategory) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
