/**
   Class: CST 183
   Assignment: Programming Assignment 9
   Name: Brandon P. Keyes
   Description: An implementation of a Ball class that contains information for it's position, velocity, size and color as well as functionality
   to process collisions between other balls and to paint it on a Graphics object.
   Date: December 12 2018
*/

import java.awt.*;
import javax.swing.*;
import java.util.Random;

public class Ball
{
   private double x; // X position for ball.
   private double y; // Y position for ball.
   private double dx; // X velocity component for ball.
   private double dy; // Y velocity component for ball.
   private double size; // Size of ball.
   private Color color; // Color of ball.
   
   /**
      Ball constructor. Sets it's position, size, a random velocity, and a random color.
      @param x The ball's initial x coordinate.
      @param y The ball's initial y coordinate.
      @param The ball's size.
   */      
   public Ball(int x, int y, int size)
   {
      Random random = new Random();
      
      // Set position and size.
      this.x = (double)x;
      this.y = (double)y;
      this.size = (double)size;
      
      // Set random velocity x and y components between -10 and 10.
      dx = random.nextInt(20) - 10;
      dy = random.nextInt(20) - 10;
      
      // Set a random color.
      float red = random.nextFloat();
      float green = random.nextFloat();
      float blue = random.nextFloat();
      color = new Color(red, green, blue);
   }
   
   /**
      Paints the ball onto a Graphics object.
      @param g The Graphics object to draw onto.
   */
   public void paint(Graphics g)
   {  
      // Shadow ball attributes since Graphics methods need integer values.
      int x =  (int)Math.round(this.x);
      int y = (int)Math.round(this.y);
      int size = (int)Math.round(this.size);
      int highlight_size = (int)Math.round(this.size * 0.88);
      int highlight_x = (int)Math.round(this.x + (2.0 * (this.size / 110.00)));
      int highlight_y = (int)Math.round(this.y + (2.0 * (this.size / 110.00)));
      int outline_x = (int)Math.round(this.x - (4.0 * (this.size / 100.00)));
      int outline_y = (int)Math.round(this.y - (4.0 * (this.size / 100.00)));
      int outline_size = (int)Math.round(this.size + (8.0 * (this.size / 110.00)));
      
      // Draw outline.
      g.setColor(Color.BLACK);
      g.fillOval(outline_x, outline_y, outline_size, outline_size);
      
      // Draw ball.  
      g.setColor(color);
      g.fillOval(x, y, size, size);
      
      // Draw highlights
      g.setColor(color.brighter()); 
      g.fillOval(highlight_x, highlight_y, highlight_size, highlight_size);
   }
   
   /**
      Check whether this ball is colliding with another ball.
      @param other_ball The other ball to check collision with.
      @return Whether the balls are colliding or not.
   */
   public boolean isCollidingWith(Ball other_ball)
   {  
      // If the distance between the balls is less than their radii then they are overlapping.
      double radius = size / 2;
      double other_ball_radius = other_ball.getSize() / 2;
      double center_x = x + radius;
      double center_y = y + radius;
      double other_ball_center_x = other_ball.getX() + other_ball_radius;
      double other_ball_center_y = other_ball.getY() + other_ball_radius;
      double distance = Math.sqrt(Math.pow((other_ball_center_x - center_x), 2) + Math.pow((other_ball_center_y - center_y), 2));
      if (distance <= (radius  + other_ball_radius))
         return true;
      else
         return false;
   }
   
