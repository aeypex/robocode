package sample;
import java.awt.Color;

import robocode.*;


/**
 * Collectrobot - a sample robot by Andreas Stock
 */
public class Collectrobot extends Robot
{
	
	/**
	 * run: GoMiddle's default behavior
	 */
	public void run() {
		// Set colors
		setBodyColor(new Color(252, 151, 63));
		setGunColor(new Color(255, 255, 255));
		setRadarColor(new Color(99, 175, 54));
		
		while(true) {
			turnGunLeft(90);
		}
	}
	
	/**
	 * onScannedPickup: Go!
	 */
	public void onScannedPickup(ScannedPickupEvent e) {
		//face forward
		turnRight(e.getBearing());
		turnGunLeft(getGunHeading()-getHeading());
		//and go!
		ahead(e.getDistance());
	}
	

	/**
	 * onScannedRobot: Fire!
	 */
	public void onScannedRobot(ScannedRobotEvent event) {
		fire(3);
	}
	
}
