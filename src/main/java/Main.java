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
        // 企业名字
        ArrayList<ArrayList<Object>> arrayLists = ExcelUtil.readExcel(new File(inpath));
        List<String> noCompanyList = new ArrayList<String>();
        List<String> hasCompanyList = new ArrayList<String>();
//        System.out.println(arrayLists);
        HSSFWorkbook wb = new HSSFWorkbook();;
        HSSFSheet sheet = wb.createSheet("数据");
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

        //        被查询公司	企业法人/投资人	公司	地区（省份缩写）	成立时间	注册资本	状态

        //TODO 一些HashMap，缓存查出来的数据，这样可要避免一些人已经查出公司
        // 缓存企业ID：对应的企业
        Map<String ,List<CompanyNameToLegalPerson>> namepersonListMap = (Map<String, List<CompanyNameToLegalPerson>>) MapBufferUtil.decodeMap(buffPath+"namepersonListMap.txt");
//        new HashMap<String ,List<CompanyNameToLegalPerson>>();
        // 缓存公司ID和主键
        Map<String,String> companyIdNameMap = (Map<String, String>) MapBufferUtil.decodeMap(buffPath+"companyIdNameMap.txt");//new HashMap<String, String>();
        Map<String,CompanyNameToLegalPerson> companyIdEntity = (Map<String, CompanyNameToLegalPerson>) MapBufferUtil.decodeMap(buffPath+"companyIdEntity.txt");//new HashMap<String, CompanyNameToLegalPerson>();
        Map<String,List<CompanyPerson>> idCompanyPerson = (Map<String, List<CompanyPerson>>) MapBufferUtil.decodeMap(buffPath+"idCompanyPerson.txt");//new HashMap<String, List<CompanyPerson>>();
        Map<String,List<CompanyJob>> companyJobMap = (Map<String, List<CompanyJob>>) MapBufferUtil.decodeMap(buffPath+"companyJobMap.txt");//new HashMap<String, List<CompanyJob>>();
        List<String> errorCompanyList = new ArrayList<String>();
        int rowIndex=0;
        //                    被查询公司	企业法人/投资人	公司	地区（省份缩写）	成立时间	注册资本	状态

        HSSFRow row = sheet.createRow(rowIndex++);
        row.createCell(0).setCellValue("被查询公司");
        row.createCell(1).setCellValue("企业法人/投资人");
        row.createCell(2).setCellValue("公司");
        row.createCell(3).setCellValue("地区（省份缩写）");
        row.createCell(4).setCellValue("成立时间）");
        row.createCell(5).setCellValue("注册资本");
        row.createCell(6).setCellValue("状态");
        row.createCell(7).setCellValue("职责");
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
                //写入之前的
                //将目前已经查询的企业写入已经查询的表中

                ExcelUtil.writeSteamToExcel(wb,outpathdata.replace("{index}",""+index+"_"+ ii +""));
                wb = new HSSFWorkbook();
                sheet = wb.createSheet("数据");
                rowIndex = 0;
                row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue("被查询公司");
                row.createCell(1).setCellValue("企业法人/投资人");
                row.createCell(2).setCellValue("公司");
                row.createCell(3).setCellValue("地区（省份缩写）");
                row.createCell(4).setCellValue("成立时间）");
                row.createCell(5).setCellValue("注册资本");
                row.createCell(6).setCellValue("状态");
                row.createCell(7).setCellValue("职责");


                index++;
