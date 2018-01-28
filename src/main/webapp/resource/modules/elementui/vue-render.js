;
'use strict';
var VueBinding = function(root) {
	var obj = {};
	obj.M = obj.mounted = function(key, func) {
		!root.datas.binding && (root.datas.binding = qdpObj({}, 'mounted', {}, 'beforeDestroy', {}, 'onPost', {}));
		!root.datas.binding.mounted && (root.datas.binding.mounted = {});
		root.datas.binding.mounted[key] = func;
		return this;
	};
	obj.D = obj.beforeDestroy = function(key, func) {
		!root.datas.binding && (root.datas.binding = qdpObj({}, 'mounted', {}, 'beforeDestroy', {}, 'onPost', {}));
		!root.datas.binding.beforeDestroy && (root.datas.binding.beforeDestroy = {});
		root.datas.binding.beforeDestroy[key] = func;
		return this;
	};
	obj.P = obj.onPost = function(key, func) {
		!root.datas.binding && (root.datas.binding = qdpObj({}, 'mounted', {}, 'beforeDestroy', {}, 'onPost', {}));
		!root.datas.binding.onPost && (root.datas.binding.onPost = {});
		root.datas.binding.onPost[key] = func;
		return this;
	};
	obj['-M'] = obj.removeMounted = function(key) {
		!root.datas.binding && (root.datas.binding = qdpObj({}, 'mounted', {}, 'beforeDestroy', {}, 'onPost', {}));
		!root.datas.binding.mounted && (root.datas.binding.mounted = {});
		if (key.charAt(0) == '*') {
			var id = key.substring(1);
			qdpMap(root.datas.binding.mounted, function(value, key) {
				if (key.endsWith(id)) {
					delete root.datas.binding.mounted[key];
				}
			});
		} else {
			delete root.datas.binding.mounted[key];
		}
		return this;
	};
	obj['-D'] = obj.removeBeforeDestroy = function(key) {
		!root.datas.binding && (root.datas.binding = qdpObj({}, 'mounted', {}, 'beforeDestroy', {}, 'onPost', {}));
		!root.datas.binding.beforeDestroy && (root.datas.binding.beforeDestroy = {});
		if (key.charAt(0) == '*') {
			var id = key.substring(1);
			qdpMap(root.datas.binding.beforeDestroy, function(value, key) {
				if (key.endsWith(id)) {
					delete root.datas.binding.beforeDestroy[key];
				}
			});
		} else {
			delete root.datas.binding.beforeDestroy[key];
		}
		return this;
	};
	obj['-P'] = obj.removeOnPost = function(key) {
		!root.datas.binding && (root.datas.binding = qdpObj({}, 'mounted', {}, 'beforeDestroy', {}, 'onPost', {}));
		!root.datas.binding.onPost && (root.datas.binding.onPost = {});
		if (key.charAt(0) == '*') {
			var id = key.substring(1);
			qdpMap(root.datas.binding.onPost, function(value, key) {
				if (key.endsWith(id)) {
					delete root.datas.binding.onPost[key];
				}
			});
		} else {
			delete root.datas.binding.onPost[key];
		}
		return this;
	};
	return obj;
};
var VueRender = function(h, thisObj) {
	var obj = {};
	qdpMap(thisObj, function(value, key) {
		obj[key] = value;
	});
	obj._thisObj = thisObj;
	obj.h = h, obj._parent = null, obj.args = [ null, {}, [] ], obj._condition = true;
	obj._dataObj = obj.args[1], obj._child = obj.args[2];
	obj._dataObj.slot = null, obj._dataObj.key = null, obj._dataObj.ref = null;
	obj._dataObj.style = {}, obj._dataObj['class'] = {}, obj._dataObj.attrs = {}, obj._dataObj.props = {}, obj._dataObj.domProps = {};
	obj._dataObj.on = {}, obj._dataObj.nativeOn = {}, obj._dataObj.scopedSlots = {};
	obj.vnode = function(value) {
		obj.args[0] = value;
		return this;
	};
	var setValue = function(target) {
		return function(value) {
			if (qdpIsObject(value)) {
				qdpMerge(target, value);
			} else if (qdpIsString(value)) {
				if (arguments.length == 1) {
					target[value] = true;
				} else {
					var args = [ target ].concat(Array.prototype.slice.call(arguments, 0));
					qdpObj.apply(this, args);
				}
			}
			return this;
		}
	};
	obj.slot = function(value) {
		obj._dataObj.slot = value;
		return this;
	};
	obj.key = function(value) {
		obj._dataObj.key = value;
		return this;
	};
	obj.ref = function(value) {
		obj._dataObj.ref = value;
		return this;
	};
	obj.style = setValue(obj._dataObj.style);
	obj.cssClass = setValue(obj._dataObj['class']);
	obj.attrs = setValue(obj._dataObj.attrs);
	obj.props = setValue(obj._dataObj.props);
	obj.domProps = setValue(obj._dataObj.domProps);
	obj.on = setValue(obj._dataObj.on);
	obj.nativeOn = setValue(obj._dataObj.nativeOn);
	obj.scopedSlots = setValue(obj._dataObj.scopedSlots);
	obj.addChild = function(content) {
		if (content != null) {
			this._child.push(content);
		}
		return this;
	};
	obj.child = function(condition) {
		var c = VueRender(h);
		c._parent = this;
		c._condition = arguments.length > 0 ? arguments[0] : true;
		return c;
	};
	obj.func = function(func) {
		var root = this;
		func.apply(root, Array.prototype.slice.call(arguments, 1));
		return this;
	};
	obj.map = function(target, func) {
		var root = this;
		if (target == null || !qdpIsFunction(func)) {
			return root;
		}
		if (qdpIsArray(target)) {
			for (var i = 0; i < target.length; i++) {
				func.call(root, target[i], i, i);
			}
		} else if (typeof (target) === 'object') {
			var i = 0;
			for ( var prop in target) {
				func.call(root, target[prop], prop, i);
				i = i + 1;
			}
		} else if (typeof (target) == "string") {
			for (var i = 0; i < target.length; i++) {
				func.call(root, target.charAt(i), i, i);
			}
		} else if (typeof (target) == "number") {
			for (var i = 1; i < target; i++) {
				func.call(root, i, i, i);
			}
		}
		return root;
	};
	obj.parent = function() {
		var tmp = this._parent;
		this._parent = null;
		if (tmp && this._condition) {
			tmp._child.push(this.h.apply(this, this.args));
		}
		return tmp;
	};
	obj.any = function(func) {
		qdpIsFunction(func) && func.call(root);
		return this;
	};
	obj.end = function() {
		if (this._parent) {
			return this.parent().end();
		} else {
			return this.h.apply(this, this.args);
		}
	};
	return obj;
};

