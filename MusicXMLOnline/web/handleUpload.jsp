<%--
  Created by IntelliJ IDEA.
  User: bouke
  Date: 26/05/2014
  Time: 20:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@include file="header.jsp" %>

<div class="message">
    <div class="message">
        <%= request.getAttribute("message") %><br />
        <%= request.getAttribute("pathToLy")%><br/>
        <%= request.getAttribute("createdOutput")%>
    </div>
</div>

</div><!-- Eof container -->
</body>
</html>