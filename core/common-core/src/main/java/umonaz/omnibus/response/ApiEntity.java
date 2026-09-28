package umonaz.omnibus.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import umonaz.omnibus.serializer.OptionalToEmptyListSerializer;

@Getter
@RequiredArgsConstructor
public final class ApiEntity<T> {

    @JsonSerialize(using = OptionalToEmptyListSerializer.class)
    private final Optional<T> data;

    private final boolean success;

    @JsonSerialize(using = OptionalToEmptyListSerializer.class)
    private final Optional<ErrorDto<T>> error;

    public static <T> ApiEntity<T> ok(T data) {
        return new ApiEntity<T>(Optional.of(data), true, Optional.empty());
    }

    public static ApiEntity<Void> ok() {
        return new ApiEntity<>(Optional.empty(), true, Optional.empty());
    }

    public static <T> ApiEntity<T> fail(ErrorDto<T> errorDto) {
        return new ApiEntity<>(Optional.empty(), false, Optional.of(errorDto));
    }
}
