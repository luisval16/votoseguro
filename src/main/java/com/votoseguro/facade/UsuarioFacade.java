/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.votoseguro.facade;

import com.votoseguro.entity.Tblrol;
import com.votoseguro.entity.Tblusuario;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Luis Eduardo Valdez
 */
@Stateless
public class UsuarioFacade extends AbstractFacade<Tblusuario>{
    
    @PersistenceContext(unitName = "votoseguroPU")
     private EntityManager em;   
    
    public UsuarioFacade(){
    super(Tblusuario.class);
    }
    
    @Override
    protected EntityManager getEntityManager(){
    return em;
    }
    
    public List<Tblusuario> obtenerUsuarios(){
     Query q = getEntityManager().createNativeQuery("select * from Tblusuario where estadodel = 'A'", Tblusuario.class);
     List<Tblusuario> listaEntity;
        try {
            listaEntity = q.getResultList();
            if (listaEntity.isEmpty()) {
                listaEntity = new ArrayList<Tblusuario>();
            }
        } catch (Exception e) {
            listaEntity = new ArrayList<Tblusuario>();
        }
        return listaEntity;
    }
    
}
