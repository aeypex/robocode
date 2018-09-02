/**
 * Copyright (c) 2001-2017 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 */
package net.sf.robocode.battle.peer;


import net.sf.robocode.battle.BoundingRectangle;
import robocode.*;
import robocode.control.snapshot.PickupState;

import java.util.List;


/**
 * @author see Bullet (original)
 * @author Andreas Stock (contributor)
 */
public class PickupPeer {
	
	public static final int
	WIDTH = 36,
	HEIGHT = 36;

	private static final int
	HALF_WIDTH_OFFSET = WIDTH / 2,
	HALF_HEIGHT_OFFSET = HEIGHT / 2;

	@SuppressWarnings("unused")
	private final BattleRules battleRules;
	private final int pickupId;

	protected RobotPeer victim;

	protected PickupState state;

	protected double x;
	protected double y;
	
	private final BoundingRectangle boundingBox;

	protected int frame; // Do not set to -1

	private final int color;

	PickupPeer(RobotPeer owner, BattleRules battleRules, int pickupId) {
		super();
		this.battleRules = battleRules;
		this.pickupId = pickupId;
		this.boundingBox = new BoundingRectangle();
		state = PickupState.SPAWNED;
		color = owner.getBulletColor(); // Store current bullet color set on robot
	}
	
	public BoundingRectangle getBoundingBox() {
		return boundingBox;
	}
	
	private void updateBoundingBox() {
		boundingBox.setRect(x - HALF_WIDTH_OFFSET, y - HALF_HEIGHT_OFFSET, WIDTH, HEIGHT);
	}
	
	private void checkRobotCollision(List<RobotPeer> robots) {
		for (RobotPeer otherRobot : robots) {
			if (!(otherRobot == null || otherRobot.isDead())
					&& otherRobot.getBoundingBox().intersects(boundingBox)) {

				state = PickupState.HIT_VICTIM;
				frame = 0;
				victim = otherRobot;

				double energyGain = Rules.PICKUP_ENERGY_GAIN;
				
				otherRobot.updateEnergy(energyGain);
				
				victim.println(
						"SYSTEM: Pickup Bonus for "
								+ (victim.getNameForEvent(otherRobot) + ": " + (int) (energyGain + .5)));

				/* 
				 *  otherRobot.addEvent(
						new HitByBulletEvent(
								robocode.util.Utils.normalRelativeAngle(heading + Math.PI - otherRobot.getBodyHeading()),
								createBullet(true))); // Bugfix #366
				*/

				break;
			}
		}
	}

	public int getPickupId() {
		return pickupId;
	}

	public int getFrame() {
		return frame;
	}

	public RobotPeer getVictim() {
		return victim;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getPaintX() {
		return x;
	}

	public double getPaintY() {
		return y;
	}

	public boolean isActive() {
		return state.isActive();
	}

	public PickupState getState() {
		return state;
	}

	public int getColor() {
		return color;
	}

	public void setVictim(RobotPeer newVictim) {
		victim = newVictim;
	}

	public void setX(double newX) {
		x = newX;
		updateBoundingBox();
	}

	public void setY(double newY) {
		y= newY;
		updateBoundingBox();
	}

	public void setState(PickupState newState) {
		state = newState;
	}

	public void update(List<RobotPeer> robots, List<PickupPeer> bullets) {
		frame++;
		if (isActive()) {
			checkRobotCollision(robots);
		}
		updatePickupState();
	}

	protected void updatePickupState() {
		switch (state) {
		case SPAWNED:
			// Note that the Pickup must be in the SPAWNED state before it goes to the AVAILABLE state
			if (frame > 0) {
				state = PickupState.AVAILABLE;
			}
			break;

		case HIT_VICTIM:
			// if some bot collided with powerup, it transitions to HIT_VICTIM and then starts its respawn cycle.
			frame = 0;
			state = PickupState.UNAVAILABLE;
			break;
			
		case UNAVAILABLE:
			if (frame >= Rules.PICKUP_RESPAWN_TIME) {
				state = PickupState.SPAWNED;
				frame = 0;
			}
			break;

		default:
		}
		
	}

	@Override
	public String toString() {
		return getVictim().getName() + " V" + (int) Rules.PICKUP_ENERGY_GAIN + " X" + (int) x + " Y" + (int) y + " " + state.toString();
	}
}
