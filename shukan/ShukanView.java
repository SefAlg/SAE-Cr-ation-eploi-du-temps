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
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;


/** Graphical view for Shukan planner.
  */
public class ShukanView extends JPanel
{
  /** Major release number. */
  private static final int major_release = 2;
  /** Minor release number. */
  private static final int minor_release = 1;
  /** Dev release number. */
  private static final int dev_release = 5;
  /** Application version number. */
  public static final long serialVersionUID
    = 1000000L * major_release + 1000L * minor_release + dev_release;

  /** Font type. */
  //private final int FONT = GLUT.BITMAP_HELVETICA_12;

  /** Max window width. */
  private final int MAX_WIDTH = 1610;
  /** Max window height. */
  private final int MAX_HEIGHT = 960;
  /** Security margin at the window border for other applications. */
  private final int SECURITY_MARGIN = 70;

  /** Maximal height of a text line. */
  private final int TEXT_MAX_HEIGHT = 15;
  /** Minimal height of a text line. */
  private final int TEXT_MIN_HEIGHT = 10;
  /** Standard height of a text line. */
  private final int STD_TEXT_HEIGHT = 13;

  /** Standard week column width. */
  private final static int STD_WEEK_WIDTH = 30;
  /** Standard activity symbol width. */
  private final static int STD_ACTIV_WIDTH = 6;
  /** Standard number of modules. */
  private final static int STD_MODULES_NUMBER = 14;
  /** Standard number of weeks. */
  private final static int STD_WEEKS_NUMBER = 20;

  /** Standard application width. */
  private final static int STD_APPLI_WIDTH =
                           STD_WEEK_WIDTH * (STD_WEEKS_NUMBER + 2);
  /** Standard application height. */
  private final int STD_APPLI_HEIGHT =
                    STD_TEXT_HEIGHT * 3 * (STD_MODULES_NUMBER + 1);

  /** Week column actual width. */
  private int weekWidth = STD_WEEK_WIDTH;
  /** Standard activity symbol actual width. */
  private int activWidth = STD_ACTIV_WIDTH;
  /** Number of weeks to display. */
  private int nbWeeks = STD_WEEKS_NUMBER;
  /** Number of modules to display. */
  private int nbModules = STD_MODULES_NUMBER;
  /** Height of a text line. */
  private int textHeight = STD_TEXT_HEIGHT;
  /** Application actual width. */
  private int appliWidth = STD_APPLI_WIDTH;
  /** Application actual height. */
  private int appliHeight = STD_APPLI_HEIGHT;

  /** Height of the font characters. */
  private int fontHeight = 10;
  /** Font metrics features. */
  private FontMetrics fontMetrics;


  /** Background color. */
  private final Color BACK_COLOR = Color.WHITE;
  /** Grid color. */
  private final Color GRID_COLOR = Color.BLACK;
  /** Free module weeks color. */
  private final Color HOLLY_COLOR = new Color (0.3f, 0.7f, 0.7f);
  /** Inactive parts of weeks color. */
  private final Color INACTIVE_COLOR = new Color (0.5f, 0.5f, 0.5f);
  /** Frame borders color. */
  private final Color ACTIVE_COLOR = new Color (0.6f, 0.6f, 0.9f);
  /** Selected activity color. */
  private final Color REACTIVE_COLOR = new Color (0.7f, 0.9f, 0.5f);
  /** Frame borders color. */
  private final Color FRAME_COLOR = Color.BLACK;
  /** Text color. */
  private final Color TEXT_COLOR = Color.BLACK;
  /** Selected text color. */
  private final Color SELECTED_TEXT_COLOR = Color.WHITE;
  /** Unselectable text color. */
  private final Color UNSEL_TEXT_COLOR = new Color (0.6f, 0.6f, 0.6f);
  /** Selected slot central color. */
  private final Color SELECTION_COLOR = new Color (0.6f, 0.6f, 0.6f);

  /** Data file manager. */
  private ShukanIO myIO = null;
  /** Displayed data base. */
  private ShukanData data = null;



  /** Creates the shukan viewer.
   * @param data Shukan data base.
   * @param myIO Shukan file manager.
   */
  public ShukanView (ShukanData data, ShukanIO myIO)
  {
    this.data = data;
    this.myIO = myIO;
  }

  /** Gets the major release number.
   * @return the major release number.
   */
  public static int majorRelease ()
  {
    return major_release;
  }

  /** Gets the minor release number.
   * @return the minor release number.
   */
  public static int minorRelease ()
  {
    return minor_release;
  }

  /** Saves present semester in data files.
   */
  public void saveData ()
  {
    myIO.save ();
  }

  /** Sets the maximal view area size.
   * @param width Maximal view area width.
   * @param height Maximal view area height.
   */
  public void restrictSize (int width, int height)
  {
    adaptSize (width, height);
  }

