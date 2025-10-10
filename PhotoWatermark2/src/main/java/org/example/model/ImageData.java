package org.example.model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 图片数据类
 */
public class ImageData {
    private Image image;
    private Image fullImage; // 添加完整图像用于预览
    private File file;
    private String name;

    public ImageData(File file) {
        this.file = file;
        this.name = file.getName();
        // Load image thumbnail
        try {
            BufferedImage originalImage = ImageIO.read(file);
            // 保存完整图像用于预览
            this.fullImage = originalImage;
            
            // 创建缩略图
            int thumbWidth = 100;
            int thumbHeight = 100;
            BufferedImage thumbnail = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = thumbnail.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(originalImage, 0, 0, thumbWidth, thumbHeight, null);
            g2d.dispose();
            this.image = thumbnail;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ImageData(Image image, String name) {
        this.image = image;
        this.name = name;
    }

    // Getters and setters
    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
    
    // 添加获取完整图像的方法
    public Image getFullImage() {
        return fullImage;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}