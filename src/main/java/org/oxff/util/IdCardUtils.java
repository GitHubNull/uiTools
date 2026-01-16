package org.oxff.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 身份证号码工具类
 * 提供身份证号码生成、校验等功能
 */
public class IdCardUtils {

    /**
     * 市级信息
     */
    public static class CityInfo {
        private final String code;
        private final String name;

        public CityInfo(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }

    // 校验位权重
    private static final int[] WEIGHTS = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    // 校验码映射
    private static final char[] CHECK_CODES = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    // 省份数据 (代码 -> 名称)
    private static final Map<String, String> PROVINCES = new TreeMap<>();

    // 市级数据 (省份代码 -> 市列表)
    private static final Map<String, List<CityInfo>> CITIES = new TreeMap<>();

    static {
        loadRegionData();
    }

    /**
     * 从资源文件加载行政区划代码数据
     */
    private static void loadRegionData() {
        try (InputStream is = IdCardUtils.class.getResourceAsStream("/region_code_data.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // 跳过空行和注释
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // 格式：代码|名称|级别 (1=省级, 2=市级)
                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    String code = parts[0];
                    String name = parts[1];
                    String level = parts[2];

                    if ("1".equals(level)) {
                        // 省级
                        PROVINCES.put(code, name);
                        CITIES.put(code, new ArrayList<>());
                    } else if ("2".equals(level)) {
                        // 市级，关联到省级
                        String provinceCode = code.substring(0, 2) + "0000";
                        List<CityInfo> cityList = CITIES.get(provinceCode);
                        if (cityList != null) {
                            cityList.add(new CityInfo(code, name));
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 如果加载失败，使用默认数据
            loadDefaultData();
        }
    }

    /**
     * 加载默认的行政区划数据（当资源文件加载失败时使用）
     */
    private static void loadDefaultData() {
        // 主要省份
        PROVINCES.put("110000", "北京市");
        PROVINCES.put("120000", "天津市");
        PROVINCES.put("130000", "河北省");
        PROVINCES.put("140000", "山西省");
        PROVINCES.put("150000", "内蒙古自治区");
        PROVINCES.put("210000", "辽宁省");
        PROVINCES.put("220000", "吉林省");
        PROVINCES.put("230000", "黑龙江省");
        PROVINCES.put("310000", "上海市");
        PROVINCES.put("320000", "江苏省");
        PROVINCES.put("330000", "浙江省");
        PROVINCES.put("340000", "安徽省");
        PROVINCES.put("350000", "福建省");
        PROVINCES.put("360000", "江西省");
        PROVINCES.put("370000", "山东省");
        PROVINCES.put("410000", "河南省");
        PROVINCES.put("420000", "湖北省");
        PROVINCES.put("430000", "湖南省");
        PROVINCES.put("440000", "广东省");
        PROVINCES.put("450000", "广西壮族自治区");
        PROVINCES.put("460000", "海南省");
        PROVINCES.put("500000", "重庆市");
        PROVINCES.put("510000", "四川省");
        PROVINCES.put("520000", "贵州省");
        PROVINCES.put("530000", "云南省");
        PROVINCES.put("540000", "西藏自治区");
        PROVINCES.put("610000", "陕西省");
        PROVINCES.put("620000", "甘肃省");
        PROVINCES.put("630000", "青海省");
        PROVINCES.put("640000", "宁夏回族自治区");
        PROVINCES.put("650000", "新疆维吾尔自治区");

        // 初始化市级列表
        for (String code : PROVINCES.keySet()) {
            CITIES.put(code, new ArrayList<>());
        }

        // 添加主要城市
        List<CityInfo> guangdongCities = CITIES.get("440000");
        if (guangdongCities != null) {
            guangdongCities.add(new CityInfo("440100", "广州市"));
            guangdongCities.add(new CityInfo("440300", "深圳市"));
            guangdongCities.add(new CityInfo("440400", "珠海市"));
            guangdongCities.add(new CityInfo("440600", "佛山市"));
            guangdongCities.add(new CityInfo("441900", "东莞市"));
            guangdongCities.add(new CityInfo("442000", "中山市"));
        }

        List<CityInfo> beijingCities = CITIES.get("110000");
        if (beijingCities != null) {
            beijingCities.add(new CityInfo("110100", "北京市市辖区"));
        }

        List<CityInfo> shanghaiCities = CITIES.get("310000");
        if (shanghaiCities != null) {
            shanghaiCities.add(new CityInfo("310100", "上海市市辖区"));
        }
    }

    /**
     * 获取所有省份名称
     * @return 省份名称数组
     */
    public static String[] getProvinceNames() {
        return PROVINCES.values().toArray(new String[0]);
    }

    /**
     * 根据省份名称获取省份代码
     * @param name 省份名称
     * @return 省份代码，如果不存在返回null
     */
    public static String getProvinceCode(String name) {
        for (Map.Entry<String, String> entry : PROVINCES.entrySet()) {
            if (entry.getValue().equals(name)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 获取指定省份的城市名称列表
     * @param provinceCode 省份代码
     * @return 城市名称数组
     */
    public static String[] getCityNames(String provinceCode) {
        List<CityInfo> cities = CITIES.get(provinceCode);
        if (cities == null || cities.isEmpty()) {
            return new String[0];
        }

        List<String> cityNames = new ArrayList<>();
        for (CityInfo city : cities) {
            cityNames.add(city.getName());
        }
        return cityNames.toArray(new String[0]);
    }

    /**
     * 根据省份代码和城市名称获取城市代码
     * @param provinceCode 省份代码
     * @param cityName 城市名称
     * @return 城市代码，如果不存在返回null
     */
    public static String getCityCode(String provinceCode, String cityName) {
        List<CityInfo> cities = CITIES.get(provinceCode);
        if (cities == null) {
            return null;
        }

        for (CityInfo city : cities) {
            if (city.getName().equals(cityName)) {
                return city.getCode();
            }
        }
        return null;
    }

    /**
     * 计算身份证校验位
     * @param idCard17 前17位身份证号码
     * @return 校验位字符
     */
    public static char calculateCheckDigit(String idCard17) {
        if (idCard17 == null || idCard17.length() != 17) {
            throw new IllegalArgumentException("身份证号码前17位长度不正确");
        }

        int sum = 0;
        for (int i = 0; i < 17; i++) {
            char c = idCard17.charAt(i);
            if (c < '0' || c > '9') {
                throw new IllegalArgumentException("身份证号码包含非数字字符");
            }
            sum += (c - '0') * WEIGHTS[i];
        }

        return CHECK_CODES[sum % 11];
    }

    /**
     * 验证身份证号码校验位是否正确
     * @param idCard 18位身份证号码
     * @return true 如果校验位正确，否则返回false
     */
    public static boolean validateCheckDigit(String idCard) {
        if (idCard == null || idCard.length() != 18) {
            return false;
        }

        String idCard17 = idCard.substring(0, 17);
        char checkDigit = idCard.charAt(17);

        try {
            return calculateCheckDigit(idCard17) == checkDigit;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证身份证号码格式
     * @param idCard 身份证号码
     * @return 验证结果消息
     */
    public static String validateIdCard(String idCard) {
        if (idCard == null || idCard.isEmpty()) {
            return "身份证号码为空";
        }

        idCard = idCard.trim();

        // 检查长度
        if (idCard.length() != 18) {
            return "长度不是18位";
        }

        // 检查前17位是否为数字
        String idCard17 = idCard.substring(0, 17);
        for (char c : idCard17.toCharArray()) {
            if (c < '0' || c > '9') {
                return "前17位包含非数字字符";
            }
        }

        // 检查第18位
        char checkChar = idCard.charAt(17);
        if ((checkChar < '0' || checkChar > '9') && checkChar != 'X' && checkChar != 'x') {
            return "第18位不是数字或X";
        }

        // 检查校验位
        if (!validateCheckDigit(idCard)) {
            return "校验位错误";
        }

        // 检查出生日期
        String birthDateStr = idCard.substring(6, 14);
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate birthDate = LocalDate.parse(birthDateStr, formatter);

            // 检查日期是否合理
            LocalDate now = LocalDate.now();
            if (birthDate.isAfter(now)) {
                return "出生日期不能晚于当前日期";
            }

            // 检查是否早于1900年
            if (birthDate.getYear() < 1900) {
                return "出生日期不能早于1900年";
            }

        } catch (DateTimeParseException e) {
            return "出生日期格式错误";
        }

        return "校验通过";
    }

    /**
     * 根据年龄计算出生日期字符串
     * @param age 年龄
     * @return 出生日期字符串 (yyyyMMdd格式)
     */
    public static String calculateBirthDate(int age) {
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("年龄必须在0-150之间");
        }

        LocalDate now = LocalDate.now();
        LocalDate birthDate = now.minusYears(age);
        return birthDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    /**
     * 生成随机顺序码（3位数字）
     * @return 3位随机数字字符串
     */
    public static String generateSequenceCode() {
        int code = (int) (Math.random() * 1000);
        return String.format("%03d", code);
    }

    /**
     * 根据性别调整顺序码
     * @param sequenceCode 原始顺序码（3位）
     * @param gender 性别 (M=男, F=女, null=随机)
     * @return 调整后的顺序码（第17位：奇数=男，偶数=女）
     */
    public static String adjustSequenceCodeForGender(String sequenceCode, String gender) {
        if (sequenceCode == null || sequenceCode.length() != 3) {
            return sequenceCode;
        }

        // 如果未指定性别，返回原始顺序码
        if (gender == null || gender.isEmpty()) {
            return sequenceCode;
        }

        char lastChar = sequenceCode.charAt(2);
        int lastDigit = lastChar - '0';

        if ("M".equalsIgnoreCase(gender)) {
            // 男：第17位为奇数
            if (lastDigit % 2 == 0) {
                lastDigit = lastDigit == 0 ? 1 : lastDigit - 1;
            }
        } else if ("F".equalsIgnoreCase(gender)) {
            // 女：第17位为偶数
            if (lastDigit % 2 != 0) {
                lastDigit = lastDigit == 9 ? 8 : lastDigit + 1;
            }
        }

        return sequenceCode.substring(0, 2) + lastDigit;
    }
}
