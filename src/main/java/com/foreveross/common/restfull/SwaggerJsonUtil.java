/*******************************************************************************
 * Copyright (c) Dec 27, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.restfull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.iff.infra.util.GsonHelper;
import org.iff.infra.util.MapHelper;

/**
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Dec 27, 2017
 */
public class SwaggerJsonUtil {
	private static final SwaggerJsonUtil me = new SwaggerJsonUtil();

	public static void main(String[] args) {
		Object o = create().title("Uber API").description("Move your app forward with the Uber API").version("1.0.0")
				.host("api.uber.com").schemes("https").basePath("/v1").consumes("application/json")
				.produces("application/json").get("/product").summary("Product Types")
				.pathDescription("The Products endpoint...").parameter("latitude").inParam()
				.parameterDescription("Latitude component of location.").parameterRequired(true)
				.parameterType(Double.class).parameterFormat(null).collectionFormat(null).addMoreParameter()
				.parameter("longitude").inParam().parameterDescription("Longitude component of location.")
				.parameterRequired(true).parameterType(Double.class).parameterFormat(null).collectionFormat(null)
				.paramDone().tags("Products").response200("An array of products").responseSchemaType("array")
				.responseSchemaRef("#/definitions/Product").addMoreResponse().responseDefault("An array of products")
				.responseSchemaType("array").responseSchemaRef("#/definitions/Product").responseDone()
				.security("oauth", "basic").securityDone().addMorePaths().delete("/product").summary("Product Types")
				.pathDescription("The Products endpoint...").parameter("latitude").inParam()
				.parameterDescription("Latitude component of location.").parameterRequired(true)
				.parameterType(Double.class).parameterFormat(null).collectionFormat(null).addMoreParameter()
				.parameter("longitude").inParam().parameterDescription("Longitude component of location.")
				.parameterRequired(true).parameterType(Double.class).parameterFormat(null).collectionFormat(null)
				.paramDone().tags("Products").response200("An array of products").responseSchemaType("array")
				.responseSchemaRef("#/definitions/Product").addMoreResponse().responseDefault("An array of products")
				.responseSchemaType("array").responseSchemaRef("#/definitions/Product").responseDone()
				.security("oauth", "basic").securityDone().pathsDone().securityDefinition("oauth")
				.securityType("oauth2").securityName("api_key").securityIn("header")
				.securityAuthorizationUrl(
						"https://twitter.com/oauth/authorize/?client_id=CLIENT-ID&redirect_uri=REDIRECT-URI&response_type=token")
				.securityFlow("implicit").securityScope("basic", "to read any and all data related to twitter")
				.securityScopeDone().securityDefinitionDone().definition("Product").definitionType("object")
				.definitionProp("product_id").definitionPropType(String.class).definitionPropFormat(null)
				.definitionPropItemsType(null).definitionPropItemsRef(null)
				.definitionPropDescription("Unique identifier...").addMoreDefinitionProp()
				.definitionProp("display_name").definitionPropType(String.class).definitionPropFormat(null)
				.definitionPropItemsType(null).definitionPropItemsRef(null)
				.definitionPropDescription("Display name of product.").definitionProDone().addMoreDefinition()
				.definition("User").definitionType("object").definitionProp("user_id").definitionPropType(String.class)
				.definitionPropFormat(null).definitionPropItemsType(null).definitionPropItemsRef(null)
				.definitionPropDescription("Unique identifier...").addMoreDefinitionProp().definitionProp("user_name")
				.definitionPropType(String.class).definitionPropFormat(null).definitionPropItemsType(null)
				.definitionPropItemsRef(null).definitionPropDescription("Display name of user.").definitionProDone()
				.definitionDone().build();
		System.out.println(GsonHelper.toJsonString(o));
		try {
			FileUtils.write(new File("/Users/zhaochen/Desktop/swagger.json"), GsonHelper.toJsonString(o));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static InfoTitle create() {
		SwaggerJson json = me.new SwaggerJson();
		json.json.put("swagger", "2.0");
		return json;
	}

	public interface InfoTitle {
		InfoDescription title(String title);
	}

	public interface InfoDescription {
		InfoVersioin description(String description);
	}

	public interface InfoVersioin {
		Host version(String version);
	}

	public interface Host {
		Schemes host(String host);
	}

	public interface Schemes {
		BasePath schemes(String... http2s);
	}

	public interface BasePath {
		Consumes basePath(String basePath);
	}

	public interface Consumes {
		Produces consumes(String... consumes);
	}

	public interface Produces {
		Paths produces(String... produces);
	}

	public interface Paths {
		Summary get(String path);

		Summary post(String path);

		Summary put(String path);

		Summary delete(String path);
	}

	public interface Summary {
		PathDescription summary(String summary);
	}

	public interface PathDescription {
		Parameter pathDescription(String description);
	}

	public interface Parameter {
		ParameterIn parameter(String name);
	}

	public interface ParameterIn {
		ParameterDescription inPath();

		ParameterDescription inParam();
	}

	public interface ParameterDescription {
		ParameterRequired parameterDescription(String description);
	}

	public interface ParameterRequired {
		ParameterType parameterRequired(boolean required);
	}

	public interface ParameterType {
		ParameterFormat parameterType(String type);

		ParameterFormat parameterType(Class<?> clazz);
	}

	public interface ParameterFormat {
		CollectionFormat parameterFormat(String format);
	}

	public interface CollectionFormat {
		ParameterMore collectionFormat(String collectionFormat);
	}

	public interface ParameterMore {
		Parameter addMoreParameter();

		Tags paramDone();
	}

	public interface Tags {
		Response tags(String... tags);
	}

	public interface Response {
		ResponseSchemaType response200(String description);

		ResponseSchemaType response400(String description);

		ResponseSchemaType response404(String description);

		ResponseSchemaType responseDefault(String description);
	}

	public interface ResponseSchemaType {
		ResponseSchemaRef responseSchemaType(String type);
	}

	public interface ResponseSchemaRef {
		ResponseMore responseSchemaRef(String ref);
	}

	public interface ResponseMore {
		Response addMoreResponse();

		Security responseDone();
	}

	public interface Security {
		SecurityMore security(String name, String... auth);
	}

	public interface SecurityMore {

		Security addMoreSecurity();

		PathsMore securityDone();
	}

	public interface PathsMore {

		Paths addMorePaths();

		SecurityDefinition pathsDone();
	}

	public interface SecurityDefinition {
		SecurityType securityDefinition(String name);
	}

	public interface SecurityType {
		SecurityName securityType(String type);
	}

	public interface SecurityName {
		SecurityIn securityName(String name);
	}

	public interface SecurityIn {
		SecurityAuthorizationUrl securityIn(String in);
	}

	public interface SecurityAuthorizationUrl {
		SecurityFlow securityAuthorizationUrl(String authorizationUrl);
	}

	public interface SecurityFlow {
		SecurityScope securityFlow(String flow);
	}

	public interface SecurityScope {
		SecurityScopeMore securityScope(String scope, String description);
	}

	public interface SecurityScopeMore {
		SecurityScope addMoreSecurityScope();

		SecurityDefinitionMore securityScopeDone();
	}

	public interface SecurityDefinitionMore {
		SecurityDefinition addMoreSecurityDefinition();

		Definition securityDefinitionDone();
	}

	public interface Definition {
		DefinitionType definition(String name);
	}

	public interface DefinitionType {
		DefinitionProp definitionType(String type);
	}

	public interface DefinitionProp {
		DefinitionPropType definitionProp(String prop);
	}

	public interface DefinitionPropType {
		DefinitionPropFormat definitionPropType(String type);

		DefinitionPropFormat definitionPropType(Class<?> clazz);
	}

	public interface DefinitionPropFormat {
		DefinitionPropItemsType definitionPropFormat(String format);
	}

	public interface DefinitionPropItemsType {
		DefinitionPropItemsRef definitionPropItemsType(String type);
	}

	public interface DefinitionPropItemsRef {
		DefinitionPropDescription definitionPropItemsRef(String ref);
	}

	public interface DefinitionPropDescription {
		DefinitionPropMore definitionPropDescription(String description);
	}

	public interface DefinitionPropMore {
		DefinitionProp addMoreDefinitionProp();

		DefinitionMore definitionProDone();
	}

	public interface DefinitionMore {
		Definition addMoreDefinition();

		Builder definitionDone();
	}

	public interface Builder {

		Map<String, Object> build();
	}

	public class SwaggerJson implements InfoTitle, InfoDescription, InfoVersioin, Host, Schemes, BasePath, Consumes,
			Produces, Paths, Summary, PathDescription, Parameter, ParameterIn, ParameterDescription, ParameterRequired,
			ParameterType, ParameterFormat, CollectionFormat, ParameterMore, Tags, Response, ResponseMore,
			ResponseSchemaType, ResponseSchemaRef, Security, SecurityMore, PathsMore, SecurityDefinition, SecurityType,
			SecurityName, SecurityIn, SecurityAuthorizationUrl, SecurityFlow, SecurityScope, SecurityScopeMore,
			SecurityDefinitionMore, Definition, DefinitionType, DefinitionProp, DefinitionPropType,
			DefinitionPropFormat, DefinitionPropItemsType, DefinitionPropItemsRef, DefinitionPropDescription,
			DefinitionPropMore, DefinitionMore, Builder {

		private Map parameterType = MapHelper.toMap(boolean.class, "boolean", Boolean.class, "boolean", byte.class,
				"integer", Byte.class, "integer", short.class, "integer", Short.class, "integer", int.class, "integer",
				Integer.class, "integer", long.class, "integer", Long.class, "integer", float.class, "number",
				Float.class, "number", double.class, "number", Double.class, "number", BigDecimal.class, "number",
				BigInteger.class, "number", char.class, "string", Character.class, "string", String.class, "string",
				Date.class, "string", Timestamp.class, "string", Array.class, "array", Object.class, "object");
		private Map parameterFormat = MapHelper.toMap(boolean.class, "", Boolean.class, "", byte.class, "", Byte.class,
				"", short.class, "", Short.class, "", int.class, "int32", Integer.class, "int32", long.class, "int64",
				Long.class, "int64", float.class, "float", Float.class, "float", double.class, "double", Double.class,
				"double", BigDecimal.class, "", BigInteger.class, "", char.class, "", Character.class, "", String.class,
				"", Date.class, "date-time", Timestamp.class, "date-time", Array.class, "", Object.class, "");

		private Map<String, Object> json = new LinkedHashMap<String, Object>();

		private List<Object[]> sessions = new ArrayList<Object[]>();//[name, parent, current, key]

		public Map<String, Object> build() {
			root();
			return json;
		}

		public Builder definitionDone() {
			return root();
		}

		public Definition addMoreDefinition() {//path=/definitions
			return root().map("definitions");
		}

		public DefinitionMore definitionProDone() {//path=/definitions/#name
			return parent("def");
		}

		public DefinitionProp addMoreDefinitionProp() {//path=/definitions/#name/properties
			return parent("properties");
		}

		public DefinitionPropMore definitionPropDescription(String description) {//path=/definitions/#name/properties/#prop
			return notEmptyPut("description", description).parent("prop");
		}

		public DefinitionPropDescription definitionPropItemsRef(String ref) {//path=/definitions/#name/properties/#prop/items/ref
			return notEmptyPut("ref", ref);
		}

		public DefinitionPropItemsRef definitionPropItemsType(String type) {//path=/definitions/#name/properties/#prop/items/type
			return map("items").notEmptyPut("type", type);
		}

		public DefinitionPropItemsType definitionPropFormat(String format) {//path=/definitions/#name/properties/#prop/{format}
			return notEmptyPut("format", format);
		}

		public DefinitionPropFormat definitionPropType(String type) {//path=/definitions/#name/properties/#prop/{type}
			return notEmptyPut("type", type);
		}

		public DefinitionPropFormat definitionPropType(Class<?> clazz) {//path=/definitions/#name/properties/#prop/{type,format}
			if (clazz.isArray()) {
				notEmptyPut("type", parameterType.get(Array.class).toString());
				notEmptyPut("format", parameterFormat.get(Array.class).toString());
			} else if (parameterType.containsKey(clazz)) {
				notEmptyPut("type", parameterType.get(clazz).toString());
				notEmptyPut("format", parameterFormat.get(clazz).toString());
			} else {
				notEmptyPut("type", Object.class.toString());
				notEmptyPut("format", parameterFormat.get(Object.class).toString());
			}
			return this;
		}

		public DefinitionPropType definitionProp(String prop) {//path=/definitions/#name/properties/#prop 
			return map("properties").map(prop, "prop");
		}

		public DefinitionProp definitionType(String type) {//path=/definitions/#name/type
			return notEmptyPut("type", type);
		}

		public DefinitionType definition(String name) {//path=/definitions/#name
			return root().map("definitions").map(name, "def");
		}

		public Definition securityDefinitionDone() {//path=/
			return root();
		}

		public SecurityDefinition addMoreSecurityDefinition() {//path=/securityDefinitions
			return parent("securityDefinitions");
		}

		public SecurityDefinitionMore securityScopeDone() {//path=/securityDefinitions/#name
			return parent("sdef");
		}

		public SecurityScope addMoreSecurityScope() {//path=/securityDefinitions/#name/scopes
			return parent("scopes");
		}

		public SecurityScopeMore securityScope(String scope, String description) {//path=/securityDefinitions/#name/scopes/#scope
			return map("scopes").notEmptyPut(scope, description);
		}

		public SecurityScope securityFlow(String flow) {//path=/securityDefinitions/#name/flow
			return notEmptyPut("flow", flow);
		}

		public SecurityFlow securityAuthorizationUrl(String authorizationUrl) {//path=/securityDefinitions/#name/authorizationUrl
			return notEmptyPut("authorizationUrl", authorizationUrl);
		}

		public SecurityAuthorizationUrl securityIn(String in) {//path=/securityDefinitions/#name/in
			return notEmptyPut("in", in);
		}

		public SecurityIn securityName(String name) {//path=/securityDefinitions/#name/name
			return notEmptyPut("name", name);
		}

		public SecurityName securityType(String type) {//path=/securityDefinitions/#name/type
			return notEmptyPut("type", type);
		}

		public SecurityType securityDefinition(String name) {//path=/securityDefinitions/#name
			return root().map("securityDefinitions").map(name, "sdef");
		}

		public SecurityDefinition pathsDone() {//path=/
			return root();
		}

		public Paths addMorePaths() {//path=/
			return root();
		}

		public Security addMoreSecurity() {//path=/paths/#path/get/security
			return parent("security");
		}

		public PathsMore securityDone() {//path=/paths/#path/get
			return parent("method");
		}

		public SecurityMore security(String name, String... auth) {//path=/paths/#path/get/security/#name[]
			return arr("security").notEmptyPut(name, auth);
		}

		public Response addMoreResponse() {//path=/paths/#path/get/responses
			return parent("responses");
		}

		public Security responseDone() {//path=/paths/#path/get
			return parent("method");
		}

		public ResponseMore responseSchemaRef(String ref) {//path=/paths/#path/get/responses/#response/schema/ref
			return map("schema").notEmptyPut("ref", ref);
		}

		public ResponseSchemaRef responseSchemaType(String type) {//path=/paths/#path/get/responses/#response/schema/type
			return map("schema").notEmptyPut("type", type);
		}

		public ResponseSchemaType response200(String description) {//path=/paths/#path/get/responses/200/description
			return map("responses").map("200", "response").notEmptyPut("description", description);
		}

		public ResponseSchemaType response400(String description) {//path=/paths/#path/get/responses/400/description
			return map("responses").map("400", "response").notEmptyPut("description", description);
		}

		public ResponseSchemaType response404(String description) {//path=/paths/#path/get/responses/404/description
			return map("responses").map("404", "response").notEmptyPut("description", description);
		}

		public ResponseSchemaType responseDefault(String description) {//path=/paths/#path/get/responses/default/description
			return map("responses").map("default", "response").notEmptyPut("description", description);
		}

		public Response tags(String... tags) {//path=/paths/#path/get/tags
			return notEmptyPut("tags", tags);
		}

		public Parameter addMoreParameter() {//path=/paths/#path/get/parameters
			return parent("parameters");
		}

		public Tags paramDone() {//path=/paths/#path/get
			return parent("method");
		}

		public ParameterMore collectionFormat(String collectionFormat) {//path=/paths/#path/get/parameters/{collectionFormat}
			return notEmptyPut("collectionFormat", collectionFormat);
		}

		public CollectionFormat parameterFormat(String format) {//path=/paths/#path/get/parameters/{format}
			return notEmptyPut("format", format);
		}

		public ParameterFormat parameterType(String type) {//path=/paths/#path/get/parameters/{type}
			return notEmptyPut("type", type);
		}

		public ParameterFormat parameterType(Class<?> clazz) {//path=/paths/#path/get/parameters/{type,format}
			if (clazz.isArray()) {
				notEmptyPut("type", parameterType.get(Array.class).toString());
				notEmptyPut("format", parameterFormat.get(Array.class).toString());
			} else if (parameterType.containsKey(clazz)) {
				notEmptyPut("type", parameterType.get(clazz).toString());
				notEmptyPut("format", parameterFormat.get(clazz).toString());
			} else {
				notEmptyPut("type", Object.class.toString());
				notEmptyPut("format", parameterFormat.get(Object.class).toString());
			}
			return this;
		}

		public ParameterType parameterRequired(boolean required) {//path=/paths/#path/get/parameters/{required}
			return notEmptyPut("required", required);
		}

		public ParameterRequired parameterDescription(String description) {//path=/paths/#path/get/parameters/{description}
			return notEmptyPut("description", description);
		}

		public ParameterDescription inPath() {//path=/paths/#path/get/parameters/{in}
			return notEmptyPut("in", "path");
		}

		public ParameterDescription inParam() {//path=/paths/#path/get/parameters/{in}
			return notEmptyPut("in", "query");
		}

		public ParameterIn parameter(String name) {//path=/paths/#path/get/parameters/{#name}
			return arr("parameters").notEmptyPut("name", name);
		}

		public Parameter pathDescription(String description) {//path=/paths/#path/get/description
			return notEmptyPut("description", description);
		}

		public PathDescription summary(String summary) {//path=/paths/#path/get/summary
			return notEmptyPut("summary", summary);
		}

		public Summary get(String path) {//path=/paths/#path/get
			return root().map("paths").map(path, "path").map("get", "method");
		}

		public Summary post(String path) {//path=/paths/#path/post
			return root().map("paths").map(path, "path").map("post", "method");
		}

		public Summary put(String path) {//path=/paths/#path/put
			return root().map("paths").map(path, "path").map("put", "method");
		}

		public Summary delete(String path) {//path=/paths/#path/delete
			return root().map("paths").map(path, "path").map("delete", "method");
		}

		public Paths produces(String... produces) {//path=produces[]
			return root().notEmptyPut("produces", produces);
		}

		public Produces consumes(String... consumes) {//path=consumes[]
			return root().notEmptyPut("consumes", consumes);
		}

		public Consumes basePath(String basePath) {//path=basePath
			return root().notEmptyPut("basePath", basePath);
		}

		public BasePath schemes(String... http2s) {//path=schemes[]
			return root().notEmptyPut("schemes", http2s);
		}

		public Schemes host(String host) {//path=host
			return root().notEmptyPut("host", host);
		}

		public Host version(String version) {//path=info/version
			return notEmptyPut("version", version).root();
		}

		public InfoVersioin description(String description) {//path=info/description
			return notEmptyPut("description", description);
		}

		public InfoDescription title(String title) {//path=info/title
			return root().map("info").notEmptyPut("title", title);
		}

		String getSessionName() {
			return (String) sessions.get(sessions.size() - 1)[0];
		}

		Object getSessionParent() {
			return sessions.get(sessions.size() - 1)[1];
		}

		Map<String, Object> getSessionCurrentMap() {
			return (Map<String, Object>) sessions.get(sessions.size() - 1)[2];
		}

		List<Object> getSessionCurrentList() {
			return (List<Object>) sessions.get(sessions.size() - 1)[2];
		}

		Object getSessionCurrent() {
			return sessions.get(sessions.size() - 1)[2];
		}

		String getSessionKey() {
			return (String) sessions.get(sessions.size() - 1)[3];
		}

		boolean isEmpty(Object value) {
			return value == null || (value instanceof String && ((String) value).length() < 1);
		}

		SwaggerJson notEmptyPut(String key, Object value) {
			if (isEmpty(value)) {
				return this;
			}
			Object o = getSessionCurrentMap().get(key);
			if (o == null) {
				getSessionCurrentMap().put(key, value);
			}
			return this;
		}

		SwaggerJson root() {
			parent("root");
			sessions.clear();
			sessions.add(new Object[] { "root", json, json, null });
			return this;
		}

		SwaggerJson parent(String name) {
			{
				StringBuilder sb = new StringBuilder().append(name).append(":");
				for (Object[] os : sessions) {
					sb.append(os[0]).append("-->");
				}
				System.out.println(sb);
			}
			while (sessions.size() > 1) {
				if (getSessionName().equals(name)) {
					break;
				}
				if (getSessionCurrent() != null && getSessionKey() != null) {
					if (getSessionCurrent() instanceof List && getSessionCurrentList().size() > 0) {
						if (getSessionParent() instanceof List) {
							((List<Object>) getSessionParent()).addAll((List<Object>) getSessionCurrentList());
						} else {
							((Map<String, Object>) getSessionParent()).put(getSessionKey(), getSessionCurrent());
						}
					} else if (!getSessionCurrentMap().isEmpty()) {
						if (getSessionParent() instanceof List) {
							((List<Object>) getSessionParent()).add(getSessionCurrent());
						} else {
							((Map<String, Object>) getSessionParent()).put(getSessionKey(), getSessionCurrent());
						}
					}
				}
				sessions.remove(sessions.size() - 1);
			}
			{
				StringBuilder sb = new StringBuilder().append(StringUtils.repeat(" ", name.length())).append(":");
				for (Object[] os : sessions) {
					sb.append(os[0]).append("-->");
				}
				System.out.println(sb);
			}
			return this;
		}

		SwaggerJson map(String key) {
			return map(key, key);
		}

		/** 创建map子节点 **/
		SwaggerJson map(String key, String name) {
			if (!getSessionName().equals(name)) {
				if (getSessionCurrent() instanceof Map && getSessionCurrentMap().containsKey(key)) {//如果子节点已经存在该节点
					sessions.add(new Object[] { name, getSessionCurrent(), getSessionCurrentMap().get(key), key });
				} else {
					sessions.add(new Object[] { name, getSessionCurrent(), new LinkedHashMap<String, Object>(), key });
				}
			}
			return this;
		}

		SwaggerJson arr(String key) {
			return arr(key, key);
		}

		/** 创建arr:[map]子节点，并返回一个map **/
		SwaggerJson arr(String key, String name) {
			if (!getSessionName().equals(name)) {
				sessions.add(new Object[] { name, getSessionCurrent(), new ArrayList<Object>(), key });
				sessions.add(new Object[] { "arrmap", getSessionCurrent(), new LinkedHashMap<String, Object>(), key });
			} else {//如果当前已经是该节点下，就只添加数组
				sessions.add(new Object[] { "arrmap", getSessionCurrent(), new LinkedHashMap<String, Object>(), key });
			}
			return this;
		}

	}
}
