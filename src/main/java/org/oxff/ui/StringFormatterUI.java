package org.oxff.ui;

import com.formdev.flatlaf.FlatLightLaf;
import org.oxff.core.OperationFactory;
import org.oxff.core.OperationCategory;
import org.oxff.operation.Operation;
import org.oxff.ui.components.UIComponentRegistry;
import org.oxff.ui.controller.*;
import org.oxff.ui.handler.ClipboardManager;
import org.oxff.ui.handler.EventHandler;
import org.oxff.ui.image.ImageDisplayManager;
import org.oxff.ui.image.ImageFileManager;
import org.oxff.ui.util.KeyboardShortcutManager;
import org.oxff.ui.util.LogManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.List;

// 添加RSyntaxTextArea相关导入
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 * 字符串格式化和编解码工具 - 主界面
 * 重构后的版本，使用模块化设计
 */
public class StringFormatterUI extends JFrame {
    // 组件注册表
    private final UIComponentRegistry registry;
    // 管理器和处理器
    private final LogManager logManager;
    private final ClipboardManager clipboardManager;
    private final KeyboardShortcutManager keyboardShortcutManager;
    private final OperationValidator operationValidator;
    private final OperationExecutor operationExecutor;
    private final UIStateManager uiStateManager;
    private ImageDisplayManager imageDisplayManager;
    private final ImageFileManager imageFileManager;
    private EventHandler eventHandler;

    // UI组件引用（用于布局构建）
    private JComboBox<String> operationComboBox;
    private JButton executeButton;
    private JButton copyInputButton;
    private JButton pasteInputButton;
    private JButton copyOutputButton;
    private JButton clearInputButton;
    private JButton clearExpressionButton;
    private JButton swapButton;
    private JCheckBox wrapCheckBox;
    private JTree operationTree;
    private JSpinner delaySecondsSpinner;
    private JSpinner charIntervalMsSpinner;
    private JRadioButton inputSourceRadio;
    private JRadioButton clipboardSourceRadio;
    private JButton selectImageButton;
    private JButton pasteImageButton;
    private JLabel selectedImageLabel;
    private JComboBox<String> timezoneComboBox;
    private JPanel imageInputPanel;
    private JPanel timezoneConfigPanel;
    private JPanel automationConfigPanel;
    private JPanel baseEncodingConfigPanel;
    private JPanel passwordGeneratorConfigPanel;
    private JPanel expressionPanel;
    private JPanel outputPanel;
    private JLabel imageDisplayLabel;
    private CardLayout outputCardLayout;
    private JPanel outputCardsPanel;
    private JSplitPane outputExpressionSplitPane;

    public StringFormatterUI() {
        // 初始化组件注册表
        this.registry = new UIComponentRegistry();

        // 初始化管理器和处理器
        this.logManager = new LogManager(new JTextArea());
        this.clipboardManager = new ClipboardManager();
        this.keyboardShortcutManager = new KeyboardShortcutManager();
        this.operationValidator = new OperationValidator();
        this.operationExecutor = new OperationExecutor(operationValidator);
        this.uiStateManager = new UIStateManager(registry, operationValidator);
        this.imageFileManager = new ImageFileManager();

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
        JPanel topPanel = createTopPanel();

        // 创建主分割面板
        JSplitPane mainSplitPane = createMainSplitPane();

        // 创建垂直分割面板（用于分离主内容和日志区域）
        JSplitPane verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        verticalSplitPane.setResizeWeight(0.8);
        verticalSplitPane.setDividerSize(10);

        // 创建文本区域面板
        JSplitPane expressionSplitPane = createExpressionSplitPane();

        // 设置主分割面板
        mainSplitPane.setLeftComponent(createOperationPanel());
        mainSplitPane.setRightComponent(expressionSplitPane);

        // 创建日志面板
        JPanel logPanel = createLogPanel();

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
        logManager.log("应用程序启动");
    }

    /**
     * 创建顶部面板
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel operationLabel = new JLabel("选择操作:");
        operationComboBox = new JComboBox<>(OperationFactory.getAllOperationNames());
        operationComboBox.addActionListener(e -> {
            String selected = (String) operationComboBox.getSelectedItem();
            if (eventHandler != null) {
                eventHandler.handleOperationSelection(selected);
            }
        });
        executeButton = new JButton("执行");

        topPanel.add(operationLabel);
        topPanel.add(operationComboBox);
        topPanel.add(executeButton);

        return topPanel;
    }

    /**
     * 创建主分割面板
     */
    private JSplitPane createMainSplitPane() {
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setResizeWeight(0.3);
        mainSplitPane.setDividerLocation(0.3);
        mainSplitPane.setDividerSize(10);
        return mainSplitPane;
    }

