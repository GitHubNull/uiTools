package org.oxff.operation;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.oxff.core.OperationCategory;

import java.io.IOException;
import java.io.StringWriter;

/**
 * XML格式化操作实现
 */
public class XmlFormatOperation implements Operation {
    @Override
    public String execute(String input) {
        try {
            Document document = DocumentHelper.parseText(input);
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
    
    @Override
    public OperationCategory getCategory() {
        return OperationCategory.FORMATTING;
    }
    
    @Override
    public String getDisplayName() {
        return "XML格式化";
    }
}