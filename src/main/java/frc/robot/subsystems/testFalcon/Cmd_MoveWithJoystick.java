// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.testFalcon;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class Cmd_MoveWithJoystick extends CommandBase {
  /** Creates a new Cmd_MoveWithJoystick. */
  private final testFalconSys m_FalconSys;
  private DoubleSupplier joyValue;
  public Cmd_MoveWithJoystick( testFalconSys falconSys, DoubleSupplier joyValue) {
    // Use addRequirements() here to declare subsystem dependencies.
    m_FalconSys = falconSys;
    addRequirements(m_FalconSys);
    this.joyValue = joyValue;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_FalconSys.setPrecentOutput(0);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    m_FalconSys.setPrecentOutput(joyValue.getAsDouble());
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
