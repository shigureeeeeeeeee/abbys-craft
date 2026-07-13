package com.abyssworld.event;

import com.abyssworld.AbyssWorld;
import com.abyssworld.entity.AbyssSovereignEntity;
import com.abyssworld.registry.ModEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AbyssWorld.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.ABYSS_SOVEREIGN.get(), AbyssSovereignEntity.createAttributes().build());
    }
}
