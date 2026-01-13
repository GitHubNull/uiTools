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
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

// 添加RSyntaxTextArea相关导入
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

public class StringFormatterUI extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(StringFormatterUI.class.getName());
    
    private RSyntaxTextArea inputTextArea;
    private RSyntaxTextArea outputTextArea;
    private RSyntaxTextArea expressionTextArea;
    private JTextArea logTextArea;
    private JComboBox<String> operationComboBox;
    private JButton executeButton;
    private JButton copyInputButton;
    private JButton pasteInputButton;
    private JButton copyOutputButton;
    private JButton clearInputButton;
    private JButton clearExpressionButton;
    private JButton swapButton;
    private JButton wrapButton;
    private JCheckBox wrapCheckBox;
    private JTree operationTree;
    private String selectedOperation;
    @SuppressWarnings("FieldCanBeLocal")
    private JSplitPane splitPane;
    @SuppressWarnings("FieldCanBeLocal")
    private JSplitPane expressionSplitPane;
    @SuppressWarnings("FieldCanBeLocal")
    private JSplitPane mainSplitPane;
    @SuppressWarnings("FieldCanBeLocal")
    private JSplitPane verticalSplitPane;
    private JSplitPane outputExpressionSplitPane; // Added to access the split pane that contains expression and output panels
    private JPanel expressionPanel; // Added to access the expression panel
    private JPanel outputPanel; // Added to access the output panel

    // 图片显示相关组件
    private JLabel imageDisplayLabel;
    private JPanel imagePanel;
    private JScrollPane imageScrollPane;
    private CardLayout outputCardLayout;
    private JPanel outputCardsPanel;
    
    // 自动化操作配置控件
    private JPanel automationConfigPanel;
    private JSpinner delaySecondsSpinner;
    private JSpinner charIntervalMsSpinner;
    private JRadioButton inputSourceRadio;
    private JRadioButton clipboardSourceRadio;

    // 图片输入相关控件
    private JPanel imageInputPanel;
    private JLabel imageInputLabel;
    private JButton selectImageButton;
    private JButton pasteImageButton;
    private JLabel selectedImageLabel;
    private String selectedImagePath;
    
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
        operationComboBox.addActionListener(e -> {
            selectedOperation = (String) operationComboBox.getSelectedItem();
            updateExpressionPanelVisibility();
            updateImageInputPanelVisibility();
        });
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
                updateExpressionPanelVisibility();
            }
        });
        
        JScrollPane treeScrollPane = new JScrollPane(operationTree);
        operationPanel.add(treeScrollPane, BorderLayout.CENTER);
        
        // 创建垂直分割面板（用于分离主内容和日志区域）
        verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        verticalSplitPane.setResizeWeight(0.8); // 主内容占80%
        verticalSplitPane.setDividerSize(10); // 设置分割条的大小
        
        // 创建文本区域面板
        // 使用垂直分割面板来分离输入、表达式和输出区域
        expressionSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        expressionSplitPane.setResizeWeight(0.4); // 输入区域占40%
        expressionSplitPane.setDividerLocation(0.4);
        expressionSplitPane.setDividerSize(8);

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

        // 自动化配置面板
        automationConfigPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        automationConfigPanel.setBorder(BorderFactory.createTitledBorder("自动化输入配置"));
        
        automationConfigPanel.add(new JLabel("延迟时间(秒):"));
        SpinnerModel delayModel = new SpinnerNumberModel(3, 0, 60, 1);
        delaySecondsSpinner = new JSpinner(delayModel);
        delaySecondsSpinner.setPreferredSize(new Dimension(60, 25));
        automationConfigPanel.add(delaySecondsSpinner);
        
        automationConfigPanel.add(Box.createHorizontalStrut(15));
        
        automationConfigPanel.add(new JLabel("字符间隔(毫秒):"));
        SpinnerModel intervalModel = new SpinnerNumberModel(100, 0, 1000, 10);
        charIntervalMsSpinner = new JSpinner(intervalModel);
        charIntervalMsSpinner.setPreferredSize(new Dimension(70, 25));
        automationConfigPanel.add(charIntervalMsSpinner);
        
        automationConfigPanel.add(Box.createHorizontalStrut(15));
        
        automationConfigPanel.add(new JLabel("输入来源:"));
        ButtonGroup sourceGroup = new ButtonGroup();
        inputSourceRadio = new JRadioButton("输入框", true);
        clipboardSourceRadio = new JRadioButton("剪贴板");
        sourceGroup.add(inputSourceRadio);
        sourceGroup.add(clipboardSourceRadio);
        automationConfigPanel.add(inputSourceRadio);
        automationConfigPanel.add(clipboardSourceRadio);
        
        // 默认隐藏自动化配置面板
        automationConfigPanel.setVisible(false);
        inputPanel.add(automationConfigPanel, BorderLayout.SOUTH);

        // 图片输入面板
        imageInputPanel = new JPanel(new BorderLayout());
        imageInputPanel.setBorder(BorderFactory.createTitledBorder("图片输入"));

        // 图片输入按钮面板
        JPanel imageInputButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectImageButton = new JButton("选择图片");
        pasteImageButton = new JButton("粘贴图片");

        imageInputButtonPanel.add(selectImageButton);
        imageInputButtonPanel.add(pasteImageButton);

        // 图片状态标签
        selectedImageLabel = new JLabel("未选择图片");
        selectedImageLabel.setForeground(Color.GRAY);
        selectedImageLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        imageInputPanel.add(imageInputButtonPanel, BorderLayout.NORTH);
        imageInputPanel.add(selectedImageLabel, BorderLayout.CENTER);

        // 默认隐藏图片输入面板
        imageInputPanel.setVisible(false);
        inputPanel.add(imageInputPanel, BorderLayout.SOUTH);

        // 表达式输入区域
        expressionPanel = new JPanel(new BorderLayout());
        expressionPanel.setBorder(BorderFactory.createTitledBorder("XPath/JSONPath表达式 (每行一个)"));

        // 表达式区域按钮面板
        JPanel expressionButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clearExpressionButton = new JButton("清空表达式");
        JLabel expressionHint = new JLabel("支持XPath (XML) 和 JSONPath (JSON) 表达式");
        expressionHint.setFont(expressionHint.getFont().deriveFont(Font.ITALIC, 10f));
        expressionHint.setForeground(Color.GRAY);

        expressionButtonPanel.add(clearExpressionButton);
        expressionButtonPanel.add(expressionHint);

        // 表达式输入文本区域
        expressionTextArea = new RSyntaxTextArea();
        expressionTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        expressionTextArea.setLineWrap(true);
        expressionTextArea.setWrapStyleWord(true);
        expressionTextArea.setCodeFoldingEnabled(false);
        expressionTextArea.setToolTipText("输入XPath或JSONPath表达式，每行一个表达式\nXML示例: //book/title\nJSON示例: $.store.book[*].title");

        RTextScrollPane expressionScrollPane = new RTextScrollPane(expressionTextArea);
        expressionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        expressionScrollPane.setPreferredSize(new Dimension(0, 80)); // 设置初始高度

        expressionPanel.add(expressionButtonPanel, BorderLayout.NORTH);
        expressionPanel.add(expressionScrollPane, BorderLayout.CENTER);

        // 创建输出区域面板（使用水平分割面板）
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5); // 初始比例为1:1
        splitPane.setDividerLocation(0.5);
        splitPane.setDividerSize(10); // 设置分割条的大小

        // 输出区域 - 使用卡片布局支持文本和图片切换
        outputCardLayout = new CardLayout();
        outputCardsPanel = new JPanel(outputCardLayout);

        // 文本输出卡片
        JPanel textOutputCard = new JPanel(new BorderLayout());
        textOutputCard.setBorder(BorderFactory.createTitledBorder("输出"));

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

        textOutputCard.add(outputButtonPanel, BorderLayout.NORTH);
        textOutputCard.add(outputScrollPane, BorderLayout.CENTER);

        // 图片输出卡片
        JPanel imageOutputCard = new JPanel(new BorderLayout());
        imageOutputCard.setBorder(BorderFactory.createTitledBorder("图片输出"));

        imagePanel = new JPanel(new BorderLayout());
        imageDisplayLabel = new JLabel();
        imageDisplayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageDisplayLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageScrollPane = new JScrollPane(imageDisplayLabel);

        imagePanel.add(imageScrollPane, BorderLayout.CENTER);
        imageOutputCard.add(imagePanel, BorderLayout.CENTER);

        // 添加按钮面板到图片输出卡片
        JPanel imageButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton saveImageButton = new JButton("保存图片");
        JButton copyImageButton = new JButton("复制图片");

        saveImageButton.addActionListener(e -> saveImage());
        copyImageButton.addActionListener(e -> copyImage());

        imageButtonPanel.add(saveImageButton);
        imageButtonPanel.add(copyImageButton);
        imageOutputCard.add(imageButtonPanel, BorderLayout.NORTH);

        // 将两个卡片添加到卡片面板
        outputCardsPanel.add(textOutputCard, "TEXT");
        outputCardsPanel.add(imageOutputCard, "IMAGE");

        // 默认显示文本输出
        outputCardLayout.show(outputCardsPanel, "TEXT");

        // 保持原有的outputPanel引用，指向卡片面板
        outputPanel = outputCardsPanel;

        // 设置垂直分割面板：上面是输入区域，下面是表达式和输出区域的水平分割
        outputExpressionSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        outputExpressionSplitPane.setResizeWeight(0.5);
        outputExpressionSplitPane.setDividerLocation(0.5);
        outputExpressionSplitPane.setDividerSize(10);

        outputExpressionSplitPane.setLeftComponent(expressionPanel);
        outputExpressionSplitPane.setRightComponent(outputPanel);

        // 设置expressionSplitPane的组件
        expressionSplitPane.setTopComponent(inputPanel);
        expressionSplitPane.setBottomComponent(outputExpressionSplitPane);
        
        // 设置主分割面板
        mainSplitPane.setLeftComponent(operationPanel);
        mainSplitPane.setRightComponent(expressionSplitPane);
        
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

    /**
     * Checks if the given operation requires expression input (XPath/JSONPath)
     * @param operationName the name of the operation
     * @return true if the operation requires expression input, false otherwise
     */
    private boolean requiresExpressionInput(String operationName) {
        if (operationName == null || operationName.isEmpty()) {
            return false;
        }

        Operation operation = OperationFactory.getOperation(operationName);
        if (operation == null) {
            return false;
        }

        // Only JSON and XML format operations require expression input
        String className = operation.getClass().getSimpleName();
        return "JsonFormatOperation".equals(className) || "XmlFormatOperation".equals(className);
    }

    /**
     * Checks if the given operation is an automation operation
     * @param operationName the name of the operation
     * @return true if the operation is an automation operation, false otherwise
     */
    private boolean isAutomationOperation(String operationName) {
        if (operationName == null || operationName.isEmpty()) {
            return false;
        }

        Operation operation = OperationFactory.getOperation(operationName);
        if (operation == null) {
            return false;
        }

        // Check if operation belongs to AUTOMATION category
        return operation.getCategory() == OperationCategory.AUTOMATION;
    }

    /**
     * Checks if the given operation requires image input (QR code decode)
     * @param operationName the name of the operation
     * @return true if the operation requires image input, false otherwise
     */
    private boolean requiresImageInput(String operationName) {
        if (operationName == null || operationName.isEmpty()) {
            return false;
        }

        Operation operation = OperationFactory.getOperation(operationName);
        if (operation == null) {
            return false;
        }

        // Check if operation is QR code decode operation
        return "QRCodeDecodeOperation".equals(operation.getClass().getSimpleName());
    }

    /**
     * Updates the visibility of the image input panel based on the selected operation
     */
    private void updateImageInputPanelVisibility() {
        boolean showImageInputPanel = requiresImageInput(selectedOperation);

        if (showImageInputPanel) {
            // 显示图片输入面板
            imageInputPanel.setVisible(true);
            // 隐藏普通的文本输入区域
            inputTextArea.setEnabled(false);
            inputTextArea.setBackground(Color.LIGHT_GRAY);
            inputTextArea.setText("请使用下方的图片选择功能选择二维码图片文件");
        } else {
            // 隐藏图片输入面板
            imageInputPanel.setVisible(false);
            // 显示普通的文本输入区域
            inputTextArea.setEnabled(true);
            inputTextArea.setBackground(Color.WHITE);
            if ("请使用下方的图片选择功能选择二维码图片文件".equals(inputTextArea.getText())) {
                inputTextArea.setText("");
            }
        }
    }

    /**
     * Updates the visibility of the expression panel and output panel based on the selected operation
     */
    private void updateExpressionPanelVisibility() {
        boolean showExpressionPanel = requiresExpressionInput(selectedOperation);
        boolean isAutomation = isAutomationOperation(selectedOperation);

        // Update expression panel visibility
        if (showExpressionPanel) {
            // Show the expression panel
            outputExpressionSplitPane.setLeftComponent(expressionPanel);
        } else {
            // Hide the expression panel by removing it
            outputExpressionSplitPane.setLeftComponent(null);
        }

        // Update output panel visibility
        if (isAutomation) {
            // For automation operations, hide the output panel
            outputExpressionSplitPane.setRightComponent(null);
            // Show automation config panel
            automationConfigPanel.setVisible(true);
        } else {
            // For other operations, show the output panel
            outputExpressionSplitPane.setRightComponent(outputPanel);
            // Hide automation config panel
            automationConfigPanel.setVisible(false);
        }

        outputExpressionSplitPane.revalidate();
        outputExpressionSplitPane.repaint();
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

        // 选择图片按钮事件
        selectImageButton.addActionListener(e -> selectImageFile());

        // 粘贴图片按钮事件
        pasteImageButton.addActionListener(e -> pasteImageFromClipboard());

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

        // 清空表达式按钮事件
        clearExpressionButton.addActionListener(e -> {
            expressionTextArea.setText("");
            log("已清空表达式内容");
        });

        // 自动换行复选框事件
        wrapCheckBox.addActionListener(e -> {
            boolean wrap = wrapCheckBox.isSelected();
            inputTextArea.setLineWrap(wrap);
            outputTextArea.setLineWrap(wrap);
            expressionTextArea.setLineWrap(wrap);
            inputTextArea.setWrapStyleWord(wrap);
            outputTextArea.setWrapStyleWord(wrap);
            expressionTextArea.setWrapStyleWord(wrap);
            log(wrap ? "已启用自动换行" : "已禁用自动换行");
        });
    }

    /**
     * 执行选定的操作
     */
    private void executeOperation() {
        String inputText = inputTextArea.getText();
        String expressions = expressionTextArea.getText().trim();
        if (selectedOperation == null || selectedOperation.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请选择一个操作", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Operation operation = OperationFactory.getOperation(selectedOperation);
        if (operation == null) {
            JOptionPane.showMessageDialog(this, "未找到操作: " + selectedOperation, "错误", JOptionPane.ERROR_MESSAGE);
            log("未找到操作: " + selectedOperation);
            return;
        }

        // 对于二维码解析操作，使用图片文件路径或输入框内容
        if (requiresImageInput(selectedOperation)) {
            if (selectedImagePath != null && !selectedImagePath.isEmpty()) {
                inputText = selectedImagePath;
            } else if (inputText.isEmpty() || "请使用下方的图片选择功能选择二维码图片文件".equals(inputText)) {
                JOptionPane.showMessageDialog(this, "请选择二维码图片文件或使用粘贴图片功能", "提示", JOptionPane.WARNING_MESSAGE);
                log("执行操作失败：未选择图片");
                return;
            }
        }
        // 对于自动化操作，不需要输入文本验证，直接执行
        else if (operation.getCategory() != OperationCategory.AUTOMATION && inputText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入要处理的文本", "提示", JOptionPane.WARNING_MESSAGE);
            log("执行操作失败：输入为空");
            return;
        }

        // 对于自动化操作，从 UI 控件读取配置并设置到操作对象
        if (operation.getCategory() == OperationCategory.AUTOMATION) {
            try {
                int delaySeconds = (Integer) delaySecondsSpinner.getValue();
                int charIntervalMs = (Integer) charIntervalMsSpinner.getValue();
                boolean useClipboard = clipboardSourceRadio.isSelected();
                
                // 通过反射设置配置
                java.lang.reflect.Method setDelayMethod = operation.getClass().getMethod("setDelaySeconds", int.class);
                java.lang.reflect.Method setIntervalMethod = operation.getClass().getMethod("setCharIntervalMs", int.class);
                java.lang.reflect.Method setClipboardMethod = operation.getClass().getMethod("setUseClipboard", boolean.class);
                
                setDelayMethod.invoke(operation, delaySeconds);
                setIntervalMethod.invoke(operation, charIntervalMs);
                setClipboardMethod.invoke(operation, useClipboard);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "设置自动化配置失败", e);
            }
        }

        try {
            long startTime = System.currentTimeMillis();
            String result;

            // 检查是否是返回图片的操作
            if (operation.returnsImage()) {
                // 图片操作
                String imageData = operation.getImageData(inputText);
                displayImage(imageData);
                log("生成二维码图片: " + selectedOperation);
                long endTime = System.currentTimeMillis();
                log("执行操作: " + selectedOperation + " (耗时: " + (endTime - startTime) + "ms)");
                return;
            }

            // 对于XML和JSON格式化操作，如果有表达式输入，使用特殊处理
            if (!expressions.isEmpty() && operation.getClass().getSimpleName().equals("XmlFormatOperation")) {
                // 通过反射调用带有表达式参数的方法
                try {
                    java.lang.reflect.Method method = operation.getClass().getMethod("execute", String.class, String.class);
                    result = (String) method.invoke(operation, inputText, expressions);
                } catch (NoSuchMethodException e) {
                    // 如果没有带表达式的方法，使用原始方法
                    result = operation.execute(inputText);
                }
            } else if (!expressions.isEmpty() && operation.getClass().getSimpleName().equals("JsonFormatOperation")) {
                // 通过反射调用带有表达式参数的方法
                try {
                    java.lang.reflect.Method method = operation.getClass().getMethod("execute", String.class, String.class);
                    result = (String) method.invoke(operation, inputText, expressions);
                } catch (NoSuchMethodException e) {
                    // 如果没有并表达式的方法，使用原始方法
                    result = operation.execute(inputText);
                }
            } else {
                result = operation.execute(inputText);
            }

            long endTime = System.currentTimeMillis();

            // 对于自动化操作，结果显示在日志区域；其他操作显示在输出框
            if (operation.getCategory() == OperationCategory.AUTOMATION) {
                log(result);
            } else {
                displayText(result);
            }

            log("执行操作: " + selectedOperation + " (耗时: " + (endTime - startTime) + "ms)" +
                (expressions.isEmpty() ? "" : " [使用表达式过滤]"));
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "执行操作失败: " + selectedOperation, ex);
            if (operation.getCategory() == OperationCategory.AUTOMATION) {
                log("执行操作失败: " + selectedOperation + " - " + ex.getMessage());
            } else {
                outputTextArea.setText("执行失败: " + ex.getMessage());
                log("执行操作失败: " + selectedOperation + " - " + ex.getMessage());
            }
        }
    }

    /**
     * 显示文本结果
     */
    private void displayText(String text) {
        // 切换到文本显示
        outputCardLayout.show(outputCardsPanel, "TEXT");
        outputTextArea.setText(text);
    }

    /**
     * 显示图片结果
     */
    private void displayImage(String imageData) {
        // 切换到图片显示
        outputCardLayout.show(outputCardsPanel, "IMAGE");

        try {
            // 解析data URL，提取Base64数据
            String base64Data = imageData.substring(imageData.indexOf(",") + 1);
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);

            // 创建图片图标
            ImageIcon icon = new ImageIcon(imageBytes);

            // 如果图片太大，进行缩放
            Image image = icon.getImage();
            int originalWidth = image.getWidth(null);
            int originalHeight = image.getHeight(null);

            // 获取显示区域大小，设置最大尺寸
            int maxWidth = Math.min(originalWidth, 400);
            int maxHeight = Math.min(originalHeight, 400);

            // 如果图片太大，进行缩放
            if (originalWidth > maxWidth || originalHeight > maxHeight) {
                double scaleX = (double) maxWidth / originalWidth;
                double scaleY = (double) maxHeight / originalHeight;
                double scale = Math.min(scaleX, scaleY);

                int scaledWidth = (int) (originalWidth * scale);
                int scaledHeight = (int) (originalHeight * scale);

                Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                icon = new ImageIcon(scaledImage);
            }

            imageDisplayLabel.setIcon(icon);
            imageDisplayLabel.setText("");

        } catch (Exception e) {
            imageDisplayLabel.setIcon(null);
            imageDisplayLabel.setText("图片显示失败: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "图片显示失败", e);
        }
    }

    /**
     * 保存图片到文件
     */
    private void saveImage() {
        if (imageDisplayLabel.getIcon() == null) {
            JOptionPane.showMessageDialog(this, "没有可保存的图片", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("qrcode.png"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG图片", "png"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String fileName = fileChooser.getSelectedFile().getAbsolutePath();
                if (!fileName.toLowerCase().endsWith(".png")) {
                    fileName += ".png";
                }

                // 获取图片数据并保存
                ImageIcon icon = (ImageIcon) imageDisplayLabel.getIcon();
                Image image = icon.getImage();

                // 创建BufferedImage并保存
                java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage(
                    image.getWidth(null), image.getHeight(null), java.awt.image.BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = bufferedImage.createGraphics();
                g2d.drawImage(image, 0, 0, null);
                g2d.dispose();

                javax.imageio.ImageIO.write(bufferedImage, "PNG", new java.io.File(fileName));
                log("图片已保存到: " + fileName);
                JOptionPane.showMessageDialog(this, "图片保存成功！", "成功", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "保存图片失败", e);
                JOptionPane.showMessageDialog(this, "保存图片失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 复制图片到剪贴板
     */
    private void copyImage() {
        if (imageDisplayLabel.getIcon() == null) {
            JOptionPane.showMessageDialog(this, "没有可复制的图片", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            ImageIcon icon = (ImageIcon) imageDisplayLabel.getIcon();
            Image image = icon.getImage();

            // 创建Transferable对象
            Transferable transferable = new Transferable() {
                @Override
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[]{DataFlavor.imageFlavor};
                }

                @Override
                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return DataFlavor.imageFlavor.equals(flavor);
                }

                @Override
                public Object getTransferData(DataFlavor flavor) {
                    if (DataFlavor.imageFlavor.equals(flavor)) {
                        return image;
                    }
                    return null;
                }
            };

            // 复制到剪贴板
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(transferable, null);

            log("图片已复制到剪贴板");
            JOptionPane.showMessageDialog(this, "图片已复制到剪贴板！", "成功", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "复制图片失败", e);
            JOptionPane.showMessageDialog(this, "复制图片失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
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
     * 选择图片文件
     */
    private void selectImageFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "图片文件", "png", "jpg", "jpeg", "gif", "bmp"));
        fileChooser.setDialogTitle("选择二维码图片文件");

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedImagePath = fileChooser.getSelectedFile().getAbsolutePath();
            selectedImageLabel.setText("已选择: " + fileChooser.getSelectedFile().getName());
            selectedImageLabel.setForeground(Color.BLACK);
            log("已选择图片文件: " + selectedImagePath);
        }
    }

    /**
     * 从剪贴板粘贴图片
     */
    private void pasteImageFromClipboard() {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable contents = clipboard.getContents(null);

            if (contents != null && contents.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                // 获取剪贴板中的图片
                Image image = (Image) contents.getTransferData(DataFlavor.imageFlavor);

                // 将图片转换为Base64编码
                String imageData = convertImageToBase64(image);
                if (imageData != null) {
                    // 将图片数据放入输入框
                    inputTextArea.setText(imageData);
                    selectedImagePath = null; // 清除文件路径
                    selectedImageLabel.setText("已粘贴图片到输入框");
                    selectedImageLabel.setForeground(Color.BLUE);
                    log("已从剪贴板粘贴图片");
                } else {
                    JOptionPane.showMessageDialog(this, "图片转换失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } else if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                // 如果是文本，尝试作为文件路径处理
                String text = (String) contents.getTransferData(DataFlavor.stringFlavor);
                if (isImageFile(text)) {
                    File file = new File(text);
                    if (file.exists()) {
                        selectedImagePath = text;
                        selectedImageLabel.setText("已选择: " + file.getName());
                        selectedImageLabel.setForeground(Color.BLACK);
                        log("已从剪贴板选择图片文件: " + selectedImagePath);
                    } else {
                        JOptionPane.showMessageDialog(this, "文件不存在: " + text, "错误", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "剪贴板中没有图片或有效的图片路径", "提示", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "剪贴板中没有图片", "提示", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "粘贴图片失败", e);
            JOptionPane.showMessageDialog(this, "粘贴图片失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 将图片转换为Base64编码
     */
    private String convertImageToBase64(Image image) {
        try {
            // 创建BufferedImage
            BufferedImage bufferedImage = new BufferedImage(
                image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();

            // 转换为PNG格式的字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "PNG", outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            // 转换为Base64
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            return "data:image/png;base64," + base64;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "图片转换失败", e);
            return null;
        }
    }

    /**
     * 判断是否为图片文件
     */
    private boolean isImageFile(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        String lowerPath = path.toLowerCase();
        return lowerPath.endsWith(".png") || lowerPath.endsWith(".jpg") ||
               lowerPath.endsWith(".jpeg") || lowerPath.endsWith(".gif") ||
               lowerPath.endsWith(".bmp");
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