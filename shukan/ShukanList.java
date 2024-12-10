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


/** Array list of Shukan elements. */
public class ShukanList
{
  /** Default size of a created list. */
  private final static int DEFAULT_SIZE = 10;

  /** Container of elements. */
  private Object[] elts = null;
  /** Effective number of elements. */
  private int count = 0;


  /** Creates a list with default size.
   */
  public ShukanList ()
  {
    elts = new Object[DEFAULT_SIZE];
  }

  /** Creates a list with given size.
   * @param size Size of the list.
   */
  public ShukanList (int size)
  {
    elts = new Object[size];
  }

  /** Clears the list.
   */
  public void empty ()
  {
    count = 0;
  }

  /** Adds an element to the list.
   * @param elt The Shukan element to add.
   */
  public void add (Object elt)
  {
    if (count == elts.length) doubleCapacity ();
    elts[count++] = elt;
  }

  /** Sets the list capacity to double size.
   */
  private void doubleCapacity ()
  {
    Object[] tab = new Object[elts.length * 2];
    for (int i = 0; i < count; i++) tab[i] = elts[i];
    elts = tab;
  }

  /** Gets the number of elements in the list.
   * @returns the number of elements.
   */
  public int size ()
  {
    return (count);
  }

  /** Removes an element from the list.
   * @param elt The Shukan element to remove.
   */
  public void remove (Object elt)
  {
    for (int i = 0; i < count; i ++)
      if (elt == elts[i])
      {
        elts[i] = elts[--count];
	i = count + 1;
      }
  }

  /** Gets a copy of elements in the list.
   * @param objects The array to fill in with the list contents.
   */
  public void fill (Object[] objects)
  {
    for (int i = 0; i < count; i++)
      objects[i] = elts[i];
  }

  /** Gets an element of the list.
   * @param index Index of the element.
   * @return the required element.
   */
  public Object get (int index)
  {
    return (elts[index]);
  }

  /** Gets the last element of the list.
   * @return the last element.
   */
  public Object last ()
  {
    return (elts[count - 1]);
  }

  /** Gets the elements of the list.
   * @return the elements of the list.
   */
  public Object[] toArray ()
  {
    Object[] obj = new Object[count];
    for (int i = 0; i < count; i++) obj[i] = elts[count];
    return (obj);
  }
}
