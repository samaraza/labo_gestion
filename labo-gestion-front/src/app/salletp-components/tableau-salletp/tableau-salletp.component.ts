import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { SalleTp } from '../../models/salletp';
import { Armoire } from '../../models/armoire';
import { SalleTpService } from '../../service/salletp_service';
import { LoginService } from '../../service/login';
import { PopupSalletpComponent } from '../popup-salletp/popup-salletp.component';
import { Role } from '../../enums/role';

@Component({
  selector: 'app-tableau-salletp',
  templateUrl: './tableau-salletp.component.html',
  styleUrls: ['./tableau-salletp.component.scss']
})
export class TableauSalletpComponent implements OnInit, AfterViewInit {
  salletps: SalleTp[] = [];
  armoires: Armoire[] = [];

  dataSource: MatTableDataSource<SalleTp> = new MatTableDataSource<SalleTp>(this.salletps);
  displayedColumns: string[] = ['numero', 'armoires', 'action'];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  isLoading = false;
  connectedUserRole?: Role;
  roles = Object.values(Role);

  constructor(
    private salleTpService: SalleTpService,
    private loginService: LoginService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    // قراءة الدور من localStorage بدلاً من الاعتماد على الخدمة فقط
    const userStr = localStorage.getItem('user');
    if (userStr) {
      this.connectedUserRole = JSON.parse(userStr).role;
    }
    this.getAllSalleTps();
  }

  getAllSalleTps(): void {
    this.isLoading = true;
    // ❌ تم حذف headers
    this.salleTpService.getAllSalleTp().subscribe({
      next: (data: SalleTp[]) => {
        this.salletps = data;
        this.dataSource.data = this.salletps;
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error fetching salles TP:', error);
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

  editSalleTp(salleTp: SalleTp): void {
    this.openPopup('Modifier salle de TP', salleTp);
  }

  deleteSalleTp(id: number | undefined): void {
    if (!id) return;
    // ❌ تم حذف headers
    if (window.confirm('Voulez-vous vraiment supprimer cette salle de TP ?')) {
      this.salleTpService.deleteSalleTp(id).subscribe({
        next: () => this.getAllSalleTps(),
        error: (error: any) => console.error('Error deleting salle TP:', error)
      });
    }
  }

  addSalleTp(): void {
    this.openPopup('Ajouter une salle de TP');
  }

  openPopup(title: string, salleTp?: SalleTp): void {
    const dialogRef = this.dialog.open(PopupSalletpComponent, {
      width: '500px',
      data: { title, salleTp: salleTp || new SalleTp() }
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result) {
        if (salleTp?.id) {
          const idSalleTp = typeof salleTp.id === 'string' ? parseInt(salleTp.id, 10) : salleTp.id;
          this.updateSalleTpToServer(idSalleTp, result);
        } else {
          this.addSalleTpToServer(result);
        }
      }
    });
  }

  addSalleTpToServer(salleTp: any): void {
    // ❌ تم حذف headers
    this.salleTpService.addSalleTp(salleTp).subscribe({
      next: () => this.getAllSalleTps(),
      error: (error: any) => console.error('Error adding salle TP:', error)
    });
  }

  updateSalleTpToServer(id: number, salleTp: any): void {
    // ❌ تم حذف headers
    this.salleTpService.updateSalleTp(salleTp, id).subscribe({
      next: () => this.getAllSalleTps(),
      error: (error: any) => console.error('Error updating salle TP:', error)
    });
  }
}
