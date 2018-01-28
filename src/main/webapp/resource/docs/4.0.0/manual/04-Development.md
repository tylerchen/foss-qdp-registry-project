开发向导
====

### 1. 开发步骤

#### 1.1. 分层Java命名规范

框架分层参看：[框架分层](?md=01-Framework.md)

* 展现层

		{domain.model.name}Controller
		说明：domain.model.name: 领域模型名称，如：User  -->  UserController

* 应用层

		{domain.model.name}Application
		{domain.model.name}ApplicationImpl
		说明：domain.model.name: 领域模型名称，如：User  -->  UserApplication，UserApplicationImpl

* 领域层

		{domain.model.name}
		说明：domain.model.name: 领域模型名称，如：User  -->  User

* 基础设施层VO

		{domain.model.name}VO
		说明：domain.model.name: 领域模型名称，如：User  -->  UserVO

----------


#### 1.2. 创建Mybatis的Mapping文件

		文件规范：
			1、文件目录：/src/main/resources/{project.package.path}/domain/{module}/
				配置文件放到/src/main/resources/下，路径与domain的包路径一致，
				如：/src/main/resources/com/foreveross/auth/domain/auth/
			2、文件命名：{domain.model.name}Mapper.xml，如果要区分不同数据库，需要命名为：{domain.model.name}Mapper-{database.type}.xml
				database.type：h2, mysql, oracle, sqlserver, db2
				如：AuthAccountMapper.xml
			3、namespace：{domain.model.name}，与JAVA的领域模型命名相同
				如：AuthAccount
			4、提供领域模型的resultMap
				如：<resultMap id="AuthAccount" type="com.foreveross.system.domain.auth.AuthAccount">
			5、提供CURD所需的语句
				get{domain.model.name}ById
				pageFind{domain.model.name}
				insert{domain.model.name}
				update{domain.model.name}
				delete{domain.model.name}
			6、命名查询规范
				1） 查询单条结果
				get+对象名+[Map]+By+条件字段名， 如通过菜单id查询菜单【getMenuById】，如果返回集合为Map【getMenuMapById】，多条件如【getMenuByIdAndName】
				
				2） 查询多条结果
				find+对象名+[Map]+By+条件字段名， 如通过菜单name查询菜单【findMenuByName】，如果返回集合为Map【findMenuMapByName】，多条件如【findMenuByNameAndParent】
				建议：建议使用pageFind代替find，pageFind在不传入分页条件时效果与find相同，采用pageFind命名的好处是让使用者在使用该查询时传入分页参数，以免find的结果记录数过大而导致程序崩溃
				
				3） 查询多条结果并分页
				pageFind+对象名+[Map]+By+条件字段名， 如通过菜单name查询菜单【pageFindMenuByName】，如果返回集合为Map【pageFindMenuMapByName】，多条件如【pageFindMenuByNameAndParent】
				
				4） 查询记录数
				count+对象名+By+条件字段名， 如通过菜单name查询菜单总数【countMenuByName】
			
			7、查询条件规范
				1) 区分#{id}与${id}，写法，#{}输出占位符，${}直接输出条件的值
					如：select * from User where id=#{id}，会输出：select * from User where id=?
					如：select * from User where id=${id}，会输出：select * from User where id=123
				2) 单条件，直接使用#{fieldName}，同时指定parameterType，为领域模型的类型
				3) 多条件，#{vo.fieldName}，不需要指定parameterType，在程序中输入查询条件时，需要构造一个Map类型，并且有一个key名称为vo
					如：{'vo':vo}，或：{'vo':vo, 'page':page}
				4) insert, update, delete，传入的parameterType，为领域模型的类型


----------


#### 1.3. 创建领域模型

		1、创建静态领域方法
			get(Domain)
			get(id)
			remove(Domain)
			remove(id)
			remove(ids)
		2、创建领域方法
			add()
			update()
			addOrUpdate()
			remove()
			validate(type)

----------


#### 1.4. 创建VO

		对应该领域模型，如有需要创建对应VO的，则创建相应VO。
		
		为VO添加XML注解，@XmlRootElement，以便于在序列化成XML使用。

----------


