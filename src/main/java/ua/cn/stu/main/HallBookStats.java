package ua.cn.stu.main;

public class HallBookStats {

    private String hallName;
    private Integer uniqueBookCount;
    private Integer loanCount;

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public Integer getUniqueBookCount() {
        return uniqueBookCount;
    }

    public void setUniqueBookCount(Integer uniqueBookCount) {
        this.uniqueBookCount = uniqueBookCount;
    }

    public Integer getLoanCount() {
        return loanCount;
    }

    public void setLoanCount(Integer loanCount) {
        this.loanCount = loanCount;
    }
}