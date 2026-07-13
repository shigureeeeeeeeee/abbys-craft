package com.abyssworld.registry;

import com.abyssworld.AbyssWorld;
import com.abyssworld.item.AbyssGodCatalystItem;
import com.abyssworld.item.AbyssKeyItem;
import com.abyssworld.item.PrimordialRelicItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AbyssWorld.MODID);

    // ---- 基礎素材 ----
    public static final RegistryObject<Item> RAW_ABYSS_IRON =
            simple("raw_abyss_iron", Rarity.COMMON);
    public static final RegistryObject<Item> ABYSS_IRON_INGOT =
            simple("abyss_iron_ingot", Rarity.COMMON);
    public static final RegistryObject<Item> ABYSS_CRYSTAL =
            simple("abyss_crystal", Rarity.UNCOMMON);
    public static final RegistryObject<Item> COMPRESSED_ABYSS_CRYSTAL =
            simple("compressed_abyss_crystal", Rarity.RARE);
    public static final RegistryObject<Item> COMPRESSED_ABYSS_IRON =
            simple("compressed_abyss_iron", Rarity.UNCOMMON);
    public static final RegistryObject<Item> HIGH_DENSITY_ABYSS_ALLOY =
            simple("high_density_abyss_alloy", Rarity.RARE);

    // ---- 忘却の森 ----
    public static final RegistryObject<Item> PRIMORDIAL_SAP =
            simple("primordial_sap", Rarity.UNCOMMON);
    public static final RegistryObject<Item> AWAKENED_VINE =
            simple("awakened_vine", Rarity.UNCOMMON);
    public static final RegistryObject<Item> PERFECT_LIFE_CORE =
            simple("perfect_life_core", Rarity.RARE);
    public static final RegistryObject<Item> ROTTEN_FOREST_CORE =
            simple("rotten_forest_core", Rarity.EPIC);

    // ---- 灰の荒野 ----
    public static final RegistryObject<Item> ETERNAL_FLAME = ITEMS.register("eternal_flame",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).fireResistant()));
    public static final RegistryObject<Item> SUPERHEATED_CORE = ITEMS.register("superheated_core",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE).fireResistant()));
    public static final RegistryObject<Item> ASH_KING_METAL = ITEMS.register("ash_king_metal",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).fireResistant()));
    public static final RegistryObject<Item> ETERNAL_FURNACE_CORE = ITEMS.register("eternal_furnace_core",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC).fireResistant()));

    // ---- 蒼氷洞窟 ----
    public static final RegistryObject<Item> UNMELTING_ICE_CRYSTAL =
            simple("unmelting_ice_crystal", Rarity.RARE);
    public static final RegistryObject<Item> FROZEN_TIME_SHARD =
            simple("frozen_time_shard", Rarity.RARE);
    public static final RegistryObject<Item> PERMAFROST_CORE =
            simple("permafrost_core", Rarity.EPIC);

    // ---- 肉体鉱山 ----
    public static final RegistryObject<Item> PRIMORDIAL_NERVE =
            simple("primordial_nerve", Rarity.UNCOMMON);
    public static final RegistryObject<Item> UNDYING_CELL =
            simple("undying_cell", Rarity.RARE);
    public static final RegistryObject<Item> WORLD_PULSE_FLUID =
            simple("world_pulse_fluid", Rarity.RARE);
    public static final RegistryObject<Item> PRIMORDIAL_NERVE_BUNDLE =
            simple("primordial_nerve_bundle", Rarity.EPIC);

    // ---- 虚無の都 ----
    public static final RegistryObject<Item> SPATIAL_ANCHOR_CRYSTAL =
            simple("spatial_anchor_crystal", Rarity.RARE);
    public static final RegistryObject<Item> VOID_STABILIZER =
            simple("void_stabilizer", Rarity.EPIC);
    public static final RegistryObject<Item> WORLD_LAW_FRAGMENT =
            simple("world_law_fragment", Rarity.EPIC);

    // ---- 最終素材・究極アイテム ----
    public static final RegistryObject<Item> ABYSS_GOD_CORE = ITEMS.register("abyss_god_core",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC).fireResistant().stacksTo(1)));
    public static final RegistryObject<Item> FIVE_LAYER_UNIFIED_CORE = ITEMS.register("five_layer_unified_core",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC).fireResistant().stacksTo(1)));
    public static final RegistryObject<Item> PRIMORDIAL_RELIC = ITEMS.register("primordial_relic",
            PrimordialRelicItem::new);
    public static final RegistryObject<Item> ABYSS_KEY = ITEMS.register("abyss_key",
            AbyssKeyItem::new);
    public static final RegistryObject<Item> ABYSS_GOD_CATALYST = ITEMS.register("abyss_god_catalyst",
            AbyssGodCatalystItem::new);

    // ---- ブロックアイテム ----
    public static final RegistryObject<Item> ABYSS_IRON_ORE_ITEM = ITEMS.register("abyss_iron_ore",
            () -> new BlockItem(ModBlocks.ABYSS_IRON_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ABYSS_CRYSTAL_ORE_ITEM = ITEMS.register("abyss_crystal_ore",
            () -> new BlockItem(ModBlocks.ABYSS_CRYSTAL_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ABYSS_IRON_BLOCK_ITEM = ITEMS.register("abyss_iron_block",
            () -> new BlockItem(ModBlocks.ABYSS_IRON_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> WORLD_RECONSTRUCTION_FURNACE_ITEM = ITEMS.register("world_reconstruction_furnace",
            () -> new BlockItem(ModBlocks.WORLD_RECONSTRUCTION_FURNACE.get(),
                    new Item.Properties().rarity(Rarity.EPIC).fireResistant()));

    public static final RegistryObject<Item> PRIMORDIAL_BLOOM_ITEM = blockItem("primordial_bloom", ModBlocks.PRIMORDIAL_BLOOM);
    public static final RegistryObject<Item> ASH_VEIN_ITEM = blockItem("ash_vein", ModBlocks.ASH_VEIN);
    public static final RegistryObject<Item> FROZEN_CLUSTER_ITEM = blockItem("frozen_cluster", ModBlocks.FROZEN_CLUSTER);
    public static final RegistryObject<Item> FLESH_DEPOSIT_ITEM = blockItem("flesh_deposit", ModBlocks.FLESH_DEPOSIT);
    public static final RegistryObject<Item> VOID_CRYSTAL_ITEM = blockItem("void_crystal", ModBlocks.VOID_CRYSTAL);

    private static RegistryObject<Item> blockItem(String name, RegistryObject<Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static RegistryObject<Item> simple(String name, Rarity rarity) {
        return ITEMS.register(name, () -> new Item(new Item.Properties().rarity(rarity)));
    }
}
