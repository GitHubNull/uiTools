package org.oxff;

import com.formdev.flatlaf.FlatLightLaf;
import org.oxff.ui.MainWindow;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {
        // 设置外观
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            try {
                MainWindow frame = new MainWindow();
                frame.setVisible(true);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "启动应用程序时发生错误", e);
            }
        });
    }
}