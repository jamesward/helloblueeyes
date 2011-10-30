Not-So-Simple "Hello, World" with BlueEyes
==========================================

This project demonstrates the [BlueEyes](https://github.com/jdegoes/blueeyes) Scala framework, Akka Actors and Refs, and Heroku; it can also be run on any
suitably equipped server.

The BlueEyes framework encapsulates Netty, which acts as a web container. The HelloBlueEyes application services are
defined by the BlueEyes DSL, including services for HTML GET, JSON GET and POST, and MongoDB. The JsonService acts as a
front-end for a hierarchy of Akka Actors, including Hanuman (the mythological monkey god), MonkeyVisor (an Akka
Supervisor) and Monkeys. Monkeys generate pages of random text. MonkeyVisors compare the text to a target document, and
Monkeys are scored on how closely their random text matches the target.

Each Monkey provides a status update after generating a page of text, and MonkeyVisors
summarize the results of the Monkeys that they supervise in order to present it to the Hanuman actor, which serves as
the interface to the JsonService. Akka Refs are passed into each Actor, which sets/gets result values atomically using
shared-nothing state.

Run locally
--------------

1. Clone this [git repo](https://github.com/jamesward/helloblueeyes).

2. Set the environment variables:

        export PORT=8585
        export MONGOLAB_URI=mongodb://127.0.0.1:27017/hello

3. Compile the app and create a start script:

        sbt stage

4. Run the app:

        target/start


Run clients against local service instances
----------------------------------------------

* Hello service - point a web browser to [http://localhost:8585/hello](http://localhost:8585/hello)
* HTML service - point a web browser to [http://localhost:8585](http://localhost:8585)
* Mongo service - point a web browser to [http://localhost:8585/mongo](http://localhost:8585/mongo)
* JSON service (without the correct `Content-Type header` there will be no response from the JSON service).

        curl --header "Content-Type:application/json" http://localhost:8585/json


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
