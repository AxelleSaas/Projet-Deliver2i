/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import io.InstanceReader;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.security.Provider.Service;
import metier.RequetePlanning;
import modele.Instance;
import modele.Shift;
import modele.Solution;
import modele.Tournee;

/**
 *
 * @author Axelle
 */
public class Test1 {
    public static void main(String[] args) {
        RequetePlanning requetePlanning = RequetePlanning.getInstance();
        EntityManager em = requetePlanning.getEntityManagerFactory().createEntityManager();
        try{
            final EntityTransaction et = em.getTransaction();
            try{
                et.begin();
                Solution s = new Solution();
                Solution s1 = new Solution();
                Solution s2 = new Solution();
                InstanceReader ir = new InstanceReader("./resources/instances/instance_1.csv");
                Instance i = ir.readInstance();
                em.persist(i);
                s.ajouterInstance(i);
                s1.ajouterInstance(i);
                s2.ajouterInstance(i);
                s.solutionTriviale();
                s1.solutionBasique();
                s2.solutionIntermediaire();
                em.persist(s);
                em.persist(s1);
                em.persist(s2);
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
              
                em.persist(s2);
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
            requetePlanning.close();
        }
    }
}
