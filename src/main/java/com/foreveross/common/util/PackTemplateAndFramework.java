/*******************************************************************************
 * Copyright (c) Jan 14, 2016 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.StringUtils;
import org.iff.infra.util.FCS;
import org.iff.infra.util.StreamHelper;
import org.iff.infra.util.StringHelper;
import org.iff.infra.util.ZipHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Jan 14, 2016
 */
public class PackTemplateAndFramework {

    public static void main(String[] args) {

        //zipTemplate();

        genVersion();

        zipFramework();

        zipSimpleAuthFramework();
    }

    public static void genVersion() {
        try {
            String version = "1.0.0";
            {
                String content = StreamHelper
                        .getContent(
                                new FileInputStream(
                                        new File(StringHelper.pathConcat(new File("").getAbsolutePath(), "pom.xml"))),
                                false);
                String[] split = StringUtils.split(content, "\n");
                for (String str : split) {
                    if (StringHelper.wildCardMatch(str, "*<version>*")) {
                        version = StringUtils.substringAfter(str, "<version>");
                        version = StringUtils.substringBefore(version, "</version>").trim();
                        break;
                    }
                }
                version = version + "-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            }
            {
                String filePath = "src/main/resources/META-INF/config/version.properties";
                String content = StreamHelper
                        .getContent(
                                new FileInputStream(
                                        new File(StringHelper.pathConcat(new File("").getAbsolutePath(), filePath))),
                                false);
                String[] split = StringUtils.splitPreserveAllTokens(StringUtils.replace(content, "\r", ""), "\n");
                for (int i = 0; i < split.length; i++) {
                    String str = split[i];
                    if (StringHelper.wildCardMatch(str, "*system.version*")) {
                        split[i] = "system.version=" + version;
                        break;
                    }
                }
                content = StringUtils.join(split, "\r\n");
                FileWriter fileWriter = new FileWriter(
                        new File(StringHelper.pathConcat(new File("").getAbsolutePath(), filePath)));
                fileWriter.write(content);
                StreamHelper.closeWithoutError(fileWriter);
                System.out.println(FCS.get("Set version {0} to file : {1}", version, filePath));
            }
            {
                String filePath = "src/main/webapp/resource/modules/elementui-2.3.5/version.js";
                String content = ";var app_version='" + version + "';";
                FileWriter fileWriter = new FileWriter(
                        new File(StringHelper.pathConcat(new File("").getAbsolutePath(), filePath)));
                fileWriter.write(content);
                StreamHelper.closeWithoutError(fileWriter);
                System.out.println(FCS.get("Set version {0} to file : {1}", version, filePath));
            }

        } catch (Exception e) {
        }
    }

    public static void zipTemplate() {
        String projectRoot = new File("").getAbsolutePath();
        List<String> zipPaths = new ArrayList<String>();
        {
            for (File f : FileUtils.listFiles(
                    new File(StringHelper.pathConcat(projectRoot, "src/main/webapp/WEB-INF/project_template/1.0.0")),
                    FileFilterUtils.fileFileFilter(), FileFilterUtils.directoryFileFilter())) {
                zipPaths.add(f.getAbsolutePath());
            }
        }
        ZipHelper.zip(zipPaths.toArray(new String[zipPaths.size()]), StringHelper.pathConcat(projectRoot,
                "src/main/webapp/WEB-INF/project_base_framework", "template-1.0.0.zip"), null);
    }

    public static void zipFramework() {
        String tmpRoot = null;
        try {
            String projectRoot = new File("").getAbsolutePath();
            List<String> paths = new ArrayList<String>();
            {
                paths.add("pom.xml");
                paths.add(".gitignore");
                paths.add("codetemplates.xml");
                paths.add("stylejava.xml");
                paths.add("stylejs.xml");
                paths.add("lib");
                paths.add("src/test");
                paths.add("src/main/java");
                paths.add("src/main/resources");
                paths.add("src/main/webapp/index.html");
                paths.add("src/main/webapp/WEB-INF");
                paths.add("src/main/webapp/resource/docs");
                paths.add("src/main/webapp/resource/modules");
                paths.add("src/main/webapp/resource/pages");
            }
            File tmpDir = File.createTempFile("project_framework", StringHelper.uuid());
            {// create project directory
                File parent = tmpDir.getParentFile();
                tmpDir.delete();
                tmpDir = new File(parent, "project_framework/" + StringHelper.uuid());
                tmpDir.mkdirs();
                FileUtils.cleanDirectory(tmpDir);
            }
            tmpRoot = tmpDir.getAbsolutePath();
            {// copy files
                for (String path : paths) {
                    String dirSrc = StringHelper.pathConcat(projectRoot, path);
                    String dirDes = StringHelper.pathConcat(tmpRoot, path);
                    File file = new File(dirSrc);
                    if (file.isDirectory()) {
                        FileUtils.copyDirectory(new File(dirSrc), new File(dirDes));
                    } else if (file.isFile()) {
                        FileUtils.copyFile(new File(dirSrc), new File(dirDes));
                    }
                }
            }
            {// replace files
                paths.clear();
                String replaceBase = "src/test/resources/replace/";
                paths.add("src");
                paths.add("pom.xml");
                for (String path : paths) {
                    String dirSrc = StringHelper.pathConcat(projectRoot, replaceBase, path);
                    String dirDes = StringHelper.pathConcat(tmpRoot, path);
                    File file = new File(dirSrc);
                    if (file.isDirectory()) {
                        FileUtils.copyDirectory(new File(dirSrc), new File(dirDes));
                    } else if (file.isFile()) {
                        FileUtils.copyFile(new File(dirSrc), new File(dirDes));
                    }
                }
            }
            {// delete files
                paths.clear();
                paths.add("src/main/java/com/foreveross/common/util/CodeGenerator.java");
                paths.add("src/main/java/com/foreveross/common/util/PackClient.java");
                paths.add("src/main/java/com/foreveross/common/util/PackTemplateAndFramework.java");
                paths.add("src/main/resources/diagrams");
                paths.add("src/main/resources/models");
                paths.add("src/test/resources/replace");
                for (String path : paths) {
                    String dirDes = StringHelper.pathConcat(tmpRoot, path);
                    File file = new File(dirDes);
                    FileUtils.deleteQuietly(file);
                }
            }
            List<String> zipPaths = new ArrayList<String>();
            {
                for (File f : tmpDir.listFiles()) {
                    zipPaths.add(f.getAbsolutePath());
                }
            }
            String targetDir = StringHelper.pathConcat(projectRoot, "src/main/webapp/resource/baseproject");
            new File(targetDir).mkdirs();
            ZipHelper.zip(zipPaths.toArray(new String[zipPaths.size()]),
                    StringHelper.pathConcat(targetDir, "qdp-4.0.0.zip"),
                    null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (StringUtils.isNotEmpty(tmpRoot)) {
                try {
                    FileUtils.deleteDirectory(new File(tmpRoot));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void zipSimpleAuthFramework() {
        String tmpRoot = null;
        try {
            String projectRoot = new File("").getAbsolutePath();
            List<String> paths = new ArrayList<String>();
            {
                paths.add("pom.xml");
                paths.add(".gitignore");
                paths.add("codetemplates.xml");
                paths.add("stylejava.xml");
                paths.add("stylejs.xml");
                paths.add("lib");
                paths.add("src/test");
                paths.add("src/main/java");
                paths.add("src/main/resources");
                paths.add("src/main/webapp/index.html");
                paths.add("src/main/webapp/WEB-INF");
                paths.add("src/main/webapp/resource/docs");
                paths.add("src/main/webapp/resource/modules");
                paths.add("src/main/webapp/resource/pages");
            }
            File tmpDir = File.createTempFile("project_framework", StringHelper.uuid());
            {// create project directory
                File parent = tmpDir.getParentFile();
                tmpDir.delete();
                tmpDir = new File(parent, "project_framework/" + StringHelper.uuid());
                tmpDir.mkdirs();
                FileUtils.cleanDirectory(tmpDir);
            }
            tmpRoot = tmpDir.getAbsolutePath();
            {// copy files
                for (String path : paths) {
                    String dirSrc = StringHelper.pathConcat(projectRoot, path);
                    String dirDes = StringHelper.pathConcat(tmpRoot, path);
                    File file = new File(dirSrc);
                    if (file.isDirectory()) {
                        FileUtils.copyDirectory(new File(dirSrc), new File(dirDes));
                    } else if (file.isFile()) {
                        FileUtils.copyFile(new File(dirSrc), new File(dirDes));
                    }
                }
            }
            {// replace files
                paths.clear();
                String replaceBase = "src/test/resources/replace-noauth/";
                paths.add("src");
                paths.add("pom.xml");
                for (String path : paths) {
                    String dirSrc = StringHelper.pathConcat(projectRoot, replaceBase, path);
                    String dirDes = StringHelper.pathConcat(tmpRoot, path);
                    File file = new File(dirSrc);
                    if (file.isDirectory()) {
                        FileUtils.copyDirectory(new File(dirSrc), new File(dirDes));
                    } else if (file.isFile()) {
                        FileUtils.copyFile(new File(dirSrc), new File(dirDes));
                    }
                }
            }
            {// delete files
                paths.clear();
                paths.add("src/test/java/com");
                paths.add("src/test/resources/replace");
                paths.add("src/test/resources/replace-noauth");
                //
                paths.add("src/main/java/com/foreveross/common/util/CodeGenerator.java");
                paths.add("src/main/java/com/foreveross/common/util/PackClient.java");
                paths.add("src/main/java/com/foreveross/common/util/PackTemplateAndFramework.java");
                //
                paths.add("src/main/java/com/foreveross/qdp/application/system/auth");
                paths.add("src/main/java/com/foreveross/qdp/application/system/common");
                paths.add("src/main/java/com/foreveross/qdp/domain/system/auth");
                paths.add("src/main/java/com/foreveross/qdp/domain/system/common");
                paths.add("src/main/java/com/foreveross/qdp/infra/vo/system/auth");
                paths.add("src/main/java/com/foreveross/qdp/infra/vo/system/common");
                paths.add("src/main/resources/META-INF/mappings/com/foreveross/qdp/domain/system/auth");
                paths.add("src/main/resources/META-INF/mappings/com/foreveross/qdp/domain/system/common");
                //
                paths.add("src/main/java/com/foreveross/extension/query");
                paths.add("src/main/java/com/foreveross/extension/mvel");
                //
                paths.add(
                        "src/main/java/com/foreveross/common/application/impl/DefaultAuthorizationApplicationImpl.java");
                paths.add("src/main/java/com/foreveross/common/application/impl/DefaultSystemApplicationImpl.java");
                paths.add("src/main/java/com/foreveross/qdp/application/system/restapi/AuthUserRestApiApplication.java");
                //
                paths.add("src/main/resources/diagrams");
                paths.add("src/main/resources/models");
                //
                paths.add("src/main/resources/META-INF/spring-rpc-consumer/rpc-consumer-systemcore.xml");
                //
                paths.add("src/main/resources/META-INF/restful");
                //
                paths.add("src/main/resources/META-INF/restclient");
                //
                paths.add("src/main/webapp/resource/pages/system");
                for (String path : paths) {
                    String dirDes = StringHelper.pathConcat(tmpRoot, path);
                    File file = new File(dirDes);
                    FileUtils.deleteQuietly(file);
                }
            }
            List<String> zipPaths = new ArrayList<String>();
            {
                for (File f : tmpDir.listFiles()) {
                    zipPaths.add(f.getAbsolutePath());
                }
            }
            String targetDir = StringHelper.pathConcat(projectRoot, "src/main/webapp/resource/baseproject");
            new File(targetDir).mkdirs();
            ZipHelper.zip(zipPaths.toArray(new String[zipPaths.size()]), StringHelper.pathConcat(targetDir, "qdp-simpleauth-4.0.0.zip"), null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (StringUtils.isNotEmpty(tmpRoot)) {
                try {
                    FileUtils.deleteDirectory(new File(tmpRoot));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
