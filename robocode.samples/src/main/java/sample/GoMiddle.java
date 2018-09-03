package sample;
import robocode.*;


/**
 * GoMiddle - a robot by Andreas Stock
 */
public class GoMiddle extends Robot
{
	/**
	 * run: GoMiddle's default behavior
	 */
	public void run() {
		while(true) {
			moveto(getBattleFieldWidth()/2,getBattleFieldHeight()/2);
			turnGunRight(360);
		}
	}
	
	private void moveto(double targetx, double targety){
		double dx = targetx - getX();
		double dy = targety - getY();
		double d = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		if(d>0){
			double anglex = Math.asin(dx/d)/Math.PI*180;
			if (dy < 0)
				anglex = 180-anglex;
			turnRight(anglex-getHeading());
			ahead(d);
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent event) {
		fire(3);
	}	
}
