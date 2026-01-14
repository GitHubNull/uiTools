package org.oxff.operation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.oxff.core.OperationCategory;

import javax.crypto.SecretKey;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Map;

/**
 * JWT解析操作实现
 */
public class JwtDecodeOperation implements Operation {

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    @Override
    public String execute(String input) {
        try {
            if (input == null || input.trim().isEmpty()) {
                return "错误：请输入JWT token";
            }

            String[] parts = input.split("\\s+");
            String jwt = parts[0];
            String key = (parts.length > 1) ? parts[1] : null;

            String[] jwtParts = jwt.split("\\.");
            if (jwtParts.length != 3) {
                return "错误：无效的JWT格式";
            }

            // 解码header和payload
            String headerJson = new String(Decoders.BASE64URL.decode(jwtParts[0]));
            String payloadJson = new String(Decoders.BASE64URL.decode(jwtParts[1]));
            String signature = jwtParts[2];

            // 简化输出：返回header、payload和签名
            String formattedHeader = formatJson(headerJson);
            String formattedPayload = formatJson(payloadJson);

            // 返回完整的JWT三部分
            return formattedHeader + "\n---\n" + formattedPayload + "\n---\n" + signature;

        } catch (Exception e) {
            return "JWT解析失败: " + e.getMessage();
        }
    }

    private String formatJson(String json) {
        try {
            JsonElement element = JsonParser.parseString(json);
            return gson.toJson(element);
        } catch (Exception e) {
            return json;
        }
    }

    @Override
    public OperationCategory getCategory() {
        return OperationCategory.ENCODING_DECODING;
    }

    @Override
    public String getDisplayName() {
        return "JWT解码";
    }
}
