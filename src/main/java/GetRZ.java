import BaseSql.Mysql.CompanyDataEntity.CompanyNameToLegalPerson;
import BaseSql.Mysql.Manager;
import Utils.ExcelUtil;
import Utils.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GetRZ {
    public static void main(String[] args){
        PropertiesUtil propertiesUtil = new PropertiesUtil("db.properties");
        String url = propertiesUtil.getProperty("jdbc.url");
        String root = propertiesUtil.getProperty("jdbc.username");
        String passowrd = propertiesUtil.getProperty("jdbc.password");


        Manager manager = null;
        try {
            manager = new Manager(url,root,passowrd);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Statement execute = null;
        Statement execute2 = null;
        try {
            execute = manager.createStatement();
            execute2 = manager.createStatement();

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        PropertiesUtil companyPropertiesUtil = new PropertiesUtil("companydate.properties");
        //所有名字
        Set<String> companyNamesSet = new HashSet<String>();
        List<ArrayList<ArrayList<Object>>> excels = new ArrayList<ArrayList<ArrayList<Object>>>();
        ArrayList<ArrayList<Object>> excels1 = ExcelUtil.readExcel(new File("D:\\projects\\companydata\\one1.xls")); //new ArrayList<ArrayList<ArrayList<Object>>>();
//        ArrayList<ArrayList<Object>> excels2 = ExcelUtil.readExcel(new File("D:\\projects\\companydata\\one2.xls")); //new ArrayList<ArrayList<ArrayList<Object>>>();
        excels.add(excels1);
//        excels.add(excels2);
//        for(int i=1;i<=6;i++){
//            String inpath = companyPropertiesUtil.getProperty("data.file."+i);
//            //查询,所有的名字
//            File dir = new File(inpath);
//            File[] files = dir.listFiles();
//            if(files!=null){
//                for(int k=0;k<files.length;k++){
//                    File file = files[k];
//                    if(file.getName().endsWith("xls")){
//                        ArrayList<ArrayList<Object>> nameLists = ExcelUtil.readExcel(file);
//                        excels.add(nameLists);
//                    }
//                }
//            }
//        }
        //写入
        HSSFWorkbook hs = new HSSFWorkbook();
        HSSFSheet sheet1 = hs.createSheet("sheet1");
        int index = 0;
        int k = 2;
        for(int i=0;i<excels.size();i++) {
            ArrayList<ArrayList<Object>> arrayLists = excels.get(i);
            for (int tmp = 1; tmp < arrayLists.size(); tmp++) {
                ArrayList<Object> arrayList = arrayLists.get(tmp);
                HSSFRow row = sheet1.createRow(index++);
                if (arrayList.size() <= 7 || "".equals(arrayList.get(7).toString().trim())) {
                    String companyName = arrayList.get(2).toString().trim();
                    String people = arrayList.get(1).toString().trim();
                    String companyNameToLegalPerson = "select t.id from company t where t.`name`='" + companyName + "' limit 1;";
                    String str = ",";
                    try {
                        ResultSet resultSet = execute.executeQuery(companyNameToLegalPerson);
                        while (resultSet.next()) {
                            String id = resultSet.getString("id");
                            if (StringUtils.isNotBlank(id)) {
                                String sql = "SELECT h.name,s.* from  company_staff s LEFT JOIN human h ON h.name='" + people + "' where s.company_id = " + id+" and h.id=s.staff_id";
                                ResultSet resultSet1 = execute2.executeQuery(sql);
                                while (resultSet1.next()) {
                                    if (StringUtils.isNotBlank(resultSet1.getString("staff_type_name"))) {
                                        str += "," + resultSet1.getString("staff_type_name");
                                    }
                                }
                                str = str.substring(1, str.length());
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    System.out.println("A:" + companyName + "B:" + people + "C:" + str);
                    int col = 0;
                    for (col = 0; col < arrayList.size(); col++) {
                        if (col == 7) {
                            row.createCell(col).setCellValue(str);
                        } else {
                            row.createCell(col).setCellValue(arrayList.get(col).toString().trim());
                        }
                    }
                    if (col <= 8) {
                        row.createCell(7).setCellValue(str);
                    }
                } else {
                    int col = 0;
                    for (Object obj : arrayList) {
                        row.createCell(col++).setCellValue(obj.toString().trim());
                    }
                }

                if (index >= 65536) {
                    index = 0;
                    sheet1 = hs.createSheet("sheet" + k++);
                }
            }
            ExcelUtil.writeSteamToExcel(hs, "D:\\\\projects\\\\companydata\\\\one_has_rz"+i+".xls");
        }
    }

}
