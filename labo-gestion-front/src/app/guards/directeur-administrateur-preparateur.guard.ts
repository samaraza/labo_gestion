import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { LoginService } from '../service/login';
import { Role } from '../enums/role';

@Injectable({
  providedIn: 'root'
})
export class DirecteurAdministrateurPreparateurGuard implements CanActivate {

  constructor(
    private router: Router,
    private login: LoginService
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    if (!this.login.connectedUser) {
      this.router.navigate(['/login']);
      return false;
    }

    const userRole = this.login.connectedUser.role;

    if (userRole === Role.DIRECTEUR ||
        userRole === Role.ADMINISTRATEUR ||
        userRole === Role.PREPARATEUR ||
        userRole === Role.PROFFESSEUR) {   // ✅ إضافة PROFFESSEUR بشكل صحيح
      console.log("✅ Accès autorisé pour", userRole);
      return true;
    } else {
      console.log("❌ Accès refusé pour", userRole);
      this.router.navigate(['/authentication']);
      return false;
    }
  }
}
