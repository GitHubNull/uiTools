package org.oxff.ui.builder;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.oxff.ui.components.UIComponentRegistry;

import javax.swing.*;
import java.awt.*;

/**
 * 输出面板构建器
 * 负责构建输出面板（支持文本和图片切换）
 */
public class OutputPanelBuilder {
    private final UIComponentRegistry registry;

    public OutputPanelBuilder(UIComponentRegistry registry) {
        this.registry = registry;
    }

    /**
     * 输出面板构建结果
     */
    public static class OutputPanelResult {
        public JPanel cardsPanel;
        public JPanel outputPanel;
        public CardLayout cardLayout;
        public JLabel imageDisplayLabel;
        public JButton copyOutputButton;
        public JButton saveImageButton;
        public JButton copyImageButton;
    }

    /**
     * 构建输出面板
     */
    public OutputPanelResult buildOutputPanel() {
        OutputPanelResult result = new OutputPanelResult();

        // 创建输出区域面板（使用卡片布局支持文本和图片切换）
        result.cardLayout = new CardLayout();
        result.cardsPanel = new JPanel(result.cardLayout);

        // 文本输出卡片
        JPanel textOutputCard = new JPanel(new BorderLayout());
        textOutputCard.setBorder(BorderFactory.createTitledBorder("输出"));

        // 输出区域按钮面板
        JPanel outputButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        result.copyOutputButton = new JButton("复制");

        outputButtonPanel.add(result.copyOutputButton);

        // 使用RSyntaxTextArea替换自定义的LineNumberTextArea
        RSyntaxTextArea outputTextArea = new RSyntaxTextArea();
        outputTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputTextArea.setLineWrap(false);
        outputTextArea.setEditable(false);
        outputTextArea.setCodeFoldingEnabled(true);

        registry.registerComponent(UIComponentRegistry.OUTPUT_TEXT_AREA, outputTextArea);

        // 使用RTextScrollPane提供行号显示
        RTextScrollPane outputScrollPane = new RTextScrollPane(outputTextArea);

        textOutputCard.add(outputButtonPanel, BorderLayout.NORTH);
        textOutputCard.add(outputScrollPane, BorderLayout.CENTER);

        // 图片输出卡片
        JPanel imageOutputCard = new JPanel(new BorderLayout());
        imageOutputCard.setBorder(BorderFactory.createTitledBorder("图片输出"));

        JPanel imagePanel = new JPanel(new BorderLayout());
        result.imageDisplayLabel = new JLabel();
        result.imageDisplayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        result.imageDisplayLabel.setVerticalAlignment(SwingConstants.CENTER);
        JScrollPane imageScrollPane = new JScrollPane(result.imageDisplayLabel);

        imagePanel.add(imageScrollPane, BorderLayout.CENTER);
        imageOutputCard.add(imagePanel, BorderLayout.CENTER);

        // 添加按钮面板到图片输出卡片
        JPanel imageButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        result.saveImageButton = new JButton("保存图片");
        result.copyImageButton = new JButton("复制图片");

        imageButtonPanel.add(result.saveImageButton);
        imageButtonPanel.add(result.copyImageButton);
        imageOutputCard.add(imageButtonPanel, BorderLayout.NORTH);

        // 将两个卡片添加到卡片面板
        result.cardsPanel.add(textOutputCard, "TEXT");
        result.cardsPanel.add(imageOutputCard, "IMAGE");

        // 默认显示文本输出
        result.cardLayout.show(result.cardsPanel, "TEXT");

        result.outputPanel = result.cardsPanel;
        return result;
    }
}
