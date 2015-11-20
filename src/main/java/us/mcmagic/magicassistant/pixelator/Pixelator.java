package us.mcmagic.magicassistant.pixelator;

import org.bukkit.Bukkit;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.pixelator.command.pixel.PixelCommandHandler;
import us.mcmagic.magicassistant.pixelator.renderer.RendererManager;

public class Pixelator {
    public static final String PREFIX = "§3[§b§lPixelator§3]§r ";
    private static Pixelator instance;
    public RendererManager rendererManager;
    public PixelCommandHandler pixelCommandHandler;

    public Pixelator() {
        long check = System.currentTimeMillis();
        instance = this;
        rendererManager = new RendererManager(MagicAssistant.getInstance());
        pixelCommandHandler = new PixelCommandHandler(MagicAssistant.getInstance());
        check = System.currentTimeMillis() - check;
        Bukkit.getLogger().info("Pixelator activated! (" + check + " ms)");
    }

    public static Pixelator getInstance() {
        return instance;
    }
}
