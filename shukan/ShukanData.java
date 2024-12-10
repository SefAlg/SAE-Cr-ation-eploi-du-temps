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


/** Shukan planner data base. */
public class ShukanData
{
  /** Displayed calendar. */
  private ShukanSemester sem = null;
  /** Cursus. */
  private String[] curs = null;
  /** Modules. */
  private ShukanModule[][] mod = null;
  /** Teachers. */
  private String[] teachers = new String[0];
  /** Sort for teachers. */
  private int[] tSort = new int[0];
  /** Students. */
  private String[] students = {"Et"};
  /** Sort for students. */
  private int[] sSort = {0};
  /** Scolar year (for displays). */
  private int scolarYear = 0;

  /** Id vs name display modality. */
  private boolean coded = false;
  /** Selected cursus. */
  private int selCur = 0; // First by default
  /** Selected module. */
  private int selMod = -1; // None by default
  /** Selected load. */
  private int selLoad = 0; // Student by default
  /** Selection first week. */
  private int selWeek = -1; // None by default
  /** Selected first week and activity. */
  private int selAct = -1;
  /** Selection length. */
  private int selLength = 0;


  /** Creates an empty data base.
   * @param scolYear Year at cursus start.
   * @param sem Cursus semester (1 or 2).
   */
  public void setSemester (int scolYear, ShukanSemester sem)
  {
    this.scolarYear = scolYear;
    this.sem = sem;
  }

  /** Gets the number of weeks in the semester.
   * @return the number of weeks in the semester.
   */
  public int semesterSize ()
  {
    return (sem.size ());
  }

  /** Gets the week numbers.
   * @return the year-number for each week.
   */
  public int[] weekNumbers ()
  {
    return (sem.weekNumbers ());
  }

  /** Gets the start week number.
   * @return the year-number of the start week.
   */
  public int startWeekNumber ()
  {
    return (sem.startWeekNumber ());
  }

  /** Gets the last week number of start year.
   * @return the last week number of start year.
   */
  public int lastWeekOfYearNumber ()
  {
    return (sem.lastWeekOfYearNumber ());
  }

  /** Gets the standard week duration.
   * @return the number of working hours in a standard week.
   */
  public int standardWeekDuration ()
  {
    return (sem.standardWeekDuration ());
  }

  /** Gets the week durations.
   * @return the number of working hours in each week.
   */
  public int[] weekDurations ()
  {
    return (sem.weekDurations ());
  }

  /** Inquires the id vs name display modality.
   * return true if identifiers such as R1.06 are displayed rather than a name.
   */
  public boolean isCoded ()
  {
    return coded;
  }

  /** Switches the id vs name display modality on or off.
   */
  public void switchNameCode ()
  {
    coded = ! coded;
  }

  /** Creates the cursus array.
   * @param names Cursus names.
   */
  public void declareCursus (String[] names)
  {
    curs = names;
    mod = new ShukanModule[names.length][];
  }

  /** Creates the module array in a cursus.
   * @param num Index of the cursus.
   * @param names Module names.
   */
  public void setModules (int num, String[] names)
  {
    mod[num] = new ShukanModule[names.length];
    for (int i = 0; i < names.length; i++)
    {
      mod[num][i] = new ShukanModule (names[i]);
      mod[num][i].setActiveWeeks (sem.copyWeekDurations ());
    }
  }

  /** Adds an activity to a module.
   * @param cursNum Index of the cursus.
   * @param modNum Index of the module.
   * @param actNum Index of the activity.
   * @param type Type of the activity.
   * @return true to update the display.
   */
  public boolean addActivity (int cursNum, int modNum, int actNum, String type)
  {
    return (mod[cursNum][modNum].addActivity (actNum, type));
  }

  /** Closes the activities of a module.
   * @param cursNum Index of the cursus.
   * @param modNum Index of the module.
   * @param size Number of activities in the module.
   */
  public void closeActivities (int cursNum, int modNum, int size)
  {
    mod[cursNum][modNum].closeActivities (size);
  }

