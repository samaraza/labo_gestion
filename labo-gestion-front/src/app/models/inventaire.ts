import { Produit } from "./produit";

export class Inventaire {
    id?: string;
    anneeScolaire?: string;
    commentaire ?: string;
    date ?: string;
    responsable ?: string;
    produit ?: Produit;
    quantiteRestante?: number
}
