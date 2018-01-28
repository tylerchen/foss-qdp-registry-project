//================vuex-router-sync.js=======================
function Sync(store, router, options) {
	var moduleName = (options || {}).moduleName || 'route'
	store.registerModule(moduleName, {
		state : cloneRoute(router.currentRoute), mutations : {
			'router/ROUTE_CHANGED' : function(state, transition) {
				store.state[moduleName] = cloneRoute(transition.to, transition.from)
			}
		}
	})

	var isTimeTraveling = false
	var currentPath

	// sync router on store change
	store.watch(function(state) {
		return state[moduleName]
	}, function(route) {
		if (route.fullPath === currentPath) {
			return;
		}
		isTimeTraveling = true
		currentPath = route.fullPath
		router.push(route)
	}, {
		sync : true
	})

	// sync store on router navigation
	router.afterEach(function(to, from) {
		if (isTimeTraveling) {
			isTimeTraveling = false
			return;
		}
		currentPath = to.fullPath
		store.commit('router/ROUTE_CHANGED', {
			to : to, from : from
		})
	})
}

function cloneRoute(to, from) {
	var clone = {
		name : to.name, path : to.path, hash : to.hash, query : to.query, params : to.params, fullPath : to.fullPath, meta : to.meta
	}
	if (from) {
		clone.from = cloneRoute(from)
	}
	return Object.freeze(clone)
}

// ================vuex-router-sync.js=======================

var getters = {};

var cache_state = {
	state : {}, mutations : {
		CHANGE_CACHE : function(state, data) {
			if (data) {
				for ( var prop in data) {
					data[prop] == null && (delete state[prop]);
					data[prop] != null && (state[prop] = data[prop]);
				}
			}
		}
	}, actions : {
		cache : function(store, data) {
			store.commit("CHANGE_CACHE", data);
		}
	}
};

getters.cache = function(key, value) {
	if(value){
		this.$store.dispatch('cache', qdpObj({}, key, value));
	}
	return this.$store.state.cache[key];
};

var form_state = {
	state : {
		form : {
			fields : [], data : {}
		}
	}, mutations : {
		FORM_FIELDS : function(state, fields) {
			Vue.set(state.form, 'fields', fields || []);
		}, FORM_DATA : function(state, data) {
			Vue.set(state.form, 'data', data || {});
		}, FORM_FIELDS_CUS : function(state, fields) {
			if (!(fields && fields.formName)) {
				return;
			}
			if (!fields.fields || fields.fields.length < 1) {
				Vue['delete'](state, fields.formName);
			}
			VUe.set(state, fields.formName, {
				fields : [], data : {}
			});
			Vue.set(state[fields.formName], 'fields', fields || []);
		}, FORM_DATA_CUS : function(state, data) {
			if (!(data && data.formName)) {
				return;
			}
			if (!data.data || data.data.length < 1) {
				Vue['delete'](state, data.formName);
			}
			VUe.set(state, data.formName, {
				fields : [], data : {}
			});
			Vue.set(state[data.formName], 'fields', data || []);
			Vue.set(state.form, 'data', data || {});
		}
	}, actions : {
		setFormFields : function(store, fields) {
			store.commit("FORM_FIELDS", fields);
		}, setFormData : function(store, data) {
			store.commit("FORM_DATA", fields);
		}
	}
}

getters.formFields = function() {
	return this.$store.state.form.fields;
};
getters.formData = function() {
	return this.$store.state.form.data;
};

var lang_state = {
	state : {
		chinese : true
	}, mutations : {
		CHANGE_LANG : function(state, selected) {
			state.chinese = selected == 1;
		}
	}, actions : {
		changeLang : function(store, selected) {
			store.commit("CHANGE_LANG", selected);
		}
	}, getters : {
		chinese : function(state) {
			return state.chinese;
		}
	}
}
var login_state = {
	state : {
		login : false, account : null
	}, mutations : {
		LOGIN : function(state) {
			state.login = true;
		}, LOGOUT : function(state) {
			state.login = false;
			state.account = "";
		}, ACCOUNT : function(state, account) {
			state.account = account || "";
		}
	}, actions : {
		login : function(store) {
			store.commit("LOGIN");
		}, logout : function(store) {
			store.commit("LOGOUT");
		}, setAccount : function(store, account) {
			store.commit("ACCOUNT", account);
		}
	}
};
getters.account = function() {
	return this.$store.state.login.account;
};
getters.accountId = function() {
	return (this.$store.state.login.account || {}).id || '';
};
getters.userId = function() {
	return (this.$store.state.login.account || {}).userId || '';
};
var menu_state = {
	state : {
		hmenu : "vGdRnsrvvq6Aik19SZ8", vmenu : ""
	}, mutations : {
		HMENU : function(state, menuId) {
			state.hmenu = menuId;
		}, VMENU : function(state, menuId) {
			state.vmenu = menuId;
		}
	}, actions : {
		setHMenu : function(store, menuId) {
			store.commit("HMENU", menuId);
		}, setVMenu : function(store, menuId) {
			store.commit("VMENU", menuId);
		}
	}
}
var page_state = {
	state : {
		models : {}
	}, mutations : {
		SET_MODEL : function(state, data) {
			data && qdpMerge(state.models, data), !data && (delete state.models[key]);
		}
	}, actions : {
		setModels : function(store, data) {
			store.commit("SET_MODEL", data);
		}
	}
}
