package org.framegen.core.file;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

/**
 * 文件工具类
 */
@Slf4j
public class FileUtil {

    public static int MaxDepth = 3;

    /**
     * 判断当前项目是否为多模块项目
     *
     * @return true: 多模块项目; false: 单模块项目;
     */
    public static Boolean isMultiModule() {

        String rootPath = System.getProperty("user.dir");
        if (new File(Paths.get(rootPath, "settings.gradle.kts").toString()).exists()) {
            return true;
        }
        if (new File(Paths.get(rootPath, "settings.gradle").toString()).exists()) {
            return true;
        }
        File xmlFile = new File(Paths.get(rootPath, "pom.xml").toString());
        if (xmlFile.exists()) {
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(xmlFile);
                doc.getDocumentElement().normalize();

                NodeList modulesList = doc.getElementsByTagName("modules");
                return modulesList.getLength() > 0;
            } catch (Exception ignored) {

            }
        }
        log.error("判断多模项目失败");
        return false;
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

    public static <T> Path findModulePath(Class<T> clazz) throws URISyntaxException {
        URL resource = clazz.getClassLoader().getResource("");
        if (null == resource) throw new NullPointerException();
        Path classPath = Paths.get(resource.toURI());
        return classPath.getParent().getParent();
    }
}
