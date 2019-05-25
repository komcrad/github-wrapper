# github-wrapper

A restful api for getting stuff from github api

## Requirements
[lein](https://leiningen.org): a build tool for Clojure


## Usage
To produce a standalone executable jar file:

    $ lein uberjar

To run the server you'll need a github api key which will be referred to as $github-key  
You can either run the jar file with

    $ java -jar /path/to/jar.jar

or you can just run

    $ lein run $github-key

Once you have it running you can use curl to get github user information

    $ curl localhost:8080/followers/komcrad

I also have a demo server running the latest version:

    $ curl https://hypofluid.com/followers/komcrad

## License

Copyright © 2019 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
