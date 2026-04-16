import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Armoire } from '../../models/armoire';
import { ArmoireService } from '../../service/armoire_service';
import { PopupArmoireComponent } from '../popup-armoire/popup-armoire.component';
import { Role } from '../../enums/role';
import { LoginService } from '../../service/login';

@Component({
  selector: 'app-tableau-armoire',
  templateUrl: './tableau-armoire.component.html',
  styleUrls: ['./tableau-armoire.component.scss']
})
export class TableauArmoireComponent implements OnInit, AfterViewInit {
  armoires: Armoire[] = [];
  dataSource: MatTableDataSource<Armoire> = new MatTableDataSource<Armoire>(this.armoires);
  displayedColumns: string[] = ['designation', 'produits', 'action'];
  isLoading = false;
  connectedUserRole?: Role;
  roles = Object.values(Role);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private armoireService: ArmoireService,
    private loginService: LoginService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      this.connectedUserRole = JSON.parse(userStr).role;
    }
    this.getAllArmoires();
  }

  getAllArmoires(): void {
    this.isLoading = true;
    this.armoireService.getAllArmoires().subscribe({
      next: (data: Armoire[]) => {
        this.armoires = data;
        this.dataSource.data = this.armoires;
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error fetching armoires:', error);
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

  editArmoire(armoire: Armoire): void {
    this.openPopup('Modifier armoire', armoire);
  }

  deleteArmoire(id: number | undefined): void {
    if (!id) return;
    if (window.confirm('Voulez-vous vraiment supprimer cette armoire ?')) {
      this.armoireService.deleteArmoire(id).subscribe({
        next: () => this.getAllArmoires(),
        error: (error: any) => console.error('Error deleting armoire:', error)
      });
    }
  }

  addArmoire(): void {
    this.openPopup('Ajouter une armoire');
  }

  openPopup(title: string, armoire?: Armoire): void {
    const dialogRef = this.dialog.open(PopupArmoireComponent, {
      width: '500px',
      data: { title, armoire: armoire || new Armoire() }
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result) {
        if (armoire?.id) {
          const idArmoire = typeof armoire.id === 'string' ? parseInt(armoire.id, 10) : armoire.id;
          this.updateArmoireToServer(idArmoire, result);
        } else {
          this.addArmoireToServer(result);
        }
      }
    });
  }

  addArmoireToServer(armoire: any): void {
    this.armoireService.addArmoire(armoire).subscribe({
      next: () => this.getAllArmoires(),
      error: (error: any) => console.error('Error adding armoire:', error)
    });
  }

  updateArmoireToServer(id: number, armoire: any): void {
    this.armoireService.updateArmoire(armoire, id).subscribe({
      next: () => this.getAllArmoires(),
      error: (error: any) => console.error('Error updating armoire:', error)
    });
  }
}
