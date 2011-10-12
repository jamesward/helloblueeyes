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

2. Have the [Heroku client](http://toolbelt.herokuapp.com/) installed and set up (ssh keys, logged in, etc).

3. Create an app on Heroku:

        heroku create -s cedar

4. Push the app to Heroku:

        git push heroku master


To run a client:
----------------

You can access a local instance via:

        curl --header "Content-Type:application/json" http://localhost:8585/

Without the correct `Content-Type header` there will be no response.
Access the heroku instance via:

        curl --header "Content-Type:application/json" http://strong-galaxy-4334.herokuapp.com/

The Heroku remote repository for this project is git@heroku.com:strong-galaxy-4334.git
Mess around with [sbt on Heroku](http://devcenter.heroku.com/articles/scala#console) with this:

        heroku run sbt console
