/* tslint:disable */
/* eslint-disable */
/**
 * Booking API
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: 0.0.0
 * Contact: lukas.schoerghuber@posteo.at
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { exists, mapValues } from '../runtime';
import {
    TicketDto,
    TicketDtoFromJSON,
    TicketDtoFromJSONTyped,
    TicketDtoToJSON,
} from './';

/**
 * 
 * @export
 * @interface BookingDto
 */
export interface BookingDto {
    /**
     * 
     * @type {string}
     * @memberof BookingDto
     */
    id: string;
    /**
     * 
     * @type {number}
     * @memberof BookingDto
     */
    departureStationId: number;
    /**
     * 
     * @type {number}
     * @memberof BookingDto
     */
    arrivalStationId: number;
    /**
     * 
     * @type {Date}
     * @memberof BookingDto
     */
    departureDate: Date;
    /**
     * 
     * @type {string}
     * @memberof BookingDto
     */
    trainCarType: BookingDtoTrainCarTypeEnum;
    /**
     * 
     * @type {string}
     * @memberof BookingDto
     */
    status: BookingDtoStatusEnum;
    /**
     * 
     * @type {Array<TicketDto>}
     * @memberof BookingDto
     */
    tickets?: Array<TicketDto>;
    /**
     * 
     * @type {string}
     * @memberof BookingDto
     */
    emailAddress: string;
}

export function BookingDtoFromJSON(json: any): BookingDto {
    return BookingDtoFromJSONTyped(json, false);
}

export function BookingDtoFromJSONTyped(json: any, ignoreDiscriminator: boolean): BookingDto {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'id': json['id'],
        'departureStationId': json['departureStationId'],
        'arrivalStationId': json['arrivalStationId'],
        'departureDate': (new Date(json['departureDate'])),
        'trainCarType': json['trainCarType'],
        'status': json['status'],
        'tickets': !exists(json, 'tickets') ? undefined : ((json['tickets'] as Array<any>).map(TicketDtoFromJSON)),
        'emailAddress': json['emailAddress'],
    };
}

export function BookingDtoToJSON(value?: BookingDto | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'id': value.id,
        'departureStationId': value.departureStationId,
        'arrivalStationId': value.arrivalStationId,
        'departureDate': (value.departureDate.toISOString().substr(0,10)),
        'trainCarType': value.trainCarType,
        'status': value.status,
        'tickets': value.tickets === undefined ? undefined : ((value.tickets as Array<any>).map(TicketDtoToJSON)),
        'emailAddress': value.emailAddress,
    };
}

/**
* @export
* @enum {string}
*/
export enum BookingDtoTrainCarTypeEnum {
    SEAT = 'SEAT',
    COUCHETTE = 'COUCHETTE',
    SLEEPER = 'SLEEPER'
}
/**
* @export
* @enum {string}
*/
export enum BookingDtoStatusEnum {
    RESERVED = 'RESERVED',
    CONFIRMED = 'CONFIRMED',
    REJECTED = 'REJECTED'
}


