Change Logs
======

* [20171026-001]: integration spring boot.
* [20171019-001]: upgrade qdp to 4.0.0.
* [20161010-001]: update spring version to 4.3.3.RELEASE, update cxf version to 3.1.7, JDK requried 1.7, src/main/resources/META-INF/spring-web/spring-servlet.xml is changed see org.springframework.http.converter.json.MappingJackson2HttpMessageConverter .
* [20160510-003]: add shortcut/hotkey support, change files: webapp/resource/js/shortcut/shortcut.js,  webapp/WEB-INF/pages/login.html .
* [20160510-002]: add spring task and quartz support, change files: pom.xml, database/003-qdp-quartz-table-h2.sql, database/003-qdp-quartz-table-mysql.sql, database/003-qdp-quartz-table-oracle.sql, QuartzJobInfo.java, QuartzSchedulerHelper.java, META-INF/config/quartz.properties, META-INF/spring-tcutil/spring-quartz.xml, META-INF/spring-tcutil/spring-task.xml .
* [20160510-001]: fix genUrl to consider the "#" condition, change files: webapp/resource/js/root.js .
* [20160503-001]: upgrade spring version to 4.0.2, need to above jdk1.6 update 10, cxf works fine: change files: pom.xml, spring-shiro.xml.
* [20160429-001]: add alibaba druid support. see files: META-INF/spring-app/data-access.xml, META-INF/spring-app/root.xml, META-INF/config/system.properties, pom.xml. docs: https://github.com/alibaba/druid/wiki.
* [20160428-001]: add login password encrypt. add login password encrypt support. Modify file: pom.xml,  AuthAccountController.java, ConstantBean.java, ProjectInitializeBean.java, _common_crud.html, login.html, META-INF/config/system.properties, resource/js/encrypt/jsencrypt.js, resource/js/encrypt/jsencrypt-min.js, resource/js/encrypt/README.md, test/resources/jetty-context.xml. 
* [20160427-001]: fix test case issue. 
* [20160316-002]: fix load resource from weblogic issue. 
* [20160316-001]: fix deploy to weblogic problem. 
* [20160315-002]: fix weblogic to load freemarker model or directive issue. see org.iff.infra.util.freemarker.ExFreeMarkerConfiguration, and src/main/resources/META-INF/spring-web/spring-servlet.xml.
* [20160315-001]: fix to many columns can't show on batch edit.
* [20160313-002]: fix docs "Back to Top".
* [20160313-001]: fix internationali issue, fix thread local issue to clean thread local variable.
* [20160310-001]: remove un-use js, and combine common js to reduce js files. 
* [20160306-003]: change META-INF/spring-app/data-access.xml "mapperLocations" to add "-oracle" and "-mysql" subfix.
* [20160306-002]: change mybatis mapper xml file, to add "-oracle" or "-mysql" subfix.
* [20160306-001]: fix oracle column name (use as) case in-sensitive.
* [20160216-001]: fix context path issue.