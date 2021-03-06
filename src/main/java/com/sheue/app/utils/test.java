package com.sheue.app.utils;

import com.sheue.app.bean.Data;
import com.sheue.app.dao.PriceDAO;
import com.sheue.ml.rnn.gru.GRUPredict;
import com.sheue.ml.rnn.lstm.LSTMPredict;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultXYDataset;

import java.util.List;

public class test {

    private static void price() {
        String[] nameList = {"春菜", "苹果", "西兰花"};
        for (int i = 0; i < nameList.length; i++) {
            List<Data> list = PriceDAO.getTrain(nameList[i]);
            DefaultXYDataset dataset = new DefaultXYDataset();
            double[][] set = new double[2][list.size()];
            for (int j = 0; j < list.size(); j++) {
                set[0][j] = j + 1;
                set[1][j] = list.get(j).getPrice();
            }
            dataset.addSeries("价格", set);
            JFreeChart freeChart = LineChartUtil.createLineChart(nameList[i] + "价格趋势图", "样本个数", "价格", dataset);
            LineChartUtil.draw("./data/" + nameList[i] + "趋势.jpeg", freeChart, 1280, 720);
        }

    }

    private static void changeLr() {
        String[] nameList = {"春菜", "苹果", "西兰花"};
        double[] lrList = {0.5};
        double acc = 0;
        for (int i = 0; i < nameList.length; i++) {
            String name = nameList[i];
            for (int j = 0; j < lrList.length; j++) {
                double lr = lrList[j];
                DefaultXYDataset dataset = new DefaultXYDataset();
                LSTMPredict lstm = new LSTMPredict().init(name, lr, acc, dataset);
                GRUPredict gru = new GRUPredict().init(name, lr, acc, dataset);

                JFreeChart freeChart = LineChartUtil.createLineChart(name + "收敛速率，lr=" + lr, "训练次数", "错误数", dataset);
                LineChartUtil.draw("./data/" + name + "lr" + lr + ".jpeg", freeChart, 1280, 720);
            }
        }

    }

    public static void main(String[] args) {
        changeLr();
    }
}
