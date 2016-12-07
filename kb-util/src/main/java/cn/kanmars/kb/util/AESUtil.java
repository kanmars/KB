package cn.kanmars.kb.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class AESUtil {
	/**
	 * IvParameterSpec
	 */
	private static String randomStr = "1234567812345678";
	
	private static String charset="utf-8";

	private static String cipherArithmetic = "AES/ECB/PKCS5Padding";//或者AES
	
	public static SecretKey getSecretKey(byte[] keyBytes){
		SecretKey result = null;
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG","SUN");
			secureRandom.setSeed(keyBytes);
			keyGenerator.init(secureRandom);
			result = keyGenerator.generateKey();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
		return result;
	}
//	public static SecretKey getSecretKey2(byte[] keyBytes){
//		SecretKey result = null;
//		try {
//			SecretKeySpec skey = new SecretKeySpec(keyBytes, "AES");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return result;
//	}
	
	public static byte[] encodeMessage(Key key,byte[] message){
		byte[] result = null;
		try {
			Cipher cipher = Cipher.getInstance(cipherArithmetic);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			result = cipher.doFinal(message);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return result;
	}
	public static byte[] decodeMessage(Key key,byte[] message){
		byte[] result = null;
		try {
			Cipher cipher = Cipher.getInstance(cipherArithmetic);
			cipher.init(Cipher.DECRYPT_MODE, key);
			result = cipher.doFinal(message);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static String encryptStr(Key key,String message){
		byte[] result = null;
		try {
			Cipher cipher = Cipher.getInstance(cipherArithmetic);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			result = cipher.doFinal(message.getBytes(charset));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			return Base64Util.encodeMessage(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String decryptStr(Key key,String message){
		byte[] result = null;
		try {
			Cipher cipher = Cipher.getInstance(cipherArithmetic);
			cipher.init(Cipher.DECRYPT_MODE, key);
			result = cipher.doFinal(Base64Util.decodeMessage(message));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		try {
			return new String(result,charset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String encryptStr(String key,String message) throws Exception{
		byte[] result = null;
		try {
			SecretKey key1 = getSecretKey(key.getBytes(charset));
			Cipher cipher = Cipher.getInstance(cipherArithmetic);
			cipher.init(Cipher.ENCRYPT_MODE, key1);
			result = cipher.doFinal(message.getBytes(charset));
			return Base64Util.encodeMessage(result);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	public static String decryptStr(String key,String message)  throws Exception{
		byte[] result = null;
		try {
			SecretKey key1 = getSecretKey(key.getBytes(charset));
			Cipher cipher = Cipher.getInstance(cipherArithmetic);
			cipher.init(Cipher.DECRYPT_MODE, key1);
			result = cipher.doFinal(Base64Util.decodeMessage(message));
			return new String(result,charset);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static void main(String[] args) {
		SecretKey key1 = getSecretKey("des-0001".getBytes());
		SecretKey key2 = getSecretKey("des-0001".getBytes());
		String message = "gome1234";
		byte[] encode = encodeMessage(key1, message.getBytes());

		System.out.println("加密后:" +encode.toString() );
		
		byte[] decode = decodeMessage(key2, encode);
		System.out.println(new String(decode));
		
		//System.out.println(AESUtil.encryptStr(key1, "112344"));
		//System.out.println(AESUtil.decryptStr(key1, "EX/y8Wrgpf+keBF3bVAivw=="));
	}
}
