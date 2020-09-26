package com.verytouch.vkit.common.util;

import org.junit.Test;

import javax.crypto.SecretKey;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import static com.verytouch.vkit.common.util.CryptUtil.*;

public class CryptUtilTest {

    public static final String DATA = "好好学习，day day up";

    @Test
    public void keyTest() throws Exception {
        String home = System.getProperty("user.home");
        Path pub = Paths.get(home + "\\Desktop\\pub.pem");
        Path pri = Paths.get(home + "\\Desktop\\pri.pem");

        KeyGen.keyPairPkcs8("RSA", 1024, pub, pri);
        String pubStr = new String(Files.readAllBytes(pub));
        String priStr = new String(Files.readAllBytes(pri));

        // System.out.println(pubStr);
        // System.out.println(priStr);

        PrivateKey rsaPri = KeyGen.privateKeyFromPkcs8("RSA", priStr);
        PublicKey rsaPub = KeyGen.publicKeyFromPkcs8("RSA", pubStr);

        String s1 = RSA.privateEncryptHex(DATA, rsaPri.getEncoded());
        System.out.println(s1);
        System.out.println(RSA.publicDecryptFromHex(s1, rsaPub.getEncoded()));
    }

    @Test
    public void aesKeyTest() throws Exception {
        SecretKey secretKey = KeyGen.secretKey("AES/CBC/PKCS5Padding", 256);
        System.out.println(secretKey.getAlgorithm());
    }

    @Test
    public void aesTest() throws Exception {
        String keySpecHex = "ca9064f80170b7e1960006bb1f4f200e1e4a53f1cf7c927e58b1599e48a92e10";
        String encryptHex = AES.encryptHex(DATA, fromHex(keySpecHex));
        System.out.println(encryptHex);
        System.out.println(AES.decryptHex(encryptHex, fromHex(keySpecHex)));
    }

    @Test
    public void rsaKeyTest() throws Exception {
        KeyPair keyPair = RSA.keyPair();
        System.out.println(toHex(keyPair.getPublic().getEncoded()));
        System.out.println(toHex(keyPair.getPrivate().getEncoded()));
    }

    @Test
    public void rsaTest() throws Exception {
        String publicKeySpecHex = "30819f300d06092a864886f70d010101050003818d003081890281810099e851b09e80b18fbd570d533fd6070a824637a92da0e1162cff6e4082d475b2bdbe06eeb5f81c12ec32c5efa6ea710b566d29e2ba3b40aad14028c962a8f7f6840a9dd1fca808a54bbf7ce9c4e485a05b2e30078f8e699ce1e2425c423d032dc4997e27bacdd996709190c0be2740818a6f4246488018866dd9fcaf89fa96f30203010001";
        String privateKeySpecHex = "30820278020100300d06092a864886f70d0101010500048202623082025e0201000281810099e851b09e80b18fbd570d533fd6070a824637a92da0e1162cff6e4082d475b2bdbe06eeb5f81c12ec32c5efa6ea710b566d29e2ba3b40aad14028c962a8f7f6840a9dd1fca808a54bbf7ce9c4e485a05b2e30078f8e699ce1e2425c423d032dc4997e27bacdd996709190c0be2740818a6f4246488018866dd9fcaf89fa96f30203010001028181008043e760c4980862fcaf0201f15f93baa45b94a3bfcd26096f32e95586333e6bda49f649170a518b4ba1fce7ff39bf406da4c89596132f8fac9845e59ffda5b85384087dcf7600bdc8a385fec840bfd1272ae0034506ba7ee36e409f76f6cdea963420c369045c3df8a62a92a2821b15985ec1c740b43b678a54086ff0458121024100e9c86b7869818f1de938f049d27ec87f43209e3de8d05776016ed99f4864370dc8b49ed7d47c0ee992ac8fb81c53fcc699c884ce68debef0f26c85b3ae90181b024100a888a73334d8c2fc6f1be7cbfc56156347d2e40e13ffaee3e8677755cbccae888fdcc720fb9c00574b7f3dcb2445ec1c6a5fa7a523a947ae259bdaa682d51a0902403eda97aa6413653b117d317d678e96b1408700847c97a72c1eee82ac04768d26b3937c3162fb87b6dcac70677b96491bcc34d11ffdaa05841a697af8f831dd89024100845024d472dcead83c72f01c99d30b9f9237e38b99efd6625f873b8d441f41b73d1adbcaed3ea095e853d133b71a9e3000e3703e9a2cc075dc58ebe4663adfe1024100e72abe0abca6b5f4d5d5819c9b652b9664a5706a82f341d4204b45a6d228411438eb90b20dd86b347105887cb7dee39c1192fa156a59ee34c1e0c70a50fe7aba";
        String encryptHex = RSA.privateEncryptHex(DATA, fromHex(privateKeySpecHex));
        System.out.println(encryptHex);
        System.out.println(RSA.publicDecryptFromHex(encryptHex, fromHex(publicKeySpecHex)));
    }

