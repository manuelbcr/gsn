import { Component, Inject, OnInit } from '@angular/core';
import { LoginService } from './services/login.service';
import { DOCUMENT } from '@angular/common';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'gsn-webui-frontend';

  constructor(
    @Inject(DOCUMENT) private document: Document,
    private route: ActivatedRoute,
    private loginService: LoginService) { }

  login_url : string = ''
  username : string = ''
  logged_in : boolean = false

  ngOnInit(): void {
    // if redirected from login
    this.route.queryParams
      .subscribe(params => {
        if(params['response_type']){
          var code = params['code']
          var response_type = params['response_type']
          var user_name = params['user_name']
          var user_email = params['user_email']

          var query_params = "?code=" + code + "&response_type=" + response_type + 
          "&user_name" + user_name + "&user_email" + user_email;
          console.log("query_params", query_params);

          // creates profile after logging in
          this.loginService.createProfile(query_params).subscribe((data: any) => {
            this.username = data.username;
            this.logged_in = data.logged_in;
            console.log("data", data)
          }, (error: any) => {
            console.error(error);
          });
        }
      }
    );

    this.getLoginUrl();
  }

  getLoginUrl() {
    this.loginService.getLoginUrl().subscribe((data: any) => {
      this.login_url = data.url
    }, (error: any) => {
      console.error(error);
    });
  }

  login(): void {
    this.document.location.href = this.login_url;
  }

}