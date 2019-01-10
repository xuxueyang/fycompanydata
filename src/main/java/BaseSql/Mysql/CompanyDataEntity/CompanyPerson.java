package BaseSql.Mysql.CompanyDataEntity;

import org.apache.poi.hssf.usermodel.HSSFRow;

import java.util.ArrayList;
import java.util.List;

public class CompanyPerson {
    private String id;
    private String name;
    private String investor_type;

    private List<Company> companyList = new ArrayList<Company>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getInvestor_type() {
        return investor_type;
    }

    public void setInvestor_type(String investor_type) {
        this.investor_type = investor_type;
    }

    public List<Company> getCompanyList() {
        return companyList;
    }

    public void setCompanyList(List<Company> companyList) {
        this.companyList = companyList;
    }
}
