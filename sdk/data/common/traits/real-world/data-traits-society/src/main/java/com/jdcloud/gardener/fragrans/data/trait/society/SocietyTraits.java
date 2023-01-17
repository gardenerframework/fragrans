package com.jdcloud.gardener.fragrans.data.trait.society;

import com.jdcloud.gardener.fragrans.sugar.trait.annotation.Trait;

/**
 * @author zhanghan30
 * @date 2023/1/5 17:04
 */
public interface SocietyTraits {
    interface AdministrativeTraits {
        interface GroupingTraits {
            @Trait
            class Country {
                /**
                 * 国家
                 */
                private String country;
            }

            @Trait
            class Province {
                /**
                 * 省份
                 */
                private String province;
            }

            @Trait
            class City {
                /**
                 * 城市
                 */
                private String city;
            }
        }
    }
}
