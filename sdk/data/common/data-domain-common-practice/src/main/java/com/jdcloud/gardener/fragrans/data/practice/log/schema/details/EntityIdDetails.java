package com.jdcloud.gardener.fragrans.data.practice.log.schema.details;

import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import com.jdcloud.gardener.fragrans.log.schema.details.Detail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * @author zhanghan30
 * @date 2022/9/23 14:14
 */
@AllArgsConstructor
@Getter
@Setter
public class EntityIdDetails<I> implements Detail, GenericTraits.IdentifierTraits.Id<I> {
    private I id;

    public EntityIdDetails(@NonNull GenericTraits.IdentifierTraits.Id<I> object) {
        this.id = object.getId();
    }
}
