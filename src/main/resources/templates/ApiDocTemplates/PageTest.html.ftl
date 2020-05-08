<!doctype html>
<html>

<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>页面调用测试</title>
    <link href="/apidocres/css/bootstrap_2.3.2.min.css" rel="stylesheet" type="text/css"/>
    <script src="/apidocres/js/jquery_2.1.4.min.js" type="text/javascript"></script>
    <link data-noprefix href="/apidocres/css/prism.css" rel="stylesheet"/>
    <script src="/apidocres/js/jquery.form_3.51.min.js" type="text/javascript"></script>

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
    <fieldset>
        <legend>${path}</legend>

        <#if IsPostJsonApi()>
            <!--Post Json 方式 -->
            <ul class="nav nav-pills">
                <li class="active" id="get_nav">
                    <a>自组成JSON采用POST方式</a>
                </li>
            </ul>
            <div class="form-horizontal" id="post_box">

                <form id="ajax_form">
                    <#list params as api_param>
                        <div class="control-group param">
                            <label class="control-label">${api_param.name}</label>

                            <div class="controls">
                                <div class="pull-left">
                                    <input type="text" id="${api_param.name}" name="${api_param.name}">
                                </div>
                                <div class="pull-left" style="margin-left:10px;">
                                    ${api_param.type!}, ${api_param.desc!}
                                </div>
                            </div>

                        </div>
                    </#list>
                </form>

                <div class="control-group">
                    <label class="control-label">PostJson 样例数据：</label>

                    <div class="controls">
                        <textarea id="sampleJson"
                                  style="min-height: 240px;min-width: 800px;">${postDataSample}</textarea>
                    </div>
                </div>

                <div class="control-group">
                    <label class="control-label">Post JSON串：</label>

                    <div class="controls">
                        <textarea id="requestJson" style="min-height: 240px;min-width: 800px;"></textarea>
                    </div>
                </div>

                <div class="control-group">
                    <div class="controls">
                        <button type="button" onclick="testPostAPI();" class="btn">测试</button>
                        <button type="button" onclick="ClearPostOutput();" class="btn">Clear Output</button>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">返回结果：</label>

                    <div class="controls">
                        <textarea id="postresponse" style="min-height: 240px;min-width: 800px;"></textarea>
                    </div>
                </div>
            </div>
            <!--Post Json 方式 -->
        </#if>

    </fieldset>
</div>
<div class='displaynone' id='hf_summit'></div>
</body>

</html>


<script type="text/javascript">
    function testAPI() {
        var apiurl = $("legend").html();
        var query_str = $("#ajax_form").serialize();
        var json_api_url = apiurl + "?" + query_str + "&timetamp=" + new Date().getTime();

        window.open(json_api_url);
    }

    function ClearOutput() {
        $("#response").val("");
    }

    function testPostAPI() {
        var apiurl = $("legend").html();
        var query_str = $("#ajax_form").serialize();
        var json_api_url = apiurl + "?" + query_str + "&timetamp=" + new Date().getTime();
        ;

        var jsondata = $("#requestJson").val();
        try {
            var obj = jQuery.parseJSON(jsondata);
        } catch (err) {
            alert("貌似json格式不对，请检查");
            return;
        }

        $.ajax({
            type: 'POST',
            url: json_api_url,
            data: jsondata, // or JSON.stringify ({name: 'jonas'}),
            contentType: "application/json",
            dataType: 'json',
            error: function () {
                alert('error');
            },
            success: function (data) {
                $("#postresponse").val(JSON.stringify(data, null, 2));
            }
        });
    }

    function testPostFormAPI() {
        var apiurl = $("legend").html();
        var query_str = $("#ajax_form").serialize();
        var json_api_url = apiurl + "?" + query_str + "&timetamp=" + new Date().getTime();
        ;

        $("#post_form").attr("action", json_api_url);

        $("#post_form_submit").click();
    }

    function ClearPostOutput() {
        $("#postresponse").val("");
    }
</script>