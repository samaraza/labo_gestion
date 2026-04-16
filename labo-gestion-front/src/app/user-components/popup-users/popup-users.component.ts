import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { User } from '../../models/user';
import { Role } from '../../enums/role';

@Component({
  selector: 'app-popup-users',
  templateUrl: './popup-users.component.html',
  styleUrls: ['./popup-users.component.scss']
})
export class PopupUsersComponent implements OnInit {
  userForm: FormGroup;
  isEditing = false;
  roles = Object.values(Role);

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<PopupUsersComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.userForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      role: ['', Validators.required],
      password: ['']
    });
  }

  ngOnInit(): void {
    if (this.data && this.data.user && this.data.user.id) {
      this.isEditing = true;
      const user = this.data.user as User;

      // Pour l'édition, le mot de passe n'est pas requis
      this.userForm.get('password')?.clearValidators();
      this.userForm.get('password')?.updateValueAndValidity();

      this.userForm.patchValue({
        firstName: user.firstname,
        lastName: user.lastname,
        email: user.email,
        role: user.role,
        password: ''
      });
    } else {
      // Pour l'ajout, le mot de passe est requis
      this.userForm.get('password')?.setValidators([Validators.required, Validators.minLength(6)]);
      this.userForm.get('password')?.updateValueAndValidity();
    }
  }

 submit() {
   if (this.userForm.valid) {
     const formValue = this.userForm.value;
     const payload: any = {
       firstName: formValue.firstName,
       lastName: formValue.lastName,
       email: formValue.email,
       role: formValue.role,
     };

     if (this.isEditing && this.data.user.id) {
       payload.id = this.data.user.id;
       // هنا لا نضيف password أبداً
     } else {
       // حالة الإضافة فقط نضيف password
       payload.password = formValue.password;
     }

     this.dialogRef.close(payload);
   }
 }

  close(): void {
    this.dialogRef.close();
  }
}
