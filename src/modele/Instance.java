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
import java.util.Objects;
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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Persistence;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Axelle
 */
@NamedQueries({
    @NamedQuery(name="Instance.findAllName",
            query = "SELECT i.nom, i.id FROM Instance i"),
    @NamedQuery(name="Instance.findAll",
            query = "SELECT i FROM Instance i")
})
@Entity
public class Instance implements Serializable {

    /* A T T R I B U T S */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(nullable = false)
    private String nom;
    
    @Column(nullable = false)
    private int dureeMinimale;
    
     @Column(nullable = false)
    private int dureeMaximale;
    
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;
    
    @OneToMany(mappedBy="instance", cascade = {
        CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.ALL
    })
    private List<Tournee> tournees;
    
    @ManyToOne
    private Solution solution;
    
    /* C O N S T R U C T E U R S */
    public Instance() {
        this.nom = "nomInstance";
        this.date = new Date();
        this.dureeMinimale = 0;
        this.dureeMaximale = 0;
        this.tournees = new ArrayList<>();
        this.solution = null;
    }

    public Instance(String nom, int dureeMinimale, int dureeMaximale, Date date) {
        this();
        this.nom = nom;
        this.dureeMinimale = dureeMinimale;
        this.dureeMaximale = dureeMaximale;
        this.date = date;
    }
    
    
    /* A C C E S S E U R S   E T   M U T A T E U R S */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getDureeMinimale() {
        return dureeMinimale;
    }

    public void setDureeMinimale(int dureeMinimale) {
        this.dureeMinimale = dureeMinimale;
    }

    public int getDureeMaximale() {
        return dureeMaximale;
    }

    public void setDureeMaximale(int dureeMaximale) {
        this.dureeMaximale = dureeMaximale;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.id);
        hash = 59 * hash + Objects.hashCode(this.nom);
        hash = 59 * hash + this.dureeMinimale;
        hash = 59 * hash + this.dureeMaximale;
        hash = 59 * hash + Objects.hashCode(this.date);
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
        final Instance other = (Instance) obj;
        if (this.dureeMinimale != other.dureeMinimale) {
            return false;
        }
        if (this.dureeMaximale != other.dureeMaximale) {
            return false;
        }
        if (!Objects.equals(this.nom, other.nom)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        return true;
    }

    
    /* T O S T R I N G */
    @Override
    public String toString() {
        return "Instance " + nom ;
    }

    /* M E T H O D S */
    public static void main(String[] args) throws ReaderException {
        final EntityManagerFactory emf =Persistence.createEntityManagerFactory("persistenceUnit");
        final EntityManager em = emf.createEntityManager();
        InstanceReader ir = new InstanceReader("./resources/instances/instance_test.csv");

        try{
            final EntityTransaction et = em.getTransaction();
            try{
                et.begin();
                Instance i = ir.readInstance();
                em.persist(i);
                Shift shift = new Shift();
                shift.ajouterTournee(i.getTournees().get(0));
                i.getTournees().get(0).setShift(shift);
                em.persist(shift);
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
