package be.tba.websockets;

import be.tba.servlets.session.WebSession;

public interface WebSocketSession 
{
	void receiveText(String text) throws Exception;
    void setCurrentUser(WebSession session);
    void disconnect(int status, String reason);
}
