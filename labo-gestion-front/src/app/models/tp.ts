import { NiveauScolaire } from "../enums/niveau_scolaire";
import { TpType } from "../enums/tp_type";
import { PreparationTP } from "./preparation_tp";
import { ProduitTP } from "./produit_tp";
import { SalleTp } from "./salletp";
import { User } from "./user";

export class Tp {
    id?:string;
    type?:TpType;
    jourTp?:string;
    prof?:User;
    salleTp?: SalleTp;
    niveauScolaire?: NiveauScolaire;
    preparations?: PreparationTP[];
    produits?:ProduitTP[]
}
