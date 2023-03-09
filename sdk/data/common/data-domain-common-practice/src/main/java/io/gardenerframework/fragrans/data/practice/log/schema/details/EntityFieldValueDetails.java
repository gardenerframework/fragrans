package io.gardenerframework.fragrans.data.practice.log.schema.details;

import io.gardenerframework.fragrans.data.trait.generic.GenericTraits;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * @author zhanghan30
 * @date 2022/9/26 06:35
 */
@Getter
@Setter
public class EntityFieldValueDetails<T, V> extends EntityFieldDetails<T> {
    private V value;

    public EntityFieldValueDetails(T id, String field, V value) {
        super(id, field);
        this.value = value;
    }

    public EntityFieldValueDetails(@NonNull GenericTraits.IdentifierTraits.Id<T> object, String field, V value) {
        super(object, field);
        this.value = value;
    }
}
