import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { HttpHeaders } from '@angular/common/http';
import { Produit } from '../../models/produit';
import { ProduitService } from '../../service/produit_service'; // ✅
import { FournisseurService } from '../../service/fournisseur_service'; // ✅
import { Category } from '../../enums/category';
import { Rubrique } from '../../enums/rubrique';
import { Durabilite } from '../../enums/durabilitie';
import { UniteMesure } from '../../enums/uniteMesure';
import { ProduitType } from '../../enums/produit_type';
import { LoginService } from '../../service/login'; // ✅

@Component({
  selector: 'app-popup-produit',
  templateUrl: './popup-produit.component.html',
  styleUrls: ['./popup-produit.component.scss']
})
export class PopupProduitComponent implements OnInit {
  produitForm: FormGroup;
  isEditing = false;
  categories = Object.values(Category);
  rubriques = Object.values(Rubrique);
  durabilites = Object.values(Durabilite);
  uniteMesures = Object.values(UniteMesure);
  produitTypes = Object.values(ProduitType);
  fournisseurs: any[] = [];

  constructor(
    private loginService: LoginService,
    private fournisseurService: FournisseurService,
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<PopupProduitComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.produitForm = this.fb.group({
      designation: ['', Validators.required],
      reference: ['', Validators.required],
      type: ['', Validators.required],
      dateExp: ['', Validators.required],
      categorie: ['', Validators.required],
      rubrique: ['', Validators.required],
      durabilite: ['', Validators.required],
      quantiteInitiale: ['', [Validators.required, Validators.min(0)]],
      fournisseur: ['', Validators.required],
      uniteMesure: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.getAllFournisseurs();
    if (this.data?.produit?.id) {
      this.isEditing = true;
      this.prefillForm();
    }
  }

  getAllFournisseurs(): void {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + localStorage.getItem('token')
    });

    this.fournisseurService.getAllFournisseurs().subscribe({
      next: (data: any[]) => {
        this.fournisseurs = data;
        if (this.isEditing) {
          this.prefillForm();
        }
      },
      error: (error: any) => {
        console.error('Error fetching fournisseurs:', error);
      }
    });
  }

  prefillForm(): void {
    if (this.data?.produit) {
      const produit = this.data.produit as Produit;
      this.produitForm.patchValue({
        designation: produit.designation,
        reference: produit.reference,
        type: produit.type,
        dateExp: produit.dateExp,
        categorie: produit.categorie,
        rubrique: produit.rubrique,
        durabilite: produit.durabilite,
        quantiteInitiale: produit.quantiteInitiale,
        fournisseur: produit.fournisseur,
        uniteMesure: produit.uniteMesure
      });
    }
  }

  submit(): void {
    if (this.produitForm.valid) {
      const formValue = this.produitForm.value;

      // ✅ Convert date to YYYY-MM-DD format
      let dateExpFormatted = formValue.dateExp;
      if (dateExpFormatted) {
        const date = new Date(dateExpFormatted);
        if (!isNaN(date.getTime())) {
          dateExpFormatted = date.toISOString().split('T')[0]; // "2027-05-20"
        }
      }

      const payload: any = {
        designation: formValue.designation,
        reference: formValue.reference,
        dateExp: dateExpFormatted,   // ✅ Use formatted date
        type: formValue.type,
        quantiteInitiale: formValue.quantiteInitiale,
        categorie: formValue.categorie,   // ✅ Will be "CHIMIE", etc.
        rubrique: formValue.rubrique,
        durabilite: formValue.durabilite,
        uniteMesure: formValue.uniteMesure,
        fournisseurId: formValue.fournisseur ? formValue.fournisseur.id : null
      };

      if (this.isEditing && this.data.produit.id) {
        payload.id = this.data.produit.id;
      }
      this.dialogRef.close(payload);
    }
    }

    close(): void {
        this.dialogRef.close();
      }


}
