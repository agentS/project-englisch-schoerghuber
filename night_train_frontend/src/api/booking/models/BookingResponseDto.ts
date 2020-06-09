/* tslint:disable */
/* eslint-disable */
/**
 * Timetable API
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: 0.0.0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import {exists} from '../runtime';

/**
 *
 * @export
 * @interface BookingResponseDto
 */
export interface BookingResponseDto {
    /**
     *
     * @type {number}
     * @memberof BookingResponseDto
     */
    bookingId?: number;
}

export function BookingResponseDtoFromJSON(json: any): BookingResponseDto {
    return BookingResponseDtoFromJSONTyped(json, false);
}

export function BookingResponseDtoFromJSONTyped(json: any, ignoreDiscriminator: boolean): BookingResponseDto {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {

        'bookingId': !exists(json, 'bookingId') ? undefined : json['bookingId'],
    };
}

export function BookingResponseDtoToJSON(value?: BookingResponseDto | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {

        'bookingId': value.bookingId,
    };
}


