package top.verytouch.vkit.mydoc.builder;import com.intellij.openapi.util.text.StringUtil;import com.intellij.psi.*;import com.intellij.psi.impl.source.PsiClassReferenceType;import com.intellij.psi.util.PsiTypesUtil;import lombok.Getter;import org.apache.commons.collections.CollectionUtils;import org.apache.commons.lang.StringUtils;import top.verytouch.vkit.mydoc.constant.ClassKind;import top.verytouch.vkit.mydoc.model.ApiConfig;import top.verytouch.vkit.mydoc.model.ApiField;import top.verytouch.vkit.mydoc.model.ApiGroup;import top.verytouch.vkit.mydoc.model.ApiOperation;import top.verytouch.vkit.mydoc.util.AnnotationUtil;import top.verytouch.vkit.mydoc.util.ApiUtil;import top.verytouch.vkit.mydoc.util.CommentUtil;import top.verytouch.vkit.mydoc.util.JsonUtil;import java.util.*;import static top.verytouch.vkit.mydoc.constant.ClassKind.*;import static top.verytouch.vkit.mydoc.constant.SpecialClassNames.*;import static top.verytouch.vkit.mydoc.util.AnnotationUtil.*;/** * 通过psi解析源文件，生成api数据 * * @author verytouch * @since 2021-11 */@Getterpublic class ApiBuilder {    /**     * 配置     */    private final ApiConfig config;    /**     * 缓存，避免重复解析     * key为类的全限定名     * value为字段信息     */    private final Map<String, List<ApiField>> classCache;    /**     * 保存类的全限定名     * 解析过程中发现某个class存在，就不解析     * 避免ApiField对象循环依赖，造成死循环     */    private final Set<String> ignoreClassCache;    public ApiBuilder(ApiConfig config) {        this.classCache = new HashMap<>();        this.ignoreClassCache = new HashSet<>();        this.config = config;    }    /**     * 解析类，作为ApiGroup     */    public ApiGroup build(PsiClass psiClass, String selectedMethod) {        if (!hasAnyAnnotation(psiClass, REST_CONTROLLER, CONTROLLER)) {            return null;        }        // 解析类，作为ApiGroup        ApiGroup apiGroup = new ApiGroup();        // 接口名称，Swagger注解 -> 类注释 -> 类名        String name = ApiUtil.getFirstNotBlankValue(psiClass.getName(),                () -> getAnnotationAttribute(psiClass, SWAGGER_API, "value"),                () -> getAnnotationAttribute(psiClass, SWAGGER_API, "tags"),                () -> CommentUtil.getNoSpacesDescription(psiClass, 20));        String path = getAnnotationAttribute(psiClass, REQUEST_MAPPING, "value");        apiGroup.setName(name);        apiGroup.setPath(config.getContextPath() + ApiUtil.getFirstPath(path));        apiGroup.setDesc(CommentUtil.getDescription(psiClass));        apiGroup.setAuthor(String.join(",", CommentUtil.getTagValue(psiClass, "author")));        apiGroup.setOperationList(new LinkedList<>());        // 解析方法        PsiMethod[] psiMethods;        if (StringUtil.isEmptyOrSpaces(selectedMethod)) {            psiMethods = psiClass.getMethods();        } else {            psiMethods = psiClass.findMethodsByName(selectedMethod, true);        }        for (PsiMethod psiMethod : psiMethods) {            ApiOperation apiOperation = buildOperation(psiMethod);            if (apiOperation == null) {                continue;            }            apiGroup.getOperationList().add(apiOperation);        }        return apiGroup;    }    /**     * 解析类     */    public List<ApiField> buildFromPsiClass(PsiType psiType) {        if (psiType == null) {            return null;        }        PsiClass psiClass = PsiTypesUtil.getPsiClass(psiType);        if (psiClass == null) {            return null;        }        // 带包名的        String className = psiType.getCanonicalText();        // 不带包名的        String simpleClassName = psiType.getPresentableText();        // 当前接口已经有该字段说明        if (ignoreClassCache.contains(className)) {            return null;        }        // 先从缓存中取        if (this.classCache.containsKey(className)) {            ignoreClassCache.add(className);            return this.classCache.get(className);        }        List<ApiField> apiFields = new ArrayList<>();        ClassKind classKind = ClassKind.of(psiType);        // 非T这种泛型，提前放入缓存        if (classKind != DIAMOND) {            this.classCache.put(className, apiFields);            this.ignoreClassCache.add(className);        }        // List需要解析里面的泛型        if (classKind == ARRAY) {            ApiField apiField = new ApiField().setType(simpleClassName).setClassKind(classKind);            PsiType diamondType = findDiamondType(psiType);            if (JAVA.isNotMatch(diamondType)) {                apiField.setChildren(buildFromPsiClass(diamondType));            }            apiFields.add(apiField);            return apiFields;        }        if (classKind.isIgnoreChildren()) {            ApiField filed = new ApiField()                    .setType(classKind == ENUM ? "Enum" : simpleClassName)                    .setClassKind(classKind);            apiFields.add(filed);            return apiFields;        }        // 解析里面的属性，包括继承过来的        for (PsiField psiField : psiClass.getAllFields()) {            if (ApiUtil.isStatic(psiField)) {                continue;            }            String hidden = getAnnotationAttribute(psiField, SWAGGER_API_MODEL_PROPERTY, "hidden");            if (Boolean.parseBoolean(hidden)) {                continue;            }            ApiField apiField = buildFromPsiField(psiField, psiType);            if (apiField == null) {                continue;            }            apiFields.add(apiField);        }        return apiFields;    }    /**     * 解析方法，作为ApiOperation     *     * @param psiMethod 方法     */    private ApiOperation buildOperation(PsiMethod psiMethod) {        // 不要私有方法和静态方法        if (ApiUtil.isPrivate(psiMethod) || ApiUtil.isStatic(psiMethod)) {            return null;        }        // 获取RequestMapping等Mapping注解，如果没有，认为不是一个接口        String[] pathAndMethod = AnnotationUtil.getApiPathAndMethod(psiMethod);        if (pathAndMethod == null) {            return null;        }        String name = ApiUtil.getFirstNotBlankValue(psiMethod.getName(),                () -> getAnnotationAttribute(psiMethod, SWAGGER_API_OPERATION, "value"),                () -> CommentUtil.getNoSpacesDescription(psiMethod, 20));        ApiOperation apiOperation = new ApiOperation();        apiOperation.setPsiMethod(psiMethod);        apiOperation.setName(name);        apiOperation.setPath(pathAndMethod[0]);        apiOperation.setMethod(pathAndMethod[1]);        apiOperation.setContentType(pathAndMethod[2]);        apiOperation.setDesc(CommentUtil.getDescription(psiMethod));        apiOperation.setAuthor(String.join(",", CommentUtil.getTagValue(psiMethod, "author")));        ignoreClassCache.clear();        apiOperation.setRequestBody(buildRequestBody(psiMethod.getParameterList()));        if (CollectionUtils.isNotEmpty(apiOperation.getRequestBody())) {            apiOperation.setContentType("application/json");            if (config.isShowExample()) {                Object requestBody = ApiUtil.buildBodyExample(apiOperation.getRequestBody());                apiOperation.setRequestBodyExample(JsonUtil.toJson(requestBody, true, true));            }        }        ignoreClassCache.clear();        apiOperation.setResponseBody(buildFromPsiClass(psiMethod.getReturnType()));        if (config.isShowExample() && CollectionUtils.isNotEmpty(apiOperation.getResponseBody())) {            Object responseBody = ApiUtil.buildBodyExample(apiOperation.getResponseBody());            apiOperation.setResponseBodyExample(JsonUtil.toJson(responseBody, true, true));        }        ignoreClassCache.clear();        apiOperation.setRequestParam(buildFromRequestParam(psiMethod.getParameterList()));        ignoreClassCache.clear();        apiOperation.setPathVariable(buildFromParameter(psiMethod.getParameterList(), REQUEST_PATH));        ignoreClassCache.clear();        apiOperation.setRequestFile(buildRequestFile(psiMethod.getParameterList()));        if (CollectionUtils.isNotEmpty(apiOperation.getRequestFile())) {            apiOperation.setContentType("multipart/form-data");        }        return apiOperation;    }    /**     * 从参数中解析RequestBody     *     * @param psiParameterList 参数列表     */    private List<ApiField> buildRequestBody(PsiParameterList psiParameterList) {        List<PsiParameter> parameterList = findAnnotatedParameter(psiParameterList, REQUEST_BODY);        if (parameterList.isEmpty() || FILE.isMatch(parameterList.get(0).getType())) {            return null;        }        return buildFromPsiClass(parameterList.get(0).getType());    }    /**     * 从参数中解析文件     *     * @param psiParameterList 参数列表     */    private List<ApiField> buildRequestFile(PsiParameterList psiParameterList) {        List<ApiField> apiFields = new ArrayList<>();        // 描述从方法注释中获取        PsiJavaDocumentedElement psiMethod = (PsiJavaDocumentedElement) psiParameterList.getParent();        Map<String, String> paramDesc = CommentUtil.getParamNameValue(psiMethod);        for (PsiParameter parameter : psiParameterList.getParameters()) {            PsiType parameterType = parameter.getType();            if (FILE.isMatch(parameterType) || (ARRAY.isMatch(parameterType) && FILE.isMatch(findDiamondType(parameterType)))) {                ApiField field = new ApiField()                        .setName(parameter.getName())                        .setType(parameterType.getPresentableText())                        .setClassKind(FILE)                        .setDesc(paramDesc.getOrDefault(parameter.getName(), "文件"));                apiFields.add(field);            }        }        return apiFields;    }    /**     * 从参数中解析RequestParam     * <p>     * 1. @RequestParam注解的参数     * 2. @RequestPart注解的参数     * 3. 没有被spring.web注解的参数     *     * @param psiParameterList 参数列表     */    private List<ApiField> buildFromRequestParam(PsiParameterList psiParameterList) {        List<ApiField> apiFields = new ArrayList<>();        List<ApiField> fromRequestParam = buildFromParameter(psiParameterList, REQUEST_PARAM);        if (CollectionUtils.isNotEmpty(fromRequestParam)) {            apiFields.addAll(fromRequestParam);        }        List<ApiField> fromRequestPart = buildFromParameter(psiParameterList, REQUEST_PART);        if (CollectionUtils.isNotEmpty(fromRequestPart)) {            apiFields.addAll(fromRequestPart);        }        // 描述从方法注释中获取        PsiJavaDocumentedElement psiMethod = (PsiJavaDocumentedElement) psiParameterList.getParent();        Map<String, String> paramDesc = CommentUtil.getParamNameValue(psiMethod);        // 没有被spring.web注解的        for (PsiParameter parameter : psiParameterList.getParameters()) {            boolean webAnnotated = Arrays.stream(parameter.getAnnotations())                    .anyMatch(annotation -> WEB_ANNO.isMatch(annotation.getQualifiedName()));            // 忽略被spring.web注解的和servlet            if (webAnnotated || SERVLET.isMatch(parameter.getType()) || FILE.isMatch(parameter.getType())) {                continue;            }            ClassKind classKind = ClassKind.of(parameter.getType());            if (classKind.isIgnoreChildren()) {                ApiField field = new ApiField()                        .setName(parameter.getName())                        .setType(classKind == ENUM ? "Enum" : parameter.getType().getPresentableText())                        .setClassKind(classKind)                        .setDesc(paramDesc.get(parameter.getName()));                apiFields.add(field);                continue;            }            List<ApiField> fromClass = buildFromPsiClass(parameter.getType());            if (CollectionUtils.isNotEmpty(fromClass)) {                apiFields.addAll(fromClass);            }        }        return apiFields.isEmpty() ? null : apiFields;    }    /**     * 从参数中解析RequestParam     *     * @param psiParameterList 参数列表     * @param annotation       注解，如RequestParam，PathVariable     */    private List<ApiField> buildFromParameter(PsiParameterList psiParameterList, String annotation) {        List<PsiParameter> parameterList = findAnnotatedParameter(psiParameterList, annotation);        if (parameterList.isEmpty()) {            return null;        }        // 描述从方法注释中获取        PsiJavaDocumentedElement psiMethod = (PsiJavaDocumentedElement) psiParameterList.getParent();        Map<String, String> paramDesc = CommentUtil.getParamNameValue(psiMethod);        List<ApiField> apiFields = new ArrayList<>();        for (PsiParameter parameter : parameterList) {            if (FILE.isMatch(parameter.getType())) {                continue;            }            String name = ApiUtil.getFirstNotBlankValue(parameter.getName(),                    () -> getAnnotationAttribute(parameter, annotation, "value"));            Boolean required = REQUEST_PATH.equals(annotation) ||                    Boolean.parseBoolean(getAnnotationAttribute(parameter, REQUEST_PARAM, "required"));            ClassKind classKind = ClassKind.of(parameter.getType());            apiFields.add(new ApiField()                    .setType(classKind == ENUM ? "Enum" : parameter.getType().getPresentableText())                    .setClassKind(classKind)                    .setName(name)                    .setDesc(paramDesc.get(parameter.getName()))                    .setRequired(required));        }        return apiFields;    }    /**     * 解析类的某个属性     *     * @param psiField  属性属性     * @param classType 类信息     */    private ApiField buildFromPsiField(PsiField psiField, PsiType classType) {        if (psiField == null || CommentUtil.hasTag(psiField, "ignore")) {            return null;        }        PsiType fieldType = psiField.getType();        ClassKind classKind = ClassKind.of(fieldType);        String desc = ApiUtil.getFirstNotBlankValue("",                () -> getAnnotationAttribute(psiField, SWAGGER_API_MODEL_PROPERTY, "value"),                () -> CommentUtil.getDescription(psiField));        // 日期格式        if ("Date".equals(fieldType.getPresentableText())) {            String datePattern = getAnnotationAttribute(psiField, DATE_FORMAT, "pattern");            if (StringUtils.isNotBlank(datePattern)) {                if (StringUtils.isNotBlank(desc)) {                    desc += " ";                }                desc += datePattern;            }        }        String required = getAnnotationAttribute(psiField, SWAGGER_API_MODEL_PROPERTY, "required");        ApiField apiField = new ApiField()                .setType(classKind == ENUM ? "Enum" : fieldType.getPresentableText())                .setClassKind(classKind)                .setName(psiField.getName())                .setRequired(Boolean.valueOf(required))                .setMock(CommentUtil.getFirstTagValue(psiField, "mock"))                .setDesc(desc);        if (classKind.isIgnoreChildren()) {            return apiField;        }        // List解析里面的泛型        if (classKind == ARRAY) {            PsiType diamondType = findDiamondType(fieldType);            if (diamondType == null) {                return apiField;            }            // List字段里面如果是T这种的，就从Class上取            if (DIAMOND.isMatch(diamondType)) {                diamondType = findDiamondType(classType);                if (diamondType != null) {                    apiField.setType("List<" + diamondType.getPresentableText() + ">");                }            }            if (ClassKind.of(diamondType).isIgnoreChildren()) {                return apiField;            }            return apiField.setChildren(buildFromPsiClass(diamondType));        }        // 泛型，形如: T field        if (classKind == DIAMOND) {            PsiType diamondType = findDiamondType(classType);            if (diamondType != null) {                apiField.setType(diamondType.getPresentableText())                        .setClassKind(ClassKind.of(diamondType));                if (apiField.getClassKind() != JAVA) {                    List<ApiField> apiFields = buildFromPsiClass(diamondType);                    if (apiFields != null && apiFields.size() == 1 && apiFields.get(0).getName() == null) {                        apiField.setChildren(apiFields.get(0).getChildren());                    } else {                        apiField.setChildren(apiFields);                    }                }            }            return apiField;        }        // 其他类        apiField.setChildren(buildFromPsiClass(fieldType));        return apiField;    }    /**     * 获取泛型的类型，即List<Bean> -> Bean     *     * @param psiType 带泛型的类型     */    private PsiType findDiamondType(PsiType psiType) {        if (!(psiType instanceof PsiClassReferenceType)) {            return null;        }        PsiType[] parameters = ((PsiClassReferenceType) psiType).getParameters();        if (parameters.length == 0) {            return null;        }        return parameters[0];    }}