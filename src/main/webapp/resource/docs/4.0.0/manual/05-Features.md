功能说明
====

描述框架中功能说明。

### 1. 工程模板

#### 1.1. 完整版

完整版，适用于初始建系统，需要完整的体系功能，包括了：

1. 授权体系：包括帐户、用户、菜单、组织、角色、资源；
2. 通用功能：字典、扩展表、I18N、OpenReport、动态查询、脚本；
3、日志功能：访问日志、操作日志；

#### 1.2. 无组织权限版

无组织权限版，适用于不带授权体系、通用功能的子系统（授权体系、通用功能已经作为子系统独立运行并提供RPC服务），或者不需要授权体系、通用功能，包括了：

1. 授权体系：无完整授权体系。只有简单的登录参见：META-INF/config/authorization.properties，或使用RPC方式接入授权体系，参见：META-INF/spring-rpc-consumer/rpc-consumer-systemauth.xml；
2. 通用功能：无；
3、日志功能：访问日志、操作日志；

### 2. 登录验证/远程验证

#### 2.1. 数据库密码

数据库密码加密方式：MD5Helper.secondSalt(MD5Helper.firstSalt(password))，带加盐的二次MD5加密。

#### 2.2. HTTP认证密码

HTTP认证传输认证字符串：base64(account:MD5Helper.firstSalt(password))，服务端拿到认证字符串先base64解密，拿到account和密码MD5的一次加密，然后验证把密码进行MD5Helper.secondSalt加密，再与数据中的account及密码进行比较。

