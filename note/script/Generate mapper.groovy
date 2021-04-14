import com.intellij.database.model.DasTable
import com.intellij.database.util.Case
import com.intellij.database.util.DasUtil

/*
 * Available context bindings:
 *   SELECTION   Iterable<DasObject>
 *   PROJECT     project
 *   FILES       files helper
 */
typeMapping = [
        (~/(?i)int/)                      : "INTEGER",
        (~/(?i)bigint/)                   : "BIGINT",
        (~/(?i)float|double|real/)        : "DOUBLE",
        (~/(?i)decimal/)                  : "DECIMAL",
        (~/(?i)datetime|timestamp/)       : "TIMESTAMP",
        (~/(?i)date/)                     : "TIMESTAMP",
        (~/(?i)time/)                     : "TIMESTAMP",
        (~/(?i)/)                         : "VARCHAR"
]

static def entityPackage() {
    // 为空时：本目录设为所选目录的兄弟目录entity
    // 如所选目录为com.mapper，则本目录为com.entity
    return ""
}

FILES.chooseDirectoryAndSave("Choose directory", "Choose where to store generated files") { dir ->
    SELECTION.filter { it instanceof DasTable }.each { generate(it, dir) }
}

def generate(table, dir) {
    def baseName = javaName(table.getName(), true)
    def basePackage = dir.toString().split("java.")[1]
            .split(".(dao|mapper)")[0]
            .replaceAll("\\\\", ".")
    new File(dir, baseName + "Mapper.java").withPrintWriter("utf-8") { out -> generateInterface(out, basePackage, baseName) }

    new File(dir, baseName + "Mapper.xml").withPrintWriter("utf-8") { out -> generateXml(table, out, basePackage, baseName) }
}

