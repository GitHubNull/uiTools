package org.oxff.core;

import org.oxff.operation.Operation;
import org.oxff.operation.formatting.JsonFormatOperation;
import org.oxff.operation.formatting.XmlFormatOperation;
import org.oxff.operation.encoding.url.UrlEncodeOperation;
import org.oxff.operation.encoding.url.UrlDecodeOperation;
import org.oxff.operation.encoding.base64.Base64EncodeOperation;
import org.oxff.operation.encoding.base64.Base64DecodeOperation;
import org.oxff.operation.encoding.base32.Base32EncodeOperation;
import org.oxff.operation.encoding.base32.Base32DecodeOperation;
import org.oxff.operation.encoding.unicode.UnicodeEncodeOperation;
import org.oxff.operation.encoding.unicode.UnicodeDecodeOperation;
import org.oxff.operation.encoding.hex.HexEncodeOperation;
import org.oxff.operation.encoding.hex.HexDecodeOperation;
import org.oxff.operation.encoding.jwt.JwtEncodeOperation;
import org.oxff.operation.encoding.jwt.JwtDecodeOperation;
import org.oxff.operation.encoding.image.ImageToBaseOperation;
import org.oxff.operation.hashing.Md5HashOperation;
import org.oxff.operation.hashing.Sha1HashOperation;
import org.oxff.operation.hashing.Sha256HashOperation;
import org.oxff.operation.automation.AutoInputOperation;
import org.oxff.operation.qrcode.QRCodeGenerateOperation;
import org.oxff.operation.qrcode.QRCodeDecodeOperation;
import org.oxff.operation.timestamp.GetCurrentTimeOperation;
import org.oxff.operation.timestamp.TimestampToDatetimeOperation;
import org.oxff.operation.timestamp.DatetimeToTimestampOperation;
import org.oxff.operation.generator.RandomPasswordOperation;
import org.oxff.operation.generator.idcard.IdCardGenerateOperation;
import org.oxff.operation.generator.idcard.IdCardValidateOperation;
import org.oxff.operation.imagetools.CreateBlankImageOperation;
import org.oxff.operation.imagetools.ImageResizeOperation;
import org.oxff.operation.imagetools.ImageCompressOperation;

import java.util.*;

/**
 * 操作工厂类，根据操作名称创建对应的操作实例
 */
public class OperationFactory {
    private static final Map<String, Operation> operations = new HashMap<>();
    private static final Map<OperationCategory, List<Operation>> operationsByCategory = new HashMap<>();
    private static final Map<OperationCategory, Map<Subcategory, List<Operation>>> operationsByCategoryAndSubcategory = new HashMap<>();

    static {
        // 初始化所有操作
        Operation[] allOperations = {
            new JsonFormatOperation(),
            new XmlFormatOperation(),
            new UrlEncodeOperation(),
            new UrlDecodeOperation(),
            new Base64EncodeOperation(),
            new Base64DecodeOperation(),
            new Base32EncodeOperation(),
            new Base32DecodeOperation(),
            new UnicodeEncodeOperation(),
            new UnicodeDecodeOperation(),
            new HexEncodeOperation(),
            new HexDecodeOperation(),
            new Md5HashOperation(),
            new Sha1HashOperation(),
            new Sha256HashOperation(),
            new AutoInputOperation(),
            new QRCodeGenerateOperation(),
            new QRCodeDecodeOperation(),
            // 时间戳操作
            new GetCurrentTimeOperation(),
            new TimestampToDatetimeOperation(),
            new DatetimeToTimestampOperation(),
            // JWT操作
            new JwtEncodeOperation(),
            new JwtDecodeOperation(),
            // 图片和生成工具操作
            new ImageToBaseOperation(),
            new RandomPasswordOperation(),
            // 身份证操作
            new IdCardGenerateOperation(),
            new IdCardValidateOperation(),
            // 图像工具操作
            new CreateBlankImageOperation(),
            new ImageResizeOperation(),
            new ImageCompressOperation()
        };
        
        // 按名称映射操作
        for (Operation op : allOperations) {
            operations.put(op.getDisplayName(), op);
        }
        
        // 按分类组织操作
        for (OperationCategory category : OperationCategory.values()) {
            operationsByCategory.put(category, new ArrayList<>());
        }

        for (Operation op : allOperations) {
            operationsByCategory.get(op.getCategory()).add(op);
        }

        // 按分类和子分类组织操作
        for (OperationCategory category : OperationCategory.values()) {
            operationsByCategoryAndSubcategory.put(category, new HashMap<>());
        }

        for (Operation op : allOperations) {
            OperationCategory category = op.getCategory();
            Subcategory subcategory = op.getSubcategory();

            if (subcategory != null) {
                operationsByCategoryAndSubcategory.get(category)
                    .computeIfAbsent(subcategory, k -> new ArrayList<>())
                    .add(op);
            } else {
                // 没有子分类的操作，使用默认子分类
                operationsByCategoryAndSubcategory.get(category)
                    .computeIfAbsent(SubcategoryRegistry.getDefaultSubcategory(), k -> new ArrayList<>())
                    .add(op);
            }
        }
    }
    
    public static Operation getOperation(String operationName) {
        return operations.get(operationName);
    }
    
    public static String[] getAllOperationNames() {
        return operations.keySet().toArray(new String[0]);
    }

    public static List<Operation> getOperationsByCategory(OperationCategory category) {
        return Collections.unmodifiableList(operationsByCategory.get(category));
    }

    public static Map<Subcategory, List<Operation>> getOperationsByCategoryWithSubcategory(OperationCategory category) {
        Map<Subcategory, List<Operation>> result = new HashMap<>();
        for (Map.Entry<Subcategory, List<Operation>> entry : operationsByCategoryAndSubcategory.get(category).entrySet()) {
            result.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
        }
        return Collections.unmodifiableMap(result);
    }

    /**
     * 获取操作的提示信息（用于日志显示）
     */
    public static String getOperationHints(String operationName) {
        switch (operationName) {
            case "JWT解码":
                return "JWT解码提示: 可在token后添加空格和密钥进行签名验证. HMAC算法密钥应为Base64编码, RSA/EC算法公钥应为X.509格式Base64编码";

            case "JWT编码":
                return "JWT编码提示: 密钥应使用Base64编码. RSA/EC算法密钥应为PKCS#8格式私钥";

            default:
                return null;
        }
    }
}