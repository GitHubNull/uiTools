package org.oxff.ui.builder;

import javax.swing.*;

/**
 * 时间戳配置面板联动监听器
 */
public class TimestampConfigListener {
    private final TimestampConfigPanelBuilder.TimestampConfigPanelsResult result;

    public TimestampConfigListener(TimestampConfigPanelBuilder.TimestampConfigPanelsResult result) {
        this.result = result;
    }

    /**
     * 获取当前时间 - 输出类型切换
     */
    public void onGetCurrrentOutputTypeChanged() {
        boolean isDatetime = result.getCurrentDatetimeRadio.isSelected();

        // 日期格式相关组件
        result.getCurrentDateFormatComboBox.setEnabled(isDatetime);
        result.getCurrentDateFormatTextField.setEnabled(isDatetime && "自定义".equals(result.getCurrentDateFormatComboBox.getSelectedItem()));

        // 时间戳位数相关组件
        result.getCurrent10DigitsRadio.setEnabled(!isDatetime);
        result.getCurrent13DigitsRadio.setEnabled(!isDatetime);
        result.getCurrentPadWithZeroCheckBox.setEnabled(!isDatetime);
    }

    /**
     * 获取当前时间 - 日期格式选择
     */
    public void onGetCurrentDateFormatChanged() {
        String selected = (String) result.getCurrentDateFormatComboBox.getSelectedItem();
        boolean isCustom = "自定义".equals(selected);

        result.getCurrentDateFormatTextField.setEnabled(isCustom && result.getCurrentDatetimeRadio.isSelected());

        if (!isCustom) {
            result.getCurrentDateFormatTextField.setText(selected);
        }
    }

    /**
     * 时间戳转日期 - 格式选择
     */
    public void onToDatetimeFormatChanged() {
        String selected = (String) result.toDatetimeFormatComboBox.getSelectedItem();
        boolean isCustom = "自定义".equals(selected);

        result.toDatetimeFormatTextField.setEnabled(isCustom);

        if (!isCustom) {
            result.toDatetimeFormatTextField.setText(selected);
        }
    }

    /**
     * 日期转时间戳 - 格式选择
     */
    public void onToTimestampFormatChanged() {
        String selected = (String) result.toTimestampFormatComboBox.getSelectedItem();
        boolean isCustom = "自定义".equals(selected);

        result.toTimestampFormatTextField.setEnabled(isCustom);

        if (!isCustom) {
            result.toTimestampFormatTextField.setText(selected);
        }
    }
}
