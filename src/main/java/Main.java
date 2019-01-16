import BaseSql.Mysql.CompanyDataEntity.CompanyJob;
import BaseSql.Mysql.CompanyDataEntity.CompanyNameToLegalPerson;
import BaseSql.Mysql.CompanyDataEntity.CompanyPerson;
import BaseSql.Mysql.CompanyDataEntity.PersonInCompany;
import BaseSql.Mysql.Manager;
import Utils.ExcelUtil;
import Utils.MapBufferUtil;
import Utils.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.IOException;
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
        String buffPath = companyPropertiesUtil.getProperty("map.buff.file.path");

        Manager manager = null;
        try {
            manager = new Manager(url,root,passowrd);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        // ��ҵ����
        ArrayList<ArrayList<Object>> arrayLists = ExcelUtil.readExcel(new File(inpath));
        List<String> noCompanyList = new ArrayList<String>();
        List<String> hasCompanyList = new ArrayList<String>();
//        System.out.println(arrayLists);
        HSSFWorkbook wb = new HSSFWorkbook();;
        HSSFSheet sheet = wb.createSheet("����");
        int ii=0;
        int index = 0;
        Statement execute = null;
        Statement execute2 = null;
        try {
            execute = manager.createStatement();
            execute2 = manager.createStatement();

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        Set<String> companyNames = new HashSet<String>();

        //        ����ѯ��˾	��ҵ����/Ͷ����	��˾	������ʡ����д��	����ʱ��	ע���ʱ�	״̬

        //TODO һЩHashMap���������������ݣ�������Ҫ����һЩ���Ѿ������˾
        // ������ҵID����Ӧ����ҵ
        Map<String ,List<CompanyNameToLegalPerson>> namepersonListMap = (Map<String, List<CompanyNameToLegalPerson>>) MapBufferUtil.decodeMap(buffPath+"namepersonListMap.txt");
//        new HashMap<String ,List<CompanyNameToLegalPerson>>();
        // ���湫˾ID������
        Map<String,String> companyIdNameMap = (Map<String, String>) MapBufferUtil.decodeMap(buffPath+"companyIdNameMap.txt");//new HashMap<String, String>();
        Map<String,CompanyNameToLegalPerson> companyIdEntity = (Map<String, CompanyNameToLegalPerson>) MapBufferUtil.decodeMap(buffPath+"companyIdEntity.txt");//new HashMap<String, CompanyNameToLegalPerson>();
        Map<String,List<CompanyPerson>> idCompanyPerson = (Map<String, List<CompanyPerson>>) MapBufferUtil.decodeMap(buffPath+"idCompanyPerson.txt");//new HashMap<String, List<CompanyPerson>>();
        Map<String,List<CompanyJob>> companyJobMap = (Map<String, List<CompanyJob>>) MapBufferUtil.decodeMap(buffPath+"companyJobMap.txt");//new HashMap<String, List<CompanyJob>>();
        List<String> errorCompanyList = new ArrayList<String>();
        int rowIndex=0;
        //                    ����ѯ��˾	��ҵ����/Ͷ����	��˾	������ʡ����д��	����ʱ��	ע���ʱ�	״̬

        HSSFRow row = sheet.createRow(rowIndex++);
        row.createCell(0).setCellValue("����ѯ��˾");
        row.createCell(1).setCellValue("��ҵ����/Ͷ����");
        row.createCell(2).setCellValue("��˾");
        row.createCell(3).setCellValue("������ʡ����д��");
        row.createCell(4).setCellValue("����ʱ�䣩");
        row.createCell(5).setCellValue("ע���ʱ�");
        row.createCell(6).setCellValue("״̬");
        row.createCell(7).setCellValue("ְ��");
        for(ArrayList<Object> arrayList:arrayLists){
            String  companyName = arrayList.get(0).toString().trim();
            if(companyNames.contains(companyName)){
                continue;
            }
            if(StringUtils.isBlank(companyName)){
                continue;
            }
            companyNames.add(companyName);
            ii++;
            if(ii%300==0){
                //д��֮ǰ��
                //��Ŀǰ�Ѿ���ѯ����ҵд���Ѿ���ѯ�ı���

                ExcelUtil.writeSteamToExcel(wb,outpathdata.replace("{index}",""+index+"_"+ ii +""));
                wb = new HSSFWorkbook();
                sheet = wb.createSheet("����");
                rowIndex = 0;
                row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue("����ѯ��˾");
                row.createCell(1).setCellValue("��ҵ����/Ͷ����");
                row.createCell(2).setCellValue("��˾");
                row.createCell(3).setCellValue("������ʡ����д��");
                row.createCell(4).setCellValue("����ʱ�䣩");
                row.createCell(5).setCellValue("ע���ʱ�");
                row.createCell(6).setCellValue("״̬");
                row.createCell(7).setCellValue("ְ��");


                index++;
//                break;
            }
            //TODO �����Ѿ����HashMap
            if(ii%50==0){
                try {
                    MapBufferUtil.encodeMap(errorCompanyList,buffPath+"errorCompanyList.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    MapBufferUtil.encodeMap(companyIdEntity,buffPath+"companyIdEntity.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    MapBufferUtil.encodeMap(idCompanyPerson,buffPath+"idCompanyPerson.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    MapBufferUtil.encodeMap(companyIdNameMap,buffPath+"companyIdNameMap.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    MapBufferUtil.encodeMap(companyJobMap,buffPath+"companyJobMap.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    MapBufferUtil.encodeMap(namepersonListMap,buffPath+"namepersonListMap.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("AA����������������������������������ʼ����"+ ii +"������:"+ companyName +"��������������������������������");
            try {


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
                        entity1.setEstiblish_time(resultSet.getString("estiblish_time"));
                        entity1.setReg_capital(resultSet.getString("reg_capital"));
                        entity1.setReg_status(resultSet.getString("reg_status"));
                        entity1.setLegal_person_id(resultSet.getString("legal_person_id"));
                        entity1.setLegal_person_name(resultSet.getString("legal_person_name"));
                        entityList.add(entity1);
                        companyIdNameMap.put(entity1.getId(),companyName);
                    }
                    namepersonListMap.put(companyName,entityList);
                }
                if(entityList.size()==0){
                    //�����ҵ�Ĺ�����ҵ
                    System.out.println("����������������������������   ��"+ companyName +"��������������������������������������");
                    noCompanyList.add(companyName);

                }else{
                    hasCompanyList.add(companyName);
                }
                for(CompanyNameToLegalPerson entity1:entityList){
//                    HSSFSheet sheet = null;
//                    try {
//                        sheet = wb.createSheet(companyName);
//                    }catch (IllegalArgumentException e){
//                        e.printStackTrace();
//                        sheet = null;
//                    }
//                    if(sheet==null){
//                        continue;
//                    }


                    // ��ҵ���ˣ� ��ҵ�������֣� ��ҵ����ID
                    List<CompanyPerson> companyPersonList = null;
                    if(idCompanyPerson.containsKey(entity1.getId())){
                        companyPersonList = idCompanyPerson.get(entity1.getId());
                    }else{
                        String companyPerson = "select h.name,c.* from company_investor c LEFT JOIN human h ON h.id=c.investor_id where c.company_id = " + entity1.getId();
                        resultSet = execute.executeQuery(companyPerson);
                        companyPersonList = new ArrayList<CompanyPerson>();
                        while (resultSet.next()){
                            CompanyPerson companyPerson1 = new CompanyPerson();
                            companyPerson1.setId(resultSet.getString("id"));
                            companyPerson1.setInvestor_id(resultSet.getString("investor_id"));

                            //TODO ���typeΪ2����Ҫ�鹫˾���������Ϊ1��˵���鷨�˱�
                            companyPerson1.setInvestor_type(resultSet.getString("investor_type"));
                            if("2".equals(companyPerson1.getInvestor_type())){
                                //˵������ҵ����Ͷ����ҹ�˾�Ĺ�˾
                                //TODO id�����֣��ٲ�Ͷ�ʹ�˾��type=2
                                if(companyIdNameMap.containsKey(companyPerson1.getInvestor_id())){
                                    companyPerson1.setName(companyIdNameMap.get(companyPerson1.getInvestor_id()));
                                }else{
                                    String searchCompanyId = "select * from company t where t.id = " + companyPerson1.getInvestor_id();
                                    ResultSet resultSet3 = execute2.executeQuery(searchCompanyId);
                                    while (resultSet3.next()){
                                        //������ҵ����
                                        CompanyNameToLegalPerson entityCompany = new CompanyNameToLegalPerson();
                                        entityCompany.setId(resultSet3.getString("id"));
                                        entityCompany.setBase(resultSet3.getString("base"));
                                        entityCompany.setEstiblish_time(resultSet3.getString("estiblish_time"));
                                        entityCompany.setReg_capital(resultSet3.getString("reg_capital"));
                                        entityCompany.setReg_status(resultSet3.getString("reg_status"));
                                        entityCompany.setLegal_person_id(resultSet3.getString("legal_person_id"));
                                        entityCompany.setLegal_person_name(resultSet3.getString("legal_person_name"));
                                        companyIdEntity.put(entityCompany.getId(),entityCompany);

                                        companyIdNameMap.put(companyPerson1.getInvestor_id(),resultSet3.getString("name"));
                                        companyPerson1.setName(resultSet3.getString("name"));
                                    }
                                }
                            }else if("1".equals(companyPerson1.getInvestor_type())){
                                //˵������,�����ı�
                                companyPerson1.setName(resultSet.getString("name"));
                            }
                            companyPersonList.add(companyPerson1);
                        }
                        idCompanyPerson.put(entity1.getId(),companyPersonList);
                    }

                    // ���ݷ��˻�ȡ����Ϣ(�жϷ�����û����Ͷ�����У����û�У����һ����Ϊ��������������ݣ�
                    boolean has=false;
                    if(StringUtils.isNotBlank(entity1.getLegal_person_id())){
                        for(int cell=0;cell<companyPersonList.size();cell++){
                            if("1".equals(companyPersonList.get(cell).getInvestor_type())&entity1.getLegal_person_id().equals(companyPersonList.get(cell).getInvestor_id())){
                                has = true;
                            }
                        }
                    }else {
                        has=true;
                    }
                    if(!has){
                        CompanyPerson companyPerson = new CompanyPerson();
                        companyPerson.setId(null);
                        companyPerson.setName(entity1.getLegal_person_name());
                        companyPerson.setInvestor_id(entity1.getLegal_person_id());
                        companyPersonList.add(companyPerson);
                    }

                    //����Ͷ���ˣ������������Ĺ�˾�������ڹ�˾��ְλ
                    for(int cell=0;cell<companyPersonList.size();cell++){
                        if(StringUtils.isBlank(companyPersonList.get(cell).getInvestor_id())){
                            continue;
                        }
                        if("2".equals(companyPersonList.get(cell).getInvestor_type())){
                            // ��������Ͳ�Ͷ�ʵĹ�˾
                            List<CompanyPerson> companyCompanyList = null;
                            if(idCompanyPerson.containsKey(companyPersonList.get(cell).getInvestor_id())){
                                companyCompanyList = idCompanyPerson.get(companyPersonList.get(cell).getInvestor_id());
                            }else{
                                String companyPerson = "select h.name,c.* from company_investor c LEFT JOIN human h ON h.id=c.investor_id where c.investor_type='2' and c.company_id = " + companyPersonList.get(cell).getInvestor_id();
                                resultSet = execute.executeQuery(companyPerson);
                                companyCompanyList = new ArrayList<CompanyPerson>();
                                while (resultSet.next()){
                                    CompanyPerson companyPerson1 = new CompanyPerson();
                                    companyPerson1.setId(resultSet.getString("id"));
                                    companyPerson1.setInvestor_id(resultSet.getString("investor_id"));

                                    // ���typeΪ2����Ҫ�鹫˾���������Ϊ1��˵���鷨�˱�
                                    companyPerson1.setInvestor_type(resultSet.getString("investor_type"));
                                    if("2".equals(companyPerson1.getInvestor_type())){
                                        //˵������ҵ����Ͷ����ҹ�˾�Ĺ�˾
                                        // id�����֣��ٲ�Ͷ�ʹ�˾��type=2
                                        if(companyIdNameMap.containsKey(companyPerson1.getInvestor_id())){
                                            companyPerson1.setName(companyIdNameMap.get(companyPerson1.getInvestor_id()));
                                        }else{
                                            String searchCompanyId = "select * from company t where t.id = " + companyPerson1.getInvestor_id();
                                            ResultSet resultSet3 = execute2.executeQuery(searchCompanyId);
                                            while (resultSet3.next()){
                                                companyIdNameMap.put(companyPerson1.getInvestor_id(),resultSet3.getString("name"));
                                                companyPerson1.setName(resultSet3.getString("name"));
                                                //������ҵ����
                                                CompanyNameToLegalPerson entityCompany = new CompanyNameToLegalPerson();
                                                entityCompany.setId(resultSet3.getString("id"));
                                                entityCompany.setBase(resultSet3.getString("base"));
                                                entityCompany.setEstiblish_time(resultSet3.getString("estiblish_time"));
                                                entityCompany.setReg_capital(resultSet3.getString("reg_capital"));
                                                entityCompany.setReg_status(resultSet3.getString("reg_status"));
                                                entityCompany.setLegal_person_id(resultSet3.getString("legal_person_id"));
                                                entityCompany.setLegal_person_name(resultSet3.getString("legal_person_name"));
                                                companyIdEntity.put(entityCompany.getId(),entityCompany);

                                            }
                                        }
                                    }else if("1".equals(companyPerson1.getInvestor_type())){
                                        //˵������,�����ı�
                                        companyPerson1.setName(resultSet.getString("name"));
                                    }
                                    companyCompanyList.add(companyPerson1);
                                }
                                idCompanyPerson.put(entity1.getId(),companyCompanyList);
                            }
                            // д��sheet��
                            for(int k=0;k<companyCompanyList.size();k++){
                                String investor_id = companyCompanyList.get(k).getInvestor_id();
                                CompanyNameToLegalPerson person = companyIdEntity.get(investor_id);
                                HSSFRow row1 = sheet.createRow(rowIndex++);
                                row1.createCell(0).setCellValue(companyName);
                                row1.createCell(1).setCellValue(companyPersonList.get(cell).getName());
                                row1.createCell(2).setCellValue(companyCompanyList.get(k).getName());
                                row1.createCell(3).setCellValue(person.getBase());
                                row1.createCell(4).setCellValue(person.getEstiblish_time());
                                row1.createCell(5).setCellValue(person.getReg_capital());
                                row1.createCell(6).setCellValue(person.getReg_status());
                            }
                            //���Ϊ�գ���ô��������һ�����ݣ�Ͷ����Ϊ����ҵ���ˣ�������ҹ�˾
                            if(companyCompanyList.size()==0){
                                if(!companyIdEntity.containsKey(companyPersonList.get(cell).getInvestor_id())){
                                    // ����ID������˾
                                    //������ҵ����
                                    String searchCompanyId = "select t.id,t.base,t.estiblish_time,t.reg_capital,t.reg_status,t.legal_person_id,t.legal_person_name from company t where t.id = " + companyPersonList.get(cell).getInvestor_id();
                                    ResultSet resultSet3 = execute2.executeQuery(searchCompanyId);
                                    while (resultSet3.next()){
                                        CompanyNameToLegalPerson entityCompany = new CompanyNameToLegalPerson();
                                        entityCompany.setId(resultSet3.getString("id"));
                                        entityCompany.setBase(resultSet3.getString("base"));
                                        entityCompany.setEstiblish_time(resultSet3.getString("estiblish_time"));
                                        entityCompany.setReg_capital(resultSet3.getString("reg_capital"));
                                        entityCompany.setReg_status(resultSet3.getString("reg_status"));
                                        entityCompany.setLegal_person_id(resultSet3.getString("legal_person_id"));
                                        entityCompany.setLegal_person_name(resultSet3.getString("legal_person_name"));
                                        companyIdEntity.put(entityCompany.getId(),entityCompany);
                                    }
                                }
                                CompanyNameToLegalPerson person = companyIdEntity.get(companyPersonList.get(cell).getInvestor_id());
                                HSSFRow row1 = sheet.createRow(rowIndex++);
                                row1.createCell(0).setCellValue(companyName);
                                //Ͷ����Ϊ�Լ�
                                row1.createCell(1).setCellValue(companyPersonList.get(cell).getName());
                                row1.createCell(2).setCellValue(companyPersonList.get(cell).getName());
                                row1.createCell(3).setCellValue(person.getBase());
                                row1.createCell(4).setCellValue(person.getEstiblish_time());
                                row1.createCell(5).setCellValue(person.getReg_capital());
                                row1.createCell(6).setCellValue(person.getReg_status());

                            }

                        }else{
                            String getCompanysByPerson = "SELECT t.`name`,t.base,t.estiblish_time,t.reg_capital,t.reg_status FROM company t "
                                    +"LEFT JOIN company_investor c on t.id = c.company_id where "
                                    +"c.investor_id="+ companyPersonList.get(cell).getInvestor_id() +"   AND c.investor_type='1'   and t.reg_capital is  not null and t.reg_capital !='' and t.reg_status!='ע��' LIMIT 60;";
//                                +"c.investor_id="+ companyPersonList.get(cell).getInvestor_id() +"  AND c.investor_type='"+ companyPersonList.get(cell).getInvestor_type() +"'    LIMIT 1000;";
                            resultSet = execute.executeQuery(getCompanysByPerson);
                            while (resultSet.next()){
                                PersonInCompany company = new PersonInCompany();
                                company.setBase(resultSet.getString("base"));
                                company.setName(resultSet.getString("name"));
                                company.setEstiblish_time(resultSet.getString("estiblish_time"));
                                company.setReg_capital(resultSet.getString("reg_capital"));
                                company.setReg_status(resultSet.getString("reg_status"));
                                companyPersonList.get(cell).getCompanyList().add(company);
                            }
                            // TODO �ӹ�˾����ְλ
                            if(companyPersonList.get(cell).getCompanyList().size()>=20){
                                //˵�������������ҵ�ܶ࣬��ôû��Ҫһ������ְλ
                                //д������
                                for(int num=companyPersonList.get(cell).getCompanyList().size()-1;num>=0;num--) {
                                    PersonInCompany company = companyPersonList.get(cell).getCompanyList().get(num);
                                    HSSFRow row1 = sheet.createRow(rowIndex++);
                                    row1.createCell(0).setCellValue(companyName);
                                    row1.createCell(1).setCellValue(companyPersonList.get(cell).getName());
                                    row1.createCell(2).setCellValue(company.getName());
                                    row1.createCell(3).setCellValue(company.getBase());
                                    row1.createCell(4).setCellValue(company.getEstiblish_time());
                                    row1.createCell(5).setCellValue(company.getReg_capital());
                                    row1.createCell(6).setCellValue(company.getReg_status());
                                }
                            }else{
                                for(int num=companyPersonList.get(cell).getCompanyList().size()-1;num>=0;num--) {
                                    PersonInCompany company = companyPersonList.get(cell).getCompanyList().get(num);
                                    List<CompanyNameToLegalPerson> companyEntitys = null;
                                    if (namepersonListMap.containsKey(company.getName())) {
                                        companyEntitys = namepersonListMap.get(company.getName());
                                    } else {
//                                String searchCompany = "select * from company t where t.`name`='"+ company.getName() +"'  and t.base='"+ company.getBase() +"';";
                                        String searchCompany = "select * from company t where t.`name`='" + company.getName() + "';";
                                        companyEntitys = new ArrayList<CompanyNameToLegalPerson>();
                                        resultSet = execute.executeQuery(searchCompany);
                                        while (resultSet.next()) {
                                            CompanyNameToLegalPerson entity_company = new CompanyNameToLegalPerson();
                                            entity_company.setId(resultSet.getString("id"));
                                            entity_company.setBase(resultSet.getString("base"));
                                            entity_company.setLegal_person_id(resultSet.getString("legal_person_id"));
                                            entity_company.setLegal_person_name(resultSet.getString("legal_person_name"));
                                            companyEntitys.add(entity_company);
                                        }
                                        namepersonListMap.put(company.getName(), companyEntitys);
                                    }
                                    String jobStr = "";
                                    for(int ind=0;ind<companyEntitys.size();ind++){
                                        if(StringUtils.isNotBlank(company.getBase())&&!company.getBase().equals(companyEntitys.get(ind).getBase())){
                                            continue;
                                        }
                                        List<CompanyJob> jobs = null;
                                        if(companyJobMap.containsKey(companyEntitys.get(ind).getId())){
                                            jobs = companyJobMap.get(companyEntitys.get(ind).getId());
                                        }else{
                                            jobs = new ArrayList<CompanyJob>();
                                            String searchCompanyJob = "SELECT h.name,s.* from  company_staff s LEFT JOIN human h ON h.id=s.staff_id where s.company_id = "+ companyEntitys.get(ind).getId();
                                            ResultSet resultSet1 = execute2.executeQuery(searchCompanyJob);
                                            // TODO �����ͬ���ģ���ID���߶���¼
                                            while (resultSet1.next()){
                                                CompanyJob companyJob = new CompanyJob();
                                                companyJob.setStaff_id(resultSet1.getString("staff_id"));
                                                companyJob.setStaff_type_name(resultSet1.getString("staff_type_name"));
                                                companyJob.setCompanyId(companyEntitys.get(ind).getId());
                                                companyJob.setId(resultSet1.getString("id"));
                                                jobs.add(companyJob);
                                            }
                                            companyJobMap.put(companyEntitys.get(ind).getId(),jobs);
                                        }
                                        // �ж���Щ �˵�ID��jobIDһ����
                                        for(int iii=0;iii<jobs.size();iii++){
                                            CompanyJob companyJob = jobs.get(iii);
                                            if(StringUtils.isNotBlank(companyJob.getStaff_id())&&companyJob.getStaff_id().equals(companyPersonList.get(cell).getInvestor_id())){
                                                jobStr += ","+companyJob.getStaff_type_name();
                                            }
                                        }
                                        if(jobStr.startsWith(",")){
                                            jobStr = jobStr.substring(1,jobStr.length());
                                        }else{
                                            //˵��û��ְλ������Ϊ��ҹ�˾��������
                                        }
                                        //д������
                                        HSSFRow row1 = sheet.createRow(rowIndex++);
                                        row1.createCell(0).setCellValue(companyName);
                                        row1.createCell(1).setCellValue(companyPersonList.get(cell).getName());
                                        row1.createCell(2).setCellValue(company.getName());
                                        row1.createCell(3).setCellValue(company.getBase());
                                        row1.createCell(4).setCellValue(company.getEstiblish_time());
                                        row1.createCell(5).setCellValue(company.getReg_capital());
                                        row1.createCell(6).setCellValue(company.getReg_status());
                                        row1.createCell(7).setCellValue(jobStr);
                                    }
                                }
                            }


                        }
                    }

                }
            } catch (SQLException e) {
                e.printStackTrace();

            }
            }catch (Exception e){
                e.printStackTrace();
                // д�������ļ�
                errorCompanyList.add(companyName);
            }
            System.out.println("BB����������������������������������������"+ ii +"������:"+ companyName +"��������������������������������");
        }
        ExcelUtil.writeSteamToExcel(wb,outpathdata.replace("{index}",""+index+"_"+ ii +""));
        {
            HSSFWorkbook wv  = new HSSFWorkbook();
            HSSFSheet nosheet = wv.createSheet("û�е���ҵ");
            int norowIndex = 0;
            for(int k=0;k<noCompanyList.size();k++){
                HSSFRow row1 = nosheet.createRow(norowIndex++);
                row1.createCell(0).setCellValue(noCompanyList.get(k));
            }
            ExcelUtil.writeSteamToExcel(wv,"D:\\\\projects\\\\companydata\\\\nonono.xls");
        }
        {
            HSSFWorkbook wv  = new HSSFWorkbook();
            HSSFSheet nosheet = wv.createSheet("�е���ҵ");
            int norowIndex = 0;
            for(int k=0;k<hasCompanyList.size();k++){
                HSSFRow row1 = nosheet.createRow(norowIndex++);
                row1.createCell(0).setCellValue(hasCompanyList.get(k));
            }
            ExcelUtil.writeSteamToExcel(wv,"D:\\\\projects\\\\companydata\\\\hashashas.xls");
        }

        try {
            execute.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            execute2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        manager.close();

    }
}
