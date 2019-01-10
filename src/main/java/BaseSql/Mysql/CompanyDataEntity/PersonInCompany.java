package BaseSql.Mysql.CompanyDataEntity;

import java.util.ArrayList;
import java.util.List;

public class PersonInCompany {
    private String base;
    private String estiblish_time;
    private String reg_capital;
    private String reg_status;
    private String name;
    private List<String> jobs = new ArrayList<String>();

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getEstiblish_time() {
        return estiblish_time;
    }

    public void setEstiblish_time(String estiblish_time) {
        this.estiblish_time = estiblish_time;
    }

    public String getReg_capital() {
        return reg_capital;
    }

    public void setReg_capital(String reg_capital) {
        this.reg_capital = reg_capital;
    }

    public String getReg_status() {
        return reg_status;
    }

    public void setReg_status(String reg_status) {
        this.reg_status = reg_status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getJobs() {
        return jobs;
    }

    public void setJobs(List<String> jobs) {
        this.jobs = jobs;
    }
}
