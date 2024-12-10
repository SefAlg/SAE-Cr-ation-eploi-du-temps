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
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StreamTokenizer;
import java.io.PrintWriter;
import java.text.DecimalFormat;

/** Shukan input/output file manager. */
public class ShukanIO
{
  /** Dictionary language. */
  private int dico = -1; // undetermined

  /** Data base. */
  private ShukanData data = null;
  /** Release number. */
  private int release = 0;
  /** Release file. */
  private File releaseFile = null;
  /** Semester directory. */
  private File semDir = null;
  /** Cursus directories. */
  private File[] cursusDir = null;

  /** Suffix for files. */
  private final static String FILE_SUFFIX = ".txt";
  /** Default data directory. */
  private final static String DATA_DIR_NAME = "data";
  /** Semester file name. */
  private final static String SEMESTER_FILE_NAME = "semestre";
  /** Release file name. */
  private final static String RELEASE_FILE_NAME = "version";
  /** Weeks file name. */
  private final static String WEEKS_FILE_NAME = "weeks";
  /** Cursus file or directory name. */
  private final static String CURSUS_FILE_NAME = "cursus";
  /** Modules file or directory name. */
  private final static String MODULES_FILE_NAME = "modules";
  /** Default number of modules. */
  private final static int DEFAULT_MODULES_NB = 30;


  /** Semester dictionnary: scholar year number for planned cursus. */
  private final static String[] SEM_SCOLAR_YEAR = {
    "ScolarYear", "AnneeScolaire"};
  /** Semester upload dictionnary: year number at planning start. */
  private final static String[] SEM_FIRST_WEEK_YEAR = {
    "YearOfFirstWeek", "AnneeDeDebut"};
  /** Semester upload dictionnary: week number at planning start. */
  private final static String[] SEM_FIRST_WEEK = {
    "FirstWeek", "PremiereSemaine"};
  /** Semester upload dictionnary: number of weeks in the semester. */
  private final static String[] SEM_LENGTH = {
    "SemesterLength", "LongueurDuSemestre"};
  /** Semester upload dictionnary: maximal number of activities in a week. */
  private final static String[] SEM_WEEK_MAX_LENGTH = {
    "DefaultWeekLength", "ChargeMaxHebdomadaire"};
  /** Semester upload dictionnary: number of working hours in standard week. */
  private final static String[] SEM_WEEK_DURATION = {
    "WeekLength", "ChargeMax"};


  /** Module dictionnary : start of parametrization lines. */
  private final static String[] MODULE_PARAMETERS = {
    "Params", "Parametres"};
  /** Module dictionnary : first week of the module. */
  private final static String[] MODULE_START = {
    "StartsOn", "DebutModule"};
  /** Module dictionnary : last week of the module. */
  private final static String[] MODULE_END = {
    "EndsOn", "FinModule"};
  /** Module dictionnary : non-working week in the module. */
  private final static String[] MODULE_HOLYWEEK = {
    "Hollyweek", "SemaineLibre"};
  /** Module dictionnary : official (BUT-like) identifier of the module. */
  private final static String[] MODULE_ID = {
    "Id", "Id"};
  /** Module dictionnary : no cumulation of student load. */
  private final static String[] MODULE_NO_SLOAD = {
    "NoStudentLoad", "DechargeEtudiant"};
  /** Module dictionnary : additional description of the module. */
  private final static String[] MODULE_SUBTITLE = {
    "Subtitle", "SousTitre"};
  /** Module dictionnary : standard teacher for lectures. */
  private final static String[] MODULE_AFFECT_CM = {
    "AffectCM", "AffectationCM"};
  /** Module dictionnary : standard teacher for training works. */
  private final static String[] MODULE_AFFECT_TD = {
    "AffectTD", "AffectationTD"};
  /** Module dictionnary : standard teacher for practical works. */
  private final static String[] MODULE_AFFECT_TP = {
    "AffectTP", "AffectationTP"};
  /** Module dictionnary : specific teacher for an activity. */
  private final static String[] MODULE_AFFECT_SPEC = {
    "AffectSpec", "Affectation"};
  /** Module dictionnary : left-shifted display of an activity. */
  private final static String[] MODULE_ON_LEFT = {
    "OnLeft", "AGauche"};
  /** Module dictionnary : right-shifted display of an activity. */
  private final static String[] MODULE_ON_RIGHT = {
    "OnRight", "ADroite"};
  /** Module dictionnary : planned weeks for each activity. */
  private final static String[] MODULE_SCHEDULE = {
    "Sched", "Progression"};


