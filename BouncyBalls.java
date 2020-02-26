/**
   Class: CST 183
   Assignment: Programming Assignment 9
   Name: Brandon P. Keyes
   Description: An applet that simulates bouncing balls.
   Date: December 12 2018
*/

import java.awt.*;
import java.awt.event.*; 
import javax.swing.*;
import javax.swing.event.*;
import java.util.ArrayList; 

public class BouncyBalls extends JApplet
{
   // Constants.
   private final int APPLET_WIDTH = 900; // Width for the applet.
   private final int APPLET_HEIGHT = 600; // Height for the applet.
   private final int INITIAL_TIME_DELAY = 10; // Time, in miliseconds, between each simulation tick.
   private final int INITIAL_BALL_SIZE = 35; // Size for each ball.
   private final int INITIAL_GRAVITY_VALUE = 98; // Initial gravity value.
   private final int INITIAL_FRICTION_VALUE = 5; // Initial friction value.
   
   // Objects.
   private JPanel control_panel; // JPanel to contain controls for the simulation.
   private JPanel drawing_canvas; // JPanel to paint our graphics on.
   private JCheckBox gravity_checkbox; //  Toggle for gravity.
   private JCheckBox friction_checkbox; // Toggle for friction.
   private JCheckBox ball_collision_checkbox; // Toggle for collision between balls.
   private JSlider gravity_slider; // Slider for gravity value.
   private JSlider friction_slider; // Slider for friction value.
   private JSlider ball_size_slider; // Silder for ball size.
   private JSlider timer_slider; // Slider for timer delay.
   private JButton reset_button; // Button to reset simulation.
   private Timer timer; // Timer object to time simulation logic.
   private ArrayList<Ball> ball_list; // Global array for all ball objects in simulation.
   private Ball ball_being_dragged; // Current ball being dragged in simulation.
   private ArrayList<MouseEvent> mouse_events; // List of last mouse events in simulation.
   
   /**
      Initializes applet. Builds and adds JPanels for controls and graphics, allocates an ArrayList for the balls in the
      simulation, creates and starts a timer to start the simulation.
   */
   public void init()
   {
      // Resize the applet.
      resize(APPLET_WIDTH, APPLET_HEIGHT);
      
      // Build and add control panel to applet.
      buildControlPanel();
      add(control_panel, BorderLayout.NORTH);
      
      // Build and add drawing canvas to applet.
      drawing_canvas = new DrawingCanvas();
      drawing_canvas.setBackground(Color.WHITE);
      drawing_canvas.addMouseListener(new MouseListener());
      drawing_canvas.addMouseMotionListener(new MouseMotionListener());
      add(drawing_canvas, BorderLayout.CENTER);
      
      // Allocate list for ball objects in simulation.
      ball_list = new ArrayList<Ball>();
      
      // Start simulation.
      timer = new Timer(INITIAL_TIME_DELAY, new SimulationCycle());
      timer.start();
   }
   
   /**
      Builds the control panel for the applet.
   */
   private void buildControlPanel()
   {
      control_panel = new JPanel();
      
      // Add gravity control to control panel.
      JPanel menu_item = new JPanel(new BorderLayout());
      JLabel title = new JLabel("Gravity");
      title.setHorizontalAlignment(SwingConstants.CENTER);
      menu_item.add(title, BorderLayout.NORTH);
      gravity_slider  = new JSlider(-50, 196, INITIAL_GRAVITY_VALUE);
      gravity_checkbox = new JCheckBox("Enable gravity");
      gravity_checkbox.setHorizontalAlignment(SwingConstants.CENTER);
      menu_item.add(gravity_slider, BorderLayout.CENTER);
      menu_item.add(gravity_checkbox, BorderLayout.SOUTH);
      control_panel.add(menu_item); 
      
      // Add friction control to control panel.
      menu_item = new JPanel(new BorderLayout());
      title = new JLabel("Friction");
      title.setHorizontalAlignment(SwingConstants.CENTER);
      menu_item.add(title, BorderLayout.NORTH);
      friction_slider  = new JSlider(0, 10, INITIAL_FRICTION_VALUE);
      menu_item.add(friction_slider, BorderLayout.CENTER);
      friction_checkbox = new JCheckBox("Enable friction");
      friction_checkbox.setHorizontalAlignment(SwingConstants.CENTER);
      menu_item.add(friction_checkbox, BorderLayout.SOUTH);
      control_panel.add(menu_item); 
      
      // Add ball size and ball collision controls to control panel.
      menu_item = new JPanel(new BorderLayout());
      title = new JLabel("Ball Size");
      title.setHorizontalAlignment(SwingConstants.CENTER);
      menu_item.add(title, BorderLayout.NORTH);
      ball_size_slider = new JSlider(10, 100, INITIAL_BALL_SIZE);
      menu_item.add(ball_size_slider, BorderLayout.CENTER);
      ball_collision_checkbox = new JCheckBox("Enable ball collisions");
      ball_collision_checkbox.setSelected(true);
      menu_item.add(ball_collision_checkbox, BorderLayout.SOUTH);
      control_panel.add(menu_item); 
      
      // Add simulation delay and reset button controls to control panel.
      menu_item = new JPanel(new BorderLayout());
      title = new JLabel("Simulation Delay");
      title.setHorizontalAlignment(SwingConstants.CENTER);
      menu_item.add(title, BorderLayout.NORTH);
      timer_slider = new JSlider(INITIAL_TIME_DELAY, 100, INITIAL_TIME_DELAY);
      timer_slider.addChangeListener(new SliderListener());
      menu_item.add(timer_slider, BorderLayout.CENTER);
      reset_button = new JButton("Reset");
      reset_button.addActionListener(new ButtonListener());
      menu_item.add(reset_button, BorderLayout.SOUTH);
      control_panel.add(menu_item);   
   }
   
