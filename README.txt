Current version of JDK: Version 8

How to compile: 

	javac -cp "$CLASSPATH:/PATH-TO-FILE/TenCompanies/json_simple-1.1-all/lib/json_simple-1.1.jar" GiveMeTenCompanies.java 

	This program uses an external jar file that just needs to be added to the classpath on compile and run time. 

How to run:

	java -cp "$CLASSPATH:/PATH-TO-FILE/TenCompanies/json_simple-1.1-all/lib/json_simple-1.1.jar" GiveMeTenCompanies Profile.json 

	This includes the classpath to the json jar file and the executable file. the "Profile.json" file is the input file that the program will read. This can be replaced by any candidate profile with similar attributes.


The two main priorities this script takes into consideration are the candidate's interests and location. The interests are turned into tags which when searched through the API, will return the most popular company for that tag. The list of the companies will be filtered through their locations to make sure that the candidate is either within the city or that the company is within the range of cities the candidate is willing to travel to. 

