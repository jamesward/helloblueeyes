Simple "Hello, World" with BlueEyes
===================================

Run locally
--------------

1. Clone this [git repo](https://github.com/jamesward/helloblueeyes).

2. Set the environment variables:

        export PORT=8585
        export MONGOLAB_URI=mongodb://127.0.0.1:27017/hello

3. Compile the app and create a start script:

        sbt stage

4. Run the app:

        sbt run


Run clients against local service instances
----------------------------------------------

* HTML service - point a web browser to [http://localhost:8585](http://localhost:8585)
* Hello service - point a web browser to [http://localhost:8585/hello](http://localhost:8585/hello)
* JSON service - point a web browser to [http://localhost:8585/json](http://localhost:8585/json)
* To see the actual JSON:

        curl --header "Content-Type:application/json" http://localhost:8585/json

* Mongo service - point a web browser to [http://localhost:8585/mongo](http://localhost:8585/mongo)


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

