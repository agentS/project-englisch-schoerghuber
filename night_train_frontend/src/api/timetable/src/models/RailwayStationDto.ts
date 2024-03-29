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
/**
 * 
 * @export
 * @interface RailwayStationDto
 */
export interface RailwayStationDto {
    /**
     * 
     * @type {number}
     * @memberof RailwayStationDto
     */
    id: number;
    /**
     * 
     * @type {string}
     * @memberof RailwayStationDto
     */
    name: string;
}

export function RailwayStationDtoFromJSON(json: any): RailwayStationDto {
    return RailwayStationDtoFromJSONTyped(json, false);
}

export function RailwayStationDtoFromJSONTyped(json: any, ignoreDiscriminator: boolean): RailwayStationDto {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'id': json['id'],
        'name': json['name'],
    };
}

export function RailwayStationDtoToJSON(value?: RailwayStationDto | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'id': value.id,
        'name': value.name,
    };
}


