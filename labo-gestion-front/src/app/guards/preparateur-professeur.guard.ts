import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { LoginService } from '../service/login'; // ✅ تصحيح المسار
import { Role } from '../enums/role';

@Injectable({
  providedIn: 'root'
})
export class PreparateurProfesseurGuard implements CanActivate {

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

    if (userRole === Role.PREPARATEUR || userRole === Role.PROFFESSEUR) {
      console.log("✅ Accès autorisé pour", userRole);
      return true;
    } else {
      console.log("❌ Accès refusé pour", userRole);
      this.router.navigate(['/authentication']);
      return false;
    }
  }
}
