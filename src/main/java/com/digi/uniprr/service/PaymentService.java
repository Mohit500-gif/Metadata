package com.digi.uniprr.service;

import java.util.List;

import com.digi.uniprr.VO.EncryptionDecryptionVo;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.PaymentDetailsVO;
import com.digi.uniprr.VO.PaymentHistoryVO;
import com.digi.uniprr.VO.PaymentStatusHistoryVO;
import com.digi.uniprr.model.UserInvoice;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;

public interface PaymentService {
	
	public Customer createCustomer(String token, String email) throws StripeException;

	public Charge chargeNewCard(double amount, String token) throws StripeException;
	
	public UserInvoice saveDataInDb(Charge chargeDetails, PaymentDetailsVO data);
	
	public PaymentHistoryVO getPaymentDetail(ManuscriptListVO manuscriptListVO); 
	
	public List<PaymentHistoryVO> getPaymentHistory(PaymentHistoryVO paymentHistoryVO);

	public EncryptionDecryptionVo encryptionDecryption(EncryptionDecryptionVo encryptionDecryptionVo)throws Exception;
	
	public String savePaymnetStatusHistory(PaymentStatusHistoryVO paymentStatusHistoryVO);
}
