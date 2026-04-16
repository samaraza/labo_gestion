import { Role } from "../enums/role";

export class User {
     id? : string;
     firstname? : String;
     lastname? : String;
     email? : String;
     password? : String;
     roles?: Role[];
     role?: Role;
     ecoleId?: number;
}
