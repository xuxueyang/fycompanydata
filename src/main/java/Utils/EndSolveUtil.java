package Utils;

import BaseSql.Mysql.CompanyDataEntity.CompanyNameToLegalPerson;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * �������Ľ�������磬(1)����˾����(3)��Ϊ��ʱ���Ƿ�����������Ϊִ�ж���,ͬʱ���ú���Ĺ�˾��ϢΪ�ù�˾��(2)�ж��ǲ��Ƿ��˺�Ͷ����
 */
public class EndSolveUtil {
    public static void main(String[] args){
//        qc("D:\\projects\\companydata\\tmp0.1.xlsx","D:\\projects\\companydata\\tmp0.1.0.xls");
//        changeName("D:\\projects\\companydata\\tmp0.1.0.xls","D:\\projects\\companydata\\tmp0.1.1.xls");
//        qc("D:\\projects\\companydata\\tmp0.1.1.xls","D:\\projects\\companydata\\tmp0.1.2.xls");
//        addRZ("D:\\projects\\companydata\\tmp0.1.2.xls","D:\\projects\\companydata\\tmp0.1.3.xls");
//          qc("D:\\projects\\companydata\\tmp0.1.3.xls","D:\\projects\\companydata\\tmp0.1.4.xls");
//        isFR("D:\\projects\\companydata\\tmp0.1.4.xls","D:\\projects\\companydata\\tmp0.1.5.xls");
          qc("D:\\projects\\companydata\\tmp0.1.5.xls","D:\\projects\\companydata\\tmp0.1.6..xls");

    }

    //TODO ���ְ��
    private static void addRZ(String path1,String path2){
        ArrayList<ArrayList<Object>> arrayLists = ExcelUtil.readExcel(new File(path1));

        List<String> nameList = new ArrayList<>();
        HSSFWorkbook hs = new HSSFWorkbook();
        HSSFSheet sheet1 = hs.createSheet("ԭ");
        int index =0;
        for(int i=0;i<arrayLists.size();i++){
            ArrayList<Object> arrayList = arrayLists.get(i);

            if(arrayList.size()<=7||"".equals(arrayList.get(7).toString().trim())){
                //ȱ����ְ��
                String name2=arrayList.get(1).toString().trim();
                String name3=arrayList.get(2).toString().trim();
                String rz = GetRZUtil.getRZ(name2, name3);
                HSSFRow row = sheet1.createRow(index++);
                for(int j=0;j<arrayList.size();j++){
                    if(j==7){
                        row.createCell(j).setCellValue(rz);
                    }else{
                        row.createCell(j).setCellValue(arrayList.get(j).toString().trim());
                    }
                }
            }else {
                HSSFRow row = sheet1.createRow(index++);
                for(int j=0;j<arrayList.size();j++){
                    row.createCell(j).setCellValue(arrayList.get(j).toString().trim());
                }
            }
        }
        ExcelUtil.writeSteamToExcel(hs,path2);
    }
    //TODO �ж��ǲ��Ƿ���
    private static void isFR(String path1,String path2){
        ArrayList<ArrayList<Object>> arrayLists = ExcelUtil.readExcel(new File(path1));
        List<String> nameList = new ArrayList<>();
        HSSFWorkbook hs = new HSSFWorkbook();
        HSSFSheet sheet1 = hs.createSheet("ԭ");
        int index =0;
        for(int i=0;i<arrayLists.size();i++){
            ArrayList<Object> arrayList = arrayLists.get(i);
            String name2=arrayList.get(1).toString().trim();
            String name3=arrayList.get(0).toString().trim();
            if(!name2.contains("(����)")){
                String legalNameByCompany = null;
                if(!hasLegalName(name2)){
                    //�Ͱ���ִ�ж���
                    legalNameByCompany = getLegalNameByCompany(name3);
                }else{
                    //�ж��ǲ���ִ�ж���
                    if(arrayList.size()>=8&&arrayList.get(7).toString().trim().contains("ִ�ж���")){
                        legalNameByCompany = name2;
                    }else{
                        legalNameByCompany = "";
                    }
                }

                if(legalNameByCompany.equals(name2)){
                    //˵���Ƿ���
                    HSSFRow row = sheet1.createRow(index++);
                    for(int j=0;j<arrayList.size();j++){
                        if(j==1){
                            row.createCell(j).setCellValue(arrayList.get(j).toString().trim()+"(����)");
                        }else {
                            row.createCell(j).setCellValue(arrayList.get(j).toString().trim());
                        }
                    }
                }else {
                    //��ΪͶ����
                    HSSFRow row = sheet1.createRow(index++);
                    for(int j=0;j<arrayList.size();j++){
                        if(j==1){
                            row.createCell(j).setCellValue(arrayList.get(j).toString().trim()+"(Ͷ����)");
                        }else {
                            row.createCell(j).setCellValue(arrayList.get(j).toString().trim());
                        }
                    }
                }
            }else{
                HSSFRow row = sheet1.createRow(index++);
                for(int j=0;j<arrayList.size();j++){
                    row.createCell(j).setCellValue(arrayList.get(j).toString().trim());
                }
            }
        }
        ExcelUtil.writeSteamToExcel(hs,path2);
    }

