package com.digi.uniprr.VO;

public class ReportRequest {

    private String startDate; // format: yyyy-MM-dd
    private String endDate;   // format: yyyy-MM-dd

    private Integer page = 0;   // default page
    private Integer size = 10;  // default size

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getPage() {
        return page != null ? page : 0;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size != null ? size : 10;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}