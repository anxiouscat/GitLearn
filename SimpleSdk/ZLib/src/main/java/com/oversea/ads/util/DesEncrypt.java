package com.oversea.ads.util;

import java.io.IOException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DesEncrypt {

	private final static String DES = "DES";
	private static final String CHAR_SET = "UTF-8";

	/**
	 * Description 根据键值进行加密
	 * 
	 * @param data
	 * @param key
	 *            加密键byte数组
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String data, String key) throws Exception {
		byte[] bt = encrypt(data.getBytes(CHAR_SET), key.getBytes(CHAR_SET));
		String strs = AndroidBase64.encodeToString(bt, 0);
		return strs;
	}

	public static byte[] encryptByte(byte[] data, byte[] key) throws Exception {
		byte[] bt = encrypt(data, key);
		byte[] strs = AndroidBase64.encode(bt, 0);
		return strs;
	}

	/**
	 * Description 根据键值进行解密
	 * 
	 * @param data
	 * @param key
	 *            加密键byte数组
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static String decryptString(String data, String key)
			throws IOException, Exception {
		if (data == null)
			return null;
		byte[] buf = AndroidBase64.decode(data.getBytes(CHAR_SET), 0);
		byte[] bt = decrypt(buf, key.getBytes(CHAR_SET));
		return new String(bt);
	}

	public static byte[] decryptByte(byte[] data, byte[] key)
			throws IOException, Exception {
		if (data == null || key == null) {
			return null;
		}
		byte[] buf = AndroidBase64.decode(data, 0);
		byte[] bt = decrypt(buf, key);
		return bt;
	}

	public static String decryptByteAndString(String data, byte[] key)
			throws IOException, Exception {
		if (data == null || key == null) {
			return null;
		}
		byte[] buf = AndroidBase64.decode(data.getBytes(CHAR_SET), 0);
		byte[] bt = decrypt(buf, key);
		return new String(bt);
	}

	/**
	 * Description 根据键值进行加密
	 * 
	 * @param data
	 * @param key
	 *            加密键byte数组
	 * @return
	 * @throws Exception
	 */
	private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
		// 生成一个可信任的随机数源
		SecureRandom sr = new SecureRandom();

		// 从原始密钥数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);

		// 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);

		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance(DES);

		// 用密钥初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);

		return cipher.doFinal(data);
	}

	/**
	 * Description 根据键值进行解密
	 * 
	 * @param data
	 * @param key
	 *            加密键byte数组
	 * @return
	 * @throws Exception
	 */
	private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
		// 生成一个可信任的随机数源
		SecureRandom sr = new SecureRandom();

		// 从原始密钥数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);

		// 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);

		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance(DES);

		// 用密钥初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);

		return cipher.doFinal(data);
	}
}
