import { Produit } from "./produit";

export class Preparation {
    id?: string;
    designation?: string;
    date?: string;
    produit1?:Produit;
    quantite1?:number;
    produit2?:Produit;
    quantite2?:number;
    quantite?:number;
}