    //TODO ȥ��
    private static void qc(String path1,String path2){
        ArrayList<ArrayList<Object>> arrayLists = ExcelUtil.readExcel(new File(path1));

        List<String> nameList = new ArrayList<>();
        HSSFWorkbook hs = new HSSFWorkbook();
        HSSFSheet sheet1 = hs.createSheet("ԭ");
        int index =0;
        for(int i=0;i<arrayLists.size();i++){
            ArrayList<Object> arrayList = arrayLists.get(i);
            String name1=arrayList.get(0).toString().trim();
            String name2=arrayList.get(1).toString().trim();
            String name3=arrayList.get(2).toString().trim();
            if(nameList.contains(name1+name2+name3)){
                //��������
            }else{
                nameList.add(name1+name2+name3);
                HSSFRow row = sheet1.createRow(index++);
                for(int j=0;j<arrayList.size();j++){
                    row.createCell(j).setCellValue(arrayList.get(j).toString().trim());
                }
            }
        }
        ExcelUtil.writeSteamToExcel(hs,path2);
    }
    //TODO ����ǹ�˾���֣����ɷ��ˣ��ҽ��������Ϣ�����ɸù�˾��
    private static void changeName(String path1,String path2){
        ArrayList<ArrayList<Object>> arrayLists = ExcelUtil.readExcel(new File(path1));
        HSSFWorkbook hs = new HSSFWorkbook();
        HSSFSheet sheet1 = hs.createSheet("ԭ");
        int index =0;
        for(int i=0;i<arrayLists.size();i++){
            ArrayList<Object> arrayList = arrayLists.get(i);
            String name1=arrayList.get(0).toString().trim();
            String name2=arrayList.get(1).toString().trim();
            String name3=arrayList.get(2).toString().trim();
            if(name2.contains("��ҵ")||name2.contains("��˾")||name2.contains("�ϻ�")){
                //�����ɸù�˾��Ӧ�ķ��ˣ��ҽ������ְ�����ó�ִ�ж��¡����name2=name1��������¼
                if(name1.equals(name2)){}
                else{
                    CompanyNameToLegalPerson legalNameByCompanyName = GetCompanyLegalName.getLegalNameByCompanyName(name2);
                    if(legalNameByCompanyName!=null&&!"".equals(legalNameByCompanyName.getLegal_person_name())){
                        HSSFRow row = sheet1.createRow(index++);
                        row.createCell(0).setCellValue(name1);
                        row.createCell(1).setCellValue(legalNameByCompanyName.getLegal_person_name()+"(����)");
                        row.createCell(2).setCellValue(name2);
                        row.createCell(3).setCellValue(GetRZUtil.changeBase(legalNameByCompanyName.getBase()));
                        row.createCell(4).setCellValue(legalNameByCompanyName.getEstiblish_time());
                        row.createCell(5).setCellValue(legalNameByCompanyName.getReg_capital());
                        row.createCell(6).setCellValue(legalNameByCompanyName.getReg_status());
                        row.createCell(7).setCellValue("ִ�ж���");
                    }
                }
            }else{
                HSSFRow row = sheet1.createRow(index++);
                for(int j=0;j<arrayList.size();j++){
                    row.createCell(j).setCellValue(arrayList.get(j).toString().trim());
                }
            }
        }
        ExcelUtil.writeSteamToExcel(hs,path2);
    }
    private static Map<String,String> map = new HashMap<String, String>();
    private static boolean hasInit = false;
    private static void initMap(){
        ArrayList<ArrayList<Object>> excels1 = ExcelUtil.readExcel(new File("D:\\projects\\companydata\\Data\\3241_result.xls"));
        List<ArrayList<ArrayList<Object>>> excel = new ArrayList<ArrayList<ArrayList<Object>>>();
        excel.add(excels1);
        for(int i=0;i<excel.size();i++){
            ArrayList<ArrayList<Object>> arrayLists = excel.get(i);
            for(ArrayList<Object> objects:arrayLists){
                String trim = objects.get(0).toString().trim();
                if(!trim.contains("A:")){
                    continue;
                }
                String companyName = trim.substring(2, trim.indexOf("B:"));
                String name = trim.substring(trim.indexOf("B:")+2, trim.length());
                map.put(companyName,name);
            }
        }
    }
    private static boolean hasLegalName(String company) {
        if(!hasInit){
            initMap();
            hasInit=true;
        }
        if(StringUtils.isBlank(company))
            return false;
        if(map.containsKey(company)){
            return true;
        }
        return false;
    }
    private static String getLegalNameByCompany(String company){
        if(!hasInit){
            initMap();
            hasInit=true;
        }
        if(StringUtils.isBlank(company))
            return "";
        if(map.containsKey(company)){
            return map.get(company);
        }
        return "";
    }
}
