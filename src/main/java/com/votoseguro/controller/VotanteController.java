/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.votoseguro.controller;

import com.votoseguro.entity.Tbldepartamento;
import com.votoseguro.entity.Tblmunicipio;
import com.votoseguro.entity.Tblrolxpermiso;
import com.votoseguro.entity.Tblvotante;
import com.votoseguro.facade.DepartamentoFacade;
import com.votoseguro.facade.MunicipioFacade;
import com.votoseguro.facade.VotanteFacade;

import com.votoseguro.util.ValidationBean;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Luis Eduardo Valdez
 */
@ViewScoped
@ManagedBean(name = "votanteController")
public class VotanteController {

    @EJB
    ValidationBean vb;
    @EJB
    MunicipioFacade mf;
    @EJB
    VotanteFacade vf;
    @EJB
    DepartamentoFacade df;

    private @Getter
    @Setter
    List<Tblvotante> listaVotantes = new ArrayList<>();

    private @Getter
    @Setter
    List<Tbldepartamento> listaDeptos = new ArrayList<>();
    
    private @Getter
    @Setter
    List<Tblmunicipio> listaMunicipios = new ArrayList<>();

    private @Setter
    @Getter
    Tblvotante selectedVotante = new Tblvotante();

    private @Setter
    @Getter
    String nombrev = "";
    private @Setter
    @Getter
    String apellidov = "";
    private @Setter
    @Getter
    String dui = "";
    private @Setter
    @Getter
    String pregunta = "";
    private @Setter
    @Getter
    String respuesta = "";
    private @Setter
    @Getter
    String passvotante = "";
    private @Setter
    @Getter
    String passvotantere = "";
    private @Setter
    @Getter
    String genero = "";
    private @Setter
    @Getter
    String fnac = "";
    private @Setter
    @Getter
    String idMuni = "";
    private @Setter
    @Getter
    String idDepto = "";
     @Inject
    private 
    LoginMantController login;
     private @Setter
    @Getter
    String nacionalidad;
     private @Setter
    @Getter
    String lugarnac;
     private @Setter
    @Getter
    String fechaexp;
     private @Setter
    @Getter
    String lugarexp;
     private @Setter
    @Getter
    String fechavenc;
     private @Setter
    @Getter
    String direccion;
     private @Setter
    @Getter
    String tramite;
     private @Setter
    @Getter
    String nit;
     private @Setter
    @Getter
    String codzona;
     private @Setter
    @Getter
    String nommadre;
     private @Setter
    @Getter
    String nompadre;
     private @Setter
    @Getter
    String estadocivil;
     private @Setter
    @Getter
    String nomconyuge;
     private @Setter
    @Getter
    String tiposangre;
     private @Setter
    @Getter
    String profesion;
     

    private @Getter
    @Setter
    String nivelPermiso = "";

    @PostConstruct
    public void init() {
        nivelPermiso = asignarNivel("mantvotante.xhtml");
        listaDeptos = df.obtenerDeptos();
        listaMunicipios = mf.obtenerMunicipios(String.valueOf(listaDeptos.get(0).getIddepto()));
        listaVotantes = vf.obtenerVotantes(String.valueOf(listaMunicipios.get(0).getIdmuni()));

    }

    public void onSelect(Tblvotante v) {

        selectedVotante = v;
        nombrev = selectedVotante.getNombrev();
        apellidov = selectedVotante.getApellidov();
        passvotante = selectedVotante.getPassvotante();
        passvotantere = selectedVotante.getPassvotante();
        dui = selectedVotante.getDui();
        genero = selectedVotante.getGenero();
        fnac = selectedVotante.getFnac();
        pregunta = selectedVotante.getPregunta();
        respuesta = selectedVotante.getRespuesta();
        idMuni = String.valueOf(selectedVotante.getIdmuni().getIdmuni());
        codzona  = selectedVotante.getCodzona();
        estadocivil = selectedVotante.getEstadocivil();
        fechaexp = selectedVotante.getFechaexp();
        fechavenc = selectedVotante.getFechavenc();
        lugarexp = selectedVotante.getLugarexp();
        lugarnac = selectedVotante.getLugarnac();
        nacionalidad = selectedVotante.getNacionalidad();
        nomconyuge = selectedVotante.getNomconyuge();
        nommadre = selectedVotante.getNommadre();
        nompadre = selectedVotante.getNompadre();
        tiposangre = selectedVotante.getTiposangre();
        tramite = selectedVotante.getTramite();
        direccion = selectedVotante.getDireccion();
        nit = selectedVotante.getNit();
        profesion = selectedVotante.getProfesion();
        System.out.println("com.votoseguro.controller.VotanteController.onSelect("+v.getApellidov()+")");

    }

    public void onChange() {
        listaVotantes = vf.obtenerVotantes(idMuni);
        limpiar();

    }
 public void onChangeDepto() {
        listaMunicipios = mf.obtenerMunicipios(idDepto);
        listaVotantes = vf.obtenerVotantes(String.valueOf(listaMunicipios.get(0).getIdmuni()));
        vb.updateComponent("votanteForm:tblVot");
        limpiar();

    }
    public void deSelect() {
        limpiar();
    }

