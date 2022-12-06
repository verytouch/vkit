package top.verytouch.vkit.mydoc.constant;

import top.verytouch.vkit.mydoc.util.JsonUtil;

import java.util.Map;

/**
 * 一些特殊的类
 *
 * @author verytouch
 * @since 2021-11
 */
public class SpecialClassNames {

    // region web注解
    public static String REST_CONTROLLER = "org.springframework.web.bind.annotation.RestController";
    public static String CONTROLLER = "org.springframework.stereotype.Controller";
    public static String REQUEST_MAPPING = "org.springframework.web.bind.annotation.RequestMapping";
    public static String GET_MAPPING = "org.springframework.web.bind.annotation.GetMapping";
    public static String POST_MAPPING = "org.springframework.web.bind.annotation.PostMapping";
    public static String PUT_MAPPING = "org.springframework.web.bind.annotation.PutMapping";
    public static String PATCH_MAPPING = "org.springframework.web.bind.annotation.PatchMapping";
    public static String DELETE_MAPPING = "org.springframework.web.bind.annotation.DeleteMapping";
    public static String REQUEST_PARAM = "org.springframework.web.bind.annotation.RequestParam";
    public static String REQUEST_PART = "org.springframework.web.bind.annotation.RequestPart";
    public static String REQUEST_PATH = "org.springframework.web.bind.annotation.PathVariable";
    public static String REQUEST_BODY = "org.springframework.web.bind.annotation.RequestBody";
    // endregion

    // region mapping对应的method
    public static Map<String, Object> MAPPING_METHOD_MAP = JsonUtil.newObject()
            .putOne(REQUEST_MAPPING, "")
            .putOne(GET_MAPPING, "GET")
            .putOne(POST_MAPPING, "POST")
            .putOne(PUT_MAPPING, "PUT")
            .putOne(PATCH_MAPPING, "PATCH")
            .putOne(DELETE_MAPPING, "DELETE");
    // endregion

    // region MediaType对应的ContentType
    public static Map<String, Object> MEDIA_TYPE_MAP = JsonUtil.newObject()
            .putOne("MediaType.APPLICATION_FORM_URLENCODED_VALUE", "application/x-www-form-urlencoded")
            .putOne("MediaType.APPLICATION_JSON_VALUE", "application/json")
            .putOne("MediaType.APPLICATION_JSON_UTF8_VALUE", "application/json;charset=UTF-8")
            .putOne("MediaType.APPLICATION_XML_VALUE", "application/xml")
            .putOne("MediaType.APPLICATION_OCTET_STREAM_VALUE", "application/octet-stream")
            .putOne("MediaType.MULTIPART_FORM_DATA_VALUE", "multipart/form-data")
            .putOne("MediaType.IMAGE_JPEG_VALUE", "image/jpeg")
            .putOne("MediaType.IMAGE_PNG_VALUE", "image/png")
            .putOne("MediaType.IMAGE_GIF_VALUE", "image/gif")
            .putOne("MediaType.TEXT_HTML_VALUE", "text/html")
            .putOne("MediaType.TEXT_XML_VALUE", "text/xml")
            .putOne("MediaType.TEXT_PLAIN_VALUE", "text/plain");
    // endregion

    // region swagger注解
    public static String SWAGGER_API = "io.swagger.annotations.Api";
    public static String SWAGGER_API_OPERATION = "io.swagger.annotations.ApiOperation";
    public static String SWAGGER_API_MODEL_PROPERTY = "io.swagger.annotations.ApiModelProperty";
    // endregion

    // region 格式注解
    public static String DATE_FORMAT = "com.fasterxml.jackson.annotation.JsonFormat";
    // endregion

}
