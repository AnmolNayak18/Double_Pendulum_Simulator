# Double_Pendulum_Simulator
A Java Swing application that simulates the chaotic motion of a double pendulum system with interactive controls.

This application simulates a double pendulum physics system, which is a classic example of chaotic motion. The simulation allows users to adjust parameters like pendulum length and mass in real-time and observe how these changes affect the pendulum's behavior.
Features

Real-time simulation of double pendulum physics
Interactive controls to adjust pendulum parameters:

Length of both pendulum arms
Mass of both pendulum bobs


Visual motion trail showing the path of the second pendulum
Display of system information (mass, angle, energy)
Start/pause and reset functionality
FPS counter

Getting Started
Prerequisites

Java Development Kit (JDK) 8 or higher
Git (optional, for cloning the repository)

Installation

Clone this repository (or download as ZIP):
git clone https://github.com/yourusername/DoublePendulumSimulation.git

Navigate to the project directory:
cd DoublePendulumSimulation

Compile the Java file:
javac DoublePendulumSimulation.java

Run the application:
java DoublePendulumSimulation


How to Use

Start/Pause: Click the "Start" button to begin the simulation. The same button changes to "Pause" when the simulation is running.
Reset: Click the "Reset" button to return the pendulums to their initial positions.
Adjusting Parameters:

Use the "Pendulum 1 Length" slider to change the length of the first rod
Use the "Pendulum 2 Length" slider to change the length of the second rod
Use the "Pendulum 1 Mass" slider to change the mass of the first bob
Use the "Pendulum 2 Mass" slider to change the mass of the second bob


Information Display:

The simulation shows the total energy of the system
Each pendulum bob displays its mass and angle
The actual FPS (frames per second) is shown in the top right corner



Physics Behind the Simulation
The double pendulum is governed by a system of differential equations. The simulation calculates the angular acceleration of each pendulum based on its current state and updates the position accordingly. The system exhibits chaotic behavior, meaning small changes in initial conditions can lead to vastly different trajectories over time.
Project Structure

DoublePendulumSimulation.java: Main class containing the simulation logic and UI components

DoublePendulumSimulation: Main JFrame that sets up the window and controls
SimulationPanel: JPanel that handles rendering and physics calculations
Pendulum: Class representing each pendulum with properties and drawing methods



Future Enhancements

Save and load simulation states
Export motion trails as images
Additional visualization options (phase space, energy graphs)
More precise physics calculations

Contributing
Contributions are welcome! Please feel free to submit a Pull Request.
License
This project is licensed under the MIT License - see the LICENSE file for details.
Acknowledgments

Inspired by various physics simulations and educational resources
Built using Java Swing for UI components
