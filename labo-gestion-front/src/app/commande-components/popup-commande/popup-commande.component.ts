import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Commande } from 'src/app/models/commande';
import { User } from 'src/app/models/user';
import { FournisseurService } from 'src/app/service/fournisseur_service';
import { LoginService } from 'src/app/service/login';
import { ProduitService } from 'src/app/service/produit_service';
import { UserService } from 'src/app/service/user';
import { Role } from 'src/app/enums/role';
import { ChangeDetectorRef } from '@angular/core';
@Component({
  selector: 'app-popup-commande',
  templateUrl: './popup-commande.component.html',
  styleUrls: ['./popup-commande.component.scss']
})
export class PopupCommandeComponent implements OnInit {
  commandeForm: FormGroup;
  produits: any[] = [];
  fournisseurs: any[] = [];
  directeurs: any[] = [];
  isEditing = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<PopupCommandeComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private produitService: ProduitService,
    private fournisseurService: FournisseurService,
    private userService: UserService,
    private loginService: LoginService,
    private cdr: ChangeDetectorRef
  ) {
    this.commandeForm = this.fb.group({
      numero: ['', Validators.required],
      date: ['', Validators.required],
      observation: ['', Validators.required],
      designation: ['', Validators.required],
      fournisseur: ['', Validators.required],
      directeur: ['', Validators.required],
      produits: this.fb.array([], Validators.required)
    });
  }

  ngOnInit(): void {
    if (this.data?.commande?.id) {
      this.isEditing = true;
      this.loadData();
    } else {
      this.getAllFournisseurs();
      this.getAllDirecteurs();
      this.getAllProduits();
    }
  }

  loadData(): void {
      this.getAllDirecteurs().then(() => {
          this.getAllFournisseurs().then(() => {
              this.getAllProduits().then(() => {
                  const commande = this.data.commande as Commande;

                  // Recherche l'objet Fournisseur complet à partir de fournisseurId
                  const fournisseurTrouve = commande.fournisseurId
                      ? this.fournisseurs.find(f => f.id === commande.fournisseurId)
                      : null;

                  // Recherche l'objet User (directeur) complet à partir de userId
                  const directeurTrouve = commande.userId
                      ? this.directeurs.find(d => d.id === commande.userId)
                      : null;

                  this.commandeForm.patchValue({
                      designation: commande.designation,
                      date: commande.date,
                      observation: commande.observation,
                      numero: commande.numero,
                      fournisseur: fournisseurTrouve,
                      directeur: directeurTrouve
                  });

                  // Gestion des produits
                  if (commande.produits && commande.produits.length > 0) {
                      commande.produits.forEach(pc => {
                          // pc contient : produitId, produitDesignation, quantiteAjoutee
                          const produitComplet = this.produits.find(p => p.id === pc.produitId);
                          this.addProduit(produitComplet, pc.quantiteAjoutee);
                      });
                  }
              });
          });
      });
  }

  get produitsFormArray(): FormArray {
    return this.commandeForm.get('produits') as FormArray;
  }

  addProduit(produit?: any, quantiteAjoutee?: number): void {
    const produitFormGroup = this.fb.group({
      produit: [produit || '', Validators.required],
      quantiteAjoutee: [quantiteAjoutee || '', Validators.required]
    });
    this.produitsFormArray.push(produitFormGroup);
  }

  removeProduit(index: number): void {
    this.produitsFormArray.removeAt(index);
  }

  getAllProduits(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this.produitService.getAllProduits().subscribe(
        (data: any) => {
          this.produits = data;
          resolve();
        },
        (error: any) => {
          console.error('Error fetching produits:', error);
          reject(error);
        }
      );
    });
  }

  getAllFournisseurs(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      this.fournisseurService.getAllFournisseurs().subscribe(
        (data: any) => {
          this.fournisseurs = data;
          resolve();
        },
        (error: any) => {
          console.error('Error fetching fournisseurs:', error);
          reject(error);
        }
      );
    });
  }

getAllDirecteurs(): Promise<void> {
  return new Promise<void>((resolve, reject) => {
    this.userService.getAllUsers().subscribe(
      (data: any) => {
        this.directeurs = data.filter((user: User) => user.role === Role.DIRECTEUR);
        console.log('Directeurs:', this.directeurs);
        this.cdr.detectChanges();   // <-- إجبار التحديث
        resolve();
      },
      (error) => reject(error)
    );
  });
}

 submit(): void {
   if (this.commandeForm.valid) {
     const formValue = this.commandeForm.value;

     // تحويل التاريخ من "02/03/2025" إلى "2025-03-02T00:00:00"
     let formattedDate = null;
     if (formValue.date) {
       const parts = formValue.date.split('/'); // ["02", "03", "2025"]
       if (parts.length === 3) {
         // أنشئ كائن Date مع الانتباه: الشهر يبدأ من 0
         const dateObj = new Date(+parts[2], +parts[1] - 1, +parts[0]);
         formattedDate = dateObj.toISOString(); // "2025-03-02T00:00:00.000Z"
       }
     }

     const produits = formValue.produits.map((prod: any) => ({
       produit: prod.produit,
       quantiteAjoutee: prod.quantiteAjoutee
     }));

     const payload = {
       id: this.data.commande?.id,
       designation: formValue.designation,
       date: formattedDate,  // أرسل الصيغة الجديدة
       observation: formValue.observation,
       numero: formValue.numero,
       fournisseur: formValue.fournisseur,
       user: formValue.directeur ? { id: formValue.directeur.id } : null,
       produits: produits
     };
     this.dialogRef.close(payload);
   }
 }

  close(): void {
    this.dialogRef.close();
  }
}
