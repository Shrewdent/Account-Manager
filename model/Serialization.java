package model;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import javafx.collections.ObservableList;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Serialization {

    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String SECRET_KEY = "YourSecretKey123"; // Change this to your secret key

    public static void main(String[] args) {

        // Load the decrypted accounts from the file
        List<Account> loadedAccounts = loadDecryptedAccountsFromFile("accounts.ser");

        // Display the loaded accounts
        for (Account account : loadedAccounts) {
            System.out.println("Username: " + account.getAccountName() + ", Password: " + account.getPassword());
        }
    }

    private static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        return new SecretKeySpec(SECRET_KEY.getBytes(), ENCRYPTION_ALGORITHM);
    }

    private static byte[] encryptData(byte[] data, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    private static byte[] decryptData(byte[] encryptedData, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(encryptedData);
    }

    public static void saveEncryptedAccountsToFile(String fileName, ObservableList<Account> accounts) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName))) {
            List<Account> serializableList = new ArrayList<>(accounts);
            outputStream.writeObject(serializableList);
            System.out.println("Accounts saved to file: " + fileName);
        } catch (IOException e) {
            System.out.println("Error saving accounts to file: " + e.getMessage());
        }
    }

    public static List<Account> loadDecryptedAccountsFromFile(String fileName) {
        List<Account> accounts = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            SecretKey secretKey = generateSecretKey();
            CipherInputStream cis = new CipherInputStream(new FileInputStream(fileName), generateCipher(secretKey, Cipher.DECRYPT_MODE));
            accounts = (List<Account>) ois.readObject();
            cis.close();
        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    private static Cipher generateCipher(SecretKey secretKey, int cipherMode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(cipherMode, secretKey);
        return cipher;
    }
}