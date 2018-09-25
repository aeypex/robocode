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
 * A ScannedRobotEvent is sent to {@link Robot#onScannedPowerup(PowerupEvent)
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
public class PowerupEvent extends Event {
	private static final long serialVersionUID = 1L;
	private final static int DEFAULT_PRIORITY = 10;

	private final double energyBonus;
	private final long respawnTime;
	private final String name;
	private final int id;
	

	
	/**
	 * @param energybonus
	 * @param respawnTime
	 * @param robotname
	 * @param id
	 */
	public PowerupEvent(double energybonus, long respawnTime, String robotname, int id) {
		super();
		this.energyBonus = energybonus;
		this.respawnTime = respawnTime;
		this.name = robotname;
		this.id = id;
	}

	/**
	 * Returns the name of the robot.
	 *
	 * @return the name of the robot
	 */
	public String getName() {
		return name;
	}

	public int getId() {
		return id;
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
	 * @return Turns until this powerup will be available again.
	 */
	public long getRespawnTime() {
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
		if (event instanceof PowerupEvent) {
			return (int) (this.getRespawnTime() - ((PowerupEvent) event).getRespawnTime());
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
			listener.onPowerup(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	byte getSerializationType() {
		return RbSerializer.PowerupEvent_TYPE;
	}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			PowerupEvent obj = (PowerupEvent) object;
			return RbSerializer.SIZEOF_TYPEINFO + serializer.sizeOf(obj.name) + RbSerializer.SIZEOF_DOUBLE + RbSerializer.SIZEOF_LONG + RbSerializer.SIZEOF_INT;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			PowerupEvent obj = (PowerupEvent) object;
			serializer.serialize(buffer, obj.name);
			serializer.serialize(buffer, obj.id);
			serializer.serialize(buffer, obj.energyBonus);
			serializer.serialize(buffer, obj.respawnTime);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			String name = serializer.deserializeString(buffer);
			int id = buffer.getInt();
			double energybonus = buffer.getDouble();
			long respawnTime = buffer.getLong();

			return new PowerupEvent(energybonus, respawnTime, name, id);
		}
	}
}