static def generateInterface(out, basePackage, baseName) {
    def date = String.format("%tF %<tT", new Date())
    def entityPackage = entityPackage()
    def entity
    if (entityPackage != null && entityPackage != '') {
        entity = "${entityPackage}.${baseName}"
    } else {
        entity = "${basePackage}.entity.${baseName}"
    }
    out.println "package ${basePackage}.mapper;"
    out.println ""
    out.println "import ${entity};"
    out.println "import org.apache.ibatis.annotations.Param;"
    out.println "import java.util.List;"
    out.println ""
    out.println "/**"
    out.println " * @author generator"
    out.println " * @date $date"
    out.println " */"
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


def generateXml(table, out, basePackage, baseName) {
    def baseResultMap = 'BaseResultMap'
    def base_Column_List = 'BaseColumnList'
    def tableName = table.getName()
    def fields = calcFields(table)

    def dao = basePackage + ".mapper." + baseName + "Mapper"
    def entity = entityPackage()
    if (entity != null && entity != '') {
        entity = "${entity}.${baseName}"
    } else {
        entity = "${entity}.entity.${baseName}"
    }

    out.println mappingsStart(dao)
    out.println resultMap(baseResultMap, entity, fields)
    out.println sql(fields, base_Column_List)
    out.println insert(tableName, fields, entity)
    out.println insertBatch(tableName, fields, entity)
    out.println selectById(tableName, fields, baseResultMap, base_Column_List)
    out.println deleteById(tableName, fields)
    out.println updateById(tableName, fields, entity)
    out.println selectList(tableName, fields, entity, base_Column_List, baseResultMap)
    out.println mappingsEnd()

}

static def notExcluded(name) {
    return !["id", "ID"].contains(name)
}

static def resultMap(baseResultMap, to, fields) {
    def inner = ''
    fields.each() {
        if (notExcluded(it.name)) {
            inner += '\t\t<result column="' + it.sqlFieldName + '" jdbcType="' + it.type + '" property="' + it.name + '" />\n'
        }

    }

    return '''\t<resultMap id="''' + baseResultMap + '''" type="''' + to + '''">
        <id column="ID" jdbcType="INTEGER" property="id" />
''' + inner + '''\t</resultMap>
'''
}

def calcFields(table) {
    DasUtil.getColumns(table).reduce([]) { fields, col ->
        def spec = Case.LOWER.apply(col.getDataType().getSpecification())
        def typeStr = typeMapping.find { p, t -> p.matcher(spec).find() }.value
        fields += [[
                           comment     : col.getComment(),
                           name        : javaName(col.getName(), false),
                           sqlFieldName: col.getName(),
                           type        : typeStr,
                           annos       : ""]]
    }

}

// ------------------------------------------------------------------------ mappings
static def mappingsStart(mapper) {
    return '''<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="''' + mapper + '''">
'''
}

// ------------------------------------------------------------------------ mappings
static def mappingsEnd() {
    return '''</mapper>'''
}

// ------------------------------------------------------------------------ selectById
static def selectById(tableName, fields, baseResultMap, base_Column_List) {
    return '''
    <select id="getById" parameterType="java.lang.Integer" resultMap="''' + baseResultMap + '''">
        select <include refid="''' + base_Column_List + '''" />
        from `''' + tableName + '''`
        where ID = #{id}
    </select>'''
}

// ------------------------------------------------------------------------ insert
static def insert(tableName, fields, parameterType) {
    return '''
    <insert id="insert" parameterType="''' + parameterType + '''" useGeneratedKeys="true" keyProperty="id">
        insert into `''' + tableName + '''` (''' + insertColumnList(fields) + '''
        ) values (''' + insertColumnValue(fields, '') + '''
        )
    </insert>'''

}

// ------------------------------------------------------------------------ insertBatch
static def insertBatch(tableName, fields, parameterType) {
    return '''
    <insert id="insertBatch" parameterType="''' + parameterType + '''" useGeneratedKeys="true" keyProperty="id">
        insert into `''' + tableName + '''` (''' + insertColumnList(fields) + '''
        ) values
        <foreach collection="list" item="item" separator=",">
        (''' + insertColumnValue(fields, 'item.') + '''
        )
        </foreach>
    </insert>'''

}

// ------------------------------------------------------------------------ updateById
static def updateById(tableName, fields, parameterType) {
    return '''
    <update id="updateById" parameterType="''' + parameterType + '''">
        update `''' + tableName + '''` set ''' + updateColumnList(fields) + '''
        where ID = #{id}
    </update>'''
}

// ------------------------------------------------------------------------ deleteById
static def deleteById(tableName, fields) {
    return '''
    <delete id="deleteById" parameterType="java.lang.Integer">
        delete from `''' + tableName + '''`
        where ID = #{id}
    </delete>'''
}

// ------------------------------------------------------------------------ selectList
static def selectList(tableName, fields, parameterType, base_Column_List, baseResultMap) {
    return '''
    <select id="getList" parameterType="''' + parameterType + '''" resultMap="''' + baseResultMap + '''">
        select <include refid="''' + base_Column_List + '''" />
        from `''' + tableName + '''`
        <where>''' + andEqIfNotNull(fields) + '''
        </where>
        order by ID desc
    </select>'''
}

// ------------------------------------------------------------------------ sql
static def sql(fields, base_Column_List) {
    def str = '''\t<sql id="''' + base_Column_List + '''">
@inner@
    </sql> '''

    def inner = ''
    fields.each() {
        inner += ('\t\t`' + it.sqlFieldName + '`,\n')
    }

    return str.replace("@inner@", inner.substring(0, inner.length() - 2))

}

static def andEqIfNotNull(fields) {
    def inner = ''
    fields.each {
        if (notExcluded(it.name)) {
            inner += '''
            <if test="''' + it.name + ''' != null">
            \tand `''' + it.sqlFieldName + '''` = #{''' + it.name + '''}
            </if>'''
        }
    }

    return inner
}

static def insertColumnList(fields) {
    def inner = ''
    fields.each {
        if (notExcluded(it.name)) {
            inner += '''
            `''' + it.sqlFieldName + '''`,'''
        }
    }
    return inner.replaceAll(/(.*),$/, "\$1")
}

static def insertColumnValue(fields, prefix) {
    def inner = ''
    fields.each {
        if (notExcluded(it.name)) {
            inner += '''
            #{''' + prefix + it.name + '''},'''
        }
    }

    return inner.replaceAll(/(.*),$/, "\$1")
}

static def updateColumnList(fields) {
    def inner = ''
    fields.each {
        if (notExcluded(it.name)) {
            inner += '''
            `''' + it.sqlFieldName + '''` = #{''' + it.name + '''},'''
        }
    }
    return inner.replaceAll(/(.*),$/, "\$1")
}