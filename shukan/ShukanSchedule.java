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


/** Sequence of queues of activities for each week. */
public class ShukanSchedule
{
  /** Bin capacity. */
  private int capacity = 0;
  /** Sequences of bins. */
  private ShukanQueue[] binSet = null;


  /** Create a list of queues.
   * @param n Number of bins.
   * @param capa Bin capacity.
   */
  public ShukanSchedule (int n, int capa)
  {
    capacity = capa;
    binSet = new ShukanQueue[n];
    for (int i = 0; i < n; i++) binSet[i] = new ShukanQueue (capa);
  }

  /** Neutralizes a queue.
   * @param n Index of the queue.
   */
  public void kill (int n)
  {
    binSet[n].kill ();
  }

  /** Gets the contents of a queue.
   * @param n Index of the queue.
   * @return the sequence of activities in the bin.
   */
  public int[] contents (int n)
  {
    return (binSet[n].contents ());
  }

  /** Gets the contents of all the queues.
   * @return the sequence of activities in all the queues.
   */
  public int[] contents ()
  {
    int[] sched = new int[100];
    int act = 0;
    for (int i = 0; i < binSet.length; i++)
    {
      int tl = binSet[i].length ();
      for (int j = 0; j < tl; j++)
      {
        if (act == sched.length)
        {
          int[] tmp = new int[sched.length + 100];
          for (int k = 0; k < sched.length; k++) tmp[k] = sched[k];
          sched = tmp;
        }
        sched[act++] = i;
      }
    }
    int[] tmp = new int[act];
    for (int i = 0; i < act; i++) tmp[i] = sched[i];
    return (tmp);
  }

  /** Clears the queues.
   */
  public void clear ()
  {
    for (int i = 0; i < binSet.length; i++) binSet[i].clear ();
  }

  /** Adds a token to a queue.
   * @param n Index of the queue in the sequence.
   * @param token The new token.
   */
  public void add (int n, int token)
  {
    binSet[n].pushRight (token);
  }

  /** Pushes a token to the left in a queue.
   * @param n Index of the queue in the sequence.
   * @return the new queue (week) for the shifted token.
   */
  public int left (int n)
  {
    if (binSet[n].empty ()) return (n);
    int left = n - 1;
    while (left >= 0 && binSet[left].killed ()) left--;
    if (left >= 0 && ! binSet[left].full ())
    {
      binSet[left].pushRight (binSet[n].popLeft ());
      return (left);
    }
    return (n);
  }

  /** Pushes a token to the right in a queue.
   * @param n Index of the queue in the sequence.
   * @return the new queue (week) for the shifted token.
   */
  public int right (int n)
  {
    if (binSet[n].empty ()) return (n);
    int right = n + 1;
    while (right < binSet.length && binSet[right].killed ()) right++;
    if (right < binSet.length && ! binSet[right].full ())
    {
      binSet[right].pushLeft (binSet[n].popRight ());
      return (right);
    }
    return (n);
  }

  /** Pushes a sequence of tokens to the left in a queue.
   * @param n1 Index of the first token in the token sequence.
   * @param n2 Index of the last token in the token sequence.
   * @return the new queue (week) for the first token.
   */
  public int left (int n1, int n2)
  {
    while (binSet[n1].killed () && n1 < n2) n1 ++;
    while (binSet[n2].killed () && n2 > n1) n2 --;
    for (int i = n1; i < n2; i ++) left (i + 1);
    return (n1);
  }

  /** Pushes a sequence of tokens to the right in a queue.
   * @param n1 Index of the first token in the token sequence.
   * @param n2 Index of the last token in the token sequence.
   * @return the new queue (week) for the first token.
   */
  public int right (int n1, int n2)
  {
    while (binSet[n1].killed () && n1 < n2) n1 ++;
    while (binSet[n2].killed () && n2 > n1) n2 --;
    for (int i = n2; i > n1; i --) right (i - 1);
    return (n1);
  }

  /** Gets the activity at given position.
   * @param pos The position of the activity.
   * @return the index of the activity, or -1 if none.
   */
  public int select (int pos)
  {
    int selBin = pos / capacity;
    int selAct = pos % capacity;
    if (binSet[selBin].length () <= selAct)
    {
      boolean looking = true;
      // Looking frontwards
      int sbin = selBin;
      while (looking && sbin >= 0)
      {
        if (binSet[sbin].length () > 0)
        {
          looking = false;
          selBin = sbin;
          selAct = binSet[sbin].length () - 1;
        }
        sbin --;
      }
      if (looking)
      {
        // Looking backwards
        sbin = selBin + 1;
        while (looking && sbin < binSet.length)
        {
          if (binSet[sbin].length () > 0)
          {
            looking = false;
            selBin = sbin;
            selAct = 0;
          }
          sbin ++;
        }
      }
      if (looking)
      {
        selBin = sbin;
        selAct = 0;
        return (-1);
      }
    }
    return (selBin * capacity + selAct);
  }

  /** Gets the index of the activity in the sequence from its position.
   * @param slot Input position
   * @return the index of the activity.
   */
  public int identify (int slot)
  {
    int activ = 0;
    for (int i = 0; i < binSet.length; i++)
      if (slot < capacity)
      {
        activ += slot;
        i = binSet.length;
      }
      else
      {
        activ += binSet[i].length ();
        slot -= capacity;
      }
    return (activ);
  }
}
