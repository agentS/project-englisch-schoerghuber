package eu.nighttrains.booking.rest;

import eu.nighttrains.booking.dto.BookingDto;
import eu.nighttrains.booking.dto.BookingRequestDto;
import eu.nighttrains.booking.service.BookingNotFoundException;
import eu.nighttrains.booking.service.BookingService;
import eu.nighttrains.booking.service.NoConnectionsAvailableException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


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
        servers = @Server(url = "http://127.0.0.1:8084", description = "Booking API development server"),
        tags = @Tag(name = BookingEndpoint.OPEN_API_TAG_NAME_BOOKING, description = "Booking-related endpoints")
)
@RestController
public class BookingEndpoint {
    public static final String OPEN_API_TAG_NAME_BOOKING = "booking";

    private final BookingService bookingService;

    public BookingEndpoint(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Parameter(name = "id", description = "ID of the booking.", required = true, example = "5ed4fee72911115ee0fe46d9")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "All information about the booking including all tickets."

            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "The booking with the specified ID does not exist.",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            )
    })
    @Tag(name = BookingEndpoint.OPEN_API_TAG_NAME_BOOKING)
    @GetMapping(value = "/booking/{id}")
    public ResponseEntity<BookingDto> findById(@PathVariable("id") String id) {
        try {
            BookingDto bookingDto = bookingService.findById(id);
            return new ResponseEntity<>(bookingDto, HttpStatus.OK);
        } catch (BookingNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Confirmation of booking with all necessary information."

            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "The provided information was not given in the correct format.",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN)
            )
    })
    @Tag(name = BookingEndpoint.OPEN_API_TAG_NAME_BOOKING)
    @PostMapping(value = "/booking")
    public ResponseEntity<BookingDto> book(@RequestBody BookingRequestDto bookingRequestDto) {
        try {
            BookingDto bookingDto = bookingService.book(bookingRequestDto.getDepartureStationId(), bookingRequestDto.getArrivalStationId(), bookingRequestDto.getDepartureDate(), bookingRequestDto.getTrainCarType());
            return new ResponseEntity<>(bookingDto, HttpStatus.CREATED);
        } catch (NoConnectionsAvailableException exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
