package dev.arketec.localflight.blocks;

import dev.arketec.localflight.blocks.entity.AbstractInventoryBlockEntity;
import dev.arketec.localflight.blocks.entity.BlockEntityFlightController;
import dev.arketec.localflight.configuration.ModConfig;
import dev.arketec.localflight.containers.ControllerContainer;
import dev.arketec.localflight.registration.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockFlightController extends AbstractModBlock implements EntityBlock {
    public static final int LIGHT_LEVEL = 8;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;
    public BlockFlightController() {
        super(BlockBehaviour.Properties.of(Material.METAL)
                .strength(0.5f)
                .sound(SoundType.METAL)
                .lightLevel((BlockState state) -> state.getValue(POWERED) ? LIGHT_LEVEL: 0));
        this.registerDefaultState(
                this.getStateDefinition()
                        .any()
                        .setValue(POWERED, false)
                        .setValue(ENABLED, false)
        );
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return (level1, blockPos, blockState, t) -> {
            if (t instanceof BlockEntityFlightController) {
                ((BlockEntityFlightController)t).tickServer(level1, blockPos, blockState);
            }
        };
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntityFlightController(blockPos, blockState);
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, ENABLED);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand p_60507_, BlockHitResult p_60508_) {
        if (!world.isClientSide && world.getBlockEntity(pos) instanceof final BlockEntityFlightController controller) {
            if (player.isCrouching())
                if (state.getValue(ENABLED))
                    world.setBlockAndUpdate(pos, state.setValue(ENABLED, false).setValue(POWERED, false));
                else
                    world.setBlockAndUpdate(pos, state.setValue(ENABLED, true));
            else
            {
                ItemStack toInsert = player.getItemInHand(InteractionHand.MAIN_HAND);
                String[] resourceName = ModConfig.fuelType.get().split(":");
                var itemResource = toInsert.getItem().getRegistryName();
                var fuelTypeResource = new ResourceLocation(resourceName[0], resourceName[1]);
                if (!toInsert.isEmpty() && toInsert.getCount() > 0 && itemResource.toString().equals(fuelTypeResource.toString())) {
                    ItemStack inInventory = controller.getItemInSlot(0).copy();
                    if (inInventory.getCount() + 1 <= controller.LIMIT) {
                        ItemStack copy = toInsert.copy();
                        controller.insertItem(0, new ItemStack(copy.getItem(), 1));
                        toInsert.shrink(1);
                        controller.update();
                    }
                } else {
                     //MenuProvider container = new SimpleMenuProvider(ControllerContainer.getServerContainer(controller, pos), BlockEntityFlightController.getTitle());
                     //NetworkHooks.openGui((ServerPlayer) player, container, pos);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void attack(BlockState state, Level world, BlockPos pos, Player player) {
        if (!world.isClientSide && world.getBlockEntity(pos) instanceof final BlockEntityFlightController controller) {
            player.displayClientMessage(new TextComponent("Current Fuel Level: " + controller.getItemInSlot(0).getCount()), false);
        }
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos pos, BlockState state, boolean b) {
        if (!blockState.is(state.getBlock())) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof BlockEntityFlightController controller) {
                //Block.dropResources(blockState, level, pos, controller, null, controller.getInventory().getStackInSlot(0));
                controller.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                        .ifPresent(x -> level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY() + 0.5D, pos.getZ(), x.getStackInSlot(0))));
            }

            super.onRemove(blockState, level, pos, state, b);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, Random r) {
        if (state.getValue(POWERED)) {
            VoxelShape shape = state.getShape(world, pos);
            if (!shape.isEmpty()) {
                AABB localBox = shape.bounds();
                double x = pos.getX() + localBox.minX + r.nextDouble() * (localBox.maxX - localBox.minX);
                double y = pos.getY() + localBox.minY + r.nextDouble() * (localBox.maxY - localBox.minY);
                double z = pos.getZ() + localBox.minZ + r.nextDouble() * (localBox.maxZ - localBox.minZ);
                world.addParticle(DustParticleOptions.REDSTONE, x, y, z, 0, 0, 0);
            }
        }
    }
}
