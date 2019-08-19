package com.zhy.web;

import com.zhy.pojo.ResponseResult;
import com.zhy.pojo.entity.UserInfo;
import com.zhy.service.UserService;
import com.zhy.utils.UID;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class XlsController {        //xlsx文件批量上传与下载
    @Autowired
    private UserService userService;

    @RequestMapping("writeOutExecel")
    @ResponseBody
    @ApiOperation("导出当前页数据")
    public ResponseResult writeOutExecel(@RequestBody Map<String,Object> map) throws IOException {
        Integer pageSize = Integer.valueOf((Integer) map.get("pageSize"));
        Integer currentPage = Integer.valueOf((Integer) map.get("currentPage"));
        System.out.println("writeOutExecel方法..."+pageSize+","+currentPage);
        Page<UserInfo> userListByExecel = userService.getUserListByExecel(pageSize, currentPage);
        List<UserInfo> userList = userListByExecel.getContent();
        XSSFWorkbook workbook = new XSSFWorkbook();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = "用户信息表"+simpleDateFormat.format(new Date())+".xlsx";
        String sheetName = "用户信息";
        String[] titile ={"编号","用户名","登录名","性别","电话","密码"};
        XSSFSheet sheet = workbook.createSheet(sheetName);
        XSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        XSSFRow titleRow = sheet.createRow(0);
        for (int i=0 ; i<titile.length ; i++) {         //创建单元格
            XSSFCell cell = titleRow.createCell(i);     //设置单元格内容
            cell.setCellValue(titile[i]);               //设置单元格样式
            cell.setCellStyle(style);
        }
        Row row = null;
        for(int i = 0; i < userList.size();i++){        //创建list.siza()行数据
            row = sheet.createRow(i + 1);     //把值写进单元格
            row.createCell(0).setCellValue(userList.get(i).getId());
            row.createCell(1).setCellValue(userList.get(i).getUserName());
            row.createCell(2).setCellValue(userList.get(i).getLoginName());
            row.createCell(3).setCellValue(userList.get(i).getSex());
            row.createCell(4).setCellValue(userList.get(i).getPassword());
        }
        File file = new File("C:/Users/宅喵/Desktop/实训项目/excelTest");
        if(!file.exists()){
            file.mkdirs();
        }
        String savePath = "C:/Users/宅喵/Desktop/实训项目/excelTest/"+fileName;
        FileOutputStream fileOut = new FileOutputStream(savePath);
        workbook.write(fileOut);
        fileOut.close();
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(200);
        responseResult.setSuccess("导出成功");
        return responseResult;
    }


    @RequestMapping("dowloudExcel")
    @ApiOperation("导入excel数据进行批量添加")
    public ResponseResult dowloudExcel(@RequestParam("file") MultipartFile multipartFile ) throws IOException {
        //获取传来的excel的输入流
        InputStream inputStream = multipartFile.getInputStream();

        //获得一个XSSFWorkbook的对象
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        //确认数据在哪个工作空间
        XSSFSheet sheet = workbook.getSheetAt(0);
        //
        int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();

        String names = "";

        for (int i = 1; i < physicalNumberOfRows; i++) {
            XSSFRow row = sheet.getRow(i);
            UserInfo user = new UserInfo();
            user.setId(UID.next());
            String uname = row.getCell(1).getStringCellValue();
            List<UserInfo> users = userService.selectUserByName(uname);     //查询是否有同名用户
            if (users != null && users.size() > 0) {
                names += uname + ",";
                continue;
            }
            user.setUserName(uname);
            user.setLoginName(row.getCell(2).getStringCellValue());
            user.setSex(new Double(row.getCell(3).getNumericCellValue()).intValue());
            user.setTel("" + new Double(row.getCell(4).getNumericCellValue()).longValue());
            user.setPassword("" + new Double(row.getCell(5).getNumericCellValue()).longValue());
            user.setUpdateTime(new Date());
            user.setCreateTime(new Date());
            if (user.getSex() == 1) {
                user.setPhotoUrl("/male.jpg");
            } else {
                user.setPhotoUrl("/female.jpg");
            }
            userService.addUser(user);      //添加用户
        }
        ResponseResult responseResult = new ResponseResult();
        if (!names.equals("")) {
            responseResult.setCode(520);
            responseResult.setError("重复了不能添加,重复的登陆名称是:"+names);
            System.out.println(names);
        } else {
            responseResult.setCode(200);
            responseResult.setSuccess("导入成功...");
        }
        return responseResult;
    }
}
