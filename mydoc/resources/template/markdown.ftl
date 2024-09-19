<#macro ftable fields>
<#if fields?? && (fields ? size > 0)>
<#nested>

| 名称 | 类型 <#if config.showRequired>| 必填 </#if>| 说明 |
| ---- | ---- <#if config.showRequired>| ---- </#if>| ---- |
<#list fields as f>
| ${f.name} | ${f.type ? html} <#if config.showRequired>| ${(f.required ! false) ? string('是', '否')} </#if>| ${f.desc} |
</#list>
<#list fields as f>
<@ftable fields=f.children>
> ${f.type ? html}
</@ftable>
</#list>
</#if>
</#macro>

<#list data as group>
### ${group.name}
<#list group.operationList as api>
#### ${api.name}
▪ **URL:**  ${group.path}${api.path}

▪ **Method:**  ${api.method}

▪ **ContentType:**  ${api.contentType?replace('*', '\\*', 'f')}

<@ftable fields=api.pathVariable>
▪ **RequestPath**
</@ftable>
<@ftable fields=api.requestParam>
▪ **RequestParam**
</@ftable>

<#if api.requestFile?? && (api.requestFile ? size > 0)>
<@ftable fields=api.requestFile>
▪ **RequestBody**
</@ftable>
<#else>
<@ftable fields=api.requestBody>
▪ **RequestBody**
</@ftable>
<#if api.requestBodyExample>
> RequestBody示例
```json
${api.requestBodyExample}
```
</#if>
</#if>

<@ftable fields=api.responseBody>
▪ **ResponseBody**
</@ftable>
<#if api.responseBodyExample>
> ResponseBody示例
```json
${api.responseBodyExample}
```
</#if>
</#list>
</#list>