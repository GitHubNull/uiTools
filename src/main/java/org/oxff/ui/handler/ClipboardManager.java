package org.oxff.ui.handler;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import javax.imageio.ImageIO;

/**
 * 剪贴板管理器，统一管理剪贴板操作
 * 从 StringFormatterUI 中提取出来，提供统一的剪贴板操作接口
 */
public class ClipboardManager {
    private final Clipboard clipboard;

    public ClipboardManager() {
        this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    /**
     * 复制文本到剪贴板
     * @param text 要复制的文本
     */
    public void copyText(String text) {
        clipboard.setContents(new StringSelection(text), null);
    }

    /**
     * 从剪贴板粘贴文本
     * @return 剪贴板中的文本，如果没有文本则返回null
     */
    public String pasteText() {
        try {
            Transferable contents = clipboard.getContents(null);
            if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) contents.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (Exception e) {
            // 返回null表示失败
        }
        return null;
    }

    /**
     * 复制图片到剪贴板
     * @param image 要复制的图片
     */
    public void copyImage(Image image) {
        Transferable transferable = new Transferable() {
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{DataFlavor.imageFlavor};
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return DataFlavor.imageFlavor.equals(flavor);
            }

            @Override
            public Object getTransferData(DataFlavor flavor) {
                if (DataFlavor.imageFlavor.equals(flavor)) {
                    return image;
                }
                return null;
            }
        };
        clipboard.setContents(transferable, null);
    }

    /**
     * 从剪贴板粘贴图片
     * @return 剪贴板中的图片，如果没有图片则返回null
     */
    public Image pasteImage() {
        try {
            Transferable contents = clipboard.getContents(null);
            if (contents != null && contents.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                return (Image) contents.getTransferData(DataFlavor.imageFlavor);
            }
        } catch (Exception e) {
            // 返回null表示失败
        }
        return null;
    }

    /**
     * 检查剪贴板中是否有图片内容
     * @return true如果有图片内容
     */
    public boolean hasImageContent() {
        try {
            Transferable contents = clipboard.getContents(null);
            return contents != null && contents.isDataFlavorSupported(DataFlavor.imageFlavor);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查剪贴板中是否有文本内容
     * @return true如果有文本内容
     */
    public boolean hasTextContent() {
        try {
            Transferable contents = clipboard.getContents(null);
            return contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 将图片转换为Base64编码
     * @param image 要转换的图片
     * @return Base64编码的data URL，失败返回null
     */
    public String convertImageToBase64(Image image) {
        try {
            // 创建BufferedImage
            BufferedImage bufferedImage = new BufferedImage(
                image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();

            // 转换为PNG格式的字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "PNG", outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            // 转换为Base64
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            return "data:image/png;base64," + base64;
        } catch (Exception e) {
            return null;
        }
    }
}
