package com.digi.uniprr.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptionDecryptionUtils {
	private static Logger log = LoggerFactory.getLogger(EncryptionDecryptionUtils.class);
	
	public static String encrypt(String input, String key) {
		byte[] crypted = null;
		try {

			SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");

			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skey);
			crypted = cipher.doFinal(input.getBytes());
		} catch (Exception e) {
			log.debug(e.toString());
		}
		java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();

		return new String(encoder.encodeToString(crypted));
	}

	public static String decrypt(String input, String key) {
		byte[] output = null;
		try {
			java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
			SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skey);
			output = cipher.doFinal(decoder.decode(input));
		} catch (Exception e) {
			log.debug(e.toString());
		}
		return new String(output);
	}
}
