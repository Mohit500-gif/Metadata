package com.digi.uniprr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.digi.uniprr.model.EmailChangeHistory;

public interface EmailChangeHistoryRepository extends JpaRepository<EmailChangeHistory, Integer> { }
