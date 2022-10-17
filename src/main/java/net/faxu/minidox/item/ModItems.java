package net.faxu.minidox.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.faxu.minidox.Minidox;
import net.faxu.minidox.entity.ModEntities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {
    public static final Item MINIDOX_SPAWN_EGG = registerItem("minidox_spawn_egg",
            new SpawnEggItem(ModEntities.MINIDOX, 4138260, 16430680,
                    new FabricItemSettings().group(ItemGroup.MISC)));

    //Register new Item
    private static Item registerItem (String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(Minidox.MOD_ID, name), item);
    }
    //Message for the debug view
    public static void registerModItems() {
        Minidox.LOGGER.debug("Registering Mod Items for " + Minidox.MOD_ID);
    }
}
