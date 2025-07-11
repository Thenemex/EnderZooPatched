package crazypants.enderzoo.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Adapted from public domain code in Tinkers Construct, CustomBowRenderer.java
 * <a href="https://github.com/SlimeKnights/TinkersConstruct/blob/master/src/main/java/">...</a>
 * tconstruct/client/CustomBowRenderer.java
 */
@SideOnly(Side.CLIENT)
public class GuardiansBowRenderer implements IItemRenderer {

  private final Minecraft mc = Minecraft.getMinecraft();

  @Override
  public boolean handleRenderType(ItemStack item, ItemRenderType type) {
    return type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
  }

  @Override
  public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
    return true;
  }

  @Override
  public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
    EntityLivingBase living = (EntityLivingBase) data[1];    
    for (int i = 0; i < item.getItem().getRenderPasses(item.getItemDamage()) + 1; i++) {
      renderItem(living, item, i, type);
    }
  }

  public void renderItem(EntityLivingBase living, ItemStack stack, int renderPass, ItemRenderType type) {

    GL11.glPushMatrix();

    IIcon icon;
    if (living instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) living;
      if (player.getItemInUse() != null) {
        icon = stack.getItem().getIcon(stack, renderPass, player, player.getItemInUse(), player.getItemInUseCount());
      } else {
        icon = living.getItemIcon(stack, renderPass);
      }
    } else {
      icon = living.getItemIcon(stack, renderPass);
    }

    if (icon == null) {
      GL11.glPopMatrix();
      return;
    }

    TextureManager texturemanager = mc.getTextureManager();
    texturemanager.bindTexture(texturemanager.getResourceLocation(stack.getItemSpriteNumber()));

    if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
      GL11.glTranslatef(0.6F, 0.5F, 0.5F);
    } else {
      GL11.glRotatef(180.0F, 0F, 0F, 1.0F);
      GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.75F);
      GL11.glTranslatef(-0.6F, -0.25F, 1.0F);
      GL11.glScalef(1.75F, 1.75F, 1.75F);
    }

    Tessellator tessellator = Tessellator.instance;
    float f = icon.getMinU();
    float f1 = icon.getMaxU();
    float f2 = icon.getMinV();
    float f3 = icon.getMaxV();
    float f4 = 0.0F;
    float f5 = 0.3F;
    GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    GL11.glTranslatef(-f4, -f5, 0.0F);
    float f6 = 1.5F;
    GL11.glScalef(f6, f6, f6);
    GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
    GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
    GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
    ItemRenderer.renderItemIn2D(tessellator, f1, f2, f, f3, icon.getIconWidth(), icon.getIconHeight(), 0.0625F);

    GL11.glDisable(GL12.GL_RESCALE_NORMAL);

    GL11.glPopMatrix();
  }

}
