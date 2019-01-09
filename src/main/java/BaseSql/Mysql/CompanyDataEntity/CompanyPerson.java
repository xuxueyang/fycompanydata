package BaseSql.Mysql.CompanyDataEntity;

import org.apache.poi.hssf.usermodel.HSSFRow;

public class CompanyPerson {
    private String id;
    private String name;
    private String investor_type;

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
}
