package org.oxff.ui;

import com.formdev.flatlaf.FlatLightLaf;
import org.oxff.core.OperationFactory;
import org.oxff.core.OperationCategory;
import org.oxff.operation.Operation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// 添加RSyntaxTextArea相关导入
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

public class StringFormatterUI extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(StringFormatterUI.class.getName());
    
    private RSyntaxTextArea inputTextArea;
    private RSyntaxTextArea outputTextArea;
    private JTextArea logTextArea;
    private JComboBox<String> operationComboBox;
    private JButton executeButton;
    private JButton copyInputButton;
    private JButton pasteInputButton;
    private JButton copyOutputButton;
    private JButton clearInputButton;
    private JButton swapButton;
    private JButton wrapButton;
    private JCheckBox wrapCheckBox;
    private JTree operationTree;
    private String selectedOperation;
    @SuppressWarnings("FieldCanBeLocal")
    private JSplitPane splitPane;
    @SuppressWarnings("FieldCanBeLocal")
    private JSplitPane mainSplitPane;
    @SuppressWarnings("FieldCanBeLocal")
    private JSplitPane verticalSplitPane;
    private boolean isWrap = false;
    
    public StringFormatterUI() {
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("字符串格式化和编解码工具");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // 设置布局
        setLayout(new BorderLayout());
        
        // 创建顶部面板
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel operationLabel = new JLabel("选择操作:");
        operationComboBox = new JComboBox<>(OperationFactory.getAllOperationNames());
        operationComboBox.addActionListener(e -> selectedOperation = (String) operationComboBox.getSelectedItem());
        executeButton = new JButton("执行");
        
        topPanel.add(operationLabel);
        topPanel.add(operationComboBox);
        topPanel.add(executeButton);
        
        // 创建主分割面板
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setResizeWeight(0.3); // 左侧占30%
        mainSplitPane.setDividerLocation(0.3);
        mainSplitPane.setDividerSize(10); // 设置分割条的大小
        
        // 创建操作分类树
        JPanel operationPanel = new JPanel(new BorderLayout());
        operationPanel.setBorder(BorderFactory.createTitledBorder("操作分类"));
        operationTree = createOperationTree();
        operationTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        operationTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) operationTree.getLastSelectedPathComponent();
            if (node == null) return;
            
            Object userObject = node.getUserObject();
            if (userObject instanceof Operation) {
                Operation operation = (Operation) userObject;
                selectedOperation = operation.getDisplayName();
                operationComboBox.setSelectedItem(selectedOperation);
            }
        });
        
        JScrollPane treeScrollPane = new JScrollPane(operationTree);
        operationPanel.add(treeScrollPane, BorderLayout.CENTER);
        
        // 创建垂直分割面板（用于分离主内容和日志区域）
        verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        verticalSplitPane.setResizeWeight(0.8); // 主内容占80%
        verticalSplitPane.setDividerSize(10); // 设置分割条的大小
        
        // 创建文本区域面板
        // 使用JSplitPane实现可调整比例的分割面板
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5); // 初始比例为1:1
        splitPane.setDividerLocation(0.5);
        splitPane.setDividerSize(10); // 设置分割条的大小
        
        // 输入区域
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("输入"));
        
        // 输入区域按钮面板
        JPanel inputButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pasteInputButton = new JButton("粘贴");
        copyInputButton = new JButton("复制");
        clearInputButton = new JButton("清空");
        swapButton = new JButton("交换");
        wrapButton = new JButton("自动换行");
        wrapCheckBox = new JCheckBox("自动换行");
        
        inputButtonPanel.add(pasteInputButton);
        inputButtonPanel.add(copyInputButton);
        inputButtonPanel.add(clearInputButton);
        inputButtonPanel.add(swapButton);
        inputButtonPanel.add(wrapCheckBox);
        
        // 使用RSyntaxTextArea替换自定义的LineNumberTextArea
        inputTextArea = new RSyntaxTextArea();
        inputTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inputTextArea.setLineWrap(false);
        inputTextArea.setCodeFoldingEnabled(true);
        
        // 使用RTextScrollPane提供行号显示
        RTextScrollPane inputScrollPane = new RTextScrollPane(inputTextArea);
        
        inputPanel.add(inputButtonPanel, BorderLayout.NORTH);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);
        
        // 输出区域
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("输出"));
        
        // 输出区域按钮面板
        JPanel outputButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        copyOutputButton = new JButton("复制");
        
        outputButtonPanel.add(copyOutputButton);
        
        // 使用RSyntaxTextArea替换自定义的LineNumberTextArea
        outputTextArea = new RSyntaxTextArea();
        outputTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputTextArea.setLineWrap(false);
        outputTextArea.setEditable(false);
        outputTextArea.setCodeFoldingEnabled(true);
        
        // 使用RTextScrollPane提供行号显示
        RTextScrollPane outputScrollPane = new RTextScrollPane(outputTextArea);
        
        outputPanel.add(outputButtonPanel, BorderLayout.NORTH);
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);
        
        // 设置分割面板的左右组件
        splitPane.setLeftComponent(inputPanel);
        splitPane.setRightComponent(outputPanel);
        
        // 设置主分割面板
        mainSplitPane.setLeftComponent(operationPanel);
        mainSplitPane.setRightComponent(splitPane);
        
        // 创建日志面板
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("日志"));
        logTextArea = new JTextArea();
        logTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        logTextArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logTextArea);
        logPanel.add(logScrollPane, BorderLayout.CENTER);
        
        // 设置垂直分割面板
        verticalSplitPane.setTopComponent(mainSplitPane);
        verticalSplitPane.setBottomComponent(logPanel);
        
        // 添加快捷键支持
        setupKeyboardShortcuts();
        
        // 添加组件到主窗口
        add(topPanel, BorderLayout.NORTH);
        add(verticalSplitPane, BorderLayout.CENTER);
        
        // 添加按钮事件监听器
        setupEventListeners();
        
        // 初始化日志
        log("应用程序启动");
    }
    
    private JTree createOperationTree() {
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
     * 设置事件监听器
     */
    private void setupEventListeners() {
        // 执行按钮事件
        executeButton.addActionListener(e -> executeOperation());

        // 复制输入按钮事件
        copyInputButton.addActionListener(e -> {
            String text = inputTextArea.getText();
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(text), null);
            log("已复制输入内容到剪贴板");
        });

        // 粘贴输入按钮事件
        pasteInputButton.addActionListener(e -> {
            try {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                Transferable contents = clipboard.getContents(null);
                if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    String text = (String) contents.getTransferData(DataFlavor.stringFlavor);
                    inputTextArea.setText(text);
                    log("已从剪贴板粘贴内容到输入框");
                }
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "粘贴操作失败", ex);
                log("粘贴操作失败: " + ex.getMessage());
            }
        });

        // 复制输出按钮事件
        copyOutputButton.addActionListener(e -> {
            String text = outputTextArea.getText();
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(text), null);
            log("已复制输出内容到剪贴板");
        });

        // 清空输入按钮事件
        clearInputButton.addActionListener(e -> {
            inputTextArea.setText("");
            log("已清空输入内容");
        });

        // 交换按钮事件
        swapButton.addActionListener(e -> {
            String inputText = inputTextArea.getText();
            String outputText = outputTextArea.getText();
            inputTextArea.setText(outputText);
            outputTextArea.setText(inputText);
            log("已交换输入和输出内容");
        });

        // 自动换行复选框事件
        wrapCheckBox.addActionListener(e -> {
            boolean wrap = wrapCheckBox.isSelected();
            inputTextArea.setLineWrap(wrap);
            outputTextArea.setLineWrap(wrap);
            inputTextArea.setWrapStyleWord(wrap);
            outputTextArea.setWrapStyleWord(wrap);
            log(wrap ? "已启用自动换行" : "已禁用自动换行");
        });
    }

    /**
     * 执行选定的操作
     */
    private void executeOperation() {
        String inputText = inputTextArea.getText();
        if (selectedOperation == null || selectedOperation.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请选择一个操作", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (inputText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入要处理的文本", "提示", JOptionPane.WARNING_MESSAGE);
            log("执行操作失败：输入为空");
            return;
        }

        try {
            Operation operation = OperationFactory.getOperation(selectedOperation);
            if (operation != null) {
                long startTime = System.currentTimeMillis();
                String result = operation.execute(inputText);
                long endTime = System.currentTimeMillis();
                outputTextArea.setText(result);
                log("执行操作: " + selectedOperation + " (耗时: " + (endTime - startTime) + "ms)");
            } else {
                JOptionPane.showMessageDialog(this, "未找到操作: " + selectedOperation, "错误", JOptionPane.ERROR_MESSAGE);
                log("未找到操作: " + selectedOperation);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "执行操作失败: " + selectedOperation, ex);
            outputTextArea.setText("执行失败: " + ex.getMessage());
            log("执行操作失败: " + selectedOperation + " - " + ex.getMessage());
        }
    }

    /**
     * 设置键盘快捷键
     */
    private void setupKeyboardShortcuts() {
        // Ctrl+E 执行操作
        executeButton.setMnemonic(KeyEvent.VK_E);

        // Ctrl+C 复制输入
        copyInputButton.setMnemonic(KeyEvent.VK_C);

        // Ctrl+V 粘贴输入
        pasteInputButton.setMnemonic(KeyEvent.VK_V);

        // Ctrl+Shift+C 复制输出
        copyOutputButton.setMnemonic(KeyEvent.VK_O);

        // Ctrl+Shift+X 清空输入
        clearInputButton.setMnemonic(KeyEvent.VK_X);

        // Ctrl+A 全选输入框
        inputTextArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK), "selectAll");
        inputTextArea.getActionMap().put("selectAll", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputTextArea.selectAll();
            }
        });

        // Ctrl+A 全选输出框
        outputTextArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK), "selectAll");
        outputTextArea.getActionMap().put("selectAll", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outputTextArea.selectAll();
            }
        });
    }

    /**
     * 记录日志
     */
    private void log(String message) {
        String timestamp = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        logTextArea.append("[" + timestamp + "] " + message + "\n");
        logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
    }
    
    public static void main(String[] args) {
        // 设置外观
        FlatLightLaf.setup();
        
        SwingUtilities.invokeLater(() -> {
            try {
                StringFormatterUI frame = new StringFormatterUI();
                frame.setVisible(true);
            } catch (Exception e) {
                Logger.getLogger(StringFormatterUI.class.getName()).log(Level.SEVERE, "启动应用程序时发生错误", e);
            }
        });
    }
}