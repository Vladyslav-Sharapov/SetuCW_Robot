package SetuCW;

import java.awt.Color;
import robocode.*;

public class Potujniy extends Robot{

    // Battlefield dimensions
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

    // Move count variables
    int moveCount = 0;
    int moveCount2 = 0;

    public void run()
    {

        initialize();
        scanTowardsCentre();
        moveToPosition(400, 400);
        scanTowardsCentre();

        while (true)
        {
            // If robot has moved 2 times and hasn't seen any enemies
            // it will scan towards the centre of the battlefield
            if(moveCount >= 2)
            {
                scanTowardsCentre();
            }
            else
            {
                squareMove();
            }
            moveCount++;

        }
    }

    /**
     * Method for moving robot in a square pattern, when sentry border wall is 300
     */
    private void squareMove()
    {
        switch (moveCount2) {
            case 0:
                moveToPosition(465, 465);
                moveCount2++;
                break;
            case 1:
                moveToPosition(335, 465);
                moveCount2++;
                break;
            case 2:
                moveToPosition(335, 335);
                moveCount2++;
                break;
            case 3:
                moveToPosition(465, 335);
                moveCount2 = 0;
                break;

        }

    }

    /**
     * Scanned robot event handler
     * @param sre Shorter for ScannedRobotEvent
     */
    public void onScannedRobot(ScannedRobotEvent sre)
    {
        if(!sre.isSentryRobot()) {
            moveCount = 0; // if robot is not a sentry robot, reset move count

            // Get enemy position
            eDistance = sre.getDistance();
            eDirection = pHeading - sre.getBearing();
            if (eDirection >= 360) { // Correcting for negative values
                eDirection -= 360;
            }

            // Get enemy X & Y position using trigonometry
            eX = pX + Math.cos(Math.toRadians(eDirection)) * eDistance;
            eY = pY + Math.sin(Math.toRadians(eDirection)) * eDistance;

            // Added this because robot doesn't move if his radar is watching right into the center of enemy robot
            double angleDifference = Math.abs(returnDegreesDifference(eX, eY, pX, pY, pGunHeading));
            if (angleDifference > 1.0F) {
                turnGunLeft(returnDegreesDifference(eX, eY, pX, pY, pGunHeading));
            }

            // Small heat check
            if (pGunHeat == 0) {
                fire(calculateFirePower());
            }
        }
    }

    /**
     * Method for calculating the firepower based on the distance to the enemy
     * Where 0.1 is the minimum firepower and 3.0 is the maximum firepower
     * 300 where is minimum distance for calculation
     * @return Firepower value
     */
    private double calculateFirePower()
    {
        double distance = Math.max(1.0F, eDistance);
        return Math.min(3.0F, Math.max(0.1F, (300.0F / distance) * 3.0F));
    }

    /**
     * Status event handler
     * @param se shorter for StatusEvent
     */
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
     * Method for scanning towards the centre of the battlefield
     */
    private void scanTowardsCentre()
    {
        double degreesDifference = returnDegreesDifference((BATTLE_FIELD_WIDTH / 2), (BATTLE_FIELD_HEIGHT / 2), pX, pY, pRadarHeading);
        turnRadarLeft(360 * (degreesDifference / Math.abs(degreesDifference)));
    }

    /**
     * Method to calculate the amount of degrees to turn after calculating the difference in degrees
     * Uses only for positive values
     * @param endDegree
     * @param startDegree
     * @return Amount of degrees to turn
     */
    private double getTurnAmount(double endDegree, double startDegree)
    {
        double turnAmount = endDegree - startDegree;

        if (turnAmount > 180)
        {
            turnAmount -= 360;
        }
        else if (turnAmount < -180)
        {
            turnAmount += 360;
        }

        return turnAmount;
    }

    /**
     * Method to calculate the difference in degrees between two points
     * @param endX X coordinate of the end point
     * @param endY Y coordinate of the end point
     * @param originX X coordinate of the origin point
     * @param originY Y coordinate of the origin point
     * @param currentHeading Current heading of the robot
     * @return Difference in degrees between the two points
     */
    private double returnDegreesDifference(double endX, double endY, double originX, double originY, double currentHeading)
    {
        double relativeX = endX - originX;
        double relativeY = endY - originY;
        double pointDegrees = Math.toDegrees(Math.atan2(relativeY, relativeX));
        return getTurnAmount(pointDegrees, currentHeading);
    }

    /**
     * Convert degrees to proper format
     * Where 90 degrees is North
     * 180 degrees is West
     * 270 degrees is South
     * 360 or 0 degrees is East
     * @param degrees Angle in degrees
     * @return Angle in proper format
     */
    private double convertToProperDegrees(double degrees)
    {
        double properDegrees = 90 - degrees;

        if (properDegrees < 0)
        {
            properDegrees += 360;
        }
        return properDegrees;
    }

    /**
     * Initialize robot settings
     */
    private void initialize() {
        // Let robot do a turn without adjusting gun
        setAdjustGunForRobotTurn(true);

        // Set robot colors
        setBodyColor(new Color(0x00, 0x00, 0x00)); // Black
        setGunColor(new Color(0x32, 0x00, 0x00)); // Dark Red
        setRadarColor(new Color(0xFF, 0x00, 0x00)); // Red
        setBulletColor(new Color(0xFF, 0xD3, 0x9B)); // Burly wood
        setScanColor(new Color(255, 255, 255)); // White
    }

    /**
     * Win event handler
     * @param we
     */
    public void onWin(WinEvent we)
    {
        setBodyColor(Color.YELLOW);
        setGunColor(Color.BLUE);
        setRadarColor(Color.BLUE);
    }

    /**
     * Method to move robot to a position
     * @param endPosX X coordinate of the end position
     * @param endPosY Y coordinate of the end position
     *                If X 400 and Y 400, robot will move to the center of the battlefield
     */
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