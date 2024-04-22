<#macro ftable fields color="333333">
<#if fields?? && (fields ? size > 0)>
    <w:p w:rsidR="00FF63BA" w:rsidRPr="001F27D6" w:rsidRDefault="00AD7DD0" w:rsidP="001F27D6">
        <w:pPr>
            <w:spacing w:line="360" w:lineRule="auto"/>
            <w:rPr>
                <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:cs="Open Sans"/>
                <w:b/>
                <w:color w:val="${color}"/>
                <w:shd w:val="clear" w:color="auto" w:fill="FFFFFF"/>
            </w:rPr>
        </w:pPr>
        <w:r w:rsidRPr="001F27D6">
            <w:rPr>
                <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:cs="Open Sans"/>
                <w:b/>
                <w:color w:val="${color}"/>
                <w:shd w:val="clear" w:color="auto" w:fill="FFFFFF"/>
            </w:rPr>
            <w:t><#nested></w:t>
        </w:r>
    </w:p>
    <w:tbl>
        <w:tblPr>
            <w:tblStyle w:val="a3"/>
            <w:tblW w:w="0" w:type="auto"/>
            <w:tblLook w:val="04A0" w:firstRow="1" w:lastRow="0" w:firstColumn="1" w:lastColumn="0" w:noHBand="0"
                       w:noVBand="1"/>
        </w:tblPr>
        <w:tblGrid>
            <w:gridCol w:w="2765"/>
            <w:gridCol w:w="2765"/>
            <w:gridCol w:w="2766"/>
        </w:tblGrid>
        <w:tr w:rsidR="00FF63BA" w:rsidRPr="00EB6AA6" w:rsidTr="00EB6AA6">
            <w:tc>
                <w:tcPr>
                    <w:tcW w:w="2765" w:type="dxa"/>
                    <w:tcBorders>
                        <w:bottom w:val="single" w:sz="4" w:space="0" w:color="auto"/>
                    </w:tcBorders>
                    <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6" w:themeFill="background2"/>
                </w:tcPr>
                <w:p w:rsidR="00FF63BA" w:rsidRPr="00EB6AA6" w:rsidRDefault="00FF63BA" w:rsidP="00AD7DD0">
                    <w:pPr>
                        <w:spacing w:line="360" w:lineRule="auto"/>
                        <w:rPr>
                            <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体"/>
                        </w:rPr>
                    </w:pPr>
                    <w:r w:rsidRPr="00EB6AA6">
                        <w:rPr>
                            <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:hint="eastAsia"/>
                        </w:rPr>
                        <w:t>名称</w:t>
                    </w:r>
                </w:p>
            </w:tc>
            <w:tc>
                <w:tcPr>
                    <w:tcW w:w="2765" w:type="dxa"/>
                    <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6" w:themeFill="background2"/>
                </w:tcPr>
                <w:p w:rsidR="00FF63BA" w:rsidRPr="00EB6AA6" w:rsidRDefault="00FF63BA" w:rsidP="00AD7DD0">
                    <w:pPr>
                        <w:spacing w:line="360" w:lineRule="auto"/>
                        <w:rPr>
                            <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体"/>
                        </w:rPr>
                    </w:pPr>
                    <w:r w:rsidRPr="00EB6AA6">
                        <w:rPr>
                            <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:hint="eastAsia"/>
                        </w:rPr>
                        <w:t>类型</w:t>
                    </w:r>
                </w:p>
            </w:tc>
            <#if config.showRequired>
                <w:tc>
                    <w:tcPr>
                        <w:tcW w:w="2765" w:type="dxa"/>
                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6" w:themeFill="background2"/>
                    </w:tcPr>
                    <w:p w:rsidR="00FF63BA" w:rsidRPr="00EB6AA6" w:rsidRDefault="00FF63BA" w:rsidP="00AD7DD0">
                        <w:pPr>
                            <w:spacing w:line="360" w:lineRule="auto"/>
                            <w:rPr>
                                <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体"/>
                            </w:rPr>
                        </w:pPr>
                        <w:r w:rsidRPr="00EB6AA6">
                            <w:rPr>
                                <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:hint="eastAsia"/>
                            </w:rPr>
                            <w:t>必填</w:t>
                        </w:r>
                    </w:p>
                </w:tc>
            </#if>
            <w:tc>
                <w:tcPr>
                    <w:tcW w:w="2766" w:type="dxa"/>
                    <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6" w:themeFill="background2"/>
                </w:tcPr>
                <w:p w:rsidR="00FF63BA" w:rsidRPr="00EB6AA6" w:rsidRDefault="00FF63BA" w:rsidP="00AD7DD0">
                    <w:pPr>
                        <w:spacing w:line="360" w:lineRule="auto"/>
                        <w:rPr>
                            <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体"/>
                        </w:rPr>
                    </w:pPr>
                    <w:r w:rsidRPr="00EB6AA6">
                        <w:rPr>
                            <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:hint="eastAsia"/>
                        </w:rPr>
                        <w:t>说明</w:t>
                    </w:r>
                </w:p>
            </w:tc>
        </w:tr>
    <#list fields as f>
        <w:tr w:rsidR="00FF63BA" w:rsidRPr="00EB6AA6" w:rsidTr="00EB6AA6">
            <w:tc>
                <w:tcPr>
                    <w:tcW w:w="2765" w:type="dxa"/>
                    <w:tcBorders>
                        <w:top w:val="single" w:sz="4" w:space="0" w:color="auto"/>
                    </w:tcBorders>
                </w:tcPr>
                <w:p w:rsidR="00FF63BA" w:rsidRPr="00EB6AA6" w:rsidRDefault="00FF63BA" w:rsidP="00AD7DD0">
                    <w:pPr>
                        <w:spacing w:line="360" w:lineRule="auto"/>
                        <w:rPr>
                            <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体"/>
                        </w:rPr>
                    </w:pPr>
                    <w:r w:rsidRPr="00EB6AA6">
                        <w:rPr>
                            <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:hint="eastAsia"/>
                        </w:rPr>
                        <w:t>${f.name ? html}</w:t>
                    </w:r>
                </w:p>
            </w:tc>
            <w:tc>
                <w:tcPr>
                    <w:tcW w:w="2765" w:type="dxa"/>
                    <w:tcBorders>
                        <w:top w:val="single" w:sz="4" w:space="0" w:color="auto"/>
                    </w:tcBorders>
                </w:tcPr>
                <w:p w:rsidR="00FF63BA" w:rsidRPr="00EB6AA6" w:rsidRDefault="00FF63BA" w:rsidP="00AD7DD0">
                    <w:pPr>
                        <w:spacing w:line="360" w:lineRule="auto"/>
                        <w:rPr>
                            <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体"/>
                        </w:rPr>
                    </w:pPr>
                    <w:r w:rsidRPr="00EB6AA6">
                        <w:rPr>
                            <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:hint="eastAsia"/>
                        </w:rPr>
                        <w:t>${f.type ? html}</w:t>
                    </w:r>
                </w:p>
            </w:tc>
            <#if config.showRequired>
                <w:tc>
                    <w:tcPr>
                        <w:tcW w:w="2765" w:type="dxa"/>
                        <w:tcBorders>
                            <w:top w:val="single" w:sz="4" w:space="0" w:color="auto"/>
                        </w:tcBorders>
                    </w:tcPr>
                    <w:p w:rsidR="00FF63BA" w:rsidRPr="00EB6AA6" w:rsidRDefault="00FF63BA" w:rsidP="00AD7DD0">
                        <w:pPr>
                            <w:spacing w:line="360" w:lineRule="auto"/>
                            <w:rPr>
                                <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体"/>
                            </w:rPr>
                        </w:pPr>
                        <w:r w:rsidRPr="00EB6AA6">
                            <w:rPr>
                                <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:hint="eastAsia"/>
                            </w:rPr>
                            <w:t>${(f.required ! false) ? string('是', '否')}</w:t>
                        </w:r>
                    </w:p>
                </w:tc>
            </#if>
            <w:tc>
                <w:tcPr>
                    <w:tcW w:w="2765" w:type="dxa"/>
                    <w:tcBorders>
                        <w:top w:val="single" w:sz="4" w:space="0" w:color="auto"/>
                    </w:tcBorders>
                </w:tcPr>
                <w:p w:rsidR="00FF63BA" w:rsidRPr="00EB6AA6" w:rsidRDefault="00FF63BA" w:rsidP="00AD7DD0">
                    <w:pPr>
                        <w:spacing w:line="360" w:lineRule="auto"/>
                        <w:rPr>
                            <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体"/>
                        </w:rPr>
                    </w:pPr>
                    <w:r w:rsidRPr="00EB6AA6">
                        <w:rPr>
                            <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:hint="eastAsia"/>
                        </w:rPr>
                        <w:t>${f.desc ? html}</w:t>
                    </w:r>
                </w:p>
            </w:tc>
        </w:tr>
    </#list>
    </w:tbl>

    <#list fields as f>
        <@ftable fields=f.children color="888888">${f.type ? html}</@ftable>
    </#list>
