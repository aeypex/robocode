/**
 * Copyright (c) 2001-2017 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 */
package robocode;


import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.sf.robocode.security.IHiddenRulesHelper;
import robocode.control.RandomFactory;


/**
 * Contains the battle rules returned by {@link robocode.control.events.BattleStartedEvent#getBattleRules()
 * BattleStartedEvent.getBattleRules()} when a battle is started and
 * {@link robocode.control.events.BattleCompletedEvent#getBattleRules() BattleCompletedEvent.getBattleRules()}
 * when a battle is completed.
 *
 * @see robocode.control.events.BattleStartedEvent BattleStartedEvent
 * @see robocode.control.events.BattleCompletedEvent BattleCompletedEvent
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.2
 */
public final class BattleRules implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private final int battlefieldWidth;
	private final int battlefieldHeight;
	private final int numRounds;
	private final double gunCoolingRate;
	private final long inactivityTime;
	private final boolean hideEnemyNames;
	private final int sentryBorderSize;

	/**
	 * Returns the battlefield width.
	 *
	 * @return the battlefield width.
	 */
	public int getBattlefieldWidth() {
		return battlefieldWidth;
	}

	/**
	 * Returns the battlefield height.
	 *
	 * @return the battlefield height.
	 */
	public int getBattlefieldHeight() {
		return battlefieldHeight;
	}

	/**
	 * Returns the number of rounds.
	 *
	 * @return the number of rounds.
	 */
	public int getNumRounds() {
		return numRounds;
	}

	/**
	 * Returns the rate at which the gun will cool down, i.e. the amount of heat the gun heat will drop per turn.
	 * <p>
	 * The gun cooling rate is default 0.1 per turn, but can be changed by the battle setup.
	 * So don't count on the cooling rate being 0.1!
	 *
	 * @return the gun cooling rate.
	 * @see Robot#getGunHeat()
	 * @see Robot#fire(double)
	 * @see Robot#fireBullet(double)
	 */
	public double getGunCoolingRate() {
		return gunCoolingRate;
	}

	/**
	 * Returns the allowed inactivity time, where the robot is not taking any action, before will begin to be zapped.
	 * The inactivity time is measured in turns, and is the allowed time that a robot is allowed to omit taking
	 * action before being punished by the game by zapping.
	 * <p>
	 * When a robot is zapped by the game, it will loose 0.1 energy points per turn. Eventually the robot will be
	 * killed by zapping until the robot takes action. When the robot takes action, the inactivity time counter is
	 * reset. 
	 * <p>
	 * The allowed inactivity time is per default 450 turns, but can be changed by the battle setup.
	 * So don't count on the inactivity time being 450 turns!
	 *
	 * @return the allowed inactivity time.
	 * @see Robot#doNothing()
	 * @see AdvancedRobot#execute()
	 */
	public long getInactivityTime() {
		return inactivityTime;
	}

	/**
	 * Returns true if the enemy names are hidden, i.e. anonymous; false otherwise.
	 * 
	 * @Since 1.7.3
	 */
	public boolean getHideEnemyNames() {
		return hideEnemyNames;
	}

	/**
	 * Returns the sentry border size for a {@link robocode.BorderSentry BorderSentry} that defines the how
	 * far a BorderSentry is allowed to move from the border edges measured in units.<br>
	 * Hence, the sentry border size defines the width/range of the border area surrounding the battlefield that
	 * BorderSentrys cannot leave (sentry robots robots must stay in the border area), but it also define the
	 * distance from the border edges where BorderSentrys are allowed/able to make damage to robots entering this
	 * border area.
	 * 
	 * @return the border size in units/pixels.
	 * 
	 * @since 1.9.0.0
	 */
	public int getSentryBorderSize() {
		return sentryBorderSize;
	}
	
	private BattleRules(int battlefieldWidth, int battlefieldHeight, int numRounds, double gunCoolingRate,
			long inactivityTime, boolean hideEnemyNames, int sentryBorderSize) {
		this.battlefieldWidth = battlefieldWidth;
		this.battlefieldHeight = battlefieldHeight;
		this.numRounds = numRounds;
		this.gunCoolingRate = gunCoolingRate;
		this.inactivityTime = inactivityTime;
		this.hideEnemyNames = hideEnemyNames;
		this.sentryBorderSize = sentryBorderSize;
	}

	static IHiddenRulesHelper createHiddenHelper() {
		return new HiddenHelper();
	}

	private static class HiddenHelper implements IHiddenRulesHelper {

		public BattleRules createRules(int battlefieldWidth, int battlefieldHeight, int numRounds, double gunCoolingRate, long inactivityTime, boolean hideEnemyNames, int sentryBorderSize) {
			return new BattleRules(battlefieldWidth, battlefieldHeight, numRounds, gunCoolingRate, inactivityTime,
					hideEnemyNames, sentryBorderSize);
		}
	}


	/**
	 * Note: its not efficient to run this code with large numbers.
	 * 
	 * @param minimaldistance minimal distance to border or other positions see nonOverlapControlGroup
	 * @param loln 1 is normal. set higher (ie. 10) so that position is more in middle. see law of large numbers.
	 * @param nonOverlapControlGroup other points that you may want to not overlap. set null if you dont care about them.
	 * @return a random position on the specified battlefield
	 * 
	 * @author Andreas Stock
	 */
	public Point2D calculateRandomPosition(double minimaldistance, int loln, List<Point2D> nonOverlapControlGroup) {
		final Random random = RandomFactory.getRandom();
		
		Point2D p = new Point2D.Double();
		int fails=0;
		boolean overlaps = false;
		do {
			overlaps = false;
			double rndX = random.nextDouble();
			double rndY = random.nextDouble();
			for (int i = 0; i < loln - 1; i++) {
				rndX += random.nextDouble();
				rndY += random.nextDouble();
			}
			rndX /= loln;
			rndY /= loln;
			double x = minimaldistance/2 + rndX * (getBattlefieldWidth() - minimaldistance);
			double y = minimaldistance/2 + rndY * (getBattlefieldHeight() - minimaldistance);
			p = new Point2D.Double(x, y);
			
			if (nonOverlapControlGroup != null) {
				for (Point2D controlpoint : nonOverlapControlGroup) {
					if (Math.abs(p.getX()-controlpoint.getX()) < minimaldistance
							|| Math.abs(p.getY()-controlpoint.getY()) < minimaldistance) {
						overlaps = true;
						fails++;
					}
				} 
			}
			
		} while (fails < 100 
				&& 
				overlaps);
		
		return p;
	}

	/**
	 * divides battlefield into columns and rows, then shuffles them. each intersection is a point.
	 * @param n number of points
	 * @return a list of points
	 */
	public List<Point2D> calculateEqualyDistributedPoints2D(int n) {
		if (n < 1) {
			return null;
		}
		
		List<Point2D> r = new ArrayList<Point2D>(n);
		List<Double> x = new ArrayList<Double>(n);
		List<Double> y = new ArrayList<Double>(n);
		
		double stepX = this.battlefieldWidth/(n+1), currentX = 0, 
				stepY = this.battlefieldHeight/(n+1), currentY = 0;
		for (int i = 0; i < n; i++) {
			currentX += stepX;
			currentY += stepY;
			x.add(currentX);
			y.add(currentY);
		}
		Collections.shuffle(x, RandomFactory.getRandom());
		Collections.shuffle(y, RandomFactory.getRandom());
		for (int i = 0; i < n; i++) {
			r.add(new Point2D.Double(x.get(i), y.get(i)));
		}
		return r;
		
	}
}
