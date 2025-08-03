package cn.nefu.ccec.graduaterecommendationevaluation.component;

import cn.nefu.ccec.graduaterecommendationevaluation.exception.XException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ObjectMapperComponent {
    private final ObjectMapper objectMapper;

    public String writeValueAsString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw XException.builder().codeN(500).message(e.getMessage()).build();
        }
    }
    public Object readValue(String json, TypeReference<?> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw XException.builder().codeN(500).message(e.getMessage()).build();
        }
    }
}
