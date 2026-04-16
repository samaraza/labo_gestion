import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Commande } from 'src/app/models/commande';
import { CommandeService } from 'src/app/service/commande_service';
import { LoginService } from 'src/app/service/login';
import { PopupCommandeComponent } from '../popup-commande/popup-commande.component';
import { Role } from 'src/app/enums/role';

@Component({
  selector: 'app-tableau-commande',
  templateUrl: './tableau-commande.component.html',
  styleUrls: ['./tableau-commande.component.scss']
})
export class TableauCommandeComponent implements OnInit, AfterViewInit {
  commandes: Commande[] = [];
  headers: any;
  connectedUserRole?: Role;
  roles = Object.keys(Role);
  dataSource: MatTableDataSource<Commande> = new MatTableDataSource<Commande>(this.commandes);
  displayedColumns: string[] = ["numero", "designation", "date", "observation", "directeur", "fournisseur", "produits", "quantites", "action"];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private commandeService: CommandeService,
    private loginService: LoginService,
    private dialog: MatDialog
  ) {}

  isLoading = false;

  ngOnInit(): void {
    this.connectedUserRole = this.loginService.connectedUser.role;
    this.headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: 'Basic ' + btoa(this.loginService.connectedUser.email + ':' + this.loginService.connectedUser.password)
    });
    this.getAllCommandes();
  }

  // تعديل دالة generatePDF
  generatePDF(id: any) {
    console.log("dkhal " + id);
    this.commandeService.generatePDF(id).subscribe(
      (response: any) => {
        const blob = new Blob([response], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blob);
        window.open(url);
      },
      (error: any) => {
        console.error('Error generating PDF:', error);
      }
    );
  }

  public getAllCommandes(): void {
    this.isLoading = true;
    this.commandeService.getAllCommandes().subscribe(
      (data: any) => {
        this.commandes = data;
        this.dataSource.data = this.commandes;
        this.isLoading = false;
      },
      (error: any) => {
        this.isLoading = false;
      }
    );
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  Filterchange(data: Event) {
    const value = (data.target as HTMLInputElement).value.trim().toLowerCase();
    this.dataSource.filter = value;
  }

  editCommande(commande: Commande) {
    this.openPopup('Modifier commande', commande);
  }

  deleteCommande(id: number) {
    if (window.confirm("Vous êtes sûr de retirer la commande de référence : " + id + " ?")) {
      this.commandeService.deleteCommande(id).subscribe(() => {
        this.getAllCommandes();
      });
    }
  }

  addCommande() {
    this.openPopup('Ajouter une commande');
  }

  openPopup(title: string, commande?: Commande) {
    if (commande) {
      const dialogRef = this.dialog.open(PopupCommandeComponent, {
        width: '40%',
        data: { title, commande }
      });
      dialogRef.afterClosed().subscribe((result: any) => {
        if (result) {
          const idCommande = commande.id!;
          this.updateCommandeTpToServer(idCommande, result);
        }
      });
    } else {
      const dialogRef = this.dialog.open(PopupCommandeComponent, {
        width: '40%',
        data: { title, commande: new Commande() }
      });
      dialogRef.afterClosed().subscribe((result: any) => {
        if (result) {
          this.addCommandeToServer(result);
        }
      });
    }
  }

  addCommandeToServer(commande: any): void {
    this.commandeService.addCommande(commande).subscribe(() => {
      this.getAllCommandes();
    });
  }

  updateCommandeTpToServer(id: any, commande: any): void {
    if (id) {
      this.commandeService.updateCommande(commande, id).subscribe(() => {
        this.getAllCommandes();
      });
    }
  }
}
