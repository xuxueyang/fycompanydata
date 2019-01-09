package BaseSql.Mysql.CompanyDataEntity;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;

public class CompanyNameToLegalPerson{
    private String legal_person_id;
    private String id;
    private String legal_person_name;
    private String base;

    public String getLegal_person_id() {
        return legal_person_id;
    }

    public void setLegal_person_id(String legal_person_id) {
        this.legal_person_id = legal_person_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLegal_person_name() {
        return legal_person_name;
    }

    public void setLegal_person_name(String legal_person_name) {
        this.legal_person_name = legal_person_name;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }
}
