/*
 * map.component.ts
 * Component for sensor list
 *
 * This file contains code based on or derived from OpenLayers (https://openlayers.org/),
 * which is licensed under the BSD 2-Clause License.
 *
 * Copyright (c) 2005-present, OpenLayers Contributors All rights reserved.
 * All rights reserved.
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */


import { Component, OnInit } from '@angular/core';
import { SensorService } from '../services/sensor.service';
import * as ol from 'ol';
import Map from 'ol/Map';
import View from 'ol/View';
import Feature from 'ol/Feature';
import Circle from 'ol/geom/Circle';
import TileLayer from 'ol/layer/Tile';
import XYZ from 'ol/source/XYZ';
import VectorSource from 'ol/source/Vector';
import VectorLayer from 'ol/layer/Vector';
import { Stroke, Style, Fill } from 'ol/style';
import { fromLonLat, toLonLat } from 'ol/proj';
import Vector from 'ol/layer/Vector';
import Point from 'ol/geom/Point';
import { Icon } from 'ol/style';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements OnInit {
  loading: boolean = true;
  test: string = "TEST";
  radius: number = 10000;
  radiusInput: any;
  zoomLevel: number = 6;
  locationSearchResult: string = '';
  locationSearchDetails: string = '';
  sensors: any[] = [];
  lat = 0; // Set the initial latitude
  lng = 0; // Set the initial longitude


  private map: Map = new Map;
  private circle: any;
  private vectorSource: any;
  private vectorLayer: any;
  circlePosition: any;

  constructor(private sensorService: SensorService) { }

  ngOnInit() {
    this.sensorService.getSensors().subscribe((data) => {
      this.sensors = data.features;
      this.loading = false;
      if (this.sensors.length > 0) {
        const firstSensor = this.sensors[0];
        this.lat = firstSensor.geometry.coordinates[0];
        this.lng = firstSensor.geometry.coordinates[1];
        this.initializeMap(); 
      }
    });

  }


  private initializeMap() {
    this.map = new Map({
      target: 'map',
      layers: [
        new TileLayer({
          source: new XYZ({
            url: 'https://{a-c}.tile.openstreetmap.org/{z}/{x}/{y}.png' // Use OpenStreetMap as the base layer
          })
        })
      ],
      view: new View({
        center: [0, 0], 
        zoom: 10 
      })
    });
    const firstSensor = this.sensors[0];
    const firstSensorCoordinates = firstSensor.geometry.coordinates;
    const centerCoordinates = fromLonLat([firstSensorCoordinates[0], firstSensorCoordinates[1]]);
    this.circlePosition = [firstSensorCoordinates[0], firstSensorCoordinates[1]];
    const circleStyle = new Style({
      stroke: new Stroke({
        color: '#428bca',
        width: 2
      }),
      fill: new Fill({
        color: 'rgba(59, 29, 189, 0.35)'
      })
    });
    this.vectorSource = new VectorSource();
    this.circle = new Feature({
      geometry: new Circle(
        fromLonLat(this.circlePosition),
        this.radius
      )
    });
    this.vectorSource.addFeature(this.circle);

    this.vectorLayer = new VectorLayer({
      source: this.vectorSource,
      style: circleStyle
    });

    this.map.addLayer(this.vectorLayer);
     const markerSource = new VectorSource();
     const markerLayer = new Vector({
       source: markerSource
     });
 
    // TODO change marker based on circle
     this.map.addLayer(markerLayer);
     this.sensors.forEach(sensor => {
      const coordinates = sensor.geometry.coordinates;
      const marker = new Feature({
        geometry: new Point(fromLonLat(coordinates))
      });

      const markerStyle = new Style({
        image: new Icon({
          src: '../../assets/285659_marker_map_icon.svg', // Provide the path to your marker icon
          anchor: [0.5, 1] // Set the anchor point of the icon (adjust if needed)
        })
      });

      marker.setStyle(markerStyle);
      markerSource.addFeature(marker);
    });

    this.map.getView().setCenter(centerCoordinates);
  }

  onRadiusInputChange(event: Event) {
    this.radiusInput = (event.target as HTMLInputElement).value;
    this.radius = Number(this.radiusInput);
    this.circle.getGeometry().setRadius(this.radius);
    // TODO update markers which are in the circle
  }

  updateCirclePosition() {
    if(typeof this.circlePosition  === 'string'){
      const coordinates = this.circlePosition.split(',').map(parseFloat);
      if (coordinates.length === 2 && !isNaN(coordinates[0]) && !isNaN(coordinates[1])) {
        this.circlePosition = coordinates;
        this.circle.getGeometry().setCenter(fromLonLat(this.circlePosition))
        this.map.getView().setCenter(fromLonLat(this.circlePosition));
      }
    } else {
        this.circle.getGeometry().setCenter(fromLonLat(this.circlePosition))
        this.map.getView().setCenter(fromLonLat(this.circlePosition));
    }
    
    // TODO update markers which are in the circle
  }

  centerOnMe() {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const latitude = position.coords.latitude;
          const longitude = position.coords.longitude;
          
          this.circlePosition = [longitude, latitude];
          this.circle.getGeometry().setCenter(fromLonLat(this.circlePosition));
          this.circle.getGeometry().setRadius(this.radius);
          this.map.getView().setCenter(fromLonLat(this.circlePosition));
        },
        (error) => {
          // Handle geolocation error
          console.log('Error getting current location:', error);
        }
      );
    } else {
      // Geolocation is not supported by the browser
      console.log('Geolocation is not supported');
    }
  }

  locationSearch() {
   
  }

  isCloseEnough() {
    /**return (sensor: any) => {
      if (!sensor.geometry) {
        return false;
      }
      const dist = this.mapDistanceService.distance(this.circlePosition, sensor);
      return dist !== null && dist < this.radius;
    }; */
  }

}
