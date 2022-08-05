package com.joshiegemfinder.betweenlandsredstone.entity.minecart;

import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;

public class RailDirectionHelper {

	public static boolean faces(EnumRailDirection direction, EnumFacing facing) {
		switch(facing) {
			case NORTH:
				return facesNorth(direction);
			case SOUTH:
				return facesSouth(direction);
			case EAST:
				return facesEast(direction);
			case WEST:
				return facesWest(direction);
			case UP:
			case DOWN:
				return direction.isAscending();
		}
		return false;
	}
	
	public static boolean facesNorth(EnumRailDirection direction) {
		return direction == EnumRailDirection.NORTH_SOUTH ||
				direction == EnumRailDirection.NORTH_EAST ||
				direction == EnumRailDirection.NORTH_WEST ||
				direction == EnumRailDirection.ASCENDING_NORTH;
	}
	
	public static boolean facesSouth(EnumRailDirection direction) {
		return direction == EnumRailDirection.NORTH_SOUTH ||
				direction == EnumRailDirection.SOUTH_EAST ||
				direction == EnumRailDirection.SOUTH_WEST ||
				direction == EnumRailDirection.ASCENDING_SOUTH;
	}
	
	public static boolean facesEast(EnumRailDirection direction) {
		return direction == EnumRailDirection.EAST_WEST ||
				direction == EnumRailDirection.NORTH_EAST ||
				direction == EnumRailDirection.SOUTH_EAST ||
				direction == EnumRailDirection.ASCENDING_EAST;
	}
	
	public static boolean facesWest(EnumRailDirection direction) {
		return direction == EnumRailDirection.EAST_WEST ||
				direction == EnumRailDirection.NORTH_WEST ||
				direction == EnumRailDirection.SOUTH_WEST ||
				direction == EnumRailDirection.ASCENDING_WEST;
	}
	
	public static EnumFacing matchToRail(EnumRailDirection direction, EnumFacing facing) {
		if(facing.getAxis() == Axis.Y) {
			facing = EnumFacing.NORTH;
		}
		return faces(direction, facing) ? facing : facing.rotateY();
	}
	
	public static boolean isCurved(EnumRailDirection direction) {
		return direction == EnumRailDirection.NORTH_EAST ||
				direction == EnumRailDirection.NORTH_WEST ||
				direction == EnumRailDirection.SOUTH_EAST ||
				direction == EnumRailDirection.SOUTH_WEST;
	}
	
	public static boolean isStraight(EnumRailDirection direction) {
		return !isCurved(direction);
	}
	
	public static EnumFacing followCurve(EnumRailDirection direction, EnumFacing facing) {
		if(isCurved(direction)) {
			//the ifs are inverse because (for example) you head south into a north-east turn
			switch(direction) {
				case NORTH_EAST: {
					if(facing == EnumFacing.SOUTH) {
						return EnumFacing.EAST;
					} else if(facing == EnumFacing.WEST) {
						return EnumFacing.NORTH;
					} else {
						return facing;
					}
				}
				case NORTH_WEST: {
					if(facing == EnumFacing.SOUTH) {
						return EnumFacing.WEST;
					} else if(facing == EnumFacing.EAST) {
						return EnumFacing.NORTH;
					} else {
						return facing;
					}
				}
				case SOUTH_EAST: {
					if(facing == EnumFacing.NORTH) {
						return EnumFacing.EAST;
					} else if(facing == EnumFacing.WEST) {
						return EnumFacing.SOUTH;
					} else {
						return facing;
					}
				}
				case SOUTH_WEST: {
					if(facing == EnumFacing.NORTH) {
						return EnumFacing.WEST;
					} else if(facing == EnumFacing.EAST) {
						return EnumFacing.SOUTH;
					} else {
						return facing;
					}
				}
				default:
					return facing;
			}
		} else {
			return facing;
		}
	}
}
