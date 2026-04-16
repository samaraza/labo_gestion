import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { AuthenticationComponent } from './authentication/authentication.component';
import { ActivateAccountComponent } from './activate-account/activate-account.component';
import { RegisterComponent } from './register/register.component';
import { HomeComponent } from './home/home.component'
import { BodyComponent } from './body/body.component';
import { ProfileComponent } from './profile/profile.component';
import { TableauUsersComponent } from './user-components/tableau-users/tableau-users.component';
import { AdministrateurDirecteurGuard } from './guards/administrateur-directeur.guard';
import { TableauArmoireComponent } from './armoire-components/tableau-armoire/tableau-armoire.component';
import { PreparateurProfesseurGuard } from './guards/preparateur-professeur.guard';
import { TableauProduitComponent } from './produit-components/tableau-produit/tableau-produit.component';
import { TableauPreparationComponent } from './preparation-components/tableau-preparation/tableau-preparation.component';
import { TableauSalletpComponent } from './salletp-components/tableau-salletp/tableau-salletp.component';
import { TableauTpComponent } from './tp-components/tableau-tp/tableau-tp.component';
import { DirecteurAdministrateurPreparateurGuard } from './guards/directeur-administrateur-preparateur.guard';
// ✅ Import the new components
import { TableauFournisseurComponent } from './fournisseur-components/tableau-fournisseur/tableau-fournisseur.component';
import { TableauCommandeComponent } from './commande-components/tableau-commande/tableau-commande.component';
import { TableauInventaireComponent } from './inventaire-components/tableau-inventaire/tableau-inventaire.component';
import { TableauPostComponent } from './post-components/tableau-post/tableau-post.component';
import { ProfesseurPreparateurDirecteurGuard } from './guards/professeur-preparateur-directeur.guard';
import { TableauComponent } from './labo-components/tableau/tableau.component';

const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'activate-account', component: ActivateAccountComponent },
  {
    path: 'home',
    component: HomeComponent,
    children: [
      {
        path: '',
        component: BodyComponent,
        children: [
          // Default page
          { path: '', redirectTo: 'profile', pathMatch: 'full' },
          { path: 'profile', component: ProfileComponent },
          { path: 'users', component: TableauUsersComponent, canActivate: [AdministrateurDirecteurGuard] },
          { path: 'armoire', component: TableauArmoireComponent, canActivate: [PreparateurProfesseurGuard] },
          { path: 'produit', component: TableauProduitComponent, canActivate: [PreparateurProfesseurGuard] },
          { path: 'preparation', component: TableauPreparationComponent, canActivate: [PreparateurProfesseurGuard] },
          { path: 'salleTP', component: TableauSalletpComponent, canActivate: [PreparateurProfesseurGuard] },
          { path: 'tp', component: TableauTpComponent, canActivate: [PreparateurProfesseurGuard] },
          { path: 'labo', component: TableauComponent,canActivate:[ProfesseurPreparateurDirecteurGuard],  },//2
          // ✅ Add new routes
          { path: 'fournisseurs', component: TableauFournisseurComponent, canActivate: [AdministrateurDirecteurGuard] },
          { path: 'commandes', component: TableauCommandeComponent, canActivate: [DirecteurAdministrateurPreparateurGuard] },
          { path: 'inventaires', component: TableauInventaireComponent, canActivate: [DirecteurAdministrateurPreparateurGuard] },
          { path: 'posts', component: TableauPostComponent, canActivate: [ProfesseurPreparateurDirecteurGuard] },
        ]
      },
    ]
  },
  { path: 'authentication', component: AuthenticationComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
