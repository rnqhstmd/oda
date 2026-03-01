package com.oda.infrastructure.security.encryption;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Converter
@RequiredArgsConstructor
public class EncryptedLongConverter implements AttributeConverter<Long, String> {

    private final AesEncryptor aesEncryptor;

    @Override
    public String convertToDatabaseColumn(Long attribute) {
        if (attribute == null) return null;
        return aesEncryptor.encrypt(String.valueOf(attribute));
    }

    @Override
    public Long convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        String decrypted = aesEncryptor.decrypt(dbData);
        return Long.parseLong(decrypted);
    }
}
