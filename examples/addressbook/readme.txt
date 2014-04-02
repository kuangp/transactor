Example for running examples.addressbook:

Running the AddressBook as a service
1. Start the name server
2. Binding AddressBook to a UAN. For example,
  java -Duan=uan:\\nameserver\myname examples.addressbook.AddressBook

Running AddUser to add a user to AddressBook actor:
java examples.addressbook.AddUser uan:\\nameserver\myname username "hello world"

Running GetEmail to get a message by name:
java examples.addressbook.GetEmail uan:\\nameserver\myname username 

Running GetName to get a user name by message:
java examples.addressbook.GetName uan:\\nameserver\myname "hello world" 

Running MigrateBook to migrate the address book to another host:
1. Start another theater at machine2:port_num
   java -Dport=port_num wwc.messaging.Theater
2. Running MigrateBook to migrate AddressBook to machine2:port_num
   java examples.addressbook.MigrateBook 
     uan:\\nameserver\myname  rmsp://machine2:port_num/anotherPlace 