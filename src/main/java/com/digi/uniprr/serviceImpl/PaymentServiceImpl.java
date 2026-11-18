package com.digi.uniprr.serviceImpl;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digi.uniprr.VO.EncryptionDecryptionVo;
import com.digi.uniprr.VO.ManuscriptListVO;
import com.digi.uniprr.VO.PaymentDetailsVO;
import com.digi.uniprr.VO.PaymentHistoryVO;
import com.digi.uniprr.VO.PaymentStatusHistoryVO;
import com.digi.uniprr.model.ManuScript;
import com.digi.uniprr.model.PaymentStatusHistory;
import com.digi.uniprr.model.User;
import com.digi.uniprr.model.UserInvoice;
import com.digi.uniprr.model.UserJournal;
import com.digi.uniprr.repository.ManuScriptRepo;
import com.digi.uniprr.repository.PaymentStatusHistoryRepo;
import com.digi.uniprr.repository.UserInvoiceRepo;
import com.digi.uniprr.repository.UserJournalRepo;
import com.digi.uniprr.repository.UserRepo;
import com.digi.uniprr.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;

@Service
public class PaymentServiceImpl implements PaymentService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static SecretKeySpec secretKey ;
	private static byte[] key ;

	private static String decryptedString;
	private static String encryptedString;
	
	@Autowired
	UserInvoiceRepo userInvoiceRepo;
	
	@Autowired
	UserRepo userRepo;
	
	@Autowired
	PaymentStatusHistoryRepo paymentStatusHistoryRepo;
	
	@Autowired
	ManuScriptRepo manuscriptRepo;
	
	@Autowired
	private UserJournalRepo userJournalRepo;
	
	 @Autowired
	 PaymentServiceImpl() {
	        Stripe.apiKey = System.getenv("STRIPE_SECRET_KEY"); // set STRIPE_SECRET_KEY env variable
	    }
	
	@Override
	public Customer createCustomer(String token, String email) throws StripeException {

        Map<String, Object> customerParams = new HashMap<String, Object>();
        customerParams.put("email", email);
        //customerParams.put("description", "test");
        return Customer.create(customerParams);
    
	}

	@Override
	public Charge chargeNewCard(double amount, String token) throws StripeException {

        Map<String, Object> chargeParams = new HashMap<String, Object>();
        List<Map<String, Object>> chargeDetails=null;		
        chargeParams.put("amount", (int)(amount));
        chargeParams.put("currency", "USD");
        chargeParams.put("source", token);
        Charge charge = Charge.create(chargeParams);
        return charge;
    
	}

	@Override
	public UserInvoice saveDataInDb(Charge chargeDetails, PaymentDetailsVO data) {
		UserInvoice userInvoice = new UserInvoice();
		User user = userRepo.findByEmail(data.getEmail());
		if(data.getPaymentMode().equalsIgnoreCase("ONLINE"))
		{
			userInvoice.setTransactionId(chargeDetails.getId());
			userInvoice.setTransactionStatus(chargeDetails.getStatus());
			userInvoice.setAmount(chargeDetails.getAmount());
			userInvoice.setTransactionMode(chargeDetails.getPaymentMethod());
		}else {
			userInvoice.setTransactionId("txn_" +data.getMauscriptId() +"_" +data.getChequeNo());
			userInvoice.setChequeNo(data.getChequeNo());
			userInvoice.setBankDetail(data.getBankDetail());
			userInvoice.setChequeDate(data.getChequeDate());
			userInvoice.setTransactionStatus("pending");
			userInvoice.setAmount(data.getAmount());
		}
		userInvoice.setPaymentMode(data.getPaymentMode());
		userInvoice.setManuscriptId(data.getMauscriptId());
		userInvoice.setUserId(user.getId());
		userInvoice.setJournalId(data.getJournalId());
		
		return userInvoiceRepo.save(userInvoice);
	}

	@Override
	public PaymentHistoryVO getPaymentDetail(ManuscriptListVO manuscriptListVO) {
		PaymentHistoryVO addPaymentHistoryVO=new PaymentHistoryVO();
		UserInvoice userInvoice = userInvoiceRepo.getPaymentDetailByUser(manuscriptListVO.getManuScriptId(), manuscriptListVO.getUserId());
         if(userInvoice!=null) {
        	 addPaymentHistoryVO.setId(userInvoice.getId());
        	 addPaymentHistoryVO.setTransactionId(userInvoice.getTransactionId());
        	 addPaymentHistoryVO.setManuscriptId(userInvoice.getManuscriptId());
        	 addPaymentHistoryVO.setUserId(userInvoice.getUserId());
        	 addPaymentHistoryVO.setJournalId(userInvoice.getJournalId());
        	 addPaymentHistoryVO.setPaymentMode(userInvoice.getPaymentMode());
        	 addPaymentHistoryVO.setChequeNo(userInvoice.getChequeNo());
        	 addPaymentHistoryVO.setChequeDate(userInvoice.getChequeDate());
        	 addPaymentHistoryVO.setBankDetail(userInvoice.getBankDetail());
        	 addPaymentHistoryVO.setTransactionMode(userInvoice.getTransactionMode());
        	 addPaymentHistoryVO.setAmount(userInvoice.getAmount());
        	 addPaymentHistoryVO.setTransactionStatus(userInvoice.getTransactionStatus());
        	 addPaymentHistoryVO.setTransactionDate(userInvoice.getTransactionDate());
        	 addPaymentHistoryVO.setPaymentMode(userInvoice.getPaymentMode());
         }
		
		return addPaymentHistoryVO;
	}

	@Override
	public List<PaymentHistoryVO> getPaymentHistory(PaymentHistoryVO paymentHistoryVO) {
		List<PaymentHistoryVO> paymentHistoryList=new ArrayList<>();
		List<Integer> JournalId = new ArrayList<>();
		if(paymentHistoryVO.getRoleId()!=null && paymentHistoryVO.getRoleId().equals(1) && paymentHistoryVO.getJournalId() == null ) {
			List<UserInvoice> userInvoiceAllList=userInvoiceRepo.findAll();
			if(userInvoiceAllList!=null && !userInvoiceAllList.isEmpty()) {
				userInvoiceAllList.parallelStream().forEach(i->{
					PaymentHistoryVO paymentVo=new PaymentHistoryVO();
					paymentVo.setUserId(i.getUserId());
					paymentVo.setPaymentId(i.getId());
					paymentVo.setRoleId(1);
					paymentVo.setManuscriptId(i.getManuscriptId());
					paymentVo.setTransactionDate(i.getTransactionDate());
					paymentVo.setTransactionStatus(i.getTransactionStatus());
					if(i.getJournal()!=null) {
						paymentVo.setJounralTitle(i.getJournal().getJournalTitle());
						paymentVo.setJournalCode(i.getJournal().getJournalCode());
						paymentVo.setJournalId(i.getJournalId());
					}
					paymentVo.setManuscriptCode(i.getManuScript().getManuscriptCode());
					paymentVo.setManuscriptVersion(i.getManuScript().getManuscriptVersion());
					paymentVo.setManuscriptTitle(i.getManuScript().getManuscriptTitle());
					paymentVo.setTransactionMode(i.getTransactionMode());
					paymentVo.setUserEmailId(i.getUser().getUsername());
					paymentVo.setUserName(i.getUser().getFirstName()+""+i.getUser().getLastName());
					paymentVo.setAmount(i.getAmount());
					paymentVo.setPaymentMode(i.getPaymentMode());
					paymentHistoryList.add(paymentVo);
				});
			}
		}else if(paymentHistoryVO.getRoleId()!=null && paymentHistoryVO.getRoleId().equals(2) && paymentHistoryVO.getJournalId() == null) {
			List<UserJournal> userJournalList1 = userJournalRepo.getUserJournalByUserId(paymentHistoryVO.getUserId());
			if(userJournalList1!=null && !userJournalList1.isEmpty()) {
				userJournalList1.parallelStream().forEach(j->{
					JournalId.add(j.getJournalId());
				});	
				List<UserInvoice> userInvoiceList=userInvoiceRepo.getPaymentHistoryByJournalId(JournalId);
				if(userInvoiceList!=null && !userInvoiceList.isEmpty()) {
					userInvoiceList.parallelStream().forEach(i->{
						PaymentHistoryVO paymentVo=new PaymentHistoryVO();
						paymentVo.setUserId(i.getUserId());
						paymentVo.setPaymentId(i.getId());
						paymentVo.setRoleId(2);
						paymentVo.setManuscriptId(i.getManuscriptId());
						paymentVo.setTransactionDate(i.getTransactionDate());
						paymentVo.setTransactionStatus(i.getTransactionStatus());
						if(i.getJournal()!=null) {
							paymentVo.setJounralTitle(i.getJournal().getJournalTitle());
							paymentVo.setJournalCode(i.getJournal().getJournalCode());
							paymentVo.setJournalId(i.getJournalId());
						}
						paymentVo.setManuscriptCode(i.getManuScript().getManuscriptCode());
						paymentVo.setManuscriptVersion(i.getManuScript().getManuscriptVersion());
						paymentVo.setManuscriptTitle(i.getManuScript().getManuscriptTitle());
						paymentVo.setTransactionMode(i.getTransactionMode());
						paymentVo.setTransactionId(i.getTransactionId());
						paymentVo.setUserEmailId(i.getUser().getUsername());
						paymentVo.setUserName(i.getUser().getFirstName()+""+i.getUser().getLastName());
						paymentVo.setAmount(i.getAmount());
						paymentVo.setPaymentMode(i.getPaymentMode());
						paymentHistoryList.add(paymentVo);
					});
				}
			}
		}else if(paymentHistoryVO.getRoleId()!=null && paymentHistoryVO.getRoleId().equals(3) && paymentHistoryVO.getJournalId() == null) {
			List<UserJournal> userJournalList = userJournalRepo.getUserJournalByUserId(paymentHistoryVO.getUserId());
			if(userJournalList!=null && !userJournalList.isEmpty()) {
				userJournalList.parallelStream().forEach(j->{
					JournalId.add(j.getJournalId());
				});	
				List<UserInvoice> userInvoiceList=userInvoiceRepo.getPaymentHistoryByJournalId(JournalId);
				if(userInvoiceList!=null && !userInvoiceList.isEmpty()) {
					userInvoiceList.parallelStream().forEach(i->{
						PaymentHistoryVO paymentVo=new PaymentHistoryVO();
						paymentVo.setUserId(i.getUserId());
						paymentVo.setPaymentId(i.getId());
						paymentVo.setRoleId(3);
						paymentVo.setManuscriptId(i.getManuscriptId());
						paymentVo.setTransactionDate(i.getTransactionDate());
						paymentVo.setTransactionStatus(i.getTransactionStatus());
						if(i.getJournal()!=null) {
							paymentVo.setJounralTitle(i.getJournal().getJournalTitle());
							paymentVo.setJournalCode(i.getJournal().getJournalCode());
							paymentVo.setJournalId(i.getJournalId());
						}
						paymentVo.setManuscriptCode(i.getManuScript().getManuscriptCode());
						paymentVo.setManuscriptVersion(i.getManuScript().getManuscriptVersion());
						paymentVo.setManuscriptTitle(i.getManuScript().getManuscriptTitle());
						paymentVo.setTransactionMode(i.getTransactionMode());
						paymentVo.setUserEmailId(i.getUser().getUsername());
						paymentVo.setTransactionId(i.getTransactionId());
						paymentVo.setUserName(i.getUser().getFirstName()+""+i.getUser().getLastName());
						paymentVo.setAmount(i.getAmount());
						paymentVo.setPaymentMode(i.getPaymentMode());
						paymentHistoryList.add(paymentVo);
					});
				}
			}
		}else if(paymentHistoryVO.getRoleId()!=null && paymentHistoryVO.getRoleId().equals(5) && paymentHistoryVO.getJournalId() == null) {
			List<UserJournal> userJournalList = userJournalRepo.getUserJournalByUserId(paymentHistoryVO.getUserId());
			if(userJournalList!=null && !userJournalList.isEmpty()) {
				userJournalList.parallelStream().forEach(j->{
					JournalId.add(j.getJournalId());
				});	
				List<UserInvoice> userInvoiceList=userInvoiceRepo.getPaymentHistoryByJournalId(JournalId);
				if(userInvoiceList!=null && !userInvoiceList.isEmpty()) {
					userInvoiceList.parallelStream().forEach(i->{
						PaymentHistoryVO paymentVo=new PaymentHistoryVO();
						paymentVo.setUserId(i.getUserId());
						paymentVo.setPaymentId(i.getId());
						paymentVo.setRoleId(5);
						paymentVo.setManuscriptId(i.getManuscriptId());
						paymentVo.setTransactionDate(i.getTransactionDate());
						paymentVo.setTransactionStatus(i.getTransactionStatus());
						if(i.getJournal()!=null) {
							paymentVo.setJounralTitle(i.getJournal().getJournalTitle());
							paymentVo.setJournalCode(i.getJournal().getJournalCode());
							paymentVo.setJournalId(i.getJournalId());
						}
						paymentVo.setManuscriptCode(i.getManuScript().getManuscriptCode());
						paymentVo.setManuscriptVersion(i.getManuScript().getManuscriptVersion());
						paymentVo.setManuscriptTitle(i.getManuScript().getManuscriptTitle());
						paymentVo.setTransactionMode(i.getTransactionMode());
						paymentVo.setTransactionId(i.getTransactionId());
						paymentVo.setUserEmailId(i.getUser().getUsername());
						paymentVo.setUserName(i.getUser().getFirstName()+""+i.getUser().getLastName());
						paymentVo.setAmount(i.getAmount());
						paymentVo.setPaymentMode(i.getPaymentMode());
						paymentHistoryList.add(paymentVo);
					});
				}
			}
		}else if(paymentHistoryVO.getRoleId()!=null && paymentHistoryVO.getRoleId().equals(4) && paymentHistoryVO.getJournalId() == null) {
			List<UserInvoice> userInvoiceList=userInvoiceRepo.getPaymentHistoryByUser(paymentHistoryVO.getUserId());
			if(userInvoiceList!=null && !userInvoiceList.isEmpty()) {
				userInvoiceList.parallelStream().forEach(i->{
					PaymentHistoryVO paymentVo=new PaymentHistoryVO();
					paymentVo.setUserId(i.getUserId());
					paymentVo.setPaymentId(i.getId());
					paymentVo.setTransactionId(i.getTransactionId());
					paymentVo.setRoleId(4);
					paymentVo.setManuscriptId(i.getManuscriptId());
					paymentVo.setTransactionDate(i.getTransactionDate());
					paymentVo.setTransactionStatus(i.getTransactionStatus());
					if(i.getJournal()!=null) {
						paymentVo.setJounralTitle(i.getJournal().getJournalTitle());
						paymentVo.setJournalCode(i.getJournal().getJournalCode());
						paymentVo.setJournalId(i.getJournalId());
					}
					paymentVo.setManuscriptCode(i.getManuScript().getManuscriptCode());
					paymentVo.setManuscriptVersion(i.getManuScript().getManuscriptVersion());
					paymentVo.setManuscriptTitle(i.getManuScript().getManuscriptTitle());
					paymentVo.setTransactionMode(i.getTransactionMode());
					paymentVo.setTransactionId(i.getTransactionId());
					paymentVo.setUserEmailId(i.getUser().getUsername());
					paymentVo.setUserName(i.getUser().getFirstName()+" "+i.getUser().getLastName());
					paymentVo.setAmount(i.getAmount());
					paymentVo.setPaymentMode(i.getPaymentMode());
					paymentHistoryList.add(paymentVo);
				});
			}
		}else if(paymentHistoryVO.getJournalId()!=null && paymentHistoryVO.getUserId()==null) {
		List<UserInvoice> userInvoiceList=userInvoiceRepo.getPaymentHistoryJournalId(paymentHistoryVO.getJournalId());
		if(userInvoiceList!=null && !userInvoiceList.isEmpty()) {
			userInvoiceList.parallelStream().forEach(i->{
				PaymentHistoryVO paymentVo=new PaymentHistoryVO();
				paymentVo.setUserId(i.getUserId());
				paymentVo.setPaymentId(i.getId());
				paymentVo.setManuscriptId(i.getManuscriptId());
				paymentVo.setTransactionDate(i.getTransactionDate());
				paymentVo.setTransactionStatus(i.getTransactionStatus());
				if(i.getJournal()!=null) {
					paymentVo.setJounralTitle(i.getJournal().getJournalTitle());
					paymentVo.setJournalCode(i.getJournal().getJournalCode());
					paymentVo.setJournalId(i.getJournalId());
				}
				paymentVo.setManuscriptCode(i.getManuScript().getManuscriptCode());
				paymentVo.setManuscriptVersion(i.getManuScript().getManuscriptVersion());
				paymentVo.setManuscriptTitle(i.getManuScript().getManuscriptTitle());
				paymentVo.setTransactionMode(i.getTransactionMode());
				paymentVo.setTransactionId(i.getTransactionId());
				paymentVo.setUserEmailId(i.getUser().getUsername());
				paymentVo.setUserName(i.getUser().getFirstName()+""+i.getUser().getLastName());
				paymentVo.setAmount(i.getAmount());
				paymentVo.setPaymentMode(i.getPaymentMode());
				paymentHistoryList.add(paymentVo);
			});
		}
	}
	return paymentHistoryList;
	
	}

	@Override
	public EncryptionDecryptionVo encryptionDecryption(EncryptionDecryptionVo encryptionDecryptionVo) throws Exception {
		EncryptionDecryptionVo add=new EncryptionDecryptionVo();
		//Encryption
		if(encryptionDecryptionVo.getData()!=null && !encryptionDecryptionVo.getData().isEmpty() && encryptionDecryptionVo.getKey()!=null && !encryptionDecryptionVo.getKey().isEmpty()) {
			setKey(encryptionDecryptionVo.getKey());
			encrypt(encryptionDecryptionVo.getData().trim());
			String encryptedData=getEncryptedString();
			 if(encryptedData!=null && !encryptedData.isEmpty()) {
				 add.setData(encryptionDecryptionVo.getData());
				 add.setKey(encryptionDecryptionVo.getKey());
				 add.setEncryptedData(encryptedData);
				 return add;
			 }
		}
		//Decryption
		if(encryptionDecryptionVo.getKey()!=null && !encryptionDecryptionVo.getKey().isEmpty() && encryptionDecryptionVo.getEncryptedData()!=null && !encryptionDecryptionVo.getEncryptedData().isEmpty()) {
			setKey(encryptionDecryptionVo.getKey());
			final String strToDecrypt = encryptionDecryptionVo.getEncryptedData();
			if(strToDecrypt!=null && !strToDecrypt.isEmpty())
				decrypt(strToDecrypt.trim());
			String decryptedData=PaymentServiceImpl.getDecryptedString();
			if(decryptedData!=null && !decryptedData.isEmpty()) {
				add.setDecrptedData(decryptedData);
				add.setEncryptedData(encryptionDecryptionVo.getEncryptedData());
				add.setKey(encryptionDecryptionVo.getKey());
				return add;
			}
		}
		return null;
	}
	
	public  void setKey(String myKey){
		MessageDigest sha = null;
		try {
		key = myKey.getBytes("UTF-8");
		log.debug("key.length " + key.length);
		sha = MessageDigest.getInstance("SHA-1");
		key = sha.digest(key);
		key = Arrays.copyOf(key, 16); // use only first 128 bit
		log.debug("key.length " + key.length);
		log.debug(new String(key,"UTF-8"));
		secretKey = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		}

		public static String getDecryptedString() {
		return decryptedString;
		}
		public static void setDecryptedString(String decryptedString) {
			PaymentServiceImpl.decryptedString = decryptedString;
		}
		public static String getEncryptedString() {
		return encryptedString;
		}
		public static void setEncryptedString(String encryptedString) {
			PaymentServiceImpl.encryptedString = encryptedString;
		}
		public String encrypt(String strToEncrypt)
		{
		try
		{
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		setEncryptedString(Base64.encodeBase64String(cipher.doFinal(strToEncrypt.getBytes("UTF-8"))));
		}
		catch (Exception e)
		{
		log.debug("Error while encrypting: "+e.toString());
		}
		return null;
		}
		public  String decrypt(String strToDecrypt)
		{
		try
		{
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		setDecryptedString(new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt))));
		}
		catch (Exception e)
		{
		log.debug("Error while decrypting: "+e.toString());
		}
		return null;
		}

		@Override
		public String savePaymnetStatusHistory(PaymentStatusHistoryVO paymentStatusHistoryVO) {
			
			ManuScript msu = manuscriptRepo.getById(paymentStatusHistoryVO.getManuscriptId());
			PaymentStatusHistory  payment = new PaymentStatusHistory();
			if(paymentStatusHistoryVO != null && msu != null) {
			payment.setJournalId(msu.getJournalId());
			payment.setManuscriptId(paymentStatusHistoryVO.getManuscriptId());
			payment.setManuscriptStatusId(msu.getManuscriptStatusId());
			payment.setUserId(paymentStatusHistoryVO.getUserId());
			payment.setIpAddress(paymentStatusHistoryVO.getIpAddress());
			payment.setComment(paymentStatusHistoryVO.getComment());		
			paymentStatusHistoryRepo.save(payment);
			return "Payment status saved successfully for article "+msu.getManuscriptCode();
			} else {				
				return "Payment status not saved";
			}
			
		}
}
