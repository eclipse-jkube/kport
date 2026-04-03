package org.eclipse.jkube.kport.server;


import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

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