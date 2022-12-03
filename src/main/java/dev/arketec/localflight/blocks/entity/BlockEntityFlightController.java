package dev.arketec.localflight.blocks.entity;

import dev.arketec.localflight.LocalFlight;
import dev.arketec.localflight.configuration.ModConfig;
import dev.arketec.localflight.registration.ModTileEntityTypes;
import dev.arketec.localflight.util.TimeoutList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;

import javax.annotation.Nonnull;
import java.util.List;

import static dev.arketec.localflight.blocks.BlockFlightController.ENABLED;
import static dev.arketec.localflight.blocks.BlockFlightController.POWERED;

public class BlockEntityFlightController extends AbstractInventoryBlockEntity {
    private TimeoutList<Player> _activePlayers;
    public static final int LIMIT = 64;
    private int lifetime;
    public BlockEntityFlightController(BlockPos pos, BlockState blockState) {
        super(ModTileEntityTypes.CONTROLLER.get(), pos, blockState, 1);
        _activePlayers = new TimeoutList<Player>(player -> {
                if (player instanceof ServerPlayer && !((ServerPlayer) player).isCreative() && !((ServerPlayer) player).isSpectator()) {
                    stopFlying(player);
                }
            }, TickEvent.Type.SERVER);
    }

    public void tickServer(Level world, BlockPos pos, BlockState state) {
        if (state == null || world == null || pos == null) return;
        int RANGE = ModConfig.range.get();
        ItemStack fuel = inventory.getStackInSlot(0);
        if (state.getValue(ENABLED)) {
            if (!ModConfig.requiresFuel.get()) {
                if (!state.getValue(POWERED))
                    world.setBlockAndUpdate(pos, state.setValue(POWERED, true));
            } else if (fuel.getCount() > 0 && lifetime >= 0) {
                if (lifetime-- == 0) {
                    fuel.shrink(1);
                    lifetime = ModConfig.fuelTime.get();
                    dirty = true;
                    update();
                }
                if (fuel.isEmpty() && lifetime > 0 && state.getValue(POWERED))
                    world.setBlockAndUpdate(pos, state.setValue(POWERED, false));
                else if (!state.getValue(POWERED))
                    world.setBlockAndUpdate(pos, state.setValue(POWERED, true));
            } else if (state.getValue(POWERED)) {
                world.setBlockAndUpdate(pos, state.setValue(POWERED, false));
            }
        }
        AABB area = new AABB(worldPosition.offset(-RANGE, -RANGE, -RANGE), worldPosition.offset(RANGE, RANGE, RANGE));
        List<Player> players = level.getNearbyPlayers(TargetingConditions.forNonCombat(), null, area);
        if (state.getValue(POWERED) && state.getValue(ENABLED))
            players.forEach(p ->
                startFlying(p)
            );
        _activePlayers.tick(TickEvent.Type.SERVER);
    }

    @Override
    protected boolean canInsert(ItemStack item) {
        String[] resourceName = ModConfig.fuelType.get().split(":");
        var itemResource = item.getItem().getRegistryName();
        var fuelTypeResource = new ResourceLocation(resourceName[0], resourceName[1]);
        return itemResource.toString().equals(fuelTypeResource.toString());
    }

    @Override
    public void tick(Level world, BlockPos pos, BlockState state, AbstractModBlockEntity p_155256_) {
        if (level.isClientSide()) return;
        super.tick();
        tickServer(world, pos, state);
    }

    @Nonnull
    @Override
    public void readPacketNBT(CompoundTag tag) {
        lifetime = tag.getInt("Lifetime");
        super.readPacketNBT(tag);
    }

    @Nonnull
    @Override
    public void writePacketNBT(CompoundTag tag) {
        tag.putInt("Lifetime", lifetime);
        super.writePacketNBT(tag);
    }

    public void setEnabled(boolean b) {
        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(ENABLED, b));
    }
    public boolean getEnabled() {
        return getBlockState().getValue(ENABLED);
    }

    public static Component getTitle() {
        return new TranslatableComponent("containers." + LocalFlight.MODID + ".controller");
    }

    private void startFlying(Player player) {
        player.getAbilities().mayfly = true;
        player.onUpdateAbilities();
        _activePlayers.setOrAddTimeout(20, player);
    }
    private void stopFlying(Player player) {
        player.getAbilities().flying = false;
        player.getAbilities().mayfly = false;
        player.onUpdateAbilities();
    }
}
