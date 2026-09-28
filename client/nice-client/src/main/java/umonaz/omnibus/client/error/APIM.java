package umonaz.omnibus.client.error;

import lombok.Getter;

@Getter
public enum APIM {
    NO_ERROR(1200, "오류 없음 (정상)"),
    REQUEST_BODY_IS_NULL(1300, "request body가 비었습니다."),
    BAD_REQUEST(1400, "잘못된 요청"),
    AUTHORIZED_REQUIRED(1401, "인증 필요"),
    UNAUTHORIZED(1402, "권한없음"),
    SERVICE_DISABLED(1403, "서비스 사용 중지됨"),
    SERVICE_NOT_FOUND(1404, "서비스를 찾을 수 없음"),
    INTERNAL_SERVER_ERROR(1500, "서버 내부 오류"),
    ACCESS_DENIED_BY_PROTECTED_SERVICE(1501, "보호된 서비스에서 엑세스가 거부되었습니다."),
    BAD_RESPONSE_FROM_PROTECTED_SERVICE(1502, "보호된 서비스에서 응답이 잘못되었습니다."),
    SERVICE_TEMPORARILY_UNAVAILABLE(1503, "일시적으로 사용할 수 없는 서비스"),
    ACCESS_NOT_ALLOWED_CLIENT_ID(1700, "엑세스가 허용되지 않습니다. - Client ID"),
    ACCESS_NOT_ALLOWED_SERVICE_URI(1701, "엑세스가 허용되지 않습니다. - Service URI"),
    ACCESS_NOT_ALLOWED_CLIENT_ID_CLIENT_IP(1702, "엑세스가 허용되지 않습니다. - Client ID + Client IP"),
    ACCESS_NOT_ALLOWED_CLIENT_ID_SERVICE_URI(1703, "엑세스가 허용되지 않습니다. - Client ID + Service URI"),
    ACCESS_NOT_ALLOWED_CLIENT_ID_BLACK_LIST_CLIENT_IP(1705, "엑세스가 허용되지 않습니다. - Client ID + Black List Client IP"),
    ACCESS_NOT_ALLOWED_CLIENT_ID_PRODUCT_CODE(1706, "액세스가 허용되지 않습니다 - Client ID + Product Code"),
    TRANSACTION_RESTRICTION_WEEK(1711, "거래제한된 요일입니다."),
    TRANSACTION_RESTRICTION_TIME(1712, "거래제한된 시간입니다."),
    TRANSACTION_RESTRICTION_WEEK_TIME(1713, "거래제한된 요일/시간입니다."),
    TRANSACTION_RESTRICTION_DATE(1714, "거래제한된 일자입니다."),
    TRANSACTION_RESTRICTION_DATE_TIME(1715, "거래제한된 일자/시간입니다."),
    TRANSACTION_RESTRICTION_HOLIDAY(1716, "공휴일 거래가 제한된 서비스입니다."),
    SQL_INJECTION_ERROR(1717, "SQL인젝션, XSS방어"),
    INVALID_TOKEN(1800, "잘못된 토큰"),
    INVALID_CLIENT_INFO(1801, "잘못된 클라이언트 정보"),
    EXCEEDED_CONNECTIONS(1900, "초과된 연결 횟수"),
    EXCEEDED_GETTOKEN_FAILED_COUNT(1901, "초과 된 토큰 조회 실패"),
    EXCEEDED_CHECKTOKEN_FAILED_COUNT(1902, "초과된 토큰 체크 실패"),
    EXCEEDED_USER_CONNECTIONS(1903, "초과된 접속자 수 "),
    EXCEEDED_CONTENT_LENGTH(1904, "전송 크기 초과"),
    OVER_CONNECTIONS(1905, "접속량이 너무 많음"),
    EXCEEDED_PRODUCT_USE_LIMIT(1906, "상품이용 한도 초과"),
    EXCEEDED_API_USE_TIME_LIMIT(1907, "API 이용 주기 초과"),
    EXCEEDED_PRODUCT_USE_TIME_LIMIT(1908, "상품 이용 주기 초과");

    // 응답 코드
    private final Integer gwRSLTCD;

    // 응답 메세지
    private final String gwRSLTMSG;

    APIM(Integer gwRSLTCD, String gwRSLTMSG) {
        this.gwRSLTCD = gwRSLTCD;
        this.gwRSLTMSG = gwRSLTMSG;
    }
}
