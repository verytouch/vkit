## 一、生命周期
* SqlSessionFactoryBuilder：用于创建SqlSessionFactory，瞬时

* SqlSessionFactory：相当于连接池，长存

* SqlSession：相当于一个连接，非线程安全，请求处理完关掉

* Mapper：由SqlSession创建后跟随SqlSession



## 二、缓存机制

* 一级缓存：默认开启，SqlSession级别，执行更新语句被清空

* 二级缓存：默认不开启，SqlSessionFactory级别



## 三、TypeHandler
 ```java
import com.hello.enums.Gender;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedJdbcTypes(JdbcType.TINYINT)
@MappedTypes(Gender.class)
public class GenderHandler extends BaseTypeHandler<Gender> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Gender parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public Gender getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return Gender.fromCode(rs.getInt(columnName));
    }

    @Override
    public Gender getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return Gender.fromCode(rs.getInt(columnIndex));
    }

    @Override
    public Gender getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return Gender.fromCode(cs.getInt(columnIndex));
    }
}
 ```



## 四、拦截器

```java
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;
import java.util.Properties;

@Intercepts({
    @Signature(type = Executor.class, method = "query", args = {
        MappedStatement.class,
        Object.class,
        RowBounds.class,
        ResultHandler.class
    })
})
@Component
public class PageInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        System.out.println(properties.toString());
    }
}
```

