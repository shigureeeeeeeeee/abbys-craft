package com.abyssworld.item;

import com.abyssworld.AbyssWorld;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 深淵の鍵 - 使用すると現世と深淵世界を行き来する。
 */
public class AbyssKeyItem extends Item {
    public static final ResourceKey<Level> ABYSS =
            ResourceKey.create(Registries.DIMENSION, new ResourceLocation(AbyssWorld.MODID, "abyss"));

    public AbyssKeyItem() {
        super(new Item.Properties().rarity(Rarity.RARE).stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.success(stack);
        }

        boolean inAbyss = level.dimension().equals(ABYSS);
        ResourceKey<Level> targetKey = inAbyss ? Level.OVERWORLD : ABYSS;
        ServerLevel target = serverPlayer.serverLevel().getServer().getLevel(targetKey);
        if (target == null) {
            player.displayClientMessage(
                    Component.translatable("item.abyssworld.abyss_key.missing_dimension")
                            .withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        BlockPos pos = serverPlayer.blockPosition();
        target.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
        BlockPos top = target.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
        if (top.getY() <= target.getMinBuildHeight() + 1) {
            top = new BlockPos(pos.getX(), 72, pos.getZ());
        }

        level.playSound(null, pos, SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.5F, 1.2F);
        serverPlayer.teleportTo(target, top.getX() + 0.5, top.getY() + 1, top.getZ() + 0.5,
                serverPlayer.getYRot(), serverPlayer.getXRot());
        target.playSound(null, top, SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.5F, 1.2F);

        serverPlayer.displayClientMessage(Component.translatable(
                inAbyss ? "item.abyssworld.abyss_key.to_overworld"
                        : "item.abyssworld.abyss_key.to_abyss")
                .withStyle(ChatFormatting.DARK_PURPLE), true);
        player.getCooldowns().addCooldown(this, 100);
        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.abyssworld.abyss_key.desc").withStyle(ChatFormatting.GRAY));
    }
}
