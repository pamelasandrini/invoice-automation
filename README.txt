Standalone Java 8 with Maven


------------------------ For devs ------------------------ 
To generate the jar file run as: mvn clean compile assembly:single

To execute or debug on eclipse run as: mvn clean install

------------------------ For users ------------------------ 

Before executing the program, please make sure you have Java 8 (1.8) or any later version installed in your machine. To check that you can run the below command on prompt
java -version

To run the program just double click on the jar file generated 

Or

You can run it on prompt to see the logs in action, just go to the path where the jar file is located and run the command below
java -jar generatedFileName.jar

The program will open a window and the user has to provide the directory where is the Invoice file(s) and the billing spreadsheet file.
Once the directory is provided then click on "Process" button, the program will start to take all the info from the Invoice files and validating against the spreadsheet.
If all the Invoice info is correct then the program will add the WOI info into the spreadsheet in the corresponding tab.
The program generates a log file in the same directory provided in the beginning. You can check this log file to see all the Invoice files that was inserted successfully and all the files that was not inserted. Also you can check any error that occured during the processing.