function vueEmpty(root, h, tag) {
	return VueRender(h).vnode(qdpIsString(tag) ? tag : 'span').addChild('\u3000').cssClass('qdp-empty').end();
}
function vueField(root, h, item, form, gridDataIndex) {
	//text|textarea|password|radio|checkbox|switch|select|date|time|cascader|number|rate|file|add|edit|delete|info|grid|tree|modalTee|modalGrid|modalSelect|label
	var fieldMap = {
		text : 'el-input',//
		textarea : 'el-input',//
		password : 'el-input',//
		hidden : 'el-input',//
		number : 'el-input-number',//
		radio : 'el-radio-group',//
		checkbox : 'el-checkbox-group',//
		checkbox2 : 'el-checkbox',//
		'switch' : 'el-switch',//
		select : 'el-select',//
		date : 'el-date-picker',//
		cascader : 'el-cascader',//
		rate : 'el-rate',//
		file : 'el-upload',//
		label : 'label',//
		modal : 'div',//
		'-' : 'el-input'//
	};
	var isGrid = form.type == 'grid';
	if (isGrid) {
		!qdpIsObject(form.data.__data) && Vue.set(form.data, '__data', {});
	} else {
		!qdpIsObject(form.__data) && Vue.set(form, '__data', {});
	}
	var type = item.type, vueType = fieldMap[type], name = item.name;
	var data = isGrid ? form.data.rows[gridDataIndex] : (form.data || form);
	var sdata = isGrid ? form.data.__data : form.__data;
	var htmlType = [ 'text', 'textarea', 'password', 'hidden' ].indexOf(type) > -1 ? type : '';
	var on = qdpObj({}, 'input', function(value) {
		Vue.set(data, name, value);
	});
	var onInput = function(value) {
		Vue.set(data, name, value);
	};
	var formTypeMap = qdpObj({}, 'search', 'small', 'grid', 'mini');
	var props = qdpObj({}, 'size', formTypeMap[item.formType || '-'] || (item.size || ''));
	htmlType && qdpObj(props, 'type', htmlType);
	if ('number' == type) {
		qdpObj(props, 'step', Number(item.step) || 1);
		Number(item.min) && (props.min = Number(item.min));
		Number(item.max) && (props.max = Number(item.max));
	}
	var formField = null;
	if (htmlType) {
		typeof (data[name]) != 'string' && Vue.set(data, name, "");
		formField = VueRender(h).vnode(vueType).props(props).props('value', data[name]).on('input', onInput).end();
	}//
	else if ('number' == type) {
		typeof (data[name]) != 'number' && Vue.set(data, name, 0);
		formField = VueRender(h).vnode(vueType).props(props).props('value', data[name]).on('input', onInput).end();
	}//
	else if ('checkbox2' == type && qdpIsObject(item.data)) {
		// el-checkbox
		var keys = qdpProps(item.data);
		Vue.set(sdata, name, data[name] == keys[1]);
		var value = sdata[name] ? item.data[keys[1]] : item.data[keys[0]];
		formField = VueRender(h).vnode(vueType).props(props).props('value', sdata[name]).on('input', function(value) {
			Vue.set(sdata, name, value);
			Vue.set(data, name, value ? keys[1] : keys[0]);
		}).addChild(value).end();
	}//
	else if ('radio' == type && qdpIsObject(item.data)) {
		// el-radio-group --- el-radio
		formField = VueRender(h).vnode(vueType).props(props).props('value', data[name]).on('input', onInput).map(item.data, function(value, key, index) {
			return this.child().vnode('el-radio').props('label', key).addChild(value).parent('to-el-radio-group');
		}).end();
	}//
	else if ('checkbox' == type && qdpIsObject(item.data)) {
		// el-checkbox-group --- el-checkbox
		formField = VueRender(h).vnode(vueType).props(props).props('value', data[name]).on('input', onInput).map(item.data, function(value, key, index) {
			return this.child().vnode('el-checkbox').props('label', key).addChild(value).parent('to-el-checkbox-group');
		}).end();
	}//
	else if ('switch' == type && qdpIsObject(item.data)) {
		// <el-switch v-model="value1" on-text="" off-text=""></el-switch>
		var keys = qdpProps(item.data);
		typeof (data[name]) != 'boolean' && Vue.set(data, name, data[name] == keys[1]);
		var onText = item.data[keys[1]], offText = item.data[keys[0]];
		formField = VueRender(h).vnode(vueType).props(props).props('value', data[name], 'off-text', offText, 'on-text', onText).on('input', onInput).addChild(
				value).end();
	}//
	else if ('select' == type && qdpIsObject(item.data)) {
		//  <el-select v-model="value5" multiple placeholder="Select"> <el-option v-for="item in options":key="item.value":label="item.label":value="item.value"> </el-option>
		formField = VueRender(h).vnode(vueType).props(props).props('value', data[name], 'multiple', item.multiple === true).on('input', onInput).map(item.data,
				function(value, key, index) {
					return this.child().vnode('el-option').props('label', value, 'value', key).parent('to-el-select');
				}).end();
	}//
	else if ('date' == type) {
		//  <el-date-picker v-model="value3"type="week"format="Week WW"placeholder="Pick a week"> </el-date-picker>
		data[name] == null && Vue.set(data, name, '');
		formField = VueRender(h).vnode(vueType).props(props).props('value', data[name], 'type', item.subType || 'date', 'format', item.format || '').on(
				'input', onInput).end();
	}//
	else if ('cascader' == type && qdpIsArray(item.data)) {
		//  <el-cascader :options="options"v-model="selectedOptions" @change="handleChange"> </el-cascader>
		!qdpIsArray(data[name]) && Vue.set(data, name, []);
		formField = VueRender(h).vnode(vueType).props(props).props('value', data[name], 'options', item.data || [], 'expand-trigger', 'hover').on('input',
				onInput).end();
	}//
	else if ('rate' == type) {
		//  <el-rate v-model="value1"></el-rate>
		data[name] != null && !qdpIsNumber(data[name]) && Vue.set(data, name, Math.min(Math.max(0, parseInt(data[name]) || 0), 5) || null);
		formField = VueRender(h).vnode(vueType).props(props).props('value', data[name]).on('input', onInput).end();
	}//
	else if ('file' == type) {
		//  <el-upload class="upload-demo"drag action="https://jsonplaceholder.typicode.com/posts/":on-preview="handlePreview":on-remove="handleRemove":file-list="fileList"multiple> <i class="el-icon-upload"></i> <div class="el-upload__text">Drop file here or <em>click to upload</em></div> <div class="el-upload__tip" slot="tip">jpg/png files with a size less than 500kb</div> </el-upload>
		data[name] != null && !qdpIsNumber(data[name]) && Vue.set(data, name, Math.min(Math.max(0, parseInt(data[name]) || 0), 5) || null);
		formField = VueRender(h).vnode(vueType).cssClass('qdp-upload').props(props).props('value', data[name], 'action', item.action, 'drag',
				item.drag == true, 'on-preview', root.handlePreview, 'on-remove', root.handleRemove, 'file-list', [], 'multiple', item.multiple == true,
				'accept', item.accept || '').on('input', onInput).func(
				function() {
					if (item.drag == true) {
						this.child().vnode('i').cssClass('el-icon-upload').parent('to-upload');
						this.child().vnode('div').cssClass('el-upload__text').addChild('Drop file here or ').func(function() {
							this.child().vnode('em').addChild('click to upload').parent('to-div');
						}).parent('to-upload');
					} else {
						this.child().vnode('el-button').cssClass('el-icon-upload').props('size', 'small', 'type', 'primary').addChild('Click to upload')
								.parent('to-upload');
					}
					this.child().vnode('div').cssClass('el-upload__ti').slot('tip').addChild(
							(item.accept || 'jpg/png') + ' files with a size less than ' + (item.size || '500kb')).parent('to-upload');
				}).end();
	}//
	else if ('label' == type) {
		//  <label></label>
		var value = data[name];
		formField = VueRender(h).vnode(vueType).addChild(value == null ? '' : (qdpIsObject(item.data) ? item.data[value] || value : value)).end();
	}//
	else if ('modal' == type) {
		//不要初始化multiSelection
		!qdpIsObject(sdata[name]) && Vue.set(sdata, name, {});
		formField = VueRender(h).vnode('el-row').props('type', 'flex', 'justify', 'start', 'align', 'middle').func(function() {
			var multi = sdata[name].multiSelection || [], one = sdata[name].oneSelection;
			var labelField = item.labelField || name, idField = form.idField || 'id';
			var label = multi.length == 1 ? qdpTitle(item, multi[0]) : (data[labelField] || item.label || name);
			label = multi.length < 1 && one ? qdpTitle(item, one) : (label || (data[labelField] || item.label || name));
			multi.length > 1 && this.addChild(vueSelectionPanel(root, h, sdata[name]));
			this.child().vnode('el-button').props('type', 'primary', 'size', props.size).addChild(label).on('click', function() {
				if (root[name]) {
					Vue['delete'](root.components.items, name);
					Vue['delete'](sdata[name], 'id');
					qdpMerge(sdata[name], qdpJsonCopy(root[name]));
					Vue.set(root.components.items, name, sdata[name]);
				} else {
					var modalName = 'item:form:modal-form-field-' + name;
					root.onPost(modalName, item, form, name);
				}
			}).parent('to-el-row');
		}).end();
	}
	var formItem = VueRender(h).vnode('el-row').props('type', 'flex', 'justify', 'start', 'align', 'middle').func(
			function() {
				this.child().vnode('el-form-item').cssClass('el-form-item-nolabel').props('prop', name, 'rules', item.rules || [], 'label', ' ', 'label-width',
						item.labelWidth || '12px').addChild(formField).parent();
			}).end();
	if (item.showLabel === false) {
		return 'label' == type ? formField : formItem;
	} else {
		var label = VueRender(h).vnode('el-row').cssClass('el-form-item').props('type', 'flex', 'justify', 'end', 'align', 'middle').func(function() {
			this.child().vnode('label').cssClass('el-form-item__label').addChild(item.label || name).parent();
		}).end();
		var withLabel = VueRender(h).vnode('el-row').func(function() {
			this.child().vnode('table').cssClass('el-form-item--table').func(function() {
				this.child().vnode('tr').func(function() {
					this.child().vnode('td').cssClass('el-form-item--title').addChild(label).parent('to-tr');
					this.child().vnode('td').cssClass('el-form-item--content').addChild(formItem).parent('to-tr');
				}).parent('to-table')
			}).parent('to-el-row')
		}).end();
		return withLabel;
	}
}
function vueForm(root, h, form) {
	!qdpIsObject(form.data) && Vue.set(form, 'data', {});
	var firstRender = false;
	!qdpIsString(form.id) && Vue.set(form, 'id', qdpRandom()) && (firstRender = true);
	return VueRender(h).vnode('el-form').cssClass('qdp-dynamic-form').props('model', form.data).ref('myform-' + form.id).nativeOn('keydown', function(event) {
		if (event.keyCode !== 13) {
			return;
		}
		LOG(qdpToString(form.data));
		event.stopPropagation(); // 阻止事件冒泡
		event.preventDefault();// 阻止该元素默认的keyup事件
	}).func(function() {
		this.child().vnode('el-row').map(form.items, function(value, key, index) {
			//!root.$slots['default']
			this.addChild(vueField(root, h, value, form));
		}).parent('to-el-form');
	}).func(
			function() {
				this.child().vnode('el-row').props('type', 'flex', 'justify', 'end', 'align', 'middle').addChild(root.$slots['button']).func(
						function() {
							this.child().vnode('el-button').ref('myform--btn-ok-' + form.id).style(form.modal ? 'display' : 'test', 'none').props('type',
									'primary', 'loading', form.loading).addChild('确定').on(
									'click',
									function(event) {
										var submitAction = function() {
											root.$confirm('提交表单', '提示', {
												confirmButtonText : '确定', cancelButtonText : '取消', type : 'warning'
											}).then(
													function() {
														var formData = qdpJsonCopy(form.data);
														var successCallback = event.successCallback;
														var url = form.url, query = form.query;
														url = qdpFormatUrl(ctx + url, query);
														LOG("FN: Form.form-submit-form.url=" + url);
														root.$http.post(url, formData).then(
																function(data) {
																	if (data.body && data.body.result == "0") {
																		root.$message({
																			type : 'success', message : data.body.data || '成功'
																		});
																	} else {
																		root.$notify({
																			title : 'Form Post=>Error',
																			message : url + '\n<br/>' + qdpToString(data.body.data), duration : 0,
																			type : 'error'
																		});
																	}
																	typeof (successCallback) == 'function' && successCallback.call(root);
																	typeof (finalCallback) == 'function' && finalCallback.call(root);
																}, function(response) {
																	(errorCallback || qdpHttpError).call(root, response);
																	typeof (finalCallback) == 'function' && finalCallback.call(root);
																});
													})['catch'](function() {
											});
										}
										root.$refs['myform-' + form.id].validate(function(valid) {
											valid && submitAction();
										});
									}).parent('to-el-row');
						}).parent('to-el-form');
			}).end();
}
function vueSearchForm(root, h, form) {
	!qdpIsObject(form.data) && Vue.set(form, 'data', {});
	var firstRender = false;
	!qdpIsString(form.id) && Vue.set(form, 'id', qdpRandom()) && (firstRender = true);
	var size = qdpLength(form.items), span = parseInt(20 / size), spanBtn = 24 - span * size;
	return VueRender(h).vnode('el-form').cssClass('qdp-search-form').props('model', form.data).ref('mysearchform-' + form.id).nativeOn('keydown',
			function(event) {
				if (event.keyCode !== 13) {
					return;
				}
				LOG(qdpToString(form.data));
				event.stopPropagation(); // 阻止事件冒泡
				event.preventDefault();// 阻止该元素默认的keyup事件
			}).func(function() {
		this.child().vnode('el-row').props('type', 'flex', 'justify', 'center', 'align', 'middle').map(form.items, function(value, key, index) {
			Vue.set(value, 'formType', 'search');
			this.child().vnode('el-col').props('span', span).addChild(vueField(root, h, value, form)).parent('to-el-row');
		}).func(function() {
			this.child().vnode('el-col').props('span', spanBtn).func(function() {
				this.child().vnode('el-row').props('type', 'flex', 'justify', 'end', 'align', 'middle').func(function() {
					this.child().vnode('el-button').props('size', 'small', 'native-type', 'reset').domProps('title', '重置').on('click', function() {
						root.$refs['mysearchform-' + form.id].resetFields();
						var target = qdpByPath(root, form.target);
						if (target && target.type == 'grid') {
							root.onPost('grid-action-' + target.id, 'search', form);
						}
					}).func(function() {
						this.child().vnode('i').cssClass('el-icon-ion-backspace-outline').parent('to-el-button');
					}).parent('to-el-row');
					this.child().vnode('el-button').props('size', 'small').domProps('title', '查询').on('click', function() {
						var target = qdpByPath(root, form.target);
						if (target && target.type == 'grid') {
							root.onPost('grid-action-' + target.id, 'search', form);
						}
					}).func(function() {
						this.child().vnode('i').cssClass('el-icon-ion-search').parent('to-el-button');
					}).parent('to-el-row');
				}).parent('to-el-col');
			}).parent('to-el-row');
		}).parent('to-el-form');
	}).end();
}

