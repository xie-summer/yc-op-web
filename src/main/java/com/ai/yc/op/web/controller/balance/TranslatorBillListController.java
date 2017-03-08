package com.ai.yc.op.web.controller.balance;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ai.slp.balance.api.translatorbill.interfaces.IBillGenerateSV;
import com.ai.slp.balance.api.translatorbill.param.*;
import com.ai.yc.op.web.model.bill.BillDetailResponse;
import com.ai.yc.translator.api.translatorservice.interfaces.IYCTranslatorServiceSV;
import com.ai.yc.translator.api.translatorservice.param.YCLSPInfoReponse;
import com.ai.yc.translator.api.translatorservice.param.searchYCLSPInfoRequest;
import com.ai.yc.user.api.userservice.interfaces.IYCUserServiceSV;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ai.opt.base.vo.PageInfo;
import com.ai.opt.sdk.components.ccs.CCSClientFactory;
import com.ai.opt.sdk.components.excel.client.AbstractExcelHelper;
import com.ai.opt.sdk.components.excel.factory.ExcelFactory;
import com.ai.opt.sdk.dubbo.util.DubboConsumerFactory;
import com.ai.opt.sdk.util.BeanUtils;
import com.ai.opt.sdk.util.CollectionUtil;
import com.ai.opt.sdk.util.StringUtil;
import com.ai.opt.sdk.web.model.ResponseData;
import com.ai.paas.ipaas.ccs.IConfigClient;
import com.ai.yc.common.api.cache.interfaces.ICacheSV;
import com.ai.yc.common.api.cache.param.SysParam;
import com.ai.yc.common.api.cache.param.SysParamSingleCond;
import com.ai.yc.op.web.constant.Constants;
import com.ai.yc.op.web.constant.Constants.ExcelConstants;
import com.ai.yc.op.web.model.order.ExAllOrder;
import com.ai.yc.op.web.model.order.OrderPageQueryParams;
import com.ai.yc.op.web.model.order.OrderPageResParam;
import com.ai.yc.op.web.utils.AmountUtil;
import com.ai.yc.op.web.utils.TimeZoneTimeUtil;
import com.ai.yc.order.api.orderquery.interfaces.IOrderQuerySV;
import com.ai.yc.order.api.orderquery.param.OrdOrderVo;
import com.ai.yc.order.api.orderquery.param.QueryOrderRequest;
import com.ai.yc.order.api.orderquery.param.QueryOrderRsponse;

@Controller
@RequestMapping("/balance")
public class TranslatorBillListController {
	
	private static final Logger logger = Logger.getLogger(TranslatorBillListController.class);
	
	@RequestMapping("/toTranslatorBillList")
	public ModelAndView toTranslatorBillList(HttpServletRequest request) {
		return new ModelAndView("jsp/balance/translatorBillList");
	}
	
	/**
     * 账单信息查询
     */
    @RequestMapping("/getBillPageData")
    @ResponseBody
    public ResponseData<PageInfo<FunAccountResponse>> getList(HttpServletRequest request,FunAccountQueryRequest funAccountQueryRequest)throws Exception{
    	ResponseData<PageInfo<FunAccountResponse>> responseData = null;
    	List<FunAccountResponse> resultList = new ArrayList<FunAccountResponse>();
    	PageInfo<FunAccountResponse> resultPageInfo  = new PageInfo<FunAccountResponse>();
		try{

			String strPageNo=(null==request.getParameter("pageNo"))?"1":request.getParameter("pageNo");
		    String strPageSize=(null==request.getParameter("pageSize"))?"10":request.getParameter("pageSize");
			resultPageInfo.setPageNo(Integer.parseInt(strPageNo));
			resultPageInfo.setPageSize(Integer.parseInt(strPageSize));
			funAccountQueryRequest.setPageInfo(resultPageInfo);
			if (funAccountQueryRequest.getState()==null){
				funAccountQueryRequest.setState(1);
			}
			IBillGenerateSV billGenerateSV = DubboConsumerFactory.getService(IBillGenerateSV.class);
			FunAccountQueryResponse funAccountQueryResponse = billGenerateSV.queryFunAccount(funAccountQueryRequest);
			if (funAccountQueryResponse.getResponseHeader().isSuccess()) {
				PageInfo<FunAccountResponse> pageInfo = funAccountQueryResponse.getPageInfo();
				BeanUtils.copyProperties(resultPageInfo, pageInfo);
				List<FunAccountResponse> result = pageInfo.getResult();
				resultPageInfo.setResult(result);
				responseData = new ResponseData<PageInfo<FunAccountResponse>>(ResponseData.AJAX_STATUS_SUCCESS, "查询成功",resultPageInfo);
			}else {
				responseData = new ResponseData<PageInfo<FunAccountResponse>>(ResponseData.AJAX_STATUS_FAILURE, "查询失败", null);
			}
		} catch (Exception e) {
			logger.error("查询账单列表失败：", e);
			responseData = new ResponseData<PageInfo<FunAccountResponse>>(ResponseData.AJAX_STATUS_FAILURE, "查询信息异常", null);
		}
	    return responseData;
    }

