;
'use strict';
/**
 * setting application context.
 */
var ctx = window['ctx'] || "";
/**
 * define the require configure.
 */
var requireConfig = {
	baseUrl : (ctx.length == 1 ? '' : ctx) + '/resource/modules', urlArgs : '_v=1.0.3', paths : {
		pages : "../../resource/pages", coms : "../../resource/pages/components",
		/**
		 * define require plugin.
		 */
		text : 'requirejs/text', css : 'requirejs/css', html : 'requirejs/html',
		/**
		 * common js
		 */
		qdp : "elementui/qdp", //
		version : "elementui/version", //
		encrypt : "encrypt/jsencrypt.min",
		/**
		 * define vue
		 */
		vue : "elementui/vue", //
		vuerouter : "elementui/vue-router", //
		vueresource : "elementui/vue-resource.min", //
		vuex : "elementui/vuex",//
		vuexsync : "elementui/vuex-router-sync", //
		vuerender : "elementui/vue-render", //
		vuel : "elementui/require-vuejs", //
		globalstate : "elementui/global-state", //
		ELEMENT : "elementui/elementui",//
		elementuicss : "elementui/elementui", //
		customcss : "elementui/custom", //
		iconcss : "elementui/elementui-ionicons"
	}, shim : {
		qdp : {
			exports : "qdp"
		}, vuerender : {
			deps : [ "qdp" ], exports : "vuerender"
		}, vue : {
			exports : "vue"
		}, vuerouter : {
			deps : [ "vue" ], exports : "vuerouter"
		}, vueresource : {
			deps : [ "vue" ], exports : "vueresource"
		}, vuex : {
			deps : [ "vue" ], exports : "vuex"
		}, vuexsync : {
			deps : [ "vue", "vuerouter" ], exports : "vuexsync"
		}, ELEMENT : {
			deps : [ "vue", "css!elementuicss", "css!customcss", "css!iconcss" ]
		}
	}, packages : [ {
		name : "codemirror", location : "codemirror", main : "lib/codemirror"
	} ]
};
/**
 * use the configuration.
 */
requirejs.config(requireConfig);
/**
 * loading the app.js file undering the html page directory.
 */
require([ 'vue', "ELEMENT", 'vuerouter', "vueresource", "vuex", "qdp", "globalstate", "version" ], function(Vue, ELEMENT, VueRouter, VueResource, Vuex) {
	//Vue.config.debug = true
	//Vue.config.devtools = true
	/**@see http://www.tuicool.com/articles/jIRrAfI**/
	Vue.use(VueRouter);
	Vue.use(VueResource);
	Vue.use(Vuex);
	Vue.use(ELEMENT);
	Vue.http.options.emulateJSON = true;
	// 配置自定义过滤器
	Vue.filter('localDateString', function(value) {
		return new Date(value).toLocaleString();
	});
	// 配置vue-resource拦截器
	Vue.http.interceptors.push(function(request, next) {
		// modify request
		// continue to next interceptor
		next(function(response) {
			// modify response
			if (response.status == 401) {
				var lastPath = sessionStorage.getItem('lastPath');
				sessionStorage.clear();
				sessionStorage.setItem('lastPath', lastPath || '/home');
				//Router.push('/login');
				var url = window.location.href, index = url.indexOf("#");
				window.location.href = url.substring(0, index < 0 ? url.length : index);
			}
		});
	});
	// 配置全局状态
	var store = new Vuex.Store({
		modules : {
			cache : cache_state, lang : lang_state, login : login_state, menu : menu_state
		}, strict : true
	});
	// 动态生成路由
	var RouterHelper = function(name) {
		var args = Array.prototype.slice.call(arguments, 1), children = [];
		for (var i = 0; i < args.length; i++) {
			children.push(RouterHelper(args[i]));
		}
		var route = {
			//name : (name.charAt(0) == '/' ? name.substring(1) : name).replace(/\//g, "-"),/* convert a/b/c to a-b-c */
			path : name, component : function(resolve) {
				//require([ "pages/" + name ], resolve);
				require([ "vuel!pages" + (name.charAt(0) == '/' ? '' : '/') + name + ".html" ], resolve);
			}
		};
		children.length > 0 && (route.children = children);
		return route;
	};
	// 配置路由
	var routes = [ RouterHelper("/login"), RouterHelper("/home", "account/list") ];
	var hasRoute = {};
	var router = new VueRouter({
		mode : 'hash', routes : routes
	});
	// 
	router.beforeEach(function(to, from, next) {
		var meta = to.meta, path = to.path, last = parseInt(sessionStorage.getItem("last") || '0'), now = new Date().getTime();
		isTimeout = (now - last) > (30 * 60 * 1000);
		to.hash = to.hash || path;

		if (path != '/login' && path != '/home' && path != '/') {
			sessionStorage.setItem('lastPath', path || '/home');
		}

		if (!isTimeout) {
			sessionStorage.setItem("last", now);
		}
		if (!isTimeout && store.state.login.login != true && sessionStorage.getItem("account")) {
			store.dispatch('login');
			store.dispatch('setAccount', eval('(' + sessionStorage.getItem("account") + ')'));
		}
		if (isTimeout || store.state.login.login != true || !sessionStorage.getItem("account")) {
			var lastPath = sessionStorage.getItem('lastPath');
			sessionStorage.clear();
			sessionStorage.setItem('lastPath', lastPath || '/home');
			if (path != '/login') {
				to = {
					path : '/login'
				};
				return next(to);
			}
		} else if (path == '/') {
			to = {
				path : '/home'
			};
			return next(to);
		} else if (path == '/login') {
			to = {
				path : '/home'
			};
			return next(to);
		} else if (path.indexOf("/home/") == 0 && hasRoute[path] == null) {
			Router.addRoutes([ RouterHelper("/home", path.substring("/home/".length)) ]);
			hasRoute[path] = 1;
			return next(to);
		}
		next();
	});

	//Sync(store, router)

	window.Router = router;
	window.Bus = new Vue();
	window.Vue = Vue;
	new Vue({
		store : store, router : router
	}).$mount('#app');
});