  /** Updates the view size.
   * @param width Maximal view width.
   * @param height Maximal view height.
   */
  private void adaptSize (int width, int height)
  {
    if (data != null)
    {
      nbWeeks = data.semesterSize ();
      nbModules = data.numberOfModules ();

      // Height
      textHeight = (int) (height / (3.0f * (nbModules + 1) + 1));
      appliHeight = textHeight * (3 * (nbModules + 1) + 1);

      // Width
      activWidth = width / (ShukanModule.MAX_ACTIV_PER_WEEK * (nbWeeks + 2));
      weekWidth = activWidth * ShukanModule.MAX_ACTIV_PER_WEEK;
      appliWidth = weekWidth * (nbWeeks + 2);
    }
    else
    {
      appliWidth = STD_APPLI_WIDTH;
      appliHeight = STD_APPLI_HEIGHT;
    }
  }

  /** Gets the display area width.
   * @return the display area width.
   */
  public int displayWidth ()
  {
    return (appliWidth);
  }

  /** Gets the display area height.
   * @return the display area height.
   */
  public int displayHeight ()
  {
    return (appliHeight);
  }

  /** Draws Shukan view. 
   * @param g Graphical context.
   */
  public void paintComponent (Graphics g)
  {
    int w = getWidth ();
    int h = getHeight ();
    adaptSize (w, h);

    Graphics2D g2 = (Graphics2D) g.create ();
    g2.setFont (new Font ("Helvetica", 0, 12));
    fontMetrics = g2.getFontMetrics ();
    fontHeight = fontMetrics.getAscent ();

    g2.setColor (BACK_COLOR);
    drawBox (g2, appliWidth - w, appliHeight - h, w, h);
    displayCalendar (g2);
  }

 
  /** Identifies an application area from view coordinates.
   * @param x X-value of the view coordinates.
   * @param y Y-value of the view coordinates.
   * @param zone Indicates whether a week interval can be selected.
   * @return true if the view should be updated.
   */
  public boolean select (int x, int y, boolean zone)
  {
    // Out of the active part of the window
    if (y < textHeight || y > appliHeight - 3 * textHeight) return (false);

    // Module detection
    y -= textHeight;
    int selMod = y / (3 * textHeight);

    // Module selection
    if (x < 2 * weekWidth) return (data.activate (selMod));
    // Activity selection
    x -= 2 * weekWidth;
    return (data.activate (selMod, (x / weekWidth), zone));
  }


  /** Identifies an activity from view coordinates.
   * @param x X-value of the view coordinates.
   * @param y Y-value of the view coordinates.
   * @param zone Indicates whether a week interval can be selected.
   * @return true if the view should be updated.
   */
  public boolean selectActivity (int x, int y, boolean zone)
  {
    // Out of the active part of the window
    if (y < textHeight || y > appliHeight - 3 * textHeight) return (false);

    // Module detection
    y -= textHeight;
    int selMod = y / (3 * textHeight);

    // Module selection
    if (x < 2 * weekWidth) return (data.activateAct (selMod));
    // Activity selection
    x -= 2 * weekWidth;
    int selw = x / weekWidth;
    int seln = (x - (selw * weekWidth)) / activWidth;
    return (data.select (selMod,
                         selw * ShukanModule.MAX_ACTIV_PER_WEEK + seln,
                         zone));
  }

 
  /** Displays the view background.
   * @param g Graphical context.
   */
  private void displayCalendar (Graphics2D g2)
  {
    // Selection
    int selMod = data.selectedModule ();
    if (selMod != -1)
    {
      int selWeek = data.selectedWeek ();
      if (selWeek != -1)
      {
        int selLength = data.selectedLength ();
        g2.setColor (ACTIVE_COLOR);
        int x = (selWeek + 2) * weekWidth;
        int y = appliHeight - textHeight - (selMod + 1) * textHeight * 3;
        drawBox (g2, x, y, weekWidth * selLength, textHeight * 3);
      }
      else
      {
        int sa = data.selectedActivity ();
        if (sa != -1)
        {
          g2.setColor (REACTIVE_COLOR);
          drawBox (g2, sa * activWidth + 2 * weekWidth,
                   appliHeight - textHeight - (selMod + 1) * textHeight * 3,
                   activWidth * data.selectedLength (), textHeight * 3);
        }
      }
    }

    // Modules holly periods
    int[] durations = data.weekDurations ();
    g2.setColor (HOLLY_COLOR);
    for (int j = 0; j < nbModules; j++)
    {
      int[] aw = data.activeWeeks (j);
      for (int i = 0; i < nbWeeks; i++)
        if (aw[i] == 0 && durations[i] != 0)
        {
          drawBox (g2, (2 + i) * weekWidth,
                       appliHeight + (2 - 3 * (j + 2)) * textHeight,
                       weekWidth, 3 * textHeight);
        }
    }

    // Holydays
    int wd = data.standardWeekDuration ();
    g2.setColor (INACTIVE_COLOR);
    for (int i = 0; i < nbWeeks; i++)
      if (durations[i] != wd)
      {
        int start = (int) ((durations[i] * weekWidth) / (float) wd);
        drawBox (g2, (2 + i) * weekWidth + start, 3 * textHeight,
                     weekWidth - start, appliHeight - 4 * textHeight);
      }

    g2.setColor (GRID_COLOR);
    // Columns
    for (int i = 2; i <= nbWeeks + 1; i++)
      drawVLine (g2, i * weekWidth, 0, appliHeight);
    // Lines
    for (int i = 0; i <= nbModules ; i++)
      drawHLine (g2, 0, (3 + 3 * i) * textHeight, appliWidth);

    // Texts : semester and load name
    drawText (g2, 0, appliHeight - textHeight,
                  2 * weekWidth, textHeight, data.cursusName ());
    drawText (g2, 0, textHeight,
                  2 * weekWidth, textHeight, data.loadTeacherName ());
    drawText (g2, 0, 2 * textHeight,
                  2 * weekWidth, textHeight, data.loadStudentName ());
    // Texts : weeks
    int[] weekNumbers = data.weekNumbers ();
    for (int i = 0; i < nbWeeks; i++)
    {
      drawText (g2, (i + 2) * weekWidth, appliHeight - textHeight,
                    weekWidth, textHeight, "S" + weekNumbers[i]);
      drawText (g2, (i + 2) * weekWidth, 2 * textHeight,
                    weekWidth, textHeight, "" + data.computeStudentLoad (i));
      if (data.teacherSelected ())
        drawText (g2, (i + 2) * weekWidth, textHeight,
                      weekWidth, textHeight, "" + data.computeLoad (i));
      drawText (g2, (2 + i) * weekWidth, 0,
                    weekWidth, textHeight, "/" + durations[i]);
    }
    // Texts : modules
    String[] modNames = data.moduleNames ();
    for (int i = 0; i < nbModules; i++)
      drawText (g2, 0, appliHeight - ((i + 1) * 3 + 1) * textHeight,
                    2 * weekWidth, 3 * textHeight, modNames[i]);
    // Texts : activities
    for (int i = 0; i < nbModules; i++)
    {
      int[] act = data.activities (i);
      boolean[] up = data.onLeft (i);
      boolean[] down = data.onRight (i);
      int numact = 0;
      int y = appliHeight - ((i + 1) * 3 + 1) * textHeight;
      for (int j = 0; j < nbWeeks; j++)
      {
        int[] sched = data.scheduleInWeek (i, j);
        for (int k = 0; k < sched.length; k++)
        {
          int ly = y;
          if (up[numact]) ly += textHeight * 2;
          else if (! down[numact]) ly += textHeight;
          numact++;
          drawText (g2, (j + 2) * weekWidth + k * activWidth, ly,
                        activWidth, textHeight,
                        ShukanModule.DISPLAY[act[sched[k]]]);
        }
      }
    }
  }


