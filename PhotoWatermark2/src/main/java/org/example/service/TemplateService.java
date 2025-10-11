package org.example.service;

import org.example.model.WatermarkConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

/**
 * 模板服务类，用于管理水印模板的保存、加载、删除等功能
 */
public class TemplateService {
    private static final String TEMPLATE_DIR = "templates";
    private static final String TEMPLATE_FILE_EXTENSION = ".json";
    private static final String LAST_CONFIG_FILE = "last_config.json";
    private static final String DEFAULT_TEMPLATE_NAME = "default";
    
    private final Gson gson;
    private final Path templateDirPath;
    private final Preferences prefs;

    public TemplateService() {
        // 创建自定义的Gson，用于处理File对象的序列化和反序列化
        this.gson = new GsonBuilder()
            .registerTypeAdapter(File.class, (JsonSerializer<File>) (file, type, jsonSerializationContext) -> 
                file != null ? new JsonPrimitive(file.getAbsolutePath()) : null)
            .registerTypeAdapter(File.class, (JsonDeserializer<File>) (json, type, jsonDeserializationContext) -> {
                if (json.isJsonPrimitive()) {
                    return new File(json.getAsString());
                }
                return null;
            })
            .setPrettyPrinting()
            .create();
        this.templateDirPath = Paths.get(TEMPLATE_DIR);
        this.prefs = Preferences.userNodeForPackage(TemplateService.class);
        
        // 确保模板目录存在
        createTemplateDirectory();
    }

    /**
     * 创建模板目录
     */
    private void createTemplateDirectory() {
        try {
            Files.createDirectories(templateDirPath);
        } catch (IOException e) {
            System.err.println("无法创建模板目录: " + e.getMessage());
        }
    }

    /**
     * 保存当前配置为模板
     *
     * @param config 要保存的水印配置
     * @param templateName 模板名称
     * @return 是否保存成功
     */
    public boolean saveTemplate(WatermarkConfig config, String templateName) {
        try {
            String json = gson.toJson(config);
            Path templatePath = templateDirPath.resolve(templateName + TEMPLATE_FILE_EXTENSION);
            Files.write(templatePath, json.getBytes());
            return true;
        } catch (IOException e) {
            System.err.println("保存模板失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 加载指定名称的模板
     *
     * @param templateName 模板名称
     * @return 水印配置，如果加载失败返回null
     */
    public WatermarkConfig loadTemplate(String templateName) {
        try {
            Path templatePath = templateDirPath.resolve(templateName + TEMPLATE_FILE_EXTENSION);
            if (!Files.exists(templatePath)) {
                return null;
            }
            
            String json = new String(Files.readAllBytes(templatePath));
            return gson.fromJson(json, WatermarkConfig.class);
        } catch (Exception e) {
            System.err.println("加载模板失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 获取所有模板名称
     *
     * @return 模板名称列表
     */
    public List<String> getAllTemplates() {
        List<String> templates = new ArrayList<>();
        try {
            if (Files.exists(templateDirPath)) {
                Files.list(templateDirPath)
                     .filter(path -> path.toString().endsWith(TEMPLATE_FILE_EXTENSION))
                     .map(path -> path.getFileName().toString())
                     .map(name -> name.substring(0, name.lastIndexOf('.')))
                     .forEach(templates::add);
            }
        } catch (IOException e) {
            System.err.println("获取模板列表失败: " + e.getMessage());
        }
        return templates;
    }

    /**
     * 删除指定名称的模板
     *
     * @param templateName 模板名称
     * @return 是否删除成功
     */
    public boolean deleteTemplate(String templateName) {
        try {
            Path templatePath = templateDirPath.resolve(templateName + TEMPLATE_FILE_EXTENSION);
            return Files.deleteIfExists(templatePath);
        } catch (IOException e) {
            System.err.println("删除模板失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 保存当前配置为默认模板
     *
     * @param config 当前水印配置
     */
    public void saveAsDefaultTemplate(WatermarkConfig config) {
        saveTemplate(config, DEFAULT_TEMPLATE_NAME);
    }

    /**
     * 加载默认模板
     *
     * @return 默认模板配置，如果没有则返回null
     */
    public WatermarkConfig loadDefaultTemplate() {
        return loadTemplate(DEFAULT_TEMPLATE_NAME);
    }

    /**
     * 保存当前配置以便下次启动时加载
     *
     * @param config 当前水印配置
     */
    public void saveLastConfig(WatermarkConfig config) {
        try {
            String json = gson.toJson(config);
            Path lastConfigPath = Paths.get(LAST_CONFIG_FILE);
            Files.write(lastConfigPath, json.getBytes());
        } catch (IOException e) {
            System.err.println("保存最后配置失败: " + e.getMessage());
        }
    }

    /**
     * 加载上次保存的配置
     *
     * @return 上次的配置，如果没有则返回null
     */
    public WatermarkConfig loadLastConfig() {
        try {
            Path lastConfigPath = Paths.get(LAST_CONFIG_FILE);
            if (!Files.exists(lastConfigPath)) {
                return null;
            }
            
            String json = new String(Files.readAllBytes(lastConfigPath));
            return gson.fromJson(json, WatermarkConfig.class);
        } catch (Exception e) {
            System.err.println("加载最后配置失败: " + e.getMessage());
            return null;
        }
    }
}