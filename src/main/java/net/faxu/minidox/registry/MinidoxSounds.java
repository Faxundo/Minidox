package net.faxu.minidox.registry;

import net.faxu.minidox.Minidox;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class MinidoxSounds {
    public static SoundEvent MINIDOX_DEATH = new SoundEvent(new Identifier(Minidox.MOD_ID, "minidox.death"));
    public static SoundEvent MINIDOX_ATTACK = new SoundEvent(new Identifier(Minidox.MOD_ID, "minidox.attack"));
    public static SoundEvent MINIDOX_HURT = new SoundEvent(new Identifier(Minidox.MOD_ID, "minidox.hurt"));
    public static SoundEvent MINIDOX_IDLE = new SoundEvent(new Identifier(Minidox.MOD_ID, "minidox.idle"));

    public static void registerSounds() {
        Registry.register(Registry.SOUND_EVENT, new Identifier(Minidox.MOD_ID, "minidox.death"), MINIDOX_DEATH);
        Registry.register(Registry.SOUND_EVENT, new Identifier(Minidox.MOD_ID, "minidox.attack"), MINIDOX_ATTACK);
        Registry.register(Registry.SOUND_EVENT, new Identifier(Minidox.MOD_ID, "minidox.hurt"), MINIDOX_HURT);
        Registry.register(Registry.SOUND_EVENT, new Identifier(Minidox.MOD_ID, "minidox.idle"), MINIDOX_IDLE);
    }
}
