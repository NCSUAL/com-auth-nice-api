package umonaz.omnibus.account.domain.credential;

import lombok.Builder;
import umonaz.omnibus.client.response.NiceAuthUrlResponse;
import umonaz.omnibus.client.response.NiceTokenResponse;

/**
 * PBKDF2 대칭키 생성에 필요한 입력값
 * - ticket: NICE 접근토큰 API 응답값 (PBKDF2 password)
 * - transactionId: NICE 인증URL API 응답값 (PBKDF2 salt)
 * - iterators: NICE 접근토큰 API 응답값 (PBKDF2 반복횟수)
 */
@Builder
public record NiceKeyMaterial(String ticket, String transactionId, int iterators) {

    public static NiceKeyMaterial of(NiceTokenResponse tokenResponse, NiceAuthUrlResponse authUrlResponse) {
        return NiceKeyMaterial.builder()
                .ticket(tokenResponse.ticket())
                .transactionId(authUrlResponse.transactionId())
                .iterators(tokenResponse.iterators())
                .build();
    }
}
