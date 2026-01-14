package org.oxff.operation.encoding.jwt;

import com.google.gson.Gson;
import org.oxff.operation.Operation;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.oxff.core.OperationCategory;

import javax.crypto.SecretKey;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * JWT生成操作实现
 * 支持多种签名算法：HS256/HS384/HS512, RS256/RS384/RS512, ES256/ES384/ES512
 */
public class JwtEncodeOperation implements Operation {

    private static final Gson gson = new Gson();

    @Override
    public String execute(String input) {
        try {
            if (input == null || input.trim().isEmpty()) {
                return getUsage();
            }

            JsonObject config = JsonParser.parseString(input).getAsJsonObject();

            if (!config.has("payload")) {
                return "错误：缺少必需的payload字段";
            }

            if (!config.has("key")) {
                return "错误：缺少必需的key字段";
            }

            JsonObject payload = config.getAsJsonObject("payload");
            String keyString = config.get("key").getAsString();
            String algorithm = config.has("algorithm") ? config.get("algorithm").getAsString() : "HS256";

            io.jsonwebtoken.JwtBuilder builder = Jwts.builder();

            Set<Map.Entry<String, JsonElement>> entries = payload.entrySet();
            for (Map.Entry<String, JsonElement> entry : entries) {
                String claimName = entry.getKey();
                JsonElement claimValue = entry.getValue();

                if (claimValue.isJsonNull()) {
                    builder.claim(claimName, null);
                } else if (claimValue.isJsonPrimitive()) {
                    if (claimValue.getAsJsonPrimitive().isNumber()) {
                        try {
                            long longValue = claimValue.getAsLong();
                            if (claimValue.getAsString().equals(String.valueOf(longValue))) {
                                if (isStandardTimeClaim(claimName)) {
                                    builder.claim(claimName, new Date(longValue * 1000));
                                } else {
                                    builder.claim(claimName, longValue);
                                }
                            } else {
                                builder.claim(claimName, claimValue.getAsDouble());
                            }
                        } catch (NumberFormatException e) {
                            builder.claim(claimName, claimValue.getAsString());
                        }
                    } else if (claimValue.getAsJsonPrimitive().isBoolean()) {
                        builder.claim(claimName, claimValue.getAsBoolean());
                    } else {
                        builder.claim(claimName, claimValue.getAsString());
                    }
                } else {
                    builder.claim(claimName, gson.fromJson(claimValue, Object.class));
                }
            }

            // 设置header中的typ字段（如果提供）
            if (config.has("typ")) {
                builder.setHeaderParam("typ", config.get("typ").getAsString());
            }

            switch (algorithm.toUpperCase()) {
                case "HS256":
                    SecretKey hmacKey256 = Keys.hmacShaKeyFor(Decoders.BASE64.decode(keyString));
                    builder.signWith(hmacKey256);
                    break;
                case "HS384":
                    SecretKey hmacKey384 = Keys.hmacShaKeyFor(Decoders.BASE64.decode(keyString));
                    builder.signWith(hmacKey384, io.jsonwebtoken.SignatureAlgorithm.HS384);
                    break;
                case "HS512":
                    SecretKey hmacKey512 = Keys.hmacShaKeyFor(Decoders.BASE64.decode(keyString));
                    builder.signWith(hmacKey512, io.jsonwebtoken.SignatureAlgorithm.HS512);
                    break;
                case "RS256":
                    PrivateKey rsaPrivateKey256 = loadPrivateKey(keyString, "RSA");
                    builder.signWith(rsaPrivateKey256, io.jsonwebtoken.SignatureAlgorithm.RS256);
                    break;
                case "RS384":
                    PrivateKey rsaPrivateKey384 = loadPrivateKey(keyString, "RSA");
                    builder.signWith(rsaPrivateKey384, io.jsonwebtoken.SignatureAlgorithm.RS384);
                    break;
                case "RS512":
                    PrivateKey rsaPrivateKey512 = loadPrivateKey(keyString, "RSA");
                    builder.signWith(rsaPrivateKey512, io.jsonwebtoken.SignatureAlgorithm.RS512);
                    break;
                case "ES256":
                    PrivateKey ecPrivateKey256 = loadPrivateKey(keyString, "EC");
                    builder.signWith(ecPrivateKey256, io.jsonwebtoken.SignatureAlgorithm.ES256);
                    break;
                case "ES384":
                    PrivateKey ecPrivateKey384 = loadPrivateKey(keyString, "EC");
                    builder.signWith(ecPrivateKey384, io.jsonwebtoken.SignatureAlgorithm.ES384);
                    break;
                case "ES512":
                    PrivateKey ecPrivateKey512 = loadPrivateKey(keyString, "EC");
                    builder.signWith(ecPrivateKey512, io.jsonwebtoken.SignatureAlgorithm.ES512);
                    break;
                default:
                    return "错误：不支持的算法 " + algorithm + "\n支持的算法：HS256, HS384, HS512, RS256, RS384, RS512, ES256, ES384, ES512";
            }

            String jwt = builder.compact();

            return jwt;

        } catch (Exception e) {
            return "JWT生成失败: " + e.getMessage() + "\n\n" +
                   "请检查错误信息后重新尝试";
        }
    }

    private boolean isStandardTimeClaim(String claimName) {
        return "exp".equals(claimName) || "nbf".equals(claimName) || "iat".equals(claimName);
    }

    private PrivateKey loadPrivateKey(String base64Key, String algorithm) throws Exception {
        byte[] keyBytes = Decoders.BASE64URL.decode(base64Key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePrivate(keySpec);
    }

    private String getUsage() {
        return "错误：请提供JSON格式的配置，包含payload和key字段\n\n示例：\n{\n  \"payload\": {\"sub\": \"1234567890\", \"name\": \"John Doe\"},\n  \"key\": \"your-base64-key\",\n  \"algorithm\": \"HS256\",\n  \"typ\": \"JWT\"\n}";
    }

    private String generateRandomHmacKey() {
        SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        return Encoders.BASE64.encode(key.getEncoded());
    }

    @Override
    public OperationCategory getCategory() {
        return OperationCategory.ENCODING_DECODING;
    }

    @Override
    public String getDisplayName() {
        return "JWT编码";
    }
}
