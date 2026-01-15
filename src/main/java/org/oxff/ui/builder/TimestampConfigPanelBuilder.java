package org.oxff.ui.builder;

import org.oxff.ui.components.UIComponentRegistry;

import javax.swing.*;
import java.awt.*;

/**
 * 时间戳配置面板构建器
 */
public class TimestampConfigPanelBuilder {
    private final UIComponentRegistry registry;

    // 常用日期格式列表
    private static final String[] COMMON_DATE_FORMATS = {
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss.SSS",
        "yyyy/MM/dd HH:mm:ss",
        "yyyy年MM月dd日 HH:mm:ss",
        "yyyy-MM-dd",
        "yyyy/MM/dd",
        "yyyyMMddHHmmss",
        "yyyyMMddHHmmssSSS",
        "MM/dd/yyyy HH:mm:ss",
        "dd-MM-yyyy HH:mm:ss",
        "自定义"
    };

    // 时区列表
    private static final String[] TIMEZONES = {
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
    };

    public TimestampConfigPanelBuilder(UIComponentRegistry registry) {
        this.registry = registry;
    }

    /**
     * 配置面板构建结果
     */
    public static class TimestampConfigPanelsResult {
        public JPanel getCurrentTimeConfigPanel;
        public JPanel timestampToDatetimeConfigPanel;
        public JPanel datetimeToTimestampConfigPanel;

        // 获取当前时间配置组件
        public JComboBox<String> getCurrentTimezoneComboBox;
        public JRadioButton getCurrentDatetimeRadio;
        public JRadioButton getCurrentTimestampRadio;
        public JComboBox<String> getCurrentDateFormatComboBox;
        public JTextField getCurrentDateFormatTextField;
        public JRadioButton getCurrent10DigitsRadio;
        public JRadioButton getCurrent13DigitsRadio;
        public JCheckBox getCurrentPadWithZeroCheckBox;

        // 时间戳转日期配置组件
        public JComboBox<String> toDatetimeTimezoneComboBox;
        public JComboBox<String> toDatetimeFormatComboBox;
        public JTextField toDatetimeFormatTextField;
        public JTextField timestampToDatetimeInputField;

        // 日期转时间戳配置组件
        public JComboBox<String> toTimestampFormatComboBox;
        public JTextField toTimestampFormatTextField;
        public JRadioButton toTimestamp10DigitsRadio;
        public JRadioButton toTimestamp13DigitsRadio;
        public JCheckBox toTimestampPadWithZeroCheckBox;
        public JTextField datetimeToTimestampInputField;
    }

    /**
     * 构建所有时间戳配置面板
     */
    public TimestampConfigPanelsResult buildAllTimestampConfigPanels() {
        TimestampConfigPanelsResult result = new TimestampConfigPanelsResult();

        result.getCurrentTimeConfigPanel = buildGetCurrentTimeConfigPanel(result);
        result.timestampToDatetimeConfigPanel = buildTimestampToDatetimeConfigPanel(result);
        result.datetimeToTimestampConfigPanel = buildDatetimeToTimestampConfigPanel(result);

        return result;
    }

    /**
     * 构建获取当前时间配置面板
     */
    private JPanel buildGetCurrentTimeConfigPanel(TimestampConfigPanelsResult result) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("获取当前时间配置"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // 第一行: 时区选择
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("时区选择:"), gbc);

        gbc.gridx = 1;
        JComboBox<String> timezoneComboBox = new JComboBox<>(TIMEZONES);
        timezoneComboBox.setPreferredSize(new Dimension(200, 25));
        panel.add(timezoneComboBox, gbc);
        result.getCurrentTimezoneComboBox = timezoneComboBox;
        registry.registerComponent(UIComponentRegistry.GET_CURRENT_TIMEZONE_COMBO_BOX, timezoneComboBox);

