package com.example.zzt.tagdaily.logic;

import android.os.Build;
import android.util.Log;

import com.example.zzt.tagdaily.BuildConfig;

import java.io.BufferedInputStream;
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
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by zzt on 10/6/15.
 * <p>
 * Usage:
 * <p>
 * an abstraction to imitate a symbolic link to a file <br/>
 * for Android method to do it demanding a very high api level<br/>
 * <p>
 * this object contains the information(now is path) of a target file
 * it can be used to encrypt/decrypt target file for it has the information
 * of target file
 */
public class FileLink {

    public static final double MAX_FILE_SIZE = Math.pow(2, 20);
    private String thisClass = this.getClass().getCanonicalName();
    /**
     * The file work as link
     */
    private File saveFile;
    /**
     * Target file information
     */
    private String linkedFilepath;
    private String encryptedFilePath;
    private String alias;

    public FileLink(File saveFile, String linkedFilepath) throws IOException, KeyStoreException, NoSuchAlgorithmException {
        this.saveFile = saveFile;
        /**
         * when will file already exist:
         *  1. re-add a already added file
         *  2. reuse a added file
         */
        if (saveFile.exists()) {
            initPath(saveFile);
        } else {
            this.linkedFilepath = linkedFilepath;
            writeToFile(saveFile, linkedFilepath);
        }
        initNewPath(linkedFilepath);
        // TODO: 10/7/15 change key in regular (2**48 blocks, 128bits/block for AES)
        // 10/11/15 using file path may cause the hacker reuse the same filename to
        // encrypt different file which will reuse the same private key and may be a problem
        alias = linkedFilepath;
        if (!KeyStores.hasAlias(alias)) {
            SecretKey secretKey = KeyGenerator.getInstance(Crypt.CRYPT_ALGO).generateKey();
            KeyStores.storeSecretKey(secretKey, alias);
        }
    }

    private void initNewPath(String path) {
        // TODO: 10/11/15 consider a dir
        String[] dirNameFromPath = getDirNameFromPath(path);
        encryptedFilePath = dirNameFromPath[0] + Default.DEFAULT_PREFIX + dirNameFromPath[1];
    }

    public void writeToFile(File file, String content) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
        outputStreamWriter.write(content);
        outputStreamWriter.flush();
        outputStreamWriter.close();
    }

    public FileLink(File saveFile) throws IOException {
        if (!saveFile.exists()) {
            throw new RuntimeException("Invalid FileLink");
        }
        initPath(saveFile);
        initNewPath(linkedFilepath);
    }

    private void initPath(File file) throws IOException {
        this.saveFile = file;
        BufferedReader br = new BufferedReader(new FileReader(file));
        linkedFilepath = br.readLine();
        br.close();
    }


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
     * 1. encrypt all contents of the file from scratch
     * and
     * 2. delete the original file
     *
     * @throws IOException
     * @throws UnrecoverableEntryException
     * @throws KeyStoreException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    public void encrypt()
            throws IOException, UnrecoverableEntryException, KeyStoreException, InvalidKeyException, NoSuchAlgorithmException {
        // prepare cipher
        Cipher aes;
        try {
            aes = Cipher.getInstance(Crypt.CRYPT_ALGO + Crypt.MODE_PADDING);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            Log.e(thisClass, "wrong Cipher argument" + e);
            return;
        }
        aes.init(Cipher.ENCRYPT_MODE, KeyStores.getSecretKey(alias));
        byte[] iv = aes.getIV();

        // write to an encrypted file
        FileOutputStream outputStream = new FileOutputStream(encryptedFilePath);
        CipherOutputStream cipherOutputStream
                = new CipherOutputStream(outputStream, aes);
        // save iv in order to decrypt it
        cipherOutputStream.write(iv);
        BufferedInputStream bufferedInputStream
                = new BufferedInputStream(new FileInputStream(linkedFilepath));
        int read = bufferedInputStream.read();
        while (read != -1) {
            cipherOutputStream.write(read);
            read = bufferedInputStream.read();
        }
        // delete original file
        // TODO: 10/11/15 consider a dir
//        boolean delete = saveFile.delete();
    }

    /**
     * Decrypt all of the content of file under original folder

     * @return The decrypted byte array
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws KeyStoreException
     * @throws UnrecoverableEntryException
     */
    public byte[] decryptAll() throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, IOException, KeyStoreException, UnrecoverableEntryException {
        DecryptedFile decryptedFile = decryptPart();
        File file = new File(linkedFilepath);
        long length = file.length();
        if (length > MAX_FILE_SIZE) {
            throw new RuntimeException("using wrong method to decrypt file");
        }
        int size = ((int) length);
        byte res[] = new byte[size];
        decryptedFile.read(res);
        return res;
    }

    /**
     * @return A decrypted file you can get byte by byte or read more depend
     * on the memory or something else
     * @throws IOException
     * @throws KeyStoreException
     * @throws UnrecoverableEntryException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    public DecryptedFile decryptPart() throws IOException, KeyStoreException, UnrecoverableEntryException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(Crypt.CRYPT_ALGO + Crypt.MODE_PADDING);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
            Log.e(thisClass, "wrong crypt algorithm");
            return null;
        }
        BufferedInputStream bufferedInputStream
                = new BufferedInputStream(new FileInputStream(encryptedFilePath));
        byte[] iv = new byte[cipher.getBlockSize()];
        int read = bufferedInputStream.read(iv);
        if (BuildConfig.DEBUG && read != cipher.getBlockSize()) {
            throw new AssertionError("can't read iv from file");
        }
        cipher.init(Cipher.DECRYPT_MODE, KeyStores.getSecretKey(alias), new IvParameterSpec(iv));
        // TODO: 10/11/15 test whether read iv make it not decrypt iv
        CipherInputStream cipherInputStream = new CipherInputStream(bufferedInputStream, cipher);
        return new DecryptedFile(cipherInputStream);
    }

    public String getLinkedFilepath() {
        return linkedFilepath;
    }
}