<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>

<html lang="en">
    <head>
         <link rel="stylesheet" href="css/metro-bootstrap.css">
         <link href="css/iconFont.css" rel="stylesheet">
         
        <script src="js/jquery/jquery.min.js"></script>
        <script src="js/jquery/jquery.widget.min.js"></script>
        <script src="js/metro/metro.min.js"></script>
        <script src="js/metro-carousel.js"></script>
           <script src="js/load-metro.js"></script>
        <title>News Aggregator</title>
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
    <div class="container">
    	<c:if test="${myForm.messages.out != null}">
        	<br><input type="text" style="position: fixed; left: 150px; background-color: orange; height: 40px" value="Kategori ${myForm.messages.out}">
        </c:if>
        <div style='position: fixed; bottom:  508px; right: 10px;width:275px;height:80px;'>
            <img src="image/Untitled2.jpg" width="283" height="31"></div>
        <div class="fg-white"><br><br><br>
            
            <form action="myServlet" method="post" style="position: fixed; left: 150px; ">
                <c:if test="${myForm.errors.text != null}">
                    <span style="color: red;">${myForm.errors.text}</span>
                    <br/>
                </c:if>
                <textarea id="text" name="text" class="fg-white" rows="20" cols="60" style="background-color: transparent; outline-color: white;" placeholder="Input news text..." data-state="success">${myForm.text}</textarea>
                <input type="hidden" id="tipe" name="tipe" value="Input Text">	
                <input type="submit" value="Categorize" class="command-button large primary" style="position: fixed; top: 400px; left: 150px;">
            </form>
			<div class="notice marker-on-bottom" style="position: fixed; width: 5px; left: 745px; background-color: #970036">
                Input .csv file
            </div>
            <div style="position: fixed; width: 15px; left: 745px; top: 200px">
            <form action="myServlet" method="post" enctype="multipart/form-data">
                <input type="file" id="file" name="file"/>
				<c:if test="${myForm.errors.file != null}">
					<span style="color: red;">${myForm.errors.file}</span>
				</c:if>
				<input type="hidden" id="tipe" name="tipe" value="Input CSV">
				<input type="submit" value="Categorize" class="command-button large primary" style="position: fixed; bottom: 330px; left: 745px">
            </form>
           
            </div>
        </div>
    </div>
    </body>
</html>
 