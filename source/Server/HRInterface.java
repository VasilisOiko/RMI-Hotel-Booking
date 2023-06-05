package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import Client.ClientNotification;


public interface HRInterface extends Remote
{
    public String list() throws RemoteException;
    public String book(char type, int NoRooms, String name) throws RemoteException;
    public String guests() throws RemoteException;
    public String cancel(char type, int NoRooms, String name) throws RemoteException;

    public void AddOnWaitingList(ClientNotification client_info, char room_type) throws RemoteException;
}
