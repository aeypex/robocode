/**
 * Copyright (c) 2001-2017 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 */
package robocode.control.snapshot;


/**
 * Defines a powerup state, which can be: just spawned, available, hitting a victim, unavailable or inactive.
 *
 * @author Andreas Stock
 */
public enum PowerupState {

	/** The Powerup has just been spawned this turn and hence just been created. This state only last one turn. */
	SPAWNED(0),

	/** The powerup is now available on the battlefield, but has not hit anything yet. */
	AVAILABLE(1),

	/** The Powerup was hit by a robot victim. */
	HIT_VICTIM(2),

	/** The Powerup was picked up and is currently waiting to be respawned.  */
	UNAVAILABLE(3),

	/** The Powerup is currently inactive. Hence, it is not active or visible on the battlefield. */
	INACTIVE(4),
	
	/** The Powerup is ok to be removed and cleaned up. */
	REMOVE(5);

	private final int value;

	private PowerupState(int value) {
		this.value = value;
	}

	/**
	 * Returns the state as an integer value.
	 *
	 * @return an integer value representing this state.
	 *
	 * @see #toState(int)
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Returns a powerupState based on an integer value that represents a powerupState.
	 *
	 * @param value the integer value that represents a specific powerupState.
	 * @return a powerupState that corresponds to the specific integer value.
	 *
	 * @see #getValue()
	 *
	 * @throws IllegalArgumentException if the specified value does not correspond
	 *                                  to a powerupState and hence is invalid.
	 */
	public static PowerupState toState(int value) {
		switch (value) {
		case 0:
			return SPAWNED;

		case 1:
			return AVAILABLE;

		case 2:
			return HIT_VICTIM;

		case 3:
			return UNAVAILABLE;

		case 4:
			return INACTIVE;

		case 5:
			return REMOVE;

		default:
			throw new IllegalArgumentException("unknown value");
		}
	}
	
	public boolean isActive() {
		return this == SPAWNED || this == AVAILABLE;
	}
}
