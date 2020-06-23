package com.fengchen.light.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.widget.Toast;

import com.fengchen.light.manager.ThreadManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ===============================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2017/9/27 16:57
 * <p>
 * = 分 类 说 明：
 * ================================================
 */

public class FileUtils {

    /* 应用公共文件夹*/
    public static final String ROOT_NAME = Environment.getExternalStorageDirectory().getPath() ;
    /*照片*/
    public static final String TYPE_PATH_IMAGE = "image";
    /*录像*/
    public static final String TYPE_PATH_VIDEO = "vidoe";
    /*缓存*/
    public static final String TYPE_PATH_CACHE = "cache";

    /* jpg*/
    public static final String MEDIA_TYPE_JPEG = ".jpg";
    /* png*/
    public static final String MEDIA_TYPE_PNG = ".png";
    /*录像*/
    public static final String MEDIA_TYPE_VIDEO = ".mp4";
    /*app安装包*/
    public static final String MEDIA_TYPE_APK = ".apk";

    /**
     * 保存图片
     *
     * @param bitmap
     * @param file
     */
    public static void keepBitmap(Bitmap bitmap, File file) {
        if (bitmap != null && file != null) {//判断是否可以进行写入
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                //更新媒体库
                FCUtils.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://" + file.getPath())));
                Toast.makeText(FCUtils.getContext(), "已保存到:" + file.getPath(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(FCUtils.getContext(), "保存失败", Toast.LENGTH_SHORT).show();
            } finally {
                IOUtils.close(out);
            }
        } else {
            Toast.makeText(FCUtils.getContext(), "保存失败", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * @param type     保存的子路径
     * @param filename 文件名
     * @param media    后缀
     * @return 获取要保存的文件
     */
    public static File getOutputFile(String type, String filename, String media) {
        File dir = new File(ROOT_NAME + File.separator + type);
        // 创建存储目录，如果它不存在
        if (!dir.exists())
            if (!dir.mkdirs())
                return null;
        if (filename == null || media == null)
            return dir;
        return new File(dir.getPath(), filename + media);
    }


    /**
     * @return 根据当前时间获取文件名
     */
    public static String getFileNameForTime() {
        return new SimpleDateFormat("'fengchen'_yyyyMMdd_HHmmss")
                .format(new Date(System.currentTimeMillis()));
    }


    /**
     * 下载文件的通知栏
     *
     * @param urlStr         下载地址
     * @param file           下载文件
     * @param notificationId 通知栏id
     * @param intentType     下载完成点击意图
     * @param runTicker      提示标题
     * @param runTitle       下载时标题
     * @param runText        下载时文字
     * @param onText         下载完文字
     * @param buf            数组大小
     */
    public static void downFile(final String urlStr, final File file, final int notificationId,
                                final String intentType, String runTicker, String runTitle,
                                String runText , final String onText, final byte[] buf) {

        //制造一个通知栏
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(FCUtils.getContext());
        mBuilder.setTicker(runTicker);
        mBuilder.setContentTitle(runTitle)
                .setContentText(runText);
//                .setSmallIcon();
        //获取通知栏管理
        final NotificationManager nm =
                (NotificationManager) FCUtils.getContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);
        //子线程开始
        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //初始化流
                FileOutputStream fos = null;
                InputStream is = null;
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //获取图片大小
                    int fileLength = conn.getContentLength();
                    String fileSize =
                            android.text.format.Formatter.formatFileSize(FCUtils.getContext(), fileLength);

                    is = conn.getInputStream();
                    fos = new FileOutputStream(file);

                    int len = 0, total = 0;

                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        total += len;
                        // 将进度指示器设置为最大值、当前完成百分比和确定状态
                        mBuilder.setContentText("下载中："
                                + android.text.format.Formatter.formatFileSize(FCUtils.getContext(), total)
                                + "/" + fileSize);
                        mBuilder.setProgress(fileLength, total, false);// 更新进度
                        nm.notify(notificationId, mBuilder.build());
                    }
                    fos.flush();
                    // 当循环结束时，更新通知
                    mBuilder.setContentText(onText);
                    //点击意图
                    Intent intent = new Intent(Intent.ACTION_VIEW)
                            .setDataAndType(Uri.parse("file://" + file.getPath()),
                                    intentType);
                    // 这将确保从活动的向后导航导致您的应用程序的主页屏幕.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(FCUtils.getContext());
                    // 将启动活动的意图添加到堆栈的顶部
                    stackBuilder.addNextIntent(intent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    mBuilder.setProgress(0, 0, false);// 删除进度条
                    nm.notify(notificationId, mBuilder.build());

                    //下载完成自动安装
                    if (file.getPath().indexOf(".apk")!=-1) {
                        openApk(file);
                        nm.cancel(notificationId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.close(is);
                    IOUtils.close(fos);
                }
            }
        });
    }

    /*打开apk文件*/
    public static void openApk(File apkFile){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        FCUtils.getContext().startActivity(intent);
    }


    /**
     * 获取缓存路径
     *
     * @param uniqueName
     * @return
     */
    public static File getDiskCacheDir(String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = FCUtils.getContext().getExternalCacheDir().getPath();
        } else {
            cachePath = FCUtils.getContext().getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }


    /*获取缓存大小*/
    public static long getDiskCacheSize(long defaultSize) {
        long size = defaultSize;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            size+=getFolderSize(new File(FCUtils.getContext().getExternalCacheDir().getPath()));
        }

        size+=getFolderSize(new File(FCUtils.getContext().getCacheDir().getPath()));

        return size;
    }

    /*删除缓存*/
    public static void deleteDiskCache() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            deleteFolderFile(FCUtils.getContext().getExternalCacheDir().getPath(),false);
        }
        deleteFolderFile(FCUtils.getContext().getCacheDir().getPath(),false);
    }


    /**
     * 获取文件夹大小
     *
     * @param file File实例
     * @return long
     */
    public static long getFolderSize(File file) {

        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);

                } else {
                    size = size + fileList[i].length();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //return size/1048576;
        return size;
    }

    /**
     * 删除指定目录下文件及目录
     *
     * @param deleteThisPath
     * @return
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 处理目录
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte(s)";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }




}
