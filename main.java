import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

public class DoublePendulumSimulation extends JFrame {
    private SimulationPanel simulationPanel;
    private JButton startButton, resetButton;
    private JSlider length1Slider, length2Slider, mass1Slider, mass2Slider;
    private JLabel length1Value, length2Value, mass1Value, mass2Value;
    private Timer timer;
    private boolean isRunning = false;
    private final int FRAME_DELAY = 5;

    public DoublePendulumSimulation() {
        super("Double Pendulum Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        simulationPanel = new SimulationPanel();
        add(simulationPanel, BorderLayout.CENTER);

        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);

        timer = new Timer(FRAME_DELAY, e -> {
            simulationPanel.updateSimulation();
            simulationPanel.repaint();
        });

        JLabel fpsLabel = new JLabel("Target FPS: " + (1000 / FRAME_DELAY));
        fpsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(fpsLabel, BorderLayout.NORTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(6, 1, 5, 5));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        startButton = new JButton("Start");
        resetButton = new JButton("Reset");

        startButton.addActionListener(e -> {
            if (isRunning) {
                isRunning = false;
                timer.stop();
                startButton.setText("Start");
            } else {
                isRunning = true;
                timer.start();
                startButton.setText("Pause");
            }
        });

        resetButton.addActionListener(e -> {
            isRunning = false;
            timer.stop();
            startButton.setText("Start");
            simulationPanel.resetPendulum();
            simulationPanel.repaint();
        });

        buttonPanel.add(startButton);
        buttonPanel.add(resetButton);
        controlPanel.add(buttonPanel);

        controlPanel.add(createSliderPanel("Pendulum 1 Length:", 50, 200, 100, 1, 
            slider -> {
                length1Slider = slider;
                length1Value = new JLabel("100");
                simulationPanel.pendulum1.length = length1Slider.getValue();
                return length1Value;
            }
        ));

        controlPanel.add(createSliderPanel("Pendulum 2 Length:", 50, 200, 80, 1, 
            slider -> {
                length2Slider = slider;
                length2Value = new JLabel("80");
                simulationPanel.pendulum2.length = length2Slider.getValue();
                return length2Value;
            }
        ));

        controlPanel.add(createSliderPanel("Pendulum 1 Mass:", 1, 10, 5, 0.1, 
            slider -> {
                mass1Slider = slider;
                mass1Value = new JLabel("5.0");
                simulationPanel.pendulum1.mass = mass1Slider.getValue() * 0.1;
                return mass1Value;
            }
        ));

        controlPanel.add(createSliderPanel("Pendulum 2 Mass:", 1, 10, 3, 0.1, 
            slider -> {
                mass2Slider = slider;
                mass2Value = new JLabel("3.0");
                simulationPanel.pendulum2.mass = mass2Slider.getValue() * 0.1;
                return mass2Value;
            }
        ));

        return controlPanel;
    }

