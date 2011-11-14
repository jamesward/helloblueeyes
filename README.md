Simple "Hello, World" with BlueEyes
===================================

This project contains four sample services built with BlueEyes:

* HTML service (HelloHtmlServices) - Shows basic request handling in Blueeyes with HTML output
* HTML service with startup/shutdown hooks (HelloStartupShutdownServices) - Shows how to add startup and shutdown hooks
* JSON service (HelloJsonServices) - Shows how to output basic JSON in a HTTP GET request
* Mongo service (HelloMongoServices) - Shows how to connect to a MongoDB instance and have GET and POST methods for retreiving and saving JSON data



Run locally
--------------

1. Clone this [git repo](https://github.com/jamesward/helloblueeyes).

2. Compile the apps

        sbt stage

3. Set the PORT environment variable for the static content server:

        export PORT=8080

4. Start the static content server:

        target/start net.interdoodle.example.HttpStaticFileServer

5. Set the environment variables for the app server:

        export PORT=8585
        export MONGOLAB_URI=mongodb://127.0.0.1:27017/hello
        export CONTENT_URL=http://localhost:8080/

6. Start the app server:

        target/start net.interdoodle.example.AppServer


Run clients against local service instances
----------------------------------------------

* HTML service - point a web browser to [http://localhost:8585](http://localhost:8585)
* Hello service - point a web browser to [http://localhost:8585/hello](http://localhost:8585/hello)
* JSON service - point a web browser to [http://localhost:8585/json](http://localhost:8585/json)
* To see the actual JSON for the sample JSON service:

        curl --header "Content-Type:application/json" http://localhost:8585/json

* Mongo service - point a web browser to [http://localhost:8585/mongo](http://localhost:8585/mongo)
* To see the actual JSON for the Mongo service:

        curl --header "Content-Type:application/json" http://localhost:8585/mongo


Run on Heroku
----------------

1. Clone  [git repo](https://github.com/jamesward/helloblueeyes).

2. Have the [Heroku client](http://toolbelt.herokuapp.com/) installed and set up (ssh keys).

        heroku login

3. Create an app on Heroku:

        heroku create --stack cedar --addons mongolab:starter

4. Push the app to Heroku; it will automatically be (re)built and run:

        git push heroku master


You can also manually run the sbt console on Heroku:

    heroku run sbt console

