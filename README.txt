=====================================================================================

		Computer Networks Lab - Or Brand, Dor Samet

=====================================================================================


Welcome to our server,

A) Steps for success:
   ================== 
	
	1. compile.bat compiles our java files to binary files, and copies the "static" directory to the serverroot directory.
	2. run.bat runs the server on the port given by the config.ini file

	Be aware that the server relies on its "static" files that will be in the serverroot directory. The compile.bat file copies the files to this directory. 
	Note that the usage of this server relies on the copying of these files from its directory. 

B) What has been implemented:
   ==========================
   	
   	1. Base classes
   		a. IParser - implemented by the ConfigFile class, and is used as a base for classes that parse files.
   		b. IClientRequest - implemented by all client requests, and are used in the requests factory in order to determine abstractly which requests we are dealing with, and in order to return response.
   		c. RequestFactory - Determines which type of request we are dealing with, and returns the appropriate response. Note that in the end, since all requests implement the IClientRequest interface, the factory returns that type. In case of an error request, the factory will return the appropriate error response. It also checks the validity of the request.
   		d. ConfigFile - Singleton config file that holds the information that the config file has.
   		e. WebServer - This is the class that runs the server from the config file, and handles the threadpool. It starts each HttpRequestHandler with a callback that runs when the handler finishes running. The callback holds a pointer to the function Manager() of the ThreadPool
   		f. HTTPRequestHandler - For each incoming request, processes the request body, and handles its response according to the determination process given by the request factory.
   		g. ThreadPool - handles all incoming requests in two separate queues: the running queue, which holds up to 10 HTTPRequestHandlers, and the waiting queue which can hold more. 
   		h. Tools - A static library of methods used across the app.
   		i. MainServer - The entry point of the web server app.

   	2. Http Request classes
   		a. HeadRequest - A class that doubles as the handler of HEAD request and response handler, as well as a polymorphic base for the rest of the classes. Since all classes return headers, and parse requests, it makes sense that they all inherit some methods from the HeadRequest.
   		b. TraceRequest - A class that handles TRACE requests. Inherits methods from HeadRequest.
   		c. GetRequest - A class that handles GET requests. Inherits methods from HeadRequest.
   		d. PostRequest - A class that handles POST requests. Inherits methods from GetRequests, since its response (for now) is almost identical to that of GET. The only two differences are in the variable processing (which is in the POST content), as well as in the response of params_info.html. 
   		e. ErrorRequest - A class that handles all types of errors the server can return (such as bad request, not found, internal server error, forbidden request, and not implemented). The server returns the appropriate error headers, as well as an HTML body to allow for a better user experience. We chose this type of implementation, since all of our errors essentially did the same thing, but with variable headers and content.

C) Main Flow:
   ==========

 	The server starts up in the MainServer class, which opens up the ConfigFile singleton, and starts up the WebServer class. The ConfigFile class reads its information from the config.ini file. The WebServer class starts the server listening on the port given the config file, and the ThreadPool. When receiving a connection, creates a handler that it sends to the ThreadPool. 
 	The ThreadPool and the handler both can manage the ThreadPool (given the right context), and as such the ThreadPool can only have 10 handlers running at a time, otherwise they go to the waiting queue, and wait there until one of the 10 handlers that are running finish. 
 	The handler then processes the request by sending it to the request factory, and by determining which type of request we deal with, send the appropriate response.