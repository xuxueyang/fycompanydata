package Utils;

import BaseSql.Mysql.CompanyDataEntity.CompanyNameToLegalPerson;
import BaseSql.Mysql.Manager;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GetCompanyLegalName {
    private static Manager manager = null;
    private static Statement execute = null;
    private static void init(){
        PropertiesUtil propertiesUtil = new PropertiesUtil("db.properties");
        String url = propertiesUtil.getProperty("jdbc.url");
        String root = propertiesUtil.getProperty("jdbc.username");
        String passowrd = propertiesUtil.getProperty("jdbc.password");
        try {
            manager = new Manager(url,root,passowrd);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        try {
            execute = manager.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }
    public static CompanyNameToLegalPerson getLegalNameByCompanyName(String companyName){
        if(manager==null){
            init();
        }
        CompanyNameToLegalPerson entity1=null;
        String companyNameToLegalPerson = "select * from company t where t.`name`='"+ companyName + "' limit 1;";
        ResultSet resultSet = null;
        try {
            resultSet = execute.executeQuery(companyNameToLegalPerson);
            while (resultSet.next()){
                entity1 = new CompanyNameToLegalPerson();
                entity1.setId(resultSet.getString("id"));
                entity1.setBase(resultSet.getString("base"));
                entity1.setEstiblish_time(resultSet.getString("estiblish_time"));
                entity1.setReg_capital(resultSet.getString("reg_capital"));
                entity1.setReg_status(resultSet.getString("reg_status"));
                entity1.setLegal_person_id(resultSet.getString("legal_person_id"));
                entity1.setLegal_person_name(resultSet.getString("legal_person_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity1;
    }
    public static void main(String[] args){

        init();


        ArrayList<ArrayList<String>> resultList = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<Object>> arrayLists = ExcelUtil.readExcel(new File("D:\\projects\\companydata\\Data\\3241_1.xlsx"));
        int index=1;
        for(ArrayList<Object> arrayList:arrayLists){
            String companyName = arrayList.get(0).toString().trim();
            String companyNameToLegalPerson = "select * from company t where t.`name`='"+ companyName + "';";
            try {
                ResultSet resultSet = execute.executeQuery(companyNameToLegalPerson);
                while (resultSet.next()){
                    String legal_person_name = resultSet.getString("legal_person_name");
                    ArrayList<String> strList = new ArrayList<>();
                    strList.add(companyName);
                    strList.add(legal_person_name);
                    System.out.println("A:"+companyName+"B:"+legal_person_name);
                    resultList.add(strList);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            index++;
            if(index%300==0){
                HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
                HSSFSheet sheet = hssfWorkbook.createSheet("sheet");
                for(int i=0;i<resultList.size();i++){
                    ArrayList<String> array = resultList.get(i);
                    HSSFRow row = sheet.createRow(i);
                    for(int j=0;j<array.size();j++){
                        row.createCell(j).setCellValue(array.get(0));
                        row.createCell(j).setCellValue(array.get(1));
                    }

                }
                ExcelUtil.writeSteamToExcel(hssfWorkbook,"D:\\projects\\companydata\\Data\\3241_result_1"+ index +".xlsx");
            }
        }
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet sheet = hssfWorkbook.createSheet("sheet");
        for(int i=0;i<resultList.size();i++){
            ArrayList<String> array = resultList.get(i);
            HSSFRow row = sheet.createRow(i);
            for(int j=0;j<array.size();j++){
                row.createCell(j).setCellValue(array.get(0));
                row.createCell(j).setCellValue(array.get(1));
            }
        }
        ExcelUtil.writeSteamToExcel(hssfWorkbook,"D:\\projects\\companydata\\Data\\3241_result.xlsx");

    }
}
