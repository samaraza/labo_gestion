import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { User } from '../../models/user';

@Component({
  selector: 'app-popup-affecter-user',
  templateUrl: './popup-affecter-user.component.html'
})
export class PopupAffecterUserComponent {
  selectedRole: string = 'PROFFESSEUR';

  constructor(
    public dialogRef: MatDialogRef<PopupAffecterUserComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { user: User }
  ) {}

  affecter(): void {
    this.dialogRef.close({ role: this.selectedRole });
  }

  close(): void {
    this.dialogRef.close();
  }
}
