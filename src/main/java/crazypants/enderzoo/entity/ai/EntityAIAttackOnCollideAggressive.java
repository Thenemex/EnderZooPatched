package crazypants.enderzoo.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIAttackOnCollideAggressive extends EntityAIBase {

  final World worldObj;
  final EntityCreature attacker;

  int ticksToNextAttack;
  final double speedTowardsTarget;
  final boolean longMemory;
  PathEntity entityPathEntity;
  Class<?> classTarget;
  private int ticksUntilNextPathingAttempt;
  private double targetX;
  private double targetY;
  private double targetZ;

//  private int failedPathFindingPenalty;

  private int attackFrequency = 20;

  public EntityAIAttackOnCollideAggressive(EntityCreature attacker, Class<?> targetClass, double attackSpeed, boolean longMemory) {
    this(attacker, attackSpeed, longMemory);
    classTarget = targetClass;
  }

  public EntityAIAttackOnCollideAggressive(EntityCreature attacker, double attackSpeed, boolean longMemory) {
    this.attacker = attacker;
    worldObj = attacker.worldObj;
    speedTowardsTarget = attackSpeed;
    this.longMemory = longMemory;
    setMutexBits(3);
  }

  public int getAttackFrequency() {
    return attackFrequency;
  }

  public EntityAIAttackOnCollideAggressive setAttackFrequency(int attackFrequency) {
    this.attackFrequency = attackFrequency;
    return this;
  }

  /**
   * Returns whether the EntityAIBase should begin execution.
   */
  @Override
  public boolean shouldExecute() {
    EntityLivingBase entitylivingbase = attacker.getAttackTarget();

    if (entitylivingbase == null) {
      return false;
    } else if (!entitylivingbase.isEntityAlive()) {
      return false;
    } else if (classTarget != null && !classTarget.isAssignableFrom(entitylivingbase.getClass())) {
      return false;
    } else {
      if (--ticksUntilNextPathingAttempt <= 0) {
        entityPathEntity = attacker.getNavigator().getPathToEntityLiving(entitylivingbase);
        ticksUntilNextPathingAttempt = 4 + attacker.getRNG().nextInt(7);
        return entityPathEntity != null;
      } else {
        return true;
      }
    }
  }

  /**
   * Returns whether an in-progress EntityAIBase should continue executing
   */
  @Override
  public boolean continueExecuting() {
    EntityLivingBase entitylivingbase = attacker.getAttackTarget();
    return entitylivingbase != null && (entitylivingbase.isEntityAlive() && (!longMemory ? !attacker.getNavigator().noPath() : attacker
            .isWithinHomeDistance(MathHelper.floor_double(entitylivingbase.posX), MathHelper.floor_double(entitylivingbase.posY),
                    MathHelper.floor_double(entitylivingbase.posZ))));
  }

  @Override
  public void startExecuting() {
    attacker.getNavigator().setPath(entityPathEntity, speedTowardsTarget);
    ticksUntilNextPathingAttempt = 0;
  }

  @Override
  public void resetTask() {
    attacker.getNavigator().clearPathEntity();
  }

  /**
   * Updates the task
   */
  @Override
  public void updateTask() {

    EntityLivingBase entitylivingbase = attacker.getAttackTarget();
    attacker.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
    double distToTargetSq = attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.boundingBox.minY, entitylivingbase.posZ);
    double attachRange = attacker.width * 2.0F * attacker.width * 2.0F + entitylivingbase.width;
    --ticksUntilNextPathingAttempt;

    if ((longMemory || attacker.getEntitySenses().canSee(entitylivingbase))
        && ticksUntilNextPathingAttempt <= 0
        && (targetX == 0.0D && targetY == 0.0D && targetZ == 0.0D || entitylivingbase.getDistanceSq(targetX, targetY, targetZ) >= 1.0D || attacker.getRNG()
            .nextFloat() < 0.05F)) {

      targetX = entitylivingbase.posX;
      targetY = entitylivingbase.boundingBox.minY;
      targetZ = entitylivingbase.posZ;

        if (distToTargetSq > 1024.0D) {
        ticksUntilNextPathingAttempt += 10;
      } else if (distToTargetSq > 256.0D) {
        ticksUntilNextPathingAttempt += 5;
      }

      if (!attacker.getNavigator().tryMoveToEntityLiving(entitylivingbase, speedTowardsTarget)) {
        ticksUntilNextPathingAttempt += 15;
      }
    }

    ticksToNextAttack = Math.max(ticksToNextAttack - 1, 0);

    if (distToTargetSq <= attachRange && ticksToNextAttack <= 20) {
      ticksToNextAttack = attackFrequency;
      if (attacker.getHeldItem() != null) {
        attacker.swingItem();
      }
      attacker.attackEntityAsMob(entitylivingbase);
    }
  }
}