function vueTableItem(root, h, items, form, editableMap, colNum, gridDataIndex) {
	colNum = qdpIsNumber(colNum) ? Math.max(1, colNum) : 1;
	var group = [], tmp = Array(colNum).fill({}), counter = 0;
	qdpMap(items, function(value, key) {
		tmp[counter] = value, counter = counter + 1;
		if (counter == colNum) {
			group.push(tmp), tmp = Array(colNum).fill({}), counter = 0;
		}
	});
	var isGrid = form.type == 'grid';
	var data = isGrid ? form.data.rows[gridDataIndex] : (form.data || form);
	return VueRender(h).vnode('table').cssClass('el-table__body', true, 'qdp-table-item', true).domProps('cellspacing', '0', 'cellpadding', '0', 'border', '0')
			.func(function() {
				this.child().vnode('tbody').map(group, function(itemArr, key0, index0) {
					this.child().vnode('tr').cssClass('el-table__row').map(itemArr, function(value, key, index) {
						this.child().vnode('td').cssClass('el-form-item--title').func(function() {
							this.child().vnode('div').cssClass('cell').addChild(qdpLabel(form, value)).parent('td');
						}).parent('to-tr');
						this.child().vnode('td').cssClass('el-form-item--content').func(function() {
							var content = editableMap[value.name] ? vueField(root, h, value, form, gridDataIndex) : data[value.name];
							this.child().vnode('div').cssClass('cell').addChild(content).parent('td');
						}).parent('to-tr');
					}).parent('to-tbody');
				}).parent('to-table');
			}).end();
}

