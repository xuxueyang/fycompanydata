package Utils;

import BaseSql.Mysql.CompanyDataEntity.CompanyNameToLegalPerson;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.util.*;

/**
 * 处理最后的结果，比如，(1)换公司名字(3)因为这时候是法人所以设置为执行董事,同时设置后面的公司信息为该公司和(2)判断是不是法人和投资人
 */
public class EndSolveUtil {
    public static void main(String[] args){
//        qc("D:\\projects\\companydata\\tmp0.1.xlsx","D:\\projects\\companydata\\tmp0.1.0.xls");
//        changeName("D:\\projects\\companydata\\tmp0.1.0.xls","D:\\projects\\companydata\\tmp0.1.1.xls");
//        qc("D:\\projects\\companydata\\tmp0.1.1.xls","D:\\projects\\companydata\\tmp0.1.2.xls");
//        addRZ("D:\\projects\\companydata\\tmp0.1.2.xls","D:\\projects\\companydata\\tmp0.1.3.xls");
//          qc("D:\\projects\\companydata\\tmp0.1.3.xls","D:\\projects\\companydata\\tmp0.1.4.xls");
//        isFR("D:\\projects\\companydata\\tmp0.1.4.xls","D:\\projects\\companydata\\tmp0.1.5.xls");
//          qc("D:\\projects\\companydata\\tmp0.1.5.xls","D:\\projects\\companydata\\tmp0.1.6.xls");
//            qcAC("D:\\projects\\companydata\\tmp0.1.6.xls","D:\\projects\\companydata\\tmp0.1.7..xls");
//        getNoCompany("D:\\projects\\companydata\\Data\\3241.xlsx","D:\\projects\\companydata\\20190117\\has.xls","D:\\projects\\companydata\\20190117\\no.xls");
//        getNoCompanyData("D:\\\\projects\\\\companydata\\\\20190117\\\\no.xls","D:\\\\projects\\\\companydata\\\\20190117\\\\noData.xls");
//        changeName("D:\\\\projects\\\\companydata\\\\20190117\\\\noData.xls","D:\\\\projects\\\\companydata\\\\20190117\\\\noData.0.1.xls");
        isFR("D:\\projects\\companydata\\20190117\\generExcel1547696300.xls","D:\\projects\\companydata\\20190117\\tmp0.1.1.xls");
        changeBase("D:\\projects\\companydata\\20190117\\tmp0.1.1.xls","D:\\projects\\companydata\\20190117\\tmp0.1.2.xls");
        qcAC("D:\\projects\\companydata\\20190117\\tmp0.1.2.xls","D:\\projects\\companydata\\20190117\\tmp1.1.2.xls");
    }
    private static void changeBase(String path1,String path2){
//        GetRZUtil.changeBase(legalNameByCompanyName.getBase()
        ArrayList<ArrayList<Object>> arrayListsIn = ExcelUtil.readExcel(new File(path1));

        List<String> nameList = new ArrayList<>();
        HSSFWorkbook hs = new HSSFWorkbook();
        HSSFSheet sheet1 = hs.createSheet("原");
        int index =0;

        for(int i=0;i<arrayListsIn.size();i++){
            ArrayList<Object> arrayList = arrayListsIn.get(i);
            HSSFRow row = sheet1.createRow(index++);
            for(int j=0;j<arrayList.size();j++){
                if(j==3){
                    row.createCell(j).setCellValue(GetRZUtil.changeBase(arrayList.get(j).toString().trim()));
                }else{
                    row.createCell(j).setCellValue(arrayList.get(j).toString().trim());
                }
            }
        }
        ExcelUtil.writeSteamToExcel(hs,path2);
    }
    private static void getNoCompanyData(String path1,String path2){
        ArrayList<ArrayList<Object>> arrayListsIn = ExcelUtil.readExcel(new File(path1));
        PropertiesUtil companyPropertiesUtil = new PropertiesUtil("companydate.properties");
        List<String> nameList = new ArrayList<>();
        for(ArrayList arrayList:arrayListsIn){
            String trim = arrayList.get(0).toString().trim();
            nameList.add(trim);
        }
        //所有名字
        List<ArrayList<ArrayList<Object>>> excels = new ArrayList<ArrayList<ArrayList<Object>>>();

        for(int i=1;i<=6;i++){
            String inpath = companyPropertiesUtil.getProperty("data.file."+i);
            //查询,所有的名字
            File dir = new File(inpath);
            File[] files = dir.listFiles();
            if(files!=null){
                for(int k=0;k<files.length;k++){
                    File file = files[k];
                    if(file.getName().endsWith("xls")||file.getName().endsWith("xlsx")){
                        ArrayList<ArrayList<Object>> nameLists = ExcelUtil.readExcel(file);
                        excels.add(nameLists);
                    }
                }
            }
        }
        HSSFWorkbook hs = new HSSFWorkbook();
        int sheet = 0;
        int index = 0;
        HSSFSheet sheet1 = hs.createSheet("sheet"+sheet++);
        for(int i=0;i<excels.size();i++) {
            ArrayList<ArrayList<Object>> arrayLists = excels.get(i);
            for (int tmp = 1; tmp < arrayLists.size(); tmp++) {
                ArrayList<Object> arrayList = arrayLists.get(tmp);
                if(nameList.contains(arrayList.get(0).toString().trim())){
                    //记录
                    int col=0;
                    //去掉那些吊销什么的公司
                    String name = arrayList.get(0).toString().trim();
                    String base = arrayList.get(3).toString().trim();
                    String time = arrayList.get(4).toString().trim();
                    String money = arrayList.get(5).toString().trim();
                    String status = arrayList.get(6).toString().trim();
                    if(StringUtils.isBlank(time)||StringUtils.isBlank(money)){
                        continue;
                    }
                    if(name.contains("北京")&&!"bj".equals(base)){
                        continue;
                    }
                    if(name.contains("江苏")&&!"js".equals(base)){
                        continue;
                    }
                    if(name.contains("上海")&&!"sh".equals(base)){
                        continue;
                    }
                    if("吊销".equals(status)
                            || "吊销,未注销".equals(status)
                            || "吊销，未注销".equals(status)
                            || "注销".equals(status)
                            || "注销企业".equals(status)){
                        continue;
                    }
                    HSSFRow row = sheet1.createRow(index++);
                    for(Object object:arrayList){
                        row.createCell(col++).setCellValue(object.toString().trim());
                    }
                }
            }
            if(index>50000){
                index = 0;
                sheet1 = hs.createSheet("sheet"+sheet++);
            }
        }
        ExcelUtil.writeSteamToExcel(hs,path2);

    }
    private static void getNoCompanyName(String path1,String path2,String outPath){
        ArrayList<ArrayList<Object>> arrayListsIn = ExcelUtil.readExcel(new File(path1));
        ArrayList<ArrayList<Object>> arrayListsOut = ExcelUtil.readExcel(new File(path2));

        List<String> nameList = new ArrayList<>();
        HSSFWorkbook hs = new HSSFWorkbook();
        HSSFSheet sheet1 = hs.createSheet("原");
        int index =0;
        for(int i=0;i<arrayListsOut.size();i++){
            ArrayList<Object> arrayList = arrayListsOut.get(i);
            String trim = arrayList.get(0).toString().trim();
            nameList.add(trim);
        }

        for(int i=0;i<arrayListsIn.size();i++){
            ArrayList<Object> arrayList = arrayListsIn.get(i);
            if(nameList.contains(arrayList.get(0).toString().trim())){

            }else{
                HSSFRow row = sheet1.createRow(index++);
                for(int j=0;j<arrayList.size();j++){
                    row.createCell(j).setCellValue(arrayList.get(j).toString().trim());
//                    System.out.println(arrayList.get(j).toString().trim());
                }
            }
        }
        ExcelUtil.writeSteamToExcel(hs,outPath);

    }
    //TODO 添加职责
    private static void addRZ(String path1,String path2){
        ArrayList<ArrayList<Object>> arrayLists = ExcelUtil.readExcel(new File(path1));

        List<String> nameList = new ArrayList<>();
        HSSFWorkbook hs = new HSSFWorkbook();
        HSSFSheet sheet1 = hs.createSheet("原");
        int index =0;
        for(int i=0;i<arrayLists.size();i++){
            ArrayList<Object> arrayList = arrayLists.get(i);

            if(arrayList.size()<=7||"".equals(arrayList.get(7).toString().trim())){
                //缺少任职的
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
    //TODO 判断是不是法人
    private static void isFR(String path1,String path2){
        ArrayList<ArrayList<Object>> arrayLists = ExcelUtil.readExcel(new File(path1));
        List<String> nameList = new ArrayList<>();
        HSSFWorkbook hs = new HSSFWorkbook();
        HSSFSheet sheet1 = hs.createSheet("原");
        int index =0;
        for(int i=0;i<arrayLists.size();i++){
            ArrayList<Object> arrayList = arrayLists.get(i);
            String name2=arrayList.get(1).toString().trim();
            String name3=arrayList.get(0).toString().trim();
            if(!name2.contains("(法人)")){
                String legalNameByCompany = null;
                if(!hasLegalName(name2)){
                    //就按照执行董事
                    legalNameByCompany = getLegalNameByCompany(name3);
                }else{
                    //判断是不是执行董事
                    if(arrayList.size()>=8&&arrayList.get(7).toString().trim().contains("执行董事")){
                        legalNameByCompany = name2;
                    }else{
                        legalNameByCompany = "";
                    }
                }

                if(legalNameByCompany.equals(name2)){
                    //说明是法人
                    HSSFRow row = sheet1.createRow(index++);
                    for(int j=0;j<arrayList.size();j++){
                        if(j==1){
                            row.createCell(j).setCellValue(arrayList.get(j).toString().trim()+"(法人)");
                        }else {
                            row.createCell(j).setCellValue(arrayList.get(j).toString().trim());
                        }
                    }
                }else {
                    //视为投资人
                    HSSFRow row = sheet1.createRow(index++);
                    for(int j=0;j<arrayList.size();j++){
                        if(j==1){
                            row.createCell(j).setCellValue(arrayList.get(j).toString().trim()+"(投资人)");
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
    //ABC作为key，但是AC不能相同
    private static void qcAC(String path1,String path2){
        ArrayList<ArrayList<Object>> arrayLists = ExcelUtil.readExcel(new File(path1));

        List<String> nameList = new ArrayList<>();
        HSSFWorkbook hs = new HSSFWorkbook();
        Set<String> nameAll = new HashSet<>();
        Set<String> nameHas = new HashSet<>();
        HSSFSheet sheet1 = hs.createSheet("原");
        int index =0;
        for(int i=0;i<arrayLists.size();i++){
            ArrayList<Object> arrayList = arrayLists.get(i);
            String name1=arrayList.get(0).toString().trim();
            String name2=arrayList.get(1).toString().trim();
            String name3=arrayList.get(2).toString().trim();
            nameAll.add(name1);
            if(nameList.contains(name1+name2+name3)){
                //不做处理
            }else{
                if(!name1.equals(name3)){
                    nameList.add(name1+name2+name3);
                    HSSFRow row = sheet1.createRow(index++);
                    nameHas.add(name1);
                    for(int j=0;j<arrayList.size();j++){
                        row.createCell(j).setCellValue(arrayList.get(j).toString().trim());
                    }
                }
            }
        }
        //TODO 打印一样的
        int k1=0;
        int k2=0;
        for(String str:nameAll){
            if(nameHas.contains(str)){
                k1++;
            }else{
                k2++;
                System.out.println(str);
            }
        }
        System.out.println("有："+k1);
        System.out.println("无:"+k2);
        ExcelUtil.writeSteamToExcel(hs,path2);
    }
    //TODO 去重
    private static void qc(String path1,String path2){
        ArrayList<ArrayList<Object>> arrayLists = ExcelUtil.readExcel(new File(path1));

        List<String> nameList = new ArrayList<>();
        HSSFWorkbook hs = new HSSFWorkbook();

        HSSFSheet sheet1 = hs.createSheet("原");
        int index =0;
        for(int i=0;i<arrayLists.size();i++){
            ArrayList<Object> arrayList = arrayLists.get(i);
            String name1=arrayList.get(0).toString().trim();
            String name2=arrayList.get(1).toString().trim();
            String name3=arrayList.get(2).toString().trim();
            if(nameList.contains(name1+name2+name3)){
                //不做处理
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
    //TODO 如果是公司名字，换成法人，且将后面的信息，换成该公司！
    private static void changeName(String path1,String path2){
        ArrayList<ArrayList<Object>> arrayLists = ExcelUtil.readExcel(new File(path1));
        HSSFWorkbook hs = new HSSFWorkbook();
        HSSFSheet sheet1 = hs.createSheet("原");
        int index =0;
        for(int i=0;i<arrayLists.size();i++){
            ArrayList<Object> arrayList = arrayLists.get(i);
            String name1=arrayList.get(0).toString().trim();
            String name2=arrayList.get(1).toString().trim();
            String name3=arrayList.get(2).toString().trim();
            if(name2.contains("企业")||name2.contains("公司")||name2.contains("合伙")){
                //改名成该公司对应的法人，且将后面的职责设置成执行董事、如果name2=name1，不做记录
                if(name1.equals(name2)){}
                else{
                    CompanyNameToLegalPerson legalNameByCompanyName = GetCompanyLegalName.getLegalNameByCompanyName(name2);
                    if(legalNameByCompanyName!=null&&!"".equals(legalNameByCompanyName.getLegal_person_name())){
                        HSSFRow row = sheet1.createRow(index++);
                        row.createCell(0).setCellValue(name1);
                        row.createCell(1).setCellValue(legalNameByCompanyName.getLegal_person_name()+"(法人)");
                        row.createCell(2).setCellValue(name2);
                        row.createCell(3).setCellValue(GetRZUtil.changeBase(legalNameByCompanyName.getBase()));
                        row.createCell(4).setCellValue(legalNameByCompanyName.getEstiblish_time());
                        row.createCell(5).setCellValue(legalNameByCompanyName.getReg_capital());
                        row.createCell(6).setCellValue(legalNameByCompanyName.getReg_status());
                        row.createCell(7).setCellValue("执行董事");
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
