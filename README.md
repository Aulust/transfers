My assumptions for this task:

1. Data is supposed to be stored in in-memory database, since otherwise transfer could be made by just using ReentrantLock on both values
2. Locking on two rows simultaneously in one transaction could be achived (by locking table or looping with lock in serializable isolation level), but will lead to significant performance degradation

So, by changing only one row in transaction with per row locking (one with subtraction) and delaying processing the other, a better performance could be achieved
RabbitMQ or other message broker might be used for queuing and processing of tasks (database is not very good choice), while in this example it is just thread poll executor

There is still a problem with possible loss of messages between broker and application. To mitigate this problem, thread republishes lost tasks to broker.

Build project with ```mvn clean package```

Run with ```java -jar -DConfigFile=src/config/application.properties target/transfers.jar```