function vueGrid(root, h, grid) {
	//初始化Data
	!qdpIsArray(grid.items) && Vue.set(grid, 'items', []);
	!qdpIsObject(grid.data) && Vue.set(grid, 'data', {});
	!qdpIsNumber(grid.editRow) && Vue.set(grid, 'editRow', -1);
	var firstRender = false;
	!qdpIsString(grid.id) && Vue.set(grid, 'id', qdpRandom()) && (firstRender = true);
	var itemMap = {}, expandMap = {}, gridItemMap = {}, items = grid.items || [], data = grid.data.rows || [];
	qdpMap(grid.items, function(value) {
		itemMap[value.name] = value;
		value.scope.indexOf('grid') > -1 && (value.expand == true || value.scope.indexOf('expand') > -1) && (expandMap[value.name] = value);
		value.scope.indexOf('grid') > -1 && value.scope.indexOf('expand') < 0 && (gridItemMap[value.name] = value);
		Vue.set(value, 'showLabel', false), Vue.set(value, 'formType', 'grid');
	});
	var isSelection = grid.mode == 'selection';
	var renderGrid = VueRender(h).vnode('div').cssClass('qdp-grid')//
	.child(isSelection && grid.multiSelect !== false).vnode('el-row').addChild(vueSelectionPanel(root, h, grid)).parent('to-el-row')//
	.child().vnode('el-row').child().vnode(isSelection ? 'div' : 'el-form').props('model', data[grid.editRow] || {}).ref('mygridform-' + grid.id)//
	.child().vnode('el-table').cssClass('el-table--custom').style('width', '100%').props('border', true, 'data', data, 'highlight-current-row', true).ref(
			'mygrid-' + grid.id)//
	//============el-table-column开始
	.child(grid.multiSelect !== false).vnode('el-table-column').props('type', 'selection', 'width', '30').parent()//el-table
	.map(
			gridItemMap,
			function(value, key, index) {
				if (value.expand === true) {
					return;
				}
				var column = this.child().vnode('el-table-column').props('fixed', value.fixed === true, 'prop', value.name, 'label', value.label, 'width',
						value.width || '', 'sortable', value.sortable == true);
				column.scopedSlots(
						'default',
						function(scope) {
							var labelField = value.labelField || value.name;
							var showValue = value.data && value.data[scope.row[labelField] || ''] ? value.data[scope.row[labelField] || '']
									: scope.row[labelField];
							return !isSelection && value.editable !== false && scope.$index == grid.editRow ? [ vueField(root, h, value, grid, scope.$index) ]
									: [ VueRender(h).vnode('span').addChild(showValue).end() ];
						}).parent();
			})//
	//-----------列表展开
	.child(qdpLength(expandMap) > 0).vnode('el-table-column').cssClass('qdp-expand-cell').props('type', 'expand').scopedSlots(
			'default',
			function(scope) {
				var editableMap = {};
				qdpMap(expandMap, function(value, key, index) {
					var idField = grid.idField || 'id', id = data[scope.$index][idField];
					var editable = !isSelection && value.editable !== false && scope.$index == grid.editRow;
					editable = editable && ((id && value.scope.indexOf('edit') > -1) || (!id && value.scope.indexOf('add') > -1));
					editableMap[value.name] = editable;
				});
				var slotDefault = [ vueTableItem(root, h, expandMap, grid, editableMap, 2, scope.$index) ];
				if (!isSelection && scope.$index == grid.editRow) {
					return slotDefault.concat(VueRender(h).vnode('el-row').child().vnode('el-button').props('type', 'primary', 'native-type', 'reset', 'size',
							'mini').addChild('重置').nativeOn('click', function() {
						root.$refs['mygridform-' + grid.id].resetFields();
						Vue.set(grid.data, '__data', {});
					}).parent('to-el-row').end());
				}
				return slotDefault;
			}).parent()//el-table-column:expand
	//-----------列表操作
	.child(!isSelection && qdpLength(grid.actionItems) > 0).vnode('el-table-column').props('label', 'OP', 'width', '' + (32 * qdpLength(grid.actionItems)),
			'class-name', 'el-table-column--op').scopedSlots(
			'default',
			function(scope) {
				return qdpMap(grid.actionItems, function(value) {
					var icon = (value.icon || 'el-icon-warning').substring('el-icon-'.length);
					return VueRender(h).vnode('el-button').style('width', '22px').domProps('title', value.title).props('size', 'mini', 'icon', icon).on(
							'click', function() {
								root.onPost('grid-action-' + grid.id, value.action, qdpObj({}, 'grid', grid, 'scope', scope));
							}).end();
				});
			}).parent()//el-table-column
	//============el-table-column结束
	//============事件开始
	.on('selection-change', function(rows) {
		if (isSelection && grid.multiSelect !== false) {
			var newArr = [], all = (grid.multiSelection || []).concat(rows || []), idName = grid.idName || 'id', keys = {};
			qdpMap(all, function(value, key, index) {
				var id = value[idName];
				if (id != null && keys[id] == null) {
					newArr.push(value), keys[id] = id;
				}
			});
			Vue.set(grid, 'multiSelection', newArr), root.$emit('on-post', 'vm:grid:multi-selection', root, grid, grid.multiSelection);
		} else {
			Vue.set(grid, 'multiSelection', rows), root.$emit('on-post', 'vm:grid:multi-selection', root, grid, grid.multiSelection);
		}
		if (grid.multiSelection && grid.multiSelection.length == 1 && grid.oneSelection != grid.multiSelection[0]) {
			Vue.set(grid, 'oneSelection', grid.multiSelection[0]), root.$emit('on-post', 'vm:grid:one-selection', root, grid, grid.oneSelection);
		}
	}, 'current-change', function(newRow, oldRow) {
		// @see row-click
	}, 'row-click', function(row, event, column) {
		if (grid.oneSelection == row) {
			root.$refs['mygrid-' + grid.id].setCurrentRow();
			grid.oneSelection = null;
		} else {
			Vue.set(grid, 'oneSelection', row);
			root.$refs['mygrid-' + grid.id].setCurrentRow(row);
		}
	}, 'sort-change', function(columnPropOrder) {
		var column = columnPropOrder.column, prop = columnPropOrder.prop, order = columnPropOrder.order;//[descending,ascending,null];
		var orderMap = qdpObj({}, 'descending', 'desc', 'ascending', 'asc', '-', '');
		root.onPost('grid-load-data-' + grid.id, qdpObj({}, 'orderBy', [ qdpObj({}, 'name', prop, 'order', orderMap[order || '-']) ]));
	})
	//============事件结束
	.parent()//el-row
	.child().vnode('el-row').props('type', 'flex', 'justify', 'end', 'align', 'middle')//
	//============分页开始
	.func(
			function() {
				var currentPage = grid.data.currentPage || 1, totalCount = grid.data.totalCount, pageSize = grid.data.pageSize
						|| (grid.query.page || {}).pageSize || 10;
				var layout = 'total, sizes, prev, pager, next, jumper';
				this.child().vnode('el-pagination').props('current-page', currentPage, 'total', totalCount, 'page-sizes', [ 5, 10, 20, 50, 100 ], 'page-size',
						pageSize, 'layout', layout).on('size-change', function(newPageSize) {
					//更新当前每页记录数
					root.onPost('grid-load-data-' + grid.id, qdpObj({}, 'pageSize', newPageSize));
				}, 'current-change', function(newCurrentPage) {
					//更新当前分页
					root.onPost('grid-load-data-' + grid.id, qdpObj({}, 'currentPage', newCurrentPage));
				}).parent('to-el-row');
			})
	//============分页结束
	.end();
	if (!firstRender) {
		return renderGrid;
	}
	//绑定加载数据事件
	VueBinding(root).P('grid-load-data-' + grid.id, function(eventType, params) {
		var query = grid.query, url = grid.url, params = params || {};
		query.page && params.orderBy && (query.page.orderBy = params.orderBy);
		query.page && qdpIsNumber(params.pageSize) && (query.page.pageSize = params.pageSize || 1) && (query.page.currentPage = 1);
		query.page && qdpIsNumber(params.currentPage) && (query.page.currentPage = params.currentPage);
		url = qdpFormatUrl(ctx + url, query);
		LOG("FN: Grid.grid-load-data.url=" + url);
		root.$http.get(url).then(function(data) {
			Vue.set(grid, 'data', data.body.body);
		}, qdpHttpError);
	});
	//绑定操作栏(ActionBar)事件
	!isSelection && VueBinding(root).P('grid-action-' + grid.id, function(eventType, actionType, arg0) {
		if (actionType == 'refresh') {
			root.onPost('grid-load-data-' + grid.id);
		}//
		else if (actionType == 'add') {
			Vue.set(grid, 'editRow', grid.data.rows.length);
			Vue.set(grid.data.rows, grid.data.rows.length, {});
		}//
		else if (actionType == 'edit') {
			var scope = arg0.scope, data = grid.data.rows[scope.$index], sdata = grid.data.__data, idField = grid.idField || 'id';
			var isSame = grid.editRow == scope.$index;
			if (!isSame) {
				root.$refs['mygridform-' + grid.id].resetFields(), Vue.set(grid.data, '__data', {}), Vue.set(grid, 'editRow', isSame ? -1 : scope.$index);
				return;
			}
			root.$confirm('提交表单', '提示', {
				confirmButtonText : '确定', cancelButtonText : '取消', type : 'warning'
			}).then(function() {
				var formData = qdpJsonCopy(data);
				qdpMap(sdata, function(value, key) {
					if (!qdpIsObject(value)) {
						return;
					}
					if (sdata.modal && sdata.modal.status == 'cancel') {
						return;
					}
					if (!(('multiSelection' in value) || ('oneSelection' in value))) {
						return;
					}
					var multi = value.multiSelection || [], one = value.oneSelection || {}, idMap = {};
					qdpMap(multi, function(value) {
						var id = value[idField];
						id && (idMap[id] = id);
					});
					var id = one[idField];
					id && (idMap[id] = id);
					data[key] = formData[key] = qdpProps(idMap, ',');
				});
				var successCallback = function() {
					root.onPost('grid-load-data-' + grid.id);
				};
				//submit form
				root.$refs['mygridform-' + grid.id].validate(function(valid) {
					valid && root.onPost('grid-submit-form-' + grid.id, formData, successCallback, qdpHttpError);
				});
			})['catch'](function() {
				var id = data[idField];
				!id && Vue['delete'](grid.data.rows, scope.$index);
				Vue.set(grid, 'editRow', isSame ? -1 : scope.$index);
			});
		}//
		else if (actionType == 'delete') {
			var scope = arg0.scope, data = grid.data.rows[scope.$index], sdata = grid.data.__data, idField = grid.idField || 'id';
			var successCallback = function() {
				root.onPost('grid-load-data-' + grid.id);
			};
			root.onPost('grid-delete-form-' + grid.id, [ data ], successCallback, qdpHttpError);
		}//
		else if (actionType == 'search') {
			var query = grid.query, vo = query.vo, hasCondition = false;
			vo && arg0 && arg0.data && qdpMap(arg0.data, function(value, key) {
				!qdpIsObject(value) && (hasCondition = true) && (vo[key] = value);
			});
			hasCondition && root.onPost('grid-load-data-' + grid.id);
		}//
		else {//把事件事广播一次
			var scope = arg0.scope, data = grid.data.rows[scope.$index], sdata = grid.data.__data, idField = grid.idField || 'id';
			var result = root.onPost.apply(root, [ actionType, grid ].concat(Array.prototype.slice.call(arguments, 2)));
			if (result === false) {
				return;
			}
			if (root[actionType]) {
				Vue['delete'](root.components.items, actionType);
				Vue.set(root[actionType], 'data', data);
				Vue.set(root[actionType].modal, 'show', true);
				Vue.set(root.components.items, actionType, root[actionType]);
			}
		}
	});
	//绑定提交表单事件
	!isSelection && VueBinding(root).P('grid-submit-form-' + grid.id, function(eventType, formData, successCallback, errorCallback, finalCallback) {
		var idField = grid.idField || 'id', id = formData[idField], url = id ? grid.editUrl : grid.addUrl, query = grid.query;
		url = qdpFormatUrl(ctx + url, query);
		LOG("FN: Grid.grid-submit-form.url=" + url);
		root.$http.post(url, formData).then(function(data) {
			if (data.body && data.body.result == "0") {
				root.$message({
					type : 'success', message : data.body.data || '成功'
				});
			} else {
				root.$notify({
					title : 'Form Post=>Error', message : url + '\n<br/>' + qdpToString(data.body.data), duration : 0, type : 'error'
				});
			}
			typeof (successCallback) == 'function' && successCallback.call(root);
			typeof (finalCallback) == 'function' && finalCallback.call(root);
		}, function(response) {
			(errorCallback || qdpHttpError).call(root, response);
			typeof (finalCallback) == 'function' && finalCallback.call(root);
		});
	});
	//绑定删除事件
	!isSelection && VueBinding(root).P('grid-delete-form-' + grid.id, function(eventType, formData, successCallback, errorCallback, finalCallback) {
		var idField = grid.idField || 'id', url = grid.deleteUrl, query = grid.query, selectedRows = qdpIsArray(formData) ? formData : [ formData ];
		url = qdpFormatUrl(ctx + url, query);
		LOG("FN: Grid.grid-delete-form.url=" + url);
		var ids = [], names = [];
		qdpMap(selectedRows, function(value) {
			ids.push(value[idField]), names.push(qdpTitle(grid, value));
		});
		root.$confirm('确定删除：' + names.join(","), '删除', {
			confirmButtonText : '确定', cancelButtonText : '取消', type : 'warning'
		}).then(function() {
			root.$http.post(url, {
				id : ids.join(',')
			}).then(function(data) {
				if (data.body && data.body.result == "0") {
					root.$message({
						type : 'success', message : data.body.data || '成功'
					});
				} else {
					root.$notify({
						title : 'Form Post=>Error', message : url + '\n<br/>' + qdpToString(data.body.data), duration : 0, type : 'error'
					});
				}
				typeof (successCallback) == 'function' && successCallback.call(root);
				typeof (finalCallback) == 'function' && finalCallback.call(root);
			}, function(response) {
				(errorCallback || qdpHttpError).call(root, response);
				typeof (finalCallback) == 'function' && finalCallback.call(root);
			});
		})['catch'](function() {
		});
	});
	grid.url && setTimeout(function() {
		root.onPost('grid-load-data-' + grid.id);
	}, 100);
	return renderGrid;
}

