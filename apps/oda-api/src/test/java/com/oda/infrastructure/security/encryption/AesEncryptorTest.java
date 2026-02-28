package com.oda.infrastructure.security.encryption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class AesEncryptorTest {

    private AesEncryptor aesEncryptor;
    // 32 bytes base64 encoded for AES-256
    private static final String TEST_KEY = "dGVzdEtleUZvckRldmVsb3BtZW50MTIzNDU2Nzg5MDE=";

    @BeforeEach
    void setUp() {
        aesEncryptor = new AesEncryptor(TEST_KEY);
    }

    @Test
    void 암호화_복호화_정상() {
        String original = "12345678";
        String encrypted = aesEncryptor.encrypt(original);
        String decrypted = aesEncryptor.decrypt(encrypted);
        assertThat(decrypted).isEqualTo(original);
    }

    @Test
    void 암호화_결과_원문과_다름() {
        String original = "12345678";
        String encrypted = aesEncryptor.encrypt(original);
        assertThat(encrypted).isNotEqualTo(original);
    }

    @Test
    void null_입력_처리() {
        assertThat(aesEncryptor.encrypt(null)).isNull();
        assertThat(aesEncryptor.decrypt(null)).isNull();
    }

    @Test
    void 동일_입력_다른_암호문() {
        String original = "12345678";
        String encrypted1 = aesEncryptor.encrypt(original);
        String encrypted2 = aesEncryptor.encrypt(original);
        assertThat(encrypted1).isNotEqualTo(encrypted2); // Different IV each time
    }

    @Test
    void 빈_문자열_암호화_복호화() {
        String encrypted = aesEncryptor.encrypt("");
        String decrypted = aesEncryptor.decrypt(encrypted);
        assertThat(decrypted).isEqualTo("");
    }
}
