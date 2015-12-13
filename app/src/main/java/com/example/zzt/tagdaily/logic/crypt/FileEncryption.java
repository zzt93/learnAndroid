package com.example.zzt.tagdaily.logic.crypt;

import android.util.Log;

import com.example.zzt.tagdaily.BuildConfig;
import com.example.zzt.tagdaily.logic.fileChooser.FileChooserBL;
import com.example.zzt.tagdaily.logic.mis.Default;
import com.example.zzt.tagdaily.logic.mis.FileUtility;

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
public class FileEncryption {

    public static final double MAX_FILE_SIZE = Math.pow(2, 24);
    public static final int ONE_TIME_ENCRYPT = 1024 * 1024;
    private static String thisClass = FileEncryption.class.getCanonicalName();
    private File saveFile;
    /**
     * Target file information which stored in the @see FileEncryption#saveFile
     */
    private String linkedFilePath;
    private String fileUri;

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
    public FileEncryption(File saveFile, String password) throws IOException {
        if (!saveFile.exists()) {
            throw new RuntimeException("Invalid FileEncryption");
        }
        this.saveFile = saveFile;
        initPath(saveFile);
        initEncryptedPath(linkedFilePath);
        this.password = password;
    }

    public FileEncryption(File saveFile, String linkedFilePath, String fileUri, String password) throws IOException, NoSuchAlgorithmException {
        if (saveFile.exists()) {
            throw new RuntimeException("Invalid FileEncryption");
        } else {
            this.saveFile = saveFile;
            this.linkedFilePath = linkedFilePath;
            this.fileUri = fileUri;
            writeToFile(saveFile, linkedFilePath, fileUri);
        }
        initEncryptedPath(linkedFilePath);
        this.password = password;
    }

    /**
     * This method decide the path which is used to store encrypted file
     *
     * @param path The path of file which user want to hide
     * @see FileChooserBL#encryptedFileDir()
     */
    private void initEncryptedPath(String path) {
        // TODO: 10/11/15 consider a dir
        String[] dirNameFromPath = FileUtility.getDirNameFromPath(path);
        encryptedFilePath = dirNameFromPath[0] + File.separator + Default.DEFAULT_PREFIX + dirNameFromPath[1];
    }

    public void writeToFile(File file, String content, String fileUri) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
        outputStreamWriter.write(content);
        outputStreamWriter.write("\n");
        outputStreamWriter.write(fileUri);
        outputStreamWriter.flush();
        outputStreamWriter.close();
    }

    private void initPath(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        linkedFilePath = br.readLine();
        fileUri = br.readLine();
        br.close();
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
     * @param checkSize The flag to indicate whether to check the size of file to be encrypted
     */
    public void decryptAll(boolean checkSize) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, UnrecoverableEntryException, IOException {
        if (decryptedFileExist()) {
            return;
        }
        DecryptedFile decryptedFile = decryptPart();
        File file = new File(encryptedFilePath);
        long length = file.length();
        if (checkSize && length > MAX_FILE_SIZE) {
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

    private boolean decryptedFileExist() {
        return new File(linkedFilePath).exists();
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

    public String getFileUri() {
        return fileUri;
    }

    public String getEncryptedFilePath() {
        return encryptedFilePath;
    }

    public String getPassword() {
        return password;
    }

    public boolean largeLinkedSize() {
        return new File(encryptedFilePath).length() > MAX_FILE_SIZE;
    }

    public boolean deleteEncrypted() {
        return new File(encryptedFilePath).delete();
    }

    public boolean deleteSaveFile() {
        return saveFile.delete();
    }


}
