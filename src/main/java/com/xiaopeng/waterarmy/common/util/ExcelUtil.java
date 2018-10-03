package com.xiaopeng.waterarmy.common.util;

import com.xiaopeng.waterarmy.common.enums.ExcelDataTypeEnum;
import com.xiaopeng.waterarmy.common.message.CodeEnum;
import com.xiaopeng.waterarmy.common.message.JsonMessage;
import com.xiaopeng.waterarmy.model.dao.LinkInfo;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * excel通用类
 * <p>
 * Created by iason on 2018/10/3.
 */
public class ExcelUtil {

    private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    public static boolean isExcel2003(String fileName) {
        return fileName.matches("^.+\\.(?i)(xls)$");
    }

    public static boolean isExcel2007(String fileName) {
        return fileName.matches("^.+\\.(?i)(xlsx)$");
    }

    public static String getStringFromNumericCell(Cell cell) {
        return new DecimalFormat("#").format(cell.getNumericCellValue());
    }

    /**
     * 导入excel数据
     *
     * @param file
     * @return
     */
    public static List<Object> importData(MultipartFile file, String tpye) {
        String fileName = file.getOriginalFilename();
        Workbook wb = null;
        List<Object> datas = null;
        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
            logger.error("上传文件格式不正确, fileName: " + fileName);
            return null;
        }
        try {
            if (ExcelUtil.isExcel2007(fileName)) {
                wb = new XSSFWorkbook(file.getInputStream());
            } else {
                wb = new HSSFWorkbook(file.getInputStream());
            }
        } catch (IOException e) {
            logger.error("importData error, ", e);
            return null;
        }
        Sheet sheet = wb.getSheetAt(0);//获取第一张表
        datas = getDatasBySheet(sheet, tpye);
        try {
            if (ObjectUtils.isEmpty(wb)) {
                wb.close();
            }
        } catch (IOException e) {
            logger.error("importData close wb error, ", e);
        }
        return datas;
    }

    private static List<Object> getDatasBySheet(Sheet sheet, String type) {
        List<Object> datas = new ArrayList<>();
        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);//获取索引为i的行，以1开始
            if (ExcelDataTypeEnum.LINK.getName().equals(type)) {
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

    /**
     * 导出excel数据
     *
     * @param datas
     * @return
     */
    public static void exportData(List<Object> datas, String templetFilePath, String exportFilePath) {
        try {
            File exportFile = new File(exportFilePath);
            File templetFile = new File(templetFilePath);
            Workbook workBook;

            if (!exportFile.exists()) {
                exportFile.createNewFile();
            }

            FileOutputStream out = new FileOutputStream(exportFile);
            FileInputStream fis = new FileInputStream(templetFile);
            if (isExcel2007(templetFile.getPath())) {
                workBook = new XSSFWorkbook(fis);
            } else {
                workBook = new HSSFWorkbook(fis);
            }

            Sheet sheet = workBook.getSheetAt(0);

//            int rowIndex = 1;
//            for (Hero item: heroList) {
//                Row row = sheet.createRow(rowIndex);
//                row.createCell(0).setCellValue(item.getHeroAge());
//                row.createCell(1).setCellValue(item.getHeroName());
//                rowIndex++;
//            }
            workBook.write(out);
            out.flush();
            out.close();
            fis.close();
        } catch (Exception e) {
            logger.error("exportHeroInfo error, ", e);
        }
    }


}
