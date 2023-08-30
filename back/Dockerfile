FROM maven:3.8.1
COPY . /usr/share/maven/src
WORKDIR /usr/share/maven/src

RUN mvn package -Dmaven.test.skip -f pom.xml

FROM tomee:8.0
COPY ./utils/postgresql-42.5.3.jar /usr/local/tomee/lib/
COPY ./utils/system.properties /usr/local/tomee/conf/
COPY --from=0 /usr/share/maven/src/target/*.war /usr/local/tomee/webapps/togepic.war
