package net.faxu.minidox.entity.client;

import net.faxu.minidox.Minidox;
import net.faxu.minidox.entity.custom.MinidoxEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class MinidoxModel extends AnimatedGeoModel<MinidoxEntity> {
    @Override
    public Identifier getModelResource(MinidoxEntity object) {
        return new Identifier(Minidox.MOD_ID, "geo/minidox.geo.json");
    }

    @Override
    public Identifier getTextureResource(MinidoxEntity object) {
        return MinidoxRenderer.LOCATION_VARIANT.get(object.getVariant());
    }

    @Override
    public Identifier getAnimationResource(MinidoxEntity animatable) {
        return new Identifier(Minidox.MOD_ID, "animations/minidox.animation.json");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void setLivingAnimations(MinidoxEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");

        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
            head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
        }
    }
}
