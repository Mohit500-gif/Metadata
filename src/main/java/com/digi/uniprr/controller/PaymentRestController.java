package com.digi.uniprr.controller;

import java.util.List;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.digi.uniprr.VO.EncryptionDecryptionVo;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.PaymentDetailsVO;
import com.digi.uniprr.VO.PaymentHistoryVO;
import com.digi.uniprr.VO.PaymentStatusHistoryVO;
import com.digi.uniprr.model.UserInvoice;
import com.digi.uniprr.service.PaymentService;
import com.stripe.model.Charge;
import com.stripe.model.Customer;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/payment")
public class PaymentRestController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PaymentService paymentService;

	JSONObject obj = new JSONObject();

	@RequestMapping(path = "/makePayment", method = RequestMethod.POST)
	public ResponseEntity<?> payment(@RequestBody PaymentDetailsVO data) throws Exception {
		Charge chargeDetails = null;
		UserInvoice userInvoice = null;
		if (data.getPaymentMode().equalsIgnoreCase("ONLINE")) {
			Customer customer = paymentService.createCustomer(data.getToken(), data.getEmail());
			chargeDetails = paymentService.chargeNewCard(data.getAmount(), data.getToken());
		}

		try {
			userInvoice = paymentService.saveDataInDb(chargeDetails, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		obj.put("Transaction_id", userInvoice.getTransactionId());
		obj.put("status", userInvoice.getTransactionStatus());

		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping("getPaymentDetail")
	public ResponseEntity<?> getPaymentDetail(@RequestBody ManuscriptListVO manuscriptListVO) {
		try {
			PaymentHistoryVO userInvoice = paymentService.getPaymentDetail(manuscriptListVO);
			if (userInvoice != null) {
				obj.put("data", userInvoice);
			} else {
				obj.put("data", "data not found");
			}
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
			obj.put("data", "Something went wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	// payment history
	@PostMapping("getPaymentHistory")
	public ResponseEntity<?> getPaymentHistory(@RequestBody PaymentHistoryVO paymentHistoryVO) {
		try {
			List<PaymentHistoryVO> userInvoiceHistory = paymentService.getPaymentHistory(paymentHistoryVO);
			if (userInvoiceHistory != null && !userInvoiceHistory.isEmpty()) {
				obj.put("data", userInvoiceHistory);
			} else {
				obj.put("data", "data not found");
			}
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
			e.printStackTrace();
			obj.put("data", "Something went wrong");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@PostMapping("encryptionDecryption")
	public ResponseEntity<?> encryptionDecryption(@RequestBody EncryptionDecryptionVo encryptionDecryptionVo) {
		JSONObject obj = new JSONObject();
		try {
			EncryptionDecryptionVo encryptionDecryption = paymentService.encryptionDecryption(encryptionDecryptionVo);
			if(encryptionDecryption!=null) {
				obj.put("data", encryptionDecryption);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}else {
				obj.put("data", "Please provide data,key,encrypted data and decrypted data");
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}
			
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
	}
	
	@PostMapping("paymentStatusHistory")
	public ResponseEntity<?> paymentStatusHistory(@RequestBody PaymentStatusHistoryVO paymentStatusHistoryVo) {
		JSONObject obj = new JSONObject();
		try {
			String data = paymentService.savePaymnetStatusHistory(paymentStatusHistoryVo);
			if(data!=null) {
				obj.put("data", data);
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}else {
				obj.put("data", " Payment status not saved , Please provide correct information.");
				return new ResponseEntity<>(obj, HttpStatus.OK);
			}
			
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
			e.printStackTrace();
			obj.put("data", "Something went wrong");
			return new ResponseEntity<>(obj, HttpStatus.OK);
		}
	}	
	

}
