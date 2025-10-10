package org.example.service;

import org.example.model.ImageData;
import org.example.model.WatermarkConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 实时预览服务类
 * 负责处理水印的实时预览功能
 */
public class PreviewService {
    private WatermarkService watermarkService;
    
    public PreviewService() {
        this.watermarkService = new WatermarkService();
    }
    
    /**
     * 生成带有水印的预览图像
     * 注意：此方法始终基于原始图像添加水印，确保不会出现多个水印叠加的情况
     * @param imageData 原始图像数据（包含原始未修改的图像）
     * @param config 水印配置
     * @param maxWidth 预览图像最大宽度
     * @param maxHeight 预览图像最大高度
     * @return 缩放后的带水印预览图像
     */
    public ImageIcon generatePreview(ImageData imageData, WatermarkConfig config, int maxWidth, int maxHeight) {
        if (imageData == null || imageData.getFullImage() == null) {
            return null;
        }
        
        // 先生成带水印的完整图像
        // 注意：WatermarkService.addWatermark 方法始终基于原始图像添加水印，不会在已有水印的图像上再次添加
        BufferedImage watermarkedImage = watermarkService.addWatermark(imageData, config);
        if (watermarkedImage == null) {
            return null;
        }
        
        // 计算缩放比例以适应预览区域
        double scale = Math.min((double) maxWidth / watermarkedImage.getWidth(),
                (double) maxHeight / watermarkedImage.getHeight());
        
        // 确保至少缩放到一定大小
        scale = Math.max(scale, 0.1);
        
        int scaledWidth = (int) (watermarkedImage.getWidth() * scale);
        int scaledHeight = (int) (watermarkedImage.getHeight() * scale);
        
        // 创建缩放后的预览图像
        BufferedImage previewImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = previewImage.createGraphics();
        
        // 设置高质量渲染提示
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 绘制缩放后的图像
        g2d.drawImage(watermarkedImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();
        
        return new ImageIcon(previewImage);
    }
    
    /**
     * 更新预览图像
     * @param previewLabel 显示预览的标签
     * @param imageData 原始图像数据
     * @param config 水印配置
     */
    public void updatePreview(JLabel previewLabel, ImageData imageData, WatermarkConfig config) {
        if (previewLabel == null || imageData == null) {
            return;
        }
        
        // 获取预览标签的尺寸
        int width = previewLabel.getWidth();
        int height = previewLabel.getHeight();
        
        // 如果尺寸无效，则使用默认尺寸
        if (width <= 0 || height <= 0) {
            width = 400;
            height = 300;
        }
        
        // 清除之前的预览内容
        previewLabel.setIcon(null);
        previewLabel.setText("");
        
        // 生成预览图像
        ImageIcon previewIcon = generatePreview(imageData, config, width, height);
        if (previewIcon != null) {
            previewLabel.setIcon(previewIcon);
        } else {
            previewLabel.setIcon(null);
            previewLabel.setText("无法生成预览");
        }
    }
}