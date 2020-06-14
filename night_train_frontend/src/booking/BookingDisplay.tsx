import React from "react";
import {Accordion, Button, Card, Table} from "react-bootstrap";

import moment from "moment";

import {MOMENT_DISPLAY_DATE_FORMAT} from "../Constants";

import {BookingApi, BookingDto} from "../api/booking";

interface BookingDisplayProperties {
	bookingApi: BookingApi;
	bookingId: string;
}

interface BookingDisplayState {
	booking?: BookingDto
}

class BookingDisplay extends React.Component<BookingDisplayProperties, BookingDisplayState> {
	constructor(properties: BookingDisplayProperties) {
		super(properties);
		this.state = {
			booking: undefined
		};
	}

	async componentDidMount() {
		await this.loadBooking();
	}

	async componentDidUpdate(previousProperties: BookingDisplayProperties) {
		if (this.props.bookingId !== previousProperties.bookingId) {
			await this.loadBooking();
		}
	}

	async loadBooking() {

		const booking = await this.props.bookingApi.findById({id: this.props.bookingId});
		this.setState({booking});

	}

	render() {

		if ((this.state.booking !== undefined) && (this.state.booking !== null)) {
			return (
				<div className="mt-4">
					<h1>{this.state.booking.departureStationId} to {this.state.booking.arrivalStationId}</h1>
					<h1>Departure
						date: {moment(this.state.booking.departureDate).format(MOMENT_DISPLAY_DATE_FORMAT)}</h1>
					<h2>Tickets</h2>
					<Accordion>
						{this.state.booking.tickets?.map((ticket, index) => (
							<Card key={index}>
								<Card.Header>
									<Accordion.Toggle as={Button} variant="link"
													  eventKey={index.toString()}>
										{ticket.trainConnectionId}
									</Accordion.Toggle>
								</Card.Header>
								<Accordion.Collapse eventKey={index.toString()}>
									<Card.Body>
										<h3>Train code: {ticket.trainConnectionId}</h3>

										<h5>Reservation</h5>
										<p>Car type: {this.state.booking?.trainCarType}</p>
										<p>Car number: {ticket.trainCarId}</p>
										<p>Place number: {ticket.placeNumber}</p>

									</Card.Body>
								</Accordion.Collapse>
							</Card>
						))}
					</Accordion>
				</div>
			);
		} else {
			return (<h3>Loading...</h3>);
		}
	}

}

export default BookingDisplay;
