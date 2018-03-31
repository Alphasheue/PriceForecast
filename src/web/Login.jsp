<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>登录</title>

    <style type="text/css">
        .input {
            width: 250px;
            height: 25px
        }

        .button {
            width: 100px;
            height: 30px
        }
    </style>

</head>

<body>
<div class="example">
    <jsp:include page="Head.jsp"></jsp:include>
    <%
        String account = request.getParameter("id") == null ? "" : request.getParameter("id");
        String passwd = request.getParameter("passwd") == null ? "" : request.getParameter("passwd");
    %>
    <form action="<%=request.getContextPath()%>/MainServlet"
          method="post">
        <p>
            账号:<input class="input" name="account" value="<%=account%>">
        </p>
        <p style="display:none">
            姓名:<input class="input" name="name"
                      value="<%=request.getParameter("name")%>">
        </p>
        <p>
            密码:<input class="input" type="password" name="passwd"
                      value="<%=passwd%>">
        </p>
        <p style="display:none">
            <input name="src" value="login">
        </p>
        <button class="button" id="sign" type="submit">登录</button>
    </form>
</div>
</body>
</html>