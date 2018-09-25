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
import robocode.control.PowerupSetup;
import robocode.control.snapshot.PowerupState;

import java.awt.geom.Point2D;
import java.util.List;


/**
 * @author see Bullet (original)
 * @author Andreas Stock (contributor)
 */
public class PowerupPeer {
	
	public static final int defaultPowerupColor = 0xFFABCDEF;
	
	public static final int
	WIDTH = 36,
	HEIGHT = 36;

	private static final int
	HALF_WIDTH_OFFSET = WIDTH / 2,
	HALF_HEIGHT_OFFSET = HEIGHT / 2;

	public static final int LOLN = 2; //so that randomized positions land more often to the middle. see "law of large numbers"

	private final BattleRules battleRules;
	private final int powerupId;

	protected RobotPeer victim;

	protected PowerupState state;

	protected double x;
	protected double y;
	
	/**
	 * The amount of Energy gained by picking up the Item.
	 */
	private double powerupEnergyBonus = Rules.POWERUP_ENERGY_BONUS;
	
	/**
	 * The amount of Turns after picking up the Item, before its available again.
	 */
	private long powerupRespawnTime = Rules.POWERUP_RESPAWN_TIME;
	
	private final BoundingRectangle boundingBox;

	protected int turnCounter; // Do not set to -1

	private final int color = defaultPowerupColor;

	public PowerupPeer(PowerupSetup ps, BattleRules battleRules, int powerupId) {
		this(battleRules,powerupId);
		if (ps.getX()!=null && ps.getX() != null) {
			this.x = ps.getX();
			this.y = ps.getY();
		} else {
			calculateRandomPosition();
		}
		this.powerupEnergyBonus = ps.getEnergyBonus();
		this.powerupRespawnTime = ps.getRespawnTime();
		
		updateBoundingBox();
	}
	
	public PowerupPeer(BattleRules battleRules, int powerupId) {
		super();
		this.battleRules = battleRules;
		this.powerupId = powerupId;
		this.boundingBox = new BoundingRectangle();
		state = PowerupState.SPAWNED;
		calculateRandomPosition();
		updateBoundingBox();
	}

	private void calculateRandomPosition() {
		Point2D p = battleRules.calculateRandomPosition(Math.max(WIDTH,HEIGHT),LOLN,null);
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

				state = PowerupState.HIT_VICTIM;
				turnCounter = 0;
				victim = otherRobot;

				double energyGain = this.powerupEnergyBonus;
				
				otherRobot.updateEnergy(energyGain);
				
				victim.println(
						"SYSTEM: Powerup Bonus for Robot"
								+ (victim.getNameForEvent(otherRobot) + ": " + (int) (energyGain + .5)));

				for (RobotPeer r : robots) {
					r.addEvent(new PowerupEvent(this.getPowerupEnergyBonus(), this.getPowerupRespawnTime(),
							otherRobot.getName(), this.getPowerupId()));
				}
				break;
			}
		}
	}

	public int getPowerupId() {
		return powerupId;
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

	public double getPowerupEnergyBonus() {
		return powerupEnergyBonus;
	}

	public long getPowerupRespawnTime() {
		return powerupRespawnTime;
	}

	public boolean isActive() {
		return state.isActive();
	}

	public PowerupState getState() {
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

	public void setState(PowerupState newState) {
		state = newState;
	}

	public void update(List<RobotPeer> robots, boolean respawnBlocked) {
		if (!respawnBlocked) {
			turnCounter++;
		}
		if (isActive()) {
			checkRobotCollision(robots);
		}
		updatePowerupState(respawnBlocked);
	}

	protected void updatePowerupState(boolean respawnBlocked) {
		switch (state) {
		case SPAWNED:
			// Note that the Powerup must be in the SPAWNED state before it goes to the AVAILABLE state
			if (turnCounter >= 0) {
				state = PowerupState.AVAILABLE;
			}
			break;

		case HIT_VICTIM:
			// if some bot collided with powerup, it transitions to HIT_VICTIM and then starts its respawn cycle.
			turnCounter = 0;
			if(this.powerupRespawnTime > 0) {
				state = PowerupState.UNAVAILABLE;
				break;
			}
			state = PowerupState.INACTIVE;
			break;
			
		case UNAVAILABLE:
			if (!respawnBlocked) {
				if (turnCounter >= this.powerupRespawnTime) {
					state = PowerupState.SPAWNED;
					turnCounter = 0;
				} 
			}
			break;

		default:
		}
		
	}

	@Override
	public String toString() {
		return getVictim().getName() + " V" + (int) this.powerupEnergyBonus + " X" + (int) x + " Y" + (int) y + " " + state.toString();
	}

	public void initializeRound() {
		
		/*if (isRandomlyPositioned) {
			battleRules.calculateRandomPosition(WIDTH, HEIGHT, LOLN);
		}*/
		setState(PowerupState.SPAWNED);
		turnCounter = 0;

		//status = new AtomicReference<PowerupStatus>(); ??
	}

	public void cleanupAfterRoundEnded() {
		setState(PowerupState.INACTIVE);
		turnCounter = 0;
		return;
	}
}
