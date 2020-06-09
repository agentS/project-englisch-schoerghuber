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
import {TrainCarType, TrainCarTypeFromJSON, TrainCarTypeToJSON,} from './';

/**
 *
 * @export
 * @interface BookingRequestDto2
 */
export interface BookingRequestDto2 {
    /**
     *
     * @type {number}
     * @memberof BookingRequestDto2
     */
    destinationId?: number;
    /**
     *
     * @type {Date}
     * @memberof BookingRequestDto2
     */
    journeyStartDate?: Date;
    /**
     *
     * @type {number}
     * @memberof BookingRequestDto2
     */
    originId?: number;
    /**
     *
     * @type {TrainCarType}
     * @memberof BookingRequestDto2
     */
    trainCarType?: TrainCarType;
}

export function BookingRequestDto2FromJSON(json: any): BookingRequestDto2 {
    return BookingRequestDto2FromJSONTyped(json, false);
}

export function BookingRequestDto2FromJSONTyped(json: any, ignoreDiscriminator: boolean): BookingRequestDto2 {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {

        'destinationId': !exists(json, 'destinationId') ? undefined : json['destinationId'],
        'journeyStartDate': !exists(json, 'journeyStartDate') ? undefined : (new Date(json['journeyStartDate'])),
        'originId': !exists(json, 'originId') ? undefined : json['originId'],
        'trainCarType': !exists(json, 'trainCarType') ? undefined : TrainCarTypeFromJSON(json['trainCarType']),
    };
}

export function BookingRequestDto2ToJSON(value?: BookingRequestDto2 | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {

        'destinationId': value.destinationId,
        'journeyStartDate': value.journeyStartDate === undefined ? undefined : (value.journeyStartDate.toISOString().substr(0, 10)),
        'originId': value.originId,
        'trainCarType': TrainCarTypeToJSON(value.trainCarType),
    };
}