   /**
      Processes collision between this ball and another ball.
      @param other_ball The other ball to process collision with.
   */
   public void updateCollision(Ball other_ball)
   {
      // Since we have a collision we will first need to move the balls so that they are no longer overlapping.
      
      double radius = size / 2;
      double other_ball_radius = other_ball.getSize() / 2;
      double center_x = x + radius;
      double center_y = y + radius;
      double other_ball_center_x = other_ball.getX() + other_ball_radius;
      double other_ball_center_y = other_ball.getY() + other_ball_radius;
      
      // Calculate the distance vector between both balls.
      double distance_x = other_ball.getX() - x;
      double distance_y = other_ball.getY() - y;
            
      // Calculate the magnitude of the distance vector.
      double distance = Math.sqrt(Math.pow(distance_x, 2) + Math.pow(distance_y, 2));
      
      // Normalize the distance vector to get just it's direction.
      distance_x /= distance;
      distance_y /= distance;
      
      // Use the ratio of the their radii to get where they touch.
      double touch_distance = (distance * (radius / (radius + other_ball_radius)));
      double contact_x = x + (distance_x * touch_distance);
      double contact_y = y + (distance_y * touch_distance);
      
      // Move each ball so they are no longer overlapping.
      x = contact_x - (distance_x * radius);
      y = contact_y - (distance_y * radius);
      other_ball.setX(contact_x + (distance_x * other_ball_radius));
      other_ball.setY(contact_y + (distance_y * other_ball_radius));
      
      // Now that they are no longer overlapping we need to calculate and apply new velocities to the balls so
      // that they bounce off of each other.
            
      // Calculate tangent vector at point of impact.
      double tangent_y = -(other_ball.getX() - x);
      double tangent_x = (other_ball.getY() - y);
      
      // Normalize tangent vector to get just it's direction.
      double tangent_length =  Math.sqrt(Math.pow(tangent_x, 2) + Math.pow(tangent_y, 2));
      tangent_x /= tangent_length;
      tangent_y /= tangent_length;
      
      // Calculate the relative velocity vector from one ball to another.
      double relative_velocity_x = dx - other_ball.getDX();
      double relative_velocity_y = dy - other_ball.getDY();
      
      // Calculate the magnitude of the relative velocity vector parallel to the tangent.
      double magnitude = (relative_velocity_x * tangent_x) + (relative_velocity_y * tangent_y);
      
      // Multiply the magnitude by the tangent vector to get the velocity vector parallel to the tangent.
      double velocity_tangent_x = tangent_x * magnitude;
      double velocity_tangent_y = tangent_y * magnitude;
      
      // Subtract the velocity vector parallel to the tangent from the relative velocity vector to
      // get the velocity vector perpendicular to the tangent.
      double velocity_perpendicular_x = relative_velocity_x - velocity_tangent_x;
      double velocity_perpendicular_y = relative_velocity_y - velocity_tangent_y;
      
      // Apply the velocity vector perpendicular to the tangent to both balls to make them bounce off of each other.
      dx -= velocity_perpendicular_x;
      dy -= velocity_perpendicular_y;
      other_ball.setDX(other_ball.getDX() + velocity_perpendicular_x);
      other_ball.setDY(other_ball.getDY() + velocity_perpendicular_y);
   }
   
   /**
      Returns the ball's x coordinate.
      @return The ball's x coordinate.
   */
   public double getX()
   {
      return x;
   }
   
   /**
      Sets the ball's x coordinate.
      @param x The x coordinate to set.
   */
   public void setX(double x)
   {
      this.x = x;
   }
   
   /**
      Returns the ball's y coordinate.
      @return The ball's y coordinate.
   */
   public double getY()
   {
      return y;
   }
   
   /**
      Sets the ball's y coordinate.
      @param x The y coordinate to set.
   */
   public void setY(double y)
   {
      this.y = y;
   }
   
   /**
      Returns the ball's x velocity.
      @return The ball's x velocity.
   */
   public double getDX()
   {
      return dx;
   }
   
   /**
      Returns the ball's y velocity.
      @return The ball's y velocity.
   */
   public double getDY()
   {
      return dy;
   }
   
   /**
      Sets the ball's x velocity.
      @param x The x velocity to set.
   */
   public void setDX(double dx)
   {
      this.dx = dx;
   }
   
   /**
      Sets the ball's y velocity.
      @param x The y velocity to set.
   */
   public void setDY(double dy)
   {
      this.dy = dy;
   }
   
   /**
      Returns the ball's size.
      @return The ball's size.
   */
   public double getSize()
   {
      return size;
   }
   
   /**
      Sets the ball's size.
      @param size The size to set.
   */
   public void setSize(double size)
   {
      this.size = size;
   }
   
   /**
      Returns the ball's color.
      @return The ball's color.
   */
   public Color getColor()
   {
      return color;
   }
}