package com.bootup.mocker.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;

/**
 * @author b0095753 on 11/3/17.
 */
public class EncryptionUtils {

  public enum HashingAlgorithm{
    SHA_256("SHA-256"),MD5("MD5"),SHA_1("SHA-1");
    String name;

    HashingAlgorithm(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  public static String getHashedString(HashingAlgorithm hashingAlgorithm,String message)
      throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance(hashingAlgorithm.getName());
    byte[] encodedhash = digest.digest(message.getBytes(StandardCharsets.UTF_8));
    return bytesToHex(encodedhash);
  }

  private static String  bytesToHex(byte[] hash) {
    return DatatypeConverter.printHexBinary(hash);
  }


}
