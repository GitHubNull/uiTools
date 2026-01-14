package org.oxff.ui.builder;

import javax.swing.*;

/**
 * 密码配置动态联动监听器
 * 处理密码生成器配置面板中各个组件之间的动态联动逻辑
 */
public class PasswordConfigListener {
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
