package com.ihewro.focus.util;

import com.blankj.ALog;
import com.ihewro.focus.callback.FileOperationCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/19
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class FileUtil {

    /***
     * 删除文件夹
     *
     * @param folderPath 文件夹完整绝对路径
     */
    public  static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            File myFilePath = new File(folderPath);
            ALog.d("删除后",myFilePath.list().length);
            myFilePath.delete(); // 删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /***
     * 删除指定文件夹下所有文件
     *
     * @param path 文件夹完整绝对路径
     * @return
     */
    public static  boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                String tempPath = temp.getAbsolutePath();
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);// 再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }


    /**
     * 从图片库中删除图片
     */
    public static void deleteImageFromGallery(String file){
        File file1 = new File(file);
        if (file1.exists() || file != ""){
            new File(file).delete();
        }
    }

    public static boolean copyFileToTarget(String origin, String target, FileOperationCallback callback){
        if (Objects.equals(origin,target)){
            copyFileToTarget(origin,origin+"copy",callback);
            deleteImageFromGallery(origin);
            copyFileToTarget(origin+"copy",target,callback);
            deleteImageFromGallery(origin+"copy");
            return true;
        }else {
            return copyFileToTarget(new File(origin),new File(target),callback);
        }
    }

    private static boolean copyFileToTarget(File source, File target, FileOperationCallback callback){
        boolean flag;
        if (Objects.equals(source.getAbsolutePath(), target.getAbsolutePath())){
            flag =  true;
        }else {
            ALog.d("源文件" + source.getAbsolutePath() + "目标文件"+ target.getAbsolutePath());
            File fileParent = target.getParentFile();
            if(!fileParent.exists()){
                fileParent.mkdirs();
                File fileTwoParent = fileParent.getParentFile();
                if (!fileTwoParent.exists()){
                    fileTwoParent.mkdir();
                }
            }
            FileInputStream fileInputStream = null;
            FileOutputStream fileOutputStream = null;
            try {
                fileInputStream = new FileInputStream(source);
                fileOutputStream = new FileOutputStream(target);
                byte[] buffer = new byte[1024];
                while (fileInputStream.read(buffer) > 0) {
                    fileOutputStream.write(buffer);
                }
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
                flag = false;
            } finally {
                try {
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        callback.onFinish();
        return flag;
    }
}
