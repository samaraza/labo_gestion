import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SalleTpService } from 'src/app/service/salletp_service';
import { LoginService } from 'src/app/service/login';
import { LaboType } from '../../models/labotype';
import { Labo } from '../../models/labo';

@Component({
  selector: 'app-popup',
  templateUrl: './popup.component.html',
  styleUrls: ['./popup.component.scss']
})
export class PopupComponent implements OnInit {
  laboForm: FormGroup;
  laboTypes = Object.keys(LaboType);
  sallesTp: any[] = [];
  isEditing = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<PopupComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private salleTpService: SalleTpService,
    private loginService: LoginService
  ) {
    this.laboForm = this.fb.group({
      laboType: ['', Validators.required],
      sallesTp: [[], Validators.required]
    });
  }

  ngOnInit(): void {
    this.getAllSalleTp();
    if (this.data?.labo?.id) {
      this.isEditing = true;
      const labo = this.data.labo as Labo;
      this.laboForm.patchValue({
        laboType: labo.laboType,
        sallesTp: labo.salleTps ? labo.salleTps.map(s => s.id) : []
      });
    }
  }

  getAllSalleTp(): void {
    this.salleTpService.getAllSalleTp().subscribe((data: any[]) => {
      if (this.isEditing && this.data.labo.id) {
        // حالة التعديل: نعرض القاعات التي لا تنتمي لأي لابو + القاعات التي تنتمي لهذا اللابو
        this.sallesTp = data.filter(st => !st.labo || st.labo.id === this.data.labo.id);
      } else {
        // حالة الإضافة: نعرض فقط القاعات التي لا تنتمي لأي لابو (labo_id = null)
        this.sallesTp = data.filter(st => !st.labo);
      }
    });
  }

  submit(): void {
    if (this.laboForm.valid) {
      const formValue = this.laboForm.value;
      const payload = {
        id: this.data.labo?.id,
        laboType: formValue.laboType,
        salleTps: formValue.sallesTp.map((id: number) => ({ id }))
      };
      this.dialogRef.close(payload);
    }
  }

  close(): void {
    this.dialogRef.close();
  }
}
