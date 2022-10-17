package net.faxu.minidox;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.faxu.minidox.entity.ModEntities;
import net.faxu.minidox.entity.custom.MinidoxEntity;
import net.faxu.minidox.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib3.GeckoLib;

public class Minidox implements ModInitializer {
	public static final String MOD_ID = "minidox";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		GeckoLib.initialize();
		FabricDefaultAttributeRegistry.register(ModEntities.MINIDOX, MinidoxEntity.setAttributes());
	}
}
