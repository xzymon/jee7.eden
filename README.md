# jee7.eden

Dedicated server: JBoss EAP 7.0.0
JDK: 1.8

How to build server artifact from shell console:
1. Go to 'eden' module
2. Run: gradle clean build
Server artifact (EAR) will be produced in ear/build/libs

How to build remote client artifact from shell console:
0. (after successful fresh build of server artifact)
1. Go to 'fx' module
2. Run: gradle clean build
3. Run: gradle fatJar
Remote client (JAR) will be produced in fx/build/libs (its the heavier of JARs)
