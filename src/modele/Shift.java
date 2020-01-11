/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele;
import io.InstanceReader;
import io.exception.ReaderException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Persistence;

/**
 *
 * @author Axelle
 */
@Entity
public class Shift implements Serializable {

    /* A T T R I B U T S */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private long tempsMort;
    
    @OneToMany(mappedBy="shift", cascade = {
        CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.ALL
    })
    private List<Tournee> tournees;
    
    @ManyToOne
    private Solution solution;
    
    /* C O N S T R U C T E U R S */
    public Shift() {
        this.tempsMort = 0;
        this.tournees = new ArrayList<>();
        this.solution = null;
    }
    
    public Shift(int tpsMort) {
        this();
        tempsMort = tpsMort;
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
        hash = 53 * hash + Objects.hashCode(this.id);
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
        if (!Objects.equals(this.id, other.id)) {
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
    public boolean ajouterTournee (Tournee tournee, long dureeMin, long dureeMax) {
        int index = 0;
        if (!this.tournees.isEmpty()) {
            Tournee derniereTournee = this.tournees.get(this.tournees.size()-1);
            if (tournee.getDebut().after(derniereTournee.getFin()) && this.duree() < dureeMax) {
                // On insÃ¨re la tournÃ©e dans la liste
                this.tournees.add(tournee);
                this.calcTempsMort((int) dureeMin); // mise à jour du temps mort à chaque ajout de tournee
                return true;
            }
            return false;
        }
        this.tournees.add(tournee);
        this.calcTempsMort((int) dureeMin); // mise à jour du temps mort à chaque ajout de tournee
        return true;
    }
    
    public long calcTempsMort(int dureeMin) {
        boolean premier = true;
        Tournee tourneePrec = this.tournees.get(0);
        long temps = 0;
        if (this.tournees.size() == 1) {
            temps = (dureeMin - tourneePrec.duree());
            this.setTempsMort(temps);
            return (temps);
        }
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
        temps = temps/1000/60;
        this.setTempsMort(temps);
        return temps;
    }
    
    public long duree() {
        long t = 0;
        if(!this.getTournees().isEmpty())
            t = this.getTournees().get(this.getTournees().size()-1).getFin().getTime() - this.getTournees().get(0).getDebut().getTime();
        return (int) (t/60/1000) ;
    }
    
    public static void main(String[] args) throws ReaderException {
        final EntityManagerFactory emf =Persistence.createEntityManagerFactory("Deliver2iPU");
        final EntityManager em = emf.createEntityManager();

        try{
            final EntityTransaction et = em.getTransaction();
            try{
                et.begin();
                Solution s = new Solution();
                Solution s1 = new Solution();
                s.ajouterInstance("./resources/instances/instance_test.csv");
                s.solutionBasique(0);
                System.out.println("Temps mort total obtenu en basique : " + s.calcTempsMortTotal(s.getInstances().get(0).getDureeMinimale()) + " minutes");
                em.persist(s);
                et.commit();
            }
            catch (Exception ex) {
                et.rollback();
            }
        } 
        finally {
            if(em != null && em.isOpen()){
                em.close();
            }
            if(emf != null && emf.isOpen()){
                emf.close();
            }
        }
    }        
}
