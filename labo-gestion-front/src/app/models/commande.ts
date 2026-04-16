import { Fournisseur } from "./fournisseur";
import { ProduitCommande } from "./produit-commande";
import { User } from "./user";

export class Commande {
     id?: string;
        designation?: string;
        date?: string;
        observation?: string;
        numero?: string;
        // حقول المورد (Fournisseur) أصبحت مسطحة
        fournisseurId?: number;
        fournisseurNom?: string;
        // حقول المستخدم (User) أصبحت مسطحة
        userId?: number;
        userFirstName?: string;
        userLastName?: string;
        produits?: any[];  // يمكنك تعديل هذا لاحقاً
}
