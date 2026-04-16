// export const navbarData =[
//     {
//         routeLink: 'labo',
//         icon:'fal fa-microscope',
//         label:'Gestion des laboratoires',
//     },
//     {
//         routeLink: 'salleTP',
//         icon: 'fa fa-chalkboard-teacher',
//         label:'Gestion des salles de TP',
//     },
//     {
//         routeLink: 'users',
//         icon: 'fa fa-users',
//         label:'Gestion des utilisateurs'
//     },
//     {
//         routeLink: 'armoire',
//         icon: 'fa fa-cabinet-filing',
//         label:'Gestion des armoires'
//     },
//     {
//         routeLink: 'fournisseur',
//         icon:'fal fa-handshake',
//         label:"Gestions des fournisseurs"
//     },
//     {
//         "routeLink": "commande",
//         "icon": "fal fa-clipboard-list",
//         "label": "Gestion des commandes"
//     },
//     {
//         routeLink: 'produit',
//         icon:'fal fa-box-open',
//         label:'Gestion des produits'
//     },
//     {
//         routeLink: 'preparation',
//         icon:'fal fa-flask',
//         label:'Gestion des préparations'
//     },
//     {
//         routeLink: 'tp',
//         icon:'fal fa-vials',
//         label:'Gestion des TPs'
//     },
//     {
//         routeLink: 'post',
//         icon:'fal fa-paper-plane',
//         label:'Gestion des posts'
//     },
//     {
//         routeLink: 'inventaire',
//         icon:'fal fa-boxes',
//         label:'Gestion des inventaires'
//     },
//     {
//         routeLink: 'profile',
//         icon:'fal fa-user',
//         label:'Profil'
//     },
// ]

import { Role } from "../enums/role";

export const navbarData = [
    {
      routeLink: 'labo',
      icon: 'fal fa-microscope',
      label: 'Gestion des laboratoires',
      roles: [ Role.DIRECTEUR, Role.PREPARATEUR,Role.PROFFESSEUR]
    },
    {
      routeLink: 'salleTP',
      icon: 'fa fa-chalkboard-teacher',
      label: 'Gestion des salles de TP',
      roles: [Role.PREPARATEUR, Role.PROFFESSEUR]
    },
    {
      routeLink: 'users',
      icon: 'fa fa-users',
      label: 'Gestion des utilisateurs',
      roles: [Role.ADMINISTRATEUR,Role.DIRECTEUR]
    },
    {
      routeLink: 'armoire',
      icon: 'fa fa-cabinet-filing',
      label: 'Gestion des armoires',
      roles: [Role.PROFFESSEUR, Role.PREPARATEUR]
    },
    {
      routeLink: 'fournisseurs',
      icon: 'fal fa-handshake',
      label: 'Gestions des fournisseurs',
      roles: [Role.ADMINISTRATEUR]
    },
    {
      routeLink: 'commandes',
      icon: 'fal fa-clipboard-list',
      label: 'Gestion des commandes',
      roles: [Role.ADMINISTRATEUR, Role.DIRECTEUR, Role.PREPARATEUR]
    },
    {
      routeLink: 'produit',
      icon: 'fal fa-box-open',
      label: 'Gestion des produits',
      roles: [Role.PROFFESSEUR, Role.PREPARATEUR]
    },
    {
      routeLink: 'preparation',
      icon: 'fal fa-flask',
      label: 'Gestion des préparations',
      roles: [Role.PROFFESSEUR, Role.PREPARATEUR]
    },
    {
      routeLink: 'tp',
      icon: 'fal fa-vials',
      label: 'Gestion des TPs',
      roles: [Role.PROFFESSEUR, Role.PREPARATEUR]
    },
    {
      routeLink: 'posts',
      icon: 'fal fa-paper-plane',
      label: 'Gestion des posts',
      roles: [Role.ADMINISTRATEUR, Role.PROFFESSEUR,Role.DIRECTEUR,Role.PREPARATEUR]
    },
    {
      routeLink: 'inventaires',
      icon: 'fal fa-boxes',
      label: 'Gestion des inventaires',
      roles: [Role.ADMINISTRATEUR, Role.PROFFESSEUR,Role.DIRECTEUR,Role.PREPARATEUR]
    },
    {
      routeLink: 'profile',
      icon: 'fal fa-user',
      label: 'Profil',
      roles: [Role.ADMINISTRATEUR, Role.DIRECTEUR, Role.PREPARATEUR, Role.PROFFESSEUR]
    },
  ];
