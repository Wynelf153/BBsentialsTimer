package de.hype.bbsentials.forge.CommandImplementations;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.shared.constants.MiningEvents;
import de.hype.bbsentials.shared.packets.mining.MiningEventPacket;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import static de.hype.bbsentials.client.common.client.BBsentials.connection;


public class CommandGoblinRaid extends CommandBase {

    @Override
    public String getCommandName() {
        return "goblinraid";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/goblinraid";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        try {
            connection.sendPacket(new MiningEventPacket(MiningEvents.GOBLIN_RAID,"", EnvironmentCore.utils.getCurrentIsland()));
        } catch (Exception e) {
            Chat.sendPrivateMessageToSelfError(e.getMessage());
        }    }
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
