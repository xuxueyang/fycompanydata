package Utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetRZUtil {
    private static Map<String,String> map = new HashMap<String, String>();
    private static boolean hasInit = false;
    private static void initMap(){
        ArrayList<ArrayList<Object>> excels1 = ExcelUtil.readExcel(new File("D:\\projects\\companydata\\one1_rz.xlsx"));
        ArrayList<ArrayList<Object>> excels2 = ExcelUtil.readExcel(new File("D:\\projects\\companydata\\one2_rz.xlsx"));
        ArrayList<ArrayList<Object>> excels3 = ExcelUtil.readExcel(new File("D:\\projects\\companydata\\one3_rz.xlsx"));
        List<ArrayList<ArrayList<Object>>> excel = new ArrayList<ArrayList<ArrayList<Object>>>();
        excel.add(excels1);
        excel.add(excels2);
        excel.add(excels3);
        for(int i=0;i<excel.size();i++){
            ArrayList<ArrayList<Object>> arrayLists = excel.get(i);
            for(ArrayList<Object> objects:arrayLists){
                String trim = objects.get(0).toString().trim();
                if(!trim.contains("A:")){
                    continue;
                }
                String substring = trim.substring(trim.indexOf("C:"), trim.length());
                String job = substring.substring(2, substring.length());
                if(job.startsWith(",")){
                    job = job.substring(1,job.length());
                }
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

    public static void main(String[] args){
        ArrayList<ArrayList<Object>> excels1 = ExcelUtil.readExcel(new File("D:\\projects\\companydata\\one_11.xls"));
//        ArrayList<ArrayList<Object>> excels2 = ExcelUtil.readExcel(new File("D:\\projects\\companydata\\one_12.xls"));
        List<ArrayList<ArrayList<Object>>> excel = new ArrayList<ArrayList<ArrayList<Object>>>();
        excel.add(excels1);
//        excel.add(excels2);
        int index=0;
        HSSFWorkbook hs = new HSSFWorkbook();
        HSSFSheet sheet1 = hs.createSheet("sheet");
        int k=2;
        for(int i=0;i<excel.size();i++){
            ArrayList<ArrayList<Object>> arrayLists = excel.get(i);
            //写入最后的文本
            for(int tmp=1;tmp<arrayLists.size();tmp++){
                ArrayList<Object> arrayList = arrayLists.get(tmp);
//                if(arrayList.size()>=1&&arrayList.get(0).toString().contains("1")){
//                    int kkk=0;
//                }
                HSSFRow row = sheet1.createRow(index++);
                for(int col = 0;col<arrayList.size();col++){
                    if(col==3){
                        row.createCell(col).setCellValue(changeBase(arrayList.get(col).toString().trim()));
                    }else{
                        String name = arrayList.get(col).toString().trim();
//                        if(col==1){
//                            //公司
//                            if(name.contains("合伙")||name.contains("公司")){
//                                name=GetCompanyLegalName.getLegalNameByCompanyName(name);
//                            }
//                        }
                        row.createCell(col).setCellValue(name);
                    }
                }
                if(arrayList.size()<8&&arrayList.size()>=3){
                    row.createCell(7).setCellValue(getRZ(arrayList.get(2).toString().trim().toString(),arrayList.get(1).toString().trim().toString()));
                }else {
                    if(arrayList.size()>=8&&StringUtils.isBlank(arrayList.get(7).toString().trim())){
                        row.getCell(7).setCellValue(getRZ(arrayList.get(2).toString().trim().toString(),arrayList.get(1).toString().trim().toString()));
                    }
                }

                if(index>=65536){
                    index=0;
                    sheet1 = hs.createSheet("sheet"+k++);
                }
            }
        }
        ExcelUtil.writeSteamToExcel(hs,"D:\\projects\\companydata\\end.xls");
    }

    private static String name = "";
    private static String companyName = "";
    private static int count = 0;
    private static int namecount = 0;

    public static String changeBase(String base){
        if(StringUtils.isBlank(base))
            return "";
        switch (base){
            case "bj": return "北京";
            case "js": return "江苏";
            case "sh": return "上海";
            case "fj": return "福建";
            case "gd": return "广东";
            case "gx": return "广西";
            case "gz": return "广州";
            case "tj": return "天津";
            case "hen": return "河南";
            case "jl": return "吉林";
            case "zj": return "浙江";
            case "ah": return "安徽";
            case "hun": return "湖南";
            case "cq": return "重庆";
            case "nmg": return "内蒙古";
            case "ln": return "辽宁";
            case "hb": return "河北";
            case "hub": return "湖北";
            case "yn": return "云南";
            case "sd": return "山东";
            case "gs": return "甘肃";
            case "xj": return "新疆";
            case "hk": return "香港";
            case "hlj": return "黑龙江";
            case "heb": return "河北";
            case "sx": return "山西";
            case "sc": return "四川";
            case "snx": return "陕西";
            case "nx": return "宁夏";
            case "xz": return "西藏";
            case "jx": return "江西";
            case "gj": return "国家";
            case "han": return "海南";
            case "qh": return "青海";
            case "hn": return "河南";

            default: return base;
        }
    }
    public static void toushi(String path,String outpath){
        ArrayList<ArrayList<Object>> excel = ExcelUtil.readExcel(new File(path));
        HSSFWorkbook hs = new HSSFWorkbook();
        int index =0;
        HSSFSheet sheet = hs.createSheet("sheet");
        for(int i=0;i<excel.size();i++){
            ArrayList<Object> arrayList = excel.get(i);
            HSSFRow row = sheet.createRow(index++);
            if(companyName.equals(arrayList.get(0).toString().trim())){
                if(name.equals( arrayList.get(1).toString().trim())){
                    for(int k=0;k<arrayList.size();k++){
                        row.createCell(k).setCellValue(arrayList.get(k).toString().trim());
                        if((k==0||k==1)&&count>0){
                            row.createCell(k).setCellValue("");
                        }
                    }
                }else{
                    //说明换人了
                    for(int k=0;k<arrayList.size();k++){
                        row.createCell(k).setCellValue(arrayList.get(k).toString().trim());
                        if((k==0)&&count>0){
                            row.createCell(k).setCellValue("");
                        }
                    }
                    namecount++;
                }
                count++;
            }else{
                //换新的了。插入
                row.createCell(0).setCellValue("总计");
                row.createCell(1).setCellValue(""+namecount+"人");
                row.createCell(2).setCellValue(""+count+"家");
                companyName = arrayList.get(0).toString().trim();
                name = arrayList.get(1).toString().trim();
                count=0;
                namecount = 0;
            }
        }

    }
    public static String getRZ(String companyName,String name){
        if(!hasInit){
            initMap();
            hasInit = true;
        }
        if(map.containsKey(companyName+"_"+name)){
            return map.get(companyName+"_"+name);
        }
        return "";
    }
}
