import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SensorListComponent } from './sensor-list/sensor-list.component';
import { SensorDetailComponent } from './sensor-detail/sensor-detail.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { MapComponent } from './map/map.component';

const routes: Routes = [ 
  {path: 'sensors', component: SensorListComponent }, 
  {path: 'sensors/:sensorname', component: SensorDetailComponent }, 
  {path: 'dashboard', component: DashboardComponent }, 
  {path: 'map', component: MapComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
