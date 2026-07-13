package com.abyssworld.registry;

import com.abyssworld.AbyssWorld;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AbyssWorld.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.abyssworld"))
                    .icon(() -> new ItemStack(ModItems.ABYSS_GOD_CORE.get()))
                    .displayItems((params, output) -> {
                        // 深淵世界への鍵
                        output.accept(ModItems.ABYSS_KEY.get());
                        output.accept(ModItems.ABYSS_GOD_CATALYST.get());
                        // 基礎素材と圧縮チェーン
                        output.accept(ModItems.RAW_ABYSS_IRON.get());
                        output.accept(ModItems.ABYSS_IRON_INGOT.get());
                        output.accept(ModItems.ABYSS_IRON_BLOCK_ITEM.get());
                        output.accept(ModItems.COMPRESSED_ABYSS_IRON.get());
                        output.accept(ModItems.HIGH_DENSITY_ABYSS_ALLOY.get());
                        output.accept(ModItems.ABYSS_CRYSTAL.get());
                        output.accept(ModItems.COMPRESSED_ABYSS_CRYSTAL.get());
                        output.accept(ModItems.ABYSS_IRON_ORE_ITEM.get());
                        output.accept(ModItems.ABYSS_CRYSTAL_ORE_ITEM.get());
                        output.accept(ModItems.PRIMORDIAL_BLOOM_ITEM.get());
                        output.accept(ModItems.ASH_VEIN_ITEM.get());
                        output.accept(ModItems.FROZEN_CLUSTER_ITEM.get());
                        output.accept(ModItems.FLESH_DEPOSIT_ITEM.get());
                        output.accept(ModItems.VOID_CRYSTAL_ITEM.get());
                        // 忘却の森
                        output.accept(ModItems.PRIMORDIAL_SAP.get());
                        output.accept(ModItems.AWAKENED_VINE.get());
                        output.accept(ModItems.PERFECT_LIFE_CORE.get());
                        output.accept(ModItems.ROTTEN_FOREST_CORE.get());
                        // 灰の荒野
                        output.accept(ModItems.ETERNAL_FLAME.get());
                        output.accept(ModItems.SUPERHEATED_CORE.get());
                        output.accept(ModItems.ASH_KING_METAL.get());
                        output.accept(ModItems.ETERNAL_FURNACE_CORE.get());
                        // 蒼氷洞窟
                        output.accept(ModItems.UNMELTING_ICE_CRYSTAL.get());
                        output.accept(ModItems.FROZEN_TIME_SHARD.get());
                        output.accept(ModItems.PERMAFROST_CORE.get());
                        // 肉体鉱山
                        output.accept(ModItems.PRIMORDIAL_NERVE.get());
                        output.accept(ModItems.UNDYING_CELL.get());
                        output.accept(ModItems.WORLD_PULSE_FLUID.get());
                        output.accept(ModItems.PRIMORDIAL_NERVE_BUNDLE.get());
                        // 虚無の都
                        output.accept(ModItems.SPATIAL_ANCHOR_CRYSTAL.get());
                        output.accept(ModItems.VOID_STABILIZER.get());
                        output.accept(ModItems.WORLD_LAW_FRAGMENT.get());
                        // 最終素材・設備・究極アイテム
                        output.accept(ModItems.ABYSS_GOD_CORE.get());
                        output.accept(ModItems.FIVE_LAYER_UNIFIED_CORE.get());
                        output.accept(ModItems.WORLD_RECONSTRUCTION_FURNACE_ITEM.get());
                        output.accept(ModItems.PRIMORDIAL_RELIC.get());
                    })
                    .build());
}
