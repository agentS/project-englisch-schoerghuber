package eu.nighttrains.booking;

import eu.nighttrains.booking.rest.BookingEndpoint;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@OpenAPIDefinition(
        info = @Info(
                title = "Booking API",
                version = "0.0.0",
                contact = @Contact(
                        name = "Lukas Schoerghuber",
                        email = "lukas.schoerghuber@posteo.at"
                ),
                license = @License(
                        name = "GPLv3",
                        url = "https://www.gnu.org/licenses/gpl-3.0.en.html"
                )
        ),
        servers = @Server(url = "http://localhost:8081/booking/", description = "Booking API development server"),
        tags = @Tag(name = BookingEndpoint.OPEN_API_TAG_NAME_BOOKING, description = "Booking-related endpoints")
)
public class BookingApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookingApplication.class, args);
    }
}
