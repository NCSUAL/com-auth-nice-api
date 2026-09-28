package umonaz.omnibus.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import umonaz.omnibus.exception.BusinessException;
import umonaz.omnibus.response.ApiEntity;
import umonaz.omnibus.response.ErrorDto;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvice {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiEntity<?>> handleBusinessException(BusinessException ex) {
        log.warn("[BusinessException] code={} message={}", ex.getErrorCode().code(), ex.getMessage());
        return ResponseEntity.status(ex.getErrorCode().httpStatus())
                .body(ApiEntity.fail(
                        ErrorDto.of(ex.getMessage(), ex.getErrorCode().code())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiEntity<?>> exception(Exception ex) {
        log.error("[UnhandledException] {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiEntity.fail(ErrorDto.of("오류가 발생했습니다. 잠시 후 다시 시도해주세요.", "INTERNAL_SERVER_ERROR")));
    }
}
