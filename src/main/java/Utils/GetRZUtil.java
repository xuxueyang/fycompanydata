package Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetRZUtil {
    private static Map<String,String> map = new HashMap<String, String>();
    {
        ArrayList<ArrayList<Object>> excels1 = ExcelUtil.readExcel(new File("D:\\projects\\companydata\\one1_rz.xls"));
        ArrayList<ArrayList<Object>> excels2 = ExcelUtil.readExcel(new File("D:\\projects\\companydata\\one2_rz.xls"));
        ArrayList<ArrayList<Object>> excels3 = ExcelUtil.readExcel(new File("D:\\projects\\companydata\\one3_rz.xls"));
        List<ArrayList<ArrayList<Object>>> excel = new ArrayList<ArrayList<ArrayList<Object>>>();
        excel.add(excels1);
        excel.add(excels2);
        excel.add(excels3);
        for(int i=0;i<excel.size();i++){
            ArrayList<ArrayList<Object>> arrayLists = excel.get(i);
            for(ArrayList<Object> objects:arrayLists){
                String trim = objects.get(0).toString().trim();
                String substring = trim.substring(trim.indexOf("C:"), trim.length());
                String job = substring.substring(2, substring.length());
                String companyName = trim.substring(2, trim.indexOf("B:"));
                String name = trim.substring(trim.indexOf("B:")+2, trim.indexOf("C:"));
                map.put(companyName+"_"+name,job);
            }
        }
    }
//    public static void main(String[] args){
//        String str = "A:北京隆泰汇力经贸有限公司B:周彬C:";
//
//    }
    public static String getRZ(String companyName,String name){
        if(map.containsKey(companyName+"_"+name)){
            return map.get(companyName+"_"+name);
        }
        return "";
    }
}
