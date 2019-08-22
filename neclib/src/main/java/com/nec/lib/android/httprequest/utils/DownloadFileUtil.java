package com.nec.lib.android.httprequest.utils;

import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


/**
 * 文件下载工具类
 */
public class DownloadFileUtil {


    public static File getFileFromUrl(String url, String savePath) throws IOException {
        savePath = savePath==null || savePath.isEmpty() ? "/Download" : (savePath.startsWith("/") ? savePath : "/"+savePath);
        String saveDirPath = Environment.getExternalStorageDirectory().getAbsoluteFile() + savePath;
        //SDCard的状态
        String state = Environment.getExternalStorageState();
        //判断SDCard是否挂载上
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Toast.makeText(AppContextUtil.getContext(), "存储不可用", Toast.LENGTH_SHORT).show();
            return null;
        }
        saveDirPath = isExistDir(saveDirPath);
        return new File(saveDirPath, getNameFromUrl(url));
    }

//    public static File getFileFromUrl(String url) throws IOException {
//        return getFileFromUrl(url, null);
//    }

    public static String getDownloadPath(String url, String savePath) throws IOException {
        File file = getFileFromUrl(url, savePath);
        return Objects.requireNonNull(file).getAbsolutePath();
    }

    private static String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    private static String isExistDir(String saveDir) throws IOException {
        File downloadFile = new File(saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        return downloadFile.getAbsolutePath();
    }

}
