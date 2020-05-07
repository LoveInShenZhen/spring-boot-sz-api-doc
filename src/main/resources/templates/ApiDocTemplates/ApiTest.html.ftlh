<!doctype html>
<html>

<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>API 调用测试</title>
    <link href="/apidocres/js/bootstrap_2.3.2.min.css" rel="stylesheet" type="text/css"/>
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
        <legend>${apiInfo.path}</legend>

        <#if apiInfo.IsGetJsonApi()>
            <!--Get 方式 -->
            <ul class="nav nav-pills">
                <li class="active">
                    <a>GET方式</a>
                </li>
            </ul>
            <div class="form-horizontal" id="get_box">
                <form id="ajax_form">
                    <#list apiInfo.params as api_param>
                        <div class="control-group param">
                            <label class="control-label">${api_param.name}</label>

                            <div class="controls">
                                <div class="pull-left">
                                    <input type="text" id="${api_param.name}" name="${api_param.name}"
                                           value="${api_param.defaultValue!}">
                                </div>
                                <div class="pull-left" style="margin-left:10px;">
                                    ${api_param.fullDesc!}
                                </div>
                            </div>

                        </div>
                    </#list>
                </form>

                <div class="control-group">
                    <div class="controls">
                        <button type="button" onclick="testAPI();" class="btn">测试</button>
                        <button type="button" onclick="ClearOutput();" class="btn">Clear Output</button>
                    </div>
                </div>
                <div class="control-group" style="max-width: 800px">
                    <label class="control-label">返回结果：</label>

                    <div class="controls" style="max-width: 800px">
                            <pre id="response" class="language-json">
                            <code># 点击 "测试" 按钮, 等待返回结果</code>
                        </pre>
                    </div>
                </div>
            </div>
            <!--Get 方式 -->
        </#if>

        <#if apiInfo.IsPostJsonApi()>
            <!--Post Json 方式 -->
            <ul class="nav nav-pills">
                <li class="active" id="get_nav">
                    <a>Post Json 方式</a>
                </li>
            </ul>

            <div class="form-horizontal" id="post_box">

                <form id="ajax_form">
                    <#list apiInfo.params as api_param>
                        <div class="control-group param">
                            <label class="control-label">${api_param.name}</label>

                            <div class="controls">
                                <div class="pull-left">
                                    <input type="text" id="${api_param.name}" name="${api_param.name}"
                                           value="${api_param.defaultValue!}">
                                </div>
                                <div class="pull-left" style="margin-left:10px;">
                                    ${api_param.fullDesc!}
                                </div>
                            </div>

                        </div>
                    </#list>
                </form>

                <#if apiInfo.hasRequestBody()>
                    <div class="control-group">
                        <label class="control-label">Post Json 数据结构：</label>

                        <div class="controls" style="max-width: 800px">
                            <pre class="language-json"><code>${apiInfo.postJsonSchema()}</code></pre>
                        </div>
                    </div>

                    <div class="control-group">
                        <label class="control-label">Post Json：</label>

                        <div class="controls">
                            <textarea id="requestJson"
                                      style="min-height: 240px;min-width: 800px;">${apiInfo.postJsonSample}</textarea>
                        </div>
                    </div>
                </#if>

                <div class="control-group">
                    <div class="controls">
                        <button type="button" onclick="testPostAPI();" class="btn">测试</button>
                        <button type="button" onclick="ClearPostOutput();" class="btn">Clear Output</button>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">返回结果：</label>

                    <div class="controls" style="max-width: 800px">
                            <pre id="postresponse" class="language-json">
                            <code># 点击 "测试" 按钮, 等待返回结果</code>
                        </pre>
                    </div>
                </div>
            </div>
            <!--Post Json 方式 -->
        </#if>

        <div class="controls">
            <pre id="errMsg"></pre>
        </div>
    </fieldset>
</div>
<div class='displaynone' id='hf_summit'></div>
</body>

</html>

<script src="/apidocres/js/prism.js"></script>
<script type="text/javascript">
    function testAPI() {
        $('#errMsg').text('');
        var apiurl = $("legend").html();
        var query_str = $("#ajax_form").serialize();
        var json_api_url = apiurl + "?" + query_str;

        $.ajax({
            url: json_api_url,
            type: 'get',
            dataType: 'json',
            error: function () {
                alert('error');
            },
            success: function (data) {
                if (data['form'] != null && data['formId'] != null) {
                    $("#hf_summit").html(data['form']);
                    $("#" + data['formId']).submit();
                } else {
                    var highLight = Prism.highlight(JSON.stringify(data, null, 2), Prism.languages.json, 'json');
                    $("#response").html(highLight);
                    showMultipleLinesErrMsg(data);
                }

            }
        });
    }

    function ClearOutput() {
        $("#response").html('<code># 点击 "测试" 按钮, 等待返回结果</code>');
        $('#errMsg').text('');
    }

    function testPostAPI() {
        $('#errMsg').text('');
        var apiurl = $("legend").html();
        var query_str = $("#ajax_form").serialize();
        var json_api_url = apiurl + "?" + query_str;

        var jsondata = $("#requestJson").val();

        if (jsondata !== undefined) {
            try {
                var obj = jQuery.parseJSON(jsondata);
            } catch (err) {
                alert("貌似json格式不对，请检查");
                return;
            }
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
                var highLight = Prism.highlight(JSON.stringify(data, null, 2), Prism.languages.json, 'json');
                $("#postresponse").html(highLight);
                showMultipleLinesErrMsg(data);
            }
        });
    }

    function testPostFormAPI() {
        $('#errMsg').text('');
        var apiurl = $("legend").html();
        var query_str = $("#ajax_form").serialize();
        var json_api_url = apiurl + "?" + query_str;

        $("#post_form").attr("action", json_api_url);

        $("#post_form").ajaxSubmit({
            error: function () {
                alert('error');
            },
            success: function (data) {
                var highLight = Prism.highlight(JSON.stringify(data, null, 2), Prism.languages.json, 'json');
                $("#postresponse").html(highLight);
                showMultipleLinesErrMsg(data);
            }
        })
    }

    function ClearPostOutput() {
        $("#postresponse").html('<code># 点击 "测试" 按钮, 等待返回结果</code>');
        $('#errMsg').text('');
    }

    function showMultipleLinesErrMsg(reply) {
        if (reply.ret != 0) {
            $('#errMsg').text(reply.errmsg);
        }
    }
</script>