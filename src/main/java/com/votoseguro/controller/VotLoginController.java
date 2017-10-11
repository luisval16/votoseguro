/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.votoseguro.controller;

import com.votoseguro.entity.Tblvotante;
import com.votoseguro.facade.VotanteFacade;
import com.votoseguro.util.ValidationBean;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Luis Eduardo Valdez
 */
@Named(value = "votController")
@SessionScoped
public class VotLoginController implements Serializable {

    /**
     * Creates a new instance of VotLoginController
     */
    
    private @Getter @Setter boolean votLogged = false;
    private @Getter @Setter Tblvotante loggedVotante = new Tblvotante();
    private @Getter @Setter String dui;
    private @Getter @Setter String pass;
    private @Getter @Setter String opentag="<div data-carousel-3d>";
    private @Getter @Setter String closetag="</div>";
    
    @EJB
    VotanteFacade vf;
    @EJB
    ValidationBean vb;
    
    public VotLoginController() {
    }
    
    public String login(){
    String outcome = "";
        try {
            loggedVotante = vf.logear(dui, pass);
            if (loggedVotante != null) {
                outcome = "/index.xhtml?faces-redirect=true";
                votLogged = true;
            }else{
            vb.lanzarMensaje("warn", "titleLogin", "lblErrorLogin");
            }
        } catch (Exception e) {
            System.out.println("com.votoseguro.controller.VotLoginController.login()");
            e.printStackTrace();
        }
    return outcome;
    }
    
     public String logout(){
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        session.invalidate();
        
        return "/index.xhtml?faces-redirect=true";
    }
     
     public String getFullName(){
     String nombrecompleto = this.loggedVotante.getNombrev().toUpperCase() + "  " + this.loggedVotante.getApellidov().toUpperCase();
     return nombrecompleto;
     }
    
}