  /** Initializes a free module with regularly-spaced activities on the grid.
   * @param cursNum Index of the cursus.
   * @param modNum Index of the module.
   */
  public void autoPlan (int cursNum, int modNum)
  {
    if (! mod[cursNum][modNum].isScheduled ())
      mod[cursNum][modNum].autoPlan ();
  }

  /** Schedules the activities of a module.
   * @param cursNum Index of the cursus.
   * @param modNum Index of the module.
   * @param weeks Available weeks.
   */
  public void setSchedule (int cursNum, int modNum, int[] weeks)
  {
    mod[cursNum][modNum].schedule (weeks);
  }

  /** Unschedules the activities of a module.
   * @param cursNum Index of the cursus.
   * @param modNum Index of the module.
    */
  public void unscheduleActivities (int cursNum, int modNum)
  {
    mod[cursNum][modNum].unschedule ();
  }

  /** Schedules an activity of a module.
   * @param cursNum Index of the cursus.
   * @param modNum Index of the module.
   * @param activity Activity to schedule.
   * @param week Target week.
   */
  public void scheduleActivity (int cursNum, int modNum, int activity, int week)
  {
    mod[cursNum][modNum].schedule (activity, week);
  }

  /** Gets the number of modules.
   * @return the number of modules.
   */
  public int numberOfModules ()
  {
    return (mod[selCur].length);
  }

  /** Gets the name of the active cursus.
   * @return the name of the active cursus.
   */
  public String cursusName ()
  {
    return (curs[selCur]);
  }

  /** Gets the name of a cursus.
   * @param num Index of the cursus.
   * @return the name of the cursus.
   */
  public String cursusName (int num)
  {
    return (curs[num]);
  }

  /** Gets the names of the modules in the selected cursus.
   * @return the names of the modules.
   */
  public String[] moduleNames ()
  {
    String[] names = new String[mod[selCur].length];
    for (int i = 0; i < mod[selCur].length; i++)
      names[i] = (coded ? mod[selCur][i].id () : mod[selCur][i].name ());
    return (names);
  }

  /** Gets the names of the modules in a cursus.
   * @param cur Index of the cursus.
   * @return the names of the modules.
   */
  public String[] moduleNames (int cur)
  {
    String[] names = new String[mod[cur].length];
    for (int i = 0; i < mod[cur].length; i++)
      names[i] = mod[cur][i].name ();
    return (names);
  }

  /** Sets a module start week.
   * @param cursNum Index of the cursus.
   * @param modNum Index of the module.
   * @param week Start week index.
   */
  public void setModuleStart (int cursNum, int modNum, int week)
  {
    mod[cursNum][modNum].setStartWeek (sem.weekIndex (week));
  }

  /** Sets a module end week.
   * @param cursNum Index of the cursus.
   * @param modNum Index of the module.
   * @param week End week index.
   */
  public void setModuleEnd (int cursNum, int modNum, int week)
  {
    mod[cursNum][modNum].setEndWeek (sem.weekIndex (week));
  }

  /** Sets a module free week.
   * @param cursNum Index of the cursus.
   * @param modNum Index of the module.
   * @param week Free week index.
   */
  public void setModuleHolly (int cursNum, int modNum, int week)
  {
    mod[cursNum][modNum].setHollyWeek (sem.weekIndex (week));
  }

  /** Gets the active weeks of a module in the selected cursus.
   * @param modNum Index of the module.
   * @return the active weeks index.
   */
  public int[] activeWeeks (int modNum)
  {
    return (mod[selCur][modNum].activeWeeks ());
  }

  /** Gets the activities of a module in the selected module.
   * @param modNum Index of the module.
   * @return the activities of the module.
   */
  public int[] activities (int modNum)
  {
    return (mod[selCur][modNum].activities ());
  }

