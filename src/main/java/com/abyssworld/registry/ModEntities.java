package com.abyssworld.registry;

import com.abyssworld.AbyssWorld;
import com.abyssworld.entity.AbyssSovereignEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AbyssWorld.MODID);

    public static final RegistryObject<EntityType<AbyssSovereignEntity>> ABYSS_SOVEREIGN =
            ENTITIES.register("abyss_sovereign",
                    () -> EntityType.Builder.of(AbyssSovereignEntity::new, MobCategory.MONSTER)
                            .sized(1.4F, 3.8F)
                            .clientTrackingRange(10)
                            .build("abyss_sovereign"));
}
