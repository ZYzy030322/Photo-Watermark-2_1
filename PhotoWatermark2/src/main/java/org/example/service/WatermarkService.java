package org.example.service;

import org.example.model.ImageData;
import org.example.model.WatermarkConfig;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 水印服务类
 */
public class WatermarkService {

    /**
     * 为图片添加水印
     * 注意：此方法始终基于原始图像添加水印，确保不会出现多个水印叠加的情况
     *
     * @param imageData 图片数据（包含原始未修改的图像）
     * @param config    水印配置
     * @return 添加水印后的图片
     */
    public BufferedImage addWatermark(ImageData imageData, WatermarkConfig config) {
        // 使用完整图像而不是缩略图
        Image originalImage = imageData.getFullImage();
        if (originalImage == null) {
            return null;
        }

        // 将Image转换为BufferedImage以便处理
        BufferedImage bufferedImage = toBufferedImage(originalImage);
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        // 创建图形上下文
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 添加水印
        if (config.getImageFile() != null && config.getImageFile().exists()) {
            // 添加图片水印
            addImageWatermark(g2d, config, width, height);
        } else if (config.getText() != null && !config.getText().isEmpty()) {
            // 添加文本水印
            addTextWatermark(g2d, config, width, height);
        }

        g2d.dispose();
        return bufferedImage;
    }