  /** Gets the activity schedule of a module in a week of the selected cursus.
   * @param modNum Index of the module.
   * @param week Index of the week.
   * @return the activity schedule of a module.
   */
  public int[] scheduleInWeek (int modNum, int week)
  {
    return (mod[selCur][modNum].schedule (week));
  }

  /** Gets the whole schedule of a module in the selected cursus.
   * @param modNum Index of the module.
   * @return the schedule of the module.
   */
  public int[] schedule (int modNum)
  {
    return (mod[selCur][modNum].schedule ());
  }

  /** Gets the whole schedule of a module.
   * @param cur Index of the cursus.
   * @param modNum Index of the module.
   * @return the schedule of the module.
   */
  public int[] schedule (int cur, int modNum)
  {
    return (mod[cur][modNum].schedule ());
  }

  /** Gets the selected module.
   * @return the index of the selected module.
   */
  public int selectedModule ()
  {
    return (selMod);
  }

  /** Gets the first selected week.
   * @return the index of the first selected week.
   */
  public int selectedWeek ()
  {
    return (selWeek);
  }

  /** Gets the selection length.
   * @return the number of selected weeks.
   */
  public int selectedLength ()
  {
    return (selLength);
  }

  /** Unselects all.
   */
  public void deactivate ()
  {
    selMod = -1;
    selWeek = -1;
    selLength = 0;
  }

  /** Selects a whole module.
   * @param modNum Index of the module.
   * @return always true: forces a display update.
   */
  public boolean activate (int modNum)
  {
    selMod = modNum;
    selWeek = 0;
    selLength = semesterSize ();
    return (true);
  }

  /** Selects a module at given week.
   * @param modNum Index of the module.
   * @param week Index of the week.
   * @param zone Indicates whether a week interval is required.
   * @return true if the display should be updated.
   */
  public boolean activate (int modNum, int week, boolean zone)
  {
    if (zone)
    {
      if (selMod != modNum) return (false);
      if (selWeek == -1) return (false);
      if (selLength <= 2 && week >= selWeek && week <= selWeek + selLength - 1)
        return (false);
      if (week < selWeek)
      {
        selLength += selWeek - week;
        selWeek = week;
        return (true);
      }
      if (week >= selWeek + selLength)
      {
        selLength = week - selWeek + 1;
        return (true);
      }
      int dist = week - selWeek;
      if (2 * dist <= selLength - 1)
      {
        selLength -= week - selWeek;
        selWeek = week;
      }
      else selLength = week - selWeek + 1;
      return (true);
    }
    else
    {
      selMod = modNum;
      selWeek = week;
      selLength = 1;
    }
    return (true);
  }

  /** Activates a whole module.
   * @param modNum Index of the module.
   * @return false: no visual modification.
   */
  public boolean activateAct (int modNum)
  {
    System.out.println ("Activation du module " + modNum);
    return (false);
/*
    selMod = modNum;
    selWeek = 0;
    selLength = semesterSize ();
    return (true);
*/
  }

  /** Shifts the selection leftwards.
   * @return true when display should be updated.
   */
  public boolean left ()
  {
    if (selMod == - 1 || selLength == 0 || selWeek == -1) return (false);
    mod[selCur][selMod].left (selWeek, selLength);
    return (true);
  }

  /** Shifts the selection rightwards.
   * @return true when display should be updated.
   */
  public boolean right ()
  {
    if (selMod == - 1 || selLength == 0 || selWeek == -1) return (false);
    mod[selCur][selMod].right (selWeek, selLength);
    return (true);
  }

  /** Shifts the selection leftwards and follows it.
   * @return true when display should be updated.
   */
  public boolean followLeft ()
  {
    if (selMod == - 1 || selLength == 0 || selWeek == -1) return (false);
    selWeek = mod[selCur][selMod].left (selWeek, selLength);
    return (true);
  }

  /** Shifts the selection rightwards and follows it.
   * @return true when display should be updated.
   */
  public boolean followRight ()
  {
    if (selMod == - 1 || selLength == 0 || selWeek == -1) return (false);
    selWeek = mod[selCur][selMod].right (selWeek, selLength);
    return (true);
  }

