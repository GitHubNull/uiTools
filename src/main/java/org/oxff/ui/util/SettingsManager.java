package org.oxff.ui.util;

import java.io.File;
import java.util.prefs.Preferences;

/**
 * 设置管理器，负责管理应用程序配置的持久化
 * 使用 Java Preferences API 存储用户配置
 */
public class SettingsManager {
    private static final String PREFS_NODE = "/org/oxff/uiTools";
    private static final String KEY_AUTO_SAVE_DIR = "auto_save_directory";
    private static final String DEFAULT_AUTO_SAVE_DIR_NAME = "uiTools_outputs";

    /**
     * 获取自动保存目录配置
     * @return 目录路径，如果未配置则返回 null
     */
    public String getAutoSaveDirectory() {
        try {
            Preferences prefs = getPreferences();
            String dir = prefs.get(KEY_AUTO_SAVE_DIR, null);
            // 如果配置的目录无效，返回 null
            if (dir != null && !dir.isEmpty()) {
                File file = new File(dir);
                if (file.exists() && file.isDirectory()) {
                    return dir;
                }
            }
            return null;
        } catch (Exception e) {
            // Preferences API 异常时返回 null
            return null;
        }
    }

    /**
     * 设置自动保存目录
     * @param path 目录路径
     */
    public void setAutoSaveDirectory(String path) {
        if (path == null || path.isEmpty()) {
            resetAutoSaveDirectory();
            return;
        }
        Preferences prefs = getPreferences();
        prefs.put(KEY_AUTO_SAVE_DIR, path);
        try {
            prefs.flush();
        } catch (Exception e) {
            // 忽略 flush 异常
        }
    }

    /**
     * 重置自动保存目录为默认值
     */
    public void resetAutoSaveDirectory() {
        Preferences prefs = getPreferences();
        prefs.remove(KEY_AUTO_SAVE_DIR);
        try {
            prefs.flush();
        } catch (Exception e) {
            // 忽略 flush 异常
        }
    }

    /**
     * 获取默认自动保存目录路径
     * @return 默认目录的完整路径
     */
    public String getDefaultAutoSaveDirectory() {
        String userHome = System.getProperty("user.home");
        return new File(userHome, DEFAULT_AUTO_SAVE_DIR_NAME).getAbsolutePath();
    }

    /**
     * 获取 Preferences 节点
     * @return Preferences 节点
     */
    private Preferences getPreferences() {
        return Preferences.userRoot().node(PREFS_NODE);
    }
}
