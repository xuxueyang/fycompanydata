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

public class DC {
    public static void main(String[] args){
        PropertiesUtil companyPropertiesUtil = new PropertiesUtil("companydate.properties");
        //所有名字
        Set<String> companyNamesSet = new HashSet<String>();
        List<ArrayList<ArrayList<Object>>> excels = new ArrayList<ArrayList<ArrayList<Object>>>();

        for(int i=1;i<=6;i++){
            String inpath = companyPropertiesUtil.getProperty("data.file."+i);
            //查询,所有的名字
            File dir = new File(inpath);
            File[] files = dir.listFiles();
            if(files!=null){
                for(int k=0;k<files.length;k++){
                    File file = files[k];
                    if(file.getName().endsWith("xls")){
                        ArrayList<ArrayList<Object>> nameLists = ExcelUtil.readExcel(file);
                        excels.add(nameLists);
                    }
                }
            }
        }
        //写入
        HSSFWorkbook hs = new HSSFWorkbook();
        HSSFSheet sheet1 = hs.createSheet("sheet1");
        int index = 0;
        int k = 2;
        for(int i=0;i<excels.size();i++){
            ArrayList<ArrayList<Object>> arrayLists = excels.get(i);
            for(int tmp=1;tmp<arrayLists.size();tmp++){

                ArrayList<Object> arrayList = arrayLists.get(tmp);
                if(!check(arrayList)){
                    continue;
                }
                HSSFRow row = sheet1.createRow(index++);
                int col = 0;
                for(Object obj:arrayList){
                    row.createCell(col++).setCellValue(obj.toString().trim());
                }
                if(index>=65536){
                    index=0;
                    sheet1 = hs.createSheet("sheet"+k++);
                }
            }
        }
//        ExcelUtil.writeSteamToExcel(hs,"D:\\\\projects\\\\companydata\\\\one.xls");

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet11 = wb.createSheet("sheet1");
        index=0;
        for(List<String> str:companyNameList){
            HSSFRow row = sheet11.createRow(index++);
            row.createCell(0).setCellValue(str.get(0));
            row.createCell(1).setCellValue(str.get(1));
        }

        ExcelUtil.writeSteamToExcel(wb,"D:\\\\projects\\\\companydata\\\\one_count200.xls");
    }

    private static String name = "";
    private static String companyName = "";
    private static int count = 0;
    private static List<ArrayList<String>> companyNameList = new ArrayList<ArrayList<String>>();
    private static boolean check(ArrayList<Object> arrayList) {
        if(arrayList.size()<5){
            return false;
        }
//        if("".equals(arrayList.get(4).toString().trim())){
//            return false;
//        }
        if(name.equals(arrayList.get(1).toString().trim())){
            count++;
        }else{
//            if(count==60||count==59||count==61||count==99||count==100||count==101||count==200||count==201||count==199||count==40){
            if(count==60||count==59||count==61||count==40||count>=59){
                //TODO 说明是限制了的企业
                ArrayList<String> list = new ArrayList<String>();
                list.add(companyName);
                list.add(name);
                companyNameList.add(list);
            }
            name = arrayList.get(1).toString().trim();
            companyName = arrayList.get(0).toString().trim();
            count=1;
        }
        return true;
    }
}
