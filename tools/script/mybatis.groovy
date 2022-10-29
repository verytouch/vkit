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
        (~/(?i)int/)                      : "long",
        (~/(?i)float|double|decimal|real/): "double",
        (~/(?i)datetime|timestamp/)       : "java.sql.Timestamp",
        (~/(?i)date/)                     : "java.sql.Date",
        (~/(?i)time/)                     : "java.sql.Time",
        (~/(?i)/)                         : "String"
]

FILES.chooseDirectoryAndSave("Choose directory", "Choose where to store generated files") { dir ->
    SELECTION.filter { it instanceof DasTable }.each { generate(it, dir) }
}

def generate(table, dir) {
    def className = javaName(table.getName(), true)
    def fields = calcFields(table)
    new File(dir, className + ".java").withPrintWriter { out -> {
        generateInterface(out, className)
    }}
}

static def generateInterface(out, className) {
    out.println "package $packageName"
    out.println ""
    out.println "import org.springframework.stereotype.Repository;"
    out.println "import org.apache.ibatis.annotations.Param;"
    out.println ""
    out.println "@Repository"
    out.println "public class ${className}Mapper {"
    out.println ""
    out.println "    int insert(${className} entity);"
    out.println ""
    out.println "    int insertBatch(@Param(\"list\") List<${className}> list);"
    out.println ""
    out.println "    int deleteById(Object id);"
    out.println ""
    out.println "    int updateById(${className} entity);"
    out.println ""
    out.println "    ${className} getById(Object id);"
    out.println ""
    out.println "    List<${className}> getList();"
    out.println ""
    out.println "}"
}

def generateXml(out, className, fields) {
    out.println "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
    out.println "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">"
    out.println "<mapper namespace=\"${className}Mapper\">"
    out.println ""
    out.println "    <resultMap id=\"BaseResultMap\" type=\"${className}\">"
    out.println "        <id column=\"ID\" jdbcType=\"INTEGER\" property=\"id\" />"
    fields.each() {
        out.println "        <result column=\"${it.col}\" jdbcType=\"${it.type}\" property=\"${it.name}\" />"
    }
    out.println "    </resultMap>"
    out.println "    "
    out.println "    <sql id=\"BaseColumnList\">"
    fields.each() {
        out.println "        `${it.col}`,"
    }
    out.println "    </sql>"
    out.println "    "
    out.println "</mapper>"
}

def calcFields(table) {
    DasUtil.getColumns(table).reduce([]) { fields, col ->
        def spec = Case.LOWER.apply(col.getDataType().getSpecification())
        def typeStr = typeMapping.find { p, t -> p.matcher(spec).find() }.value
        fields += [[
                           col : col.getName(),
                           name : javaName(col.getName(), false),
                           type : typeStr,
                           annos: ""]]
    }
}

def javaName(str, capitalize) {
    def s = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)
            .collect { Case.LOWER.apply(it).capitalize() }
            .join("")
            .replaceAll(/[^\p{javaJavaIdentifierPart}[_]]/, "_")
    capitalize || s.length() == 1? s : Case.LOWER.apply(s[0]) + s[1..-1]
}
