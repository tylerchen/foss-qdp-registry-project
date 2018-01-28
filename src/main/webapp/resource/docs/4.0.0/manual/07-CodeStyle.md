代码格式
====

### 1. 包与类命名

参考：[Framework](index.htm?md=01-Framework.md)
		
### 2. 开发规范

参考：[Development.md](index.htm?md=04-Development.md)

### 3. 代码注释


#### 3.1. 原则

* 所有类及方法都必须有注释，包括接口及实现，复杂实现必须写注释，禁止注释格式化。
* 所有JAVA代码都必须格式化，Ctrl+Shift+F。
* 所有JAVA代码不能出现黄色提示。
* 所有JAVA代码不能采用过期方法，@Deprecated。
* if 语句必须使用花括号,必须换行。
* 代码保持清洁，不能留用//注释的代码等。禁止使用//注释，代码内注释统一采用/**/。
* 无特殊要求，所有文本文件统一采用编码：UTF-8，包括.java, .properties, .xml, .html等文本（不限于）文件。

#### 3.2. Java文件

		文件命名：大写字母开头、驼峰式、只允许英文
		文件结构：文件结构按照顺序如下所示
				1、许可证或版权信息
				2、package 语句，package 语句不换行
				3、import 语句，不能使用通配符
				4、一个顶级类 

#### 3.3. 类注释

		功能描述：描述该类功能及使用方法。
		作者   ：描述作者的邮件及名称，如：<a href="mailto:youremail@gmail.com">Your Name</a>
		创建日期：说明该类的创建日期
		/**
		 * Function Application
		 * @author <a href="mailto:youremail@gmail.com">Your Name</a> 
		 * @since Aug 9, 2015
		 */
		 public interface FunctionApplication {}

#### 3.4. 方法注释

		/**
		 * <pre>
		 * 删除多个Function，根据传入的Function id
		 * </pre>
		 * @param ids Function id数组.
		 * @param userId 用户ID.
		 * @author <a href="mailto:youremail@gmail.com">Your Name</a> 
		 * @since Jan 05 2016
		 */
		void removeFunctionByIds(String[] ids, String userId);

#### 3.5. 基本命名

		命名的关键要素是简短、意图清晰,遵守下面规则:
		1、项目模块
			只能使用小写字母、数字和中划线，以中划线分割单词，不能以数字开头
			Maven多模块项目，主项目工程必须要以：-project结尾，子模块不能以：-project结尾
			如：主工程，foss-qdp-project；子模块：foss-qdp-web
		2、包
			只能使用小写字母
			如：com.foreveross.qdp.application.core
		3、类
			只能使用大小写字母，大驼峰式
			如：FunctionApplication
		4、字段
			只能使用大小写字母和下划线，小驼峰式
			对于行业习惯缩写，及命名过长的缩写，需要提供缩写对照表，确保全局统一缩写
			如：private String userName；private String ftlNo;
		5、方法
			只能使用大小写字母，小驼峰式
			方法名能尽量表达方法的意图，有复杂意图的方法应该重构，减小单个方法体积
			如：findUserByName
		6、JS、CSS、JSP
			只能英文、数字和中划线