   /**
      Custom JPanel that we draw our graphics onto.
   */
   private class DrawingCanvas extends JPanel
   {
      /**
         Executed everytime this component is painted in our applet.
         @param g The Graphics object for this component.
      */
      public void paintComponent(Graphics g)
      {
         super.paintComponent(g);
         
         // Paint each ball in the ball list.
         for (Ball ball : ball_list)
         {
            ball.paint(g);
         }
      }
   }
   
   /**
      Implementation of the ActionListener interface for our timer. This contains the logic for our simulation.
      Each time the timer goes off this logic will execute.
   */
   private class SimulationCycle implements ActionListener
   {
      /**
         Executed everytime the timer goes off. Contains simulation logic.
         @param e The ActionEvent sent.
      */
      public void actionPerformed(ActionEvent e)
      {
         // For each ball in our simulation.
         for (Ball ball : ball_list)
         {  
            // Move ball.
            ball.setX(ball.getX() + ball.getDX());
            ball.setY(ball.getY() + ball.getDY());
            
            // Make sure ball is contained in the drawing canvas.
            if (ball.getX() < 0)
            {
               ball.setX(0);
               ball.setDX(ball.getDX() * -1);
               impact(ball);
            }
            else if (ball.getX() + ball.getSize() > drawing_canvas.getWidth())
            {
               ball.setX(drawing_canvas.getWidth() - ball.getSize());
               ball.setDX(ball.getDX() * -1);
               impact(ball);
            }
            if (ball.getY() < 0)
            {
               ball.setY(0);
               ball.setDY(ball.getDY() * -1);
               impact(ball);
            }
            else if (ball.getY() + ball.getSize() > drawing_canvas.getHeight())
            {
               ball.setY(drawing_canvas.getHeight() - ball.getSize());
               ball.setDY(ball.getDY() * -1);
               impact(ball);
            }
            
            
            // Apply gravity if it is enabled;
            if (gravity_checkbox.isSelected())
            {  
               // If we are already at bottom of drawing canvas then stop the pull of gravity.
               if (ball.getY() + ball.getSize() < drawing_canvas.getHeight())
                  ball.setDY(ball.getDY() + gravity_slider.getValue() / 100.00 );     
            }
            
            // For each ball in the simulation check if we are colliding with it.
            for (int i = ball_list.indexOf(ball) + 1; i < ball_list.size(); i++)
            {
               Ball other_ball = ball_list.get(i);
               if (ball.isCollidingWith(other_ball) && ball_collision_checkbox.isSelected())
               {
                  // If we are colliding and if ball collisions are enabled then process the collisions and impact ball speeds.
                  ball.updateCollision(other_ball);
                  impact(ball);
                  impact(other_ball);
               }
            }
         }
         repaint(); // Paint graphics.
      }
      
      /**
         Impacts ball's speed if friction is enabled.
         @param ball The ball to impact.
      */
      private void impact(Ball ball)
      {
         if (friction_checkbox.isSelected())
         {
            ball.setDX(ball.getDX() * ((1.0 - (friction_slider.getValue() / 100.00)) - 0.5));
            if (Math.abs(ball.getDX()) < 0.98)
               ball.setDX(0);
            ball.setDY(ball.getDY() * ((1.0 - (friction_slider.getValue() / 100.00)) - 0.5));
            if (Math.abs(ball.getDY()) < 0.98)
               ball.setDY(0);
         }
      }
   }
   
