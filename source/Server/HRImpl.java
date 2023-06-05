package Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

//new data
import Client.ClientNotification;



public class HRImpl extends UnicastRemoteObject implements HRInterface
{
    /* Hotel Rooms */
    ArrayList <hotel_room> room = new ArrayList<>();
    /* Client bookings */
    ArrayList <client_reservation> clients = new ArrayList<>();
    /* Waiting queue  */
    ArrayList <RoomQueue> RoomsQueue = new ArrayList<>();
    
    public HRImpl() throws RemoteException
    {
        super(5001);
    }

    /* --------------------------------------INDEPENDENT METHODS-------------------------------------- */
    /* Match type of room with the index of the list (In other case this needs modification) */
    public int getIndex(char type)
    {
        int index = (int)type - 65;
        if( index < 0 || index > 4)
            index = -1;
        return index;
    }

        /* -----------------------------------Overload Method----------------------------------- */
    /* Create an array with available rooms for each type */
    public int[] availableRooms()
    {
        int []available_rooms = new int[room.size()];

        /* Store in array the available rooms for reservation */
       for (int index = 0; index<room.size(); index++)
       {
            available_rooms[index] = room.get(index).NoRooms;

           for(client_reservation registry: clients)
                available_rooms[index] -= registry.ReservedRooms(room.get(index).type); 
       }

       return available_rooms;
    }

    /* Retrun the available rooms for the given type */
    public int availableRooms(char type)
    {
        int available_room = 0;

        /* Match the type of the room with the index */
        int room_type_index = getIndex(type);
        if (room_type_index < 0)/* Case: Type not exists */
            return room_type_index;

        /* Store in array the available rooms of given type for reservation */
        available_room = room.get(room_type_index).NoRooms;

        for(client_reservation client: clients)
            available_room -= client.ReservedRooms(room.get(room_type_index).type); 

       return available_room;
    }
        /* -----------------------------------Overload Method----------------------------------- */
    /* --------------------------------------INDEPENDENT METHODS-------------------------------------- */

    /* ------------------------------------------RMI METHODS------------------------------------------ */
    /* List all available rooms */
    public String list()
    {
        
        String message = new String();

       int []available_rooms = new int[room.size()];

       /* Array with the available rooms */
        available_rooms = availableRooms();

        for (int index = 0; index<room.size(); index++)
        {
            message += "Room type: " +room.get(index).type +
                            ". Availability: " + available_rooms[index] +
                            " rooms. Price: " + room.get(index).price + "\n";
        }

        return message;
    }


    /* Make a booking */                                    //new data
    public String book(char type, int NoRooms, String name)
    {
        String message = new String();
        boolean reservation_booked = false;
        
        /* Match the type of the room with the index */
        int room_type = getIndex(type);
        if (room_type < 0)/* Case: Type not exists */
        {
            return "There is no room type: " + type + ".";
        }

        int available = availableRooms(type);

        /* Checking available rooms for the requested */
        if(available - NoRooms < 0)
        {
            message = "Cannot book " + NoRooms + ". There are " + available + " rooms available.";
            return message;
        }
        else
        {
            /* Search if the client with the given name exists */
            for (client_reservation client: clients)
            {
                if (client.name.equals(name))
                {
                    /* Search if the existed client made a booking with the same type of room */
                    for (hotel_room reservation : client.reservations)
                    {
                        if (reservation.type == type)
                        {
                            reservation.NoRooms += NoRooms;
                            reservation_booked = true;
                            break;
                        }
                    }
                    
                    /* Client booking for the first time this type of room */
                    if(reservation_booked == false)
                    {
                        client.reservations.add(new hotel_room(type, NoRooms, room.get(room_type).price));
                        reservation_booked = true;
                    }
                    message = NoRooms + " rooms added to: " + name;
                    break;
                }
            }

            /* Client booking for the first time room */
            if (reservation_booked == false)
            {
                clients.add(new client_reservation(name, type, NoRooms, room.get(room_type).price));
                message = NoRooms + " rooms Booked to: " + name;
            }
        }

        return message + ". At price: " + room.get(room_type).price*NoRooms + ".";
    }

    /* List all hotel Bookings */
    public String guests()
    {
        String message = new String();

        /* Search  */
        for (client_reservation client: clients)
        {

            message += "Client: " + client.name + "\nbooked:\n";

            for (hotel_room bookings: client.reservations)
            {
                message += "\t" + bookings.NoRooms + " " + bookings.type + " rooms\n";
            }
            message += "Total value: " + client.TotalCost() + ".\n\n";
            
        }

        return message;
    }
    

