# PhotoWatermark 照片水印工具

一个基于Java Swing的照片水印添加工具，支持文本水印和图片水印，具有直观的用户界面和丰富的功能。

![主界面](screenshots/main_interface.png)

## 功能特性

### 文件处理
- 支持单张图片拖拽或通过文件选择器导入
- 支持批量导入，可一次性选择多张图片或直接导入整个文件夹
- 在界面上显示已导入图片的列表（缩略图和文件名）

### 支持格式
- 输入格式：JPEG, PNG, BMP, TIFF
- 输出格式：JPEG, PNG

### 导出设置
- 用户可指定一个输出文件夹，防止覆盖原图
- 提供多种文件命名规则选项（保留原名、添加前缀或后缀）
- JPEG格式支持质量调节（0-100）
- 支持导出时调整图片尺寸（按宽度高度或百分比）

### 水印类型
- 文本水印：可自定义内容、字体、颜色、透明度等
- 图片水印：支持PNG透明通道，可调节大小和透明度

### 水印布局与样式
- 实时预览水印效果
- 预设9个位置选项（左上、中上、右上、左中、居中、右中、左下、中下、右下）
- 支持水印旋转（-180° 到 180°）
- 支持边缘填充，避免水印紧贴图片边缘

### 配置管理
- 水印模板保存与加载
- 自动保存上次使用的配置，启动时自动加载

## 系统要求

- Java 21 或更高版本
- Maven 3.6 或更高版本

## 构建和运行

### 使用Maven构建

```bash
mvn clean package
```

### 运行应用程序

```bash
mvn exec:java -Dexec.mainClass="org.example.SwingMainApp"
```

或者使用生成的jar文件运行：

```bash
java -jar target/PhotoWatermark2-1.0-SNAPSHOT-shaded.jar
```

## 使用说明

### 1. 导入图片
- 点击菜单栏"文件"->"导入图片"选择一个或多个图片文件
- 或点击"文件"->"导入文件夹"选择整个文件夹
- 也可以直接拖拽图片文件到预览区域

### 2. 设置水印
在"水印设置"面板中进行设置：

#### 文本水印
- 选择"文本水印"单选按钮
- 在文本框中输入水印文字
- 选择字体、字号、粗体、斜体等样式
- 点击"选择颜色"按钮选择文字颜色
- 调节透明度滑块设置文字透明度

#### 图片水印
- 选择"图片水印"单选按钮
- 点击"选择图片"按钮选择水印图片（建议使用PNG格式以支持透明背景）
- 调节缩放滑块调整图片水印大小
- 调节透明度滑块设置图片透明度

### 3. 设置位置和旋转
- 在位置下拉框中选择预设位置
- 或者在预览图中手动拖拽水印到指定位置（功能待完善）
- 使用旋转滑块调整水印旋转角度

### 4. 预览效果
- 在图片列表中选择任意图片即可在预览区查看实时效果
- 修改任何水印设置都会立即反映在预览中

### 5. 导出图片
- 在"导出设置"面板中选择输出格式（PNG/JPEG）
- 如选择JPEG格式，可以调节质量滑块
- 设置文件命名规则（保留原名、添加前缀或后缀）
- 选择输出文件夹
- 点击"导出图片"按钮完成导出

### 6. 模板管理
- 可以保存当前水印配置为模板，便于以后重复使用
- 程序会自动保存最后一次使用的配置，下次启动时自动加载

## 技术架构

### 主要类说明
- [MainFrame](src/main/java/org/example/MainFrame.java)：主窗口类，负责UI展示和事件处理
- [SwingMainApp](src/main/java/org/example/SwingMainApp.java)：应用程序入口点
- [WatermarkConfig](src/main/java/org/example/model/WatermarkConfig.java)：水印配置模型类
- [ImageData](src/main/java/org/example/model/ImageData.java)：图片数据模型类
- [WatermarkService](src/main/java/org/example/service/WatermarkService.java)：水印处理服务类
- [PreviewService](src/main/java/org/example/service/PreviewService.java)：预览服务类
- [TemplateService](src/main/java/org/example/service/TemplateService.java)：模板管理服务类

### 依赖
- [Gson](https://github.com/google/gson)：用于JSON序列化和反序列化配置文件

## 开发计划

- [x] 基本文件导入导出功能
- [x] 文本水印支持
- [x] 图片水印支持
- [x] 水印位置设置
- [x] 水印旋转功能
- [ ] 完善的手动拖拽水印定位
- [ ] 完整的配置模板管理界面
- [ ] 更多高级功能（阴影、描边等）
- [ ] 国际化支持


