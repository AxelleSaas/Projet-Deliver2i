package modele;

import io.InstanceReader;
import io.exception.ReaderException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Persistence;

/**
 *
 * @author Axelle SAAS et Hugo POLARD
 */

// Query prédéfinie permettant d'obtenir un shift n fonction de l'id de sa solution
@NamedQueries({
    @NamedQuery(name="Shift.getShiftBySolutionId",
                query = "SELECT shift FROM Shift shift WHERE shift.solution = :id ")
})
/**
 * Ensemble de tournées mises à la suite
 */
@Entity
public class Shift implements Serializable {

    /* A T T R I B U T S */
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Id du shift dans la base de données, doit être unique
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "SHIFT_ID")
    private Long id;

    /**
     * Temps mort d'un shift :
     * Temps entre les tournées durant lequel le livreur ne fait rien, c'est donc du temps de perdu
     * Le but est de réduire ce temps
     */
    private long tempsMort;

    
    /**
     * Liste des tournées qui composent un shift
     * On considère que cette liste est ordonnée par la date de debut des tournnes
     */
    @ManyToMany( fetch = FetchType.EAGER)
    @JoinTable(name = "SHIFT_TOURNEE",
            joinColumns = @JoinColumn(name = "SHIFT_ID", referencedColumnName="SHIFT_ID"),
            inverseJoinColumns = @JoinColumn(name  = "TOURNEE_ID",referencedColumnName="TOURNEE_ID")
    )
    private List<Tournee> tournees;
    
    /**
     * solution auquel appartient le shift
     */
    @ManyToOne
    private Solution solution;
    
    /* C O N S T R U C T E U R S */
    /**
     * Constructeur par défaut
     */
    public Shift() {
        this.tempsMort = 0;
        this.tournees = new ArrayList<>();
        this.solution = null;
    }
    
    /* A C C E S S E U R S   E T   M U T A T E U R S */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTempsMort() {
        return tempsMort;
    }

    public void setTempsMort(long tempsMort) {
        this.tempsMort = tempsMort;
    }

    public List<Tournee> getTournees() {
        return tournees;
    }

    public void setTournees(List<Tournee> tournees) {
        this.tournees = tournees;
    }

    public Solution getSolution() {
        return solution;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    /* E Q U A L S   E T   H A S H C O D E */    

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.id);
        hash = 71 * hash + (int) (this.tempsMort ^ (this.tempsMort >>> 32));
        hash = 71 * hash + Objects.hashCode(this.tournees);
        hash = 71 * hash + Objects.hashCode(this.solution);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Shift other = (Shift) obj;
        if (this.tempsMort != other.tempsMort) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.tournees, other.tournees)) {
            return false;
        }
        if (!Objects.equals(this.solution, other.solution)) {
            return false;
        }
        return true;
    }

    /* T O S T R I N G */
    @Override
    public String toString() {
        return "Shift{" + "id=" + id + ", tempsMort=" + tempsMort + ", tournees=" + tournees + '}';
    }
    
    /* M E T H O D S */
    
    /**
     * Retourne la derniere tournee de la liste de tournees du shift
     * 
     * @return La derniere tournee du shift
     */
    public Tournee getDerniereTournee() {
        if (!this.tournees.isEmpty()) {
            return this.tournees.get(this.tournees.size() - 1);
        }
        return null;
    }
    
    /**
     * Ajoute une tournee à la liste de tournées du shift, pour cela on regarde :
     *     - Si on peut l'ajouter avant la première tournee de la liste
     *     - Si on peut l'insérer entre deux tournnées
     *     - Si on peut l'ajouter après sans dépasser la durée maximale
     * 
     * @param tournee tournee que l'on souhaite ajouter
     * @param dureeMin duree minimum du shift (utiliser pour recalculer le temps mort)
     * @param dureeMax duree maximale du shift ( à ne pas dépasser)
     * 
     * @return true si l'ajout a été effectué et false sinon
     */
    public boolean ajouterTournee (Tournee tournee, long dureeMin, long dureeMax) {
        int index = 0;
        this.trierTournees();
        if (!this.tournees.isEmpty()) {
            // Si on dépasse la durée max en ajoutant la tournée, on ne l'ajoute pas
            if( this.duree() + tournee.duree() > dureeMax)
                return false;
            // On récupère la première tournée et on vérifie si on peut ajouter la tournée avant
            Tournee premiereTournee = this.getTournees().get(0);
            if (tournee.getFin().getTime() < premiereTournee.getDebut().getTime()) {
                // Ajout des tournées et mise à jour de l'état du shift
                this.tournees.add(tournee);
                this.calcTempsMort((int) dureeMin); // mise à jour du temps mort à chaque ajout de tournee
                this.trierTournees();   // On remet en ordre croissant en fonction du début des tournnées la liste des tournées
                return true;
            }
            
            // Boucle qui regarde si la tournée commence après la fin de la précedente et finit avant le debut de la suivante 
            Tournee tourneePrecedente = premiereTournee;
            for (Tournee tourneeSuivante : this.getTournees().subList(1, this.getTournees().size())) {
                if (tournee.getDebut().getTime() > tourneePrecedente.getFin().getTime() && tournee.getFin().getTime() < tourneeSuivante.getDebut().getTime()) {
                    // Ajout des tournées et mise à jour de l'état du shift
                    this.tournees.add(tournee);
                    this.calcTempsMort((int) dureeMin); // mise à jour du temps mort à chaque ajout de tournee
                    this.trierTournees();   // On remet en ordre croissant en fonction du début des tournnées la liste des tournées
                    return true;
                }
                tourneePrecedente = tourneeSuivante;
            }
            
            // On vérifie si on peut ajouter après la dernière tournée
            Tournee derniereTournee = this.tournees.get(this.tournees.size()-1);
            if (tournee.getDebut().after(derniereTournee.getFin()) && this.duree() < dureeMax) {
                // Ajout des tournées et mise à jour de l'état du shift
                this.tournees.add(tournee);
                this.calcTempsMort((int) dureeMin); // mise à jour du temps mort à chaque ajout de tournee
                this.trierTournees();   // On remet en ordre croissant en fonction du début des tournnées la liste des tournées
                return true;
            }
            return false;
        }
        // Si la liste est vide, on ajoute forcemment
        // Ajout des tournées et mise à jour de l'état du shift
        this.tournees.add(tournee);
        this.calcTempsMort((int) dureeMin); // mise à jour du temps mort à chaque ajout de tournee
        this.trierTournees();   // On remet en ordre croissant en fonction du début des tournnées la liste des tournées
        return true;
    }
    
    public long calcTempsMort(int dureeMin) {
        boolean premier = true;
        Tournee tourneePrec = this.tournees.get(0); // première tournée
        long temps = 0; // temps mort calculé au fur et à mesure
        
        // Si la durée est inférieure à la durée minimum, on prend en compote la durée minimum pour le temps mort
        if (this.duree() < dureeMin) {
            temps = (dureeMin - tourneePrec.duree());
            this.setTempsMort(temps);
            return (temps);
        }
        // On ajoute la somme des intervalles entre les tournées du shift
        for (Tournee t : this.tournees) {
            if (premier)
                premier = false;
            else {
                if (t.getDebut().getTime() > tourneePrec.getDebut().getTime())
                    temps += t.getDebut().getTime() - tourneePrec.getFin().getTime();
                else
                    return -1;
            };
            tourneePrec = t;
        }
        temps = temps/1000/60;  // Conversion en minutes
        this.tempsMort = temps;   // Mise à jour du temps mort (utilisation du setter car la mise à jour directe ne marche pas)
        return temps;
    }
    
    /**
     * retourne la durée du shift de bout en bout
     * 
     * @return la durée du shift
     */
    public long duree() {
        long t = 0;
        if(!this.getTournees().isEmpty())
            t = this.getTournees().get(this.getTournees().size()-1).getFin().getTime() - this.getTournees().get(0).getDebut().getTime();
        return (int) (t/60/1000) ;
    }
    
    /**
     * trie la liste de tournees du shift dans l'ordre croissant des dates de debut de chaque tournée
     */
    public void trierTournees () {
        Collections.sort(tournees, new Comparator<Tournee>() {
            @Override
            public int compare(Tournee t1, Tournee t2) {
                return t1.getDebut().compareTo(t2.getDebut());
            }
        });
    }
    
    public static void main(String[] args) throws ReaderException {
        Shift s = new Shift();
        Tournee t1 = new Tournee(new Date(2*6000), new Date(0), null);
        Tournee t2 = new Tournee(new Date(3*6000), new Date(1*6000), null);
        Tournee t3 = new Tournee(new Date(4*6000), new Date(3*6000), null);
        Tournee t4 = new Tournee(new Date(8*6000), new Date(6*6000), null);
        
        s.ajouterTournee(t1, 60, 120);
        s.ajouterTournee(t2, 60, 120);
        s.ajouterTournee(t3, 60, 120);
        s.ajouterTournee(t4, 60, 120);
        
        System.out.println(s);
        System.out.println(s.calcTempsMort(60));
        
    }        
}
