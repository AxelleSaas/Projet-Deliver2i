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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Persistence;

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

    @ManyToMany
    @JoinTable(name = "SOLUTION_INSTANCE",
            joinColumns = @JoinColumn(name = "SOLUTION_ID", referencedColumnName="SOLUTION_ID"),
            inverseJoinColumns = @JoinColumn(name  = "INSTANCE_ID",referencedColumnName="INSTANCE_ID")
    )
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
        return "Solution " + this.id;
    }
    
    /* M E T H O D S */
    public void solutionTriviale(int indexInstance){
        for(Tournee t : this.instances.get(indexInstance).getTournees()){
            Shift s = new Shift();
            t.getShifts().add(s);
            s.ajouterTournee(t);
            
            this.ajouterShift(s);
        }
    }
    
    public void ajouterInstance(Instance i){
            this.instances.add(i);
            i.getSolutions().add(this);
    }
    
    public void ajouterShift(Shift s){
        s.setSolution(this);
        this.shifts.add(s);
    }
    
    public static void main(String[] args) {
        final EntityManagerFactory emf =Persistence.createEntityManagerFactory("persistenceUnit");
        final EntityManager em = emf.createEntityManager();

        try{
            final EntityTransaction et = em.getTransaction();
            try{
                et.begin();
                Solution s = new Solution();
                //s.ajouterInstance("./resources/instances/instance_test.csv");
                System.out.println("instances " + s.instances);
                s.solutionTriviale(0);
                System.out.println("shifts " + s.shifts);
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
