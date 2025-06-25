package crazypants.enderzoo.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import crazypants.enderzoo.entity.IOwnable;

public class EntityAIFollowOwner extends EntityAIBase {

  /** The child that is following its parent. */
  final IOwnable<? extends EntityCreature, ? extends EntityLivingBase> owned;
  final double followSpeed;
  private int pathingTimer;

  private final double minDistanceSq;
  private final double maxDistanceSq;

  public EntityAIFollowOwner(IOwnable<? extends EntityCreature, ? extends EntityLivingBase> owned, double minDist, double maxDist, double followSpeed) {
    this.owned = owned;
    minDistanceSq = minDist * minDist;
    maxDistanceSq = maxDist * maxDist;
    this.followSpeed = followSpeed;
  }

  @Override
  public boolean shouldExecute() {
    if (owned.getOwner() == null) {
      return false;
    }
    return getDistanceSqFromOwner() > maxDistanceSq;
  }

  @Override
  public boolean continueExecuting() {
    EntityLivingBase owner = owned.getOwner();
    if (owner == null || !owner.isEntityAlive()) {
      return false;
    }
    return !owned.asEntity().getNavigator().noPath();
  }

  public boolean isWithinTargetDistanceFromOwner() {
    if (owned.getOwner() == null) {
      return true;
    }
    double distance = getDistanceSqFromOwner();
    return distance >= minDistanceSq && distance <= maxDistanceSq;
  }

  private double getDistanceSqFromOwner() {
      return owned.asEntity().getDistanceSqToEntity(owned.getOwner());
  }

  @Override
  public void startExecuting() {
    pathingTimer = 0;
  }

    @Override
  public void updateTask() {
    EntityLivingBase owner = owned.getOwner();
    if (owner == null) {
      return;
    }
    double distance = getDistanceSqFromOwner();
    if (distance < minDistanceSq) {
      owned.asEntity().getNavigator().clearPathEntity();
    }
    if (--pathingTimer <= 0) {
      pathingTimer = 10;
      owned.asEntity().getNavigator().tryMoveToEntityLiving(owned.getOwner(), followSpeed);
    }
  }
}