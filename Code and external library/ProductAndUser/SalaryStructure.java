package ProductAndUser;

public class SalaryStructure {
        private int baseSalary;// Base salary
        private int perfectAttendance;// The amount of full attendance reward
        private double sale14K;// Commission coefficient of 14K sales
        private double sale18K;// Commission coefficient of 18K sales
        private double sale22K;// Commission coefficient of 22K sales
        private double sale24K;// Commission coefficient of 24K sales

    public int getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(int baseSalary) {
        this.baseSalary = baseSalary;
    }

    public int getPerfectAttendance() {
        return perfectAttendance;
    }

    public void setPerfectAttendance(int perfectAttendance) {
        this.perfectAttendance = perfectAttendance;
    }

    public double getSale14K() {
        return sale14K;
    }

    public void setSale14K(double sale14K) {
        this.sale14K = sale14K;
    }

    public double getSale18K() {
        return sale18K;
    }

    public void setSale18K(double sale18K) {
        this.sale18K = sale18K;
    }

    public double getSale22K() {
        return sale22K;
    }

    public void setSale22K(double sale22K) {
        this.sale22K = sale22K;
    }

    public double getSale24K() {
        return sale24K;
    }

    public void setSale24K(double sale24K) {
        this.sale24K = sale24K;
    }
}
