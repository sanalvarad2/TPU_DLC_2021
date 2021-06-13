package DLC.TPI.API.Controladores;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import DLC.TPI.Clases.Posteo;
import DLC.TPI.DAO.Clases.PosteoOAD;

@Path("/posteos")
public class PosteosEndpoint {
    
    @Inject private PosteoOAD dao;
    
    @GET
    @Path("/todos")
    @Produces("application/json")
    public Response obtenerTodos() {
        
        List<Posteo> lista = dao.findAll();
        
        return Response.ok(lista).build();
    }
    
    @GET
    @Path("/{codigo}")
    public Response obtenerUno(@PathParam("codigo") Integer codigo) {

        Posteo resp = dao.retrieve(codigo);
        
        return Response.ok(resp).build();
    }
    
}