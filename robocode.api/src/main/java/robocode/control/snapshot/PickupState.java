/**
 * Copyright (c) 2001-2017 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 */
package robocode.control.snapshot;


/**
 * Defines a pickup state, which can be: just spawned, available, hitting a victim, unavailable or inactive.
 *
 * @author Andreas Stock
 */
public enum PickupState {

	/** The Pickup has just been spawned this turn and hence just been created. This state only last one turn. */
	SPAWNED(0),

	/** The pickup is now available on the battlefield, but has not hit anything yet. */
	AVAILABLE(1),

	/** The Pickup was hit by a robot victim. */
	HIT_VICTIM(2),

	/** The Pickup was picked up and is currently waiting to be respawned.  */
	UNAVAILABLE(3),

	/** The Pickup is currently inactive. Hence, it is not active or visible on the battlefield. */
	INACTIVE(4),
	
	/** The Pickup is ok to be removed and cleaned up. */
	REMOVE(5);

	private final int value;

	private PickupState(int value) {
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
	 * Returns a pickupState based on an integer value that represents a pickupState.
	 *
	 * @param value the integer value that represents a specific pickupState.
	 * @return a pickupState that corresponds to the specific integer value.
	 *
	 * @see #getValue()
	 *
	 * @throws IllegalArgumentException if the specified value does not correspond
	 *                                  to a pickupState and hence is invalid.
	 */
	public static PickupState toState(int value) {
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