</#if>
</#macro>
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:document xmlns:wpc="http://schemas.microsoft.com/office/word/2010/wordprocessingCanvas"
            xmlns:cx="http://schemas.microsoft.com/office/drawing/2014/chartex"
            xmlns:cx1="http://schemas.microsoft.com/office/drawing/2015/9/8/chartex"
            xmlns:mc="http://schemas.openxmlformats.org/markup-compatibility/2006"
            xmlns:o="urn:schemas-microsoft-com:office:office"
            xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships"
            xmlns:m="http://schemas.openxmlformats.org/officeDocument/2006/math" xmlns:v="urn:schemas-microsoft-com:vml"
            xmlns:wp14="http://schemas.microsoft.com/office/word/2010/wordprocessingDrawing"
            xmlns:wp="http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing"
            xmlns:w10="urn:schemas-microsoft-com:office:word"
            xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"
            xmlns:w14="http://schemas.microsoft.com/office/word/2010/wordml"
            xmlns:w15="http://schemas.microsoft.com/office/word/2012/wordml"
            xmlns:w16se="http://schemas.microsoft.com/office/word/2015/wordml/symex"
            xmlns:wpg="http://schemas.microsoft.com/office/word/2010/wordprocessingGroup"
            xmlns:wpi="http://schemas.microsoft.com/office/word/2010/wordprocessingInk"
            xmlns:wne="http://schemas.microsoft.com/office/word/2006/wordml"
            xmlns:wps="http://schemas.microsoft.com/office/word/2010/wordprocessingShape"
            mc:Ignorable="w14 w15 w16se wp14">
    <w:body>

