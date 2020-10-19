import com.intellij.database.model.DasTable
import com.intellij.database.util.Case
import com.intellij.database.util.DasUtil

/*
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

FILES.chooseDirectoryAndSave("Choose directory", "Choose where to store generated files") { dir ->
    SELECTION.filter { it instanceof DasTable }.each { generate(it, dir) }
}

// 固定entity目录可以使用下面的函数，需要注释上面的选择目录的语句
// generateEntity()

def generateEntity() {
    // 配置entity目录
    def dir = System.getProperty("user.dir") + "\\src\\main\\java\\com\\verytouch\\vkit\\entity"
    SELECTION.filter { it instanceof DasTable }.each { generate(it, dir) }
}

def generate(table, dir) {
    packageName = dir.toString().replaceAll("[/\\\\]", ".").replaceAll("^.*src(\\.main\\.java\\.)?", "") + ";"
    def className = javaName(table.getName(), true)
    def fields = calcFields(table)
    new File(dir, className + ".java").withPrintWriter { out -> generate(out, className, fields) }
}

def generate(out, className, fields) {
    def date = String.format("%tF %<tT", new Date())
    out.println "package $packageName"
    out.println ""
    out.println "import lombok.Data;"
    out.println ""
    out.println "/**"
    out.println " * @author generator"
    out.println " * @date $date"
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
