package org.oxff.ui.builder;

import org.oxff.core.OperationCategory;
import org.oxff.core.OperationFactory;
import org.oxff.operation.Operation;
import org.oxff.ui.OperationTreeCellRenderer;
import org.oxff.ui.handler.EventHandler;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.List;

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
            List<Operation> operations = OperationFactory.getOperationsByCategory(category);

            for (Operation operation : operations) {
                DefaultMutableTreeNode operationNode = new DefaultMutableTreeNode(operation.getDisplayName());
                operationNode.setUserObject(operation);
                categoryNode.add(operationNode);
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
    public JPanel buildOperationPanel(JTree operationTree, EventHandler eventHandler, JComboBox<String> operationComboBox) {
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
                operationComboBox.setSelectedItem(selected);
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
