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
/**
 * 
 * @export
 * @interface TicketDto
 */
export interface TicketDto {
    /**
     * 
     * @type {number}
     * @memberof TicketDto
     */
    departureStationId: number;
    /**
     * 
     * @type {number}
     * @memberof TicketDto
     */
    arrivalStationId: number;
    /**
     * 
     * @type {number}
     * @memberof TicketDto
     */
    trainCarId: number;
    /**
     * 
     * @type {number}
     * @memberof TicketDto
     */
    placeNumber: number;
    /**
     * 
     * @type {Date}
     * @memberof TicketDto
     */
    departureDate: Date;
    /**
     * 
     * @type {number}
     * @memberof TicketDto
     */
    trainConnectionId: number;
    /**
     * 
     * @type {Array<number>}
     * @memberof TicketDto
     */
    departureStationIds?: Array<number>;
}

export function TicketDtoFromJSON(json: any): TicketDto {
    return TicketDtoFromJSONTyped(json, false);
}

export function TicketDtoFromJSONTyped(json: any, ignoreDiscriminator: boolean): TicketDto {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'departureStationId': json['departureStationId'],
        'arrivalStationId': json['arrivalStationId'],
        'trainCarId': json['trainCarId'],
        'placeNumber': json['placeNumber'],
        'departureDate': (new Date(json['departureDate'])),
        'trainConnectionId': json['trainConnectionId'],
        'departureStationIds': !exists(json, 'departureStationIds') ? undefined : json['departureStationIds'],
    };
}

export function TicketDtoToJSON(value?: TicketDto | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'departureStationId': value.departureStationId,
        'arrivalStationId': value.arrivalStationId,
        'trainCarId': value.trainCarId,
        'placeNumber': value.placeNumber,
        'departureDate': (value.departureDate.toISOString().substr(0,10)),
        'trainConnectionId': value.trainConnectionId,
        'departureStationIds': value.departureStationIds,
    };
}


