export class TpDTO {
  id?: number;
    jourTp?: string;
    niveauScolaire?: string;
    type?: string;
    profId?: number;
    salleTpId?: number;
    // ⬇️ AJOUTER CES TROIS LIGNES
    profFirstName?: string;
    profLastName?: string;
    salleTpNumero?: string;
    preparations?: any[];
    produits?: any[];
}