  /** Creates the input/output manager.
   * @param data Shukan data base.
   */
  public ShukanIO (ShukanData data)
  {
    this.data = data;

    // Checks existence of a data directory
    File dataDir = new File (DATA_DIR_NAME);
    if (! dataDir.exists ())
    {
      System.out.println (
        "Sorry, no " + dataDir.getName () + " directory found");
      System.exit (0);
    }

    // Gets the present semester
    StreamTokenizer st = null;
    try
    {
      st = new StreamTokenizer (
        new FileReader (new File (dataDir, SEMESTER_FILE_NAME + FILE_SUFFIX)));
      st.nextToken ();
      int an = (int) st.nval;
      st.nextToken ();
      semDir = new File (DATA_DIR_NAME, an + "_" + st.sval);
    }
    catch (FileNotFoundException e)
    {
      System.out.println ("No semester file found");
      System.exit (0);
    }
    catch (Exception e)
    {
      System.out.println ("Semester file damaged");
      System.exit (0);
    }
    if (! semDir.exists ())
    {
      System.out.println (
        "No " + semDir.getName () + " directory found");
      System.exit (0);
    }

    // Gets the last release
    File relDir = null;
    DecimalFormat format = new DecimalFormat ("000");
    DecimalFormat format2 = new DecimalFormat ("000000");
    try
    {
      releaseFile = new File (semDir, RELEASE_FILE_NAME + FILE_SUFFIX);
      st = new StreamTokenizer (new FileReader (releaseFile));
      st.nextToken ();
      release = (int) st.nval;
      st.nextToken ();
      relDir = new File (semDir, "V" + format.format (release)
                                     + "_" + format2.format ((int) st.nval));
    }
    catch (FileNotFoundException e)
    {
      System.out.println ("No release file found");
      System.exit (0);
    }
    catch (Exception e)
    {
      System.out.println ("Release file damaged");
      System.exit (0);
    }
    if (! relDir.exists ())
    {
      System.out.println (
        "No " + relDir.getName () + " directory found");
      System.exit (0);
    }

    // Gets the calendar
    loadCalendar (relDir);

    // Gets the cursus
    File cursusFile = new File (relDir, CURSUS_FILE_NAME + FILE_SUFFIX);
    if (! (cursusFile.exists ()))
    {
      System.out.println ("No cursus file found");
      System.exit (0);
    }
    String[] cNames = getNames (cursusFile);
    if (cNames.length == 0)
    {
      System.out.println ("No cursus found in cursus file");
      System.exit (0);
    }
    cursusDir = new File[cNames.length];
    for (int i = 0; i < cNames.length; i++)
    {
      cursusDir[i] = new File (relDir, cNames[i]);
      if (! semDir.exists ())
      {
        System.out.println ("No " + cNames[i] + " directory found");
        System.exit (0);
      }
    }
    data.declareCursus (cNames);
    for (int i = 0; i < cursusDir.length; i++) loadModules (i);
  }