<#list data as group>
    <#if (group_index > 0)>
        <w:p w:rsidR="00377996" w:rsidRPr="00EB6AA6" w:rsidRDefault="00377996" w:rsidP="00EB6AA6">
            <w:pPr>
                <w:spacing w:line="360" w:lineRule="auto"/>
                <w:rPr>
                    <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:cs="Open Sans"/>
                    <w:color w:val="333333"/>
                    <w:shd w:val="clear" w:color="auto" w:fill="FFFFFF"/>
                </w:rPr>
            </w:pPr>
            <w:bookmarkStart w:id="0" w:name="_GoBack"/>
            <w:bookmarkEnd w:id="0"/>
        </w:p>
    </#if>
    <w:p w:rsidR="006C0C5D" w:rsidRPr="00800C1E" w:rsidRDefault="006C0C5D" w:rsidP="00EC0CF6">
        <w:pPr>
            <w:pStyle w:val="2"/>
            <w:spacing w:before="0" w:after="0" w:line="415" w:lineRule="auto"/>
            <w:rPr>
                <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体"/>
                <w:sz w:val="28"/>
                <w:szCs w:val="28"/>
            </w:rPr>
        </w:pPr>
        <w:r w:rsidRPr="00800C1E">
            <w:rPr>
                <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:hint="eastAsia"/>
                <w:sz w:val="28"/>
                <w:szCs w:val="28"/>
            </w:rPr>
            <w:t>${group.name ? html}</w:t>
        </w:r>
    </w:p>
