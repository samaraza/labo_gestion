import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { HttpHeaders } from '@angular/common/http';
import { Preparation } from '../../models/preparation';
import { ProduitService } from '../../service/produit_service';
import { LoginService } from '../../service/login';

// ✅ دالة التحقق من أن الكمية لا تتجاوز الكمية المتاحة
export function quantityValidator(produitControlName: string, quantiteControlName: string) {
  return (group: AbstractControl): ValidationErrors | null => {
    const produit = group.get(produitControlName)?.value;
    const quantite = group.get(quantiteControlName)?.value;

    if (produit && quantite && produit.quantiteInitiale) {
      if (quantite > produit.quantiteInitiale) {
        group.get(quantiteControlName)?.setErrors({ quantityExceeds: true });
        return { quantityExceeds: true };
      } else {
        group.get(quantiteControlName)?.setErrors(null);
      }
    }
    return null;
  };
}

@Component({
  selector: 'app-popup-preparation',
  templateUrl: './popup-preparation.component.html',
  styleUrls: ['./popup-preparation.component.scss']
})
export class PopupPreparationComponent implements OnInit {
  preparationForm: FormGroup;
  produits: any[] = [];
  isEditing = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<PopupPreparationComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private produitService: ProduitService,
    private loginService: LoginService
  ) {
    this.preparationForm = this.fb.group({
      designation: ['', Validators.required],
      date: ['', Validators.required],
      produit1: ['', Validators.required],
      quantite1: ['', [Validators.required, Validators.min(1)]],
      produit2: ['', Validators.required],
      quantite2: ['', [Validators.required, Validators.min(1)]],
      quantite: ['', [Validators.required, Validators.min(1)]],
    }, { validators: [
      quantityValidator('produit1', 'quantite1'),
      quantityValidator('produit2', 'quantite2')
    ]});
  }

  ngOnInit(): void {
    this.getAllProduits();
    if (this.data && this.data.preparation && this.data.preparation.id) {
      this.isEditing = true;
      this.loadData();
    }
  }

  loadData(): void {
    const preparation = this.data.preparation as Preparation;
    this.preparationForm.patchValue({
      designation: preparation.designation,
      date: preparation.date,
      produit1: preparation.produit1,
      quantite1: preparation.quantite1,
      produit2: preparation.produit2,
      quantite2: preparation.quantite2,
      quantite: preparation.quantite,
    });
  }

  getAllProduits(): void {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + localStorage.getItem('token')
    });

    this.produitService.getAllProduits().subscribe({
      next: (data: any[]) => {
        this.produits = data;
      },
      error: (error: any) => {
        console.error('Error fetching produits:', error);
      }
    });
  }

  submit(): void {
    if (this.preparationForm.valid) {
      const formValue = this.preparationForm.value;

      // تحويل التاريخ
      let dateValue = formValue.date;
      if (dateValue) {
        // إذا كان التاريخ من نوع string بصيغة dd/MM/yyyy
        if (typeof dateValue === 'string' && dateValue.includes('/')) {
          const parts = dateValue.split('/');
          if (parts.length === 3) {
            dateValue = `${parts[2]}-${parts[1]}-${parts[0]}T00:00:00`;
          } else {
            const dateObj = new Date(dateValue);
            if (!isNaN(dateObj.getTime())) {
              dateValue = dateObj.toISOString();
            }
          }
        } else if (dateValue instanceof Date) {
          dateValue = dateValue.toISOString();
        }
      }

      // دالة لاستخراج الرقم من النص (مثل "300ml" -> 300)
      const parseQuantity = (value: any): number | null => {
        if (value === null || value === undefined || value === '') return null;
        if (typeof value === 'number') return value;
        const num = parseInt(value.toString().replace(/[^0-9-]/g, ''), 10);
        return isNaN(num) ? null : num;
      };

      const quantite = parseQuantity(formValue.quantite);
      const quantite1 = parseQuantity(formValue.quantite1);
      const quantite2 = parseQuantity(formValue.quantite2);

      // التحقق من صحة الكميات
      if (quantite === null || quantite <= 0) {
        alert('La quantité doit être un nombre valide > 0');
        return;
      }
      if (quantite1 === null || quantite1 <= 0) {
        alert('La quantité 1 doit être un nombre valide > 0');
        return;
      }
      if (formValue.produit2 && (quantite2 === null || quantite2 <= 0)) {
        alert('La quantité 2 doit être un nombre valide > 0');
        return;
      }

      const payload: any = {
        designation: formValue.designation,
        date: dateValue,
        quantite: quantite,
        produit1Id: formValue.produit1.id,
        quantite1: quantite1,
        produit2Id: formValue.produit2 ? formValue.produit2.id : null,
        quantite2: quantite2 || null
      };

      if (this.isEditing && this.data.preparation.id) {
        payload.id = this.data.preparation.id;
      }

      console.log('Payload envoyé:', payload);
      this.dialogRef.close(payload);
    }
  }

  close(): void {
    this.dialogRef.close();
  }
}
