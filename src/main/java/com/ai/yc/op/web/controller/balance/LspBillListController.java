package com.ai.yc.op.web.controller.balance;

import com.ai.opt.base.vo.PageInfo;
import com.ai.opt.sdk.components.ccs.CCSClientFactory;
import com.ai.opt.sdk.components.excel.client.AbstractExcelHelper;
import com.ai.opt.sdk.components.excel.factory.ExcelFactory;
import com.ai.opt.sdk.dubbo.util.DubboConsumerFactory;
import com.ai.opt.sdk.util.BeanUtils;
import com.ai.opt.sdk.util.CollectionUtil;
import com.ai.opt.sdk.web.model.ResponseData;
import com.ai.paas.ipaas.ccs.IConfigClient;
import com.ai.slp.balance.api.translatorbill.interfaces.IBillGenerateSV;
import com.ai.slp.balance.api.translatorbill.param.*;
import com.ai.yc.op.web.constant.Constants.ExcelConstants;
import com.ai.yc.op.web.model.bill.BillDetailResponse;
import com.ai.yc.op.web.model.bill.ExAllBill;
import com.ai.yc.op.web.utils.AmountUtil;
import com.ai.yc.op.web.utils.TimeZoneTimeUtil;
import com.ai.yc.translator.api.translatorservice.interfaces.IYCTranslatorServiceSV;
import com.ai.yc.translator.api.translatorservice.param.YCLSPInfoReponse;
import com.ai.yc.translator.api.translatorservice.param.searchYCLSPInfoRequest;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/lspBill")
public class LspBillListController {
	
	private static final Logger logger = Logger.getLogger(LspBillListController.class);
	
	@RequestMapping("/toLspBillList")
	public ModelAndView toLspBillList(HttpServletRequest request) {
		return new ModelAndView("jsp/balance/lspBillList");
	}
	


	@RequestMapping("/toLspDetailBillList")
	public ModelAndView toLspDetailBillList(String billID) {
		ModelAndView view = new ModelAndView("jsp/balance/lspDetailBillList");
		view.addObject("billID", billID);
		return view;
	}


