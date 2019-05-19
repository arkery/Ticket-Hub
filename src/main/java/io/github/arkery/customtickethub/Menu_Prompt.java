package io.github.arkery.customtickethub;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@NoArgsConstructor
@Setter
@Getter

//Menu for all user actions | User input commands will call this menu - this menu will call the ticket hub.
public class Menu_Prompt{

    private ArrayList<String> customCategories;


    public void showTickets(Player player, String requiredInfo){

    }

    public void editTicket(Player player){

    }

    public void createTicket(Player player){

    }

    public void getHubStatistics(Player player){

    }

}
