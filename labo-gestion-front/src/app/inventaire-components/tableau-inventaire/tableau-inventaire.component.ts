import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core'; // ✅ إزالة التكرار
import { HttpHeaders } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Inventaire } from 'src/app/models/inventaire';
import { InventaireService } from 'src/app/service/inventaire_service';
import { LoginService } from 'src/app/service/login';
import { PopupInventaireComponent } from '../popup-inventaire/popup-inventaire.component';
import { Role } from 'src/app/enums/role';
@Component({
  selector: 'app-tableau-inventaire',
  templateUrl: './tableau-inventaire.component.html',
  styleUrls: ['./tableau-inventaire.component.scss']
})
export class TableauInventaireComponent
     implements OnInit, AfterViewInit {
       inventaires: Inventaire[] = [];
       headers:any;
       connectedUserRole?: Role ;
       roles = Object.keys(Role);

       isEmpty(value: any): boolean {
         return value == null || value === '';
     }

       dataSource: MatTableDataSource<Inventaire> = new MatTableDataSource<Inventaire>(this.inventaires);
       displayedColumns: string[] = ["anneeScolaire", "commentaire", "date", "responsable", "produit","quantiteRestante", "action"];

       @ViewChild(MatPaginator) paginator!: MatPaginator;
       @ViewChild(MatSort) sort!: MatSort;

       constructor(private inventaireService: InventaireService, private loginService: LoginService, private dialog: MatDialog) {}

       isLoading = false;

       ngOnInit(): void {
         this.connectedUserRole = this.loginService.connectedUser.role;
         this.headers = new HttpHeaders({
           'Content-Type': 'application/json',
           Authorization: 'Basic ' + btoa(this.loginService.connectedUser.email + ':' + this.loginService.connectedUser.password)
         });
         this.getAllInventaires();
       }

       public getAllInventaires(): void {
         this.isLoading = true;
         this.inventaireService.getAllInventaires().subscribe(
           data => {
             this.inventaires = data;
             this.dataSource.data = this.inventaires;
             this.isLoading = false;
           },
           error => {
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

       editInventaire(produit: Inventaire) {
         this.openPopup('Modifier inventaire', produit);
       }

       deleteInventaire(id: number) {
         if (window.confirm("Vous êtes sûr de supprimer l'inventaire avec l'ID : " + id + " ?")) {
           this.inventaireService.deleteInventaire(id).subscribe(() => {
             this.getAllInventaires();
           });
         }
       }

       addInventaire() {
         this.openPopup('Ajouter un inventaire');
       }

       openPopup(title: string, inventaire?: Inventaire) {
         if (inventaire) {
           const dialogRef = this.dialog.open(PopupInventaireComponent, {
             width: '40%',
             data: { title, inventaire }
           });

           dialogRef.afterClosed().subscribe(result => {
             if (result) {
               const idInventaire = inventaire.id!;
               this.updateInventaireToServer(idInventaire, result);
             }
           });
         } else {
           const dialogRef = this.dialog.open(PopupInventaireComponent, {
             width: '40%',
             data: { title ,produit: new Inventaire()}
           });

           dialogRef.afterClosed().subscribe(result => {
             if (result) {
               this.addInventaireToServer(result); // Ajouter un nouveau laboratoire
             }
           });
         }
       }

       addInventaireToServer(produit: any): void {
         const headers = new HttpHeaders({
           'Content-Type': 'application/json',
           Authorization: 'Basic ' + btoa(this.loginService.connectedUser.email + ':' + this.loginService.connectedUser.password)
         });

         this.inventaireService.addInventaire(produit).subscribe(() => {
           this.getAllInventaires();
         });
       }

       updateInventaireToServer(id: any,produit :any): void {
         if (id) {
           const headers = new HttpHeaders({
             'Content-Type': 'application/json',
             Authorization: 'Basic ' + btoa(this.loginService.connectedUser.email + ':' + this.loginService.connectedUser.password)
           });

           this.inventaireService.updateInventaire(produit,id).subscribe(() => {
             this.getAllInventaires();
           });
         }
       }

     }