  /** Draws a horizontal line.
   * @param g Graphical context.
   * @param x X-coordinate of the line start point.
   * @param y Y-coordinate of the line.
   * @param l Line length.
   */
  private void drawHLine (Graphics2D g2, int x, int y, int l)
  {
    g2.drawLine (x, appliHeight - y, x + l, appliHeight - y);
  }

  /** Draws a vertical line.
   * @param g Graphical context.
   * @param x X-coordinate of the line.
   * @param y Y-coordinate of the line start point.
   * @param l Line length.
   */
  private void drawVLine (Graphics2D g2, int x, int y, int l)
  {
    g2.drawLine (x, appliHeight - y, x, appliHeight - y - l);
  }

  /** Draws a rectangular box.
   * @param g2 Graphical context.
   * @param posx Lower coordinate of the box.
   * @param posy Left coordinate of the box.
   * @param width Width of the box.
   * @param height Height of the box.
   */
  private void drawBox (Graphics2D g2, int posx, int posy,
                                       int width, int height)
  {
    g2.fillRect (posx, appliHeight - posy - height, width, height);
  }

  /** Draws a centered text in the given area.
   * @param g2 Graphical context.
   * @param posx Lower coordinate of the area.
   * @param posy Left coordinate of the area.
   * @param width Width of the area.
   * @param height Height of the area.
   * @param text Text to draw in the area.
   */
  private void drawText (Graphics2D g2, float posx, float posy,
                         float width, float height, String text)
  {
    g2.drawString (text,
                   (int) (posx + (width - fontMetrics.stringWidth (text)) / 2),
                   appliHeight - (int) (posy + (height - fontHeight) / 2));
  }

  /** Draws a centered paragraph in the given area.
   * @param g2 Graphical context.
   * @param posx Lower coordinate of the area.
   * @param posy Left coordinate of the area.
   * @param width Width of the area.
   * @param height Height of the area.
   * @param text Lines of text to draw in the area.
   */
  private void drawText (Graphics2D g2, float posx, float posy,
                         float width, float height, String[] text)
  {
    if (text == null) return;
    posy += (height + (text.length - 1) * fontHeight) / 2;
    for (int i = 0; i < text.length; i ++)
    {
      drawText (g2, posx, posy, width, fontHeight, text[i]);
      posy -= fontHeight;
    }
  }
}
