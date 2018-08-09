package frc.team6434.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public abstract class Step
{
    abstract void begin(Drivetrain drivetrain, Lift lift, Intake intake);
    abstract boolean progress(Drivetrain drivetrain, Lift lift, Intake intake);
}


//step for driving straight
class Straight extends Step
{
    Timer straightTimer = new Timer();

    double initialAngle;
    final double distance;
    final double speed;

    Straight(double distance, double speed)
    {
        this.distance = distance;
        this.speed = speed;
    }

    void begin(Drivetrain drivetrain, Lift lift, Intake intake)
    {
        drivetrain.resetEncoders();
        straightTimer.start();
        initialAngle = drivetrain.readGyro();
    }

    boolean progress(Drivetrain drivetrain, Lift lift, Intake intake)
    {
        if (straightTimer.get()  > distance)
        {
//            intake.intakeStop();
            drivetrain.drive(0,0);
            return true;
        }
//        intake.getCube();
        drivetrain.driveStraight(speed, initialAngle, straightTimer.get(), distance);
        return false;
    }
}


class StraightLift extends Step
{
    Timer driveTimer = new Timer();
    Timer raiseTimer = new Timer();
    double initialAngle;
    final double distance;
    final double speed;
    boolean flagOne;
    boolean flagTwo;

    final double sw = 2;
    final double lf = 5;

    StraightLift(double distance, double speed)
    {
        this.distance = distance;
        this.speed = speed;
    }

    void begin(Drivetrain drivetrain, Lift lift, Intake intake)
    {
        drivetrain.resetEncoders();
        initialAngle = drivetrain.readGyro();
        flagOne = false;
        flagTwo = false;
        driveTimer.start();
        raiseTimer.start();
    }

    boolean progress(Drivetrain drivetrain, Lift lift, Intake intake)
    {
        if (driveTimer.get()  > distance)
        {
            drivetrain.drive(0,0);
            flagOne = true;
        }
        if(raiseTimer.get() > sw)
        {
            lift.liftStop();
            flagTwo = true;
        }
        if(flagOne == true && flagTwo == true)
        {
            return true;
        }
        if(!flagTwo) {
            lift.moveUpAuto();
        }
        if(!flagOne) {
            drivetrain.driveStraight(speed, initialAngle, drivetrain.getEncoderAvg(), distance);
        }
        return false;
    }
}

//step for turning
class Turn extends Step
{
    final double targetAngle;
    final double speed;
    final double tolerance = 3;

    Turn(double targetAngle, double speed)
    {
        this.targetAngle = targetAngle;
        this.speed = speed;
    }

    void begin(Drivetrain drivetrain, Lift lift, Intake intake) { }

    boolean progress(Drivetrain drivetrain, Lift lift, Intake intake)
    {
        double error = drivetrain.calculateError(targetAngle);

        if ((error  >  - tolerance) && (error  <  tolerance))
        {
            intake.intakeStop();
            drivetrain.drive(0,0);
            return true;
        }
        intake.getCube();
        drivetrain.turnToAngle(targetAngle, speed);
        return false;
    }
}


//step for raising the lift
class Raise extends Step
{
    final double sw = 3;
    final double lf = 5;

    Timer raiseTimer = new Timer();
//    boolean limitSwitch = lift.limitSwitch;

    Raise(){}

    void begin(Drivetrain drivetrain, Lift lift, Intake intake)
    {
        raiseTimer.start();
    }

    boolean progress(Drivetrain drivetrain, Lift lift, Intake intake)
    {
        SmartDashboard.putNumber("Raise Timer", raiseTimer.get());
        if(raiseTimer.get() > sw)
        {
            lift.liftStop();
            return true;
        }
        lift.moveUp();
        return false;
    }
}


//step for ejecting the cube (2 secs)
class Eject extends Step
{
    Timer ejectTimer = new Timer();

    Eject() {}

    void begin(Drivetrain drivetrain, Lift lift, Intake intake)
    {
        ejectTimer.start();
    }

    boolean progress(Drivetrain drivetrain, Lift lift, Intake intake)
    {
        SmartDashboard.putNumber("Eject Timer", ejectTimer.get());
        if(ejectTimer.get() > 2.0)
        {
            lift.liftStop();
            intake.intakeStop();
            return true;
        }
        lift.liftStop();
        intake.ejectCubeSlow();
        return false;
    }



//    class LowerIntake extends Step
//{
//    final double forwardTime = Constants.forwardTime;
//    final double backwardTime = Constants.backwardTime;
//    Timer driveTimer = new Timer();
////    boolean currentlyForward;
//
//    LowerIntake(){}
//
//    void begin(Drivetrain drivetrain, Lift lift, Intake intake)
//    {
////        currentlyForward = false;
//        driveTimer.reset();
//        driveTimer.start();
//    }
//
//    boolean progress(Drivetrain drivetrain, Lift lift, Intake intake)
//    {
////        if (currentlyForward == false)
////        {
////            currentlyForward = true;
////        }
//        if(driveTimer.get() < forwardTime)
//        {
//            drivetrain.driveStraight(0.5, 0, drivetrain.getEncoderAvg(), )
//        }
//        else if (driveTimer.get() < forwardTime + backwardTime)
//        {
//            getCube();
//        }
//        else
//        {
//            keepCube();
//        }
//    }
//}
}