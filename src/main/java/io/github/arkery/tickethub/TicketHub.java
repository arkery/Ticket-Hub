package io.github.arkery.tickethub;

import io.github.arkery.tickethub.Commands.Commands;
import io.github.arkery.tickethub.CustomUtils.Exceptions.AlreadyExistsException;
import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.TicketSystem.Hub;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class TicketHub extends JavaPlugin implements Listener {

    private Hub TicketSystem;
    private List<String> customCategories;

    @Override
    public void onEnable(){
        System.out.println("Starting Plugin: TicketHub | By Arkery");
        this.TicketSystem = new Hub(this.getDataFolder());
        this.customCategories = new ArrayList<>();
        this.createOrLoadConfig();
        this.TicketSystem.loadTickets();
        this.dailyMaintenance();
        this.getCommand("th").setExecutor(new Commands(this));
        //this.getCommand("ticketsystm").setExecutor(new Commands(this));
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable(){
        System.out.println("Stopping Plugin: TicketHub | By Arkery");
        this.saveConfig();
        System.out.println("TicketHub: Saving Tickets");
        this.TicketSystem.saveTickets("");
    }

    /*
    Every day:
    - Check Resolved Tickets if they are past a week - if they are, delete them
    - Save all the tickets
    - Create a backup of all the tickets in the format of BackupMMddyyyy
     */
    public void dailyMaintenance(){
        Runnable job = () -> {
            DateFormat saveFormat = new SimpleDateFormat("MMddyyyy");

            this.TicketSystem.saveTickets("");
            this.TicketSystem.saveTickets("Backup" + saveFormat.format(new Date()));
            this.TicketSystem.checkTickets();
        };
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> scheduledFuture = service.scheduleAtFixedRate(job, 0, 1, TimeUnit.DAYS);
    }

    /*
    Creates default config file on initial startup with three categories: category1, category2 and category3
    If there is pre-existing config - load it into customCategories;
     */
    public void createOrLoadConfig(){
        try{
            File file = new File(getDataFolder(), "config.yml");
            List<String> categoriesFromConfig = new ArrayList<>();

            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            if (!file.exists()) {
                System.out.println("Creating Plugin Config");
                saveDefaultConfig();
                this.getConfig().set("categories", "category1 category2 category3");
                saveConfig();

            }

            String categories = this.getConfig().getString("categories");

            if(categories.equals("") || categories.isEmpty()){
                this.getConfig().set("categories", "category1 category2 category3");
            }

            System.out.println("TicketHub: Loading Plugin Config");
            StringTokenizer st = new StringTokenizer(categories);

            while(st.hasMoreTokens()){
                categoriesFromConfig.add(st.nextToken().toLowerCase());
            }
            this.customCategories = categoriesFromConfig;

        }catch(NullPointerException e) {
            e.printStackTrace();
            getLogger().info("Unable to generate Config");
        }
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent player){

        //<String, Value>
        if(!this.TicketSystem.joinedTheServer(player.getPlayer())){
            try{
                this.TicketSystem.addUser(player.getPlayer().getName(), player.getPlayer().getUniqueId());
            }catch(AlreadyExistsException e){
                System.out.println("playerJoin Event attempting to add player despite player already existing!");
            }
        }else if(this.TicketSystem.joinedTheServer(player.getPlayer())){
            //If they changed their name....
            this.TicketSystem.maybeUpdateUser(player.getPlayer());
        }


        //On join, if they are staff - ping them how many tickets they have assigned to them.
        if(player.getPlayer().hasPermission("tickethub.staff")){

            EnumMap<Options, Object> conditions = new EnumMap<>(Options.class);
            conditions.put(Options.ASSIGNEDTO, player.getPlayer().getUniqueId());
            int assignedTickets = this.TicketSystem.filterTickets(conditions).size();

            if( this.TicketSystem.filterTickets(conditions).isEmpty()){
                assignedTickets = 0;
            }

            player.getPlayer().sendMessage(ChatColor.AQUA + "TicketHub: You have " + assignedTickets + " Tickets Assigned to You");
        }
    }
}
