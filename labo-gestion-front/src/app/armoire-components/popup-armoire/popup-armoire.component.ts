import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { HttpHeaders } from '@angular/common/http';
import { Armoire } from '../../models/armoire';
import { Produit } from '../../models/produit';
import { ProduitService } from '../../service/produit_service';

@Component({
  selector: 'app-popup-armoire',
  templateUrl: './popup-armoire.component.html',
  styleUrls: ['./popup-armoire.component.scss']
})
export class PopupArmoireComponent implements OnInit {
  armoireForm: FormGroup;
  produits: Produit[] = [];
  isEditing = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<PopupArmoireComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private produitService: ProduitService
  ) {
    this.armoireForm = this.fb.group({
      designation: ['', Validators.required],
      produits: [[], Validators.required]
    });
  }

  ngOnInit(): void {
    this.getAllProduits();
    if (this.data?.armoire?.id) {
      this.isEditing = true;
      const armoire = this.data.armoire as Armoire;
      this.armoireForm.patchValue({
        designation: armoire.designation,
        produits: armoire.produits?.map(p => p.id) || []
      });
    }
  }

 getAllProduits(): void {
   // لا حاجة لإنشاء headers، الخدمة تديرها بنفسها
   this.produitService.getAllProduits().subscribe({
     next: (data: Produit[]) => {
       this.produits = data;
     },
     error: (error: any) => {
       console.error('Error fetching produits:', error);
     }
   });
 }

  submit(): void {
    if (this.armoireForm.valid) {
      const formValue = this.armoireForm.value;
      const payload: any = {
        designation: formValue.designation,
        produits: formValue.produits.map((id: number) => ({ id }))
      };
      if (this.isEditing && this.data.armoire.id) {
        payload.id = this.data.armoire.id;
      }
      this.dialogRef.close(payload);
    }
  }

  close(): void {
    this.dialogRef.close();
  }
}
