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
}