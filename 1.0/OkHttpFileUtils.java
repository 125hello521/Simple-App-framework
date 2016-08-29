package com.ejlchina.ejl.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * linshao    lhl_012@163.com
 * 2016年5月26日18:03:34
 * 流转化为为文件
 */
public class OkHttpFileUtils {
    public static boolean inputstream2file(InputStream ins, File file) {
        File fD = new File(file.getParent());
        if (!fD.exists()) {
            fD.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[2048];
            while ((bytesRead = ins.read(buffer, 0, 2048)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
