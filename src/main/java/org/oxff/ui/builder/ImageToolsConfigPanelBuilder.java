package org.oxff.ui.builder;

import org.oxff.ui.components.UIComponentRegistry;
import org.oxff.util.ImageSizeSpecs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 图片工具配置面板构建器
 */
public class ImageToolsConfigPanelBuilder {

    /**
     * 创建生成空白图片配置面板
     */
    public static JPanel createBlankImageConfigPanel(UIComponentRegistry registry) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("生成空白图片配置"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // === 尺寸规格选择 ===
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("尺寸规格:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JPanel sizeSpecPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        JRadioButton customSizeRadio = new JRadioButton("自定义像素", true);
        JRadioButton presetSizeRadio = new JRadioButton("预设寸数", false);

        ButtonGroup sizeGroup = new ButtonGroup();
        sizeGroup.add(customSizeRadio);
        sizeGroup.add(presetSizeRadio);

        sizeSpecPanel.add(customSizeRadio);

        // 宽度和高度输入
        SpinnerNumberModel widthModel = new SpinnerNumberModel(295, 1, 10000, 1);
        JSpinner widthSpinner = new JSpinner(widthModel);
        widthSpinner.setPreferredSize(new Dimension(80, widthSpinner.getPreferredSize().height));
        sizeSpecPanel.add(widthSpinner);
        sizeSpecPanel.add(new JLabel("×"));

        SpinnerNumberModel heightModel = new SpinnerNumberModel(413, 1, 10000, 1);
        JSpinner heightSpinner = new JSpinner(heightModel);
        heightSpinner.setPreferredSize(new Dimension(80, heightSpinner.getPreferredSize().height));
        sizeSpecPanel.add(heightSpinner);
        sizeSpecPanel.add(new JLabel("像素"));

        // 寸数规格下拉框
        JComboBox<String> sizeSpecCombo = new JComboBox<>(ImageSizeSpecs.getSpecNames());
        sizeSpecCombo.setEnabled(false);
        sizeSpecPanel.add(presetSizeRadio);
        sizeSpecPanel.add(sizeSpecCombo);

        panel.add(sizeSpecPanel, gbc);

        // 注册组件
        registry.registerComponent(UIComponentRegistry.BLANK_IMAGE_WIDTH_SPINNER, widthSpinner);
        registry.registerComponent(UIComponentRegistry.BLANK_IMAGE_HEIGHT_SPINNER, heightSpinner);
        registry.registerComponent(UIComponentRegistry.BLANK_IMAGE_SIZE_SPEC_COMBO, sizeSpecCombo);
        registry.registerComponent("blankImageCustomSizeRadio", customSizeRadio);
        registry.registerComponent("blankImagePresetSizeRadio", presetSizeRadio);

        // 启用/禁用监听
        customSizeRadio.addActionListener(e -> {
            widthSpinner.setEnabled(true);
            heightSpinner.setEnabled(true);
            sizeSpecCombo.setEnabled(false);
        });

        presetSizeRadio.addActionListener(e -> {
            widthSpinner.setEnabled(false);
            heightSpinner.setEnabled(false);
            sizeSpecCombo.setEnabled(true);
            // 更新默认尺寸
            ImageSizeSpecs.SizeSpec spec = ImageSizeSpecs.getSpec((String) sizeSpecCombo.getSelectedItem());
            if (spec != null) {
                widthSpinner.setValue(spec.getWidthPixels());
                heightSpinner.setValue(spec.getHeightPixels());
            }
        });

        // 寸数规格改变时更新尺寸
        sizeSpecCombo.addActionListener(e -> {
            if (presetSizeRadio.isSelected()) {
                ImageSizeSpecs.SizeSpec spec = ImageSizeSpecs.getSpec((String) sizeSpecCombo.getSelectedItem());
                if (spec != null) {
                    widthSpinner.setValue(spec.getWidthPixels());
                    heightSpinner.setValue(spec.getHeightPixels());
                }
            }
        });

        row++;

        // === 背景颜色 ===
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("背景颜色:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        JButton colorButton = new JButton("选择颜色");
        JLabel colorPreview = new JLabel("      ");
        colorPreview.setOpaque(true);
        colorPreview.setBackground(Color.WHITE);
        colorPreview.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        colorPanel.add(colorButton);
        colorPanel.add(colorPreview);
        colorPanel.add(new JLabel("(默认白色)"));

        panel.add(colorPanel, gbc);

        // 注册组件
        registry.registerComponent(UIComponentRegistry.BLANK_IMAGE_BG_COLOR_BUTTON, colorButton);
        registry.registerComponent(UIComponentRegistry.BLANK_IMAGE_BG_COLOR_PREVIEW, colorPreview);

        // 创建隐藏的标签来存储颜色值
        final JLabel colorValueLabel = new JLabel("#FFFFFF");
        colorValueLabel.setVisible(false);
        panel.add(colorValueLabel);
        registry.registerComponent("blankImageBgColorValue", colorValueLabel);

        // 颜色选择监听
        colorButton.addActionListener(new ActionListener() {
            private Color selectedColor = Color.WHITE;

            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(panel, "选择背景颜色", selectedColor);
                if (color != null) {
                    selectedColor = color;
                    colorPreview.setBackground(color);
                    // 存储颜色值
                    String hexColor = String.format("%02X%02X%02X",
                        color.getRed(), color.getGreen(), color.getBlue());
                    colorValueLabel.setText(hexColor);
                }
            }
        });

        row++;

        // === 输出格式 ===
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("输出格式:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        JRadioButton pngRadio = new JRadioButton("PNG", true);
        JRadioButton jpegRadio = new JRadioButton("JPEG", false);

        ButtonGroup formatGroup = new ButtonGroup();
        formatGroup.add(pngRadio);
        formatGroup.add(jpegRadio);

        formatPanel.add(pngRadio);
        formatPanel.add(jpegRadio);

        panel.add(formatPanel, gbc);

        // 注册格式组件
        registry.registerComponent(UIComponentRegistry.BLANK_IMAGE_FORMAT_PNG_RADIO, pngRadio);
        registry.registerComponent(UIComponentRegistry.BLANK_IMAGE_FORMAT_JPEG_RADIO, jpegRadio);

        return panel;
    }

    /**
     * 创建图片尺寸转换配置面板
     */
    public static JPanel createImageResizeConfigPanel(UIComponentRegistry registry) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("图片尺寸转换配置"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // === 图片源 ===
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("选择图片:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JPanel sourcePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        JButton selectButton = new JButton("选择图片");
        JButton pasteButton = new JButton("粘贴图片");
        JLabel sourceLabel = new JLabel("未选择图片");
        sourceLabel.setForeground(Color.GRAY);

        sourcePanel.add(selectButton);
        sourcePanel.add(pasteButton);
        sourcePanel.add(sourceLabel);

        panel.add(sourcePanel, gbc);

        // 注册组件
        registry.registerComponent(UIComponentRegistry.IMAGE_RESIZE_SELECT_BUTTON, selectButton);
        registry.registerComponent(UIComponentRegistry.IMAGE_RESIZE_PASTE_BUTTON, pasteButton);
        registry.registerComponent(UIComponentRegistry.IMAGE_RESIZE_SOURCE_LABEL, sourceLabel);

        // 创建隐藏的标签来存储图片路径
        JLabel pathLabel = new JLabel("");
        pathLabel.setVisible(false);
        panel.add(pathLabel);
        registry.registerComponent("imageResizeSelectedPath", pathLabel);

        row++;

        // === 目标尺寸 ===
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("目标尺寸:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JPanel targetSizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        JRadioButton customSizeRadio = new JRadioButton("自定义像素", true);
        JRadioButton presetSizeRadio = new JRadioButton("预设寸数", false);

        ButtonGroup sizeGroup = new ButtonGroup();
        sizeGroup.add(customSizeRadio);
        sizeGroup.add(presetSizeRadio);

        targetSizePanel.add(customSizeRadio);

        SpinnerNumberModel widthModel = new SpinnerNumberModel(800, 1, 10000, 1);
        JSpinner widthSpinner = new JSpinner(widthModel);
        widthSpinner.setPreferredSize(new Dimension(80, widthSpinner.getPreferredSize().height));
        targetSizePanel.add(widthSpinner);
        targetSizePanel.add(new JLabel("×"));

        SpinnerNumberModel heightModel = new SpinnerNumberModel(600, 1, 10000, 1);
        JSpinner heightSpinner = new JSpinner(heightModel);
        heightSpinner.setPreferredSize(new Dimension(80, heightSpinner.getPreferredSize().height));
        targetSizePanel.add(heightSpinner);
        targetSizePanel.add(new JLabel("像素"));

        JComboBox<String> sizeSpecCombo = new JComboBox<>(ImageSizeSpecs.getSpecNames());
        sizeSpecCombo.setEnabled(false);
        targetSizePanel.add(presetSizeRadio);
        targetSizePanel.add(sizeSpecCombo);

        panel.add(targetSizePanel, gbc);

        // 注册组件
        registry.registerComponent(UIComponentRegistry.IMAGE_RESIZE_WIDTH_SPINNER, widthSpinner);
        registry.registerComponent(UIComponentRegistry.IMAGE_RESIZE_HEIGHT_SPINNER, heightSpinner);
        registry.registerComponent(UIComponentRegistry.IMAGE_RESIZE_SIZE_SPEC_COMBO, sizeSpecCombo);
        registry.registerComponent("imageResizeCustomSizeRadio", customSizeRadio);
        registry.registerComponent("imageResizePresetSizeRadio", presetSizeRadio);

        // 启用/禁用监听
        customSizeRadio.addActionListener(e -> {
            widthSpinner.setEnabled(true);
            heightSpinner.setEnabled(true);
            sizeSpecCombo.setEnabled(false);
        });

        presetSizeRadio.addActionListener(e -> {
            widthSpinner.setEnabled(false);
            heightSpinner.setEnabled(false);
            sizeSpecCombo.setEnabled(true);
        });

        row++;

        // === 保持宽高比 ===
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel(""), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JCheckBox maintainRatioCheck = new JCheckBox("保持宽高比", true);
        panel.add(maintainRatioCheck, gbc);

        // 注册组件
        registry.registerComponent(UIComponentRegistry.IMAGE_RESIZE_MAINTAIN_RATIO_CHECK, maintainRatioCheck);

        return panel;
    }

    /**
     * 创建图片压缩配置面板
     */
    public static JPanel createImageCompressConfigPanel(UIComponentRegistry registry) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("图片压缩配置"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // === 图片源 ===
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("选择图片:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JPanel sourcePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        JButton selectButton = new JButton("选择图片");
        JButton pasteButton = new JButton("粘贴图片");
        JLabel sourceLabel = new JLabel("未选择图片");
        sourceLabel.setForeground(Color.GRAY);

        sourcePanel.add(selectButton);
        sourcePanel.add(pasteButton);
        sourcePanel.add(sourceLabel);

        panel.add(sourcePanel, gbc);

        // 注册组件
        registry.registerComponent(UIComponentRegistry.IMAGE_COMPRESS_SELECT_BUTTON, selectButton);
        registry.registerComponent(UIComponentRegistry.IMAGE_COMPRESS_PASTE_BUTTON, pasteButton);
        registry.registerComponent(UIComponentRegistry.IMAGE_COMPRESS_SOURCE_LABEL, sourceLabel);

        // 创建隐藏的标签来存储图片路径
        JLabel pathLabel = new JLabel("");
        pathLabel.setVisible(false);
        panel.add(pathLabel);
        registry.registerComponent("imageCompressSelectedPath", pathLabel);

        row++;

        // === 压缩格式 ===
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("压缩格式:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        JRadioButton jpegRadio = new JRadioButton("JPEG 有损压缩", true);
        JRadioButton pngRadio = new JRadioButton("PNG 无损压缩", false);

        ButtonGroup formatGroup = new ButtonGroup();
        formatGroup.add(jpegRadio);
        formatGroup.add(pngRadio);

        formatPanel.add(jpegRadio);
        formatPanel.add(pngRadio);

        // 添加格式说明标签
        JLabel formatHintLabel = new JLabel("<html><font color='gray' style='font-size:11px'>JPEG: 文件小，有质量损失 | PNG: 质量不变，文件较大</font></html>");
        formatPanel.add(formatHintLabel);

        panel.add(formatPanel, gbc);

        // 注册组件
        registry.registerComponent(UIComponentRegistry.IMAGE_COMPRESS_FORMAT_JPEG_RADIO, jpegRadio);
        registry.registerComponent(UIComponentRegistry.IMAGE_COMPRESS_FORMAT_PNG_RADIO, pngRadio);

        row++;

        // === JPEG压缩强度 ===
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("压缩强度:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JPanel levelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        JSlider levelSlider = new JSlider(0, 100, 50);
        levelSlider.setMajorTickSpacing(25);
        levelSlider.setMinorTickSpacing(5);
        levelSlider.setPaintTicks(true);
        levelSlider.setPaintLabels(true);

        JLabel levelLabel = new JLabel("中等");
        levelLabel.setPreferredSize(new Dimension(60, levelLabel.getPreferredSize().height));

        levelPanel.add(levelSlider);
        levelPanel.add(new JLabel("("));
        levelPanel.add(levelLabel);
        levelPanel.add(new JLabel(")"));
        levelPanel.add(new JLabel("<html><font color='gray' style='font-size:11px'>值越大文件越小，质量越低</font></html>"));

        panel.add(levelPanel, gbc);

        // 注册组件
        registry.registerComponent(UIComponentRegistry.IMAGE_COMPRESS_LEVEL_SLIDER, levelSlider);
        registry.registerComponent(UIComponentRegistry.IMAGE_COMPRESS_LEVEL_LABEL, levelLabel);

        // 滑块值改变监听 - 更新描述文本
        levelSlider.addChangeListener(e -> {
            int value = levelSlider.getValue();
            String desc;
            if (value <= 20) {
                desc = "微弱";
            } else if (value <= 40) {
                desc = "轻度";
            } else if (value <= 60) {
                desc = "中等";
            } else if (value <= 80) {
                desc = "强度";
            } else {
                desc = "极大";
            }
            levelLabel.setText(desc);
        });

        row++;

        // === 显示对比 ===
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel(""), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JCheckBox showComparisonCheck = new JCheckBox("显示压缩前后对比", false);
        panel.add(showComparisonCheck, gbc);

        // 添加说明：对比信息会显示在日志区域，图片仍然显示
        gbc.gridx = 1;
        gbc.gridy = row + 1;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 5, 5, 5);
        JLabel comparisonHintLabel = new JLabel("<html><font color='gray' style='font-size:11px'>勾选后在日志区域显示对比信息，图片仍然显示</font></html>");
        panel.add(comparisonHintLabel, gbc);

        // 注册组件
        registry.registerComponent(UIComponentRegistry.IMAGE_COMPRESS_SHOW_COMPARISON_CHECK, showComparisonCheck);

        return panel;
    }
}
