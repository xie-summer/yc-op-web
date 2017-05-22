package com.ai.yc.op.web.model.utils;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.ai.yc.common.api.sysquestions.param.SaveSysQuestions;

public class ImportEmployeeChoice {
	/**
	 * POI:解析Excel文件中的数据并把每行数据封装成一个实体
	 * @param fis 文件输入流
	 * @return List<EmployeeInfo> Excel中数据封装实体的集合
	 */
	public static List<SaveSysQuestions> importEmployeeByPoi(InputStream fis) {
		//这里是解析出来的Excel的数据存放的List集合
		List<SaveSysQuestions> infos = new ArrayList<SaveSysQuestions>();
		//这里是解析出来的Excel中的每一条数据封装的实体BEAN.
		SaveSysQuestions employeeInfo = null;
		
		try {
			//创建Excel工作薄
			HSSFWorkbook hwb = new HSSFWorkbook(fis);
			//得到第一个工作表
			HSSFSheet sheet = hwb.getSheetAt(0);
			HSSFRow row = null;
			//日期格式化
			DateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
			//遍历该表格中所有的工作表，i表示工作表的数量 getNumberOfSheets表示工作表的总数 
			for(int i = 0; i < hwb.getNumberOfSheets(); i++) {
				sheet = hwb.getSheetAt(i);
				//遍历该行所有的行,j表示行数 getPhysicalNumberOfRows行的总数
				for(int j = 0; j < sheet.getPhysicalNumberOfRows(); j++) {
					row = sheet.getRow(j);
					employeeInfo = new SaveSysQuestions();
					
					//此方法规定Excel文件中的数据必须为文本格式，所以在解析数据的时候未进行判断 
                    //方法1：Excel解析出来的数字为double类型，要转化为Long类型必须做相应的处理(一开始用的方法，比较笨。) 
                    //先把解析出来的double类型转化为String类型，然后截取String类型'.'以前的字符串，最后把字符串转化为Long类型。 
                    //方法2：其实double类型可以通过(long)Double这样直接转化为Long类型。 
                    employeeInfo.setQid(row.getCell(0).toString());
                    employeeInfo.setQtype(row.getCell(1).toString());
                    employeeInfo.setChoiceQuestion(row.getCell(2).toString());
                    employeeInfo.setOptiona(row.getCell(3).toString());
                    employeeInfo.setOptionb(row.getCell(4).toString());
                    employeeInfo.setOptionc(row.getCell(5).toString());
                    employeeInfo.setOptiond(row.getCell(6).toString()); 
                    employeeInfo.setAnswer(row.getCell(7).toString()); 
                    employeeInfo.setStatus(row.getCell(8).toString()); 
                    employeeInfo.setCreateOperatorId(row.getCell(9).toString());
                    employeeInfo.setCreateOperator(row.getCell(10).toString());
                  //此方法调用getCellValue(HSSFCell cell)对解析出来的数据进行判断，并做相应的处理   
                    if(ImportEmployeeChoice.getCellValue(row.getCell(0)) != null && !"".equals(ImportEmployeeChoice.getCellValue(row.getCell(0)))) {   
                        employeeInfo.setQid(ImportEmployeeChoice.getCellValue(row.getCell(0)));   
                    }   
                    employeeInfo.setQtype(ImportEmployeeChoice.getCellValue(row.getCell(1)));   
                    employeeInfo.setChoiceQuestion(ImportEmployeeChoice.getCellValue(row.getCell(2)));
                    employeeInfo.setOptiona(ImportEmployeeChoice.getCellValue(row.getCell(3)));
                    employeeInfo.setOptionb(ImportEmployeeChoice.getCellValue(row.getCell(4)));   
                    employeeInfo.setOptionc(ImportEmployeeChoice.getCellValue(row.getCell(5)));   
                    employeeInfo.setOptiond(ImportEmployeeChoice.getCellValue(row.getCell(6)));
                    employeeInfo.setAnswer(ImportEmployeeChoice.getCellValue(row.getCell(7))); 
                    employeeInfo.setStatus(ImportEmployeeChoice.getCellValue(row.getCell(8)));
                    employeeInfo.setCreateOperatorId(ImportEmployeeChoice.getCellValue(row.getCell(9)));
                    employeeInfo.setCreateOperator(ImportEmployeeChoice.getCellValue(row.getCell(10)));
                    /*if(ImportEmployee.getCellValue(row.getCell(4)) != null && !"".equals(ImportEmployee.getCellValue(row.getCell(4)))) {   
                        try {   
                            employeeInfo.setDateOfBirth(ft.parse(ImportEmployee.getCellValue(row.getCell(4))));   
                        } catch (ParseException e) {   
                            e.printStackTrace();   
                        }   
                        employeeInfo.setTownOfBirth(ImportEmployee.getCellValue(row.getCell(5)));   
                    }   */
                    infos.add(employeeInfo);   
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return infos;
	}
	//判断从Excel文件中解析出来数据的格式   
    private static String getCellValue(HSSFCell cell){   
        String value = null;   
        //简单的查检列类型   
        switch(cell.getCellType())   
        {   
            case HSSFCell.CELL_TYPE_STRING://字符串   
                value = cell.getRichStringCellValue().getString();   
                break;   
            case HSSFCell.CELL_TYPE_NUMERIC://数字   
                long dd = (long)cell.getNumericCellValue();   
                value = dd+"";   
                break;   
            case HSSFCell.CELL_TYPE_BLANK:   
                value = "";   
                break;      
            case HSSFCell.CELL_TYPE_FORMULA:   
                value = String.valueOf(cell.getCellFormula());   
                break;   
            case HSSFCell.CELL_TYPE_BOOLEAN://boolean型值   
                value = String.valueOf(cell.getBooleanCellValue());   
                break;   
            case HSSFCell.CELL_TYPE_ERROR:   
                value = String.valueOf(cell.getErrorCellValue());   
                break;   
            default:   
                break;   
        }   
        return value;   
    }  
}
