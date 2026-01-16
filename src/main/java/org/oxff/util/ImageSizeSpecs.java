package org.oxff.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 图片寸数规格定义工具类
 * 定义常见的证件照规格尺寸
 */
public class ImageSizeSpecs {

    /**
     * 寸数规格
     */
    public static class SizeSpec {
        private final String name;
        private final int widthMM;
        private final int heightMM;
        private final int widthPixels;  // @ 300 DPI
        private final int heightPixels;

        public SizeSpec(String name, int widthMM, int heightMM, int widthPixels, int heightPixels) {
            this.name = name;
            this.widthMM = widthMM;
            this.heightMM = heightMM;
            this.widthPixels = widthPixels;
            this.heightPixels = heightPixels;
        }

        public String getName() {
            return name;
        }

        public int getWidthMM() {
            return widthMM;
        }

        public int getHeightMM() {
            return heightMM;
        }

        public int getWidthPixels() {
            return widthPixels;
        }

        public int getHeightPixels() {
            return heightPixels;
        }

        @Override
        public String toString() {
            return String.format("%s (%d×%dmm, %d×%d像素)",
                    name, widthMM, heightMM, widthPixels, heightPixels);
        }
    }

    private static final Map<String, SizeSpec> SPECS = new LinkedHashMap<>();

    static {
        // 常用证件照规格 (300 DPI: 1mm ≈ 11.8像素)
        SPECS.put("1寸", new SizeSpec("1寸", 25, 35, 295, 413));
        SPECS.put("2寸", new SizeSpec("2寸", 35, 49, 413, 579));
        SPECS.put("小2寸", new SizeSpec("小2寸", 35, 45, 413, 531));
        SPECS.put("大1寸", new SizeSpec("大1寸", 33, 48, 390, 567));

        // 护照/签证规格
        SPECS.put("护照", new SizeSpec("护照", 33, 48, 390, 567));
        SPECS.put("签证", new SizeSpec("签证", 50, 50, 591, 591));
        SPECS.put("美国签证", new SizeSpec("美国签证", 51, 51, 602, 602));
        SPECS.put("日本签证", new SizeSpec("日本签证", 45, 45, 531, 531));

        // 其他常见规格
        SPECS.put("身份证", new SizeSpec("身份证", 26, 32, 308, 378));
        SPECS.put("社保卡", new SizeSpec("社保卡", 26, 32, 308, 378));
        SPECS.put("驾驶证", new SizeSpec("驾驶证", 22, 32, 260, 378));
        SPECS.put("通行证", new SizeSpec("通行证", 33, 48, 390, 567));
    }

    /**
     * 根据名称获取规格
     * @param name 规格名称
     * @return 规格对象，如果不存在返回null
     */
    public static SizeSpec getSpec(String name) {
        return SPECS.get(name);
    }

    /**
     * 获取所有规格名称
     * @return 规格名称数组
     */
    public static String[] getSpecNames() {
        return SPECS.keySet().toArray(new String[0]);
    }

    /**
     * 获取所有规格
     * @return 规格Map
     */
    public static Map<String, SizeSpec> getAllSpecs() {
        return new LinkedHashMap<>(SPECS);
    }
}
