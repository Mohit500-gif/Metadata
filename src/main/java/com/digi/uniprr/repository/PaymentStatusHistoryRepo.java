package com.digi.uniprr.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.digi.uniprr.model.PaymentStatusHistory;

public interface PaymentStatusHistoryRepo extends JpaRepository<PaymentStatusHistory, Integer>{


}
