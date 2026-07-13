package com.abyssworld.registry;

import com.abyssworld.AbyssWorld;
import com.abyssworld.ritual.WorldReconstructionFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AbyssWorld.MODID);

    public static final RegistryObject<BlockEntityType<WorldReconstructionFurnaceBlockEntity>> WORLD_RECONSTRUCTION_FURNACE =
            BLOCK_ENTITIES.register("world_reconstruction_furnace",
                    () -> BlockEntityType.Builder.of(
                            WorldReconstructionFurnaceBlockEntity::new,
                            ModBlocks.WORLD_RECONSTRUCTION_FURNACE.get()).build(null));
}
