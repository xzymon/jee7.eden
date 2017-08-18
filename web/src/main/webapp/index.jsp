<%--
  Created by IntelliJ IDEA.
  User: root
  Date: 15.08.17
  Time: 22:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>$Title$</title>
  </head>
  <body>
  <jsp:useBean id="FeederBean" scope="request" class="com.xzymon.jee7.eden.web.managed.FeederBean"/>
  <h1><jsp:getProperty name="FeederBean" property="message"/></h1>
  ${requestScope.FeederBean.message}
  $END$
  </body>
</html>
