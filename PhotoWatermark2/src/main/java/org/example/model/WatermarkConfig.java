package org.example.model;

import java.io.File;

/**
 * 水印配置类
 */
public class WatermarkConfig {
    // 文本水印属性
    private String text = "";
    private String fontName = "Arial";
    private int fontSize = 16;
    private boolean bold = false;
    private boolean italic = false;
    private String color = "#000000";
    private double opacity = 100;

    // 图片水印属性
    private File imageFile;
    private double imageScale = 100;
    private double imageOpacity = 100;

    // 水印位置和旋转
    private String position = "CENTER";
    private double rotation = 0;
    private double x = 0;
    private double y = 0;
    
    // 边缘填充距离
    private double edgePadding = 20;

    // 导出设置
    private String exportFormat = "PNG";
    private double jpegQuality = 90;
    private String namingConvention = "ORIGINAL";
    private String prefix = "";
    private String suffix = "_watermarked";

    // 尺寸调整
    private boolean resizeEnabled = false;
    private double resizeWidth = 0;
    private double resizeHeight = 0;
    private double resizePercentage = 100;

    // getters and setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getOpacity() {
        return opacity;
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public double getImageScale() {
        return imageScale;
    }

    public void setImageScale(double imageScale) {
        this.imageScale = imageScale;
    }

    public double getImageOpacity() {
        return imageOpacity;
    }

    public void setImageOpacity(double imageOpacity) {
        this.imageOpacity = imageOpacity;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getExportFormat() {
        return exportFormat;
    }

    public void setExportFormat(String exportFormat) {
        this.exportFormat = exportFormat;
    }

    public double getJpegQuality() {
        return jpegQuality;
    }

    public void setJpegQuality(double jpegQuality) {
        this.jpegQuality = jpegQuality;
    }

    public String getNamingConvention() {
        return namingConvention;
    }

    public void setNamingConvention(String namingConvention) {
        this.namingConvention = namingConvention;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public boolean isResizeEnabled() {
        return resizeEnabled;
    }

    public void setResizeEnabled(boolean resizeEnabled) {
        this.resizeEnabled = resizeEnabled;
    }

    public double getResizeWidth() {
        return resizeWidth;
    }

    public void setResizeWidth(double resizeWidth) {
        this.resizeWidth = resizeWidth;
    }

    public double getResizeHeight() {
        return resizeHeight;
    }

    public void setResizeHeight(double resizeHeight) {
        this.resizeHeight = resizeHeight;
    }

    public double getResizePercentage() {
        return resizePercentage;
    }

    public void setResizePercentage(double resizePercentage) {
        this.resizePercentage = resizePercentage;
    }
}