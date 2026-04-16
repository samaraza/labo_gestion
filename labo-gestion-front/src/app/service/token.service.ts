import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  private readonly TOKEN_KEY = 'token';

  set token(token: string) {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  get token(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isTokenValid(): boolean {
    const token = this.token;
    if (!token) {
      return false;
    }

    const jwtHelper = new JwtHelperService();
    const isTokenExpired = jwtHelper.isTokenExpired(token);

    if (isTokenExpired) {
      this.clear();
      return false;
    }

    return true;
  }

  // ✅ Extract roles from "authorities" field
  getUserRoles(): string[] {
    const token = this.token;
    if (!token) return [];

    try {
      const jwtHelper = new JwtHelperService();
      const decodedToken = jwtHelper.decodeToken(token);

      console.log('Decoded token:', decodedToken);

      // ✅ Roles are stored in "authorities" field in the backend
      if (decodedToken.authorities && Array.isArray(decodedToken.authorities)) {
        // Remove "ROLE_" prefix if present
        const roles = decodedToken.authorities.map((role: string) => {
          return role.replace('ROLE_', '');
        });
        console.log('Extracted roles:', roles);
        return roles;
      }

      // ✅ Fallback for other formats
      if (decodedToken.roles && Array.isArray(decodedToken.roles)) {
        return decodedToken.roles.map((role: string) => role.replace('ROLE_', ''));
      }

      if (decodedToken.role) {
        return [decodedToken.role.replace('ROLE_', '')];
      }

      return [];
    } catch (error) {
      console.error('Error decoding token:', error);
      return [];
    }
  }

  // ✅ Check if user has a specific role
  hasRole(role: string): boolean {
    const roles = this.getUserRoles();
    const roleUpper = role.toUpperCase();
    const hasRole = roles.some(r => r.toUpperCase() === roleUpper);
    console.log(`User has role ${role}?`, hasRole, 'Available roles:', roles);
    return hasRole;
  }

  // ✅ Check if user has any of the specified roles
  hasAnyRole(roles: string[]): boolean {
    return roles.some(role => this.hasRole(role));
  }

  // ✅ Get complete user information
  getUserInfo(): any {
    const token = this.token;
    if (!token) return null;

    try {
      const jwtHelper = new JwtHelperService();
      return jwtHelper.decodeToken(token);
    } catch (error) {
      console.error('Error decoding token:', error);
      return null;
    }
  }

  // ✅ Get user email
  getUserEmail(): string | null {
    const userInfo = this.getUserInfo();
    return userInfo?.sub || null;
  }

  // ✅ Clear all data
  clear(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem('loggedUser');
    localStorage.removeItem('isLoggedIn');
  }
}
