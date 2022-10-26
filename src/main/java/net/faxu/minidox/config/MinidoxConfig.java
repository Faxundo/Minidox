package net.faxu.minidox.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "minidox")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class MinidoxConfig implements ConfigData {

    public boolean activateSoundIdle = true;
    public boolean activateSoundDeath = true;
    public boolean activateSoundHurt = true;
}
