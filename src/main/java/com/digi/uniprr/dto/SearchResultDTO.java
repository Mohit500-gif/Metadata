package com.digi.uniprr.dto;

import java.util.List;

public class SearchResultDTO {
    private String oldEmail;
    private List<JournalInfo> journals;

    public SearchResultDTO(String oldEmail, List<JournalInfo> journals) {
        this.oldEmail = oldEmail;
        this.journals = journals;
    }

    public String getOldEmail() { return oldEmail; }
    public List<JournalInfo> getJournals() { return journals; }

    public static class JournalInfo {
        private Integer journalId;
        private String journalTitle;

        public JournalInfo(Integer journalId, String journalTitle) {
            this.journalId = journalId;
            this.journalTitle = journalTitle;
        }

        public Integer getJournalId() { return journalId; }
        public String getJournalTitle() { return journalTitle; }
    }
}
