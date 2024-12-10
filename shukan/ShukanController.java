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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


/** Shukan controller. */
public class ShukanController
           implements KeyListener, MouseListener//, MouseMotionListener
{
  /** Controlled display area. */
  private ShukanView canvas;
  /** Controlled data base. */
  private ShukanData data;

  /** Constructs an event handler.
   * @param canvas Display area.
   * @param data Shukan data base.
   */
  public ShukanController (ShukanView canvas, ShukanData data)
  {
    this.canvas = canvas;
    this.data = data;
  }

  /** Invoked when a key has been pressed.
   * Implementation from KeyListener.
   * @param e Detected key event.
   */
  public void keyPressed (KeyEvent e)
  {
    processKeyEvent (e, true);
  }

  /** Invoked when a key has been released.
   * Implementation from KeyListener.
   * @param e Detected key event.
   */
  public void keyReleased (KeyEvent e)
  {
    processKeyEvent (e, false);
  }

  /** Invoked when a key has been pressed or released.
   * Local implementation from KeyListener.
   * @param e Detected key event.
   * @param pressed Pressed or released key status.
   */
  private void processKeyEvent (KeyEvent e, boolean pressed)
  {
    switch (e.getKeyCode ())
    {
      case KeyEvent.VK_Q :
      case KeyEvent.VK_ESCAPE :
        if (! pressed)
        {
          canvas.saveData ();
          //data.check ();
          System.exit (0);
        }
        break;
      case KeyEvent.VK_E :   // edit
        if (! pressed) new ShukanTex (data);
        break;
      case KeyEvent.VK_LEFT :
      case KeyEvent.VK_KP_LEFT :
        if (pressed)
        {
          data.toggleLoad (-1);
          canvas.repaint ();
        }
        break;
      case KeyEvent.VK_RIGHT :
      case KeyEvent.VK_KP_RIGHT :
        if (pressed)
        {
          data.toggleLoad (1);
          canvas.repaint ();
        }
        break;
      case KeyEvent.VK_UP :
      case KeyEvent.VK_KP_UP :
        if (pressed)
        {
          data.toggleCursus (false);
          canvas.repaint ();
        }
        break;
      case KeyEvent.VK_DOWN :
      case KeyEvent.VK_KP_DOWN :
        if (pressed)
        {
          data.toggleCursus (true);
          canvas.repaint ();
        }
        break;
      case KeyEvent.VK_X :
        if (pressed && e.isControlDown ())
        {
          data.exchangeExtremes ();
          canvas.repaint ();
        }
        break;
    }
  }

  /** Invoked when a key has been typed.
   * Implementation from KeyListener.
   * @param e Detected key event.
   */
  public void keyTyped (KeyEvent e)
  {
    switch (e.getKeyChar ())
    {
      case 'l' :
        if (data.followLeft ()) canvas.repaint ();
        break;
      case 'm' :
        if (data.followRight ()) canvas.repaint ();
        break;
      case 'L' :
        if (data.left ()) canvas.repaint ();
        break;
      case 'M' :
        if (data.right ()) canvas.repaint ();
        break;
      case 'o' :
        if (data.left ()) canvas.repaint ();
        break;
      case 'p' :
        if (data.right ()) canvas.repaint ();
        break;
      case 'v' :
        data.switchNameCode ();
        canvas.repaint ();
        break;
    }
  }

  /** Invoked when the mouse button has been clicked (pressed and released)
   * on a component.
   * Implementation from MouseListener.
   * @param e Detected mouse event.
   */
  public void mouseClicked (MouseEvent e)
  {
    if (e.getButton () == MouseEvent.BUTTON1)
    {
      if (e.isShiftDown ())
      {
        if (canvas.select (e.getX (), e.getY (), true))
          canvas.repaint ();
      }
      else
      {
        if (canvas.select (e.getX (), e.getY (), false))
        canvas.repaint ();
      }
    }
    if (e.getButton () == MouseEvent.BUTTON3)
    {
      if (e.isShiftDown ())
      {
        if (canvas.selectActivity (e.getX (), e.getY (), true))
          canvas.repaint ();
      }
      else
      {
        if (canvas.selectActivity (e.getX (), e.getY (), false))
        canvas.repaint ();
      }
    }
  }

  /** Invoked when the mouse enters a component.
   * Implementation from MouseListener.
   * @param e Detected mouse event.
   */
  public void mouseEntered (MouseEvent e)
  {
  }

  /** Invoked when the mouse exits a component.
   * Implementation from MouseListener.
   * @param e Detected mouse event.
   */
  public void mouseExited (MouseEvent e)
  {
  }

  /** Invoked when a mouse button has been pressed on a component.
   * Implementation from MouseListener.
   * @param e Detected mouse event.
   */
  public void mousePressed (MouseEvent e)
  {
  }

  /** Invoked when a mouse button has been released on a component.
   * Implementation from MouseListener.
   * @param e Detected mouse event.
   */
  public void mouseReleased (MouseEvent e)
  {
  }
}
