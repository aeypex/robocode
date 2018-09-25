/**
 * Copyright (c) 2001-2017 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 */
package net.sf.robocode.battle.snapshot;


import net.sf.robocode.battle.peer.PowerupPeer;
import net.sf.robocode.serialization.IXmlSerializable;
import net.sf.robocode.serialization.XmlReader;
import net.sf.robocode.serialization.SerializableOptions;
import net.sf.robocode.serialization.XmlWriter;
import robocode.Rules;
import robocode.control.snapshot.IPowerupSnapshot;
import robocode.control.snapshot.PowerupState;

import java.io.IOException;


/**
 * A snapshot of a Powerup at a specific time instant in a battle.
 * The snapshot contains a snapshot of the powerup data at that specific time.
 *
 * @author see BulletSnapshot (original)
 * @author Andreas Stock (contributor)
 *
 * @since 1.9.3.2
 */
final class PowerupSnapshot implements java.io.Serializable, IXmlSerializable, IPowerupSnapshot {

	private static final long serialVersionUID = 2L;

	/** The powerup state */
	private PowerupState state;

	/** The x position */
	private double x;

	/** The y position */
	private double y;

	/** The ARGB color of the bullet */
	private int color = PowerupPeer.defaultPowerupColor;

	private int powerupId;

	private int victimIndex = -1;

	private int turnCounter;

	private double paintX;

	private double paintY;
	
	/**
	 * The amount of Energy gained by picking up the Item.
	 */
	private double powerupEnergyBonus;

	/**
	 * The amount of Turns after picking up the Item, before its available again.
	 */
	private double powerupRespawnTime;

	/**
	 * Creates a snapshot of a bullet that must be filled out with data later.
	 */
	public PowerupSnapshot() {
		state = PowerupState.INACTIVE;
		victimIndex = -1;
		powerupEnergyBonus = Rules.POWERUP_ENERGY_BONUS;
		powerupRespawnTime = Rules.POWERUP_RESPAWN_TIME;
	}

	/**
	 * Creates a snapshot of a bullet.
	 *
	 * @param powerup the bullet to make a snapshot of.
	 */
	PowerupSnapshot(PowerupPeer powerup) {

		powerupId = powerup.getPowerupId();
		
		state = powerup.getState();

		x = paintX = powerup.getX();
		y = paintY = powerup.getY();

		powerupEnergyBonus = powerup.getPowerupEnergyBonus();
		powerupRespawnTime = powerup.getPowerupRespawnTime();

		color = powerup.getColor();

		turnCounter = powerup.getFrame();

		final net.sf.robocode.battle.peer.RobotPeer victim = powerup.getVictim();

		if (victim != null) {
			victimIndex = victim.getRobotIndex();
		}
	}

	@Override
	public String toString() {
		return "Powerup " + powerupId + " X" + (int) x + " Y" + (int) y + " "
				+ state.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getPowerupId() {
		return powerupId;
	}

	/**
	 * {@inheritDoc}
	 */
	public PowerupState getState() {
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
	public double getPowerupEnergyBonus() {
		return powerupEnergyBonus;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public double getPowerupRespawnTime() {
		return powerupRespawnTime;
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
		writer.startElement(options.shortAttributes ? "p" : "powerup"); {
			writer.writeAttribute("id", powerupId);
			
			if (state == PowerupState.HIT_VICTIM) {
				writer.writeAttribute(options.shortAttributes ? "v" : "victim", victimIndex);
			}
		
			writer.writeAttribute("x", paintX, options.trimPrecision);
			writer.writeAttribute("y", paintY, options.trimPrecision);
			
			writer.writeAttribute("EnergyBonus", this.powerupEnergyBonus, options.trimPrecision);
			writer.writeAttribute("RespawnTime", this.powerupRespawnTime, options.trimPrecision);
			
			if (!options.skipNames) {
				if (color != PowerupPeer.defaultPowerupColor) {
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
		return reader.expect("powerup", "p", new XmlReader.Element() {
			public IXmlSerializable read(XmlReader reader) {
				final PowerupSnapshot snapshot = new PowerupSnapshot();

				reader.expect("id", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.powerupId = Integer.parseInt(value);
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
						snapshot.powerupEnergyBonus = Double.parseDouble(value);
					}
				});
				
				reader.expect("RespawnTime", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.powerupRespawnTime = Double.parseDouble(value);
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
						snapshot.state = PowerupState.valueOf(value);
					}
				});
				return snapshot;
			}
		});
	}
}
