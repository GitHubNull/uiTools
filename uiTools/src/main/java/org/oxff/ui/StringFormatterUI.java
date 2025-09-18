private JTree createOperationTree() {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("所有操作");
    
    // 添加各个分类节点
    for (OperationCategory category : OperationCategory.values()) {
        DefaultMutableTreeNode categoryNode = new DefaultMutableTreeNode(category.getDisplayName());
        List<Operation> operations = OperationFactory.getOperationsByCategory(category);
        
        for (Operation operation : operations) {
            // 使用操作的显示名称作为节点文本
            DefaultMutableTreeNode operationNode = new DefaultMutableTreeNode(operation.getDisplayName());
            operationNode.setUserObject(operation); // 仍然保留原始对象用于后续处理
            categoryNode.add(operationNode);
        }
        
        root.add(categoryNode);
    }
    
    return new JTree(new DefaultTreeModel(root));
}