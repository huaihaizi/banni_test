/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.hanwin.product.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Message;
import android.util.JsonWriter;
import android.util.Log;

import com.hanwin.product.tencentim.bean.TranslateWordBean;
import com.hanwin.product.utils.ImageCompressUtils.Luban;
import com.hanwin.product.utils.ImageCompressUtils.OnCompressListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;

public class FileUtil {
    private File idCardDir;
    private static File newFile1;

    public FileUtil() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File external = Environment.getExternalStorageDirectory();
            String rootDir = "/" + "tender";
            idCardDir = new File(external, rootDir + "/crop");
            if (!idCardDir.exists()) {
                idCardDir.mkdirs();
            }
        }
    }

    public static File getSaveFile(Context context) {
        File file = new File(context.getFilesDir(), "pic.jpg");
        return file;
    }

    public File createCropFile(String filename) {
        String fileName = "";
        if (idCardDir != null) {
            fileName = filename + ".png";
        }
        return new File(idCardDir, fileName);
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }


    /**
     * 压缩图片
     *
     * @param file
     */
    public static File compressImage(Context context, final File file) {
        Log.e("原图file", "===========" + file.getAbsolutePath().toString());
        try {
            long a = FileUtil.getFileSize(file);
            Log.e("原图file大小：", "===========" + FileUtil.FormetFileSize(a));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Luban.get(context).load(file) //传要压缩的图片
                .putGear(Luban.THIRD_GEAR)//设定压缩档次，默认三挡
                .setFilename(System.currentTimeMillis() + ".jpg")//设置压缩后图片的名字
                .launch(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        // 压缩开始前调用，可以在方法内启动 loading UI
                    }

                    @Override
                    public void onSuccess(File newfile) {
                        // 压缩成功后调用，返回压缩后的图片文件
                        Log.e("newfile", "===========" + newfile.getAbsolutePath().toString());
                        try {
                            newFile1 = newfile;
                            long a = FileUtil.getFileSize(newfile);
                            Log.e("newfile大小：", "===========" + FileUtil.FormetFileSize(a));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
        return newFile1;
    }

    /**
     * 写数据
     *
     * @param str
     * @param fileName
     */
    public static void write2File(String str, String fileName) {
        try {
            //判断实际是否有SD卡，且应用程序是否有读写SD卡的能力，有则返回true
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                // 获取SD卡的目录
                File sdCardDir = Environment.getExternalStorageDirectory();
                String path = "/banniBank/";
                File dir = new File(sdCardDir + path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File targetFile = new File(sdCardDir.getCanonicalPath() + path + fileName + ".txt");
                //使用RandomAccessFile是在原有的文件基础之上追加内容，
                //而使用outputstream则是要先清空内容再写入
                RandomAccessFile raf = new RandomAccessFile(targetFile, "rw");
                //光标移到原始文件最后，再执行写入
                raf.seek(targetFile.length());
                raf.write(str.getBytes());
                raf.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 写JSON数据到json文件
     *
     * @param translateWordBean
     * @param fileName
     */
    public static void writejSON2File(TranslateWordBean translateWordBean, String fileName) {
        try {
            //判断实际是否有SD卡，且应用程序是否有读写SD卡的能力，有则返回true
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                // 获取SD卡的目录
                File sdCardDir = Environment.getExternalStorageDirectory();
                String path = "/banni/";
                File dir = new File(sdCardDir + path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File targetFile = new File(sdCardDir.getCanonicalPath() + path + fileName + ".json");
                //获取json文件
                FileOutputStream fos = new FileOutputStream(targetFile);
                Contants.list.add(translateWordBean);
                //创建JsonWrite对象
                JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, "utf-8"));
                writer.setIndent("    ");
                writer.beginArray();
                for (TranslateWordBean trans : Contants.list) {
                    writer.beginObject();
                    writer.name("videoStartTime").value(trans.getVideoStartTime());
                    writer.name("startTime").value(trans.getStartTime());
                    writer.name("endTime").value(trans.getEndTime());
                    writer.name("name").value(trans.getName());
                    writer.name("content").value(trans.getContent());
                    writer.endObject();
                }
                writer.endArray();
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据byte数组，生成文件
     */
    public static void getFile(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File sdCardDir = Environment.getExternalStorageDirectory();
        String path = "/banni/";
        File file = null;
        try {
            File dir = new File(sdCardDir + path);
            if (!dir.exists()) {//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(sdCardDir.getCanonicalPath() + path + fileName);

            /* 使用以下2行代码时，不追加方式*/
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(bfile);

            /* 使用以下3行代码时，追加方式*/
//            bos = new BufferedOutputStream(new FileOutputStream(file, true));
//            bos.write(bfile);

            bos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


    /**
     * 根据byte数组，生成文件
     */
    public static void getFile1(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File sdCardDir = Environment.getExternalStorageDirectory();
        String path = "/banni/";
        File file = null;
        try {
            File dir = new File(sdCardDir + path);
            if (!dir.exists()) {//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(sdCardDir.getCanonicalPath() + path + "99999");

            /* 使用以下2行代码时，不追加方式*/
//            bos = new BufferedOutputStream(new FileOutputStream(file));
//            bos.write(bfile);
            /* 使用以下3行代码时，追加方式*/
            bos = new BufferedOutputStream(new FileOutputStream(file, true));
            bos.write(bfile);

            bos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


    /**
     * @param data1 
     * @param data2 
     * @return data1 与 data2拼接的结果  
     */
    public static byte[] addBytes(byte[] data1, byte[] data2) {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;
    }


    public static String getAdtoSdPath(){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
        return path;
    }

}
