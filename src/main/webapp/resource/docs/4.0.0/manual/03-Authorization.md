权限组件及配置使用
====

### 1. 权限组件介绍

		【资源】(Resource)      ：RBAC模型中的资源，用于权限控制，有按钮、URL、菜单类型
		【角色】（Role）         ：RBAC模型中的角色，用于权限控制，可以分配给帐户或组织
		【菜单】（Menu）         ：菜单，是一种特殊的资源，包含URL，可被权限控制，只可以分配给组织（只允许分配根菜单）
		【组织】（Organization） ：组织，支持多维度，如公司组织结构、生产组织结构、岗位组织结构，可以分配角色及菜单
		【用户】（User）         ：用户，一个用户可以仅对应一个帐户，但并不是所有用户都拥有帐户，用户可以分配给组织，原则上一个用户仅能被一种组织包含
		【帐户】（Account）      ：帐户，一个帐户必须有对应的用户，帐户可以分配角色

		【角色】（Role）              --1----*->              【资源】(Resource) 
		【菜单】（Menu）              --1----1->              【资源】(Resource) 
		【组织】（Organization）      --1----*->              【角色】（Role） 
		【组织】（Organization）      --1----*->              【菜单】（Menu）
		【组织】（Organization）      --1----*->              【用户】（User）
		【帐户】（Account）           --1----1->              【用户】（User）

----------

### 2. 权限配置使用

		配置步骤：
		1、配置资源，URL及菜单资源
		2、配置角色，关联角色与资源
		3、配置菜单，生成一个可访问菜单
		4、配置用户
		5、配置帐户，关联用户与角色
		6、配置组织，关联用户、角色与菜单
		6、切换帐户，登录后只可看到该帐户对应的用户的直属组织机构的菜单

----------

### 3. 权限标签使用

权限标签是基于Freemarker的标签实现，只包含两个Freemarker方法：hasPermit、hasRole

* hasPermit

		判断是否拥有操作【资源】的权限，可以带多个参数，条件同时满足时返回TRUE或者帐户或用户拥有角色为“ADMIN”时返回TRUE。
			<#if hasPermit(url, button, menu)>
				// do something
			</#if>
			或者，下面只拥有其中一个权限即可：
			<#if hasPermit(url) || hasPermit(button) || hasPermit(menu)>
				// do something
			</#if>

* hasRole

		判断是否拥有操作【角色】的权限，可以带多个参数，条件同时满足时返回TRUE或者帐户或用户拥有角色为“ADMIN”时返回TRUE。
			<#if hasRole(roleCode1, roleCode2)>
				// do something
			</#if>
			或者，下面只拥有其中一个角色即可：
			<#if hasRole(roleCode1) || hasRole(roleCode2)>
				// do something
			</#if>

----------

### 4. Shiro权限说明

* Shiro配置文件

		/src/main/resources/META-INF/spring-auth/spring-shiro.xml

* Shiro的URL访问控制

		com.foreveross.common.shiro.ShiroAccessControlFilter
		
		说明：
			对访问的URL进行过滤，对需要授权访问的URL，通过Subject.hasRole()或Subject.isPermitted()进行控制

* Shiro加载角色与资源

		com.foreveross.common.shiro.MonitorRealm
		
		说明：
			加载帐户的角色与资源，其中角色代码全部转换为大写
			帐户的角色包括，帐户本身的角色，帐户对应用户所在直属组织的角色
			帐户的资源包括，帐户的所有角色对应的资源

* Shiro的*号匹配解释器

		com.foreveross.common.shiro.UrlWildcardPermissionResolver
		
		说明：
			默认把资源的匹配处理类转换成com.foreveross.common.shiro.UrlWildcardPermission

* Shiro的*号匹配处理

		com.foreveross.common.shiro.UrlWildcardPermission
		
		说明：
			仅支持*匹配，一个*号可以匹配零到多个字符，包括“/”分隔符
			例1：/*         --匹配-> /xxx/xxxx        --不匹配->a/bc 
			例2：/*/aa      --匹配-> /x/x/x/aa        --不匹配->/aa/b/c 
			例3：/*/a/*/aa  --匹配-> /xx/a/xx/aa      --不匹配->/a/aa 

----------

### 5. 权限组件扩展

* Freemarker 标签扩展

参看Freemarker标签扩展方法，或者参看：org.iff.infra.util.freemarker.model 下的已有的扩展

* Shiro 扩展

参看Shiro的Spring配置，或者参看：com.foreveross.common.shiro下已有的扩展

----------