    public String cancel(char type, int NoRooms, String name)
    {
        String message = "Cancellation failed: ";
        boolean reservation_not_exists = true;
        
        /* Search if the client with the given name exists */
        for (client_reservation client: clients)
            if (client.name.equals(name))
            {
                /* Search if the existed client made a booking with the same type of room */
                for (hotel_room reservation : client.reservations)
                {
                    if (reservation.type == type)
                    {
                        reservation_not_exists = false;
                        /* booked rooms cannot be less that requested rooms */
                        if(reservation.NoRooms >= NoRooms)
                        {
                            /* Romove only if client has 0 rooms booked */
                            reservation.NoRooms -= NoRooms;
                            if (reservation.NoRooms == 0)
                            {
                                client.reservations.remove(reservation);
                                clients.remove(client);
                                
                            }
                            message = NoRooms + " Rooms canceled from: " + name + ".";
                            NotifyWaitingClients(type);
                        }
                        else
                            message += "number of rooms are more that already booked.";

                        return message;
                    }
                }
                if(reservation_not_exists)
                {
                    message +=  name + " has no room with type " + type + " booked.";
                    return message;
                }
            }
        
        if(reservation_not_exists)
            message += name + " has no rooms booked.";

        return message;
    }

    /* Add clients in Waiting List */
    public void AddOnWaitingList(ClientNotification client_info, char room_type)
    {
        /* Search client waiting list */
        for (RoomQueue queue : RoomsQueue)
        {
            /* if there is a queue of requested type. Add client info */
            if (queue.RoomType == room_type)
            {
                queue.ClientInfo.add(client_info);
                return;
            }
        }
        RoomsQueue.add(new RoomQueue(client_info, room_type));

        return;
    }
    /* ------------------------------------------RMI METHODS------------------------------------------ */

    /* --------------------------------------CLIENT REMOTE-------------------------------------- */
    public void NotifyWaitingClients(char RoomType)
    {
        /* Search rooms */
        for (RoomQueue queue : RoomsQueue)
        {
            /* Match the room type */
            if (queue.RoomType == RoomType)
            {
                /* Notify waiting clients */
                for (ClientNotification  notify_client: queue.ClientInfo)
                {
                    try
                    {
                        notify_client.Notification("Rooms of type " + RoomType +
                                                    " capacity updated to: " + availableRooms(RoomType) + " available.");    
                    }
                    catch (RemoteException exception)
                    {
                        System.err.println("Error at sending nottifications:");
                        exception.printStackTrace();
                    }
                }
            }
        }
    }
    /* --------------------------------------CLIENT REMOTE-------------------------------------- */
}

/* Hotel room information */
class hotel_room
{
    char type;
    int NoRooms;
    float price;

    hotel_room(char type, int NoRooms, float price)
    {
        this.type = type;
        this.NoRooms = NoRooms;
        this.price = price;
    }

    hotel_room(hotel_room room)
    {
        this.type = room.type;
        this.NoRooms = room.NoRooms;
        this.price = room.price;
    }
}

/* Client info on booking */
class client_reservation
{
    String name;
    ArrayList<hotel_room> reservations = new ArrayList<>();

    client_reservation(String name, char type, int NoRooms, float price)
    {
        this.name = name;
        hotel_room room = new hotel_room(type, NoRooms, price);
        reservations.add(room);
    }

    client_reservation(String name, hotel_room room)
    {
        this.name = name;
        reservations.add(room);
    }

    public int ReservedRooms(char type)
    {
        int total_reservations = 0;

        for(int index = 0; index<reservations.size(); index++)
        {
            if (reservations.get(index).type == type)
            {
                total_reservations += reservations.get(index).NoRooms;
            }
        }

        return total_reservations;
    }

    public float TotalCost()
    {
        float value = 0;

        for (hotel_room room : reservations)
        {
            value += room.price*room.NoRooms;     
        }

        return value;
    }
}

class RoomQueue
{
    char RoomType;
    ArrayList <ClientNotification> ClientInfo = new ArrayList<>();

    RoomQueue(ClientNotification ClientInfo, char type)
    {
        RoomType = type;
        this.ClientInfo.add(ClientInfo);
    }
}