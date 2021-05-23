/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.ok;

/**
 *
 * @author Santiago
 */
@Path("/Test")
public class BuscadorEndpoint {
    @GET()
    public Response index(){
                
        return Response.ok("TEST").build();
    }
}
