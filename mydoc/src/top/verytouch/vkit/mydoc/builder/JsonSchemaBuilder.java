package top.verytouch.vkit.mydoc.builder;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.util.PsiTypesUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import top.verytouch.vkit.mydoc.constant.ClassKind;
import top.verytouch.vkit.mydoc.constant.DocType;
import top.verytouch.vkit.mydoc.model.ApiConfig;
import top.verytouch.vkit.mydoc.model.ApiField;
import top.verytouch.vkit.mydoc.util.ApiUtil;
import top.verytouch.vkit.mydoc.util.BuilderUtil;
import top.verytouch.vkit.mydoc.util.JsonUtil;
import top.verytouch.vkit.mydoc.util.JsonUtil.JsonObject;

import java.util.List;

/**
 * 复制为JsonSchema
 *
 * @author verytouch
 * @since 2021-12
 */
public class JsonSchemaBuilder extends DocBuilder {

    private JsonObject<String, Object> schema;

    public JsonSchemaBuilder(AnActionEvent event) {
        super(event, DocType.JSON_SCHEMA);
    }

    @Override
    protected void buildDoc() {
        BuilderUtil.copyToClipboard(schema.toJson());
    }

    @Override
    protected void buildApi() {
        List<PsiClass> currentJavaClass = BuilderUtil.getCurrentJavaClass(event);
        if (CollectionUtils.isEmpty(currentJavaClass)) {
            this.schema = JsonUtil.newObject();
            return;
        }
        ApiConfig config = BuilderUtil.getConfig(event);
        List<ApiField> apiFields = new ApiBuilder(config).buildFromPsiClass(PsiTypesUtil.getClassType(currentJavaClass.get(0)));
        this.schema = buildSchema(apiFields);
    }

    private JsonObject<String, Object> buildSchema(List<ApiField> apiFields) {
        JsonObject<String, Object> properties = JsonUtil.newObject();
        if (apiFields.size() == 1 && apiFields.get(0).getName() == null) {
            return buildField(apiFields.get(0));
        }
        for (ApiField apiField : apiFields) {
            if (StringUtils.isBlank(apiField.getName())) {
                continue;
            }
            properties.putOne(apiField.getName(), buildField(apiField));
        }
        return JsonUtil.newObject()
                .putOne("type", "object")
                .putOne("properties", properties);
    }

    private JsonObject<String, Object> buildField(ApiField field) {
        JsonObject<String, Object> content = JsonUtil.newObject()
                .putOne("type", field.getOpenApiType())
                .putOne("name", field.getName())
                .putOne("description", field.getDesc());
        List<ApiField> children = field.getChildren();
        if (CollectionUtils.isEmpty(children)) {
            return content;
        }
        JsonObject<String, Object> properties = JsonUtil.newObject();
        if (ClassKind.ARRAY == field.getClassKind()) {
            children.forEach(child -> properties.putOne(child.getName(), buildField(child)));
            JsonObject<String, Object> items = JsonUtil.newObject()
                    .putOne("type", "object")
                    .putOne("properties", properties);
            content.putOne("items", items);
        } else {
            children.forEach(child -> properties.putOne(child.getName(), buildField(child)));
            content.putOne("properties", properties);
        }
        return content;
    }
}
