package net.faxu.minidox.entity.custom;

import net.faxu.minidox.Minidox;
import net.faxu.minidox.entity.variant.MinidoxVariant;
import net.faxu.minidox.registry.MinidoxSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class MinidoxEntity extends TameableEntity implements IAnimatable {
    private final AnimationFactory factory = new AnimationFactory(this);
    public static int levelMob = 0;
    private static final TrackedData<Integer> MOB_LEVEL;
    private static final TrackedData<Boolean> SITTING;
    public int chargesAttack = 0;

    //Cambio de Skin por nivel
    //Habilidades por nivel: 1. Tercer Golpe potente 2. Ataque en área 3. Motiva aliados y le otorga fuerza y speed
    //Subida de estadísticas por nivel
    //Regeneracion de vida al no estar en combate
    //Rota cuando está sit
    //Sonido de Ataque

    public MinidoxEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        experiencePoints = levelMob;
    }

    private <E extends IAnimatable> PlayState deathDox(AnimationEvent<E> event) {
        if ((this.dead || this.getHealth() < 0.01 || this.isDead())) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.minidox.death", false));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <E extends IAnimatable>PlayState walkDox(AnimationEvent<E> event) {
        if(!isInSittingPose() && isTamed()) {
            if(event.isMoving()) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.minidox.walk", true));
            } else {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.minidox.idle", true));
            }
            return PlayState.CONTINUE;
        }
        event.getController().markNeedsReload();
        return PlayState.STOP;
    }

    private <E extends IAnimatable>PlayState sitDox(AnimationEvent<E> event) {
        if(isInSittingPose()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.minidox.sit", false).
                    addAnimation("animation.minidox.sit2", true));
            return PlayState.CONTINUE;
        }
        event.getController().markNeedsReload();
        return PlayState.STOP;
    }

    private <E extends IAnimatable> PlayState attackDox(AnimationEvent<E> event) {
        int randomNumber = Random.create().nextBetween(1,2);
        if (this.handSwinging && event.getController().getAnimationState().equals(AnimationState.Stopped)) {
            event.getController().markNeedsReload();
            if (randomNumber == 1) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.minidox.attack", false));
            } else {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.minidox.attack2", false));
            }
            this.handSwinging = false;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "walkController",
                0, this::walkDox));
        animationData.addAnimationController(new AnimationController(this, "attackController",
                0, this::attackDox));
        animationData.addAnimationController(new AnimationController(this, "sitController",
                0, this::sitDox));
        animationData.addAnimationController(new AnimationController(this, "deathController",
                0, this::deathDox));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    //Permanence of Entity
    @Override
    protected void updatePostDeath() {
        ++this.deathTime;
        if (this.deathTime == 65) {
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.235f)
                .add(EntityAttributes.GENERIC_ARMOR, 4)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.1);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        //Escape ->
        this.goalSelector.add(3, new SitGoal(this));
        this.goalSelector.add(4, new MeleeAttackGoal(this,1.2, true));
        this.goalSelector.add(5, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F, false));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.2, 0.0F));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
//        this.goalSelector.add(8, new LookAroundGoal(this));

        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, RaiderEntity.class, true));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, HostileEntity.class, true));
        this.targetSelector.add(5, new ActiveTargetGoal<>(this, SlimeEntity.class, true));
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        Item item = itemStack.getItem();
        Item itemForTaming = Items.NETHERITE_INGOT;

        if (item == itemForTaming && !isTamed()) {
            if (this.world.isClient()) {
                return ActionResult.CONSUME;
            } else {
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }

                if (!this.world.isClient()) {
                    super.setOwner(player);
                    this.navigation.recalculatePath();
                    this.setTarget(null);
                    this.world.sendEntityStatus(this, (byte)7);
                    setSit(true);
                }

                return ActionResult.SUCCESS;
            }
        }

        if(isTamed() && !this.world.isClient() && hand == Hand.MAIN_HAND) {
            setSit(!isSitting());
            return ActionResult.SUCCESS;
        }

        if (itemStack.getItem() == itemForTaming) {
            return ActionResult.PASS;
        }

        if (this.getHealth() < this.getMaxHealth() && itemStack.getItem() == itemForTaming) {
            heal(this.getMaxHealth());
            return ActionResult.CONSUME;
        }

        return super.interactMob(player, hand);
    }

    public void setSit(boolean sitting) {
        this.dataTracker.set(SITTING, sitting);
        super.setSitting(sitting);
    }

    public boolean isSitting() {
        return this.dataTracker.get(SITTING);
    }

    //Sounds
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        if (Minidox.CONFIG.activateSoundDeath) {
            return MinidoxSounds.MINIDOX_DEATH;
        }
        return super.getDeathSound();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        if (Minidox.CONFIG.activateSoundHurt) {
            return MinidoxSounds.MINIDOX_HURT;
        }
        return super.getHurtSound(source);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (Minidox.CONFIG.activateSoundIdle) {
            return MinidoxSounds.MINIDOX_IDLE;
        }
        return super.getAmbientSound();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_IRON_GOLEM_STEP, 0.15f, 1.0f);
    }

    @Override
    public AbstractTeam getScoreboardTeam() {
        return super.getScoreboardTeam();
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return false;
    }



    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SITTING, false);
        this.dataTracker.startTracking(MOB_LEVEL, 0);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("isSitting", this.dataTracker.get(SITTING));
        nbt.putInt("Variant", this.getTypeVariant());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(SITTING, nbt.getBoolean("isSitting"));
        this.dataTracker.set(MOB_LEVEL, nbt.getInt("Variant"));
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
                                 @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        MinidoxVariant variant = Util.getRandom(MinidoxVariant.values(), this.random);
        setVariant(variant);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    public MinidoxVariant getVariant() {
        return MinidoxVariant.byId(this.getTypeVariant() & 255);
    }

    public int getTypeVariant() {
        return this.dataTracker.get(MOB_LEVEL);
    }

    public void setVariant(MinidoxVariant variant) {
        this.dataTracker.set(MOB_LEVEL, variant.getId() & 255);
    }

    static {
        SITTING = DataTracker.registerData(MinidoxEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        MOB_LEVEL = DataTracker.registerData(MinidoxEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }
}
