package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.abstraction.entity.AbstractEntityMethods;
import com.kamikazejam.kamicommon.nms.entity.*;
import org.jetbrains.annotations.NotNull;

/**
 * !!! Gradle Compatability Requires this module to be set to Java16 !!!
 * WE ARE BUILDING FOR Java 8, do not use any Java 9+ features
 */
public class EntityMethodsProvider extends Provider<AbstractEntityMethods> {
    @Override
    protected @NotNull AbstractEntityMethods provide(int ver) {
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        if (ver == f("1.8")) {
            return new EntityMethods_1_8_R1();
        }else if (ver <= f("1.8.3")) {
            return new EntityMethods_1_8_R2();
        }else if (ver <= f("1.8.8")) {
            return new EntityMethods_1_8_R3();
        }else if (ver <= f("1.9.2")) {
            return new EntityMethods_1_9_R1();
        }else if (ver <= f("1.9.4")) {
            return new EntityMethods_1_9_R2();
        }else if (ver <= f("1.10.2")) {
            return new EntityMethods_1_10_R1();
        }else if (ver <= f("1.11.2")) {
            return new EntityMethods_1_11_R1();
        }else if (ver <= f("1.12.2")) {
            return new EntityMethods_1_12_R1();
        }else if (ver <= f("1.13")) {
            return new EntityMethods_1_13_R1();
        }else if (ver <= f("1.13.2")) {
            return new EntityMethods_1_13_R2();
        }

        // 1.14 added Bukkit api support for fetching the size
        // Every version after that no longer requires nms
        return new EntityMethods_1_14_R1();
    }
}
