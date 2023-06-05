# RMI Hotel Booking Project

This project made for the [Distributed Systems](http://www.ice.uniwa.gr/education/undergraduate/courses/distributed-systems/) course.

## Usage
### Server:
```bash
# Start Server
java Server.HRServer 
```

### Client: 
```
HRClient [host] [command]

Host:
    provice IP number. By default using port number 5001. 

Commands:

    <!-- List the rooms  -->
    list         

    Output format:
        Room type: [Room type]. Availability: [Number] rooms. Price: [Number]


    <!-- Make a booking: -->
    book [Char Type_of_Room] [int Number_Of_Rooms] [String name]

        Options:                
            [Type_of_Room]: A, B, C, D, E.
            [Number_Of_Rooms]: Number of rooms.
            [name]: Person's name which the rooms will be booked. 


    <!-- List with all the guests -->
    guests

        output format:
            Client: [name]
                booked:
                        [number] [room_type] rooms
                Total value: [total price].
    
    
    <!-- Cancels a booking -->
    Cancel [Char Type_of_Room] [int Number_Of_Rooms] [String name]

        Options:                
            [Type_of_Room]: A, B, C, D, E.
            [Number_Of_Rooms]: Number of rooms.
            [name]: Person's name which the rooms will be booked. 
```

## Examples
### List
```
>java Client.HRClient localhost list
Room type: A. Availability: 25 rooms. Price: 60.0
Room type: B. Availability: 37 rooms. Price: 80.0
Room type: C. Availability: 20 rooms. Price: 90.0
Room type: D. Availability: 15 rooms. Price: 115.0
Room type: E. Availability: 9 rooms. Price: 140.0
```
### Book
```
>java Client.HRClient localhost book B 3 bill
3 rooms Booked to: bill. At price: 240.0.
```
```
>java Client.HRClient localhost book E 1 bill
1 rooms added to: bill. At price: 140.0.
```
```
>java Client.HRClient localhost book C 4 mark
4 rooms Booked to: mark. At price: 360.0.
```
### Guests
```
>java Client.HRClient localhost guests
Client: bill
booked:
3 B rooms
1 E rooms
Total value: 380.0.
```
```
>java Client.HRClient localhost guests
Client: bill
booked:
3 B rooms
1 E rooms
Total value: 380.0.
Client: mark
booked:
4 C rooms
Total value: 360.0.
```
### Cancel
```
>java Client.HRClient localhost cancel E 1 bill
1 Rooms canceled from: bill.
```
```
>java Client.HRClient localhost book E 10 joe
Cannot book 10. There are 9 rooms available.
Would you like to be notify for future available type E rooms? (y/n) y
Waiting for available rooms...
Rooms of type E capacity updated to: 10 available.
```
