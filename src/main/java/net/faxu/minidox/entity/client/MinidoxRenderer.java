package net.faxu.minidox.entity.client;

import net.faxu.minidox.Minidox;
import net.faxu.minidox.entity.custom.MinidoxEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class MinidoxRenderer extends GeoEntityRenderer<MinidoxEntity> {

    private float rotationYaw;

    public MinidoxRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new MinidoxModel());
        //Shadow below
        this.shadowRadius = 0.4f;
    }

    @Override
    public Identifier getTextureResource(MinidoxEntity entity) {
        return new Identifier(Minidox.MOD_ID, "textures/entity/minidox.png");
    }

    @Override
    public RenderLayer getRenderType(MinidoxEntity animatable, float partialTicks, MatrixStack stack,
                                     @Nullable VertexConsumerProvider renderTypeBuffer,
                                     @Nullable VertexConsumer vertexBuilder, int packedLightIn,
                                     Identifier textureLocation) {
        //Size
        stack.scale(1.2f, 1.2f, 1.2f);
        return super.getRenderType(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
    }

    //For Death Animation
    @Override
    protected void applyRotations(MinidoxEntity entityLiving, MatrixStack PoseStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        if (entityLiving.isDead()) {
            super.applyRotations(entityLiving, PoseStackIn, ageInTicks, this.rotationYaw, partialTicks);
        } else {
            this.rotationYaw = rotationYaw;
            super.applyRotations(entityLiving, PoseStackIn, ageInTicks, rotationYaw, partialTicks);
        }
    }

    //For Death Animation
    @Override
    protected float getDeathMaxRotation(MinidoxEntity entityLivingBaseIn) {
        return 0f;
    }

    @Override
    public void render(GeoModel model, MinidoxEntity animatable, float partialTicks,
                       RenderLayer type, MatrixStack matrixStackIn,
                       @Nullable VertexConsumerProvider renderTypeBuffer, @Nullable VertexConsumer vertexBuilder,
                       int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        renderEarly(animatable, matrixStackIn, partialTicks, renderTypeBuffer, vertexBuilder, packedLightIn,
                packedOverlayIn, red, green, blue, alpha);

        if (renderTypeBuffer != null) {
            vertexBuilder = renderTypeBuffer.getBuffer(type);
        }
        renderRecursively(model.topLevelBones.get(0), matrixStackIn, vertexBuilder, packedLightIn, OverlayTexture.packUv(0,
                OverlayTexture.getV(animatable.hurtTime > 0)), red, green, blue, alpha);
    }
}
