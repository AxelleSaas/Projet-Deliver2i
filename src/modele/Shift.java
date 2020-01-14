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
import javax.persistence.OneToMany;
import javax.persistence.Persistence;

/**
 *
 * @author Axelle
 */
@NamedQueries({
    @NamedQuery(name="Shift.getShiftBySolutionId",
                query = "SELECT shift FROM Shift shift WHERE shift.solution = :id ")
})
@Entity
public class Shift implements Serializable {

    /* A T T R I B U T S */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "SHIFT_ID")
    private Long id;

    private int tempsMort;
    

        @ManyToMany( fetch = FetchType.EAGER)
    @JoinTable(name = "SHIFT_TOURNEE",
            joinColumns = @JoinColumn(name = "SHIFT_ID", referencedColumnName="SHIFT_ID"),
            inverseJoinColumns = @JoinColumn(name  = "TOURNEE_ID",referencedColumnName="TOURNEE_ID")
    )
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

    public int getTempsMort() {
        return tempsMort;
    }

    public void setTempsMort(int tempsMort) {
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
    public boolean ajouterTournee (Tournee tournee) {
        int index = 0;
        tournee.getShifts().add(this);
        if (!this.tournees.isEmpty()) {
            Tournee derniereTournee = this.tournees.get(this.tournees.size()-1);
            if (tournee.getDebut().after(derniereTournee.getDebut())) {
                // On insère la tournée dans la liste
                this.tournees.add(tournee);
                return true;
            }
            return false;
        }
        this.tournees.add(tournee);
        return true;
    }
    
    public static void main(String[] args) throws ReaderException {
        final EntityManagerFactory emf =Persistence.createEntityManagerFactory("persistenceUnit");
        final EntityManager em = emf.createEntityManager();

        try{
            final EntityTransaction et = em.getTransaction();
            try{
                et.begin();
                
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
