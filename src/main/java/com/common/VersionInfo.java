package com.common;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/*******************************************************************************
 * @date 2018-11-30 下午 4:23
 * @author: <a href=mailto:>黄跃然</a>
 * @Description: 版本信息
 ******************************************************************************/
public class VersionInfo {

    private static String VERSION = "version:";
    private static String DATATIME = "datetime:";

    private static String versionInfo;

    private static String version_str = "";
    private static String date_time_str = "";
    private static String svn_version_str = "";

    static {
        try {
            versionInfo = getVersionInfo();

            if (versionInfo != null) {
                versionInfo = versionInfo.replace(" ", "");
                versionInfo = versionInfo.replaceAll("\n", "");
                Pattern version = Pattern.compile(VERSION);
                Pattern datetime = Pattern.compile(DATATIME);
                Matcher version_matcher = version.matcher(versionInfo);
                Matcher datetime_matcher = datetime.matcher(versionInfo);

                if (version_matcher.find() && datetime_matcher.find()) {
                    version_str = versionInfo.substring(version_matcher.end(), datetime_matcher.start());
                    date_time_str = versionInfo.substring(datetime_matcher.end());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get browser version
     *
     * @return
     */
    public static String getProjectVersion() {
        return !version_str.isEmpty() ? version_str : "Unknown";
    }

    /**
     * get compiled time
     *
     * @return
     */
    public static String getCompiledTime() {
        return !date_time_str.isEmpty() ? date_time_str : "Unknown";
    }

    private static String getVersionInfo() throws IOException {
        URL sourcePath = VersionInfo.class.getProtectionDomain().getCodeSource().getLocation();
        ZipFile zipFile = new ZipFile(sourcePath.getPath());
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        for (; entries.hasMoreElements(); ) {
            ZipEntry zipEntry = entries.nextElement();
            String filename = zipEntry.getName();
            if (filename.startsWith("ver_") && filename.endsWith(".txt")) {
                InputStream resourceAsStream = VersionInfo.class.getClassLoader().getResourceAsStream(filename);
                InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream,"gbk");
                return IOUtils.toString(inputStreamReader);
            }
        }
        return null;
    }

    /**
     * 读取 InputStream 到 String字符串中
     */
    public static String readStream(InputStream in) {
        try {
            //<1>创建字节数组输出流，用来输出读取到的内容
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //<2>创建缓存大小
            byte[] buffer = new byte[1024]; // 1KB
            //每次读取到内容的长度
            int len = -1;
            //<3>开始读取输入流中的内容
            while ((len = in.read(buffer)) != -1) { //当等于-1说明没有数据可以读取了
                baos.write(buffer, 0, len);   //把读取到的内容写到输出流中
            }
            //<4> 把字节数组转换为字符串
            String content = baos.toString();
            //<5>关闭输入流和输出流
            in.close();
            baos.close();
            //<6>返回字符串结果
            return content;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

}
