/**
 * Copyright (c) 2001-2017 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 */
package net.sf.robocode.battle.snapshot;


import net.sf.robocode.battle.peer.PickupPeer;
import net.sf.robocode.serialization.IXmlSerializable;
import net.sf.robocode.serialization.XmlReader;
import net.sf.robocode.serialization.SerializableOptions;
import net.sf.robocode.serialization.XmlWriter;
import robocode.Rules;
import robocode.control.snapshot.IPickupSnapshot;
import robocode.control.snapshot.PickupState;

import java.io.IOException;


/**
 * A snapshot of a Pickup at a specific time instant in a battle.
 * The snapshot contains a snapshot of the pickup data at that specific time.
 *
 * @author see BulletSnapshot (original)
 * @author Andreas Stock (contributor)
 *
 * @since 1.9.3.2
 */
final class PickupSnapshot implements java.io.Serializable, IXmlSerializable, IPickupSnapshot {

	private static final long serialVersionUID = 2L;

	/** The pickup state */
	private PickupState state;

	/** The x position */
	private double x;

	/** The y position */
	private double y;

	/** The ARGB color of the bullet */
	private int color = PickupPeer.defaultPickupColor;

	private int pickupId;

	private int victimIndex = -1;

	private int turnCounter;

	private double paintX;

	private double paintY;
	
	/**
	 * The amount of Energy gained by picking up the Item.
	 */
	private double pickupEnergyBonus;

	/**
	 * The amount of Turns after picking up the Item, before its available again.
	 */
	private double pickupRespawnTime;

	/**
	 * Creates a snapshot of a bullet that must be filled out with data later.
	 */
	public PickupSnapshot() {
		state = PickupState.INACTIVE;
		victimIndex = -1;
		pickupEnergyBonus = Rules.PICKUP_ENERGY_BONUS;
		pickupRespawnTime = Rules.PICKUP_RESPAWN_TIME;
	}

	/**
	 * Creates a snapshot of a bullet.
	 *
	 * @param pickup the bullet to make a snapshot of.
	 */
	PickupSnapshot(PickupPeer pickup) {

		pickupId = pickup.getPickupId();
		
		state = pickup.getState();

		x = paintX = pickup.getX();
		y = paintY = pickup.getY();

		pickupEnergyBonus = pickup.getPickupEnergyBonus();
		pickupRespawnTime = pickup.getPickupRespawnTime();

		color = pickup.getColor();

		turnCounter = pickup.getFrame();

		final net.sf.robocode.battle.peer.RobotPeer victim = pickup.getVictim();

		if (victim != null) {
			victimIndex = victim.getRobotIndex();
		}
	}

	@Override
	public String toString() {
		return "Pickup " + pickupId + " X" + (int) x + " Y" + (int) y + " "
				+ state.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getPickupId() {
		return pickupId;
	}

	/**
	 * {@inheritDoc}
	 */
	public PickupState getState() {
		return state;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getX() {
		return x;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getY() {
		return y;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getPaintX() {
		return paintX;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getPaintY() {
		return paintY;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public double getPickupEnergyBonus() {
		return pickupEnergyBonus;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public double getPickupRespawnTime() {
		return pickupRespawnTime;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getColor() {
		return color;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getFrame() {
		return turnCounter;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getVictimIndex() {
		return victimIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	public void writeXml(XmlWriter writer, SerializableOptions options) throws IOException {
		writer.startElement(options.shortAttributes ? "p" : "pickup"); {
			writer.writeAttribute("id", pickupId);
			
			if (state == PickupState.HIT_VICTIM) {
				writer.writeAttribute(options.shortAttributes ? "v" : "victim", victimIndex);
			}
		
			writer.writeAttribute("x", paintX, options.trimPrecision);
			writer.writeAttribute("y", paintY, options.trimPrecision);
			
			writer.writeAttribute("EnergyBonus", this.pickupEnergyBonus, options.trimPrecision);
			writer.writeAttribute("RespawnTime", this.pickupRespawnTime, options.trimPrecision);
			
			if (!options.skipNames) {
				if (color != PickupPeer.defaultPickupColor) {
					writer.writeAttribute(options.shortAttributes ? "c" : "color",
							Integer.toHexString(color).toUpperCase());
				}
			}
			
			writer.writeAttribute("turnCounter", turnCounter);
			writer.writeAttribute("State", ""+this.getState());
			
			if (!options.skipVersion) {
				writer.writeAttribute("ver", serialVersionUID);
			}
		}
		writer.endElement();
	}

	/**
	 * {@inheritDoc}
	 */
	public XmlReader.Element readXml(XmlReader reader) {
		return reader.expect("pickup", "p", new XmlReader.Element() {
			public IXmlSerializable read(XmlReader reader) {
				final PickupSnapshot snapshot = new PickupSnapshot();

				reader.expect("id", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.pickupId = Integer.parseInt(value);
					}
				});

				reader.expect("victim", "v", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.victimIndex = Integer.parseInt(value);
					}
				});

				reader.expect("x", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.x = Double.parseDouble(value);
						snapshot.paintX = snapshot.x;
					}
				});

				reader.expect("y", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.y = Double.parseDouble(value);
						snapshot.paintY = snapshot.y;
					}
				});
				
				reader.expect("EnergyBonus", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.pickupEnergyBonus = Double.parseDouble(value);
					}
				});
				
				reader.expect("RespawnTime", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.pickupRespawnTime = Double.parseDouble(value);
					}
				});

				reader.expect("color", "c", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.color = Long.valueOf(value.toUpperCase(), 16).intValue();
					}
				});

				reader.expect("TurnCounter", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.turnCounter = Integer.parseInt(value);
					}
				});

				reader.expect("State", "s", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.state = PickupState.valueOf(value);
					}
				});
				return snapshot;
			}
		});
	}
}