  /** Loads the calendar.
   * @param semDir Path to the directory of semester files.
   */
  private void loadCalendar (File semDir)
  {
    // Checks weeks file
    File weekFile = new File (semDir, WEEKS_FILE_NAME + FILE_SUFFIX);
    if (! weekFile.exists ())
    {
      System.out.println ("Sorry, no " + WEEKS_FILE_NAME + " file found");
      System.exit (0);
    }
    try
    {
      StreamTokenizer st = new StreamTokenizer (new FileReader (weekFile));

      // Reads scolar year
      st.nextToken ();
      if (st.ttype != StreamTokenizer.TT_WORD)
        abort (WEEKS_FILE_NAME, "?", st.lineno ());
      for (int i = 0; dico == -1 && i < SEM_SCOLAR_YEAR.length; i++)
        if (st.sval.equals (SEM_SCOLAR_YEAR[i])) dico = i;
      if (dico != -1)
      {
        st.nextToken ();
        if (st.ttype != StreamTokenizer.TT_NUMBER) dico = -1;
      }
      if (dico == -1)
      {
        String msg = new String ("should be " + SEM_SCOLAR_YEAR[0]);
        for (int i = 1; i < SEM_SCOLAR_YEAR.length; i++)
          msg += " or " + SEM_SCOLAR_YEAR[i];
        abort (WEEKS_FILE_NAME, msg, st.lineno ());
      }
      int scolYear = (int) st.nval;
 
      // Reads year of first week
      st.nextToken ();
      if (st.ttype != StreamTokenizer.TT_WORD)
        abort (WEEKS_FILE_NAME, "?", st.lineno ());
      if (! st.sval.equals (SEM_FIRST_WEEK_YEAR[dico]))
        abort (WEEKS_FILE_NAME, "should be "
               + SEM_FIRST_WEEK_YEAR[dico], st.lineno ());
      st.nextToken ();
      if (st.ttype != StreamTokenizer.TT_NUMBER)
        abort (WEEKS_FILE_NAME, SEM_FIRST_WEEK_YEAR[dico], st.lineno ());
      int year = (int) st.nval;
 
      // Reads first week number
      st.nextToken ();
      if (st.ttype != StreamTokenizer.TT_WORD)
        abort (WEEKS_FILE_NAME, "?", st.lineno ());
      if (! st.sval.equals (SEM_FIRST_WEEK[dico]))
        abort (WEEKS_FILE_NAME, "should be " + SEM_FIRST_WEEK[dico],
               st.lineno ());
      st.nextToken ();
      if (st.ttype != StreamTokenizer.TT_NUMBER)
        abort (WEEKS_FILE_NAME, SEM_FIRST_WEEK[dico], st.lineno ());
      int firstWeek = (int) st.nval;
 
      // Reads semester length
      st.nextToken ();
      if (st.ttype != StreamTokenizer.TT_WORD)
        abort (WEEKS_FILE_NAME, "?", st.lineno ());
      if (! st.sval.equals (SEM_LENGTH[dico]))
        abort (WEEKS_FILE_NAME, SEM_LENGTH[dico], st.lineno ());
      st.nextToken ();
      if (st.ttype != StreamTokenizer.TT_NUMBER)
        abort (WEEKS_FILE_NAME, SEM_LENGTH[dico], st.lineno ());
      int semLength = (int) st.nval;
 
      // Reads default week length
      st.nextToken ();
      if (st.ttype != StreamTokenizer.TT_WORD)
        abort (WEEKS_FILE_NAME, "?", st.lineno ());
      if (! st.sval.equals (SEM_WEEK_MAX_LENGTH[dico]))
        abort (WEEKS_FILE_NAME, SEM_WEEK_MAX_LENGTH[dico], st.lineno ());
      st.nextToken ();
      if (st.ttype != StreamTokenizer.TT_NUMBER)
        abort (WEEKS_FILE_NAME, SEM_WEEK_MAX_LENGTH[dico], st.lineno ());
      int duration = (int) st.nval;
 
      // Creates the semester
      ShukanSemester sem = new ShukanSemester (year, firstWeek,
                                               semLength, duration);
      data.setSemester (scolYear, sem);

      // Reads the week durations
      st.nextToken ();
      while (st.ttype != StreamTokenizer.TT_EOF)
      {
        if (st.ttype != StreamTokenizer.TT_WORD)
          abort (WEEKS_FILE_NAME, "?", st.lineno ());
        if (st.sval.equals (SEM_WEEK_DURATION[dico]))
        {
          st.nextToken ();
          if (st.ttype != StreamTokenizer.TT_WORD)
            abort (WEEKS_FILE_NAME, SEM_WEEK_DURATION[dico], st.lineno ());
          if (st.sval.charAt (0) != 'S')
            abort (WEEKS_FILE_NAME, SEM_WEEK_DURATION[dico], st.lineno ());
          int noWeek = Integer.parseInt (st.sval.substring (1));
          st.nextToken ();
          if (st.ttype != StreamTokenizer.TT_NUMBER)
            abort (WEEKS_FILE_NAME, SEM_WEEK_DURATION[dico], st.lineno ());
          if (! sem.setDuration (noWeek, (int) (st.nval)))
            abort (WEEKS_FILE_NAME, SEM_WEEK_DURATION[dico], st.lineno ());
        }
        st.nextToken ();
      }
    }
    catch (IOException e)
    {
      System.out.println ("Problem in " + WEEKS_FILE_NAME + " file");
      System.exit (0);
    }
  }

