package org.example;

import org.example.model.ImageData;
import org.example.model.WatermarkConfig;
import org.example.service.PreviewService;
import org.example.service.WatermarkService;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

/**
 * 主窗口类
 */
public class MainFrame extends JFrame {
    // UI组件
    private JList<ImageData> imageList;
    private DefaultListModel<ImageData> listModel;
    private JLabel previewLabel;
    private JTextField textWatermarkField;
    private JComboBox<String> fontComboBox;
    private JSpinner fontSizeSpinner;
    private JCheckBox boldCheckBox;
    private JCheckBox italicCheckBox;
    private JButton colorButton;
    private JSlider opacitySlider;
    private JLabel opacityLabel;
    private JButton selectImageButton;
    private JLabel selectedImageLabel;
    private JSlider imageScaleSlider;
    private JLabel imageScaleLabel;
    private JSlider imageOpacitySlider;
    private JLabel imageOpacityLabel;
    private JComboBox<String> positionComboBox;
    private JSlider rotationSlider;
    private JLabel rotationLabel;
    private JRadioButton textWatermarkRadio;
    private JRadioButton imageWatermarkRadio;
    private ButtonGroup watermarkTypeGroup;
    private JComboBox<String> exportFormatComboBox;
    private JSlider jpegQualitySlider;
    private JLabel jpegQualityLabel;
    private JComboBox<String> namingConventionComboBox;
    private JTextField prefixTextField;
    private JTextField suffixTextField;
    private JButton exportButton;
    private JButton selectOutputFolderButton;
    private JLabel outputFolderLabel;

    // 数据
    private WatermarkConfig config;
    private WatermarkService watermarkService;
    private PreviewService previewService; // 新增预览服务
    private File outputFolder;

