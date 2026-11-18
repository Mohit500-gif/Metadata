package com.digi.uniprr.VO;

import java.util.List;

public class ReportResponse {

    private List<ManuscriptReportDTO> data;
    private long totalCount;
    private int totalPages;
    private int currentPage;
    private int pageSize;

    public List<ManuscriptReportDTO> getData() {
        return data;
    }

    public void setData(List<ManuscriptReportDTO> data) {
        this.data = data;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}