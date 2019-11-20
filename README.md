# LoggingFilter-Java-Spring
Logging filter to log all the requests in java-Spring

This is a java project which is used to log all types of requests such as POST, GET, PUT and DELETE etc.
Main file is RequestResponseLoggingFilter.java which is responsible for loggig. 

HttpServletCacheWrapper.java is responisble for getting the requestBody of the request. 
HttpServletResponseCopier.java and ServletOutputStreamCopier.java are responsible for getting the ResponseBody of the request. 

HttpServletCacheWrapper.java and HttpServletResponseCopier.java are two different approaches to get the requestbody and responsebody respectively. I could have made both the files same with minor changes but here i have used two different apporaches for better learning.

LogControllerConfiguration.java is the configuration file which is necessary because i am using MongoDB here and configuration file has everything required in the DB.

LogControllerConfigurationRepository.java is the file for the connection of database.

To run this first you need to put the mongoDB in the database with the name as "log-controller-configuration" and dependency(maven) of this Logging project in the project where you need to log. I have aslo added an extra feature for cacheService.It will first time take the data and store it into the cache, so whenever it has to look again into the database, it will simply look for that content into the cache not the database, making it time efficient. It will also clean the cache after every 30 minutes, in case if you made changes into the database.

LocalLogControllerConfigurationRepository.java and LoggingGeneralConfigurations.java are the files which you need to put them into the project where you need this logging feature.
LoggingGeneralConfigurations.java has BEANs in it.
