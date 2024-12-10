// Eliot DUBREUIL, Sefer ALGUL et Anthony DAMAS | TD1 et TP1 | SAÉ S2.01

import shukan.Shukan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;

/**
 * La classe Sae21 est une application GUI basée sur Swing.
 */
public class Sae21 extends JFrame {
    
    private String selectedAnnee;
    private String selectedSemestre;
    private int nombreFormation=0;
    private int nbCours=0;
    private int Cours=1;
    private int indexVacances=0;
    private Date[][] dateVacances = new Date[5][2];;
    private List<Date[]> periodesVacances = new ArrayList<>();
    private List<String> exceptionsList = new ArrayList<>();
    private JComboBox<String> FormationComboBox = new JComboBox<>();
    private JComboBox<String> ProfesseurComboBox = new JComboBox<>();
    private JComboBox<String> selectionAnneeComboBox = new JComboBox<>();
    private JComboBox<String> selectionSemestreComboBox = new JComboBox<>();
    private JComboBox<String> selectionVersionComboBox = new JComboBox<>();
    private JComboBox<String> modifierAnneeSemestreComboBox = new JComboBox<>();
    private JLabel NombreFormationLabel = new JLabel("Nombre de formation : "+nombreFormation);
    private JLabel CoursLabel = new JLabel("Cours 1");
    private JTextArea DatesVacancesArea = new JTextArea(5, 20);

    private String repertoire,digitsSemester,digitsYear,moduleRepertoire,professeursRepertoire="data/professeurs.txt",backRepertoire;
    private String[] yearParts;
    private List<String> listLeapYear = Arrays.asList("2020","2024","2028","2032");
    private List<String> TD = Arrays.asList("TD","TM","TV","SD","SM");
    private List<String> TP = Arrays.asList("TP","TP4","TP2","TQ2","SP","SQ");
    private File[] dossiersAnneeEtSemestre;
    private File[] dossiersVersion;
    private File[] dossiersFormation;
    

    private SimpleDateFormat version = new SimpleDateFormat("yyMMdd");
    private SimpleDateFormat semaine = new SimpleDateFormat("w");
    private String todayDate = version.format(new Date());
    private Date dateDebut = new Date();
    private Date finDate = new Date();

    private Vacances vacances;

