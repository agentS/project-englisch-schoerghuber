# Anmerkungen

Diese Dokumentation beschreibt hauptsächlich die im Rahmen des Projektes neu eingefügten Änderungen.


# Funktionale Anforderungen

Im Rahmen der Projektarbeit wird das Nachtzugbuchungssystem aus der zweiten und fünften Übung erweitert.
Die Anwendung soll es grundsätzlich ermöglichen Nachtzugverbindungen zwischen zwei Destinationen anzuzeigen und für diese auch Tickets "buchen" zu können. Darüber hinaus wird ein "Entdeckungsmodus" angeboten, mit dem für einen Abfahrtsbahnhof alle Ziele angezeigt werden und entsprechende Tickets gebucht werden können.
Der Benutzer eines Tickets wird mittels E-Mail-Benachrichtigungen über den Status seiner Buchung (reserviert, bestätigt oder abgelehnt) auf dem Laufenden gehalten.

# Architektur

## System

Das Diagramm unten zeigt die Architektur des Systems.
Die Services für das Suchen von Verbindungen zwischen Bahnhöfen (Timetable-Service), für das Buchen von Tickets (Booking-Service), das API-Gateway, welches Clients einen einheitlichen Endpunkt bietet und an das Booking-Service sowie das Timetable-Service delegiert, sowie das Notification-Service werden dabei einem Kubernetes-Cluster betrieben, wobei Minikube als Kubernetes-Distribution verwendet wird.

Das Timetable- und das Booking-Service stellen dabei jeweils eine REST-Schnittstelle zur Verfügung und eine entsprechende Dokumentation mittels OpenAPI zur Verfügung.
Das API-Gateway vereinheitlicht den Zugriff auf die beiden REST-Schnittstellen und stellt ebenfalls eine OpenAPI-Dokumentation bereit, welche aus den beiden OpenAPI-Dokumentationen der Timetable- und Booking-Services automatisch zusammengestellt wird.
Das Booking-Service publisht zusätzlich Buchungen, deren Status (reserviert, bestätigt oder abgelehnt) sich ändert, an eine entsprechende AMQP-Queue.
Der Notification-Service tritt als entsprechender Subscriber ein und erstellt bei Empfang einer Nachricht eine entsprechende E-Mail und sendet diese an den Benutzer, der mit der Buchung assoziiert ist.

Darüber hinaus werden noch die benötigten Infrastruktur-Komponenten, nämlich die PostgreSQL-Datenbank, die Mongo-Datebank, der RabbitMQ-Server und der Jaeger-Server in Kubernetes betrieben.
Sowohl das API-Gateway, der Timetable-Service und der Booking-Service sind mit entsprechenden Tracern implementiert, über welche Aufrufe nachverfolgt werden und an den Jaeger-Service reportet werden können.

Das Booking-Service, das Timetable-Service und das API-Gateway sollen ihre Konfiguration aus Kubernetes-ConfigMaps und -Secrets beziehen.

Die Authentifizierung erfolgt über OAuth und daher über JWTs.
Als Authentifizierungs-Provider wird – auf Anraten von Prof. Heinzelreiter – Okta verwendet.
Im Backend überprüfen sowohl API-Gateway und Booking-Service die JWTs beim Zugriff auf Ressourcen zur Buchung von Tickets.
Das API-Gateway leitet die JWTs an das Booking-Service weiter.

Das Web-Frontend ist mit React und TypeScript realisiert und bietet eine graphische Möglichkeit Verbindungen zu suchen sowie Tickets zu buchen.

![Architektur des Systems](doc/architecture/architecture.svg)

## Ablauf der Kommunikation zur Buchung eines Tickets

Lukas

## Domänenmodelle

### Timetable-Service

Die folgende Abbildung zeigt ein UML-Klassendiagramm des Domänenmodells.
Die Klassen des Domänenmodells sind jeweils mit JPA-Annotationen angereichert und werden daraus folgend mittels JPA persistiert.