    /**
     * 创建操作分类面板
     */
    private JPanel createOperationPanel() {
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

    /**
     * 创建表达式分割面板
     */
    private JSplitPane createExpressionSplitPane() {
        JSplitPane expressionSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        expressionSplitPane.setResizeWeight(0.4);
        expressionSplitPane.setDividerLocation(0.4);
        expressionSplitPane.setDividerSize(8);

        // 输入区域
        JPanel inputPanel = createInputPanel();

        // 输出表达式分割面板
        outputExpressionSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        outputExpressionSplitPane.setResizeWeight(0.5);
        outputExpressionSplitPane.setDividerLocation(0.5);
        outputExpressionSplitPane.setDividerSize(10);

        // 表达式面板
        expressionPanel = createExpressionPanel();

        // 输出面板
        outputPanel = createOutputPanel();

        outputExpressionSplitPane.setLeftComponent(expressionPanel);
        outputExpressionSplitPane.setRightComponent(outputPanel);

        expressionSplitPane.setTopComponent(inputPanel);
        expressionSplitPane.setBottomComponent(outputExpressionSplitPane);

        return expressionSplitPane;
    }

    /**
     * 创建输入面板
     */
    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("输入"));

        // 输入区域按钮面板
        JPanel inputButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pasteInputButton = new JButton("粘贴");
        copyInputButton = new JButton("复制");
        clearInputButton = new JButton("清空");
        swapButton = new JButton("交换");
        wrapCheckBox = new JCheckBox("自动换行");

        inputButtonPanel.add(pasteInputButton);
        inputButtonPanel.add(copyInputButton);
        inputButtonPanel.add(clearInputButton);
        inputButtonPanel.add(swapButton);
        inputButtonPanel.add(wrapCheckBox);

        // 使用RSyntaxTextArea替换自定义的LineNumberTextArea
        RSyntaxTextArea inputTextArea = new RSyntaxTextArea();
        inputTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inputTextArea.setLineWrap(false);
        inputTextArea.setCodeFoldingEnabled(true);

        // 立即注册到注册表
        registry.registerComponent(UIComponentRegistry.INPUT_TEXT_AREA, inputTextArea);

        // 使用RTextScrollPane提供行号显示
        RTextScrollPane inputScrollPane = new RTextScrollPane(inputTextArea);

