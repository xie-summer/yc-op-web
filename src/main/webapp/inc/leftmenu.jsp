<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<script type="text/javascript">
	var pager;
	(function () {
		seajs.use('app/inc/leftmenu', function (LeftMenuPager) {
			pager = new LeftMenuPager({element: document.body});
			pager.render();
		});
	})();
</script>
<!--左侧菜单-->
<div id="nav-col">
	<section id="col-left" class="col-left-nano">
		<div id="col-left-inner" class="col-left-nano-content">
			<div class="collapse navbar-collapse navbar-ex1-collapse"
				id="sidebar-nav">
				<ul class="nav nav-pills nav-stacked">

					<li><!-- 一级菜单--> <a href="#" class="dropdown-toggle"> <i
							class="fa fa-inbox"></i><span>订单管理</span> <i
							class="fa fa-chevron-circle-right drop-icon"></i>
					</a> <!-- 一级菜单结束--> <!--二级菜单-->
						<ul class="submenu">
							<li menuAttr="menu"><a href="${_base}/order/toOrderList" target="mainFrame">全部订单</a></li>
							<li menuAttr="menu"><a href="${_base}/toCancelOrderList" target="mainFrame">已取消订单</a></li>
							<li menuAttr="menu"><a href="${_base}/toWaitPriceOrderList" target="mainFrame">待报价订单</a></li>
						    <li menuAttr="menu"><a href="${_base}/toWaitPayOrderList" target="mainFrame">待支付订单</a></li>
						    <li menuAttr="menu"><a href="${_base}/toUnclaimOrderList" target="mainFrame">待领取订单</a></li>
						     <li menuAttr="menu"><a href="${_base}/toTranslateOrderList" target="mainFrame">翻译中订单</a></li>
						     <li menuAttr="menu"><a href="${_base}/order/toReviewOrderList" target="mainFrame">待审核订单</a></li>
						     <li menuAttr="menu"><a href="${_base}/toTbcOrderList" target="mainFrame">待确认订单</a></li>
						     <li menuAttr="menu"><a href="${_base}/toUpdateOrderList" target="mainFrame">修改中订单</a></li>
						     <li menuAttr="menu"><a href="${_base}/toDoneOrderList" target="mainFrame">已完成订单</a></li>
						     <li menuAttr="menu"><a href="${_base}/toEvaluateOrderList" target="mainFrame">已评价订单</a></li>
						      <li menuAttr="menu"><a href="${_base}/toRefundOrderList" target="mainFrame">退款订单</a></li>
						</ul> <!--二级菜单结束--></li>
					<li><!-- 一级菜单--> <a href="#" class="dropdown-toggle"> <i
							class="fa fa-inbox"></i><span>账务资产</span> <i
							class="fa fa-chevron-circle-right drop-icon"></i>
					</a> <!-- 一级菜单结束--> <!--二级菜单-->
						<ul class="submenu">
							<li menuAttr="menu"><a href="${_base}/balance/toTranslatorBillList" target="mainFrame">译员账单</a></li>
							<li menuAttr="menu"><a href="${_base}/lspBill/toLspBillList" target="mainFrame">LSP账单</a></li>
							<li menuAttr="menu"><a href="${_base}/companyBill/toCompanyBillList" target="mainFrame">企业账单</a></li>
							<li menuAttr="menu"><a href="${_base}/rechargeWithdrawals/toRechargeWithdrawals" target="mainFrame">充值提现管理</a></li>
						</ul> <!--二级菜单结束--></li>
					<%--交易记录菜单--%>
					<li><!-- 一级菜单--> <a href="#" class="dropdown-toggle"> <i
							class="fa fa-inbox"></i><span>交易记录</span> <i
							class="fa fa-chevron-circle-right drop-icon"></i>
					</a> <!-- 一级菜单结束--> <!--二级菜单-->
						<ul class="submenu">
							<li menuAttr="menu"><a href="${_base}/businessList/toBusinessList" target="mainFrame">充值交易</a></li>
						</ul> <!--二级菜单结束--></li>


						<li><!-- 一级菜单--> <a href="#" class="dropdown-toggle"> <i
							class="fa fa-inbox"></i><span>券码管理</span> <i
							class="fa fa-chevron-circle-right drop-icon"></i>
					</a> <!-- 一级菜单结束--> <!--二级菜单-->
						<ul class="submenu">
							<li menuAttr="menu"><a href="${_base}/coupon/toCouponTemplateList" target="mainFrame">券码列表</a></li>
							<li menuAttr="menu"><a href="${_base}/activity/toActivityList" target="mainFrame">活动策略</a></li>
						</ul> <!--二级菜单结束--></li>
					<li><!-- 一级菜单--> <a href="#" class="dropdown-toggle"> <i
							class="fa fa-inbox"></i><span>用户管理</span> <i
							class="fa fa-chevron-circle-right drop-icon"></i>
					</a> <!-- 一级菜单结束--> <!--二级菜单-->
						<ul class="submenu">
							<li menuAttr="menu"><a href="${_base}/user/toUserList" target="mainFrame">会员列表</a></li>
							<li menuAttr="menu"><a href="${_base}/translator/toTranslatorInfoList" target="mainFrame">译员列表</a></li>
							<li menuAttr="menu"><a href="${_base}/company/toCompanyListPager" target="mainFrame">企业用户</a></li>
						</ul> <!--二级菜单结束--></li>
						
					<li><!-- 一级菜单--> 
					<a href="#" class="dropdown-toggle"> 
						<i class="fa fa-inbox"></i><span>人工翻译</span> <i class="fa fa-chevron-circle-right drop-icon"></i>
					</a> <!-- 一级菜单结束--> 
						<!--二级菜单-->
						<ul class="submenu">
							<li menuAttr="menu"><a href="${_base}/syspurpose/toSysPurposeList" target="mainFrame">用途列表</a></li>
							<li menuAttr="menu"><a href="${_base}/sysdomain/toSysDomainList" target="mainFrame">领域列表</a></li>
							<li menuAttr="menu"><a href="${_base}/sysduad/toSysDuadList" target="mainFrame">语言对管理</a></li>
							<li menuAttr="menu"><a href="${_base}/sysitembank/toSysItemBankList" target="mainFrame">题库列表</a></li>
						</ul>
						<!--二级菜单结束--></li>
						<li><!-- 一级菜单--> 
					<a href="#" class="dropdown-toggle"> 
						<i class="fa fa-inbox"></i><span>系统管理</span> <i class="fa fa-chevron-circle-right drop-icon"></i>
					</a> <!-- 一级菜单结束--> 
						<!--二级菜单-->
						<ul class="submenu">
							<li menuAttr="menu"><a href="${_base}/sysconfig/toSysConfigList" target="mainFrame">基本设置</a></li>
							<li menuAttr="menu"><a href="${_base}/syssensitive/toSysSensitiveList" target="mainFrame">敏感词库</a></li>
						</ul>
						<!--二级菜单结束--></li>
				</ul>
			</div>
		</div>
	</section>
</div>
<!--/左侧菜单结束-->
