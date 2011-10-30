Not-So-Simple "Hello, World" with BlueEyes
==========================================

This project demonstrates the [BlueEyes](https://github.com/jdegoes/blueeyes) Scala framework, Akka Actors and Refs,
and Heroku; it can also be run on any suitably equipped server.

The BlueEyes framework encapsulates Netty, which acts as a web container. The HelloBlueEyes application services are
defined by the BlueEyes DSL, including services for HTML GET, JSON GET and POST, and MongoDB. HelloJsonServices acts as a
front-end for a hierarchy of Akka Actors, including Hanuman (the mythological monkey god), MonkeyVisor (an Akka
Supervisor) and Monkeys. Monkeys generate pages of random text. Because there might be several users, each running
their own simulation, Hanuman pairs user simulations with MonkeyVisor instances. MonkeyVisors compare the text generated
by the Monkey instances that they supervise to a target document, and Monkeys are scored on how closely their random
text matches the target.

N monkeys generate a page (1000 characters) of random text per 'tick', in the hope that they can match some portion of a document.
Monkeys are trained by passing in a map of character->probability. An large number of monkeys typing long enough should
eventually reproduce any given document. To start the process, a client first requests a new simulation ID from
HelloJsonServices, and then uploads the document that the monkeys are to attempt replicate. The process is terminated
when a specified number of 'ticks' pass, or the document has been replicated.

Monkeys send a status update message to its MonkeyVisor after generating each page of text. MonkeyVisors summarize the
results of the Monkeys that they supervise in messages sent to the Hanuman actor, which serves as the interface to
HelloJsonServices. Akka Refs are passed into each Actor, which sets/gets result values atomically using shared-nothing
state. Clients can to query the results up to the previous (or maybe the next) tick. Status within the active tick is
opaque.

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