#### 1.5. 创建应用层接口

		对应该领域模型，如有需要创建对应的应用层接口的，则创建相应应用层接口。
		
		为应用层接口添加WebService注解，@Produces和@WebService，以便于在生成SOAP或REST风格的WebService时使用。

----------


#### 1.6. 创建应用层实现

		已有应用层接口的，需要对应的应用层实现。
		
		应用层实现需要添加Inject注解，声明Bean的名称，如：@Named("authAccountApplication")。
		应用层实现需要添加事务注解，@Transactional，以便于Spring进行事务管理。

----------


#### 1.7. 创建展现层

		创建展现层类，需要继承：com.foreveross.common.web.BaseController
		为展现层类添加注解：@Controller，@RequestMapping
		其中@RequestMapping映射的路径为：/{module}/{domain.name}，全部字符小写
		
		添加方法，方法模板：
		@ResponseBody
		@RequestMapping("/{function.name}.do")
		public Map<String, Object> {function.name}({domain.name}VO vo, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) throws IOException {
			try {
				ValidateHelper validate = validate(vo, "add");
				if (validate.hasNoErrors()) {
					functionApplication.addFunction(vo);
					return success("操作成功！");
				} else {
					return error(validate.joinErrors("\n"));
				}
			} catch (Exception e) {
				return error(e);
			}
		}
		方法的编写规范：
			1、@RequestMapping中的路径为：方法名+“.do”
			2、方法返回类型为：Map<String, Object>
			3、方法名称原则上不作限定
			4、第一个参数如果需要返回一个或多个VO的值，可以在前面指定
			5、其他的参数需要以上面的相同
			6、方法体需要使用try{}catch{}，处理代码必须放在try语句块内
			7、验证统一使用ValidateHelper
			8、代码正确执行时返回success，执行错误时或验证不通过时返回error

----------


#### 1.8. 编写页面

* 创建页面规范

		1、页面相对路径要与Controller类的@RequestMapping的映射路径相同，
			如：@RequestMapping("/core/function")，对应页面为：/WEB-INF/pages/core/function
		2、页面命名
			字段描述：_fieldDesc.ftl
			列表页面：{domain.name.lowercase}_list.html
			新增页面：{domain.name.lowercase}_add.html
			修改页面：{domain.name.lowercase}_edit.html
			详细页面：{domain.name.lowercase}_info.html
			其他页面：{domain.name.lowercase}_{op.type}.html
			
			后缀命名规范：用于显示的页面以“.html”为文件扩展名，其他页面模板、定义、宏、方法等以“.ftl”结尾。

* 页面组成规范

		-------------------------------------------------
		|           1、include 模板                      |
		-------------------------------------------------
		|           2、数据加载或处理代码                   |
		-------------------------------------------------
		|           3、加载CSS， <@csstm/>                |
		-------------------------------------------------
		|           4、加载JS， <@jstm/>                  |
		-------------------------------------------------
		|           5、页面布局与HTML代码                  |
		-------------------------------------------------

