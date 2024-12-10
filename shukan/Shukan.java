/*  Copyright 2021 Philippe Even, Université de Lorraine, IUT de St Dié.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package shukan;
import javax.swing.JFrame;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsConfiguration;

/**
 * Shukan planner application.
 */
public class Shukan extends JFrame {
  /** Shukan window top margin */
  private final static int TOP_MARGIN = 30;
  /** Shukan window left margin */
  private final static int LEFT_MARGIN = 50;

  /**
   * Creates AWT Frame with Shukan tools.
   */
  public Shukan() {
      super("SHUKAN " + ShukanView.majorRelease() + "." + ShukanView.minorRelease());

      // Shukan data base creation and set up
      ShukanData data = new ShukanData();
      ShukanIO io = new ShukanIO(data);
      boolean is_editing = false;

      // Handling edition mode
      if (is_editing) {
          new ShukanTex(data);
          System.exit(0);
      }

      // Creating Shukan viewer
      ShukanView canvas = new ShukanView(data, io);

      // Getting the display size
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice gd = ge.getScreenDevices()[0];
      GraphicsConfiguration[] gc = gd.getConfigurations();
      Rectangle gcBounds = gc[0].getBounds();
      canvas.restrictSize((int) (gcBounds.getWidth()) - LEFT_MARGIN,
              (int) (gcBounds.getHeight()) - TOP_MARGIN);

      // Adding a openGL input handler (controller)
      ShukanController myController = new ShukanController(canvas, data);
      canvas.addKeyListener(myController);
      canvas.addMouseListener(myController);

      // Preparing the window closing button
      addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
              // Shukan.io.save();
              System.exit(0);
          }
      });

      // Setting the window geometry
      add(canvas);
      Insets ins = getInsets();
      setSize(canvas.displayWidth() + ins.left + ins.right, canvas.displayHeight() + ins.top + ins.bottom);
      setLocation(LEFT_MARGIN, 0);
      setBackground(Color.white);
      setVisible(true);

      // Triggering input handler loop
      canvas.requestFocus();
  }

  /**
   * Main method to launch the Shukan application.
   * @param args Shukan planner arguments.
   */
  public static void main(String[] args) {
      new Shukan();
  }
}