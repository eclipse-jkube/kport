package org.eclipse.jkube.transporter.server;


import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.jkube.kit.remotedev.RemoteDevelopmentConfig;
import org.eclipse.jkube.transporter.KubeTransporterService;

@Path("/")
public class TransporterServer {

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from RESTEasy Reactive";
    }
    @Inject
    KubeTransporterService remoteDevService;

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