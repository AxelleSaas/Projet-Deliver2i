/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
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
import javax.persistence.NamedQuery;
import javax.persistence.Persistence;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Axelle
 */

@NamedQuery(name="Tournee.getTourneeByInstanceId",
            query = "SELECT t FROM Tournee t WHERE t.instance = :id ")
@Entity
public class Tournee implements Serializable {

    /* A T T R I B U T S */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    @Temporal(TemporalType.TIME)
    private Date fin;
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIME)
    private Date debut;
    
    @ManyToOne
    private Instance instance;
    
    @ManyToOne
    private Shift shift;
    
    /* C O N S T R U C T E U R S */
    public Tournee() {
        this.debut = new Date();
        this.fin = new Date();
        this.instance = new Instance();
        this.shift = null;
    }

    public Tournee(Date fin, Date debut, Instance i) {
        this();
        if(fin != null)
            this.fin = fin;
        if(debut != null)
            this.debut = debut;
        if(i != null)
            this.instance = i;
    }
   
    
    /* A C C E S S E U R S   E T   M U T A T E U R S */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFin() {
        return fin;
    }

    public void setFin(Date fin) {
        this.fin = fin;
    }
    
    public Date getDebut() {
        return debut;
    }

    public void setDebut(Date debut) {
        this.debut = debut;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }
    
    


    /* E Q U A L S   E T   H A S H C O D E */
        @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + Objects.hashCode(this.id);
        hash = 13 * hash + Objects.hashCode(this.fin);
        hash = 13 * hash + Objects.hashCode(this.debut);
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
        final Tournee other = (Tournee) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.fin, other.fin)) {
            return false;
        }
        if (!Objects.equals(this.debut, other.debut)) {
            return false;
        }
        return true;
    }

    /* T O S T R I N G */

    @Override
    public String toString() {
        return "Tournee{" + "id=" + id + ", debut=" + debut + ", fin=" + fin + ", instance=" + instance.getNom() + "}\n";
    }

    
    /* M E T H O D S */
    public static void main(String[] args) {
        final EntityManagerFactory emf =Persistence.createEntityManagerFactory("persistenceUnit");
        final EntityManager em = emf.createEntityManager();
        try{
            final EntityTransaction et = em.getTransaction();
            try{
                et.begin();
                /*Tournee t = new Tournee(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis() + 1000));
                System.out.println(t);
                em.persist(t);*/
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
    
    public long duree() {
        return (int) (this.getFin().getTime() - this.getDebut().getTime())/1000/60;
    }
}