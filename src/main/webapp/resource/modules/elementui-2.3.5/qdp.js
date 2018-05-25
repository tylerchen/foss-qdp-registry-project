;
'use strict';
/**
 * setting application context.
 */
var ctx = window['ctx'] || "";
/**
 * debug=true will output log
 */
var DEBUG = window['DEBUG'] !== false;
/**
 * a no op function.
 */
var noop = window.noop = function() {
};
/**
 * if has console object then LOG = console.log.
 */
var LOG = (typeof (window["console"]) === "object" && DEBUG) ? console.log : noop;
/**
 * if has console object then ERR = console.error.
 */
var ERR = typeof (window["console"]) === "object" ? console.error : noop;
var EUC = window.encodeURIComponent;
var DUC = window.decodeURIComponent;
var DU = window.decodeURI;
/**
 * test if the parameter is a function or a function name.
 * @param functionName
 * @returns {Boolean}
 */
function qdpIsFunction(functionName) {
	return functionName && (typeof (functionName) == 'function' || (typeof (window[functionName]) == 'function'));
}
/**
 * test if the parameter is object type
 * @param obj
 * @returns {Boolean}
 */
function qdpIsObject(obj) {
	return qdpIsPlainObject(obj);
}
/**
 * test if the parameter is array type, consider the parent window.
 * @param obj
 * @returns {Boolean}
 */
function qdpIsArray(obj) {
	return obj != null && Object.prototype.toString.call(obj) === '[object Array]';
}
/**
 * tst if parameter is string type.
 * @param obj
 * @returns {Boolean}
 */
function qdpIsString(obj) {
	return obj != null && typeof (obj) == 'string';
}
/**
 * tst if parameter is number type.
 * @param obj
 * @returns {Boolean}
 */
function qdpIsNumber(obj) {
	return obj != null && typeof (obj) == 'number';
}
/**
 * object to string.
 * @param obj
 * @returns
 */
function qdpToString(obj) {
	if (typeof (obj) == "undefined" || obj == null) {
		return "";
	}
	if (typeof (obj) == "string") {
		return obj;
	}
	if (typeof (obj) == "number") {
		return obj + "";
	}
	if (typeof (obj) == "boolean") {
		return obj ? "true" : "false";
	}
	if (typeof (obj) == "function") {
		return obj + "";
	}
	if (obj["status"] && obj["statusText"]) {
		return "Status: " + obj["status"] + ", Status text: " + obj["statusText"] + ", Response text: " + obj["responseText"];
	}
	if (obj["stack"] && obj["message"] && obj["lineNumber"]) {
		return "Error: " + obj["message"] + ", lineNumber: " + obj["lineNumber"] + ", columnNumber: " + obj["columnNumber"] + ", stack: " + obj["stack"]
				+ ", fileName: " + obj["fileName"];
	}
	try {
		return JSON.stringify(obj);
	} catch (err) {
		return obj;
	}
}
/**
 * escape " or '.
 * @param str
 * @param br
 * @returns {String}
 */
