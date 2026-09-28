package umonaz.omnibus.account.domain.credential;

import lombok.Builder;

/**
 * PBKDF2WithHmacSHA256 유도 키 (80바이트 출력 기준)
 * - key: 0~31번째 바이트 → AES-256-GCM 대칭키 (Base64 인코딩)
 * - hmacKey: 48~79번째 바이트 → 무결성 검증용 HmacSHA256 키 (Base64 인코딩)
 * - IV: enc_data 앞 16바이트 (복호화 시점에 enc_data에서 직접 추출)
 */
@Builder
public record NiceSymmetricSalt(String key, String hmacKey) {}
