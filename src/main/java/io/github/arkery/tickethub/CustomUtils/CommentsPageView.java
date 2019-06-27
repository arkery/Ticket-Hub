package io.github.arkery.tickethub.CustomUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import org.bukkit.entity.Player;

import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatColor;

@NoArgsConstructor
public class CommentsPageView {

    /**
     * Displays list in player friendly page format
     * To be used by other methods
     *
     * @param player         the player who's sending this command
     * @param page           the command input
     * @param displayTickets list to display as
     */
    public void pageView(Player player, int page, List<String> comments) {

        DateFormat dateFormat = new SimpleDateFormat("MM.dd");
        int totalPages = (int) Math.ceil((double) comments.size() / 9);
        int topOfPage = (page - 1) * 9;
        int bottomOfPage = 9 * page - 1;

        if (page > 0 && page <= totalPages) {
            if (comments.size() < topOfPage + 9) {
                bottomOfPage = comments.size();
            }

            //60 characters per line
            for (int i = topOfPage; i < bottomOfPage; i++) {
                player.spigot().sendMessage(new Clickable(ChatColor.GRAY, comments.get(i)).text());
               
            }
        } else {
            player.spigot().sendMessage(new Clickable( ChatColor.RED, "Invalid Page!").text());
        }
    }
}