package com.abyssworld.ritual;

import com.abyssworld.registry.ModBlockEntities;
import com.abyssworld.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 世界律再構築炉 - 最終クラフト設備。
 *
 * 素材を右クリックで投入し、全素材が揃うと防衛イベントが開始される。
 * 一定時間ごとに敵のウェーブが襲来し、最終ウェーブを凌ぎ切ると原初神器が完成する。
 */
public class WorldReconstructionFurnaceBlockEntity extends BlockEntity {

    /** 防衛イベントの長さ(tick)。MVP: 2分。 */
    private static final int RITUAL_DURATION = 20 * 60 * 2;
    /** ウェーブ間隔(tick)。MVP: 20秒。 */
    private static final int WAVE_INTERVAL = 20 * 20;

    private final Map<String, Integer> inserted = new LinkedHashMap<>();
    private int ritualTicks = -1;
    private int nextWave = 0;

    public WorldReconstructionFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WORLD_RECONSTRUCTION_FURNACE.get(), pos, state);
    }

    /** 必要素材とその個数。 */
    private static Map<Item, Integer> requiredMaterials() {
        Map<Item, Integer> map = new LinkedHashMap<>();
        map.put(ModItems.ABYSS_GOD_CORE.get(), 1);
        map.put(ModItems.FIVE_LAYER_UNIFIED_CORE.get(), 1);
        map.put(ModItems.WORLD_LAW_FRAGMENT.get(), 3);
        map.put(ModItems.HIGH_DENSITY_ABYSS_ALLOY.get(), 9);
        map.put(ModItems.COMPRESSED_ABYSS_CRYSTAL.get(), 9);
        return map;
    }

    private static String keyOf(Item item) {
        return net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(item).toString();
    }

    public void interact(Player player, InteractionHand hand) {
        if (level == null || level.isClientSide) return;

        if (ritualTicks >= 0) {
            int remaining = (RITUAL_DURATION - ritualTicks) / 20;
            player.displayClientMessage(Component.translatable(
                    "block.abyssworld.world_reconstruction_furnace.defending", remaining)
                    .withStyle(ChatFormatting.RED), true);
            return;
        }

        ItemStack held = player.getItemInHand(hand);
        Map<Item, Integer> required = requiredMaterials();

        if (!held.isEmpty() && required.containsKey(held.getItem())) {
            String key = keyOf(held.getItem());
            int need = required.get(held.getItem());
            int have = inserted.getOrDefault(key, 0);
            if (have < need) {
                int insert = Math.min(need - have, held.getCount());
                held.shrink(insert);
                inserted.put(key, have + insert);
                setChanged();
                level.playSound(null, worldPosition, SoundEvents.RESPAWN_ANCHOR_CHARGE,
                        SoundSource.BLOCKS, 1.0F, 0.8F);
                player.displayClientMessage(Component.translatable(
                        "block.abyssworld.world_reconstruction_furnace.inserted",
                        held.getHoverName(), have + insert, need).withStyle(ChatFormatting.AQUA), true);
            } else {
                player.displayClientMessage(Component.translatable(
                        "block.abyssworld.world_reconstruction_furnace.full")
                        .withStyle(ChatFormatting.YELLOW), true);
            }
        } else {
            // 進捗の表示
            player.displayClientMessage(Component.translatable(
                    "block.abyssworld.world_reconstruction_furnace.status").withStyle(ChatFormatting.GOLD), false);
            for (Map.Entry<Item, Integer> entry : required.entrySet()) {
                int have = inserted.getOrDefault(keyOf(entry.getKey()), 0);
                ChatFormatting color = have >= entry.getValue() ? ChatFormatting.GREEN : ChatFormatting.GRAY;
                player.displayClientMessage(Component.literal(" - ")
                        .append(entry.getKey().getDescription())
                        .append(Component.literal(" " + have + "/" + entry.getValue()))
                        .withStyle(color), false);
            }
        }

        if (isComplete()) {
            startRitual();
        }
    }

    private boolean isComplete() {
        for (Map.Entry<Item, Integer> entry : requiredMaterials().entrySet()) {
            if (inserted.getOrDefault(keyOf(entry.getKey()), 0) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    private void startRitual() {
        if (level == null) return;
        ritualTicks = 0;
        nextWave = 0;
        setChanged();
        broadcast(Component.translatable("block.abyssworld.world_reconstruction_furnace.start")
                .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD));
        level.playSound(null, worldPosition, SoundEvents.END_PORTAL_SPAWN,
                SoundSource.BLOCKS, 1.5F, 0.6F);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state,
                                  WorldReconstructionFurnaceBlockEntity furnace) {
        if (furnace.ritualTicks < 0 || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        furnace.ritualTicks++;

        if (furnace.ritualTicks >= furnace.nextWave) {
            boolean finalWave = furnace.ritualTicks + WAVE_INTERVAL >= RITUAL_DURATION;
            furnace.spawnWave(serverLevel, pos, finalWave);
            furnace.nextWave = furnace.ritualTicks + WAVE_INTERVAL;
        }

        if (furnace.ritualTicks >= RITUAL_DURATION) {
            furnace.completeRitual(serverLevel, pos);
        }

        if (furnace.ritualTicks % 100 == 0) {
            furnace.setChanged();
        }
    }

    private void spawnWave(ServerLevel level, BlockPos pos, boolean finalWave) {
        int wave = ritualTicks / WAVE_INTERVAL + 1;
        broadcast(Component.translatable(
                finalWave ? "block.abyssworld.world_reconstruction_furnace.final_wave"
                          : "block.abyssworld.world_reconstruction_furnace.wave", wave)
                .withStyle(finalWave ? ChatFormatting.DARK_RED : ChatFormatting.RED));

        EntityType<?>[] pool = finalWave
                ? new EntityType<?>[]{EntityType.WITHER_SKELETON, EntityType.RAVAGER, EntityType.EVOKER}
                : new EntityType<?>[]{EntityType.ZOMBIE, EntityType.SKELETON, EntityType.PILLAGER, EntityType.VINDICATOR};

        int count = finalWave ? 12 : 6 + wave;
        for (int i = 0; i < count; i++) {
            EntityType<?> type = pool[level.random.nextInt(pool.length)];
            double angle = level.random.nextDouble() * Math.PI * 2;
            double distance = 12 + level.random.nextDouble() * 8;
            BlockPos spawnPos = pos.offset(
                    (int) (Math.cos(angle) * distance), 0, (int) (Math.sin(angle) * distance));
            spawnPos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, spawnPos);
            if (type.create(level) instanceof Mob mob) {
                mob.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                        level.random.nextFloat() * 360F, 0);
                mob.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos),
                        MobSpawnType.EVENT, null, null);
                mob.setPersistenceRequired();
                level.addFreshEntity(mob);
            }
        }
        level.playSound(null, pos, SoundEvents.RAID_HORN.value(), SoundSource.HOSTILE, 2.0F, 1.0F);
    }

    private void completeRitual(ServerLevel level, BlockPos pos) {
        ritualTicks = -1;
        inserted.clear();
        setChanged();

        // 演出: 雷と完成メッセージ
        for (int i = 0; i < 4; i++) {
            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
            if (bolt != null) {
                bolt.moveTo(pos.getX() + 0.5 + (i - 1.5) * 2, pos.getY(), pos.getZ() + 0.5);
                bolt.setVisualOnly(true);
                level.addFreshEntity(bolt);
            }
        }
        level.playSound(null, pos, SoundEvents.ENDER_DRAGON_DEATH, SoundSource.BLOCKS, 1.5F, 1.2F);

        ItemEntity drop = new ItemEntity(level,
                pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                new ItemStack(ModItems.PRIMORDIAL_RELIC.get()));
        drop.setUnlimitedLifetime();
        drop.setInvulnerable(true);
        level.addFreshEntity(drop);

        broadcast(Component.translatable("block.abyssworld.world_reconstruction_furnace.complete")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
    }

    private void broadcast(Component message) {
        if (level != null && level.getServer() != null) {
            level.getServer().getPlayerList().broadcastSystemMessage(message, false);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("RitualTicks", ritualTicks);
        tag.putInt("NextWave", nextWave);
        CompoundTag materials = new CompoundTag();
        inserted.forEach(materials::putInt);
        tag.put("Inserted", materials);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ritualTicks = tag.getInt("RitualTicks");
        nextWave = tag.getInt("NextWave");
        inserted.clear();
        CompoundTag materials = tag.getCompound("Inserted");
        for (String key : materials.getAllKeys()) {
            inserted.put(key, materials.getInt(key));
        }
        if (!tag.contains("RitualTicks")) {
            ritualTicks = -1;
        }
    }
}
