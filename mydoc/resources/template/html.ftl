<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>${name}</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            font-size: 16px;
            color: #333;
        }

        body {
            display: flex;
        }

        #left {
            width: 20em;
            height: 100%;
            overflow-x: hidden;
            overflow-y: auto;
            border-right: 2px solid #c4c4c4;
            margin-left: 0.5em;
        }

        #left span {
            font-size: 14px;
            display: block;
            margin: 0.5em 0;
        }

        #left a {
            margin: 0 1em;
            text-decoration: none;
            font-size: 14px;
            display: block;
            height: 2em;
            line-height: 2em;
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
            text-indent: 0.5em;
            cursor: pointer;
        }

        .active {
            font-weight: bold;
            position: relative;
        }

        .active:before {
            position: absolute;
            display: inline-block;
            content: '';
            width: 0.5em;
            height: 0.5em;
            top: 0.7em;
            left: -0.2em;
            background-color: #333;
            transform: rotate(140deg);
        }

        #right {
            width: 100%;
            height: 100%;
            overflow-y: auto;
            margin-left: 1em;
        }

        .api > div {
            line-height: 2em;
            width: 80%;
            font-weight: bold;
            margin-top: 1em;
        }

        .api > p {
            margin-top: 1em;
            text-indent: 5px;
        }

        .api > p span {
            width: 4px;
            height: 4px;
            display: inline-block;
            background-color: #333;
            border-radius: 50%;
            margin-right: 5px;
            margin-bottom: 0.2em;
        }

        #right p.sub-table-title {
            border-left: 2px solid #999;
            color: #888;
        }

        #right p.sub-table-title > a {
            cursor: pointer;
            color: #668;
            margin-left: 1em;
            font-size: smaller;
        }

        pre {
            background-color: #f9f9f9;
            border: 1px solid #ddd;
            margin-top: 1em;
            width: calc(80% - 20px);
            overflow-x: auto;
            padding: 10px;
        }

        .api {
            margin-bottom: 3em;
            display: none;
        }

        td, th {
            border: 1px solid #aaa;
            text-align: left;
            padding: 7px 0.5em;
            font-weight: normal;
        }

        th {
            background-color: #efefef;
        }

        table {
            width: 80%;
            border-collapse: collapse;
            margin-top: 1em;
        }

        ::-webkit-scrollbar {
            width: 8px;
        }

        ::-webkit-scrollbar-thumb {
            background-color: #aaa;
        }

        #left::-webkit-scrollbar {
            width: 5px;
        }

        #left::-webkit-scrollbar-thumb {
            border-radius: 2em;
        }
    </style>
</head>
<body>
<div id="left">
    <#list data as group>
        <span>📖 ${group.name}</span>
    <#list group.operationList as api>
        <a onclick="setActive(this)" href="#api-${group_index}-${api_index}" title="${api.name}">${api.name}</a>
    </#list>
    </#list>
</div>
<div id="right">
    <#macro ftable fields>
        <#if fields?? && (fields ? size > 0)>
            <#nested>
            <table>
                <tr>
                    <th>名称</th>
                    <th>类型</th>
                    <#if config.showRequired>
                        <th>必填</th></#if>
                    <th>说明</th>
                </tr>
                <#list fields as f>
                    <tr>
                        <td>${f.name}</td>
                        <td>${f.type ? html}</td>
                        <#if config.showRequired>
                            <td>${(f.required ! false) ? string('是', '否')}</td></#if>
                        <td>${f.desc}</td>
                    </tr>
                </#list>
            </table>
            <#list fields as f>
                <@ftable fields=f.children>
                    <p class="sub-table-title">${f.type ? html}</p>
                </@ftable>
            </#list>
        </#if>
    </#macro>

    <#list data as group>
    <#list group.operationList as api>
        <div class="api" id="api-${group_index}-${api_index}">
            <#if config.showApiDesc><div>${api.desc ! api.name}</div></#if>
            <p><span></span>URL: ${group.path}${api.path}</p>
            <p><span></span>Method: ${api.method}</p>
            <p><span></span>ContentType: ${api.contentType}</p>
            <@ftable fields=api.pathVariable>
                <p><span></span>RequestPath</p>
            </@ftable>
            <@ftable fields=api.requestParam>
                <p><span></span>RequestParam</p>
            </@ftable>
            <#if api.requestFile?? && (api.requestFile ? size > 0)>
                <@ftable fields=api.requestFile>
                    <p><span></span>RequestBody</p>
                </@ftable>
            <#else>
                <@ftable fields=api.requestBody>
                    <p><span></span>RequestBody</p>
                </@ftable>
                <#if api.requestBodyExample>
                    <p class="sub-table-title">
                        RequestBody示例
                        <a onclick="copyClipboard('#req-${group_index}-${api_index}')">📰</a>
                    </p>
                    <pre id="req-${group_index}-${api_index}">${api.requestBodyExample}</pre>
                </#if>
            </#if>
            <@ftable fields=api.responseBody>
                <p><span></span>ResponseBody</p>
            </@ftable>
            <#if api.responseBodyExample>
                <p class="sub-table-title">
                    ResponseBody示例
                    <a onclick="copyClipboard('#res-${group_index}-${api_index}')">📰</a>
                </p>
                <pre id="res-${group_index}-${api_index}">${api.responseBodyExample}</pre>
            </#if>
        </div>
    </#list>
    </#list>
</div>
<script>
    function setActive(e) {
        let curr = document.querySelector('.active');
        if (curr) curr.classList.remove('active');
        if (e) {
            e.classList.add('active');
            let apis = document.getElementsByClassName('api');
            for (let i = 0; i < apis.length; i++) {
                apis[i].style.display = 'none';
            }
            document.getElementById(e.href.split('#')[1]).style.display = 'block';
        }
    }

    function copyClipboard(id) {
        const code = document.querySelector(id).innerHTML;
        const textarea = document.createElement('textarea');
        textarea.value = code;
        document.body.appendChild(textarea);
        textarea.select();
        const isSuccess = document.execCommand('copy');
        if (isSuccess) {
            alert('内容已复制到剪贴板')
        } else {
            alert('复制失败')
        }
        document.body.removeChild(textarea);
    }

    window.onload = () => setActive(document.querySelector('#left a'))
</script>
</body>
</html>