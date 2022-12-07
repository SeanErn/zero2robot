// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
/** 
 * * For better comments extention (Add above package)
 * * importandt 
 * ! Deprecitated 
 * ? question 
 * TODO:
 * @param myParam (Used to define params)
 */
package frc.robot;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Button;
import frc.robot.Constants.AutoConstants;
import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.OIConstants;
import frc.robot.subsystems.Group_DriveSubsystem.Cmd_MoveForward;
import frc.robot.subsystems.Group_DriveSubsystem.Cmd_MoveReverse;
import frc.robot.subsystems.Group_DriveSubsystem.Subsys_MecanumDrive;
import frc.robot.subsystems.Group_TestFalcon.Cmd_MoveWithJoystick;
import frc.robot.subsystems.Group_TestFalcon.Subsys_FalconTest;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.MecanumControllerCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import java.util.List;

/* 
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems
  private final Subsys_MecanumDrive m_robotDrive = new Subsys_MecanumDrive();
    private final Subsys_FalconTest m_testFalcon = new Subsys_FalconTest(); //TEST NOT IN PROD
  // The driver's controller
  XboxController m_driverController = new XboxController(OIConstants.k_DriverControllerPort); 
  Joystick m_Joystick = new Joystick(OIConstants.k_JoyControllerPort);
  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Configure the button bindings
    configureButtonBindings();
    
    //* Configure default commands
    // Set the default drive command to split-stick arcade drive
    m_robotDrive.setDefaultCommand(
    //Default MecanumDrive command
        new RunCommand(
            () ->
                m_robotDrive.MecanumDrive(
                    m_driverController.getLeftY()*0.3,
                    m_driverController.getRightX()*0.3,
                    m_driverController.getLeftX()*0.3,
                    false),
            m_robotDrive));
    //Default TestFalcon Command 
    m_testFalcon.setDefaultCommand(
        new Cmd_MoveWithJoystick(
            m_testFalcon, 
            () -> m_Joystick.getZ()
        ));
  }
  /**.
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link edu.wpi.first.wpilibj.GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then calling passing it to a
   * {@link JoystickButton}.
   */
  private void configureButtonBindings() {
   
    //* Binds Buttons/Joysticks to commands
    /**Drive at half speed when the right bumper is held 
    * @param Button.kRightBumper.value Sets button imput on xbox controller
   */
    new JoystickButton(m_driverController, Button.kRightBumper.value)
        .whenPressed(() -> m_robotDrive.setMaxOutput(0.5))
        .whenReleased(() -> m_robotDrive.setMaxOutput(1));
    /**Drive forward when a button is clicked
     * @param Button.kA.value Sets button input on xbox controller */    
    new JoystickButton(m_driverController, Button.kA.value)
        .whenPressed(new Cmd_MoveForward(m_robotDrive));
    /**Drive Reverse when a button is clicked
     * @param Button.kB.value Sets button input on xbox controller */    
    new JoystickButton(m_driverController, Button.kB.value)
        .whenPressed(new Cmd_MoveReverse(m_robotDrive));
     /**Drive Forward then Reverse when a button is clicked with a inline sequental command 
     * @param Button.kY.value Sets button input on xbox controller */
    new JoystickButton(m_driverController, Button.kY.value)
        .whenPressed(new SequentialCommandGroup(
            new Cmd_MoveForward(m_robotDrive), 
            new Cmd_MoveReverse(m_robotDrive)
        ));
  }
  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // Create config for trajectory
    TrajectoryConfig config =
        new TrajectoryConfig(
                AutoConstants.k_MaxSpeedMetersPerSecond,
                AutoConstants.k_MaxAccelerationMetersPerSecondSquared)
            // Add kinematics to ensure max speed is actually obeyed
            .setKinematics(DriveConstants.k_DriveKinematics);

    // An example trajectory to follow.  All units in meters.
    Trajectory exampleTrajectory =
        TrajectoryGenerator.generateTrajectory(
            // Start at the origin facing the +X direction
            new Pose2d(0, 0, new Rotation2d(0)),
            // Pass through these two interior waypoints, making an 's' curve path
            List.of(new Translation2d(1, 1), new Translation2d(2, -1)),
            // End 3 meters straight ahead of where we started, facing forward
            new Pose2d(3, 0, new Rotation2d(0)),
            config);

    MecanumControllerCommand mecanumControllerCommand =
        new MecanumControllerCommand(
            exampleTrajectory,
            m_robotDrive::getPose,
            DriveConstants.k_Feedforward,
            DriveConstants.k_DriveKinematics,

            // Position contollers
            new PIDController(AutoConstants.k_PXController, 0, 0),
            new PIDController(AutoConstants.k_PYController, 0, 0),
            new ProfiledPIDController(
                AutoConstants.k_PThetaController, 0, 0, AutoConstants.kThetaControllerConstraints),

            // Needed for normalizing wheel speeds
            AutoConstants.k_MaxSpeedMetersPerSecond,

            // Velocity PID's
            new PIDController(DriveConstants.k_PFrontLeftVel, 0, 0),
            new PIDController(DriveConstants.k_PRearLeftVel, 0, 0),
            new PIDController(DriveConstants.k_PFrontRightVel, 0, 0),
            new PIDController(DriveConstants.k_PRearRightVel, 0, 0),
            m_robotDrive::getCurrentWheelSpeeds,
            m_robotDrive::setDriveMotorControllersVolts, // Consumer for the output motor voltages
            m_robotDrive);

    // Reset odometry to the starting pose of the trajectory.
    m_robotDrive.resetOdometry(exampleTrajectory.getInitialPose());

    // Run path following command, then stop at the end.
    return mecanumControllerCommand.andThen(() -> m_robotDrive.MecanumDrive(0, 0, 0, false));
  }
}
