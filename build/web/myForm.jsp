<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!doctype html>

<html lang="en">
    <head>
        <title>Test</title>
    </head>
    <body>
        <jsp:useBean id="myForm" class="mypackage.MyForm" scope="request" />
        <jsp:setProperty name="myForm" property="*" />
		<a href="file/a.txt">LINK</a>
        <form action="myServlet" method="post" enctype="multipart/form-data">
            <label for="text" >Text:</label>
            <input type="text" id="text" name="text" value="${myForm.text}">
            <c:if test="${myForm.errors.text != null}">
                <span style="color: red;">${myForm.errors.text}</span>
            </c:if>
            <c:if test="${myForm.messages.text != null}">
                <span style="color: green;">${myForm.messages.text}</span>
            </c:if>
            <br>

            <label for="file" >File:</label>
            <input type="file" id="file" name="file">
            <c:if test="${myForm.errors.file != null}">
                <span style="color: red;">${myForm.errors.file}</span>
            </c:if>
            <c:if test="${myForm.messages.file != null}">
                <span style="color: green;">${myForm.messages.file}
                    <c:if test="${myForm.file != null}">
                        &nbsp;<a href="file/${myForm.file.name}">Download back</a>.
                    </c:if>
                </span>
            </c:if>
            <br>

            <label for="check1" >Check 1:</label>
            <input type="checkbox" id="check1" name="check" value="check1"
                ${myForm.checked.check1 ? 'checked' : ''}>
            <c:if test="${myForm.errors.check != null}">
                <span style="color: red;">${myForm.errors.check}</span>
            </c:if>
            <c:if test="${myForm.messages.check != null}">
                <span style="color: green;">${myForm.messages.check}</span>
            </c:if>
            <br>

            <label for="check2" >Check 2:</label>
            <input type="checkbox" id="check2" name="check" value="check2"
                ${myForm.checked.check2 ? 'checked' : ''} />
            <br>

            <input type="submit">
        </form>
    </body>
</html>
