package com.digi.uniprr.VO;

import java.io.Serializable;

public class EncryptionDecryptionVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String data;
	private String key;
	private String encryptedData;
	private String decrptedData;
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getEncryptedData() {
		return encryptedData;
	}
	public void setEncryptedData(String encryptedData) {
		this.encryptedData = encryptedData;
	}
	public String getDecrptedData() {
		return decrptedData;
	}
	public void setDecrptedData(String decrptedData) {
		this.decrptedData = decrptedData;
	}
	@Override
	public String toString() {
		return "EncryptionDecryptionVo [data=" + data + ", key=" + key + ", encryptedData=" + encryptedData
				+ ", decrptedData=" + decrptedData + "]";
	}

}
