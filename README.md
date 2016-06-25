# DzenlabSpringTest

Проект аналогичный DzenlabTest с той лишь разницой, что используется Spring.
В работе использовались:
* Wildfly 10.0
* Maven 3.0.4
* JUnit 4.12
* Spring 4.3
* Netbeans 8.1
* h2database 1.4.192

Для сборки необходимо:
* Clone|download проект на локальную машину
* Перейти в каталог проекта
* собрать: mvn clean test package
* На wildfly сервере создать Non-XA DataSource с jndi name - java:/DzenlabDS (h2database)
* Задеплоить target/DzenlabSpringTest-1.0-SNAPSHOT.war
* Перейти на страницу: http://localhost:8080/DzenlabTest/ 
