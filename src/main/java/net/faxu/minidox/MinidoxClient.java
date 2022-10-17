package net.faxu.minidox;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.faxu.minidox.entity.ModEntities;
import net.faxu.minidox.entity.client.MinidoxRenderer;

public class MinidoxClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.MINIDOX, MinidoxRenderer::new);
    }
}
