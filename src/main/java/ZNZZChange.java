import Utils.ExcelUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ZNZZChange {
    private static  int changeCompanyIndustryAndClass = 1000;

    public static Map<String,String> classCodeMap = new HashMap<>();
    public static Map<String,String> industryCodeMap = new HashMap<>();

    public static void main(String[] args){
        ArrayList<ArrayList<Object>> companyList = ExcelUtil.readExcel(new File("D:\\xxy_book\\文件\\服务商数据201904011-20190416.xls")); //new ArrayList<ArrayList<ArrayList<Object>>>();
        ArrayList<ArrayList<Object>> classCode = ExcelUtil.readExcel(new File("D:\\xxy_book\\文件\\cube_code_value.xls")); //new ArrayList<ArrayList<ArrayList<Object>>>();
        ArrayList<ArrayList<Object>> industryCode = ExcelUtil.readExcel(new File("D:\\xxy_book\\文件\\cube_code_value(2).xls")); //new ArrayList<ArrayList<ArrayList<Object>>>();
        for(ArrayList<Object> tmp : classCode){
            String code = tmp.get(2).toString();
            String name = tmp.get(3).toString();
            classCodeMap.put(name,code);
        }
        for(ArrayList<Object> tmp : industryCode){
            String code = tmp.get(2).toString();
            String name = tmp.get(3).toString();
            industryCodeMap.put(name,code);
        }
        for(ArrayList<Object> company : companyList){
            String name = company.get(1).toString().trim();
            if(!"".equals(name)){
                StringBuffer classC = new StringBuffer();
                String[] className = company.get(5).toString().trim().split("，");
                for(int i=0;i<className.length;i++){
                    String classCodeByName = getClassCodeByName(className[i].trim());
                    classC.append(classCodeByName);
                    if(i<className.length-1&&!"".equals(classCodeByName)){
                        classC.append(",");
                    }
                }
                String sql = "update t_enterprise  set t_enterprise.industry = '" + classC.toString()  +"' , t_enterprise.sort = "+ changeCompanyIndustryAndClass-- +"  where t_enterprise.name = '" + name +"' ;";
                if("".equals(classC.toString())){
                    int k=1;
                }else{
                   //System.out.println(sql);
                }
                //变化案例
                StringBuffer industry = new StringBuffer();

                String[] industryName = company.get(4).toString().trim().split("，");
                for(int i=0;i<industryName.length;i++){
                    String classCodeByName = getIndustryCodeByName(industryName[i].trim());
                    industry.append(classCodeByName);
                    if(i<className.length-1&&!"".equals(classCodeByName)){
                        industry.append(",");
                    }
                }
                if("".equals(industry.toString())){
                    int k=1;
                    //可以从class里拿
                    {
                        StringBuffer tmpBuffer = new StringBuffer();
                        for(int i=0;i<className.length;i++){
                            String classCodeByName = getIndustryCodeByName(className[i].trim());
                            tmpBuffer.append(classCodeByName);
                            if(i<className.length-1&&!"".equals(classCodeByName)){
                                tmpBuffer.append(",");
                            }
                        }
                        if("".equals(tmpBuffer.toString())){
                           int k1=2;
                        }else{
                            String sqlindustry = "UPDATE t_case_maintain, t_enterprise SET t_case_maintain.industry_type = '"+ tmpBuffer.toString() +"' " +
                                    "WHERE t_enterprise.name = '"+ name +"' AND t_case_maintain.facilitator_id = t_enterprise.id;";
                            System.out.println(sqlindustry);
                        }

                    }
                }else{
                    //System.out.println(sql);
                    String sqlindustry = "UPDATE t_case_maintain, t_enterprise SET t_case_maintain.industry_type = '"+ industry.toString() +"' " +
                            "WHERE t_enterprise.name = '"+ name +"' AND t_case_maintain.facilitator_id = t_enterprise.id;";
                    System.out.println(sqlindustry);
                }

            }
        }
    }

    private static String getClassCodeByName(String s) {
        if(classCodeMap.containsKey(s)){
            return  classCodeMap.get(s);
        }
        return "";
    }
    private static String getIndustryCodeByName(String s) {
        if(industryCodeMap.containsKey(s)){
            return  industryCodeMap.get(s);
        }
        return "";
    }
}
