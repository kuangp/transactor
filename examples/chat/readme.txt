Running examples.chat

1. Start the name server

2. Run examples.chat.Speaker at two hosts. For example:
java -Duan=uan:\\nameserver\id1 examples.chat.Speaker Jason
and 
java -Duan=uan:\\nameserver\id2 examples.chat.Speaker Jeff

3. Run examples.chat.Chat
java examples.chat.Chat uan:\\nameserver\id1 uan:\\nameserver\id2