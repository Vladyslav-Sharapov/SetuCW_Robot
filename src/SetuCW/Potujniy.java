package SetuCW;

import java.awt.Color;
import robocode.*;

public class Potujniy extends Robot{

	private final long TARGETING_TIME = 30;
	private long lastTimeSeen;
    final double BATTLE_FIELD_WIDTH = 800.0F;
    final double BATTLE_FIELD_HEIGHT = 800.0F;

    // Potujniy's variables, where p is short for Potujniy.
    double pX;
    double pY;
    double pEnergy;
    double pGunHeat;
    double pGunHeading;
    double pHeading;
    double pRadarHeading;

    // Targeting variables
    double eDistance;

    public void run() 
	{

        initialize();
        scanTowardsCentre();

    while (true)
	{
       		if(getTime() - lastTimeSeen > TARGETING_TIME)
			{
				scanTowardsCentre();
			}	
			else
			{
                ahead(100);
                back(100);
			}
       }
    }

    public void onScannedRobot(ScannedRobotEvent sre)
	{
        if(!sre.isSentryRobot()) {
            lastTimeSeen = getTime(); //Updates time stamp when an enemy was seen last time

            eDistance = sre.getDistance();
		
		    double absoluteBearing =  getHeading() + sre.getBearing();//Calculates absolute bearing to the enemy
		    double turnGun =  normalizeAngle(absoluteBearing - getGunHeading());//Calculates the target angle to rotatate gun
//            if (turnGun >= 360)
//            {
//                turnGun -= 360;
//            } else if (turnGun <= -360)
//            {
//                turnGun += 360;
//            }

            System.out.println(turnGun);
            turnGunRight(turnGun);
            fire(calculateFirePower());
        }
    }

    /**
     * This is an algorithm that calculates the optimal firepower based on the distance to the enemy robot
     */
    private double calculateFirePower()
    {
        double distance = Math.max(1.0F, eDistance);
        return Math.min(3.0F, Math.max(0.1F, (400.0F / distance) * 3.0F));
    }

    public void onHitRobot(HitRobotEvent hre){
        this.turnRight(180);
    }
	
	private double normalizeAngle(double angle)
	{
		return (angle + 180) % 360 - 180;//Normalizes angle between -180 and 180
	}

    public void onHitByBullet(HitByBulletEvent hbe){
        // this.turnRight(180);
    }
    public void onHitWall(HitWallEvent hwe){
        // this.turnRight(180);
    }

    public void onStatus(StatusEvent se) {
        RobotStatus status = se.getStatus();

        pX = status.getX();
        pY = status.getY();
        pEnergy = status.getEnergy();
        pGunHeat = status.getGunHeat();
        pGunHeading = status.getGunHeading();
        pHeading = normalizeAngle(status.getHeading());
        pRadarHeading = normalizeAngle(status.getRadarHeading());
    }

    /**
     * Rotates the radar towards the center of the battlefield by calculating the shortest angular
     * difference required to align the radar's current heading with the direction of the center.
     * ---------------------------
     * The method determines the angular difference between the robot's radar heading and the
     * direction of the battlefield center using the `returnDegressDifference` helper method.
     * It then applies a full 360-degree sweep to the radar in the calculated direction.
     */
    private void scanTowardsCentre()
    {
        double degrees_difference = returnDegreesDifference((BATTLE_FIELD_WIDTH / 2), (BATTLE_FIELD_HEIGHT / 2), pX, pY, pRadarHeading);
        turnRadarLeft(360 * (degrees_difference / Math.abs(degrees_difference)));
    }

    /**
     * Calculates the shortest turn amount in degrees between two angles,
     * ensuring the result is within the range of -180 to 180 degrees.
     *
     * @param end_Degree   the target angle in degrees
     * @param start_Degree the starting angle in degrees
     * @return the shortest turn amount in degrees to reach the target angle
     *         from the starting angle
     */
    private double getTurnAmount(double end_Degree, double start_Degree)
    {
        double turn_amount = end_Degree - start_Degree;

        if (turn_amount > 180)
        {
            turn_amount -= 360;
        }
        else if (turn_amount < -180)
        {
            turn_amount += 360;
        }

        return turn_amount;
    }

    /**
     * Calculates the angular difference in degrees between a point and the heading of a reference object.
     * The angular difference is derived by determining the shortest turn amount required to align the
     * heading with the direction of the point.
     *
     * @param poX the x-coordinate of the point of interest
     * @param poY the y-coordinate of the point of interest
     * @param fromX the x-coordinate of the reference object's position
     * @param fromY the y-coordinate of the reference object's position
     * @param thingHeading the current heading angle of the reference object in degrees
     * @return the shortest turn amount in degrees to align the reference object's heading
     *         with the direction of the point
     */
    private double returnDegreesDifference(double poX, double poY, double fromX, double fromY, double thingHeading)
    {
        double relative_x = poX - fromX;
        double relative_y = poY - fromY;
        double point_degrees = Math.toDegrees(Math.atan2(relative_y, relative_x));
        return getTurnAmount(point_degrees, thingHeading);
    }

    /**
     * Initializes the robot's configuration and settings during startup.
     */
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

    public void moveToPosition(double endPosX, double endPosY)
	{
		double directionX = endPosX - getX();
		double directionY = endPosY - getY();
		
		double angle = Math.toDegrees(Math.atan2(directionX ,directionY));
		double angleTurn = angle - getHeading();
		if(angleTurn > 360)
		{
			angleTurn -= 360;
		}
		if(angleTurn < -360)
		{
			angleTurn += 360;
		}
		turnRight(angleTurn);
		
		double distance = Math.sqrt(directionX * directionX + directionY * directionY);
		ahead(distance);
	}
}// end of class