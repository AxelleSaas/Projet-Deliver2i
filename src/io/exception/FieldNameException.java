/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.exception;

/**
 *
 * @author mogier01
 */
public class FieldNameException extends ReaderException {
    public FieldNameException (int numLigne, int numColonne, String val) {
        super("La ligne numéro "+numLigne+" du fichier est incorrecte",
                "Assurez-vous que ligne numéro "+numLigne+" contienne en colonne "+numColonne+" la chaîne de caractères '"+val+"'");
    }
}
