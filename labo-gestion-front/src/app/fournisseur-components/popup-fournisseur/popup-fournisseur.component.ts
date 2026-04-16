import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Fournisseur } from 'src/app/models/fournisseur'; // ✅ تصحيح الإملاء

@Component({
  selector: 'app-popup-fournisseur',
  templateUrl: './popup-fournisseur.component.html',
  styleUrls: ['./popup-fournisseur.component.scss']
})
export class PopupFournisseurComponent  implements OnInit {
     fournisseurForm: FormGroup;
     isEditing = false; // Variable to track if the form is used for editing

     constructor(
       private fb: FormBuilder,
       private dialogRef: MatDialogRef<PopupFournisseurComponent>,
       @Inject(MAT_DIALOG_DATA) public data: any,
     ) {
       this.fournisseurForm = this.fb.group({
         nom: ['', Validators.required],
         adresse: ['', Validators.required],
         email: ['', [Validators.required, Validators.email]],
         nmrTel: ['', Validators.required],
       });
     }

     ngOnInit(): void {
       if (this.data && this.data.fournisseur.id) {
         this.isEditing = true;
         const fournisseur = this.data.fournisseur as Fournisseur;
         this.fournisseurForm.patchValue({
           nom: fournisseur.nom,
           adresse: fournisseur.adresse,
           email: fournisseur.email,
           nmrTel: fournisseur.nmrTel,
         });
       }
     }

    submit() {
      if (this.fournisseurForm.valid) {
        const formValue = this.fournisseurForm.value;
        const payload: any = {
          nom: formValue.nom,
          adresse: formValue.adresse,
          email: formValue.email,
          nmrTel: formValue.nmrTel,
        };
        if (this.isEditing && this.data.fournisseur.id) {
          payload.id = this.data.fournisseur.id;
        }
        this.dialogRef.close(payload);

        console.log('البيانات المرسلة:', JSON.stringify(payload, null, 2));
        alert('البيانات: ' + JSON.stringify(payload, null, 2));
      }
    }

     close(): void {
       this.dialogRef.close();
     }
   }
