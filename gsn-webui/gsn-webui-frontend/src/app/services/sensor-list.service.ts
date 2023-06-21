import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SensorListService {

  constructor(private http: HttpClient) { }

  getSensors(): Observable<any> {
    return this.http.get('http://localhost:8001/sensors');
  }

}
