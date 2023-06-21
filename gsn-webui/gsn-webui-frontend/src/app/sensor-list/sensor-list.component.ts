import { Component, OnInit } from '@angular/core';
import { SensorService } from "src/app/services/sensor.service";

@Component({
  selector: 'app-sensor-list',
  templateUrl: './sensor-list.component.html',
  styleUrls: ['./sensor-list.component.scss']
})
export class SensorListComponent implements OnInit {
  loading = true;
  sensors: any[] =[];
  favorites: any[] =[];


  constructor(
    private sensorService: SensorService) {

    }

  ngOnInit() {
    this.loadSensors();
  }

  loadSensors() {
    this.sensorService.getSensors().subscribe((data: any) => {
      this.sensors = data.features;
      this.loading = false;

    }, (error: any) => {
      console.error(error);
    });
  }

}
