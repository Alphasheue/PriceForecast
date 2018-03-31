package com.sheue.app.utils;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class LineChartUtil {

    public static JFreeChart createLineChart(String title, String rowTitle, String coloumTitle, DefaultCategoryDataset dataset) {
        //创建主题样式
        StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
        //设置标题字体
        standardChartTheme.setExtraLargeFont(new Font("微软雅黑", Font.PLAIN, 30));
        //设置图例的字体
        standardChartTheme.setRegularFont(new Font("微软雅黑", Font.PLAIN, 20));
        //设置轴向的字体
        standardChartTheme.setLargeFont(new Font("微软雅黑", Font.PLAIN, 20));
        //应用主题样式
        ChartFactory.setChartTheme(standardChartTheme);
        return ChartFactory.createLineChart(title, rowTitle, coloumTitle,
                dataset, PlotOrientation.VERTICAL, true, true, true);
    }

    public static void draw(String path, JFreeChart chart, int width, int height) {
        File photo = new File(path);
        try {
            ChartUtilities.saveChartAsJPEG(photo, chart, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(15, "schools", "1970");
        dataset.addValue(30, "schools", "1980");
        dataset.addValue(60, "schools", "1990");
        dataset.addValue(120, "schools", "2000");
        dataset.addValue(240, "schools", "2010");
        dataset.addValue(300, "schools", "2014");

        JFreeChart lineChart = LineChartUtil.createLineChart("错误率", "测试次数", "错误数", dataset);
        LineChartUtil.draw("./data/LineChart.jpg", lineChart, 1280, 720);
    }
}