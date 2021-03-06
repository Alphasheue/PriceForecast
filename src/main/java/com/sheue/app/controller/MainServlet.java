package com.sheue.app.controller;

import com.sheue.app.utils.LineChartUtil;
import com.sheue.ml.rnn.gru.GRUPredict;
import com.sheue.ml.rnn.lstm.LSTMPredict;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.data.xy.DefaultXYDataset;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/MainServlet")
public class MainServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        String src = request.getParameter("src");
        switch (src) {
            case "find":
                if (request.getParameter("findWord") != null) {
                    String findWord = request.getParameter("findWord");
                    request.getSession().setAttribute("findWord", findWord);
                }
                request.getRequestDispatcher("index.jsp").forward(request, response);
                break;
            case "predict":
                String image = predict(request);
                if (image == null || image.length() == 0) {
                    request.getRequestDispatcher("index.jsp").forward(request, response);
                } else {
                    request.setAttribute("image", image);
                    request.getRequestDispatcher("predict.jsp").forward(request, response);
                }
                break;
        }
    }

    public String predict(HttpServletRequest request) {
        String itemName;
        if (request.getSession().getAttribute("findWord") != null) {
            itemName = String.valueOf(request.getSession().getAttribute("findWord"));
            request.getSession().invalidate();
        } else {
            return null;
        }
        DefaultXYDataset dataset = new DefaultXYDataset();
        LSTMPredict lstmPredict = new LSTMPredict().init(itemName, 0.5, 0.5, dataset);
        request.setAttribute("lstmPredictList", lstmPredict.predict(itemName, 10));
        request.setAttribute("lstmAccuracy", lstmPredict.getAccuracy());
        GRUPredict gruPredict = new GRUPredict().init(itemName, 1, 0.33, dataset);
        request.setAttribute("gruPredictList", gruPredict.predict(itemName, 10));
        request.setAttribute("gruAccuracy", gruPredict.getAccuracy());

        JFreeChart chart = LineChartUtil.createLineChart("测试结果", "次数", "价格", dataset);
//        LineChartUtil.draw("./data/" + itemName + ".jpeg", chart, 1280, 720);
        request.setAttribute("itemName", itemName);
        //保存图片 返回图片文件名
        String filename = null;
        try {
            filename = ServletUtilities.saveChartAsJPEG(chart, 1280, 720, request.getSession());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //获取图片路径（内存中）
        String graphURL = request.getContextPath() + "/DisplayChart?filename=" + filename;
        //拼接
        String image = "<img src='"
                + graphURL
                + "' width=1280 height=720 border=0 usemap='#"
                + filename + "'/>";

        return image;
    }

}