<#list group.operationList as api>
        <#if (api_index > 0)>
        <w:p w:rsidR="00377996" w:rsidRPr="00EB6AA6" w:rsidRDefault="00377996" w:rsidP="00EB6AA6">
            <w:pPr>
                <w:spacing w:line="360" w:lineRule="auto"/>
                <w:rPr>
                    <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:cs="Open Sans"/>
                    <w:color w:val="333333"/>
                    <w:shd w:val="clear" w:color="auto" w:fill="FFFFFF"/>
                </w:rPr>
            </w:pPr>
            <w:bookmarkStart w:id="0" w:name="_GoBack"/>
            <w:bookmarkEnd w:id="0"/>
        </w:p>
        </#if>
        <w:p w:rsidR="0060307E" w:rsidRPr="00800C1E" w:rsidRDefault="0060307E" w:rsidP="005E092E">
            <w:pPr>
                <w:pStyle w:val="3"/>
                <w:spacing w:before="0" w:after="0" w:line="240" w:lineRule="auto"/>
                <w:rPr>
                    <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体"/>
                    <w:sz w:val="28"/>
                    <w:szCs w:val="28"/>
                </w:rPr>
            </w:pPr>
            <w:r w:rsidRPr="00800C1E">
                <w:rPr>
                    <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:hint="eastAsia"/>
                    <w:sz w:val="28"/>
                    <w:szCs w:val="28"/>
                </w:rPr>
                <w:t>${api.name ? html}</w:t>
            </w:r>
        </w:p>
        <#if config.showApiDesc && api.desc?? && api.name ? index_of(api.desc) == -1>
            <w:p>
                <w:r>
                    <w:t>${api.desc ? html}</w:t>
                </w:r>
            </w:p>
        </#if>
        <w:p w:rsidR="009B684E" w:rsidRPr="00EB6AA6" w:rsidRDefault="00C65065" w:rsidP="00EB6AA6">
            <w:pPr>
                <w:spacing w:line="360" w:lineRule="auto"/>
                <w:rPr>
                    <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:cs="Open Sans"/>
                    <w:color w:val="333333"/>
                    <w:shd w:val="clear" w:color="auto" w:fill="FFFFFF"/>
                </w:rPr>
            </w:pPr>
            <w:r w:rsidRPr="00EB6AA6">
                <w:rPr>
                    <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:hint="eastAsia"/>
                    <w:b/>
                </w:rPr>
                <w:t>URL</w:t>
            </w:r>
            <w:r w:rsidRPr="00EB6AA6">
                <w:rPr>
                    <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体"/>
                    <w:b/>
                </w:rPr>
                <w:t xml:space="preserve">: </w:t>
            </w:r>
            <w:r w:rsidRPr="00EB6AA6">
                <w:rPr>
                    <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:cs="Open Sans"/>
                    <w:color w:val="333333"/>
                    <w:shd w:val="clear" w:color="auto" w:fill="FFFFFF"/>
                </w:rPr>
                <w:t>${group.path ? html}${api.path ? html}</w:t>
            </w:r>
        </w:p>
        <w:p w:rsidR="00C65065" w:rsidRPr="00EB6AA6" w:rsidRDefault="00C65065" w:rsidP="00EB6AA6">
            <w:pPr>
                <w:spacing w:line="360" w:lineRule="auto"/>
                <w:rPr>
                    <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:cs="Open Sans"/>
                    <w:color w:val="333333"/>
                    <w:shd w:val="clear" w:color="auto" w:fill="FFFFFF"/>
                </w:rPr>
            </w:pPr>
            <w:r w:rsidRPr="00EB6AA6">
                <w:rPr>
                    <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:cs="Open Sans"/>
                    <w:b/>
                    <w:color w:val="333333"/>
                    <w:shd w:val="clear" w:color="auto" w:fill="FFFFFF"/>
                </w:rPr>
                <w:t xml:space="preserve">Method: </w:t>
            </w:r>
            <w:r w:rsidR="00FF63BA" w:rsidRPr="00EB6AA6">
                <w:rPr>
                    <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:cs="Open Sans"/>
                    <w:color w:val="333333"/>
                    <w:shd w:val="clear" w:color="auto" w:fill="FFFFFF"/>
                </w:rPr>
                <w:t>${api.method}</w:t>
            </w:r>
        </w:p>
        <w:p w:rsidR="00FF63BA" w:rsidRPr="00EB6AA6" w:rsidRDefault="00FF63BA" w:rsidP="00EB6AA6">
            <w:pPr>
                <w:spacing w:line="360" w:lineRule="auto"/>
                <w:rPr>
                    <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:cs="Open Sans"/>
                    <w:color w:val="333333"/>
                    <w:shd w:val="clear" w:color="auto" w:fill="FFFFFF"/>
                </w:rPr>
            </w:pPr>
            <w:r w:rsidRPr="00EB6AA6">
                <w:rPr>
                    <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:cs="Open Sans"/>
                    <w:b/>
                    <w:color w:val="333333"/>
                    <w:shd w:val="clear" w:color="auto" w:fill="FFFFFF"/>
                </w:rPr>
                <w:t xml:space="preserve">ContentType: </w:t>
            </w:r>
            <w:r w:rsidRPr="00EB6AA6">
                <w:rPr>
                    <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:cs="Open Sans"/>
                    <w:color w:val="333333"/>
                    <w:shd w:val="clear" w:color="auto" w:fill="FFFFFF"/>
                </w:rPr>
                <w:t>${api.contentType ? html}</w:t>
            </w:r>
        </w:p>

        <@ftable fields=api.pathVariable>RequestPath</@ftable>
        <@ftable fields=api.requestParam>RequestParam</@ftable>
        <#if api.requestFile?? && (api.requestFile ? size > 0)>
            <@ftable fields=api.requestFile>RequestBody</@ftable>
        <#else>
            <@ftable fields=api.requestBody>RequestBody</@ftable>
            <#if api.requestBodyExample>
                <w:p w:rsidR="00FF63BA" w:rsidRPr="001F27D6" w:rsidRDefault="00AD7DD0" w:rsidP="001F27D6">
                    <w:pPr>
                        <w:spacing w:line="360" w:lineRule="auto"/>
                        <w:rPr>
                            <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:cs="Open Sans"/>
                            <w:b/>
                            <w:color w:val="888888"/>
                            <w:shd w:val="clear" w:color="auto" w:fill="FFFFFF"/>
                        </w:rPr>
                    </w:pPr>
                    <w:r w:rsidRPr="001F27D6">
                        <w:rPr>
                            <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:cs="Open Sans"/>
                            <w:b/>
                            <w:color w:val="888888"/>
                            <w:shd w:val="clear" w:color="auto" w:fill="FFFFFF"/>
                        </w:rPr>
                        <w:t>RequestBody示例</w:t>
                    </w:r>
                </w:p>
                <w:p>
                    <w:r>
                        <w:rPr><w:color w:val="666666"/></w:rPr>
                        <w:t>${api.requestBodyExample ? replace('\r\n', '<w:br/>')}</w:t>
                    </w:r>
                </w:p>
            </#if>
        </#if>

        <@ftable fields=api.responseBody>ResponseBody</@ftable>
        <#if api.responseBodyExample>
            <w:p w:rsidR="00FF63BA" w:rsidRPr="001F27D6" w:rsidRDefault="00AD7DD0" w:rsidP="001F27D6">
                <w:pPr>
                    <w:spacing w:line="360" w:lineRule="auto"/>
                    <w:rPr>
                        <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:cs="Open Sans"/>
                        <w:b/>
                        <w:color w:val="888888"/>
                        <w:shd w:val="clear" w:color="auto" w:fill="FFFFFF"/>
                    </w:rPr>
                </w:pPr>
                <w:r w:rsidRPr="001F27D6">
                    <w:rPr>
                        <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体" w:cs="Open Sans"/>
                        <w:b/>
                        <w:color w:val="888888"/>
                        <w:shd w:val="clear" w:color="auto" w:fill="FFFFFF"/>
                    </w:rPr>
                    <w:t>ResponseBody示例</w:t>
                </w:r>
            </w:p>
            <w:p>
                <w:r>
                    <w:rPr><w:color w:val="666666"/></w:rPr>
                    <w:t>${api.responseBodyExample ? replace('\r\n', '<w:br/>')}</w:t>
                </w:r>
            </w:p>
        </#if>
</#list>
</#list>
    </w:body>
</w:document>