  /** Checks whether a teacher is selected.
   * @return true if a teacher is selected.
   */
  public boolean teacherSelected ()
  {
    return (selLoad != 0);
  }

  /** Gets the affected load to selected teacher for given week.
   * @param week The index of the week.
   * @return the teacher load.
   */
  public int computeLoad (int week)
  {
    int load = 0;
    if (selLoad < 0)
      for (int j = 0; j < curs.length; j++)
        for (int i = 0; i < mod[j].length; i++)
          load += mod[j][i].teacherLoad (tSort[selLoad + teachers.length],
                                         week);
    else for (int i = 0; i < mod[selCur].length; i++)
      load += mod[selCur][i].studentLoad (week);
    return (load);
  }

  /** Gets the affected load to students for given week.
   * @param week The index of the week.
   * @return the student load.
   */
  public int computeStudentLoad (int week)
  {
    int load = 0;
    for (int i = 0; i < mod[selCur].length; i++)
    {
      if (mod[selCur][i].sloaded ())
        load += mod[selCur][i].studentLoad (week);
    }
    return (load);
  }

  /** Gets the affectable student load for given week.
   * @param week The index of the week.
   * @return the student load.
   */
  public int studentLoad (int week)
  {
    int load = 0;
    for (int i = 0; i < mod[selCur].length; i++)
      if (mod[selCur][i].sloaded ())
        load += mod[selCur][i].studentLoad (week);
    return (load);
  }

  /** Affects activities to teachers.
   * @param curs Index of the cursus.
   * @param mod Index of the module.
   * @param typAct Type of the activity.
   * @param names Teacher names.
   */
  public void affectTo (int curs, int mod, int typAct, String[] names)
  {
    int[] numAff = new int[names.length];
    if (teachers.length == 0)
    {
      teachers = new String[] {names[0]};
      tSort = new int[] {0};
    }
    for (int i = 0; i < names.length; i++)
      numAff[i] = findTeacher (names[i], 0, tSort.length);
    this.mod[curs][mod].setTeachers (typAct, numAff);
  }

  /** Affects specific activity to teachers.
   * @param curs Index of the cursus.
   * @param mod Index of the module.
   * @param numAct Index of the activity.
   * @param names Teacher names.
   */
  public void affectSpec (int curs, int mod, int numAct, String[] names)
  {
    int[] numAff = new int[names.length];
    if (teachers.length == 0)
    {
      teachers = new String[] {names[0]};
      tSort = new int[] {0};
    }
    for (int i = 0; i < names.length; i++)
      numAff[i] = findTeacher (names[i], 0, tSort.length);
    this.mod[curs][mod].affectSpec (numAct, numAff);
  }

  /** Recursively gets the index of a teacher.
   * @param name Teacher name.
   * @param from Range start index.
   * @param to Range end index.
   * @return the index of the teacher
   */
  private int findTeacher (String name, int from, int to)
  {
    int test = (from + to) / 2;
    if (test < tSort.length
        && name.equals (teachers[tSort[test]])) return tSort[test];
    if (to - from < 1)
    {
      String[] tmp = new String[teachers.length + 1];
      for (int i = 0; i < teachers.length; i ++) tmp[i] = teachers[i];
      tmp[teachers.length] = name;
      int[] ts = new int[tmp.length];
      for (int i = 0; i < to; i ++) ts[i] = tSort[i];
      ts[to] = teachers.length;
      for (int i = to; i < teachers.length; i ++) ts[i+1] = tSort[i];
      teachers = tmp;
      tSort = ts;
      return (tSort[to]);
    }
    if (name.compareTo (teachers[tSort[test]]) > 0)
      return (findTeacher (name, test + 1, to));
    return (findTeacher (name, 0, test));
  }

