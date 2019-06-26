package io.github.arkery.tickethub.CustomUtils;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

@NonNull @EqualsAndHashCode
public class Clickable {

    private TextComponent textComponent;

    public Clickable(String DisplayText){
        this.textComponent = new TextComponent(DisplayText);
    }

    public Clickable(ChatColor Color, String DisplayText){
        this.textComponent = new TextComponent(DisplayText);
        this.textComponent.setColor(Color);
    }

    public Clickable(ChatColor Color, String DisplayText, String HoverText, HoverEvent.Action HoverAction){
        this.textComponent = new TextComponent(DisplayText);
        this.textComponent.setColor(Color);
        this.textComponent.setHoverEvent(new HoverEvent(HoverAction, new ComponentBuilder(HoverText).create()));
    }

    public Clickable(ChatColor Color, String DisplayText, String Command, ClickEvent.Action ClickAction){
        this.textComponent = new TextComponent(DisplayText);
        this.textComponent.setColor(Color);
        this.textComponent.setClickEvent(new ClickEvent(ClickAction, Command));
    }

    public Clickable(ChatColor Color, String DisplayText, String HoverText, HoverEvent.Action HoverAction,
                     String Command, ClickEvent.Action ClickAction){

        this.textComponent = new TextComponent(DisplayText);
        this.textComponent.setColor(Color);
        this.textComponent.setHoverEvent(new HoverEvent(HoverAction, new ComponentBuilder(HoverText).create()));
        this.textComponent.setClickEvent(new ClickEvent(ClickAction, Command));
    }

    public Clickable add(String text){
        this.textComponent.addExtra(text);
        return this;
    }

    public Clickable add(TextComponent text){
        this.textComponent.addExtra(text);
        return this;
    }

    public TextComponent text(){
        return this.textComponent;
    }


}
