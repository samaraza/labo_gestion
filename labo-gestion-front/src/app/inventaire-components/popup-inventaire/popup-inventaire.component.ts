import { Component, Inject, OnInit } from '@angular/core';
import { HttpHeaders } from '@angular/common/http'; // ✅ أضف هذا
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Inventaire } from 'src/app/models/inventaire';
import { LoginService } from 'src/app/service/login'; // ✅ تصحيح المسار
import { ProduitService } from 'src/app/service/produit_service';

@Component({
  selector: 'app-popup-inventaire',
  templateUrl: './popup-inventaire.component.html',
  styleUrls: ['./popup-inventaire.component.scss']
})
export class PopupInventaireComponent  implements OnInit {
       inventaireForm: FormGroup;
       isEditing = false;

       produits: any[] = [];

       constructor(
         private loginService: LoginService,
         private produitService: ProduitService,
         private fb: FormBuilder,
         private dialogRef: MatDialogRef<PopupInventaireComponent>,
         @Inject(MAT_DIALOG_DATA) public data: any,
       ) {
         this.inventaireForm = this.fb.group({
           anneeScolaire: ['', Validators.required],
           commentaire: ['', Validators.required],
           date: ['', Validators.required],
           responsable: ['', Validators.required],
           produit: ['', Validators.required],
           quantiteRestante:['', Validators.required]

         });
       }


       formatDateForBackend(dateStr: string): string {
         if (!dateStr) return '';

         // إذا كانت الصيغة تحتوي على '/' (مثل 02/05/2026)
         if (dateStr.includes('/')) {
           const parts = dateStr.split('/');
           if (parts.length === 3) {
             const day = parts[0].padStart(2, '0');
             const month = parts[1].padStart(2, '0');
             const year = parts[2];
             return `${year}-${month}-${day}T00:00:00`;
           }
         }

         // إذا كانت الصيغة تحتوي على '-' (مثل 2025-05-20)
         if (dateStr.includes('-')) {
           // إضافة الوقت إذا لم يكن موجودًا
           if (!dateStr.includes('T')) {
             return dateStr + 'T00:00:00';
           }
         }

         return dateStr;
       }

       getAllProduits(): void {
         const headers = new HttpHeaders({
           'Content-Type': 'application/json',
           Authorization: 'Basic ' + btoa(this.loginService.connectedUser.email + ':' + this.loginService.connectedUser.password)
         });

         this.produitService.getAllProduits().subscribe(
           data => {
             this.produits = data;
             if (this.isEditing) {
               this.prefillForm();
             }
           },
           error => {
             console.error('Error fetching produits:', error);
           }
         );
       }

       ngOnInit(): void {
         this.getAllProduits();
         if (this.data && this.data.inventaire && this.data.inventaire.id) {
           this.isEditing = true;
         }
       }

       prefillForm() {
         if (this.data && this.data.inventaire) {
           const inventaire = this.data.inventaire as Inventaire;
           this.inventaireForm.patchValue({
             anneeScolaire: inventaire.anneeScolaire,
             commentaire: inventaire.commentaire,
             date: inventaire.date,
             responsable: inventaire.responsable,
             produit: this.produits.find(f => f.id === inventaire.produit!.id),
             quantiteRestante:inventaire.quantiteRestante
           });
         }
       }

      submit() {
        if (this.inventaireForm.valid) {
          const formValue = this.inventaireForm.value;
          const payload = {
            anneeScolaire: formValue.anneeScolaire,
            commentaire: formValue.commentaire,
            date: this.formatDateForBackend(formValue.date),
            responsable: formValue.responsable,
            produitId: formValue.produit.id,   // ✅ id فقط
            quantiteRestante: formValue.quantiteRestante
          };
          this.dialogRef.close(payload);
        }
      }

       close(): void {
         this.dialogRef.close();
       }
     }


