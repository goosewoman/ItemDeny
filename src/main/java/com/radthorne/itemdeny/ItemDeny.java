package com.radthorne.itemdeny;

import com.radthorne.itemdeny.armorEquip.ArmorListener;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class ItemDeny extends JavaPlugin
{
    private Map<Material, List<Integer>> denyItems = new HashMap<Material, List<Integer>>();
    private Map<String, Map<Material, List<Integer>>> permissionGroups = new HashMap<String, Map<Material, List<Integer>>>();
    public static ItemDeny instance;
    @Override
    public void onEnable()
    {
        this.saveDefaultConfig();
        loadConfig();
        this.getServer().getPluginManager().registerEvents( new DenyListener( this ), this );
        this.getServer().getPluginManager().registerEvents( new ArmorListener(), this );
        instance = this;
    }

    public boolean canUseItem(Player player, ItemStack stack)
    {
        if(stack == null)
        {
            return true;
        }
        if(!denyItems.containsKey( stack.getType() ))
        {
            return true;
        }
        for(String group : permissionGroups.keySet())
        {
            if(permissionGroups.get( group ).containsKey( stack.getType() ))
            {
                if(permissionGroups.get( group ).get( stack.getType() ).contains( -1 ) || permissionGroups.get( group ).get( stack.getType() ).contains( (int)stack.getDurability() ))
                {
                    if(player.hasPermission( "itemdeny.group."+group.toLowerCase() ))
                    {
                        return true;
                    }
                }
            }
        }
        if(player.hasPermission( "itemdeny.item."+stack.getType().toString().toLowerCase() ))
        {
            return true;
        }
        return false;
    }

    public void loadConfig()
    {
        reloadConfig();
        if ( getConfig().isConfigurationSection( "deny-items" ) )
        {
            Map<String, Object> items = getConfig().getConfigurationSection( "deny-items" ).getValues( false );
            for ( String item : items.keySet() )
            {
                try
                {
                    Material material = Material.getMaterial( item.toUpperCase() );
                    int number = (Integer) items.get( item );
                    if ( denyItems.containsKey( material ) )
                    {
                        List<Integer> ints = denyItems.get( material );
                        if ( !ints.contains( -1 ) )
                        {
                            ints.add( number );
                            denyItems.put( material, ints );
                        }
                        else
                        {
                            List<Integer> negative = new ArrayList<Integer>();
                            negative.add( -1 );
                            denyItems.put( material, negative );
                        }
                    }
                    else
                    {
                        List<Integer> newList = new ArrayList<Integer>();
                        newList.add( number );
                        denyItems.put( material, newList );
                    }
                }
                catch ( Exception ex )
                {
                    getLogger().warning( "Invalid value at: " + item );
                    ex.printStackTrace();
                }
            }
        }
        if ( getConfig().isConfigurationSection( "groups" ) )
        {
            ConfigurationSection groupSection = getConfig().getConfigurationSection( "groups" );
            Map<String, Object> groups = groupSection.getValues( false );
            for ( String group : groups.keySet() )
            {
                if ( groupSection.isConfigurationSection( group ) )
                {
                    Map<String, Object> items = groupSection.getConfigurationSection( group ).getValues( false );
                    Map<Material, List<Integer>> itemMap = new HashMap<Material, List<Integer>>();

                    for ( String item : items.keySet() )
                    {
                        try
                        {
                            Material material = Material.getMaterial( item.toUpperCase() );
                            int number = (Integer) items.get( item );
                            if ( itemMap.containsKey( material ) )
                            {
                                List<Integer> ints = itemMap.get( material );
                                if ( !ints.contains( -1 ) )
                                {
                                    ints.add( number );
                                    itemMap.put( material, ints );
                                }
                                else
                                {
                                    List<Integer> negative = new ArrayList<Integer>();
                                    negative.add( -1 );
                                    itemMap.put( material, negative );
                                }
                            }
                            else
                            {
                                List<Integer> newList = new ArrayList<Integer>();
                                newList.add( number );
                                itemMap.put( material, newList );
                            }
                        }
                        catch ( Exception ex )
                        {
                            getLogger().warning( "Invalid value at: " + item );
                            ex.printStackTrace();
                        }
                    }
                    permissionGroups.put( group.toLowerCase(), itemMap );
                }
            }
        }
    }
}
