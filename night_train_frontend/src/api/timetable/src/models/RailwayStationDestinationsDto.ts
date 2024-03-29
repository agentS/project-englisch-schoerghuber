/* tslint:disable */
/* eslint-disable */
/**
 * Timetable API
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
    RailwayStationDto,
    RailwayStationDtoFromJSON,
    RailwayStationDtoFromJSONTyped,
    RailwayStationDtoToJSON,
} from './';

/**
 * 
 * @export
 * @interface RailwayStationDestinationsDto
 */
export interface RailwayStationDestinationsDto {
    /**
     * 
     * @type {Array<RailwayStationDto>}
     * @memberof RailwayStationDestinationsDto
     */
    destinations: Array<RailwayStationDto>;
    /**
     * 
     * @type {RailwayStationDto}
     * @memberof RailwayStationDestinationsDto
     */
    origin: RailwayStationDto;
}

export function RailwayStationDestinationsDtoFromJSON(json: any): RailwayStationDestinationsDto {
    return RailwayStationDestinationsDtoFromJSONTyped(json, false);
}

export function RailwayStationDestinationsDtoFromJSONTyped(json: any, ignoreDiscriminator: boolean): RailwayStationDestinationsDto {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'destinations': ((json['destinations'] as Array<any>).map(RailwayStationDtoFromJSON)),
        'origin': RailwayStationDtoFromJSON(json['origin']),
    };
}

export function RailwayStationDestinationsDtoToJSON(value?: RailwayStationDestinationsDto | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'destinations': ((value.destinations as Array<any>).map(RailwayStationDtoToJSON)),
        'origin': RailwayStationDtoToJSON(value.origin),
    };
}


