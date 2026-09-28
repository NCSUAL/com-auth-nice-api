package umonaz.omnibus.account.domain.model.symmetric;

import lombok.Builder;

/**
 * Redis 저장 컨텍스트 (TTL 600초)
 * - ticket: PBKDF2 password 역할
 * - iterators: PBKDF2 반복횟수
 * - requestNo: 세션 위변조 검증용 / Redis 키
 * - accessToken: 인증결과 API 호출용
 * - niceTransactionId: NICE가 발급한 transaction_id (인증결과 API 요청 및 PBKDF2 salt)
 */
@Builder
public record NiceSymmetricContext(
        String ticket, int iterators, String requestNo, String accessToken, String niceTransactionId) {

    public static NiceSymmetricContext of(
            String ticket, int iterators, String requestNo, String accessToken, String niceTransactionId) {
        return NiceSymmetricContext.builder()
                .ticket(ticket)
                .iterators(iterators)
                .requestNo(requestNo)
                .accessToken(accessToken)
                .niceTransactionId(niceTransactionId)
                .build();
    }
}
