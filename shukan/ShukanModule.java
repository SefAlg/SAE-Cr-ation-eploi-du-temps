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


/** A module for Shukan planner. */
public class ShukanModule
{
  /** Module name. */
  private String name;
  /** Module official (BUT-like)identifier. */
  private String butid;
  /** Module teachers. */
  private String subtitle = null;
  /** Queue of module activities. */
  private int[] activ = null;
  /** Queues of planed activities. */
  private ShukanSchedule sched = null;
  /** Module scheduling status. */
  private boolean scheduled = false;
  /** Teacher affectation for main courses. */
  private int[] affectCM = null;
  /** Teacher affectation for supervised trainings. */
  private int[] affectTD = null;
  /** Teacher affectation for practical works. */
  private int[] affectTP = null;
  /** Teacher affectation for each activity. */
  private int[][] affect = null;
  /** Left shift status for activities. */
  private boolean[] onLeft = null;
  /** Right shift status for activities. */
  private boolean[] onRight = null;
  /** Working weeks for the module. */
  private int[] activeWeeks = null;
  /** Indicates whether student load is cumulated. */
  private boolean sloaded = true;

  /** Specific activity : main course. */
  public final static int ACT_CM = 0;   // Cours magistral
  /** Specific activity : 1 hour long control. */
  public final static int ACT_EV2 = 1;  // Evaluation d'1h
  /** Specific activity : 2 hours long control. */
  public final static int ACT_EV1 = 2;  // Evaluation de 2h
  /** Specific activity : supervised training in standard room. */
  public final static int ACT_TD = 3;   // TD en salle de cours
  /** Specific activity : supervised training in computer room. */
  public final static int ACT_TM = 4;   // TD en salle ordi
  /** Specific activity : supervised training in parallel. */
  public final static int ACT_TV = 5;   // TD en parallele (eval)
  /** Specific activity : SAe supervised training in standard room. */
  public final static int ACT_SD = 6;   // TD de SAE en salle de cours
  /** Specific activity : SAe supervised training in computer room. */
  public final static int ACT_SM = 7;   // TD de SAE en salle ordi
  /** Specific activity : Practical work in computer room. */
  public final static int ACT_TP = 8;   // TP en salle ordi
  /** Specific activity : 4 hours long practical work in computer room. */
  public final static int ACT_TP4 = 8;   // TP de 4h en salle ordi
  /** Specific activity : 2 hours long practical work in computer room. */
  public final static int ACT_TP2 = 9;   // TP de 2h en salle ordi
  /** Specific activity : 2 hours long practical work in standard room. */
  public final static int ACT_TQ2 = 10;  // TP en salle de cours
  /** Specific activity : SAe practical work in computer room. */
  public final static int ACT_SP = 11;  // TP de SAE en salle ordi
  /** Specific activity : SAe practical work in standard room. */
  public final static int ACT_SQ = 12;  // TQ de SAE en salle de cours
  /** Activity types. */
  private final static String[] TYPES = {"CM", "EV2", "EV1",
                                         "TD", "TM", "TV", "SD", "SM",
                                         "TP4", "TP2", "TQ2", "SP", "SQ"};
  /** Activity symbol. */
  public final static String[] DISPLAY = {"C", "E", "e",
                                          "d", "m", "v", "s", "z",
                                          "P", "p", "q", "S", "Z"};
  /** Activity duration (in hours). */
  public final static int[] SLOAD = {2, 2, 1, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2};

  /** Default maximal number of activities in a module. */
  private final static int DEFAULT_ACTIVITY_NUMBER = 30;
  /** Maximal number of activities per week. */
  public final static int MAX_ACTIV_PER_WEEK = 6;


  /** Constructs a module.
   * @param name The module name.
   */
  public ShukanModule (String name)
  {
    this.name = new String (name);
    butid = new String (name);
    activ = new int[DEFAULT_ACTIVITY_NUMBER];
    affect = new int[DEFAULT_ACTIVITY_NUMBER][];
  }