        inputPanel.add(inputButtonPanel, BorderLayout.NORTH);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);

        // 创建一个容器面板来管理所有可选配置面板
        JPanel configContainerPanel = new JPanel(new CardLayout());

        // 自动化配置面板
        automationConfigPanel = createAutomationConfigPanel();
        configContainerPanel.add(automationConfigPanel, "AUTOMATION");

        // 图片输入面板
        imageInputPanel = createImageInputPanel();
        configContainerPanel.add(imageInputPanel, "IMAGE");

        // 时区选择面板
        timezoneConfigPanel = createTimezoneConfigPanel();
        configContainerPanel.add(timezoneConfigPanel, "TIMEZONE");

        // Base编码配置面板
        baseEncodingConfigPanel = createBaseEncodingConfigPanel();
        configContainerPanel.add(baseEncodingConfigPanel, "BASE_ENCODING");

        // 密码生成器配置面板
        passwordGeneratorConfigPanel = createPasswordGeneratorConfigPanel();
        configContainerPanel.add(passwordGeneratorConfigPanel, "PASSWORD_GENERATOR");

        // 默认显示空面板
        JPanel emptyPanel = new JPanel();
        configContainerPanel.add(emptyPanel, "EMPTY");

        inputPanel.add(configContainerPanel, BorderLayout.SOUTH);

        return inputPanel;
    }

    /**
     * 创建自动化配置面板
     */
    private JPanel createAutomationConfigPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("自动化输入配置"));

        panel.add(new JLabel("延迟时间(秒):"));
        SpinnerModel delayModel = new SpinnerNumberModel(3, 0, 60, 1);
        delaySecondsSpinner = new JSpinner(delayModel);
        delaySecondsSpinner.setPreferredSize(new Dimension(60, 25));
        panel.add(delaySecondsSpinner);

        panel.add(Box.createHorizontalStrut(15));

        panel.add(new JLabel("字符间隔(毫秒):"));
        SpinnerModel intervalModel = new SpinnerNumberModel(100, 0, 1000, 10);
        charIntervalMsSpinner = new JSpinner(intervalModel);
        charIntervalMsSpinner.setPreferredSize(new Dimension(70, 25));
        panel.add(charIntervalMsSpinner);

        panel.add(Box.createHorizontalStrut(15));

        panel.add(new JLabel("输入来源:"));
        ButtonGroup sourceGroup = new ButtonGroup();
        inputSourceRadio = new JRadioButton("输入框", true);
        clipboardSourceRadio = new JRadioButton("剪贴板");
        sourceGroup.add(inputSourceRadio);
        sourceGroup.add(clipboardSourceRadio);
        panel.add(inputSourceRadio);
        panel.add(clipboardSourceRadio);

        return panel;
    }

    /**
     * 创建图片输入面板
     */
    private JPanel createImageInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("图片输入"));

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

        panel.add(imageInputButtonPanel, BorderLayout.NORTH);
        panel.add(selectedImageLabel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 创建时区选择面板
     */
    private JPanel createTimezoneConfigPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("时区选择"));

        JLabel timezoneLabel = new JLabel("选择时区:");
        timezoneComboBox = new JComboBox<>(new String[]{
            "系统时区",
            "UTC (协调世界时)",
            "GMT (格林威治)",
            "EST (美国东部)",
            "PST (美国西部)",
            "CET (欧洲中部)",
            "GMT (英国伦敦)",
            "JST (日本)",
            "AEST (澳大利亚东部)",
            "IST (印度)",
            "CST (中国)"
        });
        timezoneComboBox.setPreferredSize(new Dimension(200, 25));

        panel.add(timezoneLabel);
        panel.add(timezoneComboBox);

        return panel;
    }

    /**
     * 创建Base编码配置面板
     */
    private JPanel createBaseEncodingConfigPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("图片转Base编码配置"));

        // 配置控制面板
        JPanel configPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        configPanel.add(new JLabel("编码类型:"));
        JComboBox<String> encodingTypeComboBox = new JComboBox<>(new String[]{
            "Base64", "Base32"
        });
        encodingTypeComboBox.setPreferredSize(new Dimension(100, 25));
        configPanel.add(encodingTypeComboBox);

        // 文件选择按钮
        JButton selectFileButton = new JButton("选择图片文件");
        configPanel.add(Box.createHorizontalStrut(15));
        configPanel.add(selectFileButton);

        panel.add(configPanel, BorderLayout.NORTH);

        // 文件状态标签
        JLabel selectedFileLabel = new JLabel("未选择文件");
        selectedFileLabel.setForeground(Color.GRAY);
        selectedFileLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.add(selectedFileLabel, BorderLayout.CENTER);

        // 注册组件
        registry.registerComponent(UIComponentRegistry.BASE_ENCODING_COMBO_BOX, encodingTypeComboBox);
        registry.registerComponent(UIComponentRegistry.SELECT_FILE_BUTTON, selectFileButton);
        registry.registerComponent(UIComponentRegistry.SELECTED_FILE_LABEL, selectedFileLabel);

        return panel;
    }

    /**
     * 创建密码生成器配置面板
     */
    private JPanel createPasswordGeneratorConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("随机密码生成配置"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // 第一行: 密码总长度
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("密码总长度:"), gbc);

        gbc.gridx = 1;
        SpinnerModel lengthModel = new SpinnerNumberModel(16, 4, 128, 1);
        JSpinner passwordLengthSpinner = new JSpinner(lengthModel);
        passwordLengthSpinner.setPreferredSize(new Dimension(80, 25));
        panel.add(passwordLengthSpinner, gbc);

        // 第二行: 数字配置
        gbc.gridx = 0;
        gbc.gridy = 1;
        JCheckBox includeDigitsCheckBox = new JCheckBox("数字");
        includeDigitsCheckBox.setSelected(true);
        panel.add(includeDigitsCheckBox, gbc);

        gbc.gridx = 1;
        JPanel digitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        digitPanel.add(new JLabel("个数:"));
        SpinnerModel digitCountModel = new SpinnerNumberModel(4, 0, 128, 1);
        JSpinner digitCountSpinner = new JSpinner(digitCountModel);
        digitCountSpinner.setPreferredSize(new Dimension(60, 25));
        digitPanel.add(digitCountSpinner);
        panel.add(digitPanel, gbc);

        // 第三行: 大写字母配置
        gbc.gridx = 0;
        gbc.gridy = 2;
        JCheckBox includeUppercaseCheckBox = new JCheckBox("大写字母");
        includeUppercaseCheckBox.setSelected(true);
        panel.add(includeUppercaseCheckBox, gbc);

        gbc.gridx = 1;
        JPanel uppercasePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        uppercasePanel.add(new JLabel("个数:"));
        SpinnerModel uppercaseCountModel = new SpinnerNumberModel(4, 0, 128, 1);
        JSpinner uppercaseCountSpinner = new JSpinner(uppercaseCountModel);
        uppercaseCountSpinner.setPreferredSize(new Dimension(60, 25));
        uppercasePanel.add(uppercaseCountSpinner);
        panel.add(uppercasePanel, gbc);

        // 第四行: 小写字母配置
        gbc.gridx = 0;
        gbc.gridy = 3;
        JCheckBox includeLowercaseCheckBox = new JCheckBox("小写字母");
        includeLowercaseCheckBox.setSelected(true);
        panel.add(includeLowercaseCheckBox, gbc);

        gbc.gridx = 1;
        JPanel lowercasePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        lowercasePanel.add(new JLabel("个数:"));
        SpinnerModel lowercaseCountModel = new SpinnerNumberModel(4, 0, 128, 1);
        JSpinner lowercaseCountSpinner = new JSpinner(lowercaseCountModel);
        lowercaseCountSpinner.setPreferredSize(new Dimension(60, 25));
        lowercasePanel.add(lowercaseCountSpinner);
        panel.add(lowercasePanel, gbc);

        // 第五行: 特殊字符配置
        gbc.gridx = 0;
        gbc.gridy = 4;
        JCheckBox includeSpecialCharsCheckBox = new JCheckBox("特殊字符");
        includeSpecialCharsCheckBox.setSelected(false);
        panel.add(includeSpecialCharsCheckBox, gbc);

        gbc.gridx = 1;
        JPanel specialCharPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        specialCharPanel.add(new JLabel("个数:"));
        SpinnerModel specialCountModel = new SpinnerNumberModel(2, 0, 128, 1);
        JSpinner specialCharCountSpinner = new JSpinner(specialCountModel);
        specialCharCountSpinner.setPreferredSize(new Dimension(60, 25));
        specialCharCountSpinner.setEnabled(false);
        specialCharPanel.add(specialCharCountSpinner);
        panel.add(specialCharPanel, gbc);

        // 第六行: 生成个数
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("生成个数:"), gbc);

        gbc.gridx = 1;
        SpinnerModel countModel = new SpinnerNumberModel(1, 1, 100, 1);
        JSpinner passwordCountSpinner = new JSpinner(countModel);
        passwordCountSpinner.setPreferredSize(new Dimension(80, 25));
        panel.add(passwordCountSpinner, gbc);

        // 注册组件
        registry.registerComponent(UIComponentRegistry.PASSWORD_LENGTH_SPINNER, passwordLengthSpinner);
        registry.registerComponent(UIComponentRegistry.INCLUDE_DIGITS_CHECK_BOX, includeDigitsCheckBox);
        registry.registerComponent(UIComponentRegistry.DIGIT_COUNT_SPINNER, digitCountSpinner);
        registry.registerComponent(UIComponentRegistry.INCLUDE_UPPERCASE_CHECK_BOX, includeUppercaseCheckBox);
        registry.registerComponent(UIComponentRegistry.UPPERCASE_COUNT_SPINNER, uppercaseCountSpinner);
        registry.registerComponent(UIComponentRegistry.INCLUDE_LOWERCASE_CHECK_BOX, includeLowercaseCheckBox);
        registry.registerComponent(UIComponentRegistry.LOWERCASE_COUNT_SPINNER, lowercaseCountSpinner);
        registry.registerComponent(UIComponentRegistry.INCLUDE_SPECIAL_CHARS_CHECK_BOX, includeSpecialCharsCheckBox);
        registry.registerComponent(UIComponentRegistry.SPECIAL_CHAR_COUNT_SPINNER, specialCharCountSpinner);
        registry.registerComponent(UIComponentRegistry.PASSWORD_COUNT_SPINNER, passwordCountSpinner);

        // 动态联动逻辑
        PasswordConfigListener listener = new PasswordConfigListener(
            passwordLengthSpinner,
            includeDigitsCheckBox, digitCountSpinner,
            includeUppercaseCheckBox, uppercaseCountSpinner,
            includeLowercaseCheckBox, lowercaseCountSpinner,
            includeSpecialCharsCheckBox, specialCharCountSpinner
        );

        // 总长度改变时调整各类型数量
        passwordLengthSpinner.addChangeListener(e -> listener.onTotalLengthChanged());

        // 数字勾选框改变时
        includeDigitsCheckBox.addActionListener(e -> listener.onDigitCheckChanged());

        // 数字数量改变时
        digitCountSpinner.addChangeListener(e -> listener.onDigitCountChanged());

        // 大写字母勾选框改变时
        includeUppercaseCheckBox.addActionListener(e -> listener.onUppercaseCheckChanged());

        // 大写字母数量改变时
        uppercaseCountSpinner.addChangeListener(e -> listener.onUppercaseCountChanged());

        // 小写字母勾选框改变时
        includeLowercaseCheckBox.addActionListener(e -> listener.onLowercaseCheckChanged());

        // 小写字母数量改变时
        lowercaseCountSpinner.addChangeListener(e -> listener.onLowercaseCountChanged());

        // 特殊字符勾选框改变时
        includeSpecialCharsCheckBox.addActionListener(e -> listener.onSpecialCharCheckChanged());

        // 特殊字符数量改变时
        specialCharCountSpinner.addChangeListener(e -> listener.onSpecialCharCountChanged());

        return panel;
    }

    /**
     * 密码配置动态联动监听器
     */
    private class PasswordConfigListener {
        private final JSpinner totalLengthSpinner;
        private final JCheckBox includeDigitsCheckBox;
        private final JSpinner digitCountSpinner;
        private final JCheckBox includeUppercaseCheckBox;
        private final JSpinner uppercaseCountSpinner;
        private final JCheckBox includeLowercaseCheckBox;
        private final JSpinner lowercaseCountSpinner;
        private final JCheckBox includeSpecialCharsCheckBox;
        private final JSpinner specialCharCountSpinner;

        private boolean isUpdating = false;

        public PasswordConfigListener(JSpinner totalLengthSpinner,
                                      JCheckBox includeDigitsCheckBox, JSpinner digitCountSpinner,
                                      JCheckBox includeUppercaseCheckBox, JSpinner uppercaseCountSpinner,
                                      JCheckBox includeLowercaseCheckBox, JSpinner lowercaseCountSpinner,
                                      JCheckBox includeSpecialCharsCheckBox, JSpinner specialCharCountSpinner) {
            this.totalLengthSpinner = totalLengthSpinner;
            this.includeDigitsCheckBox = includeDigitsCheckBox;
            this.digitCountSpinner = digitCountSpinner;
            this.includeUppercaseCheckBox = includeUppercaseCheckBox;
            this.uppercaseCountSpinner = uppercaseCountSpinner;
            this.includeLowercaseCheckBox = includeLowercaseCheckBox;
            this.lowercaseCountSpinner = lowercaseCountSpinner;
            this.includeSpecialCharsCheckBox = includeSpecialCharsCheckBox;
            this.specialCharCountSpinner = specialCharCountSpinner;
        }

        private int getTotalLength() {
            return (Integer) totalLengthSpinner.getValue();
        }

        private int getDigitCount() {
            return includeDigitsCheckBox.isSelected() ? (Integer) digitCountSpinner.getValue() : 0;
        }

        private int getUppercaseCount() {
            return includeUppercaseCheckBox.isSelected() ? (Integer) uppercaseCountSpinner.getValue() : 0;
        }

        private int getLowercaseCount() {
            return includeLowercaseCheckBox.isSelected() ? (Integer) lowercaseCountSpinner.getValue() : 0;
        }

        private int getSpecialCharCount() {
            return includeSpecialCharsCheckBox.isSelected() ? (Integer) specialCharCountSpinner.getValue() : 0;
        }

        private int getCurrentTotal() {
            return getDigitCount() + getUppercaseCount() + getLowercaseCount() + getSpecialCharCount();
        }

        public void onTotalLengthChanged() {
            if (isUpdating) return;
            isUpdating = true;

            int newTotal = getTotalLength();
            int currentTotal = getCurrentTotal();

            if (currentTotal == 0) {
                // 如果没有选择任何类型，默认全部使用小写字母
                includeLowercaseCheckBox.setSelected(true);
                lowercaseCountSpinner.setValue(newTotal);
            } else if (currentTotal != newTotal) {
                // 按比例调整各类型数量
                double ratio = (double) newTotal / currentTotal;

                adjustDigitCount(newTotal, ratio);
                adjustUppercaseCount(newTotal, ratio);
                adjustLowercaseCount(newTotal, ratio);
                adjustSpecialCharCount(newTotal, ratio);

                // 确保总和等于总长度
                balanceCounts();
            }

            isUpdating = false;
        }

        public void onDigitCheckChanged() {
            if (isUpdating) return;
            isUpdating = true;

            digitCountSpinner.setEnabled(includeDigitsCheckBox.isSelected());

            if (includeDigitsCheckBox.isSelected() && (Integer) digitCountSpinner.getValue() == 0) {
                digitCountSpinner.setValue(1);
            }

            balanceCounts();
            isUpdating = false;
        }

        public void onDigitCountChanged() {
            if (isUpdating) return;
            isUpdating = true;
            balanceCounts();
            isUpdating = false;
        }

        public void onUppercaseCheckChanged() {
            if (isUpdating) return;
            isUpdating = true;

            uppercaseCountSpinner.setEnabled(includeUppercaseCheckBox.isSelected());

            if (includeUppercaseCheckBox.isSelected() && (Integer) uppercaseCountSpinner.getValue() == 0) {
                uppercaseCountSpinner.setValue(1);
            }

            balanceCounts();
            isUpdating = false;
        }

        public void onUppercaseCountChanged() {
            if (isUpdating) return;
            isUpdating = true;
            balanceCounts();
            isUpdating = false;
        }

        public void onLowercaseCheckChanged() {
            if (isUpdating) return;
            isUpdating = true;

            lowercaseCountSpinner.setEnabled(includeLowercaseCheckBox.isSelected());

            if (includeLowercaseCheckBox.isSelected() && (Integer) lowercaseCountSpinner.getValue() == 0) {
                lowercaseCountSpinner.setValue(1);
            }

            balanceCounts();
            isUpdating = false;
        }

        public void onLowercaseCountChanged() {
            if (isUpdating) return;
            isUpdating = true;
            balanceCounts();
            isUpdating = false;
        }

        public void onSpecialCharCheckChanged() {
            if (isUpdating) return;
            isUpdating = true;

            specialCharCountSpinner.setEnabled(includeSpecialCharsCheckBox.isSelected());

            if (includeSpecialCharsCheckBox.isSelected() && (Integer) specialCharCountSpinner.getValue() == 0) {
                specialCharCountSpinner.setValue(1);
            }

            balanceCounts();
            isUpdating = false;
        }

        public void onSpecialCharCountChanged() {
            if (isUpdating) return;
            isUpdating = true;
            balanceCounts();
            isUpdating = false;
        }

        private void adjustDigitCount(int totalLength, double ratio) {
            if (includeDigitsCheckBox.isSelected()) {
                int newCount = Math.max(1, (int) Math.round(getDigitCount() * ratio));
                digitCountSpinner.setValue(Math.min(newCount, totalLength));
            }
        }

        private void adjustUppercaseCount(int totalLength, double ratio) {
            if (includeUppercaseCheckBox.isSelected()) {
                int newCount = Math.max(1, (int) Math.round(getUppercaseCount() * ratio));
                uppercaseCountSpinner.setValue(Math.min(newCount, totalLength));
            }
        }

        private void adjustLowercaseCount(int totalLength, double ratio) {
            if (includeLowercaseCheckBox.isSelected()) {
                int newCount = Math.max(1, (int) Math.round(getLowercaseCount() * ratio));
                lowercaseCountSpinner.setValue(Math.min(newCount, totalLength));
            }
        }

        private void adjustSpecialCharCount(int totalLength, double ratio) {
            if (includeSpecialCharsCheckBox.isSelected()) {
                int newCount = Math.max(1, (int) Math.round(getSpecialCharCount() * ratio));
                specialCharCountSpinner.setValue(Math.min(newCount, totalLength));
            }
        }

        private void balanceCounts() {
            int totalLength = getTotalLength();
            int currentTotal = getCurrentTotal();

            if (currentTotal == 0) {
                // 如果没有选择任何类型，默认使用小写字母
                includeLowercaseCheckBox.setSelected(true);
                lowercaseCountSpinner.setValue(totalLength);
                return;
            }

            int diff = totalLength - currentTotal;

            if (diff > 0) {
                // 需要增加
                distributeExtra(diff);
            } else if (diff < 0) {
                // 需要减少
                reduceCount(-diff);
            }
        }

        private void distributeExtra(int extra) {
            while (extra > 0) {
                // 优先分配给小写字母
                if (includeLowercaseCheckBox.isSelected()) {
                    lowercaseCountSpinner.setValue((Integer) lowercaseCountSpinner.getValue() + 1);
                    extra--;
                    continue;
                }
                // 然后大写字母
                if (includeUppercaseCheckBox.isSelected()) {
                    uppercaseCountSpinner.setValue((Integer) uppercaseCountSpinner.getValue() + 1);
                    extra--;
                    continue;
                }
                // 然后数字
                if (includeDigitsCheckBox.isSelected()) {
                    digitCountSpinner.setValue((Integer) digitCountSpinner.getValue() + 1);
                    extra--;
                    continue;
                }
                // 最后特殊字符
                if (includeSpecialCharsCheckBox.isSelected()) {
                    specialCharCountSpinner.setValue((Integer) specialCharCountSpinner.getValue() + 1);
                    extra--;
                    continue;
                }
                break;
            }
        }

        private void reduceCount(int reduce) {
            while (reduce > 0) {
                // 优先从特殊字符减少
                if (includeSpecialCharsCheckBox.isSelected() && (Integer) specialCharCountSpinner.getValue() > 1) {
                    specialCharCountSpinner.setValue((Integer) specialCharCountSpinner.getValue() - 1);
                    reduce--;
                    continue;
                }
                // 然后从数字减少
                if (includeDigitsCheckBox.isSelected() && (Integer) digitCountSpinner.getValue() > 1) {
                    digitCountSpinner.setValue((Integer) digitCountSpinner.getValue() - 1);
                    reduce--;
                    continue;
                }
                // 然后从大写字母减少
                if (includeUppercaseCheckBox.isSelected() && (Integer) uppercaseCountSpinner.getValue() > 1) {
                    uppercaseCountSpinner.setValue((Integer) uppercaseCountSpinner.getValue() - 1);
                    reduce--;
                    continue;
                }
                // 最后从小写字母减少
                if (includeLowercaseCheckBox.isSelected() && (Integer) lowercaseCountSpinner.getValue() > 1) {
                    lowercaseCountSpinner.setValue((Integer) lowercaseCountSpinner.getValue() - 1);
                    reduce--;
                    continue;
                }
                break;
            }
        }
    }

    /**
     * 创建表达式面板
     */
    private JPanel createExpressionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("XPath/JSONPath表达式 (每行一个)"));

        // 表达式区域按钮面板
        JPanel expressionButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clearExpressionButton = new JButton("清空表达式");
        JLabel expressionHint = new JLabel("支持XPath (XML) 和 JSONPath (JSON) 表达式");
        expressionHint.setFont(expressionHint.getFont().deriveFont(Font.ITALIC, 10f));
        expressionHint.setForeground(Color.GRAY);

        expressionButtonPanel.add(clearExpressionButton);
        expressionButtonPanel.add(expressionHint);

        // 表达式输入文本区域
        RSyntaxTextArea expressionTextArea = new RSyntaxTextArea();
        expressionTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        expressionTextArea.setLineWrap(true);
        expressionTextArea.setWrapStyleWord(true);
        expressionTextArea.setCodeFoldingEnabled(false);
        expressionTextArea.setToolTipText("输入XPath或JSONPath表达式，每行一个表达式\nXML示例: //book/title\nJSON示例: $.store.book[*].title");

        // 立即注册到注册表
        registry.registerComponent(UIComponentRegistry.EXPRESSION_TEXT_AREA, expressionTextArea);

        RTextScrollPane expressionScrollPane = new RTextScrollPane(expressionTextArea);
        expressionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        expressionScrollPane.setPreferredSize(new Dimension(0, 80));

        panel.add(expressionButtonPanel, BorderLayout.NORTH);
        panel.add(expressionScrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 创建输出面板
     */
    private JPanel createOutputPanel() {
        // 创建输出区域面板（使用卡片布局支持文本和图片切换）
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
        RSyntaxTextArea outputTextArea = new RSyntaxTextArea();
        outputTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputTextArea.setLineWrap(false);
        outputTextArea.setEditable(false);
        outputTextArea.setCodeFoldingEnabled(true);

        // 立即注册到注册表
        registry.registerComponent(UIComponentRegistry.OUTPUT_TEXT_AREA, outputTextArea);

        // 使用RTextScrollPane提供行号显示
        RTextScrollPane outputScrollPane = new RTextScrollPane(outputTextArea);

        textOutputCard.add(outputButtonPanel, BorderLayout.NORTH);
        textOutputCard.add(outputScrollPane, BorderLayout.CENTER);

        // 图片输出卡片
        JPanel imageOutputCard = new JPanel(new BorderLayout());
        imageOutputCard.setBorder(BorderFactory.createTitledBorder("图片输出"));

        JPanel imagePanel = new JPanel(new BorderLayout());
        imageDisplayLabel = new JLabel();
        imageDisplayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageDisplayLabel.setVerticalAlignment(SwingConstants.CENTER);
        JScrollPane imageScrollPane = new JScrollPane(imageDisplayLabel);

        imagePanel.add(imageScrollPane, BorderLayout.CENTER);
        imageOutputCard.add(imagePanel, BorderLayout.CENTER);

        // 添加按钮面板到图片输出卡片
        JPanel imageButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton saveImageButton = new JButton("保存图片");
        JButton copyImageButton = new JButton("复制图片");

        imageButtonPanel.add(saveImageButton);
        imageButtonPanel.add(copyImageButton);
        imageOutputCard.add(imageButtonPanel, BorderLayout.NORTH);

        // 将两个卡片添加到卡片面板
        outputCardsPanel.add(textOutputCard, "TEXT");
        outputCardsPanel.add(imageOutputCard, "IMAGE");

        // 默认显示文本输出
        outputCardLayout.show(outputCardsPanel, "TEXT");

        return outputCardsPanel;
    }

    /**
     * 创建日志面板
     */
    private JPanel createLogPanel() {
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("日志"));

        JTextArea logTextArea = logManager.getLogTextArea();
        logTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        logTextArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logTextArea);
        logPanel.add(logScrollPane, BorderLayout.CENTER);

        return logPanel;
    }

    /**
     * 创建操作分类树
     */
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
        // 注册所有UI组件到注册表
        registerComponents();

        // 创建事件处理器
        eventHandler = new EventHandler(
            registry,
            logManager,
            clipboardManager,
            operationExecutor,
            operationValidator,
            uiStateManager,
            imageDisplayManager,
            imageFileManager,
            operationName -> {}
        );

        // 执行按钮事件
        executeButton.addActionListener(e -> eventHandler.handleExecute(this));

        // 选择图片按钮事件
        selectImageButton.addActionListener(e -> eventHandler.handleSelectImage(this));

        // 粘贴图片按钮事件
        pasteImageButton.addActionListener(e -> eventHandler.handlePasteImage(this));

        // Base编码配置面板 - 选择文件按钮事件
        JButton selectFileButton = registry.getComponent(UIComponentRegistry.SELECT_FILE_BUTTON);
        if (selectFileButton != null) {
            selectFileButton.addActionListener(e -> eventHandler.handleSelectFileForBaseEncoding(this));
        }

        // 复制输入按钮事件
        copyInputButton.addActionListener(e -> eventHandler.handleCopyInput());

        // 粘贴输入按钮事件
        pasteInputButton.addActionListener(e -> eventHandler.handlePasteInput());

        // 复制输出按钮事件
        copyOutputButton.addActionListener(e -> eventHandler.handleCopyOutput());

        // 清空输入按钮事件
        clearInputButton.addActionListener(e -> eventHandler.handleClearInput());

        // 交换按钮事件
        swapButton.addActionListener(e -> eventHandler.handleSwap());

        // 清空表达式按钮事件
        clearExpressionButton.addActionListener(e -> eventHandler.handleClearExpression());

        // 自动换行复选框事件
        wrapCheckBox.addActionListener(e -> eventHandler.handleWrapToggle(wrapCheckBox.isSelected()));

        // 图片输出按钮事件 - 需要遍历组件查找按钮
        JPanel imageOutputCard = (JPanel) outputCardsPanel.getComponent(1);
        JButton saveImageButton = null;
        JButton copyImageButton = null;

        // 遍历 imageOutputCard 的组件查找 imageButtonPanel
        for (Component comp : imageOutputCard.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                // 查找按钮
                for (Component innerComp : panel.getComponents()) {
                    if (innerComp instanceof JButton) {
                        JButton button = (JButton) innerComp;
                        if (button.getText().equals("保存图片")) {
                            saveImageButton = button;
                        } else if (button.getText().equals("复制图片")) {
                            copyImageButton = button;
                        }
                    }
                }
            }
        }

        if (saveImageButton != null) {
            saveImageButton.addActionListener(e -> eventHandler.handleSaveImage(this));
        }
        if (copyImageButton != null) {
            copyImageButton.addActionListener(e -> eventHandler.handleCopyImage(this));
        }
    }

    /**
     * 注册所有UI组件到注册表
     * 这个方法需要在setupEventListeners()之前调用，因为事件处理器需要这些组件
     * 注意：文本区域已在创建时注册，这里只注册其他组件
     */
    private void registerComponents() {
        // 注册日志文本区域
        registry.registerComponent(UIComponentRegistry.LOG_TEXT_AREA, logManager.getLogTextArea());

        // 注册操作选择组件
        registry.registerComponent(UIComponentRegistry.OPERATION_COMBO_BOX, operationComboBox);
        registry.registerComponent(UIComponentRegistry.OPERATION_TREE, operationTree);

        // 注册按钮
        registry.registerComponent(UIComponentRegistry.EXECUTE_BUTTON, executeButton);
        registry.registerComponent(UIComponentRegistry.COPY_INPUT_BUTTON, copyInputButton);
        registry.registerComponent(UIComponentRegistry.PASTE_INPUT_BUTTON, pasteInputButton);
        registry.registerComponent(UIComponentRegistry.COPY_OUTPUT_BUTTON, copyOutputButton);
        registry.registerComponent(UIComponentRegistry.CLEAR_INPUT_BUTTON, clearInputButton);
        registry.registerComponent(UIComponentRegistry.CLEAR_EXPRESSION_BUTTON, clearExpressionButton);
        registry.registerComponent(UIComponentRegistry.SWAP_BUTTON, swapButton);
        registry.registerComponent(UIComponentRegistry.WRAP_CHECK_BOX, wrapCheckBox);
        registry.registerComponent(UIComponentRegistry.SELECT_IMAGE_BUTTON, selectImageButton);
        registry.registerComponent(UIComponentRegistry.PASTE_IMAGE_BUTTON, pasteImageButton);

        // 注册标签和面板
        registry.registerComponent(UIComponentRegistry.SELECTED_IMAGE_LABEL, selectedImageLabel);
        registry.registerComponent(UIComponentRegistry.IMAGE_INPUT_PANEL, imageInputPanel);
        registry.registerComponent(UIComponentRegistry.TIMEZONE_COMBO_BOX, timezoneComboBox);
        registry.registerComponent(UIComponentRegistry.TIMEZONE_CONFIG_PANEL, timezoneConfigPanel);
        registry.registerComponent(UIComponentRegistry.AUTOMATION_CONFIG_PANEL, automationConfigPanel);
        registry.registerComponent(UIComponentRegistry.EXPRESSION_PANEL, expressionPanel);
        registry.registerComponent(UIComponentRegistry.OUTPUT_PANEL, outputPanel);
        registry.registerComponent(UIComponentRegistry.OUTPUT_CARDS_PANEL, outputCardsPanel);
        registry.registerComponent(UIComponentRegistry.IMAGE_DISPLAY_LABEL, imageDisplayLabel);
        registry.registerComponent(UIComponentRegistry.OUTPUT_EXPRESSION_SPLIT_PANE, outputExpressionSplitPane);

        // 注册自动化配置组件
        registry.registerComponent(UIComponentRegistry.DELAY_SECONDS_SPINNER, delaySecondsSpinner);
        registry.registerComponent(UIComponentRegistry.CHAR_INTERVAL_MS_SPINNER, charIntervalMsSpinner);
        registry.registerComponent(UIComponentRegistry.INPUT_SOURCE_RADIO, inputSourceRadio);
        registry.registerComponent(UIComponentRegistry.CLIPBOARD_SOURCE_RADIO, clipboardSourceRadio);

        // 创建图片显示管理器（在组件注册后）
        imageDisplayManager = new ImageDisplayManager(imageDisplayLabel, outputCardLayout, outputCardsPanel);
    }

    /**
     * 设置键盘快捷键
     */
    private void setupKeyboardShortcuts() {
        keyboardShortcutManager.setupButtonShortcuts(
            executeButton, copyInputButton, pasteInputButton,
            copyOutputButton, clearInputButton
        );

        RSyntaxTextArea inputTextArea = registry.getInputTextArea();
        RSyntaxTextArea outputTextArea = registry.getOutputTextArea();

        keyboardShortcutManager.setupSelectAllForAll(inputTextArea, outputTextArea);
    }

    public static void main(String[] args) {
        // 设置外观
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            try {
                StringFormatterUI frame = new StringFormatterUI();
                frame.setVisible(true);
            } catch (Exception e) {
                java.util.logging.Logger.getLogger(StringFormatterUI.class.getName())
                    .severe("启动应用程序时发生错误: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
