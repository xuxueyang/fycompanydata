package BaseSql.Mysql.CompanyDataEntity;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;

public class CompanyNameToLegalPerson{
    private String legal_person_id;
    private String id;
    private String legal_person_name;
    private String base;

    private String estiblish_time;
    private String reg_status;
    private String reg_capital;

    public String getEstiblish_time() {
        return estiblish_time;
    }

    public void setEstiblish_time(String estiblish_time) {
        this.estiblish_time = estiblish_time;
    }

    public String getReg_status() {
        return reg_status;
    }

    public void setReg_status(String reg_status) {
        this.reg_status = reg_status;
    }

    public String getReg_capital() {
        return reg_capital;
    }

    public void setReg_capital(String reg_capital) {
        this.reg_capital = reg_capital;
    }

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
