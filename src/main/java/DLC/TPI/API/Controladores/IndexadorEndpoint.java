package DLC.TPI.API.Controladores;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import DLC.TPI.API.Commons.Indexacion;
import DLC.TPI.Excepciones.TechnicalException;
import DLC.TPI.Negocio.Indexador;

@Path("/indexar")
public class IndexadorEndpoint {
    
    int cantIdx;
    Indexacion resp = new Indexacion();
    @Inject private Indexador indexador;
    
    @GET
    @Path("/")
    public Response lanzarIndexacion() {
        try {
        /*
        Thread thread = new Thread(new Runnable() {
            public void run() {
                indexador.indexar();
            }
        });
        thread.start();*/
            cantIdx = indexador.indexar();
        }catch (Exception ex) {
            throw new TechnicalException(ex);
        }
         resp.setRespTxt("Proceso de indexacion finalizado. ("+cantIdx+") documentos indexados");
        return Response.ok(resp).build();
    }
    
}