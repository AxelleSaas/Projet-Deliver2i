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
public class NumberColumnsException extends ReaderException {
    public NumberColumnsException(int numLigne, String[] names) {
        super("La ligne numéro "+numLigne+" du fichier est incorrecte",
                "Assurez-vous que ligne numéro "+numLigne+" contienne desc olonnes nommées : "+names);
    }
}