  /** Adds an activity to the module.
   * @param num Index of the module.
   * @param type Activity type.
   * @return true if the addition was successful.
   */
  public boolean addActivity (int num, String type)
  {
    int numT = -1;
    for (int i = 0; i < TYPES.length && numT == -1; i++)
      if (type.equals (TYPES[i])) numT = i;
    if (numT == -1) return (false);
    if (num == activ.length)
    {
      int[] tmp = new int[activ.length + DEFAULT_ACTIVITY_NUMBER];
      int[][] tmpaff = new int[activ.length + DEFAULT_ACTIVITY_NUMBER][];
      for (int i = 0; i < activ.length; i++)
      {
        tmp[i] = activ[i];
        tmpaff[i] = affect[i];
      }
      activ = tmp;
      affect = tmpaff;
    }
    activ[num] = numT;
    affect[num] = new int[0];
    return (true);
  }

  /** Closes the list of activities.
   * @param size Number of activities to be schedulled.
   */
  public void closeActivities (int size)
  {
    if (size != activ.length)
    {
      int[] tmp = new int[size];
      int[][] tmpaff = new int[size][];
      for (int i = 0; i < size; i++)
      {
        tmp[i] = activ[i];
        tmpaff[i] = affect[i];
      }
      activ = tmp;
      affect = tmpaff;
    }

    onLeft = new boolean[activ.length];
    onRight = new boolean[activ.length];
    for (int i = 0; i < onLeft.length; i++)
    {
      onLeft[i] = false;
      onRight[i] = false;
    }
  }

  /** Places activities of an unschedulled module at regularly-spaced positions.
   */
  public void autoPlan ()
  {
    if (sched == null)
    {
      sched = new ShukanSchedule (activeWeeks.length, MAX_ACTIV_PER_WEEK);
      for (int i = 0; i < activeWeeks.length; i++)
        if (activeWeeks[i] == 0) sched.kill (i);
    }
    int durall = 0;
    for (int i = 0; i < activeWeeks.length; i++) durall += activeWeeks[i];
    double period = durall / (float) (activ.length);
    int remainder = 0;
    int placed = 0;
    int lastWeek = 0;
    for (int i = 0; i < activeWeeks.length && placed < activ.length; i++)
    {
      remainder += activeWeeks[i];
      while (remainder > period / 2 && placed < activ.length)
      {
        sched.add (i, placed++);
        lastWeek = i;
        remainder -= period;
      }
    }
    while (placed < activ.length) sched.add (lastWeek, placed++);
    scheduled = true;
  }

  /** Schedules all activities.
   * @param weeks the shedulled week for each activity.
   */
  public void schedule (int[] weeks)
  {
    if (sched == null)
    {
      sched = new ShukanSchedule (activeWeeks.length, MAX_ACTIV_PER_WEEK);
      for (int i = 0; i < activeWeeks.length; i++)
        if (activeWeeks[i] == 0) sched.kill (i);
    }
    for (int i = 0; i < weeks.length; i++) sched.add (weeks[i], i);
    scheduled = true;
  }

  /** Unschedules all the module.
   */
  public void unschedule ()
  {
    sched.clear ();
    scheduled = false;
  }

  /** Schedules an activity.
   * @param num Index of the activity.
   * @param week Week to schedule.
   */
  public void schedule (int num, int week)
  {
    if (sched == null)
    {
      sched = new ShukanSchedule (activeWeeks.length, MAX_ACTIV_PER_WEEK);
      for (int i = 0; i < activeWeeks.length; i++)
        if (activeWeeks[i] == 0) sched.kill (i);
    }
    sched.add (week, num);
    scheduled = true;
  }

  /** Checks whether the module is schedulled.
   * @return true if the module is schedulled.
   */
  public boolean isScheduled ()
  {
    return (scheduled);
  }

  /** Gets the name of the module.
   * @return the name of the module.
   */
  public String name ()
  {
    return (name);
  }

  /** Gets the official (BUT-like) identifier of the module.
   * @return the official identifier of the module.
   */
  public String id ()
  {
    return (butid);
  }

  /** Sets the start week of the module.
   * @param num Week index from planning start week.
   */
  public void setStartWeek (int num)
  {
    for (int i = 0; i < num; i++) disableWeek (i);
  }

