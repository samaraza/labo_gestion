import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';

import { TpDTO } from '../../models/tp.dto';   // ← utilisez DTO
import { Role } from '../../enums/role';
import { TpService } from '../../service/tp_service';
import { LoginService } from '../../service/login';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatDialog } from '@angular/material/dialog';
import { HttpHeaders } from '@angular/common/http';
import { PopupTpComponent } from '../popup-tp/popup-tp.component';

@Component({
  selector: 'app-tableau-tp',
  templateUrl: './tableau-tp.component.html',
  styleUrls: ['./tableau-tp.component.scss']
})
export class TableauTpComponent implements OnInit, AfterViewInit {
  tps: TpDTO[] = [];
  headers: any;
  connectedUserRole?: Role;
  roles = Object.keys(Role);

  dataSource: MatTableDataSource<TpDTO> = new MatTableDataSource<TpDTO>(this.tps);
  // Colonnes correspondant aux propriétés du DTO
  displayedColumns: string[] = ['type', 'jourTp', 'prof', 'salleTp', 'niveauScolaire',
                                  'preparations', 'quantitePrep', 'produits', 'quantiteProd', 'action'];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  isLoading = false;

  constructor(private tpService: TpService, private loginService: LoginService, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.connectedUserRole = this.loginService.connectedUser.role;
    this.headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: 'Basic ' + btoa(this.loginService.connectedUser.email + ':' + this.loginService.connectedUser.password)
    });
    this.getAllTps();
  }

  getAllTps(): void {
    this.isLoading = true;
    this.tpService.getAllTps().subscribe(
      data => {
        this.tps = data;
        this.dataSource.data = this.tps;
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

  editTp(tp: TpDTO) {
    this.openPopup('Modifier TP', tp);
  }

  deleteTp(id: number) {
    if (window.confirm("Vous êtes sûr de retirer le TP de référence : " + id + " ?")) {
      this.tpService.deleteTp(id).subscribe(() => {
        this.getAllTps();
      });
    }
  }

  addTp() {
    this.openPopup('Ajouter un TP');
  }

  openPopup(title: string, tp?: TpDTO) {
    if (tp) {
      const dialogRef = this.dialog.open(PopupTpComponent, {
        width: '40%',
        data: { title, tp }
      });
      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          this.updateTpToServer(tp.id!, result);
        }
      });
    } else {
      const dialogRef = this.dialog.open(PopupTpComponent, {
        width: '40%',
        data: { title, tp: new TpDTO() }
      });
      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          this.addTpToServer(result);
        }
      });
    }
  }

  addTpToServer(tp: TpDTO): void {
    console.log("tp", tp);
    this.tpService.addTp(tp).subscribe(() => {
      this.getAllTps();
    });
  }

  updateTpToServer(id: number, tp: TpDTO): void {
    this.tpService.updateTp(tp, id).subscribe(() => {
      this.getAllTps();
    });
  }

 // عرض أسماء التحضيرات (نفسها لا تتغير لأن PreparationDTO يرسل designation مباشرة)
// عرض أسماء التحضيرات
// عرض أسماء التحضيرات
getPreparationsNames(preparations: any[]): string {
  return preparations?.map(p => p.preparationDesignation).join(', ') || '';
}

// عرض كميات التحضيرات
getPreparationsQuantites(preparations: any[]): string {
  return preparations?.map(p => p.quantite).join(', ') || '';
}

// عرض أسماء المنتجات
getProduitsNames(produits: any[]): string {
  return produits?.map(p => p.produitDesignation).join(', ') || '';
}

// عرض كميات المنتجات
getProduitsQuantites(produits: any[]): string {
  return produits?.map(p => p.quantite).join(', ') || '';
}
}
