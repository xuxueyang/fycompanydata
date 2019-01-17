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
//        String str = "A:����¡̩������ó���޹�˾B:�ܱ�C:";
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
            //д�������ı�
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
//                            //��˾
//                            if(name.contains("�ϻ�")||name.contains("��˾")){
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
            case "bj": return "����";
            case "js": return "����";
            case "sh": return "�Ϻ�";
            case "fj": return "����";
            case "gd": return "�㶫";
            case "gx": return "����";
            case "gz": return "����";
            case "tj": return "���";
            case "hen": return "����";
            case "jl": return "����";
            case "zj": return "�㽭";
            case "ah": return "����";
            case "hun": return "����";
            case "cq": return "����";
            case "nmg": return "���ɹ�";
            case "ln": return "����";
            case "hb": return "�ӱ�";
            case "hub": return "����";
            case "yn": return "����";
            case "sd": return "ɽ��";
            case "gs": return "����";
            case "xj": return "�½�";
            case "hk": return "���";
            case "hlj": return "������";
            case "heb": return "�ӱ�";
            case "sx": return "ɽ��";
            case "sc": return "�Ĵ�";
            case "snx": return "����";
            case "nx": return "����";
            case "xz": return "����";
            case "jx": return "����";
            case "gj": return "����";
            case "han": return "����";
            case "qh": return "�ຣ";
            case "hn": return "����";

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
                    //˵��������
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
                //���µ��ˡ�����
                row.createCell(0).setCellValue("�ܼ�");
                row.createCell(1).setCellValue(""+namecount+"��");
                row.createCell(2).setCellValue(""+count+"��");
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
