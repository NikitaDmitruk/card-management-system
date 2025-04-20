package by.testprojects.cardmanagementsystem.service;

import by.testprojects.cardmanagementsystem.exception.CardEncryptionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CardEncryptionService {

    // Алгоритм шифрования
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128; // бит
    private static final int IV_LENGTH = 12; // байт (рекомендуется для GCM)

    // Ключ шифрования (инжектится из настроек)
    @Value("${card.encryption.key}")
    private String secretKey;

    /**
     * Шифрует номер карты с использованием AES-256-GCM.
     * Возвращает строку в формате BASE64(IV + ciphertext).
     */
    public String encrypt(String cardNumber) {
        try {
            // 1. Подготовка ключа
            byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

            // 2. Генерация IV (Initialization Vector)
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            // 3. Настройка шифра
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmParameterSpec);

            // 4. Шифрование
            byte[] encryptedBytes = cipher.doFinal(
                    cardNumber.getBytes(StandardCharsets.UTF_8)
            );

            // 5. Объединение IV + ciphertext
            byte[] combined = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new CardEncryptionException("Failed to encrypt card number", e);
        }
    }

    /**
     * Расшифровывает данные карты.
     */
    public String decrypt(String encryptedData) {
        try {
            // 1. Декодирование BASE64
            byte[] combined = Base64.getDecoder().decode(encryptedData);

            // 2. Извлечение IV (первые IV_LENGTH байт)
            byte[] iv = new byte[IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, iv.length);

            // 3. Извлечение ciphertext (остальные байты)
            byte[] ciphertext = new byte[combined.length - IV_LENGTH];
            System.arraycopy(combined, iv.length, ciphertext, 0, ciphertext.length);

            // 4. Подготовка ключа
            byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

            // 5. Настройка шифра
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec);

            // 6. Расшифровка
            byte[] decryptedBytes = cipher.doFinal(ciphertext);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new CardEncryptionException("Failed to decrypt card data",e);
        }
    }

    /**
     * Маскирует номер карты, оставляя первые 6 и последние 4 цифры.
     * Пример: "4242424242424242" → "424242******4242"
     */
    public String mask(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 10) {
            return cardNumber;
        }
        String prefix = cardNumber.substring(0, 6);
        String suffix = cardNumber.substring(cardNumber.length() - 4);
        return prefix + "******" + suffix;
    }
}