    @Test
    public void dsaKeyTest() throws Exception {
        KeyPair keyPair = DSA.keyPair();
        System.out.println(toHex(keyPair.getPublic().getEncoded()));
        System.out.println(toHex(keyPair.getPrivate().getEncoded()));
    }

    @Test
    public void dsaTest() throws Exception {
        String publicKeySpecHex = "308201b73082012c06072a8648ce3804013082011f02818100fd7f53811d75122952df4a9c2eece4e7f611b7523cef4400c31e3f80b6512669455d402251fb593d8d58fabfc5f5ba30f6cb9b556cd7813b801d346ff26660b76b9950a5a49f9fe8047b1022c24fbba9d7feb7c61bf83b57e7c6a8a6150f04fb83f6d3c51ec3023554135a169132f675f3ae2b61d72aeff22203199dd14801c70215009760508f15230bccb292b982a2eb840bf0581cf502818100f7e1a085d69b3ddecbbcab5c36b857b97994afbbfa3aea82f9574c0b3d0782675159578ebad4594fe67107108180b449167123e84c281613b7cf09328cc8a6e13c167a8b547c8d28e0a3ae1e2bb3a675916ea37f0bfa213562f1fb627a01243bcca4f1bea8519089a883dfe15ae59f06928b665e807b552564014c3bfecf492a038184000281802e0a73faeeccb58231e313104638dce3c54f6c09c1f9eabbb2b7ef54409b465b1a3dba4f635b177c07ecb470d5bc6a38925eaa4e39f893f4ddbc9084edfa1e55b019fc360636ad9818f49f91099a962c64b797cbcbea132e58cc25fcd55392c42966f496164866eafc005d60c65c71d7f8ea7dc6ff2fe501c0adbd79a24f16aa";
        String privateKeySpecHex = "3082014c0201003082012c06072a8648ce3804013082011f02818100fd7f53811d75122952df4a9c2eece4e7f611b7523cef4400c31e3f80b6512669455d402251fb593d8d58fabfc5f5ba30f6cb9b556cd7813b801d346ff26660b76b9950a5a49f9fe8047b1022c24fbba9d7feb7c61bf83b57e7c6a8a6150f04fb83f6d3c51ec3023554135a169132f675f3ae2b61d72aeff22203199dd14801c70215009760508f15230bccb292b982a2eb840bf0581cf502818100f7e1a085d69b3ddecbbcab5c36b857b97994afbbfa3aea82f9574c0b3d0782675159578ebad4594fe67107108180b449167123e84c281613b7cf09328cc8a6e13c167a8b547c8d28e0a3ae1e2bb3a675916ea37f0bfa213562f1fb627a01243bcca4f1bea8519089a883dfe15ae59f06928b665e807b552564014c3bfecf492a04170215009074fdf1403a838485a2ec30adc014ab51c1e2c2";
        String privateSign = DSA.privateSignHex(DATA, fromHex(privateKeySpecHex));
        System.out.println(privateSign);
        System.out.println(DSA.publicVerifyHex(DATA + "", fromHex(publicKeySpecHex), privateSign));
    }

    @Test
    public void digestTest() throws Exception {
        System.out.println(toHex(digest(DATA, Algorithm.MD5.getAlgorithm())));
        System.out.println(toHex(digest(DATA, Algorithm.SHA1.getAlgorithm())));
        System.out.println(toHex(digest(DATA, Algorithm.SHA256.getAlgorithm())));
    }

    @Test
    public void des() throws Exception {
        SecretKey secretKey = KeyGen.secretKey(Algorithm.DES.getAlgorithm(), "12345678".getBytes());
        byte[] str = encrypt(DATA, secretKey);
        System.out.println(toHex(str));
        System.out.println(decrypt(str, secretKey));
    }

}