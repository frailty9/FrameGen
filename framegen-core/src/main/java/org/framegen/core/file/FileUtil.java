package org.framegen.core.file;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * 文件工具类
 */
@Slf4j
public class FileUtil {

    public static int MaxDepth = 3;

    /**
     * 判断当前项目是否为多模块项目
     *
     * @return true: 多模块项目; false: 单模块项目; null: 无法判断
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
        return null;
    }

    /**
     * 查找模块对应路径
     * 广度优先搜索
     * @param path 当前目录
     * @param moduleName 模块名
     * @param currentDepth 当前深度
     * @return 模块路径; null: 找不到
     */
    public static Path getModulePath(File path, String moduleName, int currentDepth) {
        if (currentDepth > MaxDepth) {
            return null;
        }
        if ("src".equals(path.getName())) {
            return null;
        }

        File[] files = path.listFiles();
        ArrayList<File> dirs = new ArrayList<>();
        if (null != files) {
            for (File file : files) {
                if (file.isDirectory()) {
                    if (file.getName().equals(moduleName)) {
                        return file.toPath();
                    }
                    dirs.add(file);
                }
            }
        }
        for (File dir : dirs) {
            Path result = getModulePath(dir, moduleName, currentDepth + 1);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public static Path getModulePath(String moduleName) {
        String rootPath = System.getProperty("user.dir");

        Path result = getModulePath(new File(rootPath), moduleName, 1);
        if(null == result) {
            log.warn("FrameGen: 找不到指定模块名: {}", moduleName);
        }
        return result;
    }

    public static <T> Path getModulePath(Class<T> clazz) throws URISyntaxException {
        URL resource = clazz.getClassLoader().getResource("");
        if (null == resource) throw new NullPointerException();
        Path classPath = Paths.get(resource.toURI());
        return classPath.getParent().getParent();
    }
}