   /**
      Implementation of the MouseListener interface for our simulation.
   */
   private class MouseListener extends MouseAdapter
   {
      /**
         Executed every time the mouse is clicked.
         @param e The MouseEvent sent.
      */
      public void mouseClicked(MouseEvent e)
      {
         // If mouse is clicked then add another ball to the simulation.
         ball_list.add(new Ball(e.getX(), e.getY(), ball_size_slider.getValue()));
      }
      
      /**
         Executed every time the mouse is pressed.
         @param e The MouseEvent sent.
      */
      public void mousePressed(MouseEvent e)
      {
         // If mouse is pressed then check if it is over a ball. If so then set it to be dragged and set it's coordinates to be
         // the same as the mouse.
         
         ball_being_dragged = null;
         // Keep track of last mouse events to generate a release velocity for the ball.
         mouse_events = new ArrayList<>();
         mouse_events.add(e);
         for (Ball ball : ball_list)
         {
            if (((e.getX() >= ball.getX()) && (e.getX() <= (ball.getX() + ball.getSize()))) &&
                ((e.getY() >= ball.getY()) && (e.getY() <= (ball.getY() + ball.getSize()))))
            {
               ball_being_dragged = ball;  
            }
         }
         
         if (ball_being_dragged != null)
         {
            ball_being_dragged.setDX(0);
            ball_being_dragged.setDY(0);
         }
      }
      
      /**
         Executed every time the mouse is released.
         @param e The MouseEvent sent.
      */
      public void mouseReleased(MouseEvent e)
      {
         // Calculate a new ball velocity using the last mouse event positions and set this new velocity on the ball
         // and stop dragging the ball.
         double mouse_dx;
         double mouse_dy;
         mouse_dx = mouse_events.get(mouse_events.size()-1).getX() - mouse_events.get(0).getX();
         mouse_dy = mouse_events.get(mouse_events.size()-1).getY() - mouse_events.get(0).getY();
         
         if (ball_being_dragged != null)
         {
            ball_being_dragged.setDX(mouse_dx);
            ball_being_dragged.setDY(mouse_dy);
         }
         
         ball_being_dragged = null;
      }
   }
   
   /**
      Implementation of the MouseMotionListener interface for our simulation.
   */
   private class MouseMotionListener extends MouseMotionAdapter
   {
      /**
         Executed everytime the mouse is dragged.
         @param e The mouse event sent.
      */
      public void mouseDragged(MouseEvent e)
      {
         // Keep track of last mouse events to generate a release velocity for the ball.
         if (mouse_events.size() >= 3)
            mouse_events.remove(0);
         mouse_events.add(e);
         
         // If we aren't currently dragging a ball then check if we are over one and catch it.
         for (Ball ball : ball_list)
         {
            if (((e.getX() >= ball.getX()) && (e.getX() <= (ball.getX() + ball.getSize()))) &&
                ((e.getY() >= ball.getY()) && (e.getY() <= (ball.getY() + ball.getSize()))))
            {
               if (ball_being_dragged == null)
                  ball_being_dragged = ball;
            }
         }
         
         // If we are dragging a ball then set it's new position and velocity.
         if (ball_being_dragged != null)
         {
            ball_being_dragged.setDX(0);
            ball_being_dragged.setDY(0);
            ball_being_dragged.setX(e.getX() - ball_being_dragged.getSize() / 2);
            ball_being_dragged.setY(e.getY() - ball_being_dragged.getSize() / 2);
         }
      }
   }
   
   /**
      Implementation of the ChangeListener interface for our timer slider.
   */
   private class SliderListener implements ChangeListener
   {
      /**
         Executed everytime the timer slider is moved.
         @param e The ChangeEvent sent.
      */
      public void stateChanged(ChangeEvent e)
      {
         // Set new timer delay and restart the timer.
         timer.setDelay(timer_slider.getValue());
         timer.restart();
      }
   }
   
   /**
      Implementation of the ButtonListener interface for our reset button.
   */
   private class ButtonListener implements ActionListener
   {
      /**
         Executed every time the reset button is pressed.
         @param e The ActionEvent sent.
      */
      public void actionPerformed(ActionEvent e)
      {
         // Remove all balls from the simulation and restart the timer.
         ball_list.clear();
         timer.restart();
      }
   }
}