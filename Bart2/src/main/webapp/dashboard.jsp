<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%--<%= request.getAttribute("doctype") %>--%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="edu.cmu.kunt.bart2.Log" %>

<%--
<!DOCTYPE html>--%>
<html>
<body>

<h1>Dashboard for BART web service</h1>
<%--because this results page is accessed by URL so only "get" method is used.--%>
<form action="dashboard" method="get">
    <h2>
        1. Total of null inputs from user is <%=request.getAttribute("nullInputs")%>.<br>
        2.  <%=request.getAttribute("api200")%> out of total
        <%=request.getAttribute("totalLogs")%> logs returned data from 3P API. <br>
        3.  <%=request.getAttribute("api200Pct")%> % of total have 200 status from 3p Api.<br>
        4. The most popular train is No.<%=request.getAttribute("trainNo")%>,
        with <%=request.getAttribute("maxTrainCount")%> times searched.

    </h2>

    <h2>All logs saved in MongoDB</h2>
    <style>
        table, th, td {
            border:1px solid black;
            border-collapse: collapse;
        }
    </style>

    <table style="width:100%">
        <tr>
            <th>Timestamp</th>
            <th>Date</th>
            <th>TrainNo</th>
            <th>Station</th>
            <th>User Input</th>
            <th>API status</th>
            <th>Reply to User</th>
        </tr>
        <% ArrayList list = (ArrayList) request.getAttribute("logs"); %>

        <% for(int i=0;i<list.size();i++){%>
        <tr>
            <%Log log= (Log) list.get(i);%>
            <td><%=log.timestamp%></td>
            <td><%=log.date%></td>
            <td><%=log.trainNo%></td>
            <td><%=log.station%></td>
            <td><%=log.input%></td>
            <td><%=log.from3pApi%></td>
            <td><%=log.toAndroid%></td>

           <%-- <td> <%= list.get(i).toString()%></td>--%>
        </tr>
        <% } %>

    </table>

</form>
</body>
</html>