  /** Selects the next actor (student or teacher).
   * @param direction Selection direction.
   */
  public void toggleLoad (int direction)
  {
    selLoad += direction;
    if (selLoad >= students.length) selLoad = - teachers.length;
    else if (selLoad < - teachers.length) selLoad = students.length - 1;
  }

  /** Gets the selected load name.
   * @return the selected load name.
   */
  public String loadName ()
  {
    if (selLoad < 0) return (teachers[tSort[selLoad + teachers.length]]);
    return (students[sSort[selLoad]]);
  }

  /** Gets the selected student name.
   * @return the selected student name.
   */
  public String loadStudentName ()
  {
    return (students[sSort[0]]);
  }

  /** Gets the selected teacher name.
   * @return the selected teacher name.
   */
  public String loadTeacherName ()
  {
    if (selLoad < 0) return (teachers[tSort[selLoad + teachers.length]]);
    return (new String (""));
  }

  /** Shifts an activity to the left of the main teacher position.
   * @param curs Index of the cursus.
   * @param mod Index of the module.
   * @param act Index of the activity.
   */
  public void setOnLeft (int curs, int mod, int act)
  {
    this.mod[curs][mod].setOnLeft (act);
  }

  /** Shifts an activity to the right of the main teacher position.
   * @param curs Index of the cursus.
   * @param mod Index of the module.
   * @param act Index of the activity.
   */
  public void setOnRight (int curs, int mod, int act)
  {
    this.mod[curs][mod].setOnRight (act);
  }

  /** Gets left-shifted activities of the selected cursus.
   * @return true if it is left-shifted for each activity.
   */
  public boolean[] onLeft (int mod)
  {
    return (this.mod[selCur][mod].activitiesOnLeft ());
  }

  /** Gets right-shifted activities of the selected cursus.
   * @return true if it is right-shifted for each activity.
   */
  public boolean[] onRight (int mod)
  {
    return (this.mod[selCur][mod].activitiesOnRight ());
  }

  /** Gets the number of cursus.
   * @return the number of cursus.
   */
  public int countOfCursus ()
  {
    return (mod.length);
  }

  /** Selects the next cursus.
   * @param on selection direction: next of true, previous if false.
   */
  public void toggleCursus (boolean on)
  {
    if (on)
    {
      if (++selCur == mod.length) selCur = 0;
    }
    else if (--selCur == -1) selCur = mod.length - 1;
    selMod = -1;
  }

  /** Gets the name of a module in the selected cursus.
   * @param module Index of the module.
   * @return the name of the module.
   */
  public String name (int module)
  {
    return (mod[selCur][module].name ());
  }

  /** Gets the name of a module.
   * @param curs Index of the cursus.
   * @param module Index of the module.
   * @return the name of the module.
   */
  public String name (int curs, int module)
  {
    return (mod[curs][module].name ());
  }

  /** Checks whether student loads are cumulated for a given module.
   * @param curs Index of the cursus.
   * @param mod Index of the module.
   * @return true if loads are cumulated.
   */
  public boolean sloaded (int curs, int mod)
  {
    return (this.mod[curs][mod].sloaded ());
  }

  /** Releases student load cumulation for a given module.
   * @param curs Index of the cursus.
   * @param mod Index of the module.
   */
  public void noStudentLoad (int curs, int mod)
  {
    this.mod[curs][mod].noStudentLoad ();
  }

  /** Sets a technical (BUT-like) identifier to given module.
   * @param curs Index of the cursus.
   * @param mod Index of the module.
   * @param text Identifier of the module.
   */
  public void setId (int curs, int mod, String text)
  {
    this.mod[curs][mod].setId (text);
  }

  /** Gets the technical (BUT-like) identifier of a module in selected cursus.
   * @param module Index of the module.
   * @return the technical identifier of the module.
   */
  public String id (int module)
  {
    return (mod[selCur][module].id ());
  }

  /** Returns the technical (BUT-like) identifier of a module.
   * @param curs Index of the cursus.
   * @param module Index of the module.
   * @return the technical identifier of the module.
   */
  public String id (int curs, int module)
  {
    return (mod[curs][module].id ());
  }

