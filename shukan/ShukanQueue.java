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


/** Double-ended queues of activities. */
public class ShukanQueue
{
  /** Queue of activities. */
  private int[] queue;
  /** Head position in the queue. */
  private int head = 0;
  /** Number of activities in the queue. */
  private int length = 0;


  /** Creates a queue with given capacity.
   * @param capa The capacity of the queue.
   */
  public ShukanQueue (int capa)
  {
    queue = new int[capa];
  }

  /** Neutralizes the queue.
   */
  public void kill ()
  {
    queue = null;
    head = 0;
    length = 0;
  }

  /** Pushes a token to the right of the queue.
   * @param token The token to push to the queue.
   */
  public void pushRight (int token)
  {
    if (queue != null && length < queue.length)
      queue[(head + length++) % queue.length] = token;
  }

  /** Pushes a token to the left of the queue.
   * @param token The token to push to the queue.
   */
  public void pushLeft (int token)
  {
    if (queue != null && length < queue.length)
    {
      if (--head < 0) head = queue.length - 1;
      queue[head] = token;
      length++;
    }
  }

  /** Gets the contents of the queue.
   * @return the sequence of activity types.
   */
  public int[] contents ()
  {
    int[] contents = new int[length];
    for (int i = 0; i < length; i++)
      contents[i] = queue[(head + i) % queue.length];
    return (contents);
  }

  /** Gets the length of the queue.
   * @return the length of the queue.
   */
  public int length ()
  {
    return (length);
  }

  /** Clears the queue.
   */
  public void clear ()
  {
    length = 0;
  }

  /** Checks whether the queue is neutralized.
   * @return true if the queue is neutralized.
   */
  public boolean killed ()
  {
    return (queue == null);
  }

  /** Checks whether the queue is empty.
   * @return true if the queue is empty.
   */
  public boolean empty ()
  {
    return (queue == null || length == 0);
  }

  /** Checks whether the queue is full.
   * @return true if the queue is full.
   */
  public boolean full ()
  {
    return (queue == null || length == queue.length);
  }

  /** Withdraws the activity at the head of the queue.
   * @return the activity type withdrawn from the queue.
   */
  public int popLeft ()
  {
    int token = -1;
    if (length > 0 && length <= queue.length)
    {
      token = queue[head++];
      if (head == queue.length) head = 0;
      length --;
    }
    return (token);
  }

  /** Withdraws the activity at the tail of the queue.
   * @return the activity type withdrawn from the queue.
   */
  public int popRight ()
  {
    int token = -1;
     if (length > 0 && length <= queue.length)
      token = queue[(head + --length) % queue.length];
    return (token);
  }
}