  /** Sets the end week of the module.
   * @param num Week index from planning start week.
   */
  public void setEndWeek (int num)
  {
    for (int i = num + 1; i < activeWeeks.length; i++) disableWeek (i);
  }

  /** Sets a non-working week for the module.
   * @param num Week index from planning start week.
   */
  public void setHollyWeek (int num)
  {
    disableWeek (num);
  }

  /** Gets the activities of the module.
   * @return the sequence of activity types.
   */
  public int[] activities ()
  {
    return (activ);
  }

  /** Gets module activities schedulled in a given week.
   * @param week Index of the week.
   * @return the sequence of activity types in this week.
   */
  public int[] schedule (int week)
  {
    return (sched.contents (week));
  }

  /** Gets the complete activity schedule.
   * @return the complete sequence of schedulled weeks for the activities.
   */
  public int[] schedule ()
  {
    return (sched.contents ());
  }

  /** Shifts an activity one step to the left.
   * @param week The index of the start week where activities should be shifted.
   * @param ln The number of working weeks where activities should be shifted.
   * @return the index of the start week where the first activity was shifted.
   */
  public int left (int week, int ln)
  {
    return ((ln == 1) ? sched.left (week) : sched.left (week, week + ln - 1));
  }

  /** Shifts an activity one step to the right.
   * @param week The index of the start week where activities should be shifted.
   * @param ln The number of working weeks where activities should be shifted.
   * @return the index of the start week where the first activity was shifted.
   */
  public int right (int week, int ln)
  {
    return ((ln == 1) ? sched.right (week) : sched.right (week, week + ln - 1));
  }

  /** Checks whether the student load is cumulated for the module.
   * @return true if student loads should be cumulated.
   */
  public boolean sloaded ()
  {
    return (sloaded);
  }

  /** Clears student load accumulator for the module.
   */
  public void noStudentLoad ()
  {
    sloaded = false;
  }

  /** Gets the student load for the module in a given week.
   * @param week Index of the week.
   * @return the student load.
   */
  public int studentLoad (int week)
  {
    int[] sch = sched.contents (week);
    int load = 0;
    for (int i = 0; i < sch.length; i++) load += SLOAD[activ[sch[i]]];
    return (load);
  }

  /** Gets the teacher load for the module in a given week.
   * @param week Index of the week.
   * @return the teacher load.
   */
  public int teacherLoad (int teacher, int week)
  {
    int[] sch = sched.contents (week);
    int load = 0;
    for (int i = 0; i < sch.length; i++)
      for (int j = 0; j < affect[sch[i]].length; j++)
        if (teacher == affect[sch[i]][j]) load += SLOAD[activ[sch[i]]];
    return (load);
  }

  /** Assigns teachers to an activity type for the module.
   * @param act Activity type.
   * @param teacher Teachers to assign.
   */
  public void setTeachers (int act, int[] teacher)
  {
    if (act == ACT_TP)
    {
      affectTP = teacher;
      for (int i = 0; i < activ.length; i++)
        if (activ[i] >= act) affect[i] = teacher;
    }
    else if (act == ACT_TD)
    {
      affectTD = teacher;
      for (int i = 0; i < activ.length; i++)
        if (activ[i] >= act && activ[i] < ACT_TP) affect[i] = teacher;
    }
    else
    {
      affectCM = teacher;
      for (int i = 0; i < activ.length; i++)
        if (activ[i] < ACT_TD) affect[i] = teacher;
    }
  }

  /** Assigns tecahers to a specific activity.
   * @param act Index of the activity in the sequence.
   * @param teacher Teachers to assign.
   */
  public void affectSpec (int act, int[] teacher)
  {
    affect[act] = teacher;
  }

  /** Shifts an activity display to the left.
   * @param act Index of the activity in the sequence.
   */
  public void setOnLeft (int act)
  {
    onRight[act] = false;
    onLeft[act] = true;
  }

  /** Shifts an activity display to the right.
   * @param act Index of the activity in the sequence.
   */
  public void setOnRight (int act)
  {
    onLeft[act] = false;
    onRight[act] = true;
  }

  /** Gets the left-shifted activities for display.
   * @return the sequence of left-shifted activities.
   */
  public boolean[] activitiesOnLeft ()
  {
    return (onLeft);
  }

