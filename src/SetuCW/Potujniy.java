package SetuCW;

import java.awt.Color;
import robocode.*;

public class Potujniy extends Robot{

	private final long TARGETING_TIME = 5;
	private long lastTimeSeen;
    final double BATTLE_FIELD_WIDTH = getBattleFieldWidth();
    final double BATTLE_FIELD_HEIGHT = getBattleFieldHeight();

    // Potujniy's variables, where p is short for Potujniy.
    double pX;
    double pY;
    double pEnergy;
    double pGunHeat;
    double pHeading;
    double pRadarHeading;

    // Targeting variables
    double eDistance;

    public void run() 
	{

        this.initialize();
        this.scanTowardsCentre();

       while (true)
		{
       		if(getTime() - this.lastTimeSeen > this.TARGETING_TIME)
			{
				this.scanTowardsCentre();
			}	
			else
			{
				this.scan();
			}
       }
    }

    public void onScannedRobot(ScannedRobotEvent sre)
	{
        if(!sre.isSentryRobot()) {
            this.lastTimeSeen = getTime();

            this.eDistance = sre.getDistance();

            double absoluteBearing = this.getHeading() + sre.getBearing();
            this.turnGunRight(absoluteBearing - this.getGunHeading());

            // This is a targeting algorithm that calculates the optimal firepower based on the distance to the enemy robot
            if (this.pGunHeat == 0 && this.pEnergy > 0) {

                double distance = Math.max(1.0F, this.eDistance);
                double firePower = Math.min(3.0F, Math.max(0.1F, (400.0F / distance) * 3.0F));

                System.out.println("Distance = " + distance);
                System.out.println("firePower = " + firePower);

                this.fire(firePower);

            }
        }
    }

    public void onHitRobot(HitRobotEvent hre){
        this.turnRight(180);
    }

    public void onHitByBullet(HitByBulletEvent hbe){
        this.turnRight(180);
    }
    public void onHitWall(HitWallEvent hwe){
        this.turnRight(180);
    }

    public void onStatus(StatusEvent se) {
        RobotStatus status = se.getStatus();

        this.pX = status.getX();
        this.pY = status.getY();
        this.pEnergy = status.getEnergy();
        this.pGunHeat = status.getGunHeat();
        this.pHeading = this.convertToProperDegrees(status.getHeading());
        this.pRadarHeading = this.convertToProperDegrees(status.getRadarHeading());
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
     * Converts a heading angle from Robocode's coordinate system to a standard Cartesian coordinate system.
     * In Robocode, the heading is measured clockwise from "up" (0 degrees), whereas in Cartesian coordinates,
     * 0 degrees corresponds to the positive x-axis and angles increase counterclockwise.
     *
     * @param robocodeHeading the heading angle in degrees, as measured in Robocode's coordinate system
     * @return the equivalent angle in degrees in a standard Cartesian coordinate system
     */
    private double convertToProperDegrees(double robocodeHeading) {
        double cartesianAngle = 90.0 - robocodeHeading;
        if (cartesianAngle < 0.0) {
            cartesianAngle += 360.0;
        }

        return cartesianAngle;
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


}// end of class