function vueTree(root, h, tree) {
	typeof (tree.checkbox) != 'boolean' && Vue.set(tree, 'checkbox', true);
	!qdpIsArray(tree.checked) && Vue.set(tree, 'checked', []);
	!qdpIsArray(tree.expanded) && Vue.set(tree, 'expanded', qdpJsonCopy(tree.checked));
	var firstRender = false;
	!qdpIsString(tree.id) && Vue.set(tree, 'id', qdpRandom()) && (firstRender = true);
	var renderTree = VueRender(h).vnode('el-tree').cssClass('qdp-tree').ref('mytree-' + tree.id).props('highlight-current', true, 'show-checkbox',
			tree.checkbox, 'node-key', tree.idField || 'id', 'data', tree.data, 'default-checked-keys', tree.checked, 'default-expanded-keys', tree.expanded,
			'props', qdpObj({}, 'label', tree.labelField || 'name', 'children', tree.childrenField || 'children'))
	//============emit事件开始
	.on('current-change', function(node) {
		!tree.checkbox && tree.oneSelection != node && Vue.set(tree, 'oneSelection', node);
	}, 'check-change', function(node, selected, leafSelected) {
		Vue.set(tree, 'multiSelection', root.$refs['mytree-' + tree.id].getCheckedNodes(tree.leafCheck || true));
	})
	//============emit事件结束
	.end();
	if (!firstRender || !tree.url) {
		return renderTree;
	}
	//绑定加载数据事件
	VueBinding(root).P(
			'tree-load-data-' + tree.id,
			function(eventType, params) {
				var query = tree.query, url = tree.url, params = params || {};
				url = qdpFormatUrl(ctx + url, query);
				LOG("FN: Tree.tree-load-data.url=" + url);
				root.$http.get(url).then(
						function(data) {
							var arr = data.body.body.rows || [], treeData = [];
							if (tree.groupFields) {
								treeData = qdpGroupByTree(tree.groupFields, arr, tree.labelField || "name", tree.idField || "id", tree.pidField || "parentId",
										tree.isExpand !== false, tree.checked || [], tree.disabled || []);
							} else {
								treeData = qdpTree(arr, tree.labelField || "name", tree.idField || "id", tree.pidField || "parentId", tree.isExpand !== false,
										tree.checked || [], tree.disabled || []);
							}
							Vue.set(tree, 'data', treeData);
						}, qdpHttpError);
			});
	setTimeout(function() {
		root.onPost('tree-load-data-' + tree.id);
	}, 100);
	return renderTree;
}

function vueTreeForm(root, h, tree) {
	var form = tree.form;
	!qdpIsObject(form.data) && Vue.set(form, 'data', {});
	return VueRender(h).vnode('el-form').cssClass('tree-form').props('model', form).ref('mytreeform').nativeOn('keydown', function(event) {
		if (event.keyCode !== 13) {
			return;
		}
		LOG(qdpToString(form.data));
		event.stopPropagation();// 阻止事件冒泡
		event.preventDefault();// 阻止该元素默认的keyup事件
	})//
	.child().vnode('el-row').props('type', 'flex', 'justify', 'start', 'align', 'top')//
	//============tree开始
	.child().vnode('el-col').cssClass('treeform-tree').props('span', 8).addChild(vueTree(root, h, tree)).parent('to-el-row')//
	//============tree结束
	//============form开始
	.child().vnode('el-col').cssClass('treeform-form').props('span', 16)//
	.child().vnode('el-row').addChild(root.$slots['default']).map(form.items, function(value, key, index) {
		!root.$slots['default'] && this.addChild(vueField(root, h, value, form));
	}).parent('to-el-col')//
	.child().vnode('el-row').props('type', 'flex', 'justify', 'end', 'align', 'middle').addChild(root.$slots['button'])//
	.child(root.$slots['button'] == null).vnode('el-button').props('type', 'primary', 'loading', form.loading).addChild('确定').nativeOn('click', function() {
		alert(qdpToString(form.data));
	}).parent('to-el-row').parent('to-el-col')
	//============form结束
	.parent('to-el-row').parent('to-el-form')//
	.end();
}

// el-icon-*
var defaultIconNames = [ "arrow-down", "arrow-left", "arrow-right", "arrow-up", "caret-bottom", "caret-left", "caret-right", "caret-top", "check",
		"circle-check", "circle-close", "circle-cross", "close", "upload", "d-arrow-left", "d-arrow-right", "d-caret", "date", "delete", "document", "edit",
		"information", "loading", "menu", "message", "minus", "more", "picture", "plus", "search", "setting", "share", "star-off", "star-on", "time",
		"warning", "delete2", "upload2", "view" ];
