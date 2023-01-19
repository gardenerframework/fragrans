package io.gardenerframework.fragrans.api.options.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.gardenerframework.fragrans.api.options.schema.ApiOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

/**
 * @author zhanghan30
 * @date 2022/1/3 8:31 下午
 */
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiOption(readonly = false)
public class SimplePlainOption {
    @NotBlank
    private String stringField;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String readOnly;
}