    /**
     * Constructeur de la classe Sae21 est la première page de la section "Créer" de l'application.
     */
    public Sae21() {
        setTitle("Sae 2.1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        Accueil accueil = new Accueil();
        accueil.setVisible(true);

        JLabel labelAnneeScolaire = new JLabel("Année Scolaire");
        JComboBox<String> anneeComboBox = new JComboBox<>(new String[]{"2023-2024", "2024-2025", "2025-2026", "2026-2027", "2027-2028", "2028-2029", "2029-2030"});
        JLabel labelSemestre = new JLabel("Semestre");
        JComboBox<String> semestreComboBox = new JComboBox<>(new String[]{"Semestre 1", "Semestre 2"});
        JButton boutonSuivant = new JButton("Suivant");

        Font font = labelAnneeScolaire.getFont();
        float size = font.getSize() * 2;
        labelAnneeScolaire.setFont(font.deriveFont(size));
        anneeComboBox.setFont(font.deriveFont(size));
        labelSemestre.setFont(font.deriveFont(size));
        semestreComboBox.setFont(font.deriveFont(size));
        boutonSuivant.setFont(font.deriveFont(size));

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 5, 10);
        add(labelAnneeScolaire, gbc);
        gbc.gridx = 2;
        add(anneeComboBox, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(labelSemestre, gbc);
        gbc.gridx = 2;
        add(semestreComboBox, gbc);

        JPanel panelButtons = new JPanel();
        panelButtons.add(boutonSuivant);
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.insets = new Insets(10,10, 10, 10);
        add(panelButtons, gbc);

        boutonSuivant.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedAnnee = (String) anneeComboBox.getSelectedItem();
                selectedSemestre = (String) semestreComboBox.getSelectedItem();
                
                if (selectedAnnee != null && !selectedAnnee.isEmpty() && selectedSemestre != null && !selectedSemestre.isEmpty()) {
                    yearParts = selectedAnnee.split("-");
                    String[] semesterParts = selectedSemestre.split(" ");

                    digitsYear = yearParts[0].substring(2)+""+yearParts[1].substring(2);
                    digitsSemester = semesterParts[0].substring(0, 1)+""+semesterParts[1];

                    repertoire = "data/" + digitsYear+"_"+digitsSemester;
                    File folder = new File(repertoire);

                    if (!folder.exists()) {
                        
                        folder.mkdirs();

                        String versionFolderName = repertoire + "/V001_" + todayDate;
                        File versionFolder = new File(versionFolderName);

                        versionFolder.mkdirs();

                        try {
                            File versionFile = new File(repertoire, "version.txt");
                            versionFile.createNewFile();
                            FileWriter writer = new FileWriter(versionFile);
                            writer.write("1\n" + todayDate);
                            writer.close();
                        } 
                        catch (IOException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Erreur lors de la création du fichier de version");
                        }

                        repertoire = versionFolderName;

                        try {
                            File cursusFile = new File(repertoire, "cursus.txt");
                            cursusFile.createNewFile();
                        } 
                        catch (IOException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Erreur lors de la création du fichier du cursus");
                        }

                        try {
                            File weeksFile = new File(repertoire, "weeks.txt");
                            weeksFile.createNewFile();
                            FileWriter writer = new FileWriter(weeksFile);
                            writer.write("ScolarYear "+yearParts[0]+ 
                                         "\nYearOfFirstWeek " + (digitsSemester.equals("S1") ? yearParts[0] : yearParts[1]) + 
                                         "\nFirstWeek " + (digitsSemester.equals("S1") ? (listLeapYear.contains(yearParts[1]) ? 36 : 35) : 4) + 
                                         "\nSemesterLength " + (digitsSemester.equals("S1") ? 21 : 22) +
                                         "\nDefaultWeekLength 40\n"
                                         );
                            writer.close();
                        } 
                        catch (IOException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Erreur lors de la création du fichier des semaines");
                        }

                    }
                    else {

                        File[] files = folder.listFiles();
                        Arrays.sort(files);

                        File  lastVersionFolder = files[files.length - 2];
                        String  lastVersion = lastVersionFolder.getName().substring(1, 4);

                        int newVersion = Integer.parseInt(lastVersion) + 1;
                        
                        repertoire = repertoire + "/V" + String.format("%03d", newVersion) + "_" + todayDate;

                        File newVersionFolder = new File(repertoire);
                        newVersionFolder.mkdirs();
                        
                        try {
                            copyFolder(lastVersionFolder, newVersionFolder);
                        } 
                        catch (IOException e1) {
                            e1.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Erreur lors de la copie des fichiers de la version précédent");
                        }
                        
                    }

                    
                    File professeursFile = new File(professeursRepertoire);
                    if (!professeursFile.exists()) {
                        try {
                            professeursFile.createNewFile();
                        } 
                        catch (IOException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Erreur lors de la création du fichier des professeurs");
                        }
                    }
                }

                if (vacances == null) {
                    vacances = new Vacances(false);
                }

                vacances.updateData(selectedAnnee, selectedSemestre); // Appel de la méthode updateData
                vacances.setVisible(true);
                dispose();
            }
        });
    }

    /**
     * La classe Vacances est une classe interne à la classe Sae21 de l'application.
     */
    private class Vacances extends JFrame {
        private JButton boutonPlus;
        private JLabel labelVacances;
        private JLabel labelDateVacances;
        private JButton boutonPrecedent;
        private JButton boutonSuivantFormation;
        private JTextArea datesVacancesArea;
        private Date dateDebut;
        private boolean isFirstDateSelected = false;


        /**
         * Constructeur de la classe Vacances est la deuxième page de la section "Créer" de l'application.
         */
        public Vacances(boolean modification) {
            setTitle("Semaines");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setExtendedState(JFrame.MAXIMIZED_BOTH);

            JButton retourAccueilButton = new JButton("Retour à l'accueil");
            retourAccueilButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose(); // Fermer la fenêtre actuelle
                    Accueil accueil = new Accueil(); // Créer une nouvelle instance de la fenêtre Accueil
                    accueil.setVisible(true); // Afficher la fenêtre Accueil
                }
            });

            boutonPlus = new JButton("+");
            labelVacances = new JLabel("Vacances :");
            labelDateVacances = new JLabel("Date des vacances (clique sur + pour définir les dates):");
            boutonPrecedent = new JButton("Précédent");
            boutonSuivantFormation = new JButton("Suivant");
            datesVacancesArea = DatesVacancesArea;
            datesVacancesArea.setEditable(false);

            Font font = boutonPlus.getFont();
            float size = font.getSize() * 2;
            boutonPlus.setFont(font.deriveFont(size));
            labelVacances.setFont(font.deriveFont(size));
            boutonPrecedent.setFont(font.deriveFont(size));
            retourAccueilButton.setFont(font.deriveFont(size));
            boutonSuivantFormation.setFont(font.deriveFont(size));
            datesVacancesArea.setFont(font.deriveFont(size));
            labelDateVacances.setFont(font.deriveFont((float)(font.getSize() * 1.35)));


            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.insets = new Insets(0,0, 0, 10);
            add(labelVacances, gbc);
            gbc.gridy = 1;
            gbc.insets = new Insets(0,0, 25, 0);
            add(new JLabel("(Vacances, jour fériés, ponts, etc...)"), gbc);

            gbc.gridy = 2;
            gbc.insets = new Insets(0,0, 0, 10);
            JPanel calendrierPanel = new JPanel();
            calendrierPanel.add(labelDateVacances);
            calendrierPanel.add(boutonPlus);
            add(calendrierPanel, gbc);

            gbc.gridy = 3;
            add(new JScrollPane(datesVacancesArea), gbc);

            if (modification) {
                gbc.gridy = 6;
                gbc.gridx = 0;
                gbc.gridwidth = 6;
                gbc.gridheight = 1;
                gbc.insets = new Insets(20,10, 10, 10);
                add(retourAccueilButton, gbc);
            }
            else {
                gbc.gridx = 0;
                gbc.gridy = 5;
                gbc.gridheight = 1;
                gbc.anchor = GridBagConstraints.SOUTHWEST;
                gbc.insets = new Insets(10,10, 10, 10);
                add(boutonPrecedent, gbc);

                gbc.gridx = 6;
                gbc.gridheight = 1;
                gbc.anchor = GridBagConstraints.SOUTHEAST;
                gbc.insets = new Insets(10,10, 10, 10);
                add(boutonSuivantFormation, gbc);
            }

            boutonPrecedent.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    exceptionsList.clear();
                    setVisible(false);
                    Sae21.this.setVisible(true);
                }
            });

            boutonPlus.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    selectDate("Sélectionnez une date de début du module");
                }
            });

            boutonSuivantFormation.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(repertoire + "/cursus.txt"));
                        while ((reader.readLine()) != null) {
                            nombreFormation++;
                        }
                        NombreFormationLabel.setText("Nombre de formation : " + nombreFormation);
                        reader.close();
                    } 
                    catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Erreur lors de du compte du nombre de formation");
                    }

                    dispose();
                    Formation formation = new Formation(false);
                    formation.setVisible(true);
                }
            });
        }

        /**
         * Met à jour les données sélectionnées pour l'année et le semestre.
         *
         * @param annee L'année sélectionnée.
         * @param semestre Le semestre sélectionné.
         */
        public void updateData(String annee, String semestre) {
            selectedAnnee = annee;
            selectedSemestre = semestre;
            // Ne définissez pas les valeurs par défaut ici
        }

        /**
         * Ouvre une boîte de dialogue pour sélectionner une date tout en vérifiant les vacances et les dates valides.
         *
         * @param message Le message à afficher dans la boîte de dialogue.
         */
        private void selectDate(String message) {
            JDateChooser dateChooser = new JDateChooser();

            // Définir la date de début minimale
            Calendar minDate = Calendar.getInstance();
            minDate.set(Calendar.YEAR, Integer.parseInt(digitsSemester.equals("S1") ? yearParts[0] : yearParts[1]));
            minDate.set(Calendar.WEEK_OF_YEAR, (digitsSemester.equals("S1") ? (listLeapYear.contains(yearParts[1]) ? 36 : 35) : 4));
            minDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Lundi de la semaine
            dateChooser.setMinSelectableDate(minDate.getTime());

            // Définir la date de fin maximale
            Calendar maxDate = Calendar.getInstance();
            maxDate.set(Calendar.YEAR, Integer.parseInt(yearParts[1]));
            maxDate.set(Calendar.WEEK_OF_YEAR, (digitsSemester.equals("S1") ? (listLeapYear.contains(yearParts[1]) ? 4 : 3) : 25));
            maxDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // Dimanche de la semaine 
            dateChooser.setMaxSelectableDate(maxDate.getTime());

            // Définir la date initiale comme date minimale
            dateChooser.setDate(minDate.getTime());

            if (isFirstDateSelected) {
                dateChooser.setMinSelectableDate(dateDebut);
                dateChooser.setDate(dateDebut);
                int option = JOptionPane.showConfirmDialog(null, dateChooser, message, JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    finDate = dateChooser.getDate();

                    // Vérifier si la date de fin est antérieure à la date de début
                    if (finDate.before(dateDebut)) {
                        JOptionPane.showMessageDialog(null, "La date de fin doit être postérieure à la date de début. Veuillez choisir une autre date de fin.");
                        selectDate("Sélectionnez une date de fin du module");
                        return;
                    }

                    // Vérifier si la date de fin est dans les vacances
                    for (Date[] datePair : dateVacances) {
                        Date startDate = datePair[0];
                        Date endDate = datePair[1];
                        if (startDate != null && endDate != null && finDate.after(startDate) && finDate.before(endDate)) {
                            JOptionPane.showMessageDialog(null, "Erreur : La date de fin est déjà dans les vacances");
                            selectDate("Sélectionnez une date de fin du module");
                            return;
                        }
                    }

                    updateLabelVacances(dateDebut, finDate);
                    isFirstDateSelected = false;
                }
            } else {
                int option = JOptionPane.showConfirmDialog(null, dateChooser, message, JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    dateDebut = dateChooser.getDate();

                    // Vérifier si la date de début est dans les vacances
                    for (Date[] datePair : dateVacances) {
                        Date startDate = datePair[0];
                        Date endDate = datePair[1];
                        if (startDate != null && endDate != null && dateDebut.after(startDate) && dateDebut.before(endDate)) {
                            JOptionPane.showMessageDialog(null, "Erreur : La date de début est déjà dans les vacances");
                            selectDate("Sélectionnez une date de début du module");
                            return;
                        }
                    }

                    isFirstDateSelected = true;
                    selectDate("Sélectionnez une date de fin du module");
                }
            }
        }

        /**
         * Met à jour l'étiquette des vacances avec les dates de début et de fin, et écrit les informations dans un fichier.
         *
         * @param debutDate La date de début.
         * @param finDate La date de fin.
         */
        private void updateLabelVacances(Date debutDate, Date finDate) {
            periodesVacances.add(new Date[]{debutDate, finDate});
            dateVacances[indexVacances][0] = debutDate;
            dateVacances[indexVacances][1] = finDate;
            indexVacances++;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(debutDate);

            Calendar calendarEnd = Calendar.getInstance();
            calendarEnd.setTime(finDate);

            try {
                File weeksFile = new File(repertoire, "weeks.txt");
                FileWriter writer = new FileWriter(weeksFile, true);

                int currentWeek = Integer.parseInt(semaine.format(debutDate));
                int lastWeek = Integer.parseInt(semaine.format(finDate));

                while (currentWeek <= lastWeek) {
                    // Calcul du nombre de jours dans les 6 premiers jours de la semaine, en excluant le dimanche
                    int joursSemaine = 0;
                    for (int i = 0; i < 7; i++) {
                        int jourSemaine = calendar.get(Calendar.DAY_OF_WEEK);
                        if (jourSemaine == Calendar.SUNDAY) {
                            calendar.add(Calendar.DAY_OF_MONTH, 1);
                            break;
                        }
                        joursSemaine++;
                        if (jourSemaine == calendarEnd.get(Calendar.DAY_OF_WEEK) && currentWeek == lastWeek) {
                            break;
                        }
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                    }

                    // Écriture dans le fichier
                    writer.write("WeekLength S" + currentWeek + " " + (joursSemaine == 6 ? 0 : (40 - (4 * joursSemaine))) + "\n");

                    // Passer à la semaine suivante
                    calendar.add(Calendar.WEEK_OF_YEAR, 1);
                    currentWeek++;

                    // Vérifier si nous avons atteint la dernière semaine
                    if (currentWeek > lastWeek) {
                        break;
                    }
                }
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erreur lors de l'ajoute des dates dans le fichier des semaines");
            }

            updateDatesVacancesArea();
        }

        /**
         * Met à jour l'affichage des périodes de vacances.
         */
        private void updateDatesVacancesArea() {
            StringBuilder datesBuilder = new StringBuilder();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            for (Date[] periode : periodesVacances) {
                String debutFormatted = dateFormat.format(periode[0]);
                String finFormatted = dateFormat.format(periode[1]);
                if (datesBuilder.length() > 0) {
                    datesBuilder.append("; \n");
                }
                datesBuilder.append("Début: ").append(debutFormatted).append(" | Fin: ").append(finFormatted);
            }
            datesVacancesArea.setText(datesBuilder.toString());
        }
    }

    /**
     * La classe Formation est une classe interne à la classe Sae21 de l'application.
     */
    private class Formation extends JFrame {

        /**
         * Constructeur de la classe Formation est la troisième page de la section "Créer" de l'application.
         */
        public Formation(boolean modification) {
            setTitle("Formation");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
    
            JLabel labelNomFormation = new JLabel("Nom de la Formation:");
            JTextField textFieldNomFormation = new JTextField(20);
            JLabel nombreFormationLabel=NombreFormationLabel;
            JButton boutonPlus = new JButton("+");
            JButton boutonPrecedent = new JButton("Précédent");
            JButton boutonSuivantModules = new JButton("Suivant"); // Ajout du bouton "Suivant"

            JButton retourAccueilButton = new JButton("Retour à l'accueil");
            retourAccueilButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    nombreFormation=0;
                    nombreFormationLabel.setText("Nombre de formation : " + nombreFormation);
                    dispose(); // Fermer la fenêtre actuelle
                    Accueil accueil = new Accueil(); // Créer une nouvelle instance de la fenêtre Accueil
                    accueil.setVisible(true); // Afficher la fenêtre Accueil
                }
            });
    
            Font font = labelNomFormation.getFont();
            float size = font.getSize() * 2;
            labelNomFormation.setFont(font.deriveFont(size));
            textFieldNomFormation.setFont(font.deriveFont(size));
            nombreFormationLabel.setFont(font.deriveFont(size));
            boutonPlus.setFont(font.deriveFont(size));
            boutonPrecedent.setFont(font.deriveFont(size));
            retourAccueilButton.setFont(font.deriveFont(size));
            boutonSuivantModules.setFont(font.deriveFont(size)); // Réglage de la police du bouton "Suivant"
    
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(10, 10, 10, 10);
            add(labelNomFormation, gbc);
    
            gbc.gridx = 2;
            add(textFieldNomFormation, gbc);

            gbc.gridx = 3;
            add(boutonPlus, gbc);
    
            if (modification) gbc.gridx = 1;
            else gbc.gridx = 0;
            gbc.gridy = 1;
            add(nombreFormationLabel, gbc);

            if (modification) {
                gbc.gridy = 4;
                gbc.gridx = 0;
                gbc.gridwidth = 5;
                gbc.gridheight = 1;
                gbc.insets = new Insets(20,10, 10, 10);
                add(retourAccueilButton, gbc);
            }
            else {
                gbc.gridx = 0;
                gbc.gridy = 3;
                gbc.anchor = GridBagConstraints.SOUTHWEST;
                gbc.insets = new Insets(10,10, 10, 10);
                add(boutonPrecedent, gbc);

                gbc.gridx = 4;
                gbc.gridy = 3;
                gbc.anchor = GridBagConstraints.SOUTHEAST;
                gbc.insets = new Insets(10,10, 10, 10);
                add(boutonSuivantModules, gbc);
            }


            boutonPlus.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    if (textFieldNomFormation.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Erreur : Le nom de la formation est vide");
                        return;
                    }

                    File foramtionFolder = new File(repertoire,"/" +textFieldNomFormation.getText());

                    if (!foramtionFolder.exists()) {
                        
                        foramtionFolder.mkdirs();

                        try {
                            // Ajouter la formation à la fin du fichier cursus.txt
                            File courseFile = new File(repertoire, "cursus.txt");
                            FileWriter writer = new FileWriter(courseFile, true);
                            writer.write(textFieldNomFormation.getText()+"\n");
                            writer.close();
                        }
                        catch (IOException ex) {
                            textFieldNomFormation.setText("");
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Erreur lors de l'écriture de la formation dans le fichier cursus.txt");
                        }

                        try {
                            // Création du fichier modules.txt
                            File modulesFile = new File(repertoire, "/"+textFieldNomFormation.getText()+"/modules.txt");
                            modulesFile.createNewFile();

                            nombreFormation++;
                            nombreFormationLabel.setText("Nombre de formation : " + nombreFormation);
                        }
                        catch (IOException ex) {
                            textFieldNomFormation.setText("");
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Erreur lors de la création du fichier modules.txt");
                        }

                        textFieldNomFormation.setText("");
                    }
                    else {
                        textFieldNomFormation.setText("");
                        JOptionPane.showMessageDialog(null, "Erreur : La formation existe déjà");
                    }
                }
            });
    
            boutonPrecedent.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    nombreFormation=0;
                    nombreFormationLabel.setText("Nombre de formation : " + nombreFormation);
                    dispose();
                    vacances.setVisible(true);
                }
            });
    
            boutonSuivantModules.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(repertoire + "/cursus.txt"));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) FormationComboBox.getModel();
                            if (model.getIndexOf(line) == -1) {
                                // Ajoute le nouveau mot au modèle
                                model.addElement(line);
                            }

                        }
                        reader.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Erreur lors de la lecture du fichier des formations");
                    }

                    if (nombreFormation<1) {
                        JOptionPane.showMessageDialog(null, "Erreur : Ajouter au moins une formation");
                        return;
                    }
                    
                    dispose();
                    Modules modules = new Modules(false);
                    modules.setVisible(true);
                }
            });
        }
    }
    
    /**
     * La classe Modules est une classe interne à la classe Sae21 de l'application.
     */
    private class Modules extends JFrame {
        private JTextArea periodeTextArea;
        private JButton boutonSuivantConfigurations; // Bouton Suivant pour afficher la configuration des modules
        private JLabel labelNbCours; // Label pour afficher "Nombre de cours dans ce module"
        private JTextField nbCoursField; // Champ de texte pour afficher le nombre de cours
        private boolean isFirstDateSelected = false;
    
        /**
         * Constructeur de la classe Modules est la quatrième page de la section "Créer" de l'application.
         */
        public Modules(boolean modification) {
            setTitle("Modules");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
    
            JLabel labelNomModule = new JLabel("Nom du module:");
            JTextField textFieldNomModule = new JTextField(20);
            JLabel labelIdModule = new JLabel("ID du module (Ex : R2.04):");
            JTextField textFieldIdModule = new JTextField(20); 
            JLabel labelFormation = new JLabel("Formation:");
            JComboBox<String> formationComboBox = FormationComboBox;
            JLabel labelPeriode = new JLabel("Période du module:");
            JButton boutonPrecedent = new JButton("Précédent"); // Ajout du bouton "Précédent"
            boutonSuivantConfigurations = new JButton("Suivant Configurations"); // Bouton Suivant pour afficher la configuration des modules
            labelNbCours = new JLabel("Nombre de cours dans ce module:"); // Label pour "Nombre de cours dans ce module"
            nbCoursField = new JTextField(5); // Champ de texte pour afficher le nombre de cours
            JButton boutonPlus = new JButton("+");

            JButton retourAccueilButton = new JButton("Retour à l'accueil");
            retourAccueilButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose(); // Fermer la fenêtre actuelle
                    Accueil accueil = new Accueil(); // Créer une nouvelle instance de la fenêtre Accueil
                    accueil.setVisible(true); // Afficher la fenêtre Accueil
                }
            });

            periodeTextArea = new JTextArea(1, 20);
            periodeTextArea.setEditable(false); // Pour rendre le champ de texte non modifiable
    
            // Mise en forme du texte
            Font font = labelNomModule.getFont();
            float size = font.getSize() * 2;
            labelNomModule.setFont(font.deriveFont(size));
            textFieldNomModule.setFont(font.deriveFont(size));
            labelIdModule.setFont(font.deriveFont(size));
            textFieldIdModule.setFont(font.deriveFont(size));
            labelFormation.setFont(font.deriveFont(size));
            formationComboBox.setFont(font.deriveFont(size));
            labelPeriode.setFont(font.deriveFont(size));
            retourAccueilButton.setFont(font.deriveFont(size));
            boutonPrecedent.setFont(font.deriveFont(size)); // Réglage de la police du bouton "Précédent"
            boutonSuivantConfigurations.setFont(font.deriveFont(size)); // Réglage de la police du bouton "Suivant Configurations"
            labelNbCours.setFont(font.deriveFont(size)); // Réglage de la police du label "Nombre de cours dans ce module"
            nbCoursField.setFont(font.deriveFont(size)); // Réglage de la police du champ de texte pour afficher le nombre de cours
            boutonPlus.setFont(font.deriveFont(size));
            periodeTextArea.setFont(font.deriveFont(size));

    
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(10, 10, 10, 10);
            add(labelNomModule, gbc);
            gbc.gridx = 2;
            add(textFieldNomModule, gbc);
    
            gbc.gridy = 1;
            gbc.gridx = 1;
            add(labelIdModule, gbc);
            gbc.gridx = 2;
            add(textFieldIdModule, gbc);
    
            gbc.gridy = 2;
            gbc.gridx = 1;
            add(labelFormation, gbc);
            gbc.gridx = 2;
            add(formationComboBox, gbc);
    
            gbc.gridy = 3;
            gbc.gridx = 1;
            add(labelPeriode, gbc);
            gbc.gridx = 2;
            add(new JScrollPane(periodeTextArea), gbc); // Utilisation de JScrollPane pour afficher le JTextArea
            gbc.gridx = 3;
            add(boutonPlus, gbc);
    
            gbc.gridy = 4; // Ajout du label "Nombre de cours dans ce module"
            gbc.gridx = 1;
            add(labelNbCours, gbc);
    
            gbc.gridx = 2; // Ajout du champ de texte pour afficher le nombre de cours
            add(nbCoursField, gbc);

            gbc.gridy = 7; // Ajout du bouton "Précédent"
            gbc.gridx = 0;
            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.SOUTHWEST;
            gbc.insets = new Insets(10,10, 10, 10);
            if (modification) add(retourAccueilButton, gbc); // Ajout du bouton "Précédent"
            else add(boutonPrecedent, gbc);

            gbc.gridy = 7; // Ajout du bouton "Suivant Configurations"
            gbc.gridx = 5;
            gbc.anchor = GridBagConstraints.SOUTHEAST;
            gbc.insets = new Insets(10,10, 10, 10);
            add(boutonSuivantConfigurations, gbc); // Ajout du bouton "Suivant Configurations"
    
            boutonPlus.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    selectDate("Sélectionnez une date de début du module");
                }
            });
            
            boutonPrecedent.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    Formation formation = new Formation(false);
                    formation.setVisible(true);
                }
            });
    
            boutonSuivantConfigurations.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String ID = textFieldIdModule.getText();
                    
                    if (textFieldNomModule.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Erreur : Le nom du module est vide");
                        return;
                    }
                    
                    if (ID.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Erreur : L'identifiant du module est vide");
                        return;
                    }

                    if (!(ID.matches("R[1-6]\\.(.*)"))) {
                        JOptionPane.showMessageDialog(null, "Erreur : L'identifiant du module n'a pas le bon format \n  Format : R5.02");
                        return;
                    }
                    
                    if (periodeTextArea.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Erreur : La période du module est vide");
                        return;
                    }

                    if (nbCoursField.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Erreur : Le nombre de cours du module est vide");
                        return;
                    }
                    
                    try {
                        nbCours = Integer.parseInt(nbCoursField.getText());
                        if (nbCours<1) {
                            nbCoursField.setText("");
                            JOptionPane.showMessageDialog(null, "Erreur : Le nombre de cours du module doit être égale au supèrieur à 1");
                            return;
                        }
                    } 
                    catch (NumberFormatException nfe) {
                        nbCoursField.setText("");
                        nfe.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Erreur : Le nombre de cours du module doit être un entier");
                        return;
                    }

                    backRepertoire = repertoire;
                    repertoire = repertoire+"/"+ formationComboBox.getSelectedItem();
                    moduleRepertoire = repertoire+"/"+textFieldNomModule.getText()+".txt";

                    try {
                        // Ajouter le module à la fin du fichier modules.txt
                        File courseFile = new File(repertoire+"/modules.txt");
                        FileWriter writer = new FileWriter(courseFile, true);
                        writer.write(textFieldNomModule.getText()+"\n");
                        writer.close();
                    }
                    catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Erreur lors de l'écriture de la formation dans le fichier modules.txt");
                    }
                    
                    File moduleFile = new File(moduleRepertoire);
                    if (!moduleFile.exists()) {
                        try {
                            moduleFile.createNewFile();
                            FileWriter writer = new FileWriter(moduleFile);
                            writer.write("Params"+
                                        "\nId "+textFieldIdModule.getText()+
                                        "\nSubtitle X"+
                                        "\nStartsOn "+semaine.format(dateDebut)+
                                        "\nEndsOn "+semaine.format(finDate)+
                                        "\nAffectCM X"+
                                        "\nAffectTD 2 X X"+
                                        "\nAffectTP 3 X X X"+
                                        "\nSched"
                                        );
                            writer.close();
                        } 
                        catch (IOException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Erreur lors de la création du fichier du module");
                            return;
                        }
                    }
                    else {
                        textFieldNomModule.setText("");
                        JOptionPane.showMessageDialog(null, "Erreur : Le nom du module existe déjà");
                        return;
                    }

                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(professeursRepertoire));
                        ProfesseurComboBox.removeAllItems();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) ProfesseurComboBox.getModel();
                            if (model.getIndexOf(line) == -1) {
                                // Ajoute le nouveau mot au modèle
                                model.addElement(line);
                            }

                        }
                        reader.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Erreur lors de la lecture du fichier des modules");
                    }
                    dispose();
                    ConfigurationsModules configurationsModules = new ConfigurationsModules(modification,false); // Création d'une nouvelle instance de ConfigurationsModules
                    configurationsModules.setVisible(true); // Affichage de la fenêtre de configuration des modules
                }
            });
        }

        /**
         * Ouvre une boîte de dialogue pour sélectionner une date tout en vérifiant les vacances et les dates valides.
         *
         * @param message Le message à afficher dans la boîte de dialogue.
         */
        private void selectDate(String message) {
            JDateChooser dateChooser = new JDateChooser();

            // Définir la date de début minimale
            Calendar minDate = Calendar.getInstance();
            minDate.set(Calendar.YEAR, Integer.parseInt(digitsSemester.equals("S1") ? yearParts[0] : yearParts[1]));
            minDate.set(Calendar.WEEK_OF_YEAR, (digitsSemester.equals("S1") ? (listLeapYear.contains(yearParts[1]) ? 36 : 35) : 4));
            minDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Lundi de la semaine
            dateChooser.setMinSelectableDate(minDate.getTime());

            // Définir la date de fin maximale
            Calendar maxDate = Calendar.getInstance();
            maxDate.set(Calendar.YEAR, Integer.parseInt(yearParts[1]));
            maxDate.set(Calendar.WEEK_OF_YEAR, (digitsSemester.equals("S1") ? (listLeapYear.contains(yearParts[1]) ? 4 : 3) : 25));
            maxDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // Dimanche de la semaine 
            dateChooser.setMaxSelectableDate(maxDate.getTime());

            dateChooser.setDate(minDate.getTime());
            
            if (isFirstDateSelected) {
                dateChooser.setMinSelectableDate(dateDebut);
                dateChooser.setDate(dateDebut);
                int option = JOptionPane.showConfirmDialog(null, dateChooser, message, JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    finDate = dateChooser.getDate();

                    // Vérifier si la date de fin est antérieure à la date de début
                    if (finDate.before(dateDebut)) {
                        JOptionPane.showMessageDialog(null, "La date de fin doit être postérieure à la date de début. Veuillez choisir une autre date de fin.");
                        selectDate("Sélectionnez une date de fin du module");
                        return;
                    }

                    for (Date[] datePair : dateVacances) {
                        Date startDate = datePair[0];
                        Date endDate = datePair[1];
            
                        if (startDate != null && endDate != null && finDate.after(startDate) && finDate.before(endDate)) {
                            JOptionPane.showMessageDialog(null, "Erreur : La date de fin est déjà dans les vacances");
                            selectDate("Sélectionnez une date de fin du module");
                            return;
                        }
                    }

                    StringBuilder datesBuilder = new StringBuilder();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String debutFormatted = dateFormat.format(dateDebut);
                    String finFormatted = dateFormat.format(finDate);
                    datesBuilder.append("Début: ").append(debutFormatted).append(" | Fin: ").append(finFormatted);
                    periodeTextArea.setText(datesBuilder.toString());
                    isFirstDateSelected = false;
                }
            } 
            else {
                int option = JOptionPane.showConfirmDialog(null, dateChooser, message, JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    dateDebut = dateChooser.getDate();

                    for (Date[] datePair : dateVacances) {
                        Date startDate = datePair[0];
                        Date endDate = datePair[1];
                        if (startDate != null && endDate != null && dateDebut.after(startDate) && dateDebut.before(endDate)) {
                            JOptionPane.showMessageDialog(null, "Erreur : La date de début est déjà dans les vacances");
                            selectDate("Sélectionnez une date de début du module");
                            return;
                        }
                    }

                    isFirstDateSelected = true;
                    selectDate("Sélectionnez une date de fin du module");
                }
            }
        }
    }
    
    /**
     * La classe ConfigurationsModules est une classe interne à la classe Sae21 de l'application.
     */
    public class ConfigurationsModules extends JFrame {
        private JTextArea dateSelectionneeArea;
        private JButton retourAccueilButton; // Bouton de retour à l'accueil
        private JButton creerModuleButton;
        private JButton boutonCoursSuivant;
        private JButton boutonPrecedent;
        private Date selectedDate = dateDebut;
        private List<String> Professeurs = new ArrayList<>();
        private boolean Professeurestdispo=false;
        private int nbProfesseuresTD=0;
        private int nbProfesseuresTP=0;
        private JComboBox<String> professeurComboBox = ProfesseurComboBox;
        private JComboBox<String> typeCoursComboBox = new JComboBox<>(new String[] {"CM","EV1","EV2",
                                                                                    "TD","TM","TV",
                                                                                    "TP4","TP2","TQ2",
                                                                                    "SD","SM","SP","SQ"
                                                                                   });
                                             
        /**
         * Constructeur de la classe ConfigurationsModules est la dernière page de la section "Créer" de l'application.
         */
        public ConfigurationsModules(boolean modificationModule, boolean modificationConfigModule) {
            setTitle("Configurations modules");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setExtendedState(JFrame.MAXIMIZED_BOTH);

            JLabel coursLabel = CoursLabel;
            JLabel labelTypeCours = new JLabel("Type de cours(cliquez sur + pour ajouter une type):");
            JButton infoTypeButton = new JButton("INFO");
            JLabel labelQuand = new JLabel("Date du cours(cliquez sur + pour ajouter une date):");
            JButton plusDateButton = new JButton("+");
            dateSelectionneeArea = new JTextArea(1, 20);
            dateSelectionneeArea.setEditable(false);
            JLabel labelNomProfesseur = new JLabel("Nom du professeur(cliquez sur + pour ajouter differentes noms):");
            JTextField nomProfesseurField = new JTextField(20);
            JButton plusProfesseurButton = new JButton("+");

            JButton retourAccueilModifButton = new JButton("Retour à l'accueil");
            retourAccueilModifButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose(); // Fermer la fenêtre actuelle
                    Accueil accueil = new Accueil(); // Créer une nouvelle instance de la fenêtre Accueil
                    accueil.setVisible(true); // Afficher la fenêtre Accueil
                }
            });
    
            Font font = labelTypeCours.getFont();
            float size = font.getSize() * 2;
            labelTypeCours.setFont(font.deriveFont(size));
            coursLabel.setFont(font.deriveFont(size));
            typeCoursComboBox.setFont(font.deriveFont(size));
            infoTypeButton.setFont(font.deriveFont(size));
            labelQuand.setFont(font.deriveFont(size));
            plusDateButton.setFont(font.deriveFont(size));
            dateSelectionneeArea.setFont(font.deriveFont(size));
            labelNomProfesseur.setFont(font.deriveFont(size));
            nomProfesseurField.setFont(font.deriveFont(size));
            professeurComboBox.setFont(font.deriveFont(size));
            plusProfesseurButton.setFont(font.deriveFont(size));
            retourAccueilModifButton.setFont(font.deriveFont(size));
    
            retourAccueilButton = new JButton("Retour à l'accueil");
            retourAccueilButton.setFont(font.deriveFont(size));

            creerModuleButton = new JButton("Créer un autre module");
            creerModuleButton.setFont(font.deriveFont(size));

            boutonCoursSuivant = new JButton("Cours Suivant");
            boutonCoursSuivant.setFont(font.deriveFont(size));
    
            boutonPrecedent = new JButton("Précédent");
            boutonPrecedent.setFont(font.deriveFont(size));
    
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            
            gbc.gridx = 2;
            gbc.gridy = 0;
            gbc.gridwidth = 4;
            gbc.gridheight = 2;
            gbc.insets = new Insets(0, 30, 30, 30);
            add(coursLabel, gbc);
            
            gbc.gridx = 2;
            gbc.gridy = 2;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(10, 10, 10, 10);
            add(labelTypeCours, gbc);
            JPanel panelTypeCours = new JPanel();
            gbc.gridx = 3;
            panelTypeCours.add(typeCoursComboBox);
            panelTypeCours.add(infoTypeButton);
            add(panelTypeCours, gbc);

            gbc.gridy = 3;
            gbc.gridx = 2;
            add(labelQuand, gbc);
            gbc.gridx = 3;
            add(dateSelectionneeArea, gbc);
            gbc.gridx = 4;
            add(plusDateButton, gbc);

            JPanel panelProfesseur = new JPanel();
            gbc.gridy = 4;
            gbc.gridx = 2;
            gbc.gridwidth = 3;
            panelProfesseur.add(labelNomProfesseur);
            panelProfesseur.add(professeurComboBox);
            panelProfesseur.add(plusProfesseurButton);
            add(panelProfesseur, gbc);
    
            gbc.gridy = 6;
            gbc.gridx = 0; // positionner le bouton "Précédent" à gauche
            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.SOUTHWEST; // Aligner le bouton à gauche
            gbc.insets = new Insets(10, 10, 10, 10); // Ajouter des marges
            if (modificationConfigModule) add(retourAccueilModifButton, gbc);
            else add(boutonPrecedent, gbc);
    
            gbc.gridx = 8;
            gbc.anchor = GridBagConstraints.SOUTHEAST;
            gbc.insets = new Insets(10, 10, 10, 10);
            if (Cours==nbCours) {
                add(creerModuleButton, gbc);
                gbc.gridy = 7;
                add(retourAccueilButton, gbc);
            }
            else if (modificationConfigModule) {
                retourAccueilButton.setText("Validé");
                add(retourAccueilButton, gbc);
            }
            else add(boutonCoursSuivant, gbc);
    
            retourAccueilButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    
                    if (dateSelectionneeArea.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Erreur : La date du cours est vide");
                        return;
                    }
                    if (professeurComboBox.getItemCount() == 0) {
                        JOptionPane.showMessageDialog(null, "Erreur : Le professeur du cours est vide");
                        return;
                    }
                    String[] profPart = ((String)professeurComboBox.getSelectedItem()).split(" ");

                    try {
                        // Lire le contenu du fichier
                        BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                        StringBuilder content = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            content.append(line).append("\n");
                        }
                        reader.close();
            
                        // Trouver l'index du mot "Params"
                        int index = content.indexOf("Params");
            
                        // Insérer "TD" juste avant l'index trouvé
                        if (index != -1) {
                            content.insert(index, typeCoursComboBox.getSelectedItem()+"\n");
                        }
            
                        // Écrire le contenu mis à jour dans le fichier
                        FileWriter writer = new FileWriter(moduleRepertoire);
                        writer.write(content.toString());
                        writer.close();
            
                    } 
                    catch (IOException ioe) {
                        JOptionPane.showMessageDialog(null, "Erreur lors de l'ajoute du type du cours dans le fichier du module");
                        return;
                    }

                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                        StringBuilder content = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.contains("Sched")) {
                                line = line + " "+semaine.format(selectedDate);
                            }
                            content.append(line).append("\n");
                        }
                        reader.close();
            
                        FileWriter writer = new FileWriter(moduleRepertoire);
                        writer.write(content.toString());
                        writer.close();
            
                    } 
                    catch (IOException ioe) {
                        JOptionPane.showMessageDialog(null, "Erreur lors de l'ajoute du cours dans la planification du module");
                        return;
                    }

                    if (typeCoursComboBox.getSelectedItem().equals("CM")) {

                        try {
                            if (!Professeurs.contains(professeurComboBox.getSelectedItem())) {
                                Professeurs.add((String)professeurComboBox.getSelectedItem());
                                Professeurestdispo=true;
                            }
                            BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                            StringBuilder content = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                if (line.contains("AffectCM")) {
                                    line = "AffectCM "+profPart[0].charAt(0)+profPart[1].substring(0, 3);
                                }
                                content.append(line).append("\n");
                            }
                            reader.close();
                
                            FileWriter writer = new FileWriter(moduleRepertoire);
                            writer.write(content.toString());
                            writer.close();
                
                        } 
                        catch (IOException ioe) {
                            JOptionPane.showMessageDialog(null, "Erreur lors de l'ajoute du professeur dans l'affection en CM du module");
                            return;
                        }

                    }

                    else if (TD.contains(typeCoursComboBox.getSelectedItem())) {
                        
                        try {
                            if (!Professeurs.contains(professeurComboBox.getSelectedItem())) {
                                Professeurs.add((String)professeurComboBox.getSelectedItem());
                                Professeurestdispo=true;
                                nbProfesseuresTD+=1;

                                if (nbProfesseuresTD==1) {
                                    BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                                    StringBuilder content = new StringBuilder();
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        if (line.contains("AffectTD 2")) {
                                            line = "AffectTD 2 "+ profPart[0].charAt(0)+profPart[1].substring(0, 3) +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3);
                                        }
                                        if (line.contains("AffectTP 3 X X X")) {
                                            line = "AffectTP 3 "+ profPart[0].charAt(0)+profPart[1].substring(0, 3) +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3) +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3);
                                        }
                                        content.append(line).append("\n");
                                    }
                                    reader.close();
                        
                                    FileWriter writer = new FileWriter(moduleRepertoire);
                                    writer.write(content.toString());
                                    writer.close();
                                }
                                else if (nbProfesseuresTD==2) {
                                    BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                                    StringBuilder content = new StringBuilder();
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        if (line.contains("AffectTD 2")) {
                                            String[] linePart = line.split(" ");
                                            line = linePart[0]+" "+linePart[1]+" "+ linePart[2] +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3);
                                        }
                                        content.append(line).append("\n");
                                    }
                                    reader.close();
                        
                                    FileWriter writer = new FileWriter(moduleRepertoire);
                                    writer.write(content.toString());
                                    writer.close();
                                }
                            }                
                        } 
                        catch (IOException ioe) {
                            JOptionPane.showMessageDialog(null, "Erreur lors de l'ajoute du professeur dans l'affection en TD du module");
                            return;
                        }

                    }

                    else if (TP.contains(typeCoursComboBox.getSelectedItem())) {
                        
                        try {
                            if (!Professeurs.contains(professeurComboBox.getSelectedItem())) {
                                Professeurs.add((String)professeurComboBox.getSelectedItem());
                                Professeurestdispo=true;
                                nbProfesseuresTP+=1;

                                if (nbProfesseuresTP==1) {

                                    BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                                    StringBuilder content = new StringBuilder();
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        if (line.contains("AffectTD 2 X X")) {
                                            line = "AffectTD 2 "+ profPart[0].charAt(0)+profPart[1].substring(0, 3) +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3);
                                        }
                                        if (line.contains("AffectTP 3")) {
                                            line = "AffectTP 3 "+ profPart[0].charAt(0)+profPart[1].substring(0, 3) +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3) +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3);
                                        }
                                        content.append(line).append("\n");
                                    }
                                    reader.close();
                        
                                    FileWriter writer = new FileWriter(moduleRepertoire);
                                    writer.write(content.toString());
                                    writer.close();

                                }
                                else if (nbProfesseuresTP==2) {

                                    BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                                    StringBuilder content = new StringBuilder();
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        if (line.contains("AffectTP 3")) {
                                            String[] linePart = line.split(" ");
                                            line = linePart[0]+" "+linePart[1]+" "+ linePart[2] +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3) +" "+ linePart[2];
                                        }
                                        content.append(line).append("\n");
                                    }
                                    reader.close();
                        
                                    FileWriter writer = new FileWriter(moduleRepertoire);
                                    writer.write(content.toString());
                                    writer.close();

                                }
                                else if (nbProfesseuresTP==3) {

                                    BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                                    StringBuilder content = new StringBuilder();
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        if (line.contains("AffectTP 3")) {
                                            String[] linePart = line.split(" ");
                                            line = linePart[0]+" "+linePart[1]+" "+ linePart[2] +" "+ linePart[2] +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3);
                                        }
                                        content.append(line).append("\n");
                                    }
                                    reader.close();
                        
                                    FileWriter writer = new FileWriter(moduleRepertoire);
                                    writer.write(content.toString());
                                    writer.close();

                                }
                            }
                        } 
                        catch (IOException ioe) {
                            JOptionPane.showMessageDialog(null, "Erreur lors de l'ajoute du professeur dans l'affection en TP du module");
                            return;
                        }

                    }

                    if (Professeurestdispo) {

                        if (Professeurs.size()==1) {
                            try {
                                BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                                StringBuilder content = new StringBuilder();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    if (line.contains("Subtitle")) {
                                        line = "Subtitle "+ profPart[0].charAt(0)+profPart[1].charAt(0);
                                    }
                                    content.append(line).append("\n");
                                }
                                reader.close();
                    
                                FileWriter writer = new FileWriter(moduleRepertoire);
                                writer.write(content.toString());
                                writer.close();
                    
                            } 
                            catch (IOException ioe) {
                                JOptionPane.showMessageDialog(null, "Erreur lors de l'ajoute du professeur dans l'affection du module");
                                return;
                            }
                        }
                        else {
                            try {
                                BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                                StringBuilder content = new StringBuilder();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    if (line.contains("Subtitle")) {
                                        line = line+"-"+ profPart[0].charAt(0)+profPart[1].charAt(0);
                                    }
                                    content.append(line).append("\n");
                                }
                                reader.close();
                    
                                FileWriter writer = new FileWriter(moduleRepertoire);
                                writer.write(content.toString());
                                writer.close();
                    
                            } 
                            catch (IOException ioe) {
                                JOptionPane.showMessageDialog(null, "Erreur lors de l'ajoute du professeur dans l'affection du module");
                                return;
                            }
                        }

                        Professeurestdispo=false;

                    }
                    
                    coursLabel.setText("Cours 1");
                    Cours=1;
                    dispose();
                    Accueil accueil = new Accueil();
                    accueil.setVisible(true);
                }
            });

            creerModuleButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    
                    if (dateSelectionneeArea.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Erreur : La date du cours est vide");
                        return;
                    }
                    if (professeurComboBox.getItemCount() == 0) {
                        JOptionPane.showMessageDialog(null, "Erreur : Le professeur du cours est vide");
                        return;
                    }
                    String[] profPart = ((String)professeurComboBox.getSelectedItem()).split(" ");

                    try {
                        // Lire le contenu du fichier
                        BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                        StringBuilder content = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            content.append(line).append("\n");
                        }
                        reader.close();
            
                        // Trouver l'index du mot "Params"
                        int index = content.indexOf("Params");
            
                        // Insérer "TD" juste avant l'index trouvé
                        if (index != -1) {
                            content.insert(index, typeCoursComboBox.getSelectedItem()+"\n");
                        }
            
                        // Écrire le contenu mis à jour dans le fichier
                        FileWriter writer = new FileWriter(moduleRepertoire);
                        writer.write(content.toString());
                        writer.close();
            
                    } 
                    catch (IOException ioe) {
                        JOptionPane.showMessageDialog(null, "Erreur lors de l'ajoute du type du cours dans le fichier du module");
                        return;
                    }

                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                        StringBuilder content = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.contains("Sched")) {
                                line = line + " "+semaine.format(selectedDate);
                            }
                            content.append(line).append("\n");
                        }
                        reader.close();
            
                        FileWriter writer = new FileWriter(moduleRepertoire);
                        writer.write(content.toString());
                        writer.close();
            
                    } 
                    catch (IOException ioe) {
                        JOptionPane.showMessageDialog(null, "Erreur lors de l'ajoute du cours dans la planification du module");
                        return;
                    }

                    if (typeCoursComboBox.getSelectedItem().equals("CM")) {

                        try {
                            if (!Professeurs.contains(professeurComboBox.getSelectedItem())) {
                                Professeurs.add((String)professeurComboBox.getSelectedItem());
                                Professeurestdispo=true;
                            }
                            BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                            StringBuilder content = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                if (line.contains("AffectCM")) {
                                    line = "AffectCM "+profPart[0].charAt(0)+profPart[1].substring(0, 3);
                                }
                                content.append(line).append("\n");
                            }
                            reader.close();
                
                            FileWriter writer = new FileWriter(moduleRepertoire);
                            writer.write(content.toString());
                            writer.close();
                
                        } 
                        catch (IOException ioe) {
                            JOptionPane.showMessageDialog(null, "Erreur lors de l'ajoute du professeur dans l'affection en CM du module");
                            return;
                        }

                    }

                    else if (TD.contains(typeCoursComboBox.getSelectedItem())) {
                        
                        try {
                            if (!Professeurs.contains(professeurComboBox.getSelectedItem())) {
                                Professeurs.add((String)professeurComboBox.getSelectedItem());
                                Professeurestdispo=true;
                                nbProfesseuresTD+=1;

                                if (nbProfesseuresTD==1) {
                                    BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                                    StringBuilder content = new StringBuilder();
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        if (line.contains("AffectTD 2")) {
                                            line = "AffectTD 2 "+ profPart[0].charAt(0)+profPart[1].substring(0, 3) +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3);
                                        }
                                        if (line.contains("AffectTP 3 X X X")) {
                                            line = "AffectTP 3 "+ profPart[0].charAt(0)+profPart[1].substring(0, 3) +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3) +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3);
                                        }
                                        content.append(line).append("\n");
                                    }
                                    reader.close();
                        
                                    FileWriter writer = new FileWriter(moduleRepertoire);
                                    writer.write(content.toString());
                                    writer.close();
                                }
                                else if (nbProfesseuresTD==2) {
                                    BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                                    StringBuilder content = new StringBuilder();
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        if (line.contains("AffectTD 2")) {
                                            String[] linePart = line.split(" ");
                                            line = linePart[0]+" "+linePart[1]+" "+ linePart[2] +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3);
                                        }
                                        content.append(line).append("\n");
                                    }
                                    reader.close();
                        
                                    FileWriter writer = new FileWriter(moduleRepertoire);
                                    writer.write(content.toString());
                                    writer.close();
                                }
                            }                
                        } 
                        catch (IOException ioe) {
                            JOptionPane.showMessageDialog(null, "Erreur lors de l'ajoute du professeur dans l'affection en TD du module");
                            return;
                        }

                    }

                    else if (TP.contains(typeCoursComboBox.getSelectedItem())) {
                        
                        try {
                            if (!Professeurs.contains(professeurComboBox.getSelectedItem())) {
                                Professeurs.add((String)professeurComboBox.getSelectedItem());
                                Professeurestdispo=true;
                                nbProfesseuresTP+=1;

                                if (nbProfesseuresTP==1) {

                                    BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                                    StringBuilder content = new StringBuilder();
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        if (line.contains("AffectTD 2 X X")) {
                                            line = "AffectTD 2 "+ profPart[0].charAt(0)+profPart[1].substring(0, 3) +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3);
                                        }
                                        if (line.contains("AffectTP 3")) {
                                            line = "AffectTP 3 "+ profPart[0].charAt(0)+profPart[1].substring(0, 3) +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3) +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3);
                                        }
                                        content.append(line).append("\n");
                                    }
                                    reader.close();
                        
                                    FileWriter writer = new FileWriter(moduleRepertoire);
                                    writer.write(content.toString());
                                    writer.close();

                                }
                                else if (nbProfesseuresTP==2) {

                                    BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                                    StringBuilder content = new StringBuilder();
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        if (line.contains("AffectTP 3")) {
                                            String[] linePart = line.split(" ");
                                            line = linePart[0]+" "+linePart[1]+" "+ linePart[2] +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3) +" "+ linePart[2];
                                        }
                                        content.append(line).append("\n");
                                    }
                                    reader.close();
                        
                                    FileWriter writer = new FileWriter(moduleRepertoire);
                                    writer.write(content.toString());
                                    writer.close();

                                }
                                else if (nbProfesseuresTP==3) {

                                    BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                                    StringBuilder content = new StringBuilder();
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        if (line.contains("AffectTP 3")) {
                                            String[] linePart = line.split(" ");
                                            line = linePart[0]+" "+linePart[1]+" "+ linePart[2] +" "+ linePart[2] +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3);
                                        }
                                        content.append(line).append("\n");
                                    }
                                    reader.close();
                        
                                    FileWriter writer = new FileWriter(moduleRepertoire);
                                    writer.write(content.toString());
                                    writer.close();

                                }
                            }
                        } 
                        catch (IOException ioe) {
                            JOptionPane.showMessageDialog(null, "Erreur lors de l'ajoute du professeur dans l'affection en TP du module");
                            return;
                        }

                    }

                    if (Professeurestdispo) {

                        if (Professeurs.size()==1) {
                            try {
                                BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                                StringBuilder content = new StringBuilder();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    if (line.contains("Subtitle")) {
                                        line = "Subtitle "+ profPart[0].charAt(0)+profPart[1].charAt(0);
                                    }
                                    content.append(line).append("\n");
                                }
                                reader.close();
                    
                                FileWriter writer = new FileWriter(moduleRepertoire);
                                writer.write(content.toString());
                                writer.close();
                    
                            } 
                            catch (IOException ioe) {
                                JOptionPane.showMessageDialog(null, "Erreur lors de l'ajoute du professeur dans l'affection du module");
                                return;
                            }
                        }
                        else {
                            try {
                                BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                                StringBuilder content = new StringBuilder();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    if (line.contains("Subtitle")) {
                                        line = line+"-"+ profPart[0].charAt(0)+profPart[1].charAt(0);
                                    }
                                    content.append(line).append("\n");
                                }
                                reader.close();
                    
                                FileWriter writer = new FileWriter(moduleRepertoire);
                                writer.write(content.toString());
                                writer.close();
                    
                            } 
                            catch (IOException ioe) {
                                JOptionPane.showMessageDialog(null, "Erreur lors de l'ajoute du professeur dans l'affection du module");
                                return;
                            }
                        }

                        Professeurestdispo=false;

                    }
                    
                    repertoire = backRepertoire;
                    coursLabel.setText("Cours 1");
                    Cours=1;
                    dispose();
                    Modules module = new Modules(modificationModule);
                    module.setVisible(true);
                }
            });

            boutonCoursSuivant.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    
                    if (dateSelectionneeArea.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Erreur : La date du cours est vide");
                        return;
                    }
                    if (professeurComboBox.getItemCount() == 0) {
                        JOptionPane.showMessageDialog(null, "Erreur : Le professeur du cours est vide");
                        return;
                    }
                    String[] profPart = ((String)professeurComboBox.getSelectedItem()).split(" ");

                    try {
                        // Lire le contenu du fichier
                        BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                        StringBuilder content = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            content.append(line).append("\n");
                        }
                        reader.close();
            
                        // Trouver l'index du mot "Params"
                        int index = content.indexOf("Params");
            
                        // Insérer "TD" juste avant l'index trouvé
                        if (index != -1) {
                            content.insert(index, typeCoursComboBox.getSelectedItem()+"\n");
                        }
            
                        // Écrire le contenu mis à jour dans le fichier
                        FileWriter writer = new FileWriter(moduleRepertoire);
                        writer.write(content.toString());
                        writer.close();
            
                    } 
                    catch (IOException ioe) {
                        JOptionPane.showMessageDialog(null, "Erreur lors de l'ajoute du type du cours dans le fichier du module");
                        return;
                    }

                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                        StringBuilder content = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.contains("Sched")) {
                                line = line + " "+semaine.format(selectedDate);
                            }
                            content.append(line).append("\n");
                        }
                        reader.close();
            
                        FileWriter writer = new FileWriter(moduleRepertoire);
                        writer.write(content.toString());
                        writer.close();
            
                    } 
                    catch (IOException ioe) {
                        JOptionPane.showMessageDialog(null, "Erreur lors de l'ajoute du cours dans la planification du module");
                        return;
                    }

                    if (typeCoursComboBox.getSelectedItem().equals("CM")) {

                        try {
                            if (!Professeurs.contains(professeurComboBox.getSelectedItem())) {
                                Professeurs.add((String)professeurComboBox.getSelectedItem());
                                Professeurestdispo=true;
                            }
                            BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                            StringBuilder content = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                if (line.contains("AffectCM")) {
                                    line = "AffectCM "+profPart[0].charAt(0)+profPart[1].substring(0, 3);
                                }
                                content.append(line).append("\n");
                            }
                            reader.close();
                
                            FileWriter writer = new FileWriter(moduleRepertoire);
                            writer.write(content.toString());
                            writer.close();
                
                        } 
                        catch (IOException ioe) {
                            JOptionPane.showMessageDialog(null, "Erreur lors de l'ajoute du professeur dans l'affection en CM du module");
                            return;
                        }

                    }

                    else if (TD.contains(typeCoursComboBox.getSelectedItem())) {
                        
                        try {
                            if (!Professeurs.contains(professeurComboBox.getSelectedItem())) {
                                Professeurs.add((String)professeurComboBox.getSelectedItem());
                                Professeurestdispo=true;
                                nbProfesseuresTD+=1;

                                if (nbProfesseuresTD==1) {
                                    BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                                    StringBuilder content = new StringBuilder();
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        if (line.contains("AffectTD 2")) {
                                            line = "AffectTD 2 "+ profPart[0].charAt(0)+profPart[1].substring(0, 3) +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3);
                                        }
                                        if (line.contains("AffectTP 3 X X X")) {
                                            line = "AffectTP 3 "+ profPart[0].charAt(0)+profPart[1].substring(0, 3) +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3) +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3);
                                        }
                                        content.append(line).append("\n");
                                    }
                                    reader.close();
                        
                                    FileWriter writer = new FileWriter(moduleRepertoire);
                                    writer.write(content.toString());
                                    writer.close();
                                }
                                else if (nbProfesseuresTD==2) {
                                    BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                                    StringBuilder content = new StringBuilder();
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        if (line.contains("AffectTD 2")) {
                                            String[] linePart = line.split(" ");
                                            line = linePart[0]+" "+linePart[1]+" "+ linePart[2] +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3);
                                        }
                                        content.append(line).append("\n");
                                    }
                                    reader.close();
                        
                                    FileWriter writer = new FileWriter(moduleRepertoire);
                                    writer.write(content.toString());
                                    writer.close();
                                }
                            }                
                        } 
                        catch (IOException ioe) {
                            JOptionPane.showMessageDialog(null, "Erreur lors de l'ajoute du professeur dans l'affection en TD du module");
                            return;
                        }

                    }

                    else if (TP.contains(typeCoursComboBox.getSelectedItem())) {
                        
                        try {
                            if (!Professeurs.contains(professeurComboBox.getSelectedItem())) {
                                Professeurs.add((String)professeurComboBox.getSelectedItem());
                                Professeurestdispo=true;
                                nbProfesseuresTP+=1;

                                if (nbProfesseuresTP==1) {

                                    BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                                    StringBuilder content = new StringBuilder();
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        if (line.contains("AffectTD 2 X X")) {
                                            line = "AffectTD 2 "+ profPart[0].charAt(0)+profPart[1].substring(0, 3) +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3);
                                        }
                                        if (line.contains("AffectTP 3")) {
                                            line = "AffectTP 3 "+ profPart[0].charAt(0)+profPart[1].substring(0, 3) +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3) +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3);
                                        }
                                        content.append(line).append("\n");
                                    }
                                    reader.close();
                        
                                    FileWriter writer = new FileWriter(moduleRepertoire);
                                    writer.write(content.toString());
                                    writer.close();

                                }
                                else if (nbProfesseuresTP==2) {

                                    BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                                    StringBuilder content = new StringBuilder();
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        if (line.contains("AffectTP 3")) {
                                            String[] linePart = line.split(" ");
                                            line = linePart[0]+" "+linePart[1]+" "+ linePart[2] +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3) +" "+ linePart[2];
                                        }
                                        content.append(line).append("\n");
                                    }
                                    reader.close();
                        
                                    FileWriter writer = new FileWriter(moduleRepertoire);
                                    writer.write(content.toString());
                                    writer.close();

                                }
                                else if (nbProfesseuresTP==3) {

                                    BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                                    StringBuilder content = new StringBuilder();
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        if (line.contains("AffectTP 3")) {
                                            String[] linePart = line.split(" ");
                                            line = linePart[0]+" "+linePart[1]+" "+ linePart[2] +" "+ linePart[2] +" "+ profPart[0].charAt(0)+profPart[1].substring(0, 3);
                                        }
                                        content.append(line).append("\n");
                                    }
                                    reader.close();
                        
                                    FileWriter writer = new FileWriter(moduleRepertoire);
                                    writer.write(content.toString());
                                    writer.close();

                                }
                            }
                        } 
                        catch (IOException ioe) {
                            JOptionPane.showMessageDialog(null, "Erreur lors de l'ajoute du professeur dans l'affection en TP du module");
                            return;
                        }

                    }

                    if (Professeurestdispo) {

                        if (Professeurs.size()==1) {
                            try {
                                BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                                StringBuilder content = new StringBuilder();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    if (line.contains("Subtitle")) {
                                        line = "Subtitle "+ profPart[0].charAt(0)+profPart[1].charAt(0);
                                    }
                                    content.append(line).append("\n");
                                }
                                reader.close();
                    
                                FileWriter writer = new FileWriter(moduleRepertoire);
                                writer.write(content.toString());
                                writer.close();
                    
                            } 
                            catch (IOException ioe) {
                                JOptionPane.showMessageDialog(null, "Erreur lors de l'ajoute du professeur dans l'affection du module");
                                return;
                            }
                        }
                        else {
                            try {
                                BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire));
                                StringBuilder content = new StringBuilder();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    if (line.contains("Subtitle")) {
                                        line = line+"-"+ profPart[0].charAt(0)+profPart[1].charAt(0);
                                    }
                                    content.append(line).append("\n");
                                }
                                reader.close();
                    
                                FileWriter writer = new FileWriter(moduleRepertoire);
                                writer.write(content.toString());
                                writer.close();
                    
                            } 
                            catch (IOException ioe) {
                                JOptionPane.showMessageDialog(null, "Erreur lors de l'ajoute du professeur dans l'affection du module");
                                return;
                            }
                        }

                        Professeurestdispo=false;

                    }

                    Cours++;
                    coursLabel.setText("Cours "+Cours);
                    dateSelectionneeArea.setText("");

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            // Actualiser l'affichage après avoir ajouté ou supprimé des composants
                            if (Cours == nbCours) {
                                remove(boutonCoursSuivant);
                                add(creerModuleButton, gbc);
                                gbc.gridy = 7;
                                add(retourAccueilButton, gbc);
                            }
                            
                            // Appeler revalidate pour actualiser l'affichage
                            revalidate();
                            repaint();
                        }
                    });
                }
            });
    
            boutonPrecedent.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (Cours==1){
                        dispose();
                        Modules modules = new Modules(modificationModule);
                        modules.setVisible(true);
                    }
                    else {
                        Cours--;
                        coursLabel.setText("Cours "+Cours);
                        dateSelectionneeArea.setText("");
                        
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                // Actualiser l'affichage après avoir ajouté ou supprimé des composants
                                if (Cours+1 == nbCours) {
                                    remove(retourAccueilButton);
                                    remove(creerModuleButton);
                                    gbc.gridy = 6;
                                    add(boutonCoursSuivant, gbc);
                                }
                                
                                // Appeler revalidate pour actualiser l'affichage
                                revalidate();
                                repaint();
                            }
                        });
                        
                    }
                }
            });
    
            plusDateButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dateSelectionneeArea.setText("");
                    selectDate("Sélectionnez une date où le cours du module se déroule");
                }
            });
    
            infoTypeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(null, "Les types d'activité reconnus sont :\n\n"+
                                                                        "           CM : Cours magistral de deux heures,\n"+
                                                                        "           EV2 : Evaluation de deux heures,\n"+
                                                                        "           EV1 : Evaluation d'une heure,\n"+
                                                                        "           TD : TD de deux heures en salle de cours,\n"+
                                                                        "           TM : TD de deux heures en salle machine,\n"+
                                                                        "           TV : TD de deux heures en parallèle (même plage horaire pour tous les groupes),\n"+
                                                                        "           TP4 : TP de quatre heures en salle machine,\n"+
                                                                        "           TP2 : TP de deux heures en salle machine,\n"+
                                                                        "           TQ2 : TP de deux heures en salle de cours,\n"+
                                                                        "           SD : TD SAé de deux heures en salle de cours,\n"+
                                                                        "           SM : TD SAé de deux heures en salle machine,\n"+
                                                                        "           SP : TP SAé de deux heures en salle machine,\n"+
                                                                        "           SQ : TP SAé de deux heures en salle de cours.\n\n\n");
                }
            });
            
            plusProfesseurButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addProfesseur();
                }
            });

        }

        /**
         * Sélectionne une date en s'assurant qu'elle ne se situe pas pendant les vacances.
         *
         * @param message Message à afficher dans la boîte de dialogue.
         */
        private void selectDate(String message) {
            JDateChooser dateChooser = new JDateChooser();

            dateChooser.setMinSelectableDate(selectedDate);

            dateChooser.setMaxSelectableDate(finDate);

            dateChooser.setDate(selectedDate);

            int option = JOptionPane.showConfirmDialog(null, dateChooser, message, JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                selectedDate = dateChooser.getDate();

                for (Date[] datePair : dateVacances) {
                    Date startDate = datePair[0];
                    Date endDate = datePair[1];
                    if (startDate != null && endDate != null && selectedDate.after(startDate) && selectedDate.before(endDate)) {
                        JOptionPane.showMessageDialog(null, "Erreur : La date choisi est dans les vacances");
                        selectDate("Sélectionnez une date où le cours du module se déroule");
                        return;
                    }
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                dateSelectionneeArea.append("" + dateFormat.format(selectedDate));
            }
        }
        
        /**
         * Ajoute un professeur à la liste des professeurs et l'enregistre dans un fichier.
         */
        private void addProfesseur() {
            String nomProfesseur = JOptionPane.showInputDialog(null, "Entrez le nom du professeur (Format : Prénom Nom) :");
            if (nomProfesseur != null && !nomProfesseur.isEmpty()) {
                professeurComboBox.addItem(nomProfesseur);

                try {
                    // Ajouter le professeur à la fin du fichier professeurs.txt
                    File professeursFile = new File(professeursRepertoire);
                    FileWriter writer = new FileWriter(professeursFile, true);
                    writer.write(nomProfesseur + "\n");
                    writer.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erreur lors de l'écriture du professeur dans le fichier professeurs.txt");
                }
            }
        }
    }
    
    /**
     * La classe Accueil est une classe interne à la classe Sae21 de l'application.
     */
    private class Accueil extends JFrame {

        /**
         * Constructeur de la classe Accueil est la page d'accueil de l'application.
         */
        public Accueil() {
            setTitle("Accueil");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
    
            JButton creerButton = new JButton("Créer");
            JButton modifierButton = new JButton("Modifier");
            JButton selectionnerButton = new JButton("Sélectionner");
            JButton shukanButton = new JButton("Lancer Shukan");
    
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(10, 10, 10, 10);
            add(creerButton, gbc);
    
            gbc.gridy = 1;
            add(modifierButton, gbc);
    
            gbc.gridy = 2;
            add(selectionnerButton, gbc);

            gbc.gridy = 3;
            add(shukanButton, gbc);

            creerButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose(); // Fermer la fenêtre d'accueil actuelle
                    Sae21 sae21 = new Sae21(); // Créer une nouvelle instance de Sae21
                    sae21.setVisible(true); // Afficher la nouvelle fenêtre principale
                }
            });
    
            modifierButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    modifierAnneeSemestreComboBox.removeAllItems();

                    dossiersAnneeEtSemestre = new File("data").listFiles(File::isDirectory);
                    if (dossiersAnneeEtSemestre != null) {
                        for (File dossier : dossiersAnneeEtSemestre) {
                            if (dossier.getName().contains("2")) {
                                String[] anneeEtSemestre = dossier.getName().split("_");
                                
                                int annee = Integer.parseInt(anneeEtSemestre[0].substring(2));

                                String anneeString = "Année : 20"+(annee-1)+"-20"+annee +" | "+ anneeEtSemestre[1];
                                
                                DefaultComboBoxModel<String> modelAnnee = (DefaultComboBoxModel<String>) modifierAnneeSemestreComboBox.getModel();
                                if (modelAnnee.getIndexOf(anneeString) == -1) {
                                    modelAnnee.addElement(anneeString);
                                }
                            }
                        }
                    }

                    Modifier modifierFrame = new Modifier();
                    modifierFrame.setVisible(true);
                }
            });
            
    
            selectionnerButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    selectionAnneeComboBox.removeAllItems();

                    dossiersAnneeEtSemestre = new File("data").listFiles(File::isDirectory);
                    yearParts = dossiersAnneeEtSemestre[0].getName().split("_");
                    if (dossiersAnneeEtSemestre != null) {
                        for (File dossier : dossiersAnneeEtSemestre) {
                            if (dossier.getName().contains(yearParts[0])) {
                                String[] anneeEtSemestre = dossier.getName().split("_");
                                
                                int semestre = Integer.parseInt(anneeEtSemestre[1].substring(1));

                                String semestreString = "Semestre "+semestre;
                                
                                DefaultComboBoxModel<String> modelSemestre = (DefaultComboBoxModel<String>) selectionSemestreComboBox.getModel();
                                if (modelSemestre.getIndexOf(semestreString) == -1) {
                                    modelSemestre.addElement(semestreString);
                                }
                            }

                            if (dossier.getName().contains("2")) {
                                String[] anneeEtSemestre = dossier.getName().split("_");
                                
                                int annee = Integer.parseInt(anneeEtSemestre[0].substring(2));

                                String anneeString = "20"+(annee-1)+"-20"+annee;
                                
                                DefaultComboBoxModel<String> modelAnnee = (DefaultComboBoxModel<String>) selectionAnneeComboBox.getModel();
                                if (modelAnnee.getIndexOf(anneeString) == -1) {
                                    modelAnnee.addElement(anneeString);
                                }
                            }
                        }
                    }

                    File[] dossiersVersion = new File("data/" + dossiersAnneeEtSemestre[0].getName()).listFiles(File::isDirectory);

                    if (dossiersVersion != null) {
                        for (File dossier : dossiersVersion) {
                            if (dossier.getName().contains("V0")) {
                                String[] Versions = dossier.getName().split("_");
                                
                                String versionString = "Version "+ Integer.parseInt(Versions[0].substring(1)) + " du "+Versions[1].substring(4)+"/"+Versions[1].substring(2,4)+"/20"+Versions[1].substring(0,2);
                                
                                DefaultComboBoxModel<String> modelVersion = (DefaultComboBoxModel<String>) selectionVersionComboBox.getModel();
                                if (modelVersion.getIndexOf(versionString) == -1) {
                                    modelVersion.addElement(versionString);
                                }
                            }
                        }
                    }

                    Selectionner selectionnerFrame = new Selectionner();
                    selectionnerFrame.setVisible(true);
                }
            });

            shukanButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Shukan shukan = new Shukan();
        
                    // Rendez la fenêtre Shukan visible
                    shukan.setVisible(true);
                    
                    // Fermez la fenêtre actuelle
                    dispose();
                }
            });
        }
    }

    /**
     * La classe Modifier est une classe interne à la classe Sae21 de l'application.
     */
    private class Modifier extends JFrame {
        private List<Date> datePrecedente = new ArrayList<>();
        private List<String> cbPrecedente = new ArrayList<>();
        private int Cours = 1;
        
        /**
         * Constructeur de la classe Modifier est la page de modificationn de la section "Modifier" de l'application.
         */
        public Modifier() {
            setTitle("Modifier");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setExtendedState(JFrame.MAXIMIZED_BOTH); // Afficher en plein écran
    
            JLabel labelQuoi = new JLabel("Quoi ?");
            JComboBox<String> quoiComboBox = new JComboBox<>(new String[]{"Annee/Semestre","Formation", "Module"});

            ArrayList<String> anneeList = new ArrayList<>();
            ArrayList<String> moduleList = new ArrayList<>();
            ArrayList<String> formationList = new ArrayList<>();
    
            JLabel labelLeQuel = new JLabel("Lequel ?");
            JComboBox<String> lequelComboBox = modifierAnneeSemestreComboBox;
    
            JLabel labelTypeModification = new JLabel("Type de modification");

            String[] cb_modif_annee = new String[]{"Ajouter des vacances","Ajouter une formation","Supprimer une formation"};
            String[] cb_modif_formation = new String[]{"Changer de nom","Ajouter un module","Supprimer un module"};
            String[] cb_modif_module = new String[]{"Changer de nom", "Changer l'id","Changer de professeur","Changer la planification","Ajouter un cours","Supprimer un cours"};

            JComboBox<String> typeModificationComboBox = new JComboBox<>(cb_modif_annee);
    
            JButton retourAccueilButton = new JButton("Retour à l'accueil");
            retourAccueilButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose(); // Fermer la fenêtre actuelle
                    Accueil accueil = new Accueil(); // Créer une nouvelle instance de la fenêtre Accueil
                    accueil.setVisible(true); // Afficher la fenêtre Accueil
                }
            });

            JButton modifierButton = new JButton("Modifier");
            
    
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(10, 10, 10, 10);
            add(labelQuoi, gbc);
    
            gbc.gridx = 2;
            quoiComboBox.setPreferredSize(new Dimension(300,25));
            add(quoiComboBox, gbc);
    
            gbc.gridy = 1;
            gbc.gridx = 1;
            add(labelLeQuel, gbc);
    
            gbc.gridx = 2;
            lequelComboBox.setPreferredSize(new Dimension(300,25));
            add(lequelComboBox, gbc);
    
            gbc.gridy = 2;
            gbc.gridx = 1;
            add(labelTypeModification, gbc);
    
            gbc.gridx = 2;
            typeModificationComboBox.setPreferredSize(new Dimension(300,25));
            add(typeModificationComboBox, gbc);
    
            gbc.gridy = 3;
            gbc.gridx = 0;
            gbc.gridwidth = 2;
            add(retourAccueilButton, gbc);

            gbc.gridx = 3;
            gbc.gridwidth = 1;
            add(modifierButton, gbc);

            modifierButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String quoi = (String) quoiComboBox.getSelectedItem();
                    String lequel = (String) lequelComboBox.getSelectedItem();
                    String modif = (String) typeModificationComboBox.getSelectedItem();

                    dispose(); // Fermer la fenêtre actuelle

                    if ((quoi.equals("Annee/Semestre")) && (modif.equals("Ajouter des vacances"))) {
                        
                        String[] anneeSemestreParts = lequel.split(" ");

                        yearParts = anneeSemestreParts[2].split("-");

                        digitsYear = yearParts[0].substring(2)+""+yearParts[1].substring(2);
                        digitsSemester = anneeSemestreParts[4];

                        repertoire = "data/" + digitsYear+"_"+digitsSemester;
                        File folder = new File(repertoire);

                        File[] files = folder.listFiles();
                        Arrays.sort(files);

                        File  lastVersionFolder = files[files.length - 2];
                        String  lastVersion = lastVersionFolder.getName().substring(1, 4);

                        int newVersion = Integer.parseInt(lastVersion) + 1;
                        
                        repertoire = repertoire + "/V" + String.format("%03d", newVersion) + "_" + todayDate;

                        File newVersionFolder = new File(repertoire);
                        newVersionFolder.mkdirs();
                        
                        try {
                            copyFolder(lastVersionFolder, newVersionFolder);
                        } 
                        catch (IOException e1) {
                            e1.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Erreur lors de la copie des fichiers de la version précédent");
                        }

                        Vacances vacances = new Vacances(true);
                        vacances.setVisible(true);
                    }

                    if ((quoi.equals("Annee/Semestre")) && (modif.equals("Ajouter une formation"))) {
                        
                        String[] anneeSemestreParts = lequel.split(" ");

                        yearParts = anneeSemestreParts[2].split("-");

                        digitsYear = yearParts[0].substring(2)+""+yearParts[1].substring(2);
                        digitsSemester = anneeSemestreParts[4];

                        repertoire = "data/" + digitsYear+"_"+digitsSemester;
                        File folder = new File(repertoire);

                        File[] files = folder.listFiles();
                        Arrays.sort(files);

                        File  lastVersionFolder = files[files.length - 2];
                        String  lastVersion = lastVersionFolder.getName().substring(1, 4);

                        int newVersion = Integer.parseInt(lastVersion) + 1;
                        
                        repertoire = repertoire + "/V" + String.format("%03d", newVersion) + "_" + todayDate;

                        File newVersionFolder = new File(repertoire);
                        newVersionFolder.mkdirs();
                        
                        try {
                            copyFolder(lastVersionFolder, newVersionFolder);
                        } 
                        catch (IOException e1) {
                            e1.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Erreur lors de la copie des fichiers de la version précédent");
                        }

                        try {
                            BufferedReader reader = new BufferedReader(new FileReader(repertoire + "/cursus.txt"));
                            while ((reader.readLine()) != null) {
                                nombreFormation++;
                            }
                            NombreFormationLabel.setText("Nombre de formation : " + nombreFormation);
                            reader.close();
                        } 
                        catch (IOException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Erreur lors du compte du nombre de formation");
                        }
    
                        Formation formation = new Formation(true);
                        formation.setVisible(true);
                    }

                    if ((quoi.equals("Annee/Semestre")) && (modif.equals("Supprimer une formation"))) {

                        String[] anneeSemestreParts = lequel.split(" ");

                        yearParts = anneeSemestreParts[2].split("-");

                        digitsYear = yearParts[0].substring(2)+""+yearParts[1].substring(2);
                        digitsSemester = anneeSemestreParts[4];

                        repertoire = "data/" + digitsYear+"_"+digitsSemester;

                        formationList.clear();

                        if (formationList.isEmpty()) {

                            dossiersVersion = new File(repertoire).listFiles(File::isDirectory);
                            Arrays.sort(dossiersVersion);

                            File  lastVersionFolder = dossiersVersion[dossiersVersion.length - 2];

                            try {
                                BufferedReader reader = new BufferedReader(new FileReader(repertoire+"/"+lastVersionFolder.getName() + "/cursus.txt"));
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    String[] anneeEtSemestre = (digitsYear+"_"+digitsSemester).split("_");
                                    int annee = Integer.parseInt(anneeEtSemestre[0].substring(2));

                                    String formationString = "Formation : "+line+" | 20" + (annee - 1) + "-20" + annee +" "+ anneeEtSemestre[1];
                                    
                                    if (!(formationList.contains(formationString))) formationList.add(formationString);
                                }
                                reader.close();
                            } 
                            catch (IOException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(null, "Erreur lors de la lecture des formations");
                            }
                        }

                        JFrame frame = new JFrame("Supprimer une formation");
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        @SuppressWarnings("rawtypes")
                        JComboBox cb_formations = new JComboBox<>(formationList.toArray(new String[0]));
                        JButton supprimer = new JButton("Supprimer");
                        supprimer.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                String formations = (String) cb_formations.getSelectedItem();

                                String[] anneeSemestreParts = formations.split(" ");

                                yearParts = anneeSemestreParts[4].split("-");

                                digitsYear = yearParts[0].substring(2)+""+yearParts[1].substring(2);
                                digitsSemester = anneeSemestreParts[5];

                                repertoire = "data/" + digitsYear+"_"+digitsSemester;
                                File folder = new File(repertoire);

                                File[] files = folder.listFiles();
                                Arrays.sort(files);

                                File  lastVersionFolder = files[files.length - 2];
                                String  lastVersion = lastVersionFolder.getName().substring(1, 4);

                                int newVersion = Integer.parseInt(lastVersion) + 1;
                                
                                repertoire = repertoire + "/V" + String.format("%03d", newVersion) + "_" + todayDate;

                                File newVersionFolder = new File(repertoire);
                                newVersionFolder.mkdirs();
                                
                                try {
                                    copyFolder(lastVersionFolder, newVersionFolder);
                                } 
                                catch (IOException e1) {
                                    e1.printStackTrace();
                                    JOptionPane.showMessageDialog(null, "Erreur lors de la copie des fichiers de la version précédent");
                                }

                                File dossierFormation = new File(repertoire+"/"+anneeSemestreParts[2]);
                                try {
                                    supprimerDossier(dossierFormation);

                                    StringBuilder lignesModifiees = new StringBuilder();

                                    // Lecture du fichier
                                    BufferedReader lecteur = new BufferedReader(new FileReader(repertoire+"/cursus.txt"));
                                    String ligne;

                                    // Parcours de chaque ligne
                                    while ((ligne = lecteur.readLine()) != null) {
                                        // Vérification si la ligne contient le texte à supprimer
                                        if (!ligne.contains(anneeSemestreParts[2])) {
                                            // Ajout de la ligne à la liste si elle ne contient pas le texte
                                            lignesModifiees.append(ligne).append("\n");
                                        }
                                    }
                                    lecteur.close();

                                    // Réécriture du fichier avec les lignes modifiées
                                    BufferedWriter ecrivain = new BufferedWriter(new FileWriter(repertoire+"/cursus.txt"));
                                    ecrivain.write(lignesModifiees.toString());
                                    ecrivain.close();
        
                                    frame.dispose();  // Fermer la fenêtre
                                } 
                                catch (IOException ex) {
                                    ex.printStackTrace();
                                    JOptionPane.showMessageDialog(frame, "Échec de la suppression du répertoire.", "Erreur", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        });
        
                        JPanel panel = new JPanel(new BorderLayout());
                        
                        JPanel topPanel = new JPanel();
                        topPanel.add(new JLabel("Formation à supprimer : "));
                        topPanel.add(cb_formations);
                        
                        panel.add(topPanel, BorderLayout.CENTER);
                        panel.add(supprimer, BorderLayout.SOUTH);
                        
                        frame.add(panel);
                        frame.setSize(400, 150);  // Définir la taille de la fenêtre
                        frame.setLocationRelativeTo(null);  // Centrer la fenêtre sur l'écran
                        frame.setVisible(true);
                    }
                    
                    if ((quoi.equals("Formation")) && (modif.equals("Changer de nom"))) {

                        String[] FormationParts = lequel.split(" ");

                        JFrame frame = new JFrame("Changer le nom de la formation "+FormationParts[2]);
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        JTextField TF_formations = new JTextField();
                        TF_formations.setPreferredSize(new Dimension(200, 25));
                        JButton changer = new JButton("Changer");
                        changer.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {

                                if (TF_formations.getText().isEmpty()) {
                                    JOptionPane.showMessageDialog(null, "Erreur : Le nom de la formation est vide");
                                    return;
                                }

                                yearParts = FormationParts[4].split("-");

                                digitsYear = yearParts[0].substring(2)+""+yearParts[1].substring(2);
                                digitsSemester = FormationParts[5];

                                repertoire = "data/" + digitsYear+"_"+digitsSemester;
                                File folder = new File(repertoire);

                                File[] files = folder.listFiles();
                                Arrays.sort(files);

                                File  lastVersionFolder = files[files.length - 2];
                                String  lastVersion = lastVersionFolder.getName().substring(1, 4);

                                int newVersion = Integer.parseInt(lastVersion) + 1;
                                
                                repertoire = repertoire + "/V" + String.format("%03d", newVersion) + "_" + todayDate;

                                File newVersionFolder = new File(repertoire);
                                newVersionFolder.mkdirs();
                                
                                try {
                                    copyFolder(lastVersionFolder, newVersionFolder);
                                } 
                                catch (IOException e1) {
                                    e1.printStackTrace();
                                    JOptionPane.showMessageDialog(null, "Erreur lors de la copie des fichiers de la version précédent");
                                }
                                File dossierFormation = new File(repertoire+"/"+FormationParts[2]);
                                File newDossierFormation = new File(repertoire+"/"+TF_formations.getText());

                                dossierFormation.renameTo(newDossierFormation);

                                File fichierFormation = new File(repertoire+"/cursus.txt");
                                StringBuilder contenuModifie = new StringBuilder();
        
                                try (BufferedReader reader = new BufferedReader(new FileReader(fichierFormation))) {
                                    String ligne;
                                    while ((ligne = reader.readLine()) != null) {
                                        contenuModifie.append(ligne.replaceAll(FormationParts[2], TF_formations.getText())).append("\n");
                                    }
                                } 
                                catch (IOException e2) {
                                    JOptionPane.showMessageDialog(null,"Erreur lors de la lecture du fichier : " + e2.getMessage());
                                    return;
                                }
                                
                                try (FileWriter writer = new FileWriter(fichierFormation)) {
                                    writer.write(contenuModifie.toString());
                                    frame.dispose();
                                } 
                                catch (IOException e3) {
                                    JOptionPane.showMessageDialog(null,"Erreur lors de l'écriture dans le fichier : " + e3.getMessage());
                                    return;
                                }
                            }
                        });
        
                        JPanel panel = new JPanel(new BorderLayout());
                        
                        JPanel topPanel = new JPanel();
                        topPanel.add(new JLabel("Nouveau nom : "));
                        topPanel.add(TF_formations);
                        
                        panel.add(topPanel, BorderLayout.CENTER);
                        panel.add(changer, BorderLayout.SOUTH);
                        
                        frame.add(panel);
                        frame.setSize(400, 150);  // Définir la taille de la fenêtre
                        frame.setLocationRelativeTo(null);  // Centrer la fenêtre sur l'écran
                        frame.setVisible(true);

                    }

                    if ((quoi.equals("Formation")) && (modif.equals("Ajouter un module"))) {
                        String[] FormationParts = lequel.split(" ");
                        
                        yearParts = FormationParts[4].split("-");

                        digitsYear = yearParts[0].substring(2)+""+yearParts[1].substring(2);
                        digitsSemester = FormationParts[5];

                        repertoire = "data/" + digitsYear+"_"+digitsSemester;
                        File folder = new File(repertoire);

                        File[] files = folder.listFiles();
                        Arrays.sort(files);

                        File  lastVersionFolder = files[files.length - 2];
                        String  lastVersion = lastVersionFolder.getName().substring(1, 4);

                        int newVersion = Integer.parseInt(lastVersion) + 1;
                        
                        repertoire = repertoire + "/V" + String.format("%03d", newVersion) + "_" + todayDate;

                        File newVersionFolder = new File(repertoire);
                        newVersionFolder.mkdirs();
                        
                        try {
                            copyFolder(lastVersionFolder, newVersionFolder);
                        } 
                        catch (IOException e1) {
                            e1.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Erreur lors de la copie des fichiers de la version précédent");
                        }
                        FormationComboBox.removeAllItems();
                        FormationComboBox.addItem(FormationParts[2]);
                        
                        dispose();
                        Modules modules = new Modules(true);
                        modules.setVisible(true);

                    }

                    if ((quoi.equals("Formation")) && (modif.equals("Supprimer un module"))) {
                        String[] FormationParts = lequel.split(" ");
                        
                        yearParts = FormationParts[4].split("-");

                        digitsYear = yearParts[0].substring(2)+""+yearParts[1].substring(2);
                        digitsSemester = FormationParts[5];

                        repertoire = "data/" + digitsYear+"_"+digitsSemester;

                        moduleList.clear();

                        if (moduleList.isEmpty()) {

                            dossiersVersion = new File(repertoire).listFiles(File::isDirectory);
                            Arrays.sort(dossiersVersion);

                            File  lastVersionFolder = dossiersVersion[dossiersVersion.length - 2];

                            try {
                                BufferedReader reader = new BufferedReader(new FileReader(repertoire+"/"+lastVersionFolder.getName() +"/"+FormationParts[2] + "/modules.txt"));
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    String[] anneeEtSemestre = (digitsYear+"_"+digitsSemester).split("_");
                                    int annee = Integer.parseInt(anneeEtSemestre[0].substring(2));

                                    String moduleString = "Module : "+ line + " | "+FormationParts[2]+" / 20"+(annee - 1) + "-20" + annee +" "+ anneeEtSemestre[1];
                                    
                                    if (!(moduleList.contains(moduleString))) moduleList.add(moduleString);
                                }
                                reader.close();
                            } 
                            catch (IOException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(null, "Erreur lors de la lecture du fichier : " + ex.getMessage());
                            }
                        }

                        JFrame frame = new JFrame("Supprimer une module");
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        @SuppressWarnings("rawtypes")
                        JComboBox cb_modules = new JComboBox<>(moduleList.toArray(new String[0]));
                        JButton supprimer = new JButton("Supprimer");
                        supprimer.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                String modules = (String) cb_modules.getSelectedItem();

                                String[] ModuleParts = modules.split(" ");

                                yearParts = ModuleParts[6].split("-");

                                digitsYear = yearParts[0].substring(2)+""+yearParts[1].substring(2);
                                digitsSemester = ModuleParts[7];

                                repertoire = "data/" + digitsYear+"_"+digitsSemester;
                                File folder = new File(repertoire);

                                File[] files = folder.listFiles();
                                Arrays.sort(files);

                                File  lastVersionFolder = files[files.length - 2];
                                String  lastVersion = lastVersionFolder.getName().substring(1, 4);

                                int newVersion = Integer.parseInt(lastVersion) + 1;
                                
                                repertoire = repertoire + "/V" + String.format("%03d", newVersion) + "_" + todayDate;

                                File newVersionFolder = new File(repertoire);
                                newVersionFolder.mkdirs();
                                
                                try {
                                    copyFolder(lastVersionFolder, newVersionFolder);
                                } 
                                catch (IOException e1) {
                                    e1.printStackTrace();
                                    JOptionPane.showMessageDialog(null, "Erreur lors de la copie des fichiers de la version précédent");
                                }

                                File fichierModule = new File(repertoire+"/"+ModuleParts[4]+"/"+ModuleParts[2]+".txt");
                                try {
                                    supprimerDossier(fichierModule);

                                    StringBuilder lignesModifiees = new StringBuilder();

                                    // Lecture du fichier
                                    BufferedReader lecteur = new BufferedReader(new FileReader(repertoire+"/"+ModuleParts[4]+"/modules.txt"));
                                    String ligne;

                                    // Parcours de chaque ligne
                                    while ((ligne = lecteur.readLine()) != null) {
                                        // Vérification si la ligne contient le texte à supprimer
                                        if (!ligne.contains(ModuleParts[2])) {
                                            // Ajout de la ligne à la liste si elle ne contient pas le texte
                                            lignesModifiees.append(ligne).append("\n");
                                        }
                                    }
                                    lecteur.close();

                                    // Réécriture du fichier avec les lignes modifiées
                                    BufferedWriter ecrivain = new BufferedWriter(new FileWriter(repertoire+"/"+ModuleParts[4]+"/modules.txt"));
                                    ecrivain.write(lignesModifiees.toString());
                                    ecrivain.close();
        
                                    frame.dispose();  // Fermer la fenêtre
                                } 
                                catch (IOException ex) {
                                    ex.printStackTrace();
                                    JOptionPane.showMessageDialog(frame, "Échec de la suppression du répertoire.", "Erreur", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        });
        
                        JPanel panel = new JPanel(new BorderLayout());
                        
                        JPanel topPanel = new JPanel();
                        topPanel.add(new JLabel("Module à supprimer : "));
                        topPanel.add(cb_modules);
                        
                        panel.add(topPanel, BorderLayout.CENTER);
                        panel.add(supprimer, BorderLayout.SOUTH);
                        
                        frame.add(panel);
                        frame.setSize(400, 150);  // Définir la taille de la fenêtre
                        frame.setLocationRelativeTo(null);  // Centrer la fenêtre sur l'écran
                        frame.setVisible(true);
                    }

                    if ((quoi.equals("Module")) && (modif.equals("Changer de nom"))) {
                        String[] ModuleParts = lequel.split(" ");

                        JFrame frame = new JFrame("Changer le nom du module "+ModuleParts[2]);
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        JTextField TF_module = new JTextField();
                        TF_module.setPreferredSize(new Dimension(200, 25));
                        JButton changer = new JButton("Changer");
                        changer.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {

                                if (TF_module.getText().isEmpty()) {
                                    JOptionPane.showMessageDialog(null, "Erreur : Le nom du module est vide");
                                    return;
                                }

                                yearParts = ModuleParts[6].split("-");

                                digitsYear = yearParts[0].substring(2)+""+yearParts[1].substring(2);
                                digitsSemester = ModuleParts[7];

                                repertoire = "data/" + digitsYear+"_"+digitsSemester;
                                File folder = new File(repertoire);

                                File[] files = folder.listFiles();
                                Arrays.sort(files);

                                File  lastVersionFolder = files[files.length - 2];
                                String  lastVersion = lastVersionFolder.getName().substring(1, 4);

                                int newVersion = Integer.parseInt(lastVersion) + 1;
                                
                                repertoire = repertoire + "/V" + String.format("%03d", newVersion) + "_" + todayDate;

                                File newVersionFolder = new File(repertoire);
                                newVersionFolder.mkdirs();
                                
                                try {
                                    copyFolder(lastVersionFolder, newVersionFolder);
                                } 
                                catch (IOException e1) {
                                    e1.printStackTrace();
                                    JOptionPane.showMessageDialog(null, "Erreur lors de la copie des fichiers de la version précédent");
                                }
                                File dossierModule = new File(repertoire+"/"+ModuleParts[4]+"/"+ModuleParts[2]+".txt");
                                File newDossierModule = new File(repertoire+"/"+ModuleParts[4]+"/"+TF_module.getText()+".txt");

                                dossierModule.renameTo(newDossierModule);

                                File fichierModule = new File(repertoire+"/"+ModuleParts[4]+"/modules.txt");
                                StringBuilder contenuModifie = new StringBuilder();
        
                                try (BufferedReader reader = new BufferedReader(new FileReader(fichierModule))) {
                                    String ligne;
                                    while ((ligne = reader.readLine()) != null) {
                                        contenuModifie.append(ligne.replaceAll(ModuleParts[2], TF_module.getText())).append("\n");
                                    }
                                } catch (IOException e2) {
                                    System.out.println("Erreur lors de la lecture du fichier : " + e2.getMessage());
                                    return;
                                }
                                
                                try (FileWriter writer = new FileWriter(fichierModule)) {
                                    writer.write(contenuModifie.toString());
                                    frame.dispose();
                                } catch (IOException e3) {
                                    System.out.println("Erreur lors de l'écriture dans le fichier : " + e3.getMessage());
                                }
                            }
                        });
        
                        JPanel panel = new JPanel(new BorderLayout());
                        
                        JPanel topPanel = new JPanel();
                        topPanel.add(new JLabel("Nouveau nom : "));
                        topPanel.add(TF_module);
                        
                        panel.add(topPanel, BorderLayout.CENTER);
                        panel.add(changer, BorderLayout.SOUTH);
                        
                        frame.add(panel);
                        frame.setSize(400, 150);  // Définir la taille de la fenêtre
                        frame.setLocationRelativeTo(null);  // Centrer la fenêtre sur l'écran
                        frame.setVisible(true);

                    }

                    if ((quoi.equals("Module")) && (modif.equals("Changer l'id"))) {
                        String[] ModuleParts = lequel.split(" ");

                        String ID = "";

                        yearParts = ModuleParts[6].split("-");

                        digitsYear = yearParts[0].substring(2)+""+yearParts[1].substring(2);
                        digitsSemester = ModuleParts[7];

                        repertoire = "data/" + digitsYear+"_"+digitsSemester;
                        File folder = new File(repertoire);

                        File[] files = folder.listFiles();
                        Arrays.sort(files);

                        File  lastVersionFolder = files[files.length - 2];

                        try (BufferedReader reader = new BufferedReader(new FileReader(repertoire+"/"+lastVersionFolder.getName()+"/"+ModuleParts[4]+"/"+ModuleParts[2]+".txt"))) {
                            String ligne;
                            while ((ligne = reader.readLine()) != null) {
                                if (ligne.startsWith("Id ")) {
                                    ID = ligne.substring(3);
                                }
                            }
                        } catch (IOException e2) {
                            System.out.println("Erreur lors de la lecture du fichier : " + e2.getMessage());
                            return;
                        }

                        JFrame frame = new JFrame("Changer l'id du module "+ModuleParts[2]);
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        JTextField TF_moduleID = new JTextField();
                        TF_moduleID.setPreferredSize(new Dimension(200, 25));
                        JButton changer = new JButton("Changer");
                        changer.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                String ID = TF_moduleID.getText();

                                if (ID.isEmpty()) {
                                    JOptionPane.showMessageDialog(null, "Erreur : L'identifiant du module est vide");
                                    return;
                                }
                                
                                if (!(ID.matches("R[1-6]\\.(.*)"))) {
                                    JOptionPane.showMessageDialog(null, "Erreur : L'identifiant du module n'a pas le bon format \n  Format : R5.02");
                                    return;
                                }

                                String  lastVersion = lastVersionFolder.getName().substring(1, 4);

                                int newVersion = Integer.parseInt(lastVersion) + 1;
                                
                                repertoire = repertoire + "/V" + String.format("%03d", newVersion) + "_" + todayDate;

                                File newVersionFolder = new File(repertoire);
                                newVersionFolder.mkdirs();
                                
                                try {
                                    copyFolder(lastVersionFolder, newVersionFolder);
                                } 
                                catch (IOException e1) {
                                    e1.printStackTrace();
                                    JOptionPane.showMessageDialog(null, "Erreur lors de la copie des fichiers de la version précédent");
                                }

                                File fichierModule = new File(repertoire+"/"+ModuleParts[4]+"/"+ModuleParts[2]+".txt");
                                StringBuilder contenuModifie = new StringBuilder();
        
                                try (BufferedReader reader = new BufferedReader(new FileReader(fichierModule))) {
                                    String ligne;
                                    while ((ligne = reader.readLine()) != null) {
                                        if (ligne.startsWith("Id ")) {
                                            ligne = "Id "+TF_moduleID.getText();
                                        }
                                        contenuModifie.append(ligne).append("\n");
                                    }
                                } catch (IOException e2) {
                                    System.out.println("Erreur lors de la lecture du fichier : " + e2.getMessage());
                                    return;
                                }
                                
                                try (FileWriter writer = new FileWriter(fichierModule)) {
                                    writer.write(contenuModifie.toString());
                                    frame.dispose();
                                } catch (IOException e3) {
                                    System.out.println("Erreur lors de l'écriture dans le fichier : " + e3.getMessage());
                                }
                            }
                        });
        
                        JPanel panel = new JPanel(new BorderLayout());
                        
                        JPanel topPanel = new JPanel();
                        topPanel.add(new JLabel("Nouveau ID : "));
                        topPanel.add(TF_moduleID);

                        JLabel actuel = new JLabel("L'ID actuel : "+ID);
                        actuel.setHorizontalAlignment(SwingConstants.CENTER);
                        
                        panel.add(actuel, BorderLayout.NORTH);
                        panel.add(topPanel, BorderLayout.CENTER);
                        panel.add(changer, BorderLayout.SOUTH);
                        
                        frame.add(panel);
                        frame.setSize(400, 150);  // Définir la taille de la fenêtre
                        frame.setLocationRelativeTo(null);  // Centrer la fenêtre sur l'écran
                        frame.setVisible(true);

                    }

                    if ((quoi.equals("Module")) && (modif.equals("Changer de professeur"))) {
                        String[] ModuleParts = lequel.split(" ");
                        List<String> profList = new ArrayList<>();
                        

                        try (BufferedReader reader = new BufferedReader(new FileReader(professeursRepertoire))) {
                            String ligne;
                            while ((ligne = reader.readLine()) != null) {
                                profList.add(ligne);
                            }
                        } catch (IOException e2) {
                            System.out.println("Erreur lors de la lecture du fichier : " + e2.getMessage());
                            return;
                        }

                        JFrame frame = new JFrame("Changer un professuer du module "+ModuleParts[2]);
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        JComboBox<String> cb_profs = new JComboBox<>();
                        JComboBox<String> cb_newprof = new JComboBox<>(profList.toArray(new String[0]));

                        yearParts = ModuleParts[6].split("-");

                        digitsYear = yearParts[0].substring(2)+""+yearParts[1].substring(2);
                        digitsSemester = ModuleParts[7];

                        repertoire = "data/" + digitsYear+"_"+digitsSemester;
                        File folder = new File(repertoire);

                        File[] files = folder.listFiles();
                        Arrays.sort(files);

                        File  lastVersionFolder = files[files.length - 2];

                        try (BufferedReader reader = new BufferedReader(new FileReader(repertoire+"/"+lastVersionFolder.getName()+"/"+ModuleParts[4]+"/"+ModuleParts[2]+".txt"))) {
                            String ligne;
                            while ((ligne = reader.readLine()) != null) {
                                if (ligne.startsWith("Subtitle ")) {
                                    String[] profPart = ligne.split(" ");
                                    String[] profs = profPart[1].split("-");
                                    for (int i=0; i<profs.length ; i++) {
                                        for (String prof : profList) { 

                                            String[] nomsProfs = prof.split(" ");
                                            if (profs[i].equals(nomsProfs[0].charAt(0)+nomsProfs[1].substring(0,1))) {
                                                cb_profs.addItem(prof);
                                            }
                                        }
                                        
                                    } 
                                }
                            }
                        } catch (IOException e2) {
                            System.out.println("Erreur lors de la lecture du fichier : " + e2.getMessage());
                            return;
                        }

                        JButton changer = new JButton("Changer");
                        changer.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {

                                String  lastVersion = lastVersionFolder.getName().substring(1, 4);

                                int newVersion = Integer.parseInt(lastVersion) + 1;
                                
                                repertoire = repertoire + "/V" + String.format("%03d", newVersion) + "_" + todayDate;

                                File newVersionFolder = new File(repertoire);
                                newVersionFolder.mkdirs();
                                
                                try {
                                    copyFolder(lastVersionFolder, newVersionFolder);
                                } 
                                catch (IOException e1) {
                                    e1.printStackTrace();
                                    JOptionPane.showMessageDialog(null, "Erreur lors de la copie des fichiers de la version précédent");
                                }

                                File fichierModule = new File(repertoire+"/"+ModuleParts[4]+"/"+ModuleParts[2]+".txt");
                                StringBuilder contenuModifie = new StringBuilder();
        
                                try (BufferedReader reader = new BufferedReader(new FileReader(fichierModule))) {
                                    String ligne;
                                    String[] nomsNewProfs = ((String)cb_newprof.getSelectedItem()).split(" ");
                                    String[] nomsProfs = ((String)cb_profs.getSelectedItem()).split(" ");
                                    while ((ligne = reader.readLine()) != null) {
                                        if (ligne.contains("Subtitle")) {
                                            contenuModifie.append(ligne.replaceAll((nomsProfs[0].charAt(0)+nomsProfs[1].substring(0,1)), (nomsNewProfs[0].charAt(0)+nomsNewProfs[1].substring(0,1)))).append("\n");
                                        }
                                        else contenuModifie.append(ligne.replaceAll((nomsProfs[0].charAt(0)+nomsProfs[1].substring(0, 3)), (nomsNewProfs[0].charAt(0)+nomsNewProfs[1].substring(0, 3)))).append("\n");
                                    }
                                } catch (IOException e2) {
                                    System.out.println("Erreur lors de la lecture du fichier : " + e2.getMessage());
                                    return;
                                }
                                
                                try (FileWriter writer = new FileWriter(fichierModule)) {
                                    writer.write(contenuModifie.toString());
                                    frame.dispose();
                                } catch (IOException e3) {
                                    System.out.println("Erreur lors de l'écriture dans le fichier : " + e3.getMessage());
                                }
                            }
                        });
        
                        JPanel panel = new JPanel(new BorderLayout());
                        
                        JPanel midPanel = new JPanel();
                        midPanel.add(new JLabel("Nouveau professeur : "));
                        midPanel.add(cb_newprof);

                        JPanel topPanel = new JPanel();
                        topPanel.add(new JLabel("Professeur à changer : "));
                        topPanel.add(cb_profs);
                        
                        panel.add(topPanel, BorderLayout.NORTH);
                        panel.add(midPanel, BorderLayout.CENTER);
                        panel.add(changer, BorderLayout.SOUTH);
                        
                        frame.add(panel);
                        frame.setSize(400, 150);  // Définir la taille de la fenêtre
                        frame.setLocationRelativeTo(null);  // Centrer la fenêtre sur l'écran
                        frame.setVisible(true);

                    }

                    if ((quoi.equals("Module")) && (modif.equals("Changer la planification"))) {
                        String[] ModuleParts = lequel.split(" ");

                        yearParts = ModuleParts[6].split("-");

                        digitsYear = yearParts[0].substring(2)+""+yearParts[1].substring(2);
                        digitsSemester = ModuleParts[7];

                        repertoire = "data/" + digitsYear+"_"+digitsSemester;
                        File folder = new File(repertoire);

                        File[] files = folder.listFiles();
                        Arrays.sort(files);

                        File  lastVersionFolder = files[files.length - 2];

                        String  lastVersion = lastVersionFolder.getName().substring(1, 4);

                        int newVersion = Integer.parseInt(lastVersion) + 1;
                        
                        List<String> CoursList = new ArrayList<>();
                        boolean plusDeCours = false;

                        try (BufferedReader reader = new BufferedReader(new FileReader(repertoire + "/" + lastVersionFolder.getName() + "/" + ModuleParts[4] + "/" + ModuleParts[2] + ".txt"))) {
                            String ligne;
                            while ((ligne = reader.readLine()) != null) {
                                if (ligne.startsWith("Params")) {
                                    plusDeCours = true;
                                }
                                if (!plusDeCours) {
                                    CoursList.add(ligne);
                                }
                            }
                        } 
                        catch (IOException e6) {
                            System.out.println("Erreur lors de la lecture du fichier : " + e6.getMessage());
                            return;
                        }

                        repertoire = repertoire + "/V" + String.format("%03d", newVersion) + "_" + todayDate;

                        File newVersionFolder = new File(repertoire);
                        newVersionFolder.mkdirs();
                        
                        try {
                            copyFolder(lastVersionFolder, newVersionFolder);
                        } 
                        catch (IOException e1) {
                            e1.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Erreur lors de la copie des fichiers de la version précédent");
                        }

                        JFrame frame = new JFrame("Changer la planification du module "+ModuleParts[2]);
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        
                        JPanel centrePanel = new JPanel();
                        JDateChooser dateChooser = new JDateChooser();
                        dateChooser.setPreferredSize(new Dimension(130, 40));

                        // Définir la date de début minimale
                        Calendar minDate = Calendar.getInstance();
                        minDate.set(Calendar.YEAR, Integer.parseInt(digitsSemester.equals("S1") ? yearParts[0] : yearParts[1]));
                        minDate.set(Calendar.WEEK_OF_YEAR, (digitsSemester.equals("S1") ? (listLeapYear.contains(yearParts[1]) ? 36 : 35) : 4));
                        minDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Lundi de la semaine
                        dateChooser.setMinSelectableDate(minDate.getTime());
                        datePrecedente.add(minDate.getTime());

                        // Définir la date de fin maximale
                        Calendar maxDate = Calendar.getInstance();
                        maxDate.set(Calendar.YEAR, Integer.parseInt(yearParts[1]));
                        maxDate.set(Calendar.WEEK_OF_YEAR, (digitsSemester.equals("S1") ? (listLeapYear.contains(yearParts[1]) ? 4 : 3) : 25));
                        maxDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // Dimanche de la semaine 
                        dateChooser.setMaxSelectableDate(maxDate.getTime());

                        dateChooser.setDate(minDate.getTime());

                        JComboBox<String> comboBoxe = new JComboBox<>(CoursList.toArray(new String[0]));
                        comboBoxe.setPreferredSize(new Dimension(60, 40));
                        JLabel coursLabel = new JLabel("Cours "+Cours+" : ");
                        coursLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        centrePanel.add(coursLabel);
                        centrePanel.add(comboBoxe);
                        centrePanel.add(dateChooser);

                        JButton boutonPrecedent = new JButton("Retour à l'accueil");
                        JButton boutonCoursSuivant = new JButton("Suivant");
                
                        boutonCoursSuivant.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {

                                String elementSelectionne = (String) comboBoxe.getSelectedItem();
                                Date dateSelectionnee = dateChooser.getDate();

                                if (elementSelectionne == null && dateSelectionnee == null) {
                                    JOptionPane.showMessageDialog(null, "Erreur : La date n'est pas choisi");
                                    return;
                                }

                                if (Cours > 1) datePrecedente.add(dateSelectionnee);
                                else datePrecedente.add(minDate.getTime());
                                comboBoxe.removeItem(elementSelectionne);
                                dateChooser.setMinSelectableDate(dateSelectionnee);

                                File fichierModule = new File(repertoire+"/"+ModuleParts[4]+"/"+ModuleParts[2]+".txt");
                                StringBuilder contenuModifie = new StringBuilder();
        
                                try (BufferedReader reader = new BufferedReader(new FileReader(fichierModule))) {
                                    String ligne;
                                    int i = 0;
                                    List<String> CoursReader = new ArrayList<>();
                                    while ((ligne = reader.readLine()) != null) {
                                        if (i < CoursList.size()) CoursReader.add(ligne);
                                        else {
                                            if (ligne.startsWith("Params")) {
                                                cbPrecedente.add(elementSelectionne);
                                                CoursReader.set((Cours-1), elementSelectionne);
                                                for (String cour : CoursReader) {
                                                    contenuModifie.append(cour).append("\n");
                                                }
                                            }
    
                                            if (ligne.startsWith("Sched ")) {
                                                String[] schedPart = new String[CoursList.size() + 1];
                                                schedPart[0] = "Sched";
                                                String[] originalParts = ligne.split(" ");
                                                for (int k = 1; k < originalParts.length && k < schedPart.length; k++) {
                                                    schedPart[k] = originalParts[k];
                                                }
                        
                                                if (Cours < schedPart.length) {
                                                    schedPart[Cours] = semaine.format(dateSelectionnee);
                                                }
                        
                                                ligne = String.join(" ", schedPart);
                                            }
                                            contenuModifie.append(ligne).append("\n");
                                        }
                                        
                                        i++;
                                    }
                                } catch (IOException e2) {
                                    System.out.println("Erreur lors de la lecture du fichier : " + e2.getMessage());
                                    return;
                                }
                                
                                try (FileWriter writer = new FileWriter(fichierModule)) {
                                    writer.write(contenuModifie.toString());
                                } catch (IOException e3) {
                                    System.out.println("Erreur lors de l'écriture dans le fichier : " + e3.getMessage());
                                }

                                Cours++;
                                coursLabel.setText("Cours "+Cours+" : ");

                                if (boutonCoursSuivant.getText().equals("Validé")) {
                                    frame.dispose(); // Fermer la fenêtre actuelle
                                    Accueil accueil = new Accueil(); // Créer une nouvelle instance de la fenêtre Accueil
                                    accueil.setVisible(true); // Afficher la fenêtre Accueil
                                }

                                if (Cours == CoursList.size()) {
                                    boutonCoursSuivant.setText("Validé");
                                } else {
                                    boutonCoursSuivant.setText("Suivant");
                                }

                                if (Cours == 1) {
                                    boutonPrecedent.setText("Retour à l'accueil");
                                } 
                                else {
                                    boutonPrecedent.setText("Précédent");
                                }
                            }
                        });

                        boutonPrecedent.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {

                                if (boutonPrecedent.getText().equals("Retour à l'accueil")) {
                                    frame.dispose(); // Fermer la fenêtre actuelle
                                    Accueil accueil = new Accueil(); // Créer une nouvelle instance de la fenêtre Accueil
                                    accueil.setVisible(true); // Afficher la fenêtre Accueil
                                    return;
                                }

                                dateChooser.setMinSelectableDate(datePrecedente.getLast());
                                dateChooser.setDate(datePrecedente.getLast());
                                comboBoxe.addItem(cbPrecedente.getLast());

                                File fichierModule = new File(repertoire+"/"+ModuleParts[4]+"/"+ModuleParts[2]+".txt");
                                StringBuilder contenuModifie = new StringBuilder();
        
                                try (BufferedReader reader = new BufferedReader(new FileReader(fichierModule))) {
                                    String ligne;
                                    int i = 0;
                                    List<String> CoursReader = new ArrayList<>();
                                    while ((ligne = reader.readLine()) != null) {
                                        if (i < CoursList.size()) CoursReader.add(ligne);
                                        else {
                                            if (ligne.startsWith("Params")) {
                                                CoursReader.set((Cours-2),cbPrecedente.getLast());
                                                cbPrecedente.removeLast();
                                                for (String cour : CoursReader) {
                                                    contenuModifie.append(cour).append("\n");
                                                }
                                            }
    
                                            
                                            if (ligne.startsWith("Sched ")) {
                                                String[] schedPart = new String[CoursList.size() + 1];
                                                schedPart[0] = "Sched";
                                                String[] originalParts = ligne.split(" ");
                                                for (int k = 1; k < originalParts.length && k < schedPart.length; k++) {
                                                    schedPart[k] = originalParts[k];
                                                }
                        
                                                schedPart[Cours] = semaine.format(datePrecedente.getLast());
                                                datePrecedente.removeLast();

                                                ligne = String.join(" ", schedPart);
                                            }
                                            contenuModifie.append(ligne).append("\n");
                                        }
                                        
                                        i++;
                                    }
                                } catch (IOException e2) {
                                    System.out.println("Erreur lors de la lecture du fichier : " + e2.getMessage());
                                    return;
                                }
                                
                                try (FileWriter writer = new FileWriter(fichierModule)) {
                                    writer.write(contenuModifie.toString());
                                } catch (IOException e3) {
                                    System.out.println("Erreur lors de l'écriture dans le fichier : " + e3.getMessage());
                                }

                                Cours--;
                                coursLabel.setText("Cours "+Cours+" : ");

                                if (Cours == CoursList.size()) {
                                    boutonCoursSuivant.setText("Validé");
                                } else {
                                    boutonCoursSuivant.setText("Suivant");
                                }

                                if (Cours == 1) {
                                    boutonPrecedent.setText("Retour à l'accueil");
                                } 
                                else {
                                    boutonPrecedent.setText("Précédent");
                                }
                            }
                        });
        
                        JPanel panel = new JPanel(new BorderLayout());

                        JPanel sudPanel = new JPanel(new FlowLayout());
                        sudPanel.add(boutonPrecedent);
                        sudPanel.add(boutonCoursSuivant);
                        
                        panel.add(centrePanel, BorderLayout.CENTER);
                        panel.add(sudPanel, BorderLayout.SOUTH);
                        
                        frame.add(panel);
                        frame.setSize(300, 150);  // Définir la taille de la fenêtre
                        frame.setLocationRelativeTo(null);  // Centrer la fenêtre sur l'écran
                        frame.setVisible(true);

                    }

                    if ((quoi.equals("Module")) && (modif.equals("Ajouter un cours"))) {
                        String[] ModuleParts = lequel.split(" ");

                        yearParts = ModuleParts[6].split("-");

                        digitsYear = yearParts[0].substring(2)+""+yearParts[1].substring(2);
                        digitsSemester = ModuleParts[7];

                        repertoire = "data/" + digitsYear+"_"+digitsSemester;
                        File folder = new File(repertoire);

                        File[] files = folder.listFiles();
                        Arrays.sort(files);

                        File  lastVersionFolder = files[files.length - 2];
                        String  lastVersion = lastVersionFolder.getName().substring(1, 4);

                        int newVersion = Integer.parseInt(lastVersion) + 1;
                        
                        repertoire = repertoire + "/V" + String.format("%03d", newVersion) + "_" + todayDate;

                        File newVersionFolder = new File(repertoire);
                        newVersionFolder.mkdirs();
                        
                        try {
                            copyFolder(lastVersionFolder, newVersionFolder);
                        } 
                        catch (IOException e1) {
                            e1.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Erreur lors de la copie des fichiers de la version précédent");
                        }
                        repertoire = repertoire+"/"+ModuleParts[4];
                        moduleRepertoire = repertoire + "/" + ModuleParts[2] + ".txt";

                        try {
                            BufferedReader reader = new BufferedReader(new FileReader(professeursRepertoire));
                            ProfesseurComboBox.removeAllItems();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) ProfesseurComboBox.getModel();
                                if (model.getIndexOf(line) == -1) {
                                    // Ajoute le nouveau mot au modèle
                                    model.addElement(line);
                                }
    
                            }
                            reader.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Erreur lors de la lecture du fichier des modules");
                        }

                        Calendar minDate = Calendar.getInstance();
                        minDate.set(Calendar.YEAR, Integer.parseInt(digitsSemester.equals("S1") ? yearParts[0] : yearParts[1]));
                        minDate.set(Calendar.WEEK_OF_YEAR, (digitsSemester.equals("S1") ? (listLeapYear.contains(yearParts[1]) ? 36 : 35) : 4));
                        minDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); 

                        // Définir la date de fin maximale
                        Calendar maxDate = Calendar.getInstance();
                        maxDate.set(Calendar.YEAR, Integer.parseInt(yearParts[1]));
                        maxDate.set(Calendar.WEEK_OF_YEAR, (digitsSemester.equals("S1") ? (listLeapYear.contains(yearParts[1]) ? 4 : 3) : 25));
                        maxDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

                        dateDebut = minDate.getTime();
                        finDate = maxDate.getTime();

                        boolean plusDeCours = false;

                        try (BufferedReader reader = new BufferedReader(new FileReader(moduleRepertoire))) {
                            String ligne;
                            while ((ligne = reader.readLine()) != null) {
                                if (ligne.startsWith("StartsOn")) {
                                    String[] StartsOnPart = ligne.split(" ");
                                    minDate.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(StartsOnPart[1]));
                                    dateDebut = minDate.getTime();;
                                }
                                if (ligne.startsWith("EndsOn")) {
                                    String[] EndsOnPart = ligne.split(" ");
                                    maxDate.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(EndsOnPart[1]));
                                    finDate = maxDate.getTime();;
                                }
                                if (ligne.startsWith("Params")) {
                                    plusDeCours = true;
                                }
                                if (!plusDeCours) {
                                    nbCours++;
                                }
                            }
                        } 
                        catch (IOException e6) {
                            System.out.println("Erreur lors de la lecture du fichier : " + e6.getMessage());
                            return;
                        }
                        Cours = nbCours;
                        Cours++;
                        CoursLabel.setText("Cours "+Cours);

                        dispose();
                        ConfigurationsModules configurationsModules = new ConfigurationsModules(false,true); // Création d'une nouvelle instance de ConfigurationsModules
                        configurationsModules.setVisible(true); // Affichage de la fenêtre de configuration des modules
                    }

                    if ((quoi.equals("Module")) && (modif.equals("Supprimer un cours"))) {
                        String[] ModuleParts = lequel.split(" ");

                        yearParts = ModuleParts[6].split("-");

                        digitsYear = yearParts[0].substring(2)+""+yearParts[1].substring(2);
                        digitsSemester = ModuleParts[7];

                        repertoire = "data/" + digitsYear+"_"+digitsSemester;

                        List<String> CoursList = new ArrayList<>();
                        File folder = new File(repertoire);

                        File[] files = folder.listFiles();
                        Arrays.sort(files);

                        File  lastVersionFolder = files[files.length - 2];

                        boolean plusDeCours = false;

                        try {
                            BufferedReader reader = new BufferedReader(new FileReader(repertoire+"/"+lastVersionFolder.getName() +"/"+ModuleParts[4] + "/"+ModuleParts[2]+ ".txt"));
                            String ligne;
                            int i = 1;
                            while ((ligne = reader.readLine()) != null) {
                                if (ligne.startsWith("Params")) {
                                    plusDeCours = true;
                                }
                                if (!plusDeCours) {
                                    CoursList.add("Cours "+i+" : "+ligne+"\n");
                                    i++;
                                }
                            }
                            reader.close();
                        } 
                        catch (IOException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Erreur lors de la création de la combobox des cours");
                        }
                    

                        JFrame frame = new JFrame("Supprimer un cours");
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        @SuppressWarnings("rawtypes")
                        JComboBox cb_cours = new JComboBox<>(CoursList.toArray(new String[0]));
                        JButton supprimer = new JButton("Supprimer");
                        supprimer.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                
                                String  lastVersion = lastVersionFolder.getName().substring(1, 4);

                                int newVersion = Integer.parseInt(lastVersion) + 1;
                                
                                repertoire = repertoire + "/V" + String.format("%03d", newVersion) + "_" + todayDate;

                                File newVersionFolder = new File(repertoire);
                                newVersionFolder.mkdirs();
                                
                                try {
                                    copyFolder(lastVersionFolder, newVersionFolder);
                                } 
                                catch (IOException e1) {
                                    e1.printStackTrace();
                                    JOptionPane.showMessageDialog(null, "Erreur lors de la copie des fichiers de la version précédent");
                                }

                                try {
                                    StringBuilder lignesModifiees = new StringBuilder();
                                    String[] coursPart = ((String)cb_cours.getSelectedItem()).split(" ");
                                    int i = 1;
                                    System.out.println(repertoire+"/"+ModuleParts[4]+"/"+ModuleParts[2]+".txt");

                                    // Lecture du fichier
                                    BufferedReader lecteur = new BufferedReader(new FileReader(repertoire+"/"+ModuleParts[4]+"/"+ModuleParts[2]+".txt"));
                                    String ligne;
                                    while ((ligne = lecteur.readLine()) != null) {
                                        if (!(i == Integer.parseInt(coursPart[1]))) {

                                            if (ligne.startsWith("Sched")) {
                                                String[] SchedPart = ligne.split(" ");
                                                ligne ="Sched";
                                                for (int j=1; j<SchedPart.length; j++){
                                                    if (!(j == Integer.parseInt(coursPart[1]))) ligne += " " + SchedPart[j];
                                                }
                                            }

                                            lignesModifiees.append(ligne).append("\n");
                                        }
                                        
                                        i++;
                                    }
                                    lecteur.close();

                                    // Réécriture du fichier avec les lignes modifiées
                                    BufferedWriter ecrivain = new BufferedWriter(new FileWriter(repertoire+"/"+ModuleParts[4]+"/"+ModuleParts[2]+".txt"));
                                    ecrivain.write(lignesModifiees.toString());
                                    ecrivain.close();
        
                                    frame.dispose();  // Fermer la fenêtre
                                } 
                                catch (IOException ex) {
                                    ex.printStackTrace();
                                    JOptionPane.showMessageDialog(frame, "Échec de la suppression du répertoire.", "Erreur", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        });
        
                        JPanel panel = new JPanel(new BorderLayout());
                        
                        JPanel topPanel = new JPanel();
                        topPanel.add(new JLabel("Cours à supprimer : "));
                        topPanel.add(cb_cours);
                        
                        panel.add(topPanel, BorderLayout.CENTER);
                        panel.add(supprimer, BorderLayout.SOUTH);
                        
                        frame.add(panel);
                        frame.setSize(400, 150);  // Définir la taille de la fenêtre
                        frame.setLocationRelativeTo(null);  // Centrer la fenêtre sur l'écran
                        frame.setVisible(true);

                    }
                    
                }
            });

            // Ajouter un écouteur d'événements à la première JComboBox
            quoiComboBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    lequelComboBox.removeAllItems();
                    typeModificationComboBox.removeAllItems();
                    String choix = (String) quoiComboBox.getSelectedItem();

                    if (choix.equals("Annee/Semestre")) {

                        for (String annee : cb_modif_annee) typeModificationComboBox.addItem(annee);

                        if (anneeList.isEmpty()) {

                            dossiersAnneeEtSemestre = new File("data").listFiles(File::isDirectory);
                            if (dossiersAnneeEtSemestre != null) {
                                for (File dossier : dossiersAnneeEtSemestre) {
                                    if (dossier.getName().contains("2")) {
                                        String[] anneeEtSemestre = dossier.getName().split("_");
                                        
                                        int annee = Integer.parseInt(anneeEtSemestre[0].substring(2));
                                        String anneeString = "Année : 20" + (annee - 1) + "-20" + annee + " | " + anneeEtSemestre[1];
                                        
                                        anneeList.add(anneeString);
                                    }
                                }
                            }
                        }

                        for (String annee : anneeList.toArray(new String[0])) lequelComboBox.addItem(annee);

                    }

                    else if (choix.equals("Formation")) {
                        for (String formation : cb_modif_formation) typeModificationComboBox.addItem(formation);

                        if (formationList.isEmpty()) {

                            dossiersAnneeEtSemestre = new File("data").listFiles(File::isDirectory);
                            if (dossiersAnneeEtSemestre != null) {
                                for (File dossier : dossiersAnneeEtSemestre) {

                                    dossiersVersion = new File("data/"+dossier.getName()).listFiles();
                                    Arrays.sort(dossiersVersion);

                                    File  lastVersionFolder = dossiersVersion[dossiersVersion.length - 2];

                                    try {
                                        BufferedReader reader = new BufferedReader(new FileReader("data/"+dossier.getName()+"/"+lastVersionFolder.getName() + "/cursus.txt"));
                                        String line;
                                        while ((line = reader.readLine()) != null) {
                                            String[] anneeEtSemestre = dossier.getName().split("_");
                                            int annee = Integer.parseInt(anneeEtSemestre[0].substring(2));

                                            String formationString = "Formation : "+line+" | 20" + (annee - 1) + "-20" + annee +" "+ anneeEtSemestre[1];
                                            
                                            if (!(formationList.contains(formationString))) formationList.add(formationString);
                                        }
                                        reader.close();
                                    } 
                                    catch (IOException ex) {
                                        ex.printStackTrace();
                                        JOptionPane.showMessageDialog(null, "Erreur lors de la lecture du fichier : " + ex.getMessage());
                                    }
                                }
                            }
                        }

                        for (String formation : formationList.toArray(new String[0])) lequelComboBox.addItem(formation);
                    }

                    else if (choix.equals("Module")) {

                        for (String module : cb_modif_module) typeModificationComboBox.addItem(module);

                        if (moduleList.isEmpty()) {

                            dossiersAnneeEtSemestre = new File("data").listFiles(File::isDirectory);
                            if (dossiersAnneeEtSemestre != null) {
                                for (File dossier : dossiersAnneeEtSemestre) {

                                    dossiersVersion = new File("data/"+dossier.getName()).listFiles();
                                    Arrays.sort(dossiersVersion);

                                    File  lastVersionFolder = dossiersVersion[dossiersVersion.length - 2];

                                    dossiersFormation = new File("data/"+dossier.getName()+"/"+lastVersionFolder.getName()).listFiles(File::isDirectory);

                                    for (File dossierF : dossiersFormation) {

                                        try {
                                            BufferedReader reader = new BufferedReader(new FileReader("data/"+dossier.getName()+"/"+lastVersionFolder.getName()+"/"+dossierF.getName() + "/modules.txt"));
                                            String line;
                                            while ((line = reader.readLine()) != null) {
                                                String[] anneeEtSemestre = dossier.getName().split("_");
                                                int annee = Integer.parseInt(anneeEtSemestre[0].substring(2));

                                                String moduleString = "Module : "+ line+ " | "+dossierF.getName()+" / 20"+(annee - 1) + "-20" + annee +" "+ anneeEtSemestre[1];
                                                
                                                if (!(moduleList.contains(moduleString))) moduleList.add(moduleString);
                                            }
                                            reader.close();
                                        } 
                                        catch (IOException ex) {
                                            ex.printStackTrace();
                                            JOptionPane.showMessageDialog(null, "Erreur lors de la lecture du fichier : " + ex.getMessage());
                                        }
                                    }
                                }
                            }
                        }

                        for (String module : moduleList.toArray(new String[0])) lequelComboBox.addItem(module);
                    }
                }
            });
    
            pack(); // Ajuster la taille de la fenêtre
            setLocationRelativeTo(null); // Centrer la fenêtre sur l'écran
        }

    }
    
    /**
     * La classe Selectionner est une classe interne à la classe Sae21 de l'application.
     */
    private class Selectionner extends JFrame {

        private JComboBox<String> anneeComboBox = selectionAnneeComboBox;
        private JComboBox<String> semestreComboBox = selectionSemestreComboBox;
        private JComboBox<String> versionComboBox = selectionVersionComboBox;

        /**
         * Constructeur de la classe Selectionner est la page de selection de la section "Sélectionner" de l'application.
         */
        public Selectionner() {
            setTitle("Sélectionner");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setExtendedState(JFrame.MAXIMIZED_BOTH); // Afficher en plein écran
    
            JLabel labelAnneeScolaire = new JLabel("Année scolaire:");
    
            JLabel labelSemestre = new JLabel("Semestre:");
    
            JLabel labelVersion = new JLabel("Version:");
    
            JButton retourAccueilButton = new JButton("Sauvegarder et retour à l'accueil");
            JButton lancerShukanButton = new JButton("Sauvegarder et lancer Shukan");
    
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(10, 10, 10, 10);
            add(labelAnneeScolaire, gbc);
    
            gbc.gridx = 1;
            add(anneeComboBox, gbc);
    
            gbc.gridy = 1;
            gbc.gridx = 0;
            add(labelSemestre, gbc);
    
            gbc.gridx = 1;
            add(semestreComboBox, gbc);
    
            gbc.gridy = 2;
            gbc.gridx = 0;
            add(labelVersion, gbc);
    
            gbc.gridx = 1;
            add(versionComboBox, gbc);
    
            gbc.gridy = 3;
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.EAST;
            add(retourAccueilButton, gbc);
            gbc.gridy = 4;
            add(lancerShukanButton, gbc);

            retourAccueilButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    
                    selectedAnnee = (String) anneeComboBox.getSelectedItem();
                    selectedSemestre = (String) semestreComboBox.getSelectedItem();
                    String selectedVersion = (String) versionComboBox.getSelectedItem();

                    if (selectedVersion != null && selectedSemestre != null) {

                        yearParts = selectedAnnee.split("-");
                        String[] semesterParts = selectedSemestre.split(" ");
                        String[] Version = selectedVersion.split(" ");
                        String[] VersionDate = Version[3].split("/");

                        digitsYear = yearParts[0].substring(2)+""+yearParts[1].substring(2);
                        digitsSemester = semesterParts[0].substring(0, 1)+""+semesterParts[1];

                        try {
                            File semestreFile = new File("data/semestre.txt");
                            FileWriter writer = new FileWriter(semestreFile,false);
                            writer.write(digitsYear +"\n"+ digitsSemester);
                            writer.close();
                        } 
                        catch (IOException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Erreur lors de l'écriture dans le fichier semestre");
                            return;
                        }

                        try {
                            File versionFile = new File("data/" + digitsYear+"_"+digitsSemester+"/version.txt");
                            FileWriter writer = new FileWriter(versionFile);
                            writer.write(Version[1]+"\n" + 
                                         VersionDate[2].substring(2)+""+VersionDate[1]+""+VersionDate[0]);
                            writer.close();
                        } 
                        catch (IOException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Erreur lors de l'écriture dans le fichier de version");
                            return;
                        }

                    }
                    else {
                        JOptionPane.showMessageDialog(null, "Erreur : Choisier une annéee scolaire pour affichier les autre champs");
                        return;
                    }
                    
                    dispose(); // Fermer la fenêtre actuelle
                    Accueil accueil = new Accueil(); // Créer une nouvelle instance de la fenêtre Accueil
                    accueil.setVisible(true); // Afficher la fenêtre Accueil
                }
            });

            lancerShukanButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    
                    selectedAnnee = (String) anneeComboBox.getSelectedItem();
                    selectedSemestre = (String) semestreComboBox.getSelectedItem();
                    String selectedVersion = (String) versionComboBox.getSelectedItem();

                    if (selectedVersion != null && selectedSemestre != null) {

                        yearParts = selectedAnnee.split("-");
                        String[] semesterParts = selectedSemestre.split(" ");
                        String[] Version = selectedVersion.split(" ");
                        String[] VersionDate = Version[3].split("/");

                        digitsYear = yearParts[0].substring(2)+""+yearParts[1].substring(2);
                        digitsSemester = semesterParts[0].substring(0, 1)+""+semesterParts[1];

                        try {
                            File semestreFile = new File("data/semestre.txt");
                            FileWriter writer = new FileWriter(semestreFile,false);
                            writer.write(digitsYear +"\n"+ digitsSemester);
                            writer.close();
                        } 
                        catch (IOException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Erreur lors de l'écriture dans le fichier semestre");
                            return;
                        }

                        try {
                            File versionFile = new File("data/" + digitsYear+"_"+digitsSemester+"/version.txt");
                            FileWriter writer = new FileWriter(versionFile);
                            writer.write(Version[1]+"\n" + 
                                         VersionDate[2].substring(2)+""+VersionDate[1]+""+VersionDate[0]);
                            writer.close();
                        } 
                        catch (IOException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Erreur lors de l'écriture dans le fichier de version");
                            return;
                        }

                    }
                    else {
                        JOptionPane.showMessageDialog(null, "Erreur : Choisier une annéee scolaire pour affichier les autre champs");
                        return;
                    }
                    
                    Shukan shukan = new Shukan();
        
                    // Rendez la fenêtre Shukan visible
                    shukan.setVisible(true);
                    
                    // Fermez la fenêtre actuelle
                    dispose();
                }
            });

            // Ajouter un écouteur d'événements à la première JComboBox
            anneeComboBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    semestreComboBox.removeAllItems();
                    String choix = (String) anneeComboBox.getSelectedItem();

                    yearParts = choix.split("-");
                    digitsYear = yearParts[0].substring(2)+""+yearParts[1].substring(2);

                    if (dossiersAnneeEtSemestre != null) {
                        for (File dossier : dossiersAnneeEtSemestre) {
                            if (dossier.getName().contains(digitsYear)) {
                                String[] anneeEtSemestre = dossier.getName().split("_");
                                
                                int semestre = Integer.parseInt(anneeEtSemestre[1].substring(1));

                                String semestreString = "Semestre "+semestre;
                                
                                DefaultComboBoxModel<String> modelSemestre = (DefaultComboBoxModel<String>) semestreComboBox.getModel();
                                if (modelSemestre.getIndexOf(semestreString) == -1) {
                                    modelSemestre.addElement(semestreString);
                                }
                            }
                        }
                    }
                }
            });

            // Ajouter un écouteur d'événements à la deuxième JComboBox
            semestreComboBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    versionComboBox.removeAllItems();

                    selectedAnnee = (String) anneeComboBox.getSelectedItem();
                    selectedSemestre = (String) semestreComboBox.getSelectedItem();

                    if (selectedAnnee != null && selectedSemestre != null) {
                
                        yearParts = selectedAnnee.split("-");
                        String[] semesterParts = selectedSemestre.split(" ");

                        digitsYear = yearParts[0].substring(2)+""+yearParts[1].substring(2);
                        digitsSemester = semesterParts[0].substring(0, 1)+""+semesterParts[1];

                        File[] dossiersVersion = new File("data/" + digitsYear+"_"+digitsSemester).listFiles(File::isDirectory);

                        if (dossiersVersion != null) {
                            for (File dossier : dossiersVersion) {
                                if (dossier.getName().contains("V0")) {
                                    String[] Versions = dossier.getName().split("_");
                                    
                                    String versionString = "Version "+ Integer.parseInt(Versions[0].substring(1)) + " du "+Versions[1].substring(4)+"/"+Versions[1].substring(2,4)+"/20"+Versions[1].substring(0,2);
                                    
                                    DefaultComboBoxModel<String> modelVersion = (DefaultComboBoxModel<String>) versionComboBox.getModel();
                                    if (modelVersion.getIndexOf(versionString) == -1) {
                                        modelVersion.addElement(versionString);
                                    }
                                }
                            }
                        }
                    }
                }
            });
    
            setLocationRelativeTo(null); // Centrer la fenêtre sur l'écran
        }
    }
    
    /**
     * Copie un dossier de la source vers la destination.
     *
     * @param sourceFolder      Le dossier source à copier.
     * @param destinationFolder Le dossier de destination où le contenu doit être copié.
     * @throws IOException Si une erreur d'E/S se produit.
     */
    private void copyFolder(File sourceFolder, File destinationFolder) throws IOException {
        if (sourceFolder.isDirectory()) {
            if (!destinationFolder.exists()) {
                destinationFolder.mkdirs();
            }

            String[] files = sourceFolder.list();
            for (String file : files) {
                File srcFile = new File(sourceFolder, file);
                File destFile = new File(destinationFolder, file);

                // Appel récursif si c'est un dossier
                if (srcFile.isDirectory()) {
                    copyFolder(srcFile, destFile);
                } else {
                    Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    /**
     * Supprime récursivement un dossier.
     *
     * @param dossier Le dossier à supprimer.
     */
    private static void supprimerDossier(File dossier) {
        if (dossier.isDirectory()) {
            // Liste des fichiers et dossiers dans le dossier
            File[] fichiers = dossier.listFiles();
            if (fichiers != null) {
                for (File fichier : fichiers) {
                    // Appel récursif pour supprimer les fichiers/dossiers
                    supprimerDossier(fichier);
                }
            }
        }
        // Suppression du dossier
        dossier.delete();
    }

    /**
     * La méthode principale pour démarrer l'application.
     *
     * @param args Arguments de la ligne de commande (non utilisés).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Sae21();
            }
        });
    }
}