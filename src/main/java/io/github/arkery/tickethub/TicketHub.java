package io.github.arkery.tickethub;

import io.github.arkery.tickethub.Commands.Commands;
import io.github.arkery.tickethub.CustomUtils.Exceptions.AlreadyExistsException;
import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.TicketSystem.Hub;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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
@NoArgsConstructor
public class TicketHub extends JavaPlugin implements Listener {

    private Hub ticketSystem = new Hub(); 
    private Set<String> customCategories = new HashSet<>();
    private String customCategoriesDisplay = "";
    private final File ticketFolder = new File(this.getDataFolder() + "/Tickets"); 
    private final String ticketFile = "tickets"; //This should not have .json

    @Override
    public void onEnable(){
        System.out.println("Starting Plugin: TicketHub");
        this.LoadConfig();
        this.ticketSystem.loadTickets(this.ticketFile, this.ticketFolder);
        this.dailyMaintenance();
        this.getCommand("th").setExecutor(new Commands(this));
        Bukkit.getPluginManager().registerEvents(this, this);
        this.alreadyOnline();

        for(String i : this.customCategories){
            this.customCategoriesDisplay += " " +  i;
        }
    }

    @Override
    public void onDisable(){
        System.out.println("Stopping Plugin: TicketHub | By Arkery");
        this.saveConfig();
        System.out.println("TicketHub: Saving Tickets");
        this.ticketSystem.saveTickets(ticketFile, this.ticketFolder);
    }

    /**
     * Every day: Check resolved tickets if they are past one week - close them if they are. 
     * Save Tickets and create a backup daily. 
     */
    private void dailyMaintenance(){
        Runnable job = () -> {
            DateFormat saveFormat = new SimpleDateFormat("MMddyyyy");

            this.ticketSystem.saveTickets(this.ticketFile, this.ticketFolder); 
            this.ticketSystem.saveTickets(saveFormat.format(new Date()), new File(this.ticketFolder + "/Backups"));
            this.ticketSystem.checkTickets();
        };
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> scheduledFuture = service.scheduleAtFixedRate(job, 0, 1, TimeUnit.DAYS);
    }

  
    /**
     * Load config. Create default if it doesn't exist
     */
    private void LoadConfig(){
        try{
            File file = new File(getDataFolder(), "config.yml");
            //List<String> categoriesFromConfig = new ArrayList<>();
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

            System.out.println("[TicketHub] Loading Plugin Config");
            
            StringTokenizer st = new StringTokenizer(categories);
            while(st.hasMoreTokens()){
                this.customCategories.add(st.nextToken().toLowerCase());
            }
            //this.customCategories = categoriesFromConfig;

        }catch(NullPointerException e) {
            e.printStackTrace();
            System.out.println("[TicketHub] Unable to create config file");
        }
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent player){

        //<String, Value>
        if(!this.ticketSystem.joinedTheServer(player.getPlayer())){
            try{
                this.ticketSystem.addUser(player.getPlayer().getName(), player.getPlayer().getUniqueId());
            }catch(AlreadyExistsException e){
                System.out.println("playerJoin Event attempting to add player despite player already existing!");
            }
        }else if(this.ticketSystem.joinedTheServer(player.getPlayer())){
            //If they changed their name....
            this.ticketSystem.maybeUpdateUser(player.getPlayer());
        }

        //On join, if they are staff - ping them how many tickets they have assigned to them.
        if(player.getPlayer().hasPermission("tickethub.staff")){

            EnumMap<Options, Object> conditions = new EnumMap<>(Options.class);
            conditions.put(Options.ASSIGNEDTO, player.getPlayer().getUniqueId());
            int assignedTickets = 0; 
            try{
                assignedTickets = this.ticketSystem.filterTickets(conditions).size(); 
            }catch(NullPointerException e){ assignedTickets = 0; }
            player.getPlayer().sendMessage(ChatColor.AQUA + "TicketHub: You have " + assignedTickets + " Tickets Assigned to You");
        }
    }

    /**
     * Auto populates players that are already on the server and adds them to the existing players Bi-Map
     * 
     */
    private void alreadyOnline(){
        for(Player player : Bukkit.getOnlinePlayers()){
            if(!this.ticketSystem.joinedTheServer(player.getPlayer())){
                try{
                    this.ticketSystem.addUser(player.getPlayer().getName(), player.getPlayer().getUniqueId());
                }catch(AlreadyExistsException e){
                    System.out.println("playerJoin Event attempting to add player despite player already existing!");
                }
            }else if(this.ticketSystem.joinedTheServer(player.getPlayer())){
                //If they changed their name....
                this.ticketSystem.maybeUpdateUser(player.getPlayer());
            }
        }
    }
}
