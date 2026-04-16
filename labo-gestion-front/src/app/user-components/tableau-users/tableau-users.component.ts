// tableau-users.component.ts
import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { User } from '../../models/user';
import { UserService } from '../../service/user';
import { PopupUsersComponent } from '../popup-users/popup-users.component';
import { LoginService } from '../../service/login';
import { Role } from '../../enums/role';
import { PopupAffecterUserComponent } from '../popup-affecter-user/popup-affecter-user.component';

@Component({
  selector: 'app-tableau-users',
  templateUrl: './tableau-users.component.html',
  styleUrls: ['./tableau-users.component.scss']
})
export class TableauUsersComponent implements OnInit, AfterViewInit {
  users: User[] = [];
  dataSource = new MatTableDataSource<User>([]);
  displayedColumns: string[] = ['firstName', 'lastName', 'email', 'role', 'action'];
  isLoading = false;

  // Role-based access flags
  isAdmin = false;
  isDirector = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private userService: UserService,
    private dialog: MatDialog,
    private loginService: LoginService
  ) {}

  ngOnInit(): void {
    this.getAllUsers();
    this.checkUserRole();

    console.log('===== Current logged in user =====');
    console.log('Email:', this.loginService.connectedUser?.email);
    console.log('Ecole ID:', this.loginService.connectedUser?.ecoleId);
    console.log('Role:', this.loginService.connectedUser?.role);
  }

  // Determine logged-in user's role
  checkUserRole() {
    const userRole = this.loginService.connectedUser?.role;
    this.isAdmin = userRole === Role.ADMINISTRATEUR;
    this.isDirector = userRole === Role.DIRECTEUR;
  }

  getAllUsers(): void {
    this.isLoading = true;
    this.userService.getAllUsers().subscribe({
      next: (data: User[]) => {
        this.users = data;
        this.dataSource.data = this.users;
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading users', err);
        this.isLoading = false;
      }
    });
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  Filterchange(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  // Only Admin can add a user
  addUser(): void {
    if (!this.isAdmin) return;
    this.openPopup(null);
  }

  // Only Admin can edit a user
  editUser(user: User): void {
    if (!this.isAdmin) return;
    this.openPopup(user);
  }

  openPopup(user: User | null): void {
    const dialogRef = this.dialog.open(PopupUsersComponent, {
      width: '650px',
      data: { user: user }
    });
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result) {
        if (user && user.id) {
          const id = typeof user.id === 'string' ? parseInt(user.id, 10) : user.id;
          this.userService.updateFromAdmin(result, id).subscribe(() => this.getAllUsers());
        } else {
          this.userService.addUser(result).subscribe(() => this.getAllUsers());
        }
      }
    });
  }

  // Only Admin can delete a user
  deleteUser(userId: number | undefined): void {
    if (!this.isAdmin) return;
    if (!userId) return;
    const id = typeof userId === 'string' ? parseInt(userId, 10) : userId;
    if (confirm('Are you sure you want to delete this user?')) {
      this.userService.deleteUser(id).subscribe({
        next: () => this.getAllUsers(),
        error: (err) => console.error('Delete error', err)
      });
    }
  }

  // Director: assign existing user to his school
  affecterUser(user: User): void {
    const dialogRef = this.dialog.open(PopupAffecterUserComponent, {
      width: '400px',
      data: { user: user }
    });
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result && result.role) {
        this.userService.affecterUserToEcole(user.id, result.role).subscribe({
          next: () => {
            alert(`User ${user.firstname} ${user.lastname} successfully assigned to your school.`);
            // Optionally refresh the list
            // this.getAllUsers();
          },
          error: (err) => console.error('Assignment error', err)
        });
      }
    });
  }
}
