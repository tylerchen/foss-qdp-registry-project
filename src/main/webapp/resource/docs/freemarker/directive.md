Freemarker 标签
====

* [assign](#assign)
* [attempt](#attempt)
* [break: in switch](#switch) [break: in list](#list)
* [case](#case)
* [compress](#compress)
* [default](#default)
* [else: in if](#if) [else: in list](#list)
* [elseif](#elseif)
* [escape](#escape)
* [fallback](#fallback)
* [function](#function)
* [flush](#flush)
* [ftl](#ftl)
* [global](#global)
* [if](#if)
* [import](#import)
* [include](#include)
* [items](#items)
* [list](#list)
* [local](#local)
* [lt](#lt)
* [macro](#macro)
* [nested](#nested)
* [noescape](#noescape)
* [noparse](#noparse)
* [nt](#nt)
* [recover](#recover)
* [recurse](#recurse)
* [return: in macro](#macro) [return: in function](#function)
* [rt](#rt)
* [sep](#sep)
* [setting](#setting)
* [stop](#stop)
* [switch](#switch)
* [t](#t)
* [User-defined directive <@...>](#user_defined_directive)
* [visit](#visit)


### assign
		
		<#assign name1=value1 name2=value2 ... nameN=valueN>
		或
		<#assign same as above... in namespacehash>
		或
		<#assign name>
		  capture this
		</#assign>
		或
		<#assign name in namespacehash>
		  capture this
		</#assign>
		
		这里：
		
		    name：变量的名字。 它不是表达式。而它可以写作是字符串，如果变量名包含保留字符这是很有用的， 
		    比如 <#assign "foo-bar" = 1>。 请注意这个字符串没有展开插值(如"${foo}")； 如果需要赋值一个动态创建的名字，
		    那么不得不使用 这个技巧。
		    =：赋值操作符。 它也可以是一个简写的赋值操作符(从 FreeMarker 2.3.23 版本开始)： ++，--， +=，-=， *=，/= 或 %=。
		    比如 <#assign x++> 和 <#assign x = x + 1> 是一样的，并且 <#assign x += 2> 和 <#assign x = x + 2> 是相同的。 
		    请注意， ++ 通常意味着算术加法 (对于非数字将会失败)，不像 + 或 += 可以进行字符连接等重载操作。
		    value： 存储的值。是表达式。
		    namespacehash：(通过 import) 为命名空间创建的哈希表。是表达式。
		
		描述
		
		使用该指令你可以创建一个新的变量， 或者替换一个已经存在的变量。注意仅仅顶级变量可以被创建/替换 
		(也就是说你不能创建/替换 some_hash.subvar， 除了 some_hash)。
		
		关于变量的更多内容，请阅读：模板开发指南/其它/在模板中定义变量
		
		比如：变量 seq 存储一个序列：
		
		<#assign seq = ["foo", "bar", "baz"]>
		
		比如：变量 x 中存储增长的数字：
		
		<#assign x++>
		
		作为一个方便的特性，你可以使用一个 assign 标记来进行多次定义。比如这个会做上面两个例子中相同的事情：
		
		<#assign
		  seq = ["foo", "bar", "baz"]
		  x++
		>
		
		如果你知道什么是命名空间：assign 指令在命名空间中创建变量。通常它在当前的命名空间 (也就是和标签所在模板关联的命名空间)中创建变量。
		但如果你是用了 in namespacehash， 那么你可以用另外一个 命名空间 来创建/替换变量。 
		比如，这里你在命名空间中 /mylib.ftl 创建/替换了变量 bgColor：
		
		<#import "/mylib.ftl" as my>
		<#assign bgColor="red" in my>
		
		assign 的极端使用是当它捕捉它的开始标记和结束标记中间生成的输出时。 也就是说，在标记之间打印的东西将不会在页面上显示， 
		但是会存储在变量中。比如：
		
		<#macro myMacro>foo</#macro>
		<#assign x>
		  <#list 1..3 as n>
		    ${n} <@myMacro />
		  </#list>
		</#assign>
		Number of words: ${x?word_list?size}
		${x}
		
		将会输出：
		
		Number of words: 6
		    1 foo
		    2 foo
		    3 foo
		 
		
		请注意，你不应该使用它来往字符串中插入变量：
		
		<#assign x>Hello ${user}!</#assign> <#-- BAD PRACTICE! -->
		
		你可以这么来写：
		
		<#assign x="Hello ${user}!">

### attempt
### recover
		
		<#attempt>
		  attempt block
		<#recover>
		  recover block
		</#attempt>
		
		这里：
		
		    attempt block：任意内容的模板块。这是会被执行的， 但是如果期间发生了错误，那么这块内容的输出将会回滚， 
		    之后 recover block 就会被执行。
		    recover block: 任意内容的模板块。 这个仅在 attempt block 执行期间发生错误时被执行。你可以在这里打印错误信息或其他操作。
		
		recover 是强制的。 attempt/recover 可以嵌套在其他 attempt block 或 recover block中。
		Note:
		
		上面的格式是从 2.3.3 版本开始支持的，之前它是 <#attempt>...<#recover>...</#recover>，也支持向下兼容。
		此外， 这些指令是在 FreeMarker 2.3.1 版本时引入的，在 2.3 版本中是不存在的。
		描述
		
		如果你想让页面成功输出内容，尽管它在页面特定位置发生错误也这样， 那么这些指令就是有用的。
		如果一个错误在 attempt block 执行期间发生， 那么模板执行就会中止，但是 recover block 会代替 attempt block 执行。 
		如果在 attempt block 执行期间没有发生错误， 那么 recover block 就会忽略。 一个简单的示例如下：
		
		Primary content
		<#attempt>
		  Optional content: ${thisMayFails}
		<#recover>
		  Ops! The optional content is not available.
		</#attempt>
		Primary content continued
		
		如果 thisMayFails 变量不存在，将会输出：
		
		Primary content
		  Ops! The optional content is not available.
		Primary content continued
		
		如果 thisMayFails 变量存在而且值为 123，将会输出：
		
		Primary content
		  Optional content: 123
		Primary content continued
		
### switch
### case
### default
### break
		
		<#switch value>
		  <#case refValue1>
		    ...
		    <#break>
		  <#case refValue2>
		    ...
		    <#break>
		  ...
		  <#case refValueN>
		    ...
		    <#break>
		  <#default>
		    ...
		</#switch>
		
		这里：
		
		    value， refValue1，等： 表达式将会计算成相同类型的标量。
		
		break 和 default 是可选的。
		描述
		
		这个指令的用法是不推荐的，因为向下通过的行为容易出错。使用 elseif来代替， 除非你想利用向下通过这种行为。
		
		Switch 被用来选择模板中的一个片段，如何选择依赖于表达式的值：
		
		<#switch animal.size>
		  <#case "small">
		     This will be processed if it is small
		     <#break>
		  <#case "medium">
		     This will be processed if it is medium
		     <#break>
		  <#case "large">
		     This will be processed if it is large
		     <#break>
		  <#default>
		     This will be processed if it is neither
		</#switch>
		
		在 switch 中间必须有一个或多个 <#case value>， 在所有 case 标签之后，有一个可选的 <#default>。 
		当FreeMarker到达 switch 指令时，它会选择一个 case 指令，这里的 refValue 等于 value 并且继续模板处理。 
		如果没有和合适的值匹配的 case 指令，那么就继续处理 default 指令，如果它存在，否则就继续处理 switch 结束标签之后的内容。
		现在有一个混乱的事情： 当它选择一个 case 指令后，它就会继续处理其中的内容， 直到遇到 break 指令。
		也就是它遇到另外一个 case 指令或 <#default> 标记时也不会自动离开 switch 指令。比如：
		
		<#switch x>
		  <#case 1>
		    1
		  <#case 2>
		    2
		  <#default>
		    d
		</#switch>
		
		如果 x 是 1，它会打印1 2 d；如果 x 是 2，那么就会打印2 d；如果 x 是 3，那么就会打印d。这就是前面提到的向下通过行为。 
		break 标记指示 FreeMarker 直接略过剩下的 switch 代码段。

### compress
		
		<#compress>
		  ...
		</#compress>
		
		描述
		
		当你使用了对空白不敏感的格式(比如HTML或XML) 时压缩指令对于移除多余的 空白 是很有用的。它捕捉在指令体(也就是在开始标签和结束标签中)
		中生成的内容， 然后缩小所有不间断的空白序列到一个单独的空白字符。 如果被替代的序列包含换行符或是一段空间，
		那么被插入的字符也会是一个 换行符。 开头和结尾的不间断的空白序列将会完全被移除。
		
		<#assign x = "    moo  \n\n   ">
		(<#compress>
		  1 2  3   4    5
		  ${moo}
		  test only
		
		  I said, test only
		
		</#compress>)
		
		将会输出：
		
		(1 2 3 4 5
		moo
		test only
		I said, test only)

### if
### else
### elseif
		
		<#if condition>
		  ...
		<#elseif condition2>
		  ...
		<#elseif condition3>
		  ...
		...
		<#else>
		  ...
		</#if>
		
		这里：
		
		    condition, condition2， 等：将被计算成布尔值的表达式。
		
		elseif 和 else 是可选的。
		描述
		
		你可以使用 if， elseif 和 else 指令来条件判断是否越过模板的一个部分。 condition 必须计算成布尔值， 否则错误将会中止模板处理。
		elseif 和 else 必须出现在 if 内部 (也就是，在 if 的开始标签和结束标签之间)。 if 中可以包含任意数量的 elseif(包括0个) 
		而且结束时 else 是可选的。比如：
		
		只有 if 没有 elseif 和 else：
		
		<#if x == 1>
		  x is 1
		</#if>
		
		只有 if 没有 elseif 但是有 else：
		
		<#if x == 1>
		  x is 1
		<#else>
		  x is not 1
		</#if>
		
		有 if 和两个 elseif 但是没有 else：
		
		<#if x == 1>
		  x is 1
		<#elseif x == 2>
		  x is 2
		<#elseif x == 3>
		  x is 3
		</#if>
		
		有 if 和三个 elseif 还有 else:
		
		<#if x == 1>
		  x is 1
		<#elseif x == 2>
		  x is 2
		<#elseif x == 3>
		  x is 3
		<#elseif x == 4>
		  x is 4
		<#else>
		  x is not 1 nor 2 nor 3 nor 4
		</#if>
		
		要了解更多布尔表达式，可以参考：模板开发指南/模板/表达式.
		
		你(当然)也可以嵌套 if 指令：
		
		<#if x == 1>
		  x is 1
		  <#if y == 1>
		    and y is 1 too
		  <#else>
		    but y is not
		  </#if>
		<#else>
		  x is not 1
		  <#if y < 0>
		    and y is less than 0
		  </#if>
		</#if>
		
		Note:
		
		当你想测试是否 x > 0 或 x >= 0，编写 <#if x > 0> 和 <#if x >= 0> 是错误的， 因为第一个 > 会结束 #if 标签。
		要这么来做，可以编写 <#if x gt 0> 或 <#if gte 0>。也请注意，如果比较发生在括号内部，那么就没有这样的问题， 
		比如 <#if foo.bar(x > 0)> 就会得到想要的结果。

### escape
### noescape
		
		<#escape identifier as expression>
		  ...
		  <#noescape>...</#noescape>
		  ...
		</#escape>
		
		描述
		
		当你使用escape指令包围模板中的一部分时，在块中出现的插值 (${...}) 会和转义表达式自动结合。这是一个避免编写相似表达式的很方便的方
		法。 它不会影响在字符串形式的插值(比如在 <#assign x = "Hello ${user}!">)。而且，它也不会影响数值插值 (#{...})。
		
		例如：
		
		<#escape x as x?html>
		  First name: ${firstName}
		  Last name: ${lastName}
		  Maiden name: ${maidenName}
		</#escape>
		
		事实上它等同于：
		
		  First name: ${firstName?html}
		  Last name: ${lastName?html}
		  Maiden name: ${maidenName?html}
		
		请注意，它和你在指令中用什么样的标识符无关 - 它仅仅是作为一个转义表达式的正式参数。
		
		当在调用宏或者 include 指令时， 理解 在模板文本 中转义仅仅对出现在 <#escape ...> 和 </#escape> 中的插值起作用是很重要的。 
		也就是说，它不会转义文本中 <#escape ...> 之前的东西或 </#escape> 之后的东西， 也不会从 escape 的部分中来调用。
		
		<#assign x = "<test>">
		<#macro m1>
		  m1: ${x}
		</#macro>
		<#escape x as x?html>
		  <#macro m2>m2: ${x}</#macro>
		  ${x}
		  <@m1/>
		</#escape>
		${x}
		<@m2/>
		
		将会输出：
		
		  &lt;test&gt;
		  m1: <test>
		<test>
		m2: &lt;test&gt;
		
		从更深的技术上说， escape 指令的作用是用在模板解析的时间而不是模板处理的时间。 这就表示如果你调用一个宏或从一个转义块中包含另外
		一个模板， 它不会影响在宏/被包含模板中的插值，因为宏调用和模板包含被算在模板处理时间。 另外一方面，如果你用一个转义区块包围一个
		或多个宏声明(算在模板解析时间，和宏调用想法)， 那么那些宏中的插值将会和转义表达式合并。
		
		有时需要暂时为一个或两个在转义区块中的插值关闭转义。你可以通过关闭， 过后再重新开启转义区块来达到这个功能，但是那么你不得不编写两遍
		转义表达式。 你可以使用非转义指令来替代：
		
		<#escape x as x?html>
		  From: ${mailMessage.From}
		  Subject: ${mailMessage.Subject}
		  <#noescape>Message: ${mailMessage.htmlFormattedBody}</#noescape>
		  ...
		</#escape>
		
		和这个是等同的：
		
		  From: ${mailMessage.From?html}
		  Subject: ${mailMessage.Subject?html}
		  Message: ${mailMessage.htmlFormattedBody}
		  ...
		
		转义可以被嵌套(尽管你不会在罕见的情况下来做)。 因此，你可以编写如下面代码(这个例子固然是有点伸展的， 正如你可能会使用 list 来
		迭代序列中的每一项， 但是我们现在所做的是阐述这个观点)的东西：
		
		<#escape x as x?html>
		  Customer Name: ${customerName}
		  Items to ship:
		  <#escape x as itemCodeToNameMap[x]>
		    ${itemCode1}
		    ${itemCode2}
		    ${itemCode3}
		    ${itemCode4}
		  </#escape>
		</#escape>
		
		实际上和下面是等同的：
		
		  Customer Name: ${customerName?html}
		  Items to ship:
		    ${itemCodeToNameMap[itemCode1]?html}
		    ${itemCodeToNameMap[itemCode2]?html}
		    ${itemCodeToNameMap[itemCode3]?html}
		    ${itemCodeToNameMap[itemCode4]?html}
		
		当你在嵌入的转义区块内使用非转义指令时，它仅仅不处理一个单独层级的转义。 因此，为了在两级深的转义区块内完全关闭转义，
		你需要使用两个嵌套的非转义指令。

### visit
### recurse
### fallback
		
		<#visit node using namespace>
		或
		<#visit node>
		
		<#recurse node using namespace>
		或
		<#recurse node>
		或
		<#recurse using namespace>
		或
		<#recurse>
		
		<#fallback>
		
		这里：
		
		    node： 算作 结点变量 的表达式。
		    namespace： 命名空间，或者是命名空间的序列。 命名空间可以以命名空间哈希表(又称为根哈希表)给定， 
		    或者可以引入一个存储模板路径的字符串。代替命名空间哈希表， 你也可以使用普通哈希表。
		
		描述
		
		visit 和 recurse 指令是用来递归处理树的。在实践中，这通常被用来 处理XML。
		Visit
		
		当你调用了 <#visit node>时， 它看上去像用户自定义指令(比如宏)来调用从结点名称 (node?node_name) 
		和命名空间 (node?node_namesoace) 中有名称扣除的结点。名称扣除的规则：
		
		    如果结点不支持结点命名空间(如XML中的文本结点)， 那么这个指令名仅仅是结点的名称 (node?node_name)。 
		    如果 getNodeNamespace 方法返回 null 时结点就不支持结点命名空间了。
		
		    如果结点支持结点命名空间(如XML中的元素结点)， 那么从结点命名空间中的前缀扣除可能在结点名称前和一个做为分隔符 
		    (比如 e:book)的冒号追加上去。前缀，以及是否使用前缀， 依赖于何种前缀 FTL命名空间 中用 ftl 指令的 ns_prefixes 
		    参数注册的， 那里 visit 寻找控制器指令 (visit 调用的相同FTL命名空间不是重要的，后面你将会看到)。 具体来说，
		    如果没有用 ns_prefixes 注册默认的命名空间， 那么对于不属于任何命名空间(当 getNodeNamespace 返回 "")的
		    结点来说就不使用前缀。 如果使用 ns_prefixes 给不属于任意命名空间的结点注册了默认命名空间， 那么就使用前缀 N，
		    而对于属于默认结点命名空间的结点就不使用前缀了。 否则，这两种情况下，用 ns_prefixes 关联结点命名空间的前缀已经被使用了。 
		    如果没有关联结点命名空间的结点前缀，那么 visit 仅仅就好像没有以合适的名称发现指令。
		
		自定义指令调用的结点对于特殊变量 .node 是可用的。比如：
		
		<#-- Assume that nodeWithNameX?node_name is "x" -->
		<#visit nodeWithNameX>
		Done.
		<#macro x>
		   Now I'm handling a node that has the name "x".
		   Just to show how to access this node: this node has ${.node?children?size} children.
		</#macro>
		
		将会输出：
		
		   Now I'm handling a node that has the name "x".
		   Just to show how to access this node: this node has 3 children.
		Done.
		
		如果使用可选的 using 从句来指定一个或多个命名空间， 那么 visit 就会在那么命名空间中寻找指令， 和先前列表中指定的命名空间都获得
		优先级。如果指定 using 从句， 对最后一个未完成的 visit 调用的用 using 从句指定命名空间的命名空间或序列被重用了。
		如果没有这样挂起的 visit 调用，那么当前的命名空间就被使用。 比如，如果你执行这个模板：
		
		<#import "n1.ftl" as n1>
		<#import "n2.ftl" as n2>
		
		<#-- This will call n2.x (because there is no n1.x): -->
		<#visit nodeWithNameX using [n1, n2]>
		
		<#-- This will call the x of the current namespace: -->
		<#visit nodeWithNameX>
		
		<#macro x>
		  Simply x
		</#macro>
		
		这是 n1.ftl：
		
		<#macro y>
		  n1.y
		</#macro>
		
		这是 n2.ftl：
		
		<#macro x>
		  n2.x
		  <#-- This callc n1.y as it inherits the "using [n1, n2]" from the pending visit call: -->
		  <#visit nodeWithNameY>
		  <#-- This will call n2.y: -->
		  <#visit nodeWithNameY using .namespace>
		</#macro>
		
		<#macro y>
		  n2.y
		</#macro>
		
		将会输出：
		
		  n2.x
		  n1.y
		  n2.y
		
		  Simply x
		 
		
		如果 visit 既没有在和之前描述规则的名称扣除相同名字的FTL命名空间发现自定义指令， 那么它会尝试用名称 @node_type 查找， 
		又如果结点不支持结点类型属性 (也就是 node?node_type 返回未定义变量)， 那么使用名称 @default。对于查找来说，
		它使用和之前描述相同的机制。 如果仍然没有找到处理结点的自定义指令，那么 visit 停止模板执行， 并抛出错误。
		一些XML特定的结点类型在这方面有特殊的处理； 参考：XML处理指南/声明的XML处理/具体细节。比如：
		
		<#-- Assume that nodeWithNameX?node_name is "x" -->
		<#visit nodeWithNameX>
		
		<#-- Assume that nodeWithNameY?node_type is "foo" -->
		<#visit nodeWithNameY>
		
		<#macro x>
		Handling node x
		</#macro>
		
		<#macro @foo>
		There was no specific handler for node ${node?node_name}
		</#macro>
		
		将会输出：
		
		Handling node x
		  
		There was no specific handler for node y
		
		 
		
		Recurse
		
		<#recurse> 指令是真正纯语义上的指令。 它访问结点的所有子结点(而没有结点本身)。所以来写：
		
		<#recurse someNode using someLib>
		
		和这个是相等的：
		
		<#list someNode?children as child><#visit child using someLib></#list>
		
		而目标结点在 recurse 指令中是可选的。 如果目标结点没有指定，那就仅仅使用 .node。 因此，<#recurse> 这个精炼的指令和
		下面这个是相同的：
		
		<#list .node?children as child><#visit child></#list>
		
		对于熟悉XSLT的用户的评论，<#recurse> 是和XSLT中 <xsl:apply-templates/> 指令相当类似的。
		Fallback
		
		正如前面所学的，在 visit 指令的文档中， 自定义指令控制的结点也许在多个FTL命名空间中被搜索。 fallback 指令可以被用在自定义
		指令中被调用处理结点。 它指挥 FreeMarker 在更多的命名空间 (也就是，在当前调用列表中自定义指令命名空间之后的命名空间) 
		中来继续搜索自定义指令。如果结点处理器被发现， 那么就被调用，否则 fallback 不会做任何事情。
		
		这个指令的典型用法是在处理程序库之上写定制层，有时传递控制到定制的库中：
		
		<#import "/lib/docbook.ftl" as docbook>
		
		<#--
		  We use the docbook library, but we override some handlers
		  in this namespace.
		-->
		<#visit document using [.namespace, docbook]>
		
		<#--
		  Override the "programlisting" handler, but only in the case if
		  its "role" attribute is "java"
		-->
		<#macro programlisting>
		  <#if .node.@role[0]!"" == "java">
		    <#-- Do something special here... -->
		    ...
		  <#else>
		    <#-- Just use the original (overidden) handler -->
		    <#fallback>
		  </#if>
		</#macro>

### function
### return
		
		<#function name param1 param2 ... paramN>
		  ...
		  <#return returnValue>
		  ...
		</#function>
		
		这里：
		
		    name：方法变量的名称(不是表达式)
		    param1, param2 等： 局部变量的名称， 存储参数的值(不是表达式)，在 = 号后面和默认值 (是表达式)是可选的。
		    paramN，最后一个参数， 可以可选的包含一个尾部省略(...)， 这就意味着宏接受可变的参数数量。局部变量 paramN 将是额外参数的序列。
		    returnValue： 计算方法调用值的表达式。
		
		return 指令可以在 <#function ...> 和 </#function> 之间被用在任意位置和任意次数。
		
		没有默认值的参数必须在有默认值参数 (paramName=defaultValue) 之前
		描述
		
		创建一个方法变量(在当前命名空间中，如果你知道命名空间的特性)。 这个指令和 macro 指令 的工作方式一样，除了 return 指令必须
		有一个参数来指定方法的返回值，而且视图写入输出的将会被忽略。 如果到达 </#function> (也就是说没有 return returnValue)， 
		那么方法的返回值就是未定义变量。
		
		示例1：创建一个方法来计算两个数的平均值：
		
		<#function avg x y>
		  <#return (x + y) / 2>
		</#function>
		${avg(10, 20)}
		
		将会输出：
		
		15
		
		示例2：创建一个方法来计算多个数的平均值：
		
		<#function avg nums...>
		  <#local sum = 0>
		  <#list nums as num>
		    <#local sum = sum + num>
		  </#list>
		  <#if nums?size != 0>
		    <#return sum / nums?size>
		  </#if>
		</#function>
		${avg(10, 20)}
		${avg(10, 20, 30, 40)}
		${avg()!"N/A"}
		
		将会输出：
		
		15
		25
		N/A

### flush

		<#flush>
		
		描述
		
		当 FreeMarker 生成输出时，它通常不会立即发送到最终接收端 (比如web浏览器或最终的文件)，而是会将内容累积在缓冲区，
		发送一个大块的内容。 缓冲区的精确规则不是由 FreeMarker 决定的，而是由嵌入的软件决定的。 将缓冲区中累积的内容发送出去称为冲洗。
		尽管冲洗是自动发生的， 有时你想在模板处理时的一点强制执行，这就是 flush 指令要做的。如果需要在确定之处用到它，
		这是由程序员决定的，而不是设计师。
		

### ftl
		
		<#ftl param1=value1 param2=value2 ... paramN=valueN>
		
		这里：
		
		    param1, param2 等： 参数的名字，不是表达式。允许的参数有： encoding， strip_whitespace， strip_text 等。参见下面。
		    value1, value2 等： 参数的值。必须是一个常量表达式(如 true， 或 "ISO-8859-5"，或 {x:1, y:2})。它不能用变量。
		
		描述
		
		告诉 FreeMarker 和其他工具关于模板的信息， 而且帮助程序员来自动检测一个文本文件是否是 FTL 文件。该指令， 
		如果存在，必须是模板的第一句代码。该指令前的任何 空白 将被忽略。 该指令的老式语法(#-less)格式是不支持的。
		
		一些设置(编码方式，空白剥离等)在这里给定的话就有最高的优先级， 也就是说，它们直接作用于模板而不管其他任何 FreeMarker 的配置设置。
		
		参数：
		
		    encoding： 模板指定编码方式，比如 ISO-8859-5，UTF-8 或 Shift_JIS。
		    
		    strip_whitespace： 这将开启/关闭 空白剥离。 合法的值是布尔值常量 true 和 false，默认值 true。
		
		    strip_text：当开启它时， 当模板被解析时模板中所有顶级文本被移除。这个不会影响宏，指令，或插值中的文本。 
		    合法值是布尔值常量 true 和 false，默认值 false。
		
		    strict_syntax：这会开启/关闭"严格的语法"。 合法值是布尔值常量 true 和 false， 默认值true。
		
		    ns_prefixes：这是关联结点命名空间前缀的哈希表。
		
		    attributes：这是关联模板任意属性(名-值对)的哈希表。 
		
		该指令也决定模板是否使用尖括号语法(比如 <#include 'foo.ftl'>)或 方括号语法 (如 [#include 'foo.ftl'])。
		简单而言， 该指令使用的语法将会是整个模板使用的语法， 而不管 FreeMarker 是如何配置的。

### global
		
		<#global name=value>
		或
		<#global name1=value1 name2=value2 ... nameN=valueN>
		或
		<#global name>
		  capture this
		</#global>
		
		这里：
		
		    name：变量的名称。 它不是表达式。但它可以被写作是字符串形式，如果变量名包含保留字符这是很有用的， 
		    比如 <#global "foo-bar" = 1>。 注意这个字符串没有扩展插值(如 "${foo}")。
		    =：赋值操作符，也可以简写的赋值操作符之一 (++， +=，等...)，和 assign 指令 相似
		    value：存储的值，是表达式。
		
		描述
		
		该指令和 assign 相似， 但是被创建的变量在所有的 命名空间 中都可见， 但又不会存在于任何一个命名空间之中。精确地说，
		正如你会创建 (或替换)一个数据模型变量。因此，这个变量是全局的。如果在数据模型中， 一个相同名称的变量存在的话，
		它会被使用该指令创建的变量隐藏。 如果在当前的命名空间中，一个相同名称的变量存在的话， 那么会隐藏由 global 指令创建的变量。
		
		例如，用 <#global x = 1> 创建一个变量， 那么在所有命名空间中 x 都可见， 除非另外一个称为 x 的变量隐藏了它 
		(比如你已经用 <#assign x = 2> 创建了一个变量)。 这种情形下，你可以使用 特殊变量 globals，比如 ${.globals.x}。
		请注意， 使用 globals 你看到所有全局可访问的变量； 不但由 global 指令创建的变量，而且是数据模型中的变量。
		
		自定义JSP标记的用户请注意：用这个指令创建的变量集合和JSP页面范围对应。 这就意味着，如果自定义JSP标记想获得一个页面范围的
		属性(page-scope bean)， 在当前命名空间中一个相同名称的变量，从JSP标记的观点出发，将不会隐藏。

### import

		<#import path as hash>
		
		这里：
		
		    path：模板的路径。 这是一个算作是字符串的表达式。(换句话说，它不是一个固定的字符串， 它可以是这样的一些东西，
		    比如，profile.baseDir + "/menu.ftl"。)
		    hash: 访问命名空间的哈希表变量不带引号的名字。不是表达式。 (如果要引入动态创建的名字，那么就不得不使用 这个技巧。)
		
		描述
		
		引入一个库。也就是说，它创建一个新的空命名空间， 然后在那个命名空间中执行给定 path 参数中的模板， 
		所以模板用变量(宏，函数等)填充命名空间。 然后使得新创建的命名空间对哈希表的调用者可用。 
		这个哈希表变量将会在命名空间中，由 import (就像你可以用 assign 指令来创建一样。) 的调用者被创建成一个普通变量，
		名字就是 hash 参数给定的。
		
		如果你用同一个 path 多次调用 import，它会创建命名空间， 但是只运行第一次 import 的调用。 后面的调用仅仅创建一个哈希表变量，
		你只是通过它来访问 同一个 命名空间。
		
		由引入的模板打印的输出内容将会被忽略 (不会在包含它的地方被插入)。模板的执行是来用变量填充命名空间， 而不是写到输出中。
		
		比如：
		
		<#import "/libs/mylib.ftl" as my>
		
		<@my.copyright date="1999-2002"/>
		
		path 参数可以是一个相对路径，比如 "foo.ftl" 和 "../foo.ftl"，或者是像 "/foo.ftl" 一样的绝对路径。 
		相对路径是相对于使用 import 指令模板的目录。 绝对路径是程序员配置 FreeMarker 时定义的相对于根路径 
		(通常指代"模板的根目录")的路径。
		
		通常使用/(斜杠)来分隔路径组成， 而不是\(反斜杠)。如果你从你本地的文件系统中加载模板， 
		那么它使用反斜杠(比如在Windows环境下)，FreeMarker 将会自动转换它们。
		
		像 include 指令一样，获得机制 和 本地化查找 也可以用来解决路径问题。

### include
		
		<#include path>
		或
		<#include path options>
		
		这里：
		
		    path： 要包含文件的路径；一个算作是字符串的表达式。(用其他话说， 它不用是一个固定的字符串，
		    它也可以是像 profile.baseDir + "/menu.ftl"这样的东西。)
		    options： 一个或多个这样的选项： encoding=encoding, parse=parse
		        encoding： 算作是字符串的表达式
		        parse： 算作是布尔值的表达式(为了向下兼容，也接受一部分字符串值)
		        ignore_missing: 算作是布尔值的表达式
		
		描述
		
		你可以使用它在你的模板中插入另外一个 FreeMarker 模板文件 (由 path 参数指定)。 被包含模板的输出格式是在 include 标签
		出现的位置插入的。 被包含的文件和包含它的模板共享变量，就像是被复制粘贴进去的一样。 include 指令不能由被包含文件的内容所替代， 
		它只是当 FreeMarker 每次在模板处理期间到达 include 指令时处理被包含的文件。所以对于如果 include 在 list 循环之中的例子， 
		你可以为每个循环周期内指定不同的文件名。


### list
### else
### items
### sep
### break
		
		形式 1：
		
		<#list sequence as item>
		    Part repeated for each item
		<#else>
		    Part executed when there are 0 items
		</#list>
		
		这里：
		
		    else 部分是可选的， 而且仅仅从 FreeMarker 2.3.23 版本开始支持。
		    sequence： 将我们想要迭代的项，算作是序列或集合的表达式
		    item： 循环变量 的名称 (不是表达式)
		    在标签之间的多个 "parts" 可以是任意的FTL (包括嵌套的 list)
		
		形式 2 (从 FreeMarker 2.3.23 版本开始)：
		
		<#list sequence>
		    Part executed once if we have more than 0 items
		    <#items as item>
		        Part repeated for each item
		    </#items>
		    Part executed once if we have more than 0 items
		<#else>
		    Part executed when there are 0 items
		</#list>
		
		这里：和上面形式1的 "这里" 部分相同。
		描述
		最简形式
		
		假设 users 包含 ['Joe', 'Kate', 'Fred'] 序列：
		
		<#list users as user>
		  <p>${user}
		</#list>
		
		  <p>Joe
		  <p>Kate
		  <p>Fred
		
		list 指令执行在 list 开始标签和 list 结束标签 ( list 中间的部分) 之间的代码， 对于在序列(或集合)中每个值指定为它的第一个参数。
		对于每次迭代，循环变量(本例中的 user)将会存储当前项的值。
		
		循环变量(user) 仅仅存在于 list 标签体内。 而且从循环中调用的宏/函数不会看到它(就像它只是局部变量一样)。
		else 指令
		Note:
		
		在 list 中的 else 仅从 FreeMarker 2.3.23 版本开始支持。
		
		当没有迭代项时，才使用 else 指令， 可以输出一些特殊的内容而不只是空在那里：
		
		<#list users as user>
		  <p>${user}
		<#else>
		  <p>No users
		</#list>
		
		该输出和之前示例是相同的，除了当 users 包含0项时：
		
		  <p>No users
		
		请注意，循环变量 (user) 在 else 标签和 list 结束标签中间不存在， 因为那部分不是循环中的部分。
		
		else 必须是真的在 (也就是在源代码中) list 指令体内部。也就是说， 不能将它移出到宏或包含的模板中。
		items 指令
		Note:
		
		items 从 FreeMarker 2.3.23 版本开始存在
		
		如果不得不在第一列表项之前或在最后一个列表项之后打印一些东西， 那么就要使用 items 指令，但至少要有一项。典型的示例为：
		
		<#list users>
		  <ul>
		    <#items as user>
		      <li>${user}</li>
		    </#items>
		  </ul>
		</#list>
		
		  <ul>
		      <li>Joe</li>
		      <li>Kate</li>
		      <li>Fred</li>
		  </ul>
		
		如果没有迭代项，那么上面的代码不会输出任何内容， 因此不用以空的 <ul></ul> 来结束。
		
		也就是说，当 list 指令没有 as item 参数， 如果只有一个迭代项，指令体中的代码仅仅执行一次，否则就不执行。 
		必须内嵌的 items 指令体会对每个迭代项执行， 那么 items 指令使用 as item 定义循环变量，而不是 list。
		
		有 items 的 list 指令也可以有 else 指令：
		
		<#list users>
		  <ul>
		    <#items as user>
		      <li>${user}</li>
		    </#items>
		  </ul>
		<#else>
		  <p>No users
		</#list>
		
		更多细节：
		
		    解析器会检查没有 as item 参数的 list 通常会有嵌入的 items 指令，该 items 指令通常会有一个包围的 list，
		    它没有 as item 参数。当模板解析时就会检查，而不是当模板执行的时候。因此，这些规则也适用于FTL源代码本身， 
		    所以不能将 items 移出到宏或者被包含的模板中。
		
		    list 可以有多个 items 指令， 但是只有一个允许执行(直到不离开或重新进入包围的 list 指令)； 之后试图调用 items 会发生错误。
		    所以多个 items 可以用于不同的 if-else 分支中去，但不能迭代两次。
		
		    items 指令不能有它自己的嵌入 else 指令，只能被包含的 list 可以有。
		
		    循环变量 (user) 仅仅存在于 items 指令体内部。
		
		sep 指令
		Note:
		
		sep 从 FreeMarker 2.3.23 版本开始存在。
		
		当不得不显示介于每个迭代项(但不能在第一项之前或最后一项之后) 之间的一些内容时，可以使用 sep。例如：
		
		<#list users as user>${user}<#sep>, </#list>
		
		Joe, Kate, Fred
		
		上面的 <#sep>, </#list> 是 <#sep>, </#sep></#list> 的简写； 如果将它放到被包含的指令关闭的位置时，sep 结束标签可以忽略。
		下面的示例中，就不能使用该简写 (HTML标签不会结束任何代码，它们只是 FreeMarker 输出的原生文本)：
		
		<#list users as user>
		  <div>
		    ${user}<#sep>, </#sep>
		  </div>
		</#list>
		
		sep 是编写 <#if item?has_next>...</#if> 的方便形式，有 list 或 items 循环变量时，它就可以使用，并且不限次数。而且， 
		也可以有任意的 FTL 作嵌入的内容。
		
		解析器会检查在 list ... as item 内部使用的 sep 或者 items 指令，所以不能将 sep 从重复的部分移出到宏或被包含的模板中。
		break 指令
		
		可以使用 break 指令在迭代的任意点退出。例如：
		
		<#list 1..10 as x>
		  ${x}
		  <#if x == 3>
		    <#break>
		  </#if>
		</#list>
		
		  1
		  2
		  3
		
		break 指令可以放在 list 中的任意位置，直到有 as item 参数， 否则，可以放在 items 指令中的任意位置。 如果 break 在 items 内
		部， 那么就只能从 items 开始时存在，而不能从 list 开始时存在。通常来说，break 将仅存在于为每个迭代项调用的指令体中，
		而且只能存在于这样的指令中。 例如不能在 list 的 else 部分使用 break，除非 list 内嵌到了其它 可以 break 的指令中。
		
		像 else 和 items， break 只能在指令体内部使用，而不能移出到宏或被包含的模板中。
		访问迭代状态
		
		从 2.3.23 版本开始， 循环变量内建函数 就是访问当前迭代状态的最佳方式。例如，这里我们使用 counter 和 item_parity 循环变量
		内建函数(在 循环变量内建函数参考 中查看它们全部)：
		
		<#list users>
		  <table>
		    <#items as user>
		      <tr class="${user?item_parity}Row">
		        <td>${user?counter}
		        <td>${user}
		    </#items>
		  </table>
		</#list>
		
		  <table>
		      <tr class="oddRow">
		        <td>1
		        <td>Joe
		      <tr class="evenRow">
		        <td>2
		        <td>Kate
		      <tr class="oddRow">
		        <td>3
		        <td>Fred
		  </table>
		
		在 2.3.22 和之前的版本中，有两个额外的循环变量来获得迭代状态 (出于向后兼容考虑，它们仍然存在)：
		
		    item_index (已废弃，由 item?index 代替)： 循环中当前项的索引(从0开始的数字)。
		
		    item_has_next (已废弃，由 item?has_next 代替)： 辨别当前项是否是序列的最后一项的布尔值。
		
		所以在上面的示例中，可以将 ${user?counter} 替换为 ${user_index + 1}。
		相互嵌套循环
		
		很自然地，list 或 items 可以包含更多 list：
		
		<#list 1..2 as i>
		  <#list 1..3 as j>
		    i = ${i}, j = ${j}
		  </#list>
		</#list>
		
		    i = 1, j = 1
		    i = 1, j = 2
		    i = 1, j = 3
		    i = 2, j = 1
		    i = 2, j = 2
		    i = 2, j = 3
		
		允许使用冲突的循环变量名称，比如：
		
		<#list 1..2 as i>
		  Outer: ${i}
		  <#list 10..12 as i>
		    Inner: ${i}
		  </#list>
		  Outer again: ${i}
		</#list>
		
		  Outer: 1
		    Inner: 10
		    Inner: 11
		    Inner: 12
		  Outer again: 1
		  Outer: 2
		    Inner: 10
		    Inner: 11
		    Inner: 12
		  Outer again: 2
		
		Java程序员请注意
		
		如果经典兼容模式下 list 接受标量，并将它视为单元素序列。
		
		如果传递包装了 java.util.Iterator 的集合到 list 中，那么只能迭代其中的元素一次，因为 Iterator 是它们天然的一次性对象。 
		当视图再次去迭代这样的集合变量时，会发生错误并中止模板处理。

### local
		
		<#local name=value>
		或
		<#local name1=value1 name2=value2 ... nameN=valueN>
		或
		<#local name>
		  capture this
		</#local>
		
		这里：
		
		    name： 在root中局部对象的名称。它不是一个表达式。但它可以被写作是字符串形式， 如果变量名包含保留字符，这是很有用的，
		    比如 <#local "foo-bar" = 1>。 请注意，这个字符串没有扩展插值(如"${foo}")。
		    =：赋值操作符，也可以简写的赋值操作符之一 (++，+= 等...)，和 the assign 指令 相似。
		    value： 存储的值，是表达式。
		
		描述
		
		它和 assign 指令 类似，但是它创建或替换局部变量。 这仅仅在宏和方法的内部定义才会有作用。
		
		要获得更多关于变量的信息，可以阅读：模板开发指南/其它/在模板中定义变量

### t
### lt
### rt
		
		<#t>
		
		<#lt>
		
		<#rt>
		
		描述
		
		这些指令，指示FreeMarker去忽略标记中行的特定的空白：
		
		    t (整体削减)：忽略本行中首和尾的所有空白。
		
		    lt (左侧削减)：忽略本行中首部所有的空白。
		
		    rt (右侧削减)：忽略本行中尾部所有的空白。
		
		这里：
		
		    "首部空白" 表示本行所有空格和制表符 (和其他根据 UNICODE 中的空白字符，除了换行符) 在第一个非空白字符之前。
		
		    "尾部空白" 表示本行所有的空格和制表符 (和其他根据 UNICODE 中的空白字符，除了换行符) 在最后一个非空白字符之后，
		    还有 行末尾的换行符。
		
		理解这些检查模板本身的指令是很重要的，而 不是 当你合并数据模型时，模板生成的输出。 (也就是说，空白的移除发生在解析阶段。)
		
		例如：
		
		--
		  1 <#t>
		  2<#t>
		  3<#lt>
		  4
		  5<#rt>
		  6
		--
		
		将会输出：
		
		--
		1 23
		  4
		  5  6
		--
		
		这些指令在行内的放置不重要。也就是说，不管你是将它们放在行的开头， 或是行的末尾，或是在行的中间，效果都是一样的。

### macro
### nested
### return
		
		<#macro name param1 param2 ... paramN>
		  ...
		  <#nested loopvar1, loopvar2, ..., loopvarN>
		  ...
		  <#return>
		  ...
		</#macro>
		
		这里：
		
		    name： 宏变量的名称，它不是表达式。和 顶层变量 的语法相同，比如 myMacro 或 my\-macro。 然而，它可以被写成字符串的形式，
		    如果宏名称中包含保留字符时，这是很有用的， 比如 <#macro "foo~bar">...。 注意这个字符串没有扩展插值(如 "${foo}")。
		    param1， param2，等...： 局部变量 的名称，存储参数的值 (不是表达式)，在 = 号后面和默认值(是表达式)是可选的。 
		    默认值也可以是另外一个参数，比如 <#macro section title label=title>。参数名称和 顶层变量 的语法相同，
		    所以有相同的特性和限制。
		    paramN， 最后一个参数，可能会有三个点(...)， 这就意味着宏接受可变数量的参数，不匹配其它参数的参数可以作为最后一个参数 
		    (也被称作笼统参数)。当宏被命名参数调用， paramN 将会是包含宏的所有未声明的键/值对的哈希表。当宏被位置参数调用， 
		    paramN 将是额外参数的序列。 (在宏内部，要查找参数，可以使用 myCatchAllParam?is_sequence。)
		    loopvar1， loopvar2等...： 可选的，循环变量 的值， 是 nested 指令想为嵌套内容创建的。这些都是表达式。
		
		return 和 nested 指令是可选的，而且可以在 <#macro ...> 和 </#macro> 之间被用在任意位置和任意次数。
		
		没有默认值的参数必须在有默认值参数 (paramName=defaultValue) 之前。
		描述
		
		创建一个宏变量(在当前命名空间中，如果你知道命名空间的特性)。 如果你对宏和自定义指令不了解，你应该阅读 自定义指令指南。
		
		宏变量存储模板片段(称为宏定义体)可以被用作 自定义指令。 这个变量也存储自定义指令的被允许的参数名。当你将这个变量作为指令时， 
		你必须给所有参数赋值，除了有默认值的参数。 默认值当且仅当你调用宏而不给参数赋值时起作用。
		
		变量会在模板开始时被创建；而不管 macro 指令放置在模板的什么位置。因此，这样也可以：
		
		<#-- call the macro; the macro variable is already created: -->
		<@test/>
		...
		
		<#-- create the macro variable: -->
		<#macro test>
		  Test text
		</#macro>
		
		然而，如果宏定义被插在 include 指令中， 它们直到 FreeMarker 执行 include 指令时才会可用。
		
		例如：没有参数的宏：
		
		<#macro test>
		  Test text
		</#macro>
		<#-- call the macro: -->
		<@test/>
		
		将会输出：
		
		  Test text
		 
		
		示例：有参数的宏：
		
		<#macro test foo bar baaz>
		  Test text, and the params: ${foo}, ${bar}, ${baaz}
		</#macro>
		<#-- call the macro: -->
		<@test foo="a" bar="b" baaz=5*5-2/>
		
		将会输出：
		
		  Test text, and the params: a, b, 23
		   
		
		示例：有参数和默认值参数的宏：
		
		<#macro test foo bar="Bar" baaz=-1>
		  Test text, and the params: ${foo}, ${bar}, ${baaz}
		</#macro>
		<@test foo="a" bar="b" baaz=5*5-2/>
		<@test foo="a" bar="b"/>
		<@test foo="a" baaz=5*5-2/>
		<@test foo="a"/>
		
		将会输出：
		
		  Test text, and the params: a, b, 23
		  Test text, and the params: a, b, -1
		  Test text, and the params: a, Bar, 23
		  Test text, and the params: a, Bar, -1
		 
		
		示例：更为复杂的宏。
		
		<#macro list title items>
		  <p>${title?cap_first}:
		  <ul>
		    <#list items as x>
		      <li>${x?cap_first}
		    </#list>
		  </ul>
		</#macro>
		<@list items=["mouse", "elephant", "python"] title="Animals"/>
		
		将会输出：
		
		  <p>Animals:
		  <ul>
		      <li>Mouse
		      <li>Elephant
		      <li>Python
		  </ul>
		 
		
		示例：支持多个参数和命名参数的宏：
		
		<#macro img src extra...>
		  <img src="/context${src?html}" 
		  <#list extra?keys as attr>
		    ${attr}="${extra[attr]?html}"
		  </#list>
		  >
		</#macro>
		<@img src="/images/test.png" width=100 height=50 alt="Test"/>
		
		将会输出：
		
		  <img src="/context/images/test.png"
		    alt="Test"
		    height="50"
		    width="100"
		  >
		
		示例：支持多个位置参数的宏，不管是否使用命名或位置参数传递：
		
		<#macro m a b ext...>
		  a = ${a}
		  b = ${b}
		  <#if ext?is_sequence>
		    <#list ext as e>
		      ${e?index} = ${e}
		    </#list>
		  <#else>
		    <#list ext?keys as k>
		      ${k} = ${ext[k]}
		    </#list>
		  </#if>
		</#macro>
		
		<@m 1 2 3 4 5 />
		
		<@m a=1 b=2 c=3 d=4 e=5 data\-foo=6 myns\:bar=7 />
		
		将会输出：
		
		  a = 1
		  b = 2
		      0 = 3
		      1 = 4
		      2 = 5
		
		  a = 1
		  b = 2
		      c = 3
		      d = 4
		      e = 5
		      data-foo=6
		      myns:bar=7
		
		Warning!
		
		当前，命名的笼统参数是无序的，也就是说，不知道它们枚举时的顺序。 那么它们不会按相同传递顺序返回(上述示例输出相同的顺序只是为了理解)。
		nested
		
		nested 指令执行自定义指令开始和结束标签中间的模板片段。 嵌套的片段可以包含模板中任意合法的内容：插值，指令等...它在上下文环境
		中被执行， 也就是宏被调用的地方，而不是宏定义体的上下文中。因此，比如， 你不能看到嵌套部分的宏的局部变量。
		如果你没有调用 nested 指令， 自定义指令开始和结束标记中的部分将会被忽略。
		
		比如：
		
		<#macro do_twice>
		  1. <#nested>
		  2. <#nested>
		</#macro>
		<@do_twice>something</@do_twice>
		
		将会输出：
		
		  1. something
		  2. something
		 
		
		nested 指令可以对嵌套内容创建循环变量。例如：
		
		<#macro do_thrice>
		  <#nested 1>
		  <#nested 2>
		  <#nested 3>
		</#macro>
		<@do_thrice ; x>
		  ${x} Anything.
		</@do_thrice>
		
		将会输出：
		
		  1 Anything.
		  2 Anything.
		  3 Anything.
		 
		
		更为复杂的示例：
		
		<#macro repeat count>
		  <#list 1..count as x>
		    <#nested x, x/2, x==count>
		  </#list>
		</#macro>
		<@repeat count=4 ; c, halfc, last>
		  ${c}. ${halfc}<#if last> Last!</#if>
		</@repeat>
		
		将会输出：
		
		  1. 0.5
		  2. 1
		  3. 1.5
		  4. 2 Last!
		 
		
		return
		
		使用 return 指令， 你可以在任意位置留下一个宏或函数定义。比如：
		
		<#macro test>
		  Test text
		  <#return>
		  Will not be printed.
		</#macro>
		<@test/>
		
		将会输出：
		
		  Test text


### noparse
		
		<#noparse>
		  ...
		</#noparse>
		
		描述
		
		FreeMarker 不会在这个指令体中间寻找FTL标签， 插值和其他特殊的字符序列，除了noparse的结束标记。
		
		比如：
		
		Example:
		--------
		
		<#noparse>
		  <#list animals as animal>
		  <tr><td>${animal.name}<td>${animal.price} Euros
		  </#list>
		</#noparse>
		
		将会输出：
		
		Example:
		--------
		
		  <#list animals as animal>
		  <tr><td>${animal.name}<td>${animal.price} Euros
		  </#list>
		 
### nt
		
		<#nt>
		
		描述
		
		"不要削减"。该指令禁用行中出现的 剥离空白。 它也关闭其他同一行中出现的削减指令(t， rt，lt的效果)。


### setting
		
		<#setting name=value>
		
		这里：
		
		    name： 设置的名称。不是表达式！
		    value： 设置的新值，是表达式。
		
		描述
		
		为进一步的处理而设置。设置是影响 FreeMarker 行为的值。 新值仅仅在被设置的模板处理时出现，而且不触碰模板本身。 
		设置的初始值是由程序员设定的 (参考： 程序开发指南/配置(Configuration)/配置设置)。

### stop

		<#stop>
		或
		<#stop reason>
		
		这里：
		
		    reason： 关于终止原因的信息化消息。是表达式，被算做是字符串。
		
		描述
		
		中止模板处理，给出(可选的)错误消息。 不要在普通情况下对结束模板处理使用！ FreeMarker 模板的调用者会将它视为失败的模板呈现， 
		而不是普通完成的。
		
		该指令抛出 StopException，而且 StopException 会持有 reason 参数的值

### user_defined_directive
		
		<@user_def_dir_exp param1=val1 param2=val2 ... paramN=valN/>
		(注意 XML 风格， / 在 > 之前)  
		或如果需要循环变量 (更多细节...)
		<@user_def_dir_exp param1=val1 param2=val2 ... paramN=valN ; lv1, lv2, ..., lvN/>
		
		或和上面两个相同但是使用结束标签 (更多细节...):
		
		<@user_def_dir_exp ...>
		  ...
		</@user_def_dir_exp>
		或
		<@user_def_dir_exp ...>
		  ...
		</@>
		
		或和上面的相同但是使用位置参数传递 (更多细节...):
		
		<@user val1, val2, ..., valN/>
		等...
		
		这里：
		
		    user_def_dir_exp： 表达式算作是自定义指令(比如宏)，将会被调用。
		    param1， param2等...： 参数的名称，它们 不是 表达式。
		    val1， val2等...： 参数的值，它们 是 表达式。
		    lv1， lv2等...： 循环变量 的名称， 它们 不是 表达式。
		
		参数的数量可以是0(也就是没有参数)。
		
		参数的顺序并不重要(除非你使用了位置参数传递)。 参数名称必须唯一。在参数名中小写和大写的字母被认为是不同的字母 
		(也就是 Color 和 color 是不同的)。
		描述
		
		这将调用用户自定义指令，比如宏。参数的含义， 支持和需要的参数的设置依赖于具体的自定义指令。
		
		你可以阅读 自定义指令。


