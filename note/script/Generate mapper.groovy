package src

import com.intellij.database.model.DasTable
import com.intellij.database.util.Case
import com.intellij.database.util.DasUtil

/*
 * Available context bindings:
 *   SELECTION   Iterable<DasObject>
 *   PROJECT     project
 *   FILES       files helper
 */

// TODO package
basePackage = "com.vstecs.crm"

FILES.chooseDirectoryAndSave("Choose directory", "Choose where to store generated files") { dir ->
    SELECTION.filter { it instanceof DasTable }.each { generate(it, dir) }
}

def generate(table, dir) {
    // TODO module和Mapper后缀
    def baseName = javaName(table.getName(), true)
    def module = basePackage + "." + dir.toString().replaceAll(".*[/\\\\]crm-(.*?)[\\-/\\\\].*", "\$1")
    new File(dir, baseName + "Mapper.java").withPrintWriter { out -> generateInterface(out, baseName, module) }
}

static def generateInterface(out, baseName, module) {
    def date = String.format("%tF %<tT", new Date())
    out.println "package ${module}.mapper;"
    out.println ""
    // TODO entity包
    out.println "import com.vstecs.crm.common.entity.${baseName};"
    out.println "import org.springframework.stereotype.Repository;"
    out.println "import org.apache.ibatis.annotations.Param;"
    out.println "import java.util.List;"
    out.println ""
    out.println "/**"
    out.println " * @author generator"
    out.println " * @date $date"
    out.println " */"
    out.println "@Repository"
    out.println "public interface ${baseName}Mapper {" // 可自定义
    out.println ""
    out.println "    int insert(${baseName} entity);"
    out.println ""
    out.println "    int insertBatch(@Param(\"list\") List<${baseName}> list);"
    out.println ""
    out.println "    int deleteById(Integer id);"
    out.println ""
    out.println "    int updateById(${baseName} entity);"
    out.println ""
    out.println "    ${baseName} getById(Integer id);"
    out.println ""
    out.println "    List<${baseName}> getList(${baseName} entity);"
    out.println ""
    out.println "}"
}

def javaName(str, capitalize) {
    def s = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)
            .collect { Case.LOWER.apply(it).capitalize() }
            .join("")
            .replaceAll(/[^\p{javaJavaIdentifierPart}[_]]/, "_")
    name = capitalize || s.length() == 1 ? s : Case.LOWER.apply(s[0]) + s[1..-1]
}