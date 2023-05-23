package org.eclipse.jkube.kport.server;


import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.jkube.kit.remotedev.RemoteDevelopmentConfig;
import org.eclipse.jkube.kport.KportService;

@Path("/")
public class KportServer {

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from RESTEasy Reactive";
    }
    @Inject
    KportService remoteDevService;

    @POST
    @Path("/start")
    public String start(RemoteDevelopmentConfig config) {
        remoteDevService.start(config, false);
        return "started";
    }



    @POST
    @Path("/stop")
    public String stop() {
        remoteDevService.stop();
        return "stopped";
    }
}