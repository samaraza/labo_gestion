import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { HttpHeaders } from '@angular/common/http';
import { SalleTp } from '../../models/salletp';
import { ArmoireService } from '../../service/armoire_service';
import { LoginService } from '../../service/login';

@Component({
  selector: 'app-popup-salletp',
  templateUrl: './popup-salletp.component.html',
  styleUrls: ['./popup-salletp.component.scss']
})
export class PopupSalletpComponent implements OnInit {
  salleTpForm: FormGroup;
  armoires: any[] = [];
  isEditing = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<PopupSalletpComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private armoireService: ArmoireService,
    private loginService: LoginService
  ) {
    this.salleTpForm = this.fb.group({
      numero: ['', Validators.required],
      armoires: [[], Validators.required]
    });
  }

  ngOnInit(): void {
    this.getAllArmoires();

    if (this.data && this.data.salleTp && this.data.salleTp.id) {
      this.isEditing = true;
      const salleTp = this.data.salleTp as SalleTp;
      this.salleTpForm.patchValue({
        numero: salleTp.numero,
        armoires: salleTp.armoires ? salleTp.armoires.map(a => a.id) : []
      });
    }
  }

  getAllArmoires(): void {
    // ❌ تم حذف headers (الـ Interceptor سيضيف التوكن)
    this.armoireService.getAllArmoires().subscribe({
      next: (data: any[]) => {
        this.armoires = data;
      },
      error: (error: any) => {
        console.error('Error fetching armoires:', error);
      }
    });
  }

  submit(): void {
    if (this.salleTpForm.valid) {
      const formValue = this.salleTpForm.value;
      const payload: any = {
        numero: formValue.numero,
        armoires: formValue.armoires.map((id: number) => ({ id }))
      };
      if (this.isEditing && this.data.salleTp.id) {
        payload.id = this.data.salleTp.id;
      }
      this.dialogRef.close(payload);
    }
  }

  close(): void {
    this.dialogRef.close();
  }
}
