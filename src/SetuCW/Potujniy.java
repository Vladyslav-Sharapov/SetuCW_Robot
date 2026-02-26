package SetuCW;

import java.awt.Color;

import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.*;

public class Potujniy extends Robot{

	private final long TARGETING_TIME = 5;
	private long lastTimeSeen;

    public void run() 
	{

        initialize();

       while (true)
		{
       		if(getTime() - lastTimeSeen > TARGETING_TIME)
			{
				turnRadarRight(360);
			}	
			else
			{
				scan();
			}
       }
    }

    public void onScannedRobot(ScannedRobotEvent e)
	{
		lastTimeSeen = getTime();
		
		double absoluteBearing = getHeading() + e.getBearing();
		turnGunRight(absoluteBearing - getGunHeading());
       fire(1);
    }

    public void onHitRobot(HitRobotEvent e){
        turnRight(180);
    }

    public void onHitByBullet(HitByBulletEvent e){
        turnRight(180);
    }
    public void onHitWall(HitWallEvent e){
        turnRight(180);
    }

    private void initialize() {
        // Let
        this.setAdjustGunForRobotTurn(true);

        // Set robot colors
        this.setBodyColor(new Color(0x00, 0x00, 0x00)); // Black
        this.setGunColor(new Color(0x32, 0x00, 0x00)); // Dark Red
        this.setRadarColor(new Color(0xFF, 0x00, 0x00)); // Red
        this.setBulletColor(new Color(0xFF, 0xD3, 0x9B)); // Burly wood
        this.setScanColor(new Color(0xCA, 0xFF, 0x70)); // Olive
    }


}// end of class