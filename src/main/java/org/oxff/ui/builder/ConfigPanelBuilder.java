package org.oxff.ui.builder;

import org.oxff.ui.components.UIComponentRegistry;

import javax.swing.*;
import java.awt.*;

/**
 * 配置面板构建器
 * 负责构建所有操作配置面板
 */
public class ConfigPanelBuilder {
    private final UIComponentRegistry registry;

    public ConfigPanelBuilder(UIComponentRegistry registry) {
        this.registry = registry;
    }

    /**
     * 配置面板构建结果
     */
    public static class ConfigPanelsResult {
        public JPanel automationConfigPanel;
        public JPanel imageInputPanel;
        public JPanel baseEncodingConfigPanel;
        public JPanel passwordGeneratorConfigPanel;

        // 时间戳配置面板
        public JPanel getCurrentTimeConfigPanel;
        public JPanel timestampToDatetimeConfigPanel;
        public JPanel datetimeToTimestampConfigPanel;

        // 身份证配置面板
        public JPanel idCardGenerateConfigPanel;

        // 图片工具配置面板
        public JPanel blankImageConfigPanel;
        public JPanel imageResizeConfigPanel;
        public JPanel imageCompressConfigPanel;

        // 自动化配置组件
        public JSpinner delaySecondsSpinner;
        public JSpinner charIntervalMsSpinner;
        public JRadioButton inputSourceRadio;
        public JRadioButton clipboardSourceRadio;

        // 图片输入组件
        public JButton selectImageButton;
        public JButton pasteImageButton;
        public JLabel selectedImageLabel;

        // Base编码组件
        public JComboBox<String> baseEncodingComboBox;
        public JButton selectFileButton;
        public JLabel selectedFileLabel;
    }

    /**
     * 创建所有配置面板
     */
    public ConfigPanelsResult buildAllConfigPanels() {
        ConfigPanelsResult result = new ConfigPanelsResult();

        result.automationConfigPanel = createAutomationConfigPanel();
        result.delaySecondsSpinner = (JSpinner) registry.getComponent(UIComponentRegistry.DELAY_SECONDS_SPINNER);
        result.charIntervalMsSpinner = (JSpinner) registry.getComponent(UIComponentRegistry.CHAR_INTERVAL_MS_SPINNER);
        result.inputSourceRadio = (JRadioButton) registry.getComponent(UIComponentRegistry.INPUT_SOURCE_RADIO);
        result.clipboardSourceRadio = (JRadioButton) registry.getComponent(UIComponentRegistry.CLIPBOARD_SOURCE_RADIO);

        result.imageInputPanel = createImageInputPanel();
        result.selectImageButton = (JButton) registry.getComponent(UIComponentRegistry.SELECT_IMAGE_BUTTON);
        result.pasteImageButton = (JButton) registry.getComponent(UIComponentRegistry.PASTE_IMAGE_BUTTON);
        result.selectedImageLabel = (JLabel) registry.getComponent(UIComponentRegistry.SELECTED_IMAGE_LABEL);

        result.baseEncodingConfigPanel = createBaseEncodingConfigPanel();
        result.baseEncodingComboBox = (JComboBox<String>) registry.getComponent(UIComponentRegistry.BASE_ENCODING_COMBO_BOX);
        result.selectFileButton = (JButton) registry.getComponent(UIComponentRegistry.SELECT_FILE_BUTTON);
        result.selectedFileLabel = (JLabel) registry.getComponent(UIComponentRegistry.SELECTED_FILE_LABEL);

        result.passwordGeneratorConfigPanel = createPasswordGeneratorConfigPanel();

        // 使用 TimestampConfigPanelBuilder 创建时间戳配置面板
        TimestampConfigPanelBuilder timestampBuilder = new TimestampConfigPanelBuilder(registry);
        TimestampConfigPanelBuilder.TimestampConfigPanelsResult timestampResult =
            timestampBuilder.buildAllTimestampConfigPanels();

        result.getCurrentTimeConfigPanel = timestampResult.getCurrentTimeConfigPanel;
        result.timestampToDatetimeConfigPanel = timestampResult.timestampToDatetimeConfigPanel;
        result.datetimeToTimestampConfigPanel = timestampResult.datetimeToTimestampConfigPanel;

        // 构建身份证配置面板
        result.idCardGenerateConfigPanel = IdCardConfigPanelBuilder.createIdCardGenerateConfigPanel(registry);

        // 构建图片工具配置面板
        result.blankImageConfigPanel = ImageToolsConfigPanelBuilder.createBlankImageConfigPanel(registry);
        result.imageResizeConfigPanel = ImageToolsConfigPanelBuilder.createImageResizeConfigPanel(registry);
        result.imageCompressConfigPanel = ImageToolsConfigPanelBuilder.createImageCompressConfigPanel(registry);

        return result;
    }

