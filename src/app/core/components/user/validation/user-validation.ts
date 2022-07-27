import { Validators } from "@angular/forms";
import { AbstractValidation } from "src/app/core/shared/commons/validation/abstract-validation";

export class UserValidation extends AbstractValidation {
   
    static fullName(): Validators[]{
        return [Validators.required, Validators.minLength(5)]
    }

    static password(): Validators[]{
        return [Validators.required, Validators.minLength(6)];
    }

    static username(): Validators[]{
        return [Validators.required, Validators.minLength(6), Validators.pattern("[a-zA-Z]*")];
    }
    
    static required(): Validators[]{
        return [Validators.required];
    }
}