import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { LoginService } from '../service/login';
import { Role } from '../enums/role';

@Injectable({
  providedIn: 'root'
})
export class ProfesseurPreparateurDirecteurGuard implements CanActivate {

  constructor(
    private router: Router,
    private login: LoginService
  ) {}

  canActivate(): boolean {
    if (!this.login.connectedUser) {
      this.router.navigate(['/login']);
      return false;
    }

    const userRole = this.login.connectedUser.role;

    if (userRole === Role.PROFFESSEUR ||
        userRole === Role.PREPARATEUR ||
        userRole === Role.DIRECTEUR ||
          userRole === Role.ADMINISTRATEUR){
      return true;
    }

    this.router.navigate(['/authentication']);
    return false;
  }
}
