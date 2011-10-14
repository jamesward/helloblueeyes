Simple "Hello, World" with BlueEyes
===================================

To run locally:
---------------

1. Clone this repo

2. Create a BlueEyes conf file:

        echo -e "server {\n  port = 8585\n  sslEnable = false\n}" > server.conf

3. Compile the app and create a start script:

        sbt stage

4. Run the app:

        target/start


To run on Heroku:
-----------------

1. Clone this repo.

2. Have the [Heroku client](http://toolbelt.herokuapp.com/) installed and set up (ssh keys).

        heroku login

3. Create an app on Heroku:

        heroku create -s cedar

4. Push the app to Heroku; it will automatically be (re)built and run:

        git push heroku master


You can run the sbt console on Heroku:

    heroku run sbt console


To run a client:
----------------

1. You can access a local instance of the services:
   1. Hello service:

    http://localhost:8585/hello

   2. HTML service:

    http://localhost:8585/

   3. JSON service (without the correct `Content-Type header` there will be no response from the JSON service).

    curl --header "Content-Type:application/json" http://localhost:8585/json


2. Access [Mike Slinn's](http://micronauticsresearch.com) Heroku instance:
   a. Hello service:

    http://strong-galaxy-4334.herokuapp.com/hello

   b. HTML service:

    http://strong-galaxy-4334.herokuapp.com

   c. JSON service (without the correct `Content-Type header` there will be no response from the JSON service).

    curl --header "Content-Type:application/json" http://strong-galaxy-4334.herokuapp.com/json