  /** Loads the modules of a cursus.
   * @int n Index of the cursus.
   */
  private void loadModules (int n)
  {
    File modulesFile = new File (cursusDir[n], MODULES_FILE_NAME + FILE_SUFFIX);
    if (! (modulesFile.exists ()))
    {
      System.out.println (
        "No modules file found in " + cursusDir[n].getName ());
      System.exit (0);
    }
    String[] names = getNames (modulesFile);
    if (names.length == 0)
    {
      System.out.println ("Empty modules file in " + cursusDir[n].getName ());
      System.exit (0);
    }
    data.setModules (n, names);

    // Reads each module
    for (int i = 0; i < names.length; i++)
      loadModule (n, i, cursusDir[n], names[i]);
  }

  /** Loads a module.
   * @param cursNum Index of the cursus.
   * @param modNum Index of the module.
   * @param modDir Path to module directory.
   * @param name Name of the module.
   */
  private void loadModule (int cursNum, int modNum, File modDir, String name)
  {
    int sw = data.startWeekNumber ();
    int lw = data.lastWeekOfYearNumber ();
    int ss = data.semesterSize ();
    int[] wd = data.weekDurations ();

    // Checks module file
    File modFile = new File (modDir, name + FILE_SUFFIX);
    if (! modFile.exists ())
    {
      System.out.println ("Sorry, no " + name + " file found");
      System.exit (0);
    }
    try
    {
      // Reads modules
      int actNum = 0;
      StreamTokenizer st = new StreamTokenizer (new FileReader (modFile));
      st.nextToken ();
      while (st.ttype != StreamTokenizer.TT_EOF)
      {
        if (st.ttype != StreamTokenizer.TT_WORD)
          abort (name, "SYNTAX", st.lineno ());
        if (st.sval.equals (MODULE_PARAMETERS[dico]))
        {
          data.closeActivities (cursNum, modNum, actNum);
        }
        else if (st.sval.equals (MODULE_START[dico]))
        {
          st.nextToken ();
          if (st.ttype != StreamTokenizer.TT_NUMBER)
            abort (name, MODULE_START[dico], st.lineno ());
          data.setModuleStart (cursNum, modNum, (int) st.nval);
        }
        else if (st.sval.equals (MODULE_END[dico]))
        {
          st.nextToken ();
          if (st.ttype != StreamTokenizer.TT_NUMBER)
            abort (name, MODULE_END[dico], st.lineno ());
          data.setModuleEnd (cursNum, modNum, (int) st.nval);
        }
        else if (st.sval.equals (MODULE_HOLYWEEK[dico]))
        {
          st.nextToken ();
          if (st.ttype != StreamTokenizer.TT_NUMBER)
            abort (name, MODULE_HOLYWEEK[dico], st.lineno ());
          data.setModuleHolly (cursNum, modNum, (int) st.nval);
        }
        else if (st.sval.equals (MODULE_ID[dico]))
        {
          st.nextToken ();
          if (st.ttype != StreamTokenizer.TT_WORD)
            abort (name, MODULE_ID[dico], st.lineno ());
          data.setId (cursNum, modNum, new String (st.sval));
        }
        else if (st.sval.equals (MODULE_NO_SLOAD[dico]))
        {
          data.noStudentLoad (cursNum, modNum);
        }
        else if (st.sval.equals (MODULE_SUBTITLE[dico]))
        {
          st.nextToken ();
          if (st.ttype != StreamTokenizer.TT_WORD)
            abort (name, MODULE_SUBTITLE[dico], st.lineno ());
          data.setSubtitle (cursNum, modNum, new String (st.sval));
        }
        else if (st.sval.equals (MODULE_AFFECT_CM[dico]))
        {
             st.nextToken ();
             if (st.ttype != StreamTokenizer.TT_WORD)
               abort (name, MODULE_AFFECT_CM[dico], st.lineno ());
             data.affectTo (cursNum, modNum, ShukanModule.ACT_CM,
                            new String[] {st.sval});
        }
        else if (st.sval.equals (MODULE_AFFECT_TD[dico]))
        {
          st.nextToken ();
          if (st.ttype != StreamTokenizer.TT_NUMBER)
            abort (name, MODULE_AFFECT_TD[dico], st.lineno ());
          String[] aff = new String[(int) st.nval];
          for (int j = 0; j < aff.length; j++)
          {
            st.nextToken ();
            if (st.ttype != StreamTokenizer.TT_WORD)
              abort (name, MODULE_AFFECT_TD[dico], st.lineno ());
            aff[j] = st.sval;
          }
          data.affectTo (cursNum, modNum, ShukanModule.ACT_TD, aff);
        }
        else if (st.sval.equals (MODULE_AFFECT_TP[dico]))
        {
          st.nextToken ();
          if (st.ttype != StreamTokenizer.TT_NUMBER)
            abort (name, MODULE_AFFECT_TP[dico], st.lineno ());
          String[] aff = new String[(int) st.nval];
          for (int j = 0; j < aff.length; j++)
          {
            st.nextToken ();
            if (st.ttype != StreamTokenizer.TT_WORD)
              abort (name, MODULE_AFFECT_TP[dico], st.lineno ());
            aff[j] = st.sval;
          }
          data.affectTo (cursNum, modNum, ShukanModule.ACT_TP, aff);
        }
        else if (st.sval.equals (MODULE_AFFECT_SPEC[dico]))
        {
          st.nextToken ();
          if (st.ttype != StreamTokenizer.TT_NUMBER)
            abort (name, MODULE_AFFECT_SPEC[dico], st.lineno ());
          int num = (int) st.nval;
          st.nextToken ();
          if (st.ttype != StreamTokenizer.TT_NUMBER)
            abort (name, MODULE_AFFECT_SPEC[dico], st.lineno ());
          String[] aff = new String[(int) st.nval];
          for (int j = 0; j < aff.length; j++)
          {
            st.nextToken ();
            if (st.ttype != StreamTokenizer.TT_WORD)
              abort (name, MODULE_AFFECT_SPEC[dico], st.lineno ());
            aff[j] = st.sval;
          }
          data.affectSpec (cursNum, modNum, num, aff);
        }
        else if (st.sval.equals (MODULE_ON_LEFT[dico]))
        {
          st.nextToken ();
          if (st.ttype != StreamTokenizer.TT_NUMBER)
            abort (name, MODULE_ON_LEFT[dico], st.lineno ());
          data.setOnLeft (cursNum, modNum, (int) st.nval);
        }
        else if (st.sval.equals (MODULE_ON_RIGHT[dico]))
        {
          st.nextToken ();
          if (st.ttype != StreamTokenizer.TT_NUMBER)
            abort (name, MODULE_ON_RIGHT[dico], st.lineno ());
          data.setOnRight (cursNum, modNum, (int) st.nval);
        }
        else if (st.sval.equals (MODULE_SCHEDULE[dico]))
        {
          int nb = data.activityCount (cursNum, modNum);
          boolean ok = (nb != 0);
          if (ok)
          {
            int[] sch = new int[nb];
            for (int i = 0; ok && i < nb; i++)
            {
              st.nextToken ();
              if (st.ttype != StreamTokenizer.TT_NUMBER) ok = false;
              sch[i] = (int) st.nval;
              if (sch[i] < sw) sch[i] = lw - sw + sch[i];
              else sch[i] -= sw;
              if (sch[i] < 0 || sch[i] >= ss || wd[sch[i]] == 0)
                abort (name, MODULE_SCHEDULE[dico], st.lineno ());
            }
            if (ok) data.setSchedule (cursNum, modNum, sch);
          }
          if (! ok) abort (name, MODULE_SCHEDULE[dico], st.lineno ());
        }

        else if (! data.addActivity (cursNum, modNum, actNum++, st.sval))
          abort (name, st.sval, st.lineno ());
        st.nextToken ();
      }
      data.autoPlan (cursNum, modNum);
    }
    catch (IOException e)
    {
      System.out.println ("Problem in " + name + " file");
      System.exit (0);
    }
  }

