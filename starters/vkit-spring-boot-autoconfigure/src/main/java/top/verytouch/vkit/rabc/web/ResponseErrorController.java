package top.verytouch.vkit.rabc.web;

import top.verytouch.vkit.common.base.ApiCode;
import top.verytouch.vkit.common.base.Response;
import top.verytouch.vkit.common.util.JsonUtils;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 错误处理，包括401/404
 *
 * @author verytouch
 * @since 2021/5/17 17:06
 */
public class ResponseErrorController extends BasicErrorController {

    public ResponseErrorController(ErrorAttributes errorAttributes, ErrorProperties errorProperties) {
        super(errorAttributes, errorProperties);
    }

    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Map<String, Object> info = getErrorAttributes(request, isIncludeStackTrace(request, MediaType.ALL));
        HttpStatus status = getStatus(request);
        ApiCode apiCode;
        switch (status) {
            case UNAUTHORIZED:
                apiCode = ApiCode.SYS_UNAUTHENTICATED;
                break;
            case FORBIDDEN:
                apiCode = ApiCode.SYS_FORBIDDEN;
                break;
            case NOT_FOUND:
                apiCode = ApiCode.SYS_NO_HANDLER;
                break;
            default:
                apiCode = ApiCode.ERROR;
        }
        Response<String> body = Response.of(apiCode).setExtra(info);
        return new ResponseEntity<>(JsonUtils.mapFromJson(JsonUtils.toJson(body)), status);
    }
}
