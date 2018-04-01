<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="com.sheue.app.bean.Data" %>
<%@ page import="com.sheue.app.dao.PriceDAO" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>大数据分析与预测网站</title>

    <style type="text/css">
        body {
            align: center;
            text-align: center;
            font-family: "Microsoft YaHei";
            color: #555;
        }

        .table {
            margin: auto;
            padding: 5px
        }

        .button {
            width: 100px;
            height: 30px
        }

        .black_overlay {
            display: none;
            position: absolute;
            top: 0%;
            left: 0%;
            width: 100%;
            height: 100%;
            background-color: black;
            z-index: 1001;
            -moz-opacity: 0.8;
            opacity: .80;
            filter: alpha(opacity=88);
        }

        .white_content {
            display: none;
            position: absolute;
            top: 25%;
            left: 25%;
            width: 55%;
            height: 55%;
            padding: 20px;
            border: 4px black;
            background-color: white;
            z-index: 1002;
            overflow: auto;
        }
    </style>

</head>

<body>
<div class="example">
    <jsp:include page="Head.jsp"></jsp:include>
    <form style="vertical-align:middle" action="/MainServlet" method="post">
        <p style="display:none">
            <input name="src" value="find">
        </p>
        <p>
            <input type="text" name="findWord" style="width:200px;height:30px">
            <button class="button" type="submit">搜索</button>
        </p>
    </form>
    <form style="vertical-align:middle" action="/MainServlet" method="post">
        <p style="display:none">
            <input name="src" value="predict">
        </p>
        <p>
            <%--<a href="javascript:void(0)"--%>
            <%--onclick="document.getElementById('light').style.display='block';document.getElementById('fade').style.display='block'">--%>
            <button class="button" type="submit" onclick="alert('温馨提示：模型构建需1~2分钟时间，请耐心等待！')">预测</button>
            <%--</a>--%>
        </p>
    </form>

        <div id="light" class="white_content">温馨提示：模型构建需3~5分钟时间，请耐心等待！
        <button onclick="document.getElementById('light').style.display='none';document.getElementById('fade').style.display='none'">
            点这里关闭本窗口
        </button>
    </div>
    <div id="fade" class="black_overlay"></div>

    <h3>蔬菜与水果价格：</h3>

    <div>
        <table class="table" border="1">
            <thead>
            <tr>
                <th>名字</th>
                <th>均价（元/公斤）</th>
                <th>规格</th>
                <th>日期</th>
            </tr>
            </thead>
            <tbody>
            <%
                List<Data> list = new ArrayList<Data>();
                int currentPage;
                if (request.getParameter("currentPage") == null) {
                    currentPage = 1;
                } else {
                    currentPage = Integer.parseInt(String.valueOf(request.getParameter("currentPage")));
                }
                int total;
                String fruitName = "";
                if (request.getSession().getAttribute("findWord") != null) {
                    fruitName = String.valueOf(request.getParameter("findWord"));
                    list = PriceDAO.getPage(fruitName, currentPage);
                    total = PriceDAO.getTotal(fruitName);
                } else {
                    list = PriceDAO.getPage(currentPage);
                    total = PriceDAO.getTotal();
                }
                int totalPage = (total + 9) / 10;

                for (Data data : list) {
            %>
            <tr>
                <td><%=data.getName()%>
                </td>
                <td><%=data.getPrice()%>
                </td>
                <td><%=data.getStandard()%>
                </td>
                <td><%=new SimpleDateFormat("yy-MM-dd").format(data.getDate())%>
                </td>
            </tr>
            <%
                }
            %>
            </tbody>
            <div>
                <span>共<%=total%>条记录</span>
                <% if (currentPage == 1) { %>
                <span>上一页</span>
                <% } else { %>
                <a href="index.jsp?currentPage=<%=currentPage - 1%>&findWord=<%=fruitName%>">上一页</a>
                <a href="index.jsp?currentPage=1&findWord=<%=fruitName%>">1</a>
                <% } %>
                <span> 第<%=currentPage%>页</span>
                <% if (currentPage == totalPage) { %>
                <span>下一页</span>
                <% } else { %>
                <a href="index.jsp?currentPage=<%=totalPage%>&findWord=<%=fruitName%>"><%=totalPage%>
                </a>
                <a href="index.jsp?currentPage=<%=currentPage + 1%>&findWord=<%=fruitName%>">下一页</a>
                <% } %>
            </div>
        </table>
    </div>

</body>
</html>