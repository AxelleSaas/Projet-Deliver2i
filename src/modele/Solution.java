
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
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
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
import metier.RequetePlanning;

/**
 *
 * @author Axelle
 */
@NamedQueries({
    @NamedQuery(name="Solution.findAll",
            query = "SELECT s FROM Solution s")
})
@Entity
public class Solution implements Serializable {

    /* A T T R I B U T S */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "SOLUTION_ID")
    private Long id;

    @ManyToOne
    private Instance instance;
    
    /**
     * Ensemble des shifts qui composent une solution
     * Une solution est composée de plusieurs shifts dans lequel sont placées les tournées de son instance
     */
    @OneToMany(mappedBy="solution", cascade = {
        CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.ALL
    })
    private List<Shift> shifts;

    /* C O N S T R U C T E U R S */
    public Solution() {
        this.instance = null;
        this.shifts = new ArrayList<>();
    }
    
    
    /* A C C E S S E U R S   E T   M U T A T E U R S */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }


    public List<Shift> getShifts() {
        return shifts;
    }

    public void setShifts(List<Shift> shifts) {
        this.shifts = shifts;
    }

    
    /* E Q U A L S   E T   H A S H C O D E */   
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Solution)) {
            return false;
        }
        Solution other = (Solution) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    /* T O S T R I N G */
    @Override
    public String toString() {
        return "Solution " + this.id;
    }
    
    /* M E T H O D S */ 
    public void ajouterInstance(Instance i){
            this.instance = i;
            i.getSolutions().add(this);
    }
    
    public void ajouterShift(Shift s){
        s.setSolution(this);
        this.shifts.add(s);
    }
    
    public void solutionTriviale(){
        for(Tournee t : this.instance.getTournees()){
            Shift s = new Shift();
            t.getShifts().add(s);
            s.ajouterTournee(t, this.instance.getDureeMinimale(), this.instance.getDureeMaximale());
            
            this.ajouterShift(s);
        }
    }
    
    public void solutionBasique(){
        boolean ajout = false;
        
        Instance instance = this.instance;
        instance.trier();
        
        this.ajouterShift(new Shift());
        for(Tournee t : this.instance.getTournees()){
            ajout = false;
            for (Shift s : this.getShifts()) {
                // Si on l'ajoute, on arrete la boucle
                if(s.ajouterTournee(t, instance.getDureeMinimale(), instance.getDureeMaximale())) {
                    ajout = true;
                    break;
                }
            }
            // On n'a pû l'ajouter dans aucun shift
            if (!ajout) {
                Shift shTemp = new Shift();
                shTemp.ajouterTournee(t, instance.getDureeMinimale(), instance.getDureeMaximale());
                this.ajouterShift(shTemp);
            }
        }
        System.out.println(this.shifts);
    }
    
    public void solutionIntermediaire(){
        boolean ajout = false;
        
        Instance instance = this.instance;
        instance.trier();
        this.ajouterShift(new Shift());
        for(Tournee t : this.instance.getTournees()){
            ajout = false;
            for (Shift s : this.getShifts()) {
                Tournee derniereTournee = new Tournee();
                if (s.getTournees().isEmpty()) {
                    if(s.ajouterTournee(t, instance.getDureeMinimale(), instance.getDureeMaximale())) {
                        ajout = true;
                        break;
                    }
                }
                derniereTournee = s.getTournees().get(s.getTournees().size() - 1);
                if (!(Math.abs(t.getDebut().getTime()/60000 - derniereTournee.getFin().getTime()/60000) > 60)) {
                    // Si on l'ajoute, on arrete la boucle
                    if(s.ajouterTournee(t, instance.getDureeMinimale(), instance.getDureeMaximale())) {
                        ajout = true;
                        break;
                    }
                }
            }
            // On n'a pû l'ajouter dans aucun shift
            if (!ajout) {
                Shift shTemp = new Shift();
                shTemp.ajouterTournee(t, instance.getDureeMinimale(), instance.getDureeMaximale());
                this.ajouterShift(shTemp);
            }
        }
        System.out.println(this.getShifts());
    }
    
    public int calcTempsMortTotal(int dureeMin){
        int tempsMort = 0;
        for (Shift s : this.getShifts()) {
            tempsMort += s.calcTempsMort(dureeMin);
        }
        return tempsMort;
    }
    
 

    public static void main(String[] args) {
        final EntityManagerFactory emf =Persistence.createEntityManagerFactory("Deliver2iPU");
        final EntityManager em = emf.createEntityManager();
        try{
            final EntityTransaction et = em.getTransaction();
            try{
                et.begin();
                Solution s = new Solution();
                Solution s1 = new Solution();
                Solution s2 = new Solution();
                InstanceReader ir = new InstanceReader("./resources/instances/instance_1.csv");
                s.ajouterInstance(ir.readInstance());
                s1.ajouterInstance(ir.readInstance());
                s2.ajouterInstance(ir.readInstance());
                s.solutionTriviale();
                s1.solutionBasique();
                s2.solutionIntermediaire();
                System.out.println(s2.getShifts());
                int duree = 0;
                int duree1 = 0;
                int duree2 = 0;
                for (Shift sh : s.getShifts()) {
                    for (Tournee t : sh.getTournees()) 
                        duree += t.duree();
                }
                for (Shift sh : s1.getShifts()) {
                    for (Tournee t : sh.getTournees()) 
                        duree1 += t.duree();
                }
                for (Shift sh : s2.getShifts()) {
                    for (Tournee t : sh.getTournees()) 
                        duree2 += t.duree();
                }
                System.out.println("Temps mort total obtenu en triviale : " + s.calcTempsMortTotal(s.getInstance().getDureeMinimale()) + " minutes (le temps utile total est de "+duree1+")");
                System.out.println("Temps mort total obtenu en basique : " + s1.calcTempsMortTotal(s.getInstance().getDureeMinimale()) + " minutes (le temps utile total est de "+duree+")");
                System.out.println("Temps mort total obtenu en intermediaire : " + s2.calcTempsMortTotal(s.getInstance().getDureeMinimale()) + " minutes (le temps utile total est de "+duree+")");
              
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
