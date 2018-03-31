<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>用户注册</title>

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
    <h5 id="hint"><%=request.getParameter("hint") == null ? "" : request.getParameter("hint")%>请输入你的账号信息
    </h5>
    <form action="<%=request.getContextPath()%>/MainServlet"
          method="post">
        <p>
            账号:<input class="input" name="id">
        </p>
        <p>
            姓名:<input class="input" name="name">
        </p>
        <p>
            密码:<input class="input" type="password" name="passwd">
        </p>
        <p style="display:none">
            <input name="src" value="signup">
        </p>
        <button class="button" id="sign" type="submit">提交</button>
    </form>
</div>
</body>
</html>