  /** Interrupts the application in case of format problem.
   * @param name Name of the loaded module.
   * @param beacon Tag of the loaded feature.
   * @param lineno Number of the line where the error occured.
   */
  private static void abort (String name, String beacon, int lineno)
  {
    System.out.println (name + ", ligne " + lineno
                        + ": mauvais format pour " + beacon);
    System.exit (0);
  }

  /** Saves all the schedules.
   */
  public void save ()
  {
    // Updates the release file
    releaseFile.delete ();
    String now = data.date ();
    PrintWriter pw = null;
    try
    {
      releaseFile.createNewFile ();
      pw = new PrintWriter (releaseFile);
      pw.println (++release);
      pw.println (now);
    }
    catch (IOException exc)
    {
      System.out.println ("Unable to create the new release");
      System.exit (0);
    }
    finally
    {
      pw.close ();
    }
    
    // Creates the new release directory
    DecimalFormat format = new DecimalFormat ("000");
    File relDir = new File (semDir, "V" + format.format (release) + "_" + now);
    relDir.mkdir ();

    // Saves the calendar
    saveCalendar (relDir);

    // Creates the cursus file
    File curFile = new File (relDir, CURSUS_FILE_NAME + FILE_SUFFIX);
    try
    {
      curFile.createNewFile ();
      pw = new PrintWriter (curFile);
      for (int i = 0; i < cursusDir.length; i++)
        pw.println (data.cursusName (i));
    }
    catch (IOException exc)
    {
      System.out.println ("Unable to create cursus file");
      System.exit (0);
    }
    finally
    {
      pw.close ();
    }

    // Creates and updates cursus directories
    for (int i = 0; i < cursusDir.length; i++)
    {
      cursusDir[i] = new File (relDir, data.cursusName (i));
      cursusDir[i].mkdir ();
      save (i);
    }
  }

