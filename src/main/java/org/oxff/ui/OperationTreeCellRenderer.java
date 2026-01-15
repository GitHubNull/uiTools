package org.oxff.ui;

import org.oxff.operation.Operation;
import org.oxff.core.OperationCategory;
import org.oxff.core.Subcategory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * 自定义树节点渲染器，用于正确显示操作名称
 */
public class OperationTreeCellRenderer extends DefaultTreeCellRenderer {
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, 
            boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        
        if (value instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();
            
            // 设置节点文本
            if (userObject instanceof Operation) {
                Operation operation = (Operation) userObject;
                setText(operation.getDisplayName());
                OperationCategory category = operation.getCategory();
                Subcategory subcategory = operation.getSubcategory();

                if (subcategory != null) {
                    setToolTipText("分类: " + category.getDisplayName() + " > " + subcategory.getDisplayName());
                } else {
                    setToolTipText("分类: " + category.getDisplayName());
                }
            } else if (userObject instanceof String) {
                setText((String) userObject);
                // 为分类节点设置工具提示
                String text = (String) userObject;
                if (text.equals("编解码") || text.equals("格式化") || text.equals("哈希") ||
                    text.equals("自动化操作") || text.equals("二维码") || text.equals("时间戳") || text.equals("生成工具")) {
                    setToolTipText(text + "操作分类");
                } else if (text.contains(" > ")) {
                    // 子分类节点
                    setToolTipText(text);
                }
            }
        }
        
        return this;
    }
}