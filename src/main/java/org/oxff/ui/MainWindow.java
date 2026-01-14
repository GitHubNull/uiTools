package org.oxff.ui;

import com.formdev.flatlaf.FlatLightLaf;
import org.oxff.core.OperationFactory;
import org.oxff.core.OperationCategory;
import org.oxff.operation.Operation;
import org.oxff.ui.components.UIComponentRegistry;
import org.oxff.ui.builder.ConfigPanelBuilder;
import org.oxff.ui.builder.ConfigPanelBuilder.ConfigPanelsResult;
import org.oxff.ui.builder.ExpressionPanelBuilder;
import org.oxff.ui.builder.ExpressionPanelBuilder.ExpressionPanelResult;
import org.oxff.ui.builder.OperationTreeBuilder;
import org.oxff.ui.builder.OutputPanelBuilder;
import org.oxff.ui.builder.OutputPanelBuilder.OutputPanelResult;
import org.oxff.ui.builder.PasswordConfigListener;
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
 * 主应用窗口
 * 重构后的版本，使用模块化设计，职责为UI组件协调和布局组装
 */
public class MainWindow extends JFrame {
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

    public MainWindow() {
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
        OperationTreeBuilder treeBuilder = new OperationTreeBuilder();
        operationTree = treeBuilder.buildOperationTree();
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

        JPanel operationPanel = new JPanel(new BorderLayout());
        operationPanel.setBorder(BorderFactory.createTitledBorder("操作分类"));
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

        // 使用 ConfigPanelBuilder 创建所有配置面板
        ConfigPanelBuilder configBuilder = new ConfigPanelBuilder(registry);
        ConfigPanelsResult configResult = configBuilder.buildAllConfigPanels();

        // 保存配置面板和组件引用
        automationConfigPanel = configResult.automationConfigPanel;
        imageInputPanel = configResult.imageInputPanel;
        timezoneConfigPanel = configResult.timezoneConfigPanel;
        baseEncodingConfigPanel = configResult.baseEncodingConfigPanel;
        passwordGeneratorConfigPanel = configResult.passwordGeneratorConfigPanel;

        delaySecondsSpinner = configResult.delaySecondsSpinner;
        charIntervalMsSpinner = configResult.charIntervalMsSpinner;
        inputSourceRadio = configResult.inputSourceRadio;
        clipboardSourceRadio = configResult.clipboardSourceRadio;
        selectImageButton = configResult.selectImageButton;
        pasteImageButton = configResult.pasteImageButton;
        selectedImageLabel = configResult.selectedImageLabel;
        timezoneComboBox = configResult.timezoneComboBox;

        // 创建一个容器面板来管理所有可选配置面板
        JPanel configContainerPanel = new JPanel(new CardLayout());
        configContainerPanel.add(automationConfigPanel, "AUTOMATION");
        configContainerPanel.add(imageInputPanel, "IMAGE");
        configContainerPanel.add(timezoneConfigPanel, "TIMEZONE");
        configContainerPanel.add(baseEncodingConfigPanel, "BASE_ENCODING");
        configContainerPanel.add(passwordGeneratorConfigPanel, "PASSWORD_GENERATOR");

        // 默认显示空面板
        JPanel emptyPanel = new JPanel();
        configContainerPanel.add(emptyPanel, "EMPTY");

        inputPanel.add(configContainerPanel, BorderLayout.SOUTH);

        return inputPanel;
    }

    /**
     * 创建表达式面板
     */
    private JPanel createExpressionPanel() {
        ExpressionPanelBuilder expressionBuilder = new ExpressionPanelBuilder(registry);
        ExpressionPanelBuilder.ExpressionPanelResult result = expressionBuilder.buildExpressionPanel();
        clearExpressionButton = result.clearExpressionButton;
        return result.panel;
    }

    /**
     * 创建输出面板
     */
    private JPanel createOutputPanel() {
        OutputPanelBuilder outputBuilder = new OutputPanelBuilder(registry);
        OutputPanelBuilder.OutputPanelResult result = outputBuilder.buildOutputPanel();

        // 保存结果引用
        outputCardsPanel = result.cardsPanel;
        outputCardLayout = result.cardLayout;
        imageDisplayLabel = result.imageDisplayLabel;
        copyOutputButton = result.copyOutputButton;
        outputPanel = result.outputPanel;

        // 保存图片按钮引用用于事件监听
        JButton saveImageButton = result.saveImageButton;
        JButton copyImageButton = result.copyImageButton;

        // 注册图片按钮到注册表以便在事件监听中获取
        registry.registerComponent("saveImageButton", saveImageButton);
        registry.registerComponent("copyImageButton", copyImageButton);

        return outputPanel;
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

        // 图片输出按钮事件 - 从注册表获取按钮
        JButton saveImageButton = registry.getComponent("saveImageButton");
        JButton copyImageButton = registry.getComponent("copyImageButton");

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
                MainWindow frame = new MainWindow();
                frame.setVisible(true);
            } catch (Exception e) {
                java.util.logging.Logger.getLogger(MainWindow.class.getName())
                    .severe("启动应用程序时发生错误: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
