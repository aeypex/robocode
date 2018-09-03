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
import robocode.control.PickupSetup;
import robocode.control.snapshot.PickupState;

import java.awt.geom.Point2D;
import java.util.List;


/**
 * @author see Bullet (original)
 * @author Andreas Stock (contributor)
 * @since 1.9.3.2
 */
public class PickupPeer {
	
	public static final int defaultPickupColor = 0xFFABCDEF;
	
	public static final int
	WIDTH = 36,
	HEIGHT = 36;

	private static final int
	HALF_WIDTH_OFFSET = WIDTH / 2,
	HALF_HEIGHT_OFFSET = HEIGHT / 2;

	public static final int LOLN = 4; //so that randomized positions land more often to the middle. see "law of large numbers"

	private final BattleRules battleRules;
	private final int pickupId;

	protected RobotPeer victim;

	protected PickupState state;

	protected double x;
	protected double y;
	
	/**
	 * The amount of Energy gained by picking up the Item.
	 */
	private double pickupEnergyBonus = Rules.PICKUP_ENERGY_BONUS;
	
	/**
	 * The amount of Turns after picking up the Item, before its available again.
	 */
	private double pickupRespawnTime = Rules.PICKUP_RESPAWN_TIME;
	
	private final BoundingRectangle boundingBox;

	protected int turnCounter; // Do not set to -1

	private final int color = defaultPickupColor;

	public PickupPeer(PickupSetup ps, BattleRules battleRules, int pickupId) {
		this(battleRules,pickupId);
		if (ps.getX()!=null && ps.getX() != null) {
			this.x = ps.getX();
			this.y = ps.getY();
		} else {
			calculateRandomPosition();
		}
		this.pickupEnergyBonus = ps.getEnergygain();
		this.pickupRespawnTime = ps.getRespawntime();
		
		updateBoundingBox();
	}
	
	public PickupPeer(BattleRules battleRules, int pickupId) {
		super();
		this.battleRules = battleRules;
		this.pickupId = pickupId;
		this.boundingBox = new BoundingRectangle();
		state = PickupState.SPAWNED;
		calculateRandomPosition();
		updateBoundingBox();
	}

	private void calculateRandomPosition() {
		Point2D p = battleRules.calculateRandomPosition(WIDTH,HEIGHT,LOLN);
		this.x = p.getX();
		this.y = p.getY();
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
				turnCounter = 0;
				victim = otherRobot;

				double energyGain = this.pickupEnergyBonus;
				
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
		return turnCounter;
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

	public double getPickupEnergyBonus() {
		return pickupEnergyBonus;
	}

	public double getPickupRespawnTime() {
		return pickupRespawnTime;
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

	public void update(List<RobotPeer> robots) {
		turnCounter++;
		if (isActive()) {
			checkRobotCollision(robots);
		}
		updatePickupState();
	}

	protected void updatePickupState() {
		switch (state) {
		case SPAWNED:
			// Note that the Pickup must be in the SPAWNED state before it goes to the AVAILABLE state
			if (turnCounter > 0) {
				state = PickupState.AVAILABLE;
			}
			break;

		case HIT_VICTIM:
			// if some bot collided with powerup, it transitions to HIT_VICTIM and then starts its respawn cycle.
			turnCounter = 0;
			if(this.pickupRespawnTime > 0) {
				state = PickupState.UNAVAILABLE;
				break;
			}
			state = PickupState.INACTIVE;
			break;
			
		case UNAVAILABLE:
			if (turnCounter >= this.pickupRespawnTime) {
				state = PickupState.SPAWNED;
				turnCounter = 0;
			}
			break;

		default:
		}
		
	}

	@Override
	public String toString() {
		return getVictim().getName() + " V" + (int) this.pickupEnergyBonus + " X" + (int) x + " Y" + (int) y + " " + state.toString();
	}

	public void initializeRound() {
		
		/*if (isRandomlyPositioned) {
			battleRules.calculateRandomPosition(WIDTH, HEIGHT, LOLN);
		}*/
		setState(PickupState.SPAWNED);
		turnCounter = 0;

		//status = new AtomicReference<PickupStatus>(); ??
	}

	public void cleanupAfterRoundEnded() {
		setState(PickupState.INACTIVE);
		turnCounter = 0;
		return;
	}
}
