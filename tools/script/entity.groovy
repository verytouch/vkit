import com.intellij.database.model.DasTable
import com.intellij.database.util.Case
import com.intellij.database.util.DasUtil

/*
 * 生成实体类
 * Available context bindings:
 *   SELECTION   Iterable<DasObject>
 *   PROJECT     project
 *   FILES       files helper
 */
packageName = ""
typeMapping = [
        (~/(?i)int/)                      : "Integer",
        (~/(?i)bigint/)                   : "Long",
        (~/(?i)float|double|real/)        : "Double",
        (~/(?i)decimal/)                  : "java.math.BigDecimal",
        (~/(?i)datetime|timestamp/)       : "java.util.Date",
        (~/(?i)date|time/)                : "java.util.Date",
        (~/(?i)/)                         : "String"
]

// 1.配置实体目录，为空时需要在生成的时候手动选择
entityDir = ""
if (entityDir != null && entityDir != '') {
    SELECTION.filter { it instanceof DasTable }.each { generate(it, entityDir) }
} else {
    FILES.chooseDirectoryAndSave("Choose directory", "Choose where to store generated files") { dir ->
        SELECTION.filter { it instanceof DasTable }.each { generate(it, dir) }
    }
}

def generate(table, dir) {
    packageName = dir.toString().replaceAll("[/\\\\]", ".").replaceAll("^.*src(\\.main\\.java\\.)?", "") + ";"
    def className = javaName(table.getName(), true)
    def fields = calcFields(table)
    new File(dir, className + ".java").withPrintWriter("utf-8") { out -> generate(out, className, fields) }
}

def generate(out, className, fields) {
    def date = String.format("%tF %<tT", new Date())
    out.println "package $packageName"
    out.println ""
    out.println "import lombok.Data;"
    out.println ""
    out.println "/**"
    out.println " * @author groovy"
    out.println " * @since $date"
    out.println " */"
    out.println "@Data"
    out.println "public class $className {"
    out.println ""
    fields.each() {
        if (it.comment != null && it.comment != '') {
            out.println "    /**"
            out.println "     * ${it.comment}"
            out.println "     */"
        }
        if (it.annos != "") out.println "    ${it.annos}"
        out.println "    private ${it.type} ${it.name};"
        out.println ""
    }
    out.println "}"
}

def calcFields(table) {
    DasUtil.getColumns(table).reduce([]) { fields, col ->
        def spec = Case.LOWER.apply(col.getDataType().getSpecification())
        def typeStr = typeMapping.find { p, t -> p.matcher(spec).find() }.value
        fields += [[
                           name   : javaName(col.getName(), false),
                           type   : typeStr,
                           comment: col.getComment(),
                           annos  : ""]]
    }
}

def javaName(str, capitalize) {
    def s = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)
            .collect { Case.LOWER.apply(it).capitalize() }
            .join("")
            .replaceAll(/[^\p{javaJavaIdentifierPart}[_]]/, "_")
    capitalize || s.length() == 1 ? s : Case.LOWER.apply(s[0]) + s[1..-1]
}
