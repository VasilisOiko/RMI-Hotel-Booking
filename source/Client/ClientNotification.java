package Client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientNotification extends Remote
{
    public void Notification(String message) throws RemoteException;
}
