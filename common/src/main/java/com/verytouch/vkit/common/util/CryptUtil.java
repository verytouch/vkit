package com.verytouch.vkit.common.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NullCipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 加解密工具类
 *
 * @author verytouch
 * @since 2020/9/17 16:34
 */
public final class CryptUtil {

    // 电码本模式
    public static final String MODE_ECB = "ECB";
    // 加密块链模式，推荐
    public static final String MODE_CBC = "CBC";
    // 输出反馈模式
    public static final String MODE_OFB = "OFB";
    // 加密反馈模式
    public static final String MODE_CFB = "CFB";
    // 计数器模式，推荐
    public static final String MODE_CTR = "CTR";

    // 块加密算法要求数据是N的倍数，不足N时以此种方式填充，JDK默认方式，不支持PKCS7
    public static final String PADDING_PKCS5 = "PKCS5Padding";
    // 不填充且不是N的倍数时会抛异常
    public static final String PADDING_NO_PADDING = "NoPadding";

    /**
     * 通用加密算法
     * @param data 待加密数据
     * @param algorithm 算法[/模式][/填充]
     * @param key 密钥
     * @param iv 偏移向量，对称加密ECB模式和非对称加密传null
     * @param blockSize 分段加密数据块大小，传-1不分段
     */
    public static byte[] encrypt(String data, String algorithm, Key key, byte[] iv, int blockSize) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        if (iv != null) {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        }
        if (blockSize == -1) {
            return cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } else {
            return doFinalByBlock(cipher, data.getBytes(StandardCharsets.UTF_8), blockSize);
        }
    }

    /**
     * 通用解密方法
     * @param data 待解密数据
     * @param algorithm 算法[/模式][/填充]
     * @param key 密钥
     * @param iv 偏移向量，对称加密ECB模式和非对称加密传null
     * @param blockSize 分段加密数据块大小，传-1不分段
     * @return
     * @throws Exception
     */
    public static String decrypt(byte[] data, String algorithm, Key key, byte[] iv, int blockSize) throws Exception {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            cipher = new NullCipher();
        }
        if (iv != null) {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key);
        }
        if (blockSize == -1) {
            return new String(cipher.doFinal(data));
        } else {
            return new String(doFinalByBlock(cipher, data, blockSize));
        }
    }

    /**
     * 分段加解密
     */
    private static byte[] doFinalByBlock(Cipher cipher, byte[] data, int blockSize) throws Exception {
        int dataLen = data.length;
        int blockCount = (int) Math.ceil(dataLen * 1.0 / blockSize);
        byte[] cache;
        int offset, len;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            for (int i = 0; i < blockCount; i++) {
                offset = i * blockSize;
                len = Math.min(dataLen - offset, blockSize);
                cache = cipher.doFinal(data, offset, len);
                baos.write(cache, 0, cache.length);
            }
            return baos.toByteArray();
        }
    }

    /**
     * 通用签名方法，私钥签名
     * @param data 待签名数据
     * @param algorithm 算法[/模式][/填充]
     * @param privateKey 私钥
     */
    public static byte[] sign(String data, String algorithm, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance(algorithm);
        signature.initSign(privateKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        return signature.sign();
    }

    /**
     * 通用验证方法，公钥验证
     * @param data 待验证数据
     * @param algorithm 算法[/模式][/填充]
     * @param publicKey 公钥
     * @param sign 签名
     */
    public static boolean verify(String data, String algorithm, PublicKey publicKey, byte[] sign) throws Exception {
        Signature signature = Signature.getInstance(algorithm);
        signature.initVerify(publicKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        return signature.verify(sign);
    }

    /**
     * 通用摘要方法
     * @param data 待摘要信息
     * @param algorithm 摘要算法
     */
    public static byte[] digest(String data, String algorithm) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.update(data.getBytes(StandardCharsets.UTF_8));
        return digest.digest();
    }

    /**
     * 16进制编码
     */
    public static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        String hex;
        for (byte datum : data) {
            hex = Integer.toHexString(datum & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 16进制解码
     */
    public static byte[] fromHex(String hexStr) {
        if (hexStr.length() < 1) {
            return new byte[0];
        }
        byte[] result = new byte[hexStr.length() / 2];
        int high, low;
        for (int i = 0; i < hexStr.length() / 2; i++) {
            high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * 秘钥生成
     * <p>KeyGenerator和SecretKeyFactory，都是javax.crypto包的，生成的key主要是提供给AES，DES，3DES，MD5，SHA1等对称单向加密算法。</p>
     * <p>KeyPairGenerator和KeyFactory，都是java.security包的，生成的key主要是提供给DSA，RSA， EC等非对称加密算法。</p>
     */
    public static final class KeyGen {
        /**
         * 对称算法密钥生成
         */
        public static SecretKey secretKey(String algorithm, int keySize) throws Exception {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
            keyGenerator.init(keySize);
            return keyGenerator.generateKey();
        }

        /**
         * 对称算法密钥还原
         */
        public static SecretKey secretKey(String algorithm, byte[] encodedKey) {
            return new SecretKeySpec(encodedKey, algorithm);
        }

        /**
         * 非对称算法密钥对生成
         */
        public static KeyPair keyPair(String algorithm, int keySize) throws Exception {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
            keyPairGenerator.initialize(keySize);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            return keyPair;
        }

        /**
         * 非对称算法公钥还原
         */
        public static PublicKey publicKey(String algorithm, byte[] encodedKey) throws Exception {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            return keyFactory.generatePublic(keySpec);
        }

        /**
         * 非对称算法私钥还原
         */
        public static PrivateKey privateKey(String algorithm, byte[] encodedKey) throws Exception {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            return keyFactory.generatePrivate(keySpec);
        }

        private KeyGen() {}
    }

    public static class AES {

        private static final String ALG = "AES/CBC/PKCS5Padding";
        private static final byte[] IV = "1234567812345678".getBytes();

        public static String encryptHex(String data, String keySpec) throws Exception {
            SecretKey secretKey = KeyGen.secretKey("AES", fromHex(keySpec));
            return toHex(encrypt(data, ALG, secretKey, IV, -1));
        }

        public static String decryptHex(String data, String keySpec) throws Exception {
            SecretKey secretKey = KeyGen.secretKey("AES", fromHex(keySpec));
            return decrypt(fromHex(data), ALG, secretKey, IV, -1);
        }

        private AES () {}
    }

    private CryptUtil() {}
}
