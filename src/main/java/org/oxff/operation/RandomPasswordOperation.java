package org.oxff.operation;

import org.oxff.core.OperationCategory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 随机密码生成操作实现
 * 根据指定规则生成随机密码
 */
public class RandomPasswordOperation implements Operation {

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    private static final SecureRandom RANDOM = new SecureRandom();

    // 配置参数（由UI设置）
    private int passwordLength = 16;
    private boolean includeDigits = true;
    private int digitCount = 4;
    private boolean includeUppercase = true;
    private int uppercaseCount = 4;
    private boolean includeLowercase = true;
    private int lowercaseCount = 4;
    private boolean includeSpecialChars = false;
    private int specialCharCount = 2;
    private int passwordCount = 1;

    /**
     * 设置密码长度
     */
    public void setPasswordLength(int passwordLength) {
        this.passwordLength = passwordLength;
    }

    /**
     * 设置是否包含数字
     */
    public void setIncludeDigits(boolean includeDigits) {
        this.includeDigits = includeDigits;
    }

    /**
     * 设置数字个数
     */
    public void setDigitCount(int digitCount) {
        this.digitCount = digitCount;
    }

    /**
     * 设置是否包含大写字母
     */
    public void setIncludeUppercase(boolean includeUppercase) {
        this.includeUppercase = includeUppercase;
    }

    /**
     * 设置大写字母个数
     */
    public void setUppercaseCount(int uppercaseCount) {
        this.uppercaseCount = uppercaseCount;
    }

    /**
     * 设置是否包含小写字母
     */
    public void setIncludeLowercase(boolean includeLowercase) {
        this.includeLowercase = includeLowercase;
    }

    /**
     * 设置小写字母个数
     */
    public void setLowercaseCount(int lowercaseCount) {
        this.lowercaseCount = lowercaseCount;
    }

    /**
     * 设置是否包含特殊字符
     */
    public void setIncludeSpecialChars(boolean includeSpecialChars) {
        this.includeSpecialChars = includeSpecialChars;
    }

    /**
     * 设置特殊字符数量
     */
    public void setSpecialCharCount(int specialCharCount) {
        this.specialCharCount = specialCharCount;
    }

    /**
     * 设置生成个数
     */
    public void setPasswordCount(int passwordCount) {
        this.passwordCount = passwordCount;
    }

    @Override
    public String execute(String input) {
        try {
            List<String> passwords = new ArrayList<>();

            for (int i = 0; i < passwordCount; i++) {
                passwords.add(generatePassword());
            }

            // 如果只生成一个密码，直接返回
            if (passwords.size() == 1) {
                return passwords.get(0);
            }

            // 多个密码用换行分隔
            return String.join("\n", passwords);

        } catch (Exception e) {
            return "生成密码失败: " + e.getMessage();
        }
    }

    /**
     * 生成单个密码
     */
    private String generatePassword() {
        List<Character> passwordChars = new ArrayList<>();

        // 根据配置添加各类字符
        if (includeDigits && digitCount > 0) {
            for (int i = 0; i < digitCount; i++) {
                passwordChars.add(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
            }
        }

        if (includeUppercase && uppercaseCount > 0) {
            for (int i = 0; i < uppercaseCount; i++) {
                passwordChars.add(UPPERCASE.charAt(RANDOM.nextInt(UPPERCASE.length())));
            }
        }

        if (includeLowercase && lowercaseCount > 0) {
            for (int i = 0; i < lowercaseCount; i++) {
                passwordChars.add(LOWERCASE.charAt(RANDOM.nextInt(LOWERCASE.length())));
            }
        }

        if (includeSpecialChars && specialCharCount > 0) {
            for (int i = 0; i < specialCharCount; i++) {
                passwordChars.add(SPECIAL_CHARS.charAt(RANDOM.nextInt(SPECIAL_CHARS.length())));
            }
        }

        // 如果没有选择任何类型，默认使用小写字母
        if (passwordChars.isEmpty()) {
            for (int i = 0; i < passwordLength; i++) {
                passwordChars.add(LOWERCASE.charAt(RANDOM.nextInt(LOWERCASE.length())));
            }
        }

        // 打乱字符顺序
        Collections.shuffle(passwordChars, RANDOM);

        // 构建最终密码字符串
        StringBuilder result = new StringBuilder();
        for (Character c : passwordChars) {
            result.append(c);
        }

        return result.toString();
    }

    @Override
    public OperationCategory getCategory() {
        return OperationCategory.GENERATOR;
    }

    @Override
    public String getDisplayName() {
        return "生成随机密码";
    }
}