// el-icon-ion-*
var iconNames = [ "ionic", "arrow-up-a", "arrow-right-a", "arrow-down-a", "arrow-left-a", "arrow-up-b", "arrow-right-b", "arrow-down-b", "arrow-left-b",
		"arrow-up-c", "arrow-right-c", "arrow-down-c", "arrow-left-c", "arrow-return-right", "arrow-return-left", "arrow-swap", "arrow-shrink", "arrow-expand",
		"arrow-move", "arrow-resize", "chevron-up", "chevron-right", "chevron-down", "chevron-left", "navicon-round", "navicon", "drag", "log-in", "log-out",
		"checkmark-round", "checkmark", "checkmark-circled", "close-round", "close", "close-circled", "plus-round", "plus", "plus-circled", "minus-round",
		"minus", "minus-circled", "information", "information-circled", "help", "help-circled", "backspace-outline", "backspace", "help-buoy", "asterisk",
		"alert", "alert-circled", "refresh", "loop", "shuffle", "home", "search", "flag", "star", "heart", "heart-broken", "gear-a", "gear-b", "toggle-filled",
		"toggle", "settings", "wrench", "hammer", "edit", "trash-a", "trash-b", "document", "document-text", "clipboard", "scissors", "funnel", "bookmark",
		"email", "email-unread", "folder", "filing", "archive", "reply", "reply-all", "forward", "share", "paper-airplane", "link", "paperclip", "compose",
		"briefcase", "medkit", "at", "pound", "quote", "cloud", "upload", "more", "grid", "calendar", "clock", "compass", "pinpoint", "pin", "navigate",
		"location", "map", "lock-combination", "locked", "unlocked", "key", "arrow-graph-up-right", "arrow-graph-down-right", "arrow-graph-up-left",
		"arrow-graph-down-left", "stats-bars", "connection-bars", "pie-graph", "chatbubble", "chatbubble-working", "chatbubbles", "chatbox", "chatbox-working",
		"chatboxes", "person", "person-add", "person-stalker", "woman", "man", "female", "male", "transgender", "fork", "knife", "spoon", "soup-can-outline",
		"soup-can", "beer", "wineglass", "coffee", "icecream", "pizza", "power", "mouse", "battery-full", "battery-half", "battery-low", "battery-empty",
		"battery-charging", "wifi", "bluetooth", "calculator", "camera", "eye", "eye-disabled", "flash", "flash-off", "qr-scanner", "image", "images", "wand",
		"contrast", "aperture", "crop", "easel", "paintbrush", "paintbucket", "monitor", "laptop", "ipad", "iphone", "ipod", "printer", "usb", "outlet", "bug",
		"code", "code-working", "code-download", "fork-repo", "network", "pull-request", "merge", "xbox", "playstation", "steam", "closed-captioning",
		"videocamera", "film-marker", "disc", "headphone", "music-note", "radio-waves", "speakerphone", "mic-a", "mic-b", "mic-c", "volume-high",
		"volume-medium", "volume-low", "volume-mute", "levels", "play", "pause", "stop", "record", "skip-forward", "skip-backward", "eject", "bag", "card",
		"cash", "pricetag", "pricetags", "thumbsup", "thumbsdown", "happy-outline", "happy", "sad-outline", "sad", "bowtie", "tshirt-outline", "tshirt",
		"trophy", "podium", "ribbon-a", "ribbon-b", "university", "magnet", "beaker", "erlenmeyer-flask", "egg", "earth", "planet", "lightbulb", "cube",
		"leaf", "waterdrop", "flame", "fireball", "bonfire", "umbrella", "nuclear", "no-smoking", "thermometer", "speedometer", "model-s", "plane", "jet",
		"load-a", "load-b", "load-c", "load-d", "ios-ionic-outline", "ios-arrow-back", "ios-arrow-forward", "ios-arrow-up", "ios-arrow-right",
		"ios-arrow-down", "ios-arrow-left", "ios-arrow-thin-up", "ios-arrow-thin-right", "ios-arrow-thin-down", "ios-arrow-thin-left", "ios-circle-filled",
		"ios-circle-outline", "ios-checkmark-empty", "ios-checkmark-outline", "ios-checkmark", "ios-plus-empty", "ios-plus-outline", "ios-plus",
		"ios-close-empty", "ios-close-outline", "ios-close", "ios-minus-empty", "ios-minus-outline", "ios-minus", "ios-information-empty",
		"ios-information-outline", "ios-information", "ios-help-empty", "ios-help-outline", "ios-help", "ios-search", "ios-search-strong", "ios-star",
		"ios-star-half", "ios-star-outline", "ios-heart", "ios-heart-outline", "ios-more", "ios-more-outline", "ios-home", "ios-home-outline", "ios-cloud",
		"ios-cloud-outline", "ios-cloud-upload", "ios-cloud-upload-outline", "ios-cloud-download", "ios-cloud-download-outline", "ios-upload",
		"ios-upload-outline", "ios-download", "ios-download-outline", "ios-refresh", "ios-refresh-outline", "ios-refresh-empty", "ios-reload",
		"ios-loop-strong", "ios-loop", "ios-bookmarks", "ios-bookmarks-outline", "ios-book", "ios-book-outline", "ios-flag", "ios-flag-outline", "ios-glasses",
		"ios-glasses-outline", "ios-browsers", "ios-browsers-outline", "ios-at", "ios-at-outline", "ios-pricetag", "ios-pricetag-outline", "ios-pricetags",
		"ios-pricetags-outline", "ios-cart", "ios-cart-outline", "ios-chatboxes", "ios-chatboxes-outline", "ios-chatbubble", "ios-chatbubble-outline",
		"ios-cog", "ios-cog-outline", "ios-gear", "ios-gear-outline", "ios-settings", "ios-settings-strong", "ios-toggle", "ios-toggle-outline",
		"ios-analytics", "ios-analytics-outline", "ios-pie", "ios-pie-outline", "ios-pulse", "ios-pulse-strong", "ios-filing", "ios-filing-outline", "ios-box",
		"ios-box-outline", "ios-compose", "ios-compose-outline", "ios-trash", "ios-trash-outline", "ios-copy", "ios-copy-outline", "ios-email",
		"ios-email-outline", "ios-undo", "ios-undo-outline", "ios-redo", "ios-redo-outline", "ios-paperplane", "ios-paperplane-outline", "ios-folder",
		"ios-folder-outline", "ios-paper", "ios-paper-outline", "ios-list", "ios-list-outline", "ios-world", "ios-world-outline", "ios-alarm",
		"ios-alarm-outline", "ios-speedometer", "ios-speedometer-outline", "ios-stopwatch", "ios-stopwatch-outline", "ios-timer", "ios-timer-outline",
		"ios-clock", "ios-clock-outline", "ios-time", "ios-time-outline", "ios-calendar", "ios-calendar-outline", "ios-photos", "ios-photos-outline",
		"ios-albums", "ios-albums-outline", "ios-camera", "ios-camera-outline", "ios-reverse-camera", "ios-reverse-camera-outline", "ios-eye",
		"ios-eye-outline", "ios-bolt", "ios-bolt-outline", "ios-color-wand", "ios-color-wand-outline", "ios-color-filter", "ios-color-filter-outline",
		"ios-grid-view", "ios-grid-view-outline", "ios-crop-strong", "ios-crop", "ios-barcode", "ios-barcode-outline", "ios-briefcase",
		"ios-briefcase-outline", "ios-medkit", "ios-medkit-outline", "ios-medical", "ios-medical-outline", "ios-infinite", "ios-infinite-outline",
		"ios-calculator", "ios-calculator-outline", "ios-keypad", "ios-keypad-outline", "ios-telephone", "ios-telephone-outline", "ios-drag", "ios-location",
		"ios-location-outline", "ios-navigate", "ios-navigate-outline", "ios-locked", "ios-locked-outline", "ios-unlocked", "ios-unlocked-outline",
		"ios-monitor", "ios-monitor-outline", "ios-printer", "ios-printer-outline", "ios-game-controller-a", "ios-game-controller-a-outline",
		"ios-game-controller-b", "ios-game-controller-b-outline", "ios-americanfootball", "ios-americanfootball-outline", "ios-baseball",
		"ios-baseball-outline", "ios-basketball", "ios-basketball-outline", "ios-tennisball", "ios-tennisball-outline", "ios-football", "ios-football-outline",
		"ios-body", "ios-body-outline", "ios-person", "ios-person-outline", "ios-personadd", "ios-personadd-outline", "ios-people", "ios-people-outline",
		"ios-musical-notes", "ios-musical-note", "ios-bell", "ios-bell-outline", "ios-mic", "ios-mic-outline", "ios-mic-off", "ios-volume-high",
		"ios-volume-low", "ios-play", "ios-play-outline", "ios-pause", "ios-pause-outline", "ios-recording", "ios-recording-outline", "ios-fastforward",
		"ios-fastforward-outline", "ios-rewind", "ios-rewind-outline", "ios-skipbackward", "ios-skipbackward-outline", "ios-skipforward",
		"ios-skipforward-outline", "ios-shuffle-strong", "ios-shuffle", "ios-videocam", "ios-videocam-outline", "ios-film", "ios-film-outline", "ios-flask",
		"ios-flask-outline", "ios-lightbulb", "ios-lightbulb-outline", "ios-wineglass", "ios-wineglass-outline", "ios-pint", "ios-pint-outline",
		"ios-nutrition", "ios-nutrition-outline", "ios-flower", "ios-flower-outline", "ios-rose", "ios-rose-outline", "ios-paw", "ios-paw-outline",
		"ios-flame", "ios-flame-outline", "ios-sunny", "ios-sunny-outline", "ios-partlysunny", "ios-partlysunny-outline", "ios-cloudy", "ios-cloudy-outline",
		"ios-rainy", "ios-rainy-outline", "ios-thunderstorm", "ios-thunderstorm-outline", "ios-snowy", "ios-moon", "ios-moon-outline", "ios-cloudy-night",
		"ios-cloudy-night-outline", "android-arrow-up", "android-arrow-forward", "android-arrow-down", "android-arrow-back", "android-arrow-dropup",
		"android-arrow-dropup-circle", "android-arrow-dropright", "android-arrow-dropright-circle", "android-arrow-dropdown", "android-arrow-dropdown-circle",
		"android-arrow-dropleft", "android-arrow-dropleft-circle", "android-add", "android-add-circle", "android-remove", "android-remove-circle",
		"android-close", "android-cancel", "android-radio-button-off", "android-radio-button-on", "android-checkmark-circle", "android-checkbox-outline-blank",
		"android-checkbox-outline", "android-checkbox-blank", "android-checkbox", "android-done", "android-done-all", "android-menu",
		"android-more-horizontal", "android-more-vertical", "android-refresh", "android-sync", "android-wifi", "android-call", "android-apps",
		"android-settings", "android-options", "android-funnel", "android-search", "android-home", "android-cloud-outline", "android-cloud",
		"android-download", "android-upload", "android-cloud-done", "android-cloud-circle", "android-favorite-outline", "android-favorite",
		"android-star-outline", "android-star-half", "android-star", "android-calendar", "android-alarm-clock", "android-time", "android-stopwatch",
		"android-watch", "android-locate", "android-navigate", "android-pin", "android-compass", "android-map", "android-walk", "android-bicycle",
		"android-car", "android-bus", "android-subway", "android-train", "android-boat", "android-plane", "android-restaurant", "android-bar", "android-cart",
		"android-camera", "android-image", "android-film", "android-color-palette", "android-create", "android-mail", "android-drafts", "android-send",
		"android-archive", "android-delete", "android-attach", "android-share", "android-share-alt", "android-bookmark", "android-document",
		"android-clipboard", "android-list", "android-folder-open", "android-folder", "android-print", "android-open", "android-exit", "android-contract",
		"android-expand", "android-globe", "android-chat", "android-textsms", "android-hangout", "android-happy", "android-sad", "android-person",
		"android-people", "android-person-add", "android-contact", "android-contacts", "android-playstore", "android-lock", "android-unlock",
		"android-microphone", "android-microphone-off", "android-notifications-none", "android-notifications", "android-notifications-off",
		"android-volume-mute", "android-volume-down", "android-volume-up", "android-volume-off", "android-hand", "android-desktop", "android-laptop",
		"android-phone-portrait", "android-phone-landscape", "android-bulb", "android-sunny", "android-alert", "android-warning", "social-twitter",
		"social-twitter-outline", "social-facebook", "social-facebook-outline", "social-googleplus", "social-googleplus-outline", "social-google",
		"social-google-outline", "social-dribbble", "social-dribbble-outline", "social-octocat", "social-github", "social-github-outline", "social-instagram",
		"social-instagram-outline", "social-whatsapp", "social-whatsapp-outline", "social-snapchat", "social-snapchat-outline", "social-foursquare",
		"social-foursquare-outline", "social-pinterest", "social-pinterest-outline", "social-rss", "social-rss-outline", "social-tumblr",
		"social-tumblr-outline", "social-wordpress", "social-wordpress-outline", "social-reddit", "social-reddit-outline", "social-hackernews",
		"social-hackernews-outline", "social-designernews", "social-designernews-outline", "social-yahoo", "social-yahoo-outline", "social-buffer",
		"social-buffer-outline", "social-skype", "social-skype-outline", "social-linkedin", "social-linkedin-outline", "social-vimeo", "social-vimeo-outline",
		"social-twitch", "social-twitch-outline", "social-youtube", "social-youtube-outline", "social-dropbox", "social-dropbox-outline", "social-apple",
		"social-apple-outline", "social-android", "social-android-outline", "social-windows", "social-windows-outline", "social-html5", "social-html5-outline",
		"social-css3", "social-css3-outline", "social-javascript", "social-javascript-outline", "social-angular", "social-angular-outline", "social-nodejs",
		"social-sass", "social-python", "social-chrome", "social-chrome-outline", "social-codepen", "social-codepen-outline", "social-markdown", "social-tux",
		"social-freebsd-devil", "social-usd", "social-usd-outline", "social-bitcoin", "social-bitcoin-outline", "social-yen", "social-yen-outline",
		"social-euro", "social-euro-outline" ];
