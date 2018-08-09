package frc.team6434.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Timer;

import edu.wpi.first.wpilibj.Servo;

public class Robot extends IterativeRobot {

    Step[] currentStrategy;

    int currentStep;
    boolean holdCube = false;
    boolean fixCube = false;
    final double triggerThreshold = Constants.triggerThreshold;

    Assistive_Climb assistive_climb;
    XboxController controller;
    Drivetrain drivetrain;
    Intake intake;
    Lift lift;
    Strategy strategy;
    Timer t1, t2;
    Servo pin = new Servo(3);

    @Override
    public void robotInit()
    {
        assistive_climb = new Assistive_Climb();
        drivetrain = new Drivetrain();
        controller = new XboxController(0);
        intake = new Intake();
        lift = new Lift();
        strategy = new Strategy();
        t1 = new Timer();
        t2 = new Timer();
        drivetrain.init();
        lift.init();
        intake.init();
//        assistive_climb.init();
        strategy.init();
        CameraServer.getInstance().startAutomaticCapture();
    }

    @Override
    public void disabledInit() { }

    @Override
    public void autonomousInit()
    {
        drivetrain.resetEncoders();
        drivetrain.resetGyro();

        currentStrategy = strategy.pickStrategy();
        currentStep = 0;
        currentStrategy[currentStep].begin(drivetrain, lift, intake);
    }

    @Override
    public void autonomousPeriodic()
    {
        strategy.showDashboard();
        drivetrain.showDashboard();
        intake.showDashboard();
        lift.showDashboard();

        if (currentStep < currentStrategy.length) {
            if (currentStrategy[currentStep].progress(drivetrain, lift, intake)) {
                currentStep = currentStep + 1;
                if (currentStep < currentStrategy.length)
                {
                    currentStrategy[currentStep].begin(drivetrain, lift, intake);
                }
            }
        }
    }


    @Override
    public void teleopInit()
    {
//        astTimer.start();
    }

    @Override
    public void testInit() { }

    @Override
    public void disabledPeriodic()
    {
        strategy.showDashboard();
    }

    @Override
    public void teleopPeriodic()
    {

        Hand LEFT = Hand.kLeft;
        Hand RIGHT = Hand.kRight;

        lift.showDashboard();
        intake.showDashboard();
        drivetrain.showDashboard();


        //Drivetrain
        drivetrain.arcadeDrive(controller.getX(LEFT), controller.getY(LEFT));

        //Lift
        if (controller.getBButton())
        {
//            t1.start();
            lift.moveUp();
//            if (t1.get() >= 3)
//            {
//                lift.liftStop();
//                t1.stop();
//                t2.reset();
//            }
        }
        else if (controller.getAButton())
        {
//            t2.start();
            lift.moveDown();
//            if (t2.get() >= 3)
//            {
//                lift.liftStop();
//                t2.stop();
//                t1.reset();
//            }
        }
        else
        {
            lift.liftStop();
        }

        //Intake
        if (controller.getBumper(LEFT))
        {
            fixCube = true;
            intake.fixCube();
        }
        else
        {
            fixCube = false;
            intake.stopFixCube();
            if (controller.getTriggerAxis(LEFT) > triggerThreshold)
            {
                intake.getCube();
                holdCube = true;
            }
            else if (controller.getYButton())
            {
                holdCube = false;
            }
            else if (controller.getTriggerAxis(RIGHT) > triggerThreshold)
            {
                intake.ejectCubeSlow();
                holdCube = false;
            }
            else if (controller.getBumper(RIGHT))
            {
                intake.ejectCubeFast();
                holdCube = false;
            }
            else if (holdCube)
            {
                intake.keepCube();
            }
            else
            {
                intake.intakeStop();
            }
        }


        //Assitive Climb

        if ((controller.getRawButton(7)) && (controller.getRawButton(8)))
        {
            pin.set(0.18);
//            assistive_climb.extend();
        }
        if (controller.getXButton()) {
            pin.set(0.8);
        }



//        if (controller.getXButton())
//        {
//            assistive_climb.climbUp();
//        }
//        else {
//            assistive_climb.climbStop();
//        }

    }

    @Override
    public void testPeriodic()
    {
        if (controller.getXButton())
        {
            pin.set(0.8);
//            assistive_climb.extend();
        }
        if (controller.getYButton())
        {
            pin.set(0.18);
        }
        strategy.showDashboard();

    }
}