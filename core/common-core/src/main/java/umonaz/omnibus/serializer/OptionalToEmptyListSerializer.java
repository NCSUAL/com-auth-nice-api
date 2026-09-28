package umonaz.omnibus.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 *  커스텀 직렬화 클래스 정의
 *  공식 문서: https://docs.oracle.com/javame/8.0/api/json/api/com/oracle/json/stream/JsonGenerator.html
 * Writes JSON data to an output source in a streaming way. The class Json contains methods to create generators for character or output streams (Writer and OutputStream).
 * The following example shows how to create a JSON generator:
 *
 *
 * JsonGenerator generator = Json.createGenerator(...);
 *
 *
 * The class JsonGeneratorFactory also contains methods to create JsonGenerator instances. JsonGeneratorFactory should be used when creating multiple generator instances, as in the following example:
 *
 *
 * JsonGeneratorFactory factory = Json.createGeneratorFactory();
 * JsonGenerator generator1 = factory.createGenerator(...);
 * JsonGenerator generator2 = factory.createGenerator(...);
 *
 *
 * JSON objects can be created using JsonGenerator by calling the writeStartObject() method and then adding name/value pairs with the write method.
 *
 * The following example shows how to generate an empty JSON object:
 *
 *
 * JsonGenerator generator = ...;
 * generator.writeStartObject().writeEnd().close();
 *
 *
 * JSON arrays can be created using JsonGenerator by calling the writeStartArray() method and then adding values with the write method.
 * The following example shows how to generate an empty JSON array:
 */
public class OptionalToEmptyListSerializer extends JsonSerializer<Optional<?>> {

    @Override
    public void serialize(Optional<?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (Objects.isNull(value) || value.isEmpty()) {
            gen.writeStartArray();
            gen.writeEndArray();
        } else {
            serializers.defaultSerializeValue(value.get(), gen);
        }
    }
}
