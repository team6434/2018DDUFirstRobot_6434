package frc.team6434.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake {

    VictorSP intakeMotorLeft, intakeMotorRight;
    DigitalInput intakeLimitSwitch;

    final double ejectTime = Constants.ejectTime;
    final double intakeTime = Constants.intakeTime;
    boolean currentlyFixing = false;
    Timer fixTimer = new Timer();

    public void init()
    {
        intakeMotorLeft = new VictorSP(7);
        intakeMotorRight = new VictorSP(8);
    }

    //set speed of both intake motors
    private void intakeSpeed (double speed)
    {
        SmartDashboard.putNumber("Intake speed:", speed);
        
        intakeMotorLeft.set(-speed);
        intakeMotorRight.set(-speed);
    }

    //Get the cube
    public void getCube()
    {
        intakeSpeed(0.60);
    }

    //For keep in the cube while driving
    public void keepCube()
    {
        intakeSpeed(0.25);
    }

    //Ejects the cube fast
    public void ejectCubeFast()
    {
        intakeSpeed(-0.8);
    }

    //Ejects the cube slow
    public void ejectCubeSlow()
    {
        intakeSpeed(-0.4);
    }

    //Stops intake
    public void intakeStop()
    {
        intakeSpeed(0);
    }


    public void fixCube()
    {
        if (currentlyFixing == false)
        {
            currentlyFixing = true;
            fixTimer.reset();
            fixTimer.start();
        }
        if(fixTimer.get() < ejectTime)
        {
            ejectCubeFast();
        }
        else if (fixTimer.get() < ejectTime + intakeTime)
        {
            getCube();
        }
        else
        {
            keepCube();
        }

    }

    public void stopFixCube()
    {
        currentlyFixing = false;

        intakeStop();
        fixTimer.stop();

    }

    public void showDashboard()
    {
        SmartDashboard.putNumber("Left Intake Power", intakeMotorLeft.get());
        SmartDashboard.putNumber("Right Intake Power", intakeMotorRight.get());
    }


}
