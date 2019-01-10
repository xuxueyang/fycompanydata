package BaseSql.Mysql.CompanyDataEntity;

public class CompanyJob {
    private String staff_type_name;
    private String staff_id;
//    private String name;
    private String id;
    private String companyId;
    public String getStaff_type_name() {
        return staff_type_name;
    }

    public void setStaff_type_name(String staff_type_name) {
        this.staff_type_name = staff_type_name;
    }

    public String getStaff_id() {
        return staff_id;
    }

    public void setStaff_id(String staff_id) {
        this.staff_id = staff_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}
