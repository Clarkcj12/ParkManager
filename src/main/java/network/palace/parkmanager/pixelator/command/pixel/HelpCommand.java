package network.palace.parkmanager.pixelator.command.pixel;

import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.pixelator.command.CommandDetails;
import network.palace.parkmanager.pixelator.command.ICommand;
import org.bukkit.command.CommandSender;

@CommandDetails(
        name = "help",
        usage = "/pixel help [page]",
        description = "Shows the help pages",
        executableAsConsole = true
)
public class HelpCommand implements ICommand {

    public void execute(ParkManager plugin, CommandSender sender, String[] params) {
        int page = 1;
        if (params.length == 1) {
            try {
                page = Integer.parseInt(params[0]);
                if (!plugin.pixelator.pixelCommandHandler.helpPage.hasPage(sender, page)) {
                    sender.sendMessage("§3[§b§lPixelator§3]§r §cThe help page §6" + page + " §cdoesn\'t exist!");
                    return;
                }
            } catch (Exception var6) {
                sender.sendMessage("§3[§b§lPixelator§3]§r §6" + params[0] + " §cisn\'t numeric!");
                return;
            }
        }

        plugin.pixelator.pixelCommandHandler.helpPage.showPage(sender, page);
    }
}
