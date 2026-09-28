package umonaz.omnibus.service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Service;

/**
 * UseCase Layer 를 나타내는 어노테이션
 * Service Layer 와 구분하기 위해 별도의 어노테이션으로 정의
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Service
public @interface UseCase {

    @AliasFor(annotation = Service.class)
    String value() default "";
}
