Freemarker 内置函数
====


* [abs](#abs "绝对值")
* [ancestors](#ancestors)
* [api](#api)
* [boolean](#boolean)
* [byte](#byte)
* [c](#c)
* [cap_first](#cap_first)
* [capitalize](#capitalize)
* [ceiling](#ceiling)
* [children](#children)
* [chop_linebreak](#chop_linebreak)
* [chunk](#chunk)
* [contains](#contains)
* [counter](#counter)
* [date for dates, date for strings](#date)
* [date_if_unknown](#date_if_unknown)
* [datetime for dates, for strings](#datetime)
* [datetime_if_unknown](#datetime_if_unknown)
* [double](#double)
* [ends_with](#ends_with)
* [ensure_ends_with](#ensure_ends_with)
* [ensure_starts_with](#ensure_starts_with)
* [eval](#eval)
* [first](#first)
* [floor](#floor)
* [groups](#groups)
* [float](#float)
* [has_api](#has_api)
* [has_content](#has_content)
* [has_next](#has_next)
* [html](#html)
* [index](#index)
* [index_of](#index_of)
* [int](#int)
* [interpret](#interpret)
* [item_cycle](#item_cycle)
* [item_parity](#item_parity)
* [item_parity_cap](#item_parity_cap)
* [is_even_item](#is_even_item)
* [is_first](#is_first)
* [is_infinite](#is_infinite)
* [is_last](#is_last)
* [is_nan](#is_nan)
* [is_odd_item](#is_odd_item)
* [is_type](#is_type)
* [iso, iso_...](#iso)
* [j_string](#j_string)
* [join](#join)
* [js_string](#js_string)
* [keep_after](#keep_after)
* [keep_after_last](#keep_after_last)
* [keep_before](#keep_before)
* [keep_before_last](#keep_before_last)
* [keys](#keys)
* [last](#last)
* [last_index_of](#last_index_of)
* [left_pad](#left_pad)
* [length](#length)
* [long](#long)
* [lower_abc](#lower_abc)
* [lower_case](#lower_case)
* [matches](#matches)
* [namespace](#namespace)
* [new](#new)
* [node_namespace](#node_namespace)
* [node_name](#node_name)
* [node_type](#node_type)
* [number](#number)
* [number_to_date, number_to_datetime, number_to_time](#number_to_date)
* [parent](#parent)
* [replace](#replace)
* [remove_beginning](#remove_beginning)
* [remove_ending](#remove_ending)
* [reverse](#reverse)
* [right_pad](#right_pad)
* [round](#round)
* [root](#root)
* [rtf](#rtf)
* [short](#short)
* [size](#size)
* [sort](#sort)
* [seq_contains](#seq_contains)
* [seq_index_of](#seq_index_of)
* [seq_last_index_of](#seq_last_index_of)
* [sort_by](#sort_by)
* [split](#split)
* [starts_with](#starts_with)
* [string: for strings, for numbers, for booleans, for date/time/date-time](#string)
* [substring (deprecated)](#substring)
* [switch](#switch)
* [then](#then)
* [time for date/time/date-time, for strings](#time)
* [time_if_unknown](#time_if_unknown)
* [trim](#trim)
* [uncap_first](#uncap_first)
* [upper_abc](#upper_abc)
* [upper_case](#upper_case)
* [url](#url)
* [values](#values)
* [word_list](#word_list)
* [xhtml](#xhtml)
* [xml](#xml)


### abs

		绝对值。比如 x?abs ，如果 x 是 -5，会得到5。

### ancestors

		绝对值。比如 x?abs ，如果 x 是 -5，会得到5。

### api
### has_api

		调用对象的JAVA API，不建议使用。
		如 value?api.someJavaMethod()，例如，当有一个 Map，并放入数据模型 (使用默认的对象包装器)，
		模板中的 myMap.myMethod() 基本上翻译成Java的 ((Method) myMap.get("myMethod")).invoke(...)，
		因此不能调用 myMethod。如果编写了 myMap?api.myMethod() 来代替，那么就是Java中的 myMap.myMethod()。
		api_builtin_enabled 配置设置项必须设置为 true。

### boolean

		字符串转为布尔值。字符串必须是 true 或 false (大小写敏感！)，或者必须是由 boolean_format 设置的特定格式。

### byte
### double
### float
### int
### long
### short

		转换成指定的类型值。
		从2.3.9版本开始，解包器有本质上改进， 所以将基本不会使用到这些内建函数来转换数字类型了。
		如，日期转换为long，.now?long

### c

		打印字符串，并去除本地化和格式化。
		如，a=3000000, ${a} 输出：3,000,000，${a?c} 输出：3000000。

### cap_first

		字符串中的首单词的首字母大写。 关于"单词"的准确意义，可以参考 word_list 内建函数。 例如：
		
		${"  green mouse"?cap_first}
		${"GreEN mouse"?cap_first}
		${"- green mouse"?cap_first}
		
		将会输出：
		
		  Green mouse
		GreEN mouse
		- green mouse

### capitalize

		字符串中所有单词的首字母大写。 关于"单词"的准确意义，可以参考 word_list 内建函数。例如：
		
		${"  green  mouse"?capitalize}
		${"GreEN mouse"?capitalize}
		
		将会输出：
		
		  Green Mouse
		Green Mouse
		- green mouse

### round
### floor
### ceiling

		使用确定的舍入法则，转换一个数字到整数：
		
		    round：返回最近的整数。 如果数字以.5结尾，那么它将进位(也就是向正无穷方向进位)
		
		    floor：返回数字的舍掉小数后的整数 (也就是向负无穷舍弃)
		
		    ceiling：返回数字小数进位后的整数 (也就是向正无穷进位)
		
		例如：
		
		<#assign testlist=[
		  0, 1, -1, 0.5, 1.5, -0.5,
		  -1.5, 0.25, -0.25, 1.75, -1.75]>
		<#list testlist as result>
		    ${result} ?floor=${result?floor} ?ceiling=${result?ceiling} ?round=${result?round}
		</#list>
		
		输出：
		
		    0 ?floor=0 ?ceiling=0 ?round=0            
		    1 ?floor=1 ?ceiling=1 ?round=1        
		    -1 ?floor=-1 ?ceiling=-1 ?round=-1      
		    0.5 ?floor=0 ?ceiling=1 ?round=1      
		    1.5 ?floor=1 ?ceiling=2 ?round=2      
		    -0.5 ?floor=-1 ?ceiling=0 ?round=0     
		    -1.5 ?floor=-2 ?ceiling=-1 ?round=-1    
		    0.25 ?floor=0 ?ceiling=1 ?round=0     
		    -0.25 ?floor=-1 ?ceiling=0 ?round=0    
		    1.75 ?floor=1 ?ceiling=2 ?round=2     
		    -1.75 ?floor=-2 ?ceiling=-1 ?round=-2
		
		这些内建函数在分页处理时也许有用。如果你仅仅想 展示 数字的舍入形式，那么应该使用 string 内建函数 或者 number_format 设置。

### children
		
		一个包含该结点所有子结点(也就是直接后继结点)的序列。
		
		XML：这和特殊的哈希表的键 * 几乎是一样的。 除了它返回所有结点，而不但是元素。所以可能的子结点是元素结点， 
		文本结点，注释结点，处理指令结点等，但 不是 属性结点。属性结点排除在序列之外。

### chop_linebreak
		
		在末尾没有换行符的字符串， 那么可以换行，否则不改变字符串。

### chunk
		
		该内建函数将序列分隔为多个序列，长度为第一个参数给定的值 (比如 mySeq?chunk(3))。结果是包含这些序列的一个序列。 
		最后一个序列可能比给定的长度要小，除非第二个参数也给定了 (比如 比如 mySeq?chunk(3, '-'))， 这就是用来填充最后一个序列，
		以达到给定的长度。例如：
		
		<#assign seq = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j']>
		
		<#list seq?chunk(4) as row>
		  <#list row as cell>${cell} </#list>
		</#list>
		
		<#list seq?chunk(4, '-') as row>
		  <#list row as cell>${cell} </#list>
		</#list>
		
		将会输出：
		
		  a b c d 
		  e f g h 
		  i j 
		
		  a b c d 
		  e f g h 
		  i j - - 
		 
		
		该内建函数通常在输出的序列中使用表格/柱状的格式。 当被用于HTML表格时，第二个参数通常是"\xA0" 
		(也就是不换行的空格代码，也就是我们所知的"nbsp")， 所以空TD的边界就不会不丢失。
		
		第一个参数必须是一个数字，而且至少是1。如果这个数字不是整数， 那么它会被静默地去掉小数部分(也就是说3.1和3.9都会被规整为3)。 
		第二个参数可以是任意类型的值。

### contains

		如果函数中的参数指定的子串出现在源字符串中， 那么返回true。比如：
		
		<#if "piceous"?contains("ice")>It contains "ice"</#if>
		
		将会输出
		
		It contains "ice"

### counter

		返回当前迭代(由循环变量名称识别)从1开始的索引。
		
		<#list ['a', 'b', 'c'] as i>
		  ${i?counter}: ${i}
		</#list>
		
		  1: a
		  2: b
		  3: c
		
		Note:
		
		要从0开始的索引，请使用 index 内建函数。

### date
### time
### datetime 

		当用于日期/时间/日期-时间值时
		
		这些内建函数用来指定日期变量中的哪些部分被使用：
		
		    date：仅日期部分，没有一天当中的时间部分。
		
		    time：仅一天当中的时间部分，没有日期部分。
		
		    datetime：日期和时间都在
		
		在最佳情况下，你不需要使用这些内建函数。不幸的是， 由于Java平台上的技术限制，FreeMarker 有时不能发现日期中的哪一部分在使用； 
		询问程序员哪些变量会有这个问题。如果 FreeMarker 不得不执行需要这些信息的操作 --比如用文本显示日期--但是它不知道哪一部分在使用，
		它会以错误来中止运行。 这就是你不得不使用这些内建函数的时候了。比如，假设 openingTime 是一个有这样问题的变量：
		
		<#assign x = openingTime> <#-- no problem can occur here -->
		${openingTime?time} <#-- without ?time it would fail -->
		<#-- For the sake of better understanding, consider this: -->
		<#assign openingTime = openingTime?time>
		${openingTime} <#-- this will work now -->
		
		这些内建函数也可以用来将日期-时间值转换成日期或时间。例如：
		
		Last updated: ${lastUpdated} <#-- assume that lastUpdated is a date-time value -->
		Last updated date: ${lastUpdated?date}
		Last updated time: ${lastUpdated?time}
		
		将会输出：
		
		Last updated: 04/25/2003 08:00:54 PM
		Last updated date: 04/25/2003
		Last updated time: 08:00:54 PM
		
		如果 ? 左边是字符串，那么这些内建函数 将字符串转换成日期/时间/日期时间。

### date
### time
### datetime
		
		字符串转换成日期值，时间或日期-时间值。需要一个由 date_format， time_format 和 datetime_format 设置指定的格式。 
		如果字符串不是适当的格式，那么当访问该内建函数时， 就会发生错误中止模板的处理。
		
		<#-- The date_format, time_format and datetime_format settings must match this format! -->
		<#assign someDate = "Oct 25, 1995"?date>
		<#assign someTime = "3:05:30 PM"?time>
		<#assign someDatetime = "Oct 25, 1995 03:05:00 PM"?datetime>
		
		<#-- Changing the setting value changes the expected format: -->
		<#setting datetime_format="iso">
		<#assign someDatetime = "1995-10-25T15:05"?datetime>
		
		也可以指定明确的格式，比如 ?datetime.format 或 ?datetime["format"] (由于历史原因，也可以是 ?datetime("format"))， 
		它们与 ?date 和 ?time是相同的。 对于格式化值的语法和意义，可以参考 date_format, time_format 和 datetime_format 
		设置的可能的值。 比如：
		
		<#-- Parsing XML Schema xs:date, xs:time and xs:dateTime values: -->
		<#assign someDate = "1995-10-25"?date.xs>
		<#assign someTime = "15:05:30"?time.xs>
		<#assign someDatetime = "1995-10-25T15:05:00"?datetime.xs>
		
		<#-- Parsing ISO 8601 (both extended and basic formats): -->
		<#assign someDatetime = "1995-10-25T15:05"?datetime.iso>
		<#assign someDatetime = "19951025T1505"?datetime.iso>
		
		<#-- Parsing with SimpleDateFormat patterns: -->
		<#assign someDate = "10/25/1995"?date("MM/dd/yyyy")>
		<#assign someTime = "15:05:30"?time("HH:mm:ss")>
		<#assign someDatetime = "1995-10-25 03:05 PM"?datetime("yyyy-MM-dd hh:mm a")>
		
		避免误解，左侧值无需是字符串文本。比如，当从XML结点 (此处的所有值都是未被解析的字符串)读取数据，
		那么就需要这样来做 order.confirmDate?date.xs，将字符串转化成真实的日期。
		
		当然，格式也可以是一个变量，比如： "..."?datetime[myFormat]。

### date_if_unknown
### time_if_unknown
### datetime_if_unknown

		date_if_unknown， time_if_unknown， datetime_if_unknown 内建函数使用一些子类型来标记日期类型的值：
		日期没有时间，时间，或日期-时间。 如果变量值已经持有这些信息，那么内建函数就不会起作用。也就是说， 
		它不会转换变量值的子类型，如果它是未知的，则会添加子类型。

### ends_with

		返回是否这个字符串以参数中指定的子串结尾。 比如 "ahead"?ends_with("head") 返回布尔值 true。
		"head"?ends_with("head") 也返回 true。

### ensure_ends_with

		如果字符串没有以第一个参数指定的子串结尾， 那么就会将它加到字符串后面，否则返回原字符串。
		比如， "foo"?ensure_ends_with("/") 和 "foo/"?ensure_ends_with("/") 返回 "foo/"。

### ensure_starts_with

		如果字符串没有以第一个参数指定的子串开头， 那么就会将它加到字符串开头，否则返回原字符串。
		比如， "foo"?ensure_starts_with("/") 和 "/foo"?ensure_starts_with("/") 返回 "/foo"。

		如果指定两个参数，那么第一个参数就被解释成Java正则表达式， 如果它不匹配字符串的开头，那么第二个参数指定的字符串就会添加到字符串开头。
		 比如 someURL?ensure_starts_with("[a-zA-Z]+://", "http://") 就会检查如果字符串是否以 "[a-zA-Z]+://" 开头 
		 (请注意，不需要 ^)，如果不是的话，就添加 "http://"。
		
		该方法也接受第三个标志位参数。因为调用两个参数暗指 "r"(也就是正则表达式模式)，那么就需要第三个参数了。 
		值得注意的一点是当不需要第一参数被解释成正则表达式，而只是普通文本， 但是又想让比较是大小写敏感的，
		那么此时就需要使用 "i" 作为第三个参数。

### eval

		这个函数求一个作为FTL表达式的字符串的值。比如 "1+2"?eval返回数字3。
		
		在调用 eval 的地方， 已被求值的表达式看到相同的变量(比如本地变量)是可见的。 也就是说，它的行为就像在 s?eval 处， 
		你有 s 的 值。除了，指向在 s 之外创建的循环变量，它不能使用 循环变量内建函数。
		
		配置设置项影响来自 Configuration 对象表达式解析(比如语法)，而不是来自调用 eval 的的模板。

### first

		序列的第一个子变量。如果序列为空，那么模板处理将会中止。

### round
### floor
### ceiling
		
		使用确定的舍入法则，转换一个数字到整数：
		
		    round：返回最近的整数。 如果数字以.5结尾，那么它将进位(也就是向正无穷方向进位)
		
		    floor：返回数字的舍掉小数后的整数 (也就是向负无穷舍弃)
		
		    ceiling：返回数字小数进位后的整数 (也就是向正无穷进位)
		
		例如：
		
		<#assign testlist=[
		  0, 1, -1, 0.5, 1.5, -0.5,
		  -1.5, 0.25, -0.25, 1.75, -1.75]>
		<#list testlist as result>
		    ${result} ?floor=${result?floor} ?ceiling=${result?ceiling} ?round=${result?round}
		</#list>
		
		输出：
		
		    0 ?floor=0 ?ceiling=0 ?round=0            
		    1 ?floor=1 ?ceiling=1 ?round=1        
		    -1 ?floor=-1 ?ceiling=-1 ?round=-1      
		    0.5 ?floor=0 ?ceiling=1 ?round=1      
		    1.5 ?floor=1 ?ceiling=2 ?round=2      
		    -0.5 ?floor=-1 ?ceiling=0 ?round=0     
		    -1.5 ?floor=-2 ?ceiling=-1 ?round=-1    
		    0.25 ?floor=0 ?ceiling=1 ?round=0     
		    -0.25 ?floor=-1 ?ceiling=0 ?round=0    
		    1.75 ?floor=1 ?ceiling=2 ?round=2     
		    -1.75 ?floor=-2 ?ceiling=-1 ?round=-2
		
		这些内建函数在分页处理时也许有用。如果你仅仅想 展示 数字的舍入形式，那么应该使用 string 内建函数 或者 number_format 设置。

### groups

		这个函数只作用于内建函数 matches 的结果。参考matches。

### has_content

		如果变量(不是Java的 null) 存在而且不是"空"就返回 true，否则返回 false。"空”"含义靠具体的情形来决定。 它是直观的常识性概念。
		下面这些都是空：长度为0的字符串， 没有子变量的序列或哈希表，一个已经超过最后一项元素的集合。 如果值不是字符串，序列，
		哈希表或集合，如果它是数字，日期或布尔值 (比如 0 和 false 是非空的)， 那么它被认为是非空的，否则就是空的。
		注意当你的数据模型实现了多个模板模型接口， 你可能会得到不是预期的结果。然而，
		当你有疑问时你通常可以使用 expr!?size > 0 或 expr!?length > 0 来代替 expr?has_content。
		
		这个函数是个特殊的函数，你可以使用像 默认值操作符 那样的圆括号手法。也就是说，你可以编写 product.color?has_content 
		和 (product.color)?has_content 这样的代码。 第一个没有控制当 product 为空的情形，而第二个控制了。

### has_next
		
		辨别循环项是否是当前迭代(由循环变量名称识别)的最后一项。
		
		<#list ['a', 'b', 'c'] as i>${i?has_next?c} </#list>
		
		true true false 
		
		Note:
		
		使用逗号等隔开循环项，请使用 <#sep>separator</#sep> 来代替 <#if var?has_next>separator</#if>，这样可读性更强。
		(此外 </#sep> 经常被忽略，比如在 <#list ... as var>...${var}...<#sep>separator</#list> 中)
		Note:
		
		如果需要对该内建函数取反，请使用 var?is_last 来代替 !var?has_next， 因为它的可读性更强。

### html

		字符串按照HTML标记输出。也就是说，下面字符串将会被替代：
		
		    < 替换为 &lt;
		    > 替换为 &gt;
		    & 替换为 &amp;
		    " 替换为 &quot;
		    如果程序员设置了FreeMarker(将 incompatible_improvements 设置为 2.3.20 或更高；那么 ' 被替换为 &#39;。
		
		请注意，如果想安全地插入一个属性， 必须在HTML模板中使用引号标记(是 "，而不是 ')为属性值加引号：
		
		<input type=text name=user value="${user?html}">
		
		请注意，在HTML页面中，通常想对所有插值使用这个内建函数。 所以可以使用 escape 指令 来节约很多输入，减少偶然错误的机会。

### index
		
		返回当前迭代(由循环变量名称识别)从0开始的索引。
		
		<#list ['a', 'b', 'c'] as i>
		  ${i?index}: ${i}
		</#list>
		
		  0: a
		  1: b
		  2: c
		
		Note:
		
		要从1开始的索引，请使用 counter 内建函数。

### index_of

		返回第一次字符串中出现子串时的索引位置。 例如 "abcabc"?index_of("bc") 将会返回1 (不要忘了第一个字符的索引是0)。
		而且，你可以指定开始搜索的索引位置： "abcabc"?index_of("bc", 2) 将会返回4。 这对第二个参数的数值没有限制：如果它是负数，
		那就和是0是相同效果了， 如果它比字符串的长度还大，那么就和它是字符串长度那个数值是一个效果。 小数会被切成整数。
		
		如果第一个参数作为子串没有在该字符串中出现时 (如果你使用了第二个参数，那么就从给定的序列开始)，那么就返回-1。

### interpret

		该内建函数解析字符串作为FTL模板，而且返回一个用户自定义指令， 也就是当应用于任意代码块中时，执行模板就像它当时 被包含 一样。例如：
		
		<#assign x=["a", "b", "c"]>
		<#assign templateSource = r"<#list x as y>${y}</#list>">
		<#-- Note: That r was needed so that the ${y} is not interpreted above -->
		<#assign inlineTemplate = templateSource?interpret>
		<@inlineTemplate />
		
		将会输出：
		
		abc
		
		正如你看到的，inlineTemplate 是用户自定义指令， 也就是当被执行时，运行当时内容是是 templateSource 值的模板。
		
		配置设置项影响来自 Configuration 对象的解析(比如标签语法和命名转换)，而不是来自调用 interpret 的模板。 
		这也就以为着之前的自动探测标签语法或命名转换不会影响已解释模板的解析。 这也和 include 指令 的工作方式一致。
		
		通过 interpret 创建的模板名称就是模板调用 interpret 的名称，并加上 "->anonymous_interpreted"。例如， 
		如果模板调用的内建函数是 "foo/bar.ftl"， 那么结果模板的名称就是 "foo/bar.ftl->anonymous_interpreted"。 
		因此，在已解释模板中的相对路径也就相对于该路径 (也就是说，根路径就是 "foo")， 而且在已解释模板内部的错误将会指向这个生成的模板名称。
		
		要得到更多有用的错误消息，可以在 "->" 之后覆盖模板名称部分。例如，我们说 mailTemplateSource 来自于数据库表 mail_template，
		那么在错误时， 想得到包含数据库ID的失败模板的错误日志：
		
		<#assign inlineTemplate = [mailTemplateSource, "mail_templates id=${mailTemplateId}"]?interpret>
		
		正如你看到的，interpret 可以应用于有两项的序列， 这里的第一项是要解释的FTL字符串，第二项是在 "->" 之后使用的模板名称。

### item_cycle
		
		这是 item_parity 内建函数 更为通用的版本，这里可以指定何值来代替 "odd" 和 "even"。 它也允许多余两个值来循环。
		
		<#list ['a', 'b', 'c', 'd', 'e', 'f', 'g'] as i>
		  <tr class="${i?item_cycle('row1', 'row2', 'row3')}">${i}</tr>
		</#list>
		
		  <tr class="row1">a</tr>
		  <tr class="row2">b</tr>
		  <tr class="row3">c</tr>
		  <tr class="row1">d</tr>
		  <tr class="row2">e</tr>
		  <tr class="row3">f</tr>
		  <tr class="row1">g</tr>
		
		一些细节：
		
		    参数的个数至少是1个，没有上限。
		
		    参数的类型是任意的，无需只是字符串。
		
		Note:
		
		如果需要"odd" 和 "even"，请使用 item_parity 内建函数 来代替。

### item_parity
		
		基于当前迭代(由循环变量名称识别)间隔为1的索引的奇偶性， 返回字符串值 "odd" 或 "even"。 这通常用于表格中行间的颜色变换：
		
		<#list ['a', 'b', 'c', 'd'] as i>
		  <tr class="${i?item_parity}Row">${i}</tr>
		</#list>
		
		  <tr class="oddRow">a</tr>
		  <tr class="evenRow">b</tr>
		  <tr class="oddRow">c</tr>
		  <tr class="evenRow">d</tr>
		
		Note:
		
		请使用 item_parity_cap 内建函数 来大写 "Odd" 和 "Even"。请使用 item_cycle 内建函数 来指定自定义值，或多于两个值。

### item_parity_cap
		
		基于当前迭代(由循环变量名称识别)间隔为1的索引的奇偶性， 返回字符串值 "Odd" 或 "Even" (请注意大写)。
		
		<#list ['a', 'b', 'c', 'd'] as i>
		  <tr class="row${i?item_parity_cap}">${i}</tr>
		</#list>
		
		  <tr class="rowOdd">a</tr>
		  <tr class="rowEven">b</tr>
		  <tr class="rowOdd">c</tr>
		  <tr class="rowEven">d</tr>
		
		Note:
		
		请使用 item_parity 内建函数 来小写 "odd" 和 "even"。

### is_even_item
		
		辨别循环项是否是当前迭代(由循环变量名称识别)间隔1的奇数项。
		
		<#list ['a', 'b', 'c', 'd'] as i>${i?is_even_item?c} </#list>
		
		false true false true
		
		Note:
		
		要将表格进行行间变色等操作，请使用 var?item_parity 或 var?item_cycle(...) 来代替。

### is_first
		
		辨别是否为序列的第一个子变量。

### is_infinite
		
		辨别数字是否是无限浮点数(根据IEEE 754)。比如， 基于 someNumber 的值是否是无限， someNumber?is_infinite 
		结果是 true 或 false，当然， 如果该数不是浮点类型，那么将会返回 false。

### is_last

		辨别循环项是否是当前迭代(由循环变量名称识别)的最后一项。
		
		<#list ['a', 'b', 'c'] as i>${i?is_last?c} </#list>
		
		false false true
		
		Note:
		
		如果需要对该内建函数取反，请使用 var?has_next 来代替 !var?is_last， 因为它的可读性更强。
		Note:
		
		使用逗号等隔开循环项，请使用 <#sep>separator</#sep> 来代替 <#if var?has_next>separator</#if>，
		因为它的可读性更强。(此外 </#sep> 经常被忽略，比如在 <#list ... as var>...${var}...<#sep>separator</#list> 中)

### is_nan

		辨别数字是否是浮点数NaN(根据IEEE 754)。比如， 基于 someNumber 的值是否是 NaN， someNumber?is_nan 
		结果是 true 或 false，当然， 如果该数不是浮点类型，那么将会返回 false。

### is_odd_item

		辨别循环项是否是当前迭代(由循环变量名称识别)间隔1的偶数项。
		
		<#list ['a', 'b', 'c', 'd'] as i>${i?is_odd_item?c} </#list>
		
		true false true false 
		
		Note:
		
		要将表格进行行间变色等操作，请使用 var?item_parity 或 var?item_cycle(...) 来代替。

### is_type

		is_...
		
		这些内建函数用来检查变量的类型，然后根据类型返回 true 或 false。 下面是 is_... 内建函数列表：
		内建函数 	如果值是 … 时返回 true
		is_string 	字符串
		is_number 	数字
		is_boolean 	布尔值
		is_date 	不要使用它！使用 is_date_like 来代替， 它们是相同的。往后也许会修改它的含义为 date_only。
		is_date_like 	日期，也就似乎日期，时间或者日期-时间， 亦或者是未知精确类型的日期(从 FreeMarker 2.3.21 版本开始)
		is_date_only 	日期 (没有时间部分) (从 FreeMarker 2.3.21 版本开始)
		is_time 	时间 (没有年-月-日部分) (从 FreeMarker 2.3.21 版本开始)
		is_datetime 	日期-时间 (包含年-月-日和时间两者)
		is_unknown_date_like 	不清楚是日期或时间或日期-时间的日期
		is_method 	方法
		is_transform 	变换
		is_macro 	宏或函数(当然，由于历史问题，也对函数有效)
		is_hash 	哈希表 (包含扩展的哈希表)
		is_hash_ex 	扩展的哈希表 (支持 ?keys 和 ?values)
		is_sequence 	序列
		is_collection 	集合 (包含扩展的集合)
		is_collection_ex 	扩展的集合 (支持 ?size)
		is_enumerable 	序列或集合
		is_indexable 	序列
		is_directive 	指令类型 (例如宏 或 TemplateDirectiveModel， TemplateTransformModel, 等...)， 或者函数 (由于历史问题)
		is_node 	结点

### iso

		iso_...
		
		这些内建函数从 FreeMarker 2.3.21 版本开始废弃， 因为 date_format，time_format 和 datetime_format 
		设置理解 "iso" (ISO 8601:2004 格式) 和 "xs" (XML Schema 格式)，此外还有 Java SimpleDateFormat 格式。
		因此默认格式可以设置为ISO 8601， 要使用一次ISO格式，可以使用 myDate?string.iso。 

### j_string

		根据Java语言字符串转义规则来转义字符串， 所以它很安全的将值插入到字符串类型中。要注意它 不会 在被插入的值的两侧添加引号； 
		你需要在字符串值 内部 来使用。
		
		所有 UCS 编码下指向0x20的字符会被转义。 当它们在Java语言中(比如\n，\t等) 没有专门的转义序列时，
		将会被用UNICODE进行转义替换 (\uXXXX)。
		
		例如：
		
		<#assign beanName = 'The "foo" bean.'>
		String BEAN_NAME = "${beanName?j_string}";
		
		将会输出：
		
		String BEAN_NAME = "The \"foo\" bean.";

### join

		使用给定的分隔符来连接序列中的项为一个独立的字符串，例如：
		
		<#assign colors = ["red", "green", "blue"]>
		${colors?join(", ")}
		
		将会输出：
		
		red, green, blue
		
		序列中不是字符串的项会被转换为字符串，使用 ${...} 相同的转换规则 (当然这里不会应用自动转义)。
		
		?join(...) 最多可以有3个参数：
		
		    分隔符，是必须的:插入到每一项中间的字符串
		
		    空值，默认是 "" (空字符串)： 如果序列为空，使用该值。
		
		    列表结尾，默认是 "" (空字符串): 如果列表序列不为空，该值在最后一个值后面输出。
		
		所以 (这里 [] 意味着空序列)：
		
		${colors?join(", ", "-")}
		${[]?join(", ", "-")}
		
		${colors?join(", ", "-", ".")}
		${[]?join(", ", "-", ".")}
		
		将会输出：
		
		red, green, blue
		-
		
		red, green, blue.
		-
		
		来自Java的序列可能会包含 null 值。那些值会被该内建函数忽略， 就像它们从列表中被移除了。

### js_string
		
		根据JavaScript语言字符串转义规则来转义字符串， 所以它很安全的将值插入到字符串类型中。要注意， 它不会在被插入的值两侧添加引号； 
		你需要在字符串值 内部 来使用。
		
		引号(")和单引号(')要被转义。 从 FreeMarker 2.3.1 开始，也要将 > 转义为 \>(为了避免 </script>)。
		
		所有在 UCS 编码下指向0x20的字符将会被转义。 当它们在JavaScript中没有专用的转义序列时 (比如 \n，\t 等)， 
		它们会被UNICODE字符代替(\uXXXX)。
		
		例如：
		
		<#assign user = "Big Joe's \"right hand\"">
		<script>
		  alert("Welcome ${user?js_string}!");
		</script>
		
		将会输出：
		
		<script>
		  alert("Welcome Big Joe\'s \"right hand\"!");
		</script>

### keep_after
		
		移除字符串中的一部分内容，该部分是给定子串第一次出现之前的部分。 比如：
		
		${"abcdefgh"?keep_after("de")}
		
		将会输出
		
		fgh
		
		如果参数字符串没有找到，它会返回空串。如果参数是长度为0的字符串， 它会返回源字符串，不会改变。
		
		该方法接受可选的 标志位参数，作为它的第二个参数：
		
		${"foo : bar"?keep_after(r"\s*:\s*", "r")}
		
		将会输出
		
		bar

### keep_after_last
		
		和 keep_after 相同， 但是它会保留参数最后一次出现后的部分，而不是第一次。比如：
		
		${"foo.bar.txt"?keep_after_last(".")}
		
		将会输出
		
		txt
		
		若使用 keep_after 则会得到 bar.txt。

### keep_before
		
		移除字符串的一部分，该部分是从给定子串开始的部分。 比如：
		
		${"abcdef"?keep_before("de")}
		
		将会输出
		
		abc
		
		如果参数字符串没有找到，它会返回源字符串，不会改变。 如果参数是长度为0的字符串，它会返回空串。
		
		该方法接受可选的 标志位参数，作为它的第二个参数：
		
		${"foo : bar"?keep_before(r"\s*:\s*", "r")}
		
		将会输出
		
		foo

### keep_before_last
		
		和 keep_before 相同， 但是保留参数最后一次出现之前的部分，而不是第一次出现之前。比如：
		
		${"foo.bar.txt"?keep_after_last(".")}
		
		将会输出
		
		foo.bar
		
		若使用 keep_before 则会得到 foo。

### keys

		一个包含哈希表中查找到的键的序列。 请注意，并不是所有的哈希表都支持这个 (询问程序员一个指定的哈希表是否允许这么操作)。
		
		<#assign h = {"name":"mouse", "price":50}>
		<#assign keys = h?keys>
		<#list keys as key>${key} = ${h[key]}; </#list>
		
		将会输出：
		
		name = mouse; price = 50;
		
		因为哈希表通常没有定义子变量的顺序，那么键名称的返回顺序就是任意的。 然而，一些哈希表维持一个有意义的顺序(询问程序员指定的哈希表是否是这样)。 
		比如，由上述 {...} 语法创建的哈希表保存了和你指定子变量相同的顺序。


### last

		序列的最后一个子变量。如果序列为空，那么模板处理将会中止。

### last_index_of

		返回最后一次(最右边)字符串中出现子串时的索引位置。 它返回子串第一个(最左边)字符所在位置的索引。
		例如： "abcabc"?last_index_of("ab")：将会返回3。 而且可以指定开始搜索的索引。
		例如， "abcabc"?last_index_of("ab", 2)，将会返回0。 要注意第二个参数暗示了子串开始的最大索引。
		对第二个参数的数值没有限制： 如果它是负数，那么效果和是零的一样，如果它比字符串的长度还大， 
		那么就和它是字符串长度那个数值是一个效果。小数会被切成整数。
		
		如果第一个参数作为子串没有在该字符串中出现时 (如果你使用了第二个参数，那么就从给定的序列开始)，那么就返回-1。

### left_pad
		
		如果它仅仅用1个参数，那么它将在字符串的开始插入空白， 直到整个串的长度达到参数指定的值。 
		如果字符串的长度达到指定数值或者比指定的长度还长， 那么就什么都不做了。比如这样：
		
		[${""?left_pad(5)}]
		[${"a"?left_pad(5)}]
		[${"ab"?left_pad(5)}]
		[${"abc"?left_pad(5)}]
		[${"abcd"?left_pad(5)}]
		[${"abcde"?left_pad(5)}]
		[${"abcdef"?left_pad(5)}]
		[${"abcdefg"?left_pad(5)}]
		[${"abcdefgh"?left_pad(5)}]
		
		将会输出：
		
		[     ]
		[    a]
		[   ab]
		[  abc]
		[ abcd]
		[abcde]
		[abcdef]
		[abcdefg]
		[abcdefgh]
		
		如果使用了两个参数，那么第一个参数表示的含义和你使用一个参数时的相同， 第二个参数指定用什么东西来代替空白字符。比如：
		
		[${""?left_pad(5, "-")}]
		[${"a"?left_pad(5, "-")}]
		[${"ab"?left_pad(5, "-")}]
		[${"abc"?left_pad(5, "-")}]
		[${"abcd"?left_pad(5, "-")}]
		[${"abcde"?left_pad(5, "-")}]
		
		将会输出：
		
		[-----]
		[----a]
		[---ab]
		[--abc]
		[-abcd]
		[abcde]
		
		第二个参数也可以是个长度比1大的字符串。 那么这个字符串会周期性的插入，比如：
		
		[${""?left_pad(8, ".oO")}]
		[${"a"?left_pad(8, ".oO")}]
		[${"ab"?left_pad(8, ".oO")}]
		[${"abc"?left_pad(8, ".oO")}]
		[${"abcd"?left_pad(8, ".oO")}]
		
		将会输出：
		
		[.oO.oO.o]
		[.oO.oO.a]
		[.oO.oOab]
		[.oO.oabc]
		[.oO.abcd]
		
		第二个参数必须是个字符串值，而且至少有一个字符。

### length

		字符串中字符的数量。

### lower_abc
		
		将 1， 2， 3，等...，转换为字符串 "a"， "b"， "c"，等... 当到达 "z"时，那么会继续转换成如 "aa"， "ab"，
		等... 这和电子表格应用程序(比如Excel或Calc) 的列标签有着相同的逻辑。数字的最小值是 1。 没有上限值。
		如果数字是 0 或更小或它不是整数， 那么模板处理将会中止并发生错误。
		
		例如：
		
		<#list 1..30 as n>${n?lower_abc} </#list>
		
		输出：
		
		a b c d e f g h i j k l m n o p q r s t u v w x y z aa ab ac ad 
		
		请参考： upper_abc

### lower_case

		字符串的小写形式。比如 "GrEeN MoUsE"?lower_case 将会是 "green mouse"。


### matches

		这是一个"超级用户"函数。如果不懂 正则表达式，就忽略它吧。
		
		该内建函数决定了字符串是否精确匹配模式。而且，它会返回匹配子串的列表。 返回值是多类型值：
		
		    布尔值：如果字符串整体匹配了模式，就是 true， 否则就是 false。比如："fooo"?matches('fo*') 就是 true，
		    但是 "fooo bar"?matches('fo*') 是 false。
		
		    序列：字符串匹配的子串的列表。很有可能是长度为0的序列。
		
		比如：
		
		<#if "fxo"?matches("f.?o")>Matches.<#else>Does not match.</#if>
		
		<#assign res = "foo bar fyo"?matches("f.?o")>
		<#if res>Matches.<#else>Does not match.</#if>
		Matching sub-strings:
		<#list res as m>
		- ${m}
		</#list>
		
		将会输出：
		
		Matches.
		
		Does not match.
		Matching sub-strings:
		- foo
		- fyo
		
		如果正则表达式包含分组(圆括号)，那么可以使用 groups 内建函数来访问它们：
		
		<#-- Entire input match -->
		<#assign res = "John Doe"?matches(r"(\w+) (\w+)")>
		<#if res> <#-- Must not try to access groups if there was no match! -->
		  First name: ${res?groups[1]}
		  Second name: ${res?groups[2]}
		</#if>
		
		<#-- Subtring matches -->
		<#assign res = "aa/rx; ab/r;"?matches("(.+?)/*(.+?);")>
		<#list res as m>
		  - "${m}" is "${m?groups[1]}" per "${m?groups[2]}"
		</#list>
		
		将会输出：
		
		  First name: John
		  Second name: Doe
		
		  - "aa/rx;" is "a" per "a/rx"
		  - " ab/r;" is " " per "ab/r"
		
		请注意，上面的 groups 对子串匹配和整个字符串匹配的结果都起作用。
		
		matches 接受可选的第二参数， 标志位。请注意， 它不支持标志 f，也会忽略 r 标志。

### namespace

		该内建函数返回和宏变量或函数变量关联的命名空间 (也就是命名空间的"入口"哈希表)。你只能和宏和函数一起来用它。

### new

		这是用来创建一个确定的 TemplateModel 实现变量的内建函数。
		
		在 ? 的左边你可以指定一个字符串， 是 TemplateModel 实现类的完全限定名。 结果是调用构造方法生成一个方法变量，然后将新变量返回。
		
		比如：
		
		<#-- Creates an user-defined directive be calling the parameterless constructor of the class -->
		<#assign word_wrapp = "com.acmee.freemarker.WordWrapperDirective"?new()>
		<#-- Creates an user-defined directive be calling the constructor with one numerical argument -->
		<#assign word_wrapp_narrow = "com.acmee.freemarker.WordWrapperDirective"?new(40)>
		
		更多关于构造方法参数被包装和如何选择重载的构造方法信息， 请阅读： 程序开发指南/其它/Bean的包装
		
		该内建函数可以是出于安全考虑的， 因为模板作者可以创建任意的Java对象，只要它们实现了 TemplateModel 接口，然后来使用这些对象。 
		而且模板作者可以触发没有实现 TemplateModel 接口的类的静态初始化块。
		你可以(从 2.3.17版开始)使用 Configuration.setNewBuiltinClassResolver(TemplateClassResolver) 
		或设置 new_builtin_class_resolver 来限制这个内建函数对类的访问。 参考Java API文档来获取详细信息。
		如果允许并不是很可靠的用户上传模板， 那么你一定要关注这个问题。

### node_namespace

		返回结点命名空间的字符串。FreeMarker 没有为结点命名空间定义准确的含义； 它依赖于变量是怎么建模的。
		也可能结点没有定义任何结点命名空间。 这种情形下，该函数应该返回未定义的变量 (也就是 node?node_namespace?? 的值是 false)，
		所以不能使用这个返回值。
		
		XML：这种情况下的XML，就是XML命名空间的URI (比如 "http://www.w3.org/1999/xhtml")。 
		如果一个元素或属性结点没有使用XML命名空间，那么这个函数就返回一个空字符串。 对于其他XML结点这个函数返回未定义的变量。

### node_name

		当被"访问"时，返回用来决定哪个自定义指令来调用控制这个结点的字符串。 可以参见 visit 和 recurse 指令。
		
		XML：如果结点是元素或属性，那么字符串就会是元素或属性的本地 (没有前缀)名字。否则，名称通常在结点类型之后以 @ 开始。 
		可以参见 该表格。 要注意这个结点名称与在DOM API中返回的结点名称不同； FreeMarker 结点名称的目标是给要处理结点的用户自定义指令命名。

### node_type

		描述结点类型的字符串。FreeMarker 没有对结点类型定义准确的含义； 它依赖于变量是怎么建模的。也可能结点并不支持结点类型。 
		在这种情形下，该函数就返回未定义值，所以就不能使用返回值。 (可以用 node?node_type?? 继续检查一个结点是否是支持类型属性。)
		
		XML：可能的值是： "attribute"， "text"， "comment"， "document_fragment"， "document"， "document_type"，
		 "element"， "entity"， "entity_reference"， "notation"， "pi"。 请注意，没有 "cdata" 类型，因为CDATA被认为是普通文本元素。

### number

		字符串转化为数字格式。这个数字必须是 "计算机语言" 格式。也就是说， 它必须是本地化独立的形式，小数的分隔符就是一个点，没有分组。
		
		该内建函数识别FreeMarker模板语言使用的数字格式。此外， 它也识别科学记数法(比如 "1.23E6"，"1.5e-8") 
		从 FreeMarker 2.3.21 版本开始，它也识别所有XML Schema数字格式，比如 NaN，INF，-INF， 还有Java本地格式Infinity 和 -Infinity。
		
		如果字符串不是适当的格式，当尝试访问该内建函数时就会发生错误， 并中止模板执行。
		
		实际上，字符串是由当前 arithmetic_engine 的 toNumber 方法解析的，这是可以配置的设置项。不过该方法应该和上面描述的行为相似。 

### number_to_date
### number_to_time
### number_to_datetime

		它们被用来转换数字(通常是Java的 long类型)到日期， 时间或时间日期类型。这就使得它们和Java中的 new java.util.Date(long) 
		是一致的。那也就是说， 现在数字可以被解释成毫秒数进行参数传递。数字可以是任意内容和任意类型， 只要它的值可以认为是 long 就行。
		如果数字不是完整的， 那么它就会根据"五入"原则进行进位。这个转换不是自动进行的。
		
		比如：
		
		${1305575275540?number_to_datetime}
		${1305575275540?number_to_date}
		${1305575275540?number_to_time}
		
		将会输出这样的内容(基于当前的本地化设置和时区)：
		
		May 16, 2011 3:47:55 PM
		May 16, 2011
		3:47:55 PM

### parent

		在结点树中，返回该结点的直接父结点。根结点没有父结点， 所以对于根结点，表达式 node?parent?? 的值就是 false。
		
		XML：注意通过这个函数返回的值也是一个序列 (当编写 someNode[".."] 时，和XPath表达式 .. 的结果是一样的)。
		也要注意属性结点， 它返回属性所属的元素结点，尽管属性结点不被算作是元素的子结点。

### replace

		在源字符串中，用另外一个字符串来替换原字符串中出现它的部分。 它不处理词的边界。比如：
		
		${"this is a car acarus"?replace("car", "bulldozer")}
		
		将会输出：
		
		this is a bulldozer abulldozerus
		
		替换是从左向右执行的。这就意味着：
		
		${"aaaaa"?replace("aaa", "X")}
		
		将会输出：
		
		Xaa
		
		如果第一个参数是空字符串，那么所有的空字符串将会被替换， 比如 "foo"?replace("","|")，就会得到 "|f|o|o|"。
		
		replace 接受可选的 标志位参数，作为它的第三参数。

### remove_beginning
		
		从字符串的开头移除参数中的子串，如果它不以参数中的子串开头， 那么就或者返回原字符串。比如：
		
		${"abcdef"?remove_beginning("abc")}
		${"foobar"?remove_beginning("abc")}
		
		将会输出：
		
		def
		foobar

### remove_ending
		
		从字符串的结尾移除参数中的子串，如果它不以参数中的子串结尾， 那么就或者返回原字符串。比如：
		
		${"abcdef"?remove_ending("def")}
		${"foobar"?remove_ending("def")}
		
		将会输出：
		
		abc
		foobar

### reverse

		序列的反序形式。

### right_pad
		
		它和 left_pad 相同， 但是它从末尾开始插入字符而不是从开头。
		
		比如：
		
		[${""?right_pad(5)}]
		[${"a"?right_pad(5)}]
		[${"ab"?right_pad(5)}]
		[${"abc"?right_pad(5)}]
		[${"abcd"?right_pad(5)}]
		[${"abcde"?right_pad(5)}]
		[${"abcdef"?right_pad(5)}]
		[${"abcdefg"?right_pad(5)}]
		[${"abcdefgh"?right_pad(5)}]
		
		[${""?right_pad(8, ".oO")}]
		[${"a"?right_pad(8, ".oO")}]
		[${"ab"?right_pad(8, ".oO")}]
		[${"abc"?right_pad(8, ".oO")}]
		[${"abcd"?right_pad(8, ".oO")}]
		
		将会输出：
		
		[     ]
		[a    ]
		[ab   ]
		[abc  ]
		[abcd ]
		[abcde]
		[abcdef]
		[abcdefg]
		[abcdefgh]
		
		[.oO.oO.o]
		[aoO.oO.o]
		[abO.oO.o]
		[abc.oO.o]
		[abcdoO.o]

### round
### floor
### ceiling
		
		使用确定的舍入法则，转换一个数字到整数：
		
		    round：返回最近的整数。 如果数字以.5结尾，那么它将进位(也就是向正无穷方向进位)
		
		    floor：返回数字的舍掉小数后的整数 (也就是向负无穷舍弃)
		
		    ceiling：返回数字小数进位后的整数 (也就是向正无穷进位)
		
		例如：
		
		<#assign testlist=[
		  0, 1, -1, 0.5, 1.5, -0.5,
		  -1.5, 0.25, -0.25, 1.75, -1.75]>
		<#list testlist as result>
		    ${result} ?floor=${result?floor} ?ceiling=${result?ceiling} ?round=${result?round}
		</#list>
		
		输出：
		
		    0 ?floor=0 ?ceiling=0 ?round=0            
		    1 ?floor=1 ?ceiling=1 ?round=1        
		    -1 ?floor=-1 ?ceiling=-1 ?round=-1      
		    0.5 ?floor=0 ?ceiling=1 ?round=1      
		    1.5 ?floor=1 ?ceiling=2 ?round=2      
		    -0.5 ?floor=-1 ?ceiling=0 ?round=0     
		    -1.5 ?floor=-2 ?ceiling=-1 ?round=-1    
		    0.25 ?floor=0 ?ceiling=1 ?round=0     
		    -0.25 ?floor=-1 ?ceiling=0 ?round=0    
		    1.75 ?floor=1 ?ceiling=2 ?round=2     
		    -1.75 ?floor=-2 ?ceiling=-1 ?round=-2
		
		这些内建函数在分页处理时也许有用。如果你仅仅想 展示 数字的舍入形式，那么应该使用 string 内建函数 或者 number_format 设置。

### root

		该结点所属结点树的根结点。
		
		XML：根据W3C，XML文档的根结点不是最顶层的元素结点， 而是文档本身，是最高元素的父结点。例如， 如果想得到被称为是 foo 的XML 
		(所谓的"文档元素"，不要和"文档"搞混了)的最高 元素， 那么不得不编写 someNode?root.foo。如果仅仅写了 someNode?root，
		那么得到的是文档本身，而不是文档元素。

### rtf

		字符串作为富文本(RTF 文本)，也就是说，下列字符串：
		
		    \ 替换为 \\
		
		    { 替换为 \{
		
		    } 替换为 \}

### size

		序列中子变量的数量(作为数字值)。假设序列中至少有一个子变量， 那么序列 s 中最大的索引是 s?size - 1 (因为第一个子变量的序列是0)。

### sort

		以升序方式返回序列。(要使用降序排列时，使用它之后使用 reverse 内建函数。) 这仅在子变量都是字符串时有效，或者子变量都是数字，
		或者子变量都是日期值 (日期，时间，或日期+时间)，或者所有子变量都是布尔值时(从2.3.17版本开始)。 如果子变量是字符串，
		它使用本地化(语言)的具体单词排序(通常是大小写不敏感的)。比如：
		
		<#assign ls = ["whale", "Barbara", "zeppelin", "aardvark", "beetroot"]?sort>
		<#list ls as i>${i} </#list>
		
		将会输出(至少是US区域设置)：
		
		aardvark Barbara beetroot whale zeppelin

### seq_contains

		Note:
		
		seq_ 前缀在该内建函数名字中是需要的， 用来和 contains 内建函数 区分开。contains 用来在字符串中查找子串 (因为变量可以同时当作字符串和序列)。
		
		辨别序列中是否包含指定值。它包含一个参数，就是来查找的值。比如：
		
		<#assign x = ["red", 16, "blue", "cyan"]>
		"blue": ${x?seq_contains("blue")?string("yes", "no")}
		"yellow": ${x?seq_contains("yellow")?string("yes", "no")}
		16: ${x?seq_contains(16)?string("yes", "no")}
		"16": ${x?seq_contains("16")?string("yes", "no")}
		
		将会输出：
		
		"blue": yes
		"yellow": no
		16: yes
		"16": no
		
		为了查找值，该内建函数使用了 FreeMarker 的比较规则 (就像使用 == 操作符)，除了比较两个不同类型的值，
		或 FreeMarker 不支持的类型来比较， 其他都不会引起错误，只是会评估两个值不相等。因此，你可以使用它来查找标量值 
		(也就是字符串，数字，布尔值，或日期/时间类型)。 对于其他类型结果通常都是 false。
		
		对于容错性，该内建函数还对 collection 起作用。

### seq_index_of

		Note:
		
		seq_ 前缀在该内建函数名字中是需要的， 用来和 index_of 内建函数 区分开。index_of 用来在字符串中查找子串 (因为变量可以同时当作字符串和序列)。
		
		返回序列中第一次出现该值时的索引位置， 如果序列不包含指定的值时返回 -1。 要查找的值作为第一个参数。比如这个模板：
		
		<#assign colors = ["red", "green", "blue"]>
		${colors?seq_index_of("blue")}
		${colors?seq_index_of("red")}
		${colors?seq_index_of("purple")}
		
		将会输出：
		
		2
		0
		-1
		
		为了查找值，该内建函数使用了FreeMarker 的比较规则 (就像使用了 == 操作符)，除了比较两个不同类型的值，
		或 FreeMarker 不支持的类型来比较， 其他都不会引起错误，只是会评估两个值不相等。因此，你可以使用它来查找标量值 
		(也就是字符串，数字，布尔值，或日期/时间类型)。 对于其他类型结果通常是 -1。
		
		搜索开始的索引值可以由第二个可选参数来确定。 如果在同一个序列中相同的项可以多次出现时，这是很有用的。 
		第二个参数的数值没有什么限制：如果它是负数，那么就和它是零的效果一样， 而如果它是比序列长度还大的数，
		那么就和它是序列长度值的效果一样。 小数值会被切成整数。比如：
		
		<#assign names = ["Joe", "Fred", "Joe", "Susan"]>
		No 2nd param: ${names?seq_index_of("Joe")}
		-2: ${names?seq_index_of("Joe", -2)}
		-1: ${names?seq_index_of("Joe", -1)}
		 0: ${names?seq_index_of("Joe", 0)}
		 1: ${names?seq_index_of("Joe", 1)}
		 2: ${names?seq_index_of("Joe", 2)}
		 3: ${names?seq_index_of("Joe", 3)}
		 4: ${names?seq_index_of("Joe", 4)}
		
		将会输出：
		
		No 2nd param: 0
		-2: 0
		-1: 0
		 0: 0
		 1: 2
		 2: 2
		 3: -1
		 4: -1

### seq_last_index_of

		Note:
		
		seq_ 前缀在该内建函数名字中是需要的， 用来和 last_index_of 内建函数 区分开。last_index_of 用来在字符串中查找子串 (因为变量可以同时当作字符串和序列)。
		
		返回序列中最后一次出现值的索引位置， 如果序列不包含指定的值时返回 -1。也就是说，和 seq_index_of 相同， 
		只是在序列中从最后一项开始向前搜索。 它也支持可选的第二个参数来确定从哪里开始搜索的索引位置。比如：
		
		<#assign names = ["Joe", "Fred", "Joe", "Susan"]>
		No 2nd param: ${names?seq_last_index_of("Joe")}
		-2: ${names?seq_last_index_of("Joe", -2)}
		-1: ${names?seq_last_index_of("Joe", -1)}
		 0: ${names?seq_last_index_of("Joe", 0)}
		 1: ${names?seq_last_index_of("Joe", 1)}
		 2: ${names?seq_last_index_of("Joe", 2)}
		 3: ${names?seq_last_index_of("Joe", 3)}
		 4: ${names?seq_last_index_of("Joe", 4)}
		
		将会输出：
		
		No 2nd param: 2
		-2: -1
		-1: -1
		 0: 0
		 1: 0
		 2: 2
		 3: 2
		 4: 2

### sort_by

		返回由给定的哈希表子变量来升序排序的哈希表序列。 (要降序排列使用该内建函数后还要使用 reverse 内建函数。) 这个规则
		和 sort 内建函数 是一样的， 除了序列中的子变量必须是哈希表类型，而且你不得不给哈希变量的命名， 那会用来决定排序顺序。比如：
		
		<#assign ls = [
		  {"name":"whale", "weight":2000},
		  {"name":"Barbara", "weight":53},
		  {"name":"zeppelin", "weight":-200},
		  {"name":"aardvark", "weight":30},
		  {"name":"beetroot", "weight":0.3}
		]>
		Order by name:
		<#list ls?sort_by("name") as i>
		- ${i.name}: ${i.weight}
		</#list>
		
		Order by weight:
		<#list ls?sort_by("weight") as i>
		- ${i.name}: ${i.weight}
		</#list>
		
		将会输出(至少是US区域设置)：
		
		Order by name:
		- aardvark: 30
		- Barbara: 53
		- beetroot: 0.3
		- whale: 2000
		- zeppelin: -200
		
		Order by weight:
		- zeppelin: -200
		- beetroot: 0.3
		- aardvark: 30
		- Barbara: 53
		- whale: 2000
		
		如果你用来排序的子变量的层次很深 (也就是说，它是子变量的子变量的子变量，以此类推)， 那么你可以使用序列来作为参数，
		它指定了子变量的名字， 来向下引导所需的子变量。比如：
		
		<#assign members = [
		    {"name": {"first": "Joe", "last": "Smith"}, "age": 40},
		    {"name": {"first": "Fred", "last": "Crooger"}, "age": 35},
		    {"name": {"first": "Amanda", "last": "Fox"}, "age": 25}]>
		Sorted by name.last: 
		<#list members?sort_by(['name', 'last']) as m>
		- ${m.name.last}, ${m.name.first}: ${m.age} years old
		</#list>
		
		将会输出(至少是US区域设置)：
		
		Sorted by name.last: 
		- Crooger, Fred: 35 years old
		- Fox, Amanda: 25 years old
		- Smith, Joe: 40 years old

### split

		它被用来根据另外一个字符串的出现将原字符串分割成字符串序列。 比如：
		
		<#list "someMOOtestMOOtext"?split("MOO") as x>
		- ${x}
		</#list>
		
		将会输出：
		
		- some
		- test
		- text
		
		请注意，假设所有的分隔符都在新项之前出现 (除了使用 "r" 标志 - 后面详细介绍) 因此：
		
		<#list "some,,test,text,"?split(",") as x>
		- "${x}"
		</#list>
		
		将会输出：
		
		- "some"
		- ""
		- "test"
		- "text"
		- ""
		
		split 接受可选的 标志位参数， 作为它的第二个参数。由于历史使用 r (正则表达式)标志的差错；它会从结果列表中移除空元素， 
		所以在最后示例中使用 ?split(",", "r")， "" 会从输出中消失。
		Note:
		
		要检查一个字符串是否以...结尾或者要附加它， 使用 ensure_ends_with 内建函数。

### starts_with

		如果字符串以指定的子字符串开头，那么返回true。 比如 "redirect"?starts_with("red") 返回布尔值 true，
		而且 "red"?starts_with("red") 也返回 true。
		Note:
		
		要检查一个字符串是否以...开头或者要在前面附加它， 使用 ensure_starts_with 内建函数。

### string

		string (当用作是数字类型时)
		
		将一个数字转换成字符串。它使用程序员通过 number_format 和 locale 设置的默认格式。
		也可以明确地用这个内建函数再指定一个数字格式， 这在后面会展示。
		
		有四种预定义的数字格式：computer， currency，number 和 percent。这些格式的明确含义是本地化(国家)指定的， 
		受Java平台安装环境所控制，而不是FreeMarker，除了 computer，用作和 c 内建函数是相同的格式。可以这样来使用预定义的格式：
		
		<#assign x=42>
		${x}
		${x?string}  <#-- the same as ${x} -->
		${x?string.number}
		${x?string.currency}
		${x?string.percent}
		${x?string.computer}
		
		如果你本地是US English，将会输出：
		
		42
		42
		42
		$42.00
		4,200%
		42
		
		前三个表达式的输出是相同的，因为前两个表达式是默认格式， 这里是"数字"。可以使用一个设置来改变默认设置：
		
		<#setting number_format="currency">
		<#assign x=42>
		${x}
		${x?string}  <#-- the same as ${x} -->
		${x?string.number}
		${x?string.currency}
		${x?string.percent}
		
		将会输出：
		
		$42.00
		$42.00
		42
		$42.00
		4,200%
		
		因为默认的数字格式被设置成了"货币"。
		
		除了这三种预定义格式，还可以使用 Java 数字格式语法 中的任意数字格式：
		
		<#assign x = 1.234>
		${x?string["0"]}
		${x?string["0.#"]}
		${x?string["0.##"]}
		${x?string["0.###"]}
		${x?string["0.####"]}
		
		${1?string["000.00"]}
		${12.1?string["000.00"]}
		${123.456?string["000.00"]}
		
		${1.2?string["0"]}
		${1.8?string["0"]}
		${1.5?string["0"]} <-- 1.5, rounded towards even neighbor
		${2.5?string["0"]} <-- 2.5, rounded towards even neighbor
		
		${12345?string["0.##E0"]}
		
		将会输出：
		
		1
		1.2
		1.23
		1.234
		1.234
		
		001.00
		012.10
		123.46
		
		1
		2
		2 <-- 1.5, rounded towards even neighbor
		2 <-- 2.5, rounded towards even neighbor
		
		1.23E4
		
		请注意，在 FreeMarker 中，foo.bar 和 foo["bar"] 是相同的，也可以将 x?string.currency 写为 x?string["currency"]，
		当然实际中不这么用。 但是在上述示例中，我们不得不使用方括号语法，因为在语法上， 使用的字符(数字，点，#)不允许在点操作符之后。
		
		由于历史原因，也可以编写如下代码 x?string("0.#")，它和 x?string["0.#"] 完全相同。
		
		在金融和统计学实践中，四舍五入都是根据所谓的一半原则， 这就意味着对最近的"邻居"进行四舍五入，除非离两个邻居距离相等， 
		这种情况下，它四舍五入到偶数的邻居。如果你注意看1.5和2.5的四舍五入的话， 这在上面的示例中是可以看到的，两个都被四舍五入到2，
		因为2是偶数，但1和3是奇数。
		
		正如之前展示的预定义格式，数字的默认格式可以在模板中设置：
		
		<#setting number_format="0.##">
		${1.234}
		
		将会输出：
		
		1.23
		
		请注意，数字格式是本地化敏感的，本地化设置在格式化中起作用：
		
		<#setting number_format=",##0.00">
		<#setting locale="en_US">
		US people write:     ${12345678}
		<#setting locale="hu">
		German people write: ${12345678}
		
		将会输出：
		
		US people write:     12,345,678.00
		German people write: 12.345.678,00
		
		
		string (当用于日期/时间/日期-时间值时)
		
		这个内建函数以指定的格式转换日期类型到字符串类型。
		Note:
		
		应该很少使用这个内建函数，因为日期/时间/日期-时间值的默认格式可以全局指定 FreeMarker 的 date_format，time_format 
		和 datetime_format 设置。 该内建函数只在期望格式和常用格式不同的地方使用。在其它地方， 默认格式应该由程序员在模板之外合理地设置。
		
		期望的格式可以由 ?string.format 或 ?string["format"] (或历史上等同的， ?string("format")) 来指定。这些都是等同的，
		除了使用引号格式的，它可以在 format 中包含任意字符， 比如空格。format 的语法和配置设置项 date_format，time_format 
		和 datetime_format 是一样的； 请参考这些可能值的文档。
		
		例如：如果输出的本地化设置是美国英语，时区是美国太平洋时区，并且 openingTime 是 java.sql.Time， nextDiscountDay 
		是 java.sql.Date 而 lastUpdated 是 java.sql.Timestamp 或 java.util.Date 那么：
		
		${openingTime?string.short}
		${openingTime?string.medium}
		${openingTime?string.long}
		${openingTime?string.full}
		${openingTime?string.xs}
		${openingTime?string.iso}
		
		${nextDiscountDay?string.short}
		${nextDiscountDay?string.medium}
		${nextDiscountDay?string.long}
		${nextDiscountDay?string.full}
		${nextDiscountDay?string.xs}
		${nextDiscountDay?string.iso}
		
		${lastUpdated?string.short}
		${lastUpdated?string.medium}
		${lastUpdated?string.long}
		${lastUpdated?string.full}
		${lastUpdated?string.medium_short} <#-- medium date, short time -->
		${lastUpdated?string.xs}
		${lastUpdated?string.iso}
		
		<#-- SimpleDateFormat patterns: -->
		${lastUpdated?string["dd.MM.yyyy, HH:mm"]}
		${lastUpdated?string["EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'"]}
		${lastUpdated?string["EEE, MMM d, ''yy"]}
		${lastUpdated?string.yyyy} <#-- Same as ${lastUpdated?string["yyyy"]} -->
		
		<#-- Advanced ISO 8601-related formats: -->
		${lastUpdated?string.iso_m_u}
		${lastUpdated?string.xs_ms_nz}
		
		将会输出：
		
		01:45 PM
		01:45:09 PM
		01:45:09 PM PST
		01:45:09 PM PST
		13:45:09-08:00
		13:45:09-08:00
		
		2/20/07
		Apr 20, 2007
		April 20, 2007
		Friday, April 20, 2007
		2007-02-20-08:00
		2007-02-20
		
		2/20/07 01:45 PM
		Feb 20, 2007 01:45:09 PM
		February 20, 2007 01:45:09 PM PST
		Friday, February 20, 2007 01:45:09 PM PST
		Feb 8, 2003 9:24 PM
		2007-02-20T13:45:09-08:00
		2007-02-20T13:45:09-08:00
		
		08.04.2003 21:24
		Tuesday, April 08, 2003, 09:24 PM (PDT)
		Tue, Apr 8, '03
		2003
		
		2007-02-20T21:45Z
		2007-02-20T13:45:09.000
		
		Warning!
		
		不幸的是，由于Java平台的限制，在数据模型中可以有日期变量， 那里 FreeMarker 不能决定变量是否是日期(年，月，日)，
		还是时间 (时，分，秒，毫秒)，或者是日期-时间值。这种情况下，当你编写如 ${lastUpdated?string.short} 
		或 ${lastUpdated?string.xs} 时，FreeMarker 不知道如何来显示日期，也就是说，格式不指定精确的字段去显示， 
		或者如果只是简单使用 ${lastUpdated}， 那么模板会中止执行并抛出错误。要阻止这些发生，可以使用 ?date， ?time 
		和 ?datetime 内建函数 来帮助 FreeMarker。例如： ${lastUpdated?datetime?string.short}。 
		要询问程序员数据模型中确定的变量是否有这个问题， 或通常使用内建函数 ?date，?time 和 ?datetime 来安全处理。
		Note:
		
		不需和格式模式使用 ?date, ?time 或 ?datetime， 比如 "yyyy.MM.dd HH:mm"，因为使用这些模式， 
		就告诉 FreeMarker 来显示哪部分日期。那么，FreeMarker 将盲目地相信你， 如果你显示的部分不存在于变量中，
		则可以显示"干扰"。例如， ${openingTime?string["yyyy-MM-dd hh:mm:ss a"]}, 而 openingTime 中只存储了时间，
		将会显示 1970-01-01 09:24:44 PM。
		
		要避免误解，需要的格式不是字符串，它可以是变量或任意表达式，比如 "..."?string[myFormat]。
		
		请参考： 日期的插入

### substring

		该内建函数被废弃，由 字符串切分 替代，比如 str[from..<toExclusive]， str[from..], 和 str[from..*maxLength]。
		
		如果处理XML那么有一点警示：因为分割表达式作用于序列和字符串， 而且XML结点通常既是序列又是字符串，
		那么相等的表达式是 someXmlNode?string[from..<toExclusive] 和 exp?string[from..]， 
		因为没有 ?string 它会分割结点序列而不是结点的字符串值。
		Note:
		
		一些典型的字符串分割的用例已经由一些内建函数方便地实现： remove_beginning， remove_ending， keep_before， keep_after， keep_before_last， keep_after_last

### switch
		
		这是 switch-case-default 指令 的基本内联(表达式)版本。它的通用版本就像 
		matchedValue?switch(case1, result1, case2, result2, ... caseN, resultN, defaultResult)，
		这里的 defaultResult 可以被忽略。比如：
		
		<#list ['r', 'w', 'x', 's'] as flag>
		  ${flag?switch('r', 'readable', 'w' 'writable', 'x', 'executable', 'unknown flag: ' + flag)}
		</#list>
		
		  readable
		  writable
		  executable
		  unknown flag: s
		
		也就是说， switch 会找到第一个 case 和参数 (从左到右)值 matchedValue 相等， 之后返回直接在 case 参数后的 result 参数的值，
		如果它没有找到一个相等的 case，那么就返回 defaultResult 的值，如果没有 defaultResult 参数 (换言之，参数的个数是基数)，
		那么就发生错误中止模板处理。
		
### then
		
		使用于 booleanExp?then(whenTrue, whenFalse)，就像是类C语言中的三元运算符 
		(也就是说，booleanExp ? whenTrue : whenFalse)。如果 booleanExp 评估为布尔值 true， 那么就评估并返回第一个参数，
		而若 booleanExp 评估为布尔值 false，那么就评估并返回它的第二个参数。当然，三个表达式可以是任意复杂的。 
		参数表达式可以是任意类型，也可以是不同类型。
		
		该内建函数的一个重要特殊属性是只有一个参数表达式会被评估。 这和普通的方法调用不同，它们会评估所有的参数表达式，
		而不管方法是否需要它们。 这也就以为着不需要的参数也可以用于不存在的变量而不会引发错误。 (当然它不能是非法的语法。)
		
		例如：
		
		<#assign foo = true>
		${foo?then('Y', 'N')}
		
		<#assign foo = false>
		${foo?then('Y', 'N')}
		
		<#assign x = 10>
		<#assign y = 20>
		<#-- Prints 100 plus the maximum of x and y: -->
		${100 + (x > y)?then(x, y)}
		
		Y
		
		N
		
		120

### trim

		去掉字符串首尾的空格。例如：
		
		(${"  green mouse  "?trim})
		
		将会输出：
		
		(green mouse)

### uncap_first

		和 cap_first 相反。 字符串中所有单词的首字母小写。

### upper_abc

		和 lower_abc 相同， 但是它转换成大写字母，比如 "A"， "B"， "C"，… ， "AA"， "AB"， 等...

### upper_case

		字符串的大写形式。比如 "GrEeN MoUsE" 将会是 "GREEN MOUSE".

### url
		
		在URL之后的字符串进行转义。这意味着， 所有非US-ASCII的字符和保留的URL字符将会被 %XX 形式转义。例如：
		
		<#assign x = 'a/b c'>
		${x?url}
		
		将会输出(假设用来转义的字符集是US-ASCII兼容的字符集)：
		
		a%2Fb%20c
		
		请注意，它会转义 所有 保留的URL字符 (/， =， &，等...)， 所以编码可以被用来对查询参数的值进行，比如：
		
		<a href="foo.cgi?x=${x?url}&y=${y?url}">Click here...</a>
		
		Note:
		
		上面的没有HTML编码(?html)是需要的， 因为URL转义所有保留的HTML编码。但是要小心：通常引用的属性值， 用普通引号(")包括，
		而不是单引号 (')，因为单引号是不被URL转义的。
		
		为了进行URL转义，必须要选择 字符集，它被用来计算被转义的部分 (%XX)。 如果你是HTML页面设计者，而且你不懂这个，
		不要担心： 程序员应该配置 FreeMarker，则它默认使用恰当的字符集 (程序员应该多看看下面的内容...)。 
		如果你是一个比较热衷于技术的人，那么你也许想知道被 url_escaping_charset 设置的指定字符集， 
		它可以在模板的执行时间设置(或者，更好的是，由程序员之前设置好)。例如：
		
		<#--
		  This will use the charset specified by the programmers
		  before the template execution has started.
		-->
		<a href="foo.cgi?x=${x?url}">foo</a>
		
		<#-- Use UTF-8 charset for URL escaping from now: -->
		<#setting url_escaping_charset="UTF-8">
		
		<#-- This will surely use UTF-8 charset -->
		<a href="bar.cgi?x=${x?url}">bar</a>
		
		此外，你可以明确地指定一个为单独URL转义的字符集，作为内建函数的参数：
		
		<a href="foo.cgi?x=${x?url('ISO-8895-2')}">foo</a>

### values

		一个包含哈希表中子变量的序列。 注意并不是所有的哈希表都支持这个 (询问程序员一个指定的哈希表是否允许这么操作)。
		
		至于返回的值的顺序，和内建函数 keys 的应用是一样的；看看上面的叙述就行了。

### word_list

		包含字符串中所有单词的序列，顺序为出现在字符串中的顺序。 单词是不间断的字符序列，包含了任意字符，但是没有 空白。例如：
		
		<#assign words = "   a bcd, .   1-2-3"?word_list>
		<#list words as word>[${word}]</#list>
		
		将会输出：
		
		[a][bcd,][.][1-2-3]

### xhtml

		字符串作为XHTML格式文本，下面这些：
		
		    < 替换为 &lt;
		    > 替换为 &gt;
		    & 替换为 &amp;
		    " 替换为 &quot;
		    ' 替换为 &#39;
		
		该内建函数和 xml 内建函数的唯一不同是 xhtml内建函数转义 ' 为 &#39;，而不是 &apos;， 因为一些老版本的浏览器不能正确解释 &apos;。

### xml

		字符串作为XML格式文本，下面这些：
		
		    < 替换为 &lt;
		    > 替换为 &gt;
		    & 替换为 &amp;
		    " 替换为 &quot;
		    ' 替换为 &apos;



