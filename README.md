Simple "Hello, World" with BlueEyes
===================================

This project contains four sample services built with BlueEyes:

* HTML service (HelloHtmlServices) - Shows basic request handling in Blueeyes with HTML output
* HTML service with startup/shutdown hooks (HelloStartupShutdownServices) - Shows how to add startup and shutdown hooks
* JSON service (HelloJsonServices) - Shows how to output basic JSON in a HTTP GET request
* Mongo service (HelloMongoServices) - Shows how to connect to a MongoDB instance and have GET and POST methods for retrieving and saving JSON data


The Really Scalable Architecture
--------------------------------

The JSON and Mongo Services are intended to show how to create really scalable web applications.  To do this the application is split into two pieces, the client and the server.  The client is fully HTML and JavaScript while the server is stateless services that produce and consume JSON.  This approach allows the two pieces to be scaled independently.  Scaling the client tier is as simple as putting all of the HTML, JavaScript, CSS, and images on a Content Delivery Network (CDN).  Scaling the server is simply a matter of adding additional servers behind a load balancer.

However, this architecture does present a dilemma.  Most browsers today do not allow cross-origin requests for data.  Or more specifically they don't allow access to the response data of cross-origin requests.  There are several workarounds for this including JSONP and iframes with subdomains.  The method for making cross-origin requests that we chose for this example is to actually not make cross-domain requests, yet still serve most of the static content from another origin/domain (which could be a CDN).  To make development easy, this project includes the BlueEyes server and a Netty-based static file server.  Let's follow a request through the system to see how it works.

If you are running locally (as outlined below) you will have the example JSON back-end service running on:

    http://localhost:8080/json

If you make a get request to that URL with a "Content-type:application/json" HTTP request header you will get some JSON data back.  But in our case we want an HTML page using JavaScript and jQuery to make that request.  So if you make a request to that URL without asking for JSON you will get back a thin HTML page that loads jQuery and a custom JavaScript file from the static file server.  The URLs will be:

    http://localhost:9090/jquery-1.7.min.js
    http://localhost:9090/hello_json.js

So by loading those JavaScript files in a thin page served from the same origin as our JSON services, the JavaScript can now make requests (and read responses) from the back-end server.  So within the context the thin page served by http://localhost:8080/json static assets can be loaded from another origin/domain (http://localhost:9090/) but then make data requests to the back-end services (http://localhost:8080).  Now in production replace http://localhost:8080 with http://www.foo.com (running on a scalable cloud application provider, like Heroku).  And replace http://localhost:9090 with http://cdn.foo.com (running on a CDN provider like Amazon CloudFront or Akamai).  Now you have a very lightweight, very fast, and independently scalable back-end server that talks JSON plus a very fast, edge cached service for all of the static assets.  It's Client/Server all over again, this time with JavaScript/JSON over HTTP.


Run Locally
-----------

1. Clone this [git repo](https://github.com/jamesward/helloblueeyes). // todo: fix url

2. Compile the apps

        sbt stage

3. Set the PORT environment variable for the static content server:

        export PORT=9090

4. Start the static content server:

        target/start net.interdoodle.example.HttpStaticFileServer

5. Set the environment variables for the app server:

        export PORT=8080
        export MONGOLAB_URI=mongodb://127.0.0.1:27017/hello
        export CONTENT_URL=http://localhost:9090/

6. Start the app server:

        target/start net.interdoodle.example.AppServer


Example Endpoints
-----------------

* HTML service - point a web browser to [http://localhost:8080](http://localhost:8080)
* Hello service - point a web browser to [http://localhost:8080/hello](http://localhost:8080/hello)
* JSON service - point a web browser to [http://localhost:8080/json](http://localhost:8080/json)
* To see the actual JSON for the sample JSON service:

        curl --header "Content-Type:application/json" http://localhost:8080/json

* Mongo service - point a web browser to [http://localhost:8080/mongo](http://localhost:8080/mongo)
* To see the actual JSON for the Mongo service:

        curl --header "Content-Type:application/json" http://localhost:8080/mongo


Host the static files on a CDN
------------------------------

// todo


Host the static files on a Heroku app
-------------------------------------

// todo


Run the BlueEyes Server on Heroku
---------------------------------

1. Install the [Heroku command-line client](http://toolbelt.herokuapp.com/)

        heroku login

2. Create an app on Heroku:

        heroku create --stack cedar --addons mongolab:starter

3. Set the CONTENT_URL environment variable to the URL of the static files (feel free to use cdn-example.interdoodle.net or your own as described above):

        heroku config:add CONTENT_URL=http://cdn-example.interdoodle.net/

4. Push the app to Heroku where it will automatically be (re)built and run:

        git push heroku master

5. Run the app in your browser:

        heroku open

You can also manually run the sbt console on Heroku:

    heroku run sbt console