	@RequestMapping("/toTranslatorDetailBillList")
	public ModelAndView toTranslatorDetailBillList(String billID) {
		ModelAndView view = new ModelAndView("jsp/balance/translatorDetailBillList");
		view.addObject("billID", billID);
		return view;
	}

	/**
	 * 账单明细查询
	 */
	@RequestMapping("/getBillDetailData")
	@ResponseBody
	public ResponseData<PageInfo<BillDetailResponse>> getDetailList(HttpServletRequest request,String billId)throws Exception{
		FunAccountDetailQueryRequest funAccountDetailQueryRequest = new FunAccountDetailQueryRequest();
		funAccountDetailQueryRequest.setBillID(billId);
		ResponseData<PageInfo<BillDetailResponse>> responseData = null;
		List<BillDetailResponse> resultList = new ArrayList<BillDetailResponse>();
		PageInfo<BillDetailResponse> resultPageInfo  = new PageInfo<BillDetailResponse>();
		
		try{
			IBillGenerateSV billGenerateSV = DubboConsumerFactory.getService(IBillGenerateSV.class);
			IYCTranslatorServiceSV translatorServiceSV = DubboConsumerFactory.getService(IYCTranslatorServiceSV.class);
			FunAccountDetailPageResponse funAccountDetailPageResponse = billGenerateSV.queryFunAccountDetail(funAccountDetailQueryRequest);
			if (funAccountDetailPageResponse.getResponseHeader().isSuccess()) {
				PageInfo<FunAccountDetailResponse> pageInfo = funAccountDetailPageResponse.getPageInfo();
				BeanUtils.copyProperties(resultPageInfo, pageInfo);
				List<FunAccountDetailResponse> result = pageInfo.getResult();
				for (int i=0;i<result.size();i++){
					Integer id = i+1;
					String lspID = result.get(i).getLspId();
					searchYCLSPInfoRequest searchLSPParams = new searchYCLSPInfoRequest();
					searchLSPParams.setLspId(lspID);
					YCLSPInfoReponse yclspInfoReponse = translatorServiceSV.searchLSPInfo(searchLSPParams);
					String lspName = yclspInfoReponse.getLspName();
					BillDetailResponse billDetailResponse = new BillDetailResponse();
					billDetailResponse.setId(id);
					billDetailResponse.setLspName(lspName);
					BeanUtils.copyProperties(billDetailResponse,result.get(i));
					resultList.add(billDetailResponse);
				}
				resultPageInfo.setResult(resultList);
				responseData = new ResponseData<PageInfo<BillDetailResponse>>(ResponseData.AJAX_STATUS_SUCCESS, "查询成功",resultPageInfo);
			}else {
				responseData = new ResponseData<PageInfo<BillDetailResponse>>(ResponseData.AJAX_STATUS_FAILURE, "查询失败", null);
			}

		} catch (Exception e) {
			logger.error("查询账单列表失败：", e);
			responseData = new ResponseData<PageInfo<BillDetailResponse>>(ResponseData.AJAX_STATUS_FAILURE, "查询信息异常", null);
		}
		return responseData;
	}


