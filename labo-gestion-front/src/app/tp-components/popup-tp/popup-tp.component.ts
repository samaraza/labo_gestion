import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TpDTO } from '../../models/tp.dto';
import { User } from '../../models/user';
import { SalleTp } from '../../models/salletp';
import { Produit } from '../../models/produit';
import { Preparation } from '../../models/preparation';
import { UserService } from '../../service/user';
import { SalleTpService } from '../../service/salletp_service';
import { ProduitService } from '../../service/produit_service';
import { PreparationService } from '../../service/preparation_service';
import { TpType } from '../../enums/tp_type';
import { NiveauScolaire } from '../../enums/niveau_scolaire';

@Component({
  selector: 'app-popup-tp',
  templateUrl: './popup-tp.component.html',
  styleUrls: ['./popup-tp.component.scss']
})
export class PopupTpComponent implements OnInit {
  tpForm: FormGroup;
  users: User[] = [];
  salles: SalleTp[] = [];
  produits: Produit[] = [];
  preparations: Preparation[] = [];
  tpTypes = Object.values(TpType);
  niveaux = Object.values(NiveauScolaire);
  isEditing = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<PopupTpComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private userService: UserService,
    private salleService: SalleTpService,
    private produitService: ProduitService,
    private prepService: PreparationService
  ) {
    this.tpForm = this.fb.group({
      type: ['', Validators.required],
      jourTp: ['', Validators.required],
      profId: ['', Validators.required],
      salleTpId: ['', Validators.required],
      niveauScolaire: ['', Validators.required],
      produits: this.fb.array([]),
      preparations: this.fb.array([])
    });
  }

  ngOnInit(): void {
    this.loadLists();
    if (this.data?.tp?.id) {
      this.isEditing = true;
      this.loadTpData();
    }
  }

  loadLists(): void {
    this.userService.getAllUsers().subscribe(data => this.users = data);
    this.salleService.getAllSalleTp().subscribe(data => this.salles = data);
    this.produitService.getAllProduits().subscribe(data => this.produits = data);
    this.prepService.getAllPreparations().subscribe(data => this.preparations = data);
  }

  loadTpData(): void {
    const tp = this.data.tp as TpDTO;
    this.tpForm.patchValue({
      type: tp.type,
      jourTp: tp.jourTp,
      profId: tp.profId,
      salleTpId: tp.salleTpId,
      niveauScolaire: tp.niveauScolaire
    });
    // vider les FormArray
    while (this.produitsFormArray.length) this.produitsFormArray.removeAt(0);
    while (this.preparationsFormArray.length) this.preparationsFormArray.removeAt(0);
    // ajouter les lignes existantes
    tp.produits?.forEach(p => this.addProduit(p.produit, p.quantite));
    tp.preparations?.forEach(p => this.addPreparation(p.preparation, p.quantite));
  }

  get produitsFormArray(): FormArray { return this.tpForm.get('produits') as FormArray; }
  get preparationsFormArray(): FormArray { return this.tpForm.get('preparations') as FormArray; }

  addProduit(produit?: Produit, quantite?: number): void {
    this.produitsFormArray.push(this.fb.group({
      produit: [produit || '', Validators.required],
      quantite: [quantite || '', [Validators.required, Validators.min(1)]]
    }));
  }
  removeProduit(index: number): void { this.produitsFormArray.removeAt(index); }

  addPreparation(preparation?: Preparation, quantite?: number): void {
    this.preparationsFormArray.push(this.fb.group({
      preparation: [preparation || '', Validators.required],
      quantite: [quantite || '', [Validators.required, Validators.min(1)]]
    }));
  }
  removePreparation(index: number): void { this.preparationsFormArray.removeAt(index); }

 submit(): void {
   if (this.tpForm.invalid) return;
   const form = this.tpForm.value;

   const tpDTO: any = {
     jourTp: form.jourTp,
     type: form.type,
     niveauScolaire: form.niveauScolaire,
     profId: +form.profId,
     salleTpId: +form.salleTpId,
     produits: form.produits.map((p: any) => ({
       produitId: p.produit?.id || p.produitId,   // ✅ معرف المنتج
       quantite: p.quantite
     })),
     preparations: form.preparations.map((p: any) => ({
       preparationId: p.preparation?.id || p.preparationId,  // ✅ معرف التحضيرة
       quantite: p.quantite
     }))
   };

   if (this.isEditing && this.data.tp?.id) {
     tpDTO.id = this.data.tp.id;
   }

   console.log('بيانات TP المرسلة:', JSON.stringify(tpDTO, null, 2)); // للتأكد
   this.dialogRef.close(tpDTO);
 }

  close(): void {
    this.dialogRef.close();
  }
}