* 列表页面编写

		1、引用Module的页面模板及字段描述
			<#include "/auth/_layout.ftl" /><#include "_fieldDesc.html" />

		2、数据加载或处理代码，加载数据
			<@mvel var="page">return org.iff.infra.util.mybatis.plugin.Page.pageable(10,${helper('NumberHelper').getLong(pageNo!'1',1)},0,null);</@mvel>
			<@mvel var="vo">return new com.foreveross.system.infra.vo.auth.AuthAccountVO();</@mvel>
			${vo.setLoginEmail(loginEmail!'')}
			<#assign page=bean('authAccountApplication').pageFindAuthAccountMap(vo, page) />
			
			这里使用mvel来创建page, vo参数，调用后台方法返回数据列表
		
		3、数据加载或处理代码，操作，并且带有权限控制
			<#assign action_li_html>
				<li><a href="javascript:;" onclick="window.history.back();"><em class="ico-back"></em>返回</a></li>
				<#if hasPermit(getActionUrl(requestURI))>
				<li><a href="${requestURI}"><em class="ico-list"></em>列表</a></li>
				<li><a href="javascript:;" class="pol-dialog" pol-type="search" pol-el="searchForm" pol-width="350px"  pol-height="250px"><em class="ico-search"></em>搜索</a></li>
				</#if>
				<#if hasPermit(getActionUrl(requestURI,"add"))>
				<li><a href="javascript:;" class="pol-dialog" pol-type="add" pol-cb="reloadPage" pol-width="650px"><em class="ico-add"></em>添加</a></li>
				</#if>
				<#if hasPermit(getActionUrl(requestURI,"edit"))>
				<li><a href="javascript:;" class="pol-dialog" pol-type="edit" pol-cb="reloadPage" pol-width="650px" pol-validate="selectOne"><em class="ico-edit"></em>编辑</a></li>
				</#if>
				<#if hasPermit(getActionUrl("/auth/authaccount/deleteAuthAccount.do"))>
				<li><a href="${ctx}/auth/authaccount/deleteAuthAccount.do" class="pol-dialog" pol-type="delete" pol-validate="selectOne"><em class="ico-del"></em>删除</a></li>
				</#if>
				<#if hasPermit(getActionUrl(requestURI,"info"))>
				<li><a href="javascript:;" class="pol-dialog" pol-type="info" pol-width="650px" pol-validate="selectOne"><em class="ico-logs"></em>查看</a></li>
				</#if>
				<#if hasPermit(getActionUrl(requestURI,"changePassword"))>
				<li><a href="javascript:;" class="pol-dialog" pol-type="changePassword" pol-width="650px" pol-validate="selectOne"><em class="ico-logs"></em>修改密码</a></li>
				</#if>
			</#assign>

		4、加载CSS， <@csstm/>
			CSS的加载必须在生成或编写HTML代码前
		5、加载JS， <@jstm/>
			JS的加载可以在任何地方，页面会自动把JS文件和代码按加载的顺序添加到页面最后
		6、页面布局与HTML代码
			<@layout_index title="${mainTitle}-账号管理" action_title="账号管理" action_li_html=action_li_html>
				<@page_list page=page url=helper('ActionHelper').fixQueryString("${requestUrl}","pageNo") />
				<@hsearch name="searchForm" />
			</@layout_index>

* 新增页面编写

		1、引用Module的页面模板及字段描述
			<#include "/auth/_layout.ftl" /><#include "_fieldDesc.html" />
		2、页面布局与HTML代码
			<@layout_dialog title="后台管理系统-资源管理-资源编辑">
				<@hform name="firstform" action=ctx+"/auth/sysdictionary/addSysDictionary.do" title="添加数据字典" grid=1/>
			</@layout_dialog>

* 修改页面编写

		1、引用Module的页面模板及字段描述
			<#include "/auth/_layout.ftl" /><#include "_fieldDesc.html" />
		2、数据加载或处理代码，加载数据
			<#assign vo=bean('sysDictionaryApplication').getSysDictionaryById(id!'') />
		3、页面布局与HTML代码
			<@layout_dialog title="后台管理系统-资源管理-资源编辑">
				<@hform name="firstform" data=vo action=ctx+"/auth/sysdictionary/editSysDictionary.do" title="修改数据字典" grid=1/>
			</@layout_dialog>

* 信息页面编写

		1、引用Module的页面模板及字段描述
			<#include "/auth/_layout.ftl" /><#include "_fieldDesc.html" />
		2、数据加载或处理代码，加载数据
			<#assign vo=bean('sysDictionaryApplication').getSysDictionaryById(id!'') />
		3、页面布局与HTML代码
			<@layout_dialog title="后台管理系统-帐户列表">
				<@showInfo data=vo title="账号信息显示" />
			</@layout_dialog>


* 编写字段描述模板 _fieldDesc.html

		字段描述模板，_fieldDesc.html，非常重要，页面的表单与列表都使用字面描述模板。
		关于组件：<@fieldDesc/> 的描述请参看组件的使用说明。

		该页面编写说明：
			1、 列表页面必须有一个ID字段，列表页面会默认第一个：type="hidden" ，的fieldDesc为主键，
			   该主键用于列表页面的选择框被选中或者该行记录被高亮选中时，返回记录的主键。对于复合主键的场景，
			   需要先对记录进行处理，生成一个复合主键才能被支持。

----------

#### 1.8. 国际化支持

----------

#### 1.9. 单元测试

----------

#### 1.10. 记录分页

----------




