  /** Sets additional description to a module.
   * @param curs Index of the cursus.
   * @param mod Index of the module.
   * @param text Identifier of the module.
   */
  public void setSubtitle (int curs, int mod, String text)
  {
    this.mod[curs][mod].setSubtitle (text);
  }

  /** Gets the additional description of a module in the selected cursus.
   * @param module Index of the module.
   * @return the additional description.
   */
  public String subtitle (int module)
  {
    return (mod[selCur][module].subtitle ());
  }

  /** Gets the additional description of a module in the selected cursus.
   * @param curs Index of the cursus.
   * @param module Index of the module.
   * @return the additional description.
   */
  public String subtitle (int curs, int module)
  {
    return (mod[curs][module].subtitle ());
  }

  /** Gets the year at start of the planning.
   * @return the year at start of the planning.
   */
  public int scolarYear ()
  {
    return (scolarYear);
  }

  /** Gets the teacher list.
   * @return the teacher list.
   */
  public String[] teachers ()
  {
    return (teachers);
  }

  /** Gets a teacher name.
   * @param num Index of the teacher in the list.
   * @return the teacher name.
   */
  public String teacherName (int num)
  {
    return (teachers[tSort[num]]);
  }

  /** Gets a teacher load on given week.
   * @param num Index of the teacher in the list.
   * @param week Index of the week.
   * @return the teacher name.
   */
  public int teacherLoad (int num, int week)
  {
    int load = 0;
    for (int j = 0; j < curs.length; j++)
      for (int i = 0; i < mod[j].length; i++)
        load += mod[j][i].teacherLoad (tSort[num], week);
    return (load);
  }

  /** Gets the activity sequence of a module.
   * @param curs Index of the cursus.
   * @param mod Index of the module.
   * @return the type of each activity in the sequence.
   */
  public String[] progression (int curs, int mod)
  {
    return (this.mod[curs][mod].progression ());
  }

  /** Gets the number of activities in a module.
   * @param curs Index of the cursus.
   * @param mod Index of the module.
   * @return the number of activities.
   */
  public int activityCount (int curs, int mod)
  {
    return (this.mod[curs][mod].activityCount ());
  }

  /** Gets the start week of a module.
   * @param curs Index of the cursus.
   * @param mod Index of the module.
   * @return the index of the start week.
   */
  public int moduleStart (int curs, int mod)
  {
    int[] wk = this.mod[curs][mod].activeWeeks ();
    if (wk[0] != 0) return (0);
    int sw = 1;
    while (wk[sw] == 0)
      if (++sw == wk.length) return (0);
    sw += sem.startWeekNumber ();
    if (sw > sem.lastWeekOfYearNumber ()) sw -= sem.lastWeekOfYearNumber ();
    return (sw);
  }

  /** Gets the end week of a module.
   * @param curs Index of the cursus.
   * @param mod Index of the module.
   * @return the index of the end week.
   */
  public int moduleEnd (int curs, int mod)
  {
    int[] wk = this.mod[curs][mod].activeWeeks ();
    if (wk[wk.length - 1] != 0) return (0);
    int ew = wk.length - 2;
    while (wk[ew] == 0)
      if (--ew == -1) return (0);
    ew += sem.startWeekNumber ();
    if (ew > sem.lastWeekOfYearNumber ()) ew -= sem.lastWeekOfYearNumber ();
    return (ew);
  }

