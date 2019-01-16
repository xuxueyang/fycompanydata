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

public class reduce {
    private static ArrayList<ArrayList<Object>> count200;
    public static void main(String[] args){
        count200 = ExcelUtil.readExcel(new File("D:\\\\projects\\\\companydata\\\\one_count200.xls"));


        PropertiesUtil companyPropertiesUtil = new PropertiesUtil("companydate.properties");
        //��������
        Set<String> companyNamesSet = new HashSet<String>();
        List<ArrayList<ArrayList<Object>>> excels = new ArrayList<ArrayList<ArrayList<Object>>>();

        for(int i=1;i<=6;i++){
            String inpath = companyPropertiesUtil.getProperty("data.file."+i);
            //��ѯ,���е�����
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
        //д��
        HSSFWorkbook hs = new HSSFWorkbook();
        HSSFSheet sheet1 = hs.createSheet("sheet1");
        int index = 0;
        int k = 1;
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
                    ExcelUtil.writeSteamToExcel(hs,"D:\\\\projects\\\\companydata\\\\one"+ k++ +".xls");
                    hs = new HSSFWorkbook();
                    sheet1 = hs.createSheet("sheet");
                    index=0;
//                    sheet1 = hs.createSheet("sheet"+k++);
                }
            }
        }
        ExcelUtil.writeSteamToExcel(hs,"D:\\\\projects\\\\companydata\\\\one"+ k++ +".xls");

    }

    private static boolean check(ArrayList<Object> arrayList) {
        //�ж��ڲ�������
        boolean has = false;
        for(ArrayList<Object> dic:count200){
            if(dic.get(0).toString().trim().equals(arrayList.get(0).toString().trim())
                    && dic.get(1).toString().trim().equals(arrayList.get(1).toString().trim())){
                has=true;
                break;
            }
        }
        if(!has)
            return true;
        if("".equals(arrayList.get(4).toString().trim())){
            return false;
        }
        if("".equals(arrayList.get(5).toString().trim())){
            return false;
        }
        // �жϵ�ͼ
        String base = arrayList.get(3).toString().trim();
        String companyName = arrayList.get(0).toString().trim();
        if(companyName.contains("����")&&!"bj".equals(base)){
            return false;
        }
        if(companyName.contains("����")&&!"js".equals(base)){
            return false;
        }
        if(companyName.contains("�Ϻ�")&&!"sh".equals(base)){
            return false;
        }
        if(companyName.contains("����")
                &&!"fj".equals(base)
                &&!"bj".equals(base)
                &&!"tj".equals(base)
                &&!"sh".equals(base)){
            return false;
        }
        if(companyName.contains("����")
                &&!"hub".equals(base)
                &&!"bj".equals(base)
                &&!"tj".equals(base)
                &&!"sh".equals(base)){
            return false;
        }
        if(companyName.contains("����")&&!"gz".equals(base)&&!"gd".equals(base)&&!"gx".equals(base)){
            return false;
        }
        if(companyName.contains("���")&&(!"tj".equals(base)&&!"bj".equals(base))){
            return false;
        }
        if("����".equals(arrayList.get(6).toString().trim())
                || "����,δע��".equals(arrayList.get(6).toString().trim())
                || "������δע��".equals(arrayList.get(6).toString().trim())
                || "ע��".equals(arrayList.get(6).toString().trim())
                || "ע����ҵ".equals(arrayList.get(6).toString().trim())){
            return false;
        }
        return true;
    }
}
