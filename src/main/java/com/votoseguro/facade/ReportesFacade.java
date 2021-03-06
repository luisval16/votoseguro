/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.votoseguro.facade;

import com.votoseguro.entity.Tblperiodo;
import com.votoseguro.entity.Tblvoto;
import com.votoseguro.report.CandidatoPartido;
import com.votoseguro.report.DeptosGanados;
import com.votoseguro.report.RangoEdad;
import com.votoseguro.report.VotantesDepto;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ejb.Stateless;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author Luis Eduardo Valdez
 */
@Stateless
public class ReportesFacade extends AbstractFacade<Tblvoto> {
    
    @PersistenceContext(unitName = "votoseguroPU")
    private EntityManager em;
    
    public ReportesFacade() {
        super(Tblvoto.class);
    }
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public void pruebaReport() {
        try {
            
            Connection cn = em.unwrap(java.sql.Connection.class);
            String dir = "pages\\reportes\\report1.jrxml";
            String destino = System.getProperty("user.dir") + File.separator + "pdf\\prueba1.pdf";
            File file = new File(dir);
            
            try {
                
                ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                String realPath = ec.getRealPath("/");
                System.out.println(realPath + dir);
                JasperReport report = JasperCompileManager.compileReport(realPath + dir);
                
                String canonicalPath = file.getCanonicalPath();
                JasperPrint print = JasperFillManager.fillReport(report, null, cn);
                JRExporter exporter = new JRPdfExporter();
                
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destino);
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
                exporter.exportReport();
                System.out.println("File Created: " + destino);
            } catch (Exception e) {
                System.out.println("com.votoseguro.facade.ReportesFacade.pruebaReport()");
                e.printStackTrace();
            }
            System.out.println("exito");
        } catch (Exception e) {
            System.out.println("com.votoseguro.facade.ReportesFacade.pruebaReport()");
            e.printStackTrace();
        }
        
    }
    
    public List<Integer> obtenerCantidadGenero(int idperiodo) {
        
        List<Integer> lista = new ArrayList<>();
        try {
            Connection cn = em.unwrap(java.sql.Connection.class);
            PreparedStatement ps = cn.prepareStatement("select sum(males) as \"Hombres\", sum(females) as \"Mujeres\"\n"
                    + "from\n"
                    + "(select count(*) as Males, 0 as Females from (SELECT DISTINCT v.idvotante FROM tblvoto v inner join tblvotante vo on vo.IDVOTANTE = v.IDVOTANTE where vo.GENERO = 'M' and v.IDPERIODO = ?)\n"
                    + "union all\n"
                    + "select 0 as males, count(*) as Females\n"
                    + "from (SELECT DISTINCT v.idvotante FROM tblvoto v inner join tblvotante vo on vo.IDVOTANTE = v.IDVOTANTE where vo.GENERO = 'F' and v.IDPERIODO = ?))");
            ps.setInt(1, idperiodo);
            ps.setInt(2, idperiodo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(rs.getInt("Hombres"));
                lista.add(rs.getInt("Mujeres"));
            }
        } catch (Exception e) {
            System.out.println("com.votoseguro.facade.ReportesFacade.obtenerPorcentajeGenero()");
            e.printStackTrace();
        }
        
        return lista;
    }
    
    public List<String> obtenerCantidadVotaron() {
        
        List<String> lista = new ArrayList<>();
        try {
            Connection cn = em.unwrap(java.sql.Connection.class);
            PreparedStatement ps = cn.prepareStatement("select  TO_CHAR(fn_obtener_porcentaje(1),'99.99') as \"Porcentaje que Votaron\",TO_CHAR(fn_obtener_porcentaje(2),'99.99') as \"Porcentaje que No Votaron\"  from dual");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(rs.getString("Porcentaje que Votaron"));
                lista.add(rs.getString("Porcentaje que No Votaron"));
            }
        } catch (Exception e) {
            System.out.println("com.votoseguro.facade.ReportesFacade.obtenerPorcentajeGenero()");
            e.printStackTrace();
        }
        
        return lista;
    }
    
    public BufferedImage GraficoCantidadGenero(int idperiodo) {
        DefaultPieDataset pd = new DefaultPieDataset();
        List<Integer> lista = new ArrayList<>();
        JFreeChart jc = null;
        try {
            lista = obtenerCantidadGenero(idperiodo);
            pd.setValue("Hombres", lista.get(0));
            pd.setValue("Mujeres", lista.get(1));
            jc = ChartFactory.createPieChart("Cantidad de hombres y mujeres que votaron", pd, true, true, Locale.US);
        } catch (Exception e) {
            System.out.println("com.votoseguro.facade.ReportesFacade.GraficoCantidadGenero()");
            e.printStackTrace();
        }
        return jc.createBufferedImage(500, 500);
        
    }
   
    
    public BufferedImage GraficoCantidadVotaron() {
        DefaultPieDataset pd = new DefaultPieDataset();
        List<String> lista = new ArrayList<>();
        JFreeChart jc = null;
        try {
            lista = obtenerCantidadVotaron();
            pd.setValue("Ya Votaron", Double.parseDouble(lista.get(0)));
            pd.setValue("No Votaron", Double.parseDouble(lista.get(1)));
            jc = ChartFactory.createPieChart("Cantidad de Personas que votaron y que no votaron", pd, true, true, Locale.US);
        } catch (Exception e) {
            System.out.println("com.votoseguro.facade.ReportesFacade.GraficoCantidadGenero()");
            e.printStackTrace();
        }
        return jc.createBufferedImage(500, 500);
        
    }
    
    public void reporteCantidadGenero(Tblperiodo periodo) {
        try {
            
            Connection cn = em.unwrap(java.sql.Connection.class);
            String dir = "pages\\reportes\\CantidadGenero.jrxml";
            String destino = System.getProperty("user.dir") + File.separator + "pdf\\CantidadGenero.pdf";
            File file = new File(dir);
            Map parametersMap = new HashMap();
            parametersMap.put("grafico", GraficoCantidadGenero(Integer.parseInt(String.valueOf(periodo.getIdperiodo()))));
            parametersMap.put("idperiodo", Integer.parseInt(String.valueOf(periodo.getIdperiodo())));
            parametersMap.put("anio", String.valueOf(periodo.getAnio()));
            try {
                
                ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                String realPath = ec.getRealPath("/");
                System.out.println(realPath + dir);
                JasperReport report = JasperCompileManager.compileReport(realPath + dir);

                //String canonicalPath = file.getCanonicalPath();
                JasperPrint print = JasperFillManager.fillReport(report, parametersMap, cn);
                JRExporter exporter = new JRPdfExporter();
                
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destino);
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
                exporter.exportReport();
                System.out.println("File Created: " + destino);
            } catch (Exception e) {
                System.out.println("com.votoseguro.facade.ReportesFacade.pruebaReport()");
                e.printStackTrace();
            }
            System.out.println("exito");
        } catch (Exception e) {
            System.out.println("com.votoseguro.facade.ReportesFacade.pruebaReport()");
            e.printStackTrace();
        }
        
    }
    
    public void reporteCantidadVotaron() {
        try {
            
            Connection cn = em.unwrap(java.sql.Connection.class);
            String dir = "pages\\reportes\\votaronynovotaron.jrxml";
            String destino = System.getProperty("user.dir") + File.separator + "pdf\\votaronynovotaron.pdf";
            File file = new File(dir);
            Map parametersMap = new HashMap();
            parametersMap.put("grafico", GraficoCantidadVotaron());
            
            try {
                
                ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                String realPath = ec.getRealPath("/");
                System.out.println(realPath + dir);
                JasperReport report = JasperCompileManager.compileReport(realPath + dir);

                //String canonicalPath = file.getCanonicalPath();
                JasperPrint print = JasperFillManager.fillReport(report, parametersMap, cn);
                JRExporter exporter = new JRPdfExporter();
                
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destino);
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
                exporter.exportReport();
                System.out.println("File Created: " + destino);
            } catch (Exception e) {
                System.out.println("com.votoseguro.facade.ReportesFacade.pruebaReport()");
                e.printStackTrace();
            }
            System.out.println("exito");
        } catch (Exception e) {
            System.out.println("com.votoseguro.facade.ReportesFacade.pruebaReport()");
            e.printStackTrace();
        }
        
    }
    
    public List<VotantesDepto> obtenerCantidadPorDepto(Tblperiodo periodo) {
        
        List<VotantesDepto> lista = new ArrayList<>();
        
        try {
            Connection cn = em.unwrap(java.sql.Connection.class);
            PreparedStatement ps = cn.prepareStatement("select count(distinct v.IDVOTANTE) as total,d.nomdepto from tbldepartamento d\n"
                    + "inner join tblmunicipio m on m.iddepto = d.iddepto \n"
                    + "inner join tblvotante v on v.idmuni = m.IDMUNI \n"
                    + "inner join tblvoto vo on vo.idvotante = v.IDVOTANTE\n"
                    + "where idperiodo = ?\n"
                    + "group by d.nomdepto");
            ps.setInt(1, Integer.parseInt(String.valueOf(periodo.getIdperiodo())));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new VotantesDepto(rs.getString("NOMDEPTO"), rs.getInt("TOTAL")));
            }
        } catch (Exception e) {
            System.out.println("com.votoseguro.facade.ReportesFacade.obtenerCantidadPorDepto()");
            e.printStackTrace();
        }
        
        return lista;
    }
    
    public void reporteCantidadDepto(Tblperiodo periodo) {
        try {
            
            Connection cn = em.unwrap(java.sql.Connection.class);
            String dir = "pages\\reportes\\cantidadDepto.jrxml";
            String destino = System.getProperty("user.dir") + File.separator + "pdf\\cantidadDepto.pdf";
            File file = new File(dir);
            Map parametersMap = new HashMap();
            parametersMap.put("idperiodo", Integer.parseInt(String.valueOf(periodo.getIdperiodo())));
            parametersMap.put("grafico", graficoBarraCantDepto(periodo));
            parametersMap.put("anio", String.valueOf(periodo.getAnio()));
            try {
                
                ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                String realPath = ec.getRealPath("/");
                System.out.println(realPath + dir);
                JasperReport report = JasperCompileManager.compileReport(realPath + dir);

                //String canonicalPath = file.getCanonicalPath();
                JasperPrint print = JasperFillManager.fillReport(report, parametersMap, cn);
                JRExporter exporter = new JRPdfExporter();
                
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destino);
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
                exporter.exportReport();
                System.out.println("File Created: " + destino);
            } catch (Exception e) {
                System.out.println("com.votoseguro.facade.ReportesFacade.pruebaReport()");
                e.printStackTrace();
            }
            System.out.println("exito");
        } catch (Exception e) {
            System.out.println("com.votoseguro.facade.ReportesFacade.pruebaReport()");
            e.printStackTrace();
        }
        
    }
    
    public BufferedImage graficoBarraCantDepto(Tblperiodo periodo) {
        DefaultCategoryDataset dc = new DefaultCategoryDataset();
        JFreeChart jc = null;
        try {
            Connection cn = em.unwrap(java.sql.Connection.class);
            PreparedStatement ps = cn.prepareStatement("select count(distinct v.IDVOTANTE) as total,d.nomdepto from tbldepartamento d\n"
                    + "inner join tblmunicipio m on m.iddepto = d.iddepto \n"
                    + "inner join tblvotante v on v.idmuni = m.IDMUNI \n"
                    + "inner join tblvoto vo on vo.idvotante = v.IDVOTANTE\n"
                    + "where idperiodo = ? \n"
                    + "group by d.nomdepto");
            ps.setInt(1, Integer.parseInt(String.valueOf(periodo.getIdperiodo())));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {                
                
                dc.addValue(rs.getInt("TOTAL"), rs.getString("NOMDEPTO"), rs.getString("NOMDEPTO"));
            }
            jc = ChartFactory.createBarChart("Total votantes por departamento elecciones diputados " + String.valueOf(periodo.getAnio()) , "Departamentos", "Votantes", dc, PlotOrientation.HORIZONTAL, true, true, true);
            CategoryPlot plot = jc.getCategoryPlot();
            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            //renderer.setSeriesPaint(0, Color.green);
            //renderer.setSeriesPaint(1, Color.blue);
            //renderer.setMaximumBarWidth(2);
            renderer.setItemMargin(0.0000000000000000000000000000000000000000000000000000000000000001);
            
        } catch (Exception e) {
            System.out.println("com.votoseguro.facade.ReportesFacade.graficoBarraCantDepto()");
            e.printStackTrace();
        }
        
        return jc.createBufferedImage(700, 500);
        
    }
    
    public void reporteCantidadCandidatoPorPartido(Tblperiodo periodo) {
        try {
            
            Connection cn = em.unwrap(java.sql.Connection.class);
            String dir = "pages\\reportes\\diputadosPorPartido.jrxml";
            String destino = System.getProperty("user.dir") + File.separator + "pdf\\diputadosPorPartido.pdf";
            //File file = new File(dir);
            Map parametersMap = new HashMap();
            parametersMap.put("idperiodo", Integer.parseInt(String.valueOf(periodo.getIdperiodo())));
            //parametersMap.put("grafico", graficoBarraCantDepto(periodo));
            parametersMap.put("anio", String.valueOf(periodo.getAnio()));
            try {
                
                ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                String realPath = ec.getRealPath("/");
                System.out.println(realPath + dir);
                JasperReport report = JasperCompileManager.compileReport(realPath + dir);

                //String canonicalPath = file.getCanonicalPath();
                JasperPrint print = JasperFillManager.fillReport(report, parametersMap, cn);
                JRExporter exporter = new JRPdfExporter();
                
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destino);
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
                exporter.exportReport();
                System.out.println("File Created: " + destino);
            } catch (Exception e) {
                System.out.println("com.votoseguro.facade.ReportesFacade.diputadosPorPartido()");
                e.printStackTrace();
            }
            System.out.println("exito");
        } catch (Exception e) {
            System.out.println("com.votoseguro.facade.ReportesFacade.diputadosPorPartido()");
            e.printStackTrace();
        }
        
    }
    
    
    public List<DeptosGanados> obtenerDeptosGanadosPorPartido(int idperiodo){
    String sql= "select sum(valor) as TOTAL,d.NOMDEPTO,p.NOMPARTIDO \n" +
"from tbldepartamento d\n" +
"inner join tblcandidato c \n" +
"on c.IDDEPTO = d.IDDEPTO\n" +
"inner join tblvoto v \n" +
"on v.IDCANDIDATO = c.IDCANDIDATO\n" +
"inner join tblpartido p\n" +
"on p.IDPARTIDO = c.IDPARTIDO\n" +
"where v.idperiodo = ? \n" +
"group by d.NOMDEPTO,p.NOMPARTIDO order by d.nomdepto";
    List<DeptosGanados> lista = new ArrayList<>();
        try {
            Connection cn = em.unwrap(java.sql.Connection.class);
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, idperiodo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {                
                lista.add(new DeptosGanados(rs.getDouble("total"),rs.getString("nomdepto"),rs.getString("nompartido")));
            }
        } catch (Exception e) {
              System.out.println("com.votoseguro.facade.ReportesFacade.obtenerDeptosGanadosPorPartido()");
              e.printStackTrace();
        }
        return lista;
    }
    
    
    public void reporteDeptosGanados(Tblperiodo periodo) {
        try {
           // JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(obtenerDeptosGanadosPorPartido(Integer.parseInt(String.valueOf(periodo.getIdperiodo()))));
            //Connection cn = em.unwrap(java.sql.Connection.class);
            String dir = "pages\\reportes\\deptoPartido.jrxml";
            String destino = System.getProperty("user.dir") + File.separator + "pdf\\deptoPartido.pdf";
            //File file = new File(dir);
            
            Map parametersMap = new HashMap();
            //parametersMap.put("idperiodo", Integer.parseInt(String.valueOf(periodo.getIdperiodo())));
            //parametersMap.put("grafico", graficoBarraCantDepto(periodo));
            parametersMap.put("anio", String.valueOf(periodo.getAnio()));
            try {
                
                ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                String realPath = ec.getRealPath("/");
                System.out.println(realPath + dir);
                JasperReport report = JasperCompileManager.compileReport(realPath + dir);
                
                //String canonicalPath = file.getCanonicalPath();
                String uri = System.getProperty("user.dir")+ File.separator + "xml"+File.separator+"deptoPartido.xml";
                InputStream inputStream = new FileInputStream(new File(uri));
                JRXmlDataSource data = new JRXmlDataSource(uri,"/ganados/fila");
                JasperPrint print = JasperFillManager.fillReport(report, parametersMap,data);
                JRExporter exporter = new JRPdfExporter();
                
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destino);
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
                exporter.exportReport();
                System.out.println("File Created: " + destino);
            } catch (Exception e) {
                System.out.println("com.votoseguro.facade.ReportesFacade.diputadosPorPartido()");
                e.printStackTrace();
            }
            System.out.println("exito");
        } catch (Exception e) {
            System.out.println("com.votoseguro.facade.ReportesFacade.diputadosPorPartido()");
            e.printStackTrace();
        }
        
    }
    
     public RangoEdad obtenerDeptosGanadosPorPartido(int idperiodo, int i, int f){
    String sql= " select  count(*) as cantidad \n" +
"     from (select distinct v.nombrev from tblvotante  v \n" +
"     inner join tblvoto vo\n" +
"    on vo.IDVOTANTE = v.IDVOTANTE where FN_OBTENER_EDAD(fnac) between ? and ? and idperiodo = ?)";
    List<RangoEdad> lista = new ArrayList<>();
        try {
            Connection cn = em.unwrap(java.sql.Connection.class);
            PreparedStatement ps = cn.prepareStatement(sql);
            
            ps.setInt(1, i);
            ps.setInt(2, f);
            ps.setInt(3, idperiodo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {                
                lista.add(new RangoEdad("Votantes entre " + i + " y " + f,rs.getInt("CANTIDAD")));
            }
        } catch (Exception e) {
              System.out.println("com.votoseguro.facade.ReportesFacade.obtenerDeptosGanadosPorPartido()");
              e.printStackTrace();
        }
        return lista.get(0);
    }
     
     
      public void reporteRangoEdad(Tblperiodo periodo, BufferedImage grafico) {
        try {
           // JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(obtenerDeptosGanadosPorPartido(Integer.parseInt(String.valueOf(periodo.getIdperiodo()))));
            //Connection cn = em.unwrap(java.sql.Connection.class);
            String dir = "pages\\reportes\\RangoEdad.jrxml";
            String destino = System.getProperty("user.dir") + File.separator + "pdf\\RangoEdad.pdf";
            //File file = new File(dir);
            
            Map parametersMap = new HashMap();
            //parametersMap.put("idperiodo", Integer.parseInt(String.valueOf(periodo.getIdperiodo())));
            parametersMap.put("grafico", grafico);
            parametersMap.put("anio", String.valueOf(periodo.getAnio()));
            try {
                
                ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                String realPath = ec.getRealPath("/");
                System.out.println(realPath + dir);
                JasperReport report = JasperCompileManager.compileReport(realPath + dir);
                
                //String canonicalPath = file.getCanonicalPath();
                String uri = System.getProperty("user.dir")+ File.separator + "xml"+File.separator+"RangoEdad.xml";
                InputStream inputStream = new FileInputStream(new File(uri));
                JRXmlDataSource data = new JRXmlDataSource(uri,"/rango/fila");
                JasperPrint print = JasperFillManager.fillReport(report, parametersMap,data);
                JRExporter exporter = new JRPdfExporter();
                
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destino);
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
                exporter.exportReport();
                System.out.println("File Created: " + destino);
            } catch (Exception e) {
                System.out.println("com.votoseguro.facade.ReportesFacade.rangoedad()");
                e.printStackTrace();
            }
            System.out.println("exito");
        } catch (Exception e) {
            System.out.println("com.votoseguro.facade.ReportesFacade.rangoedad()");
            e.printStackTrace();
        }
        
    }
      
       public BufferedImage GraficoRangoEdad(List<RangoEdad> lista) {
        DefaultPieDataset pd = new DefaultPieDataset();
        //List<Integer> lista = new ArrayList<>();
        JFreeChart jc = null;
        try {
            //lista = obtenerCantidadGenero(idperiodo);
            for (RangoEdad re : lista) {
                pd.setValue(re.getTitulo(), re.getCantidad());
            }
            jc = ChartFactory.createPieChart("Cantidad de votantes en un rango de edad", pd, true, true, Locale.US);
        } catch (Exception e) {
            System.out.println("com.votoseguro.facade.ReportesFacade.GraficoCantidadGenero()");
            e.printStackTrace();
        }
        return jc.createBufferedImage(500, 500);
        
    }
       
       
        public List<CandidatoPartido> obtenerCandsPorPart(int idperiodo){
    String sql= "select sum(valor) as Votos, nomcand || ' ' || apecand as Candidato, nompartido as Partido from tblvoto v \n" +
"inner join tblcandidato c on c.idcandidato = v.idcandidato inner join tblpartido \n" +
"p on p.idpartido = c.idpartido where idperiodo = ? group by nomcand,apecand, nompartido order by nompartido, Votos desc";
    List<CandidatoPartido> lista = new ArrayList<>();
        try {
            Connection cn = em.unwrap(java.sql.Connection.class);
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, idperiodo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {                
                lista.add(new CandidatoPartido(rs.getDouble("Votos"),rs.getString("Partido"),rs.getString("Candidato")));
            }
        } catch (Exception e) {
              System.out.println("com.votoseguro.facade.ReportesFacade.obtenerDeptosGanadosPorPartido()");
              e.printStackTrace();
        }
        return lista;
    }
    
}
