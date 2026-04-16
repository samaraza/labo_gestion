import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { HttpHeaders } from '@angular/common/http';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Preparation } from '../../models/preparation';
import { PreparationService } from '../../service/preparation_service';
import { PopupPreparationComponent } from '../popup-preparation/popup-preparation.component';
import { Role } from '../../enums/role';
import { LoginService } from '../../service/login';

@Component({
  selector: 'app-tableau-preparation',
  templateUrl: './tableau-preparation.component.html',
  styleUrls: ['./tableau-preparation.component.scss']
})
export class TableauPreparationComponent
     implements OnInit, AfterViewInit {
       preparations: Preparation[] = [];
       headers:any;
       connectedUserRole?: Role ;
       roles = Object.keys(Role);

       dataSource: MatTableDataSource<Preparation> = new MatTableDataSource<Preparation>(this.preparations);
       displayedColumns: string[] = ["designation", "date","quantite","produit1","quantite1","produit2", "quantite2", "action"];

       @ViewChild(MatPaginator) paginator!: MatPaginator;
       @ViewChild(MatSort) sort!: MatSort;

       constructor(private preparationService: PreparationService, private loginService: LoginService, private dialog: MatDialog) {}

       isLoading = false;

       ngOnInit(): void {
         this.connectedUserRole = this.loginService.connectedUser.role;
         this.headers = new HttpHeaders({
           'Content-Type': 'application/json',
           Authorization: 'Basic ' + btoa(this.loginService.connectedUser.email + ':' + this.loginService.connectedUser.password)
         });
         this.getAllPreparations();
       }

       // Get all preaparations
       public getAllPreparations(): void {
         this.isLoading = true; // Début du chargement
         this.preparationService.getAllPreparations().subscribe(
           data => {
             this.preparations = data;
             this.dataSource.data = this.preparations; // Assignez les données récupérées à dataSource
             this.isLoading = false; // Fin du chargement
           },
           error => {
             this.isLoading = false; // Fin du chargement
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

       editPreparation(preparation: Preparation) {
         this.openPopup('Modifier préparation', preparation);
       }

       deletePreparation(id: number) {
         if (window.confirm("Vous êtes sûr de retirer la préparation de référence : " + id + " ?")) {
           this.preparationService.deletePreparation(id).subscribe(() => {
             this.getAllPreparations();
           });
         }
       }

       addPreparation() {
         this.openPopup('Ajouter une préparation');
       }

       openPopup(title: string, preparation?: Preparation) {
         if (preparation) {
           const dialogRef = this.dialog.open(PopupPreparationComponent, {
             width: '40%',
             maxHeight: '90vh',
             data: { title, preparation: preparation || new Preparation()  }
           });

           dialogRef.afterClosed().subscribe(result => {
             if (result) {
               const idPreparation = preparation.id!;
               this.updatePreparationToServer(idPreparation, result);
             }
           });
         } else {
           const dialogRef = this.dialog.open(PopupPreparationComponent, {
             width: '40%',
             data: { title ,preparation: new Preparation()}
           });

           dialogRef.afterClosed().subscribe(result => {
             if (result) {
               this.addPreparationToServer(result); // Ajouter un nouveau laboratoire
             }
           });
         }
       }


       addPreparationToServer(commande: any): void {
         this.preparationService.addPreparation(commande).subscribe(() => {
           this.getAllPreparations();
         });
       }

       updatePreparationToServer(id: any,preparation :any): void {
         if (id) {

           this.preparationService.updatePreparation(preparation,id).subscribe(() => {
             this.getAllPreparations();
           });
         }
       }
     }
