package com.gamingmesh.jobs.listeners;

import java.util.List;

import com.gamingmesh.jobs.config.BlockProtectionManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.stuff.Util;

public class PistonProtectionListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockMove(BlockPistonExtendEvent event) {
	if (!Jobs.getGCManager().useBlockProtection || !Jobs.getGCManager().canPerformActionInWorld(event.getBlock().getWorld()))
	    return;

	BlockFace dir = event.getDirection();

	for (int i = event.getBlocks().size() - 1; i >= 0; i--) {
	    Location oldLoc = event.getBlocks().get(i).getLocation();
	    Location newLoc = oldLoc.clone().add(dir.getModX(), dir.getModY(), dir.getModZ());

	    relocateProtections(oldLoc, newLoc);
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockRetractMove(BlockPistonRetractEvent event) {
	if (!Jobs.getGCManager().useBlockProtection || !Jobs.getGCManager().canPerformActionInWorld(event.getBlock().getWorld()))
	    return;

	BlockFace dir = event.getDirection();

	List<Block> blocks = Util.getPistonRetractBlocks(event);
	for (int i = blocks.size() - 1; i >= 0; i--) {
	    Location oldLoc = blocks.get(i).getLocation();
	    Location newLoc = oldLoc.clone().add(dir.getModX(), dir.getModY(), dir.getModZ());

	    relocateProtections(oldLoc, newLoc);
	}
    }

    private static void relocateProtections(Location oldLocation, Location newLocation) {
	BlockProtectionManager.forActionType(actionType -> {
	    Long bp = Jobs.getBpManager().getTime(actionType, oldLocation);
	    if (bp == null)
		return;

	    Jobs.getBpManager().addP(actionType, newLocation, bp, false, true);
	});
    }
}
