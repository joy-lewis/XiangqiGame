README for the GameServer Project (Adapted for Xiangqi Implementation)

This project serves as an introduction to the implementation task (HA2 SWTPP).

The task should be delivered as a servlet on an Apache Tomcat web server. In Eclipse, this is referred to as a "dynamic web project".

The following provides an overview of the provided resources. Afterwards, there are some explanations about the functioning of the servlet. Additionally, the comments in the existing files should be taken into account as they contain additional hints.

## 1) Project Overview

- `GameServer`(possible to have `-Full` or `Vorgabe` folder depending on the variant)
 The main project folder containing the Eclipse configuration and project files, as well as all the folders described below.
   
   - src
      This folder contains all the Java source code files, divided into packages. In Eclipse, the packages are visible as a whole (separated by dots). In the file system, they correspond to subfolders.
   
      - `de.tuberlin.sese.swtpp.gameserver.control`
        Package for the controller classes of the class model.
         
      - `de.tuberlin.sese.swtpp.gameserver.model`
         Package for the entity classes of the abstract class model, which represents the actual data model of the server without a specific game implementation.

      - `de.tuberlin.sese.swtpp.gameserver.model.crazyhouse`
         Package for the entity classes of the class model, which represents the data model of the specific game.
         
      - `de.tuberlin.sese.swtpp.gameserver.test.crazyhouse`
         This folder contains the jUnit test cases and test suites that need to be adapted by you.
         
      - `de.tuberlin.sese.swtpp.gameserver.swtpp.web`
         This folder contains the GameServerServlet class, which is the central component of the server-side web application. More information about the servlet will be provided later.
         
   - `build`
      This folder contains the compiled classes.
      
   - `WebContent`
     This folder stores the web resources of the project, which are made available by the Apache Tomcat server as a web server. Images, HTML pages, CSS files can be accessed via a URL (depending on the project name, e.g., http://localhost:8080/GameServerServlet/...) with the path within this folder. If the resource is linked within a page on the Tomcat server, a relative path can also be used. e.g. `<a href="meinbild.jpg">Bild</>`
  
## 2) Implementation Task

We have already completed the web functionality in the given project. Interaction with the requests is already possible for all use cases. The GUI (HTML/JavaScript for the client's browser) has also been implemented. The same applies to server management, the abstract game, and a large part of the specific game. You only need to implement some functions in the XiangqiGame class and the corresponding tests (see task sheet). Existing code should not be modified. The following chapters provide orientation and explanation for the parts that have already been completed. 
  
## 3) The Servlet

A servlet is a container for a dynamic web application that can be executed on an application server such as Apache Tomcat. The core of the servlet is the servlet class (in our case, GameServerServlet), which inherits from the HttpServlet class. It is registered in the deployment descriptor (project configuration), which signals to the Apache Web Server that it should provide the servlet for HTTP requests. All user requests to the URL of the servlet (http://localhost:8080/GameServer/GameServerServlet) are forwarded to the servlet class. In the example, the requests are forwarded to the doGet() method of GameServerServlet.

Important:
The servlet class is instantiated once by the server. Regardless of the user, session, browser, etc., you always end up in the same servlet object. The attributes are always visible and are suitable for global variables and data that does not depend on the current state of a user's session.

## 4) The Client-Side

The client-side in this project consists of HTML files, where dynamic content is created with JavaScript. The JavaScript functions send requests to the servlet, which in turn responds with the requested data. The core of the application relies on the libraries from chessboardjs.com (adapted for the respective game), which provide the game board functionality.
