package org.oxff.ui.builder;

import org.oxff.ui.components.UIComponentRegistry;
import org.oxff.util.IdCardUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;

/**
 * 身份证配置面板构建器
 */
public class IdCardConfigPanelBuilder {

    /**
     * 创建身份证生成配置面板
     */
    public static JPanel createIdCardGenerateConfigPanel(UIComponentRegistry registry) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("身份证号码生成配置"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // === 省份选择 ===
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel("省份:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        String[] provinces = IdCardUtils.getProvinceNames();
        JComboBox<String> provinceComboBox = new JComboBox<>(provinces);
        provinceComboBox.insertItemAt("全部", 0);
        provinceComboBox.setSelectedIndex(0);
        registry.registerComponent(UIComponentRegistry.ID_CARD_PROVINCE_COMBO_BOX, provinceComboBox);
        panel.add(provinceComboBox, gbc);

        row++;

        // === 市级选择 ===
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel("市级:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JComboBox<String> cityComboBox = new JComboBox<>();
        cityComboBox.addItem("全部");
        registry.registerComponent(UIComponentRegistry.ID_CARD_CITY_COMBO_BOX, cityComboBox);
        panel.add(cityComboBox, gbc);

        row++;

        // 省市联动监听器
        provinceComboBox.addActionListener(e -> {
            String selectedProvince = (String) provinceComboBox.getSelectedItem();
            cityComboBox.removeAllItems();

            if ("全部".equals(selectedProvince)) {
                cityComboBox.addItem("全部");
            } else {
                cityComboBox.addItem("全部");
                String provinceCode = IdCardUtils.getProvinceCode(selectedProvince);
                if (provinceCode != null) {
                    String[] cities = IdCardUtils.getCityNames(provinceCode);
                    for (String city : cities) {
                        cityComboBox.addItem(city);
                    }
                }
            }
        });

        // === 年龄/出生日期选择 ===
        JPanel ageBirthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JRadioButton ageRadio = new JRadioButton("年龄:", true);
        JRadioButton birthDateRadio = new JRadioButton("出生日期:", false);

        ButtonGroup ageBirthGroup = new ButtonGroup();
        ageBirthGroup.add(ageRadio);
        ageBirthGroup.add(birthDateRadio);

        SpinnerNumberModel ageModel = new SpinnerNumberModel(25, 0, 150, 1);
        JSpinner ageSpinner = new JSpinner(ageModel);
        ageSpinner.setPreferredSize(new Dimension(80, ageSpinner.getPreferredSize().height));

        // 日期选择器
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        // 年
        SpinnerNumberModel yearModel = new SpinnerNumberModel(
            Calendar.getInstance().get(Calendar.YEAR) - 25,
            1900,
            Calendar.getInstance().get(Calendar.YEAR),
            1
        );
        JSpinner yearSpinner = new JSpinner(yearModel);
        yearSpinner.setPreferredSize(new Dimension(70, yearSpinner.getPreferredSize().height));

        // 月
        SpinnerNumberModel monthModel = new SpinnerNumberModel(1, 1, 12, 1);
        JSpinner monthSpinner = new JSpinner(monthModel);
        monthSpinner.setPreferredSize(new Dimension(50, monthSpinner.getPreferredSize().height));

        // 日
        SpinnerNumberModel dayModel = new SpinnerNumberModel(1, 1, 31, 1);
        JSpinner daySpinner = new JSpinner(dayModel);
        daySpinner.setPreferredSize(new Dimension(50, daySpinner.getPreferredSize().height));

        datePanel.add(yearSpinner);
        datePanel.add(new JLabel("-"));
        datePanel.add(monthSpinner);
        datePanel.add(new JLabel("-"));
        datePanel.add(daySpinner);

        // 组装年龄/出生日期面板
        ageBirthPanel.add(ageRadio);
        ageBirthPanel.add(ageSpinner);
        ageBirthPanel.add(new JLabel("岁 "));
        ageBirthPanel.add(birthDateRadio);
        ageBirthPanel.add(datePanel);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        panel.add(ageBirthPanel, gbc);

        // 注册组件
        registry.registerComponent(UIComponentRegistry.ID_CARD_AGE_RADIO, ageRadio);
        registry.registerComponent(UIComponentRegistry.ID_CARD_BIRTHDATE_RADIO, birthDateRadio);
        registry.registerComponent(UIComponentRegistry.ID_CARD_AGE_SPINNER, ageSpinner);

        // 启用/禁用监听
        ageSpinner.setEnabled(true);
        yearSpinner.setEnabled(false);
        monthSpinner.setEnabled(false);
        daySpinner.setEnabled(false);

        ageRadio.addActionListener(e -> {
            ageSpinner.setEnabled(true);
            yearSpinner.setEnabled(false);
            monthSpinner.setEnabled(false);
            daySpinner.setEnabled(false);
        });

        birthDateRadio.addActionListener(e -> {
            ageSpinner.setEnabled(false);
            yearSpinner.setEnabled(true);
            monthSpinner.setEnabled(true);
            daySpinner.setEnabled(true);
        });

        // 存储日期组件的引用（用于后续获取值）
        registry.registerComponent("idCardBirthDateYearSpinner", yearSpinner);
        registry.registerComponent("idCardBirthDateMonthSpinner", monthSpinner);
        registry.registerComponent("idCardBirthDateDaySpinner", daySpinner);

        row++;

        // === 性别选择 ===
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("性别:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        JRadioButton randomRadio = new JRadioButton("随机", true);
        JRadioButton maleRadio = new JRadioButton("男", false);
        JRadioButton femaleRadio = new JRadioButton("女", false);

        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(randomRadio);
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);

        genderPanel.add(randomRadio);
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);

        panel.add(genderPanel, gbc);

        // 注册性别组件
        registry.registerComponent(UIComponentRegistry.ID_CARD_GENDER_RANDOM_RADIO, randomRadio);
        registry.registerComponent(UIComponentRegistry.ID_CARD_GENDER_MALE_RADIO, maleRadio);
        registry.registerComponent(UIComponentRegistry.ID_CARD_GENDER_FEMALE_RADIO, femaleRadio);

        row++;

        // === 生成个数 ===
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("生成个数:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        SpinnerNumberModel countModel = new SpinnerNumberModel(1, 1, 100, 1);
        JSpinner countSpinner = new JSpinner(countModel);
        registry.registerComponent(UIComponentRegistry.ID_CARD_COUNT_SPINNER, countSpinner);
        panel.add(countSpinner, gbc);

        return panel;
    }
}
