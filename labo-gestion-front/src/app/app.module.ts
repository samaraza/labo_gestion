import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { AdministrateurGuard } from './guards/administrateur.guard';
import { PreparateurProfesseurGuard } from './guards/preparateur-professeur.guard';
import { DirecteurAdministrateurPreparateurGuard } from './guards/directeur-administrateur-preparateur.guard';
import { AdministrateurDirecteurGuard } from './guards/administrateur-directeur.guard';

// Material Imports
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatDialogModule } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginatorModule } from '@angular/material/paginator';

// Routing
import { AppRoutingModule } from './app-routing.module';

// Components
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { AuthenticationComponent } from './authentication/authentication.component';
import { ActivateAccountComponent } from './activate-account/activate-account.component';
import { RegisterComponent } from './register/register.component';
import { HomeComponent } from './home/home.component';
import { BodyComponent } from './body/body.component';
import { SidenavComponent } from './sidenav/sidenav.component';
import { ProfileComponent } from './profile/profile.component';
import { EditUserDialogComponent } from './profile/edit-user-dialog/edit-user-dialog.component';
import { CodeInputModule } from 'angular-code-input';
import { PopupUsersComponent } from './user-components/popup-users/popup-users.component';
import { TableauUsersComponent } from './user-components/tableau-users/tableau-users.component';
import { PopupArmoireComponent } from './armoire-components/popup-armoire/popup-armoire.component';
import { TableauArmoireComponent } from './armoire-components/tableau-armoire/tableau-armoire.component';
import { PopupProduitComponent } from './produit-components/popup-produit/popup-produit.component';
import { TableauProduitComponent } from './produit-components/tableau-produit/tableau-produit.component';
import { PopupPreparationComponent } from './preparation-components/popup-preparation/popup-preparation.component';
import { TableauPreparationComponent } from './preparation-components/tableau-preparation/tableau-preparation.component';
import { PopupSalletpComponent } from './salletp-components/popup-salletp/popup-salletp.component';
import { TableauSalletpComponent } from './salletp-components/tableau-salletp/tableau-salletp.component';
import { PopupTpComponent } from './tp-components/popup-tp/popup-tp.component';
import { TableauTpComponent } from './tp-components/tableau-tp/tableau-tp.component';
import { TableauComponent } from './labo-components/tableau/tableau.component';
// ✅ استيراد المكونات الجديدة
import { TableauFournisseurComponent } from './fournisseur-components/tableau-fournisseur/tableau-fournisseur.component';
import { PopupFournisseurComponent } from './fournisseur-components/popup-fournisseur/popup-fournisseur.component';
import { TableauCommandeComponent } from './commande-components/tableau-commande/tableau-commande.component';
import { PopupCommandeComponent } from './commande-components/popup-commande/popup-commande.component';
import { TableauInventaireComponent } from './inventaire-components/tableau-inventaire/tableau-inventaire.component';
import { PopupInventaireComponent } from './inventaire-components/popup-inventaire/popup-inventaire.component';
import { TableauPostComponent } from './post-components/tableau-post/tableau-post.component';
import { PopupPostComponent } from './post-components/popup-post/popup-post.component';

import { AuthInterceptor } from './interceptors/auth-interceptor';
import { PopupAffecterUserComponent } from './user-components/popup-affecter-user/popup-affecter-user.component';

import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';

import { PopupComponent } from './labo-components/popup/popup.component';
import { ProfesseurPreparateurDirecteurGuard } from './guards/professeur-preparateur-directeur.guard';
@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    AuthenticationComponent,
    ActivateAccountComponent,
    RegisterComponent,
    HomeComponent,
    BodyComponent,
    SidenavComponent,
    ProfileComponent,
    EditUserDialogComponent,
    PopupUsersComponent,
    TableauUsersComponent,
    PopupArmoireComponent,
    TableauArmoireComponent,
    PopupProduitComponent,
    TableauProduitComponent,
    PopupPreparationComponent,
    TableauPreparationComponent,
    PopupSalletpComponent,
    TableauSalletpComponent,
    PopupTpComponent,
    TableauTpComponent,
    TableauComponent,
     PopupComponent,



    // ✅ إضافة المكونات الجديدة
    TableauFournisseurComponent,
    PopupFournisseurComponent,
    TableauCommandeComponent,
    PopupCommandeComponent,
    TableauInventaireComponent,
    PopupInventaireComponent,
    TableauPostComponent,
    PopupPostComponent,
    PopupAffecterUserComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    HttpClientModule,
    CommonModule,
    ReactiveFormsModule,
    CodeInputModule,
    MatPaginatorModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatTableModule,
    MatSortModule,
    MatDialogModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatIconModule,
    MatDatepickerModule,
    MatNativeDateModule,

  ],
  providers: [
    AdministrateurGuard,
    AdministrateurDirecteurGuard,
    PreparateurProfesseurGuard,
    DirecteurAdministrateurPreparateurGuard,
     ProfesseurPreparateurDirecteurGuard,
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
