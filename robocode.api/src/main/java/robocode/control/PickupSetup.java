/**
 * Copyright (c) 2001-2017 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 */
package robocode.control;

import robocode.Rules;

/**
 * Contains the initial position for a Pickup.
 *
 * @author see RobotSetup (original)
 * @author Andreas Stock
 * 
 * @since 1.9.3.2
 */
public class PickupSetup implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private final Double x;
	private final Double y;

	private double energygain = Rules.PICKUP_ENERGY_BONUS;

	private double respawntime = Rules.PICKUP_RESPAWN_TIME;

	/**
	 * Constructs a new PickupSetup.
	 *
	 * @param x is the x coordinate, where {@code null} means random.
	 * @param y is the y coordinate, where {@code null} means random.
	 */
	public PickupSetup(Double x, Double y) {
		this.x = x;
		this.y = y;
	}

	public PickupSetup(double x2, double y2, double energygain, double respawntime) {
		this(x2,y2);
		this.energygain = energygain;
		this.respawntime = respawntime;
	}

	/**
	 * Returns the x coordinate.
	 * @return the x coordinate, where {@code null} means unspecified (random).
	 */
	public Double getX() {
		return x;
	}

	/**
	 * Returns the y coordinate.
	 * @return the y coordinate, where {@code null} means unspecified (random).
	 */
	public Double getY() {
		return y;
	}

	public double getEnergygain() {
		return energygain;
	}

	public double getRespawntime() {
		return respawntime;
	}

	

}