function vueIconPanel(root, h, iconPanel) {
	var iconMap = {}, classis = {};
	qdpMap(defaultIconNames, function(value, key, index) {// el-icon-*
		value.indexOf('-') > 0 && (classis[value.substring(0, value.indexOf('-'))] = '-');
		iconMap['el-icon-' + value] = '-';
	});
	qdpMap(iconNames, function(value, key, index) {// el-icon-ion-*
		value.indexOf('-') > 0 && (classis[value.substring(0, value.indexOf('-'))] = '-');
		iconMap['el-icon-ion-' + value] = '-';
	});
	typeof (iconPanel.search) != 'string' && Vue.set(iconPanel, 'search', '');
	typeof (iconPanel.classify) != 'string' && Vue.set(iconPanel, 'classify', '');
	typeof (iconPanel.oneSelection) != 'string' && Vue.set(iconPanel, 'oneSelection', '');
	return VueRender(h).vnode('div').cssClass('icon-panel')//
	.child().vnode('el-row').cssClass('icon-panel-search').props('type', 'flex', 'justify', 'center', 'align', 'middle').func(function() {
		this.child().vnode('el-col').props('span', 10).addChild('Icon Name: ').func(function() {
			this.child().vnode('el-input').props('value', iconPanel.search, 'size', 'small').on('input', function(value) {
				Vue.set(iconPanel, 'search', value);
			}).parent('to-el-col');
		}).parent('to-el-row');
		this.child().vnode('el-col').props('span', 10).addChild('Pack Name: ').func(function() {
			this.child().vnode('el-select').props('value', iconPanel.classify, 'size', 'small').map(classis, function(value, key, index) {
				index == 0 && this.child().vnode('el-option').props('label', 'All', 'value', '').parent('to-el-select');
				this.child().vnode('el-option').props('label', key, 'value', key).parent('to-el-select');
			}).on('input', function(value) {
				Vue.set(iconPanel, 'classify', value);
			}).parent('to-el-col');
		}).parent('to-el-row');
	}).parent('to-div')//
	.child().vnode('el-row').cssClass('icon-panel-list').func(function() {
		this.child().vnode('el-row').props('type', 'flex', 'justify', 'center', 'align', 'middle').func(function() {
			this.child().vnode('ul').map(iconMap, function(value, key, index) {
				var match = iconPanel.search ? key.indexOf(iconPanel.search) > -1 : true;
				match = match && (iconPanel.classify ? key.indexOf(iconPanel.classify) > -1 : true);
				match && this.child().vnode('li').on('click', function(event) {
					iconPanel.oneSelection == key ? (iconPanel.oneSelection = '') : (iconPanel.oneSelection = key);
					event.stopPropagation();// 阻止事件冒泡
					event.preventDefault();// 阻止该元素默认的keyup事件
				}).func(function() {
					this.child().vnode('div').cssClass(key == iconPanel.oneSelection ? 'on-selection' : 'no-selection').func(function() {
						this.child().vnode('i').cssClass(key).parent('to-div')//
						.child().vnode('br').parent('to-div')//
						.child().vnode('span').addChild(key).parent('to-div')//
					}).parent('to-li');
				}).parent('to-ul');
			}).parent('to-el-row')//
		}).parent('to-el-row')//
	}).parent('to-div')//
	.end();
}

function vueSelectionPanel(root, h, selection, tag) {
	!qdpIsArray(selection.multiSelection) && Vue.set(selection, 'multiSelection', []);
	if (selection.multiSelection.length < 1) {
		return VueRender(h).vnode('el-row').end();
	}
	return VueRender(h).vnode('div').cssClass('selection-panel').map(selection.multiSelection, function(value, key, index) {
		this.child().vnode('el-tag').props('closable', true, 'close-transition', false, 'type', 'gray').on('close', function(tag) {
			var pos = selection.multiSelection.indexOf(value);
			pos > -1 && Vue['delete'](selection.multiSelection, pos);
		}).addChild(qdpIsObject(value) ? qdpTitle(selection, value) : value).parent('to-el-row');
	})//
	.end();
}

function vueHMenu(root, h, hmenu) {
	typeof (hmenu.router) != 'boolean' && Vue.set(hmenu, 'router', false);
	var submenuFunc = function(value, key, index) {
		var index = hmenu.router ? qdpUrl(hmenu, value) : (value.id || key);
		var title = qdpTitle(hmenu, value);
		var icon = qdpIcon(hmenu, value);
		var submenu = qdpSubItem(hmenu, value);
		if ((qdpIsArray(submenu) || qdpIsObject(submenu)) && qdpLength(submenu) > 0) {
			this.child().vnode('el-submenu').props('index', value.id || key).func(function() {
				this.child().vnode('template').slot('title').func(function() {
					this.child().vnode('i').cssClass(icon || 'el-icon-ion-android-menu').parent('to-template');
				}).addChild(title).parent('to-el-submenu');
			}).map(submenu, submenuFunc).parent('to-el-menu');
		} else {
			this.child().vnode('el-menu-item').props('index', index).func(function() {
				this.child().vnode('i').cssClass(icon || 'el-icon-ion-android-menu').parent('to-el-menu-item');
			}).addChild(title).parent('to-el-menu');
		}
	};
	return VueRender(h).vnode('el-menu').cssClass('qdp-el-hmenu').props('default-active', hmenu.active, 'mode', 'horizontal', 'router', hmenu.router).map(
			hmenu.items, submenuFunc).on('select', function(index, indexPath) {
		root.onPost('hmenu:index:indexPath', hmenu, index, indexPath);
	}).end();
}

function vueVMenu(root, h, vmenu) {
	typeof (vmenu.router) != 'boolean' && Vue.set(vmenu, 'router', false);
	var submenuFunc = function(value, key, index) {
		var index = vmenu.router ? qdpUrl(vmenu, value) : (value.id || key);
		var title = qdpTitle(vmenu, value);
		var icon = qdpIcon(vmenu, value);
		var submenu = qdpSubItem(vmenu, value);
		if ((qdpIsArray(submenu) || qdpIsObject(submenu)) && qdpLength(submenu) > 0) {
			this.child().vnode('el-submenu').props('index', value.id || key).func(function() {
				this.child().vnode('template').slot('title').func(function() {
					this.child().vnode('i').cssClass(icon || 'el-icon-ion-android-menu').parent('to-template');
				}).addChild(title).parent('to-el-submenu');
			}).map(submenu, submenuFunc).parent('to-el-menu');
		} else {
			this.child().vnode('el-menu-item').props('index', index).func(function() {
				this.child().vnode('i').cssClass(icon || 'el-icon-ion-android-menu').parent('to-el-menu-item');
			}).addChild(title).parent('to-el-menu');
		}
	};
	return VueRender(h).vnode('el-menu').cssClass('qdp-el-vmenu').props('default-active', vmenu.active, 'router', vmenu.router).map(vmenu.items, submenuFunc)
			.on('select', function(index, indexPath) {
				root.onPost('vmenu:index:indexPath', vmenu, index, indexPath);
			}).end();
}

function vueActionBar(root, h, action) {
	return VueRender(h).vnode('el-row').cssClass('qdp-el-action').map(action.items, function(value, key, index) {
		var title = qdpTitle(action, value);
		var icon = qdpIcon(action, value);
		this.child().vnode('el-button').domProps('title', title).props('size', 'small').on('click', function() {
			var type = value.action, target = qdpByPath(root, action.target);
			if ("back" == type) {
				Router.go(-1);
			}
			// 刷新
			else if ("refresh" == type) {
				if (target && target.type == 'grid') {
					root.onPost('grid-action-' + target.id, type);
				}
			}
			//
			else {
				if (target && target.type == 'grid') {
					root.onPost('grid-action-' + target.id, type);
				}
			}
		}).func(function() {
			this.child(icon.length > 0).vnode('i').cssClass(icon).parent('el-button');
		}).parent('el-row');
	}).end();
}

