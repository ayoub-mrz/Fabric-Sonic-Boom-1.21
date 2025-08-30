package net.ayoubmrz.sonicboommod.mixin;

import net.ayoubmrz.sonicboommod.block.ModBlocks;
import net.ayoubmrz.sonicboommod.particle.ModParticles;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerMovementMixin {
	private Vec3d lastPosition = Vec3d.ZERO;
	private int numberToBoom = 25;
	private boolean sonicBoom = false;
	private boolean timerStart = false;
	private int timer = 0;
	private boolean hasGotItem = false;

	private static final String SONIC_BOOM_DATA_KEY = "SonicBoomMod";
	private static final String HAS_GOT_ITEM_KEY = "hasGotItem";

	@Inject(method = "tick", at = @At("HEAD"))
	private void onPlayerTick(CallbackInfo ci) {
		PlayerEntity player = (PlayerEntity) (Object) this;
		Vec3d currentPos = player.getPos();

		if (!currentPos.equals(lastPosition)) {
			double speed = currentPos.distanceTo(lastPosition);
			ItemStack chestItem = player.getEquippedStack(EquipmentSlot.CHEST);

			if (speed > 1.7 && !sonicBoom && chestItem.isOf(Items.ELYTRA)) {
				numberToBoom--;
			}

			if (timerStart) {
				timer++;
			}

			if (timer == 1200) {
				this.sonicBoom = false;
				this.numberToBoom = 20;
				this.timerStart = false;
				this.timer = 0;
			}

			if (numberToBoom == 0 && !sonicBoom) {

				player.getWorld().playSound(
						null,
						player.getX(), player.getY(), player.getZ(),
						SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
						SoundCategory.HOSTILE,
						30.0f,
						1.4f
				);

				if (player.getWorld() instanceof ServerWorld serverWorld) {

					serverWorld.spawnParticles(
							ModParticles.SONIC_BOOM_EFFECT,
							player.getX(), player.getY(), player.getZ(),
							1, 0.0, 0.0, 0.0, 0.0
					);

					if (!hasGotItem) {
						ItemStack stack = new ItemStack(ModBlocks.EGLE_STATUE, 1);
						boolean inserted = player.getInventory().insertStack(stack);

						if (!inserted) {
							player.dropItem(stack, false);
						}
						hasGotItem = true;

					}
				}
				sonicBoom = true;
				timerStart = true;

				// grant Sonic Boom Advancement
				if (player instanceof ServerPlayerEntity serverPlayer) {
					Identifier advancementId = Identifier.of("sonicboommod", "end/sonic_boom");
					AdvancementEntry advancementEntry = serverPlayer.getServer().getAdvancementLoader().get(advancementId);

					if (advancementEntry != null) {
						AdvancementProgress progress = serverPlayer.getAdvancementTracker().getProgress(advancementEntry);
						if (!progress.isDone()) {
							for (String criterion : progress.getUnobtainedCriteria()) {
								serverPlayer.getAdvancementTracker().grantCriterion(advancementEntry, criterion);
							}
						}
					}
				}
			}
		}

		lastPosition = currentPos;
	}

	@Inject(method = "readCustomData", at = @At("TAIL"))
	protected void readCustomData(ReadView view, CallbackInfo ci) {
		this.hasGotItem = view.getBoolean("hasGotItem", false);
	}

	@Inject(method = "writeCustomData", at = @At("TAIL"))
	protected void writeCustomData(WriteView view, CallbackInfo ci) {
		view.putBoolean("hasGotItem", this.hasGotItem);
	}

}












