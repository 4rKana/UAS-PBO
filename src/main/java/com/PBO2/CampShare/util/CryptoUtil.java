package com.PBO2.CampShare.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utility untuk enkripsi/dekripsi isi pesan chat ("encryption at rest").
 *
 * PENTING — batasan model ini, supaya tidak disalahpahami sebagai E2EE:
 * - Enkripsi/dekripsi dilakukan di BACKEND (server), bukan di browser.
 * - Server tetap MEMEGANG kunci dan MAMPU membaca isi pesan kapan saja
 *   (saat memproses request, sebelum dikirim ke client).
 * - Tujuan enkripsi ini adalah melindungi data SAAT DIAM (at rest) di database —
 *   misal kalau file database/backup bocor atau dicuri, isi kolom "message"
 *   tidak langsung terbaca sebagai teks biasa.
 * - Ini BUKAN proteksi terhadap admin/developer yang punya akses ke server
 *   dan kuncinya, dan BUKAN proteksi kalau server itu sendiri diserang/disusupi
 *   saat sedang berjalan.
 *
 * Algoritma: AES-256-GCM (otentikasi + enkripsi sekaligus, standar modern).
 * Setiap pesan punya IV (nonce) acak sendiri, disimpan bersama ciphertext
 * dalam satu string base64, format: base64(IV || ciphertext || authTag).
 */
@Component
public class CryptoUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_LENGTH_BYTES = 12;   // 96-bit IV, standar rekomendasi untuk GCM
    private static final int TAG_LENGTH_BITS = 128;   // panjang auth tag GCM

    private final SecretKeySpec secretKey;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Kunci diambil dari properti aplikasi (lihat application.properties),
     * yang sebaiknya bersumber dari environment variable, BUKAN ditulis
     * langsung/hardcode di kode atau di-commit ke Git.
     *
     * Nilai chat.encryption.secret harus berupa passphrase apa saja yang kamu mau;
     * kelas ini akan men-derive-nya menjadi kunci AES-256 yang valid lewat PBKDF2,
     * jadi kamu TIDAK perlu menyediakan key sepanjang 32 byte secara manual.
     */
    public CryptoUtil(@Value("${chat.encryption.secret}") String secret) {
        this.secretKey = deriveKey(secret);
    }

    private SecretKeySpec deriveKey(String secret) {
        try {
            // Salt statis cukup di sini karena tujuannya hanya men-derive 1 kunci
            // tetap dari passphrase config, bukan untuk hashing password user.
            byte[] salt = "CampShareChatSaltV1".getBytes(StandardCharsets.UTF_8);
            PBEKeySpec spec = new PBEKeySpec(secret.toCharArray(), salt, 65536, 256);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            return new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            throw new IllegalStateException("Gagal menyiapkan kunci enkripsi chat", e);
        }
    }

    /** Mengenkripsi plaintext, hasil berupa string base64 siap disimpan ke DB. */
    public String encrypt(String plainText) {
        if (plainText == null) return null;
        try {
            byte[] iv = new byte[IV_LENGTH_BYTES];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BITS, iv));

            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // Gabungkan IV + ciphertext supaya bisa disimpan dalam satu kolom
            byte[] combined = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new IllegalStateException("Gagal mengenkripsi pesan", e);
        }
    }

    /** Mendekripsi string base64 (hasil dari encrypt()) kembali ke plaintext. */
    public String decrypt(String encryptedBase64) {
        if (encryptedBase64 == null) return null;
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedBase64);

            byte[] iv = new byte[IV_LENGTH_BYTES];
            byte[] cipherText = new byte[combined.length - IV_LENGTH_BYTES];
            System.arraycopy(combined, 0, iv, 0, IV_LENGTH_BYTES);
            System.arraycopy(combined, IV_LENGTH_BYTES, cipherText, 0, cipherText.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BITS, iv));

            byte[] plainBytes = cipher.doFinal(cipherText);
            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Penting: jangan biarkan satu pesan korup/lama-format-beda bikin seluruh
            // percakapan gagal dimuat. Kembalikan placeholder yang jelas alih-alih
            // melempar exception ke atas.
            return "[Pesan tidak dapat didekripsi]";
        }
    }
}
