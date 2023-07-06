package com.covenant.tribe.security;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Component
@Getter
public class KeysReader {

    public PrivateKey getPrivateKey(String pathToKey) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        ClassPathResource classPathResource = new ClassPathResource("keys/" + pathToKey);

        byte[] privateKeyInBytes = FileCopyUtils.copyToByteArray(classPathResource.getInputStream());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyInBytes);
        return keyFactory.generatePrivate(keySpec);
    }

    public RSAPublicKey getPublicKey(String pathToKey) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        ClassPathResource classPathResource = new ClassPathResource("keys/" + pathToKey);
        byte[] binaryPublicKey = FileCopyUtils.copyToByteArray(classPathResource.getInputStream());

        String publicKeyString = new String(binaryPublicKey, Charset.defaultCharset());
        String publicKeyPem = publicKeyString
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");
        byte[] publicKeyInBytes = Base64.decodeBase64(publicKeyPem);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyInBytes);
        return (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
    }

}