  /** Gets the holly weeks in a module.
   * @param curs Index of the cursus.
   * @param mod Index of the module.
   * @return the index of each holly week.
   */
  public int[] moduleHollyWeeks (int curs, int mod)
  {
    int[] wk = this.mod[curs][mod].activeWeeks ();
    int lw = wk.length - 1;
    while (wk[lw] == 0)
      if (--lw == -1) return (new int[0]);

    int[] sk = sem.weekDurations ();
    int[] hk = new int[sk.length];
    int nb = 0;
    boolean ok = false;
    for (int i = 0; i < lw; i++)
    {
      if (wk[i] != 0) ok = true;
      else if (ok && sk[i] != 0) hk[nb++] = i;
    }

    int[] res = new int[nb];
    int sw = sem.startWeekNumber ();
    int lyw = sem.lastWeekOfYearNumber ();
    for (int i = 0; i < nb; i++)
    {
      res[i] = hk[i] + sw;
      if (res[i] > lyw) res[i] -= lyw;
    }
    return (res);
  }

  /** Gets the teachers for a specific type of activity in a module.
   * @param curs Index of the cursus.
   * @param mod Index of the module.
   * @param typact Activity type.
   * @return the teachers for this activity type.
    */
  public String[] moduleTeachers (int curs, int mod, int typact)
  {
    int[] ids = this.mod[curs][mod].teachers (typact);
    if (ids == null) return (new String[0]);
    String[] tch = new String[ids.length];
    for (int i = 0; i < tch.length; i++) tch[i] = teachers[ids[i]];
    return (tch);
  }

  /** Gets the specific teachers in a module.
   * @param curs Index of the cursus.
   * @param mod Index of the module.
   * @return the specific teachers for each activity.
   */
  public String[][] moduleSpecTeachers (int curs, int mod)
  {
    int[][] ids = this.mod[curs][mod].specTeachers ();
    String[][] tch = new String[ids.length][];
    for (int i = 0; i < ids.length; i++)
    {
      tch[i] = new String[ids[i].length];
      for (int j = 0; j < ids[i].length; j++) tch[i][j] = teachers[ids[i][j]];
    }
    return (tch);
  }

  /** Gets the left-shifted activities of a module.
   * @param curs Index of the cursus.
   * @param mod Index of the module.
   * @return true if it is left-shifted for each activity.
   */
  public boolean[] moduleOnLeft (int curs, int mod)
  {
    return (this.mod[curs][mod].onLeftActivities ());
  }

  /** Gets the right-shifted activities of a module.
   * @param curs Index of the cursus.
   * @param mod Index of the module.
   * @return true if it is right-shifted for each activity.
   */
  public boolean[] moduleOnRight (int curs, int mod)
  {
    return (this.mod[curs][mod].onRightActivities ());
  }

  /** Gets the editable date of the planned semester.
   * @return the editable date.
   */
  public String date ()
  {
    return (sem.date ());
  }

  /** Gets the planned semester.
   * @return the planned semester.
   */
  public ShukanSemester semester ()
  {
    return (sem);
  }

  /** Selects an activity in the selected module.
   * @param mod Index of the module.
   * @param pos Position of the activity in the planning line.
   * @param zone Indicates whether a week interval can be selected.
   * @return true if the view should be updated.
   */
  public boolean select (int mod, int pos, boolean zone)
  {
    int selection = this.mod[selCur][mod].select (pos);
    if (selection != -1)
    {
      if (selWeek != -1) deactivate ();

      if (zone && selMod == mod && selection != selAct)
      {
        if (selection < selAct)
        {
          selLength += selAct - selection;
          selAct = selection;
        }
        else if (selection < selAct + selLength)
        {
          if (selection - selAct < selLength / 2)
          {
            selLength -= (selection - selAct);
            selAct = selection;
          }
          else
            selLength = selection - selAct + 1;
        }
        else
          selLength = selection - selAct + 1;
      }
      else
      {
        selMod = mod;
        selLength = 1;
        selAct = selection;
      }
      return true;
    }
    return false;
  }

  /** Gets the first selected activity.
   * @return the activity index.
   */
  public int selectedActivity ()
  {
    return (selAct);
  }

  /** Exchanges first and last activities in the selected area.
   */
  public void exchangeExtremes ()
  {
    if (selMod != -1 && selAct != -1 && selLength > 1)
      this.mod[selCur][selMod].exchange (selAct, selAct + selLength - 1);
  }
}
