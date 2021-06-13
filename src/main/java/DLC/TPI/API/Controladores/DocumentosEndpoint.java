package DLC.TPI.API.Controladores;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import DLC.TPI.Clases.Documento;
import DLC.TPI.DAO.Clases.DocumentoOAD;

@Path("/documentos")
public class DocumentosEndpoint {
    
    @Inject private DocumentoOAD dao;
    
    @GET
    @Path("/{codigo}")
    public Response obtener(@PathParam("codigo") Integer codigo) {
        Documento resp = dao.retrieve(codigo);
        
        return Response.ok(resp).build();
    }
    
    @GET
    @Path("/todos")
    @Produces("application/json")
    public Response obtenerTodos() {
        List<Documento> lista = dao.findAll();
        
        return Response.ok(lista).build();
    }
    
}