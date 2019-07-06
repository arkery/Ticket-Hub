package io.github.arkery.tickethub.CustomUtils;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

@NonNull @EqualsAndHashCode @NoArgsConstructor
public class Clickable {

    private TextComponent textComponent;

    /**
     * Show only the text
     * 
     * @param DisplayText Text to show
     */
    public Clickable(String DisplayText){
        this.textComponent = new TextComponent(DisplayText);
    }

    /**
     * Show Text with Color
     * 
     * @param Color         Color to display it as
     * @param DisplayText   Text to display
     */
    public Clickable(ChatColor Color, String DisplayText){
        this.textComponent = new TextComponent(DisplayText);
        this.textComponent.setColor(Color);
    }

    /**
     * Text with Color & Only Hover Text
     * 
     * @param Color         Text Color
     * @param DisplayText   Text to display
     * @param HoverText     Text shown upon hover
     */
    public Clickable(ChatColor Color, String DisplayText, String HoverText){
        this.textComponent = new TextComponent(DisplayText);
        this.textComponent.setColor(Color);
        this.textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(HoverText).create()));
    }

    /**
     * Text with Color & Only Click Event Action
     * 
     * @param Color         Text Color
     * @param DisplayText   Text to display
     * @param Command       Command Run or Suggested
     * @param ClickAction   Action upon Click (Suggest or Run)
     */
    public Clickable(ChatColor Color, String DisplayText, String Command, ClickEvent.Action ClickAction){
        this.textComponent = new TextComponent(DisplayText);
        this.textComponent.setColor(Color);
        this.textComponent.setClickEvent(new ClickEvent(ClickAction, Command));
    }

    /**
     * Text with Color & Click Event Action + Hover Text Message
     * 
     * @param gray          Text Color
     * @param DisplayText   Text to display
     * @param HoverText     Text Upon Hover
     * @param Command       Command that is run or suggested
     * @param ClickAction   Action upon Click (Suggest or Run)
     */
    public Clickable(ChatColor gray, String DisplayText, String HoverText, String Command, ClickEvent.Action ClickAction){

        this.textComponent = new TextComponent(DisplayText);
        this.textComponent.setColor(gray);
        this.textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(HoverText).create()));
        this.textComponent.setClickEvent(new ClickEvent(ClickAction, Command));
    }

    /**
     * Add String to TextComponent (Like String's +)
     * 
     * @param text  String to Add
     * @return      This Object 
     */
    public Clickable add(String text){
        this.textComponent.addExtra(text);
        return this;
    }

    /**
     * Add TextComponents to each other
     * 
     * @param text  TextComponent to add
     * @return      This Object
     */
    public Clickable add(TextComponent text){
        this.textComponent.addExtra(text);
        return this;
    }

    /**
     * Add Clickable to this Clickable
     *      
     * @param text  Clickable to add
     * @return      This Object
     */
    public Clickable add(Clickable text){
        this.textComponent.addExtra(text.text());
        return this;
    }

    /**
     * Retrieve this Text component
     * 
     * @return  This object's Text Component
     */
    public TextComponent text(){
        return this.textComponent;
    }


}