    /**
     * 添加文本水印
     */
    private void addTextWatermark(Graphics2D g2d, WatermarkConfig config, int imageWidth, int imageHeight) {
        String text = config.getText();
        String fontName = config.getFontName();
        int fontSize = config.getFontSize();
        boolean bold = config.isBold();
        boolean italic = config.isItalic();
        String colorStr = config.getColor();
        float opacity = (float) (config.getOpacity() / 100.0);
        String position = config.getPosition();
        double rotation = config.getRotation();

        // 设置字体，确保支持中文字符
        int style = Font.PLAIN;
        if (bold && italic) {
            style = Font.BOLD | Font.ITALIC;
        } else if (bold) {
            style = Font.BOLD;
        } else if (italic) {
            style = Font.ITALIC;
        }
        
        // 获取支持中文的字体
        Font font = getCompatibleFont(fontName, style, fontSize);
        g2d.setFont(font);

        // 设置颜色和透明度
        Color color = Color.decode(colorStr);
        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (255 * opacity)));

        // 计算文本尺寸
        FontRenderContext frc = g2d.getFontRenderContext();
        Rectangle2D textBounds = font.getStringBounds(text, frc);
        int textWidth = (int) textBounds.getWidth();
        int textHeight = (int) textBounds.getHeight();

        // 根据位置设置坐标
        int posX, posY;
        switch (position) {
            case "TOP_LEFT":
                posX = 20; // 添加边缘填充
                posY = 20 + textHeight;
                break;
            case "TOP_CENTER":
                posX = (imageWidth - textWidth) / 2;
                posY = 20 + textHeight;
                break;
            case "TOP_RIGHT":
                posX = imageWidth - textWidth - 20;
                posY = 20 + textHeight;
                break;
            case "CENTER_LEFT":
                posX = 20;
                posY = (imageHeight + textHeight) / 2;
                break;
            case "CENTER":
                posX = (imageWidth - textWidth) / 2;
                posY = (imageHeight + textHeight) / 2;
                break;
            case "CENTER_RIGHT":
                posX = imageWidth - textWidth - 20;
                posY = (imageHeight + textHeight) / 2;
                break;
            case "BOTTOM_LEFT":
                posX = 20;
                posY = imageHeight - 20;
                break;
            case "BOTTOM_CENTER":
                posX = (imageWidth - textWidth) / 2;
                posY = imageHeight - 20;
                break;
            case "BOTTOM_RIGHT":
                posX = imageWidth - textWidth - 20;
                posY = imageHeight - 20;
                break;
            default: // 自定义位置
                posX = (int) config.getX();
                posY = (int) config.getY();
                break;
        }

        // 确保文本不会超出图片边界
        if (posX < 0) {
            posX = 0;
        }
        if (posY < 0) {
            posY = 0;
        }
        if (posX + textWidth > imageWidth) {
            posX = imageWidth - textWidth;
        }
        if (posY + textHeight > imageHeight) {
            posY = imageHeight - textHeight;
        }

        // 应用旋转
        if (rotation != 0) {
            AffineTransform oldTransform = g2d.getTransform();
            g2d.rotate(Math.toRadians(rotation), posX + textWidth / 2.0, posY - textHeight / 2.0);
            g2d.drawString(text, posX, posY);
            g2d.setTransform(oldTransform);
        } else {
            g2d.drawString(text, posX, posY);
        }
    }

    /**
     * 获取支持中文的字体
     * @param fontName 字体名称
     * @param style 字体样式
     * @param size 字体大小
     * @return 支持中文的字体
     */
    private Font getCompatibleFont(String fontName, int style, int size) {
        // 常见的支持中文的字体列表
        String[] chineseFontNames = {
            "Microsoft YaHei", "SimHei", "KaiTi", "SimSun", "FangSong", 
            "YouYuan", "STHeiti", "STSong", "STKaiti", "STFangsong",
            "PingFang SC", "Hiragino Sans GB", "Source Han Sans CN",
            "Noto Sans CJK SC", "WenQuanYi Micro Hei", "WenQuanYi Zen Hei"
        };
        
        // 首先尝试使用用户指定的字体
        Font font = new Font(fontName, style, size);
        if (isFontSupportChinese(font)) {
            return font;
        }
        
        // 如果用户指定的字体不支持中文，尝试使用系统中的中文字体
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] availableFontFamilyNames = ge.getAvailableFontFamilyNames();
        Set<String> availableFonts = new HashSet<>(Arrays.asList(availableFontFamilyNames));
        
        // 查找系统中可用的中文字体
        for (String chineseFontName : chineseFontNames) {
            if (availableFonts.contains(chineseFontName)) {
                font = new Font(chineseFontName, style, size);
                if (isFontSupportChinese(font)) {
                    return font;
                }
            }
        }
        
        // 如果没有找到支持中文的字体，使用系统默认字体
        font = new Font(Font.DIALOG, style, size);
        if (isFontSupportChinese(font)) {
            return font;
        }
        
        // 最后的备选方案
        return new Font(Font.SANS_SERIF, style, size);
    }
    
    /**
     * 检查字体是否支持中文字符
     * @param font 要检查的字体
     * @return 是否支持中文
     */
    private boolean isFontSupportChinese(Font font) {
        // 测试一些常见的中文字符
        String testChars = "你好水印测试";
        for (int i = 0; i < testChars.length(); i++) {
            if (font.canDisplay(testChars.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 添加图片水印
     */
    private void addImageWatermark(Graphics2D g2d, WatermarkConfig config, int imageWidth, int imageHeight) {
        File imageFile = config.getImageFile();
        if (imageFile == null || !imageFile.exists()) {
            return;
        }

        try {
            BufferedImage watermarkImage = ImageIO.read(imageFile);
            float scale = (float) (config.getImageScale() / 100.0);
            float opacity = (float) (config.getImageOpacity() / 100.0);
            String position = config.getPosition();
            double rotation = config.getRotation();

            int watermarkWidth = (int) (watermarkImage.getWidth() * scale);
            int watermarkHeight = (int) (watermarkImage.getHeight() * scale);

            // 根据位置设置坐标
            int posX, posY;
            switch (position) {
                case "TOP_LEFT":
                    posX = 0;
                    posY = 0;
                    break;
                case "TOP_CENTER":
                    posX = (imageWidth - watermarkWidth) / 2;
                    posY = 0;
                    break;
                case "TOP_RIGHT":
                    posX = imageWidth - watermarkWidth;
                    posY = 0;
                    break;
                case "CENTER_LEFT":
                    posX = 0;
                    posY = (imageHeight - watermarkHeight) / 2;
                    break;
                case "CENTER":
                    posX = (imageWidth - watermarkWidth) / 2;
                    posY = (imageHeight - watermarkHeight) / 2;
                    break;
                case "CENTER_RIGHT":
                    posX = imageWidth - watermarkWidth;
                    posY = (imageHeight - watermarkHeight) / 2;
                    break;
                case "BOTTOM_LEFT":
                    posX = 0;
                    posY = imageHeight - watermarkHeight;
                    break;
                case "BOTTOM_CENTER":
                    posX = (imageWidth - watermarkWidth) / 2;
                    posY = imageHeight - watermarkHeight;
                    break;
                case "BOTTOM_RIGHT":
                    posX = imageWidth - watermarkWidth;
                    posY = imageHeight - watermarkHeight;
                    break;
                default: // 自定义位置
                    posX = (int) config.getX();
                    posY = (int) config.getY();
                    break;
            }

            // 设置透明度
            Composite oldComposite = g2d.getComposite();
            if (opacity < 1.0f) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            }

            // 应用旋转
            if (rotation != 0) {
                AffineTransform oldTransform = g2d.getTransform();
                g2d.rotate(Math.toRadians(rotation), posX + watermarkWidth / 2.0, posY + watermarkHeight / 2.0);
                g2d.drawImage(watermarkImage, posX, posY, watermarkWidth, watermarkHeight, null);
                g2d.setTransform(oldTransform);
            } else {
                g2d.drawImage(watermarkImage, posX, posY, watermarkWidth, watermarkHeight, null);
            }

            // 恢复透明度设置
            g2d.setComposite(oldComposite);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存图片到文件
     */
    public void saveImage(BufferedImage image, File outputFile, String format, int jpegQuality) throws IOException {
        if ("JPEG".equalsIgnoreCase(format) || "JPG".equalsIgnoreCase(format)) {
            // 处理JPEG透明度问题
            BufferedImage jpegBufferedImage = new BufferedImage(
                    image.getWidth(),
                    image.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g = jpegBufferedImage.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, image.getWidth(), image.getHeight());
            g.drawImage(image, 0, 0, null);
            g.dispose();
            image = jpegBufferedImage;

            // 保存JPEG图片
            // 注意：ImageIO不直接支持JPEG质量设置，需要使用其他方法或第三方库
            ImageIO.write(image, "JPEG", outputFile);
        } else {
            // 保存PNG图片
            ImageIO.write(image, "PNG", outputFile);
        }
    }

    /**
     * 将Image转换为BufferedImage
     */
    private BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // 创建 BufferedImage，使用完整图像尺寸
        BufferedImage bufferedImage = new BufferedImage(
                img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return bufferedImage;
    }
}