        // 第二行: 输出类型
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("输出类型:"), gbc);

        gbc.gridx = 1;
        JPanel outputTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JRadioButton datetimeRadio = new JRadioButton("日期时间", true);
        JRadioButton timestampRadio = new JRadioButton("时间戳");
        ButtonGroup outputTypeGroup = new ButtonGroup();
        outputTypeGroup.add(datetimeRadio);
        outputTypeGroup.add(timestampRadio);
        outputTypePanel.add(datetimeRadio);
        outputTypePanel.add(timestampRadio);
        panel.add(outputTypePanel, gbc);
        result.getCurrentDatetimeRadio = datetimeRadio;
        result.getCurrentTimestampRadio = timestampRadio;
        registry.registerComponent(UIComponentRegistry.GET_CURRENT_DATETIME_RADIO, datetimeRadio);
        registry.registerComponent(UIComponentRegistry.GET_CURRENT_TIMESTAMP_RADIO, timestampRadio);

        // 第三行: 日期格式
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("日期格式:"), gbc);

        gbc.gridx = 1;
        JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JComboBox<String> dateFormatComboBox = new JComboBox<>(COMMON_DATE_FORMATS);
        dateFormatComboBox.setPreferredSize(new Dimension(180, 25));
        JTextField dateFormatTextField = new JTextField(20);
        dateFormatTextField.setText("yyyy-MM-dd HH:mm:ss");
        dateFormatTextField.setEnabled(false);
        formatPanel.add(dateFormatComboBox);
        formatPanel.add(dateFormatTextField);
        panel.add(formatPanel, gbc);
        result.getCurrentDateFormatComboBox = dateFormatComboBox;
        result.getCurrentDateFormatTextField = dateFormatTextField;
        registry.registerComponent(UIComponentRegistry.GET_CURRENT_DATE_FORMAT_COMBO_BOX, dateFormatComboBox);
        registry.registerComponent(UIComponentRegistry.GET_CURRENT_DATE_FORMAT_TEXT_FIELD, dateFormatTextField);

        // 第四行: 时间戳位数
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("时间戳位数:"), gbc);

        gbc.gridx = 1;
        JPanel digitsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JRadioButton digits10Radio = new JRadioButton("10位(秒)");
        JRadioButton digits13Radio = new JRadioButton("13位(毫秒)", true);
        ButtonGroup digitsGroup = new ButtonGroup();
        digitsGroup.add(digits10Radio);
        digitsGroup.add(digits13Radio);
        digitsPanel.add(digits10Radio);
        digitsPanel.add(digits13Radio);
        panel.add(digitsPanel, gbc);
        result.getCurrent10DigitsRadio = digits10Radio;
        result.getCurrent13DigitsRadio = digits13Radio;
        registry.registerComponent(UIComponentRegistry.GET_CURRENT_10_DIGITS_RADIO, digits10Radio);
        registry.registerComponent(UIComponentRegistry.GET_CURRENT_13_DIGITS_RADIO, digits13Radio);

        // 第五行: 前面补0
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("格式选项:"), gbc);

        gbc.gridx = 1;
        JCheckBox padWithZeroCheckBox = new JCheckBox("前面补0");
        panel.add(padWithZeroCheckBox, gbc);
        result.getCurrentPadWithZeroCheckBox = padWithZeroCheckBox;
        registry.registerComponent(UIComponentRegistry.GET_CURRENT_PAD_WITH_ZERO_CHECK_BOX, padWithZeroCheckBox);

        // 添加联动监听
        TimestampConfigListener listener = new TimestampConfigListener(result);
        datetimeRadio.addActionListener(e -> listener.onGetCurrrentOutputTypeChanged());
        timestampRadio.addActionListener(e -> listener.onGetCurrrentOutputTypeChanged());
        dateFormatComboBox.addActionListener(e -> listener.onGetCurrentDateFormatChanged());

        // 初始化状态
        listener.onGetCurrrentOutputTypeChanged();

        return panel;
    }

    /**
     * 构建时间戳转日期配置面板
     */
    private JPanel buildTimestampToDatetimeConfigPanel(TimestampConfigPanelsResult result) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("时间戳转日期配置"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // 第一行: 时区选择
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("时区选择:"), gbc);

        gbc.gridx = 1;
        JComboBox<String> timezoneComboBox = new JComboBox<>(TIMEZONES);
        timezoneComboBox.setPreferredSize(new Dimension(200, 25));
        panel.add(timezoneComboBox, gbc);
        result.toDatetimeTimezoneComboBox = timezoneComboBox;
        registry.registerComponent(UIComponentRegistry.TO_DATETIME_TIMEZONE_COMBO_BOX, timezoneComboBox);

        // 第二行: 日期格式
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("日期格式:"), gbc);

        gbc.gridx = 1;
        JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JComboBox<String> formatComboBox = new JComboBox<>(COMMON_DATE_FORMATS);
        formatComboBox.setSelectedIndex(0);
        formatComboBox.setPreferredSize(new Dimension(180, 25));
        JTextField formatTextField = new JTextField(20);
        formatTextField.setText("yyyy-MM-dd HH:mm:ss");
        formatTextField.setEnabled(false);
        formatPanel.add(formatComboBox);
        formatPanel.add(formatTextField);
        panel.add(formatPanel, gbc);
        result.toDatetimeFormatComboBox = formatComboBox;
        result.toDatetimeFormatTextField = formatTextField;
        registry.registerComponent(UIComponentRegistry.TO_DATETIME_FORMAT_COMBO_BOX, formatComboBox);
        registry.registerComponent(UIComponentRegistry.TO_DATETIME_FORMAT_TEXT_FIELD, formatTextField);

        // 第三行: 输入时间戳
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("输入时间戳:"), gbc);

        gbc.gridx = 1;
        JTextField inputTextField = new JTextField(30);
        inputTextField.setPreferredSize(new Dimension(300, 25));
        panel.add(inputTextField, gbc);
        result.timestampToDatetimeInputField = inputTextField;
        registry.registerComponent(UIComponentRegistry.TIMESTAMP_TO_DATETIME_INPUT_FIELD, inputTextField);

        // 添加联动监听
        TimestampConfigListener listener = new TimestampConfigListener(result);
        formatComboBox.addActionListener(e -> listener.onToDatetimeFormatChanged());

        return panel;
    }

    /**
     * 构建日期转时间戳配置面板
     */
    private JPanel buildDatetimeToTimestampConfigPanel(TimestampConfigPanelsResult result) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("日期转时间戳配置"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // 第一行: 输入格式
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("输入格式:"), gbc);

        gbc.gridx = 1;
        JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JComboBox<String> formatComboBox = new JComboBox<>(COMMON_DATE_FORMATS);
        formatComboBox.setSelectedIndex(0);
        formatComboBox.setPreferredSize(new Dimension(180, 25));
        JTextField formatTextField = new JTextField(20);
        formatTextField.setText("yyyy-MM-dd HH:mm:ss");
        formatTextField.setEnabled(false);
        formatPanel.add(formatComboBox);
        formatPanel.add(formatTextField);
        panel.add(formatPanel, gbc);
        result.toTimestampFormatComboBox = formatComboBox;
        result.toTimestampFormatTextField = formatTextField;
        registry.registerComponent(UIComponentRegistry.TO_TIMESTAMP_FORMAT_COMBO_BOX, formatComboBox);
        registry.registerComponent(UIComponentRegistry.TO_TIMESTAMP_FORMAT_TEXT_FIELD, formatTextField);

        // 第二行: 输出位数
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("输出位数:"), gbc);

        gbc.gridx = 1;
        JPanel digitsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JRadioButton digits10Radio = new JRadioButton("10位(秒)");
        JRadioButton digits13Radio = new JRadioButton("13位(毫秒)", true);
        ButtonGroup digitsGroup = new ButtonGroup();
        digitsGroup.add(digits10Radio);
        digitsGroup.add(digits13Radio);
        digitsPanel.add(digits10Radio);
        digitsPanel.add(digits13Radio);
        panel.add(digitsPanel, gbc);
        result.toTimestamp10DigitsRadio = digits10Radio;
        result.toTimestamp13DigitsRadio = digits13Radio;
        registry.registerComponent(UIComponentRegistry.TO_TIMESTAMP_10_DIGITS_RADIO, digits10Radio);
        registry.registerComponent(UIComponentRegistry.TO_TIMESTAMP_13_DIGITS_RADIO, digits13Radio);

        // 第三行: 前面补0
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("格式选项:"), gbc);

        gbc.gridx = 1;
        JCheckBox padWithZeroCheckBox = new JCheckBox("前面补0");
        panel.add(padWithZeroCheckBox, gbc);
        result.toTimestampPadWithZeroCheckBox = padWithZeroCheckBox;
        registry.registerComponent(UIComponentRegistry.TO_TIMESTAMP_PAD_WITH_ZERO_CHECK_BOX, padWithZeroCheckBox);

        // 第四行: 输入日期
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("输入日期:"), gbc);

        gbc.gridx = 1;
        JTextField inputTextField = new JTextField(30);
        inputTextField.setPreferredSize(new Dimension(300, 25));
        panel.add(inputTextField, gbc);
        result.datetimeToTimestampInputField = inputTextField;
        registry.registerComponent(UIComponentRegistry.DATETIME_TO_TIMESTAMP_INPUT_FIELD, inputTextField);

        // 添加联动监听
        TimestampConfigListener listener = new TimestampConfigListener(result);
        formatComboBox.addActionListener(e -> listener.onToTimestampFormatChanged());

        return panel;
    }
}
