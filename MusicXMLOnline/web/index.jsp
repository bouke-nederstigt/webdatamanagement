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
    <% List documents = MusicXmlOnline.getDocuments(); %>
    <table class="table">
        <th>
            <td>Movement</td>
            <td>PDF</td>
            <td>Music</td>
        </th>
        <%= documents %>
        <c:forEach items="${documents}" var="title">
            <tr>
                <td><c:out value="${title}" /></td>
                <td>link to pdf</td>
                <td>Link to midi</td>
            </tr>
        </c:forEach>
    </table>
</div>

</div><!-- Eof container -->
</body>
</html>
