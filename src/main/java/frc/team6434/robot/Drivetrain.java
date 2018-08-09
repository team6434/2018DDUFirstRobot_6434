package frc.team6434.robot;

import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
public class Drivetrain implements Subsystem {

    double lastError;
    final double encoderRatio = Constants.encoderRatio;

    public ADXRS450_Gyro gyro;
    VictorSP left, right;
    Encoder leftEncoder, rightEncoder;

    public void init()
    {
        right = new VictorSP(0);
        left = new VictorSP(1);
        rightEncoder = new Encoder(2, 3);
        rightEncoder.setDistancePerPulse(1);
        leftEncoder = new Encoder(0,1);
        leftEncoder.setDistancePerPulse(1);
        gyro = new ADXRS450_Gyro();
        gyro.calibrate();
    }

    //sets the speeds of all driving motors
    public void drive(double leftSpeed, double rightSpeed) {
        left.set(-leftSpeed);
        right.set(rightSpeed);
    }

    //teleop driving
    public void arcadeDrive(double x, double y)
    {
        x = x * Math.abs(x);
        y = y * Math.abs(y);

        double left = y - x;
        double right = y + x;
        if (left > 1) {
            left = 1;
        }
        if (right > 1) {
            right = 1;
        }
        drive(-left/0.8, -right/0.8);
        //1.35 for comp

    }

    //Resets gyro
    public void resetGyro()
    {
       gyro.reset();
    }

    //reads gyro (between 0-360)
    public double readGyro()
    {
        return (gyro.getAngle() % 360 + 360) % 360;
    }

    //resets both encoders
    public void resetEncoders() {
        leftEncoder.reset();
        rightEncoder.reset();
    }

    public double getEncoderAvg()
    {
//        return - encoderRatio * (((rightEncoder.get()) + (leftEncoder.get())) / 2);

        return ((encoderRatio * ((rightEncoder.get())/*+(leftEncoder.get())*/)) / -2);
    }

    //adjusts the speed based on how far has been driven
    public void distanceSensitivity(double leftSpeed, double rightSpeed, double currentDistance, double targetDistance)
    {
        final double firstSensitivity = 0.6;
        final double secondSensitivity = 0.9;

        if(currentDistance < 500){
            drive(firstSensitivity*leftSpeed, firstSensitivity*rightSpeed);
        }
        else if(currentDistance < 1000){
            drive(secondSensitivity*leftSpeed, secondSensitivity*rightSpeed);
        }
        else if((targetDistance - currentDistance) < 1000)
        {
            drive(firstSensitivity*leftSpeed, firstSensitivity*rightSpeed);
        }
        else if((targetDistance - currentDistance) < 2400)
        {
            drive(secondSensitivity*leftSpeed, secondSensitivity*rightSpeed);
        }
        else
        {
            drive(leftSpeed, rightSpeed);
        }
    }

    //drives straight using gyro
    public void driveStraight(double speed, double targetAngle, double currentDistance, double targetDistance)
    {
        //sensitivit settings so you can change all 4 instances of it at once
        final double firstSensitivity = 0.85;
        final double secondSensitivity = 0.5;

        double error = calculateError(targetAngle);
        if (error < -10) {
            distanceSensitivity(speed , speed* secondSensitivity, currentDistance, targetDistance);
        }
        else if (error < 0)
        {
            distanceSensitivity(speed, speed * firstSensitivity, currentDistance, targetDistance);
        }
        else if (error < 10)
        {
            distanceSensitivity(speed * firstSensitivity, speed, currentDistance, targetDistance);
        }
        else
        {
            distanceSensitivity(speed * secondSensitivity, speed, currentDistance, targetDistance);
        }
    }

    //calculates the error bewteen the target angle and the current angle
    public double calculateError(double targetAngle)
    {
        double error = readGyro() - targetAngle;

        while (error > 180) {
            error = error - 360;
        }

        while (error < -180) {
            error = error + 360;
        }
        return error;
    }

    //turns to specified angle
    public void turnToAngle(double targetAngle, double speed)
    {
        double error = calculateError(targetAngle);
        if(error < 0)
        {
            drive(speed, -speed);
        }
        else
        {
            drive(-speed, speed);
        }
        this.lastError = error;
    }

    //put dashboard stuff here
    public void showDashboard()
    {
        SmartDashboard.putNumber("Gyro Angle", readGyro());
        SmartDashboard.putNumber("Left Power", left.get());
        SmartDashboard.putNumber("Right Power", right.get());
        SmartDashboard.putNumber("Right Encoder", -rightEncoder.get() * encoderRatio);
        SmartDashboard.putNumber("Encoder Average", getEncoderAvg());
        SmartDashboard.putNumber("Error", lastError);



    }

}
