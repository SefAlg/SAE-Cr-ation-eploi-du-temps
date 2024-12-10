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
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;



/** LaTeX file editor for Shukan planner. */
public class ShukanTex
{
  /** Shukan data base. */
  private ShukanData data;
  /** Year file suffix name. */
  private String dateSuffix;
  /** Year file prefix name. */
  private String dateName;

  /** TeX file suffix. */
  private final static String SUFFIX = ".tex";
  /** Load directory prefix. */
  private final static String LATEX_DIR_PREFIX = "loads";
  /** Teacher load file prefix. */
  private final static String LOAD_FILE_NAME = "charges";
  /** Page width. */
  private final static int PICT_WIDTH = 180;
  /** Page height. */
  private final static int PICT_HEIGHT = 270;
  /** Page top margin. */
  private final static int TOP_MARGIN = -30;
  /** Page left margin. */
  private final static int LEFT_MARGIN = -20;

  /** Month numbers. */
  private static final String[] MONTH_SHORT = {
    "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
  /** Month french names. */
  private static final String[] MONTH_NAME = {
    "janvier", "f\\'evrier", "mars", "avril", "mai", "juin",
    "juillet", "ao\\^ut", "septembre", "octobre", "novembre", "d\\'ecembre"};


  /** Creates a LaTeX file for semester edition.
   * @param data Shukan data base.
   */
  public ShukanTex (ShukanData data)
  {
    this.data = data;

    // Prepares the date suffix and full name
    Calendar kal = Calendar.getInstance ();
    int jour = kal.get (Calendar.DAY_OF_MONTH);
    dateSuffix = (kal.get (Calendar.YEAR) - 2000)
                 + MONTH_SHORT[kal.get (Calendar.MONTH)]
                 + (jour < 10 ? "0" + jour : "" + jour);
    dateName = kal.get (Calendar.DAY_OF_MONTH)
               + " " + MONTH_NAME[kal.get (Calendar.MONTH)] + " "
               + kal.get (Calendar.YEAR);

    // Starts the edition process
    try
    {
      int n = data.countOfCursus ();
      for (int i = 0; i < n; i++)
      {
        data.toggleCursus (true);
        write ();
      }
      writeTeacherLoad ();
    }
    catch (Exception ex)
    {
      System.out.println ("Texing soucy");
    }
  }

  /** Edits the semester.
   */
  public void write () throws IOException
  {
    int[] weekNo = data.weekNumbers ();
    int nbWeek = weekNo.length;
    double weekHeight = PICT_HEIGHT / (nbWeek + 2.);
    int nbMod = data.numberOfModules ();
    double modWidth = PICT_WIDTH / (nbMod + 1.);

    DecimalFormat df = new DecimalFormat ("0.0");
    DecimalFormatSymbols dfs = new DecimalFormatSymbols ();
    dfs.setDecimalSeparator ('.');
    df.setDecimalFormatSymbols (dfs);

    // Opens the LaTeX file
    File texDir = new File (LATEX_DIR_PREFIX + "_" + dateSuffix);
    if (! texDir.exists ()) texDir.mkdir ();
    File texFile = new File (texDir,
                             data.cursusName () + "_" + dateSuffix + SUFFIX);
    if (texFile.exists ()) texFile.delete ();
    else texFile.createNewFile ();
    PrintWriter tex = new PrintWriter (texFile);

    // Edits the preamble */
    tex.println ("\\documentclass[francais,a4,11pt]{article}");
    tex.println ("\\usepackage[francais]{babel}");
    tex.println ("\\usepackage{graphics}");
    tex.println ("\\usepackage{color}");
    tex.println ("\\setlength{\\unitlength}{1.0mm}");
    tex.println ("\\textheight " + df.format (PICT_HEIGHT / 10.) + "cm");
    tex.println ("\\textwidth " + df.format (PICT_WIDTH / 10.) + "cm");
    tex.println ("\\topmargin " + df.format (TOP_MARGIN / 10.) + "cm");
    tex.println ("\\headheight 0.0cm");
    tex.println ("\\headsep 0.0cm");
    tex.println ("\\oddsidemargin " + df.format (LEFT_MARGIN / 10.) + "cm");
    tex.println ("\\evensidemargin " + df.format (LEFT_MARGIN / 10.) + "cm");
    tex.println ("\\pagestyle{empty}");
    tex.println ("\\begin{document}\n");
    tex.println ("\\begin{picture}("
                 + PICT_WIDTH + "," + PICT_HEIGHT + ")(0,0)");
    tex.println ("\\put(0," + df.format (weekHeight * (nbWeek + 1))
                 + "){\\makebox(" + PICT_WIDTH + "," + df.format (weekHeight)
                 + ")[l]{\\LARGE \\bf Progressions " + data.scolarYear () + "/"
                 + (data.scolarYear () + 1) + " -- "
                 + data.cursusName () + "}}");
    tex.println ("\\put(0," + df.format (weekHeight * (nbWeek + 1))
                 + "){\\makebox(" + PICT_WIDTH + "," + df.format (weekHeight)
                 + ")[r]{\\LARGE \\bf " + dateName + "}}");
    tex.println ("\\multiput(0,0)(0," + df.format (weekHeight)
                 + "){" + (nbWeek + 1) + "}{\\line(1,0){"
                 + df.format(modWidth * (nbMod + 1)) + "}}");
    tex.println ("\\multiput(" + df.format (modWidth / 2) + ",0)("
                 + df.format (modWidth) + ",0){" + (nbMod + 1)
                 + "}{\\line(0,1){" + df.format (weekHeight * (nbWeek + 1))
                 + "}}");
 
    // Edits the week numbers */
    for (int i = 0; i < nbWeek; i++)
    {
      tex.println ("\\put(0," + df.format (weekHeight * i) + "){\\makebox("
                   + df.format (modWidth / 2) + "," + df.format (weekHeight)
                   + "){S" + weekNo[nbWeek - 1 - i] + "}}");
    }

    // Edits the module names */
    String[] modNames = data.moduleNames ();
    for (int i = 0; i < nbMod; i++)
    {
      tex.println ("\\put(" + df.format (modWidth * (i + 0.5)) + ","
                   + df.format (weekHeight * (nbWeek + 0.5)) + "){\\makebox("
                   + df.format (modWidth) + "," + df.format (weekHeight / 2)
                   + "){\\scriptsize \\bf " + modNames[i] + "}}");

      String st = data.subtitle (i);
      if (st != null)
        tex.println ("\\put(" + df.format (modWidth * (i + 0.5)) + ","
                     + df.format (weekHeight * (nbWeek)) + "){\\makebox("
                     + df.format (modWidth) + "," + df.format (weekHeight / 2)
                     + "){\\tiny \\bf " + st + "}}");
    }

    // Edits the activities */
    for (int i = 0; i < nbMod; i++)
    {
      int[] act = data.activities (i);
      boolean[] onLeft = data.onLeft (i);
      boolean[] onRight = data.onRight (i);
      double lx = modWidth * (i + 1);
      int numact = 0;
      for (int j = 0; j < nbWeek; j++)
      {
        int[] sched = data.scheduleInWeek (i, j);
        for (int k = 0; k < sched.length; k++)
        {
          double x = lx;
          if (onLeft[numact]) x -= modWidth / 4.;
          else if (onRight[numact]) x += modWidth / 4.;
          numact++;
          double y = weekHeight * (nbWeek - j - (1.5 + 2 * k) / 10.);
          switch (act[sched[k]])
          {
            case ShukanModule.ACT_CM :
              tex.println ("\\put(" + df.format (x) + "," + df.format (y)
                           + "){\\circle*{1}}");
              break;
            case ShukanModule.ACT_EV2 :
              tex.println ("\\put(" + df.format (x-1) + "," + df.format (y-1)
                           + "){\\makebox(2,2){$\\ast\\ast$}}");
              break;
            case ShukanModule.ACT_EV1 :
              tex.println ("\\put(" + df.format (x-1) + "," + df.format (y-1)
                           + "){\\makebox(2,2){$\\ast$}}");
              break;
            case ShukanModule.ACT_TD :
              tex.println ("\\put(" + df.format (x) + "," + df.format (y)
                           + "){\\circle{1}}");
              break;
            case ShukanModule.ACT_TM :
              tex.println ("\\put(" + df.format (x) + "," + df.format (y)
                           + "){\\circle{1}}");
              tex.println ("\\put(" + df.format (x-1) + "," + df.format (y)
                           + "){\\line(1,0){2}}");
              break;
            case ShukanModule.ACT_TV :
              tex.println ("\\put(" + df.format (x) + "," + df.format (y)
                           + "){\\circle{1}}");
              tex.println ("\\put(" + df.format (x-1) + "," + df.format (y-1)
                           + "){\\makebox(2,2){X}}");
              break;
            case ShukanModule.ACT_SD :
              tex.println ("\\put(" + df.format (x) + "," + df.format (y)
                           + "){\\circle{1}}");
              tex.println ("\\put(" + df.format (x+1) + "," + df.format (y-1)
                           + "){s}");
              break;
            case ShukanModule.ACT_SM :
              tex.println ("\\put(" + df.format (x) + "," + df.format (y)
                           + "){\\circle{1}}");
              tex.println ("\\put(" + df.format (x-1) + "," + df.format (y)
                           + "){\\line(1,0){2}}");
              tex.println ("\\put(" + df.format (x+1) + "," + df.format (y-1)
                           + "){s}");
              break;
            case ShukanModule.ACT_TP4 :
              tex.println ("\\put(" + df.format (x-1) + ","
                           + df.format (y-0.5) + "){\\framebox(1,1)}");
              tex.println ("\\put(" + df.format (x+1) + ","
                           + df.format (y-0.5) + "){\\framebox(1,1)}");
              break;
            case ShukanModule.ACT_TP2 :
              tex.println ("\\put(" + df.format (x-0.5) + ","
                           + df.format (y-0.5) + "){\\framebox(1,1)}");
              break;
            case ShukanModule.ACT_TQ2 :
              tex.println ("\\put(" + df.format (x-0.5) + ","
                           + df.format (y-0.5) + "){\\framebox(1,1){+}}");
              break;
            case ShukanModule.ACT_SP :
              tex.println ("\\put(" + df.format (x-0.5) + ","
                           + df.format (y-0.5) + "){\\framebox(1,1)}");
              tex.println ("\\put(" + df.format (x+1) + "," + df.format (y-1)
                           + "){s}");
              break;
            case ShukanModule.ACT_SQ :
              tex.println ("\\put(" + df.format (x-0.5) + ","
                           + df.format (y-0.5) + "){\\framebox(1,1){+}}");
              tex.println ("\\put(" + df.format (x+1) + "," + df.format (y-1)
                           + "){s}");
              break;
          }
        }
      }
    }

    // Edits the week loads */
    int[] durations = data.weekDurations ();
    for (int i = 0; i < nbWeek; i++)
    {
      if (durations[nbWeek - 1 - i] != 0)
        tex.println ("\\put(" + df.format (modWidth * (nbMod + 0.5)) + ","
                     + df.format (weekHeight * i) + "){\\makebox("
                     + df.format (modWidth / 2) + "," + df.format (weekHeight)
                     + "){" + data.studentLoad (nbWeek - 1 - i) + "}}");
    }

    // Terminates the file edition
    tex.println ("\\end{picture}");
    tex.println ("\\end{document}");
    tex.close ();
  }

  /** Edits all teacher load files.
   */
  public void writeTeacherLoad () throws IOException
  {
    int[] weekNo = data.weekNumbers ();
    int nbWeek = weekNo.length;
    String[] teach = data.teachers ();

    // Opens the teachers directory and file
    File teachDir = new File (LATEX_DIR_PREFIX + "_" + dateSuffix);
    if (! teachDir.exists ()) teachDir.mkdir ();
    File teacherFile = new File (teachDir,
                                 LOAD_FILE_NAME + dateSuffix + SUFFIX);
    if (teacherFile.exists ()) teacherFile.delete ();
    else teacherFile.createNewFile ();
    PrintWriter tex = new PrintWriter (teacherFile);

    // Edits the preamble */
    tex.println ("\\documentclass[francais,a4paper,landscape,11pt]{article}");
    tex.println ("\\usepackage[francais]{babel}");
    tex.println ("\\usepackage{graphics}");
    tex.println ("\\usepackage{color}");
    tex.println ("\\setlength{\\unitlength}{1.0mm}");
    tex.println ("\\textheight 17.0cm");
    tex.println ("\\textwidth 24.0cm");
    tex.println ("\\topmargin -1.0cm");
    tex.println ("\\headheight 0.0cm");
    tex.println ("\\headsep 0.0cm");
    tex.println ("\\oddsidemargin -1.0cm");
    tex.println ("\\evensidemargin -1.0cm");
    tex.println ("\\pagestyle{empty}");
    tex.println ("\\begin{document}\n");
    tex.println ("\\begin{center}");

    tex.println ("{\\LARGE \\bf Charges hebdomadaires "
                 + data.scolarYear () + "/" + (data.scolarYear () + 1)
                 + "}");
    tex.println ("\\begin{picture}(20,1) \\end{picture}");
    tex.println ("{\\large \\bf (" + dateName + ")}\\\\");
    tex.println ("\\begin{picture}(4,6)(0,0) \\end{picture} \\\\");

    // Write the week bar
    tex.print ("\\begin{tabular}{|l||");
    for (int j = 0; j < nbWeek; j++) tex.print ("r|");
    tex.print ("}\n\\hline\n");
    for (int j = 0; j < nbWeek; j++) tex.print (" & S" + weekNo[j]);
    tex.println (" \\\\\n\\hline \\hline");

    for (int i = 0; i < teach.length; i++)
    {
      tex.print (data.teacherName (i));
      for (int j = 0; j < nbWeek; j++)
        tex.print (" & " + data.teacherLoad (i, j));
      tex.println ("\\\\ \\hline");
    }
    tex.println ("\\end{tabular} \\\\");
    tex.println ("\\end{center}");

    tex.println ("{\\bf L\\'egende des grilles de progressions :} \\\\");
    tex.println ("\\begin{tabular}{llcllcll}");

    tex.print ("\\begin{picture}(4,4)(0,0) ");
    tex.print ("\\put(2,1){\\circle*{1}} ");
    tex.println ("\\end{picture}");
    tex.println ("& CM & \\begin{picture}(10,1) \\end{picture} ");

    tex.print ("& \\begin{picture}(4,4)(0,0) ");
    tex.print ("\\put(2,1){\\circle{1}} ");
    tex.println ("\\end{picture}");
    tex.println ("& TD en salle de cours ");
    tex.println ("& \\begin{picture}(10,1) \\end{picture} ");

    tex.print ("& \\begin{picture}(4,4)(0,0) ");
    tex.print ("\\put(1.5,1){\\framebox(1,1)} ");
    tex.println ("\\end{picture}");
    tex.println ("& TP en salle machine\\\\");

    tex.print ("\\begin{picture}(4,4)(0,0) ");
    tex.print ("\\put(1,0){\\makebox(2,2){$\\ast$}} ");
    tex.println ("\\end{picture}");
    tex.println ("& Evaluation d'une heure ");

    tex.print ("& & \\begin{picture}(4,4)(0,0) ");
    tex.print ("\\put(2,1){\\circle{1}} ");
    tex.print ("\\put(1,1){\\line(1,0){2}} ");
    tex.println ("\\end{picture}");
    tex.println ("& TD en salle machine (TM) ");

    tex.print ("& & \\begin{picture}(4,4)(0,0) ");
    tex.print ("\\put(1.5,1){\\framebox(1,1){+}} ");
    tex.println ("\\end{picture}");
    tex.println ("& TP en salle de cours\\\\");

    tex.print ("\\begin{picture}(4,4)(0,0) ");
    tex.print ("\\put(1,0){\\makebox(2,2){$\\ast\\ast$}} ");
    tex.println ("\\end{picture}");
    tex.println ("& Evaluation de deux heures ");

    tex.print ("& & .s & SAe ");

    tex.print ("& & .s & SAe");

    tex.println ("\\end{tabular}");
    tex.println ("\\end{document}");
    tex.close ();
  }
}
