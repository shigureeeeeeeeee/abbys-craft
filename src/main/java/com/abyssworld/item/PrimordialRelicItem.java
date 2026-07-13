package com.abyssworld.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 原初神器 - 剣・鎌・杖・砲へ形態変化する究極武器。
 */
public class PrimordialRelicItem extends Item {
    private static final String TAG_MODE = "AbyssMode";
    private static final UUID DAMAGE_UUID = UUID.fromString("8a4d1a8e-2f5b-4c3d-9e6f-1b2c3d4e5f60");
    private static final UUID SPEED_UUID = UUID.fromString("1f6c2b3a-4d5e-6f70-8192-a3b4c5d6e7f8");

    public enum Mode {
        BLADE(29.0D, -2.2D),
        SCYTHE(19.0D, -3.0D),
        STAFF(9.0D, -2.6D),
        CANNON(14.0D, -2.8D);

        final double damage;
        final double speed;

        Mode(double damage, double speed) {
            this.damage = damage;
            this.speed = speed;
        }

        Mode next() {
            Mode[] values = values();
            return values[(ordinal() + 1) % values.length];
        }

        String translationKey() {
            return "item.abyssworld.primordial_relic.mode." + name().toLowerCase();
        }
    }

    public PrimordialRelicItem() {
        super(new Item.Properties().rarity(Rarity.EPIC).fireResistant().stacksTo(1));
    }

    public static Mode getMode(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return Mode.BLADE;
        int ordinal = tag.getInt(TAG_MODE);
        return Mode.values()[Mth.clamp(ordinal, 0, Mode.values().length - 1)];
    }

    private static void setMode(ItemStack stack, Mode mode) {
        stack.getOrCreateTag().putInt(TAG_MODE, mode.ordinal());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // スニーク使用で形態変化
        if (player.isShiftKeyDown()) {
            Mode next = getMode(stack).next();
            setMode(stack, next);
            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.translatable("item.abyssworld.primordial_relic.switched",
                                Component.translatable(next.translationKey())).withStyle(ChatFormatting.LIGHT_PURPLE),
                        true);
                level.playSound(null, player.blockPosition(),
                        SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.8F, 1.6F);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        switch (getMode(stack)) {
            case BLADE -> {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 2));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1));
                player.getCooldowns().addCooldown(this, 200);
                level.playSound(null, player.blockPosition(),
                        SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 0.6F);
            }
            case SCYTHE -> {
                AABB area = player.getBoundingBox().inflate(8.0D);
                List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area,
                        e -> e != player && e.isAlive());
                for (LivingEntity target : targets) {
                    target.hurt(level.damageSources().playerAttack(player), 40.0F);
                }
                player.getCooldowns().addCooldown(this, 200);
                level.playSound(null, player.blockPosition(),
                        SoundEvents.WITHER_SHOOT, SoundSource.PLAYERS, 1.0F, 0.5F);
            }
            case STAFF -> {
                Vec3 eye = player.getEyePosition();
                Vec3 end = eye.add(player.getLookAngle().scale(48.0D));
                HitResult hit = level.clip(new ClipContext(eye, end,
                        ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
                if (bolt != null) {
                    bolt.moveTo(hit.getLocation());
                    bolt.setCause(player instanceof net.minecraft.server.level.ServerPlayer sp ? sp : null);
                    level.addFreshEntity(bolt);
                }
                player.getCooldowns().addCooldown(this, 100);
            }
            case CANNON -> {
                Vec3 look = player.getLookAngle();
                LargeFireball fireball = new LargeFireball(level, player,
                        look.x, look.y, look.z, 3);
                fireball.setPos(player.getX() + look.x * 1.5D,
                        player.getEyeY() + look.y * 1.5D,
                        player.getZ() + look.z * 1.5D);
                level.addFreshEntity(fireball);
                player.getCooldowns().addCooldown(this, 60);
                level.playSound(null, player.blockPosition(),
                        SoundEvents.GHAST_SHOOT, SoundSource.PLAYERS, 1.0F, 0.8F);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // 通常攻撃の余波: 周囲の敵にも波及ダメージ
        if (!target.level().isClientSide && attacker instanceof Player player) {
            AABB area = target.getBoundingBox().inflate(3.0D);
            List<LivingEntity> nearby = target.level().getEntitiesOfClass(LivingEntity.class, area,
                    e -> e != player && e != target && e.isAlive());
            for (LivingEntity entity : nearby) {
                entity.hurt(target.level().damageSources().playerAttack(player), 20.0F);
            }
        }
        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.MAINHAND) {
            Mode mode = getMode(stack);
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(DAMAGE_UUID,
                    "Relic damage", mode.damage, AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(SPEED_UUID,
                    "Relic speed", mode.speed, AttributeModifier.Operation.ADDITION));
            return builder.build();
        }
        return super.getAttributeModifiers(slot, stack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Mode mode = getMode(stack);
        tooltip.add(Component.translatable("item.abyssworld.primordial_relic.current_mode",
                Component.translatable(mode.translationKey())).withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable(mode.translationKey() + ".desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.abyssworld.primordial_relic.hint").withStyle(ChatFormatting.DARK_GRAY));
    }
}
