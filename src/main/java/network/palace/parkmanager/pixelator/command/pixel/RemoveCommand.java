package network.palace.parkmanager.pixelator.command.pixel;

import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.pixelator.command.CommandDetails;
import network.palace.parkmanager.pixelator.command.ICommand;
import network.palace.parkmanager.pixelator.renderer.types.MapImageRenderer;
import org.bukkit.command.CommandSender;

@CommandDetails(
        name = "remove",
        usage = "/pixel remove <id>",
        description = "Removes an image map",
        executableAsConsole = true,
        permission = "Pixelator.remove"
)
public class RemoveCommand implements ICommand {

    public void execute(ParkManager plugin, CommandSender sender, String[] params) {
        short id;
        try {
            id = Short.parseShort(params[0]);
        } catch (Exception var6) {
            sender.sendMessage("§3[§b§lPixelator§3]§r §6" + params[0] + " §cisn\'t numeric!");
            return;
        }

        MapImageRenderer m = plugin.pixelator.rendererManager.getRenderer(id);
        if (m == null) {
            sender.sendMessage("§3[§b§lPixelator§3]§r §cThere\'s no image map with this id!");
        } else {
            plugin.pixelator.rendererManager.unregister(m);
            sender.sendMessage("§3[§b§lPixelator§3]§r §aThe image map with id §6" + id + " §awas removed!");
        }
    }
}
