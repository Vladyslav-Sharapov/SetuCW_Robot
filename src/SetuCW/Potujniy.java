package SetuCW;

import java.awt.Color;
import robocode.*;

public class Potujniy extends Robot{

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
    double eDirection;

    // Targeting variables
    double eX;
    double eY;
    double eDistance;

    int moveCount = 0;

    public void run()
    {

        initialize();
        scanTowardsCentre();
        moveToPosition(400, 400);
        scanTowardsCentre();

        while (true)
        {
            if(moveCount >= 2)
            {
                scanTowardsCentre();
            }
            else
            {
                moveToPosition(465, 465);
                moveToPosition(335, 465);
                moveToPosition(335, 335);
                moveToPosition(465, 335);
            }
        }
    }

    public void onScannedRobot(ScannedRobotEvent sre)
    {
        if(!sre.isSentryRobot()) {
            moveCount = 0;

            eDistance = sre.getDistance();
            eDirection = pHeading - sre.getBearing();
            if (eDirection >= 360) {
                eDirection -= 360;
            }

            this.eX = this.pX + Math.cos(Math.toRadians(eDirection)) * eDistance;
            this.eY = this.pY + Math.sin(Math.toRadians(eDirection)) * eDistance;
            double abc = Math.abs(this.returnDegreesDifference(this.eX, this.eY, this.pX, this.pY, this.pGunHeading));
            if (abc > 1.0F) {
                turnGunLeft(returnDegreesDifference(eX, eY, pX, pY, pGunHeading));
            }

            if (pGunHeat == 0) {
                fire(calculateFirePower());
            }
        }
    }

    /**
     * Calculates the firepower of the robot based on the distance to the target.
     * The firepower is dynamically adjusted to balance between energy conservation
     * and effective damage. The calculation ensures that the firepower remains
     * within a specific range, with higher firepower applied for closer distances
     * and lower firepower for farther distances.
     *
     * @return the calculated firepower value, clamped between 0.1 and 3.0.
     */
    private double calculateFirePower()
    {
        double distance = Math.max(1.0F, eDistance);
        return Math.min(3.0F, Math.max(0.1F, (300.0F / distance) * 3.0F));
    }

    public void onHitRobot(HitRobotEvent hre){
        // this.turnRight(180);
    }

    private double normalizeAngle(double angle)
    {
        return (angle + 180) % 360 - 180;//Normalizes angle between -180 and 180
    }

    public void onHitByBullet(HitByBulletEvent hbbe) {

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
        pGunHeading = convertToProperDegrees(status.getGunHeading());
        pHeading = convertToProperDegrees(status.getHeading());
        pRadarHeading = convertToProperDegrees(status.getRadarHeading());
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
     * Converts a given input angle in degrees to a "proper" angle in accordance
     * with a specific transformation logic. The transformation adjusts the input
     * angle such that standard angles are remapped to match desired values.
     *
     * @param silly_Degrees the input angle in degrees, representing the original
     *                      angle to be converted.
     * @return the properly transformed angle in degrees, normalized to ensure
     *         it is within the range of [0, 360).
     */
    private double convertToProperDegrees(double silly_Degrees)
    {
		/*silly_Degrees == 0 => proper_degrees == 90
		silly_Degrees == 90 => proper_degrees == 0
		silly_Degrees == 270 => proper_degrees == 180
		silly_Degrees == 180 => proper_degrees == 270
		silly_Degrees == 45 => proper_degrees == 45
		silly_Degrees == 135 => proper_degrees == 315
		silly_Degrees == 225 => proper_degrees == 225
		silly_Degrees == 315 => proper_degrees == 135*/

        double proper_degrees = 90 - silly_Degrees;

        if (proper_degrees < 0)
        {
            proper_degrees += 360;
        }
        return proper_degrees;
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
        this.setScanColor(new Color(255, 255, 255)); // Olive
    }

    public void onWin(WinEvent we)
    {
        setBodyColor(Color.YELLOW);
        setGunColor(Color.BLUE);
        setRadarColor(Color.BLUE);
    }

    public void moveToPosition(double endPosX, double endPosY)
    {
        double directionX = endPosX - pX;
        double directionY = endPosY - pY;

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