//                break;
            }
            //TODO 缓存已经查的HashMap
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
            System.out.println("AA————————————————开始处理"+ ii +"条数据:"+ companyName +"————————————————");
            try {


            //根据企业名字，获取到：企业的ID id、企业的法人名字 legal_persion_name 、企业法人ID legal_persion_id
            String companyNameToLegalPerson = "select * from company t where t.`name`='"+ companyName + "';";
//            Field[] declaredFields =  new CompanyNameToLegalPerson().getClass().getDeclaredFields();
            // 根据企业ID获取到人员构成


//            ID	公司	地区（省份缩写）	成立时间	注册资本	状态
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
                    //查该企业的关联企业
                    System.out.println("》》》》》》》》》》》》》》   ："+ companyName +"————————————————不存在");
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


                    // 企业法人： 企业法人名字： 企业法人ID
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

                            //TODO 如果type为2，需要查公司表，如果数据为1，说明查法人表
                            companyPerson1.setInvestor_type(resultSet.getString("investor_type"));
                            if("2".equals(companyPerson1.getInvestor_type())){
                                //说明是企业，查投资这家公司的公司
                                //TODO id查名字，再查投资公司，type=2
                                if(companyIdNameMap.containsKey(companyPerson1.getInvestor_id())){
                                    companyPerson1.setName(companyIdNameMap.get(companyPerson1.getInvestor_id()));
                                }else{
                                    String searchCompanyId = "select * from company t where t.id = " + companyPerson1.getInvestor_id();
                                    ResultSet resultSet3 = execute2.executeQuery(searchCompanyId);
                                    while (resultSet3.next()){
                                        //塞入企业数据
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
                                //说明是人,不做改变
                                companyPerson1.setName(resultSet.getString("name"));
                            }
                            companyPersonList.add(companyPerson1);
                        }
                        idCompanyPerson.put(entity1.getId(),companyPersonList);
                    }

                    // 根据法人获取到信息(判断法人有没有在投资人中，如果没有，添加一条作为法人搜索相关数据）
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

                    //根据投资人，依次找下属的公司，和所在公司的职位
                    for(int cell=0;cell<companyPersonList.size();cell++){
                        if(StringUtils.isBlank(companyPersonList.get(cell).getInvestor_id())){
                            continue;
                        }
                        if("2".equals(companyPersonList.get(cell).getInvestor_type())){
                            // 这种情况就查投资的公司
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

                                    // 如果type为2，需要查公司表，如果数据为1，说明查法人表
                                    companyPerson1.setInvestor_type(resultSet.getString("investor_type"));
                                    if("2".equals(companyPerson1.getInvestor_type())){
                                        //说明是企业，查投资这家公司的公司
                                        // id查名字，再查投资公司，type=2
                                        if(companyIdNameMap.containsKey(companyPerson1.getInvestor_id())){
                                            companyPerson1.setName(companyIdNameMap.get(companyPerson1.getInvestor_id()));
                                        }else{
                                            String searchCompanyId = "select * from company t where t.id = " + companyPerson1.getInvestor_id();
                                            ResultSet resultSet3 = execute2.executeQuery(searchCompanyId);
                                            while (resultSet3.next()){
                                                companyIdNameMap.put(companyPerson1.getInvestor_id(),resultSet3.getString("name"));
                                                companyPerson1.setName(resultSet3.getString("name"));
                                                //塞入企业数据
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
                                        //说明是人,不做改变
                                        companyPerson1.setName(resultSet.getString("name"));
                                    }
                                    companyCompanyList.add(companyPerson1);
                                }
                                idCompanyPerson.put(entity1.getId(),companyCompanyList);
                            }
                            // 写入sheet表
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
                            //如果为空，那么设置至少一条数据：投资人为该企业法人，就是这家公司
                            if(companyCompanyList.size()==0){
                                if(!companyIdEntity.containsKey(companyPersonList.get(cell).getInvestor_id())){
                                    // 根据ID搜索公司
                                    //塞入企业数据
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
                                //投资人为自己
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
                                    +"c.investor_id="+ companyPersonList.get(cell).getInvestor_id() +"   AND c.investor_type='1'   and t.reg_capital is  not null and t.reg_capital !='' and t.reg_status!='注销' LIMIT 60;";
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
                            // TODO 从公司搜索职位
                            if(companyPersonList.get(cell).getCompanyList().size()>=20){
                                //说明这个人所属企业很多，那么没必要一个个查职位
                                //写入数据
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
                                            // TODO 如果是同名的，看ID或者都记录
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
                                        // 判断这些 人的ID和jobID一样的
                                        for(int iii=0;iii<jobs.size();iii++){
                                            CompanyJob companyJob = jobs.get(iii);
                                            if(StringUtils.isNotBlank(companyJob.getStaff_id())&&companyJob.getStaff_id().equals(companyPersonList.get(cell).getInvestor_id())){
                                                jobStr += ","+companyJob.getStaff_type_name();
                                            }
                                        }
                                        if(jobStr.startsWith(",")){
                                            jobStr = jobStr.substring(1,jobStr.length());
                                        }else{
                                            //说明没有职位，不认为这家公司属于他！
                                        }
                                        //写入数据
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
                // 写入错误的文件
                errorCompanyList.add(companyName);
            }
            System.out.println("BB————————————————结束处理"+ ii +"条数据:"+ companyName +"————————————————");
        }
        ExcelUtil.writeSteamToExcel(wb,outpathdata.replace("{index}",""+index+"_"+ ii +""));
        {
            HSSFWorkbook wv  = new HSSFWorkbook();
            HSSFSheet nosheet = wv.createSheet("没有的企业");
            int norowIndex = 0;
            for(int k=0;k<noCompanyList.size();k++){
                HSSFRow row1 = nosheet.createRow(norowIndex++);
                row1.createCell(0).setCellValue(noCompanyList.get(k));
            }
            ExcelUtil.writeSteamToExcel(wv,"D:\\\\projects\\\\companydata\\\\nonono.xls");
        }
        {
            HSSFWorkbook wv  = new HSSFWorkbook();
            HSSFSheet nosheet = wv.createSheet("有的企业");
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
