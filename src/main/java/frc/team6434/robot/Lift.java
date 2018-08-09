package frc.team6434.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Lift implements Subsystem{

    VictorSP liftMotor;
    DigitalInput limitSwitch;

    public void init()
    {
        liftMotor = new VictorSP(4);
//        limitSwitch = new DigitalInput(4);
    }

    private void setMotorSpeed(double speed)
    {
        liftMotor.set(-speed);
    }

    public void moveUp()
    {
//        setMotorSpeed(0.8);
        setMotorSpeed(0.7);
    }

    public void moveUpAuto()
    {
        setMotorSpeed(1);
    }

    public void moveDown()
    {
        setMotorSpeed(-0.3);
    }

    //Stop lift at current position
    public void liftStop ()
    {
        setMotorSpeed(0.08);
    }

    public void showDashboard()
    {
        SmartDashboard.putNumber("Lift Motor Speed", liftMotor.get());
    }
}
