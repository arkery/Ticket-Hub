package io.github.arkery.tickethub.OldCommands.FilterTicketsConv;

import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.TicketHub;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.EnumMap;

@AllArgsConstructor
public class dateupdatedFilter extends StringPrompt {

    private TicketHub plugin;
    private EnumMap<Options, Object> filterConditions;
    private static final DateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy");

    @Override
    public String getPromptText(ConversationContext conv) {

        return ChatColor.AQUA + "Enter the Ticket Date Updated in the format of MM.DD.YYYY";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {

        try{
            filterConditions.put(Options.DATEUPDATED, dateFormat.parse(answer));
            return new OptionForMoreConditions(this.plugin, this.filterConditions);
        }catch(ParseException e){
            conv.getForWhom().sendRawMessage(ChatColor.RED + "Invalid Date Format");
            return this;
        }
    }
}
