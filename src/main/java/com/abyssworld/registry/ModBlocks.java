package com.abyssworld.registry;

import com.abyssworld.AbyssWorld;
import com.abyssworld.block.WorldReconstructionFurnaceBlock;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, AbyssWorld.MODID);

    public static final RegistryObject<Block> ABYSS_IRON_ORE = BLOCKS.register("abyss_iron_ore",
            () -> new DropExperienceBlock(
                    BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE).strength(5.0F, 4.0F),
                    UniformInt.of(1, 3)));

    public static final RegistryObject<Block> ABYSS_CRYSTAL_ORE = BLOCKS.register("abyss_crystal_ore",
            () -> new DropExperienceBlock(
                    BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_DIAMOND_ORE).strength(6.0F, 4.0F),
                    UniformInt.of(3, 7)));

    public static final RegistryObject<Block> ABYSS_IRON_BLOCK = BLOCKS.register("abyss_iron_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(8.0F, 8.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHERITE_BLOCK)));

    public static final RegistryObject<Block> WORLD_RECONSTRUCTION_FURNACE = BLOCKS.register("world_reconstruction_furnace",
            () -> new WorldReconstructionFurnaceBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(50.0F, 1200.0F)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 9)
                    .sound(SoundType.NETHERITE_BLOCK)));

    // ---- 階層別採取ブロック ----
    public static final RegistryObject<Block> PRIMORDIAL_BLOOM = BLOCKS.register("primordial_bloom",
            () -> depositBlock(MapColor.PLANT, 2.0F, SoundType.GRASS));
    public static final RegistryObject<Block> ASH_VEIN = BLOCKS.register("ash_vein",
            () -> depositBlock(MapColor.TERRACOTTA_GRAY, 3.0F, SoundType.BASALT));
    public static final RegistryObject<Block> FROZEN_CLUSTER = BLOCKS.register("frozen_cluster",
            () -> depositBlock(MapColor.ICE, 2.5F, SoundType.GLASS));
    public static final RegistryObject<Block> FLESH_DEPOSIT = BLOCKS.register("flesh_deposit",
            () -> depositBlock(MapColor.COLOR_RED, 2.0F, SoundType.SLIME_BLOCK));
    public static final RegistryObject<Block> VOID_CRYSTAL = BLOCKS.register("void_crystal",
            () -> depositBlock(MapColor.COLOR_PURPLE, 4.0F, SoundType.AMETHYST));

    private static Block depositBlock(MapColor color, float hardness, SoundType sound) {
        return new Block(BlockBehaviour.Properties.of()
                .mapColor(color)
                .strength(hardness, hardness + 2.0F)
                .requiresCorrectToolForDrops()
                .sound(sound));
    }
}
