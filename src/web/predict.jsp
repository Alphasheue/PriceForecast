<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>预测数据图</title>

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

    </style>

</head>

<body>
<jsp:include page="Head.jsp"></jsp:include>
<h1>预测数据图</h1>
<div>${image}</div>
<hr/>

<h3>未来十天的价格预测</h3>
<%
    List<String> predictList = (List<String>) request.getAttribute("predictList");
    if (predictList != null && predictList.size() != 0) {
%>
<table class="table" border="1">
    <thead>
    <tr>
        <th style="padding: 20px">预测数据</th>
        <%
            for (int i = 1; i <= predictList.size(); i++) {
        %>
        <th style="padding: 20px"><%=i%>
        </th>
        <%
            }
        %>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td style="padding: 20px">预测值</td>
        <%
            for (String result : predictList) {
        %>
        <td style="padding: 20px"><%=result%>
        </td>
        <%
            }
        %>
    </tr>
    </tbody>
</table>
<%
    }
%>
</body>
</html>