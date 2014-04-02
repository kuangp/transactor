Running examples.ping

1. Start the naming server
2. Start a theater at host1:port
3. Run examples.ping.EchoAgent at another host:
     java -Duan=<uan_addr> examples.ping.EchoAgent <target_ual>
   In this example, the target_ual should be rmsp://host1:port/id
4. Run examples.ping.Ping by providing the uan of the EchoAgent
     java examples.ping.Ping <uan_addr>
