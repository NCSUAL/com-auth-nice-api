package umonaz.omnibus.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    public String code();

    public String message();

    public HttpStatus httpStatus();
}