  /** Gets the right-shifted activities for display.
   * @return the sequence of right-shifted activities.
   */
  public boolean[] activitiesOnRight ()
  {
    return (onRight);
  }

  /** Sets an official (BUT-like) identifier to the module.
   * @param text The official identifier.
   */
  public void setId (String text)
  {
    butid = text;
  }

  /** Sets a additional description to the module.
   * @param text The additional description.
   */
  public void setSubtitle (String text)
  {
    subtitle = text;
  }

  /** Gets the additional description of the module.
   * @return the description or null if none.
   */
  public String subtitle ()
  {
    return (subtitle);
  }

  /** Sets module working weeks.
   * @param aw Sequence of working weeks capacities.
   */
  public void setActiveWeeks (int[] aw)
  {
    activeWeeks = aw;
  }

  /** Deactivates a week: sets its working capacity to 0.
   * @param num Index of the week in the sequence of working weeks.
   */
  public void disableWeek (int num)
  {
    activeWeeks[num] = 0;
  }

  /** Gets the module working weeks.
   * @return the sequence of working week capacities.
   */
  public int[] activeWeeks ()
  {
    return (activeWeeks);
  }

  /** Gets the module sequence of activities.
   * @return the sequence of activity types.
   */
  public String[] progression ()
  {
    String[] prog = new String[activ.length];
    for (int i = 0; i < prog.length; i++) prog[i] = TYPES[activ[i]];
    return (prog);
  }

  /** Gets the teachers for a kind of activities.
   * @param act Activity type.
   * @return the list of teachers for this type of activity.
   */
  public int[] teachers (int act)
  {
    if (act == ACT_TP) return (affectTP);
    else if (act == ACT_TD) return (affectTD);
    else return (affectCM);
  }

  /** Gets the specific teachers for all activities.
   * @returns the list of teachers for each specific activity.
   */
  public int[][] specTeachers ()
  {
    int[][] st = new int[activ.length][];
    for (int i = 0; i < activ.length; i++)
    {
      int[] aff = affectCM;
      if (activ[i] >= ACT_TP) aff = affectTP;
      else if (activ[i] >= ACT_TD) aff = affectTD;

      boolean ok = true;
      if (affect[i].length != aff.length) ok = false;
      else
      {
        for (int j = 0; ok && j < affect[i].length; j++)
          if (affect[i][j] != aff[j]) ok = false;
      }
      st[i] = (ok ? new int[0] : affect[i]);
    }
    return (st);
  }

  /** Gets the left-shifted activities for display.
   * @return for each activity, true if it is left-shifted.
   */
  public boolean[] onLeftActivities ()
  {
    return (onLeft);
  }

  /** Gets the right-shifted activities for display.
   * @return for each activity, true if it is right-shifted.
   */
  public boolean[] onRightActivities ()
  {
    return (onRight);
  }

  /** Gets the number of activities in the sequence.
   * @return the number of activities.
   */
  public int activityCount ()
  {
    return (activ == null ? 0 : activ.length);
  }

  /** Gets the activity at given position.
   * @return the index of the found activity or -1 if none.
   */
  public int select (int position)
  {
    return (sched.select (position));
  }

  /** Gets a description of the module.
   * @return a string of characters, that represents the module.
   */
  public String toString ()
  {
    String s = name + " " + activ.length;
    for (int i = 0; i < activ.length; i++)
      s += " " + TYPES[activ[i]];
    return s;
  }

  /** Exchanges first and last activities at given positions.
   * @param a1 position of the first activity.
   * @param a2 position of the last activity.
   */
  public void exchange (int a1, int a2)
  {
    int n1 = sched.identify (a1);
    int n2 = sched.identify (a2);
    System.out.println (n1 + " <--> " + n2);
    int val = activ[n1];
    activ[n1] = activ[n2];
    activ[n2] = val;
    int[] aff = affect[n1];
    affect[n1] = affect[n2];
    affect[n2] = aff;
    boolean on = onLeft[n1];
    onLeft[n1] = onLeft[n2];
    onLeft[n2] = on;
    on = onRight[n1];
    onRight[n1] = onRight[n2];
    onRight[n2] = on;
  }
}
