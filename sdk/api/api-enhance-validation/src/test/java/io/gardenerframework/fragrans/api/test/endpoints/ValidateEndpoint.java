package io.gardenerframework.fragrans.api.test.endpoints;

import io.gardenerframework.fragrans.api.validation.ObjectMapperEnhanceValidationSupport;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Negative;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Date;
import java.util.Map;

/**
 * 测试一下reactive的编程方式
 */
@RequestMapping("/validate")
@RestController
@RequiredArgsConstructor
public class ValidateEndpoint {

    private final ObjectMapperEnhanceValidationSupport objectMapperEnhanceValidationSupport;

    @GetMapping("/{id}")
    public void validate(@Valid @PathVariable @Positive String id, @Valid @Negative @NotNull @RequestParam(required = false) Long depth, @Valid @NotNull Long noParam) {

    }

    @PostMapping("/{id}")
    public void validate(@Valid @PathVariable @Positive String id, @Valid @RequestBody Body body) {

    }

    @GetMapping("/param")
    public void validate(@Valid Param param) {

    }

    @PostMapping("/json")
    public void validate(@Valid @RequestBody Map<String, ?> param) {
        objectMapperEnhanceValidationSupport.validate(param, Param.class);
    }

    public static class Body {
        @Positive
        @NotNull
        private String idInBody;

        public String getIdInBody() {
            return idInBody;
        }

        public void setIdInBody(String idInBody) {
            this.idInBody = idInBody;
        }
    }

    public static class Param {
        @NotNull
        @Valid
        private Nested nested;

        public Nested getNested() {
            return nested;
        }

        public void setNested(Nested nested) {
            this.nested = nested;
        }

        @Getter
        @Setter
        public static class Nested {
            @Positive
            @NotNull
            Integer name;

            @NotNull
            private Date date;
        }
    }
}