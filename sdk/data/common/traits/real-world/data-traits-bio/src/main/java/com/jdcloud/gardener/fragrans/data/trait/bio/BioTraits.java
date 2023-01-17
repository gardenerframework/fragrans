package com.jdcloud.gardener.fragrans.data.trait.bio;

import com.jdcloud.gardener.fragrans.sugar.trait.annotation.Trait;

/**
 * @author zhanghan30
 * @date 2022/8/14 2:07 下午
 */
public interface BioTraits {
    interface SignatureTraits {
        interface IdentifierTraits {
            /**
             * @author zhanghan30
             * @date 2022/8/13 7:16 下午
             */
            @Trait
            class FaceId {
                /**
                 * 人脸id
                 */
                private String faceId;
            }

            @Trait
            class FingerprintId {
                private String fingerprintId;
            }
        }
    }
}
