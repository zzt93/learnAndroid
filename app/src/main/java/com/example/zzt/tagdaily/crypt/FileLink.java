package com.example.zzt.tagdaily.crypt;

import android.util.Log;

import com.example.zzt.tagdaily.BuildConfig;
import com.example.zzt.tagdaily.logic.Default;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by zzt on 10/6/15.
 * <p/>
 * Usage:
 * <p/>
 * an abstraction to imitate a symbolic link to a file <br/>
 * for Android method to do it demanding a very high api level<br/>
 * <p/>
 * this object contains the information(now is path) of a target file
 * it can be used to encrypt/decrypt target file for it has the information
 * of target file
 */
public class FileLink {

    public static final double MAX_FILE_SIZE = Math.pow(2, 24);
    public static final int ONE_TIME_ENCRYPT = 1024 * 1024;
    private static String thisClass = FileLink.class.getCanonicalName();
    /**
     * Target file information
     */
    private String linkedFilePath;
    private String encryptedFilePath;
    private String password;

    /**
     * To reuse an added fileLink, can't change linkedFilePath
     * which is always means to decrypt the file
     *
     * @param saveFile the file to save target file information
     *                 which work as symbolic link
     * @param password Encryption base password
     * @throws IOException
     */
    public FileLink(File saveFile, String password) throws IOException {
        if (!saveFile.exists()) {
            throw new RuntimeException("Invalid FileLink");
        }
        initPath(saveFile);
        initNewPath(linkedFilePath);
        this.password = password;
    }

    public FileLink(File saveFile, String linkedFilePath, String password) throws IOException, NoSuchAlgorithmException {
        if (saveFile.exists()) {
            initPath(saveFile);
        } else {
            /**
             * when will file already exist:
             *  1. re-add a already added file
             *  2. reuse a added file
             */
            this.linkedFilePath = linkedFilePath;
            writeToFile(saveFile, linkedFilePath);
        }
        initNewPath(linkedFilePath);
        this.password = password;
    }

    private void initNewPath(String path) {
        // TODO: 10/11/15 consider a dir
        String[] dirNameFromPath = getDirNameFromPath(path);
        encryptedFilePath = dirNameFromPath[0] + File.separator + Default.DEFAULT_PREFIX + dirNameFromPath[1];
    }

    public void writeToFile(File file, String content) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
        outputStreamWriter.write(content);
        outputStreamWriter.flush();
        outputStreamWriter.close();
    }

    private void initPath(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        linkedFilePath = br.readLine();
        br.close();
    }

    /*
        File utility
     */
    public static String getNameFromPath(String original) {
        return original.substring(original.lastIndexOf(File.separator) + 1);
    }

    public static String getDirFromPath(String original) {
        return original.substring(0, original.lastIndexOf(File.separator));
    }

    public static String[] getDirNameFromPath(String original) {
        int lastIndexOf = original.lastIndexOf(File.separator);
        return new String[]{
                original.substring(0, lastIndexOf),
                original.substring(lastIndexOf + 1)
        };
    }


    /**
     * This function will
     * 1. encrypt all contents of the file from scratch -- TODO may too slow
     * 2. produce a encrypted version of original file in original folder
     *
     * @throws IOException
     * @throws UnrecoverableEntryException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    public boolean encrypt()
            throws IOException, UnrecoverableEntryException, InvalidKeyException, NoSuchAlgorithmException {
        // prepare cipher
        Cipher cipher;
        byte[] iv = DeriveKey.getRandomByte(Crypt.KEY_BITS);
        try {
            cipher = Cipher.getInstance(Crypt.CRYPT_ALGO + Crypt.MODE_PADDING);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            Log.e(thisClass, "wrong Cipher argument" + e);
            return false;
        }
        CryptInfo cryptInfo = DeriveKey.deriveSecretKey(getPassword());
        SecretKey secretKey = cryptInfo.getSecretKey();
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey,
                    new IvParameterSpec(iv));
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        // write to an encrypted file
        FileOutputStream outputStream = new FileOutputStream(encryptedFilePath);
        CipherOutputStream cipherOutputStream
                = new CipherOutputStream(outputStream, cipher);
        // save iv/salt in plain text in order to decrypt it
        outputStream.write(iv);
        outputStream.write(cryptInfo.getSalt());

        BufferedInputStream bufferedInputStream
                = new BufferedInputStream(new FileInputStream(linkedFilePath));
        byte[] read = new byte[ONE_TIME_ENCRYPT];
        while ((bufferedInputStream.read(read)) != -1) {
            cipherOutputStream.write(read);
        }
        cipherOutputStream.close();

        return true;
    }

    public boolean deleteOriginal() {
        // TODO: 10/11/15 consider a dir
        return new File(linkedFilePath).delete();
    }

    /**
     * Decrypt all of the content of file under original folder
     * and write it to some file
     *
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws UnrecoverableEntryException
     */
    public void decryptAll() throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, UnrecoverableEntryException, IOException {
        DecryptedFile decryptedFile = decryptPart();
        File file = new File(encryptedFilePath);
        long length = file.length();
        if (length > MAX_FILE_SIZE) {
            throw new RuntimeException("using wrong method to decrypt file");
        }
        BufferedOutputStream bufferedOutputStream =
                new BufferedOutputStream(new FileOutputStream(new File(linkedFilePath)));
        byte[] write = new byte[ONE_TIME_ENCRYPT];
        // If I don't close stream in encryption, it will have the following error because incomplete padding
        // java.io.IOException: error:06065064:digital envelope routines:EVP_DecryptFinal_ex:bad decrypt
        while ((decryptedFile.read(write)) != -1) {
            bufferedOutputStream.write(write);
        }
        decryptedFile.close();
        bufferedOutputStream.close();
    }

    /**
     * @return A decrypted file you can get byte by byte or read more depend
     * on the memory or something else
     * @throws UnrecoverableEntryException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    public DecryptedFile decryptPart()
            throws UnrecoverableEntryException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, FileNotFoundException {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(Crypt.CRYPT_ALGO + Crypt.MODE_PADDING);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
            Log.e(thisClass, "wrong crypt algorithm");
            return null;
        }
        // read iv from cipher text
        BufferedInputStream bufferedInputStream
                = new BufferedInputStream(new FileInputStream(encryptedFilePath));
        byte[] iv = new byte[cipher.getBlockSize()];
        byte[] salt = new byte[cipher.getBlockSize()];
        int readIV = 0;
        int readS = 0;
        try {
            readIV = bufferedInputStream.read(iv);
            readS = bufferedInputStream.read(salt);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (BuildConfig.DEBUG && readIV != cipher.getBlockSize()
                && readS != cipher.getBlockSize()) {
            throw new AssertionError("can't read iv from file");
        }
        SecretKey secretKey = DeriveKey.recoverSecretKey(password, salt);
        // init cipher with key and iv
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        CipherInputStream cipherInputStream = new CipherInputStream(bufferedInputStream, cipher);
        return new DecryptedFile(cipherInputStream);
    }

    public String getLinkedFilePath() {
        return linkedFilePath;
    }

    public String getPassword() {
        return password;
    }

    public boolean largeLinkedSize() {
        return new File(encryptedFilePath).length() > MAX_FILE_SIZE;
    }
}
