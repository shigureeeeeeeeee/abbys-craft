#!/usr/bin/env python3
"""abyssworld のアイテムモデル・ブロックモデル・テクスチャ(16x16 PNG)を生成する。"""
import json
import os
import random
import struct
import zlib

ROOT = os.path.join(os.path.dirname(__file__), "..", "src", "main", "resources")
ASSETS = os.path.join(ROOT, "assets", "abyssworld")


def write_png(path, pixels):
    """pixels: 16x16 list of (r,g,b,a)"""
    raw = b""
    for y in range(16):
        raw += b"\x00"
        for x in range(16):
            raw += struct.pack("4B", *pixels[y][x])

    def chunk(tag, data):
        c = struct.pack(">I", len(data)) + tag + data
        return c + struct.pack(">I", zlib.crc32(tag + data) & 0xFFFFFFFF)

    png = b"\x89PNG\r\n\x1a\n"
    png += chunk(b"IHDR", struct.pack(">IIBBBBB", 16, 16, 8, 6, 0, 0, 0))
    png += chunk(b"IDAT", zlib.compress(raw))
    png += chunk(b"IEND", b"")
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "wb") as f:
        f.write(png)


def clamp(v):
    return max(0, min(255, int(v)))


def shade(color, factor):
    return tuple(clamp(c * factor) for c in color[:3]) + (255,)


def gem_texture(base, seed):
    """結晶・核系: 中央にひし形の輝き"""
    rng = random.Random(seed)
    px = [[(0, 0, 0, 0)] * 16 for _ in range(16)]
    cx, cy = 7.5, 7.5
    for y in range(16):
        for x in range(16):
            d = abs(x - cx) + abs(y - cy)
            if d <= 6.5:
                f = 1.25 - d * 0.09 + rng.uniform(-0.05, 0.05)
                px[y][x] = shade(base, f)
    for x, y in [(7, 5), (8, 6), (6, 7)]:
        px[y][x] = shade((255, 255, 255), 0.95)
    return px


def ingot_texture(base, seed):
    """インゴット系: 横長の板"""
    rng = random.Random(seed)
    px = [[(0, 0, 0, 0)] * 16 for _ in range(16)]
    for y in range(5, 12):
        for x in range(2, 14):
            f = 1.1 - (y - 5) * 0.06 + rng.uniform(-0.04, 0.04)
            px[y][x] = shade(base, f)
    for x in range(2, 14):
        px[5][x] = shade(base, 1.35)
        px[11][x] = shade(base, 0.6)
    return px


def blob_texture(base, seed):
    """有機物・液体系: 不定形の塊"""
    rng = random.Random(seed)
    px = [[(0, 0, 0, 0)] * 16 for _ in range(16)]
    cx, cy = 7.5, 8.0
    for y in range(16):
        for x in range(16):
            d = ((x - cx) ** 2 + (y - cy) ** 2) ** 0.5
            if d <= 5.5 + rng.uniform(-1.0, 1.0):
                f = 1.15 - d * 0.08 + rng.uniform(-0.08, 0.08)
                px[y][x] = shade(base, f)
    return px


def relic_texture(base, seed):
    """原初神器: 縦の刃と柄"""
    rng = random.Random(seed)
    px = [[(0, 0, 0, 0)] * 16 for _ in range(16)]
    for i in range(11):
        x = 12 - i
        y = 3 + i
        for dx in range(-1, 2):
            if 0 <= x + dx < 16:
                f = 1.3 - abs(dx) * 0.35 + rng.uniform(-0.05, 0.05)
                px[y][x + dx] = shade(base, f)
    for i in range(4):
        x, y = 3 - min(i, 1), 12 + i
        if 0 <= x < 16 and 0 <= y < 16:
            px[y][x] = (72, 48, 24, 255)
    px[3][12] = (255, 255, 255, 255)
    px[4][11] = shade((255, 230, 160), 1.0)
    return px


def key_texture(base, seed):
    """鍵: 上部のリングと下部の刃"""
    px = [[(0, 0, 0, 0)] * 16 for _ in range(16)]
    # リング(上部)
    for y in range(2, 7):
        for x in range(5, 11):
            d = ((x - 7.5) ** 2 + (y - 4) ** 2) ** 0.5
            if 1.5 <= d <= 2.8:
                px[y][x] = shade(base, 1.1)
    # 軸
    for y in range(7, 14):
        px[y][7] = shade(base, 1.0)
        px[y][8] = shade(base, 0.8)
    # 刃(下部の歯)
    for x in (9, 10):
        px[12][x] = shade(base, 1.05)
    for x in (9,):
        px[10][x] = shade(base, 1.05)
    px[3][7] = (255, 255, 255, 255)
    return px


def block_texture(base, seed, noisy=True, spots=None):
    """フルブロック用 16x16"""
    rng = random.Random(seed)
    px = [[None] * 16 for _ in range(16)]
    for y in range(16):
        for x in range(16):
            f = 1.0 + (rng.uniform(-0.12, 0.12) if noisy else 0)
            px[y][x] = shade(base, f)
    if spots:
        for _ in range(14):
            x, y = rng.randint(1, 14), rng.randint(1, 14)
            px[y][x] = shade(spots, 1.1)
            px[y][min(x + 1, 15)] = shade(spots, 0.85)
    return px


