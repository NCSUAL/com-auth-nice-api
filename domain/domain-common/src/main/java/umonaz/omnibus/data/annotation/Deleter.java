package umonaz.omnibus.data.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Repository;

/**
 * Deleter Layer 를 나타내는 어노테이션
 * Repository Layer 와 구분하기 위해 별도의 어노테이션으로 정의
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repository
@Primary
@Documented
public @interface Deleter {

    @AliasFor(annotation = Repository.class)
    String value() default "";
}