function qdpEscape(str, char) {
	var s = "";
	if (!str || str.length == 0) {
		return "";
	}
	s = str.replace(/\\/g, "\\\\");
	if (char == "'") {
		s = s.replace(/\'/g, "\\'");
	} else if (char == '"') {
		s = s.replace(/\"/g, '\\"');
	}
	return s;
}
/**
 * encode html chars.
 * @param str
 * @param br
 * @returns {String}
 */
function qdpEncode(str, br) {
	var s = "";
	if (!str || str.length == 0) {
		return "";
	}
	s = str.replace(/&/g, "&gt;");
	s = s.replace(/</g, "&lt;");
	s = s.replace(/>/g, "&gt;");
	s = s.replace(/ /g, "&nbsp;");
	s = s.replace(/\'/g, "&apos;");
	s = s.replace(/\"/g, "&quot;");
	s = br === true ? s.replace(/\n/g, "<br>") : s;
	return s;
}
/**
 * decode html chars.
 * @param str
 * @param br
 * @returns {String}
 */
function qdpDecode(str, br) {
	var s = "";
	if (!str || str.length == 0) {
		return "";
	}
	s = str.replace(/&gt;/g, "&");
	s = s.replace(/&lt;/g, "<");
	s = s.replace(/&gt;/g, ">");
	s = s.replace(/&nbsp;/g, " ");
	s = s.replace(/&apos;/g, "\'");
	s = s.replace(/&quot;/g, "\"");
	s = br === true ? s.replace(/<br>/g, "\n") : s;
	return s;
}
/**
 * destroy a object.
 * @param obj
 */
function qdpDestroy(obj) {
	if (obj) {
		for ( var prop in obj) {
			try {
				delete obj[prop];
			} catch (err) {
			}
		}
	}
}

function qdpRandom() {
	return (Math.random() + '').substring(2);
}

function qdpHash(str) {
	var hash = 0, i, chr, len;
	if (str.length === 0) {
		return hash;
	}
	for (i = 0, len = str.length; i < len; i++) {
		chr = str.charCodeAt(i);
		hash = ((hash << 5) - hash) + chr;
		hash |= 0; // Convert to 32bit integer
	}
	return hash;
}

function qdpLength(obj) {
	if (obj == null) {
		return 0;
	} else if (obj.length > 0) {
		return parseInt(obj.length);
	} else if (obj.length === 0) {
		return 0;
	}
	var len = 0;
	for ( var key in obj) {
		if (key != "toString" || key != "valueOf" /* ie contains this property */) {
			len = len + 1;
		}
	}
	return len;
}

function qdpIsEmpty(obj) {
	return qdpLength(obj) < 1;
}

function qdpIsPlainObject(val) {
	return Object.prototype.toString.call(val) === '[object Object]';
}

function qdpToMap() {
	var obj = {};
	for (var i = 0; i < arguments.length; i++) {
		obj[arguments[i]] = obj[i + 1];
		i = i + 1;
	}
	return obj;
}

function qdpExtend(src, src2) {
	if (typeof (src) == 'object' && typeof (src2) == 'object') {
		for ( var prop in src2) {
			src[prop] = src2[prop];
		}
	}
	return src;
}

function qdpJsonCopy(src) {
	if (src == null) {
		return null;
	}
	return JSON.parse(JSON.stringify(src));
}

function qdpCombine() {
	var args = Array.prototype.slice.call(arguments, 0);
	var obj = {};
	for (var i = 0; i < args.length; i++) {
		qdpMerge(obj, qdpJsonCopy(args[i]));
	}
	return obj;
}

function qdpMerge(to, from, useVue) {
	if (!from) {
		return to;
	}
	to = to == null ? {} : to;
	var key, toVal, fromVal;
	var keys = Object.keys(from);
	var hasOwn = function(obj, key) {
		return Object.prototype.hasOwnProperty.call(obj, key);
	};
	var set = (Vue && useVue !== false) ? Vue.set : function(target, key, val) {
		if (Array.isArray(target)) {
			target.length = Math.max(target.length, key);
			target.splice(key, 1, val);
			return val;
		}
		target[key] = val;
		return val;
	};
	for (var i = 0; i < keys.length; i++) {
		key = keys[i];
		toVal = to[key];
		fromVal = from[key];
		if (!hasOwn(to, key)) {
			set(to, key, fromVal);
		} else if (qdpIsPlainObject(toVal) && qdpIsPlainObject(fromVal)) {
			qdpMerge(toVal, fromVal);
		} else {
			set(to, key, fromVal);
		}
	}
	return to;
}

function qdpPathJoin() {
	var path = '';
	for (var i = 0; i < arguments.length; i++) {
		var arg = qdpIsString(arguments[i]) ? arguments[i] : (arguments[i] + '');
		if (!arg) {
			continue;
		}
		if (!path) {
			path = arg;
		} else {
			var pathEnd = path.charAt(path.length - 1) == '/';
			var argStart = arg.charAt(0) == '/';
			if (pathEnd && argStart) {
				path = path + arg.substring(1);
			} else if (!pathEnd && !argStart) {
				path = path + '/' + arg;
			} else {
				path = path + arg;
			}
		}
	}
	return path;
}

function qdpParamMap(paramStr, name, map) {
	var pattern = /(\w*)=([a-zA-Z0-9\u4e00-\u9fa5]+)/ig, params = map || {}; /*定义正则表达式和一个空对象*/
	decodeURIComponent(paramStr || window.location.href, true).replace(pattern, function(a, b, c) {
		params[b] = c;
	});
	return name ? (params[name] ? params[name] : null) : params;
}

function qdpFixUrl(baseUrl, paramStrs, removeParams) {
	baseUrl = baseUrl || window.location.href;
	baseUrl = baseUrl.indexOf("?") > -1 ? baseUrl.substring(0, baseUrl.indexOf("?")) : baseUrl;
	var paramMap = qdpParamMap(baseUrl, null);
	if (qdpIsArray(paramStrs)) {
		for (var i = 0; i < paramStrs.length; i++) {
			if (paramStrs[i]) {
				paramMap = qdpParamMap(paramStrs[i], null, paramMap);
			}
		}
	}
	if (qdpIsArray(removeParams)) {
		for (var i = 0; i < removeParams; i++) {
			if (removeParams[i]) {
				delete paramMap[removeParams[i]];
			}
		}
	}
	var paramSplit = [];
	for ( var p in paramMap) {
		paramSplit.push(p + '=' + paramMap[p]);
	}
	var lastChar = baseUrl.charAt(baseUrl.length - 1), queryChar = baseUrl.indexOf("?") < 0 ? "?" : (lastChar == "?" ? "" : (lastChar == "&" ? "" : "&"));
	var url = baseUrl + queryChar + (paramSplit.length > 0 ? paramSplit.join("&") : "");
	return url.charAt(url.length - 1) == "?" || url.charAt(url.length - 1) == "&" ? url.substring(0, url.length - 1) : url;
}

function qdpStringTemplate(str, data, parseJson) {
	if (str == null || str == "") {
		return str;
	}
	var value = str || "", d = data;
	if (value) {
		var values = [ [] ];
		/**
		 * parse {{ and }} block.
		 */
		for (var i = 0; i < value.length; i++) {
			var c = value.charAt(i);
			if (c == "{" && value.charAt(i + 1) == "{") {
				i = i + 1;
				/**
				 * join before array as string.
				 */
				var before = values.pop();
				if (before.length > 0) {
					values.push(before.join(""));
				}
				values.push([ "{{" ]);
				continue;
			}
			if (c == "}" && value.charAt(i + 1) == "}") {
				i = i + 1;
				var evalStr = values.pop().join("").substring(2);
				/**
				 * 如果{{}}中的是一个表达式，那么就要处理了，如{{ctx.length}}。
				 */
				var evalValue = eval("(" + evalStr + ")");
				if (evalValue != null) {
					values.push(evalValue);
				}
				/**
				 * 添加一个空数组用于存放后面的字符串。
				 */
				i < value.length && values.push([]);
				continue;
			}
			/**
			 * 添加字符。
			 */
			values[values.length - 1].push(c);
		}
		if (values.length == 1 || (values.length == 2 && values[1].length == 0)) {
			/**
			 * 如果只是一个{{fields}}表达式，那么返回的可能不是一个字符串，而是其他对象。
			 */
			value = qdpIsArray(values[0]) && qdpIsString(values[0][0]) ? values[0].join("") : values[0];
		} else {
			/**
			 * join last array as string.
			 */
			var last = values.pop();
			if (last.length > 0) {
				values.push(last.join(""));
			}
			value = values.join("");
		}
		/**
		 * parse json block for: {} and [].
		 */
		if (parseJson == true && value && qdpIsString(value) && (value.charAt(0) == "{" || value.charAt(0) == "[")) {
			value = eval("(" + value + ")");
		}
	}
	return value;
}

function qdpGrid(json, name) {
	var grid = qdpJsonCopy(json[name] || {}), fields = json.fields || [], columns = [], col, field;
	/**添加checkbox**/
	if (grid.multiSelect !== false) {
		col = {}, col.type = "selection", col.width = 35, col.align = "center", columns.push(col);
	}
	grid.columns = columns;
	for (var i = 0; i < fields.length; i++) {
		field = qdpJsonCopy(fields[i]);
		if (!field || !field.scope || field.scope.indexOf(name) < 0) {
			continue;
		}
		if ([ "radio", "checkbox", "checkbox2", "select", "switch" ].indexOf(field.type) > -1) {
			field.render = function(h, rowColumnIndex) {
				var row = rowColumnIndex.row, column = rowColumnIndex.column, index = rowColumnIndex.index;
				return h('span', {}, column.data ? (column.data[row[column.name]] || row[column.name]) : row[column.name]);
			};
		}
		field.ellipsis == null && (field.ellipsis = true);
		(!field.title) && (field.title = field.label), !field.key && (field.key = field.name), columns.push(field);
	}
	return grid;
}

function qdpForm(json, name) {
	var form = qdpJsonCopy(json[name] || {}), fields = json.fields || [], items = [], field;
	form.items = items;
	for (var i = 0; i < fields.length; i++) {
		field = qdpJsonCopy(fields[i]), item = {};
		if (!field || !field.scope || field.scope.indexOf(name) < 0) {
			continue;
		}
		items.push(field);
	}
	return form;
}
/**
 * actionStr=actionTitle,actionIcon,actionCode;actionTitle,actionIcon,actionCode;...
 */
function qdpParseActions(actionStr) {
	var actions = [];
	actionStr = actionStr || '';
	var split1 = actionStr.split(';');
	for (var i = 0; i < split1.length; i++) {
		var split2 = split1[i].split(',');
		if (split2.length == 3) {
			actions.push({
				title : split2[0], icon : split2[1], action : split2[2]
			});
		}
	}
	return actions;
}
/**
 * scope:A=add, E=edit, I=info, D=delete, G=grid, T=tree, S=Search
 * fieldStr=label,name,type,scope,sort,required
 * { "label" : "id", "scope" : "edit,info,delete", "type" : { "default" : "hidden", "edit" : "label" }, "" : "主键" },
 */
function qdpParseField(fieldStr, objMerged) {
	var field = {
		label : '', name : '', type : 'text', scope : 'AEIGTS', sortable : false, required : false
	};
	fieldStr = fieldStr || '';
	var sp = fieldStr.split(',');
	sp.length > 0 && (field.label = sp[0]);
	sp.length > 1 && (field.name = sp[1]);
	sp.length > 2 && (field.type = sp[2] || field.type);
	sp.length > 3 && (field.scope = sp[3]);
	sp.length > 4 && (field.sortable = sp[4] == 'true');
	sp.length > 5 && (field.required = sp[5] == 'true');
	var scope = '';
	field.scope.indexOf('A') > -1 && (scope += 'add,');
	field.scope.indexOf('E') > -1 && (scope += 'edit,');
	field.scope.indexOf('I') > -1 && (scope += 'info,');
	field.scope.indexOf('D') > -1 && (scope += 'delete,');
	field.scope.indexOf('G') > -1 && (scope += 'grid,');
	field.scope.indexOf('T') > -1 && (scope += 'tree,');
	field.scope.indexOf('S') > -1 && (scope += 'search,');
	field.scope.indexOf('X') > -1 && (scope += 'expand,');
	field.scope = scope;
	field.required && (field.rules = {
		required : true, type : 'number' == field.type ? 'number' : 'string', trigger : "blur"
	});
	if (objMerged) {
		qdpMerge(field, objMerged, false);
	}
	return field;
}

function qdpTree(nodeArrJson, labelField, idField, pidField, isExpand, checkedArr, disabledArr) {
	//qdpTree([{id:"aaa", type1:"aaa", type2:"bbb", title:"aaa-bbb"},{id:"bbb", type1:"aaa", type2:"ccc", pid:"aaa", title:"aaa-ccc"}], "title", "id", "pid", true, [], [])
	var treeData = [], idMap = {}, pidMap = {}, checkedMap = {}, disableMap = {};
	nodeArrJson = nodeArrJson || [], labelField = labelField || "title", idField = idField || "id", pidField = pidField || "pid", isExpand = isExpand === true,
			checkedArr = checkedArr || [], disabledArr = disabledArr || [];
	if (checkedArr.length > 0 && typeof (checkedArr[0]) == 'object') {
		var newArr = [];
		for (var i = 0; i < checkedArr.length; i++) {
			newArr.push(checkedArr[i][idField]);
		}
		checkedArr = newArr;
	}
	for (var i = 0; i < nodeArrJson.length; i++) {
		var data = nodeArrJson[i], id = data[idField], pid = data[pidField], d = qdpObj(qdpCombine({}, data), idField, id, pidField, pid, 'data', data,
				labelField, data[labelField]);
		id && (idMap[id] = d); // 把数据按ID装进idMap
		!pid && (pidMap[id] = d); // 如果pid为空，则自已为要结点，放进pidMap
		pid && (pidMap[pid] = idMap[pid] || {}); // 如果pid不为空，则把pid这条数据放进pidMap，有可能idMap中还没有包括这条件数据
		pidMap[id] && (pidMap[id] = d); // 这里是确保上一行没包括数据时把正确的数据放进去
		isExpand && (d.expand = true);
		checkedArr.indexOf(id) > -1 && (d.checked = true);
		disabledArr.indexOf(id) > -1 && (d.disabled = true);
	}
	for ( var id in idMap) {
		var pid = idMap[id][pidField];
		pid && pidMap[pid] && (pidMap[pid].children = pidMap[pid].children || []) && pidMap[pid].children.push(idMap[id]);
		!pid && treeData.push(idMap[id]);
	}
	return treeData;
}

var qdpGroupByTreeMap = qdpObj({}, "S", "默认", "MENU", "菜单", "URL", "URL", "BUTTON", "按钮", "OTHER", "其他");

function qdpGroupByTree(groupFields, nodeArrJson, labelField, idField, pidField, isExpand, checkedArr, disabledArr) {
	//qdpGroupByTree(["type1","type2"], [{id:"aaa", type1:"aaa", type2:"bbb", title:"aaa-bbb"},{id:"bbb", type1:"aaa", type2:"ccc", title:"aaa-ccc"}], "title", "id", "pid", true, [], [])
	var treeData = [], idMap = {}, pidMap = {}, checkedMap = {}, disableMap = {};
	nodeArrJson = nodeArrJson || [], labelField = labelField || "name", idField = idField || "id", pidField = pidField || "pid", isExpand = isExpand === true,
			checkedArr = checkedArr || [], disabledArr = disabledArr || [];
	if (!groupFields || !groupFields.length) {
		return treeData;
	}
	if (checkedArr.length > 0 && typeof (checkedArr[0]) == 'object') {
		var newArr = [];
		for (var i = 0; i < checkedArr.length; i++) {
			newArr.push(checkedArr[i][idField]);
		}
		checkedArr = newArr;
	}
	for (var i = 0; i < nodeArrJson.length; i++) {
		var data = nodeArrJson[i], id = data[idField], d = qdpObj(qdpCombine({}, data), idField, id, pidField, null, 'data', data, labelField, data[labelField]);
		var groupId = "", parentGroupId = null;
		for (var j = 0; j < groupFields.length; j++) {
			parentGroupId = groupId;
			var groupFieldData = data[groupFields[j]];
			groupId = groupId + "#" + groupFieldData;
			pidMap[groupId] = pidMap[groupId]
					|| qdpObj({}, idField, groupId, pidField, parentGroupId || null, labelField, qdpGroupByTreeMap[groupFieldData] || groupFieldData, 'expand',
							isExpand, 'checked', false);
			idMap[groupId] = idMap[groupId] || pidMap[groupId];
		}
		id && (d[pidField] = groupId) && (idMap[id] = d);
		isExpand && (d.expand = true);
		checkedArr.indexOf(id) > -1 && (d.checked = true);
		disabledArr.indexOf(id) > -1 && (d.disabled = true);
	}
	for ( var id in idMap) {
		var pid = idMap[id][pidField];
		pid && pidMap[pid] && (pidMap[pid].children = pidMap[pid].children || []) && pidMap[pid].children.push(idMap[id]);
		!pid && treeData.push(idMap[id]);
	}
	return treeData;
}

function qdpActionName(actionName, componentType) {
	if (!actionName) {
		return '';
	}
	if (componentType) {
		return actionName + ':' + componentType;
	}
	return actionName.indexOf(':') > 0 ? actionName.substring(0, actionName.indexOf(':')) : actionName;
}

function qdpHttpError(response) {
	this.$notify({
		title : 'Http=>Error', desc : response.url + '\n<br/>' + qdpToString(response), duration : 0
	});
}

function qdpFormatUrl(url, query) {
	query = query || {};
	var condition = {};
	for (prop in query) {
		var param = query[prop];
		param = param == null ? '' : param;
		param = typeof (param) == 'object' || qdpIsArray(param) ? JSON.stringify(param) : (param + '');
		var val = EUC(param);
		condition[prop] = val;
	}
	return qdpStringTemplate((url || '').replace('\/\/',''), condition);
}
function qdpJsonForm(formData) {
	var newFormData = {};
	qdpMap(formData, function(value, key) {
		var val = typeof (value) == 'object' || qdpIsArray(value) ? JSON.stringify(value) : (value + '');
		newFormData[key] = val;
	});
	return newFormData;
}

function qdpProps(obj, join) {
	var props = [];
	if (typeof (obj) == 'object') {
		props = Object.keys(obj);
	}
	return join ? props.join(join) : props;
}

function qdpObj(obj) {
	obj = obj || {};
	var args = Array.prototype.slice.call(arguments, 1) || [];
	for (var i = 0; i < args.length; i++) {
		obj[args[i]] = args[i + 1] == null ? null : args[i + 1];
		i++;
	}
	return obj;
}
function qdpMap(obj, func) {
	var arr = [];
	if (obj == null) {
		return arr;
	}
	if (qdpIsArray(obj)) {
		for (var i = 0; i < obj.length; i++) {
			arr.push(func(obj[i], i, i));
		}
	} else if (typeof (obj) === 'object') {
		var i = 0;
		for ( var prop in obj) {
			arr.push(func(obj[prop], prop, i));
			i = i + 1;
		}
	} else if (typeof (obj) == "string") {
		for (var i = 0; i < obj.length; i++) {
			arr.push(func(obj.charAt(i), i, i));
		}
	} else if (typeof (obj) == "number") {
		for (var i = 1; i < obj; i++) {
			arr.push(func(i, i, i));
		}
	}
	return arr;
}
function qdpByPath(target, path) {
	var obj = target, paths = (path || '').split('/'), p = "";
	for (var i = 0; i < paths.length; i++) {
		var p = paths[i];
		if (p == "") {
			continue;
		}
		if (p && qdpIsObject(obj)) {
			obj = obj[p];
		} else {
			return null;
		}
	}
	return obj;
}
function qdpUrl(config, value) {
	return value[config.urlField || 'url'] || value.url || value.link || value.path || '';
}
function qdpTitle(config, value) {
	return value[config.titleField || config.labelField || 'title'] || value.title || value.label || value.name || '';
}
function qdpLabel(config, value) {
	return value[config.labelField || 'label'] || value.label || value.label || value.name || '';
}
function qdpIcon(config, value) {
	var icon = value[config.iconField || 'icon'] || value.icon || value.ico || '';
	return icon.indexOf('el-icon-') == 0 ? icon : '';
}
function qdpSubItem(config, value) {
	return value[config.subItemField || 'items'] || value.children || value.items || value.subMenus || [];
}
function qdpId(config, value) {
	return value[config.idField || 'id'] || value.id;
}
function qdpLGetItem(key, defaultValue) {
	var value = localStorage.getItem(key);
	if (value == null) {
		return defaultValue || value;
	}
	if (value.charAt(0) == '{' || value.charAt(0) == '[') {//'}',']'
		return JSON.parse(value);
	}
	return typeof (defaultValue) == typeof (value) ? (value || defaultValue) : defaultValue;
}
function qdpLSetItem(key, value) {
	if (value == null) {
		localStorage.removeItem(key);
	} else {
		localStorage.setItem(key, value == null ? '' : (typeof (value) == 'string' ? value : JSON.stringify(value)));
	}
}
function qdpSGetItem(key, defaultValue) {
	var value = sessionStorage.getItem(key);
	if (value == null) {
		return defaultValue || value;
	}
	if (value.charAt(0) == '{' || value.charAt(0) == '[') {//'}',']'
		return JSON.parse(value);
	}
	return typeof (defaultValue) == typeof (value) ? (value || defaultValue) : defaultValue;
}
function qdpSSetItem(key, value) {
	if (value == null) {
		sessionStorage.removeItem(key);
	} else {
		sessionStorage.setItem(key, value == null ? '' : (typeof (value) == 'string' ? value : JSON.stringify(value)));
	}
}
function qdpIsSuccess(data) {//"header":{"status":"success"
	return qdpAjaxDataHead(data, 'status') == 'success';
}
function qdpAjaxData(data) {
	return data && data.body != null ? data.body : null;
}
function qdpAjaxDataBody(data) {
	return data && data.body && data.body.body != null ? data.body.body : null;
}
function qdpAjaxDataHead(data, key) {
	return data && data.body && data.body.header != null ? (key != null ? data.body.header[key] : data.body.header) : null;
}

function setCookie(name, value, days) {
	var d = new Date();
	days = (value == null ? -1 : (days || 0));
	d.setTime(d.getTime() + 24 * 60 * 60 * 1000 * days);
	window.document.cookie = name + "=" + (value == null ? '' : value) + ";path=/;expires=" + d.toGMTString();
}
function getCookie(name) {
	var v = window.document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
	return v ? v[2] : null;
}
