基础框架
====

### 1. 框架技术

SpringMVC + Mybatis + Freemarker

----------

### 2. 框架分层

本框架使得领域驱动设计思想做的架构分层：

* 展现层

		展现层就是我们的MVC这一层，包括Controller和页面。
		
		包名：{project.pakcage.name}.web.{module.name}
		
		说明：project.pakcage.name: 项目包名
		     module.name         : 模块名
		
		依赖：
			 展现层 -- 依赖 --> 应用层接口、基础设施层  （严禁依赖领域层！）

* 应用层

		应用层包含应用层接口及实现。（理论上在应用层与展现层间有一个facade层会更合理，但考虑层次过多，开发人员容易弄混）
		应用层通过接口向外部提供应用服务，需要对外隐藏业务细节，业务在领域层中实现，也就要求接口参数不能传入、传出领域层的东西。
		
		包名，接口：{project.pakcage.name}.application.{module.name}
		包名，实现：{project.pakcage.name}.application.{module.name}.impl

		说明：project.pakcage.name: 项目包名
		     module.name         : 模块名
		
		依赖：严禁依赖展现层！
			 应用层接口 -- 依赖 --> 基础设施层                  （严禁依赖展现层、领域层！）
		     应用层实现 -- 依赖 --> 基础设施层、领域层、应用层接口  （严禁依赖展现层！）

* 领域层

		领域层，业务核心层，理论上所有的业务都必须在本领域层实现。
		目前一个领域对像(domain)，与数据库的一个表相对应，比如，User领域对象对应数据库中的User表，
		但领域对象是围绕着业务为核心的，而不是围绕着数据对象为核心的，这就意味着可以创建一个领域对象，
		但不对应任何的表，如，授权领域对象，这个领域对象可以操作用户、角色、权限实体（领域对象），
		以完成授权这个领域业务（以前我们叫这个做DomainService，但这个就是Domain没必要用Service区分）。
		
		包名：{project.pakcage.name}.domain.{module.name}

		说明：project.pakcage.name: 项目包名
		     module.name         : 模块名
		
		依赖：严禁依赖展现层、应用层！
			 领域层 -- 依赖 --> 基础设施层

* 基础设施层

		基础设施层，包含工具类，VO，DTO，通用的处理工具，业务无关。
		
		包名，VO或DTO包名：{project.pakcage.name}.infra.vo.{module.name}
		包名，其他       ：这里只对应用中的VO或DTO使用到的包含作规定，工具类的包名不限定

		说明：project.pakcage.name: 项目包名
		     module.name         : 模块名
		
		依赖：严禁依赖展现层、应用层！

----------




