ITEM_TEXTURES = {
    # 基礎素材
    "raw_abyss_iron": (blob_texture, (86, 70, 110)),
    "abyss_iron_ingot": (ingot_texture, (128, 100, 180)),
    "compressed_abyss_iron": (ingot_texture, (96, 72, 150)),
    "high_density_abyss_alloy": (ingot_texture, (60, 40, 110)),
    "abyss_crystal": (gem_texture, (150, 90, 220)),
    "compressed_abyss_crystal": (gem_texture, (100, 50, 180)),
    # 忘却の森
    "primordial_sap": (blob_texture, (110, 190, 70)),
    "awakened_vine": (blob_texture, (60, 140, 50)),
    "perfect_life_core": (gem_texture, (120, 230, 120)),
    "rotten_forest_core": (gem_texture, (70, 110, 40)),
    # 灰の荒野
    "eternal_flame": (blob_texture, (255, 140, 30)),
    "superheated_core": (gem_texture, (255, 90, 30)),
    "ash_king_metal": (ingot_texture, (150, 140, 140)),
    "eternal_furnace_core": (gem_texture, (255, 60, 0)),
    # 蒼氷洞窟
    "unmelting_ice_crystal": (gem_texture, (140, 210, 255)),
    "frozen_time_shard": (gem_texture, (90, 160, 230)),
    "permafrost_core": (gem_texture, (60, 110, 200)),
    # 肉体鉱山
    "primordial_nerve": (blob_texture, (230, 120, 140)),
    "undying_cell": (blob_texture, (200, 60, 90)),
    "world_pulse_fluid": (blob_texture, (170, 30, 60)),
    "primordial_nerve_bundle": (gem_texture, (240, 100, 120)),
    # 虚無の都
    "spatial_anchor_crystal": (gem_texture, (180, 160, 255)),
    "void_stabilizer": (gem_texture, (110, 90, 160)),
    "world_law_fragment": (gem_texture, (230, 220, 255)),
    # 最終素材・究極アイテム
    "abyss_god_core": (gem_texture, (40, 0, 80)),
    "five_layer_unified_core": (gem_texture, (255, 215, 90)),
    "primordial_relic": (relic_texture, (200, 170, 255)),
    "abyss_key": (key_texture, (170, 120, 230)),
    "abyss_god_catalyst": (gem_texture, (80, 0, 120)),
}

BLOCK_TEXTURES = {
    "abyss_iron_ore": ((45, 42, 60), (128, 100, 180)),
    "abyss_crystal_ore": ((45, 42, 60), (150, 90, 220)),
    "abyss_iron_block": ((110, 85, 160), None),
    "world_reconstruction_furnace": ((25, 15, 45), (255, 215, 90)),
    "primordial_bloom": ((40, 70, 35), (120, 220, 80)),
    "ash_vein": ((50, 45, 45), (255, 120, 40)),
    "frozen_cluster": ((180, 210, 240), (140, 200, 255)),
    "flesh_deposit": ((120, 30, 45), (220, 80, 100)),
    "void_crystal": ((30, 20, 50), (180, 160, 255)),
}


def main():
    # アイテムテクスチャ + モデル
    for name, (fn, color) in ITEM_TEXTURES.items():
        write_png(os.path.join(ASSETS, "textures", "item", name + ".png"), fn(color, name))
        model = {"parent": "minecraft:item/generated",
                 "textures": {"layer0": "abyssworld:item/" + name}}
        if name == "primordial_relic":
            model["parent"] = "minecraft:item/handheld"
        path = os.path.join(ASSETS, "models", "item", name + ".json")
        os.makedirs(os.path.dirname(path), exist_ok=True)
        with open(path, "w") as f:
            json.dump(model, f, indent=2)

    # ブロックテクスチャ + モデル + blockstate + ブロックアイテムモデル
    for name, (base, spots) in BLOCK_TEXTURES.items():
        write_png(os.path.join(ASSETS, "textures", "block", name + ".png"),
                  block_texture(base, name, spots=spots))
        bmodel = {"parent": "minecraft:block/cube_all",
                  "textures": {"all": "abyssworld:block/" + name}}
        with open(os.path.join(ASSETS, "models", "block", name + ".json"), "w") as f:
            json.dump(bmodel, f, indent=2)
        state = {"variants": {"": {"model": "abyssworld:block/" + name}}}
        os.makedirs(os.path.join(ASSETS, "blockstates"), exist_ok=True)
        with open(os.path.join(ASSETS, "blockstates", name + ".json"), "w") as f:
            json.dump(state, f, indent=2)
        imodel = {"parent": "abyssworld:block/" + name}
        with open(os.path.join(ASSETS, "models", "item", name + ".json"), "w") as f:
            json.dump(imodel, f, indent=2)

    print("generated:", len(ITEM_TEXTURES), "items,", len(BLOCK_TEXTURES), "blocks")


if __name__ == "__main__":
    main()