	/**
	 * 账单结算
	 */
	@RequestMapping("/settleBill")
	@ResponseBody
	public ResponseData<Boolean> settleBill(SettleParam param)throws Exception{
		IBillGenerateSV billGenerateSV = DubboConsumerFactory.getService(IBillGenerateSV.class);
		String billId=null;
		try {
			billId = billGenerateSV.settleBill(param);
		}catch (Exception e){
			logger.error("系统异常，请稍后重试", e);
			return new ResponseData<Boolean>(ResponseData.AJAX_STATUS_FAILURE, "系统异常，请稍后重试", null);
		}
		if(billId==null){
			logger.error("系统异常，请稍后重试");
			return new ResponseData<Boolean>(ResponseData.AJAX_STATUS_FAILURE, "系统异常，请稍后重试", null);
		}
		if (!param.getBillID().equals(billId)){
			return new ResponseData<Boolean>(ResponseData.AJAX_STATUS_FAILURE, "系统异常，请稍后重试", null);
		}
		return new ResponseData<Boolean>(ResponseData.AJAX_STATUS_SUCCESS, "账单结算成功", true);
	}

	/**
     * 订单信息导出
     */
    @RequestMapping("/export")
    @ResponseBody
    public void  export(HttpServletRequest request, HttpServletResponse response, OrderPageQueryParams queryRequest) {
    	logger.error("进入导出方法>>>>");
    	QueryOrderRequest ordReq = new QueryOrderRequest();
    	BeanUtils.copyProperties(ordReq, queryRequest);
    	String pgeOrderId = queryRequest.getOrderPageId();
    	if(!StringUtil.isBlank(pgeOrderId)) {
			boolean isNum = pgeOrderId.matches("[0-9]+");
			if(isNum) {
				ordReq.setOrderId(Long.parseLong(pgeOrderId));
			}else {
				ordReq.setOrderId(0l);
			}
		}
    	Long orderTimeBegin = queryRequest.getOrderTimeS();
		if (orderTimeBegin!=null) {
			Timestamp orderTimeS = new Timestamp(orderTimeBegin);
			ordReq.setOrderTimeStart(orderTimeS);
		}
		Long orderTimeEnd = queryRequest.getOrderTimeE();
		if (orderTimeEnd!=null) {
			Timestamp orderTimeE = new Timestamp(orderTimeEnd);
			ordReq.setOrderTimeEnd(orderTimeE);
		}
		//支付时间
		Long payTimeBegin = queryRequest.getPayTimeS();
		if (payTimeBegin!=null) {
			Timestamp payTimeS = new Timestamp(payTimeBegin);
			ordReq.setPayTimeStart(payTimeS);
		}
		Long payTimeEnd = queryRequest.getPayTimeE();
		if (payTimeEnd!=null) {
			Timestamp payTimeE = new Timestamp(payTimeEnd);
			ordReq.setPayTimeEnd(payTimeE);
		}
	    ordReq.setPageNo(1);
	    try {
	  //获取配置中的导出最大数值
	    	logger.error("获取导出最大条数配置>>>>");
	    IConfigClient configClient = CCSClientFactory.getDefaultConfigClient();
        String maxRow =  configClient.get(ExcelConstants.EXCEL_OUTPUT_MAX_ROW);
        int excelMaxRow = Integer.valueOf(maxRow);
	    ordReq.setPageSize(excelMaxRow);
	    IOrderQuerySV orderQuerySV = DubboConsumerFactory.getService(IOrderQuerySV.class);
	    ICacheSV iCacheSV = DubboConsumerFactory.getService(ICacheSV.class);
	    logger.error("调用查询方法>>>>");
	    QueryOrderRsponse orderListResponse = orderQuerySV.queryOrder(ordReq);
	    PageInfo<OrdOrderVo> pageInfo = orderListResponse.getPageInfo();
		List<OrdOrderVo> orderList = pageInfo.getResult();
		List<ExAllOrder> exportList = new ArrayList<ExAllOrder>();
		if(!CollectionUtil.isEmpty(orderList)){
			logger.error("查询数据非空进行数据整合>>>>");
			for(OrdOrderVo vo:orderList){
				if(!CollectionUtil.isEmpty(vo.getOrdProdExtendList())){
					for(int i=0;i<vo.getOrdProdExtendList().size();i++){
						ExAllOrder exOrder = new ExAllOrder();
						////翻译订单来源
						SysParamSingleCond	paramCond = new SysParamSingleCond();
						paramCond.setTenantId(Constants.TENANT_ID);
						paramCond.setColumnValue(vo.getChlId());
						paramCond.setTypeCode(Constants.TYPE_CODE);
						paramCond.setParamCode(Constants.ORD_CHL_ID);
		        		SysParam chldParam = iCacheSV.getSysParamSingle(paramCond);
		        		if(chldParam!=null){
		        			exOrder.setChlId(chldParam.getColumnDesc());
		        		}
		        		//翻译订单类型
		        		paramCond = new SysParamSingleCond();
		        		paramCond.setTenantId(Constants.TENANT_ID);
						paramCond.setColumnValue(vo.getTranslateType());
						paramCond.setTypeCode(Constants.TYPE_CODE);
						paramCond.setParamCode(Constants.ORD_TRANSLATE_TYPE);
		        		SysParam orderTypeParam = iCacheSV.getSysParamSingle(paramCond);
		        		if(orderTypeParam!=null){
		        			exOrder.setOrderType(orderTypeParam.getColumnDesc());
		        		}
		        		//翻译支付方式
		        		paramCond = new SysParamSingleCond();
		        		paramCond.setTenantId(Constants.TENANT_ID);
						paramCond.setColumnValue(vo.getPayStyle());
						paramCond.setTypeCode(Constants.TYPE_CODE);
						paramCond.setParamCode(Constants.ORD_PAY_STYLE);
		        		SysParam payStyleParam = iCacheSV.getSysParamSingle(paramCond);
		        		if(payStyleParam!=null){
		        			exOrder.setPayStyle(payStyleParam.getColumnDesc());
		        		}
		        		//翻译订单状态
		        		paramCond = new SysParamSingleCond();
		        		paramCond.setTenantId(Constants.TENANT_ID);
						paramCond.setColumnValue(vo.getState());
						paramCond.setTypeCode(Constants.TYPE_CODE);
						paramCond.setParamCode(Constants.ORD_STATE);
		        		SysParam stateParam = iCacheSV.getSysParamSingle(paramCond);
		        		if(stateParam!=null){
		        			exOrder.setState(stateParam.getColumnDesc());
		        		}
		        		//转换金额格式
                		if(!StringUtil.isBlank(vo.getCurrencyUnit())){
                			if(Constants.CURRENCY_UNIT_S.equals(vo.getCurrencyUnit())){
                				exOrder.setRealFee("$"+AmountUtil.liToYuan(vo.getTotalFee()));
                				exOrder.setTotalFee("$"+AmountUtil.liToYuan(vo.getTotalFee()));
                			}else{
                				exOrder.setRealFee("¥"+AmountUtil.liToYuan(vo.getTotalFee()));
                				exOrder.setTotalFee("¥"+AmountUtil.liToYuan(vo.getTotalFee()));
                			}
                		}
                		if(vo.getOrderTime()!=null){
		        			exOrder.setOrderTime(TimeZoneTimeUtil.getTimes(vo.getOrderTime()));
                		}
		        		exOrder.setUserName(vo.getUserName());
		        		exOrder.setOrderId(vo.getOrderId());
		        		if(vo.getPayTime()!=null){
		        			exOrder.setPayTime(TimeZoneTimeUtil.getTimes(vo.getPayTime()));
		        		}
		        		exOrder.setLangire(vo.getOrdProdExtendList().get(i).getLangungePairChName());
		        		exportList.add(exOrder);
					}
				}else{
					ExAllOrder exOrder = new ExAllOrder();
					////翻译订单来源
					SysParamSingleCond	paramCond = new SysParamSingleCond();
					paramCond.setTenantId(Constants.TENANT_ID);
					paramCond.setColumnValue(vo.getChlId());
					paramCond.setTypeCode(Constants.TYPE_CODE);
					paramCond.setParamCode(Constants.ORD_CHL_ID);
	        		SysParam chldParam = iCacheSV.getSysParamSingle(paramCond);
	        		if(chldParam!=null){
	        			exOrder.setChlId(chldParam.getColumnDesc());
	        		}
	        		//翻译订单类型
	        		paramCond = new SysParamSingleCond();
	        		paramCond.setTenantId(Constants.TENANT_ID);
	        		paramCond.setColumnValue(vo.getTranslateType());
					paramCond.setTypeCode(Constants.TYPE_CODE);
					paramCond.setParamCode(Constants.ORD_TRANSLATE_TYPE);
	        		SysParam orderTypeParam = iCacheSV.getSysParamSingle(paramCond);
	        		if(orderTypeParam!=null){
	        			exOrder.setOrderType(orderTypeParam.getColumnDesc());
	        		}
	        		//翻译支付方式
	        		paramCond = new SysParamSingleCond();
	        		paramCond.setTenantId(Constants.TENANT_ID);
					paramCond.setColumnValue(vo.getPayStyle());
					paramCond.setTypeCode(Constants.TYPE_CODE);
					paramCond.setParamCode(Constants.ORD_PAY_STYLE);
	        		SysParam payStyleParam = iCacheSV.getSysParamSingle(paramCond);
	        		if(payStyleParam!=null){
	        			exOrder.setPayStyle(payStyleParam.getColumnDesc());
	        		}
	        		//翻译订单状态
	        		paramCond = new SysParamSingleCond();
	        		paramCond.setTenantId(Constants.TENANT_ID);
					paramCond.setColumnValue(vo.getState());
					paramCond.setTypeCode(Constants.TYPE_CODE);
					paramCond.setParamCode(Constants.ORD_STATE);
	        		SysParam stateParam = iCacheSV.getSysParamSingle(paramCond);
	        		if(stateParam!=null){
	        			exOrder.setState(stateParam.getColumnDesc());
	        		}
	        		//转换金额格式
            		if(!StringUtil.isBlank(vo.getCurrencyUnit())){
            			if(Constants.CURRENCY_UNIT_S.equals(vo.getCurrencyUnit())){
            				exOrder.setRealFee("$"+AmountUtil.liToYuan(vo.getTotalFee()));
            				exOrder.setTotalFee("$"+AmountUtil.liToYuan(vo.getTotalFee()));
            			}else{
            				exOrder.setRealFee("¥"+AmountUtil.liToYuan(vo.getTotalFee()));
            				exOrder.setTotalFee("¥"+AmountUtil.liToYuan(vo.getTotalFee()));
            			}
            		}
            		if(vo.getOrderTime()!=null){
            			exOrder.setOrderTime(TimeZoneTimeUtil.getTimes(vo.getOrderTime()));
            		}
	        		
	        		exOrder.setUserName(vo.getUserName());
	        		if(vo.getPayTime()!=null){
	        			exOrder.setPayTime(TimeZoneTimeUtil.getTimes(vo.getPayTime()));
	        		}
	        		exOrder.setOrderId(vo.getOrderId());
	        		exportList.add(exOrder);
				}
			}
		}else{
			logger.error("查询数据为空>>>>");
		}
			logger.error("获取输出流>>>>");
			ServletOutputStream outputStream = response.getOutputStream();
			response.reset();// 清空输出流
            response.setContentType("application/msexcel");// 定义输出类型
            response.setHeader("Content-disposition", "attachment; filename=order"+new Date().getTime()+".xls");// 设定输出文件头
            String[] titles = new String[]{"订单来源", "订单类型", "订单编号", "下单时间", "昵称", "语种方向","订单金额","实付金额","支付方式","支付时间","状态"};
    		String[] fieldNames = new String[]{"chlId", "orderType", "orderId", "orderTime",
    				"userName", "langire","totalFee","realFee","payStyle","payTime","state"};
			 AbstractExcelHelper excelHelper = ExcelFactory.getJxlExcelHelper();
			 logger.error("写入数据到excel>>>>");
			 excelHelper.writeExcel(outputStream, "order"+new Date().getTime(), ExAllOrder.class, exportList,fieldNames, titles);
		} catch (Exception e) {
			logger.error("导出订单列表失败："+e.getMessage(), e);
		}
	}

}
