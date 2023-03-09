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
public class EntityFieldDetails<T> extends EntityIdDetails<T> {
    private String field;

    public EntityFieldDetails(T id, String field) {
        super(id);
        this.field = field;
    }

    public EntityFieldDetails(GenericTraits.IdentifierTraits.@NonNull Id<T> object, String field) {
        super(object);
        this.field = field;
    }
}
