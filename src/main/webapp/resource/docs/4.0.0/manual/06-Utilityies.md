工具类
====

工具类位置：org.iff.infra.util

### 1. 加密解密

#### 1.1. BaseCryptHelper

	提供base64，base62（少了=/两个字符），URL（UTF-8）等编码及解码

#### 1.2. AESCoderHelper

	提供AES编码解码。

#### 1.3. RSAHelper

	提供RSA编码解码。

#### 1.4. MD5Helper

	提供MD5编码。

### 2. 缓存

#### 2.1. EhcacheHelper

	提供Ehcache操作，使用前需要调用init，以注入缓存实例

#### 2.2. JedisHelper

	提供Redis操作，使用前需要调用init，以注入缓存实例

#### 2.3. CacheHelper

	提供缓存能用操作，使用前需要调用init，以注入缓存实例

### 3. 断言

#### 3.1. Assert

	提供断言，尽量使用本断言

### 4. Bean属性拷贝

#### 4.1. BeanHelper

	提供Bean属性拷贝操作，如果setUsePOVOCopyHelper为true可以使用POVOCopyHelper，会自动生成groovy代码，并编译，属性拷贝性能接近get/set，第一次调用会耗时

#### 4.2. POVOCopyHelper

	生成groovy代码的属性拷贝

### 5. HTTP、流、文件相关

#### 5.1. ActionHelper

	接受request/response对象，可以提供redirect/forward，下载文件名称编码，URL参数拼接等

#### 5.2. FileSplitHelper

	分段从InputStream中读取数据，通过Iterator对象返回，可以提高读取性能

#### 5.3. HttpHelper

	提供HTTP的Header属性访问，获得来源IP，访问MD5验证等

#### 5.4. ImageHelper

	图片处理，改变图片大小

#### 5.5. ScrollableByteArrayOutputStream

	可滚动输出的流，只保留最近内容，如可以用于输出系统最近的日志

#### 5.6. SocketHelper

	Socket操作，使用test方法可以ping测试，closeWithoutError，可以关闭流

#### 5.7. StreamHelper

	流操作，可以读取流的内容为文件（UTF-8），或字节数组

#### 5.8. ZipHelper

	ZIP压缩及解压操作

### 6. 序列化

#### 6.1. JsonHelper

	提供JSON的序列化操作，默认使用GSON

#### 6.2. GsonHelper

	提供JSON的序列化操作

#### 6.3. KryoRedisSerializer

	提供Kryo的序列化操作，可以用于Redis序列化或Ehcache序列化

### 7. 异常

#### 7.1. Exceptions

	提供异常的包装类

### 8. 字符串格式化

#### 8.1. FCS/FormatableCharSequence

	提供字符串的格式：FCS.get(FCS.get("fuck {0},{1}", "ie6"), "ie7") =》fuck ie6,ie7

### 9. 过滤HTML防止XSS攻击

#### 9.1. HtmlHelper

	使用clean方法，可以调用Jsoup.clean，来防止XSS攻击

### 10. 日志

#### 10.1. Logger

	提供能用的日志类，使用该类可以对请求路径进行跟踪

### 11. 资源加载

#### 11.1. I18nHelper

	加载I18N

#### 11.2. ResourceHelper

	加载资源，可以使用前缀来加载，如：classpath://，jar://，file://

#### 11.3. PropertiesHelper

	加载属性文件

### 12. ID生成

#### 12.1. StringHelper
	
	提供字符串操作，及19位的UUID生成

#### 12.2. SnowflakeIdHelper
	
	提供SnowflakeId生成，一定要指定唯一的workerId 和 datacenterId

#### 12.3. SnowflakeIdExHelper
	
	扩展SnowflakeId，workerId采用非Local IP的最后一段，datacenterId采用随机生成，也可以指定这两个数，建议在局域网中使用，IP是关键

#### 12.4. IdHelper
	
	提供上面uuid及SnowflakeIdExHelper的id

### 13. 验证

#### 13.1. Validations
	
	提供验证操作

### 14. DAO工具

#### 14.1. ExDao/Dao
	
	提供DAO能用工具，默认需要注入tcRepositoryService。

	<T> java.util.List<T>	queryList(java.lang.String queryDsl, java.lang.Object params)
	查询列表
	<T> T	queryOne(java.lang.String queryDsl, java.lang.Object params)
	查询单条记录
	Page	queryPage(java.lang.String queryDsl, java.lang.Object params)
	分面查询
	long	querySize(java.lang.String queryDsl, java.lang.Object params)
	查询总记录数
	int	remove(java.lang.String queryDsl, java.lang.Object params)
	删除
	int	save(java.lang.String queryDsl, java.lang.Object params)
	保存
	int	update(java.lang.String queryDsl, java.lang.Object params)
	更新

### 15. 分布式锁

#### 15.1. DistributeLockFactory
	
	依赖Zookeeper实现的分布式锁



