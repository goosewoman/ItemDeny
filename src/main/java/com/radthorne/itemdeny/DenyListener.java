package com.radthorne.itemdeny;

import com.radthorne.itemdeny.armorEquip.ArmorEquipEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/*
 * Copyright 2015 Luuk Jacobs

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class DenyListener implements Listener
{
    private ItemDeny plugin;

    public DenyListener( ItemDeny itemDeny )
    {
        this.plugin = itemDeny;
    }

    @EventHandler( priority = EventPriority.HIGH )
    public void onPlayerUse( PlayerInteractEvent event )
    {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();
        if ( !plugin.canUseItem( player, stack ) )
        {
            PlayerInventory inventory = player.getInventory();
            int newslot;
            if ( inventory.getHeldItemSlot() > 0 )
            {
                newslot = inventory.getHeldItemSlot() - 1;
            }
            else
            {
                newslot = 1;
            }
            inventory.setHeldItemSlot( newslot );
            event.setCancelled( true );
            player.sendMessage( ChatColor.translateAlternateColorCodes( '&', plugin.getConfig().getString( "deny-message" ) ) );
            player.updateInventory();
        }
    }

    @EventHandler( priority = EventPriority.HIGH )
    public void onHeldItemChange( PlayerItemHeldEvent event )
    {
        if ( !plugin.canUseItem( event.getPlayer(), event.getPlayer().getInventory().getItem( event.getNewSlot() ) ) )
        {
            event.setCancelled( true );
            event.getPlayer().sendMessage( ChatColor.translateAlternateColorCodes( '&', plugin.getConfig().getString( "deny-message" ) ) );
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler( priority = EventPriority.HIGH )
    public void onDamageEntity( EntityDamageByEntityEvent event )
    {
        if ( event.getDamager() instanceof Player )
        {
            Player player = (Player) event.getDamager();
            if ( !plugin.canUseItem( player, player.getItemInHand() ) )
            {
                event.setCancelled( true );
            }
        }
    }

    @EventHandler( priority = EventPriority.HIGH )
    public void onEquip( ArmorEquipEvent event )
    {
        Player player = event.getPlayer();
        ItemStack stack = event.getNewArmorPiece();
        if ( event.getMethod() == ArmorEquipEvent.EquipMethod.DRAG || event.getMethod() == ArmorEquipEvent.EquipMethod.SHIFT_CLICK || event.getMethod() == ArmorEquipEvent.EquipMethod.HOTBAR )
        {
            if ( !plugin.canUseItem( player, stack ) )
            {
                event.setCancelled( true );
                event.getPlayer().sendMessage( ChatColor.translateAlternateColorCodes( '&', plugin.getConfig().getString( "deny-message" ) ) );
                event.getPlayer().updateInventory();
            }
        }
    }


    // If someone somehow still manages to wear armour without having permissions, DROP EM ON THE FLOOR.
    @EventHandler
    public void onInventoryClick( InventoryCloseEvent event )
    {
        if ( event.getPlayer() instanceof Player )
        {
            Player player = (Player) event.getPlayer();
            if ( !plugin.canUseItem( player, player.getInventory().getHelmet() ) )
            {
                player.getWorld().dropItem( player.getLocation(), player.getInventory().getHelmet() );
                player.getInventory().setHelmet( null );
                player.sendMessage( ChatColor.translateAlternateColorCodes( '&', plugin.getConfig().getString( "deny-message" ) ) );
                player.updateInventory();
            }
            if ( !plugin.canUseItem( player, player.getInventory().getChestplate() ) )
            {
                player.getWorld().dropItem( player.getLocation(), player.getInventory().getChestplate() );
                player.getInventory().setChestplate( null );
                player.sendMessage( ChatColor.translateAlternateColorCodes( '&', plugin.getConfig().getString( "deny-message" ) ) );
                player.updateInventory();
            }
            if ( !plugin.canUseItem( player, player.getInventory().getLeggings() ) )
            {
                player.getWorld().dropItem( player.getLocation(), player.getInventory().getLeggings() );
                player.getInventory().setLeggings( null );
                player.sendMessage( ChatColor.translateAlternateColorCodes( '&', plugin.getConfig().getString( "deny-message" ) ) );
                player.updateInventory();
            }
            if ( !plugin.canUseItem( player, player.getInventory().getBoots() ) )
            {
                player.getWorld().dropItem( player.getLocation(), player.getInventory().getBoots() );
                player.getInventory().setBoots( null );
                player.sendMessage( ChatColor.translateAlternateColorCodes( '&', plugin.getConfig().getString( "deny-message" ) ) );
                player.updateInventory();
            }
        }
    }
}
