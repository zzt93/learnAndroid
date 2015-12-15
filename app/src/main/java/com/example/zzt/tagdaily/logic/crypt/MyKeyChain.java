package com.example.zzt.tagdaily.logic.crypt;

import com.facebook.crypto.exception.KeyChainException;
import com.facebook.crypto.keychain.KeyChain;

/**
 * Created by zzt on 12/15/15.
 * <p/>
 * Usage:
 */
public class MyKeyChain implements KeyChain {

    private String password;

    @Override
    public byte[] getCipherKey() throws KeyChainException {
        byte[] salt = new byte[Crypt.KEY_BYTES];
        return DeriveKey.initSecretKey(password, Crypt.KEY_BITS, Crypt.CRYPT_ALGO, salt).getEncoded();
    }

    @Override
    public byte[] getMacKey() throws KeyChainException {
        return new byte[0];
    }

    @Override
    public byte[] getNewIV() throws KeyChainException {
        return DeriveKey.getRandomByte(Crypt.KEY_BITS);
    }

    @Override
    public void destroyKeys() {

    }
}
