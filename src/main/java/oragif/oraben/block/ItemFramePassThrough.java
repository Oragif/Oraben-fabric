package oragif.oraben.block;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.block.BlockState;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import oragif.oraben.Oraben;

import static oragif.oraben.util.PlayerUtil.emptyHands;

public class ItemFramePassThrough {
    public static void register() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient || !Oraben.cfg.itemframePassThrough) { return ActionResult.PASS; }
            if (entity instanceof ItemFrameEntity) {
                if (!player.isSneaking()) {
                    if (!emptyHands(player)) { return ActionResult.PASS; }
                    BlockPos pos = entity.getBlockPos();
                    Direction oppositeDirection = entity.getHorizontalFacing().getOpposite();
                    BlockPos hangingPos = pos.add(oppositeDirection.getOffsetX(), oppositeDirection.getOffsetY(), oppositeDirection.getOffsetZ());
                    BlockState hangingState = world.getBlockState(hangingPos);
                    Vec3d hanginPosVec3d = new Vec3d(hangingPos.getX(), hangingPos.getY(), hangingPos.getZ());
                    BlockHitResult hangingHitResult = new BlockHitResult(hanginPosVec3d, oppositeDirection, pos, false);
                    hangingState.getBlock().onUse(hangingState, world, hangingPos, player, hand, hangingHitResult);

                    return ActionResult.SUCCESS;
                }
            }

            return ActionResult.PASS;
        });
    }
}
