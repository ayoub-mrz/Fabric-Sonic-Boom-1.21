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
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerMovementMixin {
	private Vec3d lastPosition = Vec3d.ZERO;
	private int numberToBoom = 35;
	private boolean sonicBoom = false;
	private boolean timerStart = false;
	private int timer = 0;
	private boolean hasGotItem = false;
	private int speedBoostDuration = 0;
	private Vec3d baseVelocity = Vec3d.ZERO;

	private static final String SONIC_BOOM_DATA_KEY = "SonicBoomMod";
	private static final String HAS_GOT_ITEM_KEY = "hasGotItem";

	@Inject(method = "tick", at = @At("HEAD"))
	private void onPlayerTick(CallbackInfo ci) {
		PlayerEntity player = (PlayerEntity) (Object) this;
		Vec3d currentPos = player.getPos();
		double speed = player.getVelocity().length();
		player.sendMessage(Text.literal("Sonic: " + Math.round(speed)), true);

		if (!currentPos.equals(lastPosition)) {
			ItemStack chestItem = player.getEquippedStack(EquipmentSlot.CHEST);

			if (speed > 1.7 && !sonicBoom && chestItem.isOf(Items.ELYTRA)) {
				numberToBoom--;
			}

			if (timerStart) {
				timer++;
			}

			if (timer == 1200) {
				player.sendMessage(Text.literal("Sonic Boom Count Reset!"), true);
				this.sonicBoom = false;
				this.numberToBoom = 20;
				this.timerStart = false;
				this.timer = 0;
			}

			if (speedBoostDuration > 0) {
				if (speedBoostDuration == 20) {
					baseVelocity = player.getVelocity();
				}

				Vec3d lookDirection = player.getRotationVector();
				double impulseStrength = 1.4;
				Vec3d impulse = lookDirection.multiply(impulseStrength);

				Vec3d newVelocity = baseVelocity.add(impulse);
				player.setVelocity(newVelocity);
				player.velocityModified = true;

				speedBoostDuration--;
			}

			if (numberToBoom == 0 && !sonicBoom) {

				// Increase player speed
				speedBoostDuration = 20;

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

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
		if (nbt.contains(SONIC_BOOM_DATA_KEY)) {
			NbtCompound sonicBoomData = nbt.getCompound(SONIC_BOOM_DATA_KEY).orElse(new NbtCompound());
			this.hasGotItem = sonicBoomData.getBoolean(HAS_GOT_ITEM_KEY).orElse(false);
		}
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
		NbtCompound sonicBoomData = new NbtCompound();
		sonicBoomData.putBoolean(HAS_GOT_ITEM_KEY, this.hasGotItem);
		nbt.put(SONIC_BOOM_DATA_KEY, sonicBoomData);
	}
}