    public void limpiar() {
        selectedVotante = new Tblvotante();
        nombrev = "";
        apellidov = "";
        passvotante = "";
        passvotantere = "";
        dui = "";
        genero = "";
        fnac = "";
        pregunta = "";
        respuesta = "";
        codzona  = "";
        estadocivil = "";
        fechaexp = "";
        fechavenc = "";
        lugarexp = "";
        lugarnac = "";
        nacionalidad = "";
        nomconyuge = "";
        nommadre = "";
        nompadre = "";
        tiposangre = "";
        tramite = "";
        direccion = "";
        nit = "";
        profesion = "";

    }

    public void insert() {

        if (selectedVotante == null || selectedVotante.getIdvotante() == null) {
            if (setValores()) {
                if (vf.revisarDui(selectedVotante.getDui(), "0") == 0) {
                     vf.create(selectedVotante);
                listaVotantes = vf.obtenerVotantes(idMuni);
                limpiar();

                vb.lanzarMensaje("info", "lblMantVot", "lblAgregarSuccess");
                }else{
                    vb.lanzarMensaje("error", "lblMantVot", "lblDuiRepetido");
                }

            }
        } else {
            vb.lanzarMensaje("warn", "lblMantVot", "lblLimpVot");
        }
    }

    public void modificar() {

        vf.edit(selectedVotante);
        listaVotantes = vf.obtenerVotantes(idMuni);
        limpiar();
        vb.lanzarMensaje("info", "lblMantVot", "lblbtnModifiarSucces");

    }
    
    public String asignarNivel(String keyword) {
        String res = "";
        for (Tblrolxpermiso t : login.getLogedUser().getIdrol().getTblrolxpermisoList()) {
            if (t.getIdpermiso().getUrlpermiso().toLowerCase().contains(keyword.toLowerCase())) {
                res = String.valueOf(t.getNivelpermiso());

            }
        }
        return res;
    }
    

    public void eliminar() {
        selectedVotante.setEstadodel("I");
        vf.edit(selectedVotante);
        listaVotantes = vf.obtenerVotantes(idMuni);
        limpiar();
        vb.lanzarMensaje("info", "lblMantVot", "lblEliminarSuccess");
    }
     public void cerrarDialogo() {
        limpiar();
        vb.ejecutarJavascript("$('.modalPseudoClass').modal('hide'); ");
    }

    public void cerrarDialogo2() {
        limpiar();
        listaVotantes = vf.obtenerVotantes(idMuni);
        vb.ejecutarJavascript("$('.modalPseudoClass2').modal('hide'); ");
    }

    public void validarEliminar() {
        if (selectedVotante!= null && selectedVotante.getIdvotante() != null) {
            vb.ejecutarJavascript("$('.modalPseudoClass').modal('show');");

        } else {
            vb.lanzarMensaje("error", "lblMantVot", "lblVotReqMod");
        }

    }

    public void validarModificar() {
        if (selectedVotante!= null && selectedVotante.getIdvotante() != null) {
            if (setValores()) {
                int revisarDui = vf.revisarDui(selectedVotante.getDui(), String.valueOf(selectedVotante.getIdvotante()));
                if (revisarDui == 0) {
                
                vb.ejecutarJavascript("$('.modalPseudoClass2').modal('show');");
                
            }else{
                    vb.lanzarMensaje("error", "lblMantVot", "lblDuiRepetido");
                    listaVotantes = vf.obtenerVotantes(String.valueOf(idMuni));
                }
                
                }

        } else {
            vb.lanzarMensaje("error", "lblMantVot", "lblVotReqMod");
        }

    }
    