Die Klasse `RailwayStation` dient zur Abbildung von Bahnhöfen, welche über eine ID und einen Namen verfügen.
Die Klasse `TrainConnection` stellt eine Zugverbindung dar, welche über eine ID, einen Code zur Identifizierung durch Passagiere und Personal (z.B. NJ466) und eine Menge von Waggons verfügt.

Ein Waggon (Klasse `TrainCar`) hat ebenfalls eine ID, eine Nummer, die innerhalb des Zuges eindeutig ist (z.B. 21) und Passagieren hilft, den Waggon, in welchem sich ihr Platz befindet, zu finden, einen Typ, also ob es sich um einen Schlaf-, Liege- oder Sitzwagen handelt und die Kapazität des Waggons.
Eine Navigation von einem Waggon zu der Zugverbindung, auf welcher dieser eingesetzt wird, ist ebenfalls möglich.

Den Zusammenhang welche Bahnhöfe mittels welcher Zugverbindung erreicht werden können stellt die Klasse `RailwayStationConnection` dar.
Eine Ausprägung dieser Domänenklasse definiert einen Hop einer Verbindung.
Ein Hop verbindet jeweils zwei aufeinanderfolgende Bahnhöfe, z.B. Wien Hauptbahnhof nach Wien Meidling auf der Verbindung von Wien Hauptbahnhof nach Zürich Hauptbahnhof.
Die gesamte Verbindung von z.B. Wien Hauptbahnhof nach Zürich Hauptbahnhof wird als eine Sequenz von Objekten der Klasse `RailwayStationConnection` modelliert: Ausgehend von Wien Hauptbahnhof nach Wien Meidling gibt es pro Zwischenhalt ein Objekt der Klasse `RailwayStationConnection` bis der Ankunftsbahnhof Zürich Hauptbahnhof entspricht.
Der Zielbahnhof eines Hops ist daher der Abfahrtsbahnhof des darauf folgenden Hops.
So ist z.B. Wien Meidlung der Ankunftsbahnhof des Hops von Wien Hauptbahnhof nach Wien Meidling und gleichzeitig der Ausgangsbahnhof des Hops von Wien Meidling nach St. Pölten.
Alle Hops einer direkten Zugverbindung (z.B. von Wien Hauptbahnhof nach Zürich Hauptbahnhof) haben daher das gleiche Objekt vom Typ `TrainConnection` zugeordnet.
Ein Umsteigen (z.B. in Wien Hauptbahnhof auf der Verbindung von Rom nach Berlin Hauptbahnhof) wird durch einen Wechsel des Objekts vom Typ `TrainConnection` signalisiert, wie er eben im Hop mit Ankunftsbahnhof Wien Hauptbahnhof zum nächsten Hop mit Abfahrtsbahnhof Wien Hauptbahnhof stattfindet.

![Klassendiagramm des Domänenmodells des Timetable-Services](doc/timetable/domainModellClassDiagram.svg)

### Booking-Service

Die folgende Abbildung zeigt ein UML-Klassendiagramm des Domänenmodells. Die Klassen des Domänenmodells sind jeweils mit JPA-Annotationen angereichert und werden daraus folgend mittels JPA persistiert. 

Die Klasse `Booking` dient zur Abbildung von Buchungen, welche über eine ID, eine DepartureStationId (Id des Startbahnhofs), ArrivalStationId (Id des Zielbahnhofs), ein Abfahrdatum, einen Wagentyp (Schlaf-, Liege- oder Sitzwagen), die E-Mail-Adresse des Kunden, der die Buchung vorgenommen hat, einen Status (reserviert, bestätigt oder abgelehnt) und eine Menge an Tickets verfügt.
Dabei bezieht sich der Startbahnhof auf den Start und der Zielbahnhof auf das Ziel der Zugverbindung (eine Zugverbindung besteht aus mehreren Hops).

