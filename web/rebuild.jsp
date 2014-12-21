<!DOCTYPE html>

<html lang="en">
    <head>
         <link rel="stylesheet" href="css/metro-bootstrap.css">
         <link href="css/iconFont.css" rel="stylesheet">
        <script src="js/jquery/jquery.min.js"></script>
        <script src="js/jquery/jquery.widget.min.js"></script>
        <script src="js/metro/metro.min.js"></script>
        <title>help</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    
    <body class="metro" style="background-color:  #004206">
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
        <div class="container">
        <div class="stepper " data-steps="5" data-role="stepper" data-start="1">
			<c:if test="${myForm.errors.error != null}">
				<span style="color: red;">${myForm.errors.error}</span>
			</c:if>
			<c:if test="${myForm.messages.out != null}">
				<span style="color: green;">${myForm.messages.out}</span>
			</c:if>
		</div>
        </div>
       
</html>
 