    private JPanel createSliderPanel(String labelText, int min, int max, double initialValue, double step, 
                                     SliderCreationCallback callback) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(120, 20));
        panel.add(label);

        int sliderMin = (int)(min / step);
        int sliderMax = (int)(max / step);
        int sliderInitial = (int)(initialValue / step);
        
        JSlider slider = new JSlider(sliderMin, sliderMax, sliderInitial);
        JLabel valueLabel = callback.createSlider(slider);
        
        slider.addChangeListener(e -> {
            double value = slider.getValue() * step;
            valueLabel.setText(String.format("%.1f", value));
            
            if (slider == length1Slider) {
                simulationPanel.pendulum1.length = value;
                simulationPanel.calculatePositions();
            } else if (slider == length2Slider) {
                simulationPanel.pendulum2.length = value;
                simulationPanel.calculatePositions();
            } else if (slider == mass1Slider) {
                simulationPanel.pendulum1.mass = value;
            } else if (slider == mass2Slider) {
                simulationPanel.pendulum2.mass = value;
            }
        });

        panel.add(slider);
        panel.add(valueLabel);
        
        return panel;
    }

    private interface SliderCreationCallback {
        JLabel createSlider(JSlider slider);
    }

    public static class Pendulum {
        double angle;
        double angularVelocity;
        double angularAcceleration;
        double length;
        double mass;
        int x, y;
        int bobRadius;
        Color color;

        public Pendulum(double angle, double length, double mass, Color color) {
            this.angle = angle;
            this.length = length;
            this.mass = mass;
            this.angularVelocity = 0;
            this.angularAcceleration = 0;
            this.bobRadius = 15;
            this.color = color;
        }

        public void draw(Graphics g, int pivotX, int pivotY) {
            g.setColor(color);
            
            // Draw rod
            g.drawLine(pivotX, pivotY, x, y);
            
            // Draw bob
            g.fillOval(x - bobRadius, y - bobRadius, bobRadius * 2, bobRadius * 2);
            
            // Draw information
            g.setColor(Color.BLACK);
            g.drawString(String.format("m: %.1f", mass), x + bobRadius, y);
            g.drawString(String.format("θ: %.1f°", Math.toDegrees(angle)), x + bobRadius, y + 15);
        }
    }

    public class SimulationPanel extends JPanel {
        private Pendulum pendulum1;
        private Pendulum pendulum2;
        private final double GRAVITY = 0.25;
        private int pivotX, pivotY;
        private long frameCount = 0;
        private long lastFpsTime = 0;
        private int fps = 0;
        private java.util.List<Point> tracePoints = new java.util.ArrayList<>();
        private final int MAX_TRACE_POINTS = 500;

        public SimulationPanel() {
            setBackground(Color.WHITE);
            resetPendulum();
        }

        public void resetPendulum() {
            pendulum1 = new Pendulum(Math.PI / 4, 100, 0.5, Color.BLUE);
            pendulum2 = new Pendulum(Math.PI / 2, 80, 0.3, Color.RED);
            pivotX = getWidth() / 2;
            pivotY = getHeight() / 3;
            calculatePositions();
            tracePoints.clear();
        }

        public void calculatePositions() {
            pivotX = getWidth() / 2;
            pivotY = getHeight() / 3;
            
            pendulum1.x = pivotX + (int)(pendulum1.length * Math.sin(pendulum1.angle));
            pendulum1.y = pivotY + (int)(pendulum1.length * Math.cos(pendulum1.angle));
            
            pendulum2.x = pendulum1.x + (int)(pendulum2.length * Math.sin(pendulum2.angle));
            pendulum2.y = pendulum1.y + (int)(pendulum2.length * Math.cos(pendulum2.angle));
        }

        public void updateSimulation() {
            double m1 = pendulum1.mass;
            double m2 = pendulum2.mass;
            double l1 = pendulum1.length / 100; // Scale for better physics
            double l2 = pendulum2.length / 100;
            double a1 = pendulum1.angle;
            double a2 = pendulum2.angle;
            double a1_v = pendulum1.angularVelocity;
            double a2_v = pendulum2.angularVelocity;
            
            // Calculate angular accelerations using pendulum physics equations
            double num1 = -GRAVITY * (2 * m1 + m2) * Math.sin(a1);
            double num2 = -m2 * GRAVITY * Math.sin(a1 - 2 * a2);
            double num3 = -2 * Math.sin(a1 - a2) * m2 * (a2_v * a2_v * l2 + a1_v * a1_v * l1 * Math.cos(a1 - a2));
            double den = l1 * (2 * m1 + m2 - m2 * Math.cos(2 * a1 - 2 * a2));
            double a1_a = (num1 + num2 + num3) / den;
            
            num1 = 2 * Math.sin(a1 - a2);
            num2 = a1_v * a1_v * l1 * (m1 + m2);
            num3 = GRAVITY * (m1 + m2) * Math.cos(a1);
            double num4 = a2_v * a2_v * l2 * m2 * Math.cos(a1 - a2);
            den = l2 * (2 * m1 + m2 - m2 * Math.cos(2 * a1 - 2 * a2));
            double a2_a = (num1 * (num2 + num3 + num4)) / den;
            
            // Update velocities and angles
            pendulum1.angularVelocity += a1_a;
            pendulum2.angularVelocity += a2_a;
            pendulum1.angle += pendulum1.angularVelocity;
            pendulum2.angle += pendulum2.angularVelocity;
            
            // Apply damping to simulate air resistance
            pendulum1.angularVelocity *= 0.999;
            pendulum2.angularVelocity *= 0.999;
            
            // Calculate new positions
            calculatePositions();
            
            // Add trace point
            tracePoints.add(new Point(pendulum2.x, pendulum2.y));
            if (tracePoints.size() > MAX_TRACE_POINTS) {
                tracePoints.remove(0);
            }
            
            frameCount++;
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastFpsTime > 1000) {
                fps = (int)frameCount;
                frameCount = 0;
                lastFpsTime = currentTime;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw pivot point
            g.setColor(Color.BLACK);
            g.fillOval(pivotX - 5, pivotY - 5, 10, 10);
            
            // Draw traces
            g.setColor(new Color(200, 200, 255, 128));
            for (int i = 0; i < tracePoints.size() - 1; i++) {
                Point p1 = tracePoints.get(i);
                Point p2 = tracePoints.get(i + 1);
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
            
            // Draw pendulums
            pendulum1.draw(g, pivotX, pivotY);
            pendulum2.draw(g, pendulum1.x, pendulum1.y);
            
            // Draw information
            g.setColor(Color.BLACK);
            g.drawString("Total Energy: " + String.format("%.2f", calculateTotalEnergy()), 10, 20);
            g.drawString("Actual FPS: " + fps, getWidth() - 100, 20);
        }
        
        private double calculateTotalEnergy() {
            double kineticEnergy1 = 0.5 * pendulum1.mass * pendulum1.length * pendulum1.angularVelocity * pendulum1.angularVelocity;
            double kineticEnergy2 = 0.5 * pendulum2.mass * pendulum2.length * pendulum2.angularVelocity * pendulum2.angularVelocity;
            
            double potentialEnergy1 = pendulum1.mass * GRAVITY * pendulum1.length * (1 - Math.cos(pendulum1.angle));
            double potentialEnergy2 = pendulum2.mass * GRAVITY * pendulum2.length * (1 - Math.cos(pendulum2.angle));
            
            return kineticEnergy1 + kineticEnergy2 + potentialEnergy1 + potentialEnergy2;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DoublePendulumSimulation::new);
    }
}
