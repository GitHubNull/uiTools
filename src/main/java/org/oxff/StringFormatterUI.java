package org.oxff;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StringFormatterUI extends JFrame {
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JComboBox<String> operationComboBox;
    private JButton executeButton;
    
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
        setSize(800, 600);
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
        JPanel textPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        textPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
        
        // 输入区域
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("输入"));
        inputTextArea = new JTextArea();
        inputTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);
        
        // 输出区域
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("输出"));
        outputTextArea = new JTextArea();
        outputTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputTextArea.setLineWrap(true);
        outputTextArea.setWrapStyleWord(true);
        outputTextArea.setEditable(false);
        JScrollPane outputScrollPane = new JScrollPane(outputTextArea);
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);
        
        textPanel.add(inputPanel);
        textPanel.add(outputPanel);
        
        // 添加组件到主窗口
        add(topPanel, BorderLayout.NORTH);
        add(textPanel, BorderLayout.CENTER);
        
        // 添加按钮事件监听器
        executeButton.addActionListener(new ExecuteButtonListener());
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