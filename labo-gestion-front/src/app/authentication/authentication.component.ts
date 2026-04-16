import { Component, HostListener, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../service/login';

@Component({
  selector: 'app-authentication',
  templateUrl: './authentication.component.html',
  styleUrls: ['./authentication.component.scss']
})
export class AuthenticationComponent implements OnInit{


      constructor(private router: Router,private loginService: LoginService) { }

       ngOnInit(): void {

       }


       @HostListener('window:popstate', ['$event'])
       onPopState(event: any) {
         // Perform your action here, such as navigating to a specific route
         // For example, navigate to the login page when the back button is pressed
         this.goToRoute()
       }

        goToRoute() {
                 // ✅ استخدام Router بدلاً من window.open للحفاظ على حالة التطبيق
                 this.router.navigate(['/login']);
          }


}
