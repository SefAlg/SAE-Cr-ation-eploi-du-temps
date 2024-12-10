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
import java.util.Calendar;
import java.text.DecimalFormat;


/** Cursus semester. */
public class ShukanSemester
{
  /** Month names. */
  private static final String[] MONTH_FULL_NAMES = {
    "janvier", "fevrier", "mars", "avril", "mai", "juin",
    "juillet", "aout", "septembre", "octobre", "novembre", "decembre"};
  /** Month short names. */
  private static final String[] MONTH_ACRONYMS = {
    "janv.", "fev.", "mars", "avr.", "mai", "juin",
    "juil.", "aout", "sept.", "oct.", "nov.", "dec."};

  /** Year at start of the cursus. */
  private int startYear;
  /** Names of each week. */
  private String[] weekNames;
  /** Maximal duration of working weeks (in hours). */
  private int standardWeekDuration;
  /** Maximal duration of each week (in hours). */
  private int[] weekDurations;
  /** Number of the first week in the cursus. */
  private int firstWeek;
  /** Number of the last week in the cursus year.
   * Deprecated; inferable but just left as still used in early plannings. */
  private int lastWeekOfYear;


  /** Creates a semester from a directory name.
   * @param year Year at start of the cursus.
   * @param first Number of the first week.
   * @param length Duration of the cursus (in weeks).
   * @param duration Maximal duration of working weeks (in hours).
   */
  public ShukanSemester (int year, int first, int length, int duration)
  {
    startYear = year;
    Calendar cal = Calendar.getInstance ();
    cal.set (Calendar.YEAR, year);
    lastWeekOfYear = cal.getActualMaximum (Calendar.WEEK_OF_YEAR);
    firstWeek = first;

    weekNames = new String[length];
    standardWeekDuration = duration;
    weekDurations = new int[length];
    int weekNo = first;
    DecimalFormat df = new DecimalFormat ("00");
    for (int i = 0; i < length; i++)
    {
      cal.set (Calendar.WEEK_OF_YEAR, weekNo);
      cal.set (Calendar.DAY_OF_WEEK, 0);
      weekNames[i] = df.format (cal.get (Calendar.DAY_OF_MONTH))
                     + " " + MONTH_ACRONYMS[cal.get (Calendar.MONTH)];
      weekDurations[i] = duration;
      if (++weekNo > lastWeekOfYear) weekNo = 1;
    }
  }

  /** Sets a week duration.
   * @param week Index of the week.
   * @param duration Maximal number of working hours in this week.
   * @return true if the setting is valid.
   */
  public boolean setDuration (int week, int duration)
  {
    if (week > lastWeekOfYear) return false;
    week -= firstWeek;
    if (week < 0) week += lastWeekOfYear;
    if (week >= weekDurations.length) return false;
    weekDurations[week] = duration;
    return true;
  }

  /** Gets the number of weeks in the cursus.
   * @return the number of weeks.
   */
  public int size ()
  {
    return (weekDurations.length);
  }

  /** Gets the standard week duration.
   * @return the maximal number of working hours in any week.
   */
  public int standardWeekDuration ()
  {
    return (standardWeekDuration);
  }

  /** Gets the week durations.
   * @return the maximal number of working hours in each week.
   */
  public int[] weekDurations ()
  {
    return (weekDurations);
  }

  /** Gets the week durations.
   * @return a copy of the maximal number of working hours in each week.
   */
  public int[] copyWeekDurations ()
  {
    int[] wd = new int[weekDurations.length];
    for (int i = 0; i < weekDurations.length; i++) wd[i] = weekDurations[i];
    return (wd);
  }

  /** Gets the semester length.
   * @return the number of weeks (working or not) in the semester.
   */
  public int length ()
  {
    return (weekDurations.length);
  }

  /** Gets the week numbers.
   * @return the yearly number of each week in the cursus.
   */
  public int[] weekNumbers ()
  {
    int[] nb = new int[weekDurations.length];
    int num = firstWeek;
    for (int i = 0; i < nb.length; i++)
    {
      nb[i] = num++;
      if (num > lastWeekOfYear) num = 1;
    }
    return (nb);
  }

  /** Gets a week number.
   * @weekIndex Index of the week.
   * @return the yearly number of this week in the cursus.
   */
  public int weekNumber (int weekIndex)
  {
    int wk = firstWeek + weekIndex;
    if (wk > lastWeekOfYear) wk -= lastWeekOfYear;
    return (wk);
  }

  /** Gets the start week number.
   * @return the yearly number of the week when the cursus starts.
   */
  public int startWeekNumber ()
  {
    return (firstWeek);
  }

  /** Gets the number of the last week in start year.
   * Obsolete: could be inferred.
   * @return the number of the last week in start year.
   */
  public int lastWeekOfYearNumber ()
  {
    return (lastWeekOfYear);
  }

  /** Gets the index of a week from its yearly number.
   * @param week The yearly number of the week.
   * @return the index of this week in the cursus.
   */
  public int weekIndex (int week)
  {
    if (week < firstWeek) return (week + lastWeekOfYear - firstWeek);
    else return (week - firstWeek);
  }

  /** Gets a description of the semester.
   * @return a text, that represents the cursus semester.
   */
  public String toString ()
  {
    String s = new String ();
    for (int i = 0; i < weekNames.length; i++) s += weekNames[i] + "\n";
    return s;
  }

  /** Gets the present date in text format.
   * @return a text with the present date.
   */
  public String date ()
  {
    Calendar kal = Calendar.getInstance ();
    DecimalFormat format = new DecimalFormat ("00");
    return ((kal.get (Calendar.YEAR) % 100)
            + format.format (kal.get (Calendar.MONTH) + 1)
            + format.format (kal.get (Calendar.DAY_OF_MONTH)));
  }

  /** Gets the year at cursus start.
   * @return the year at the start of the cursus.
   */
  public int yearOfFirstWeek ()
  {
    return (startYear);
  }
}
