<html>
<head>
    <meta charset="utf-8">
    <title>WSO2 Firealarm</title>
    <style type="text/css">
        body {
            background-color: #f4f4f4;
            color: #5a5656;
            font-family: 'Open Sans', Arial, Helvetica, sans-serif;
            font-size: 16px;
            line-height: 1.5em;
        }
        a { text-decoration: none; }
        h1 { font-size: 1em; }
        h1, p {
            margin-bottom: 10px;
        }
        strong {
            font-weight: bold;
        }
        .uppercase { text-transform: uppercase; }

        /* ---------- LOGIN ---------- */
        #login {
            margin: 50px auto;
            width: 300px;
        }
        form fieldset input[type="text"], input[type="password"] {
            background-color: #e5e5e5;
            border: none;
            border-radius: 3px;
            -moz-border-radius: 3px;
            -webkit-border-radius: 3px;
            color: #5a5656;
            font-family: 'Open Sans', Arial, Helvetica, sans-serif;
            font-size: 14px;
            height: 50px;
            outline: none;
            padding: 0px 10px;
            width: 280px;
            -webkit-appearance:none;
        }
        form fieldset input[type="submit"] {
            background-color: #008dde;
            border: none;
            border-radius: 3px;
            -moz-border-radius: 3px;
            -webkit-border-radius: 3px;
            color: #f4f4f4;
            cursor: pointer;
            font-family: 'Open Sans', Arial, Helvetica, sans-serif;
            height: 50px;
            text-transform: uppercase;
            width: 300px;
            -webkit-appearance:none;
        }
        form fieldset a {
            color: #5a5656;
            font-size: 10px;
        }
        form fieldset a:hover { text-decoration: underline; }
        .btn-round {
            background-color: #5a5656;
            border-radius: 50%;
            -moz-border-radius: 50%;
            -webkit-border-radius: 50%;
            color: #f4f4f4;
            display: block;
            font-size: 12px;
            height: 50px;
            line-height: 50px;
            margin: 30px 125px;
            text-align: center;
            text-transform: uppercase;
            width: 50px;
        }

    </style>
</head>
<body>

<%
    String token=(String)request.getSession().getAttribute("token");
    if(token!=null && !token.isEmpty()){
        response.sendRedirect("control.jsp");
    }
%>
<div id="login">


    <h1><strong>Welcome.</strong> Please login.</h1>
    <form action="login" method="post">
        <fieldset>
            <p><input type="text" name="username" id="username" required value="Username" onBlur="if(this.value=='')this.value='Username'" onFocus="if(this.value=='Username')this.value='' "></p>
            <p><input type="password" name="password" id="password" required value="Password" onBlur="if(this.value=='')this.value='Password'" onFocus="if(this.value=='Password')this.value='' "></p>

            <p><input type="submit" value="Login"></p>
        </fieldset>
    </form>

    <%
        String msg = (String) request.getAttribute("errMsg");
        if (msg != null) {
    %>
    <p><span class="btn-round">Invalid Credential
    <%=msg%>
    </span></p>
    <%
        }
    %>

</div> <!-- end login -->
</body>
</html>