Die Klasse `Ticket` dient zur Abbildung von Tickets, also einen Hop auf der Strecke von Start- nach Zielbahnhof der Buchung, welche eine ID des Startbahnhofs, eine ID des Zielbahnhofs, die ID des Wagens für welchen die Reservierung gilt, die Platznummer innerhalb des Wagens, die ID der Zugverbindung und das Abfahrdatum der Zugverbindung verfügt.
Zusätzlich werden in der Menge mit dem Namen `departureStationIds` die IDs aller Abfahrtsbahnhöfe gespeichert, was die Überprüfung, ob der Zug auf einer Teilstrecke bereits voll ist, wesentlich erleichtert.

![Klassendiagramm des Domänenmodells des Booking-Services](doc/booking/domainModellClassDiagram.svg)

# Timetable-Service

Lukas

# Booking-Service

Lukas

## Spring Cloud OpenFeign

Um die Kommunikation mit dem TimeTable-Service über dessen REST-Schnittstelle auf intuitive weise in die
Geschäftslogik des Booking-Services zu integrieren wurde OpenFeign verwendet. Es handelt sich hierbei um ein Framework, welches die REST-Operationen auf ein Java-Interface über Annotationen bindet.

```java
@FeignClient(name = "timeTableRestClientFeign", url = "${timetable.url}", primary = false)
@Profile("!test")
public interface TimeTableRestClientFeign {
    @RequestMapping(method = RequestMethod.GET, value = "/destinations/from/{originId}/to/{destinationId}")
    List<RailwayStationConnectionDto> getRailwayConnections(@PathVariable("originId") Long originId,
                                                            @PathVariable("destinationId") Long destinationId);

    @RequestMapping(method = RequestMethod.GET, value = "/trainConnection/{id}/cars")
    List<TrainCarDto> getTrainCarsForConnectionId(@PathVariable("id") Long id);

    @RequestMapping(method = RequestMethod.GET, value = "/trainConnection/code/{code}/cars")
    List<TrainCarDto> getTrainCarsForConnectionCode(@PathVariable("code") String code);
}
```
Wie der obige Ausschnitt aus `TimeTableRestClientFeign` zeigt, wird mit der `@FeignClient` Annotation der Ziel-URL aus der Spring-Konfigurationsdatei übergeben und die abzufragenden Endpunkte so definiert, wie sich auch im REST-Service definiert sind.
Mittels der `@RequestMapping` Annotation wird der Endpunkt-URL mit der dazugehörigen HTTP-Methode und den Parametern definiert.
Dieses Interface kann mittels Dependency-Injection nun verwendet werden und OpenFeign löst die Methodenaufrufe zu HTTP-Aufrufen im Hintergrund auf.

### Exception-Translation

Da es vorkommen kann, dass die Methode `getRailwayConnections(originId, destinationId)` den HTTP-Fehlercode 404 zurückliefert, muss dieser zur sauberen Übersetzung in eine businesslogikspezifische Excpetion `NoConnectionsAvailableException` noch angepasst werden.
Um dies zu realisieren, wird Springs AOP-Mechanismus verwendet und die Exceptions mit einem After-Throwing-Advice entsprechend übersetzt.

```java
@Aspect
@Component
public class TimeTableRestClientFeignAdvice {
	@AfterThrowing(pointcut = "execution(public * eu.nighttrains.booking.service.rest.TimeTableRestClientFeign.getRailwayConnections(..))", throwing = "exception")
	public void translateNotFoundExceptionRailwayConnectionLookup(FeignException.NotFound exception) throws Throwable {
		throw new NoConnectionsAvailableException();
	}
}
```

## Circuit-Breaker

Um Ausfälle des Timetable-Services zu erkennen und im Falle eines Ausfalls Timeouts und damit kaskadierende Fehler zu verhindern, werden Zugriffe auf den Timetable-Rest-Service über das Circuit-Breaker-Pattern realisiert.

### Verfügbare Implementierungen

