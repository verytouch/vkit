package top.verytouch.vkit.rabc.oauth2;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import top.verytouch.vkit.common.base.ApiCode;
import top.verytouch.vkit.common.base.Response;
import top.verytouch.vkit.common.util.JsonUtils;
import top.verytouch.vkit.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 认证异常的序列化
 *
 * @author verytouch
 * @since 2021/5/13 15:10
 */
@Slf4j
public class OauthExceptionSerializer extends StdSerializer<OauthException> {

    protected OauthExceptionSerializer() {
        super(OauthException.class);
    }

    @Override
    public void serialize(OauthException value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        log.error("认证失败", value);
        gen.writeObject(Response.error(StringUtils.substring(value.getMessage(), 0, 50)));
    }

    /**
     * 异常处理方法
     */
    public static void exceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
        log.error(request.getRequestURI(), e);
        ApiCode error;
        int status;
        if (e instanceof AuthenticationException) {
            error = ApiCode.SYS_UNAUTHENTICATED;
            status = 401;
        } else if (e instanceof AccessDeniedException) {
            error = ApiCode.SYS_FORBIDDEN;
            status = 403;
        } else {
            status = 500;
            error = ApiCode.ERROR;
        }
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write(JsonUtils.toJson(Response.error(error.getCode(), error.getDesc())));
        writer.flush();
        writer.close();
    }
}
