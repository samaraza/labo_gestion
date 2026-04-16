// src/app/fournisseur-components/tableau-fournisseur/tableau-fournisseur.component.ts
import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Fournisseur } from 'src/app/models/fournisseur';
import { FournisseurService } from 'src/app/service/fournisseur_service';
import { LoginService } from 'src/app/service/login';
import { PopupFournisseurComponent } from '../popup-fournisseur/popup-fournisseur.component';

@Component({
  selector: 'app-tableau-fournisseur',
  templateUrl: './tableau-fournisseur.component.html',
  styleUrls: ['./tableau-fournisseur.component.scss']
})
export class TableauFournisseurComponent implements OnInit, AfterViewInit {
  fournisseurs: Fournisseur[] = [];
  dataSource: MatTableDataSource<Fournisseur> = new MatTableDataSource<Fournisseur>(this.fournisseurs);
  displayedColumns: string[] = ["nom", "adresse", "email", "nmrTel", "action"];
  isLoading = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private fournisseurService: FournisseurService,
    private loginService: LoginService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.getAllFournisseurs();
  }

  getAllFournisseurs(): void {
    this.isLoading = true;
    this.fournisseurService.getAllFournisseurs().subscribe({
      next: (data: Fournisseur[]) => {
        this.fournisseurs = data;
        this.dataSource.data = this.fournisseurs;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error fetching fournisseurs:', error);
        this.isLoading = false;
      }
    });
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  Filterchange(event: Event): void {
    const value = (event.target as HTMLInputElement).value.trim().toLowerCase();
    this.dataSource.filter = value;
  }

  editFournisseur(fournisseur: Fournisseur): void {
    this.openPopup('Modifier fournisseur', fournisseur);
  }

  deleteFournisseur(id: number): void {
    if (window.confirm("Vous êtes sûr de retirer le fournisseur ?")) {
      this.fournisseurService.deleteFournisseur(id).subscribe(() => {
        this.getAllFournisseurs();
      });
    }
  }

  addFournisseur(): void {
    this.openPopup('Ajouter un fournisseur');
  }

  openPopup(title: string, fournisseur?: Fournisseur): void {
    const dialogRef = this.dialog.open(PopupFournisseurComponent, {
      width: '40%',
      data: { title, fournisseur: fournisseur || new Fournisseur() }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        if (fournisseur?.id) {
          const idFournisseur = typeof fournisseur.id === 'string' ? parseInt(fournisseur.id, 10) : fournisseur.id;
          this.updateFournisseurToServer(idFournisseur, result);
        } else {
          this.addFournisseurToServer(result);
        }
      }
    });
  }

 addFournisseurToServer(fournisseur: any): void {
   this.fournisseurService.addFournisseur(fournisseur).subscribe({
     next: () => this.getAllFournisseurs(),
     error: (err) => {
       console.error('Error adding fournisseur:', err);
       alert('Erreur lors de l\'ajout du fournisseur. Vérifiez la console.');
     }
   });
 }

  updateFournisseurToServer(id: number, fournisseur: any): void {
    this.fournisseurService.updateFournisseur(fournisseur, id).subscribe(() => {
      this.getAllFournisseurs();
    });
  }
}