  /** Save the calendar of the semester.
   * @param dir Path to the semester directory.
   */
  private void saveCalendar (File dir)
  {
    File weekFile = new File (dir, WEEKS_FILE_NAME + FILE_SUFFIX);
    PrintWriter pw = null;
    try
    {
      weekFile.createNewFile ();
      pw = new PrintWriter (weekFile);
 
      // Writes scolar year
      pw.println (SEM_SCOLAR_YEAR[dico] + " " + data.scolarYear ());
      ShukanSemester sem = data.semester ();
      pw.println (SEM_FIRST_WEEK_YEAR[dico] + " " + sem.yearOfFirstWeek ());
      pw.println (SEM_FIRST_WEEK[dico] + " " + sem.startWeekNumber ());
      pw.println (SEM_LENGTH[dico] + " " + sem.length ());

      // Writes week durations
      int sd = sem.standardWeekDuration ();
      pw.println (SEM_WEEK_MAX_LENGTH[dico] + " " + sd);
      int[] wd = sem.weekDurations ();
      for (int i = 0; i < wd.length; i++)
        if (wd[i] != sd) pw.println (SEM_WEEK_DURATION[dico] + " S"
                                     + sem.weekNumber (i) + " " + wd[i]);
    }
    catch (IOException e)
    {
      System.out.println ("Can(t write " + WEEKS_FILE_NAME + " file");
      System.exit (0);
    }
    finally
    {
      pw.close ();
    }
  }

  /** Saves a schedulled cursus.
   * @param curs Index of the cursus.
   */
  private void save (int curs)
  {
    // Create module names file
    String[] modNames = data.moduleNames (curs);
    File moduleFile = new File (cursusDir[curs],
                                MODULES_FILE_NAME + FILE_SUFFIX);
    PrintWriter pw = null;
    try
    {
      moduleFile.createNewFile ();
      pw = new PrintWriter (moduleFile);
      for (int i = 0; i < modNames.length; i++) pw.println (modNames[i]);
    }
    catch (IOException exc)
    {
      System.out.println ("Unable to create modules file for "
                          + data.cursusName (curs));
      System.exit (0);
    }
    finally
    {
      pw.close ();
    }

    for (int i = 0; i < modNames.length; i++)
    {
      // Creates the module schedule file
      moduleFile = new File (cursusDir[curs], modNames[i] + FILE_SUFFIX);
      try
      {
        moduleFile.createNewFile ();
        pw = new PrintWriter (moduleFile);
        save (pw, curs, i);
      }
      catch (IOException exc)
      {
        System.out.println ("Unable to create schedule for " + modNames[i]);
        System.exit (0);
      }
      finally
      {
        pw.close ();
      }
    }
  }

