package org.example;

import javax.swing.*;

/**
 * Swing主应用程序类
 */
public class SwingMainApp {
    public static void main(String[] args) {
        // 设置系统外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 在事件调度线程中启动应用
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}