    public MainFrame() {
        config = new WatermarkConfig();
        watermarkService = new WatermarkService();
        previewService = new PreviewService(); // 初始化预览服务
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupDragAndDrop();
        setTitle("照片水印工具");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null); // 居中显示
    }

    /**
     * 初始化组件
     */
    private void initializeComponents() {
        // 图片列表
        listModel = new DefaultListModel<>();
        imageList = new JList<>(listModel);
        imageList.setCellRenderer(new ImageListCellRenderer());
        imageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 预览标签
        previewLabel = new JLabel();
        previewLabel.setHorizontalAlignment(JLabel.CENTER);
        previewLabel.setVerticalAlignment(JLabel.CENTER);
        previewLabel.setPreferredSize(new Dimension(400, 300));
        previewLabel.setBorder(BorderFactory.createEtchedBorder());

        // 文本水印字段
        textWatermarkField = new JTextField("水印文字");

        // 字体组合框
        String[] fonts = {"Arial", "Times New Roman", "Courier New", "Verdana",
                "Georgia", "Comic Sans MS", "Trebuchet MS", "Impact"};
        fontComboBox = new JComboBox<>(fonts);
        fontComboBox.setSelectedItem("Arial");

        // 字体大小微调器
        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(24, 8, 72, 1));

        // 粗体和斜体复选框
        boldCheckBox = new JCheckBox("粗体");
        italicCheckBox = new JCheckBox("斜体");

        // 颜色按钮
        colorButton = new JButton("选择颜色");
        colorButton.setBackground(Color.BLACK);

        // 不透明度滑块
        opacitySlider = new JSlider(0, 100, 100);
        opacityLabel = new JLabel("100%");

        // 选择图片按钮和标签
        selectImageButton = new JButton("选择图片");
        selectedImageLabel = new JLabel("未选择图片");

        // 图片缩放滑块
        imageScaleSlider = new JSlider(0, 200, 100);
        imageScaleLabel = new JLabel("100%");

        // 图片不透明度滑块
        imageOpacitySlider = new JSlider(0, 100, 100);
        imageOpacityLabel = new JLabel("100%");

        // 位置组合框
        String[] positions = {"TOP_LEFT", "TOP_CENTER", "TOP_RIGHT",
                "CENTER_LEFT", "CENTER", "CENTER_RIGHT",
                "BOTTOM_LEFT", "BOTTOM_CENTER", "BOTTOM_RIGHT"};
        positionComboBox = new JComboBox<>(positions);
        positionComboBox.setSelectedItem("CENTER");

        // 旋转滑块
        rotationSlider = new JSlider(-180, 180, 0);
        rotationLabel = new JLabel("0°");

        // 水印类型单选按钮
        textWatermarkRadio = new JRadioButton("文本水印", true);
        imageWatermarkRadio = new JRadioButton("图片水印");
        watermarkTypeGroup = new ButtonGroup();
        watermarkTypeGroup.add(textWatermarkRadio);
        watermarkTypeGroup.add(imageWatermarkRadio);

        // 导出格式组合框
        String[] formats = {"PNG", "JPEG"};
        exportFormatComboBox = new JComboBox<>(formats);
        exportFormatComboBox.setSelectedItem("PNG");

        // JPEG质量滑块
        jpegQualitySlider = new JSlider(0, 100, 90);
        jpegQualityLabel = new JLabel("90");

        // 命名规则组合框
        String[] namingConventions = {"ORIGINAL", "PREFIX", "SUFFIX"};
        namingConventionComboBox = new JComboBox<>(namingConventions);
        namingConventionComboBox.setSelectedItem("ORIGINAL");

        // 前缀和后缀文本框
        prefixTextField = new JTextField();
        suffixTextField = new JTextField("_watermarked");

        // 导出按钮
        exportButton = new JButton("导出图片");
        exportButton.setEnabled(false);

        // 选择输出文件夹按钮和标签
        selectOutputFolderButton = new JButton("选择输出文件夹");
        outputFolderLabel = new JLabel("未选择输出文件夹");
    }

    /**
     * 设置布局
     */
    private void setupLayout() {
        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 左侧面板 - 图片列表和导入按钮
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("图片列表"));
        leftPanel.add(new JScrollPane(imageList), BorderLayout.CENTER);

        // 导入按钮面板
        JPanel importButtonPanel = new JPanel(new FlowLayout());
        JButton importImagesButton = new JButton("导入图片");
        JButton importFolderButton = new JButton("导入文件夹");
        JButton deleteImageButton = new JButton("删除图片"); // 新增删除按钮
        importButtonPanel.add(importImagesButton);
        importButtonPanel.add(importFolderButton);
        importButtonPanel.add(deleteImageButton); // 添加删除按钮
        leftPanel.add(importButtonPanel, BorderLayout.SOUTH);

        // 右侧面板 - 预览和设置
        JPanel rightPanel = new JPanel(new BorderLayout());

        // 预览面板
        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBorder(BorderFactory.createTitledBorder("预览"));
        previewPanel.add(previewLabel, BorderLayout.CENTER);

        // 操作按钮面板
        JPanel actionPanel = new JPanel(new FlowLayout());
        JButton applyWatermarkButton = new JButton("应用水印");
        actionPanel.add(applyWatermarkButton);
        previewPanel.add(actionPanel, BorderLayout.SOUTH);

        rightPanel.add(previewPanel, BorderLayout.CENTER);

        // 设置面板
        JPanel settingsPanel = new JPanel(new BorderLayout());
        settingsPanel.setBorder(BorderFactory.createTitledBorder("水印设置"));

        // 水印类型面板
        JPanel watermarkTypePanel = new JPanel(new FlowLayout());
        watermarkTypePanel.setBorder(BorderFactory.createTitledBorder("水印类型"));
        watermarkTypePanel.add(textWatermarkRadio);
        watermarkTypePanel.add(imageWatermarkRadio);
        settingsPanel.add(watermarkTypePanel, BorderLayout.NORTH);

        // 水印详细设置选项卡
        JTabbedPane tabbedPane = new JTabbedPane();

        // 文本水印面板
        JPanel textWatermarkPanel = createTextWatermarkPanel();
        tabbedPane.addTab("文本水印", textWatermarkPanel);

        // 图片水印面板
        JPanel imageWatermarkPanel = createImageWatermarkPanel();
        tabbedPane.addTab("图片水印", imageWatermarkPanel);

        // 位置设置面板
        JPanel positionPanel = createPositionPanel();
        tabbedPane.addTab("位置设置", positionPanel);

        // 导出设置面板
        JPanel exportPanel = createExportPanel();
        tabbedPane.addTab("导出设置", exportPanel);

        settingsPanel.add(tabbedPane, BorderLayout.CENTER);
        rightPanel.add(settingsPanel, BorderLayout.SOUTH);

        // 添加左右面板到主面板
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        add(mainPanel);

        // 设置事件监听器
        importImagesButton.addActionListener(e -> handleImportImagesAction());
        importFolderButton.addActionListener(e -> handleImportFolderAction());
        applyWatermarkButton.addActionListener(e -> handleApplyWatermarkAction());
        selectOutputFolderButton.addActionListener(e -> handleSelectOutputFolderAction());
        exportButton.addActionListener(e -> handleExportAction());
        colorButton.addActionListener(e -> handleSelectColorAction());
        selectImageButton.addActionListener(e -> handleSelectWatermarkImageAction());
        deleteImageButton.addActionListener(e -> handleDeleteImageAction()); // 添加删除按钮事件监听器
    }

    /**
     * 创建文本水印面板
     */
    private JPanel createTextWatermarkPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // 文本水印标签和字段
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("水印文本:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(textWatermarkField, gbc);

        // 字体标签和组合框
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("字体:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(fontComboBox, gbc);

        // 字体大小标签和微调器
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("字体大小:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(fontSizeSpinner, gbc);

        // 粗体和斜体复选框
        JPanel fontStylePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fontStylePanel.add(boldCheckBox);
        fontStylePanel.add(italicCheckBox);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(fontStylePanel, gbc);

        // 颜色按钮
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("颜色:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(colorButton, gbc);

        // 不透明度标签、滑块和值标签
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("不透明度:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(opacitySlider, gbc);
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(opacityLabel, gbc);

        return panel;
    }

    /**
     * 创建图片水印面板
     */
    private JPanel createImageWatermarkPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // 选择图片按钮和标签
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(selectImageButton, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(selectedImageLabel, gbc);

        // 缩放标签、滑块和值标签
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("缩放:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(imageScaleSlider, gbc);
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(imageScaleLabel, gbc);

        // 不透明度标签、滑块和值标签
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("不透明度:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(imageOpacitySlider, gbc);
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(imageOpacityLabel, gbc);

        return panel;
    }

    /**
     * 创建位置设置面板
     */
    private JPanel createPositionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // 位置标签和组合框
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("位置:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(positionComboBox, gbc);

        // 旋转标签、滑块和值标签
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("旋转角度:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(rotationSlider, gbc);
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(rotationLabel, gbc);

        return panel;
    }

    /**
     * 创建导出设置面板
     */
    private JPanel createExportPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // 导出格式标签和组合框
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("导出格式:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(exportFormatComboBox, gbc);

        // JPEG质量标签、滑块和值标签
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("JPEG质量:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(jpegQualitySlider, gbc);
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(jpegQualityLabel, gbc);

        // 命名规则标签和组合框
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("命名规则:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(namingConventionComboBox, gbc);

        // 前缀标签和文本框
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("前缀:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(prefixTextField, gbc);

        // 后缀标签和文本框
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("后缀:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(suffixTextField, gbc);

        // 输出文件夹选择按钮和标签
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(selectOutputFolderButton, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(outputFolderLabel, gbc);

        // 导出按钮
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(exportButton, gbc);

        return panel;
    }

    /**
     * 设置事件处理器
     */
    private void setupEventHandlers() {
        // 图片列表选择事件
        imageList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updatePreview();
            }
        });

        // 水印类型切换事件
        ActionListener watermarkTypeListener = e -> {
            updateWatermarkTypeVisibility();
            updatePreview(); // 实时更新预览
        };
        textWatermarkRadio.addActionListener(watermarkTypeListener);
        imageWatermarkRadio.addActionListener(watermarkTypeListener);

        // 导出格式切换事件
        exportFormatComboBox.addActionListener(e -> {
            boolean isJpeg = "JPEG".equals(exportFormatComboBox.getSelectedItem());
            jpegQualitySlider.setEnabled(isJpeg);
            jpegQualityLabel.setEnabled(isJpeg);
        });

        // 命名规则切换事件
        namingConventionComboBox.addActionListener(e -> {
            String selected = (String) namingConventionComboBox.getSelectedItem();
            prefixTextField.setEnabled("PREFIX".equals(selected));
            suffixTextField.setEnabled("SUFFIX".equals(selected));
        });

        // 滑块值变化事件 - 实时更新预览
        opacitySlider.addChangeListener(e -> {
            opacityLabel.setText(opacitySlider.getValue() + "%");
            updatePreview();
        });
        imageScaleSlider.addChangeListener(e -> {
            imageScaleLabel.setText(imageScaleSlider.getValue() + "%");
            updatePreview();
        });
        imageOpacitySlider.addChangeListener(e -> {
            imageOpacityLabel.setText(imageOpacitySlider.getValue() + "%");
            updatePreview();
        });
        rotationSlider.addChangeListener(e -> {
            rotationLabel.setText(rotationSlider.getValue() + "°");
            updatePreview();
        });
        jpegQualitySlider.addChangeListener(e -> {
            jpegQualityLabel.setText(String.valueOf(jpegQualitySlider.getValue()));
            updatePreview();
        });

        // 文本框内容变化事件 - 实时更新预览
        textWatermarkField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updatePreview();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updatePreview();
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updatePreview();
            }
        });

        // 字体选择变化事件 - 实时更新预览
        fontComboBox.addActionListener(e -> updatePreview());
        fontSizeSpinner.addChangeListener(e -> updatePreview());
        boldCheckBox.addActionListener(e -> updatePreview());
        italicCheckBox.addActionListener(e -> updatePreview());
        colorButton.addActionListener(e -> updatePreview());
        positionComboBox.addActionListener(e -> updatePreview());

        // 添加鼠标右键菜单以删除图片
        imageList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleListMouseClick(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleListMouseClick(e);
            }
        });
    }

    /**
     * 处理列表鼠标点击事件（包括右键菜单）
     */
    private void handleListMouseClick(MouseEvent e) {
        int index = imageList.locationToIndex(e.getPoint());
        if (index >= 0 && index < listModel.size()) {
            // 选中被点击的项
            imageList.setSelectedIndex(index);
            
            // 如果是右键点击，显示删除菜单
            if (SwingUtilities.isRightMouseButton(e)) {
                JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem deleteItem = new JMenuItem("删除图片");
                deleteItem.addActionListener(ev -> handleDeleteImageAction());
                popupMenu.add(deleteItem);
                popupMenu.show(imageList, e.getX(), e.getY());
            }
        }
    }

    /**
     * 更新水印类型可见性
     */
    private void updateWatermarkTypeVisibility() {
        // 可以根据选择的水印类型启用/禁用相应的控件
    }

    /**
     * 导入文件
     */
    private void importFiles(List<File> files) {
        for (File file : files) {
            if (file.isDirectory()) {
                // 如果是目录，则递归导入其中的图片文件
                File[] imageFiles = file.listFiles(this::isImageFile);
                if (imageFiles != null) {
                    for (File imageFile : imageFiles) {
                        addImageToList(imageFile);
                    }
                }
            } else if (isImageFile(file)) {
                // 如果是图片文件，则添加到列表
                addImageToList(file);
            }
        }
    }

    /**
     * 检查文件是否为图片文件
     */
    private boolean isImageFile(File file) {
        if (file == null || !file.isFile()) {
            return false;
        }
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                fileName.endsWith(".png") || fileName.endsWith(".bmp") ||
                fileName.endsWith(".tiff") || fileName.endsWith(".tif");
    }

    /**
     * 添加图片到列表
     */
    private void addImageToList(File file) {
        ImageData imageData = new ImageData(file);
        listModel.addElement(imageData);

        // 如果是第一张图片，自动选中并在预览区显示
        if (listModel.getSize() == 1) {
            imageList.setSelectedIndex(0);
            updatePreview(); // 使用新的预览更新方法
        }
    }

    // UI事件处理方法

    private void handleImportImagesAction() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "图片文件 (*.jpg, *.jpeg, *.png, *.bmp, *.tiff, *.tif)",
                "jpg", "jpeg", "png", "bmp", "tiff", "tif"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            importFiles(List.of(selectedFiles));
        }
    }

    private void handleImportFolderAction() {
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = folderChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = folderChooser.getSelectedFile();
            importFiles(List.of(selectedDirectory));
        }
    }

    private void handleSelectWatermarkImageAction() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "图片文件 (*.png, *.jpg, *.jpeg)", "png", "jpg", "jpeg"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            config.setImageFile(selectedFile);
            selectedImageLabel.setText(selectedFile.getName());
            updatePreview(); // 更新预览
        }
    }

    private void handleSelectColorAction() {
        Color chosenColor = JColorChooser.showDialog(this, "选择颜色", colorButton.getBackground());
        if (chosenColor != null) {
            colorButton.setBackground(chosenColor);
            // 将颜色转换为十六进制字符串
            String hex = String.format("#%02x%02x%02x",
                    chosenColor.getRed(),
                    chosenColor.getGreen(),
                    chosenColor.getBlue());
            config.setColor(hex);
            updatePreview(); // 更新预览
        }
    }

    private void handleApplyWatermarkAction() {
        ImageData selectedImage = imageList.getSelectedValue();
        if (selectedImage == null) {
            JOptionPane.showMessageDialog(this, "请选择要应用水印的图片", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 更新配置
        updateConfigFromUI();
        
        // 使用预览服务更新预览，而不是手动处理
        updatePreview();
    }

    private void handleSelectOutputFolderAction() {
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = folderChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            outputFolder = folderChooser.getSelectedFile();
            outputFolderLabel.setText(outputFolder.getAbsolutePath());
            exportButton.setEnabled(true);
        }
    }

    private void handleExportAction() {
        if (outputFolder == null) {
            JOptionPane.showMessageDialog(this, "请选择输出文件夹", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (listModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有可导出的图片", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 禁用导出按钮防止重复点击
        exportButton.setEnabled(false);

        // 在后台线程执行导出操作
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                exportImages();
                return null;
            }

            @Override
            protected void done() {
                exportButton.setEnabled(true);
                JOptionPane.showMessageDialog(MainFrame.this, "导出完成！", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        };
        worker.execute();
    }

    /**
     * 删除选中的图片
     */
    private void handleDeleteImageAction() {
        ImageData selectedImage = imageList.getSelectedValue();
        if (selectedImage != null) {
            int option = JOptionPane.showConfirmDialog(this, 
                "确定要删除选中的图片吗？\n" + selectedImage.getName(), 
                "确认删除", 
                JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                int selectedIndex = imageList.getSelectedIndex();
                listModel.removeElement(selectedImage);
                
                // 如果删除的是最后一张图片，清空预览
                if (listModel.isEmpty()) {
                    previewLabel.setIcon(null);
                    previewLabel.setText("");
                } else {
                    // 选中下一张图片或者上一张图片
                    if (selectedIndex < listModel.size()) {
                        imageList.setSelectedIndex(selectedIndex);
                    } else if (listModel.size() > 0) {
                        imageList.setSelectedIndex(listModel.size() - 1);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "请先选择要删除的图片", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * 更新配置对象中的值
     */
    private void updateConfigFromUI() {
        config.setText(textWatermarkField.getText());
        config.setFontName((String) fontComboBox.getSelectedItem());
        config.setFontSize((Integer) fontSizeSpinner.getValue());
        config.setBold(boldCheckBox.isSelected());
        config.setItalic(italicCheckBox.isSelected());
        config.setOpacity(opacitySlider.getValue());
        config.setImageScale(imageScaleSlider.getValue());
        config.setImageOpacity(imageOpacitySlider.getValue());
        config.setPosition((String) positionComboBox.getSelectedItem());
        config.setRotation(rotationSlider.getValue());
        config.setExportFormat((String) exportFormatComboBox.getSelectedItem());
        config.setJpegQuality(jpegQualitySlider.getValue());
        config.setNamingConvention((String) namingConventionComboBox.getSelectedItem());
        config.setPrefix(prefixTextField.getText());
        config.setSuffix(suffixTextField.getText());
    }

    /**
     * 更新预览显示
     * 此方法确保预览始终基于原始图像生成，避免出现多个水印的情况
     */
    private void updatePreview() {
        ImageData selectedImage = imageList.getSelectedValue();
        if (selectedImage != null) {
            // 更新配置
            updateConfigFromUI();
            
            // 重新创建 ImageData 对象以确保使用原始图像
            // 这样可以确保每次预览都是在原始图像上添加水印，而不是在已有水印的图像上再次添加
            ImageData freshImageData = new ImageData(selectedImage.getFile());
            
            // 清除之前的预览内容，确保只显示当前位置的水印
            previewLabel.setIcon(null);
            previewLabel.setText("");
            
            // 使用预览服务更新预览
            // 注意：PreviewService 始终基于原始图像生成预览，确保只有一个水印显示在新位置
            previewService.updatePreview(previewLabel, freshImageData, config);
        }
    }

    /**
     * 导出所有图片
     */
    private void exportImages() throws IOException {
        updateConfigFromUI();

        String format = config.getExportFormat();
        int jpegQuality = (int) config.getJpegQuality();
        String namingConvention = config.getNamingConvention();
        String prefix = config.getPrefix();
        String suffix = config.getSuffix();

        for (int i = 0; i < listModel.getSize(); i++) {
            ImageData imageData = listModel.getElementAt(i);

            // 生成输出文件名
            String originalName = imageData.getFile().getName();
            String nameWithoutExtension = originalName.substring(0, originalName.lastIndexOf('.'));
            String extension = format.toLowerCase();

            String outputName;
            switch (namingConvention) {
                case "PREFIX":
                    outputName = prefix + nameWithoutExtension + "." + extension;
                    break;
                case "SUFFIX":
                    outputName = nameWithoutExtension + suffix + "." + extension;
                    break;
                default: // ORIGINAL
                    outputName = nameWithoutExtension + "." + extension;
                    break;
            }

            File outputFile = new File(outputFolder, outputName);

            // 添加水印并保存图片
            // 重新加载原始图像以避免重复添加水印
            ImageData freshImageData = new ImageData(imageData.getFile());
            BufferedImage watermarkedImage = watermarkService.addWatermark(freshImageData, config);
            if (watermarkedImage != null) {
                watermarkService.saveImage(watermarkedImage, outputFile, format, jpegQuality);
            }
        }
    }

    /**
     * 设置拖拽支持
     */
    private void setupDragAndDrop() {
        // 为图片列表设置拖拽支持
        imageList.setDropTarget(new DropTarget());
        try {
            imageList.getDropTarget().addDropTargetListener(new DropTargetListener() {
                @Override
                public void dragEnter(DropTargetDragEvent dtde) {
                    if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        dtde.acceptDrag(DnDConstants.ACTION_COPY);
                    } else {
                        dtde.rejectDrag();
                    }
                }

                @Override
                public void dragOver(DropTargetDragEvent dtde) {
                    // 空实现
                }

                @Override
                public void dropActionChanged(DropTargetDragEvent dtde) {
                    // 空实现
                }

                @Override
                public void dragExit(DropTargetEvent dte) {
                    // 空实现
                }

                @Override
                public void drop(DropTargetDropEvent dtde) {
                    try {
                        Transferable transferable = dtde.getTransferable();
                        if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                            dtde.acceptDrop(DnDConstants.ACTION_COPY);
                            List<File> files = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                            importFiles(files);
                            // 如果导入了文件且没有选中任何图片，则选中第一张图片
                            if (imageList.getSelectedValue() == null && listModel.getSize() > 0) {
                                imageList.setSelectedIndex(0);
                            }
                            dtde.dropComplete(true);
                        } else {
                            dtde.rejectDrop();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        dtde.rejectDrop();
                    }
                }
            });
        } catch (TooManyListenersException e) {
            e.printStackTrace();
        }
        
        // 保留原有的预览区域拖拽支持
        previewLabel.setDropTarget(new DropTarget());
        try {
            previewLabel.getDropTarget().addDropTargetListener(new DropTargetListener() {
                @Override
                public void dragEnter(DropTargetDragEvent dtde) {
                    if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        dtde.acceptDrag(DnDConstants.ACTION_COPY);
                    } else {
                        dtde.rejectDrag();
                    }
                }

                @Override
                public void dragOver(DropTargetDragEvent dtde) {
                    // 空实现
                }

                @Override
                public void dropActionChanged(DropTargetDragEvent dtde) {
                    // 空实现
                }

                @Override
                public void dragExit(DropTargetEvent dte) {
                    // 空实现
                }

                @Override
                public void drop(DropTargetDropEvent dtde) {
                    try {
                        Transferable transferable = dtde.getTransferable();
                        if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                            dtde.acceptDrop(DnDConstants.ACTION_COPY);
                            List<File> files = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                            importFiles(files);
                            // 如果导入了文件且没有选中任何图片，则选中第一张图片
                            if (imageList.getSelectedValue() == null && listModel.getSize() > 0) {
                                imageList.setSelectedIndex(0);
                            }
                            dtde.dropComplete(true);
                        } else {
                            dtde.rejectDrop();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        dtde.rejectDrop();
                    }
                }
            });
        } catch (TooManyListenersException e) {
            e.printStackTrace();
        }
    }

    /**
     * 图片列表单元格渲染器
     */
    private static class ImageListCellRenderer extends JPanel implements ListCellRenderer<ImageData> {
        private final JLabel imageLabel = new JLabel();
        private final JLabel nameLabel = new JLabel();

        public ImageListCellRenderer() {
            setLayout(new BorderLayout());
            imageLabel.setPreferredSize(new Dimension(50, 50));
            add(imageLabel, BorderLayout.WEST);
            add(nameLabel, BorderLayout.CENTER);
            setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends ImageData> list,
                                                      ImageData value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            if (value != null) {
                if (value.getImage() != null) {
                    ImageIcon icon = new ImageIcon(value.getImage());
                    Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(img));
                }
                nameLabel.setText(value.getName());
            }

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            return this;
        }
    }
}