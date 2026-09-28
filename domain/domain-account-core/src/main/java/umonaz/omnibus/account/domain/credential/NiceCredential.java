package umonaz.omnibus.account.domain.credential;

import lombok.Builder;

/**
 * 본인인증 인증 폼 모델
 * - transactionId: Redis 조회키 (NICE 인증URL API 응답)
 * - authUrl: 사용자를 리다이렉트할 NICE 인증 URL
 */
@Builder
public record NiceCredential(String transactionId, String authUrl) {}
