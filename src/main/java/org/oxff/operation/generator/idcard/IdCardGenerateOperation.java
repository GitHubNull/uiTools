package org.oxff.operation.generator.idcard;

import org.oxff.core.OperationCategory;
import org.oxff.core.Subcategory;
import org.oxff.core.SubcategoryRegistry;
import org.oxff.operation.Operation;
import org.oxff.util.IdCardUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 身份证号码生成操作
 * 可指定省份、市、年龄/出生日期、性别、生成个数
 */
public class IdCardGenerateOperation implements Operation {

    private String provinceCode;  // 省份代码
    private String cityCode;      // 市代码
    private int age;              // 年龄
    private String birthDate;     // 出生日期（格式：yyyy-MM-dd，优先级高于age）
    private String gender;        // 性别：M（男）/F（女）/null（随机）
    private int count = 1;        // 生成个数

    // 随机数生成器
    private final Random random = new Random();

    // Setter方法（通过反射调用）
    public void setProvinceCode(String code) {
        this.provinceCode = code;
    }

    public void setCityCode(String code) {
        this.cityCode = code;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setBirthDate(String date) {
        this.birthDate = date;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String execute(String input) {
        List<String> results = new ArrayList<>();

        // 确定行政区划代码（前6位）
        String regionCode = determineRegionCode();

        // 确定出生日期（第7-14位）
        String birthDateStr = determineBirthDate();

        // 生成指定个数的身份证号码
        for (int i = 0; i < count; i++) {
            // 生成顺序码（第15-17位）
            String sequenceCode = generateSequenceCode();

            // 根据性别调整顺序码
            String adjustedSequenceCode = IdCardUtils.adjustSequenceCodeForGender(sequenceCode, gender);

            // 组合前17位
            String idCard17 = regionCode + birthDateStr + adjustedSequenceCode;

            // 计算校验位（第18位）
            char checkDigit = IdCardUtils.calculateCheckDigit(idCard17);

            // 组合完整的18位身份证号码
            String idCard = idCard17 + checkDigit;
            results.add(idCard);
        }

        // 多个结果用换行分隔
        return String.join("\n", results);
    }

    /**
     * 确定行政区划代码（前6位）
     */
    private String determineRegionCode() {
        // 如果指定了市代码，直接使用
        if (cityCode != null && !cityCode.isEmpty() && cityCode.length() == 6) {
            return cityCode;
        }

        // 如果指定了省份代码，生成该省下的随机市代码
        if (provinceCode != null && !provinceCode.isEmpty()) {
            // 获取该省的所有城市
            String[] cityNames = IdCardUtils.getCityNames(provinceCode);
            if (cityNames != null && cityNames.length > 0) {
                // 随机选择一个城市
                String randomCity = cityNames[random.nextInt(cityNames.length)];
                String cityCode = IdCardUtils.getCityCode(provinceCode, randomCity);
                if (cityCode != null) {
                    return cityCode;
                }
            }
            // 如果没有找到城市，使用省份代码的后4位加上随机数
            return provinceCode.substring(0, 2) + String.format("%02d", random.nextInt(100)) + "00";
        }

        // 如果没有指定省份，随机生成一个行政区划代码
        // 这里使用一个常见的范围
        int regionCodeNum = 110000 + random.nextInt(540000 - 110000);
        // 确保是6位数字
        return String.format("%06d", regionCodeNum);
    }

    /**
     * 确定出生日期（第7-14位，yyyyMMdd格式）
     */
    private String determineBirthDate() {
        // 如果指定了出生日期，优先使用
        if (birthDate != null && !birthDate.isEmpty()) {
            // 转换格式：yyyy-MM-dd -> yyyyMMdd
            return birthDate.replace("-", "");
        }

        // 如果指定了年龄，根据年龄计算出生日期
        if (age > 0) {
            return IdCardUtils.calculateBirthDate(age);
        }

        // 如果都没有指定，生成一个随机的合理出生日期
        // 年龄范围：18-70岁
        int randomAge = 18 + random.nextInt(53);
        return IdCardUtils.calculateBirthDate(randomAge);
    }

    /**
     * 生成随机顺序码（3位数字）
     */
    private String generateSequenceCode() {
        return IdCardUtils.generateSequenceCode();
    }

    @Override
    public OperationCategory getCategory() {
        return OperationCategory.GENERATOR;
    }

    @Override
    public String getDisplayName() {
        return "生成身份证号码";
    }

    @Override
    public Subcategory getSubcategory() {
        return SubcategoryRegistry.getSubcategory("idcard");
    }
}
