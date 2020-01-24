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
import modele.Solution;
import modele.Tournee;

/**
 *
 * @author Axelle
 */
public class Test1 {
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
                InstanceReader ir = new InstanceReader("./resources/instances/instance_test.csv");
                s.ajouterInstance(ir.readInstance());
                s1.ajouterInstance(ir.readInstance());
                s2.ajouterInstance(ir.readInstance());
                
                s.solutionBasique(0);
                s1.solutionTriviale(0);
                //s2.solutionIntermediaire(0);
                System.out.println(s.getShifts());
                int duree = 0;
                int duree1 = 0;
                int duree2 = 0;
                for (Tournee t : s.getInstances().get(0).getTournees()) {
                    duree += t.duree();
                }
                for (Tournee t : s1.getInstances().get(0).getTournees()) {
                    duree1 += t.duree();
                }
                for (Tournee t : s2.getInstances().get(0).getTournees()) {
                    duree1 += t.duree();
                }
                System.out.println("Temps mort total obtenu en triviale : " + s1.calcTempsMortTotal(s.getInstances().get(0).getDureeMinimale()) + " minutes (le temps utile total est de "+duree1+")");
                System.out.println("Temps mort total obtenu en basique : " + s.calcTempsMortTotal(s.getInstances().get(0).getDureeMinimale()) + " minutes (le temps utile total est de "+duree+")");
                System.out.println("Temps mort total obtenu en intermediaire : " + s2.calcTempsMortTotal(s.getInstances().get(0).getDureeMinimale()) + " minutes (le temps utile total est de "+duree2+")");

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
