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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@include file="header.jsp" %>

<div class="documents">

    <%

        if(request.getParameter("searchInput") != null){
            List documents = MusicXmlOnline.searchLyrics(request.getParameter("searchInput"));
            pageContext.setAttribute("documents", documents);
        }else{
            List documents = MusicXmlOnline.getDocuments();
            pageContext.setAttribute("documents", documents);
        }

    %>
    <% if(pageContext.getAttribute("documents") != null){ %>
        <table class="table">
            <th>Movement</th>
            <th>PDF</th>
            <th>Music</th>

            <c:forEach items="${pageScope.documents}" var="doc">
                <tr>
                    <td><%= MusicXmlOnline.getDocTitle((String) pageContext.getAttribute("doc")) %></td>
                    <td><a href="<%= request.getContextPath() %>/upload/<c:out value="${doc}" />.pdf"><c:out value="${doc}"/></a></td>
                    <td><a href="<%= request.getContextPath() %>/player.jsp?doc=<c:out value="${doc}" />">Play music</a></td>
                </tr>
            </c:forEach>
        </table>
    <% }else{ %>
        <p>Your query didn't return any results.</p>
    <% } %>
</div>

</div><!-- Eof container -->
</body>
</html>
