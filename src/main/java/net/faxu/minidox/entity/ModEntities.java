package net.faxu.minidox.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.faxu.minidox.Minidox;
import net.faxu.minidox.entity.custom.MinidoxEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModEntities {
    public static final EntityType<MinidoxEntity> MINIDOX = Registry.register(
            Registry.ENTITY_TYPE, new Identifier(Minidox.MOD_ID, "minidox"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, MinidoxEntity::new)
                    .dimensions(EntityDimensions.fixed(0.67f, 1.2f)).build());
}
