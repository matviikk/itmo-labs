package server;

import utility.Request;
import utility.Response;
import utility.UserDAO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.channels.SelectionKey;
import java.util.concurrent.ExecutorService;

public class RequestHandler implements Runnable{
    private final Server server;
    private final Request request;
    private final ExecutorService service;
    private final SelectionKey key;
    private final UserDAO userDAO = new UserDAO();

    public RequestHandler(Server server, ByteArrayOutputStream byteArrayOutputStream, ExecutorService service, SelectionKey key) {
        try{
            this.server = server;
            byte[] requestBytes = byteArrayOutputStream.toByteArray();
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(requestBytes));
            this.request = (Request) objectInputStream.readObject();
            this.service = service;
            this.key = key;
        }
        catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public Response handle(Request request){
        System.out.println(request.getCommand());
        if (request.getCommand().split(" ")[0].equals("registration")){
            if (userDAO.getUserByUsername(request.getLogin()) != null){
                Response response =  new Response("Пользователь с таким логином уже существует");
                return response;
            }

            userDAO.insertUser(request.getLogin(), request.getPassowrd());
            Response response =  new Response("Успешная регистрация");
            return response;
        }

        if (!userDAO.verifyUserPassword(request.getLogin(), request.getPassowrd())){
            Response response =  new Response(null, "Проблема авторизации");
            response.setNeedAuth(true);
            return response;
        }

        if (request.getCommand().split(" ")[0].equals("save")) return new Response(null, "Запрещенная команда");
        Response result = server.getCommandManager().executeCommand(request);
        System.out.println("response created: " + result);
        return result;
    }

    @Override
    public void run() {

        System.out.println("REQUEST HANDLE LEVEL");
//        System.out.println("DELAY 3s");
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        Response response = handle(request);
        service.submit(new ResponseSender(server, response, key));
    }
}
