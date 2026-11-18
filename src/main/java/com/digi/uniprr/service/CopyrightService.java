package com.digi.uniprr.service;

import com.digi.uniprr.model.ManuScriptCoAuthor;
import java.util.List;

public interface CopyrightService {
    String queueCopyrightEmails(Integer manuscriptId, Integer journalId, List<ManuScriptCoAuthor> authorsList,boolean flag);

    public String resendCopyrightLink(Integer manuscriptId, Integer journalId, Integer coAuthorId);
}

