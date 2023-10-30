#jdk版本
FROM openjdk:8-jdk-alpine

#挂载目录
VOLUME /tmp

#将jar包添加到容器中并更名为demosw.jar
ADD /cake-dingtalk-sso-server/target/cake-dingtalk-sso-server.jar cake-dingtalk-sso-server.jar

#docker运行命令
ENTRYPOINT ["java","-Dspring.profiles.active=test","-jar","/cake-dingtalk-sso-server.jar"]