define('app/jsp/syssensitive/sysSensitiveList', function (require, exports, module) {
    'use strict';
    var $=require('jquery'),
    Widget = require('arale-widget/1.2.0/widget'),
    Dialog = require("optDialog/src/dialog"),
    Paging = require('paging/0.0.1/paging-debug'),
    AjaxController = require('opt-ajax/1.0.0/index');
    require("jsviews/jsrender.min");
    require("jsviews/jsviews.min");
    require("bootstrap-paginator/bootstrap-paginator.min");
    require("app/util/jsviews-ext");
    
    require("jquery-validation/1.15.1/jquery.validate");
	require("app/util/aiopt-validate-ext");
    
    require("opt-paging/aiopt.pagination");
    require("twbs-pagination/jquery.twbsPagination");
    require('bootstrap/js/modal');
    var SendMessageUtil = require("app/util/sendMessage");
    //实例化AJAX控制处理对象
    var ajaxController = new AjaxController();
    //定义页面组件类
    var OrderListPager = Widget.extend({
    	
    	Implements:SendMessageUtil,
    	//属性，使用时由类的构造函数传入
    	attrs: {
    	},
    	Statics: {
    		DEFAULT_PAGE_SIZE: 20
    	},
    	//事件代理
    	events: {
			//查询
            "click #search":"_searchSysSensitiveList",
            "click #add":"_add",
            "click #update":"_update",
            "click #deleteAll":"_deleteAll",
            "click #add-close":"_closeDialog",
            "click #colseImage":"_closeDialog"
        },
    	//重写父类
    	setup: function () {
    		OrderListPager.superclass.setup.call(this);
    		// 初始化执行搜索
    		this._searchSysSensitiveList();
    		var formValidator=this._initValidate();
			$(":input").bind("focusout",function(){
				formValidator.element(this);
			});
    	},
    	_add:function(){
			window.location.href = _base+"/syssensitive/toAddSysSensitive";
		},
		_deleteAll:function(){
			var id_array=new Array();  
			$('input[name="subSen"]:checked').each(function(){  
			    id_array.push($(this).val());//向数组中添加元素  
			});  
			var ids=id_array.join(',');//将数组元素连接起来以构建一个字符串  
			var _this=this;
			 ajaxController.ajax({
 				type: "post",
 				processing: true,
 				message: "删除数据中，请等待...",
 				data: {ids:ids},
 				url: _base + "/syssensitive/deleteSysSensitive",
 				success: function (rs) {
 					 window.location.reload();
 				}
 			});
		},
    	_searchSysSensitiveList:function(){
			var _this=this;
			var url = _base+"/syssensitive/getSysSensitivePageData";
			var queryData = this._getSearchParams();
			$("#pagination").runnerPagination({
				url:url,
				method: "POST",
				dataType: "json",
				messageId:"showMessage",
				renderId:"sysSensitiveListData",
				data : queryData,
				pageSize: OrderListPager.DEFAULT_PAGE_SIZE,
				visiblePages:5,
				message: "正在为您查询数据..",
				render: function (data) {
					if(data&&data.length>0){
						var template = $.templates("#sysSensitiveListTemple");
						var htmlOut = template.render(data);
						$("#sysSensitiveListData").html(htmlOut);
					}else{
						$("#sysSensitiveListData").html("未搜索到信息");
					}
				},
			});
		},
		_show:function(id,sensitiveWords,replaceWords,site,state){
			var _this= this;
    		$("#id").val("");
    		$("#updateSensitiveWords").val("");
    		$("#replaceWords").val("");
			//弹出框展示
			$('#eject-mask').fadeIn(100);
			$('#add-samll').slideDown(200);
			
    		$("#updateSensitiveWords").val(sensitiveWords);
    		$("#replaceWords").val(replaceWords);
    		$("#id").val(id);
    		
    		var states = document.getElementsByName("state");
    		for(var i=0;i<states.length;i++){
    			if(state == states[i].value){
    				states[i].checked = true;
    			}
    		}
    		var sites = document.getElementsByName("site");
    		for(var i=0;i<sites.length;i++){
    			if(site == sites[i].value){
    				sites[i].checked = true;
    			}
    		}
		},
		_update:function(){
			var _this = this;
			var formValidator=_this._initValidate();
			formValidator.form();
			if(!$("#dataForm").valid()){
				return false;
			}
			var param = $("#dataForm").serializeArray();
			var url = _base + "/syssensitive/updateSysSensitive";
			ajaxController.ajax({
				type: "post",
				dataType:"json",
				processing: true,
				message: "保存数据中，请等待...",
				url: url,
				data: param,
				success: function (rs) {
					window.location.reload();
				}
			});
		},
		_getSearchParams:function(){
    		return {
    			"sensitiveWords":jQuery.trim($("#sensitiveWords").val()),
    			"site":jQuery.trim($("#site option:selected").val()),
    			"state":jQuery.trim($("#state option:selected").val()),
    			"creatPeople":jQuery.trim($("#creatPeople").val()),
    		}
    	},
		_initValidate:function(){
    		var formValidator=$("#dataForm").validate({
    			rules: {
    			},
    			messages: {
    			}
    		});
    		return formValidator;
    	},
		_closeDialog:function(){
    		$("#errorMessage").html("");
    		$('#eject-mask').fadeOut(100);
    		$('#add-samll').slideUp(150);
    	}
    });
    
    module.exports = OrderListPager
});
