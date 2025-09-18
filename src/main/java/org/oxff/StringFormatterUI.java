package org.oxff;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class StringFormatterUI extends JFrame {
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JComboBox<String> operationComboBox;
    private JButton executeButton;
    private JButton copyInputButton;
    private JButton pasteInputButton;
    private JButton copyOutputButton;
    private JButton clearInputButton;
    private JSplitPane splitPane;
    
    // 定义操作类型
    private static final String[] OPERATIONS = {
        "JSON格式化", 
        "XML格式化", 
        "URL编码", 
        "URL解码", 
        "Base64编码", 
        "Base64解码", 
        "Base32编码", 
        "Base32解码", 
        "Unicode编码", 
        "Unicode解码",
        "MD5哈希",
        "SHA1哈希",
        "SHA256哈希"
    };
    
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
        operationComboBox = new JComboBox<>(OPERATIONS);
        executeButton = new JButton("执行");
        
        topPanel.add(operationLabel);
        topPanel.add(operationComboBox);
        topPanel.add(executeButton);
        
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
        
        inputButtonPanel.add(pasteInputButton);
        inputButtonPanel.add(copyInputButton);
        inputButtonPanel.add(clearInputButton);
        
        inputTextArea = new JTextArea();
        inputTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
        
        inputPanel.add(inputButtonPanel, BorderLayout.NORTH);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);
        
        // 输出区域
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("输出"));
        
        // 输出区域按钮面板
        JPanel outputButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        copyOutputButton = new JButton("复制");
        
        outputButtonPanel.add(copyOutputButton);
        
        outputTextArea = new JTextArea();
        outputTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputTextArea.setLineWrap(true);
        outputTextArea.setWrapStyleWord(true);
        outputTextArea.setEditable(false);
        JScrollPane outputScrollPane = new JScrollPane(outputTextArea);
        
        outputPanel.add(outputButtonPanel, BorderLayout.NORTH);
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);
        
        // 设置分割面板的左右组件
        splitPane.setLeftComponent(inputPanel);
        splitPane.setRightComponent(outputPanel);
        
        // 添加快捷键支持
        setupKeyboardShortcuts();
        
        // 添加组件到主窗口
        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        
        // 添加按钮事件监听器
        setupEventListeners();
    }
    
    /**
     * 设置键盘快捷键
     */
    private void setupKeyboardShortcuts() {
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
     * 设置事件监听器
     */
    private void setupEventListeners() {
        // 执行按钮事件
        executeButton.addActionListener(new ExecuteButtonListener());
        
        // 复制输入按钮事件
        copyInputButton.addActionListener(e -> {
            String text = inputTextArea.getSelectedText();
            if (text == null || text.isEmpty()) {
                text = inputTextArea.getText();
            }
            if (text != null && !text.isEmpty()) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new StringSelection(text), null);
                showMessage("已复制到剪贴板");
            }
        });
        
        // 粘贴输入按钮事件
        pasteInputButton.addActionListener(e -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable contents = clipboard.getContents(null);
            if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    String text = (String) contents.getTransferData(DataFlavor.stringFlavor);
                    inputTextArea.setText(text);
                } catch (Exception ex) {
                    showMessage("粘贴失败: " + ex.getMessage());
                }
            }
        });
        
        // 清空输入按钮事件
        clearInputButton.addActionListener(e -> {
            inputTextArea.setText("");
        });
        
        // 复制输出按钮事件
        copyOutputButton.addActionListener(e -> {
            String text = outputTextArea.getSelectedText();
            if (text == null || text.isEmpty()) {
                text = outputTextArea.getText();
            }
            if (text != null && !text.isEmpty()) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new StringSelection(text), null);
                showMessage("已复制到剪贴板");
            }
        });
    }
    
    /**
     * 显示消息
     */
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "提示", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * 执行按钮事件监听器
     */
    private class ExecuteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = inputTextArea.getText();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(StringFormatterUI.this, "请输入要处理的文本", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String operation = (String) operationComboBox.getSelectedItem();
            String result = performOperation(input, operation);
            outputTextArea.setText(result);
        }
        
        private String performOperation(String input, String operation) {
            switch (operation) {
                case "JSON格式化":
                    return StringUtils.formatJson(input);
                case "XML格式化":
                    return StringUtils.formatXml(input);
                case "URL编码":
                    return StringUtils.urlEncode(input);
                case "URL解码":
                    return StringUtils.urlDecode(input);
                case "Base64编码":
                    return StringUtils.base64Encode(input);
                case "Base64解码":
                    return StringUtils.base64Decode(input);
                case "Base32编码":
                    return StringUtils.base32Encode(input);
                case "Base32解码":
                    return StringUtils.base32Decode(input);
                case "Unicode编码":
                    return StringUtils.unicodeEncode(input);
                case "Unicode解码":
                    return StringUtils.unicodeDecode(input);
                case "MD5哈希":
                    return StringUtils.md5Hash(input);
                case "SHA1哈希":
                    return StringUtils.sha1Hash(input);
                case "SHA256哈希":
                    return StringUtils.sha256Hash(input);
                default:
                    return "不支持的操作: " + operation;
            }
        }
    }
    
    public static void main(String[] args) {
        // 设置外观
        FlatLightLaf.setup();
        
        SwingUtilities.invokeLater(() -> {
            try {
                StringFormatterUI frame = new StringFormatterUI();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}