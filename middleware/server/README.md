# Physical Bits server

The middleware contains a small set of tools that allow to compile, debug, and transmit the programs to the Arduino board through a serial connection.

In order for the web-based IDE to interact with these tools the middleware exposes a REST API that allows to: connect and disconnect from the robot; compile, run, and install programs; and a websocket endpoint that notifies the clients of state changes in the robot, allowing them to access sensor and global data (among other things).

These tools were originally prototyped using [Squeak](https://squeak.org/), an open source version of [Smalltalk](https://en.wikipedia.org/wiki/Smalltalk). We used Smalltalk to build the first prototypes mainly due to our familiarity with the language. But we eventually had to face some of its limitations and so we decided to port the entire Smalltalk codebase to [Clojure](https://clojure.org/). This is the result of that port.

## Quick start

We are using the [leiningen](https://leiningen.org/) for development. Open a REPL by running:

    $ lein repl

Then start the server by evaluating the `start` function.

    $ user=> (start)

You can then open a browser pointing to the IDE by running the `open-browser` function.

    $ user=> (open-browser)

If everything goes well you should see something like this:

![repl](/docs/img/repl.png)

## Compilation

To build a jar file you can close the REPL and execute:

    $ lein uberjar

This command will generate a jar file in the `/target/uberjar/` directory. You can then start the server by running the jar file:

    $ java -jar target/uberjar/middleware-x.y.z-standalone.jar [args]

Make sure to specify valid arguments so that the server knows where to locate the uzi libraries as well as the web resources.

The options are:

    -u, --uzi             PATH      Uzi libraries folder (default: "uzi")  
    -w, --web             PATH      Web resources folder (default: "web")
    -s, --server-port     PORT      Arduino port name (optional)
    -o, --open-browser              Open a browser automatically (optional)
    -h, --help

## Dependencies

* [Leiningen](https://leiningen.org/)
* [Java](https://openjdk.java.net/)
