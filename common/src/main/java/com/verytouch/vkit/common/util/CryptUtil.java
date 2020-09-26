package com.verytouch.vkit.common.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NullCipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.function.BiFunction;

/**
 * 加解密工具类
 *
 * @author verytouch
 * @since 2020/9/17 16:34
 */
public final class CryptUtil {

    private CryptUtil() {}

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
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(System.currentTimeMillis());
        if (iv != null) {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec, secureRandom);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, key, secureRandom);
        }
        if (blockSize == -1) {
            return cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } else {
            return doFinalByBlock(cipher, data.getBytes(StandardCharsets.UTF_8), blockSize);
        }
    }

    /**
     * 通用加密算法，使用默认参数
     * @param data 待加密数据
     * @param key 密钥
     */
    public static byte[] encrypt(String data, Key key) throws Exception {
        return encrypt(data, key.getAlgorithm(), key, null, -1);
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
     * 通用解密方法，使用默认参数
     * @param data 待解密数据
     * @param key 密钥
     */
    public static String decrypt(byte[] data, Key key) throws Exception {
        return decrypt(data, key.getAlgorithm(), key, null, -1);
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

        private KeyGen() {}

        public static final String[] PKCS8_PUBLIC = new String[] {"-----BEGIN PUBLIC KEY-----", "-----END PUBLIC KEY-----"};
        public static final String[] PKCS8_PRIVATE = new String[] {"-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----"};

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
        public static SecretKey secretKey(String algorithm, byte[] key) {
            return new SecretKeySpec(key, algorithm);
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
         * 非对称算法密钥对生成并保存pkcs8格式到指定文件
         */
        public static void keyPairPkcs8(String algorithm, int keySize, Path pub, Path pri) throws Exception {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
            keyPairGenerator.initialize(keySize);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            BiFunction<byte[], String[], String> pkcs8Fun = (data, addtional) -> {
                String s = Base64.getEncoder().encodeToString(data).replaceAll("(.{64})", "$1\n");
                return String.format("%s\n%s\n%s", addtional[0], s, addtional[1]);
            };

            Files.write(pub, pkcs8Fun.apply(keyPair.getPublic().getEncoded(), PKCS8_PUBLIC).getBytes());
            Files.write(pri, pkcs8Fun.apply(keyPair.getPrivate().getEncoded(), PKCS8_PRIVATE).getBytes());
        }

        /**
         * 非对称算法公钥还原
         */
        public static PublicKey publicKey(String algorithm, byte[] key) throws Exception {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            return keyFactory.generatePublic(keySpec);
        }

        /**
         * 非对称算法私钥还原
         */
        public static PrivateKey privateKey(String algorithm, byte[] key) throws Exception {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            return keyFactory.generatePrivate(keySpec);
        }

        /**
         * pkcs8公钥还原
         */
        public static PublicKey publicKeyFromPkcs8(String algorithm, String pkcs8) throws Exception {
            String base64 = pkcs8.replace(PKCS8_PUBLIC[0], "")
                    .replace(PKCS8_PUBLIC[1], "")
                    .replaceAll("[\r\n]", "");
            return publicKey(algorithm, Base64.getDecoder().decode(base64));
        }

        /**
         * pkcs8私钥还原
         */
        public static PrivateKey privateKeyFromPkcs8(String algorithm, String pkcs8) throws Exception {
            String base64 = pkcs8.replace(PKCS8_PRIVATE[0], "")
                    .replace(PKCS8_PRIVATE[1], "")
                    .replaceAll("[\r\n]", "");
            return privateKey(algorithm, Base64.getDecoder().decode(base64));
        }
    }

    /**
     * AES对称加密
     */
    public static final class AES {

        private AES() {}

        private static final String ALG = "AES/CTR/NoPadding";
        private static final byte[] IV = "1234567812345678".getBytes();
        private static final int KEY_SIZE = 256;

        /**
         * 生成256位密钥
         */
        public static byte[] secretKey() throws Exception {
            return KeyGen.secretKey("AES", KEY_SIZE).getEncoded();
        }

        /**
         * 加密
         */
        public static String encryptHex(String data, byte[] key) throws Exception {
            SecretKey secretKey = KeyGen.secretKey("AES", key);
            return toHex(encrypt(data, ALG, secretKey, IV, -1));
        }

        /**
         * 解密
         */
        public static String decryptHex(String data, byte[] key) throws Exception {
            SecretKey secretKey = KeyGen.secretKey("AES", key);
            return decrypt(fromHex(data), ALG, secretKey, IV, -1);
        }
    }

    /**
     * RSA非对称加密
     */
    public static final class RSA {

        private RSA() {}

        private static final int ENCRYPT_BLOCK_SIZE = 117;
        private static final int DECRYPT_BLOCK_SIZE = 128;
        private static final int KEY_SIZE = 1024;

        /**
         * 生成1024位密钥对
         */
        public static KeyPair keyPair() throws Exception {
            return KeyGen.keyPair("RSA", KEY_SIZE);
        }

        /**
         * 私钥加密
         */
        public static String privateEncryptHex(String data, byte[] key) throws Exception {
            PrivateKey privateKey = KeyGen.privateKey("RSA", key);
            return toHex(encrypt(data, "RSA", privateKey, null, ENCRYPT_BLOCK_SIZE));
        }

        /**
         * 公钥解密
         */
        public static String publicDecryptFromHex(String data, byte[] key) throws Exception {
            PublicKey publicKey = KeyGen.publicKey("RSA", key);
            return decrypt(fromHex(data), "RSA", publicKey, null, DECRYPT_BLOCK_SIZE);
        }
    }

    /**
     * DSA签名认证
     */
    public static final class DSA {

        private DSA() {}

        private static final int KEY_SIZE = 1024;

        /**
         * 生成1024位密钥对
         */
        public static KeyPair keyPair() throws Exception {
            return KeyGen.keyPair("DSA", KEY_SIZE);
        }

        /**
         * 私钥签名
         */
        public static String privateSignHex(String data, byte[] key) throws Exception {
            PrivateKey privateKey = KeyGen.privateKey("DSA", key);
            return toHex(CryptUtil.sign(data, "DSA", privateKey));
        }

        /**
         * 公钥认证
         */
        public static boolean publicVerifyHex(String data, byte[] key, String sign) throws Exception {
            PublicKey publicKey = KeyGen.publicKey("DSA", key);
            return verify(data, "DSA", publicKey, fromHex(sign));
        }
    }

    /**
     * 一些算法
     */
    public static enum Algorithm {
        // 对称加密算法
        DES(1, "DES", "密钥长度56，加8校验位"),
        TRIPPLE_DES(1, "DESede", "密钥长度112, 168，加密方式位DES加密一次，解密一次，再加密一次"),
        AES(1, "AES", "密钥长度128，192，256"),
        RC4(1, "RC4", "密钥长度40-1024"),
        BLOWFISH(1, "Blowfish", "密钥长度32-448，且必须是8的倍数"),
        // 非对称加密算法
        RSA(2, "RSA", "密钥长度512，1024, 2048, 3072，4096...."),
        // 摘要算法
        MD5(3, "MD5", ""),
        SHA1(3, "SHA-1", ""),
        SHA256(3, "SHA-256", ""),
        // 签名算法
        DSA(4, "DSA", "密钥长度参考RSA，只用作签名"),
        MD5_RSA(4, "SHA1withRSA", ""),
        SHA1_RSA(4, "MD5withRSA", "");

        private final int type;
        private final String algorithm;
        private final String remark;

        Algorithm(int type, String algorithm, String remark) {
            this.type = type;
            this.algorithm = algorithm;
            this.remark = remark;
        }

        public int getType() {
            return type;
        }

        public String getAlgorithm() {
            return algorithm;
        }

        public String getRemark() {
            return remark;
        }
    }
}
