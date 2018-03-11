<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
    <link href="//netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">
    <script src="//code.jquery.com/jquery-1.11.1.min.js"></script>
    <script src="//netdna.bootstrapcdn.com/bootstrap/3.0.0/js/bootstrap.min.js"></script>
    <link href="css/timetable.css" rel="stylesheet" />
    <!------ Include the above in your HEAD tag ---------->
</head>
<body>
    <div class="container">

        <!-- begin snippet: js hide: false -->

        <!-- language: lang-css -->

        <div class="container">
            <div class="row">
                <div class="col-md-12">
                    <h2 class="text-center">
                        Timetable
                    </h2>
                </div>
                <c:forEach var="key" items="${timetableDTO.tables.keySet()}">
                <div id="no-more-tables">
                    <table class="col-sm-12 table table-bordered table-striped table-condensed cf">
                        <thead class="cf">
                        <tr>
                            <th>Группа: ${key}</th>
                            <c:forEach var="day" items="${timetableDTO.daysOfWeek}">
                                <th>${day}</th>
                            </c:forEach>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="cells" items="${timetableDTO.tables.get(key)}" varStatus="loop">
                            <tr>
                                <td>${timetableDTO.timeslots[loop.index]}</td>
                                <c:forEach var="cell" items="${cells}">
                                    <td>${cell}</td>
                                </c:forEach>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
                </c:forEach>
            </div>
        </div>


    </div>
</body>
</html>