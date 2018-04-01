package com.sheue.app.controller;

import com.sheue.app.bean.User;
import com.sheue.app.dao.UserDAO;
import com.sheue.app.utils.LineChartUtil;
import com.sheue.ml.rnn.gru.GRUPredict;
import com.sheue.ml.rnn.lstm.LSTMPredict;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
            case "signup": {
                String result = SignUp(request);
                if (result.equals("ok")) {
                    request.getRequestDispatcher("/Login.jsp").forward(request, response);
                } else if (result.equals("用户名已被注册！")) {
                    request.setAttribute("hint", result);
                    request.getRequestDispatcher("/SignUp.jsp").forward(request, response);
                }
                break;
            }
            case "login": {
                String result = login(request);
                if (result.equals("ok")) {
                    System.out.print("密码正确");
                    addCookie(request.getParameter("account"), request.getParameter("name"), request.getParameter("passwd"),
                            response, request);
                    request.getSession().setAttribute("account", request.getParameter("account"));
                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                } else if (result.equals("wrong")) {
                    System.out.print("密码错误");
                    request.getRequestDispatcher("/Login.jsp").forward(request, response);
                }
                break;
            }
            case "find":
                if (request.getParameter("findWord") != null) {
                    String findWord = request.getParameter("findWord");
                    request.getSession().setAttribute("findWord", findWord);
                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                }
                break;
            case "predict":
                String image = predict(request);
                if (image == null || image.length() == 0) {
                    request.getRequestDispatcher("index.jsp").forward(request, response);
                } else {
                    request.setAttribute("image", image);
                    request.getRequestDispatcher("/predict.jsp").forward(request, response);
                }
                break;
        }
    }

    public void addCookie(String acc, String name, String passwd, HttpServletResponse response,
                          HttpServletRequest request) {
        try {
            if (acc == null) {
                System.out.println("kongle");
            }
            Cookie myCook1 = new Cookie("acc", URLEncoder.encode(acc, "utf-8"));
            Cookie myCook2 = new Cookie("name", URLEncoder.encode(name, "utf-8"));
            Cookie myCook3 = new Cookie("password", URLEncoder.encode(passwd, "utf-8"));
            myCook1.setMaxAge(60 * 60 * 2 * 365);
            response.addCookie(myCook1);
            myCook2.setMaxAge(60 * 60 * 2 * 365);
            response.addCookie(myCook2);
            myCook3.setMaxAge(60 * 60 * 2 * 365);
            response.addCookie(myCook3);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String SignUp(HttpServletRequest request) {
        User u = new User();

        u.setAccount(request.getParameter("id"));
        u.setName(request.getParameter("name"));
        u.setPassword(request.getParameter("passwd"));

        String result = UserDAO.has(u);
        if (result.equals("empty")) {
            UserDAO.add(u);
        } else {
            result = "用户名已被注册！";
        }

        return result;
    }

    public String login(HttpServletRequest request) {
        User u = new User();

        u.setAccount(request.getParameter("account"));
        u.setPassword(request.getParameter("passwd"));

        String result = UserDAO.has(u);

        return result;
    }

    public String predict(HttpServletRequest request) {
        String findWord;
        if (request.getSession().getAttribute("findWord") != null) {
            findWord = String.valueOf(request.getSession().getAttribute("findWord"));
            request.getSession().invalidate();
        } else {
            return null;
        }
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        LSTMPredict lstmPredict = LSTMPredict.init(findWord, 1, 0.5, dataset);
        request.setAttribute("lstmPredictList", lstmPredict.predict(findWord, 10));
        GRUPredict gruPredict = GRUPredict.init(findWord, 1, 0.33, dataset);
        request.setAttribute("gruPredictList", gruPredict.predict(findWord, 10));

        JFreeChart chart = LineChartUtil.createLineChart("测试结果", "次数", "价格", dataset);

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
//        System.out.println("fileName:" + filename);
//        System.out.println("graphURL:" + graphURL);
//        System.out.println("image:" + image);

        return image;
    }

}