function vueHeadBmenuFooterLayout(root, h, page) {
	return VueRender(h).vnode('div').cssClass('qdp-head-bmenu-footer-layout').func(
			function() {
				this.child().vnode('el-row').cssClass('qdp-header').props('type', 'flex', 'justify', 'start', 'align', 'top').func(
						function() {
							this.child().vnode('el-col').cssClass('qdp-header-start').props('span', 6).addChild(
									vueAny(root, h, page['qdp-header-start']) || vueEmpty(root, h)).parent('to-el-row');
							this.child().vnode('el-col').cssClass('qdp-header-center').props('span', 12).addChild(
									vueAny(root, h, page['qdp-header-center']) || vueEmpty(root, h)).parent('to-el-row');
							this.child().vnode('el-col').cssClass('qdp-header-end').props('span', 6).addChild(
									vueAny(root, h, page['qdp-header-end']) || vueEmpty(root, h)).parent('to-el-row');
						}).parent('to-div');
				this.child().vnode('el-row').cssClass('qdp-hmenu').props('type', 'flex', 'justify', 'start', 'align', 'top').func(
						function() {
							this.child().vnode('el-col').cssClass('qdp-hmenu-start').props('span', 4).addChild(
									vueAny(root, h, page['qdp-hmenu-start']) || vueEmpty(root, h)).parent('to-el-row');
							this.child().vnode('el-col').cssClass('qdp-hmenu-end').props('span', 20).addChild(
									vueAny(root, h, page['qdp-hmenu-end']) || vueEmpty(root, h)).parent('to-el-row');
						}).parent('to-div');
				this.child().vnode('el-row').cssClass('qdp-content').props('type', 'flex', 'justify', 'start', 'align', 'top').func(
						function() {
							this.child().vnode('el-col').cssClass('qdp-vmenu-start').addChild(vueAny(root, h, page['qdp-vmenu-start']) || vueEmpty(root, h))
									.props('span', 4).parent('to-el-row');
							this.child().vnode('el-col').cssClass('qdp-content-end').props('span', 20).func(function() {
								this.child().vnode('router-view').on('on-post', function() {
									root.onPost.apply(root, Array.prototype.slice.call(arguments, 0));
								}).parent('to-el-col');
							}).parent('to-el-row');
						}).parent('to-div');
				this.child().vnode('el-row').cssClass('qdp-footer').props('type', 'flex', 'justify', 'center', 'align', 'top').func(function() {
					this.child().vnode('div').addChild(vueAny(root, h, page['qdp-footer']) || vueEmpty(root, h)).parent('to-el-row');
				}).parent('to-div');
			}).end();
}
function vueHeadVmenuFooterLayout(root, h, page) {
	return VueRender(h).vnode('div').cssClass('qdp-head-vmenu-footer-layout').func(
			function() {
				this.child().vnode('el-row').cssClass('qdp-header').props('type', 'flex', 'justify', 'start', 'align', 'top').func(
						function() {
							this.child().vnode('el-col').cssClass('qdp-header-start').props('span', 6).addChild(
									vueAny(root, h, page['qdp-header-start']) || vueEmpty(root, h)).parent('to-el-row');
							this.child().vnode('el-col').cssClass('qdp-header-center').props('span', 12).addChild(
									vueAny(root, h, page['qdp-header-center']) || vueEmpty(root, h)).parent('to-el-row');
							this.child().vnode('el-col').cssClass('qdp-header-end').props('span', 6).addChild(
									vueAny(root, h, page['qdp-header-end']) || vueEmpty(root, h)).parent('to-el-row');
						}).parent('to-div');
				this.child().vnode('el-row').cssClass('qdp-content').props('type', 'flex', 'justify', 'start', 'align', 'top').func(
						function() {
							this.child().vnode('el-col').cssClass('qdp-vmenu-start').addChild(vueAny(root, h, page['qdp-vmenu-start']) || vueEmpty(root, h))
									.props('span', 4).parent('to-el-row');
							this.child().vnode('el-col').cssClass('qdp-content-end').props('span', 20).addChild(
									vueAny(root, h, page['qdp-content-end']) || vueEmpty(root, h)).parent('to-el-row');
						}).parent('to-div');
				this.child().vnode('el-row').cssClass('qdp-footer').props('type', 'flex', 'justify', 'center', 'align', 'top').func(function() {
					this.child().vnode('div').addChild(vueAny(root, h, page['qdp-footer']) || vueEmpty(root, h)).parent('to-el-row');
				}).parent('to-div');
			}).end();
}
function vueHeadHmenuFooterLayout(root, h, page) {
	return VueRender(h).vnode('div').cssClass('qdp-head-hmenu-footer-layout').func(
			function() {
				this.child().vnode('el-row').cssClass('qdp-header').props('type', 'flex', 'justify', 'start', 'align', 'top').func(
						function() {
							this.child().vnode('el-col').cssClass('qdp-header-start').props('span', 6).addChild(
									vueAny(root, h, page['qdp-header-start']) || vueEmpty(root, h)).parent('to-el-row');
							this.child().vnode('el-col').cssClass('qdp-header-center').props('span', 12).addChild(
									vueAny(root, h, page['qdp-header-center']) || vueEmpty(root, h)).parent('to-el-row');
							this.child().vnode('el-col').cssClass('qdp-header-end').props('span', 6).addChild(
									vueAny(root, h, page['qdp-header-end']) || vueEmpty(root, h)).parent('to-el-row');
						}).parent('to-div');
				this.child().vnode('el-row').cssClass('qdp-hmenu').props('type', 'flex', 'justify', 'start', 'align', 'top').func(
						function() {
							this.child().vnode('el-col').cssClass('qdp-hmenu-end').props('span', 24).addChild(
									vueAny(root, h, page['qdp-hmenu-end']) || vueEmpty(root, h)).parent('to-el-row');
						}).parent('to-div');
				this.child().vnode('el-row').cssClass('qdp-content').props('type', 'flex', 'justify', 'start', 'align', 'top').func(
						function() {
							this.child().vnode('el-col').cssClass('qdp-content-end').props('span', 24).addChild(
									vueAny(root, h, page['qdp-content-end']) || vueEmpty(root, h)).parent('to-el-row');
						}).parent('to-div');
				this.child().vnode('el-row').cssClass('qdp-footer').props('type', 'flex', 'justify', 'center', 'align', 'top').func(function() {
					this.child().vnode('div').addChild(vueAny(root, h, page['qdp-footer']) || vueEmpty(root, h)).parent('to-el-row');
				}).parent('to-div');
			}).end();
}
function vueListLayout(root, h, page) {
	return VueRender(h).vnode('div').cssClass('qdp-list-layout').map(
			page.items,
			function(value, key, index) {
				this.child().vnode('el-row').cssClass('qdp-list-content').props('type', 'flex', 'justify', value.justify || 'start', 'align', 'top').addChild(
						vueAny(root, h, page.items[key])).parent('to-div');
			}).end();
}
function vueComponent(root, h, comp) {
	return VueRender(h).vnode('div').cssClass('qdp-component').map(comp.items, function(value, key, index) {
		var any = vueAny(root, h, value);
		any && this.addChild(any);
	}).end();
}

function vueAny(root, h, model, parent) {
	var contentMap = qdpObj({}, 'form', vueForm, 'grid', vueGrid, 'tree', vueTree, 'treeform', vueTreeForm, 'searchform', vueSearchForm, 'iconpanel',
			vueIconPanel, 'selectionpanel', vueSelectionPanel, 'hmenu', vueHMenu, 'vmenu', vueVMenu, 'actionbar', vueActionBar, 'headbmenufooterlayout',
			vueHeadBmenuFooterLayout, 'headvmenufooterlayout', vueHeadVmenuFooterLayout, 'headhmenufooterlayout', vueHeadHmenuFooterLayout, 'listlayout',
			vueListLayout, 'component', vueComponent, 'empty', vueEmpty);
	model = model || {};
	var any = contentMap[model.type || '-'] || root[model.type || '-'];
	if (any) {
		var vnode = any.apply(root, Array.prototype.slice.call(arguments, 0));
		return qdpIsObject(model.modal) ? vueModal(root, h, model, vnode, parent) : vnode;
	}
	return '';
}

function vueModal(root, h, modal, content, parent) {
	var parentModel = null;
	if (qdpIsObject(modal.modal)) {
		parentModel = modal, modal = modal.modal;
	}
	if (!modal || modal.show !== true) {
		return VueRender(h).vnode('div').end()//;
	}
	typeof (modal.title) != 'string' && Vue.set(modal, 'title', 'Dialog');
	typeof (modal.show) != 'boolean' && Vue.set(modal, 'show', false);
	typeof (modal.size) != 'string' || [ 'tiny', 'small', 'large', 'full' ].indexOf(modal.size) < 0 && Vue.set(modal, 'size', 'small');
	typeof (modal.allowClose) != 'boolean' && Vue.set(modal, 'allowClose', true);
	return VueRender(h).vnode('el-dialog').props('title', modal.title || 'Dialog', 'visible', modal.show, 'size', modal.size, 'close-on-click-modal', false,
			'before-close', function(done) {
				modal.allowClose !== false && done();
			})
	//============emit事件开始
	.on('open', function() {
	}, 'close', function() {
	}, 'update:visible', function(value) {
		Vue.set(modal, 'show', value === true);
		Vue.set(modal, 'status', 'cancel');
		!value && parentModel && VueBinding(root)['-P']('*-' + parentModel.id || '-');
	}, 'visible-change', function(value) {
	})
	//============emit事件结束
	//============footer开始
	.child(modal.footer !== false).vnode('span').cssClass('dialog-footer').slot('footer')//
	.child().vnode('el-button').on('click', function() {
		Vue.set(modal, 'show', false);
		Vue.set(modal, 'status', 'cancel');
		parentModel && VueBinding(root)['-P']('*-' + parentModel.id || '-');
	}).addChild('Cancel').parent()//for el-button
	.child().vnode('el-button').props('type', 'primary').on('click', function(event) {
		Vue.set(modal, 'status', 'ok');
		if (parentModel && parentModel.type == 'form') {
			root.$refs['myform--btn-ok-' + parentModel.id].handleClick({
				successCallback : function() {
					Vue.set(modal, 'show', false);
					VueBinding(root)['-P']('*-' + parentModel.id || '-');
				}
			});
		} else {
			Vue.set(modal, 'show', false);
			VueBinding(root)['-P']('*-' + parentModel.id || '-');
		}
	}).addChild('Confirm').parent()//for el-button
	.parent()//for sapn
	.child(content == null).vnode('div').addChild(root.$slots['default'] || '').parent()//
	.child(content != null).vnode('div').addChild(qdpIsObject(content) && content.type ? vueAny.apply(root, [ root, h ].concat(content.data || [])) : content)
			.parent()//
			//============footer结束
			.end();
}
