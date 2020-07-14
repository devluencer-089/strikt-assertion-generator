# strikt-assertion-generator
Generate beautiful strikt assertions for Kotlin data classes



#### Debugging the annotation processor:

Run tests without a gradle daemon using the following command:
```shell script
./gradlew clean check --no-daemon -Dorg.gradle.debug=true -Dkotlin.compiler.execution.strategy="in-process" -Dkotlin.daemon.jvm.options="-Xdebug,-Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n"
```

Then attach to the JVM on localhost port 5005 using teh following command line arguments:
```shell script
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
```