    public boolean setValores() {
        boolean flag = false;
        try {
            if (vb.validarCampoVacio(nombrev.trim(), "warn", "lblMantVot", "lblNomReqVot")
                    && vb.validarLongitudCampo(nombrev, 4, 50, "warn", "lblMantVot", "lblLongVotName")
                    && vb.validarSoloLetras(nombrev,  "warn", "lblMantVot", "lblLetrasVotName")
                    && vb.validarCampoVacio(apellidov.trim(), "warn", "lblMantVot", "lblApeReqVot")
                    && vb.validarLongitudCampo(apellidov, 4, 50, "warn", "lblMantVot", "lblLongVotApe")
                    && vb.validarSoloLetras(apellidov,  "warn", "lblMantVot", "lblLetrasVotApe")
                    && vb.validarLongitudCampo(dui.trim(), 10, 10, "warn", "lblMantVot", "lblLongVotDui")
                    && vb.validarCampoVacio(dui.trim(), "warn", "lblMantVot", "lblDuiReqVot")
                    && vb.validarCampoVacio(respuesta.trim(), "warn", "lblMantVot", "lblRespReqVot")
                    && vb.validarLongitudCampo(respuesta, 4, 50, "warn", "lblMantVot", "lblLongVotResp")
                    && vb.validarSoloLetras(respuesta,  "warn", "lblMantVot", "lblLetrasVotResp")
                    && vb.validarCampoVacio(fnac, "warn", "lblMantVot", "lblVotFnacReq")
                    && vb.validarLongitudCampo(fnac, 10, 10, "warn", "lblMantVot", "lblLongVotFnac")
                    && vb.validarFecha(fnac, "warn", "lblMantVot", "lblFnacVotValid")
                    && vb.validarCampoVacio(genero, "warn", "lblMantVot", "lblGenReqVot")
                    && vb.validarCampoVacio(passvotante, "warn", "lblMantVot", "lblPassReqVot")
                    && vb.validarLongitudCampo(passvotante, 4, 8, "warn", "lblMantVot", "lblLongVotPass")
                    && vb.validarCampoVacio(passvotantere, "warn", "lblMantVot", "lblPassreReqVot")
                    && vb.validarLongitudCampo(passvotantere, 4, 8, "warn", "lblMantVot", "lblLongVotPassre")
                    && vb.validarSoloLetras(nacionalidad, "warn", "lblMantVot", "lblNacionalidadSolo")
                    && vb.validarLongitudCampo(nacionalidad, 0, 30, "warn", "lblMantVot", "lblNacionalidadLong")
                    && vb.validarSoloLetras(lugarnac, "warn", "lblMantVot", "lblLugarNacSolo")
                    && vb.validarLongitudCampo(lugarnac, 0, 30, "warn", "lblMantVot", "lblLugarNacLong")
                 //   && vb.validarFecha(fechaexp,"warn", "lblMantVot", "lblFechaExpInvalid")
                    && vb.validarSoloLetras(lugarexp, "warn", "lblMantVot", "lblLuagrExpSolo")
                    && vb.validarLongitudCampo(lugarexp, 0, 30, "warn", "lblMantVot", "lblLugarExpLong")
                   // && vb.validarFecha(fechavenc,"warn", "lblMantVot", "lblFechaVencInvalid")
                    && vb.validarLongitudCampo(direccion, 4, 199, "warn", "lblMantVot", "lblDireccionLong")
                    && vb.validarLongitudCampo(tramite, 0, 14, "warn", "lblMantVot", "lblTipoTramiteLong")
                    && vb.validarLongitudCampo(codzona, 0, 20, "warn", "lblMantVot", "lblCodZonaLong")
                    && vb.validarLongitudCampo(nommadre, 0, 70, "warn", "lblMantVot", "lblNomMadreSolo")
                    && vb.validarSoloLetras(nommadre, "warn", "lblMantVot", "lblNomMadreLong")
                    && vb.validarLongitudCampo(nompadre, 0, 70, "warn", "lblMantVot", "lblNomPadreSolo")
                    && vb.validarSoloLetras(nompadre, "warn", "lblMantVot", "lblNomPadreLong")
                    && vb.validarLongitudCampo(nomconyuge, 0, 70, "warn", "lblMantVot", "lblNomConyugeSolo")
                    && vb.validarSoloLetras(nomconyuge, "warn", "lblMantVot", "lblNomConyugeLong")
                    && vb.validarLongitudCampo(tiposangre, 0, 10, "warn", "lblMantVot", "lblTipoSangreLong")
                    && vb.validarLongitudCampo(profesion, 0, 30, "warn", "lblMantVot", "lblProfSolo")
                    && vb.validarSoloLetras(profesion, "warn", "lblMantVot", "lblProfLong")
                    ) {
                                 if (passvotante.equals(passvotantere)) {
                     flag = true;
                      selectedVotante.setNombrev(nombrev);
                      selectedVotante.setApellidov(apellidov);
                      selectedVotante.setDui(dui);
                      selectedVotante.setPregunta(pregunta);
                      selectedVotante.setRespuesta(respuesta);
                      selectedVotante.setPassvotante(passvotante);
                      selectedVotante.setGenero(genero);
                      selectedVotante.setFnac(fnac);
                      selectedVotante.setEstadodel("A");
                      selectedVotante.setIdmuni(mf.find(new BigDecimal(idMuni)));
                      selectedVotante.setIduser(login.getLogedUser());
                      selectedVotante.setNacionalidad(nacionalidad);
                      selectedVotante.setLugarnac(lugarnac);
                      selectedVotante.setLugarexp(lugarexp);
                      selectedVotante.setFechaexp(fechaexp);
                      selectedVotante.setFechavenc(fechavenc);
                      selectedVotante.setDireccion(direccion);
                      selectedVotante.setTramite(tramite);
                      selectedVotante.setNit(nit);
                      selectedVotante.setCodzona(codzona);
                      selectedVotante.setNommadre(nommadre);
                      selectedVotante.setNompadre(nompadre);
                      selectedVotante.setEstadocivil(estadocivil);
                      selectedVotante.setNomconyuge(nomconyuge);
                      selectedVotante.setTiposangre(tiposangre);
                      selectedVotante.setProfesion(profesion);
                     
                }else{
                                     vb.lanzarMensaje("warn", "lblMantVot", "lblPasEqualsVot");
                                 }
               

                    
                       
               

            }

        } catch (Exception e) {
            System.out.println("com.votoseguro.controller.UsuarioController.setValores()");
            e.printStackTrace();
        }

        return flag;

    }
}