  /** Saves a schedulled module.
   * @param pw Output stream towards module file.
   * @param curs Index of the cursus.
   * @param mod Index of the module.
   */
  private void save (PrintWriter pw, int curs, int mod)
  {
    // Writes the progression
    String[] prog = data.progression (curs, mod);
    for (int j = 0; j < prog.length; j++) pw.println (prog[j]);
    pw.println (MODULE_PARAMETERS[dico]);

    // Writes the appelation
    String st = data.id (curs, mod);
    if (st != null && ! st.equals (data.name (curs, mod)))
      pw.println (MODULE_ID[dico] + " " + st);
    st = data.subtitle (curs, mod);
    if (st != null) pw.println (MODULE_SUBTITLE[dico] + " " + st);
    if (! data.sloaded (curs, mod)) pw.println (MODULE_NO_SLOAD[dico]);

    // Writes the availabilities
    int wk = data.moduleStart (curs, mod);
    if (wk != 0) pw.println (MODULE_START[dico] + " " + wk);
    wk = data.moduleEnd (curs, mod);
    if (wk != 0) pw.println (MODULE_END[dico] + " " + wk);
    int[] hk = data.moduleHollyWeeks (curs, mod);
    for (int j = 0; j < hk.length; j++)
      pw.println (MODULE_HOLYWEEK[dico] + " " + hk[j]);

    // Writes the affectations
    String[] t = data.moduleTeachers (curs, mod, ShukanModule.ACT_CM);
    if (t.length != 0)
    {
      pw.print (MODULE_AFFECT_CM[dico]);
      for (int j = 0; j < t.length; j++) pw.print (" " + t[j]);
      pw.println ();
    }
    t = data.moduleTeachers (curs, mod, ShukanModule.ACT_TD);
    if (t.length != 0)
    {
      pw.print (MODULE_AFFECT_TD[dico] + " " + t.length);
      for (int j = 0; j < t.length; j++) pw.print (" " + t[j]);
      pw.println ();
    }
    t = data.moduleTeachers (curs, mod, ShukanModule.ACT_TP);
    if (t.length != 0)
    {
      pw.print (MODULE_AFFECT_TP[dico] + " " + t.length);
      for (int j = 0; j < t.length; j++) pw.print (" " + t[j]);
      pw.println ();
    }
    String[][] spt = data.moduleSpecTeachers (curs, mod);
    for (int j = 0; j < spt.length; j++)
      if (spt[j].length != 0)
      {
        pw.print (MODULE_AFFECT_SPEC[dico] + " " + j + " " + spt[j].length);
        for (int k = 0; k < spt[j].length; k++)
          pw.print (" " + spt[j][k]);
        pw.println ();
      }
    boolean[] dec = data.moduleOnLeft (curs, mod);
    for (int j = 0; j < dec.length; j++)
      if (dec[j]) pw.println (MODULE_ON_LEFT[dico] + " " + j);
    dec = data.moduleOnRight (curs, mod);
    for (int j = 0; j < dec.length; j++)
      if (dec[j]) pw.println (MODULE_ON_RIGHT[dico] + " " + j);

    // Writes the schedule
    int[] wn = data.weekNumbers ();
    int[] sched = data.schedule (curs, mod);
    pw.print (MODULE_SCHEDULE[dico]);
    for (int j = 0; j < sched.length; j++) pw.print (" " + wn[sched[j]]);
    pw.println ();
  }

  /** Gets the module names.
   * @param f File containing the module names.
   * @return the module names.
   */
  private static String[] getNames (File f)
  {
    String[] names = new String[DEFAULT_MODULES_NB];
    int num = 0;

    // Checks modules file
    try
    {
      // Reads names
      StreamTokenizer st = new StreamTokenizer (new FileReader (f));
      st.nextToken ();
      while (st.ttype != StreamTokenizer.TT_EOF)
      {
        if (st.ttype != StreamTokenizer.TT_WORD)
          abort (f.getName (), "?", st.lineno ());
        if (num == names.length)
        {
          String[] tmp = new String[names.length + DEFAULT_MODULES_NB];
          for (int i = 0; i < names.length; i++) tmp[i] = names[i];
          names = tmp;
        }
        names[num++] = new String (st.sval);
        st.nextToken ();
      }
      if (num != names.length)
      {
        String[] tmp = new String[num];
        for (int i = 0; i < num; i++) tmp[i] = names[i];
        names = tmp;
      }
    }
    catch (IOException e)
    {
      System.out.println ("Problem in " + f.getName () + " file");
      System.exit (0);
    }
    return (names);
  }
}
