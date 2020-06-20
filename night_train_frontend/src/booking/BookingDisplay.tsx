import React from "react";
import {Accordion, Button, Card} from "react-bootstrap";

import moment from "moment";

import {MOMENT_DISPLAY_DATE_FORMAT} from "../Constants";

import {BookingApi, BookingDto, BookingDtoStatusEnum} from "../api/booking";
import {TimetableApi} from "../api/timetable";

interface BookingDisplayProperties {
    bookingApi: BookingApi;
    timetableApi: TimetableApi;
    bookingId: string;
}

interface BookingDisplayState {
    booking?: BookingDto;
    isLoading: boolean;
    railwayStationNames: Map<number, string>;
    trainConnectionCodes: Map<number, string>;
}

class BookingDisplay extends React.Component<BookingDisplayProperties, BookingDisplayState> {
    constructor(properties: BookingDisplayProperties) {
        super(properties);
        this.state = {
            booking: undefined,
            isLoading: true,
            railwayStationNames: new Map<number, string>(),
            trainConnectionCodes: new Map<number, string>()
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

    getRailwayStationName(railwayStationId : number){
        if(!this.state.railwayStationNames.has(railwayStationId)){
            this.props.timetableApi.railwayStationIdGet({id: railwayStationId}).then(result =>{
                let oldMap = this.state.railwayStationNames
                oldMap.set(railwayStationId, result.name);
                this.setState({railwayStationNames: oldMap})
            });
        }else{
            return this.state.railwayStationNames.get(railwayStationId);
        }
        return railwayStationId;
    }

    getTrainConnectionCode(trainConnectionId : number){
        if(!this.state.trainConnectionCodes.has(trainConnectionId)){
            this.props.timetableApi.trainConnectionIdGet({id: trainConnectionId}).then(result =>{
                let oldMap = this.state.trainConnectionCodes
                oldMap.set(trainConnectionId, result.code);
                this.setState({trainConnectionCodes: oldMap})
            });
        }else{
            return this.state.trainConnectionCodes.get(trainConnectionId);
        }
        return trainConnectionId;
    }

    async loadBooking() {
        this.setState({isLoading: true});
        let booking = undefined;
        try {
            booking = await this.props.bookingApi.findById({id: this.props.bookingId});
        } catch (exception) {
            console.error(exception);
        }
        this.setState({booking, isLoading: false});
    }

    render() {
        if (this.state.isLoading) {
            return <span> Fetching booking information
			<div className="spinner-border" role="status">
				<span className="sr-only">Loading...</span>
			</div>
			</span>
        } else if (this.state.booking == undefined) {
            return <h1>Booking not found.</h1>
        } else {
            return (
                <div className="mt-4">
                    <h1>Your journey
                        from {this.getRailwayStationName(this.state.booking.departureStationId)} to {this.getRailwayStationName(this.state.booking.arrivalStationId)}</h1>
                    <h2>Information</h2>
                    <ul>
                        <li><b>Email</b>: {this.state.booking.emailAddress}</li>
                        <li><b>Departure
                            date</b>: {moment(this.state.booking.departureDate).format(MOMENT_DISPLAY_DATE_FORMAT)}</li>
                        <li><b>Status</b>: {this.getTrainStatusLabel(this.state.booking.status)}</li>
                        <li><b>Train car type</b>: {this.state.booking.trainCarType.toString()}</li>
                    </ul>
                    <h2>Tickets</h2>
                    <Accordion>
                        {this.state.booking.tickets?.map((ticket, index) => (
                            <Card key={index}>
                                <Card.Header>
                                    <Accordion.Toggle as={Button} variant="link"
                                                      eventKey={index.toString()}>
                                        Train: {this.getTrainConnectionCode(ticket.trainConnectionId)}
                                    </Accordion.Toggle>
                                </Card.Header>
                                <Accordion.Collapse eventKey={index.toString()}>
                                    <Card.Body>
                                        <h5>Reservation</h5>
                                        <ul>
                                            <li><b>Train cart</b>: {ticket.trainCarId}</li>
                                            <li><b>Seat</b>: {ticket.placeNumber}</li>
                                            <li><b>Car type</b>: {this.state.booking?.trainCarType}</li>
                                        </ul>
                                        <h5>Journey</h5>
                                        <ul>
                                            <li><b>Departure
                                                Date</b>: {moment(ticket.departureDate).format(MOMENT_DISPLAY_DATE_FORMAT)}</li>
                                            <li><b>Departure Station</b>: {this.getRailwayStationName(ticket.departureStationId)}</li>
                                            <li><b>Arrival Station</b>: {this.getRailwayStationName(ticket.arrivalStationId)}</li>
                                        </ul>
                                    </Card.Body>
                                </Accordion.Collapse>
                            </Card>
                        ))}
                    </Accordion>
                </div>
            );
        }
    }

    private getTrainStatusLabel(status: BookingDtoStatusEnum) {
        switch (status) {
            case BookingDtoStatusEnum.CONFIRMED: return <span style={{color: "green"}}>{status.toString()}</span>;
            case BookingDtoStatusEnum.REJECTED: return <span style={{color: "red"}}>{status.toString()}</span>;
            case BookingDtoStatusEnum.RESERVED: return <span style={{color: "yellow"}}>{status.toString()}</span>;
        }
    }
}

export default BookingDisplay;
