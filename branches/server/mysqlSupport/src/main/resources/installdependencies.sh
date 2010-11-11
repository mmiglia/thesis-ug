#!/bin/bash
mvn install:install-file -Dfile=db4o-7.12.132.14217-all-java5.jar -DgroupId=com.db4o -DartifactId=db4o -Dversion=7.12.132 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=gdata-calendar-2.0.jar -DgroupId=com.google.gdata -DartifactId=gdata-calendar -Dversion=2.0 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=gdata-client-1.0.jar -DgroupId=com.google.gdata -DartifactId=gdata-client -Dversion=1.41.1 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=gdata-core-1.0.jar -DgroupId=com.google.gdata -DartifactId=gdata-core -Dversion=1.41.1 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=google-collect-1.0-rc1.jar -DgroupId=com.google -DartifactId=google-collect -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=org.semanticweb.HermiT.jar -DgroupId=org.semanticweb -DartifactId=HermiT -Dversion=1.2.3 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=org.semanticweb.owl.owlapi.jar -DgroupId=org.semanticweb -DartifactId=owlapi -Dversion=3.0.0 -Dpackaging=jar -DgeneratePom=true


mvn install:install-file -Dfile=org.semanticweb.owl.owlapi.jar -DgroupId=org.semanticweb -DartifactId=owlapi -Dversion=3.0.0 -Dpackaging=jar -DgeneratePom=true
