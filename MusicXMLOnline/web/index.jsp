<%@ page import="java.util.ArrayList" %>
<%@ page import="MusicXMLOnline.MusicXmlOnline" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: bouke
  Date: 25/05/2014
  Time: 17:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@include file="header.jsp" %>

<div class="message">
    <% List message = MusicXmlOnline.getMessage(); %>
    <div class="message">
        <%= message.get(0) %>
    </div>
</div>

</div><!-- Eof container -->
</body>
</html>
