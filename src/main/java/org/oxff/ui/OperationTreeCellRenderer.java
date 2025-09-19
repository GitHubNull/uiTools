package org.oxff.ui;

import org.oxff.operation.Operation;
import org.oxff.core.OperationCategory;

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
                setToolTipText("分类: " + operation.getCategory().getDisplayName());
            } else if (userObject instanceof String) {
                setText((String) userObject);
                // 为分类节点设置工具提示
                String text = (String) userObject;
                if (text.equals("编解码") || text.equals("格式化") || text.equals("哈希")) {
                    setToolTipText(text + "操作分类");
                }
            }
        }
        
        return this;
    }
}