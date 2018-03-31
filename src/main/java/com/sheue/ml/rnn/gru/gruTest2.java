package com.sheue.ml.rnn.gru;

import com.sheue.ml.layers.MatIniter;
import org.jblas.DoubleMatrix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class gruTest2 {
    public static double train_x[][] = new double[105][4];
    public static double test_x[][] = new double[45][4];
    public static double train_y[][] = new double[105][3];
    public static double test_y[][] = new double[45][3];
    private static GRU gru;

    public static void main(String[] args) {
        loadData();
        int hiddenSize = 4;//隐含层数量
        double lr = 0.1;
        gru = new GRU(4, hiddenSize, new MatIniter(MatIniter.Type.Uniform, 0.1, 0, 0), 3);//4是输入层，3是输出层
        for (int i = 0; i < 400; i++) {
            double error = 0;
            double num = 0;
            double start = System.currentTimeMillis();
            Map<String, DoubleMatrix> acts = new HashMap<>();
            for (int s = 0; s < train_x.length; s++) {
                double newx[][] = new double[1][4];
                newx[0] = train_x[s];
                DoubleMatrix xt = new DoubleMatrix(newx);//获取字的矩阵
                //System.out.println(xt.getColumns()+" "+xt.getRows());
                acts.put("x" + s, xt);

                gru.active(s, acts);

                DoubleMatrix predcitYt = gru.decode(acts.get("h" + s));
                acts.put("py" + s, predcitYt);
                double newy[][] = new double[1][3];
                newy[0] = train_y[s];
                DoubleMatrix trueYt = new DoubleMatrix(newy);
                acts.put("y" + s, trueYt);

                System.out.println("predcitYtmax:" + predcitYt.argmax());
                System.out.println("trueYtmax:" + trueYt.argmax());
                if (predcitYt.argmax() != trueYt.argmax())
                    error++;

                // bptt
                num++;
            }
            gru.bptt(acts, train_x.length - 1, lr);
//            System.out.println("Iter = " + i + ", error = " + error / num + ", time = " + (System.currentTimeMillis() - start) / 1000 + "s");
        }//结束迭代

        //开始测试
        int num = 0, error = 0;
        Map<String, DoubleMatrix> acts = new HashMap<>();
        for (int s = 0; s < test_x.length; s++) {
            double newx[][] = new double[1][4];
            newx[0] = test_x[s];
            DoubleMatrix xt = new DoubleMatrix(newx);
            acts.put("x" + s, xt);

            gru.active(s, acts);

            DoubleMatrix predcitYt = gru.decode(acts.get("h" + s));
            acts.put("py" + s, predcitYt);
            double newy[][] = new double[1][3];
            newy[0] = test_y[s];
            DoubleMatrix trueYt = new DoubleMatrix(newy);
            acts.put("y" + s, trueYt);
            if (predcitYt.argmax() != trueYt.argmax())
                error++;
            // bptt
            num++;
        }
        System.out.println("错误数:" + error + "/" + num);
    }

    public static void loadData() {
        List<String> list = readFileForList("data/train.txt");//训练集
        for (int i = 0; i < list.size(); i++) {
            String str[] = list.get(i).split(",");
            for (int k = 0; k < 4; k++)
                train_x[i][k] = Double.valueOf(str[k]);
            train_y[i][Integer.valueOf(str[4])] = 1;//将所属类别设置为1
        }
        list = readFileForList("data/test.txt");//测试集
        for (int i = 0; i < list.size(); i++) {
            String str[] = list.get(i).split(",");
            for (int k = 0; k < 4; k++)
                test_x[i][k] = Double.valueOf(str[k]);
            test_y[i][Integer.valueOf(str[4])] = 1;
        }
    }

    public static List<String> readFileForList(String fileName) {//读取文件到list
        File file = new File(fileName);
        BufferedReader reader = null;
        List<String> s = new ArrayList<String>();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                s.add(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
            return s;
        }
    }


}
