// src/app/models/produit.ts
import { Fournisseur } from "./fournisseur"; // ✅ تصحيح: founisseur → fournisseur

export class Produit {
    id?: number;  // ✅ utiliser number pour l'ID
    designation?: string;
    reference?: string;
    type?: string;
    dateExp?: string;
    categorie?: string;
    rubrique?: string;
    durabilite?: string;
    quantiteInitiale?: number;
    fournisseur?: Fournisseur;
    uniteMesure?: string;
}
