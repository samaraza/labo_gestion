import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Produit } from '../../models/produit';
import { ProduitService } from '../../service/produit_service';
import { LoginService } from '../../service/login';
import { PopupProduitComponent } from '../popup-produit/popup-produit.component';
import { Role } from '../../enums/role';

@Component({
  selector: 'app-tableau-produit',
  templateUrl: './tableau-produit.component.html',
  styleUrls: ['./tableau-produit.component.scss']
})
export class TableauProduitComponent implements OnInit, AfterViewInit {
  produits: Produit[] = [];
  connectedUserRole?: Role;
  roles = Object.values(Role);

  dataSource: MatTableDataSource<Produit> = new MatTableDataSource<Produit>(this.produits);
  displayedColumns: string[] = ['reference', 'designation', 'type', 'dateExp', 'categorie', 'rubrique', 'durabilite', 'quantiteInitiale', 'fournisseur', 'uniteMesure', 'action'];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  isLoading = false;

  constructor(
    private produitService: ProduitService,
    private loginService: LoginService,
    private dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.connectedUserRole = this.loginService.connectedUser?.role;
    this.getAllProduits();
  }

  isEmpty(value: any): boolean {
    return value == null || value === '';
  }

  getAllProduits(): void {
    this.isLoading = true;
    this.produitService.getAllProduits().subscribe({
      next: (data: Produit[]) => {
        this.produits = data;
        this.dataSource.data = this.produits;
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error fetching produits:', error);
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

  editProduit(produit: Produit): void {
    this.openPopup('Modifier produit', produit);
  }

  deleteProduit(id: number | undefined): void {
    if (!id) return;
    if (window.confirm('Voulez-vous vraiment supprimer ce produit ?')) {
      this.produitService.deleteProduit(id).subscribe({
        next: () => this.getAllProduits(),
        error: (error: any) => console.error('Error deleting produit:', error)
      });
    }
  }

  addProduit(): void {
    this.openPopup('Ajouter un produit');
  }

  openPopup(title: string, produit?: Produit): void {
    const dialogRef = this.dialog.open(PopupProduitComponent, {
      width: '600px',
      data: { title, produit: produit || new Produit() }
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result) {
        if (produit?.id) {
          const idProduit = typeof produit.id === 'string' ? parseInt(produit.id, 10) : produit.id;
          this.updateProduitToServer(idProduit, result);
        } else {
          this.addProduitToServer(result);
        }
      }
    });
  }


  addProduitToServer(produit: any): void {
    this.produitService.addProduit(produit).subscribe({
      next: () => this.getAllProduits(),
      error: (error: any) => {
        console.error('Error adding produit:', error);
        // Show user-friendly message
        if (error.error?.businessErrorDescription) {
          alert(error.error.businessErrorDescription);
        } else if (error.status === 500) {
          alert('Erreur serveur: vérifiez référence unique et format date (YYYY-MM-DD)');
        } else {
          alert('Erreur lors de l\'ajout du produit');
        }
      }
    });
  }

   // Add this method after addProduitToServer()
   updateProduitToServer(id: number, produit: any): void {
     this.produitService.updateProduit(produit, id).subscribe({
       next: () => this.getAllProduits(),
       error: (error: any) => console.error('Error updating produit:', error)
     });
   }

}
