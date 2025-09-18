package org.oxff;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class StringUtils {
    
    /**
     * 格式化JSON字符串
     */
    public static String formatJson(String json) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Object obj = gson.fromJson(json, Object.class);
            return gson.toJson(obj);
        } catch (JsonSyntaxException e) {
            return "无效的JSON格式: " + e.getMessage();
        }
    }
    
    /**
     * 格式化XML字符串
     */
    public static String formatXml(String xml) {
        try {
            Document document = DocumentHelper.parseText(xml);
            StringWriter sw = new StringWriter();
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            XMLWriter xw = new XMLWriter(sw, format);
            xw.write(document);
            xw.close();
            return sw.toString();
        } catch (DocumentException | IOException e) {
            return "无效的XML格式: " + e.getMessage();
        }
    }
    
    /**
     * URL编码
     */
    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return "编码错误: " + e.getMessage();
        }
    }
    
    /**
     * URL解码
     */
    public static String urlDecode(String str) {
        try {
            return URLDecoder.decode(str, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return "解码错误: " + e.getMessage();
        }
    }
    
    /**
     * Base64编码
     */
    public static String base64Encode(String str) {
        return Base64.encodeBase64String(str.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Base64解码
     */
    public static String base64Decode(String str) {
        try {
            byte[] decodedBytes = Base64.decodeBase64(str);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "Base64解码错误: " + e.getMessage();
        }
    }
    
    /**
     * Base32编码
     */
    public static String base32Encode(String str) {
        Base32 base32 = new Base32();
        return base32.encodeAsString(str.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Base32解码
     */
    public static String base32Decode(String str) {
        try {
            Base32 base32 = new Base32();
            byte[] decodedBytes = base32.decode(str);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "Base32解码错误: " + e.getMessage();
        }
    }
    
    /**
     * Unicode编码
     */
    public static String unicodeEncode(String str) {
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (c < 128) {
                sb.append(c);
            } else {
                sb.append("\\u").append(String.format("%04x", (int) c));
            }
        }
        return sb.toString();
    }
    
    /**
     * Unicode解码
     */
    public static String unicodeDecode(String str) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < str.length()) {
            if (i + 6 <= str.length() && str.substring(i, i + 2).equals("\\u")) {
                try {
                    int codePoint = Integer.parseInt(str.substring(i + 2, i + 6), 16);
                    sb.append((char) codePoint);
                    i += 6;
                } catch (NumberFormatException e) {
                    sb.append(str.charAt(i));
                    i++;
                }
            } else {
                sb.append(str.charAt(i));
                i++;
            }
        }
        return sb.toString();
    }
    
    /**
     * MD5哈希
     */
    public static String md5Hash(String str) {
        return DigestUtils.md5Hex(str);
    }
    
    /**
     * SHA1哈希
     */
    public static String sha1Hash(String str) {
        return DigestUtils.sha1Hex(str);
    }
    
    /**
     * SHA256哈希
     */
    public static String sha256Hash(String str) {
        return DigestUtils.sha256Hex(str);
    }
}