import { Component, EventEmitter, HostListener, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from 'src/app/service/login';
import { navbarData } from './nav-data';
import { animate, style, transition, trigger } from '@angular/animations';
import { Role } from '../enums/role';

interface SideNavToggle {
  screenWidth: number;
  collapsed: boolean;
}

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.scss'],
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('300ms', style({ opacity: 1 }))
      ]),
      transition(':leave', [
        style({ opacity: 1 }),
        animate('300ms', style({ opacity: 0 }))
      ])
    ])
  ]
})
export class SidenavComponent implements OnInit {

  @Output() onToggleSideNav: EventEmitter<SideNavToggle> = new EventEmitter();

  collapsed = false;
  screenWidth = 0;

  navData = navbarData;
  connectedUserRole?: Role;

  constructor(
    private loginService: LoginService,
    private router: Router
  ) {}

  @HostListener('window:resize')
  onResize() {
    this.screenWidth = window.innerWidth;
    if (this.screenWidth <= 768) {
      this.collapsed = false;
      this.onToggleSideNav.emit({
        collapsed: this.collapsed,
        screenWidth: this.screenWidth
      });
    }
  }

  ngOnInit(): void {
    this.screenWidth = window.innerWidth;

    // ✅ تحميل بيانات المستخدم من localStorage (أو من الخدمة)
    const user = this.loginService.getUserData();
    if (user && user.role) {
      this.connectedUserRole = user.role;
    } else {
      this.connectedUserRole = this.loginService.connectedUser?.role;
    }

    this.filterNavData();
  }

  filterNavData(): void {
    if (this.connectedUserRole) {
      this.navData = navbarData.filter(item =>
        item.roles.includes(this.connectedUserRole!)
      );
    } else {
      this.navData = [];
    }
  }

  toggleCollapse(): void {
    this.collapsed = !this.collapsed;
    this.onToggleSideNav.emit({
      collapsed: this.collapsed,
      screenWidth: this.screenWidth
    });
  }

  closeSidenav(): void {
    this.collapsed = false;
    this.onToggleSideNav.emit({
      collapsed: this.collapsed,
      screenWidth: this.screenWidth
    });
  }

  logout(): void {
    this.loginService.logOut();
    this.router.navigate(['/login']);
  }
}
