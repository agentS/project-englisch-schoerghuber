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
 * @interface BookingRequestDto
 */
export interface BookingRequestDto {
    /**
     * 
     * @type {number}
     * @memberof BookingRequestDto
     */
    departureStationId: number;
    /**
     * 
     * @type {number}
     * @memberof BookingRequestDto
     */
    arrivalStationId: number;
    /**
     * 
     * @type {Date}
     * @memberof BookingRequestDto
     */
    departureDate: Date;
    /**
     * 
     * @type {string}
     * @memberof BookingRequestDto
     */
    trainCarType: BookingRequestDtoTrainCarTypeEnum;
}

export function BookingRequestDtoFromJSON(json: any): BookingRequestDto {
    return BookingRequestDtoFromJSONTyped(json, false);
}

export function BookingRequestDtoFromJSONTyped(json: any, ignoreDiscriminator: boolean): BookingRequestDto {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'departureStationId': json['departureStationId'],
        'arrivalStationId': json['arrivalStationId'],
        'departureDate': (new Date(json['departureDate'])),
        'trainCarType': json['trainCarType'],
    };
}

export function BookingRequestDtoToJSON(value?: BookingRequestDto | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'departureStationId': value.departureStationId,
        'arrivalStationId': value.arrivalStationId,
        'departureDate': (value.departureDate.toISOString().substr(0,10)),
        'trainCarType': value.trainCarType,
    };
}

/**
* @export
* @enum {string}
*/
export enum BookingRequestDtoTrainCarTypeEnum {
    SEAT = 'SEAT',
    COUCHETTE = 'COUCHETTE',
    SLEEPER = 'SLEEPER'
}

