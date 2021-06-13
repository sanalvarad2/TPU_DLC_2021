/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DLC.TPI.API.Controladores;

/**
 *
 * @author Santiago
 */
import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import DLC.TPI.DAO.Clases.ArchivoDAO;

/**
 *
 * @author Santiago
 */
@Path("/archivos")
public class ArchivosEndpoint extends HttpServlet {

    @Inject private ArchivoDAO dao;
    
    
    @GET
    @Path("/{codigo}")
    public Response obtenerUno(@PathParam("codigo") Integer codigo) {

        String resp = dao.getFile(codigo);
        
        return Response.ok(resp).build();
    }  
}
