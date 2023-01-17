package com.jdcloud.gardener.fragrans.api.test.endpoints;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Negative;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * 测试一下reactive的编程方式
 */
@RequestMapping("/validate")
@RestController
public class ValidateEndpoint {


    @GetMapping("/{id}")
    public void validate(@Valid @PathVariable @Positive String id, @Valid @Negative @NotNull @RequestParam(required = false) Long depth, @Valid @NotNull Long noParam) {

    }

    @PostMapping("/{id}")
    public void validate(@Valid @PathVariable @Positive String id, @Valid @RequestBody Body body) {

    }

    @GetMapping("/param")
    public void validate(@Valid Param param) {

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

        public static class Nested {
            @Positive
            @NotNull
            Integer name;

            public Integer getName() {
                return name;
            }

            public void setName(Integer name) {
                this.name = name;
            }
        }
    }
}