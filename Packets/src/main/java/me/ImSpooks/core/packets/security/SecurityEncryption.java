package me.ImSpooks.core.packets.security;

import me.ImSpooks.core.helpers.Global;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

/**
 * Created by Nick on 02 okt. 2019.
 * Copyright © ImSpooks
 */
public class SecurityEncryption {

    private Cipher encryptor;
    private Cipher decryptor;

    private SecurityEncryption(String key, String initVector) throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException {
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));

        encryptor = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        encryptor.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

        decryptor = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        decryptor.init(Cipher.DECRYPT_MODE, skeySpec, iv);
    }

    public static SecurityEncryption newInstance(String key, String initVector) throws InvalidCredentialsException {
        if (key.length() != 16)
            throw new IllegalArgumentException("Invalid key size (" + key.length() + ") must be 16");
        if (initVector.length() != 16)
            throw new IllegalArgumentException("Invalid initVector size (" + initVector.length() + ") must be 16");

        try {
            return new SecurityEncryption(key, initVector);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new InvalidCredentialsException(e);
        }
    }

    public String encrypt(byte[] value) {
        try {
            return new String(Base64.getEncoder().encode(this.encryptor.doFinal(value)));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] decrypt(String encrypted) {
        try {
            return this.decryptor.doFinal(Base64.getDecoder().decode(encrypted));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public String encryptCollection(ArrayList collection) {
        return this.encrypt(Global.GSON.toJson(collection).getBytes());
    }

    public String encryptCollection(Object... objects) {
        return this.encrypt(Global.GSON.toJson(new ArrayList<>(Arrays.asList(objects))).getBytes());
    }

    public ArrayList decryptCollection(String encrypted) {
        return Global.GSON.fromJson(new String(this.decrypt(encrypted)), ArrayList.class);
    }

//    public byte[] encryptClasses(Object... o) {
//        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            DataOutputStream out = new DataOutputStream(baos);
//            for (Object obj : o) {
//                if (obj == null)
//                    throw new IllegalArgumentException("Input may not be null");
//
//                if (obj instanceof String) {
//                    out.writeUTF((String) obj);
//                } else if (obj instanceof Long) {
//                    out.writeLong((long) obj);
//                } else {
//                    throw new UnsupportedOperationException("Unknown encryption type " + obj.getClass().getName());
//                }
//            }
//            out.flush();
//            return this.encrypt(baos.toString());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public Object[] decryptClasses(byte[] encrypted, Class<?>... classes) throws IOException {
//        if (encrypted == null)
//            throw new IllegalArgumentException("Encrypted byte array may not be null");
//
//        if (classes == null || classes.length == 0)
//            throw new IllegalArgumentException("Types may not be null or empty");
//
//        Object[] result = new Object[classes.length];
//        int pointer = 0;
//
//        DataInputStream in = new DataInputStream(new ByteArrayInputStream(this.decrypt(encrypted)));
//        for (Class<?> c : classes) {
//            if (c == null)
//                throw new IllegalArgumentException("Type may not be null");
//            if (c == String.class) {
//                result[pointer++] = in.readUTF();
//            } else if (c == Long.class) {
//                result[pointer++] = in.readLong();
//            } else {
//                throw new UnsupportedOperationException("Unknown class type " + c.getName());
//            }
//        }
//        return result;
//    }

}
