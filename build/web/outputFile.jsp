<%@page import="model.WekaClass"%>
<%@page import="mypackage.Paket"%>
<%@page import="java.util.ArrayList"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>

<html lang="en"
	xmlns:ui="http://java.sun.com/jsf/facelets">
    <head>
         <link rel="stylesheet" href="css/metro-bootstrap.css">
         <link href="css/iconFont.css" rel="stylesheet">
        <script src="js/jquery/jquery.min.js"></script>
        <script src="js/jquery/jquery.widget.min.js"></script>
        <script src="js/metro/metro.min.js"></script>
        <title>output</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    
    <body class="metro" style="background-color:  #004206">
    	<jsp:useBean id="myForm" class="mypackage.MyForm" scope="request" />
		<jsp:setProperty name="myForm" property="*" />
        <nav class="navigation-bar white">
            <nav class="navigation-bar-content">
                <div class="ol-emerald">
                <div class="fg-emerald">
                <div class="element">
                    <a class="navbar-content" href="index.jsp"><span class="icon-home"></span><b> HOME </b></a> 
                </div>
                 <span class="element-divider"></span>
                <div class="element">
                    <a class="navbar-content" href="help.html"><span class="icon-help"></span><b> HELP </b></a> 
                </div>
                  <span class="element-divider"></span>
                <div class="element">
                    <a class="navbar-content" href="about.html"><span class="icon-user"></span><b> ABOUT US </b></a> 
                </div>
                </div></div>
            </nav>
        </nav>
        
        <div style='position: fixed; bottom:  508px; right: 10px;width:275px;height:80px;'>
            <img src="image/Untitled2.jpg" width="283" height="31"></div>
      
        <br><br>
        <div class="container" style="position: fixed; left: 150px">
		
		<form action="myServlet" method="post">
			<br><input type="text" style="background-color: orange; height: 40px" value="Accuracy ${myForm.messages.out}%">
			<input type="hidden" id="idxsalah" name="idxsalah" value="${myForm.messages.idxsalah}">
			<input type="hidden" id="tipe" name="tipe" value="Rebuild">
			<input type="submit" value="Rebuild" class="command-button large primary" style="left: 745px">
                        <a class="command-button large primary" href="resource/output.csv">Download</a> 
		</form>
                
        <table class="table striped hovered dataTable" id="dataTables-1" style="position: fixed; width: 800px">
                <thead>
                <tr>
                    <th class="text-left">Id Text ${myForm.messages.idxsalah}</th>
                    <th class="text-left">Output</th>
                    <th class="text-left">Hasil Seharusnya</th>
                </tr>
                </thead>

                <tbody>
                <% 
                ArrayList<Paket> rs=myForm.readCSV();
                rs.remove(0);
                for (Paket p:rs){ %>
                    <tr>
                        <th class="text-left"><%=p.getId()%></th>
                        <th class="text-left"><%=p.getPred()%></th>
                        <th class="text-left"><%=p.getAct()%></th>
                    </tr>
                <% } %>
                </tbody>
        </table>
        </div>
   
    </body>
</html>
 