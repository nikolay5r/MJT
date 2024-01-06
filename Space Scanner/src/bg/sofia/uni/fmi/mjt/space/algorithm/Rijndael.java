package bg.sofia.uni.fmi.mjt.space.algorithm;

import bg.sofia.uni.fmi.mjt.space.exception.CipherException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Rijndael implements SymmetricBlockCipher {
    private SecretKey secretKey;
    private Cipher cipher;
    private static final String ENCRYPTION_ALGORITHM = "AES";

    public Rijndael(SecretKey secretKey) {
        this.secretKey = secretKey;

    }

    @Override
    public void encrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {
        try {
            cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            try (var cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
                byte[] buffer = inputStream.readAllBytes();

                for (int bytesRead: buffer) {
                    cipherOutputStream.write(bytesRead);
                    cipherOutputStream.flush();
                }
            }
        } catch (IOException |
                NoSuchAlgorithmException |
                NoSuchPaddingException |
                InvalidKeyException e) {
            throw new CipherException("Something went wrong with encrypting.", e);
        }
    }

    @Override
    public void decrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {
        try {
            cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            try (var cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
                byte[] buffer = inputStream.readAllBytes();

                for (int bytesRead: buffer) {
                    cipherOutputStream.write(bytesRead);
                    cipherOutputStream.flush();
                }
            }
        } catch (IOException |
                 NoSuchAlgorithmException |
                 NoSuchPaddingException |
                 InvalidKeyException e) {
            throw new CipherException("Something went wrong with decrypting.", e);
        }
    }
}
