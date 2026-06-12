package server;

import utility.Request;
import utility.Response;

public class RequestHandler {
    private final Server server;

    public RequestHandler(Server server) {
        this.server = server;
    }
    public Response handle(Request request){
        if (request.getCommand().split(" ")[0].equals("save")) return new Response(null, "Запрещенная команда");
        Response result = server.getCommandManager().executeCommand(request);
        System.out.println("response created: " + result);
        return result;
    }
}
