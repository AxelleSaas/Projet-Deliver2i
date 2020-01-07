/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io;

import io.exception.EmptyFieldException;
import io.exception.FieldFormatException;
import io.exception.FieldNameException;
import io.exception.FileExistException;
import io.exception.FormatFileException;
import io.exception.NumberColumnsException;
import io.exception.OpenFileException;
import io.exception.ReaderException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import modele.Instance;
import modele.Tournee;

/**
 * Classe qui permet de lire une instance pour le projet de POO3 2019/2020.
 * Le format des instances est decrit dans le sujet du projet.
 * Attention, ceci est un squellette de code, il faut le compléter.
 * Des commentaires contenant 'TODO' vous aident pour voir a quel endroit vous pouvez completer.
 * N'hésitez pas a apporter toutes les modifications que vous jugez utiles.
 * 
 * @author Maxime Ogier
 */
public class InstanceReader {
    /**
     * Le fichier contenant l'instance.
     */
    private File instanceFile;
    /**
     * Le numero de la ligne courante dans le fichier.
     */
    private int numLigne;

    /**
     * Constructeur par donnee du chemin du fichier d'instance.
     * @param inputPath le chemin du fichier d'acces, qui doit terminer 
     * par l'extension du fichier (.csv).
     * @throws ReaderException lorsque le fichier n'est pas au bon format ou ne pas etre ouvert.
     */
    public InstanceReader(String inputPath) throws ReaderException {
        if (inputPath == null) {
            throw new OpenFileException();
        }
        if (!inputPath.endsWith(".csv")) {
            throw new FormatFileException("csv", "csv");
        }
        String instanceName = inputPath;
        this.instanceFile = new File(instanceName);
        this.numLigne = 0;
    }
    
    /**
     * Methoide principale pour lire le fichier d'instance.
     * @throws ReaderException lorsque les donnees dans le fichier d'instance 
     * sont manquantes ou au mauvais format.
     * @return Instance
     */
    public Instance readInstance() throws ReaderException {
        Scanner scanner = null;
        try {
            scanner = new Scanner(instanceFile);
        } catch (FileNotFoundException ex) {
            throw new FileExistException(instanceFile.getName());
        }
        // Dans les 4 lignes qui suivent vous recuperez des informations generales sur l'instance.
        String name = readStringInLine(scanner, "Nom");
        int dureeMin = readIntInLine(scanner, "Duree min");
        int dureeMax = readIntInLine(scanner, "Duree max");
        Date date = readDateInLine(scanner, "Date");
        
        Instance instance = new Instance(name, dureeMin, dureeMax, date);
        
        readStringInLine(scanner, new String[]{"Debut", "Fin"});
        // Dans la boucle qui suit, nous allons lire les donnees relatives a chaque tournee.
        while(true) {
            DebutFin elem = readPaireInLine(scanner);
            if(elem == null) {
                break;
            }
            //elem.addTime(date);
            // Notez que elem est un objet qui contient deux attributs : une date de debut et une date de fin
            // Vous pouvez acceder a ces deux attributs de la maniere suivante :
            // elem.getDebut()
            // elem.getFin()
            // Il est egalement important de noter que ces dates correspondent 
            // a la fusion de la date de l'instance au format jj/mm/aaa
            // et et des dates du trip au format hh:mm
            // Nous obtenons donc des dates au format jj/mm/aaaa hh:mm
            Tournee t = new Tournee(elem.getFin(), elem.getDebut(), instance);
            instance.getTournees().add(t);
            ////////////////////////////////////////////
            // TODO : Vous pouvez ajoutez chacune des tournees a votre instance
            ////////////////////////////////////////////
        }
        return instance;
    }

    /**
     * Lecture de plusieurs chaines de caracteres dans une seule ligne
     * @param scanner lecteur du fichier
     * @param names tous lesnoms qui doivent etre dans la ligne courante (separes par des points-virgules)
     * @throws ReaderException lorsque le format attendu n'est pas respecte
     */
    private void readStringInLine(Scanner scanner, String[] names) throws ReaderException {
        String[] values = nextLine(scanner);
        if(values.length != names.length) {
            throw new NumberColumnsException(numLigne, names);
        }
        for(int col=0; col<values.length; col++) {
            if (!values[col].equalsIgnoreCase(names[col])) {
                throw new FieldNameException(numLigne, col+1, names[col]);
            }
        } 
    }

