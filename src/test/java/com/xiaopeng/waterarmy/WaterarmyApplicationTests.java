package com.xiaopeng.waterarmy;

import com.xiaopeng.waterarmy.common.util.ExcelUtil;
import com.xiaopeng.waterarmy.model.dao.LinkInfo;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WaterarmyApplicationTests {

	@Test
	public void test1() {
		//(int) Double.parseDouble(row.getCell(5).getStringCellValue());
	}

	@Test
	public void contextLoads() {
		System.out.println("去请求");
		File file = new File("E:/水军系统研发/link.xls");
		importData(file, LinkInfo.class);
	}

	/**
	 * 导入excel数据
	 *
	 * @param file
	 * @return
	 */
	public static List<Object> importData(File file, Object object) {
		System.out.println("~~~~~~~~~~"+ (object instanceof LinkInfo));
		Workbook wb = null;
		List<Object> datas = null;
		try {
			if (ExcelUtil.isExcel2007(file.getPath())) {
				wb = new XSSFWorkbook(new FileInputStream(file));
			} else {
				wb = new HSSFWorkbook(new FileInputStream(file));
			}
		} catch (IOException e) {
			//logger.error("importData error, ", e);
			return null;
		}
		Sheet sheet = wb.getSheetAt(0);//获取第一张表
		datas = getDatasBySheet(sheet, object);
		try {
			if (ObjectUtils.isEmpty(wb)) {
				wb.close();
			}
		} catch (IOException e) {
			//logger.error("importData close wb error, ", e);
		}
		return datas;
	}

	private static List<Object> getDatasBySheet(Sheet sheet, Object object) {
		List<Object> datas = new ArrayList<>();
		for (int i = 1; i < sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);//获取索引为i的行，以1开始
			if (object instanceof LinkInfo) {
				LinkInfo info = new LinkInfo();
				String link = row.getCell(0).getStringCellValue();//获取第i行的索引为0的单元格数据
				if (ObjectUtils.isEmpty(link)) {
					break;
				}
				info.setLink(link);
				datas.add(info);
			}
		}
		return datas;
	}


}
