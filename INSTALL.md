Install instructions
=============
1. Grab play from http://www.playframework.org/ and install it.

   Basically it is: unzip, add to $PATH, run it.
   Or follow these instructions: http://www.playframework.org/documentation/2.0.2/Installing
2. Set up a mysql-database for this project, e.g.:

   `CREATE DATABASE tawusel;
   GRANT ALL ON tawusel.* TO tawusel@localhost IDENTIFIED BY 'pass';`
   (if you want to use other credentials, change file `conf/application.conf` accordingly)
3. In the project's root folder (i.e. here) run `play run`
4. Point web browser to http://localhost:9000/

