package com.github.thedeathlycow.moregeodes.testmod;

import com.github.thedeathlycow.moregeodes.blocks.CrystalBlock;
import com.github.thedeathlycow.moregeodes.blocks.ModBlocks;
import com.github.thedeathlycow.moregeodes.sounds.CrystalBlockSoundGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class CrystalSoundTests {
    private static final BlockPos POS = MoreGeodesTestHelper.CENTER;

    private static void assertSoundGroup(GameTestHelper context, BlockPos pos, CrystalBlockSoundGroup expected) {
        var block = context.getBlockState(pos).getBlock();
        context.assertTrue(
                block instanceof CrystalBlock,
                "block at " + pos + " must be a CrystalBlock"
        );
        context.assertTrue(
                expected.equals(((CrystalBlock) block).getHitSoundGroup()),
                "crystal block at " + pos + " must carry sound group " + expected
        );
    }

    @GameTest(template = "geodes-test:empty15")
    public void F_015_crystal_blocks_carry_their_own_sound_groups(GameTestHelper context) {
        context.setBlock(POS, ModBlocks.EMERALD_CRYSTAL_BLOCK);
        assertSoundGroup(context, POS, CrystalBlockSoundGroup.EMERALD_GEODE);

        context.setBlock(POS, ModBlocks.QUARTZ_CRYSTAL_BLOCK);
        assertSoundGroup(context, POS, CrystalBlockSoundGroup.QUARTZ_GEODE);

        context.setBlock(POS, ModBlocks.DIAMOND_CRYSTAL_BLOCK);
        assertSoundGroup(context, POS, CrystalBlockSoundGroup.DIAMOND_GEODE);

        context.setBlock(POS, ModBlocks.LAPIS_CRYSTAL_BLOCK);
        assertSoundGroup(context, POS, CrystalBlockSoundGroup.LAPIS);

        context.setBlock(POS, ModBlocks.GYPSUM_CRYSTAL_BLOCK);
        assertSoundGroup(context, POS, CrystalBlockSoundGroup.GYPSUM);

        context.succeed();
    }

    @GameTest(template = "geodes-test:empty15")
    public void F_015_projectile_hit_plays_chime_and_keeps_block(GameTestHelper context) {
        context.setBlock(POS, ModBlocks.EMERALD_CRYSTAL_BLOCK);

        Projectile projectile = context.spawn(EntityType.EGG, POS.above());
        BlockHitResult hit = new BlockHitResult(
                Vec3.atCenterOf(context.absolutePos(POS)),
                net.minecraft.core.Direction.UP,
                context.absolutePos(POS),
                false
        );
        ((CrystalBlock) context.getBlockState(POS).getBlock()).onProjectileHit(
                context.getLevel(), context.getBlockState(POS), hit, projectile
        );

        context.assertBlockPresent(ModBlocks.EMERALD_CRYSTAL_BLOCK, POS);
        context.succeed();
    }
}
