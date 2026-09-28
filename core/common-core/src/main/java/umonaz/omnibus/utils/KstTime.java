package umonaz.omnibus.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * 응답에 적는 시각의 기준
 *
 */
public final class KstTime {

    public static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    /**
     * 오프셋까지 적는 ISO 8601 형식(예: {@code 2026-09-01T01:23:51+09:00}).
     */
    private static final DateTimeFormatter ISO_OFFSET =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX").withZone(ZONE);

    private KstTime() {}

    /** 시각을 KST ISO 8601 문자열로. 값이 없으면 {@code null} 이다. */
    public static String iso(final Timestamp timestamp) {
        return Objects.isNull(timestamp) ? null : ISO_OFFSET.format(timestamp.toInstant());
    }

    /**
     * KST 벽시계로 적어 둔 시각을 같은 형식의 문자열로 표현
     */
    public static String iso(final LocalDateTime dateTime) {
        return Objects.isNull(dateTime) ? null : ISO_OFFSET.format(dateTime.atZone(ZONE));
    }
}
