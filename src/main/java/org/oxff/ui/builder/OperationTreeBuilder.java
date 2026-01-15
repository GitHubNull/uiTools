package org.oxff.ui.builder;

import org.oxff.core.OperationCategory;
import org.oxff.core.OperationFactory;
import org.oxff.core.Subcategory;
import org.oxff.core.SubcategoryRegistry;
import org.oxff.operation.Operation;
import org.oxff.ui.OperationTreeCellRenderer;
import org.oxff.ui.handler.EventHandler;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * 操作分类树构建器
 * 负责构建操作分类树和操作面板
 */
public class OperationTreeBuilder {

    /**
     * 构建操作分类树
     */
    public JTree buildOperationTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("所有操作");

        // 添加各个分类节点
        for (OperationCategory category : OperationCategory.values()) {
            DefaultMutableTreeNode categoryNode = new DefaultMutableTreeNode(category.getDisplayName());

            // 获取该分类下的所有子分类
            Map<Subcategory, List<Operation>> subcategoryMap =
                OperationFactory.getOperationsByCategoryWithSubcategory(category);

            // 如果只有一个默认子分类，直接添加操作
            if (subcategoryMap.size() == 1 &&
                subcategoryMap.containsKey(SubcategoryRegistry.getDefaultSubcategory())) {

                List<Operation> operations = subcategoryMap.get(SubcategoryRegistry.getDefaultSubcategory());
                for (Operation operation : operations) {
                    DefaultMutableTreeNode operationNode = new DefaultMutableTreeNode(operation.getDisplayName());
                    operationNode.setUserObject(operation);
                    categoryNode.add(operationNode);
                }
            } else {
                // 多个子分类，创建子分类节点
                for (Map.Entry<Subcategory, List<Operation>> entry : subcategoryMap.entrySet()) {
                    Subcategory subcategory = entry.getKey();
                    List<Operation> operations = entry.getValue();

                    // 创建子分类节点
                    DefaultMutableTreeNode subcategoryNode = new DefaultMutableTreeNode(
                        category.getDisplayName() + " > " + subcategory.getDisplayName()
                    );

                    for (Operation operation : operations) {
                        DefaultMutableTreeNode operationNode = new DefaultMutableTreeNode(operation.getDisplayName());
                        operationNode.setUserObject(operation);
                        subcategoryNode.add(operationNode);
                    }

                    categoryNode.add(subcategoryNode);
                }
            }

            root.add(categoryNode);
        }

        JTree tree = new JTree(new DefaultTreeModel(root));
        tree.setCellRenderer(new OperationTreeCellRenderer());
        return tree;
    }

    /**
     * 构建操作分类面板
     */
    public JPanel buildOperationPanel(JTree operationTree, EventHandler eventHandler) {
        JPanel operationPanel = new JPanel(new BorderLayout());
        operationPanel.setBorder(BorderFactory.createTitledBorder("操作分类"));

        operationTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        operationTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) operationTree.getLastSelectedPathComponent();
            if (node == null) return;

            Object userObject = node.getUserObject();
            if (userObject instanceof Operation) {
                Operation operation = (Operation) userObject;
                String selected = operation.getDisplayName();
                if (eventHandler != null) {
                    eventHandler.handleOperationSelection(selected);
                }
            }
        });

        JScrollPane treeScrollPane = new JScrollPane(operationTree);
        operationPanel.add(treeScrollPane, BorderLayout.CENTER);
        return operationPanel;
    }
}
