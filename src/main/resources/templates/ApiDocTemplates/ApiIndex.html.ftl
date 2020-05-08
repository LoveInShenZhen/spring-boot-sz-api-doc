<!doctype html>
<html>

<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>API 调用测试</title>
    <link href="/apidocres/js/bootstrap_2.3.2.min.css" rel="stylesheet" type="text/css"/>
    <script src="/apidocres/js/jquery_2.1.4.min.js" type="text/javascript"></script>

    <style type="text/css">
        body {
            min-height: 100%;
            min-width: 1080px;
            font-family: 'Microsoft YaHei', 'Heiti SC', simhei, 'Lucida Sans Unicode', 'Myriad Pro', 'Hiragino Sans GB', Verdana;
            background: #e3dede;
            color: #000;
            font-size: 13px;
        }

        /*全局样式*/

        ul,
        li {
            list-style: none;
            margin: 0px;
        }

        a {
            color: #444;
        }

        a:hover {
            text-decoration: none;
            color: #646464;
        }

        .container {
            width: 960px;
            margin: 0 auto;
        }
    </style>

</head>

<body>
<div class="container">
    <#list groups as group>
        <div class="list">
            <ul class="nav nav-pills nav-stacked" style="margin-top:20px;">
                <li class="active"><a href="#" style="cursor:default;">${group.groupName}</a></li>
            </ul>
            <table class="table table-hover">
                <thead>
                <tr style="background: #D2D2D2;">
                    <th>API</th>
                    <th>http method</th>
                    <th>说明</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <#list group.apiInfoList as apiInfo>
                    <tr>
                        <td><a href='${apiInfo.DocPage()}' target="_blank">${apiInfo.path}</a></td>
                        <td>${apiInfo.httpMethod}</td>
                        <td>${apiInfo.apiComment}</td>
                        <td><a href='${apiInfo.TestPage()}' target="_blank">点击测试</a></td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </div>
    </#list>
</div>

</body>

</html>