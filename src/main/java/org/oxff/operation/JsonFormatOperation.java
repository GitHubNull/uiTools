package org.oxff.operation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.oxff.core.OperationCategory;

/**
 * JSON格式化操作实现
 */
public class JsonFormatOperation implements Operation {
    @Override
    public String execute(String input) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Object obj = gson.fromJson(input, Object.class);
            return gson.toJson(obj);
        } catch (JsonSyntaxException e) {
            return "无效的JSON格式: " + e.getMessage();
        }
    }
    
    @Override
    public OperationCategory getCategory() {
        return OperationCategory.FORMATTING;
    }
    
    @Override
    public String getDisplayName() {
        return "JSON格式化";
    }
}