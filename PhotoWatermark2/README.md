# PhotoWatermark 照片水印工具

一个基于JavaFX的照片水印添加工具，支持文本水印和图片水印，具有直观的用户界面和丰富的功能。

## 功能特性

1. **文件处理**
   - 支持单张图片拖拽或通过文件选择器导入
   - 支持批量导入，可一次性选择多张图片或直接导入整个文件夹
   - 在界面上显示已导入图片的列表（缩略图和文件名）

2. **支持格式**
   - 输入格式：JPEG, PNG, BMP, TIFF
   - 输出格式：JPEG, PNG

3. **导出设置**
   - 用户可指定一个输出文件夹，防止覆盖原图
   - 提供多种文件命名规则选项
   - JPEG格式支持质量调节
   - 支持导出时调整图片尺寸

4. **水印类型**
   - 文本水印：可自定义内容、字体、颜色、透明度等
   - 图片水印：支持PNG透明通道，可调节大小和透明度

5. **水印布局与样式**
   - 实时预览水印效果
   - 预设9个位置选项
   - 支持手动拖拽定位（部分实现）
   - 支持水印旋转

6. **配置管理**
   - 水印模板保存与加载（基本实现）

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
mvn javafx:run
```

或者使用生成的jar文件运行：

```bash
java -jar target/PhotoWatermark2-1.0-SNAPSHOT.jar
```

## 生成Windows可执行文件

要生成Windows可执行文件(.exe)，您可以使用以下方法之一：

### 方法1：使用JavaFX Maven插件（推荐）

```bash
mvn javafx:jlink
```

这将创建一个包含运行时的独立应用程序包，位于 `target/javafx/app` 目录中。

### 方法2：使用JPackage工具（Java 14+）

1. 首先创建一个包含所有依赖的JAR文件：
   ```bash
   mvn clean package
   ```

2. 使用JPackage创建Windows可执行文件：
   ```bash
   jpackage --input target --name PhotoWatermark --app-version 1.0 --main-class org.example.MainApp --main-jar PhotoWatermark2-1.0-SNAPSHOT.jar --type exe --win-shortcut --win-menu
   ```

### 方法3：使用Launch4j（第三方工具）

1. 下载并安装Launch4j
2. 使用Maven构建JAR文件：
   ```bash
   mvn clean package
   ```
3. 使用Launch4j包装JAR文件为exe文件

## 使用说明

1. **导入图片**
   - 点击菜单栏"文件"->"导入图片"选择一个或多个图片文件
   - 或点击"文件"->"导入文件夹"选择整个文件夹
   - 也可以直接拖拽图片文件到预览区域

2. **设置水印**
   - 在"水印设置"标签页中选择水印类型（文本或图片）
   - 配置水印的各项参数：
     - 文本水印：输入文字、选择字体、设置大小、颜色和透明度
     - 图片水印：选择图片文件，调整缩放比例和透明度
   - 设置水印位置和旋转角度

3. **预览效果**
   - 在图片列表中选择任意图片即可在预览区查看
   - 点击"应用水印"按钮查看水印效果

4. **导出图片**
   - 在"导出设置"标签页中选择输出格式和质量
   - 设置文件命名规则（保留原名、添加前缀或后缀）
   - 选择输出文件夹
   - 点击"导出图片"按钮完成导出

## 技术架构

- **语言**: Java 21
- **框架**: JavaFX for UI, Maven for build
- **主要类**:
  - `MainApp`: 应用程序入口点
  - `MainController`: UI控制器，处理用户交互
  - `WatermarkService`: 水印处理服务
  - `WatermarkConfig`: 水印配置模型
  - `ImageData`: 图片数据模型

## 开发计划

- [x] 基本文件导入导出功能
- [x] 文本水印支持
- [x] 图片水印支持
- [x] 水印位置设置
- [x] 水印旋转功能
- [ ] 手动拖拽水印定位
- [ ] 完整的配置模板管理
- [ ] 更多高级功能（阴影、描边等）

## 许可证

MIT License