///*******************************************************************************
// * Copyright (c) Dec 4, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
// * All rights reserved.
// *
// * Contributors:
// *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
// ******************************************************************************/
//package com.foreveross.common.restfull;
//
//import static org.apache.juneau.dto.html5.HtmlBuilder.*;
//
//import java.beans.BeanInfo;
//import java.beans.IntrospectionException;
//import java.beans.Introspector;
//import java.beans.PropertyDescriptor;
//import java.io.File;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.juneau.dto.html5.HtmlBuilder;
//import org.apache.juneau.dto.html5.HtmlElement;
//import org.apache.juneau.dto.html5.Input;
//import org.apache.juneau.html.HtmlSerializer;
//import org.iff.infra.util.Exceptions;
//import org.iff.infra.util.FCS;
//import org.iff.infra.util.StringHelper;
//import org.iff.infra.util.mybatis.plugin.Page;
//
//import com.foreveross.qdp.infra.vo.system.auth.AuthAccountVO;
//
///**
// * 没有使用
// * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
// * @since Dec 4, 2017
// */
//@Deprecated
//public class JuneauUtil {
//
//	private static String css = ""
//			+ "body{background-color:#3b4b54;margin:0;font-family:HelveticaNeue-Light,\"Helvetica Neue Light\","
//			+ "\"Helvetica Neue\",Helvetica,Arial,\"Lucida Grande\",sans-serif;color:#b3b3b3;height:100%}body{f"
//			+ "ont-size:14px}body textarea,body pre{-moz-tab-size:3;-o-tab-size:3;-webkit-tab-size:3;tab-size:3"
//			+ "}header{background-color:#26343f}header *{font-size:14px;color:#b3b3b3;margin:0;text-decoration:"
//			+ "none;font-weight:normal}header h1{padding:10px 20px;font-size:16px;border-bottom:2px solid #3453"
//			+ "4b;color:white}header h2{padding:10px 20px;font-size:14px;border-bottom:2px solid #34534b}nav{ma"
//			+ "rgin:10px 20px 10px 20px;color:#94a3ab}nav>ol{list-style-type:none;margin:0 10px;padding:0}nav>o"
//			+ "l>li{display:inline}nav li:not(:first-child):before{content:\" - \"}nav a{font-size:10pt;color:#"
//			+ "94a3ab;text-decoration:none;margin:0 15px;text-transform:uppercase;cursor:pointer}nav a:active,n"
//			+ "av a:hover{color:white;text-decoration:underline}section{display:table;width:100%;margin-bottom:"
//			+ "50px}article{display:table-cell}article *{font-size:9pt;color:#26343f}article div.data{padding:1"
//			+ "0px;background-color:white;border-radius:4px;margin:20px;display:inline-block;box-shadow:2px 3px"
//			+ " 3px 0 rgba(0,0,0,0.5);font-family:sans-serif;color:#26343f}article table{border:0}article th{bo"
//			+ "rder-top:1px solid #d9dcde;padding:4px 8px;font-weight:bold;text-align:center;background-color:#"
//			+ "f4f6f9}article td{vertical-align:top;border-bottom:1px solid #d9dcde;border-right:1px solid #d9d"
//			+ "cde;padding:2px 5px}article ul{margin:0;padding-left:20px}article a{color:#116998;text-decoratio"
//			+ "n:none}article a:hover{text-decoration:underline}article iframe{background-color:#f6f7f9;border:"
//			+ "1px solid gray;padding:0;overflow:hidden;width:100%;min-height:400px}aside{display:table-cell;ve"
//			+ "rtical-align:top;padding:20px 20px}footer{padding:10px;width:100%;bottom:0;position:fixed;backgr"
//			+ "ound-color:#26343f;text-align:center}.popup-content{display:none;position:absolute;background-color:#f4f6f9;white-"
//			+ "space:nowrap;padding:5px;box-shadow:3px 3px 10px rgba(0,0,0,0.5);z-index:1;margin-top:10px;borde"
//			+ "r-radius:4px}.popup-content *{color:black;font-size:11px}.popup-content a:hover{color:#94a3ab}.p"
//			+ "opup-show{display:block}.tooltip{position:relative;display:inline-block}.tooltip .tooltiptext{vi"
//			+ "sibility:hidden;background-color:#fef9e7;color:black;padding:5px;border-radius:6px;position:abso"
//			+ "lute;z-index:1;top:0;left:0;margin-left:30px;box-shadow:2px 3px 3px 0 rgba(0,0,0,0.5);opacity:0;"
//			+ "transition:opacity .5s;font-weight:normal}.tooltip:hover .tooltiptext{visibility:visible;opacity"
//			+ ":1}.tooltiptext{white-space:nowrap;float:left;border:1px solid black}.table{display:table}.row{d"
//			+ "isplay:table-row}.cell{display:table-cell}.monospace{font-family:monospace}.menu-item{position:r"
//			+ "elative;display:inline-block}.menu-item{position:relative;display:inline-block}.menu-item{positi"
//			+ "on:relative;display:inline-block}.menu-item{position:relative;display:inline-block}.menu-item{po"
//			+ "sition:relative;display:inline-block}.menu-item{position:relative;display:inline-block}";
//
//	private static String js = ""
//			+ "var popupItem;function closePopup(){if(popupItem!=null){popupItem.nextElementSibling.classList.re"
//			+ "move('popup-show')}}function menuClick(item){closePopup();item.nextElementSibling.classList.add('"
//			+ "popup-show');popupItem=item}window.onclick=function(event){if(popupItem!=null&&popupItem!=event.t"
//			+ "arget&&!popupItem.nextElementSibling.contains(event.target)){closePopup()}};var popupItem;functio"
//			+ "n closePopup(){if(popupItem!=null){popupItem.nextElementSibling.classList.remove('popup-show')}}f"
//			+ "unction menuClick(item){closePopup();item.nextElementSibling.classList.add('popup-show');popupIte"
//			+ "m=item}window.onclick=function(event){if(popupItem!=null&&popupItem!=event.target&&!popupItem.nex"
//			+ "tElementSibling.contains(event.target)){closePopup()}};var popupItem;function closePopup(){if(pop"
//			+ "upItem!=null){popupItem.nextElementSibling.classList.remove('popup-show')}}function menuClick(ite"
//			+ "m){closePopup();item.nextElementSibling.classList.add('popup-show');popupItem=item}window.onclick"
//			+ "=function(event){if(popupItem!=null&&popupItem!=event.target&&!popupItem.nextElementSibling.conta"
//			+ "ins(event.target)){closePopup()}};var popupItem;function closePopup(){if(popupItem!=null){popupIt"
//			+ "em.nextElementSibling.classList.remove('popup-show')}}function menuClick(item){closePopup();item."
//			+ "nextElementSibling.classList.add('popup-show');popupItem=item}window.onclick=function(event){if(p"
//			+ "opupItem!=null&&popupItem!=event.target&&!popupItem.nextElementSibling.contains(event.target)){cl"
//			+ "osePopup()}};var popupItem;function closePopup(){if(popupItem!=null){popupItem.nextElementSibling"
//			+ ".classList.remove('popup-show')}}function menuClick(item){closePopup();item.nextElementSibling.cl"
//			+ "assList.add('popup-show');popupItem=item}window.onclick=function(event){if(popupItem!=null&&popup"
//			+ "Item!=event.target&&!popupItem.nextElementSibling.contains(event.target)){closePopup()}};var popu"
//			+ "pItem;function closePopup(){if(popupItem!=null){popupItem.nextElementSibling.classList.remove('po"
//			+ "pup-show')}}function menuClick(item){closePopup();item.nextElementSibling.classList.add('popup-sh"
//			+ "ow');popupItem=item}window.onclick=function(event){if(popupItem!=null&&popupItem!=event.target&&!"
//			+ "popupItem.nextElementSibling.contains(event.target)){closePopup()}};";
//
//	public static void main(String[] args) {
//		System.out.println(FCS.get("var a={ },b='{0}'", "hello", "world"));
//		try {
//			Map<String, List<Object>> format = new LinkedHashMap<String, List<Object>>();
//			String out = Builder.create().title("Hello").detail("test hello").nav(li(a("/", "Up")))
//					.nav(li(a("/", "Down"))).nav(Util.form(format, "Query", "/", AuthAccountVO.class, Page.class))
//					.next()
//					.grid(table(tbody(tr(th("AuthAccountVO"), td(Util.descript(AuthAccountVO.class))),
//							tr(th("Page"), td(Util.descript(Page.class))))))
//					.data("data").footer("foss").serialize(format);
//			FileUtils.writeStringToFile(new File("/Users/zhaochen/Desktop/test.html"), out);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static void main2(String[] args) {
//		try {
//			String out = HtmlSerializer.DEFAULT.serialize(getContent());
//			System.out.println(out);
//			String body = "<header><h1>Pet Store</h1><h2>The complete list of pets in the store</h2><a href=\"http://juneau.apache.org\"><img src=\"/petstore/htdocs/juneau.png\" style=\"position:absolute;top:5;right:5;background-color:transparent;height:30px\"></a></header><nav><ol><li><a href=\"/\">up</a></li><li><a href=\"/petstore/?method=OPTIONS\">options</a></li><li><div class=\"menu-item\">	<a class=\"link\" onclick=\"menuClick(this)\">query</a>	<div class=\"popup-content\"><form style=\"margin:0px\">	<table>		<tbody><tr>			<th>Search:</th>			<td>				<input name=\"s\" size=\"50\" value=\"\">			</td>			<td>				<div class=\"tooltip\">					<small>(?)</small>					<span class=\"tooltiptext\">						Comma-delimited list of key/value pair search terms.						<br>						<br>Keys are column names.  Values are search terms.						<br>						<br><b>Example:</b>&nbsp;&nbsp;<code>[column1=foo*, column2&lt;100, column3=2013-2016.06.30]</code>						<br>						<br><b>String fields:</b>						<br> -<code>'*'</code> represents any character						<br> -<code>'?'</code> represents one character						<br> - Use single or double quotes for phrases						<br>&nbsp;&nbsp;&nbsp;e.g.<code>[column='foo bar']</code> - The term 'foo bar'						<br> - Multiple search terms are ORed 						<br>&nbsp;&nbsp;&nbsp;e.g.<code>[column=foo bar]</code> - 'foo' OR 'bar'						<br> - Prepend<code>'+'</code> on tokens that must match						<br>&nbsp;&nbsp;&nbsp;e.g.<code>[column=+foo* +*bar]</code> - Start with 'foo' AND end with 'bar'.						<br> - Prepend<code>'-'</code> on tokens that must not match 						<br>&nbsp;&nbsp;&nbsp;e.g.<code>[column=+foo* -*bar]</code> - Start with 'foo' AND does not end with 'bar'.						<br>						<br><b>Numeric fields:</b>						<br><code>[column=123]</code> - A single number						<br><code>[column=1 2 3]</code>	- Multiple numbers						<br><code>[column=1-100]</code> - Between two numbers						<br><code>[column=1-100 200-300]</code> - Two ranges of numbers						<br><code>[column&gt;100]</code> - Greater than a number						<br><code>[column&gt;=100]</code> - Greater than or equal to a number						<br><code>[column=!123]</code> - Not a specific number						<br>						<br><b>Date/Calendar fields:</b>						<br><code>[column=2001]</code> - A specific year						<br><code>[column=2001.01.01.10.50]</code> - A specific time						<br><code>[column&gt;2001]</code> - After a specific year						<br><code>[column&gt;=2001]</code> - During or after a specific year						<br><code>[column=2001-2003.06.30]</code> - A date range						<br><code>[column=2001 2003 2005]</code> - Multiple ORed dates					</span>				</div>			</td>		</tr>		<tr>			<th>View:</th>			<td>				<input name=\"v\" size=\"50\" value=\"\">			</td>			<td>				<div class=\"tooltip\">					<small>(?)</small>					<span class=\"tooltiptext\">						Comma-delimited list of columns to display.						<br>						<br><b>Example:</b>&nbsp;&nbsp;<code>[column1, column2]</code>					</span>				</div>			</td>		</tr>		<tr>			<th>Sort:</th>			<td>				<input name=\"o\" size=\"50\" value=\"\">			</td>			<td>				<div class=\"tooltip\">					<small>(?)</small>					<span class=\"tooltiptext\">						Comma-delimited list of columns to sort by.						<br>Columns can be suffixed with '-' to indicate descending order.						<br>						<br><b>Example:</b>&nbsp;&nbsp;<code>[column1, column2-]</code>						<br>						<br><b>Notes:</b>						<br> - Columns containing collections/arrays/lists are sorted by size.					</span>				</div>			</td>		</tr>		<tr>			<th>Page:</th>			<td>				Position:<input name=\"p\" style=\"width:50px\" step=\"20\" min=\"0\" value=\"\" type=\"number\">				Limit:<input name=\"l\" style=\"width:50px\" step=\"20\" min=\"0\" value=\"\" type=\"number\">				<span style=\"float:right\">Ignore-case:<input name=\"i\" value=\"true\" type=\"checkbox\"></span>			</td>			<td>			</td>		</tr> 		<tr>			<th>				&nbsp;			</th>			<td colspan=\"2\" style=\"text-align:right\">				<input value=\"Reset\" type=\"reset\">				<input value=\"Submit\" type=\"submit\">			</td>		</tr>	</tbody></table></form>	</div></div></li><li><div class=\"menu-item\">	<a class=\"link\" onclick=\"menuClick(this)\">content-type</a>	<div class=\"popup-content\"><div><a href=\"/petstore?plainText=true&amp;Accept=application%2Fjson\">application/json</a><br><a href=\"/petstore?plainText=true&amp;Accept=application%2Fjson%2Bschema\">application/json+schema</a><br><a href=\"/petstore?plainText=true&amp;Accept=application%2Fjson%2Bsimple\">application/json+simple</a><br><a href=\"/petstore?plainText=true&amp;Accept=application%2Fx-www-form-urlencoded\">application/x-www-form-urlencoded</a><br><a href=\"/petstore?plainText=true&amp;Accept=octal%2Fmsgpack\">octal/msgpack</a><br><a href=\"/petstore?plainText=true&amp;Accept=text%2Fhtml\">text/html</a><br><a href=\"/petstore?plainText=true&amp;Accept=text%2Fhtml%2Bschema\">text/html+schema</a><br><a href=\"/petstore?plainText=true&amp;Accept=text%2Fhtml%2Bstripped\">text/html+stripped</a><br><a href=\"/petstore?plainText=true&amp;Accept=text%2Fn-triple\">text/n-triple</a><br><a href=\"/petstore?plainText=true&amp;Accept=text%2Fn3\">text/n3</a><br><a href=\"/petstore?plainText=true&amp;Accept=text%2Fplain\">text/plain</a><br><a href=\"/petstore?plainText=true&amp;Accept=text%2Fturtle\">text/turtle</a><br><a href=\"/petstore?plainText=true&amp;Accept=text%2Fuon\">text/uon</a><br><a href=\"/petstore?plainText=true&amp;Accept=text%2Fxml\">text/xml</a><br><a href=\"/petstore?plainText=true&amp;Accept=text%2Fxml%2Brdf\">text/xml+rdf</a><br><a href=\"/petstore?plainText=true&amp;Accept=text%2Fxml%2Brdf%2Babbrev\">text/xml+rdf+abbrev</a><br><a href=\"/petstore?plainText=true&amp;Accept=text%2Fxml%2Bschema\">text/xml+schema</a><br><a href=\"/petstore?plainText=true&amp;Accept=text%2Fxml%2Bsoap\">text/xml+soap</a><br></div>	</div></div></li><li><div class=\"menu-item\">	<a class=\"link\" onclick=\"menuClick(this)\">styles</a>	<div class=\"popup-content\"><div><a href=\"/petstore?stylesheet=styles%2Fdevops.css\">devops</a><br><a href=\"/petstore?stylesheet=styles%2Flight.css\">light</a><br><a href=\"/petstore?stylesheet=styles%2Foriginal.css\">original</a><br><a href=\"/petstore?stylesheet=styles%2Fdark.css\">dark</a><br></div>	</div></div></li><li><a href=\"https://github.com/apache/juneau/blob/master/juneau-examples-rest/src/main/java/org/apache/juneau/examples/rest/PetStoreResource.java\">source</a></li><li><div class=\"menu-item\">	<a class=\"link\" onclick=\"menuClick(this)\">add</a>	<div class=\"popup-content\"><div><form id=\"form\" action=\"/petstore\" method=\"POST\"><table style=\"white-space:nowrap\"><tbody><tr><th>ID:</th><td><input name=\"id\" value=\"108\" type=\"number\"></td><td><div class=\"tooltip\"><small>(?)</small><span class=\"tooltiptext\">A unique identifer for the pet.<br>Must not conflict with existing IDs</span></div></td></tr><tr><th>Name:</th><td><input name=\"name\" type=\"text\"></td><td><div class=\"tooltip\"><small>(?)</small><span class=\"tooltiptext\">The name of the pet.<br>e.g. 'Fluffy'</span></div></td></tr><tr><th>Kind:</th><td><select name=\"kind\"><option>CAT</option><option>DOG</option><option>BIRD</option><option>FISH</option><option>MOUSE</option><option>RABBIT</option><option>SNAKE</option></select></td><td><div class=\"tooltip\"><small>(?)</small><span class=\"tooltiptext\">The kind of animal.</span></div></td></tr><tr><th>Breed:</th><td><input name=\"breed\" type=\"text\"></td><td><div class=\"tooltip\"><small>(?)</small><span class=\"tooltiptext\">The breed of animal.<br>Can be any arbitrary text</span></div></td></tr><tr><th>Gets along with:</th><td><input name=\"getsAlongWith\" type=\"text\"></td><td><div class=\"tooltip\"><small>(?)</small><span class=\"tooltiptext\">A comma-delimited list of other animal types that this animal gets along with.</span></div></td></tr><tr><th>Price:</th><td><input name=\"price\" placeholder=\"1.0\" step=\"0.01\" min=\"1\" max=\"100\" type=\"number\"></td><td><div class=\"tooltip\"><small>(?)</small><span class=\"tooltiptext\">The price to charge for this pet.</span></div></td></tr><tr><th>Birthdate:</th><td><input name=\"birthDate\" type=\"date\"></td><td><div class=\"tooltip\"><small>(?)</small><span class=\"tooltiptext\">The pets birthday.</span></div></td></tr><tr><td colspan=\"2\" style=\"text-align:right\"><button type=\"reset\">Reset</button><button type=\"button\" onclick=\"window.location.href='/'\">Cancel</button><button type=\"submit\">Submit</button></td></tr></tbody></table></form></div>	</div></div></li></ol></nav><section><article><div class=\"outerdata\"><div class=\"data\" id=\"data\"><table _type=\"array\"><tbody><tr><th>id</th><th>name</th><th>kind</th><th>price</th><th>birthDate</th><th>age</th></tr><tr><td><a href=\"/petstore/101\">101</a></td><td>Mr. Frisky</td><td style=\"background-color:#FDF2E9\"><img src=\"/petstore/htdocs/cat.png\"></td><td>39.99</td><td>2012-07-04</td><td>5</td></tr><tr><td><a href=\"/petstore/102\">102</a></td><td>Kibbles</td><td style=\"background-color:#FDF2E9\"><img src=\"/petstore/htdocs/dog.png\"></td><td>99.99</td><td>2014-09-01</td><td>3</td></tr><tr><td><a href=\"/petstore/103\">103</a></td><td>Hoppy</td><td style=\"background-color:#FDF2E9\"><img src=\"/petstore/htdocs/rabbit.png\"></td><td>49.99</td><td>2017-04-16</td><td>0</td></tr><tr><td><a href=\"/petstore/104\">104</a></td><td>Hoppy 2</td><td style=\"background-color:#FDF2E9\"><img src=\"/petstore/htdocs/rabbit.png\"></td><td>49.99</td><td>2017-04-17</td><td>0</td></tr><tr><td><a href=\"/petstore/105\">105</a></td><td>Gorton</td><td style=\"background-color:#FDF2E9\"><img src=\"/petstore/htdocs/fish.png\"></td><td>1.99</td><td>2017-06-20</td><td>0</td></tr><tr><td><a href=\"/petstore/106\">106</a></td><td>Hackwrench</td><td style=\"background-color:#FDF2E9\"><img src=\"/petstore/htdocs/mouse.png\"></td><td>4.99</td><td>2017-06-20</td><td>0</td></tr><tr><td><a href=\"/petstore/107\">107</a></td><td>Just Snake</td><td style=\"background-color:#FDF2E9\"><img src=\"/petstore/htdocs/snake.png\"></td><td>9.99</td><td>2017-06-21</td><td>0</td></tr></tbody></table></div></div></article><aside><div style=\"max-width:400px\" class=\"text\"> 	<p>This page shows a standard REST resource that renders bean summaries and details.</p> 	<p>It shows how different properties can be rendered on the same bean in different views.</p> 	<p>It also shows examples of HtmlRender classes and @BeanProperty(format) annotations.</p> 	<p>It also shows how the Queryable converter and query widget can be used to create searchable interfaces.</p></div></aside></section>";
//			out = StringUtils.replace(out, "=css=", css);
//			out = StringUtils.replace(out, "=js=", js);
//			out = StringUtils.replace(out, "=body=", body);
//			FileUtils.writeStringToFile(new File("/Users/zhaochen/Desktop/test.html"), out);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static Object getContent() {
//		return html(//
//				head(//
//						meta().attr("http-equiv", "Content-Type").attr("content", "Content-text/html; charset=utf-8"),
//						link("/petstore/htdocs/cat.png").attr("rel", "icon"), style("=css="),
//						script("text/javascript", "=js=")),
//				body("=body="));
//	}
//
//	//	public static Object getNav(){
//	//		
//	//	}
//
//	public static void main1(String[] args) {
//		try {
//			BeanInfo info = Introspector.getBeanInfo(com.foreveross.qdp.infra.vo.system.auth.AuthAccountVO.class);
//			PropertyDescriptor[] pds = info.getPropertyDescriptors();
//			for (PropertyDescriptor pd : pds) {
//				System.out.println(pd.getName());
//				System.out.println(pd.getReadMethod());
//				System.out.println(pd.getWriteMethod());
//			}
//		} catch (IntrospectionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	public static class Util {
//		public interface Any {
//			<T> T any();
//		}
//
//		public static Object descript(final Class<?> clazz) {
//			return table(tbody(new Util.Any() {
//				public Object[] any() {
//					List<Object> list = new ArrayList<Object>();
//					try {
//						BeanInfo info = Introspector.getBeanInfo(clazz);
//						PropertyDescriptor[] pds = info.getPropertyDescriptors();
//						list.add(tr(th("name"), th("type")));
//						for (PropertyDescriptor pd : pds) {
//							if (pd.getReadMethod() != null && pd.getWriteMethod() != null) {
//								list.add(tr(td(pd.getName()), td(pd.getPropertyType().getName())));
//							}
//						}
//					} catch (IntrospectionException e) {
//						e.printStackTrace();
//					}
//					return list.toArray();
//				}
//			}.any())).attr("_type", "array");
//		}
//
//		public static Object[] descriptForm(final Class<?> clazz, final List<String> fields) {
//			List<Object> list = new ArrayList<Object>();
//			try {
//				if (clazz == Page.class) {
//					list.add(tr(td("Page"),
//							td("currentPage:",
//									input("number").name("currentPage").step("1").style("width:100px")
//											.id("qdp_currentPage"),
//									"ASC:", input().name("asc").style("width:100px").id("qdp_asc")),
//							td(div(small("(?)"), span("Page only sort asc").attr("class", "tooltiptext")).attr("class",
//									"tooltip"))));
//					fields.add("currentPage:long");
//					fields.add("asc:text");
//					return list.toArray();
//				}
//				BeanInfo info = Introspector.getBeanInfo(clazz);
//				PropertyDescriptor[] pds = info.getPropertyDescriptors();
//				for (PropertyDescriptor pd : pds) {
//					if (pd.getReadMethod() != null && pd.getWriteMethod() != null) {
//						String type = inputType(pd.getPropertyType());
//						String name = pd.getName();
//						fields.add(name + ":" + type);
//						String trueType = "long".equals(type) || "float".equals(type) || "boolean".equals(type)
//								? "number" : type;
//						Input input = input(trueType).name(name).size("50").id("qdp_" + name);
//						if ("long".equals(type)) {
//							input.step("1");
//						}
//						list.add(
//								tr(td(name), td(input),
//										td(div(small("(?)"),
//												span(pd.getPropertyType().getName()).attr("class", "tooltiptext"))
//														.attr("class", "tooltip"))));
//					}
//				}
//			} catch (IntrospectionException e) {
//				e.printStackTrace();
//			}
//			return list.toArray();
//		}
//
//		public static String inputType(Class<?> clazz) {
//			if (clazz.isAssignableFrom(Date.class)) {
//				return "date";
//			}
//			if (clazz == short.class || clazz == int.class || clazz == long.class || clazz == Short.class
//					|| clazz == Integer.class || clazz == Long.class) {
//				return "long";
//			}
//			if (clazz == float.class || clazz == double.class || clazz == Float.class || clazz == Double.class
//					|| clazz == BigDecimal.class) {
//				return "float";
//			}
//			if (clazz == boolean.class || clazz == Boolean.class) {
//				return "boolean";
//			}
//			return "text";
//		}
//
//		public static Object form(Map<String, List<Object>> format, String name, String action,
//				final Class<?>... clazzs) {
//			final String functionId = StringHelper.uuid();
//			Object[] any = null;
//			{
//				List<Object> list = new ArrayList<Object>();
//				StringBuilder scriptText = new StringBuilder();
//				scriptText.append("function getIdVal_").append(functionId).append(
//						"(idType){var it=idType.split(':'); var val=document.getElementById('qdp_'+it[0]).value||'';");
//				scriptText.append(
//						"var tval=(it[1]=='float'||it[1]=='long')?(val=='0'?0:(Number(val)||'')):(it[1]=='boolean'?(val=='1'||val=='true'?true:false):val);return tval;");
//				scriptText.append("};");
//				scriptText.append("var argArr=[];");
//				for (int i = 0; i < clazzs.length; i++) {
//					Class<?> clazz = clazzs[i];
//					ArrayList<String> fields = new ArrayList<String>();
//					Object[] trs = descriptForm(clazz, fields);
//					list.add(tr(th("arg" + i), td(table(tbody(trs)))));
//					if (clazz.isAssignableFrom(Page.class)) {
//						String ss = FCS
//								.get("function arg{index}(){var arg={ }; var cp=getIdVal_{funId}('currentPage:long'),asc=getIdVal_{funId}('asc:text'); (cp!==null&&cp!=='')&&(arg.currentPage=cp); (asc!=null&&asc!='')&&(arg.orderBy=[{'name':asc,'order':'asc'}]); return arg;}argArr.push(arg{index});",
//										i, functionId, functionId, i)
//								.toString();
//						scriptText.append(ss);
//					} else {
//						String ss = FCS
//								.get("function arg{index}(){var arg={ }; var fts='{fields}'.split(',');for(var i=0;i<fts.length;i++){var idval=getIdVal_{funId}(fts[i]);(idval!==null&&idval!=='')&&(arg[fts[i].split(':')[0]]=idval);}return arg;}argArr.push(arg{index});",
//										i, StringUtils.join(fields, ','), functionId, i)
//								.toString();
//						scriptText.append(ss);
//					}
//				}
//				String genUrl = FCS.get(
//						"function genUrl_{funId}(){var action=document.getElementById('qdp_{funId}').action||'';"
//								+ "var argVals=[];for(var i=0;i<argArr.length;i++){" + "var val=argArr[i]();"
//								+ "argVals.push('arg'+i+'='+window.encodeURIComponent(typeof(val)!='object'?(val+''):JSON.stringify(val)));}"
//								+ "var url=action+'/'+argVals.join('/');"
//								+ "document.getElementById('qdp_url_{funId}').value=url;}",
//						functionId, functionId, functionId).toString();
//				scriptText.append(genUrl);
//				String ajax = FCS.get(
//						"function ajax_{funId}(){var ajx=new XMLHttpRequest();var url=document.getElementById('qdp_url_{funId}').value;ajx.open('GET',url,true);console.log(url);"
//								+ "ajx.onreadystatechange=function(){if(ajx.readyState==4&&ajx.status==200){document.getElementById('qdp_code').innerText=ajx.responseText;}};"
//								+ "ajx.setRequestHeader('Content-type','application/x-www-form-urlencoded; charset=utf-8');ajx.send();}",
//						functionId, functionId).toString();
//				scriptText.append(ajax);
//				format.put("=$$" + functionId + "$$=", Arrays.asList((Object) scriptText.toString()));
//
//				list.add(tr(th("URL"), td(textarea().cols(60).rows(2).id("qdp_url_" + functionId))));
//				list.add(tr(th(),
//						td(script("text/javascript", "=$$" + functionId + "$$="), input("reset").value("Reset"),
//								input("button").value("Gen Url").onclick("genUrl_" + functionId + "()"),
//								input("button").value("Submit").onclick("ajax_" + functionId + "()"))
//										.style("text-align:right")));
//				any = list.toArray();
//			}
//			return li(//
//					div(//
//							a("javascript:;", name).attr("class", "link").onclick("menuClick(this)"), //
//							div(//
//									HtmlBuilder.form(action, //
//											table(//
//													tbody(//
//															any)))
//											.id("qdp_" + functionId).style("margin:0px")).attr("class",
//													"popup-content")).attr("class", "menu-item"));
//		}
//	}
//
//	public static class BuilderInterface {
//		public interface SetTitle {
//			SetDetail title(Object obj);
//		}
//
//		public interface SetDetail {
//			SetNav detail(Object obj);
//		}
//
//		public interface SetNav {
//			SetNav nav(Object obj);
//
//			SetGrid next();
//		}
//
//		public interface SetGrid {
//			SetData grid(Object obj);
//		}
//
//		public interface SetData {
//			SetFooter data(Object obj);
//		}
//
//		public interface SetFooter {
//			Builder footer(Object obj);
//		}
//	}
//
//	public static class Builder implements BuilderInterface.SetTitle, BuilderInterface.SetDetail,
//			BuilderInterface.SetNav, BuilderInterface.SetGrid, BuilderInterface.SetData, BuilderInterface.SetFooter {
//
//		private Map<String, List<Object>> map = new LinkedHashMap<String, List<Object>>();
//		private HtmlElement template = html(//
//				head(//
//						meta().attr("http-equiv", "Content-Type").attr("content", "Content-text/html; charset=utf-8"), //
//						style("=$$css$$="), //
//						script("text/javascript", "=$$js$$=")//
//		), //
//				body(//
//						header(//
//								h1("=$$title$$="), //
//								h2("=$$detail$$=")), //
//						HtmlBuilder.nav(ol("=$$nav$$=")), //
//						section(//
//								article(//
//										div(//
//												div("=$$grid$$=").attr("class", "data").attr("id", "data"))
//														.attr("class", "outerdata")), //
//								aside(//
//										div(//
//												code("=$$data$$=").id("qdp_code")).attr("class", "text")
//														.style("max-width:400px"))), //
//						HtmlBuilder.footer("=$$footer$$="))//
//		);
//
//		public static Builder create() {
//			return new Builder();
//		}
//
//		public String serialize() {
//			String out = "";
//			try {
//				out = HtmlSerializer.DEFAULT.serialize(template);
//				StringBuilder sb = new StringBuilder(256);
//				for (Entry<String, List<Object>> entry : map.entrySet()) {
//					sb.setLength(0);
//					for (Object o : entry.getValue()) {
//						if (o instanceof HtmlElement) {
//							sb.append(HtmlSerializer.DEFAULT.serialize(o));
//						} else {
//							sb.append(o);
//						}
//					}
//					out = StringUtils.replace(out, entry.getKey(), sb.toString());
//				}
//				out = StringUtils.replace(out, "=$$css$$=", css);
//				out = StringUtils.replace(out, "=$$js$$=", js);
//			} catch (Exception e) {
//				Exceptions.runtime("error:", e);
//			}
//			return out;
//		}
//
//		public String serialize(Map<String, List<Object>> map) {
//			String out = "";
//			try {
//				out = serialize();
//				StringBuilder sb = new StringBuilder(256);
//				for (Entry<String, List<Object>> entry : map.entrySet()) {
//					sb.setLength(0);
//					for (Object o : entry.getValue()) {
//						if (o instanceof HtmlElement) {
//							sb.append(HtmlSerializer.DEFAULT.serialize(o));
//						} else {
//							sb.append(o);
//						}
//					}
//					out = StringUtils.replace(out, entry.getKey(), sb.toString());
//				}
//			} catch (Exception e) {
//				Exceptions.runtime("error:", e);
//			}
//			return out;
//		}
//
//		public Builder footer(Object obj) {
//			map.put("=$$footer$$=", Arrays.asList(obj));
//			return this;
//		}
//
//		public BuilderInterface.SetFooter data(Object obj) {
//			map.put("=$$data$$=", Arrays.asList(obj));
//			return this;
//		}
//
//		public BuilderInterface.SetData grid(Object obj) {
//			map.put("=$$grid$$=", Arrays.asList(obj));
//			return this;
//		}
//
//		public BuilderInterface.SetNav nav(Object obj) {
//			if (map.get("<string>=$$nav$$=</string>") == null) {
//				map.put("<string>=$$nav$$=</string>", new ArrayList<Object>(Arrays.asList(obj)));
//			} else {
//				map.get("<string>=$$nav$$=</string>").add(obj);
//			}
//			return this;
//		}
//
//		public BuilderInterface.SetGrid next() {
//			return this;
//		}
//
//		public BuilderInterface.SetNav detail(Object obj) {
//			map.put("=$$detail$$=", Arrays.asList(obj));
//			return this;
//		}
//
//		public BuilderInterface.SetDetail title(Object obj) {
//			map.put("=$$title$$=", Arrays.asList(obj));
//			return this;
//		}
//	}
//}
