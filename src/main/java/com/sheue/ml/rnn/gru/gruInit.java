package com.sheue.ml.rnn.gru;

import com.sheue.app.bean.Data;
import com.sheue.app.dao.PriceDAO;
import com.sheue.ml.layers.MatIniter;
import org.jblas.DoubleMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class gruInit {
    private static GRU gru;
    private static int inSize = 4;
    private static int hiddenSize = 4;
    private static int outSize = 3;
    private static int deSize = 3;

    static int rowX = 360;
    static int rowY = 60;

    public static double train_x[][] = new double[rowX][inSize];
    public static double train_y[][] = new double[rowX][outSize];
    public static double test_x[][] = new double[rowY][inSize];
    public static double test_y[][] = new double[rowY][outSize];

    public static void train() {
        double error = 0;
        double num = 0;
        double start = System.currentTimeMillis();
        double rate = 0.1;
        gru = new GRU(inSize, hiddenSize, new MatIniter(MatIniter.Type.Uniform, 0.1, 0, 0), deSize);
        for (int i = 0; i < train_x.length; i++) {
            Map<String, DoubleMatrix> acts = new HashMap<>();
            for (int j = 0; j < num; j++) {
                double newx[][] = new double[1][inSize];
                newx[0] = train_x[j];
                DoubleMatrix xt = new DoubleMatrix(newx);
                acts.put("x" + j, xt);

                gru.active(j, acts);

                DoubleMatrix predictYt = gru.decode(acts.get("h" + j));
                acts.put("py" + j, predictYt);
                double newy[][] = new double[1][outSize];
                newy[0] = train_y[j];
                DoubleMatrix trueYt = new DoubleMatrix(newy);
                acts.put("y" + j, trueYt);

                if (predictYt.argmax() != trueYt.argmax()) {
                    error++;
                }
                num++;
            }
            gru.bptt(acts, train_x.length - 1, rate);
        }
        System.out.println("模型构建完成，用时" + (System.currentTimeMillis() - start) / 1000
                + "s,准确率：" + String.format("%.2f%%", (num - error) / num * 100));
    }

    public static void loadData(String name) {
        List<Data> list = PriceDAO.getTrain(name);
        for (int i = 0; i < list.size(); i++) {
            Data data = list.get(i);
            train_x[i][0] = data.getDate().getYear() - 100;
            train_x[i][1] = data.getDate().getMonth() + 1;
            train_x[i][2] = data.getDate().getDate();
            // 按照价格升降分类置一
            if (i == 0) train_y[i][1] = 1;
            else {
                double oldPrice = list.get(i - 1).getPrice();
                double newPrice = data.getPrice();
                if (newPrice < oldPrice) train_y[i][0] = 1;
                else if (newPrice == oldPrice) train_y[i][1] = 1;
                else if (newPrice > oldPrice) train_y[i][2] = 1;
            }
        }

        list = PriceDAO.getTest(name);
        for (int i = 0; i < list.size(); i++) {
            Data data = list.get(i);
            test_x[i][0] = data.getDate().getYear() - 100;
            test_x[i][1] = data.getDate().getMonth() + 1;
            test_x[i][2] = data.getDate().getDate();
            if (i == 0) train_y[i][1] = 1;
            else {
                double oldPrice = list.get(i - 1).getPrice();
                double newPrice = data.getPrice();
                if (newPrice < oldPrice) test_y[i][0] = 1;
                else if (newPrice == oldPrice) test_y[i][1] = 1;
                else if (newPrice > oldPrice) test_y[i][2] = 1;
            }
        }
    }

    public static void test() {
        double error = 0;
        double num = rowY;
        Map<String, DoubleMatrix> acts = new HashMap<>();
        for (int i = 0; i < num; i++) {
            double newx[][] = new double[1][inSize];
            newx[0] = test_x[i];
            DoubleMatrix xt = new DoubleMatrix(newx);
            acts.put("x" + i, xt);

            gru.active(i, acts);

            DoubleMatrix predictYt = gru.decode(acts.get("h" + i));
            acts.put("py" + i, predictYt);
            double newy[][] = new double[1][outSize];
            newy[0] = test_y[i];
            DoubleMatrix trueYt = new DoubleMatrix(newy);
            acts.put("y" + i, trueYt);
//            System.out.println("predcitmax:" + predcitYt.argmax());
//            System.out.println("truemax:" + trueYt.argmax());

            if (predictYt.argmax() != trueYt.argmax()) {
                error++;
            }
        }
        System.out.println("预测错误数:" + error + "/" + num);
        System.out.println("准确率:" + String.format("%.2f%%", (num - error) / num * 100));
    }

    public static List<Data> getPredict(String name) {
        List<Data> list = new ArrayList<Data>();
        return list;
    }

    public static void init(String name) {
        loadData(name);
        train();
        test();
    }

}
