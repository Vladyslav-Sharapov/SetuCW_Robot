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
    int moveCount2 = 0;

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
                squareMove();
            }
            moveCount++;

        }
    }

    /**
     *
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
     *
     * @param sre
     */
    public void onScannedRobot(ScannedRobotEvent sre)
    {
        if(!sre.isSentryRobot()) {
            moveCount = 0;

            eDistance = sre.getDistance();
            eDirection = pHeading - sre.getBearing();
            if (eDirection >= 360) {
                eDirection -= 360;
            }

            eX = pX + Math.cos(Math.toRadians(eDirection)) * eDistance;
            eY = pY + Math.sin(Math.toRadians(eDirection)) * eDistance;
            double abc = Math.abs(returnDegreesDifference(eX, eY, pX, pY, pGunHeading));
            if (abc > 1.0F) {
                turnGunLeft(returnDegreesDifference(eX, eY, pX, pY, pGunHeading));
            }

            if (pGunHeat == 0) {
                fire(calculateFirePower());
            }
        }
    }

    /**
     *
     * @return
     */
    private double calculateFirePower()
    {
        double distance = Math.max(1.0F, eDistance);
        return Math.min(3.0F, Math.max(0.1F, (300.0F / distance) * 3.0F));
    }

    /**
     *
     * @param se
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
     *
     */
    private void scanTowardsCentre()
    {
        double degreesDifference = returnDegreesDifference((BATTLE_FIELD_WIDTH / 2), (BATTLE_FIELD_HEIGHT / 2), pX, pY, pRadarHeading);
        turnRadarLeft(360 * (degreesDifference / Math.abs(degreesDifference)));
    }

    /**
     *
     * @param endDegree
     * @param startDegree
     * @return
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
     *
     * @param endX
     * @param endY
     * @param originX
     * @param originY
     * @param currentHeading
     * @return
     */
    private double returnDegreesDifference(double endX, double endY, double originX, double originY, double currentHeading)
    {
        double relativeX = endX - originX;
        double relativeY = endY - originY;
        double pointDegrees = Math.toDegrees(Math.atan2(relativeY, relativeX));
        return getTurnAmount(pointDegrees, currentHeading);
    }

    /**
     *
     * @param degrees
     * @return
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
     *
     */
    private void initialize() {
        // Let
        setAdjustGunForRobotTurn(true);

        // Set robot colors
        setBodyColor(new Color(0x00, 0x00, 0x00)); // Black
        setGunColor(new Color(0x32, 0x00, 0x00)); // Dark Red
        setRadarColor(new Color(0xFF, 0x00, 0x00)); // Red
        setBulletColor(new Color(0xFF, 0xD3, 0x9B)); // Burly wood
        setScanColor(new Color(255, 255, 255)); // White
    }

    /**
     *
     * @param we
     */
    public void onWin(WinEvent we)
    {
        setBodyColor(Color.YELLOW);
        setGunColor(Color.BLUE);
        setRadarColor(Color.BLUE);
    }

    /**
     *
     * @param endPosX
     * @param endPosY
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