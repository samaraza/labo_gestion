import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { LoginService } from '../service/login';
import { TokenService } from '../service/token.service';
import { JwtHelperService } from '@auth0/angular-jwt';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  constructor(
    private router: Router,
    private loginService: LoginService,
    private tokenService: TokenService
  ) {}

  signInForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(6)])
  });

  submit() {
    if (this.signInForm.valid) {
      const payload = {
        email: this.signInForm.get('email')?.value as string,
        password: this.signInForm.get('password')?.value as string
      };
      console.log("Payload sent:", payload);
      this.signInRequest(payload);
    } else {
      this.signInForm.markAllAsTouched();
    }
  }

  signInRequest(payload: { email: string; password: string }) {
    this.loginService.signIn(payload).subscribe({
      next: (response: any) => {
        console.log('Server response:', response);

        if (response.token) {
          // ✅ حفظ التوكن
          this.tokenService.token = response.token;

          // ✅ حفظ بيانات المستخدم (من الرد المباشر)
          const userData = {
            id: response.id,
            email: response.email,
            firstname: response.firstname,
            lastname: response.lastname,
            role: response.role
          };
          this.loginService.setUserData(userData);

          // ✅ (اختياري) فك التوكن للتأكد من محتواه
          const jwtHelper = new JwtHelperService();
          const decodedToken = jwtHelper.decodeToken(response.token);
          console.log('Full decoded token:', decodedToken);
          console.log('Authorities field:', decodedToken.authorities);

          // ✅ استخراج الأدوار من التوكن (إذا أردت التحقق)
          const roles = this.tokenService.getUserRoles();
          console.log('Extracted roles:', roles);

          alert('Login successful!');

          // ✅ توجيه المستخدم حسب الدور (اختياري)
          if (this.tokenService.hasRole('ADMINISTRATEUR')) {
            this.router.navigate(['/home/users']);
          } else if (this.tokenService.hasRole('DIRECTEUR')) {
            this.router.navigate(['/home/users']);
          } else if (this.tokenService.hasRole('PROFFESSEUR')) {
            this.router.navigate(['/home/produit']);
          } else if (this.tokenService.hasRole('PREPARATEUR')) {
            this.router.navigate(['/home/armoire']);
          } else {
            console.log('⚠️ Unknown role, redirecting to profile page');
            this.router.navigate(['/home/profile']);
          }
        }
      },
      error: (err: HttpErrorResponse) => {
        console.error('Error:', err);
        const errorMessage = err.error?.message || 'Invalid email or password';
        alert(errorMessage);
      }
    });
  }

  goToRegister(): void {
    this.router.navigate(['/register']);
  }

  get emailErrorMessage() {
    const email = this.signInForm.get('email');
    if (email?.hasError('required')) {
      return 'Email is required*';
    } else if (email?.hasError('email')) {
      return 'Invalid email format';
    }
    return '';
  }

  get passwordErrorMessage() {
    const password = this.signInForm.get('password');
    if (password?.hasError('required')) {
      return 'Password is required*';
    }
    return '';
  }
}
