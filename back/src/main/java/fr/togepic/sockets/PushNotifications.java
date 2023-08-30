package fr.togepic.sockets;

import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.annotation.WebListener;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint("/chat")
@WebListener
public class PushNotifications implements ServletRequestAttributeListener {

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        System.out.println("OPEN");
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("CLOSE");
    }
}

