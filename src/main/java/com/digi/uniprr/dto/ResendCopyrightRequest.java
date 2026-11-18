package com.digi.uniprr.dto;

import lombok.Data;

@Data
public class ResendCopyrightRequest {
    private Integer manuscriptId;
    private Integer journalId;
    private Integer coAuthorId;
}

