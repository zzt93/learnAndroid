package com.example.zzt.tagdaily.logic;

import android.util.Log;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;

import javax.crypto.SecretKey;

/**
 * Created by zzt on 10/11/15.
 * <p>
 * Usage: Store the private keys of encryption
 */
public class KeyStores {
    private static String thisClass = "my keyStore";

    static {

        try {
            ks = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

    private static KeyStore ks;

    public static void storeSecretKey(SecretKey key, String alias) throws KeyStoreException {
        if (ks.containsAlias(alias)) {
            String detailMessage = "duplicate alias";
            Log.e(thisClass, detailMessage);
            throw new RuntimeException(detailMessage);
        }
        KeyStore.SecretKeyEntry keyEntry = new KeyStore.SecretKeyEntry(key);
        ks.setEntry(alias, keyEntry, null);
    }

    public static SecretKey getSecretKey(String alias) throws KeyStoreException, UnrecoverableEntryException, NoSuchAlgorithmException {
        if (!ks.containsAlias(alias)) {
            throw new RuntimeException("no such alias");
        }
        KeyStore.Entry entry = ks.getEntry(alias, null);
        if (!(entry instanceof KeyStore.SecretKeyEntry)) {
            Log.e(thisClass, "Not an instance of a PrivateKeyEntry");
            return null;
        }
        return ((KeyStore.SecretKeyEntry) entry).getSecretKey();
    }


    public static void deleteSecretKey(String alias) throws KeyStoreException {
        if (!ks.containsAlias(alias)) {
            throw new RuntimeException("no such alias");
        }
        ks.deleteEntry(alias);
    }
}
