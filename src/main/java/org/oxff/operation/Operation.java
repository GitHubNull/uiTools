package org.oxff.operation;

import org.oxff.core.OperationCategory;

/**
 * 操作接口，所有字符串操作都需要实现此接口
 */
public interface Operation {
    /**
     * 执行操作
     * @param input 输入字符串
     * @return 处理结果
     */
    String execute(String input);
    
    /**
     * 获取操作分类
     * @return 操作分类
     */
    OperationCategory getCategory();
    
    /**
     * 获取操作显示名称
     * @return 显示名称
     */
    String getDisplayName();

    /**
     * 判断是否返回图片数据
     * @return true 如果操作返回图片数据，false 如果返回文本数据
     */
    default boolean returnsImage() {
        return false;
    }

    /**
     * 获取图片数据（当returnsImage为true时）
     * @param input 输入字符串
     * @return 图片数据的Base64编码（data URL格式）
     */
    default String getImageData(String input) {
        return null;
    }
}