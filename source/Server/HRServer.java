package Server;

import java.net.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class HRServer
{
    
    public static void main(String[] args)
    {

        try
        { //special exception handler for registry creation
			LocateRegistry.createRegistry(5001);
			System.out.println("java RMI registry created.");
		}
        catch (RemoteException e)
        {
			//do nothing, error means registry already exists
			System.out.println("java RMI registry already exists.");
        }

        try 
        {
            HRImpl hotel = new HRImpl();
            hotel.room.add(new hotel_room('A', 25, 60));
            hotel.room.add(new hotel_room('B', 40, 80));
            hotel.room.add(new hotel_room('C', 20, 90));
            hotel.room.add(new hotel_room('D', 15,115));
            hotel.room.add(new hotel_room('E', 10, 140));

            Naming.rebind("rmi://localhost:5001/HRInterface", hotel);
        }
        catch (RemoteException Exception)
        {
            System.out.println("Server: Remote connection: " + Exception);
            Exception.printStackTrace();
        }
        catch(MalformedURLException Exception)
        {
            System.out.println("Server: Malformed URL has occurred: " + Exception);
            Exception.printStackTrace();
        }
    }
}
