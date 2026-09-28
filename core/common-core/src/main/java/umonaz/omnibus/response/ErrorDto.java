package umonaz.omnibus.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class ErrorDto<T> {
    private final T message;

    private final String errorCode;

    /**
     * 대부분의 예외는 채우지 않으므로 값이 없으면 응답에서 빠짐
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Object data;

    public static <T> ErrorDto<T> of(final T message, final String errorCode) {
        return new ErrorDto<T>(message, errorCode, null);
    }

    public static <T> ErrorDto<T> of(final T message, final String errorCode, final Object data) {
        return new ErrorDto<T>(message, errorCode, data);
    }
}
