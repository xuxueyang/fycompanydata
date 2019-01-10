import BaseSql.Mysql.CompanyDataEntity.Company;
import BaseSql.Mysql.CompanyDataEntity.CompanyNameToLegalPerson;
import BaseSql.Mysql.CompanyDataEntity.CompanyPerson;
import BaseSql.Mysql.Manager;
import Utils.ExcelUtil;
import Utils.PropertiesUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public class Main {
    public static void main(String[] args){
        PropertiesUtil propertiesUtil = new PropertiesUtil("db.properties");
        String url = propertiesUtil.getProperty("jdbc.url");
        String root = propertiesUtil.getProperty("jdbc.username");
        String passowrd = propertiesUtil.getProperty("jdbc.password");
        PropertiesUtil companyPropertiesUtil = new PropertiesUtil("companydate.properties");
        String inpath = companyPropertiesUtil.getProperty("in.companyname.file.path")+companyPropertiesUtil.getProperty("in.companyname.file.name");
        String outpathname = companyPropertiesUtil.getProperty("out.companydata.file.path")+companyPropertiesUtil.getProperty("out.companyname.file.name");
        String outpathdata = companyPropertiesUtil.getProperty("out.companydata.file.path")+companyPropertiesUtil.getProperty("out.companydata.file.name");
        Manager manager = null;
        try {
            manager = new Manager(url,root,passowrd);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        // ��ҵ����
        ArrayList<ArrayList<Object>> arrayLists = ExcelUtil.readExcel(new File(inpath));
//        System.out.println(arrayLists);
        HSSFWorkbook wb = new HSSFWorkbook();;
        int ii=0;
        int index = 0;
        Statement execute = null;
        try {
            execute = manager.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        Set<String> companyNames = new HashSet<String>();

        //        ����ѯ��˾	��ҵ����/Ͷ����	��˾	������ʡ����д��	����ʱ��	ע���ʱ�	״̬

        //TODO һЩHashMap���������������ݣ�������Ҫ����һЩ���Ѿ������˾
        // ������ҵID����Ӧ����ҵ
        Map<String ,List<CompanyNameToLegalPerson>> namepersonListMap = new HashMap<String ,List<CompanyNameToLegalPerson>>();
        Map<String,CompanyPerson> idCompanyPerson = new HashMap<String, CompanyPerson>();


        for(ArrayList<Object> arrayList:arrayLists){
            String  companyName = arrayList.get(0).toString().trim();
            if(companyNames.contains(companyName)){
                continue;
            }
            companyNames.add(companyName);
            ii++;
            if(ii%50==0){
                //д��֮ǰ��
                ExcelUtil.writeSteamToExcel(wb,outpathdata.replace("{index}",""+index));
                wb = new HSSFWorkbook();
                index++;
            }
            System.out.println("AA����������������������������������ʼ����"+ ii +"������:"+ companyName +"��������������������������������");
            //������ҵ���֣���ȡ������ҵ��ID id����ҵ�ķ������� legal_persion_name ����ҵ����ID legal_persion_id
            String companyNameToLegalPerson = "select * from company t where t.`name`='"+ companyName + "';";
//            Field[] declaredFields =  new CompanyNameToLegalPerson().getClass().getDeclaredFields();
            // ������ҵID��ȡ����Ա����


//            ID	��˾	������ʡ����д��	����ʱ��	ע���ʱ�	״̬
            try {
                List<CompanyNameToLegalPerson> entityList = null;
                ResultSet resultSet = null;
                if(namepersonListMap.containsKey(companyName)){
                    entityList = namepersonListMap.get(companyName);
                }else{
                    resultSet = execute.executeQuery(companyNameToLegalPerson);
                    entityList = new ArrayList<CompanyNameToLegalPerson>();

                    while (resultSet.next()){
                        CompanyNameToLegalPerson entity1 = new CompanyNameToLegalPerson();
                        entity1.setId(resultSet.getString("id"));
                        entity1.setBase(resultSet.getString("base"));
                        entity1.setLegal_person_id(resultSet.getString("legal_person_id"));
                        entity1.setLegal_person_name(resultSet.getString("legal_person_name"));
                        entityList.add(entity1);
                    }
                    namepersonListMap.put(companyName,entityList);

                }


                for(CompanyNameToLegalPerson entity1:entityList){
                    HSSFSheet sheet = null;
                    try {
                        sheet = wb.createSheet(companyName);
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                        sheet = null;
                    }
                    if(sheet==null){
                        continue;
                    }
                    int rowIndex=0;
                    HSSFRow row = sheet.createRow(rowIndex++);
                    row.createCell(0).setCellValue("��ҵ����");
                    row.createCell(1).setCellValue(entity1.getLegal_person_id());
                    row.createCell(2).setCellValue(entity1.getLegal_person_name());

                    // ��ҵ���ˣ� ��ҵ�������֣� ��ҵ����ID
                    String companyPerson = "select h.name,c.* from company_investor c LEFT JOIN human h ON h.id=c.investor_id where c.company_id = " + entity1.getId();
                    resultSet = execute.executeQuery(companyPerson);
                    List<CompanyPerson> companyPersonList = new ArrayList<CompanyPerson>();
                    while (resultSet.next()){
                        CompanyPerson companyPerson1 = new CompanyPerson();
                        companyPerson1.setId(resultSet.getString("id"));
                        companyPerson1.setName(resultSet.getString("name"));
                        companyPerson1.setInvestor_type(resultSet.getString("investor_type"));
                    }

                    //����Ͷ���ˣ������������Ĺ�˾�������ڹ�˾��ְλ
                    for(int cell=0;cell<companyPersonList.size();cell++){

                        String getCompanysByPerson = "SELECT t.`name`,t.base,t.estiblish_time,t.reg_capital,t.reg_status FROM company t "
                                +"LEFT JOIN company_investor c on t.id = c.company_id LEFT JOIN company_category b on t.id = b.company_id where "
                                +"c.investor_id="+ companyPersonList.get(cell).getId() +"  AND c.investor_type='"+ companyPersonList.get(cell).getInvestor_type() +"'    LIMIT 100;";
                        resultSet = execute.executeQuery(getCompanysByPerson);
                        while (resultSet.next()){
                            Company company = new Company();
                            company.setBase(resultSet.getString("base"));
                            company.setName(resultSet.getString("name"));
                            company.setEstiblish_time(resultSet.getString("estiblish_time"));
                            company.setReg_capital(resultSet.getString("reg_capital"));
                            company.setReg_status(resultSet.getString("reg_status"));
                            companyPersonList.get(cell).getCompanyList().add(company);
                        }


                    }

                }
            } catch (SQLException e) {
                e.printStackTrace();

            }
            System.out.println("BB����������������������������������������"+ ii +"������:"+ companyName +"��������������������������������");
        }
        manager.close();
    }
}
