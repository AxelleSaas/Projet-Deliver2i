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
import javax.persistence.OneToMany;
import javax.persistence.Persistence;

/**
 *
 * @author Axelle
 */
@Entity
public class Solution implements Serializable {

    /* A T T R I B U T S */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy="solution", cascade = {
        CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.ALL
    })
    private List<Instance> instances;
    
    @OneToMany(mappedBy="solution", cascade = {
        CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.ALL
    })
    private List<Shift> shifts;

    /* C O N S T R U C T E U R S */
    public Solution() {
        this.instances = new ArrayList<>();
        this.shifts = new ArrayList<>();
    }
    
    /* A C C E S S E U R S   E T   M U T A T E U R S */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Instance> getInstances() {
        return instances;
    }

    public void setInstances(List<Instance> instances) {
        this.instances = instances;
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
        return "Solution{" + "id=" + id + ", instances=" + instances + ", shifts=" + shifts + '}';
    }
    
    /* M E T H O D S */
    public void solutionTriviale(int indexInstance){
        Instance instance = this.instances.get(indexInstance);
        for(Tournee t : instance.getTournees()){
            Shift s = new Shift();
            s.ajouterTournee(t, instance.getDureeMinimale(), instance.getDureeMaximale());
            this.ajouterShift(s);
        }
    }
    
    public void solutionBasique(int indexInstance){
        boolean ajout = false;
        
        Instance instance = this.instances.get(indexInstance);
        instance.trier();
        
        this.ajouterShift(new Shift());
        for(Tournee t : this.instances.get(indexInstance).getTournees()){
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
                Shift sTemp = new Shift();
                sTemp.ajouterTournee(t, instance.getDureeMinimale(), instance.getDureeMaximale());
                this.ajouterShift(sTemp);
            }
        }
    }
    
    public int calcTempsMortTotal(int dureeMin){
        int tempsMort = 0;
        for (Shift s : this.getShifts()) {
            tempsMort += s.calcTempsMort(dureeMin);
        }
        return tempsMort;
    }
    
    public void ajouterInstance(String chemin){
        try {
            InstanceReader ir = new InstanceReader(chemin);
            this.instances.add(ir.readInstance());
            this.instances.get(this.instances.size()-1).setSolution(this);
        } catch (ReaderException ex) {
            Logger.getLogger(Solution.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void ajouterShift(Shift s){
        s.setSolution(this);
        this.shifts.add(s);
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
                s.ajouterInstance("./resources/instances/instance_3.csv");
                s1.ajouterInstance("./resources/instances/instance_3.csv");
                s.solutionBasique(0);
                s1.solutionTriviale(0);
                System.out.println(s);
                int duree = 0;
                int duree1 = 0;
                for (Tournee t : s.getInstances().get(0).getTournees()) {
                    duree += t.duree();
                }
                for (Tournee t : s1.getInstances().get(0).getTournees()) {
                    duree1 += t.duree();
                }
                System.out.println("Temps mort total obtenu en basique : " + s.calcTempsMortTotal(s.getInstances().get(0).getDureeMinimale()) + " minutes (le temps utile total est de "+duree+")");
                System.out.println("Temps mort total obtenu en triviale : " + s1.calcTempsMortTotal(s.getInstances().get(0).getDureeMinimale()) + " minutes (le temps utile total est de "+duree1+")");
                
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