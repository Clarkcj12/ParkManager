package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;

/**
 * Created by Marc on 3/20/15
 */
@CommandMeta(description = "Open Resource Pack menu")
public class PackCommand extends CoreCommand {

    public PackCommand() {
        super("pack");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        ParkManager.getInstance().getPackManager().openMenu(player);
    }
}