    /**
     * Lecture d'un nombre entier dans la seconde colonne d'une ligne 
     * @param scanner lecteur du fichier
     * @param name le nom qui doit etre indique dans la premiere colonne
     * @return le nombre entier
     * @throws ReaderException lorsque le format attendu n'est pas respecte
     */
    private int readIntInLine(Scanner scanner, String name) throws ReaderException {
        String[] values = nextLine(scanner);
        checkLine(values, name);
        int val;
        try {
            val = Integer.parseInt(values[1]);
        } catch (NumberFormatException ex) {
            throw new FieldFormatException(numLigne, 2, "un nombre entier");
        }
        return val;
    }
    
    /**
     * Lecture de dates de deux dates au format hh:mm dans les deux premieres colonnes
     * @param scanner lecteur du fichier
     * @return les deux dates : debut et fin
     * @throws ReaderException lorsque le format attendu n'est pas respecte
     */
    private DebutFin readPaireInLine(Scanner scanner) throws ReaderException {
        String[] values = nextLine(scanner);
        if(values.length == 0) {
            return null;
        }
        if (values.length < 2) {
            throw new EmptyFieldException(numLigne, 2);
        } 
        DebutFin val;
        try {
            String pattern = "HH:mm";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            Date debut = simpleDateFormat.parse(values[0]);
            Date fin = simpleDateFormat.parse(values[1]);
            val = new DebutFin(debut, fin);
        } catch (ParseException ex) {
            throw new FieldFormatException(numLigne, 2, "un temps au format hh:mm");
        }
        return val;
    }
    
    /**
     * Lecture d'une date dans la seconde colonne d'une ligne, au format jj/mm/aaaa
     * @param scanner lecteur du fichier
     * @param name le nom qui doit etre indique dans la premiere colonne
     * @return la date 
     * @throws ReaderException lorsque le format attendu n'est pas respecte
     */
    private Date readDateInLine(Scanner scanner, String name) throws ReaderException {
        String[] values = nextLine(scanner);
        checkLine(values, name);
        Date val;
        try {
            String pattern = "dd/MM/yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            val = simpleDateFormat.parse(values[1]);
        } catch (ParseException ex) {
            throw new FieldFormatException(numLigne, 2, "une date au format jj/mm/aaaa");
        }
        return val;
    }

    /**
     * Lecture d'une chaine de caracteres dans la seconde colonne d'une ligne
     * @param scanner lecteur du fichier
     * @param name le nom qui doit etre indique dans la premiere colonne
     * @return la chaine de caracteres
     * @throws ReaderException lorsque le format attendu n'est pas respecte
     */
    private String readStringInLine(Scanner scanner, String name) throws ReaderException {
        String[] values = nextLine(scanner);
        checkLine(values, name);
        return values[1];    
    }

    /**
     * Verification de la validite d'une ligne.
     * @param values les valeurs dans la ligne, separes par colonne
     * @param name le nom qui doit etre dans la premiere colonne
     * @throws FieldNameException lorsque le nom dans la premiere colonne n'est pas correct
     * @throws EmptyFieldException losque la valeur dans la seconde colonne n'est pas remplie
     */
    private void checkLine(String[] values, String name) throws FieldNameException, EmptyFieldException {
        if (!values[0].equalsIgnoreCase(name)) {
            throw new FieldNameException(numLigne, 1, name);
        }
        if (values.length == 1 || values[1].length() == 0) {
            throw new EmptyFieldException(numLigne, 2);
        }
    }

    /**
     * Lecture de la prochaine ligne du fichier
     * @param scanner lecteur du fichier
     * @return les valeurs presentes dans les differentes colonnes (separees par des points-virgules)
     */
    private String[] nextLine(Scanner scanner) {
        String[] values = null;
        do {
            if(!scanner.hasNextLine()) {
                return new String[]{};
            }
            String line = scanner.nextLine();
            values = line.split(";");
            this.numLigne++;
        } while (values.length == 0);
        return values;
    }
    
    /**
     * Classe interne qui represente une paire de dates.
     */
    class DebutFin {
        private Date debut;
        private Date fin;

        public DebutFin(Date debut, Date fin) {
            this.debut = debut;
            this.fin = fin;
        }
        
        public void addTime(Date d) {
            debut = new Date(debut.getTime() + d.getTime());
            fin = new Date(fin.getTime() + d.getTime());
        }

        public Date getDebut() {
            return debut;
        }

        public Date getFin() {
            return fin;
        }
    }

    /**
     * Un petite test pour verifier que tout fonctionne correctement.
     */
    public static void main(String[] args) {
        try {
            InstanceReader reader = new InstanceReader("./resources/instances/instance_test.csv");
            reader.readInstance();
            System.out.println("Instance lue avec succes !");
        } catch (ReaderException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
