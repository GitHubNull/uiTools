package org.oxff.core;

import org.oxff.operation.*;

import java.util.*;

/**
 * 操作工厂类，根据操作名称创建对应的操作实例
 */
public class OperationFactory {
    private static final Map<String, Operation> operations = new HashMap<>();
    private static final Map<OperationCategory, List<Operation>> operationsByCategory = new HashMap<>();

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
            new AutoInputOperation()
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
}