package org.oxff.ui.builder;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.oxff.ui.components.UIComponentRegistry;

import javax.swing.*;
import java.awt.*;

/**
 * 表达式面板构建器
 * 负责构建 XPath/JSONPath 表达式输入面板
 */
public class ExpressionPanelBuilder {
    private final UIComponentRegistry registry;

    public ExpressionPanelBuilder(UIComponentRegistry registry) {
        this.registry = registry;
    }

    /**
     * 表达式面板构建结果
     */
    public static class ExpressionPanelResult {
        public JPanel panel;
        public JButton clearExpressionButton;
    }

    /**
     * 构建表达式面板
     */
    public ExpressionPanelResult buildExpressionPanel() {
        ExpressionPanelResult result = new ExpressionPanelResult();

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("XPath/JSONPath表达式 (每行一个)"));

        // 表达式区域按钮面板
        JPanel expressionButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        result.clearExpressionButton = new JButton("清空表达式");
        JLabel expressionHint = new JLabel("支持XPath (XML) 和 JSONPath (JSON) 表达式");
        expressionHint.setFont(expressionHint.getFont().deriveFont(Font.ITALIC, 10f));
        expressionHint.setForeground(Color.GRAY);

        expressionButtonPanel.add(result.clearExpressionButton);
        expressionButtonPanel.add(expressionHint);

        // 表达式输入文本区域
        RSyntaxTextArea expressionTextArea = new RSyntaxTextArea();
        expressionTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        expressionTextArea.setLineWrap(true);
        expressionTextArea.setWrapStyleWord(true);
        expressionTextArea.setCodeFoldingEnabled(false);
        expressionTextArea.setToolTipText("输入XPath或JSONPath表达式，每行一个表达式\nXML示例: //book/title\nJSON示例: $.store.book[*].title");

        registry.registerComponent(UIComponentRegistry.EXPRESSION_TEXT_AREA, expressionTextArea);

        RTextScrollPane expressionScrollPane = new RTextScrollPane(expressionTextArea);
        expressionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        expressionScrollPane.setPreferredSize(new Dimension(0, 80));

        panel.add(expressionButtonPanel, BorderLayout.NORTH);
        panel.add(expressionScrollPane, BorderLayout.CENTER);

        result.panel = panel;
        return result;
    }
}
