package org.oxff.core;

import java.util.HashMap;
import java.util.Map;

/**
 * 子分类注册表，维护所有可用的子分类
 */
public class SubcategoryRegistry {
    private static final Map<String, Subcategory> subcategories = new HashMap<>();

    static {
        // 编解码子分类
        register(new Subcategory("base64", "Base64"));
        register(new Subcategory("base32", "Base32"));
        register(new Subcategory("hex", "Hex"));
        register(new Subcategory("url", "URL"));
        register(new Subcategory("unicode", "Unicode"));
        register(new Subcategory("jwt", "JWT"));
        register(new Subcategory("image", "图片编码"));

        // 时间戳子分类
        register(new Subcategory("conversion", "时间戳转换"));
        register(new Subcategory("format", "时间戳格式化"));
        register(new Subcategory("utc", "UTC时间"));

        // 身份证子分类
        register(new Subcategory("idcard", "身份证"));

        // 其他分类可以只有一个默认子分类
        register(new Subcategory("default", "默认"));
    }

    private static void register(Subcategory subcategory) {
        subcategories.put(subcategory.getId(), subcategory);
    }

    public static Subcategory getSubcategory(String id) {
        return subcategories.get(id);
    }

    public static Subcategory getDefaultSubcategory() {
        return subcategories.get("default");
    }
}
