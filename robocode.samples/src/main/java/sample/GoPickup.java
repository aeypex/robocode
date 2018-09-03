package sample;
import java.awt.Color;

import robocode.*;


/**
 * GoPickup - a robot by Andreas Stock
 */
public class GoPickup extends Robot
{
	double turn = 0;
	double travel = 0;
	
	/**
	 * run: GoMiddle's default behavior
	 */
	public void run() {
		// Set colors
		setBodyColor(new Color(0, 0, 0));
		setGunColor(new Color(255, 255, 255));
		setRadarColor(new Color(255, 0, 0));
		
		turnGunRight(360);
		while(true) {
			turnRight(turn);
			ahead(travel);
			turnGunLeft(360);
		}
	}
	
	/**
	 * onScannedPickup: Go!
	 */
	public void onScannedPickup(ScannedPickupEvent e) {
		turn = e.getBearing();
		travel = e.getDistance();
	}

	/**
	 * onScannedRobot: Fire!
	 */
	public void onScannedRobot(ScannedRobotEvent event) {
		fire(3);
	}
}
