基础框架配置
====

### 1. Spring Framework 配置

Spring Framework 配置目录

		/META-INF/
		|---spring                    : Spring配置总目录，所有root.xml（根配置）只用于引用其他Spring配置
			\---root.xml              : 引用了app, auth, web（除spring-servlet.xml）, ws，等根配置
		|---spring-app                : 应用部份的配置
			|---root.xml              : 根配置
			|---common.xml            : 配置，缓存、工具类、加密解密、应用初始化Bean
			\---data-access.xml       : 配置，Mybatis，事务，数据源
		|---spring-auth               :
			\---spring-shiro.xml      : Shiro相关配置
		|---spring-web                :
			|---root.xml              : 根配置
			|---base.xml              : 只配置了placeholder
			\---spring-servlet.xml    : 配置，SpringMVC，在web.xml中加载
		\---spring-ws                 :
			|---webservice-cxf.xml    : 配置，CXF，会引用本目录下的*-webservice-cxf.xml
			\---*-webservice-cxf.xml  : 每个CXF服务的注册配置

----------

### 2. 缓存配置

Spring配置文件：/META-INF/spring-app/common.xml，默认使用Ehcache。

		<!-- 引用ehCache的配置 -->
		<bean id="cacheManager"
			class="org.springframework.cache.ehcache.EhCacheManagerFactoryBean">
			<property name="configLocation" value="classpath:ehcache.xml" />
		</bean>
		<!-- 定义ehCache的工厂，并设置所使用的Cache name -->
		<bean id="ehCache" class="org.springframework.cache.ehcache.EhCacheFactoryBean">
			<property name="cacheName" value="DEFAULT_CACHE" />
			<property name="cacheManager" ref="cacheManager" />
		</bean>
		<bean id="authCache" class="org.springframework.cache.ehcache.EhCacheFactoryBean">
			<property name="cacheName" value="auth" />
			<property name="cacheManager" ref="cacheManager" />
		</bean>

Ehcache配置文件：ehcache.xml

		<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		         xsi:noNamespaceSchemaLocation="ehcache.xsd" updateCheck="false">
		
			<!-- 硬盘路径 -->
		    <diskStore path="java.io.tmpdir/ehcache"/>
			
			<defaultCache
		            maxElementsInMemory="10000"
		            eternal="false"
		            timeToIdleSeconds="120"
		            timeToLiveSeconds="120"
		            overflowToDisk="false"
		            diskSpoolBufferSizeMB="30"
		            maxElementsOnDisk="10000000"
		            diskPersistent="false"
		            diskExpiryThreadIntervalSeconds="120"
		            memoryStoreEvictionPolicy="LRU"
		            />
		</ehcache>


----------

### 3. 国际化（I18N）配置

国际化文件目录：/META-INF/i18n

国际化初始化：com.foreveross.common.ProjectInitializeBean

		public class ProjectInitializeBean implements InitializingBean {
		
			private static Boolean hasInit = false;
		
			public synchronized void afterPropertiesSet() throws Exception {
				if (!hasInit) {
					I18nHelper.loadDefualtMessages("classpath://META-INF/i18n");
					hasInit = true;
				}
			}
		}

----------

### 4. 数据库脚本

数据库脚本目录：/src/main/resources/database

数据库表结构：{project.name}-table-{db.type}.sql

		说明:
		    project.name: 工程名
		    db.type     : 数据库类型，mysql, h2, oracle, sqlserver, db2

数据库初始数据：{project.name}-data-{db.type}.sql

		说明:
		    project.name: 工程名
		    db.type     : 数据库类型，mysql, h2, oracle, sqlserver, db2

导入脚本时，应先导入表结构， 再导入初始数据。

----------

### 5. Jetty 默认配置

配置文件：/src/test/resources/webdefault.xml

用于采用Jetty启动调试项目时，允许编辑资源文件，如JS，CSS，图片等。

----------

### 6. web.xml 配置

配置文件：/src/main/webapp/WEB-INF/web.xml

Listener：org.springframework.web.util.IntrospectorCleanupListener

		说明：Spring 刷新Introspector防止内存泄露

Filter：org.springframework.web.filter.CharacterEncodingFilter

		说明：UTF-8编码过滤器

Filter：org.springframework.web.filter.DelegatingFilterProxy

		说明：Spring过滤器，Shiro过滤拦截

Filter：org.springframework.web.servlet.DispatcherServlet

		说明：SpringMVC Servlet

----------












