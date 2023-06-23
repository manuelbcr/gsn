import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  constructor(private http: HttpClient) { }

  // gets login url from backend
  getLoginUrl(): Observable<any> {
    return this.http.get('http://localhost:8000/oauth_code');
  }

  // creates profile after login in
  // returns user
  createProfile(params: string): Observable<any> {
    var url = 'http://localhost:8000/profile/' + params;
    return this.http.get(url);
  }

}


