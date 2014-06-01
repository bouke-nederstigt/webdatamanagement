<%@ page import="MusicXMLOnline.MusicXmlOnline" %>
<%--
  Created by IntelliJ IDEA.
  User: bouke
  Date: 29/05/2014
  Time: 16:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@include file="header.jsp" %>

<% String doc = request.getParameter("doc"); %>

<div class="player">
    <embed src="<%= request.getContextPath() %>/upload/<%= doc%>.mid" width="200" height="60" autoStart="true" />
</div>

<div class="lyrics">
    <%= MusicXmlOnline.getLyrics(doc) %>
</div>

</div><!-- Eof container -->
</body>
</html>
