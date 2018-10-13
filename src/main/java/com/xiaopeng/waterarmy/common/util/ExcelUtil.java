package com.xiaopeng.waterarmy.common.util;

import com.xiaopeng.waterarmy.common.enums.AccountLevelEnum;
import com.xiaopeng.waterarmy.common.enums.ExcelDataTypeEnum;
import com.xiaopeng.waterarmy.common.message.CodeEnum;
import com.xiaopeng.waterarmy.common.message.JsonMessage;
import com.xiaopeng.waterarmy.model.dao.Account;
import com.xiaopeng.waterarmy.model.dao.ContentInfo;
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

    /**
     * 单元格为Numeric格式(譬如电话号)，带有指数E。因此，若想获取其String类型时，需要进行格式转换
     *
     * @param cell
     * @return
     */
    public static String getStringFromNumericCell(Cell cell) {
        return new DecimalFormat("#").format(cell.getNumericCellValue());
    }

    /**
     * 导入excel数据
     *
     * @param file
     * @return
     */
    public static List<Object> importData(MultipartFile file, String type) {
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
        datas = getDatasBySheet(sheet, type);
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
        Integer lastRowNum = sheet.getLastRowNum();
        for (int i = 1; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);//获取索引为i的行，以1开始
            if (ExcelDataTypeEnum.LINK.getName().equals(type)) {
                LinkInfo info = new LinkInfo();
                String link = row.getCell(0).getStringCellValue();//获取第i行的索引为0的单元格数据
                if (ObjectUtils.isEmpty(link)) {
                    break;
                }
                info.setLink(link);
                datas.add(info);
            } else if (ExcelDataTypeEnum.CONTENT.getName().equals(type)) {
                ContentInfo info = new ContentInfo();
                String title = row.getCell(0).getStringCellValue();
                String content = row.getCell(1).getStringCellValue();
                if (ObjectUtils.isEmpty(title) || ObjectUtils.isEmpty(content)) {
                    break;
                }
                info.setTitle(title);
                info.setContent(content);
                datas.add(info);
            } else if (ExcelDataTypeEnum.ACCOUNT.getName().equals(type)) {
                Account info = new Account();
                String userName = getStringFromNumericCell(row.getCell(0));
                String fullName = "";
                if (!ObjectUtils.isEmpty(row.getCell(1))) {
                    fullName = getStringFromNumericCell(row.getCell(1));
                }
                String password = row.getCell(2).getStringCellValue();;
                String mobile = "";
                if (!ObjectUtils.isEmpty(row.getCell(3))) {
                    mobile = getStringFromNumericCell(row.getCell(3));//.getStringCellValue();
                }
                String email = "";
                if (!ObjectUtils.isEmpty(row.getCell(4))) {
                    email = row.getCell(4).getStringCellValue();
                }
//                Integer level = !ObjectUtils.isEmpty(row.getCell(5).getStringCellValue())
//                        ? Integer.parseInt(row.getCell(5).getStringCellValue()) : AccountLevelEnum.PRIMARY.getIndex();
                String platform = row.getCell(6).getStringCellValue();
                if (ObjectUtils.isEmpty(userName) || ObjectUtils.isEmpty(password) ||  ObjectUtils.isEmpty(platform)) {
                    break;
                }
                info.setUserName(userName);
                info.setFullName(fullName);
                info.setPassword(password);
                info.setMobile(mobile);
                info.setEmail(email);
                //info.setLevel(level);
                info.setPlatform(platform);
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