package umonaz.omnibus.auth.domain.nice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import umonaz.omnibus.auth.domain.AuthCommand;

/**
 * 나이스 본인인증 API-> 검증된 데이터
 *
 *  # SETTER 사용 X
 *  # Gateway에서는 조회용으로만 사용
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NiceVerifiedUser(

        /**
         * 응답고유번호
         */
        @JsonProperty("requestno") String requestno,

        /**
         * 암호화 일시(YYYYMMDDHH24MISS)
         */
        @JsonProperty("enctime") String encTime,

        /**
         * 사이트코드
         */
        @JsonProperty("sitecode") String siteCode,

        /**
         * 응답고유번호
         */
        @JsonProperty("responseno") String responseNo,

        /**
         * 인증수단
         */
        @JsonProperty("authtype") String authType,

        /**
         * 이름
         */
        String name,

        /**
         * UTF8로 URLEncoding된 이름 값
         */
        @JsonProperty("utf8_name") String utf8Name,

        /**
         * 생년월일 8자리
         */
        @JsonProperty("birthdate") String birthDate,

        /**
         * 성별
         */
        String gender,

        /**
         * 내외국인
         */
        @JsonProperty("national_info") String nationalInfo,

        /**
         * 이통사 구분(휴대폰 인증 시)
         */
        @JsonProperty("mobile_co") String mobileCo,

        /**
         * 휴대폰 번호(휴대폰 인증 시)
         */
        @JsonProperty("mobile_no") String mobileNo,

        /**
         * 개인 식별 코드(CI)
         */
        String ci,

        /**
         * 중복가입 확인정보(DI)
         */
        String di,

        /**
         * 추가 데이터
         */
        @JsonProperty("receivedata") String receiveData,

        /**
         * 결과 상태
         */
        @JsonProperty("resultcode") String resultCode)
        implements AuthCommand {

    public NiceVerifiedUser {

        if (gender.equals("1")) {
            gender = "M";
        }

        if (gender.equals("2")) {
            gender = "F";
        }
    }
}