Für Spring Boot gibt es viele verschiedene Implementierungen des Circuit-Breaker-Patterns.
Die [ursprüngliche Implementierung Spring Cloud Netflix Hystrix](https://spring.io/guides/gs/circuit-breaker/) wird mittlerweile nicht mehr aktiv weiterentwickelt, hat aber die breiteste Unterstützung inklusive [einer sehr guten Integration in Spring Cloud OpenFeign](https://spring.io/guides/gs/circuit-breaker/).

Als moderne Alternative gibt es [Spring Cloud CircuitBreaker](https://spring.io/projects/spring-cloud-circuitbreaker), welches über verschiedene Circuit-Breaker-Implementierungen abstrahiert und somit ein einheitliches API mit austauschbaren Implementierungen bietet.
Allerdings ist dieses Projekt erst im April 2019 der Öffentlichkeit vorgestellt worden und daher gibt es noch wenig Dokumentation und nicht allzu viele Integrationen.
So [fehlt z.B. die Integration in Spring Cloud OpenFeign](https://github.com/spring-cloud/spring-cloud-circuitbreaker/issues/3).

Zusätzlich gibt es noch das [Spring-Boot-Starter, welches direkt von Resilience4J bereitgestellt wird](https://resilience4j.readme.io/docs/getting-started-3).
Dieses bietet über Add-Ons Integration in OpenFeign.

Wir haben uns für unsere Implementierung für Spring Cloud CircuitBreaker entschieden, da dieses direkt vom Spring-Team gewartet wird und eine Integration für das moderne Resilience4J bietet.

Die von uns gewünschte Funktionalität zur Benachrichtigung, sobald der Circuit-Breaker wieder in den geschlossenen Zustand geht, bietet leider kein aktuelles Framework in ausreichendem Maße an.

### Spring Cloud CircuitBreaker

Wie gesagt stehen für Spring Clud CircuitBreaker noch nicht viele Code-Beispiele zur Verfügung.
Die Implementierung auf unserer Seite orientiert sich an der [Beispielimplementierung des Spring-Cloud-Teams](https://github.com/spring-cloud-samples/spring-cloud-circuitbreaker-demo/blob/master/spring-cloud-circuitbreaker-demo-resilience4j/src/main/java/org/springframework/cloud/circuitbreaker/demo/resilience4jcircuitbreakerdemo/DemoController.java).

Hierbei ergibt sich die folgende Hierarchie, wie sie auch das folgende Klassendiagramm zeigt:
Das Interface `TimeTableService` spezifiziert die Schnittstelle für den Zugriff auf den Timetable-Service.
Den Fall, dass das Timetable-Service ein entferntes Service ist, implementiert die Klasse `TimeTableServiceRest`.
Dieses wrappt die Zugriffe auf das entfernte Service in Circuit-Breaker-Aufrufe.
Der Zugriff auf das entfernte Service erfolgt mit dem Spring Cloud OpenFeign-REST-Client, welcher aus die Schnittstelle `TimeTableRestClientFeign` generiert wird.

![Klassenhierarchie bei Verwendung des Circuit-Breakers](doc/circuitBreaker/timeTableServiceHierarchy.svg)

Diese Hierarchie bietet den Vorteil, dass der Zugriff den entfernten Service mittels des Circuit-Breakers in den Integrationstests behandelt werden kann, da einfach die Komponente `TimeTableRestClientFeign` durch eine Mock-Implementierung ausgetauscht werden kann.

Die Komponente `TimeTableServiceRest` wrappt die Aufrufe der Methoden der Instanz des generierten Feign-Clients, wie das folgende Listing zeigt.
Die Circuit-Breaker-Instanzen werden dabei mittels der via DI zur Verfügung gestellten Instanz der Klasse `CircuitBreakerFactory` erzeugt.
In den Wrappern wird dabei die Fallback-Methode so implementiert, dass eine entsprechende Ausnahme vom Typ `TimeTableServiceNotAvailableException` ausgelöst wird.

Zusätzlich muss in der Methode `getRailwayConnections` überprüft werden, ob die Exception des Circuit-Breakers durch das Nichtvorhandensein einer Route vom Abfahrtsbahnhof zum Zielbahnhof ausgelöst wurde und diese Exception muss entsprechend weitergegeben werden.

Uns ist bewusst, dass die aspektorientierte Programmierung bessere Möglichkeiten, die weniger Code zur Lösung des Problemes erfordert hätten, bietet.
Allerdings ist es uns aufgrund des aktuellen Zeitmangels durch die vielen Hausübungen und anstehenden Tests und dem anderen Fokus der Übung nicht möglich gewesen, dies wie gewünscht zu realisieren.

```java
@Service
public class TimeTableServiceRest implements TimeTableService {
	private final TimeTableRestClientFeignWrapper timeTableRestClient;
	private final CircuitBreakerFactory circuitBreakerFactory;

	public TimeTableServiceRest(
			@Autowired TimeTableRestClientFeignWrapper timeTableRestClient,
			@Autowired CircuitBreakerFactory circuitBreakerFactory
	) {
		this.timeTableRestClient = timeTableRestClient;
		this.circuitBreakerFactory = circuitBreakerFactory;
	}

	@Override
	public List<RailwayStationConnectionDto> getRailwayConnections(Long originId, Long destinationId) {
		try {
			return this.circuitBreakerFactory.create("getRailwayConnections").run(
					() -> this.timeTableRestClient.getRailwayConnections(originId, destinationId)
			);
		} catch (NoFallbackAvailableException exception) {
			if (exception.getCause() instanceof NoConnectionsAvailableException) {
				throw ((NoConnectionsAvailableException) exception.getCause());
			}
			throw new TimeTableServiceNotAvailableException(exception);
		}
	}

	@Override
	public List<TrainCarDto> getTrainCarsForConnectionId(Long id) {
		return this.circuitBreakerFactory.create("getTrainCarsForConnectionId").run(
				() -> this.timeTableRestClient.getTrainCarsForConnectionId(id),
				(exception) -> { throw new TimeTableServiceNotAvailableException(exception); }
		);
	}

	@Override
	public List<TrainCarDto> getTrainCarsForConnectionCode(String code) {
		return this.circuitBreakerFactory.create("getTrainCarsForConnectionCode").run(
				() -> this.timeTableRestClient.getTrainCarsForConnectionCode(code),
				(exception) -> { throw new TimeTableServiceNotAvailableException(exception); }
		);
	}
}
```

# API-Gateway

Das API-Gateway bietet einen zentralen Punkt, mit dem Clients auf das Timetable- und das Booking-Service zugreifen können.
Das API-Gateway ist mit Spring Cloud Gateway realisiert und in Java programmiert.
Zusätzlich übernimmt das API-Gateway noch die Authentifizierung und leitet die OAuth-Tokens an die Services weiter.

## Gateway-Funktionalität

Das Forwarding wird in einer YAML-Konfigurationsdatei für das Gateway eingestellt.
Dies bietet den großen Vorteil, dass über Spring-Boot-Profiles die für den Betrieb des Gateways in einem gewissen Modus (Entwicklung, Kubernetes-Cluster) je eine maßgeschneiderte Konfigurationsdatei bereit gestellt werden kann.

Die Konfigurationsdatei für den Betrieb im Entwicklungsmodus ist im folgenden Listing zu sehen.
Interessant ist hierbei die Sektion für das Spring-Cloud-Gateway.
In dieser werden drei Routen erstellt, wobei die erste Route für das Timetable-Service dient.
Hier wird mittels eines Pfad-Prädikates festgelegt, dass alle Anfragen, deren Pfad mit `/timetable/` beginnt an den Timetable-Service, welcher unter der URL `http://localhost:8082/` erreichbar ist, weitergeleitet werden.
Zusätzlich wird über Filter festgelegt, dass die URL so verändert wird, sodass der erste Teil des Pfades zwischen den ersten beiden Slashes, welcher für das Timetable-Service immer `/timetable/` lautet entfernt wird.
Dies ist notwendig, da ansonsten die volle URL, welche mit `/timetable/` beginnt, an den Timetable-Service geschickt werden würde, dieser aber das Pfad-Präfix `/timetable/` nicht kennt und daher einen HTTP-Status-Code 404 zurückliefern würde.
Der zweite Filter aktiviert noch einen Circuit-Breaker zum Zugriff auf die Methoden des Timetable-Services.
Dies kann in Spring Cloud Gateway komfortable durch hinzufügen der Dependency `spring-cloud-starter-circuitbreaker-reactor-resilience4j` realisiert werden.

Die Route für den Booking-Service ist prinzipiell gleich wie für den Timetable-Service realisiert.
Da die Zugriffe auf den Booking-Service jedoch eine Authentifizierung verlangen, wird noch ein Token-Relay-Filter hinzugefügt, welcher das OAuth-Token, welches das API-Gateway vom Client erhält, an den Booking-Service weiterleitet.
Dadurch kann der Booking-Service ebenfalls die Berechtigungen überprüfen.

Die dritte Regel wird benötigt, damit die Zugriffe auf die OpenAPI-Dokumente vom API-Gateway an die entsprechenden Services weitergeleitet werden.
Die Funktionalität mit den OpenAPI-Gruppen wird im nächsten Abschnitt genauer beschrieben.
Für jede Gruppe wird das OpenAPI-Dokument vom entsprechenden Service geladen.
Da Swagger-UI jedoch auf die Routen `/v3/api-docs/timetable` respektive `/v3/api-docs/booking` zugreift, welche standardmäßig vom OpenAPI-Gateway behandelt werden, muss eine Regel eingefügt werden, die z.B. `/v3/api-docs/timetable` auf dessen OpenAPI-Pfad `/timetable/v3/api-docs/` abbildet.
Anschließend greift die erste Regel und mappt den Zugriff auf die benötigte Ressource des Timetable-Services.
Das selbe Prinzip wird ebenfalls für den Booking-Service angewandt.

```yaml
server:
  port: 8081

spring:
  cloud:
    gateway:
      routes:
      - id: timetable
        uri: http://localhost:8082/
        predicates:
        - Path=/timetable/**
        filters:
        - StripPrefix=1
        - CircuitBreaker=timetableCircuitBreaker
      - id: booking
        uri: http://localhost:8084/
        predicates:
        - Path=/booking/**
        filters:
        - StripPrefix=1
        - CircuitBreaker=bookingCircuitBreaker
        - TokenRelay=
      - id: openAPI
        uri: http://localhost:${server.port}/
        predicates:
        - Path=/v3/api-docs/**
        filters:
        - RewritePath=/v3/api-docs/(?<path>.*), /$\{path}/v3/api-docs/
opentracing:
  jaeger:
    udp-sender:
      host: localhost
      port: 6831
``` 

## OpenAPI

Das API-Gateway stellt eine gemeinsame OpenAPI-Dokumentation zur Verfügung, welche sich aus jener des Booking-Services und des Timetable-Services zusammensetzt.
Um die Dokumente nicht duplizieren zu müssen und somit Redundanz zu erzeugen, wird die Gruppierungsfunktionalität von OpenAPI verwendet.
Für jedes Service wird eine entsprechende Gruppe erzeugt, welche auf das OpenAPI-Dokument des Services verweist.
Der OpenAPI-Client (z.B. Swagger UI) kann anschließend die benötigte Gruppe für das Service auswählen und sich die entsprechende Dokumentation generieren lassen.
Dies wird in den nächsten beiden Screenshots verdeutlicht, wobei das Dropdown-Menü zur Auswahl der Gruppe hervorgehoben ist.
Im ersten Screenshot ist die Ansicht der OpenAPI-Gruppe für das Timetable-Service ersichtlich, während im zweiten die Ansicht der OpenAPI-Gruppe für das Booking-Service gezeigt wird.

![Ansicht der OpenAPI-Gruppe für den Timetable-Service](doc/openapi/timetableOpenAPI.png)
![Ansicht der OpenAPI-Gruppe für den Booking-Service](doc/openapi/bookingOpenAPI.png)

Um dies am API-Gateway zu realisieren, werden die unten gezeigten Dependencies benötigt.

```xml
<dependency>
	<groupId>org.springdoc</groupId>
	<artifactId>springdoc-openapi-webflux-core</artifactId>
	<version>${springdoc.version}</version>
</dependency>
<dependency>
	<groupId>org.springdoc</groupId>
	<artifactId>springdoc-openapi-webflux-ui</artifactId>
	<version>${springdoc.version}</version>
</dependency>
```

Anschließend müssen aus den Routendefinitionen noch die OpenAPI-Gruppen gewonnen werden.
Der unten zu sehende Code definiert ein Bean `apis`, welches die OpenAPI-Gruppen definiert.
Dabei werden die Definitionen für den Booking und den Timetable-Service geladen und aus diesen OpenAPI-Gruppen-Einträge erzeugt.

```java
@Configuration
public class OpenApiConfiguration {
	@Bean
	public List<GroupedOpenApi> apis(SwaggerUiConfigProperties swaggerUiConfigProperties, RouteDefinitionLocator routeDefinitionLocator) {
		List<GroupedOpenApi> groups = new ArrayList<>();
		List<RouteDefinition> definitions = routeDefinitionLocator
				.getRouteDefinitions()
				.collectList()
				.block();
		definitions.stream()
				.filter(routeDefinition ->
						routeDefinition.getId().equals("booking")
						|| routeDefinition.getId().equals("timetable")
				)
				.forEach(routeDefinition -> {
					swaggerUiConfigProperties.addGroup(routeDefinition.getId());
					groups.add(GroupedOpenApi.builder()
						.pathsToMatch("/" + routeDefinition.getId() + "/**")
						.group(routeDefinition.getId())
						.build()
					);
				});
		return groups;
	}
}
```

Abschließend müssen die Server-URLs in der Beschreibung des Timetable- und des Booking-Services noch so angepasst werden, dass diese auf die unter dem API-Gateway erreichbare URL des Services verweisen.
Dies wird in den folgenden zwei Quellcodeauszügen gezeigt.

```java
@OpenAPIDefinition(
        // ...
        servers = @Server(url = "http://172.17.37.171:30451/timetable/", description = "Timetable API development server"),
        // ...
)
// ...
public class TimetableApiApplication extends Application {
    public static final String OPEN_API_TAG_NAME_TIMETABLE = "timetable";
}
```

```java
// ...
@OpenAPIDefinition(
        // ...
        servers = @Server(url = "http://172.17.37.171:30451/booking/", description = "Booking API development server"),
        // ...
)
public class BookingApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookingApplication.class, args);
    }
}
```

## Authentifizierung

Das API-Gateway überprüft den OAuth-Token und leitet diesen an die entsprechenden Services, welche ebenfalls die Authentifizierung überprüfen, in diesem Projekt das Booking-Service, weiter.
Um dies zu realisieren, sind die folgenden Dependencies einzubinden:

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
<dependency>
	<groupId>com.okta.spring</groupId>
	<artifactId>okta-spring-boot-starter</artifactId>
	<version>1.4.0</version>
</dependency>
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-oauth2</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-security</artifactId>
</dependency>
```

In der Konfiguration für das Forwarding zu den Services wurde bereits die Token-Weiterleitung für das Booking-Service über einen Filter realisiert.
Anschließend muss noch eine Spring-Security-Konfiguration so definiert werden, dass die Authentifizierung über OAuth2 durchgeführt wird und für Zugriffe auf das Booking-Service benötigt wird.

```java
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfig {
	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		http.csrf().disable();

		http
				.authorizeExchange()
						.pathMatchers(HttpMethod.POST, "/booking/booking/**").authenticated()
						.and()
				.authorizeExchange()
						.pathMatchers(HttpMethod.GET, "/booking/booking/**").authenticated()
						.and()
				.authorizeExchange()
						.anyExchange().permitAll()
						.and()
				.oauth2Login()
						.and()
				.oauth2ResourceServer()
						.jwt();
		return http.build();
	}
}
```


Wie in dem Kapitel zum Web-Frontend beschrieben, hatten wir Probleme mit der React-Integration von Okta und mussten daher auf den folgenden Ansatz ausweichen:
Das Gateway verwendet eine Web-Anwendungs-Konfiguration von Okta und leitet bei fehlender Authentifizierung auf die Login-Seite von Okta weiter.
Anschließend wird der OAuth-Token in der HTTP-Session am Gateway gespeichert und zur Authentifzierung herangezogen.
Dies erklärt den Aufruf von `.oauth2Login()` zur Aktivierung des OAuth2-Logins.

Die Okta-Konfiguration ist im folgenden Screenshot zu sehen.
Hierbei wird das Template für eine Webanwendung verwendet und die URLs werden entsprechend den [Beispielen aus dem Okta-Blog](https://developer.okta.com/blog/2019/08/28/reactive-microservices-spring-cloud-gateway) gesetzt.

![Okta-Konfiguration für das Gateway-Service mit Zugriff vom Localhost](doc/authentication/oktaConfigurationLocalhost.png)

Da sich beim Betrieb im Kubernetes-Cluster die URLs unterscheiden, muss für den Betrieb im Kubernetes-Cluster eine zweite, äquivalente Dokumentation erstellt werden.

Ebenfalls müssen die Credentials des Okta-Zugangs am Gateway-Service in einer Konfigurationsdatei hinterlegt werden.
Der folgende Quellcodeauszug zeigt die benötigten Einstellungen zur Verknüpfung der Spring-Boot-Applikation mit der Okta-Konfiguration.
Um die Sicherheit der Credentials zu gewährleisten, wird die Konfigurationsdatei über ein eigenes Profil namens *security* geladen und **nicht** in das Git-Repository eingecheckt.

```properties
okta.oauth2.issuer = https://dev-649162.okta.com/oauth2/default
okta.oauth2.client-id = 0oaf4thceV73lVUCp4x6
okta.oauth2.client-secret = ...
```

Für den Betrieb im Kubernetes-Cluster wurde noch eine eigene Konfigurationsdatei und ein eigenes Profil erstellt.

### CORS

Natürlich muss für das API-Gateway ebenfalls eine CORS-Konfiguration vorgenommen werden.
Da der Client Cookies bei einem einem CORS-Request mitsenden muss, müssen die erlaubten Origins explizit festgelegt werden sowie die Option zur Erlaubnis von Credentials aktiviert werden.
Dies und die andere standardmäßige Konfiguration wird in dem folgenden Quellcodeauszug gezeigt.

```java
@Configuration
public class WebCorsConfiguration {
	@Value("${cors.origins.webFrontend}")
	private String corsOriginWebFrontend;

	@Value("${cors.origins.swaggerUI}")
	private String corsOriginSwaggerUI;

	@Bean
	public CorsWebFilter corsWebFilter() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowedOrigins(List.of(
				this.corsOriginWebFrontend,
				this.corsOriginSwaggerUI
		));
		corsConfiguration.setMaxAge(3600L);
		corsConfiguration.setAllowedMethods(List.of("*"));
		corsConfiguration.setAllowedHeaders(List.of("*"));
		corsConfiguration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource corsConfigurationSource
				= new UrlBasedCorsConfigurationSource();
		corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsWebFilter(corsConfigurationSource);
	}
}
```

# Kubernetes

Lukas

## Infrastruktur-Services

Lukas

# Frontend

Daniel

# Authentifizierung

## API-Gateway und Services

Lukas

## Frontend

Daniel
