package pers.gwyog.customneiplugins.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import pers.gwyog.customneiplugins.network.CNPNetwork;
import pers.gwyog.customneiplugins.network.PacketClientCommand;

public class CommandCNP extends CommandBase {
    private List aliases;
    
    public CommandCNP() {
        this.aliases = new ArrayList<String>();
        this.aliases.add("customneiplugins");
        this.aliases.add("cnp");
    }
    
    @Override
    public String getCommandName() {
        return "cnp";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/cnp <reload|status>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0)
            sender.addChatMessage(new ChatComponentText("/cnp <reload|status>"));
        else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload"))
                CNPNetwork.INSTANCE.sendTo(new PacketClientCommand(0), (EntityPlayerMP) sender);
            else if (args[0].equalsIgnoreCase("status"))
                CNPNetwork.INSTANCE.sendTo(new PacketClientCommand(1), (EntityPlayerMP) sender);
            else
                sender.addChatMessage(new ChatComponentTranslation("customneiplugins.command.invalid.info"));
        }
        else
            sender.addChatMessage(new ChatComponentTranslation("customneiplugins.command.invalid.info"));
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length != 1? null: getListOfStringsMatchingLastWord(args, new String[]{"reload", "status"});
    }
    
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
    
    @Override
    public List getCommandAliases() {
        return this.aliases;
    }
    
}
