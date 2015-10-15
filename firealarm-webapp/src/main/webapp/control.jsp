<%@ page import="org.wso2.iot.firealarm.access.api.Device" %>
<%@ page import="org.wso2.iot.firealarm.access.api.DeviceManager" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <title>WSO2 Firealarm</title>
    <style type="text/css">

        .btn-round {
            background-color: #5a5656;
            border-radius: 75%;
            -moz-border-radius: 75%;
            -webkit-border-radius: 75%;
            color: #f4f4f4;
            display: block;
            font-size: 12px;
            height: 90px;
            line-height: 50px;
            margin: 30px 125px;
            text-align: center;
            text-transform: uppercase;
            width: 90px;
        }

        .CSSTableGenerator {
            margin: 0px;
            padding: 0px;
            width: 47%;
            table-layout: fixed;
            box-shadow: 10px 10px 5px #888888;
            border: 1px solid #000000;

            -moz-border-radius-bottomleft: 0px;
            -webkit-border-bottom-left-radius: 0px;
            border-bottom-left-radius: 0px;

            -moz-border-radius-bottomright: 0px;
            -webkit-border-bottom-right-radius: 0px;
            border-bottom-right-radius: 0px;

            -moz-border-radius-topright: 0px;
            -webkit-border-top-right-radius: 0px;
            border-top-right-radius: 0px;

            -moz-border-radius-topleft: 0px;
            -webkit-border-top-left-radius: 0px;
            border-top-left-radius: 0px;

        }

        .CSSTableGenerator tr:last-child td:last-child {
            -moz-border-radius-bottomright: 0px;
            -webkit-border-bottom-right-radius: 0px;
            border-bottom-right-radius: 0px;
        }

        .CSSTableGenerator table tr:first-child td:first-child {
            -moz-border-radius-topleft: 0px;
            -webkit-border-top-left-radius: 0px;
            border-top-left-radius: 0px;
        }

        .CSSTableGenerator table tr:first-child td:last-child {
            -moz-border-radius-topright: 0px;
            -webkit-border-top-right-radius: 0px;
            border-top-right-radius: 0px;
        }

        .CSSTableGenerator tr:last-child td:first-child {
            -moz-border-radius-bottomleft: 0px;
            -webkit-border-bottom-left-radius: 0px;
            border-bottom-left-radius: 0px;
        }

        .CSSTableGenerator tr:hover td {

        }

        .CSSTableGenerator tr:nth-child(odd) {
            background-color: #20c8f7;
        }

        .CSSTableGenerator tr:nth-child(even) {
            background-color: #ffffff;
        }

        .CSSTableGenerator td {
            vertical-align: middle;

            border: 1px solid #000000;
            border-width: 0px 1px 1px 0px;
            text-align: left;
            padding: 13px;
            font-size: 15px;
            font-family: Arial;
            font-weight: normal;
            color: #000000;
        }

        .CSSTableGenerator tr:last-child td {
            border-width: 0px 1px 0px 0px;
        }

        .CSSTableGenerator tr td:last-child {
            border-width: 0px 0px 1px 0px;
        }

        .CSSTableGenerator tr:last-child td:last-child {
            border-width: 0px 0px 0px 0px;
        }

        .CSSTableGenerator tr:first-child td {
            background: -o-linear-gradient(bottom, #208cbf 5%, #208cbf 100%);
            background: -webkit-gradient(linear, left top, left bottom, color-stop(0.05, #208cbf), color-stop(1, #208cbf));
            background: -moz-linear-gradient(center top, #208cbf 5%, #208cbf 100%);
            filter: progid:DXImageTransform.Microsoft.gradient(startColorstr="#208cbf", endColorstr="#208cbf");
            background: -o-linear-gradient(top, #208cbf, 208 cbf);

            background-color: #208cbf;
            border: 0px solid #000000;
            text-align: center;
            border-width: 0px 0px 1px 1px;
            font-size: 16px;
            font-family: Arial;
            font-weight: bold;
            color: #ffffff;
        }

        .CSSTableGenerator tr:first-child:hover td {
            background: -o-linear-gradient(bottom, #208cbf 5%, #208cbf 100%);
            background: -webkit-gradient(linear, left top, left bottom, color-stop(0.05, #208cbf), color-stop(1, #208cbf));
            background: -moz-linear-gradient(center top, #208cbf 5%, #208cbf 100%);
            filter: progid:DXImageTransform.Microsoft.gradient(startColorstr="#208cbf", endColorstr="#208cbf");
            background: -o-linear-gradient(top, #208cbf, 208 cbf);

            background-color: #208cbf;
        }

        .CSSTableGenerator tr:first-child td:first-child {
            border-width: 0px 0px 1px 0px;
        }

        .CSSTableGenerator tr:first-child td:last-child {
            border-width: 0px 0px 1px 1px;
        }

    </style>

    <script src="libraries/jquery-latest.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>

    <script>
        function sendData(operationType, device) {
            var e = document.getElementById(device);
            var protocol = e.options[e.selectedIndex].text;

            $.post("operation", {operation: operationType, deviceId: device, protocol: protocol})
                    .done(function (data) {
                        alert("Data Loaded: " + data);
                    });

        }

        function sendBulb(operationType, device) {
            var e = document.getElementById(device);
            var protocol = e.options[e.selectedIndex].text;

            $.post("operation", {
                operation: "bulb",
                deviceId: device,
                state: operationType,
                protocol: protocol
            })
                    .done(function (data) {
                        alert("Data Loaded: " + data);
                    });

        }
    </script>

</head>
<body>

<%
    String token = (String) request.getSession().getAttribute("token");
    if (token == null || token.isEmpty()) {
        response.sendRedirect("index.jsp");

    }
%>
<div align="center">
    <div class="CSSTableGenerator" align="center">
        <table>
            <tr>
                <td>
                    Device Id
                </td>
                <td>
                    Device Name
                </td>
                <td>
                    Switch Bulb
                </td>
                <td>Temperature</td>
                <td>Humidity</td>
                <td>Protocol</td>
            </tr>

            <% DeviceManager deviceManager = new DeviceManager();
                List<Device> devices = deviceManager.getDevice(
                        (String) request.getSession().getAttribute("token"),
                        (String) request.getSession().getAttribute("username"));
                if (devices != null) {

                    for (Device device : devices) {
            %>
            <tr>
                <td>
                    <%=device.getId()%>
                </td>
                <td>
                    <%=device.getName()%>
                </td>
                <td>
                    <button onclick='sendBulb("ON","<%=device.getId()%>");'>on</button>
                    <button onclick='sendBulb("OFF","<%=device.getId()%>");'> off</button>
                </td>
                <td>
                    <button onclick='sendData("temp","<%=device.getId()%>");'>Get Temperature
                    </button>
                    <label></label>
                </td>
                <td>
                    <button onclick='sendData("humid","<%=device.getId()%>");'>Get Humidity</button>
                    <label></label>
                </td>
                <td>
                    <select id="<%=device.getId()%>">
                        <option value="http" selected="selected">HTTP</option>
                        <option value="mqtt">MQTT</option>
                        <option value="xmpp">XMPP</option>
                    </select>
                </td>
            </tr>
            <%
                    }
                }
            %>

        </table>


    </div>

    <p>

    <form action="logout" method="post">
        <button class="btn-round" onclick="submit()">Logout</button>
    </form>
    </p>
</div>
</body>
</html>