# Assignment 2

This is the "read me" documentation for assignment 2. It explains how to execute the application and contains a guide to the source code.

## How to execute the application

`application.properties` file and database initial data needs to be prepared before execution.

__Prepare application.properties__

The application requires `application.properties` file, which contains configuration parameters to mysql database, in order to run. Before running the system, please fill out the `application.properties` in the `distribution` folder to properly reflect the configuration of your MySQL database.

A sample `application.properties` file content will look like:

```
mysql.port=3306
mysql.user=dbuser
mysql.pass=dbpass
mysql.params=
```

__Load initial data__

The application assumes the database is in the initial state as described in the `A2-Installation-2015.pdf` as distributed with the application package from BlackBoard. In summary, it assumes `orderinfo` and `inventory` database has been imported. If that's not the case, please follow the `A2-Installation-2015.pdf` to do so first.

Next, the `schema.sql` in the `distribution` folder needs to be imported into the MySQL database. You can do so by executing:

```
mysql -u $dbuser -p $dbpass < schema.sql
```

The above command assumes `$dbuser` is the username and `$dbpass` is the password. If more configuration options are needs, please consult [MySQL online manual](https://dev.mysql.com/doc/refman/5.7/en/mysql-batch-commands.html).

__To execute on Windows(x64)__

Double click the `app.exe` inside the `distribution` folder.

__To execute on Mac OS X__

```
chmod +x distribution/runApp.sh
./distribution/runApp.sh
```

__To execute manually__

Referencing the following variables:
- `Java8_JRE_Home`: the JRE root directory for __Java8__
- `PROP_DIR`: the directory where you have placed `application.properties`

```
Windows: $Java8_JRE_Home/bin/java.exe app.jar $PROP_DIR
Mac Os X: $Java8_JRE_Home/bin/java app.jar $PROP_DIR
```

For example, if my Java8 JRE runtime is placed in `C://Java` and I have put a `application.properties` file in `D://`, I will execute:

```
C://Java/bin/java.exe app.jar D://
```

## Brief introduction into source code

__Overview__

The application is build with `Java8 SDK`. It's a desktop application utilizing `JavaFX` GUI technologies.

__Building the source code__

The application uses [Apache Maven 3.3.3](https://maven.apache.org) as its build system. The application can be build with command:

```
mkdir workspace && cd workspace
git clone https://github.com/jeremyh/jBCrypt.git
cd jBCrypt
mvn clean javadoc:jar source:jar install
cd ..
git clone https://github.com/davidiamyou/17-655-A2.git
cd 17-655-A2
mvn clean install
```

The above commands downloads an external dependency (not available in Maven Central) from Github and installs it into your local Maven cache. Then it downloads this project's source code and builds it.

The file jar file can be found in `17-655-A2/app/target/jfx/app/app-1.0-jfx.jar`. Before executing this jar file, please consult the previous section for preparation work.

__Diving into the source code__

The project includes five modules `app`, `common`, `inventory`, `order`, `shipping`:
- `common`: Contains application infrastructure (i.e. security & session, IOC container) and common utilities (i.e. modal UI window).
- `inventory`: Contains views and logic belonging to the inventory app.
- `order`: Contains views and logic belonging to the order app.
- `shipping`: Contains views and logic belonging to the shipping app.
- `app`: Contains security logic, authentication UI, app selection UI and bootstraps the three apps above.

The main class can be located at `app/src/main/java/a2/A2Application.java`

__Notable conventions__

- `**FxViewController` are the view controllers handling JavaFX view logic.
- `**Controller` (excluding the `**FxViewController`) are the business controllers handling business logic (i.e. validation, data transformation, etc.,)
- `**Dao` are the data access objects handling communication with database
- `**.fxml` are the JavaFX XML files that defines the view
