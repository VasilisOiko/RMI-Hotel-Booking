package Client;

import java.net.*;
// import java.rmi.AccessException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
// import java.io.*;
// import java.util.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import Server.HRInterface;

public class HRClient extends UnicastRemoteObject implements ClientNotification
{    
    protected HRClient() throws RemoteException
    {
        super(); 
    }
    
    /* Implementation */
    public void Notification(String message) throws RemoteException
    {
        System.out.println(message);
    }

    public static void main(String[] args)
    {
        if (args.length < 2)
        {
            System.out.println("Too few arguments to function, 0 passed and at least 2 expected\n\n" +
                                "Usage: HRClient [host] [command] \n\n" + 
                                "Commands:\n\n" + 
                                "\tlist\n\n" + 
                                "\tbook [Char Type_of_Room] [int Number_Of_Rooms] [String name]\n\n" + 
                                "\tguests\n\n" + 
                                "\tCancel [Char Type_of_Room] [int Number_Of_Rooms] [String name]\n\n");
        }
        else
        {
            int port = 5001;

            try
            {
                HRInterface remoteOBJ = (HRInterface)Naming.lookup("rmi://" + args[0] +":"+ port + "/HRInterface");


                /* Call LIST method */
                if (args[1].equals("list") && args.length  == 2)
                {
                    System.out.println(remoteOBJ.list());
                }
                /* Call BOOK method */
                else if (args[1].equals("book") && args.length  == 5 )
                {
                    String incoming_message = new String();

                    incoming_message = remoteOBJ.book(args[2].charAt(0), Integer.parseInt(args[3]), args[4]);

                    System.out.println(incoming_message);

                    if (incoming_message.contains("Cannot"))
                    {
                        char room_type =args[2].charAt(0);
                        
                        try (Scanner user_input = new Scanner(System.in))
                        {
                            char choice;

                            System.out.print("Would you like to be notify for future available type " + room_type + " rooms? (y/n) ");
                            choice = user_input.next().charAt(0);

                            
                            if(choice == 'y')
                            {
                                HRClient client_info = new HRClient();
                                remoteOBJ.AddOnWaitingList(client_info, room_type);
                                System.out.println("Waiting for available rooms...");
                            }
                            else if (choice == 'n');
                            else
                            {
                                System.out.println("Ivalid Choice.");
                            }
                        }

                    }
                }
                /* Call GUEST method */
                else if (args[1].equals("guests") && args.length  == 2)
                {
                    System.out.println(remoteOBJ.guests());
                }
                /* Call CANCEL method */
                else if (args[1].equals("cancel") && args.length  == 5)
                {
                    System.out.println(remoteOBJ.cancel(args[2].charAt(0), Integer.parseInt(args[3]), args[4]));
                }
                else
                {
                    System.out.println("Wrong Syntax.");
                    return;
                }
            }
            catch (RemoteException | NotBoundException | MalformedURLException exception)
            {
                System.err.println("Client: RMI Connection failed: " + exception);
                exception.printStackTrace();
            }
        }

    }


}