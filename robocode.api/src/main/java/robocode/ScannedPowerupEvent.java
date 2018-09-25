/**
 * Copyright (c) 2001-2017 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 */
package robocode;


import net.sf.robocode.peer.IRobotStatics;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;
import robocode.robotinterfaces.IBasicEvents;
import robocode.robotinterfaces.IBasicRobot;

import java.awt.*;
import java.nio.ByteBuffer;


/**
 * A ScannedRobotEvent is sent to {@link Robot#onScannedPowerup(ScannedPowerupEvent)
 * onScannedRobot()} when you scan a powerup.
 * You can use the information contained in this event to determine what to do.
 * <p>
 * <b>Note</b>: You should not inherit from this class in your own event class!
 * The internal logic of this event class might change. Hence, your robot might
 * not work in future Robocode versions, if you choose to inherit from this class.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Andreas Stock (contributor)
 */
public class ScannedPowerupEvent extends Event {
	private static final long serialVersionUID = 1L;
	private final static int DEFAULT_PRIORITY = 11;

	private final int id;
	private final double energyBonus;
	private final double bearing;
	private final double distance;
	private final double respawnTime;

	/**
	 * Called by the game to create a new ScannedPowerupEvent.
	 *
	 * @param energyBonus   the energy of the scanned powerup
	 * @param bearing  the bearing of the scanned powerup, in radians
	 * @param distance the distance from your robot to the scanned powerup
	 * @param id TODO
	 * 
	 */
	public ScannedPowerupEvent(double energyBonus, double bearing, double distance, double respawnTime, int id) {
		super();
		this.id = id;
		this.energyBonus = energyBonus;
		this.bearing = bearing;
		this.distance = distance;
		this.respawnTime = respawnTime;
	}

	public int getId() {
		return id;
	}

	/**
	 * Returns the bearing to the powerup you scanned, relative to your robot's
	 * heading, in degrees (-180 <= getBearing() < 180)
	 *
	 * @return the bearing to the powerup you scanned, in degrees
	 */
	public double getBearing() {
		return bearing * 180.0 / Math.PI;
	}

	/**
	 * Returns the bearing to the powerup you scanned, relative to your robot's
	 * heading, in radians (-PI <= getBearingRadians() < PI)
	 *
	 * @return the bearing to the powerup you scanned, in radians
	 */
	public double getBearingRadians() {
		return bearing;
	}

	/**
	 * Returns the distance to the powerup (your center to his center).
	 *
	 * @return the distance to the powerup.
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * Returns the energybonus of the powerup.
	 *
	 * @return the energybonus of the powerup
	 */
	public double getEnergyBonus() {
		return energyBonus;
	}
	
	/**
	 * @return turns until this powerup will be available again.
	 */
	public double getRespawnTime() {
		return respawnTime;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int compareTo(Event event) {
		final int res = super.compareTo(event);
		if (res != 0) {
			return res;
		}
		// Compare the distance, if the events are ScannedRobotEvents
		// The shorter distance to the robot, the higher priority
		if (event instanceof ScannedPowerupEvent) {
			return (int) (this.getDistance() - ((ScannedPowerupEvent) event).getDistance());
		}
		// No difference found
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final int getDefaultPriority() {
		return DEFAULT_PRIORITY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void dispatch(IBasicRobot robot, IRobotStatics statics, Graphics2D graphics) {
		IBasicEvents listener = robot.getBasicEventListener();
		if (listener != null) {
			listener.onScannedPowerup(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	byte getSerializationType() {
		return RbSerializer.ScannedPowerupEvent_TYPE;
	}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			return RbSerializer.SIZEOF_TYPEINFO + 4 * RbSerializer.SIZEOF_DOUBLE;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			ScannedPowerupEvent obj = (ScannedPowerupEvent) object;
			serializer.serialize(buffer, obj.energyBonus);
			serializer.serialize(buffer, obj.bearing);
			serializer.serialize(buffer, obj.distance);
			serializer.serialize(buffer, obj.respawnTime);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			double energybonus = buffer.getDouble();
			double bearing = buffer.getDouble();
			double distance = buffer.getDouble();
			double respawnTime = buffer.getDouble();

			return new ScannedPowerupEvent(energybonus, bearing, distance, respawnTime, -1);
		}
	}
}