    /**
     * 创建自动化配置面板
     */
    private JPanel createAutomationConfigPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("自动化输入配置"));

        panel.add(new JLabel("延迟时间(秒):"));
        SpinnerModel delayModel = new SpinnerNumberModel(3, 0, 60, 1);
        JSpinner delaySecondsSpinner = new JSpinner(delayModel);
        delaySecondsSpinner.setPreferredSize(new Dimension(60, 25));
        panel.add(delaySecondsSpinner);
        registry.registerComponent(UIComponentRegistry.DELAY_SECONDS_SPINNER, delaySecondsSpinner);

        panel.add(Box.createHorizontalStrut(15));

        panel.add(new JLabel("字符间隔(毫秒):"));
        SpinnerModel intervalModel = new SpinnerNumberModel(100, 0, 1000, 10);
        JSpinner charIntervalMsSpinner = new JSpinner(intervalModel);
        charIntervalMsSpinner.setPreferredSize(new Dimension(70, 25));
        panel.add(charIntervalMsSpinner);
        registry.registerComponent(UIComponentRegistry.CHAR_INTERVAL_MS_SPINNER, charIntervalMsSpinner);

        panel.add(Box.createHorizontalStrut(15));

        panel.add(new JLabel("输入来源:"));
        ButtonGroup sourceGroup = new ButtonGroup();
        JRadioButton inputSourceRadio = new JRadioButton("输入框", true);
        JRadioButton clipboardSourceRadio = new JRadioButton("剪贴板");
        sourceGroup.add(inputSourceRadio);
        sourceGroup.add(clipboardSourceRadio);
        panel.add(inputSourceRadio);
        panel.add(clipboardSourceRadio);
        registry.registerComponent(UIComponentRegistry.INPUT_SOURCE_RADIO, inputSourceRadio);
        registry.registerComponent(UIComponentRegistry.CLIPBOARD_SOURCE_RADIO, clipboardSourceRadio);

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
        JButton selectImageButton = new JButton("选择图片");
        JButton pasteImageButton = new JButton("粘贴图片");

        imageInputButtonPanel.add(selectImageButton);
        imageInputButtonPanel.add(pasteImageButton);

        registry.registerComponent(UIComponentRegistry.SELECT_IMAGE_BUTTON, selectImageButton);
        registry.registerComponent(UIComponentRegistry.PASTE_IMAGE_BUTTON, pasteImageButton);

        // 图片状态标签
        JLabel selectedImageLabel = new JLabel("未选择图片");
        selectedImageLabel.setForeground(Color.GRAY);
        selectedImageLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        registry.registerComponent(UIComponentRegistry.SELECTED_IMAGE_LABEL, selectedImageLabel);

        panel.add(imageInputButtonPanel, BorderLayout.NORTH);
        panel.add(selectedImageLabel, BorderLayout.CENTER);

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
        registry.registerComponent(UIComponentRegistry.BASE_ENCODING_COMBO_BOX, encodingTypeComboBox);

        // 文件选择按钮
        JButton selectFileButton = new JButton("选择图片文件");
        configPanel.add(Box.createHorizontalStrut(15));
        configPanel.add(selectFileButton);
        registry.registerComponent(UIComponentRegistry.SELECT_FILE_BUTTON, selectFileButton);

        panel.add(configPanel, BorderLayout.NORTH);

        // 文件状态标签
        JLabel selectedFileLabel = new JLabel("未选择文件");
        selectedFileLabel.setForeground(Color.GRAY);
        selectedFileLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.add(selectedFileLabel, BorderLayout.CENTER);
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
}
