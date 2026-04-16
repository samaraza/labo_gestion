import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from '../service/AuthenticationService';

@Component({
  selector: 'app-activate-account',
  templateUrl: './activate-account.component.html',
  styleUrls: ['./activate-account.component.scss']
})
export class ActivateAccountComponent {

  message = '';
  isOkay = true;
  submitted = false;

  constructor(
    private router: Router,
    private authService: AuthenticationService
  ) {}

  private confirmAccount(token: string): void {
    this.authService.confirm({ token }).subscribe({
      next: () => {
        this.message = 'Your account has been successfully activated.\nNow you can proceed to login';
        this.submitted = true;
      },
      error: (error: any) => {  // ✅ أضف : any هنا
        console.error('Activation error:', error);
        this.message = 'Token has been expired or invalid';
        this.submitted = true;
        this.isOkay = false;
      }
    });
  }

  redirectToLogin(): void {
    this.router.navigate(['login']);
  }

  onCodeCompleted(token: string): void {
    this.confirmAccount(token);
  }
}
