package org.framegen.core.file;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件工具类
 */
@Slf4j
public class FileUtil {

    public static int MaxDepth = 3;

    // 匹配 include 后的模块名（支持 "name"、'name'、:name）
    private static final Pattern INCLUDE_PATTERN = Pattern.compile(
            "include\\s+([\"'`:][^\"'`)]+)"
    );
    // 匹配模块名（从 "module" 或 :module 中提取）
    private static final Pattern MODULE_NAME_PATTERN = Pattern.compile(
            "[\"':]([^\"':\\s,()]+)"
    );

    /**
     * 判断当前项目是否为多模块项目
     *
     * @return true: 多模块项目; false: 单模块项目;
     */
    public static List<String> getModuleNames() {

        String rootPath = System.getProperty("user.dir");
        Path settings = Paths.get(rootPath, "settings.gradle");
        Path settingsKt = Paths.get(rootPath, "settings.gradle.kts");
        Path mavenPom = Paths.get(rootPath, "pom.xml");

        if (Files.exists(settings)) {
            return extractModulesFromGradle(settings);
        }
        if (Files.exists(settingsKt)) {
            return extractModulesFromGradle(settingsKt);
        }
        if (Files.exists(mavenPom)) {
            return extractModulesFromMaven(mavenPom);
        }

        return Collections.emptyList();
    }

    /**
     * 从 settings.gradle 文件中提取子模块名（正则解析，初期方案）
     *
     * @param settingsFile settings.gradle 文件路径
     * @param charset      文件编码
     * @return 模块名列表，如 ["app", "lib"]
     */
    public static List<String> extractModulesFromGradle(Path settingsFile, Charset charset) {
        try {
            // 读取文件内容
            String content = new String(Files.readAllBytes(settingsFile), charset);
            // 解析模块名
            List<String> modules = new ArrayList<>();
            Matcher matcher = INCLUDE_PATTERN.matcher(content);
            // 循环匹配
            while (matcher.find()) {
                String includePart = matcher.group(1);
                Matcher nameMatcher = MODULE_NAME_PATTERN.matcher(includePart);
                while (nameMatcher.find()) {
                    String moduleName = nameMatcher.group(1).trim();
                    if (!moduleName.isEmpty()) {
                        modules.add(moduleName);
                    }
                }
            }
            return modules;
        } catch (IOException e) {
            System.err.println("Failed to read settings file: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // 重载方法，使用默认字符集为 UTF-8
    // TODO: 后期适配多字符集
    public static List<String> extractModulesFromGradle(Path settingsFile) {
        return extractModulesFromGradle(settingsFile, StandardCharsets.UTF_8);
    }

    /**
     * 从 Maven 的 pom.xml 文件中提取子模块名称列表。
     *
     * @param pomPath pom.xml 文件路径
     * @return 模块名列表，如 ["app", "lib"]
     */
    public static List<String> extractModulesFromMaven(Path pomPath) {
        List<String> modules = new ArrayList<>();
        try {
            // 创建 XML 解析器工厂和构建器
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // 将 Path 转为 File 并解析 XML 文档
            File pomFile = pomPath.toFile();
            Document doc = builder.parse(pomFile);

            // 获取根元素并验证是否为 project（基础校验）
            Element root = doc.getDocumentElement();
            if (root == null || !"project".equalsIgnoreCase(root.getTagName())) {
                return Collections.emptyList();
            }
            // 查找所有 <module> 标签
            NodeList moduleNodes = doc.getElementsByTagName("module");

            // 遍历每个 <module> 节点，提取文本内容作为模块名
            for (int i = 0; i < moduleNodes.getLength(); i++) {
                Node node = moduleNodes.item(i);
                // 确保是元素节点
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String moduleName = node.getTextContent().trim();
                    if (!moduleName.isEmpty()) {
                        modules.add(moduleName);
                    }
                }
            }
        } catch (Exception e) {
            // 解析失败时返回空列表
            return Collections.emptyList();
        }
        // 返回不可变列表（避免外部修改）
        return Collections.unmodifiableList(modules);
    }

    /**
     * 查找模块对应路径（广度优先搜索）
     *
     * @param startDir   起始目录
     * @param moduleName 模块名（要匹配的目录名）
     * @param maxDepth   最大搜索深度（从 startDir 开始计算）
     * @return 模块对应的 Path
     * @throws NoSuchFileException      如果未找到模块
     * @throws IOException              如果访问目录时发生 I/O 错误
     * @throws SecurityException        如果无权限访问某目录
     * @throws IllegalArgumentException 如果输入参数无效
     */
    public static Path findModulePath(Path startDir, String moduleName, int maxDepth)
            throws IOException {

        // 参数校验
        if (startDir == null) {
            throw new IllegalArgumentException("起始目录不能为 null");
        }
        if (moduleName == null || moduleName.trim().isEmpty()) {
            throw new IllegalArgumentException("模块名不能为空");
        }
        if (maxDepth < 0) {
            throw new IllegalArgumentException("最大深度不能为负数");
        }
        if (!Files.exists(startDir)) {
            throw new NoSuchFileException("起始目录不存在: " + startDir.toAbsolutePath());
        }
        if (!Files.isDirectory(startDir)) {
            throw new NotDirectoryException("起始路径不是目录: " + startDir.toAbsolutePath());
        }

        // 使用队列实现 BFS
        Queue<Path> queue = new LinkedList<>();
        queue.add(startDir);

        // 防止循环（如符号链接导致的重复访问）
        Set<Path> visited = new HashSet<>();
        visited.add(startDir);

        while (!queue.isEmpty()) {
            Path current = queue.poll();

            // 计算当前深度（相对于起始目录）
            int depth = current.getNameCount() - startDir.getNameCount();
            if (depth > maxDepth) {
                continue; // 超出深度，不再深入
            }

            // 检查当前目录名是否匹配模块名
            if (current.getFileName() != null &&
                    current.getFileName().toString().equals(moduleName)) {
                return current; // 找到！直接返回
            }

            // 跳过 "src" 目录（根据你的原始逻辑）
            Path fileName = current.getFileName();
            if (fileName != null && "src".equals(fileName.toString())) {
                continue;
            }

            // 遍历子目录
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(current, Files::isDirectory)) {
                for (Path subDir : stream) {
                    if (visited.add(subDir)) { // 如果是新目录
                        queue.add(subDir);
                    }
                }
            } catch (IOException | SecurityException e) {
                // 记录警告，但不停止整个搜索
                System.err.println("无法访问目录，跳过: " + current + " -> " + e.getMessage());
                // 继续搜索其他路径
            }
        }

        // BFS 结束仍未找到，抛出异常
        throw new NoSuchFileException(
                "未找到模块 '" + moduleName + "'。在路径 '" + startDir.toAbsolutePath() +
                        "' 下深度 " + maxDepth + " 范围内未搜索到。");
    }

    public static Path findModulePath(String moduleName) throws IOException {
        String rootPath = System.getProperty("user.dir");

        return findModulePath(Paths.get(rootPath), moduleName, 1);
    }

    /**
     * 查找模块路径（根据类所在包名）
     *
     * @param clazz 要查找的类
     * @return 模块路径 ！！注意可能为null！！
     */
    public static Path findModulePath(Class<?> clazz) {
        try {
        URL resource = clazz.getClassLoader().getResource("");
        if (null == resource) throw new NullPointerException();
        Path classPath = Paths.get(resource.toURI());
        return classPath.getParent().getParent();
        } catch (URISyntaxException e) {
            return null;
        }
    }
}
