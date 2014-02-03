Snabb
=====
Pom.txt does not work correctly as of 26.11.2013 but you can try if you are brave.

To build program first download and install LEAP SDK into local directory:

mvn org.apache.maven.plugins:maven-install-plugin:2.3.1:install-file \
    -Dfile=PATH_TO_JAR \
    -DgroupId=com.leapmotion.leap -DartifactId=leapMotion \
    -Dversion=1.0.0 -Dpackaging=jar

Afterwards add Leap Native Library to the java build path

--------------------
Notes
------------------
### Errors
 * Unsatisfied Link Error can happen when the javaFX runtime is not correct. Then include your own found under Java/jdk1.7.0_45/jre/lib