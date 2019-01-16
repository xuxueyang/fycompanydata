import Utils.ExcelUtil;
import Utils.PropertiesUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QS {
    public static void main(String[] args){
        PropertiesUtil companyPropertiesUtil = new PropertiesUtil("companydate.properties");
        String inpath = companyPropertiesUtil.getProperty("in.companyname.file.path")+companyPropertiesUtil.getProperty("in.companyname.file.name");
        //所有名字
        ArrayList<ArrayList<Object>> arrayLists = ExcelUtil.readExcel(new File(inpath));
        List<String>  companyNames = new ArrayList<String>();
        Set<String> companyNamesSet = new HashSet<String>();
        for(ArrayList<Object> arrayList:arrayLists){
            String  companyName = arrayList.get(0).toString().trim();
            companyNames.add(companyName);
        }

        for(int i=1;i<=6;i++){
            inpath = companyPropertiesUtil.getProperty("data.file."+i);
            //查询,所有的名字
            File dir = new File(inpath);
            File[] files = dir.listFiles();
            if(files!=null){
                for(int k=0;k<files.length;k++){
                    File file = files[k];
                    if(file.getName().endsWith("xls")){
                        ArrayList<ArrayList<Object>> nameLists = ExcelUtil.readExcel(file);
                        for(ArrayList<Object> nameList:nameLists){
                            String  companyName = nameList.get(0).toString().trim();
                            companyNamesSet.add(companyName);
                        }
                    }
                }
            }
        }
        List<String> noName = new ArrayList<String>();
        for(String name:companyNames){
            if(companyNamesSet.contains(name)){

            }else{
                noName.add(name);
            }
        }
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("数据");
        int rowIndex = 0;
        for(int k=0;k<noName.size();k++){
            HSSFRow row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(noName.get(k));
        }
        ExcelUtil.writeSteamToExcel(wb,"D:\\\\projects\\\\companydata\\\\noName.xls");

    }
}
