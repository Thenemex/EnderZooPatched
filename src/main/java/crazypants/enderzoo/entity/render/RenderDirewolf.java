package crazypants.enderzoo.entity.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import crazypants.enderzoo.entity.EntityDireWolf;

public class RenderDirewolf extends RenderLiving {

  //private static final ResourceLocation wolfTextures = new ResourceLocation("enderzoo:entity/dire_wolf.png");
  private final ResourceLocation wolfTextures = new ResourceLocation("enderzoo:entity/dire_wolf.png");

  private int debugCounter = 0;

  public RenderDirewolf() {
    super(new ModelDireWolf(), 0.5f);
    //setRenderPassModel(renderpassModel);
  }

  protected float handleRotationFloat(EntityDireWolf wolf, float p_77044_2_) {
    return wolf.getTailRotation();
  }

  @Override
  protected void preRenderCallback(EntityLivingBase entity, float partialTick) {

    if (debugCounter == 4) {
      System.out.println("RenderDirewolf.preRenderCallback: ");
      mainModel = new ModelDireWolf();
      debugCounter++;
    }

    float scale = 1.25f;
    GL11.glPushMatrix();
    GL11.glTranslatef(0.1f, 0, 0);
    GL11.glScalef(scale - 0.1f, scale, scale);
  }

    @Override
  protected float handleRotationFloat(EntityLivingBase p_77044_1_, float p_77044_2_) {
    return this.handleRotationFloat((EntityDireWolf) p_77044_1_, p_77044_2_);
  }

  @Override
  public void doRender(Entity entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {
    super.doRender(entity, x, y, z, p_76986_8_, p_76986_9_);
    GL11.glPopMatrix();
    //RenderUtil.renderEntityBoundingBox((EntityLiving) entity, x, y, z);

  }

  @Override
  protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
    return wolfTextures;
  }

}
