package com.thepaperraven.listeners;

import com.thepaperraven.ai.Vault;
import com.thepaperraven.ai.gui.VaultInventory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class VaultInventoryListener implements Listener {

    private Plugin plugin;

    public VaultInventoryListener(Plugin plugin){

        this.plugin = plugin;
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInv = event.getClickedInventory();
        Inventory destInv = event.getView().getBottomInventory();
        if (clickedInv == null || destInv == null) {
            return;
        }
        if (clickedInv.getType() != InventoryType.PLAYER) {
            return;
        }

        Vault vault = Vault.getVaultFromInventory(destInv);
        if (vault == null){
            return;
        }
        VaultInventory vaultInv = vault.getVaultInventory();
        Player player = (Player) event.getWhoClicked();
        if (!vaultInv.getVault().getMetadata().isOwner(player) && !player.isOp() && vault.isLocked()) {
            event.setCancelled(true);
            return;
        }
        ItemStack item = event.getCurrentItem();
        if (item != null && item.getType() != vaultInv.getVault().getMetadata().getAllowedMaterial()) {
            event.setCancelled(true);
        }


        //Sync methods!
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Vault vault = Vault.getVaultFromInventory(event.getInventory());

        if (vault != null && ((!vault.getMetadata().isOwner(player)||player.isOp())|| !vault.isLocked())) {
            for (HumanEntity viewer : event.getInventory().getViewers()) {
                if (viewer instanceof Player) {
                    viewer.sendMessage("You have been kicked from this vault because you are not the owner.");
                }
            }

            vault.getVaultInventory().getInventory().getViewers().remove(player);
        }

        //Sync chests
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        Vault vault = Vault.getVaultFromInventory(event.getInventory());

        if (vault != null && ((!vault.getMetadata().isOwner(player)||player.isOp())|| !vault.isLocked())) {
            event.setCancelled(true);
        }


    }
}
