import { Component } from '@angular/core';
import { FormGroup, FormControl, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router } from '@angular/router';
import { RegisterService } from '../service/register.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {

  registerForm = new FormGroup({
    firstName: new FormControl('', [Validators.required]),
    lastName: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(8)]),
    confirmPassword: new FormControl('', [Validators.required]),
    role: new FormControl('PREPARATEUR', [Validators.required])
  }, { validators: this.passwordMatchValidator });

  constructor(
    private router: Router,
    private registerService: RegisterService
  ) {}

  passwordMatchValidator(group: AbstractControl): ValidationErrors | null {
    const password = group.get('password')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { mismatch: true };
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      const userData = {
        firstname: this.registerForm.get('firstName')?.value,
        lastname: this.registerForm.get('lastName')?.value,
        email: this.registerForm.get('email')?.value,
        password: this.registerForm.get('password')?.value,
        role: this.registerForm.get('role')?.value
      };

      this.registerService.register(userData).subscribe({
        next: () => {
          alert('Inscription réussie ! Un email de confirmation a été envoyé.');
          this.router.navigate(['/activate-account']);
        },
        error: (error: any) => {
          console.error('Erreur d\'inscription:', error);
          alert('Erreur lors de l\'inscription. Veuillez réessayer.');
        }
      });
    } else {
      this.registerForm.markAllAsTouched();
    }
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }
}
