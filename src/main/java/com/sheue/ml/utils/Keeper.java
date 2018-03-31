package com.sheue.ml.utils;

import java.io.*;

public class Keeper {

    /**
     * 往文件中写入并覆盖原模型
     *
     * @param path
     * @param model
     */
    public static void saveModel(String path, Object model) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(model);

            byte[] data = bos.toByteArray();

            bos.close();
            oos.close();
            os.write(data, 0, data.length);
            os.flush(); //将存储在管道中的数据强制刷新出去
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 从文件中读取已建立好的模型
     *
     * @param path
     */
    public static Object getModel(String path) {
        byte[] model;
        BufferedInputStream bis = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(path));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            int len = bis.read();
            System.out.println("len:" + len);
            byte[] buf = new byte[1024 * 1024]; // 分配1MB大小

            while ((len = bis.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            model = bos.toByteArray();

            bos.close();
            return model;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return null;
    }
}
