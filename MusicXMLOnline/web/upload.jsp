<%--
  Created by IntelliJ IDEA.
  User: bouke
  Date: 26/05/2014
  Time: 18:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="header.jsp" %>

<h1>Upload a music xml file (.xml)</h1>

<form method="post" action="uploadServlet" enctype="multipart/form-data">
    <div class="input-group">
        <input type="file" name="musicXmlFile" class="form-control"/>
    </div>
    <input type="submit" value="Upload" class="btn btn-default"/>
</form>

</body>
</html>