	/**
     * 订单信息导出
     */
    @RequestMapping("/export")
    @ResponseBody
    public void  export(HttpServletRequest request, HttpServletResponse response, FunAccountQueryRequest funAccountQueryRequest) {
    	logger.error("进入导出方法>>>>");
		List<ExAllBill> exAllBills = new ArrayList<ExAllBill>();
		PageInfo<FunAccountResponse> resultPageInfo  = new PageInfo<FunAccountResponse>();
	    try {
	  //获取配置中的导出最大数值
	    	logger.error("获取导出最大条数配置>>>>");
	    IConfigClient configClient = CCSClientFactory.getDefaultConfigClient();
        String maxRow =  configClient.get(ExcelConstants.EXCEL_OUTPUT_MAX_ROW);
        int excelMaxRow = Integer.valueOf(maxRow);
		resultPageInfo.setPageSize(excelMaxRow);
		resultPageInfo.setPageNo(1);
		funAccountQueryRequest.setPageInfo(resultPageInfo);
		IBillGenerateSV billGenerateSV = DubboConsumerFactory.getService(IBillGenerateSV.class);
	    logger.error("调用查询方法>>>>");
		FunAccountQueryResponse funAccountQueryResponse = billGenerateSV.queryFunAccount(funAccountQueryRequest);
		PageInfo<FunAccountResponse> pageInfo = funAccountQueryResponse.getPageInfo();
		List<FunAccountResponse> billList = pageInfo.getResult();
		if(!CollectionUtil.isEmpty(billList)){
			for (FunAccountResponse funAccountResponse:billList){
				ExAllBill exAllBill = new ExAllBill();
				//编号
				exAllBill.setBillId(funAccountResponse.getBillId());
				//LSP名称
				exAllBill.setTargetName(funAccountResponse.getLspAdmin());
				//管理员
				exAllBill.setLspAdmin(funAccountResponse.getLspAdmin());
				//开始时间
				if (funAccountResponse.getStartAccountTime()!=null){
					exAllBill.setStartAccountTime(funAccountResponse.getStartAccountTime().toString());
				}
				//结束时间
				if (funAccountResponse.getEndAccountTime()!=null){
					exAllBill.setEndAccountTime(TimeZoneTimeUtil.getTimes(funAccountResponse.getEndAccountTime()));
				}
				//本期账单金额billFee
				if (funAccountResponse.getFlag().equals("0")){
					exAllBill.setBillFee("¥"+AmountUtil.liToYuan(funAccountResponse.getBillFee()));
				}else {
					exAllBill.setBillFee("$"+AmountUtil.liToYuan(funAccountResponse.getBillFee()));
				}
				//平台费用
				if (funAccountResponse.getFlag().equals("0")){
					exAllBill.setPlatFee("¥"+ AmountUtil.liToYuan(funAccountResponse.getPlatFee()));
				}else {
					exAllBill.setPlatFee("$"+funAccountResponse.getPlatFee());
				}
				//译员支出
				if (funAccountResponse.getFlag().equals("0")){
					exAllBill.setTranslatorFee("¥"+AmountUtil.liToYuan(funAccountResponse.getTranslatorFee()));
				}else {
					exAllBill.setTranslatorFee("$"+AmountUtil.liToYuan(funAccountResponse.getTranslatorFee()));
				}
				//应结金额
				if (funAccountResponse.getFlag().equals("0")){
					exAllBill.setAccountAmout("¥"+AmountUtil.liToYuan(funAccountResponse.getAccountAmout()));
				}else {
					exAllBill.setAccountAmout("$"+AmountUtil.liToYuan(funAccountResponse.getAccountAmout()));
				}
				//账单周期
				exAllBill.setAccountPeriod("1个月");
				//账单生成时间
				if (funAccountResponse.getCreateTime()!=null){
					exAllBill.setCreateTime(TimeZoneTimeUtil.getTimes(funAccountResponse.getCreateTime()));
				}
				//结算方式
				if (funAccountResponse.getAccountType()!=null){
					if (funAccountResponse.getAccountType().equals("1")){
						exAllBill.setAccountType("支付宝");
					}else if (funAccountResponse.getAccountType().equals("2")){
						exAllBill.setAccountType("微信");
					}else if (funAccountResponse.getAccountType().equals("3")){
						exAllBill.setAccountType("银行汇款/转账");
					}else if(funAccountResponse.getAccountType().equals("4")){
						exAllBill.setAccountType("PayPal");
					}
				}
				//结算账户
				if (funAccountResponse.getSettleAccount()!=null){
					exAllBill.setSettleAccount(funAccountResponse.getSettleAccount());
				}
				//结算时间
				if (funAccountResponse.getActAccountTime()!=null){
					exAllBill.setActAccountTime(TimeZoneTimeUtil.getTimes(funAccountResponse.getActAccountTime()));
				}
				//结算状态
				if (funAccountResponse.getState()!=null){
					if (funAccountResponse.getState()==1){
						exAllBill.setState("已结算");
					}else {
						exAllBill.setState("未结算");
					}
				}
				exAllBills.add(exAllBill);
			}
		}else{
			logger.error("查询数据为空>>>>");
		}
			logger.error("获取输出流>>>>");
			ServletOutputStream outputStream = response.getOutputStream();
			response.reset();// 清空输出流
            response.setContentType("application/msexcel");// 定义输出类型
            response.setHeader("Content-disposition", "attachment; filename=lspBill"+new Date().getTime()+".xls");// 设定输出文件头
            String[] titles = new String[]{"编号", "LSP名称", "管理员", "开始时间", "结束时间", "本期结算金额","平台费用","译员支出","应结金额","账单周期","账单生成时间","结算方式","结算账户"
											,"结算时间","结算状态"};
    		String[] fieldNames = new String[]{"billId", "lspAdmin", "lspAdmin", "startAccountTime",
    				"endAccountTime", "billFee","platFee","translatorFee","accountAmout","accountPeriod","createTime","accountType","settleAccount","actAccountTime","state"};
			 AbstractExcelHelper excelHelper = ExcelFactory.getJxlExcelHelper();
			 logger.error("写入数据到excel>>>>");
			 excelHelper.writeExcel(outputStream, "lspBill"+new Date().getTime(), ExAllBill.class, exAllBills,fieldNames, titles);
		} catch (Exception e) {
			logger.error("导出账单列表失败："+e.getMessage(), e);
		}
	}

}
