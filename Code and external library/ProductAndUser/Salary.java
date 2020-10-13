package ProductAndUser;

public class Salary {
    private String userid;// Employee's ID
    private String year;// Year of salary table
    private String month;// Month of salary table
    private String baseSalary; // Base salary
    private String perfectAttendance;// The value of the total attendance award
    private String sale14K;// Total commission from sales of 14K
    private String sale18K;//Total commission from sales of 18K
    private String sale22K;//Total commission from sales of 22K
    private String sale24K;//Total commission from sales of 24K
    private String overTimeSalary;// overtime salary
    private String deduction;// Amount of deduction
    private String note;// Deduction notes
    private String totalSalary;// Total salary of current table

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(String baseSalary) {
        this.baseSalary = baseSalary;
    }

    public String getPerfectAttendance() {
        return perfectAttendance;
    }

    public void setPerfectAttendance(String perfectAttendance) {
        this.perfectAttendance = perfectAttendance;
    }

    public String getSale14K() {
        return sale14K;
    }

    public void setSale14K(String sale14K) {
        this.sale14K = sale14K;
    }

    public String getSale18K() {
        return sale18K;
    }

    public void setSale18K(String sale18K) {
        this.sale18K = sale18K;
    }

    public String getSale22K() {
        return sale22K;
    }

    public void setSale22K(String sale22K) {
        this.sale22K = sale22K;
    }

    public String getSale24K() {
        return sale24K;
    }

    public void setSale24K(String sale24K) {
        this.sale24K = sale24K;
    }

    public String getOverTimeSalary() {
        return overTimeSalary;
    }

    public void setOverTimeSalary(String overTimeSalary) {
        this.overTimeSalary = overTimeSalary;
    }

    public String getDeduction() {
        return deduction;
    }

    public void setDeduction(String deduction) {
        this.deduction = deduction;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTotalSalary() {
        return totalSalary;
    }

    public void setTotalSalary(String currentSalary) {
        this.totalSalary = currentSalary;
    }
}
