import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { HttpHeaders } from '@angular/common/http';
import { LoginService } from '../service/login';
import { UserService } from '../service/user';
import { User } from '../models/user';
import { EditUserDialogComponent } from './edit-user-dialog/edit-user-dialog.component';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {

  user: User = new User();

  constructor(
    private loginService: LoginService,
    private dialog: MatDialog,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      this.user = JSON.parse(userStr);
      console.log("User ID: " + this.user.id);
      console.log("User Role: " + this.user.role);
    }
  }

  openEditDialog(): void {
    const dialogRef = this.dialog.open(EditUserDialogComponent, {
      width: '400px',
      data: { user: this.user }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // تحديث البيانات محليًا
        this.user = { ...this.user, ...result };
        console.log("Updated user:", result);
        this.updateUserToServer(this.user.id, result);
      }
    });
  }


  updateUserToServer(id: any, userData: any): void {
    if (!id) return;

    const oldEmail = this.user.email;  // حفظ البريد القديم

    this.userService.updateUser(userData, id).subscribe({
      next: (updatedUser: User) => {
        console.log("User updated successfully", updatedUser);
        this.user = updatedUser;
        localStorage.setItem('user', JSON.stringify(updatedUser));
        this.loginService.setUserData(updatedUser);

        // ✅ إذا تم تغيير البريد الإلكتروني
        if (oldEmail !== updatedUser.email) {
          alert(`Your email has been changed to: ${updatedUser.email}. You will be logged out. Please login again with your new email.`);
          this.loginService.logOut();  // تسجيل الخروج
        } else {
          alert("Profile updated successfully");
        }
      },
      error: (error: any) => {
        console.error("Error updating user:", error);
        alert("Error: " + (error.error?.message || error.message));
      }
    });
  }


}
