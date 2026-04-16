import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { User } from '../../models/user';

@Component({
  selector: 'app-edit-user-dialog',
  templateUrl: './edit-user-dialog.component.html',
  styleUrls: ['./edit-user-dialog.component.scss']
})
export class EditUserDialogComponent {

  editUserForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<EditUserDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { user: User }
  ) {
    this.editUserForm = this.fb.group({
      firstName: [data.user?.firstname || '', Validators.required],   // ✅ مطابق للـ HTML
      lastName: [data.user?.lastname || '', Validators.required],     // ✅ مطابق للـ HTML
      email: [data.user?.email || '', [Validators.required, Validators.email]],
      password: ['']
    });
  }

  save(): void {
    if (this.editUserForm.valid) {
      const formValue = this.editUserForm.value;
      const updatedData: any = {
        firstName: formValue.firstName,   // ✅ كما ينتظر الخادم
        lastName: formValue.lastName,     // ✅ كما ينتظر الخادم
        email: formValue.email,
      };
      if (formValue.password) {
        updatedData.password = formValue.password;
      }
      this.dialogRef.close(updatedData);
    }
  }

  close(): void {
    this.dialogRef.close();
  }
}
