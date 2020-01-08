package com.mrcrayfish.vehicle.client.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.HeldVehicleEvents;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.common.CommonEvents;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class LayerHeldVehicle extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>
{
    private EntityType<VehicleEntity> cachedType = null;
    private VehicleEntity cachedEntity = null;

    public LayerHeldVehicle(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> renderer)
    {
        super(renderer);
    }

    @Override
    public void func_225628_a_(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int i, AbstractClientPlayerEntity playerEntity, float v, float v1, float partialTicks, float v3, float v4, float v5)
    {
        CompoundNBT tagCompound = playerEntity.getDataManager().get(CommonEvents.HELD_VEHICLE);
        if(!tagCompound.isEmpty())
        {
            Optional<EntityType<?>> optional = EntityType.byKey(tagCompound.getString("id"));
            if(optional.isPresent())
            {
                EntityType<?> entityType = optional.get();
                Entity entity = entityType.create(playerEntity.world);
                if(entity instanceof VehicleEntity)
                {
                    entity.read(tagCompound);
                    entity.getDataManager().getAll().forEach(dataEntry -> entity.notifyDataManagerChange(dataEntry.getKey()));
                    this.cachedType = (EntityType<VehicleEntity>) entityType;
                    this.cachedEntity = (VehicleEntity) entity;
                }
            }
            if(this.cachedEntity != null && this.cachedType != null)
            {
                matrixStack.func_227860_a_();
                {
                    HeldVehicleEvents.AnimationCounter counter = HeldVehicleEvents.idToCounter.get(playerEntity.getUniqueID());
                    if(counter != null)
                    {
                        float width = this.cachedEntity.getWidth() / 2;
                        matrixStack.func_227861_a_(0F, 1F - 1F * counter.getProgress(partialTicks), -0.5F * Math.sin(Math.PI * counter.getProgress(partialTicks)) - width * (1.0F - counter.getProgress(partialTicks)));
                    }
                    Vec3d heldOffset = this.cachedEntity.getProperties().getHeldOffset();
                    matrixStack.func_227861_a_(heldOffset.x * 0.0625D, heldOffset.y * 0.0625D, heldOffset.z * 0.0625D);
                    matrixStack.func_227863_a_(Axis.POSITIVE_X.func_229187_a_(180F));
                    matrixStack.func_227863_a_(Axis.POSITIVE_Y.func_229187_a_(-90F));
                    matrixStack.func_227861_a_(0F, playerEntity.isCrouching() ? 0.3125F : 0.5625F, 0F);
                    EntityRenderer<VehicleEntity> render = (EntityRenderer<VehicleEntity>) Minecraft.getInstance().getRenderManager().renderers.get(this.cachedType);
                    render.func_225623_a_(this.cachedEntity, 0.0F, 0.0F, matrixStack, renderTypeBuffer, i);
                }
                matrixStack.func_227865_b_();
            }
        }
        else
        {
            this.cachedType = null;
            this.cachedEntity = null;
        }
    }
}
