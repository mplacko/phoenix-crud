# phoenix-crud
An example for explaining how to work with HBase/Phoenix JDBC Thin Client – CRUD.

### HOW TO CONFIGURE THE PROJECT

## Building and Running

### Build
To build the application it is required to have this installed:
- `Java 9`
- `Maven 3.x`

Then just run this:
```sh
mvn clean install assembly:single
```

### Run
- `$ su <user>`
- `$ cd /home/<user>`
- `$ chmod 770 ./hbase/phoenix-crud-0.0.1-SNAPSHOT-jar-with-dependencies.jar`
- `$ chown <user>:<user> ./hbase/phoenix-crud-0.0.1-SNAPSHOT-jar-with-dependencies.jar`
- `$ kinit -kt /etc/security/keytabs/<user>.keytab <user>`
- `$ java -jar ./hbase/phoenix-crud-0.0.1-SNAPSHOT-jar-with-dependencies.jar "jdbc:phoenix:thin:url=https://<pqs.endpoint>:8765;serialization=PROTOBUF;authentication=SPNEGO;principal=<hdfs_user>@<krb_realm>;keytab=/etc/security/keytabs/<hdfs_user>.keytab"

```sh
Result
Connection created
Dropping tables
Creating tables
Populating tables
*** CREATING ORDER***
Found 1 record(s) in table ORDER
*** RESULTS ***
SELECT * FROM ITEM
ITM001 | Book | 3
ITM002 | Pen | 2
ITM003 | Soap | 3
ITM004 | Shampoo | 5
ITM005 | Phone | 5
ITM006 | Charger | 5
SELECT * FROM CUSTOMER
CU001 | John | foo
CU002 | Angel | faa
CU003 | David | soo
CU004 | Robert | laa
CU005 | James | naa
SELECT * FROM "ORDER"
OR001 | CU001 | 2023-05-20 14:45:44
SELECT * FROM ORDER_LINE_ITEM
OR001 | ITM001 | 1
OR001 | ITM002 | 1
OR001 | ITM003 | 1
```