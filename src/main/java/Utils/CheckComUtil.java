package Utils;

import java.util.ArrayList;
import java.util.List;

public class CheckComUtil {
    private static String name = "";
    private static String companyName = "";
    private static int count = 0;
    private static boolean hasRZ = false;
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
            //如果有任职，记录一下
            String company = arrayList.get(2).toString().trim();
            String people = arrayList.get(1).toString().trim();
            if(arrayList.size()>=8&&(!"".equals(arrayList.get(7).toString().trim())|| !"".equals(GetRZUtil.getRZ(company,people)))){
                hasRZ=true;
            }
        }else{
//            if(count==60||count==59||count==61||count==99||count==100||count==101||count==200||count==201||count==199||count==40){
            if(count>=20){
                //TODO 说明是限制了的企业
                ArrayList<String> list = new ArrayList<String>();

                list.add(companyName);
                list.add(name);
                if(hasRZ&&count>=20){
                    list.add("1");
                }else{
                    list.add("0");

                }
                list.add(""+count);
                companyNameList.add(list);
            }

            name = arrayList.get(1).toString().trim();
            companyName = arrayList.get(0).toString().trim();
            count=1;
        }
        return true;
    }
}
