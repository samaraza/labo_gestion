import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { PopupComponent } from '../popup/popup.component';
import { Labo } from '../../models/labo';
import { LaboService } from 'src/app/service/labo_service';
import { LoginService } from 'src/app/service/login';
import { Role } from 'src/app/enums/role';

@Component({
  selector: 'app-tableau',
  templateUrl: './tableau.component.html',
  styleUrls: ['./tableau.component.scss']
})
export class TableauComponent implements OnInit, AfterViewInit {
  labos: Labo[] = [];
  connectedUserRole?: Role;
  roles = Object.keys(Role);
  dataSource = new MatTableDataSource<Labo>(this.labos);
  displayedColumns: string[] = ["laboType", "sallesTp", "action"];
  isLoading = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private laboService: LaboService,
    private loginService: LoginService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.connectedUserRole = this.loginService.connectedUser?.role;
    this.getAllLabos();
  }

  getAllLabos(): void {
    this.isLoading = true;
    this.laboService.getAllLabos().subscribe({
      next: (data) => {
        this.labos = data;
        this.dataSource.data = this.labos;
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
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

  editLabo(labo: Labo): void {
    this.openPopup('Modifier laboratoire', labo);
  }

  deleteLabo(id: number): void {
    if (window.confirm(`Supprimer le laboratoire ID ${id} ?`)) {
      this.laboService.deleteLabo(Number(id)).subscribe(() => this.getAllLabos());
    }
  }

  addLabo(): void {
    this.openPopup('Ajouter un laboratoire');
  }

  openPopup(title: string, labo?: Labo): void {
    const dialogRef = this.dialog.open(PopupComponent, {
      width: '40%',
      data: { title, labo: labo || new Labo() }
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        if (labo && labo.id) {
          this.updateLaboToServer(Number(labo.id), result);
        } else {
          this.addLaboToServer(result);
        }
      }
    });
  }

  addLaboToServer(labo: any): void {
    this.laboService.addLabo(labo).subscribe(() => this.getAllLabos());
  }

  updateLaboToServer(id: number, labo: any): void {
    this.laboService.updateLabo(labo, id).subscribe(() => this.getAllLabos());
  }
}
