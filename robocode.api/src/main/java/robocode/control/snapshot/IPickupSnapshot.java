/**
 * Copyright (c) 2001-2017 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 */
package robocode.control.snapshot;


/**
 * Interface of a pickup snapshot at a specific time in a battle.
 * 
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 * @author Andreas Stock (contributor)
 *
 */
public interface IPickupSnapshot {

	/**
	 * Returns the pickup state.
	 *
	 * @return the pickup state.
	 */
	PickupState getState();

	/**
	 * Returns the X position of the pickup.
	 *
	 * @return the X position of the pickup.
	 */
	double getX();

	/**
	 * Returns the Y position of the pickup.
	 *
	 * @return the Y position of the pickup.
	 */
	double getY();

	/**
	 * Returns the X painting position of the pickup.
	 * Note that this is not necessarily equal to the X position of the pickup, even though
	 * it will be in most cases. The painting position of the pickup is needed as the pickup
	 * will "stick" to its victim when it has been hit, but only visually. 
	 *
	 * @return the X painting position of the pickup.
	 */
	double getPaintX();

	/**
	 * Returns the Y painting position of the pickup.
	 * Note that this is not necessarily equal to the Y position of the pickup, even though
	 * it will be in most cases. The painting position of the pickup is needed as the pickup
	 * will "stick" to its victim when it has been hit, but only visually. 
	 *
	 * @return the Y painting position of the pickup.
	 */
	double getPaintY();
	
	/**
	 * @return The amount of Energy gained by picking up the pickup
	 */
	double getPickupEnergyBonus();
	
	
	/**
	 * @return The amount of Turns after the picked up pickup is available again.
	 */
	double getPickupRespawnTime();
	

	/**
	 * Returns the color of the pickup.
	 *
	 * @return an ARGB color value. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue)
	 * 
	 * @see java.awt.Color#getRGB()
	 */
	int getColor();

	/**
	 * Returns the current frame number to display, i.e. when the pickup respawns.
	 *
	 * @return the current frame number.
	 */
	int getFrame();

	/**
	 * Returns the ID of the pickup used for identifying the pickup in a collection of pickups.
	 *
	 * @return the ID of the pickup.
	 */
	int getPickupId();

	/**
	 * @return contestantIndex of the victim, or -1 if still in air
	 */
	int getVictimIndex();
}
